/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.bussiness.aop;

import java.util.*;
import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.framework.annotation.AuthData;
import com.j2eefast.framework.sys.entity.SysRoleEntity;
import com.j2eefast.framework.utils.UserUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.framework.annotation.DataFilter;
import com.j2eefast.framework.utils.Constant;

/**
 * @Description:数据过滤，切面处理类
 * @author zhouzhou loveingowp@163.com
 * @time 2018-12-03 17:25
 */
@Order(5)
@Aspect
@Component
public class DataFilterAspect {

	@Pointcut("@annotation(com.j2eefast.framework.annotation.DataFilter)")
	public void dataFilterCut() {

	}

	@SuppressWarnings("unchecked")
	@Before("dataFilterCut()")
	public void dataFilter(JoinPoint point) throws Throwable {
		Object params = point.getArgs()[0];
		if (params != null && params instanceof Map) {

			Map map = (Map) params;
			// 先清空防止前端注入
			map.put(Constant.SQL_FILTER,StrUtil.EMPTY);

			// 特除业务 不需要数据过滤
			boolean _isADMIN =  ToolUtil.isEmpty(map.get("__ISADMIN"))?false: (boolean) map.get("__ISADMIN");

			LoginUserEntity user = UserUtils.getUserInfo();
			// 如果不是超级管理员，则进行数据过滤
			if (!user.getId().equals(Constant.SUPER_ADMIN)) {

				// 不需要数据过滤
				if(_isADMIN){
					return;
				}

				MethodSignature signature = (MethodSignature) point.getSignature();
				DataFilter dataFilter = signature.getMethod().getAnnotation(DataFilter.class);
				// 获取表的别名
				String compAlias = dataFilter.compAlias();
				String deptAlias = dataFilter.deptAlias();
				String userAlias = dataFilter.userAlias();
				boolean isDeptTable = dataFilter.isDeptTable();
				//特殊角色特殊处理
				AuthData[]	authData = dataFilter.auths();
				if(ToolUtil.isNotEmpty(authData)) {
					lableB:
					for(AuthData a: authData){
						for(Object o: user.getRoles()){
							SysRoleEntity role = (SysRoleEntity) o;
							if(role.getRoleKey().equals(a.auth())){
								compAlias = a.compAlias();
								deptAlias = a.deptAlias();
								userAlias = a.userAlias();
								break lableB;
							}
						}
					}
				}
				map.put(Constant.SQL_FILTER, getSQLFilter(user,
						compAlias,deptAlias,userAlias,
						(String[])map.get(Constant.REQUIRES_PERMISSIONS),isDeptTable));
			}
			return;
		}
		throw new RxcException("数据权限接口，只能是Map类型参数，且不能为NULL");
	}

