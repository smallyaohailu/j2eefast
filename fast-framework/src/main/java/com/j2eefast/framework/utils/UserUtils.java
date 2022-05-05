/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.utils;

import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.framework.shiro.realm.OtherRealm;
import com.j2eefast.framework.shiro.realm.UserNameRealm;
import com.j2eefast.framework.shiro.realm.ValideCodeRealm;
import com.j2eefast.framework.sys.constant.factory.ConstantFactory;
import com.j2eefast.framework.sys.entity.SysRoleEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.utils.ToolUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 登陆用户信息
 * @author zhouzhou loveingowp@163.com
 * @time 2018-11-15 20:20
 * @version V1.0
 */
public class UserUtils {


	/** 加密算法 */
	public final static String hashAlgorithmName = "SHA-256";
	/** 循环次数 */
	public final static int hashIterations = 16;

	public static String sha256(String password, String salt) {
		return new SimpleHash(hashAlgorithmName, password, salt, hashIterations).toString();
	}

	public static Session getSession() {
		return SecurityUtils.getSubject().getSession();
	}

	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	/**
	 * 获取当前登录用户信息
	 * @return
	 */
	public static LoginUserEntity getUserInfo() {
		return (LoginUserEntity)getSubject().getPrincipal();
	}

	/**
	 * 获取用户角色集合
	 * @return
	 */
	public static List<String> getRoleKeys(){
		List<Object> objects = getUserInfo().getRoles();
		List<String> roles = new ArrayList<>();
		for(Object o: objects){
			SysRoleEntity role = (SysRoleEntity) o;
			roles.add(role.getRoleKey());
		}
		return roles;
	}

	/**
	 * 获取登陆IP
	 * @return
	 */
	public static String getIp(){
        return getSubject().getSession().getHost();
    }


	/**
	 * 重新装载用户信息--> 修改了当前用户信息需要调用
	 * @param user
	 */
	public static void reloadUser(LoginUserEntity user){
		Subject subject = getSubject();
		PrincipalCollection principalCollection = subject.getPrincipals();
		String realmName = principalCollection.getRealmNames().iterator().next();
		PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(user, realmName);
		// 重新加载Principal
		subject.runAs(newPrincipalCollection);
	}

	/**
	 * 清空缓存
	 */
	public static void clearCachedAuthorizationInfo() {
		RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
		AuthorizingRealm realm = (AuthorizingRealm) rsm.getRealms().iterator().next();
		if(realm instanceof UserNameRealm) {
			UserNameRealm userNameRealm = (UserNameRealm) realm;
			userNameRealm.clearCachedAuthorizationInfo();
		}

		if(realm instanceof ValideCodeRealm) {
			ValideCodeRealm valideCodeRealm = (ValideCodeRealm) realm;
			valideCodeRealm.clearCachedAuthorizationInfo();
		}

		if(realm instanceof OtherRealm) {
			OtherRealm otherRealm = (OtherRealm) realm;
			otherRealm.clearCachedAuthorizationInfo();
		}
	}

	/**
	 * 用户ID
	 * @return
	 */
	public static Long getUserId() {
		return ToolUtil.isEmpty(getUserInfo())?-1L:getUserInfo().getId();
	}

	public static String getUserIdToStr() {
		return String.valueOf(getUserInfo().getId());
	}


	/**
	 * 是否最大管理员
	 * @return
	 */
	public static boolean isSupAdmin(){
		return  hasRole(Constant.SU_ADMIN);
	}

	/**
	 * 是否管理租户
	 * @return
	 */
	public static boolean isSuperTenant(){
		return getUserInfo().getSuperTenant();
	}

	/**
	 * 用户账号
	 * @return
	 */
	public static String getLoginName() {
		return getUserInfo().getUsername();
	}

	/**
	 * 获取登录者公司名称
	 * @return
	 */
	public static String getCompName(){
		return ConstantFactory.me().getCompName(UserUtils.getUserId());
	}

	/**
	 * 获取登录用户名称
	 * @return
	 */
	public static String getName() {
		return getUserInfo().getName();
	}
	
	/**
	 * 获取登录用户部门名称
	 * @return
	 */
	public static String getDeptName() {
		return  ConstantFactory.me().getDeptName(getUserId());
	}

	/**
	 * 校验
	 * @param roleKey
	 * @return
	 */
	public static boolean hasRole(String roleKey){
		return  getSubject().hasRole(roleKey);
	}

	/** 
	* @Title: hasAnyRoleKeys 
	* @return  boolean 
	* @Date: 2020年9月29日
	*/
	public static boolean hasAnyRoleKeys(String... keys) {
		for (String key : keys) {
			if (hasRole(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	* @Title: hasAnyRoleKey 
	* @Description: None of them will thow exception
	* @param keys  role key 
	* @Date: 2020年9月24日
	 */
	public static void hasAnyRoles(String... keys) {
		boolean flag = false;
		if (null!= keys && keys.length > 0 ) {
			for (String key : keys) {
				if (hasRole(key)) {
					flag = true;
					break;
				}
			}
		}
		if (!flag) {
			throw new AuthorizationException("No permission ---> it does not have role "+ StringUtils.join(keys, ","));
		}
	}

	public static void setSessionAttribute(Object key, Object value) {
		getSession().setAttribute(key, value);
	}

	public static Object getSessionAttribute(Object key) {
		return getSession().getAttribute(key);
	}

	public static void removeSessionAttribute(String key){
		getSession().removeAttribute(key);
	}

	/**
	 * 当前请求是否登录
	 * @return
	 */
	public static boolean isLogin() {
		Subject subject = SecurityUtils.getSubject();
		if(ToolUtil.isEmpty(subject) ||
				(!subject.isAuthenticated() && !subject.isRemembered())){
			return false;
		}
		return true;
	}

	/**
	 * 获取用户租户ID
	 * @return
	 */
	public static String getTenantId(){
		return getUserInfo().getTenantId();
	}

	public static void logout() {
		SecurityUtils.getSubject().logout();
	}

	public static String getKaptcha(String key) {
		Object kaptcha = getSessionAttribute(key);
		if (kaptcha == null) {
			throw new RxcException(ToolUtil.message("sys.login.code.invalid"),"50004");
		}
		getSession().removeAttribute(key);
		return kaptcha.toString();
	}

	public static boolean isContains(List<Object> roles, String Key){
		boolean flag = false;
		for(Object o: roles){
			SysRoleEntity role = (SysRoleEntity) o;
			if(role.getRoleKey().equals(Key)){
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static List<String> getRoleKey(List<Object> roles){
		List<String> list  = new ArrayList<>();
		for(Object o: roles){
			SysRoleEntity role = (SysRoleEntity) o;
			list.add(role.getRoleKey());
		}
		return list;
	}

	/**
	 * 生成随机盐
	 */
	public static String randomSalt(){
		return RandomStringUtils.randomAlphanumeric(20);
	}


}
