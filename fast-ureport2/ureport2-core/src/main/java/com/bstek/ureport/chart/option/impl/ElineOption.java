package com.bstek.ureport.chart.option.impl;

import com.bstek.ureport.chart.option.Option;

public class ElineOption implements Option {

    /**
     * 是否平滑
     */
    private String smooth = "false";

    @Override
    public String buildOptionJson() {
        StringBuffer sb = new StringBuffer();
        sb.append("smooth:" + smooth);
        return sb.toString();
    }

    @Override
    public String getType() {
        return "line";
    }

    public String getSmooth() {
        return smooth;
    }

    public void setSmooth(String smooth) {
        this.smooth = smooth;
    }
}
