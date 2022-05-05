package com.j2eefast.framework.shiro.token;

import org.apache.shiro.authc.RememberMeAuthenticationToken;

/**
 * 第三方登录Token
 * @author huanzhou
 * @date 2021-09-24
 */
public class OtherToken implements RememberMeAuthenticationToken{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 第三方OPENID
	 */
	private String openId;
	
	/**
	 * 租户ID
	 */
	private String tenantId;
	
	/**
	 * 记住我登录
	 */
    private boolean rememberMe = false;

    public OtherToken() {
	}
    
    public OtherToken( final String openId, final String tenantId) {
    	this(openId, tenantId, false);
	}
    
    public OtherToken( final String openId, final String tenantId,final boolean rememberMe ) {
    	this.openId = openId;
    	this.rememberMe = rememberMe;
    	this.tenantId = tenantId;
	}
    
    
    @Override
	public Object getPrincipal() {
		return getOpenId();
	}

	@Override
	public Object getCredentials() {
		return getOpenId();
	}
    
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

}
