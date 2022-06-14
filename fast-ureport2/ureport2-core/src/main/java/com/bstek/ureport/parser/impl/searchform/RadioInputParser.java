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
import com.bstek.ureport.definition.searchform.RadioInputComponent;
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
 * @since 2017年10月24日
 */
public class RadioInputParser implements FormParser<RadioInputComponent> {
	@Override
	public RadioInputComponent parse(Element element) {
		RadioInputComponent radio=new RadioInputComponent();
		radio.setBindParameter(element.attributeValue("bind-parameter"));
		radio.setOptionsInline(Boolean.valueOf(element.attributeValue("options-inline")));
		radio.setLabel(element.attributeValue("label"));
		radio.setType(element.attributeValue("type"));
		radio.setTitleValue(StrUtil.nullToDefault(element.attributeValue("title-value"),StrUtil.EMPTY));
		radio.setLabelClass(StrUtil.nullToDefault(element.attributeValue("label-class"),StrUtil.EMPTY));
		radio.setInputClass(StrUtil.nullToDefault(element.attributeValue("input-class"),StrUtil.EMPTY));

		radio.setLabelPosition(LabelPosition.valueOf(element.attributeValue("label-position")));

		String useDict = element.attributeValue("use-dict");

		if(StringUtils.isNotBlank(useDict)){
			radio.setUseDict(Boolean.valueOf(useDict));
			radio.setDictType(element.attributeValue("dict-type"));
			//获取系统字典
			Map<String,String> dict = null;
			Collection<ReportProvider> providers= SpringUtil.getBeansOfType(ReportProvider.class).values();
			for(ReportProvider provider:providers){
				if(provider.disabled() || provider.getName()==null){
					continue;
				}
				dict = provider.getTypeDict(radio.getDictType());
				if(ToolUtils.isNotEmpty(dict)){
					break;
				}
			}
			radio.setDicts(dict);
		}else{
			radio.setDictType(StrUtil.EMPTY);
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
		radio.setOptions(options);

		for(Object obj:element.elements()){
			if(obj==null || !(obj instanceof Element)){
				continue;
			}
			Element ele=(Element)obj;
			if(!ele.getName().equals("js-data")){
				continue;
			}
			String jsFun =  ele.getText().trim();
			radio.setJsFun(jsFun);
		}

		return radio;
	}
	@Override
	public boolean support(String name) {
		return name.equals("input-radio");
	}
}
