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

import cn.hutool.core.util.StrUtil;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.build.paging.Page;
import com.bstek.ureport.cache.CacheUtils;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.definition.PagingMode;
import com.bstek.ureport.definition.ReportDefinition;
import com.bstek.ureport.definition.dataset.DatasetDefinition;
import com.bstek.ureport.definition.dataset.Parameter;
import com.bstek.ureport.definition.dataset.SqlDatasetDefinition;
import com.bstek.ureport.definition.datasource.DataType;
import com.bstek.ureport.export.*;
import com.bstek.ureport.export.excel.high.ExcelProducer;
import com.bstek.ureport.export.excel.low.Excel97Producer;
import com.bstek.ureport.export.html.HtmlProducer;
import com.bstek.ureport.export.html.HtmlReport;
import com.bstek.ureport.export.pdf.PdfProducer;
import com.bstek.ureport.export.word.high.WordProducer;
import com.bstek.ureport.expression.ExpressionUtils;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.model.Report;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.utils.ToolUtil;
import java.util.List;
import java.util.Map;

/**
 * 自动注入数据过滤SQL
 * @author huanzhou、ZhouZhou
 */
public class FastExportManagerImpl implements ExportManager {

    private ReportRender reportRender;
    private HtmlProducer htmlProducer=new HtmlProducer();
    private WordProducer wordProducer=new WordProducer();
    private ExcelProducer excelProducer=new ExcelProducer();
    private Excel97Producer excel97Producer=new Excel97Producer();
    private PdfProducer pdfProducer=new PdfProducer();
    private static final String _SQLFILTER_ = "_SQLFILTER_";
    @Override
    public HtmlReport exportHtml(String file, String contextPath, Map<String, Object> parameters) {


//        ReportDefinition reportDefinition= new ReportDefinition();
//        reportDefinition= reportRender.parseReport(file);
//        reportRender.rebuildReportDefinition(reportDefinition);
//        CacheUtils.cacheReportDefinition(file, reportDefinition);

        //不留缓存--
        ReportDefinition reportDefinition=reportRender.getReportDefinition(file);

        //J2eeFAST 修改支持 数据过滤注入
        setSqlFilter(reportDefinition,parameters);
        //---------------------

        Report report=reportRender.render(reportDefinition, parameters);
        Map<String, ChartData> chartMap=report.getContext().getChartDataMap();
        if(chartMap.size()>0){
            CacheUtils.storeChartDataMap(chartMap);
        }
        HtmlReport htmlReport=new HtmlReport();
        String content=htmlProducer.produce(report);
        htmlReport.setContent(content);
        if(reportDefinition.getPaper().isColumnEnabled()){
            htmlReport.setColumn(reportDefinition.getPaper().getColumnCount());
        }
        htmlReport.setWatermark(reportDefinition.getPaper().getWatermark());
        htmlReport.setStyle(reportDefinition.getStyle());
        htmlReport.setPage(reportDefinition.getPaper().getPagingMode().equals(PagingMode.fixrows));
        htmlReport.setSearchFormData(reportDefinition.buildSearchFormData(report.getContext().getDatasetMap(),parameters));
        htmlReport.setReportAlign(report.getPaper().getHtmlReportAlign().name());
        htmlReport.setChartDatas(report.getContext().getChartDataMap().values());
        htmlReport.setHtmlIntervalRefreshValue(report.getPaper().getHtmlIntervalRefreshValue());
        return htmlReport;
    }

