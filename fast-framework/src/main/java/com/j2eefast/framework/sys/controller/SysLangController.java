/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.sys.controller;

import com.j2eefast.common.config.entity.SysLangEntity;
import com.j2eefast.common.config.service.SysLangService;
import com.j2eefast.common.core.business.annotaion.BussinessLog;
import com.j2eefast.common.core.enums.BusinessType;
import com.j2eefast.common.core.i18n.MessageSource;
import com.j2eefast.common.core.utils.*;
import com.j2eefast.framework.annotation.RepeatSubmit;
import com.j2eefast.common.core.controller.BaseController;
import com.j2eefast.framework.sys.entity.SysModuleEntity;
import com.j2eefast.framework.sys.service.SysModuleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.ui.ModelMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

/**
 * 国际化配置页面控制器
 * @author ZhouZhou
 * @date 2021-10-12
 */
@Controller
@RequestMapping("/sys/lang")
public class SysLangController extends BaseController{

    private String prefix = "modules/sys/lang";

    @Autowired
    private SysLangService sysLangService;
    @Autowired
    private SysModuleService sysModuleService;
    @Autowired
    private MessageSource messageSource;

    /**
     * 视图页面
     */
    @RequiresPermissions("sys:lang:view")
    @GetMapping()
    public String lang(ModelMap mmap){
        List<SysModuleEntity> modules = sysModuleService.list();
        mmap.put("modules", modules);
        return prefix + "/lang";
    }
    
    /**
     * 页面表格分页查询
     * @param params
     * @param sysLangEntity
     * @return
     */
    @RequiresPermissions("sys:lang:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params, SysLangEntity sysLangEntity) {
        PageUtil page = sysLangService.findPage(params,sysLangEntity);
		return success(page);
    }

    /**
     * 新增视图
     * @param mmap
     * @return
     */
    @GetMapping("/add")
    public String add(ModelMap mmap){
        List<SysModuleEntity> modules = sysModuleService.list();
        mmap.put("modules", modules);
        return prefix + "/add";
    }

    /**
     * 新增
     */
    @RepeatSubmit
    @RequiresPermissions("sys:lang:add")
    @BussinessLog(title = "国际化配置", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addSysLang(@Validated SysLangEntity sysLang){
        //校验参数
        ValidatorUtil.validateEntity(sysLang);
        if(sysLangService.addSysLang(sysLang)){
            //重新加载
            messageSource.reload();
            return success();
        }
        return error("新增失败!");
    }

    /**
     * 修改
     */
    @RequiresPermissions("sys:lang:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap){
        SysLangEntity sysLang = sysLangService.findSysLangById(id);
        List<SysModuleEntity> modules = sysModuleService.list();
        mmap.put("modules", modules);
        mmap.put("sysLang", sysLang);
        return prefix + "/edit";
    }

    /**
     * 修改保存国际化配置
     */
    @RepeatSubmit
    @RequiresPermissions("sys:lang:edit")
    @BussinessLog(title = "国际化配置", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editSysLang(SysLangEntity sysLang){
        //校验参数
		ValidatorUtil.validateEntity(sysLang);
		if(sysLangService.updateSysLangById(sysLang)){
            //重新加载
            messageSource.reload();
            return success();
        }
        return error("修改失败!");
    }

    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("sys:lang:del")
    @BussinessLog(title = "国际化配置", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      if(sysLangService.deleteBatchByIds(ids)){
          //重新加载
          messageSource.reload();
          return success();
      }
      return error("删除失败!");
    }

    @BussinessLog(title = "国际化配置", businessType = BusinessType.CLEAN)
    @RequestMapping(value = "/clearLang", method = RequestMethod.GET)
    @RequiresPermissions("sys:lang:clear")
    @RepeatSubmit
    @ResponseBody
    public ResponseData clearLang(){
        messageSource.reload();
        return success();
    }

}
