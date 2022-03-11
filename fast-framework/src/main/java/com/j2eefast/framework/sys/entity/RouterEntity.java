package com.j2eefast.framework.sys.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

/**
 * 路由配置
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class RouterEntity {
    /**
     * 路由名字
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件地址
     */
    private String component;


    /**
     * 重定向地址，当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
     */
    private String redirect;


    /**
     * 其他元素
     */
    private MetaEntity meta;

    /**
     * 子路由
     */
    private List<RouterEntity> children;
}