	/**
	 * 获取数据过滤的SQL
	 * @param user 当前用户
	 * @param permissions 控制层权限字符
	 * @return
	 */
	public static String getSQLFilter(LoginUserEntity user,String compAlias,
									  String deptAlias,String userAlias
			,String[] permissions, boolean isDeptTable) {

		StringBuilder sqlFilter = new StringBuilder(StrUtil.EMPTY);
		if (ToolUtil.isEmpty(compAlias)) {
			return sqlFilter.toString();
		}
		//
//		List<String> dataScopes = new ArrayList<>();
//		List<Long> releIds = new ArrayList<>();
//
//		//获取当前用户 所有数据权限控制范围
//		List<Object> roles = user.getRoles();
//
//		int data = -1;
//		for(Object o: roles){
//			if(ToolUtil.isEmpty(permissions)){
//				for (Object key : map.keySet()) {
//					SysRoleEntity role = (SysRoleEntity) key;
//					if(data < Integer.parseInt(role.getDataScope())){
//						data = Integer.parseInt(role.getDataScope());
//					}
//					releIds.add(role.getId());
//				}
//			}else{
//				for(String s: permissions){
//					for (Map.Entry<Object, Object> entry : map.entrySet()) {
//						Set<String> tempSet = (Set<String>) entry.getValue();
//						if(tempSet.contains(s)){
//							SysRoleEntity role = (SysRoleEntity) entry.getKey();
//							if(data < Integer.parseInt(role.getDataScope())){
//								data = Integer.parseInt(role.getDataScope());
//							}
//							releIds.add(role.getId());
//						}
//					}
//				}
//			}
//		}
//		if(data != -1){
//			dataScopes.add(String.valueOf(data));
//		}
		//////

		for(Object o: user.getRoles()){

			SysRoleEntity role = (SysRoleEntity) o;

			String dataScope = role.getDataScope();

			//所有数据权限与未设置
			if (Constant.DATA_SCOPE_ALL.equals(dataScope)){
				break;
			}

			// 公司以下数据权限 如果是ADMIN 用户则直接查询本公司下所有数据
			if(Constant.DATA_SCOPE_COMP.equals(dataScope)
					|| UserUtils.hasRole(Constant.SU_ADMIN)){
				sqlFilter.append(StrUtil.format(
						(isDeptTable? " OR ({} IN ( SELECT id FROM sys_comp WHERE del_flag = '0' AND (id = {} OR FIND_IN_SET( {} , parent_ids ))))"
								:" OR ({} = {})"),
						StrUtil.contains(compAlias,StrUtil.DOT)?compAlias:compAlias+".id", user.getCompId(),user.getCompId()));
				if(UserUtils.hasRole(Constant.SU_ADMIN)) {
					break;
				}
			}

			if (Constant.DATA_SCOPE_NULL.equals(dataScope)){
				sqlFilter.append(" OR 1=0");
				break;
			}

			// 部门以下数据权限
			if(Constant.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)){
				sqlFilter.append(StrUtil.format(
						" OR ({} IN ( SELECT id FROM sys_comp WHERE del_flag = '0' AND (id = {} OR FIND_IN_SET( {} , parent_ids ))))",
						StrUtil.contains(deptAlias,StrUtil.DOT)?deptAlias:deptAlias+".id", user.getDeptId(), user.getDeptId()));
			}
			//本部门数据权限
			if(Constant.DATA_SCOPE_DEPT.equals(dataScope)){
				sqlFilter.append(StrUtil.format(
						" OR ({} = {})",
						StrUtil.contains(deptAlias,StrUtil.DOT)?deptAlias:deptAlias+".id", user.getDeptId()));
			}
			//自定义数据
			if (Constant.DATA_SCOPE_CUSTOM.equals(dataScope)){
				sqlFilter.append(StrUtil.format(
						" OR ({} IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {})) ", StrUtil.contains(deptAlias,StrUtil.DOT)?deptAlias:deptAlias+".id",
						role.getId()));
			}
			//仅本人数据权限
			if(Constant.DATA_SCOPE_SELF.equals(dataScope)){
				if(!userAlias.equals(StrUtil.EMPTY)){
					sqlFilter.append(StrUtil.format(
							" OR ({} = {})",
							StrUtil.contains(userAlias,StrUtil.DOT)?deptAlias:userAlias+".id", user.getId()));
				}else{
					// 如果注解不填表别名则不允许任何查询
					sqlFilter.append(" OR 1=0");
				}
			}
		}

		//角色不是ADMIN用户 排除ADMIN
		if(ToolUtil.isNotEmpty(userAlias)
				&& !UserUtils.hasRole(Constant.SU_ADMIN)){
			sqlFilter.append(StrUtil.format(" AND ({} NOT IN( SELECT user_id FROM sys_user_role WHERE role_id = 1 ))"
					,StrUtil.contains(userAlias,StrUtil.DOT)?userAlias:userAlias+".id"));
		}

		//角色是ADMIN 用户 排除 SUP_ADMIN
		if(ToolUtil.isNotEmpty(userAlias) &&
				UserUtils.hasRole(Constant.SU_ADMIN)){
			sqlFilter.append(StrUtil.format(" AND ({} != 1)"
					,StrUtil.contains(userAlias,StrUtil.DOT)?userAlias:userAlias+".id"));
		}

		if(ToolUtil.isNotEmpty(sqlFilter.toString())){
			return sqlFilter.substring(4);
		}

		return sqlFilter.toString();
	}
}
