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
import org.dom4j.Element;

import com.bstek.ureport.definition.searchform.LabelPosition;
import com.bstek.ureport.definition.searchform.TextInputComponent;

/**
 * @author Jacky.gao
 * @since 2017年10月24日
 */
public class TextInputParser implements FormParser<TextInputComponent> {
	@Override
	public TextInputComponent parse(Element element) {
		TextInputComponent component=new TextInputComponent();
		component.setBindParameter(element.attributeValue("bind-parameter"));
		component.setLabel(element.attributeValue("label"));
		component.setTitleValue(StrUtil.nullToDefault(element.attributeValue("title-value"),StrUtil.EMPTY));
		component.setLabelClass(StrUtil.nullToDefault(element.attributeValue("label-class"),StrUtil.EMPTY));
		component.setInputClass(StrUtil.nullToDefault(element.attributeValue("input-class"),StrUtil.EMPTY));
		component.setType(element.attributeValue("type"));
		component.setLabelPosition(LabelPosition.valueOf(element.attributeValue("label-position")));

		for(Object obj:element.elements()){
			if(obj==null || !(obj instanceof Element)){
				continue;
			}
			Element ele=(Element)obj;
			if(!ele.getName().equals("js-data")){
				continue;
			}
			String jsFun =  ele.getText().trim();
			component.setJsFun(jsFun);
		}

		return component;
	}
	@Override
	public boolean support(String name) {
		return name.equals("input-text");
	}
}
