/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro.realm;

import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.enums.LogType;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.manager.AsyncManager;
import com.j2eefast.common.core.utils.SpringUtil;
import com.j2eefast.framework.manager.factory.AsyncFactory;
import com.j2eefast.framework.shiro.service.SysLoginService;
import com.j2eefast.framework.shiro.token.OtherToken;
import com.j2eefast.framework.sys.constant.factory.ConstantFactory;
import com.j2eefast.framework.sys.entity.SysRoleEntity;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.UserUtils;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import java.util.*;

/**
 * 第三方账号免密登录 指定Shiro验证用户登录的类 认证
 */
@Slf4j
public class OtherRealm extends AuthorizingRealm {

    /**
     * 免密授权授权认证
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        LoginUserEntity user = (LoginUserEntity) principals.getPrimaryPrincipal();
        Long userId = user.getId();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 系统管理员，拥有最高权限
        if (userId.equals(Constant.SUPER_ADMIN)){
            info.addRole(Constant.SU_ADMIN);
            info.addStringPermission("*:*:*");
        } else {
            info.addRoles(UserUtils.getRoleKey(user.getRoles()));
            //获取菜单权限
            info.setStringPermissions(ConstantFactory.me().findPermissionsByUserId(userId));
        }
        return info;
    }


    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        OtherToken token = (OtherToken) authcToken;
        String openId = token.getOpenId();
        String tenantId = token.getTenantId();
        // 查询用户信息
        LoginUserEntity authenUser = new LoginUserEntity();
        try {
            authenUser = SpringUtil.getBean(SysLoginService.class).freeLoginVerify(openId,tenantId);
        }catch (RxcException e) {
            //不同异常不同抛出
            if(e.getCode().equals("50001")) {
                throw new UnknownAccountException(e.getMessage(), e);
            }else if(e.getCode().equals("50002")) {
                throw new LockedAccountException(e.getMessage(), e);
            }else if(e.getCode().equals("50003")) {
                throw new ExcessiveAttemptsException(e.getMessage(), e);
            }else if(e.getCode().equals("50005")) {
                throw new IncorrectCredentialsException(e.getMessage(), e);
            }else if(e.getCode().equals("50004")) {
                throw new UnknownAccountException(e.getMessage(), e);
            }else if(e.getCode().equals("50006")) {
                throw new UnknownAccountException(e.getMessage(), e);
            }
        }catch (Exception e){
            log.info("对用户[" + authenUser.getUsername() + "]进行登录验证..验证未通过{}", e.getMessage());
            throw new AuthenticationException(e.getMessage(), e);
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(authenUser, openId, getName());
        return info;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OtherToken;
    }
    
    /**
	 * 退出系统
	 */
	@Override
	public void onLogout(PrincipalCollection principals) {
		LoginUserEntity user = (LoginUserEntity) principals.getPrimaryPrincipal();
		//记录退出
		AsyncManager.me().execute(AsyncFactory.outLoginInfor(user.getUsername(),
				user.getCompId(),user.getDeptId(), "00000","退出系统!",
				DateUtil.date(),
				LogType.OTHER.getVlaue(),user.getTenantId()));

        //super.onLogout(principals);
    }

    /**
     * 清理缓存权限
     */
    public void clearCachedAuthorizationInfo() {

        LoginUserEntity loginUser = UserUtils.getUserInfo();

        //清理缓存
        ConstantFactory.me().clearMenu();
        ConstantFactory.me().clearRole();
        ConstantFactory.me().clearModules();
        if(!loginUser.getId().equals(Constant.SUPER_ADMIN)){

            //获取用户角色列表
            List<SysRoleEntity> roleList = ConstantFactory.me().getRoles(loginUser.getId());
            List<Object> reles = new ArrayList<>();
            roleList.forEach(r->{
                reles.add(r);
            });

            loginUser.setRoles(reles);

            //刷新用户
            UserUtils.reloadUser(loginUser);
        }
        this.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
    }
}
