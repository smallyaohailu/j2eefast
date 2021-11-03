package com.j2eefast.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 特殊角色处理
 * @author huanzhou
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
public @interface AuthData {

    /**
     * 角色权限字符
     * @return
     */
    String auth() default "";

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
}
