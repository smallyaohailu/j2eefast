package com.bstek.ureport.chart;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.dataset.EDataset;
import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.chart.plugins.Plugin;
import com.bstek.ureport.model.Cell;

import java.util.ArrayList;
import java.util.List;

public class EChart {

    private EDataset dataset;
    private List<Option> options=new ArrayList<Option>();
    private List<Plugin> plugins=new ArrayList<Plugin>();

    public ChartData doCompute(Cell cell, Context context){
        StringBuilder sb=new StringBuilder();
        String type = dataset.getType();
        // 返回前端数据集类型 - 与具体图形样式
        sb.append("{type:'"+dataset.getDateType()+"',chartType:'"+type+"',");

        // 拼接前半部分
        sb.append(dataset.BeforeDataJson(context, cell,options,plugins));

        // 后部分
        sb.append(dataset.AfterDataJson(context,cell,options,plugins));

//        if(options!=null && options.size()>0){
//            for(int i=0;i<options.size();i++){
//                Option option=options.get(i);
//                if("title".equals(option.getType())){
//                    sb.append(","+option.buildOptionJson());
//                }
//                if("tool".equals(option.getType())){
//                    sb.append(",tooltip:{");
//                    option.buildOptionJson();
//                    sb.append("}");
//                }
//            }
//        }

//        if("basicBar".equals(type)){
//            sb.append(dataset.buildDataJson(context, cell));
//            sb.append("xAxis:{type: 'category',data:");
//            sb.append(dataset.buildLabelsJson(context,cell));
//            sb.append("},");
//            sb.append("yAxis: {type: 'value'}");
//            if(options!=null && options.size()>0){
//                for(int i=0;i<options.size();i++){
//                    Option option=options.get(i);
//                    if("title".equals(option.getType())){
//                        sb.append(","+option.buildOptionJson());
//                    }
//                    if("tool".equals(option.getType())){
//                        sb.append(",tooltip:{");
//                        option.buildOptionJson();
//                        sb.append("}");
//                    }
//                }
//            }
//        }else if("bar_Background".equals(type)){
//            sb.append(dataset.buildDataJson(context, cell));
//            if(options!=null && options.size()>0){
//                for(int i=0;i<options.size();i++){
//                    Option option=options.get(i);
//                    if("cylinder".equals(option.getType())){
//                        sb.append(","+option.buildOptionJson());
//                        break;
//                    }
//                }
//            }
//            if (dataset.getDateType().equals("sql")) {
//                sb.append("}],");
//            } else {
//                sb.append(",data: []}],");
//            }
//            sb.append("xAxis:{type: 'category',data:");
//            sb.append(dataset.buildLabelsJson(context,cell));
//            sb.append("},");
//            sb.append("yAxis: {type: 'value'}");
//            if(options!=null && options.size()>0){
//                for(int i=0;i<options.size();i++){
//                    Option option=options.get(i);
//                    if("cylinder".equals(option.getType())){
//                        continue;
//                    }
//                    if("title".equals(option.getType())){
//                        sb.append(","+option.buildOptionJson());
//                    }
//                    if("tool".equals(option.getType())){
//                        sb.append(",tooltip:{");
//                        option.buildOptionJson();
//                        sb.append("}");
//                    }
//                }
//            }
//        }
//        else if("line_basic".equals(type) || "line_area".equals(type)){
//            sb.append(dataset.buildDataJson(context, cell));
//            if(options!=null && options.size()>0){
//                for(int i=0;i<options.size();i++){
//                    Option option=options.get(i);
//                    if("line".equals(option.getType())){
//                        sb.append(","+option.buildOptionJson());
//                        break;
//                    }
//                }
//            }
//            sb.append(",data: []}],");
//            sb.append("xAxis:{type: 'category',data:");
//            sb.append(dataset.buildLabelsJson(context,cell));
//            sb.append("},");
//            sb.append("yAxis: {type: 'value'}");
//            if(options!=null && options.size()>0){
//                for(int i=0;i<options.size();i++){
//                    Option option=options.get(i);
//                    if("title".equals(option.getType())){
//                        sb.append(","+option.buildOptionJson());
//                    }
//                    if("tool".equals(option.getType())){
//                        sb.append(",tooltip:{");
//                        option.buildOptionJson();
//                        sb.append("}");
//                    }
//                    if("legend".equals(option.getType())){
//                        sb.append(",legend:{");
//                        sb.append(option.buildOptionJson());
//                        sb.append(",data:");
//                        sb.append(dataset.buildLabelsJson(context,cell));
//                        sb.append("}");
//                    }
//                }
//            }
//        }
//        else if("line_stack".equals(type) || "line_step".equals(type) ){
//            sb.append(dataset.buildDataJson(context, cell));
//            if(options!=null && options.size()>0){
//                for(int i=0;i<options.size();i++){
//                    Option option=options.get(i);
//                    if("line".equals(option.getType())){
//                        sb.append(","+option.buildOptionJson());
//                        break;
//                    }
//                }
//            }
//            sb.append(",data:[]}],xAxis:{type: 'category',data:");
//            sb.append(dataset.buildLabelsJson(context,cell));
//            sb.append("},");
//            sb.append("yAxis: {type: 'value'}");
//            if(options!=null && options.size()>0){
//                for(int i=0;i<options.size();i++){
//                    Option option=options.get(i);
//                    if("title".equals(option.getType())){
//                        sb.append(","+option.buildOptionJson());
//                    }
//                    if("tool".equals(option.getType())){
//                        sb.append(",tooltip:{");
//                        option.buildOptionJson();
//                        sb.append("}");
//                    }
//                    if("legend".equals(option.getType())){
//                        sb.append(",legend:{");
//                        sb.append(option.buildOptionJson());
//                        sb.append(",data:");
//                        sb.append(dataset.buildLabelsJson(context,cell));
//                        sb.append("}");
//                    }
//                }
//            }
//        }
//        else if("Bar_Label_Rotation".equals(type) ||
//                "bar_stack".equals(type)){
//            sb.append(dataset.buildDataJson(context, cell));
//            if("Bar_Label_Rotation".equals(type)){
//                sb.append("xAxis:{type: 'category',data:");
//                sb.append(dataset.buildLabelsJson(context,cell));
//                sb.append("},");
//                sb.append("yAxis: {type: 'value'}");
//            }
//
//            if("bar_stack".equals(type)){
//                sb.append("yAxis:{type: 'category',data:");
//                sb.append(dataset.buildLabelsJson(context,cell));
//                sb.append("},");
//                sb.append("xAxis: {type: 'value'}");
//            }
//
//            if(options!=null && options.size()>0){
//                for(int i=0;i<options.size();i++){
//                    Option option=options.get(i);
//                    if("title".equals(option.getType())){
//                        sb.append(","+option.buildOptionJson());
//                    }
//                    if("tool".equals(option.getType())){
//                        sb.append(",tooltip:{");
//                        option.buildOptionJson();
//                        sb.append("}");
//                    }
//                    if("legend".equals(option.getType())){
//                        sb.append(",legend:{");
//                        sb.append(option.buildOptionJson());
//                        sb.append(",data:");
//                        sb.append(dataset.buildLabelsJson(context,cell));
//                        sb.append("}");
//                    }
//                }
//            }
//            sb.append(",toolbox: {");
//            sb.append("show:true,");
//            sb.append("orient:'vertical',");
//            sb.append("left:'right',");
//            sb.append("top:'center',");
//            sb.append("feature:{");
//            sb.append("mark:{show:true},");
//            sb.append("dataView:{show:true,readOnly: false },");
//            sb.append("magicType:{show:true,type:['line','bar','stack']},");
//            sb.append("restore:{show:true},");
//            sb.append("saveAsImage:{show:true}");
//            sb.append("}}");
//        }else if("pie_simple".equals(type) || "pie_doughnut".equals(type)
//                || "pie_roseType".equals(type)){
//            sb.append(dataset.buildDataJson(context, cell));
//            if(options!=null && options.size()>0){
//                for(int i=0;i<options.size();i++){
//                    Option option=options.get(i);
//                    if("pie".equals(option.getType())){
//                        sb.append(","+option.buildOptionJson());
//                        break;
//                    }
//                }
//            }
//            sb.append(",data: []}]");
//            if(options!=null && options.size()>0){
//                for(int i=0;i<options.size();i++){
//                    Option option=options.get(i);
//                    if("pie".equals(option.getType())){
//                        continue;
//                    }
//                    if("title".equals(option.getType())){
//                        sb.append(","+option.buildOptionJson());
//                    }
//                    if("tool".equals(option.getType())){
//                        sb.append(",tooltip:{");
//                        option.buildOptionJson();
//                        sb.append("}");
//                    }
//                    if("legend".equals(option.getType())){
//                        sb.append(",legend:{");
//                        sb.append(option.buildOptionJson());
//                        sb.append("}");
//                    }
//                }
//            }
//        }
        sb.append("}}");
        System.out.println(sb.toString());
        //组织图表JSON
        ChartData chartData=new ChartData(sb.toString(),cell,"echart");
        context.addEchartData(chartData);
        return chartData;
    }

    public EDataset getDataset() {
        return dataset;
    }

    public void setDataset(EDataset dataset) {
        this.dataset = dataset;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }
}
