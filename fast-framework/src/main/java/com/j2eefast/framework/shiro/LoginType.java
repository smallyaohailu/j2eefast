/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro;

/**
 * 弃用
 * @author huanzhou
 */
@Deprecated
public enum LoginType {
    /**
     * 密码登录
     */
    NORMAL("User"),
    /**
     * 免密码登录
     */
    FREE("Free");

    private String desc;

    LoginType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
