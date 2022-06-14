package com.bstek.ureport.chart.option.impl;

import cn.hutool.core.util.StrUtil;
import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.utils.ToolUtils;

/**
 * @author huanzhou
 */
public class ETooltipOption implements Option {

    private boolean display;
    //字体大小
    private String fontSize;
    //颜色
    private String color;

    private String trigger;

    @Override
    public String buildOptionJson() {
        StringBuilder sb=new StringBuilder();
        if(!display){
            return StrUtil.EMPTY;
        }
        sb.append("tooltip:{");
        if (ToolUtils.isNotEmpty(trigger)) {
            sb.append("trigger:'").append(trigger).append("'");
        }
        if(ToolUtils.isNotEmpty(fontSize) ||
                ToolUtils.isNotEmpty(color)){
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
        return "tool";
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
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

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
