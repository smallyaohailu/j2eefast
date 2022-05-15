/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.config;

import java.io.Serializable;
import java.util.*;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.j2eefast.common.core.shiro.RedisCacheManager;
import com.j2eefast.common.core.shiro.RedisSessionDAO;
import com.j2eefast.common.core.shiro.ShiroSessionFactory;
import com.j2eefast.common.core.shiro.ShiroSessionManager;
import com.j2eefast.common.core.shiro.filter.InnerSessionFilter;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.shiro.RestLogoutFilter;
import com.j2eefast.framework.shiro.UserFilter;
import com.j2eefast.framework.shiro.auth.UserModularRealmAuthorizer;
import com.j2eefast.framework.shiro.realm.OtherRealm;
import com.j2eefast.framework.shiro.realm.UserNameRealm;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.j2eefast.framework.shiro.ShiroSessionListener;
import com.j2eefast.framework.shiro.auth.UserModularRealmAuthenticator;
import com.j2eefast.framework.shiro.realm.ValideCodeRealm;
import javax.servlet.Filter;

/**
 * Shiro的配置文件
 * @author huanzhou
 */
@Configuration
public class ShiroConfig {

	/**
	 * Session超时时间，单位为毫秒（默认30分钟）
	 */
	@Value("#{ @environment['shiro.session.expireTime'] ?: 30 }")
	private int expireTime;

	/**
	 * 设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 (默认设置5分钟)
	 */
	@Value("#{ @environment['shiro.session.validationTime'] ?: 5 }")
	private int validationTime;

	/**
	 * 设置Cookie的域名
	 */
	@Value("#{ @environment['shiro.cookie.domain'] ?: '' }")
	private String domain;
	
	/**
	 * 登录
	 */
	@Value("#{ @environment['shiro.loginUrl'] ?: '/login' }")
	private String loginUrl;
	
	@Value("#{ @environment['shiro.successUrl'] ?: '/index' }")
	private String successUrl;

	/**
	 * 设置cookie的有效访问路径设置与项目路径一直
	 */
	@Value("#{ @environment['server.servlet.context-path'] ?: '/' }")
	private String path;

	/**
	 * 设置HttpOnly属性
	 */
	@Value("#{ @environment['shiro.cookie.httpOnly'] ?: true }")
	private boolean httpOnly;

	/**
	 * 登录排挤踢人
	 */
	@Value("#{ @environment['shiro.session.kickoutAfter'] ?: false }")
	private boolean kickoutAfter;

//	@Value("#{ @environment['shiro.isHideLoginUrl'] ?: false }")
//	private boolean isHideLoginUrl;
	/**
	 * 是否开启记住我功能
	 */
	@Value("#{ @environment['shiro.rememberMe.enabled'] ?: false }")
	private boolean rememberMe;

	/**
	 * 密钥
	 */
	@Value("#{ @environment['shiro.rememberMe.cipherKey'] ?: '' }")
	private String cipherKey;

	/**
	 * 设置Cookie的过期时间，秒为单位
	 */
	@Value("#{ @environment['shiro.cookie.maxAge'] ?: 30 }")
	private int maxAge;

	/**
	 * 设置会话ID
	 */
	@Value("#{ @environment['shiro.session.sessionId'] ?: 'fast.session.id' }")
	private String sessionId;
	
	/**
	 * 系统是否开启多租户模式
	 */
	@Value("#{ @environment['fast.tenantModel.enabled'] ?: false }")
	private boolean enabled;

	/**
	 * Shiro授权认证配置 系统YML文件配置信息
	 */
	@Value("#{${shiro.filterMap}}")
	private LinkedHashMap<String, String> filterMap ;

	/**
	 * shiro缓存管理器;
	 * 需要添加到securityManager中
	 * @return
	 */
	@Bean
	public RedisCacheManager getRedisCacheManager() {
		RedisCacheManager redisCacheManager = new RedisCacheManager();
		//redis中针对不同用户缓存
		redisCacheManager.setPrincipalIdFieldName("username");
		//用户权限信息缓存时间
		redisCacheManager.setExpire(200000);
		return redisCacheManager;
	}

	//-----------------------------  登录Realm ------------------
	/**
	 * 账号密码登录 Realm
	 */
	@Bean
	public UserNameRealm userNameRealm() {
		UserNameRealm userNameRealm = new UserNameRealm();
		userNameRealm.setCacheManager(getRedisCacheManager());
		return userNameRealm;
	}

