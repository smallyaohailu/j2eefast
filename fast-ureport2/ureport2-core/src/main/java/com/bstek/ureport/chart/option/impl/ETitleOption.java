package com.bstek.ureport.chart.option.impl;

import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.utils.ToolUtils;

/**
 * 标题
 * @author huanzhou
 */
public class ETitleOption  implements Option {

    private boolean display;
    //标题
    private String text;
    //距左边
    private String left;
    //字体大小
    private String fontSize;
    //颜色
    private String color;
    //距顶部
    private String top;

    @Override
    public String buildOptionJson() {
        if(!display){
            return "title:{show: false}";
        }
        StringBuilder sb=new StringBuilder();
        sb.append("title:{");
        sb.append("text:'"+text+"',");
        if(ToolUtils.isNotEmpty(left)){
            sb.append("left:'"+left+"',");
        }
        if(ToolUtils.isNotEmpty(top)){
            sb.append("top:'"+top+"',");
        }
        if(ToolUtils.isNotEmpty(fontSize) ||
                ToolUtils.isNotEmpty(color)){
            sb.append("textStyle:{");
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
        return "title";
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }
}
