package com.bstek.ureport.chart.dataset.impl.category;

import cn.hutool.core.util.StrUtil;
import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.chart.dataset.CollectType;
import com.bstek.ureport.chart.dataset.EDataset;
import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.model.Cell;
import com.bstek.ureport.utils.DataUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class ECategoryDataset implements EDataset {

    private CollectType collectType=CollectType.select;

    //API接口URL
    private String apiUrl;

    private String dataType;

    //数据集对象
    private String datasetName;
    //类别 X
    private String categoryProperty;
    //
    private String seriesProperty;
    //值属性 Y
    private String valueProperty;

    private String seriesText;
    private SeriesType seriesType=SeriesType.text;

    private String labels;
    private String format;
    
    @JsonIgnore
    private Map<Object, Map<Object,List<Object>>> seriesDataMap;

    public Map<Object, Map<Object, List<Object>>> getSeriesDataMap() {
        return seriesDataMap;
    }

    public void setSeriesDataMap(Map<Object, Map<Object, List<Object>>> seriesDataMap) {
        this.seriesDataMap = seriesDataMap;
    }

    protected String buildDatasetJson(Context context, Cell cell, String props){

        if(!"sql".equals(dataType)){
            //API
            if("api".equals(dataType)){
                StringBuffer sb = new StringBuffer();
                sb.append("apiUrl:'"+apiUrl+"'");
                return sb.toString();
            }

            return StrUtil.EMPTY;
        }

        List<?> dataList= DataUtils.fetchData(cell, context, datasetName);
        List<Object> categoryList=new ArrayList<Object>();
        //系列数据
        Map<Object, Map<Object,List<Object>>> seriesDataMap=new LinkedHashMap<Object,Map<Object,List<Object>>>();
        for(Object obj:dataList){
            Object category= Utils.getProperty(obj, categoryProperty);
            if(category==null){
                continue;
            }
            if(!categoryList.contains(category)){
                categoryList.add(category);
            }
            //系列
            Object series=null;
            if(seriesType.equals(SeriesType.property)){
                series=Utils.getProperty(obj, seriesProperty);
            }else{
                series=seriesText;
            }

            if(series==null){
                continue;
            }

            //值属性 Y
            Object value=Utils.getProperty(obj, valueProperty);
            if(value==null){
                continue;
            }

            if(seriesDataMap.containsKey(series)){
                Map<Object,List<Object>> categoryMap=seriesDataMap.get(series);
                List<Object> valueList=null;
                if(categoryMap.containsKey(category)){
                    valueList=categoryMap.get(category);
                }else{
                    valueList=new ArrayList<Object>();
                    categoryMap.put(category, valueList);
                }
                valueList.add(value);
            }else{
                Map<Object,List<Object>> categoryMap=new LinkedHashMap<Object,List<Object>>();
                seriesDataMap.put(series, categoryMap);
                for(Object cg:categoryList){
                    categoryMap.put(cg, new ArrayList<Object>());
                }
                List<Object> valueList=categoryMap.get(category);
                valueList.add(value);
            }
        }
        setLabels(toLabel(categoryList));
        setSeriesDataMap(seriesDataMap);
        return buildDatasets(seriesDataMap,props);
    }


    protected String apibuildDatasets(String props){
        StringBuilder sb = new StringBuilder();
        if("basicBar".equals(props)){
            sb.append("{type:'bar'");
            sb.append("}");
        }else if("pie_simple".equals(props) ||
                "pie_doughnut".equals(props)){
            sb.append("type:'pie'");
        }else if("pie_roseType".equals(props)){
            sb.append("type:'pie',roseType: 'area'");
        }

        return sb.toString();
    }

    //数据
    protected String buildDatasets(Map<Object,Map<Object,List<Object>>> map,String props){
        StringBuilder sb = new StringBuilder();
        int i=0;
        if("basicBar".equals(props)){
            for(Object series:map.keySet()){
                sb.append("{type:'bar',");
                sb.append("data:"+buildData(map.get(series)));
                sb.append(",name:'"+series+"'");
                sb.append("}");
            }
        }
        if("bar_Background".equals(props)){
            for(Object series:map.keySet()){
                sb.append("{type:'bar',");
                sb.append("data:"+buildData(map.get(series)));
                sb.append(",name:'"+series+"'");
                //sb.append("}");
            }
        }
//        for(Object series:map.keySet()){
//            if(i>0){
//                sb.append(",");
//            }
//            sb.append("{");
//            sb.append("\"label\":\""+series+"\",");
//            String color=null;
//            if(this instanceof LineDataset){
//                color="rgb("+getRgbColor(i)+")";
//            }else{
//                color="rgba("+getRgbColor(i)+",0.3)";
//            }
//            sb.append("\"backgroundColor\":\""+color+"\",");
//            sb.append("\"borderColor\":\"rgb("+getRgbColor(i)+")\",");
//            sb.append("\"borderWidth\": 1,");
//            sb.append("\"data\":"+buildData(map.get(series)));
//            if(this instanceof LineDataset){
//                sb.append(",");
//                if(this instanceof AreaDataset){
//                    sb.append("\"fill\":true");
//                }else{
//                    sb.append("\"fill\":false");
//                }
//            }
//            if(props!=null){
//                sb.append(","+props);
//            }
//            sb.append("}");
//            i++;
//        }
        return sb.toString();
    }

    /**
     * [data1,data2]
     * @param categoryMap
     * @return
     */
    protected String buildData(Map<Object,List<Object>> categoryMap){
        StringBuilder sb=new StringBuilder();
        sb.append("[");
        for(Object category:categoryMap.keySet()){
            List<Object> list=categoryMap.get(category);
            double data=collectData(list);
            if(sb.length()>1){
                sb.append(",");
            }
            sb.append(data);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * [{name:"",value:""},{name:"",value:""}]
     * @param categoryMap
     * @return
     */
    protected String buildNameValueData(Map<Object,List<Object>> categoryMap){
        StringBuilder sb=new StringBuilder();
        sb.append("[");
        for(Object category:categoryMap.keySet()){
            List<Object> list=categoryMap.get(category);
            double data=collectData(list);
            if(sb.length()>1){
                sb.append(",");
            }
            sb.append("{name:'"+category+"',value:"+data+"}");
        }
        sb.append("]");
        return sb.toString();
    }

    protected double collectData(List<Object> list){
        double result=0;
        if(list.size()==0){
            return result;
        }
        switch(collectType){
            case select:
                result = Utils.toBigDecimal(list.get(0)).doubleValue();
                break;
            case avg:
                double total=0;
                for(Object data:list){
                    total+=Utils.toBigDecimal(data).doubleValue();
                }
                result=Utils.toBigDecimal(total).divide(Utils.toBigDecimal(list.size()),8, BigDecimal.ROUND_HALF_UP).doubleValue();
                break;
            case count:
                result=list.size();
                break;
            case max:
                Double max=null;
                for(Object data:list){
                    double value=Utils.toBigDecimal(data).doubleValue();
                    if(max==null){
                        max=value;
                    }else if(max<value){
                        max=value;
                    }
                }
                result=max;
                break;
            case min:
                Double min=null;
                for(Object data:list){
                    double value=Utils.toBigDecimal(data).doubleValue();
                    if(min==null){
                        min=value;
                    }else if(min>value){
                        min=value;
                    }
                }
                result=min;
                break;
            case sum:
                for(Object data:list){
                    double value=Utils.toBigDecimal(data).doubleValue();
                    result+=value;
                }
                break;
        }
        return result;
    }
    private String toLabel(List<Object> categoryList){
        StringBuilder sb=new StringBuilder();
        for(Object obj:categoryList){
            if(sb.length()>0){
                sb.append(",");
            }else{
                sb.append("[");
            }
            if(StringUtils.isNotBlank(format)){
                if(obj instanceof Date){
                    Date date=(Date)obj;
                    SimpleDateFormat sd=new SimpleDateFormat(format);
                    obj=sd.format(date);
                }else{
                    try{
                        BigDecimal data=Utils.toBigDecimal(obj);
                        DecimalFormat df=new DecimalFormat(format);
                        obj=df.format(data.doubleValue());
                    }catch(Exception ex){
                        throw new ReportComputeException("Can not format data ["+obj+"] use pattern ["+format+"]");
                    }
                }
            }
            sb.append("'"+obj+"'");
        }
        if(sb.length()==0){
            sb.append("[");
        }
        sb.append("]");
        return sb.toString();
    }

    public String getSeriesText() {
        return seriesText;
    }

    public void setSeriesText(String seriesText) {
        this.seriesText = seriesText;
    }


    public SeriesType getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(SeriesType seriesType) {
        this.seriesType = seriesType;
    }

    public CollectType getCollectType() {
        return collectType;
    }

    public void setCollectType(CollectType collectType) {
        this.collectType = collectType;
    }

    public String getDatasetName() {
        return datasetName;
    }
    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }
    public String getCategoryProperty() {
        return categoryProperty;
    }
    public void setCategoryProperty(String categoryProperty) {
        this.categoryProperty = categoryProperty;
    }
    public String getSeriesProperty() {
        return seriesProperty;
    }
    public void setSeriesProperty(String seriesProperty) {
        this.seriesProperty = seriesProperty;
    }
    public String getValueProperty() {
        return valueProperty;
    }
    public void setValueProperty(String valueProperty) {
        this.valueProperty = valueProperty;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getLabels() {
        return labels;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
