/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.redis.SysConfigRedis;
import com.j2eefast.framework.sys.entity.SysDictDataEntity;
import com.j2eefast.framework.sys.mapper.SysDictDataMapper;
import com.j2eefast.framework.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据字典
 */
@Service
public class SysDictDataService  extends ServiceImpl<SysDictDataMapper,SysDictDataEntity> {

	@Autowired
	private SysConfigRedis sysConfigRedis;

	/**
	 * 页面展示查询翻页
	 */
	public PageUtil findPage(Map<String, Object> params) {
		String dictLabel = (String) params.get("dictLabel");
		String dictType = (String) params.get("dictType");
		String status = (String) params.get("status");
		Page<SysDictDataEntity> page = this.baseMapper.selectPage(new Query<SysDictDataEntity>(params).getPage(),
				new QueryWrapper<SysDictDataEntity>().like(ToolUtil.isNotEmpty(dictLabel),
						"dict_label", dictLabel).like(ToolUtil.isNotEmpty(dictType),
						"dict_type", dictType).like(ToolUtil.isNotEmpty(status),"status",status));
		return new PageUtil(page);
	}

//	public List<SysDictDataEntity> selectDictDataByType(String dictType) {
//		List<SysDictDataEntity>  list = sysConfigRedis.getRedisDict(dictType);
//		if(ToolUtil.isEmpty(list)){
//			list =  this.list(new QueryWrapper<SysDictDataEntity>().eq("dict_type",dictType).
//					eq("status","0").orderBy(true, true, "dict_sort"));
//			sysConfigRedis.saveOrUpdateDict(dictType,list);
//			return list;
//		}else{
//			com.alibaba.fastjson.JSONArray array= com.alibaba.fastjson.JSONArray.parseArray(JSON.toJSONString(list));
//			list = JSONObject.parseArray(array.toJSONString(), SysDictDataEntity.class);
//			return list;
//		}
//	}
//
//	public String selectDictLabel(String dictType, String dictValue) {
//		List<SysDictDataEntity>  list = sysConfigRedis.getRedisDict(dictType);
//		if(ToolUtil.isEmpty(list)){
//			return this.baseMapper.selectDictLabel(dictType,dictValue);
//		}else{
//			String r = "";
//			JSONArray jsonArray = JSONUtil.parseArray(list,false);
//			List<SysDictDataEntity> list1 = jsonArray.toList(SysDictDataEntity.class);
//			for(SysDictDataEntity dict: list1){
//				if(dict.getDictValue().equals(dictValue)){
//					r = dict.getDictLabel();
//					break;
//				}
//			}
//			return r;
//		}
//	}

	public List<SysDictDataEntity> selectDictDataByType(String dictType) {
		//1.先获取系统字典
		List<SysDictDataEntity>  list = Constant.SYS_DICT.get(dictType);
		if(ToolUtil.isNotEmpty(list)){
			return list;
		}
		//2.获取缓存
		list = sysConfigRedis.getRedisDict(dictType);
		if(ToolUtil.isEmpty(list)){
			list =  this.list(new QueryWrapper<SysDictDataEntity>().eq("dict_type",dictType).
					eq("status","0").orderBy(true, true, "dict_sort"));
			sysConfigRedis.saveOrUpdateDict(dictType,list);
			return list;
		}else{
			com.alibaba.fastjson.JSONArray array= com.alibaba.fastjson.JSONArray.parseArray(JSON.toJSONString(list));
			list = JSONObject.parseArray(array.toJSONString(), SysDictDataEntity.class);
			return list;
		}
	}

	/**
	 * 添加/更新 系统缓存字典
	 * @param dictType
	 * @param dictData
	 */
	public void saveOrUpdateSysDict(String dictType, SysDictDataEntity dictData){
		ConfigConstant.DICT_TAG = RandomUtil.randomString(5);
		List<SysDictDataEntity>  list = Constant.SYS_DICT.get(dictType);
		if(ToolUtil.isEmpty(list)){
			list = new ArrayList<>();
			if(dictData.getStatus().equals("0")){
				list.add(dictData);
				Constant.SYS_DICT.put(dictType,list);
			}
		}else{
			//循环匹配找到删除
			for (Iterator<SysDictDataEntity> dicts = list.iterator(); dicts.hasNext();) {
				SysDictDataEntity dict =  dicts.next();
				if(StrUtil.equalsIgnoreCase(dict.getDictValue(), dictData.getDictValue())){
					dicts.remove();
				}
			}
			if(dictData.getStatus().equals("0")){
				list.add(dictData);
				Constant.SYS_DICT.put(dictType,list);
			}
		}
	}

