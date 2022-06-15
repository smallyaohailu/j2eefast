package com.bstek.ureport.build.compute;

import com.bstek.ureport.build.BindData;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.ChartData;
import com.bstek.ureport.chart.EChart;
import com.bstek.ureport.definition.value.EChartValue;
import com.bstek.ureport.definition.value.ValueType;
import com.bstek.ureport.model.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huanzhou
 */
public class EChartValueCompute implements ValueCompute {

    @Override
    public List<BindData> compute(Cell cell, Context context) {
        EChartValue chartValue = (EChartValue) cell.getValue();
        EChart chart = chartValue.geteChart();
        ChartData data=chart.doCompute(cell, context);
        List<BindData> list=new ArrayList<BindData>();
        list.add(new BindData(data));
        return list;
    }

    @Override
    public ValueType type() {
        return ValueType.echart;
    }
}
