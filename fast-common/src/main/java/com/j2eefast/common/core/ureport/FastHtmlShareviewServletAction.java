package com.j2eefast.common.core.ureport;

import cn.hutool.core.util.StrUtil;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.cache.CacheUtils;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.console.RenderPageServletAction;
import com.bstek.ureport.console.cache.TempObjectCache;
import com.bstek.ureport.console.exception.ReportDesignException;
import com.bstek.ureport.definition.Paper;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.export.ExportManager;
import com.bstek.ureport.export.FullPageData;
import com.bstek.ureport.export.PageBuilder;
import com.bstek.ureport.export.ReportRender;
import com.bstek.ureport.export.html.HtmlProducer;
import com.bstek.ureport.export.html.HtmlReport;
import com.bstek.ureport.model.Report;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j2eefast.common.core.exception.RxcException;
import com.j2eefast.common.core.utils.RedisUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.io.PrintWriter;
import java.util.*;

/**
 * 分享控制
 */
@Slf4j
public class FastHtmlShareviewServletAction extends RenderPageServletAction {

    private ExportManager exportManager;
    private ReportBuilder reportBuilder;
    private RedisUtil redisUtil;
    private ReportRender reportRender;
    private HtmlProducer htmlProducer=new HtmlProducer();

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 方法
        String method = retriveMethod(req);
        if(method!=null){
            invokeMethod(method, req, resp);
        }else{
            String url = "ureport/html/shareview.html";
            String errorMsg= "异常请求,请确认访问链接是否正确!";
            HtmlReport htmlReport=null;
            String errCode = "";
            VelocityContext context = new VelocityContext();
            String customParameters= buildCustomParameters(req);
            context.put("customParameters", customParameters);
            try{
                htmlReport=loadReport(req);
            }catch (RxcException e){
                errorMsg = "错误代码:"+e.getCode() +" 错误信息:" + e.getMsg();
                context.put("errCode",e.getCode());
                errCode = e.getCode();
            }catch(Exception ex){
                log.error("报表计算出错，错误信息如下：",ex);
                errorMsg ="报表内部异常,请查看后台日志.";
            }
            if(htmlReport == null){
                String title = "异常请求";
                context.put("title", title);
                context.put("content", "<div style='width: 550px;margin: 7% auto;margin-top: 0;padding-top: 20%;'><div style='color:red;font-size:25px;text-align: center;'><strong>"+errorMsg+"</strong></div></div>");
                context.put("error", true);
                context.put("style", "");
                context.put("reportAlign", "initial");
                context.put("searchFormJs", "");
                String __surl = req.getParameter("__surl");
                context.put("__surl", StrUtil.blankToDefault(__surl,""));
                if(errCode.equals("B00001")){
                    url = "ureport/html/login.html";
                }
            }else{
                context.put("error", false);
                context.put("content", htmlReport.getContent());
                context.put("style", htmlReport.getStyle());
                context.put("pageAll", htmlReport.isPage());
            }
            context.put("contextPath", req.getContextPath());
            resp.setContentType("text/html");
            resp.setCharacterEncoding("utf-8");
            VelocityEngine veTemp = new VelocityEngine();
            veTemp.setProperty(Velocity.RESOURCE_LOADER, "class");
            veTemp.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            veTemp.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM,new NullLogChute());
            veTemp.init();
            Template template= veTemp.getTemplate(url,"utf-8");
            PrintWriter writer= resp.getWriter();
            template.merge(context, writer);
            writer.close();
        }
    }

    private String convertJson(Collection<ChartData> data){
        if(data==null || data.size()==0){
            return "";
        }
        ObjectMapper mapper=new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(data);
            return json;
        } catch (Exception e) {
            throw new ReportComputeException(e);
        }
    }

    private String buildCustomParameters(HttpServletRequest req){
        StringBuilder sb=new StringBuilder();
        Enumeration<?> enumeration=req.getParameterNames();
        while(enumeration.hasMoreElements()){
            Object obj=enumeration.nextElement();
            if(obj==null){
                continue;
            }
            String name=obj.toString();
            String value=req.getParameter(name);
            if(name==null || value==null || (name.startsWith("__pass"))){
                continue;
            }
            if(sb.length()>0){
                sb.append("&");
            }
            sb.append(name);
            sb.append("=");
            sb.append(value);
        }
        return sb.toString();
    }


    /**
     * 在线打印
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void loadPrintPages(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long start=System.currentTimeMillis();
        Map<String, Object> parameters = buildParame(req);
        Map<String,Object> ref = new HashMap<>();
        String __surl = (String) parameters.get("__surl");
        String __pass = (String) parameters.get("__pass");
        String file = "";
        String password = "";
        String code = redisUtil.get(__surl);
        if(ToolUtil.isEmpty(code)){
            Map<String, Object> map = UreportUtils.isShare("fast-"+__surl);
            if((Boolean) map.get("isShare")
                    && (Integer) map.get("time") == 0){
                file = (String) map.get("file");
                password = (String) map.get("pass");
            }else{
                ref.put("code","A00003");
                ref.put("msg","链接无效!");
                writeObjectToJson(resp, ref);
                return;
            }
        }else{
            password = code.split("#")[1];
            code = code.split("#")[0];
            Map<String, Object> map = UreportUtils.isShare("fast-"+code);
            if((Boolean) map.get("isShare")){
                file = (String) map.get("file");
            }else{
                ref.put("code","A00003");
                ref.put("msg","链接无效!");
                writeObjectToJson(resp, ref);
                return;
            }
        }
        if(ToolUtil.isEmpty(__pass) || !__pass.equals(password)){
            ref.put("code","B00001");
            ref.put("msg","访问密码错误!");
            writeObjectToJson(resp, ref);
            return;
        }
        String fpage = req.getParameter("_i");
        // 是否打印所有  0 当前  1所有
        String all = req.getParameter("__a");

        // J2eeFAST 整合系统 支持多租户模式
//        parameters.put("tenantId",((LoginUserEntity) SecurityUtils.getSubject()
//                .getPrincipal()).getTenantId());
        //操作用户
//        parameters.put("__userName",((LoginUserEntity) SecurityUtils.getSubject()
//                .getPrincipal()).getName());

        ReportDefinition reportDefinition=null;

        //分页 当前页
        if(ToolUtil.isNotEmpty(fpage) &&
                ToolUtil.isNotEmpty(all)
                && all.equals("0")){
            parameters.put("__page", Integer.parseInt(fpage));
            reportDefinition=reportRender.getReportDefinition(file);
            Report report=reportBuilder.buildReport(reportDefinition, parameters);
            Map<String, ChartData> chartMap=report.getContext().getChartDataMap();
            if(chartMap.size()>0){
                CacheUtils.storeChartDataMap(chartMap);
            }
            FullPageData pageData= PageBuilder.buildFullPageData(report);
            StringBuilder sb=new StringBuilder();
            List<List<Page>> list=pageData.getPageList();
            Context context=report.getContext();
            if(list.size()>0){
                for(int i=0;i<list.size();i++){
                    List<Page> columnPages=list.get(i);
                    if(i==0){
                        String html=htmlProducer.produce(context,columnPages,pageData.getColumnMargin(),false);
                        sb.append(html);
                    }else{
                        String html=htmlProducer.produce(context,columnPages,pageData.getColumnMargin(),false);
                        sb.append(html);
                    }
                }
            }else{
                List<Page> pages=report.getPages();
                for(int i=0;i<pages.size();i++){
                    Page page=pages.get(i);
                    if(i==0){
                        String html=htmlProducer.produce(context,page, false);
                        sb.append(html);
                    }else{
                        String html=htmlProducer.produce(context,page, true);
                        sb.append(html);
                        break;
                    }
                }
            }
            Map<String,Object> map=new HashMap<>();
            Paper paper = report.getPaper();
            map.put("paper",paper);
            map.put("html", sb.toString());
            map.put("code","000000");
            map.put("watermark", reportDefinition.getPaper().getWatermark());
            writeObjectToJson(resp, map);
        }else{
            if(ToolUtil.isNotEmpty(fpage) &&
                    ToolUtil.isNotEmpty(all)
                    && all.equals("1")){
                //当前页
                parameters.put("__page", 1);
                reportDefinition=reportRender.getReportDefinition(file);
                Report report=reportBuilder.buildReport(reportDefinition, parameters);
                Map<String, ChartData> chartMap=report.getContext().getChartDataMap();
                if(chartMap.size()>0){
                    CacheUtils.storeChartDataMap(chartMap);
                }
                FullPageData pageData= PageBuilder.buildFullPageData(report);
                StringBuilder sb=new StringBuilder();
                List<List<Page>> list=pageData.getPageList();
                Context context=report.getContext();
                if(list.size()>0){
                    for(int i=0;i<list.size();i++){
                        List<Page> columnPages=list.get(i);
                        if(i==0){
                            String html=htmlProducer.produce(context,columnPages,pageData.getColumnMargin(),false);
                            sb.append(html);
                        }else{
                            String html=htmlProducer.produce(context,columnPages,pageData.getColumnMargin(),false);
                            sb.append(html);
                        }
                    }
                }else{
                    List<Page> pages=report.getPages();
                    for(int i=0;i<pages.size();i++){
                        Page page=pages.get(i);
                        String html=htmlProducer.produce(context,page, false);
                        sb.append(html);
                        break;
                    }
                }

                long __totalPage = (long) parameters.get("__totalPage");

                for(int k=2; k<=__totalPage;k++){
                    //当前页
                    parameters.put("__page", k);
                    if(file.equals(PREVIEW_KEY)){
                        reportDefinition=(ReportDefinition) TempObjectCache.getObject(PREVIEW_KEY);
                        if(reportDefinition==null){
                            throw new ReportDesignException("Report data has expired,can not do export excel.");
                        }
                    }else{
                        reportDefinition=reportRender.getReportDefinition(file);
                    }
                    report=reportBuilder.buildReport(reportDefinition, parameters);
                    chartMap=report.getContext().getChartDataMap();
                    if(chartMap.size()>0){
                        CacheUtils.storeChartDataMap(chartMap);
                    }
                    pageData= PageBuilder.buildFullPageData(report);
                    list=pageData.getPageList();
                    context=report.getContext();
                    if(list.size()>0){
                        for(int i=0;i<list.size();i++){
                            List<Page> columnPages=list.get(i);
                            if(i==0){
                                String html=htmlProducer.produce(context,columnPages,pageData.getColumnMargin(),false);
                                sb.append(html);
                            }else{
                                String html=htmlProducer.produce(context,columnPages,pageData.getColumnMargin(),false);
                                sb.append(html);
                            }
                        }
                    }else{
                        List<Page> pages=report.getPages();
                        for(int i=0;i<pages.size();i++){
                            Page page=pages.get(i);
                            if(i==0){
                                String html=htmlProducer.produce(context,page, true);
                                sb.append(html);
                            }else{
                                String html=htmlProducer.produce(context,page, true);
                                sb.append(html);
                                break;
                            }
                        }
                    }
                }

                Map<String,Object> map=new HashMap<>();
                Paper paper = report.getPaper();
                map.put("paper",paper);
                map.put("html", sb.toString());
                map.put("code","000000");
                map.put("watermark", reportDefinition.getPaper().getWatermark());
                writeObjectToJson(resp, map);
            }else{
                reportDefinition=reportRender.getReportDefinition(file);
                Report report = reportBuilder.buildReport(reportDefinition, parameters);
                Map<String, ChartData> chartMap=report.getContext().getChartDataMap();
                if(chartMap.size()>0){
                    CacheUtils.storeChartDataMap(chartMap);
                }
                FullPageData pageData= PageBuilder.buildFullPageData(report);
                StringBuilder sb=new StringBuilder();
                List<List<Page>> list=pageData.getPageList();
                Context context=report.getContext();
                if(list.size()>0){
                    for(int i=0;i<list.size();i++){
                        List<Page> columnPages=list.get(i);
                        if(i==0){
                            String html=htmlProducer.produce(context,columnPages,pageData.getColumnMargin(),false);
                            sb.append(html);
                        }else{
                            String html=htmlProducer.produce(context,columnPages,pageData.getColumnMargin(),false);
                            sb.append(html);
                        }
                    }
                }else{
                    List<Page> pages=report.getPages();
                    for(int i=0;i<pages.size();i++){
                        Page page=pages.get(i);
                        if(i==0){
                            String html=htmlProducer.produce(context,page, false);
                            sb.append(html);
                        }else{
                            String html=htmlProducer.produce(context,page, true);
                            sb.append(html);
                            break;
                        }
                    }
                }
                Map<String,Object> map=new HashMap<>();
                Paper paper = report.getPaper();
                map.put("paper",paper);
                map.put("html", sb.toString());
                map.put("code","000000");
                map.put("watermark", reportDefinition.getPaper().getWatermark());
                writeObjectToJson(resp, map);
            }
        }
        long end=System.currentTimeMillis();
        String msg="~~~ 打印传输数据总耗时:"+(end-start)+"ms";
        log.info(msg);
    }

    public void loadPagePaper(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> parameters = buildParame(req);
        Map<String,Object> ref = new HashMap<>();
        String __surl = (String) parameters.get("__surl");
        String __pass = (String) parameters.get("__pass");
        String _file = "";
        String password = "";
        String code = redisUtil.get(__surl);
        if(ToolUtil.isEmpty(code)){
            Map<String, Object> map = UreportUtils.isShare("fast-"+__surl);
            if((Boolean) map.get("isShare")
                    && (Integer) map.get("time") == 0){
                _file = (String) map.get("file");
                password = (String) map.get("pass");
            }else{
                ref.put("code","A00003");
                ref.put("msg","链接无效!");
                writeObjectToJson(resp, ref);
                return;
            }
        }else{
            password = code.split("#")[1];
            code = code.split("#")[0];
            Map<String, Object> map = UreportUtils.isShare("fast-"+code);
            if((Boolean) map.get("isShare")){
                _file = (String) map.get("file");
            }else{
                ref.put("code","A00003");
                ref.put("msg","链接无效!");
                writeObjectToJson(resp, ref);
                return;
            }
        }
        if(ToolUtil.isEmpty(__pass) || !__pass.equals(password)){
            ref.put("code","B00001");
            ref.put("msg","访问密码错误!");
            writeObjectToJson(resp, ref);
            return;
        }
        ReportDefinition report=reportRender.getReportDefinition(_file);
        Paper paper = report.getPaper();
        ref.put("code","000000");
        ref.put("data",paper);
        writeObjectToJson(resp, ref);
    }

    public void verify(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> parameters = buildParame(req);
        Map<String,Object> ref = new HashMap<>();
        String pageIndex=req.getParameter("_i");
        String __surl = (String) parameters.get("__surl");
        String __pass = (String) parameters.get("__pass");
        String password = "";
        //分页 当前页
        if(ToolUtil.isNotEmpty(pageIndex)){
            parameters.put("__page", Integer.parseInt(pageIndex));
        }
        String _file = "";
        String code = redisUtil.get(__surl);
        if(ToolUtil.isEmpty(code)){
            Map<String, Object> map = UreportUtils.isShare("fast-"+__surl);
            if((Boolean) map.get("isShare")
                    && (Integer) map.get("time") == 0){
                _file = (String) map.get("file");
                password = (String) map.get("pass");
            }else{
                ref.put("code","A00003");
                ref.put("msg","链接无效!");
                writeObjectToJson(resp, ref);
                return;
            }
        }else{
            password = code.split("#")[1];
            code = code.split("#")[0];
            Map<String, Object> map = UreportUtils.isShare("fast-"+code);
            if((Boolean) map.get("isShare")){
                _file = (String) map.get("file");
            }else{
                ref.put("code","A00003");
                ref.put("msg","链接无效!");
                writeObjectToJson(resp, ref);
                return;
            }
        }
        HtmlReport htmlReport = new HtmlReport();
        if(ToolUtil.isEmpty(__pass) || !__pass.equals(password)){
            ref.put("code","B00001");
            ref.put("msg","访问密码错误!");
            writeObjectToJson(resp, ref);
            return;
        }
        if(StringUtils.isNotBlank(pageIndex) && !pageIndex.equals("0")){
            int index=Integer.valueOf(pageIndex);
            htmlReport=exportManager.exportHtml(_file,req.getContextPath(),parameters,index);
        }else{
            htmlReport=exportManager.exportHtml(_file,req.getContextPath(),parameters);
        }
        htmlReport.setFile(_file);
        ref.put("code","000000");
        ref.put("msg","成功");
        ref.put("data",htmlReport);
        writeObjectToJson(resp, ref);
        return;
    }

    public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> parameters = buildParame(req);
        Map<String,Object> ref = new HashMap<>();
        String pageIndex=req.getParameter("_i");
        String __surl = (String) parameters.get("__surl");
        String __pass = (String) parameters.get("__pass");
        String password = "";
        //分页 当前页
        if(ToolUtil.isNotEmpty(pageIndex)){
            parameters.put("__page", Integer.parseInt(pageIndex));
        }
        String _file = "";
        String code = redisUtil.get(__surl);
        if(ToolUtil.isEmpty(code)){
            Map<String, Object> map = UreportUtils.isShare("fast-"+__surl);
            if((Boolean) map.get("isShare")
                    && (Integer) map.get("time") == 0){
                _file = (String) map.get("file");
                password = (String) map.get("pass");
            }else{
                ref.put("code","A00003");
                ref.put("msg","链接无效!");
                writeObjectToJson(resp, ref);
                return;
            }
        }else{
            password = code.split("#")[1];
            code = code.split("#")[0];
            Map<String, Object> map = UreportUtils.isShare("fast-"+code);
            if((Boolean) map.get("isShare")){
                _file = (String) map.get("file");
            }else{
                ref.put("code","A00003");
                ref.put("msg","链接无效!");
                writeObjectToJson(resp, ref);
                return;
            }
        }
        HtmlReport htmlReport = new HtmlReport();
        if(ToolUtil.isEmpty(__pass) || !__pass.equals(password)){
            ref.put("code","B00001");
            ref.put("msg","访问密码错误!");
            writeObjectToJson(resp, ref);
            return;
        }
        if(StringUtils.isNotBlank(pageIndex) && !pageIndex.equals("0")){
            int index=Integer.valueOf(pageIndex);
            htmlReport=exportManager.exportHtml(_file,req.getContextPath(),parameters,index);
        }else{
            htmlReport=exportManager.exportHtml(_file,req.getContextPath(),parameters);
        }
        htmlReport.setFile(_file);
        ref.put("code","000000");
        ref.put("msg","成功");
        ref.put("data",htmlReport);
        writeObjectToJson(resp, ref);
        return;
    }

    private HtmlReport loadReport(HttpServletRequest req) {
        Map<String, Object> parameters = buildParame(req);
        String pageIndex=req.getParameter("_i");
        String __surl = (String) parameters.get("__surl");
        String __pass = (String) parameters.get("__pass");
        String password = "";
        if(ToolUtil.isEmpty(__surl)){
            throw new RxcException("链接无效!","A00003");
        }
        //分页 当前页
        if(ToolUtil.isNotEmpty(pageIndex)){
            parameters.put("__page", Integer.parseInt(pageIndex));
        }
        String _file = "";
        //1. 有限制
        String code = redisUtil.get(__surl);
        if(ToolUtil.isEmpty(code)){
            Map<String, Object> map = UreportUtils.isShare("fast-"+__surl);
            if((Boolean) map.get("isShare")
                    && (Integer) map.get("time") == 0){
                _file = (String) map.get("file");
                password = (String) map.get("pass");
            }else{
                throw new RxcException("链接无效!","A00003");
            }
        }else{
            password = code.split("#")[1];
            code = code.split("#")[0];
            Map<String, Object> map = UreportUtils.isShare("fast-"+code);
            if((Boolean) map.get("isShare")){
                _file = (String) map.get("file");
            }else{
                throw new RxcException("链接无效!","A00003");
            }
        }

        if(ToolUtil.isEmpty(__pass) || !__pass.equals(password)){
            throw new RxcException("访问密码错误","B00001");
        }

        HtmlReport htmlReport = new HtmlReport();
        if(StringUtils.isNotBlank(pageIndex) && !pageIndex.equals("0")){
            int index=Integer.valueOf(pageIndex);
            htmlReport=exportManager.exportHtml(_file,req.getContextPath(),parameters,index);
        }else{
            htmlReport=exportManager.exportHtml(_file,req.getContextPath(),parameters);
        }

        htmlReport.setFile(_file);

        return htmlReport;
    }

    public void setExportManager(ExportManager exportManager) {
        this.exportManager = exportManager;
    }

    public void setRedisUtil(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    public void setReportBuilder(ReportBuilder reportBuilder) {
        this.reportBuilder = reportBuilder;
    }
    public void setReportRender(ReportRender reportRender) {
        this.reportRender = reportRender;
    }


    @Override
    public String url() {
        return "/shareview";
    }
}
