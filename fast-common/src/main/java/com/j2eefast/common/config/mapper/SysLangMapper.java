/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j2eefast.common.config.entity.SysLangEntity;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * sys_lang Mapper接口
 * @author: ZhouZhou
 * @date 2021-10-12 21:04:20
 */
public interface SysLangMapper extends BaseMapper<SysLangEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  sysLangEntity 实体类
     */
    Page<SysLangEntity> findPage(IPage<SysLangEntity> page,
                                      @Param("sysLang") SysLangEntity sysLangEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    SysLangEntity findSysLangById(@Param("id") Long id);

    /**
     * 查询列表
     * @param sysLangEntity 查询条件对象
     * @return
     */
    List<SysLangEntity> findList(SysLangEntity sysLangEntity);

}
