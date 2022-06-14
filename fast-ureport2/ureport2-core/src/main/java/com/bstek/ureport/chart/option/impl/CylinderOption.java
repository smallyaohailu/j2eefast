package com.bstek.ureport.chart.option.impl;

import com.bstek.ureport.chart.option.Option;

/**
 * @author huanzhou
 */
public class CylinderOption  implements Option {

    private boolean display=false;

    private String color;

    @Override
    public String buildOptionJson() {
        StringBuffer sb = new StringBuffer();
        if(display){
            sb.append("showBackground: true,");
            //backgroundStyle: {
            //                      color: 'rgba(180, 180, 180, 0.2)'
            //                    }
            sb.append("backgroundStyle:{");
            sb.append("color:'"+color+"'");
            sb.append("}");
        }
        return sb.toString();
    }

    @Override
    public String getType() {
        return "cylinder";
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
