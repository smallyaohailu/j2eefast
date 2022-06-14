package com.bstek.ureport.chart.dataset.impl.category;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.chart.plugins.Plugin;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.utils.ToolUtils;

import java.util.List;

/**
 * 多数据柱状图
 * @author J2eeFAST
 */
public class RotationBarDateset extends ECategoryDataset{

    @Override
    public String BeforeDataJson(Context context, Cell cell, List<Option> options, List<Plugin> plugins) {
        StringBuffer sb = new StringBuffer();
        String data = buildDatasetJson(context, cell,getType());
        if("sql".equals(this.getDataType())){
            sb.append("data:{");
            sb.append("series:[");
            for(Object series:this.getSeriesDataMap().keySet()){
                sb.append("{type:'bar',emphasis:{focus: 'series'},");
                sb.append("data:"+buildData(this.getSeriesDataMap().get(series)));
                sb.append(",name:'"+series+"'");
                sb.append("},");
            }
            sb.append("],");
        }else{
            sb.append(data);
            sb.append(",data:{series:[");
            sb.append("{type:'bar',emphasis:{focus: 'series'},data:[]");
            sb.append("}],");
        }
        return sb.toString();
    }


    @Override
    public String AfterDataJson(Context context, Cell cell, List<Option> options, List<Plugin> plugins) {
        StringBuffer sb = new StringBuffer();
        sb.append("xAxis:{type: 'category',data:");
        sb.append((getLabels() == null ? "[]": getLabels()));
        sb.append("},");
        sb.append("yAxis: {type: 'value'}");
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
        return "Bar_Label_Rotation";
    }

    @Override
    public String getDateType() {
        return super.getDataType();
    }
}
