/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.web.test.controller;

import com.j2eefast.common.core.business.annotaion.BussinessLog;
import com.j2eefast.common.core.enums.BusinessType;
import com.j2eefast.common.core.utils.*;
import com.j2eefast.framework.annotation.RepeatSubmit;
import com.j2eefast.common.core.controller.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.ui.ModelMap;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import com.j2eefast.web.test.entity.MssqlTestEntity;
import com.j2eefast.web.test.service.MssqlTestService;

/**
 * 数据库测试页面控制器
 * @author ZhouZhou
 * @date 2021-08-31 22:02:57
 */
@Controller
@RequestMapping("/mssql/test")
public class MssqlTestController extends BaseController{

    private String prefix = "modules/mssql/test";

    @Autowired
    private MssqlTestService mssqlTestService;

    /**
     * 视图页面
     */
    @RequiresPermissions("mssql:test:view")
    @GetMapping()
    public String test(){
        return prefix + "/test";
    }
    
    /**
     * 页面表格分页查询
     * @param params
     * @param mssqlTestEntity
     * @return
     */
    @RequiresPermissions("mssql:test:list")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData list(@RequestParam Map<String, Object> params,MssqlTestEntity mssqlTestEntity) {
        PageUtil page = mssqlTestService.findPage(params);
		return success(page);
    }

    /**
     * 新增视图
     * @param mmap
     * @return
     */
    @GetMapping("/add")
    public String add(){
        return prefix + "/add";
    }

    /**
     * 新增
     */
    @RepeatSubmit
    @RequiresPermissions("mssql:test:add")
    @BussinessLog(title = "数据库测试", businessType = BusinessType.INSERT)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData addMssqlTest(@Validated MssqlTestEntity mssqlTest){
        //校验参数
        ValidatorUtil.validateEntity(mssqlTest);
        return mssqlTestService.addMssqlTest(mssqlTest)? success(): error("新增失败!");
    }

    /**
     * 修改
     */
    @RequiresPermissions("mssql:test:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap){
        MssqlTestEntity mssqlTest = mssqlTestService.findMssqlTestById(id);
        mmap.put("mssqlTest", mssqlTest);
        return prefix + "/edit";
    }

    /**
     * 修改保存数据库测试
     */
    @RepeatSubmit
    @RequiresPermissions("mssql:test:edit")
    @BussinessLog(title = "数据库测试", businessType = BusinessType.UPDATE)
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData editMssqlTest(MssqlTestEntity mssqlTest){
        //校验参数
		ValidatorUtil.validateEntity(mssqlTest);
        return mssqlTestService.updateMssqlTestById(mssqlTest)? success(): error("修改失败!");
    }

    /**
     * 删除
     */
    @RepeatSubmit
    @RequiresPermissions("mssql:test:del")
    @BussinessLog(title = "数据库测试", businessType = BusinessType.DELETE)
    @RequestMapping(value = "/del", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData del(Long[] ids) {
      return mssqlTestService.deleteBatchByIds(ids)? success(): error("删除失败!");
    }

}
