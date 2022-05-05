/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.NumberChineseFormatter;
import org.springframework.stereotype.Component;

/**
 * 前端工具类
 * @author huanzhou
 */
@Component
public class Tools {

    /**
     * 阿拉伯数字转换成中文,小数点后四舍五入保留两位. 使用于整数、小数的转换.
     *
     * @param amount           数字
     * @param isUseTraditional 是否使用繁体
     * @param isMoneyMode      是否为金额模式
     * @return 中文
     */
    public String numToChinese(String amount, boolean isUseTraditional, boolean isMoneyMode){
        return NumberChineseFormatter.format(Convert.toDouble(amount),isUseTraditional,isMoneyMode);
    }

}

