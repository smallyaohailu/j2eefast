/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.config.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.j2eefast.common.core.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * sys_lang
 * @author: ZhouZhou
 * @date 2021-10-12 21:04:20
 */
@Getter
@Setter
@TableName("sys_lang")
public class SysLangEntity extends BaseEntity{

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;

    /** 归属模块编码 */
    private String moduleCode;

    /** 语言编码 */
    private String langCode;

    /** 语言编码 */
    private String langType;

    /** 语言译文 */
    private String langText;

    /** 模块名称 */
    private String moduleName;

}
