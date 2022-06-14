package com.bstek.ureport.chart.dataset.impl.category;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.chart.plugins.Plugin;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.utils.ToolUtils;

import java.util.List;

/**
 * 环形图
 */
public class PieDoughnutDateset extends ECategoryDataset{

    @Override
    public String BeforeDataJson(Context context, Cell cell, List<Option> options, List<Plugin> plugins) {
        StringBuffer sb = new StringBuffer();
        String data = buildDatasetJson(context, cell,getType());
        if("sql".equals(this.getDataType())){
            sb.append("data:{");
            sb.append("series:[");
            if(ToolUtils.isNotEmpty(this.getSeriesDataMap())){
                for(Object series:this.getSeriesDataMap().keySet()){
                    sb.append("{type:'pie',");
                    sb.append("data:"+buildNameValueData(this.getSeriesDataMap().get(series)));
                    sb.append(",name:'"+series+"'");
                }
            }else{
                sb.append("{type:'pie',data:[]");
            }
        }else{
            sb.append(data);
            sb.append(",data:{series:[{");
            sb.append("type:'pie'");
        }
        if(options!=null && options.size()>0){
            for(int i=0;i<options.size();i++){
                Option option=options.get(i);
                if("pie".equals(option.getType())){
                    if(ToolUtils.isNotEmpty(option.buildOptionJson())){
                        sb.append(","+option.buildOptionJson());
                    }
                    break;
                }
            }
        }
        sb.append("}]");
        return sb.toString();
    }

    @Override
    public String AfterDataJson(Context context, Cell cell, List<Option> options, List<Plugin> plugins) {
        StringBuffer sb = new StringBuffer();
        if(options!=null && options.size()>0){
            for(int i=0;i<options.size();i++){
                Option option=options.get(i);
                if("pie".equals(option.getType())){
                    continue;
                }
                if(ToolUtils.isNotEmpty(option.buildOptionJson())){
                    sb.append(","+option.buildOptionJson());
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String getType() {
        return "pie_doughnut";
    }

    @Override
    public String getDateType() {
        return super.getDataType();
    }
}
