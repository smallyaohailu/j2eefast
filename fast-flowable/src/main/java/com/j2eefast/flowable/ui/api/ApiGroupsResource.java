/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.flowable.ui.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.sys.entity.SysRoleEntity;
import com.j2eefast.framework.sys.entity.SysUserEntity;
import com.j2eefast.framework.sys.service.SysRoleService;
import com.j2eefast.framework.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.flowable.ui.common.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>候选组,职位</p>
 *
 * @author: zhouzhou
 * @date: 2020-04-17 22:48
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@RestController
@RequestMapping(value = "/flowable/app")
@Api("BPM前端接口")
@Slf4j
public class ApiGroupsResource {

//	@Autowired
//	private GroupService gupService;
//	@Autowired
//	private UserService userService;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysUserService sysUserService;


	/**
	 * 获取角色
	 * @param filter
	 * @return
	 */
	@RequestMapping(value = "/rest/editor-groups", method = RequestMethod.GET, produces = {"application/json"})
	@ApiOperation("获取角色")
	public ResultListDataRepresentation getGroups(@RequestParam(required = false, value = "filter") String filter) {

		List<SysRoleEntity>  roles = sysRoleService.list(new QueryWrapper<SysRoleEntity>().like(ToolUtil.isNotEmpty(filter),
				"role_name", filter));
		List<GroupRepresentation> result = new ArrayList<>();
		for(SysRoleEntity g: roles){
			RemoteGroup rGrop = new RemoteGroup();
			rGrop.setId(String.valueOf(g.getId()));
			rGrop.setName(g.getRoleName()+"("+g.getRoleKey()+")");
			rGrop.setType(g.getRoleKey());
			result.add(new GroupRepresentation(rGrop));
		}
		return new ResultListDataRepresentation(result);
	}


	/**
	 * 获取指派人
	 * @param filter
	 * @return
	 */
	@RequestMapping(value = "/rest/editor-users", method = RequestMethod.GET)
	@ApiOperation("获取指派人")
	public ResultListDataRepresentation getUsers(@RequestParam(value = "filter", required = false) String filter) {

		log.info("filter:" + filter);

		List<SysUserEntity> listUser = sysUserService.list(new QueryWrapper<SysUserEntity>().like(ToolUtil.isNotEmpty(filter),
				"name", filter));
		List<UserRepresentation> userRepresentations = new ArrayList<>(listUser.size());
		for(SysUserEntity user: listUser){
			RemoteUser remoteUser = new RemoteUser();
			remoteUser.setId(String.valueOf(user.getId()));
			remoteUser.setEmail(user.getEmail());
			remoteUser.setFirstName(user.getName()+"("+user.getId()+")");
			remoteUser.setTenantId(user.getTenantId());
			userRepresentations.add(new UserRepresentation(remoteUser));
		}
		//TODO 后期再设置权限问题
		return new ResultListDataRepresentation(userRepresentations);
	}

}
