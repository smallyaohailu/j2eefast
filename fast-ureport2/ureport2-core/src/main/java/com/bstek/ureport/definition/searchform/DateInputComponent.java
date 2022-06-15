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
public class DateInputComponent extends InputComponent {
	//日期格式
	private String format;
	private String dateType;
	public void setFormat(String format) {
		this.format = format;
	}
	public String getFormat() {
		return format;
	}
	@Override
	public String initJs(RenderContext context) {
		StringBuffer sb=new StringBuffer();
//		sb.append("$('#"+context.buildComponentId(this)+"').datetimepicker({");
//		sb.append("format:'"+this.format+"',");
//		sb.append("autoclose:1");
////		sb.append("});");
//		sb.append("$(function(){var time=$(\"#"+context.buildComponentId(this)+"\");var type=time.attr(\"data-type\")||\"date\";" +
//				"var format=time.attr(\"data-format\")||\"yyyy-MM-dd\";" +
//				"var buttons=time.attr(\"data-btn\")||\"clear|now|confirm\",newBtnArr=[];" +
//				"if(buttons){if(buttons.indexOf(\"|\")>0){var btnArr=buttons.split(\"|\"),btnLen=btnArr.length;" +
//				"for(var j=0;j<btnLen;j++){if(\"clear\"===btnArr[j]||\"now\"===btnArr[j]||\"confirm\"===btnArr[j])" +
//				"{newBtnArr.push(btnArr[j])}}}else{if(\"clear\"===buttons||\"now\"===buttons||\"confirm\"===buttons)" +
//				"{newBtnArr.push(buttons)}}}else{newBtnArr=[\"clear\",\"now\",\"confirm\"]}laydate.render({elem:\"#"+context.buildComponentId(this)+"\"," +
//				"trigger:\"click\",type:type,format:format,btns:newBtnArr,done:function(value,data){time.val(value)}})});");
		String name=getBindParameter();
		if(ToolUtils.isEmpty(this.getJsFun())){
			sb.append("function "+(context.buildComponentId(this)+"_val_")+"(vl){return vl};");
		}else{
			sb.append(ToolUtils.cleanCommons(StrUtil.replace(this.getJsFun(),"validate",context.buildComponentId(this)+"_val_"))).append(";");
		}
		sb.append("formElements.push(");
		sb.append("function(){");
		sb.append("if(''==='"+name+"'){");
		sb.append("alert('日期输入框未绑定查询参数名，不能进行查询操作!');");
		sb.append("throw '日期输入框未绑定查询参数名，不能进行查询操作!'");
		sb.append("}");
		sb.append(";var vl = $('#"+context.buildComponentId(this)+"').val();");
		sb.append("vl = "+context.buildComponentId(this)+"_val_(vl);");
		sb.append("return {");
		sb.append("\""+name+"\":");		
		sb.append("vl");
		sb.append("}");
		sb.append("}");
		sb.append(");");
		return sb.toString();
	}
	
	@Override
	public String inputHtml(RenderContext context) {
		String name=getBindParameter();
		Object value=context.getParameter(name)==null ? "" : context.getParameter(name);
		StringBuffer sb=new StringBuffer();
		sb.append("<div class='input-group'>");
		sb.append("<input id='"+context.buildComponentId(this)+"'type='text' data-type=\""+this.dateType+"\" data-format=\""+this.format+"\" name='"+name+"' value=\""+value+"\" class='form-control time-input'>");
		sb.append("<span class='input-group-addon' style=\"font-size:12px\"><span class='glyphicon glyphicon-calendar'></span></span>");
		sb.append("</div>");
		return sb.toString();
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}
}
