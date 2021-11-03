/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro.realm;

import java.util.ArrayList;
import java.util.List;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.enums.LogType;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.manager.AsyncManager;
import com.j2eefast.common.core.utils.SpringUtil;
import com.j2eefast.framework.manager.factory.AsyncFactory;
import com.j2eefast.framework.shiro.service.SysLoginService;
import com.j2eefast.framework.shiro.token.MobileToken;
import com.j2eefast.framework.sys.constant.factory.ConstantFactory;
import com.j2eefast.framework.sys.entity.SysRoleEntity;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.UserUtils;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 手机验证码登录
 * @author huanzhou
 * @date 2021-09-24
 */
@Slf4j
public class ValideCodeRealm extends AuthorizingRealm {

	/**
	 * 权限验证
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		LoginUserEntity user = (LoginUserEntity) principals.getPrimaryPrincipal();
		log.debug("---系统权限认证---");
        Long userId = user.getId();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 系统管理员，拥有最高权限
        if (userId.equals(Constant.SUPER_ADMIN)){
            info.addRole("ADMIN");
            info.addStringPermission("*:*:*");
        } else {
            info.addRoles(UserUtils.getRoleKey(user.getRoles()));
            //获取菜单权限
            info.setStringPermissions(ConstantFactory.me().findPermissionsByUserId(userId));
        }
        return info;
	}

	/**
	 * 登录验证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) 
			throws AuthenticationException {
		
		MobileToken mobileToken = null;
		
        if (authcToken instanceof MobileToken) {
			mobileToken = (MobileToken) authcToken;
		} else {
			return null;
		}
        log.debug("---系统登录认证---");
        
        //手机号码
		String mobile = mobileToken.getMobile();
		
		//验证码
		String valideCode = mobileToken.getValideCode(); 
		
		// 查询用户信息
        LoginUserEntity authenUser = new LoginUserEntity();
        
        try {
            authenUser = SpringUtil.getBean(SysLoginService.class).valideCodeLoginVerify(mobile, valideCode);
        }catch (RxcException e) {
            //不同异常不同抛出
            if(e.getCode().equals("60001")) {
                throw new UnknownAccountException(e.getMessage(), e);
            }else if(e.getCode().equals("60002")) {
                throw new ExcessiveAttemptsException(e.getMessage(), e);
            }else if(e.getCode().equals("50006")) {
				throw new UnknownAccountException(e.getMessage(), e);
			}
        }catch (Exception e){
            log.info("对用户[" + mobile + "]进行登录验证..验证未通过{}", e.getMessage());
            throw new AuthenticationException(e.getMessage(), e);
        }
        
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(authenUser, mobile, getName());
        return info;
	}
	
	@Override
	public boolean supports(AuthenticationToken token) {
		return token instanceof MobileToken;
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
				LogType.MOBILE.getVlaue(),user.getTenantId()));
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
