package com.bstek.ureport.chart.option.impl;

import com.bstek.ureport.chart.option.Option;

/**
 * 背景颜色
 * @author huanzhou
 */
public class EBodyOption implements Option {

    private boolean display;
    private String backgroundColor;

    @Override
    public String buildOptionJson() {
        StringBuilder sb=new StringBuilder();
        if(display){
            sb.append("\"backgroundColor\":\""+backgroundColor+"\"");
        }
        return sb.toString();
    }


    @Override
    public String getType() {
        return "body";
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
