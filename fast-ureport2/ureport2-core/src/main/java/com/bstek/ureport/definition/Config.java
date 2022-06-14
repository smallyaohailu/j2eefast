package com.bstek.ureport.definition;

import java.io.Serializable;

/**
 * xml文件相关配置参数
 */
public class Config implements Serializable {

    //名称
    private String name;
    //下载文件名
    private String downName;
    //支持控制方法
    private String func;
    //权限字符
    private String roleKeys;
    //权限表名别称
    private String tableAlias;
    //表描述
    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDownName() {
        return downName;
    }

    public void setDownName(String downName) {
        this.downName = downName;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getRoleKeys() {
        return roleKeys;
    }

    public void setRoleKeys(String roleKeys) {
        this.roleKeys = roleKeys;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
