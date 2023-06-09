/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.sys.entity.SysMsgPushUserEntity;
import com.j2eefast.framework.sys.mapper.SysMsgPushUserMapper;
import com.j2eefast.framework.utils.Constant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 推送用户Service接口
 * @author: ZhouZhou
 * @date 2021-02-22 21:04:41
 */
@Service
public class SysMsgPushUserService extends ServiceImpl<SysMsgPushUserMapper,SysMsgPushUserEntity> {

	@Resource
	private SysMsgPushUserMapper sysMsgPushUserMapper;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<SysMsgPushUserEntity> queryWrapper = new QueryWrapper<SysMsgPushUserEntity>();
		String msgId = (String) params.get("msgId");
        queryWrapper.eq(ToolUtil.isNotEmpty(msgId), "msg_id", msgId);
		String userCode = (String) params.get("userCode");
        queryWrapper.eq(ToolUtil.isNotEmpty(userCode), "user_code", userCode);
		String userName = (String) params.get("userName");
        queryWrapper.like(ToolUtil.isNotEmpty(userName), "user_name", userName);
		String isRead = (String) params.get("isRead");
        queryWrapper.eq(ToolUtil.isNotEmpty(isRead), "is_read", isRead);
		String readDate = (String) params.get("readDate");
        queryWrapper.eq(ToolUtil.isNotEmpty(readDate), "read_date", readDate);
		Page<SysMsgPushUserEntity> page = sysMsgPushUserMapper.selectPage(new Query<SysMsgPushUserEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,SysMsgPushUserEntity sysMsgPushUserEntity) {
		Page<SysMsgPushUserEntity> page = sysMsgPushUserMapper.findPage(new Query<SysMsgPushUserEntity>(params).getPage(),
					sysMsgPushUserEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<SysMsgPushUserEntity> findList(SysMsgPushUserEntity sysMsgPushUserEntity){
		return sysMsgPushUserMapper.findList(sysMsgPushUserEntity);
	}

	public List<SysMsgPushUserEntity> findList(Long msgId){
		return this.list(new QueryWrapper<SysMsgPushUserEntity>().eq("msg_id",msgId));
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
	public boolean delSysMsgPushUserById(Long id) {
		return this.removeById(id);
	}


	public boolean delSysMsgPushUserByMsgId(Long msgId){
		return this.remove(new QueryWrapper<SysMsgPushUserEntity>().eq("msg_id",msgId));
	}

	/**
     * 保存
     */
	public boolean addSysMsgPushUser(SysMsgPushUserEntity sysMsgPushUser){
		if(this.save(sysMsgPushUser)){
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	public boolean updateSysMsgPushUserById(SysMsgPushUserEntity sysMsgPushUser) {
		if(this.updateById(sysMsgPushUser)){
			return true;
		}
		return false;
	}


	public boolean setIsRead(String isRead, Long msgId, Long userId){
		return this.baseMapper.setIsRead(isRead,msgId,userId) > 0;
	}

	/**
     * 根居ID获取对象
     */
	public SysMsgPushUserEntity findSysMsgPushUserById(Long id){
		return sysMsgPushUserMapper.findSysMsgPushUserById(id);
	}


}
