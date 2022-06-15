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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jacky.gao
 * @since 2017年10月23日
 */
public class RadioInputComponent extends InputComponent {
	/**
	 * 是否为系统字典
	 */
	private boolean useDict;

	private Map<String,String> dicts = new HashMap<>();
	/**
	 * 字典值
	 */
	private String dictType;

	private boolean optionsInline;

	private List<Option> options;
	@Override
	String inputHtml(RenderContext context) {
		StringBuilder sb=new StringBuilder();
		String name=getBindParameter();
		Object pvalue=context.getParameter(name)==null ? "" : context.getParameter(name);
		String[] data=pvalue.toString().split(",");
		List<String> list=Arrays.asList(data);
		//sb.append("<div>");

		if(useDict && dicts.size() != 0){
			for (Map.Entry<String, String> entry : dicts.entrySet()) {
				String value = entry.getKey();
				String label = entry.getValue();
				String checked=list.contains(value) ? "checked" : "";
				sb.append("<label class='radio-box'><input value='"+value+"' "+checked+" type='radio' name='"+name+"'>"+label+"</label>");
			}
		}else{
			for(Option option:options){
				String value=option.getValue();
				String label=option.getLabel();
				String checked=list.contains(value) ? "checked" : "";
				if(this.optionsInline){
//				sb.append("<span class='checkbox-inline' style='padding-top:0px;padding-left:2px;padding-top:0px'><input value='"+value+"' "+checked+" type='radio' name='"+name+"'> "+label+"</span>");
					sb.append("<label class='radio-box'><input value='"+value+"' "+checked+" type='radio' name='"+name+"'/>"+label+"</label>");
				}else{
					sb.append("<span class='checkbox'><label class='radio-box'><input value='"+value+"' type='radio' "+checked+" name='"+name+"' style='margin-left: auto'/> <span style=\"margin-left:5px\"/>"+label+"</span></label></span>");
				}
			}
		}

		//sb.append("</div>");
		return sb.toString();
	}

	@Override
	public String initJs(RenderContext context) {
		String name=getBindParameter();
		StringBuilder sb=new StringBuilder();
		if(ToolUtils.isEmpty(this.getJsFun())){
			sb.append("function "+(context.buildComponentId(this)+"_val_")+"(vl){return vl};");
		}else{
			sb.append(ToolUtils.cleanCommons(StrUtil.replace(this.getJsFun(),"validate",context.buildComponentId(this)+"_val_"))).append(";");
		}
		sb.append("formElements.push(");
		sb.append("function(){");
		sb.append("if(''==='"+name+"'){");
		sb.append("alert('单选框未绑定查询参数名，不能进行查询操作!');");
		sb.append("throw '单选框未绑定查询参数名，不能进行查询操作!'");
		sb.append("}");

		sb.append(";var vl = ");
		sb.append("$(\"input[name='"+getBindParameter()+"']:checked\").val();");
		sb.append("vl = "+context.buildComponentId(this)+"_val_(vl);");
		sb.append("return {");
		sb.append("\""+name+"\":");
		sb.append("vl");
		sb.append("}");
		sb.append("}");
		sb.append(");");
//		sb.append("return {");
//		sb.append("\""+name+"\":");
//		sb.append("$(\"input[name='"+getBindParameter()+"']:checked\").val()");
//		sb.append("}");
//		sb.append("}");
//		sb.append(");");
		return sb.toString();
	}

	public boolean isUseDict() {
		return useDict;
	}

	public void setUseDict(boolean useDict) {
		this.useDict = useDict;
	}

	public Map<String, String> getDicts() {
		return dicts;
	}

	public void setDicts(Map<String, String> dicts) {
		this.dicts = dicts;
	}

	public String getDictType() {
		return dictType;
	}

	public void setDictType(String dictType) {
		this.dictType = dictType;
	}

	public void setOptionsInline(boolean optionsInline) {
		this.optionsInline = optionsInline;
	}
	public boolean isOptionsInline() {
		return optionsInline;
	}
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	public List<Option> getOptions() {
		return options;
	}
}
