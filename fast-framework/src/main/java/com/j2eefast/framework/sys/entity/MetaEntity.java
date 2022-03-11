package com.j2eefast.framework.sys.entity;

import lombok.Data;

/**
 * 路由显示信息
 */
@Data
public class MetaEntity {
    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    private String title;

    /**
     * 设置该路由的图标，对应路径src/assets/icons/svg
     */
    private String icon;


    /**
     * 是否隐藏
     */
    private boolean hideMenu;

    public MetaEntity(String title,String icon, boolean hideMenu ){
        this.title = title;
        this.icon = icon;
        this.hideMenu = hideMenu;
    }
}
