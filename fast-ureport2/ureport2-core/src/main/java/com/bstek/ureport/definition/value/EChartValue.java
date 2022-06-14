package com.bstek.ureport.definition.value;

import com.bstek.ureport.chart.EChart;

/**
 * @author huanzhou
 */
public class EChartValue implements Value {

    private EChart eChart;

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public ValueType getType() {
        return ValueType.echart;
    }

    public EChart geteChart() {
        return eChart;
    }

    public void seteChart(EChart eChart) {
        this.eChart = eChart;
    }
}