	/**
	 * 第三方授权登录 Realm
	 * @return
	 */
	@Bean
	public OtherRealm otherRealm() {
		OtherRealm otherRealm = new OtherRealm();
		otherRealm.setCacheManager(getRedisCacheManager());
		return otherRealm;
	}
	
	/**
	 * 手机验证码 Realm
	 * @date 2021-09-24
	 * @return
	 */
	@Bean
	public ValideCodeRealm valideCodeRealm() {
		ValideCodeRealm valideCodeRealm = new ValideCodeRealm();
		valideCodeRealm.setCacheManager(getRedisCacheManager());
		return valideCodeRealm;
	}
	//-------------------------------------------------------

	/**
	 * SessionDAO的作用是为Session提供CRUD并进行持久化的一个shiro组件
	 * MemorySessionDAO 直接在内存中进行会话维护
	 * EnterpriseCacheSessionDAO  提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
	 * @return
	 */
	@Bean("sessionDAO")
	public RedisSessionDAO sessionDAO() {
		RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
		redisSessionDAO.setSessionIdGenerator(new CustomSessionIdGenerator());
		return redisSessionDAO;
	}

	@Bean("sessionFactory")
	public ShiroSessionFactory sessionFactory(){
		ShiroSessionFactory sessionFactory = new ShiroSessionFactory();
		return sessionFactory;
	}

	/**
	 * 设置session cookie属性
	 * @return
	 */
	@Bean("sessionIdCookie")
    public SimpleCookie sessionIdCookie() {
		SimpleCookie cookie= new org.apache.shiro.web.servlet.SimpleCookie();
		cookie.setName(sessionId);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(-1);
		return cookie;
    }

	@Bean("sessionListener")
	public ShiroSessionListener sessionListener() {
		ShiroSessionListener sessionListener = new ShiroSessionListener();
		return sessionListener;
	}

	/**
	 * session 管理
	 * @return
	 */
	@Bean("sessionManager")
	public SessionManager sessionManager() {
		
		ShiroSessionManager sessionManager = new ShiroSessionManager();
		Collection<SessionListener> listeners = new ArrayList<>();
		// 配置监听
		listeners.add(sessionListener());
		
		sessionManager.setSessionListeners(listeners);

		sessionManager.setSessionIdCookieEnabled(true);
		
		sessionManager.setSessionIdCookie(sessionIdCookie());
		//redis缓存
		sessionManager.setSessionDAO(sessionDAO());

		sessionManager.setCacheManager(getRedisCacheManager());

		sessionManager.setSessionFactory(sessionFactory());
		
		// 设置session过期时间为1小时(单位：毫秒)，默认为30分钟
		sessionManager.setGlobalSessionTimeout(1000 * 60 * expireTime);
		// 是否开启删除无效的session对象 默认为true
		sessionManager.setDeleteInvalidSessions(true);
		// 是否开启定时调度器进行检测过期session 默认为true
		sessionManager.setSessionValidationSchedulerEnabled(true);
		// 设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
		sessionManager.setSessionValidationInterval(1000 * 60 * validationTime);
		// 去掉 JSESSIONID
		sessionManager.setSessionIdUrlRewritingEnabled(false);

		return sessionManager;
	}


	/**
	 * 系统自带的Realm管理，主要针对多realm 登录认证
	 */
	@Bean
	public ModularRealmAuthenticator modularRealmAuthenticator(){
		//使用重写的ModularRealmAuthenticator
		UserModularRealmAuthenticator modularRealmAuthenticator = new UserModularRealmAuthenticator();
		modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
		return modularRealmAuthenticator;
	}

	/**
	 * 系统自带的Realm管理，主要针对多realm 权限认证
	 */
	@Bean
	public ModularRealmAuthorizer modularRealmAuthorizer() {
		//自己重写的ModularRealmAuthorizer
		UserModularRealmAuthorizer modularRealmAuthenticator = new UserModularRealmAuthorizer();
		return modularRealmAuthenticator;
	}



