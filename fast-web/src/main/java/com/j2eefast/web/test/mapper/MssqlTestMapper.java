/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.mapper;

import com.j2eefast.web.test.entity.MssqlTestEntity;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * mssql_test Mapper接口
 * @author: ZhouZhou
 * @date 2021-08-31 22:02:57
 */
public interface MssqlTestMapper extends BaseMapper<MssqlTestEntity> {

    /**
     * 自定义分页查询
     * @param  page
     * @param  mssqlTestEntity 实体类
     */
    Page<MssqlTestEntity> findPage(IPage<MssqlTestEntity> page,
                                      @Param("mssqlTest") MssqlTestEntity mssqlTestEntity,
                                      @Param("sql_filter") String sql_filter);

    /**
     * 通过ID查询
     * @param id 查询ID
     * @return
     */
    MssqlTestEntity findMssqlTestById(@Param("id") Long id);

    /**
     * 查询列表
     * @param mssqlTestEntity 查询条件对象
     * @return
     */
    List<MssqlTestEntity> findList(MssqlTestEntity mssqlTestEntity);

}
