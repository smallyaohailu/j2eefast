/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.constant.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.constants.Cache;
import com.j2eefast.common.core.constants.CacheKey;
import com.j2eefast.common.core.utils.RedisUtil;
import com.j2eefast.common.core.utils.SpringUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.log.entity.SysLoginInfoEntity;
import com.j2eefast.framework.log.mapper.SysLoginInfoMapper;
import com.j2eefast.framework.sys.entity.SysMenuEntity;
import com.j2eefast.framework.sys.entity.SysModuleEntity;
import com.j2eefast.framework.sys.entity.SysRoleEntity;
import com.j2eefast.framework.sys.entity.SysUserEntity;
import com.j2eefast.framework.sys.mapper.SysModuleMapper;
import com.j2eefast.framework.sys.mapper.SysRoleMapper;
import com.j2eefast.framework.sys.mapper.SysUserMapper;
import com.j2eefast.framework.sys.service.SysFileService;
import com.j2eefast.framework.sys.service.SysFileUploadService;
import com.j2eefast.framework.sys.service.SysMenuService;
import com.j2eefast.framework.utils.Constant;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * <p>获取方法</p>
 *
 * @author: zhouzhou
 * @date: 2019-04-02 13:13
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Component
@DependsOn("springUtil")
public class ConstantFactory implements IConstantFactory {

	private SysRoleMapper sysRoleMapper = SpringUtil.getBean(SysRoleMapper.class);
	private SysUserMapper sysUserMapper = SpringUtil.getBean(SysUserMapper.class);
	private SysModuleMapper sysModuleMapper = SpringUtil.getBean(SysModuleMapper.class);
	private SysMenuService sysMenuService = SpringUtil.getBean(SysMenuService.class);
	private SysLoginInfoMapper sysLoginInfoMapper = SpringUtil.getBean(SysLoginInfoMapper.class);
	private  SysFileUploadService sysFileUploadService = SpringUtil.getBean(SysFileUploadService.class);
	private  SysFileService sysFileService = SpringUtil.getBean(SysFileService.class);
	public static IConstantFactory me() {
		return SpringUtil.getBean("constantFactory");
	}




	@Override
	public String getUserNameById(Long userId) {
		SysUserEntity sysUser = sysUserMapper.selectById(userId);
		if (ToolUtil.isNotEmpty(sysUser)) {
			return sysUser.getName();
		} else {
			return "--";
		}
	}

