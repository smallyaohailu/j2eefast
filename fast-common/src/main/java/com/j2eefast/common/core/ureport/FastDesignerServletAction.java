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

import com.bstek.ureport.cache.CacheUtils;
import com.bstek.ureport.console.RenderPageServletAction;
import com.bstek.ureport.console.cache.TempObjectCache;
import com.bstek.ureport.console.designer.ReportDefinitionWrapper;
import com.bstek.ureport.console.designer.ReportUtils;
import com.bstek.ureport.console.exception.ReportDesignException;
import com.bstek.ureport.definition.HtmlReportAlign;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.dsl.ReportParserLexer;
import com.bstek.ureport.dsl.ReportParserParser;
import com.bstek.ureport.export.ReportRender;
import com.bstek.ureport.expression.ErrorInfo;
import com.bstek.ureport.expression.ScriptErrorListener;
import com.bstek.ureport.parser.ReportParser;
import com.bstek.ureport.provider.report.ReportProvider;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.utils.SpringUtil;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 修改主页 报表设计 优化权限问题
 * @author Jacky.gao
 * @since 2017年1月25日
 * @author ZhouZhou
 * @date 2021-09-13
 */
@Slf4j
public class FastDesignerServletAction extends RenderPageServletAction {
    private ReportRender reportRender;
    private ReportParser reportParser;
    private List<ReportProvider> reportProviders=new ArrayList<ReportProvider>();
    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method=retriveMethod(req);
        //权限限制 只能管理员 才能设计报表否则直接屏蔽
        Subject subject = SecurityUtils.getSubject();
        LoginUserEntity loginUserEntity = (LoginUserEntity) subject
                .getPrincipal();
        if(!loginUserEntity.getId().equals(1L)){
            VelocityContext context = new VelocityContext();
            context.put("contextPath", req.getContextPath());
            resp.setContentType("text/html");
            resp.setCharacterEncoding("utf-8");
            //自定义页面
            VelocityEngine veTemp = new VelocityEngine();
            veTemp.setProperty(Velocity.RESOURCE_LOADER, "class");
            veTemp.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            veTemp.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM,new NullLogChute());
            veTemp.init();
            //非超级管理员 直接屏蔽
            context.put("content", "<div style='width: 460px;margin: 7% auto;margin-top: 0;padding-top: 20%;'><div style='color:red;font-size:25px;text-align: center;'><strong>您无权限访问,请联系技术人员!</strong></div></div>");
            context.put("error", true);
            context.put("searchFormJs", "");
            context.put("downSearchFormHtml", "");
            context.put("upSearchFormHtml", "");
            Template template= veTemp.getTemplate("ureport/html/preview.html","utf-8");
            PrintWriter writer=resp.getWriter();
            template.merge(context, writer);
            writer.close();
            return;
        }
        if(method!=null){
            invokeMethod(method, req, resp);
        }else{
            VelocityContext context = new VelocityContext();
            context.put("contextPath", req.getContextPath());
            resp.setContentType("text/html");
            resp.setCharacterEncoding("utf-8");
            //自定义页面
            VelocityEngine veTemp = new VelocityEngine();
            veTemp.setProperty(Velocity.RESOURCE_LOADER, "class");
            veTemp.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            veTemp.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM,new NullLogChute());
            veTemp.init();
            Template template= veTemp.getTemplate("ureport/html/designer.html","utf-8");
            PrintWriter writer=resp.getWriter();
            template.merge(context, writer);
            writer.close();
        }
    }
    public void scriptValidation(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String content=req.getParameter("content");
        ANTLRInputStream antlrInputStream=new ANTLRInputStream(content);
        ReportParserLexer lexer=new ReportParserLexer(antlrInputStream);
        CommonTokenStream tokenStream=new CommonTokenStream(lexer);
        ReportParserParser parser=new ReportParserParser(tokenStream);
        ScriptErrorListener errorListener=new ScriptErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        parser.expression();
        List<ErrorInfo> infos=errorListener.getInfos();
        writeObjectToJson(resp, infos);
    }

    public void conditionScriptValidation(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String content=req.getParameter("content");
        ANTLRInputStream antlrInputStream=new ANTLRInputStream(content);
        ReportParserLexer lexer=new ReportParserLexer(antlrInputStream);
        CommonTokenStream tokenStream=new CommonTokenStream(lexer);
        ReportParserParser parser=new ReportParserParser(tokenStream);
        ScriptErrorListener errorListener=new ScriptErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        parser.expr();
        List<ErrorInfo> infos=errorListener.getInfos();
        writeObjectToJson(resp, infos);
    }


    public void parseDatasetName(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String expr=req.getParameter("expr");
        ANTLRInputStream antlrInputStream=new ANTLRInputStream(expr);
        ReportParserLexer lexer=new ReportParserLexer(antlrInputStream);
        CommonTokenStream tokenStream=new CommonTokenStream(lexer);
        ReportParserParser parser=new ReportParserParser(tokenStream);
        parser.removeErrorListeners();
        ReportParserParser.DatasetContext ctx=parser.dataset();
        String datasetName=ctx.Identifier().getText();
        Map<String,String> result=new HashMap<String,String>();
        result.put("datasetName", datasetName);
        writeObjectToJson(resp, result);
    }

    public void savePreviewData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String content=req.getParameter("content");
        content=decodeContent(content);
        InputStream inputStream= IOUtils.toInputStream(content,"utf-8");
        ReportDefinition reportDef=reportParser.parse(inputStream,"p");
        reportRender.rebuildReportDefinition(reportDef);
        IoUtil.close(inputStream);
        TempObjectCache.putObject(PREVIEW_KEY, reportDef);
    }

    public void loadReport(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String file=req.getParameter("file");
        if(file==null){
            throw new ReportDesignException("Report file can not be null.");
        }
        file= ReportUtils.decodeFileName(file);
        Object obj=TempObjectCache.getObject(file);
        if(obj!=null && obj instanceof ReportDefinition){
            ReportDefinition reportDef=(ReportDefinition)obj;
            TempObjectCache.removeObject(file);
            writeObjectToJson(resp, new ReportDefinitionWrapper(reportDef));
        }else{
            ReportDefinition reportDef=reportRender.parseReport(file);
            //修改默认居中
            reportDef.getPaper().setHtmlReportAlign(HtmlReportAlign.center);
            writeObjectToJson(resp, new ReportDefinitionWrapper(reportDef));
        }
    }

    public void deleteReportFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String file=req.getParameter("file");
        if(file==null){
            throw new ReportDesignException("Report file can not be null.");
        }
        ReportProvider targetReportProvider=null;
        for(ReportProvider provider:reportProviders){
            if(file.startsWith(provider.getPrefix())){
                targetReportProvider=provider;
                break;
            }
        }
        if(targetReportProvider==null){
            throw new ReportDesignException("File ["+file+"] not found available report provider.");
        }
        targetReportProvider.deleteReport(file);
    }


    public void saveReportFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String file=req.getParameter("file");
        file=ReportUtils.decodeFileName(file);
        String content=req.getParameter("content");
        content=decodeContent(content);
        ReportProvider targetReportProvider=null;
        for(ReportProvider provider:reportProviders){
            if(file.startsWith(provider.getPrefix())){
                targetReportProvider=provider;
                break;
            }
        }

        if(targetReportProvider==null){
            throw new ReportDesignException("File ["+file+"] not found available report provider.");
        }

        try{
            //修改 保存下载文件名称
            targetReportProvider.saveReport(file, content);
        }catch (RxcException e){
            throw new ReportDesignException("File ["+file+"] have been in existence.");
        }

        InputStream inputStream= IOUtils.toInputStream(content,"utf-8");
        ReportDefinition reportDef=reportParser.parse(inputStream, file);
        reportRender.rebuildReportDefinition(reportDef);
        CacheUtils.cacheReportDefinition(file, reportDef);
        IOUtils.closeQuietly(inputStream);
    }

    public void loadReportProviders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取系统权限
        List<Object> outHtml  = new ArrayList<>(5);
        outHtml.addAll(reportProviders);
        ReportProvider targetReportProvider=null;
        for(ReportProvider provider:reportProviders){
            if("fast-".startsWith(provider.getPrefix())){
                targetReportProvider=provider;
                break;
            }
        }
        if(targetReportProvider==null){
            throw new ReportDesignException("文件前缀必须fast-");
        }

        Map<String, Object> map = new HashMap<>();
        try{
            Method method= targetReportProvider.getClass().getMethod("getSysRoles", new Class<?>[]{});
            List<Object> o = (List<Object>) method.invoke(targetReportProvider, new Object[]{});
            map.put("roles",o);
        }catch (Exception e){
            log.error("获取默认下载文件名失败!",e);
        }
        outHtml.add(map);
        writeObjectToJson(resp, outHtml);
    }

    public void setReportRender(ReportRender reportRender) {
        this.reportRender = reportRender;
    }

    public void setReportParser(ReportParser reportParser) {
        this.reportParser = reportParser;
    }

//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext)throws BeansException {
//        super.setApplicationContext(applicationContext);
//        Collection<ReportProvider> providers=applicationContext.getBeansOfType(ReportProvider.class).values();
//        for(ReportProvider provider:providers){
//            if(provider.disabled() || provider.getName()==null){
//                continue;
//            }
//            reportProviders.add(provider);
//        }
//    }

    public void initReportProviders(){
        applicationContext = SpringUtil.getApplicationContext();
        Collection<ReportProvider> providers= SpringUtil.getBeansOfType(ReportProvider.class).values();
        for(ReportProvider provider:providers){
            if(provider.disabled() || provider.getName()==null){
                continue;
            }
            reportProviders.add(provider);
        }
    }


    @Override
    public String url() {
        return "/designer";
    }
}
