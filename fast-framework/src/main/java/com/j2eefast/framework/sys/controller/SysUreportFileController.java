/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.j2eefast.common.core.business.annotaion.BussinessLog;
import com.j2eefast.common.core.enums.BusinessType;
import com.j2eefast.common.core.utils.*;
import com.j2eefast.framework.annotation.RepeatSubmit;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.framework.sys.entity.SysRoleEntity;
import com.j2eefast.framework.sys.service.SysRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

import com.j2eefast.framework.sys.entity.SysNoticeEntity;
import com.j2eefast.framework.sys.entity.SysUreportFileEntity;
import com.j2eefast.framework.sys.service.SysUreportFileService;

import cn.hutool.core.io.FileUtil;

/**
 * ureport2报配置存储页面控制器
 * @author ZhouZhou
 * @date 2021-08-14 19:57:51
 */
@Controller
@RequestMapping("/sys/ureport")
public class SysUreportFileController extends BaseController{

    private String prefix = "modules/sys/ureport";
    private String redisPrefix = "fast:ureport:";

    @Autowired
    private SysUreportFileService sysUreportFileService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 视图页面
     */
    @RequiresPermissions("sys:ureport:view")
    @GetMapping()
    public String ureport(){
        return prefix + "/ureport";
    }
    
    /**
     * 页面表格分页查询
     * @param params
     * @param sysUreportFileEntity
     * @return
     */
    @RequiresPermissions("sys:ureport:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,SysUreportFileEntity sysUreportFileEntity) {
        PageUtil page = sysUreportFileService.findPage(params,sysUreportFileEntity);
		return success(page);
    }

    
    /**
     * 修改报表权限 数据权限
     */
    @GetMapping("/edit/{noticeId}")
    public String edit(@PathVariable("noticeId") Long noticeId, ModelMap mmap) {

    	SysUreportFileEntity sysFile = sysUreportFileService.getById(noticeId);
        List<SysRoleEntity> roles = new ArrayList<>();
        SysRoleEntity roleEntity = new SysRoleEntity();
        roleEntity.setRoleName("所有角色");
        roleEntity.setRoleKey("_ALL");
        roles.add(roleEntity);

        roleEntity = new SysRoleEntity();
        roleEntity.setRoleName("禁用");
        roleEntity.setRoleKey("_DISABLE");
        roles.add(roleEntity);
        sysRoleService.getRolesAll().forEach(e->{
            roles.add(e);
        });
        mmap.put("roles", roles);
        mmap.put("selectRoles", sysFile.getRoleKeys());
        mmap.put("selectFuncs", sysFile.getFunc());

        List<String> alias = StrUtil.split(sysFile.getTableAlias(),StrUtil.C_COMMA);
        for(int i=0;i<alias.size();i++){
            mmap.put("table"+i, alias.get(i));
        }
        mmap.put("sysFile", sysFile);
        return prefix + "/edit";
    }

    /**
     * 分享取消
     * @param code
     * @return
     */
    @RequiresPermissions("sys:ureport:cancel")
    @PostMapping("/cancelShare")
    @ResponseBody
    public ResponseData cancelShare(String code){
        redisUtil.del(code);
        return  sysUreportFileService.update(new UpdateWrapper<SysUreportFileEntity>()
                .eq("code",code).set("share","N"))? success(): error("取消失败!");
    }

    @RequiresPermissions("sys:ureport:getShare")
    @PostMapping("/getShare")
    @ResponseBody
    public ResponseData getShare(String code){
       String __code = redisUtil.get(code);
        if(ToolUtil.isNotEmpty(__code)){
            return success();
        }else{
            return error("不存在");
        }
    }


    @RequiresPermissions("sys:ureport:create")
    @PostMapping("/createShare")
    @ResponseBody
    public ResponseData createShare(Long id, int time){
        String code = RandomUtil.randomString(RandomUtil.BASE_NUMBER + RandomUtil.BASE_CHAR +RandomUtil.BASE_CHAR.toUpperCase(), 22);
        String pass = RandomUtil.randomString(RandomUtil.BASE_NUMBER + RandomUtil.BASE_CHAR +RandomUtil.BASE_CHAR.toUpperCase(), 4);
        if(sysUreportFileService.update(new UpdateWrapper<SysUreportFileEntity>()
                .eq("id",id).set("share","Y").set("code",code).set("pass",pass).set("time",time))){
            if(time != 0){
                redisUtil.set(code, code + "#" + pass,time * 24 * 60 * 60L);
            }
        }
        return success().put("__surl",code).put("pass",pass).put("time",time);
    }

    /**
     * 修改保存公告
     */
    @RequiresPermissions("sys:ureport:edit")
    @BussinessLog(title = "ureport2报配置存储", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public ResponseData edit(SysUreportFileEntity sysUreportFileEntity) {
    	//校验参数
        ValidatorUtil.validateEntity(sysUreportFileEntity);
        redisUtil.set(redisPrefix + "func:"+sysUreportFileEntity.getName(),sysUreportFileEntity.getFunc());
        redisUtil.set(redisPrefix + "role:"+sysUreportFileEntity.getName(),sysUreportFileEntity.getRoleKeys());
        return sysUreportFileService.updateById(sysUreportFileEntity)? success(): error("更新失败!");
    }
    
    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("sys:ureport:del")
    @BussinessLog(title = "ureport2报配置存储", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      for(Long id:ids){
          SysUreportFileEntity sysUreportFileEntity =
                  sysUreportFileService.getById(id);
          redisUtil.del(redisPrefix + "func:"+sysUreportFileEntity.getName());
          redisUtil.del(redisPrefix + "role:"+sysUreportFileEntity.getName());
      }
      return sysUreportFileService.deleteBatchByIds(ids)? success(): error("删除失败!");
    }

}
