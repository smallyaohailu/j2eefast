package com.bstek.ureport.chart.dataset.impl.category;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.chart.plugins.Plugin;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.utils.ToolUtils;

import java.util.List;

/**
 * 柱状堆叠
 * @author huanzhou
 */
public class BarStackDateset extends ECategoryDataset{

    @Override
    public String BeforeDataJson(Context context, Cell cell, List<Option> options, List<Plugin> plugins) {
        StringBuffer sb = new StringBuffer();
        if("sql".equals(this.getDataType())){
            sb.append("data:{");
            sb.append("series:[");
            sb.append(buildDatasetJson(context, cell,getType()));
            sb.append("],");
        }else{
            sb.append(buildDatasetJson(context, cell,getType()));
            sb.append(",data:{series:[");
            sb.append("{type:'bar',emphasis:{focus: 'series'},stack: 'total',data:[]");
            sb.append("}],");
        }
        return sb.toString();
    }


    @Override
    public String AfterDataJson(Context context, Cell cell, List<Option> options, List<Plugin> plugins) {
        StringBuffer sb = new StringBuffer();
        sb.append("yAxis:{type: 'category',data:");
        sb.append((getLabels() == null ? "[]": getLabels()));
        sb.append("},");
        sb.append("xAxis: {type: 'value'}");
        if(options!=null && options.size()>0){
            for(int i=0;i<options.size();i++){
                Option option=options.get(i);
                if(ToolUtils.isNotEmpty(option.buildOptionJson())){
                    sb.append(","+option.buildOptionJson());
                }
            }
        }
        //添加工具栏
        sb.append(",toolbox: {");
        sb.append("show:true,");
        sb.append("orient:'vertical',");
        sb.append("left:'right',");
        sb.append("top:'center',");
        sb.append("feature:{");
        sb.append("mark:{show:true},");
        sb.append("dataView:{show:true,readOnly: false },");
        sb.append("magicType:{show:true,type:['line','bar','stack']},");
        sb.append("restore:{show:true},");
        sb.append("saveAsImage:{show:true}");
        sb.append("}}");
        return sb.toString();
    }

    @Override
    public String getType() {
        return "bar_stack";
    }

    @Override
    public String getDateType() {
            return super.getDataType();
    }
}
