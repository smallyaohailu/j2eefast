/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.service;

import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.web.test.entity.ExampleAsyncTreeEntity;
import com.j2eefast.web.test.entity.MssqlTestEntity;
import com.j2eefast.web.test.mapper.MssqlTestMapper;
import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.base.entity.Ztree;
import com.j2eefast.common.core.utils.SpringUtil;
import com.j2eefast.framework.sys.entity.SysCompEntity;
import com.j2eefast.framework.sys.service.SysCompService;
import com.j2eefast.framework.utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.j2eefast.common.core.mutidatasource.annotaion.DataSource;
import com.j2eefast.common.core.mutidatasource.DataSourceContextHolder;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.utils.Constant;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import javax.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * 数据库测试Service接口
 * @author: ZhouZhou
 * @date 2021-08-31 22:02:57
 */
@Service
public class MssqlTestService extends ServiceImpl<MssqlTestMapper,MssqlTestEntity> {

	@Resource
	private MssqlTestMapper mssqlTestMapper;
	@Autowired
	private ExampleAsyncTreeService exampleAsyncTreeService;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
    @DataSource(name="db2")
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<MssqlTestEntity> queryWrapper = new QueryWrapper<MssqlTestEntity>();
		String name = (String) params.get("name");
        queryWrapper.like(ToolUtil.isNotEmpty(name), "name", name);
		String email = (String) params.get("email");
        queryWrapper.eq(ToolUtil.isNotEmpty(email), "email", email);
		String phone = (String) params.get("phone");
        queryWrapper.eq(ToolUtil.isNotEmpty(phone), "phone", phone);
		Page<MssqlTestEntity> page = mssqlTestMapper.selectPage(new Query<MssqlTestEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	@DataSource(name="db2")
	public PageUtil findPage(Map<String, Object> params,MssqlTestEntity mssqlTestEntity) {
		Page<MssqlTestEntity> page = mssqlTestMapper.findPage(new Query<MssqlTestEntity>(params).getPage(),
					mssqlTestEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	@DataSource(name="db2")
	public List<MssqlTestEntity> findList(MssqlTestEntity mssqlTestEntity){
		return mssqlTestMapper.findList(mssqlTestEntity);
	}

	/**
     * 批量删除
     */
    @DataSource(name="db2")
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteBatchByIds(Long[] ids) {
		return this.removeByIds(Arrays.asList(ids));
	}

	/**
     * 单个删除
     */
    @DataSource(name="db2")
	@Transactional(rollbackFor = Exception.class)
	public boolean delMssqlTestById(Long id) {
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addMssqlTest(MssqlTestEntity mssqlTest){
		//切换数据源
		DataSourceContextHolder.setDataSourceType("db2");
		if(this.save(mssqlTest)){
			DataSourceContextHolder.clearDataSourceType();

			ExampleAsyncTreeEntity exampleAsyncTreeEntity = new ExampleAsyncTreeEntity();
			exampleAsyncTreeEntity.setName("测试");
			exampleAsyncTreeEntity.setFullName("测试000");
			exampleAsyncTreeEntity.setParentId(1340859479062753281L);
			exampleAsyncTreeService.save(exampleAsyncTreeEntity);
			throw new RxcException("测试JAT事务回滚","50001");
			//return true;
		}
		DataSourceContextHolder.clearDataSourceType();
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateMssqlTestById(MssqlTestEntity mssqlTest) {
		//切换数据源
		DataSourceContextHolder.setDataSourceType("db2");
		if(this.updateById(mssqlTest)){
			DataSourceContextHolder.clearDataSourceType();
			return true;
		}
		DataSourceContextHolder.clearDataSourceType();
		return false;
	}

	/**
     * 根居ID获取对象
     */
    @DataSource(name="db2")
	public MssqlTestEntity findMssqlTestById(Long id){
		return mssqlTestMapper.findMssqlTestById(id);
	}


}
