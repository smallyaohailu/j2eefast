/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j2eefast.common.core.page.Query;
import com.j2eefast.common.core.utils.PageUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.sys.entity.SysMsgCcUserEntity;
import com.j2eefast.framework.sys.entity.SysMsgPushEntity;
import com.j2eefast.framework.sys.entity.SysMsgPushUserEntity;
import com.j2eefast.framework.sys.mapper.SysMsgPushMapper;
import com.j2eefast.framework.utils.Constant;
import com.j2eefast.framework.utils.FileUploadUtils;
import com.j2eefast.framework.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 内部消息Service接口
 * @author: ZhouZhou
 * @date 2021-02-21 22:43:07
 */
@Service
public class SysMsgPushService extends ServiceImpl<SysMsgPushMapper,SysMsgPushEntity> {

	@Resource
	private SysMsgPushMapper sysMsgPushMapper;
	@Resource
	private SysMsgPushUserService sysMsgPushUserService;
	@Resource
	private SysMsgCcUserService sysMsgCcUserService;

	/**
	 * mybaitis-plus 单表页面分页查询
     */
	public PageUtil findPage(Map<String, Object> params) {
		QueryWrapper<SysMsgPushEntity> queryWrapper = new QueryWrapper<SysMsgPushEntity>();
		String msgTitle = (String) params.get("msgTitle");
        queryWrapper.eq(ToolUtil.isNotEmpty(msgTitle), "msg_title", msgTitle);
		String msgLevel = (String) params.get("msgLevel");
        queryWrapper.eq(ToolUtil.isNotEmpty(msgLevel), "msg_level", msgLevel);
		String isComments = (String) params.get("isComments");
        queryWrapper.eq(ToolUtil.isNotEmpty(isComments), "is_comments", isComments);
		String sendUserName = (String) params.get("sendUserName");
        queryWrapper.eq(ToolUtil.isNotEmpty(sendUserName), "send_user_name", sendUserName);
		Page<SysMsgPushEntity> page = sysMsgPushMapper.selectPage(new Query<SysMsgPushEntity>(params).getPage(), queryWrapper);
		return new PageUtil(page);
    }

