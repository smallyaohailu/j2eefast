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
package com.bstek.ureport.definition.searchform;

import cn.hutool.core.util.StrUtil;
import com.bstek.ureport.utils.ToolUtils;

import java.util.List;

/**
 * @author Jacky.gao
 * @since 2017年10月23日
 */
public class SearchForm {
	private List<Component> components;
	/**
	 * 初始化CSS
	 */
	private String loadCss;
	/**
	 * 初始化JS
	 */
	private String loadJs;

	/**
	 * 表单标题提示
	 */
	private String fromTitle;

	private FormPosition formPosition;
	public String toHtml(RenderContext context){

		StringBuilder sb=new StringBuilder();

		if(ToolUtils.isNotEmpty(loadCss) &&
				ToolUtils.isNotEmpty(ToolUtils.cleanCommons(loadCss))){
			sb.append("<style>");
			sb.append(ToolUtils.cleanCommons(loadCss));
			sb.append("</style>");
		}

		if(ToolUtils.isNotEmpty(loadJs) &&
				ToolUtils.isNotEmpty(ToolUtils.cleanCommons(loadJs))){
			sb.append("<script>");
			sb.append(ToolUtils.cleanCommons(loadJs).replaceAll("initForm","__INITFORM__"));
			sb.append("</script>");
		}


		if(!ToolUtils.isEmpty(components)){
			sb.append("<div class=\"from-search\"><form class=\"form-horizontal\"><h4 class=\"form-header h4\">"+ StrUtil.nullToDefault(fromTitle,"")+"</h4><div class=\"form-body\">");
			for(Component component:components){
				sb.append(component.toHtml(context));
			}
			sb.append("</form></div></div>");
		}
		return sb.toString();
	}
	public String toJs(RenderContext context){
		StringBuilder sb=new StringBuilder();
		for(Component component:components){
			sb.append(component.initJs(context));
		}
		return sb.toString();
	}

	public String getLoadCss() {
		return loadCss;
	}

	public void setLoadCss(String loadCss) {
		this.loadCss = loadCss;
	}

	public String getLoadJs() {
		return loadJs;
	}

	public void setLoadJs(String loadJs) {
		this.loadJs = loadJs;
	}

	public String getFromTitle() {
		return fromTitle;
	}

	public void setFromTitle(String fromTitle) {
		this.fromTitle = fromTitle;
	}

	public List<Component> getComponents() {
		return components;
	}
	public void setComponents(List<Component> components) {
		this.components = components;
	}
	public FormPosition getFormPosition() {
		return formPosition;
	}
	public void setFormPosition(FormPosition formPosition) {
		this.formPosition = formPosition;
	}
}
