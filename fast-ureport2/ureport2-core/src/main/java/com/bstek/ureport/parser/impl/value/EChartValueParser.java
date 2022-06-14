package com.bstek.ureport.parser.impl.value;

import com.bstek.ureport.chart.EChart;
import com.bstek.ureport.chart.dataset.CollectType;
import com.bstek.ureport.chart.dataset.EDataset;
import com.bstek.ureport.chart.dataset.impl.category.*;
import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.chart.option.impl.*;
import com.bstek.ureport.definition.value.EChartValue;
import com.bstek.ureport.definition.value.Value;
import com.bstek.ureport.exception.ReportParseException;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

/**
 * Echarts 图表
 * @author huanzhou
 */
public class EChartValueParser extends ValueParser {

    @Override
    public Value parse(Element element) {
        EChartValue value = new EChartValue();
        EChart chart = new EChart();
        value.seteChart(chart);
        for(Object obj:element.elements()){
            if(obj==null || !(obj instanceof Element)){
                continue;
            }
            Element ele=(Element)obj;
            String name=ele.getName();
            if(name.equals("dataset")){
                EDataset dataset = parseDataset(ele);
                chart.setDataset(dataset);
            }else if(name.equals("option")){
                chart.getOptions().add(parseOption(ele));
            }
        }
        return value;
    }

    //解析数据集
    private EDataset parseDataset(Element element) {

        String type=element.attributeValue("type");

        EDataset dataset=null;
        //基础柱形图
        if(type.equals("basicBar")){
            BasicBarDateset barDateset = new BasicBarDateset();
            dataset = barDateset;
        }else if(type.equals("bar_Background")){
            BgBarDateset barDateset = new BgBarDateset();
            dataset = barDateset;
        }else if(type.equals("Bar_Label_Rotation")){
            RotationBarDateset barDateset = new RotationBarDateset();
            dataset = barDateset;
        }else if(type.equals("bar_stack")){
            BarStackDateset barDateset = new BarStackDateset();
            dataset = barDateset;
        }else if(type.equals("line_basic")){
            ElineBasicDataset barDateset = new ElineBasicDataset();
            dataset = barDateset;
        }else if(type.equals("line_area")){
            ElineAreaDataset barDateset = new ElineAreaDataset();
            dataset = barDateset;
        }else if(type.equals("line_stack")){
            ElineStackDataset barDateset = new ElineStackDataset();
            dataset = barDateset;
        }else if(type.equals("line_step")){
            ElineStepDataset barDateset = new ElineStepDataset();
            dataset = barDateset;
        }else if(type.equals("pie_simple")){
            PieSimpleDateset barDateset = new PieSimpleDateset();
            dataset = barDateset;
        }else if(type.equals("pie_doughnut")){
            PieDoughnutDateset barDateset = new PieDoughnutDateset();
            dataset = barDateset;
        }else if(type.equals("pie_roseType")){
            PieRoseTypeDateset barDateset = new PieRoseTypeDateset();
            dataset = barDateset;
        }

        if(dataset != null && dataset instanceof ECategoryDataset){

            ECategoryDataset ds = (ECategoryDataset) dataset;

            // <dataset dataset-name="user0" type="basicBar" category-property="cName" series-type="text" value-property="value"/>
            //<dataset dataset-name="user0" type="bar" category-property="cName" series-property="value" series-type="property" value-property="value" collect-type="select"/>
            String datasetType=element.attributeValue("dataset-type");
            ds.setDataType(datasetType);
            if("api".equals(datasetType)){
                String apiUrl =element.attributeValue("api-url");
                ds.setApiUrl(apiUrl);
            }else{
                //数据集名称
                String datasetName=element.attributeValue("dataset-name");
                ds.setDatasetName(datasetName);

                // 类别 X
                String categoryProperty=element.attributeValue("category-property");
                ds.setCategoryProperty(categoryProperty);

                // 值属性 Y
                String valueProperty=element.attributeValue("value-property");
                ds.setValueProperty(valueProperty);


                String seriesType=element.attributeValue("series-type");
                if(StringUtils.isNotBlank(seriesType)){
                    ds.setSeriesType(SeriesType.valueOf(seriesType));
                }

                String seriesProperty=element.attributeValue("series-property");
                if(StringUtils.isNotBlank(seriesProperty)){
                    ds.setSeriesProperty(seriesProperty);
                }

                String seriesText = element.attributeValue("series-text");
                if(StringUtils.isNotBlank(seriesText)){
                    ds.setSeriesText(seriesText);
                }else{
                    ds.setSeriesText("__SERIES__");
                }

                //聚合方式
                String collectType=element.attributeValue("collect-type");
                if(StringUtils.isNotBlank(collectType)){
                    ds.setCollectType(CollectType.valueOf(collectType));
                }
            }
        }
        if(dataset!=null){
            return dataset;
        }
        throw new ReportParseException("Unknow chart type : "+type);
    }

