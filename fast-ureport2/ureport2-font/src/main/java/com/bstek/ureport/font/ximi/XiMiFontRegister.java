package com.bstek.ureport.font.ximi;

import com.bstek.ureport.export.pdf.font.FontRegister;

/**
 * 小米字体
 * @author huanzhou
 */
public class XiMiFontRegister implements FontRegister {
    @Override
    public String getFontName() {
        return "MiSans";
    }

    @Override
    public String getFontPath() {
        return "com/bstek/ureport/font/ximi/MiSans-Normal.ttf";
    }
}
