/*******************************************************************************
 * Copyright 2017 Bstek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.parser.impl.searchform;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.bstek.ureport.definition.searchform.LabelPosition;
import com.bstek.ureport.definition.searchform.Option;
import com.bstek.ureport.definition.searchform.SelectInputComponent;
import com.bstek.ureport.provider.report.ReportProvider;
import com.bstek.ureport.utils.ToolUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Jacky.gao
 * @since 2017年10月30日
 */
public class SelectInputParser implements FormParser<SelectInputComponent> {
	@Override
	public SelectInputComponent parse(Element element) {
		SelectInputComponent select=new SelectInputComponent();
		select.setBindParameter(element.attributeValue("bind-parameter"));
		select.setLabel(element.attributeValue("label"));
		select.setType(element.attributeValue("type"));
		select.setTitleValue(StrUtil.nullToDefault(element.attributeValue("title-value"),StrUtil.EMPTY));
		select.setLabelClass(StrUtil.nullToDefault(element.attributeValue("label-class"),StrUtil.EMPTY));
		select.setInputClass(StrUtil.nullToDefault(element.attributeValue("input-class"),StrUtil.EMPTY));
		select.setLabelPosition(LabelPosition.valueOf(element.attributeValue("label-position")));
		String useDataset=element.attributeValue("use-dataset");
		if(StringUtils.isNotBlank(useDataset)){
			select.setUseDataset(Boolean.valueOf(useDataset));
			select.setDataset(element.attributeValue("dataset"));
			select.setLabelField(element.attributeValue("label-field"));
			select.setValueField(element.attributeValue("value-field"));
		}
		String useDict = element.attributeValue("use-dict");
		if(StringUtils.isNotBlank(useDict)){
			select.setUseDict(Boolean.valueOf(useDict));
			select.setDictType(element.attributeValue("dict-type"));
			//获取系统字典
			Map<String,String> dict = null;
			Collection<ReportProvider> providers= SpringUtil.getBeansOfType(ReportProvider.class).values();
			for(ReportProvider provider:providers){
				if(provider.disabled() || provider.getName()==null){
					continue;
				}
				dict = provider.getTypeDict(select.getDictType());
				if(ToolUtils.isNotEmpty(dict)){
					break;
				}
			}
			select.setDicts(dict);
		}
		String multiple = element.attributeValue("multiple");
		if(StringUtils.isNotBlank(multiple)){
			select.setMultiple(Boolean.valueOf(multiple));
		}else{
			select.setMultiple(false);
		}
		List<Option> options=new ArrayList<Option>();
		for(Object obj:element.elements()){
			if(obj==null || !(obj instanceof Element)){
				continue;
			}
			Element ele=(Element)obj;
			if(!ele.getName().equals("option")){
				continue;
			}
			Option option=new Option();
			options.add(option);
			option.setLabel(ele.attributeValue("label"));
			option.setValue(ele.attributeValue("value"));
		}
		select.setOptions(options);

		for(Object obj:element.elements()){
			if(obj==null || !(obj instanceof Element)){
				continue;
			}
			Element ele=(Element)obj;
			if(!ele.getName().equals("js-data")){
				continue;
			}
			String jsFun =  ele.getText().trim();
			select.setJsFun(jsFun);
		}

		return select;
	}
	@Override
	public boolean support(String name) {
		return name.equals("input-select");
	}
}
