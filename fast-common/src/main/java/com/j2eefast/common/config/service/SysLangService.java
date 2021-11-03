/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.config.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j2eefast.common.config.entity.SysLangEntity;
import com.j2eefast.common.config.mapper.SysLangMapper;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 国际化配置Service接口
 * @author: ZhouZhou
 * @date 2021-10-12 21:04:20
 */
@Service
public class SysLangService extends ServiceImpl<SysLangMapper, SysLangEntity> {

	@Resource
	private SysLangMapper sysLangMapper;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<SysLangEntity> queryWrapper = new QueryWrapper<SysLangEntity>();
		String langCode = (String) params.get("langCode");
        queryWrapper.eq(ToolUtil.isNotEmpty(langCode), "lang_code", langCode);
		String langType = (String) params.get("langType");
        queryWrapper.eq(ToolUtil.isNotEmpty(langType), "lang_type", langType);
		Page<SysLangEntity> page = sysLangMapper.selectPage(new Query<SysLangEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,SysLangEntity sysLangEntity) {
		Page<SysLangEntity> page = sysLangMapper.findPage(new Query<SysLangEntity>(params).getPage(),
					sysLangEntity,
				(String) params.get(ConfigConstant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<SysLangEntity> findList(SysLangEntity sysLangEntity){
		return sysLangMapper.findList(sysLangEntity);
	}

	/**
     * 批量删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteBatchByIds(Long[] ids) {
		return this.removeByIds(Arrays.asList(ids));
	}

	/**
     * 单个删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean delSysLangById(Long id) {
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addSysLang(SysLangEntity sysLang){
		if(this.save(sysLang)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateSysLangById(SysLangEntity sysLang) {
		if(this.updateById(sysLang)){
			return true;
		}
		return false;
	}

	/**
     * 根居ID获取对象
     */
	public SysLangEntity findSysLangById(Long id){
		return sysLangMapper.findSysLangById(id);
	}


}