	/**
	 * 自定义分页查询，含关联实体对像
	 */
	public PageUtil findPage(Map<String, Object> params,SysMsgPushEntity sysMsgPushEntity) {
		Page<SysMsgPushEntity> page = sysMsgPushMapper.findPage(new Query<SysMsgPushEntity>(params).getPage(),
					sysMsgPushEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	public PageUtil findUserPage(Map<String, Object> params,SysMsgPushEntity sysMsgPushEntity) {
		Page<SysMsgPushEntity> page = sysMsgPushMapper.findUserPage(new Query<SysMsgPushEntity>(params).getPage(),
				sysMsgPushEntity,
				(String) params.get(Constant.SQL_FILTER));
		return new PageUtil(page);
	}

	/**
	* 查列表
	*/
	public List<SysMsgPushEntity> findList(SysMsgPushEntity sysMsgPushEntity){
		return sysMsgPushMapper.findList(sysMsgPushEntity);
	}

	public List<SysMsgPushEntity> findUserList(SysMsgPushEntity sysMsgPushEntity){
		return sysMsgPushMapper.findUserList(sysMsgPushEntity);
	}

	/**
     * 批量删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteBatchByIds(Long[] ids) {
		for (Long id : ids) {
			FileUploadUtils.me().removeFileUpload(id,"sys_msg_push_file");
		}
		return this.removeByIds(Arrays.asList(ids));
	}

	/**
     * 单个删除
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean delSysMsgPushById(Long id) {
		FileUploadUtils.me().removeFileUpload(id,"sys_msg_push_file");
		return this.removeById(id);
	}

	/**
     * 保存
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean addSysMsgPush(SysMsgPushEntity sysMsgPush){

		sysMsgPush.setSendUserId(UserUtils.getUserId());
		sysMsgPush.setSendUserName(UserUtils.getUserInfo().getName());
		sysMsgPush.setPushDate(new Date());
		if(this.save(sysMsgPush)){

			//更新关联附件信息
			Long pkId =  sysMsgPush.getId();
			FileUploadUtils.saveFileUpload(pkId,"sys_msg_push_file");

			//保存发送用户
			if(ToolUtil.isNotEmpty(sysMsgPush.getUserCode())){
				for(int i=0; i< sysMsgPush.getUserCode().length; i++){
					SysMsgPushUserEntity  sysMsgPushUserEntity = new SysMsgPushUserEntity();
					sysMsgPushUserEntity.setMsgId(pkId);
					sysMsgPushUserEntity.setUserId(sysMsgPush.getUserCode()[i]);
					sysMsgPushUserEntity.setUserName(sysMsgPush.getUserCodeName()[i]);
					sysMsgPushUserEntity.setIsRead("0");
					sysMsgPushUserService.save(sysMsgPushUserEntity);
				}
			}

			//保存抄送用户
			if(ToolUtil.isNotEmpty(sysMsgPush.getCcUserCode())){
				for(int i=0; i< sysMsgPush.getCcUserCode().length; i++){
					SysMsgCcUserEntity sysMsgCcUserEntity = new SysMsgCcUserEntity();
					sysMsgCcUserEntity.setMsgId(pkId);
					sysMsgCcUserEntity.setUserId(sysMsgPush.getCcUserCode()[i]);
					sysMsgCcUserEntity.setUserName(sysMsgPush.getCcUserCodeName()[i]);
					sysMsgCcUserEntity.setIsRead("0");
					sysMsgCcUserService.save(sysMsgCcUserEntity);
				}
			}
			return true;
		}
        return false;
    }

	/**
     * 修改根居ID
     */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateSysMsgPushById(SysMsgPushEntity sysMsgPush) {
		sysMsgPush.setPushDate(new Date());
		if(this.updateById(sysMsgPush)){
			//更新关联附件信息
			Long pkId =  sysMsgPush.getId();
			FileUploadUtils.saveFileUpload(pkId,"sys_msg_push_file");

			sysMsgPushUserService.delSysMsgPushUserByMsgId(sysMsgPush.getId());
			sysMsgCcUserService.delSysMsgCcUserByMsgId(sysMsgPush.getId());

			//保存发送用户
			if(ToolUtil.isNotEmpty(sysMsgPush.getUserCode())){
				for(int i=0; i< sysMsgPush.getUserCode().length; i++){
					SysMsgPushUserEntity  sysMsgPushUserEntity = new SysMsgPushUserEntity();
					sysMsgPushUserEntity.setMsgId(pkId);
					sysMsgPushUserEntity.setUserId(sysMsgPush.getUserCode()[i]);
					sysMsgPushUserEntity.setUserName(sysMsgPush.getUserCodeName()[i]);
					sysMsgPushUserEntity.setIsRead("0");
					sysMsgPushUserService.save(sysMsgPushUserEntity);
				}
			}

			//保存抄送用户
			if(ToolUtil.isNotEmpty(sysMsgPush.getCcUserCode())){
				for(int i=0; i< sysMsgPush.getCcUserCode().length; i++){
					SysMsgCcUserEntity sysMsgCcUserEntity = new SysMsgCcUserEntity();
					sysMsgCcUserEntity.setMsgId(pkId);
					sysMsgCcUserEntity.setUserId(sysMsgPush.getCcUserCode()[i]);
					sysMsgCcUserEntity.setUserName(sysMsgPush.getCcUserCodeName()[i]);
					sysMsgCcUserEntity.setIsRead("0");
					sysMsgCcUserService.save(sysMsgCcUserEntity);
				}
			}
			return true;
		}
		return false;
	}


	/**
	 * 读取合规性检查
	 * @param msgId 消息ID
	 * @param userId 用户ID
	 * @return
	 */
	public boolean readComplianceInspection(Long msgId, Long userId){
		long pushCount =  sysMsgPushUserService.count(new QueryWrapper<SysMsgPushUserEntity>()
				.eq("msg_id",msgId).eq("user_id",userId));
		long ccCount =  sysMsgCcUserService.count(new QueryWrapper<SysMsgCcUserEntity>()
				.eq("msg_id",msgId).eq("user_id",userId));
		long count =  this.count(new QueryWrapper<SysMsgPushEntity>()
				.eq("id",msgId).eq("send_user_id",userId));
		if(pushCount > 0 || ccCount > 0
				|| count > 0){
			return true;
		}
		return false;
	}

	/**
	 * 修改已读标志
	 * @param msgId
	 * @param userId
	 * @return
	 */
	public boolean updateReadTag(Long msgId, Long userId){
		if(sysMsgPushUserService.count(new QueryWrapper<SysMsgPushUserEntity>()
				.eq("msg_id",msgId).eq("user_id",userId)) > 0){
			return  sysMsgPushUserService.setIsRead("1",msgId,userId);
		}else{
			return sysMsgCcUserService.update(new UpdateWrapper<SysMsgCcUserEntity>()
					.set("is_read","1").eq("msg_id",msgId).eq("user_id",userId));
		}
	}



	/**
     * 根居ID获取对象
     */
	public SysMsgPushEntity findSysMsgPushById(Long id){
		return sysMsgPushMapper.findSysMsgPushById(id);
	}


}
