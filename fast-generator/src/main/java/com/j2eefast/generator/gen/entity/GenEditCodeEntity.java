/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.generator.gen.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * @author huanzhou
 */
@TableName("gen_edit_code")
@Data
public class GenEditCodeEntity {

    @TableId
    private Long id;

    /**
     * 路径MD5信息
     */
    private String md5;


    /**
     * 文件名
     */
    private String name;

    /**
     * 后缀名
     */
    private String suffix;

    /**
     * 主表ID
     */
    private Long tableId;

    /**
     * 模板信息
     */
    private byte[] template;

    @TableField(exist = false)
    private String templateValue;

    /**
     * 模板路径
     */
    private String path;


    /** 创建时间 */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date createTime;


    /** 更新时间 */
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date updateTime;

}
