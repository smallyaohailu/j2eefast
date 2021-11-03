/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据过滤
 * @author huanzhou
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFilter {


    /**
     * 是否机构表 机构表权限查询特殊处理
     * @return
     */
    boolean isDeptTable() default false;

    /**
     * sys_comp表的别名
     */
    String compAlias() default "c";

    /**
     *
     * @return
     */
    String deptAlias() default  "u";

    /**
     * 用户表的别名
     */
    String userAlias() default "";


    AuthData[] auths() default {};

}
