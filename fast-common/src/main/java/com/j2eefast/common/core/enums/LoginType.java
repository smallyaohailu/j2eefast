/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.enums;

/**
 * 登录类型
 * @author huanzhou
 */
public enum LoginType {
    /**
     * 账号密码登录
     */
    USERNAME("UserName"),

    /**
     * 第三方登录
     */
    OTHER("Other"),
	
    /**
     * 手机验证码登录
     */
    MOBILE("Mobile");

    private String desc;

    LoginType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
