/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.j2eefast.common.core.ureport;

import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.console.BaseServletAction;
import com.bstek.ureport.console.cache.TempObjectCache;
import com.bstek.ureport.console.exception.ReportDesignException;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.export.ExportConfigure;
import com.bstek.ureport.export.ExportConfigureImpl;
import com.bstek.ureport.export.ExportManager;
import com.bstek.ureport.export.excel.high.ExcelProducer;
import com.bstek.ureport.model.Report;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.utils.ToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * 修改ureport2 报表导出excel 支持多租户 
 * url: _excel
 * -- 前端页面使用 http://host[:port][/context-path]/ureport/_excel?_u=报表名称
 * 报表设计添加 tenantId 字段
 * @author Jacky.gao
 * @since 2017年4月17日
 * @author huanzhou
 * @date 2021-09-18
 */
@Slf4j
public class FastExportExcelServicetAction extends BaseServletAction {

    private ReportBuilder reportBuilder;
    private ExportManager exportManager;
    private ExcelProducer excelProducer=new ExcelProducer();

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method=retriveMethod(req);
        if(method!=null){
            invokeMethod(method, req, resp);
        }else{
            buildExcel(req, resp,false,false);
        }
    }
    
    public void paging(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        buildExcel(req, resp, true, false);
    }

    public void sheet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        buildExcel(req, resp, false, true);
    }

    public void buildExcel(HttpServletRequest req, HttpServletResponse resp,
    		boolean withPage,boolean withSheet) throws IOException {
    	
        String file=req.getParameter("_u");
        file=decode(file);
        if(StringUtils.isBlank(file)){
            throw new ReportComputeException("Report file can not be null.");
        }
        String fileName=req.getParameter("_n");
        //J2eeFAST 修改如果前端不传 下载之后的文件名 则使用 系统数据库保存的默认名称
        if(ToolUtil.isEmpty(fileName)){
            //正在设计
            if(file.equals(PREVIEW_KEY)){
                fileName = "设计中临时报表";
            //指定已经设计好的模板
            }else{
                fileName = UreportUtils.getDefaultName(file);
            }
        }
        fileName=buildDownloadFileName(file, fileName, ".xlsx");
        resp.setContentType("application/octet-stream;charset=ISO8859-1");
        fileName=new String(fileName.getBytes("UTF-8"),"ISO8859-1");
        resp.setHeader("Content-Disposition","attachment;filename=\"" + fileName + "\"");
        Map<String, Object> parameters = buildParameters(req);
        // J2eeFAST 整合系统 支持多租户模式
        parameters.put("tenantId",((LoginUserEntity) SecurityUtils.getSubject()
                .getPrincipal()).getTenantId());
        OutputStream outputStream=resp.getOutputStream();
        if(file.equals(PREVIEW_KEY)){
            ReportDefinition reportDefinition=(ReportDefinition) TempObjectCache.getObject(PREVIEW_KEY);
            if(reportDefinition==null){
                throw new ReportDesignException("Report data has expired,can not do export excel.");
            }
            Report report=reportBuilder.buildReport(reportDefinition, parameters);
            if(withPage){
                excelProducer.produceWithPaging(report, outputStream);
            }else if(withSheet){
                excelProducer.produceWithSheet(report, outputStream);
            }else{
                excelProducer.produce(report, outputStream);
            }
        }else{

            if(!UreportUtils.isFunc(file,"dcexcel")){
                throw new RxcException("未开放此功能!","A00002");
            }

            //已经设计完成
            if(!UreportUtils.isPermissions(file)){
                throw new RxcException("无权限访问!","A00001");
            }

            //数据权限
            parameters.put(ConfigConstant.SQLFILTER,UreportUtils.getSQLFilter(file));

            ExportConfigure configure=new ExportConfigureImpl(file,parameters,outputStream);

            if(withPage){
                exportManager.exportExcelWithPaging(configure);
            }else if(withSheet){
                exportManager.exportExcelWithPagingSheet(configure);
            }else{
                exportManager.exportExcel(configure);
            }
        }
        outputStream.flush();
        outputStream.close();
    }

    public void setReportBuilder(ReportBuilder reportBuilder) {
        this.reportBuilder = reportBuilder;
    }
    public void setExportManager(ExportManager exportManager) {
        this.exportManager = exportManager;
    }

    @Override
    public String url() {
        return "/excel";
    }
}
