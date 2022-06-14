package com.bstek.ureport.chart.option.impl;

import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.utils.ToolUtils;

public class ELegendOption implements Option {

    private boolean display=true;
    private String orient;
    private String top;
    private String left;
    private String color;
    private String fontSize;

    @Override
    public String buildOptionJson() {
        StringBuilder sb=new StringBuilder();
        sb.append("legend:{");
        sb.append("show:"+display+"");
        sb.append(",top:'"+top+"'");
        if(ToolUtils.isNotEmpty(orient)){
            sb.append(",orient:'"+orient+"'");
        }
        sb.append(",left:'"+left+"'");
        if(display && (ToolUtils.isNotEmpty(fontSize) ||
                ToolUtils.isNotEmpty(color))){
            sb.append(",textStyle:{");
            if(ToolUtils.isNotEmpty(color)){
                sb.append("color:'"+color+"',");
            }
            if(ToolUtils.isNotEmpty(fontSize)){
                sb.append("fontSize:'"+fontSize+"'");
            }
            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String getType() {
        return "legend";
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getOrient() {
        return orient;
    }

    public void setOrient(String orient) {
        this.orient = orient;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }
}
