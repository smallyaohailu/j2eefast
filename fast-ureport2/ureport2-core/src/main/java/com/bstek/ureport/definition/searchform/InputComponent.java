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

/**
 * @author Jacky.gao
 * @since 2016年1月11日
 */
public abstract class InputComponent implements Component{
	private String label;
	private String bindParameter;
	private String type;
	/**
	 * 标题样式
	 */
	private String labelClass;

	private String titleValue;

	/**
	 * 元素父样式
	 */
	private String inputClass;

	/**
	 * 增强JS
	 */
	private String jsFun;

	protected LabelPosition labelPosition=LabelPosition.top;
	
	abstract String inputHtml(RenderContext context);
	@Override
	public String toHtml(RenderContext context) {
		StringBuffer sb=new StringBuffer();
//		if(this.labelPosition.equals(LabelPosition.top)){
//			sb.append("<div>");
//		}else{
//			sb.append("<div class='form-horizontal'>");
//		}
//		sb.append("<div class='form-group' style='margin:0px 0px 10px 0px'>");
		if(ToolUtils.isEmpty(labelClass)){
			labelClass = "";
		}
		if(ToolUtils.isEmpty(inputClass)){
			inputClass = "";
		}
		sb.append("<div class='form-group'>");
		String titleTip = StrUtil.EMPTY;
		String icon = StrUtil.EMPTY;
		if(ToolUtils.isNotEmpty(titleValue)){
			titleTip = " title='"+titleValue+"'";
			icon = " <i class=\"fa fa-question-circle-o\" style=\"color: orange\"></i>";
		}
		if(this.labelPosition.equals(LabelPosition.top)){
			sb.append("<span class='"+labelClass+"'"+titleTip+">"+this.label+icon+"</span>");
//			sb.append("<span style='font-size:13px'>"+this.label+"</span>");
			sb.append(inputHtml(context));
		}else{					
//			sb.append("<span class='col-md-3' style='text-align:right;padding-right:1px;font-size:13px'>"+this.label+"</span>");
//			sb.append("<div class='col-md-9' style='padding-left:1px;'>");

			sb.append("<span class='control-label col-md-4 "+labelClass+"'"+titleTip+">"+this.label+icon+"</span>");
			sb.append("<div class='col-md-8 '"+inputClass+">");
			sb.append(inputHtml(context));
			sb.append("</div>");
		}
//		sb.append("</div>");
		sb.append("</div>");
		return sb.toString();
	}

	public String getTitleValue() {
		return titleValue;
	}

	public void setTitleValue(String titleValue) {
		this.titleValue = titleValue;
	}

	public String getLabelClass() {
		return labelClass;
	}

	public void setLabelClass(String labelClass) {
		this.labelClass = labelClass;
	}

	public String getInputClass() {
		return inputClass;
	}

	public void setInputClass(String inputClass) {
		this.inputClass = inputClass;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	public String getLabel() {
		return label;
	}
	public void setLabelPosition(LabelPosition labelPosition) {
		this.labelPosition = labelPosition;
	}
	public LabelPosition getLabelPosition() {
		return labelPosition;
	}
	public String getBindParameter() {
		return bindParameter;
	}
	public void setBindParameter(String bindParameter) {
		this.bindParameter = bindParameter;
	}
	@Override
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getJsFun() {
		return jsFun;
	}

	public void setJsFun(String jsFun) {
		this.jsFun = jsFun;
	}
}
