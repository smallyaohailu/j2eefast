/**
 * Copyright (c) 2021-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro.token;

import org.apache.shiro.authc.RememberMeAuthenticationToken;

/**
 * 自定义手机验证码登录Token
 * @author huanzhou
 * @date 2021-09-24
 */
public class MobileToken implements RememberMeAuthenticationToken{

	private static final long serialVersionUID = 1L;

	/**
	 * 手机号码
	 */
	private String mobile;
	
	/**
	 * 手机验证码
	 */
	private String valideCode;
	
	/**
	 * 记住我登录
	 */
    private boolean rememberMe = false;
	
	/**
     * JavaBeans compatible no-arg constructor.
     */
    public MobileToken() {
    }
    
    
    public MobileToken(final String mobile, final String valideCode){
    	this(mobile,valideCode,false);
    }
    
    public MobileToken(final String mobile, final String valideCode, final boolean rememberMe) {
    	this.mobile = mobile;
    	this.valideCode = valideCode;
    	this.rememberMe = rememberMe;
    }
    
    
    public void clear() {
        this.mobile = null;
        this.valideCode = null;
        this.rememberMe = false;
    }
	
 
	
	@Override
	public Object getPrincipal() {
		return getMobile();
	}

	@Override
	public Object getCredentials() {
		return getMobile();
	}

	@Override
	public boolean isRememberMe() {
		return rememberMe;
	}

	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getValideCode() {
		return valideCode;
	}

	public void setValideCode(String valideCode) {
		this.valideCode = valideCode;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

}
