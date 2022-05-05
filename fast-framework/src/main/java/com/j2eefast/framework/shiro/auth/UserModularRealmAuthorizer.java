/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro.auth;

import cn.hutool.json.JSONUtil;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.enums.LoginType;
import com.j2eefast.framework.shiro.realm.OtherRealm;
import com.j2eefast.framework.shiro.realm.UserNameRealm;
import com.j2eefast.framework.shiro.realm.ValideCodeRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * 自定义ModularRealmAuthorizer 解决多个Redalm 授权调用多次问题
 * 可以不同登录方式 不同权限处理
 * @author huanzhou
 * @date 2021-09-24
 */
@Slf4j
public class UserModularRealmAuthorizer extends ModularRealmAuthorizer {
	
	@Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
	    log.debug("isPermitted -- > principals对象:{} permission:{}",
	    		JSONUtil.parse(principals).toString(),permission);
	    
        assertRealmsConfigured();
        //获取登录用户
        LoginUserEntity user = getLoginUser(principals);

        for (Realm realm : getRealms()) {

        	if (!(realm instanceof Authorizer)) {
                continue;
            }

        	// 针对不同登录方式不同 Realm处理
        	if(realm instanceof UserNameRealm){
        	     if(user.getLoginType().getDesc()
                         .equals(LoginType.USERNAME.getDesc())){
                     return ((UserNameRealm) realm).isPermitted(principals,permission);
                 }
            }

            if(realm instanceof OtherRealm){
                if(user.getLoginType().getDesc()
                        .equals(LoginType.OTHER.getDesc())){
                    return ((OtherRealm) realm).isPermitted(principals,permission);
                }
            }

            if(realm instanceof ValideCodeRealm){
                if(user.getLoginType().getDesc()
                        .equals(LoginType.MOBILE.getDesc())){
                    return ((ValideCodeRealm) realm).isPermitted(principals,permission);
                }
            }

        }
        return false;
    }

    @Override
    public boolean hasRole(PrincipalCollection principals, String roleIdentifier) {
    	
        log.debug("hasRole -- > principals对象:{} permission:{}", 
        		JSONUtil.parse(principals).toString(),roleIdentifier);
        
        assertRealmsConfigured();
        
        LoginUserEntity user = getLoginUser(principals);
        for (Realm realm : getRealms()) {

            if (!(realm instanceof Authorizer)) {
                continue;
            }

            if(realm instanceof UserNameRealm){
                if(user.getLoginType().getDesc()
                        .equals(LoginType.USERNAME.getDesc())){
                    return ((UserNameRealm) realm).hasRole(principals,roleIdentifier);
                }
            }

            if(realm instanceof OtherRealm){
                if(user.getLoginType().getDesc()
                        .equals(LoginType.OTHER.getDesc())){
                    return ((OtherRealm) realm).hasRole(principals,roleIdentifier);
                }
            }

            if(realm instanceof ValideCodeRealm){
                if(user.getLoginType().getDesc()
                        .equals(LoginType.MOBILE.getDesc())){
                    return ((ValideCodeRealm) realm).hasRole(principals,roleIdentifier);
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean isPermitted(PrincipalCollection principals, Permission permission) {
    	
    	assertRealmsConfigured();
        LoginUserEntity user = getLoginUser(principals);
        for (Realm realm : getRealms()) {

            if (!(realm instanceof Authorizer)) {
                continue;
            }

            if(realm instanceof UserNameRealm){
                if(user.getLoginType().getDesc()
                        .equals(LoginType.USERNAME.getDesc())){
                    return ((UserNameRealm) realm).isPermitted(principals,permission);
                }
            }

            if(realm instanceof OtherRealm){
                if(user.getLoginType().getDesc()
                        .equals(LoginType.OTHER.getDesc())){
                    return ((OtherRealm) realm).isPermitted(principals,permission);
                }
            }

            if(realm instanceof ValideCodeRealm){
                if(user.getLoginType().getDesc()
                        .equals(LoginType.MOBILE.getDesc())){
                    return ((ValideCodeRealm) realm).isPermitted(principals,permission);
                }
            }
        }
        return false;
    }

    /**
     * 获取当前登录对象
     * @param principals
     * @return
     */
    public LoginUserEntity getLoginUser(PrincipalCollection principals){
        return (LoginUserEntity) principals.getPrimaryPrincipal();
    }
}
