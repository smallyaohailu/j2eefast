package com.j2eefast.common.core.enums;

public enum LogType {
	
	/**
	 * 系统账号密码登录
	 */
	SYS("sys"),
	
	/**
     * 第三方登录
     */
    OTHER("other"),
	
    /**
     * 手机验证码登录
     */
    MOBILE("mobile");
	
	private String vlaue;

	public String getVlaue() {
		return vlaue;
	}

	public void setVlaue(String vlaue) {
		this.vlaue = vlaue;
	}
	
	LogType(String value){
		this.vlaue = value;
	}
}