    @Override
    public HtmlReport exportHtml(String file,String contextPath,Map<String, Object> parameters, int pageIndex) {
        ReportDefinition reportDefinition=reportRender.getReportDefinition(file);
        //J2eeFAST 修改支持 数据过滤注入
        setSqlFilter(reportDefinition,parameters);
        //---------------------
        Report report= reportRender.render(reportDefinition, parameters);
        Map<String, ChartData> chartMap=report.getContext().getChartDataMap();

        if(chartMap.size()>0){
            CacheUtils.storeChartDataMap(chartMap);
        }

        int __page = pageIndex;
        int totalPages = 0;
        if(ToolUtil.isNotEmpty(parameters.get("__paging")) &&
                (boolean)parameters.get("__paging")){
            __page = 1;
        }

        SinglePageData pageData= PageBuilder.buildSinglePageData(__page, report);

        List<Page> pages=pageData.getPages();

        String content=null;
        if(pages.size()==1){
            content=htmlProducer.produce(report.getContext(),pages.get(0),false);
        }else{
            content=htmlProducer.produce(report.getContext(),pages,pageData.getColumnMargin(),false);
        }
        HtmlReport htmlReport=new HtmlReport();
        htmlReport.setContent(content);
        if(reportDefinition.getPaper().isColumnEnabled()){
            htmlReport.setColumn(reportDefinition.getPaper().getColumnCount());
        }
        htmlReport.setWatermark(reportDefinition.getPaper().getWatermark());
        htmlReport.setStyle(reportDefinition.getStyle());
        htmlReport.setSearchFormData(reportDefinition.buildSearchFormData(report.getContext().getDatasetMap(),parameters));
        htmlReport.setPageIndex(pageIndex);
        htmlReport.setPage(reportDefinition.getPaper().getPagingMode().equals(PagingMode.fixrows));
        if(ToolUtil.isNotEmpty(parameters.get("__paging")) &&
                (boolean)parameters.get("__paging")){
            Dataset value= report.getContext().getDatasetMap().entrySet().iterator().next().getValue();
            totalPages = value.getCount();
            int pageSzie = value.getPageSize();
            totalPages = totalPages / pageSzie;
            totalPages = totalPages % pageSzie > 0 ? (totalPages + 1) : totalPages;
        }else {
            totalPages = pageData.getTotalPages();
        }
        htmlReport.setTotalPage(totalPages);
        htmlReport.setReportAlign(report.getPaper().getHtmlReportAlign().name());
        htmlReport.setChartDatas(report.getContext().getChartDataMap().values());
        htmlReport.setHtmlIntervalRefreshValue(report.getPaper().getHtmlIntervalRefreshValue());
        return htmlReport;
    }

    @Override
    public void exportPdf(ExportConfigure config) {
        String file=config.getFile();
        Map<String, Object> parameters=config.getParameters();
        ReportDefinition reportDefinition=reportRender.getReportDefinition(file);
        //J2eeFAST 修改支持 数据过滤注入
        setSqlFilter(reportDefinition,parameters);
        //---------------------
        Report report=reportRender.render(reportDefinition, parameters);
        pdfProducer.produce(report, config.getOutputStream());
    }
    @Override
    public void exportWord(ExportConfigure config) {
        String file=config.getFile();
        Map<String, Object> parameters=config.getParameters();
        ReportDefinition reportDefinition=reportRender.getReportDefinition(file);
        //J2eeFAST 修改支持 数据过滤注入
        setSqlFilter(reportDefinition,parameters);
        //---------------------
        Report report=reportRender.render(reportDefinition, parameters);
        wordProducer.produce(report, config.getOutputStream());
    }
    @Override
    public void exportExcel(ExportConfigure config) {
        String file=config.getFile();
        Map<String, Object> parameters=config.getParameters();
        ReportDefinition reportDefinition=reportRender.getReportDefinition(file);
        //J2eeFAST 修改支持 数据过滤注入
        setSqlFilter(reportDefinition,parameters);
        //---------------------
        Report report=reportRender.render(reportDefinition, parameters);
        excelProducer.produce(report, config.getOutputStream());
    }

    @Override
    public void exportExcel97(ExportConfigure config) {
        String file=config.getFile();
        Map<String, Object> parameters=config.getParameters();
        ReportDefinition reportDefinition=reportRender.getReportDefinition(file);
        //J2eeFAST 修改支持 数据过滤注入
        setSqlFilter(reportDefinition,parameters);
        //---------------------
        Report report=reportRender.render(reportDefinition, parameters);
        excel97Producer.produce(report, config.getOutputStream());
    }

    @Override
    public void exportExcelWithPaging(ExportConfigure config) {
        String file=config.getFile();
        Map<String, Object> parameters=config.getParameters();
        ReportDefinition reportDefinition=reportRender.getReportDefinition(file);
        //J2eeFAST 修改支持 数据过滤注入
        setSqlFilter(reportDefinition,parameters);
        //---------------------
        Report report=reportRender.render(reportDefinition, parameters);
        excelProducer.produceWithPaging(report, config.getOutputStream());
    }
    @Override
    public void exportExcel97WithPaging(ExportConfigure config) {
        String file=config.getFile();
        Map<String, Object> parameters=config.getParameters();
        ReportDefinition reportDefinition=reportRender.getReportDefinition(file);
        //J2eeFAST 修改支持 数据过滤注入
        setSqlFilter(reportDefinition,parameters);
        //---------------------
        Report report=reportRender.render(reportDefinition, parameters);
        excel97Producer.produceWithPaging(report, config.getOutputStream());
    }