	/**
	 * 安全管理器
	 * @return
	 */
	@Bean
	public SecurityManager securityManager() {
		
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

		securityManager.setAuthenticator(modularRealmAuthenticator());

		securityManager.setAuthorizer(modularRealmAuthorizer());

		List<Realm> realms = new ArrayList<>(3);
		
		//账号密码登录realm
		realms.add(userNameRealm());
		//第三方授权登录realm
		realms.add(otherRealm());
		//手机验证码登录realm
		realms.add(valideCodeRealm());
		// 设置realm.
		securityManager.setRealms(realms);

		
		// 记住我
		if(rememberMe){
			securityManager.setRememberMeManager(rememberMeManager());
		}else{
			securityManager.setRememberMeManager(null);
		}

		// 设置缓存管理
        securityManager.setCacheManager(getRedisCacheManager());

        // 设置session管理
        securityManager.setSessionManager(sessionManager());

		return securityManager;
	}



	/**
     * cookie 属性设置
     */
    public SimpleCookie rememberMeCookie(){
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAge * 24 * 60 * 60);
        return cookie;
    }
	
	/**
     * 记住我 设置密钥
     */
    public CookieRememberMeManager rememberMeManager(){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        
        // 判断yml文件是否配置密钥没有则随机生成 确保系统安全、禁止使用默认密钥Key
		if(ToolUtil.isEmpty(cipherKey)){
			cookieRememberMeManager.setCipherKey(ToolUtil.generateNewKey());
		}else{
			cookieRememberMeManager.setCipherKey(Base64.decode(cipherKey));
		}
        return cookieRememberMeManager;
    }

	/** 
	 * Shiro连接约束配置,即过滤链的定义
	 */
	@Bean
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		shiroFilter.setSecurityManager(securityManager);
		//登录认证失败
		shiroFilter.setLoginUrl(loginUrl);
		//这里的/index是后台的接口名,非页面,登录成功后要跳转的链接
		shiroFilter.setSuccessUrl(successUrl);

		LinkedHashMap<String, Filter> filtersMap = new LinkedHashMap<>();

		// ------------------- 系统必须默认
        filterMap.put("/login", "anon");
        filterMap.put("/static/**", "anon");
		filterMap.put("/swagger/**'", "anon");
		filterMap.put("/swagger-ui/**", "anon");
		filterMap.put("/swagger-resources/**", "anon");
		filterMap.put("/webjars/**", "anon");
		filterMap.put("/api/trade.receive/**", "anon");
		filterMap.put("/profile/fileUeditor/upload/image/**", "anon");
		filterMap.put("/sys/comm/download/**", "anon");
		filterMap.put("/authInsu", "anon");
		filterMap.put("/authInsu/uploadLic", "anon");
		filterMap.put("/ureport/res/**", "anon");
		filterMap.put("/ureport/shareview/**", "anon");
		filterMap.put("/v3/api-docs", "anon");
        filterMap.put("/favicon.ico", "anon");
		filterMap.put("/captcha.gif", "anon");
		filterMap.put("/getLoginValideCode", "anon");
		filterMap.put("/valideCodeLogin", "anon");
		if(enabled) {
			filterMap.put("/tenant/list", "anon");
		}
		filterMap.put("/captcha/**", "anon");
		//自定义拦截器限制并发人数
		filtersMap.put("inner", innerSessionFilter());
		filtersMap.put("logout", new RestLogoutFilter());
		filtersMap.put("user", new UserFilter());
		filterMap.put("/**", "user,inner");
		shiroFilter.setFilters(filtersMap);

		//授权认证配置
		if (null !=filterMap && filterMap.size() != 0) {			
			  shiroFilter.setFilterChainDefinitionMap(filterMap);
		}
		return shiroFilter;
	}

	/**
	 * 并发登录控制
	 * @return
	 */
	@Bean
	public InnerSessionFilter innerSessionFilter(){
		InnerSessionFilter innerSessionFilter = new InnerSessionFilter();
		//用于根据会话ID，获取会话进行踢出操作的；
		innerSessionFilter.setSessionManager(sessionManager());
		//是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；
		innerSessionFilter.setKickoutAfter(kickoutAfter);
		//被踢出后重定向到的地址；
		innerSessionFilter.setKickoutUrl(loginUrl+"?kickout=");

		return innerSessionFilter;
	}

	@Bean("lifecycleBeanPostProcessor")
	public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
		proxyCreator.setProxyTargetClass(true);
		return proxyCreator;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
			@Qualifier("securityManager") SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

	class CustomSessionIdGenerator implements SessionIdGenerator{
		@Override
		public Serializable generateId(Session session) {
			return IdUtil.fastSimpleUUID();
		}
	}
}