	@Override
	public String getUserUserNameById(Long userId) {
		SysUserEntity sysUser = sysUserMapper.selectById(userId);
		if (ToolUtil.isNotEmpty(sysUser)) {
			return sysUser.getUsername();
		} else {
			return "--";
		}
	}



	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.ROLES_NAME + "'+#userId")
	public String getRoleName(Long userId) {
		if (ToolUtil.isEmpty(userId)) {
			return "";
		}
		List<SysRoleEntity> roleList = sysRoleMapper.getRolesByUserId(userId);
		if(ToolUtil.isEmpty(roleList)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		roleList.forEach(role->{
			sb.append(role.getRoleName()).append(",");
		});
		return StrUtil.removeSuffix(sb.toString(),",");
	}

	@Override
	public List<Long> getRoleIds(Long userId) {
		if (ToolUtil.isEmpty(userId)) {
			return null;
		}
		List<SysRoleEntity>  roleList = sysRoleMapper.getRolesByUserId(userId);
		if(ToolUtil.isEmpty(roleList)) {
			return null;
		}
		List<Long> roleids = new ArrayList<>(roleList.size());
		roleList.forEach(role->{
			roleids.add(role.getId());
		});
		return roleids;
	}

	@Override
	public List<SysRoleEntity> getRoles(Long userId) {
		if (ToolUtil.isEmpty(userId)) {
			return null;
		}
		List<SysRoleEntity>  roleList = sysRoleMapper.getRolesByUserId(userId);
		if(ToolUtil.isEmpty(roleList)) {
			return null;
		}
		return roleList;
	}

	@Override
	@Cacheable(value = Cache.MODULES_CONSTANT, key = "'" + CacheKey.MODULES + "'+#userId")
	public List<Map<String, Object>> getModules(Long userId) {
//		List<SysRoleEntity> roles = this.getRoles(userId);
//		List<String> roleKey = new ArrayList<>();
//		roles.forEach(role->{
//			roleKey.add(role.getRoleKey());
//		});
		List<Map<String, Object>>  results;
		if(userId.equals(Constant.SUPER_ADMIN)){
			List<SysModuleEntity> modules = sysModuleMapper.findModules();
			results = new ArrayList<>(modules.size());
			modules.forEach(module->{
				Map<String, Object> map = BeanUtil.beanToMap(module);
				results.add(map);
			});
			return results;
		}else{
			//获取用户角色列表
			List<Long> roleList = ConstantFactory.me().getRoleIds(userId);
			List<SysModuleEntity> modules = sysModuleMapper.findModuleByRoleIds(roleList);
			results = new ArrayList<>(modules.size());
			modules.forEach(module->{
				Map<String, Object> map = BeanUtil.beanToMap(module);
				results.add(map);
			});
			return results;
		}
	}

	@Override
	@Cacheable(value = Cache.ROLECONSTANT, key = "'" + CacheKey.ROLE + "'+#roleId")
	public SysRoleEntity getRoleById(Long roleId) {
		if (0 == roleId) {
			return null;
		}
		SysRoleEntity roleObj = sysRoleMapper.selectById(roleId);
		if (ToolUtil.isNotEmpty(roleObj)) {
			return roleObj;
		}
		return null;
	}

	@Override
	@Cacheable(value = Cache.ROLECONSTANT, key = "'" + CacheKey.PERMISSIONS + "'+#userId")
	public Set<String> findPermissionsByUserId(Long userId) {

		//获取用户角色列表
		List<Long> roleList = getRoleIds(userId);

		//设置权限列表
		Set<String> permissionSet = new HashSet<>();
		for (Long roleId : roleList) {
			List<String> permissions = sysMenuService.findPermsByRoleId(roleId);
			if (permissions != null) {
				for (String permission : permissions) {
					if (ToolUtil.isNotEmpty(permission)) {
						String[] perm = StrUtil.splitToArray(permission,",");
						for(String s: perm){
							permissionSet.add(s);
						}
					}
				}
			}
		}

		return permissionSet;
	}

//	@Override
//	@Cacheable(value = Cache.ROLECONSTANT, key = "'" + CacheKey.SINGLE_ROLE_NAME + "'+#roleId")
//	public String getSingleRoleName(Long roleId) {
//		if (0 == roleId) {
//			return "--";
//		}
//		SysRoleEntity roleObj = sysRoleMapper.selectById(roleId);
//		if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getRoleName())) {
//			return roleObj.getRoleName();
//		}
//		return "";
//	}
//
//	@Override
//	@Cacheable(value = Cache.ROLECONSTANT, key = "'" + CacheKey.SINGLE_ROLE_KEY + "'+#roleId")
//	public String getSingleRoleKey(Long roleId) {
//		if (0 == roleId) {
//			return "--";
//		}
//		SysRoleEntity roleObj = sysRoleMapper.selectById(roleId);
//		if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getRoleKey())) {
//			return roleObj.getRoleKey();
//		}
//		return "";
//	}

	/**
	 * 根据用户ID获取用户部门名称
	 */
	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.DEPT_NAME + "'+#userId")
	public String getDeptName(Long userId) {
		if (ToolUtil.isEmpty(userId)) {
			return "";
		}
		String deptName = sysUserMapper.findDeptNameByUserId(userId);
		if (ToolUtil.isNotEmpty(deptName)) {
			return deptName;
		}
		return "";
	}

	@Override
	public String getStatusName(String status) {
		return null;
	}

	@Override
	public String getMenuStatusName(String status) {
		return null;
	}

	@Override
	@Cacheable(value = Cache.MENU_CONSTANT, key = "'" + CacheKey.MENU_NAME + "'+ T(String).valueOf(#userId).concat('-').concat(#moduleCode)")
	public List<SysMenuEntity> getMenuByUserIdModuleCode(Long userId, String moduleCode, LoginUserEntity user) {
		List<SysMenuEntity> menuList = sysMenuService.findUserModuleMenuList(userId,moduleCode,
				false);
		if(ToolUtil.isNotEmpty(menuList)){
			return menuList;
		}
		return null;
	}



	@Override
	public String getCacheObject(String para) {
		return null;
	}

	@Override
	public List<Long> getSubDeptId(Long deptId) {
		return null;
	}

	@Override
	public List<Long> getParentDeptIds(Long deptId) {
		return null;
	}

	@Override
	public String getPositionName(Long userId) {
		return null;
	}

	@Override
	public String getPositionIds(Long userId) {
		return null;
	}

	@Override
	@Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.COMP_NAME + "'+#userId")
	public String getCompName(Long userId) {
		if (ToolUtil.isEmpty(userId)) {
			return "";
		}
		String compName = sysUserMapper.findCompNameByUserId(userId);
		if (ToolUtil.isNotEmpty(compName)) {
			return compName;
		}
		return "";
	}

	@Override
	@CacheEvict(value=Cache.MENU_CONSTANT, allEntries=true)
	public void clearMenu() {
	}

	/**
	 * 根据用户ID删除
	 * @param userId
	 */
	@Override
	public void clearMenu(Long userId) {
		SpringUtil.getBean(RedisUtil.class).deletes(Cache.MODULES_CONSTANT +"::"+CacheKey.MODULES + userId);
	}

	@Override
	@CacheEvict(value=Cache.ROLECONSTANT, allEntries=true)
	public void clearRole() {
	}

	@Override
	@CacheEvict(value=Cache.ROLECONSTANT,key = "'" + CacheKey.PERMISSIONS + "'+#userId", allEntries=true)
	public void clearRole(Long userId) {

	}

	/**
	 * 根据用户id对应公司名称清除缓存
	 */
	@Override
	@CacheEvict(value = Cache.CONSTANT, key = "'" + CacheKey.COMP_NAME + "'+#userId", allEntries=true)
	public void clearCompName(Long userId) {
			
	}
	

	/**
	 * 根据用户id部门名称清除缓存
	 */
	@Override
	@CacheEvict(value = Cache.CONSTANT, key = "'" + CacheKey.DEPT_NAME + "'+#userId", allEntries=true)
	public void clearDeptName(Long userId) {
		
	}

	@Override
	@CacheEvict(value=Cache.MODULES_CONSTANT, allEntries=true)
	public void clearModules() {

	}

	@Override
	public void clearModules(Long userId) {
		SpringUtil.getBean(RedisUtil.class).deletes(Cache.MODULES_CONSTANT +"::"+CacheKey.MODULES + userId);
	}

	/**
	 * 根据用户ID清理用户缓存信息
	 */
	@Override
	public void clearUser(Long userId) {
		ConstantFactory.me().clearCompName(userId);
		ConstantFactory.me().clearDeptName(userId);
		ConstantFactory.me().clearMenu(userId);
		ConstantFactory.me().clearModules(userId);
		ConstantFactory.me().clearRole(userId);
	}


	@Override
	public SysLoginInfoEntity getFirstLoginInfo(String username) {
		SysLoginInfoEntity loginInfo = sysLoginInfoMapper.findFirstLoginInfo(username);
		if(ToolUtil.isEmpty(loginInfo)){
			loginInfo = new SysLoginInfoEntity();
			loginInfo.setLoginLocation("首次登陆");
			loginInfo.setLoginTime(DateUtil.date());
		}
		return loginInfo;
	}

	@Override
	public SysFileUploadService getSysFileUploadService() {
		return this.sysFileUploadService;
	}

	@Override
	public SysFileService getSysFileService() {
		return this.sysFileService;
	}
}
