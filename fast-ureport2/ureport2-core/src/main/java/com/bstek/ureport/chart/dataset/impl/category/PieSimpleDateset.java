package com.bstek.ureport.chart.dataset.impl.category;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.chart.plugins.Plugin;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.utils.ToolUtils;

import java.util.List;

/**
 * 饼状图
 * @author J2eeFAST
 */
public class PieSimpleDateset extends ECategoryDataset{

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
            sb.append(",data:{series:[{");
            sb.append("type:'pie'");
        }
        if(options!=null && options.size()>0){
            for(int i=0;i<options.size();i++){
                Option option=options.get(i);
                if("pie".equals(option.getType())){
                    sb.append(","+option.buildOptionJson());
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
        return "pie_simple";
    }

    @Override
    public String getDateType() {
        return super.getDataType();
    }
}