    @Override
    public void exportExcelWithPagingSheet(ExportConfigure config) {
        String file=config.getFile();
        Map<String, Object> parameters=config.getParameters();
        ReportDefinition reportDefinition=reportRender.getReportDefinition(file);
        //J2eeFAST 修改支持 数据过滤注入
        setSqlFilter(reportDefinition,parameters);
        //---------------------
        Report report=reportRender.render(reportDefinition, parameters);
        excelProducer.produceWithSheet(report, config.getOutputStream());
    }

    @Override
    public void exportExcel97WithPagingSheet(ExportConfigure config) {
        String file=config.getFile();
        Map<String, Object> parameters=config.getParameters();
        ReportDefinition reportDefinition=reportRender.getReportDefinition(file);
        //J2eeFAST 修改支持 数据过滤注入
        setSqlFilter(reportDefinition,parameters);
        //---------------------
        Report report=reportRender.render(reportDefinition, parameters);
        excel97Producer.produceWithSheet(report, config.getOutputStream());
    }

    public void setReportRender(ReportRender reportRender) {
        this.reportRender = reportRender;
    }


    /**
     * 新增SQL数据权限过滤
     * @param reportDefinition
     * @param parameters
     * @author huanzhou
     */
    public void setSqlFilter(ReportDefinition reportDefinition,Map<String, Object> parameters){
    	//报表没有设置SQL
        if(ToolUtil.isEmpty(reportDefinition.getDatasources()) ||
                ToolUtil.isEmpty(reportDefinition.getDatasources().get(0).getDatasets())){
            return;
        }

        List<DatasetDefinition> definitions = reportDefinition.getDatasources().get(0).getDatasets();

        for(DatasetDefinition dsDef : definitions){
            //获取设置的SQL
            SqlDatasetDefinition sqlDataset=(SqlDatasetDefinition)dsDef;
            String sql = sqlDataset.getSql();

            //先查看内存是否有保存_SQLFILTER_
            //前端是否添加数据过滤条件
            boolean _sqlfilter = false;
            for(Parameter p: sqlDataset.getParameters()){
                if(p.getName().equals(_SQLFILTER_)){
                    _sqlfilter = true;
                    break;
                }
            }

            //保存原始SQL
            if(StrUtil.contains(sql,StrUtil.C_COLON+ConfigConstant.SQLFILTER)){
                Parameter parameter = new Parameter();
                parameter.setName("_SQLFILTER_");
                parameter.setType(DataType.String);
                parameter.setDefaultValue(sql);
                sqlDataset.getParameters().add(parameter);
            }else if(!_sqlfilter){
                return;
            }

            //sqlDataset.setSql(sql);
            if(sql.startsWith(ExpressionUtils.EXPR_PREFIX) && sql.endsWith(ExpressionUtils.EXPR_SUFFIX)){
                //表达式SQL 注入数据过滤SQL
                sql = "";
                for(Parameter p: sqlDataset.getParameters()){
                    if(p.getName().equals(_SQLFILTER_)){
                        sql = p.getDefaultValue();
                        break;
                    }
                }
                sql = sql.substring(2,sql.length()-1);
                sql = StrUtil.replaceIgnoreCase(sql,StrUtil.COLON+ ConfigConstant.SQLFILTER,
                        (String)parameters.get(ConfigConstant.SQLFILTER));
                Expression expr=ExpressionUtils.parseExpression(sql);
                sqlDataset.setSqlExpression(expr);
            }else{
                //前端没有表达式 - 剔除and 注入数据过滤SQL
                sql = "";
                for(Parameter p: sqlDataset.getParameters()){
                    if(p.getName().equals(_SQLFILTER_)){
                        sql = p.getDefaultValue();
                        break;
                    }
                }
                if(ToolUtil.isEmpty((String)parameters.get(ConfigConstant.SQLFILTER))){
                    sql = StrUtil.replaceIgnoreCase(sql,"and "+StrUtil.COLON+ ConfigConstant.SQLFILTER,
                            (String)parameters.get(ConfigConstant.SQLFILTER));
                }else{
                    sql = StrUtil.replaceIgnoreCase(sql,StrUtil.COLON+ ConfigConstant.SQLFILTER,
                            (String)parameters.get(ConfigConstant.SQLFILTER));
                }
                sqlDataset.setSql(sql);
            }
        }

    }
}