    //解析图形报表设置
    private Option parseOption(Element element){
        String type=element.attributeValue("type");
        Option target=null;
        if(type.equals("title")){
            //<option type="title" position="center" display="true" color="#000000" fontSize="18" text="用户管理"/>
            ETitleOption option=new ETitleOption();
            String display= element.attributeValue("display");
            if(StringUtils.isNotBlank(display)){
                option.setDisplay(Boolean.valueOf(display));
            }

            String left=element.attributeValue("left");
            if(StringUtils.isNotBlank(left)){
                option.setLeft(left);
            }

            String top=element.attributeValue("top");
            if(StringUtils.isNotBlank(left)){
                option.setTop(top);
            }

            String text = element.attributeValue("text");
            option.setText(text);


            String fontSize=element.attributeValue("fontSize");
            if(StringUtils.isNotBlank(fontSize)){
                option.setFontSize(fontSize);
            }

            String color=element.attributeValue("color");
            if(StringUtils.isNotBlank(color)){
                option.setColor(color);
            }
            target=option;
        }else if("tool".equals(type)){
            ETooltipOption option = new ETooltipOption();
            String display= element.attributeValue("display");
            if(StringUtils.isNotBlank(display)){
                option.setDisplay(Boolean.valueOf(display));
            }
            String fontSize=element.attributeValue("fontSize");
            if(StringUtils.isNotBlank(fontSize)){
                option.setFontSize(fontSize);
            }

            String color=element.attributeValue("color");
            if(StringUtils.isNotBlank(color)){
                option.setColor(color);
            }

            String trigger=element.attributeValue("trigger");
            if(StringUtils.isNotBlank(trigger)){
                option.setTrigger(trigger);
            }

            target=option;
        }else if("legend".equals(type)){
            ELegendOption option = new ELegendOption();
            String display= element.attributeValue("display");
            if(StringUtils.isNotBlank(display)){
                option.setDisplay(Boolean.valueOf(display));
            }

            String fontSize=element.attributeValue("fontSize");
            if(StringUtils.isNotBlank(fontSize)){
                option.setFontSize(fontSize);
            }

            String color=element.attributeValue("color");
            if(StringUtils.isNotBlank(color)){
                option.setColor(color);
            }

            String left=element.attributeValue("left");
            if(StringUtils.isNotBlank(left)){
                option.setLeft(left);
            }

            String top=element.attributeValue("top");
            if(StringUtils.isNotBlank(left)){
                option.setTop(top);
            }

            String orient =element.attributeValue("orient");
            if(StringUtils.isNotBlank(orient)){
                option.setOrient(orient);
            }

            target=option;
        }else if("pie".equals(type)){
            EPieOption option = new EPieOption();
            String radius=element.attributeValue("radius");
            if(StringUtils.isNotBlank(radius)){
                option.setRadius(radius);
            }
            String labelLine =element.attributeValue("labelLine");
            if(StringUtils.isNotBlank(radius)){
                option.setLabelLine(labelLine);
            }
            target=option;
        }else if("body".equals(type)){
            EBodyOption option = new EBodyOption();
            String display= element.attributeValue("display");
            if(StringUtils.isNotBlank(display)){
                option.setDisplay(Boolean.valueOf(display));
            }
            String backgroundColor =element.attributeValue("backgroundColor");
            if(StringUtils.isNotBlank(backgroundColor)){
                option.setBackgroundColor(backgroundColor);
            }
            target=option;
        }else if("cylinder".equals(type)){

            CylinderOption option = new CylinderOption();
            String display= element.attributeValue("display");

            if(StringUtils.isNotBlank(display)){
                option.setDisplay(Boolean.valueOf(display));
            }

            String color =element.attributeValue("color");
            if(StringUtils.isNotBlank(color)){
                option.setColor(color);
            }
            target=option;
        }else if("line".equals(type)){
            ElineOption option = new ElineOption();
            String smooth= element.attributeValue("smooth");
            if(StringUtils.isNotBlank(smooth)){
                option.setSmooth(smooth);
            }
            target=option;
        }

        if(target!=null){
            return target;
        }
        throw new ReportParseException("Unknow option :"+type);
    }

}
