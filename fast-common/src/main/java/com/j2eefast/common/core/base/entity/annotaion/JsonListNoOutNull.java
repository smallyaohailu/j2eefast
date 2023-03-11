/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.base.entity.annotaion;

import java.lang.annotation.*;

/**
 * 分页List 不输入null字段
 * @author huanzhou
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JsonListNoOutNull {
}
