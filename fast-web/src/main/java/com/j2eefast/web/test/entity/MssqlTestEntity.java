/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.j2eefast.common.core.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * mssql_test
 * @author: ZhouZhou
 * @date 2021-08-31 22:02:57
 */
@Getter
@Setter
@TableName("mssql_test")
public class MssqlTestEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /** 姓名 */
    private String name;

    /** 电子邮件 */
    private String email;

    /** 电话 */
    private String phone;

}
