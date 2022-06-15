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
import com.bstek.ureport.definition.searchform.DateInputComponent;
import com.bstek.ureport.definition.searchform.LabelPosition;
import org.dom4j.Element;

/**
 * @author Jacky.gao
 * @since 2017年10月24日
 */
public class DatetimeInputParser implements FormParser<DateInputComponent> {
	@Override
	public DateInputComponent parse(Element element) {
		DateInputComponent component=new DateInputComponent();
		component.setBindParameter(element.attributeValue("bind-parameter"));
		component.setLabel(element.attributeValue("label"));
		component.setType(element.attributeValue("type"));
		component.setTitleValue(StrUtil.nullToDefault(element.attributeValue("title-value"),StrUtil.EMPTY));
		component.setLabelClass(StrUtil.nullToDefault(element.attributeValue("label-class"),StrUtil.EMPTY));
		component.setInputClass(StrUtil.nullToDefault(element.attributeValue("input-class"),StrUtil.EMPTY));
		component.setLabelPosition(LabelPosition.valueOf(element.attributeValue("label-position")));
		component.setFormat(element.attributeValue("format"));
		String dateType = "date";
		if("yyyy-MM-dd HH:mm:ss".equals(component.getFormat())){
			dateType = "datetime";
		}else if("yyyy".equals(component.getFormat())){
			dateType = "year";
		}else if("yyyyMM".equals(component.getFormat())){ //
			dateType = "month";
		}else if("yyyy-MM".equals(component.getFormat())){ //
			dateType = "month";
		}else if("HH:mm:ss".equals(component.getFormat())){
			dateType = "time";
		}
		component.setDateType(dateType);

		for(Object obj:element.elements()){
			if(obj==null || !(obj instanceof Element)){
				continue;
			}
			Element ele=(Element)obj;
			if(ele.getName().equals("js-data")){
				String jsFun =  ele.getText().trim();
				component.setJsFun(jsFun);
			}
		}

		return component;
	}
	@Override
	public boolean support(String name) {
		return name.equals("input-datetime");
	}
}
