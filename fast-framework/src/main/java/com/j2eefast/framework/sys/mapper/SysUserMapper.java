/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j2eefast.framework.sys.entity.SysUserEntity;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户 Mapper 接口
 * @author zhouzhou
 * @date 2020-03-08 21:20
 */
public interface SysUserMapper extends BaseMapper<SysUserEntity> {

    /**
     * 用户页面查询分页
     * @return
     */
    Page<SysUserEntity> findPage(IPage<?> params,
                                 @Param("username") String username,
                                 @Param("status") String status,
                                 @Param("mobile") String mobile,
                                 @Param("email") String email,
                                 @Param("compId") String compId,
                                 @Param("deptId") String deptId,
                                 @Param("name") String name,
                                 @Param("roleKey") String roleKey,
                                 @Param("noId") String noId,
                                 @Param("sql_filter") String sql_filter);


    List<SysUserEntity> findList(@Param("username") String username,
                                 @Param("status") String status,
                                 @Param("mobile") String mobile,
                                 @Param("email") String email,
                                 @Param("compId") String compId,
                                 @Param("ids") Long[] ids, 
                                 @Param("sql_filter") String sql_filter);

    /**
     *通过角色ID查询所有用户
     * @return
     */
    Page<SysUserEntity> findUserByRolePage(IPage<?> params,
                                           @Param("roleId") String roleId,
                                           @Param("username") String username,
                                           @Param("status") String status,
                                           @Param("mobile") String mobile,
                                           @Param("email") String email,
                                           @Param("compId") String compId,
                                           @Param("sql_filter") String sql_filter);

    /**
     * 根据条件分页查询未分配用户角色列表
     * @return
     */
    Page<SysUserEntity> findUnallocatedList(IPage<?> params,
                                            @Param("roleId") String roleId,
                                            @Param("username") String username ,
                                            @Param("mobile") String mobile ,
                                            @Param("email") String email ,
                                            @Param("compId") String compId,
                                            @Param("sql_filter") String sql_filter);

    /**
     * 修改用户
     * @param user
     * @return
     */
    int updateUser(SysUserEntity user);

    /**
     * 查询用户的所有菜单ID
     */
    List<Long> findAllMenuId(Long id);

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param password 新密码
     * @param salt 盐值
     * @param pwdSecurityLevel 密码安全等级
     * @return
     */
    int updatePassWord(@Param("id") Long userId,
                       @Param("password") String password,
                       @Param("salt") String salt,
                       @Param("pwdSecurityLevel") String pwdSecurityLevel);

    /**
     * 修改头像
     * @param userId 用户ID
     * @param avatar 头像路径
     * @return
     */
    int updateAvatar(@Param("id") Long userId,
                     @Param("avatar") String avatar);


    /**
     * 修改状态
     * @param userId
     * @param status
     * @return
     */
    int setStatus(@Param("id") Long userId,
                  @Param("status") String status);


    /**
     * 彻底清除用户信息
     * @param userId
     * @return
     */
    int cleanUser(@Param("id") Long userId);

    /**
     * 通过用户账号获取用户信息
     * @param userName
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    SysUserEntity findUserByUserName(@Param("userName") String userName,
                                    @Param("tenantId") String tenantId);

    /**
     * 通过用户账号获取用户信息包含已删除数据
     * @param userName
     * @return
     */
    SysUserEntity findUserByUserNameNoDel(@Param("userName") String userName);

    /**
     * 免密登录通过第三方授权id查询用户情况
     * @param username
     * @return
     */
    SysUserEntity findAuthByUserName(@Param("username") String username);

    
    
    @InterceptorIgnore(tenantLine = "true")
	SysUserEntity findAuthByUuid(@Param("uuid") String uuid,
								 @Param("tenantId") String tenantId);
    
    /**
     * 手机号码获取用户信息
     * @param mobile
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    SysUserEntity findUserByMobile(@Param("mobile") String mobile,
                                   @Param("tenantId") String tenantId);


    /**
     * 邮箱获取用户信息
     * @param email
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    SysUserEntity findUserByEmail(@Param("email") String email,
                                  @Param("tenantId") String tenantId);


    /**
     * 通过部门ID 角色获取手机号码
     * @date 2021-09-23
     * @param deptId
     * @param roleKey
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<String> findMobilesByUserId(@Param("deptId") Long deptId,
                                     @Param("roleKey") String roleKey);

    /**
     * 通过部门ID 角色获取邮箱号码
     * @param deptId
     * @param roleKey
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SysUserEntity> findEmailsByUserId(@Param("deptId") Long deptId,
                                     @Param("roleKey") String roleKey);

    /**
     * 查询手机号码是否存在
     * @date 2021-09-23
     * @param mobile
     * @param tenantId
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    int findIsMobile(@Param("mobile") String mobile,
    				 @Param("tenantId") String tenantId);

    /**
     * 通过用户ID获取所属公司
     * @param id
     * @return
     */
    String findCompNameByUserId(@Param("id") Long id);
    
    
    /**
     * 通过用户ID获取部门名称
     * @author ZhouZhou
     * @date 2021-09-18 23:08
     * @param id
     * @return
     */
    String findDeptNameByUserId(@Param("id") Long id);


    /**
     * 通过用户ID获取完整用户信息
    * @param id
     * @return
     */
    SysUserEntity findUserByUserId(@Param("id") Long id);
}
