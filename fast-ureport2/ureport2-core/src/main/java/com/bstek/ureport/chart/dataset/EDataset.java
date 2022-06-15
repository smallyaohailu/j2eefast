package com.bstek.ureport.chart.dataset;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.chart.plugins.Plugin;
import com.bstek.ureport.model.Cell;

import java.util.List;

public interface EDataset {

    String BeforeDataJson(Context context, Cell cell, List<Option> options,List<Plugin> plugins);

    String AfterDataJson(Context context, Cell cell, List<Option> options, List<Plugin> plugins);

    String getType();

    //获取数据源类型
	String getDateType();

}
