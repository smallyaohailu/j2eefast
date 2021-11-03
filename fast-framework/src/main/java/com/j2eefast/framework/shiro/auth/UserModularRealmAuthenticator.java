/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro.auth;

import java.util.Collection;

import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.enums.LoginType;
import com.j2eefast.framework.shiro.realm.OtherRealm;
import com.j2eefast.framework.shiro.realm.UserNameRealm;
import com.j2eefast.framework.shiro.realm.ValideCodeRealm;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;

/**
 * 自定义 ModularRealmAuthenticator
 * 区分不能登录方式不同业务处理
 * @author huanzhou
 * @date 2021-09-24
 */
public class UserModularRealmAuthenticator extends ModularRealmAuthenticator {

    /**
     * 重写解决登录授权验证
     * @param realms
     * @param token
     * @return
     */
	@Override
    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {
        
		// 1. 判断getRealms()是否返回为空
        assertRealmsConfigured();

        // 2. 通过supports()方法，匹配对应的Realm
        Realm uniqueRealm = null;
        for (Realm realm : realms) {
            if (realm.supports(token)) {
                uniqueRealm = realm;
                break;
            }
        }
        
        // 为空抛出异常
        if (uniqueRealm == null) {
            throw new UnsupportedTokenException();
        }

        return uniqueRealm.getAuthenticationInfo(token);
    }

    /**
     * 重写系统主动退出方法
     * @param principals
     */
    @Override
    public void onLogout(PrincipalCollection principals) {
        LoginUserEntity user = getLoginUser(principals);
        Collection<Realm> realms = getRealms();
        if (!CollectionUtils.isEmpty(realms)) {

            for (Realm realm : realms) {

                if(realm instanceof UserNameRealm){
                    if(user.getLoginType().getDesc()
                            .equals(LoginType.USERNAME.getDesc())){
                        ((LogoutAware) realm).onLogout(principals);
                        return;
                    }
                }

                if(realm instanceof OtherRealm){
                    if(user.getLoginType().getDesc()
                            .equals(LoginType.OTHER.getDesc())){
                        ((LogoutAware) realm).onLogout(principals);
                        return;
                    }
                }

                if(realm instanceof ValideCodeRealm){
                    if(user.getLoginType().getDesc()
                            .equals(LoginType.MOBILE.getDesc())){
                        ((LogoutAware) realm).onLogout(principals);
                        return;
                    }
                }

            }
        }
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