	public void delByIdSysDict(String dictType, Long id){
		ConfigConstant.DICT_TAG = RandomUtil.randomString(5);
		List<SysDictDataEntity>  list = Constant.SYS_DICT.get(dictType);
		if(ToolUtil.isNotEmpty(list)){
			for (Iterator<SysDictDataEntity> dicts = list.iterator(); dicts.hasNext();) {
				SysDictDataEntity dict =  dicts.next();
				if(id.equals(dict.getId())){
					dicts.remove();
				}
			}
		}
	}

	public void delByValueSysDict(String dictType, String dictValue){
		ConfigConstant.DICT_TAG = RandomUtil.randomString(5);
		List<SysDictDataEntity>  list = Constant.SYS_DICT.get(dictType);
		if(ToolUtil.isNotEmpty(list)){
			for (Iterator<SysDictDataEntity> dicts = list.iterator(); dicts.hasNext();) {
				SysDictDataEntity dict =  dicts.next();
				if(dictValue.equals(dict.getDictValue())){
					dicts.remove();
				}
			}
		}
	}

	public String selectDictLabel(String dictType, String dictValue) {
		List<SysDictDataEntity>  list = Constant.SYS_DICT.get(dictType);
		if(ToolUtil.isEmpty(list)){
			list = sysConfigRedis.getRedisDict(dictType);
			if(ToolUtil.isEmpty(list)){
				return this.baseMapper.selectDictLabel(dictType,dictValue);
			}
		}
		String r = "";
		JSONArray jsonArray = JSONUtil.parseArray(list,false);
		List<SysDictDataEntity> list1 = jsonArray.toList(SysDictDataEntity.class);
		for(SysDictDataEntity dict: list1){
			if(dict.getDictValue().equals(dictValue)){
				r = dict.getDictLabel();
				break;
			}
		}
		return r;
	}

	public long countDictDataByType(String dictType) {
		return this.count(new QueryWrapper<SysDictDataEntity>().eq("dict_type",dictType));
	}

	public boolean updateDictData(SysDictDataEntity dictData) {
		boolean r =  this.updateById(dictData);
		List<SysDictDataEntity>  list = this.list(new QueryWrapper<SysDictDataEntity>().eq("dict_type",dictData.getDictType()).
				eq("status","0").orderBy(true, true, "dict_sort"));
		sysConfigRedis.saveOrUpdateDict(dictData.getDictType(),list);
		return r;
	}

	public boolean deleteBatchByIds(Long[] ids) {
		SysDictDataEntity dict = this.getById(ids[0]);
		boolean r = this.baseMapper.deleteDictDataByIds(ids) > 0;
		List<SysDictDataEntity>  list = this.list(new QueryWrapper<SysDictDataEntity>().eq("dict_type",dict.getDictType()).
				eq("status","0").orderBy(true, true, "dict_sort"));
		if(ToolUtil.isEmpty(list)){
			sysConfigRedis.delRedisDict(dict.getDictType());
		}else{
			sysConfigRedis.saveOrUpdateDict(dict.getDictType(),list);
		}
		return r;
	}

	public String getDictDefaultLabelValue(String dictType) {
		String r ="";
		List<SysDictDataEntity>  list = sysConfigRedis.getRedisDict(dictType);
		if(ToolUtil.isEmpty(list)){
			list = this.list(new QueryWrapper<SysDictDataEntity>().eq("dict_type",dictType).
					eq("status","0").orderBy(true, true, "dict_sort"));
			if(ToolUtil.isNotEmpty(list)){
				for(SysDictDataEntity dict: list){
					if(dict.getIsDefault().equals("Y")){
						r = dict.getDictValue();
						break;
					}
				}
			}
		}else{
			JSONArray jsonArray = JSONUtil.parseArray(list,false);
			List<SysDictDataEntity> list1 = jsonArray.toList(SysDictDataEntity.class);
			for(SysDictDataEntity dict: list1){
				if(dict.getIsDefault().equals("Y")){
					r = dict.getDictLabel();
					break;
				}
			}
		}
		return  r;
	}

}
