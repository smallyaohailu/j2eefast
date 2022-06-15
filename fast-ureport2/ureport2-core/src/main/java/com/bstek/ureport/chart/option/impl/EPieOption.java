package com.bstek.ureport.chart.option.impl;

import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.utils.ToolUtils;

/**
 * 饼状属性
 */
public class EPieOption  implements Option {
    //半径
    private String radius;

    //是否连线
    private String labelLine;

    @Override
    public String buildOptionJson() {
        StringBuilder sb=new StringBuilder();
        if (ToolUtils.isNotEmpty(radius)) {
            // radius: ['40%', '60%'],
            sb.append("radius:");
            if(radius.indexOf("[") != -1){
                sb.append(radius).append(",");
            }else{
                sb.append("'"+radius+"'").append(",");
            }
        }
        if (ToolUtils.isNotEmpty(labelLine)) {
            // // labelLine: {
            //      //   show: true
            //      // },
            sb.append("labelLine:{");
            sb.append("show:").append(labelLine);
            sb.append("},");
        }
        int len = sb.toString().length();
        return len == 0 ? "" :sb.toString().substring(0,len-1);
    }

    @Override
    public String getType() {
        return "pie";
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getLabelLine() {
        return labelLine;
    }

    public void setLabelLine(String labelLine) {
        this.labelLine = labelLine;
    }
}
