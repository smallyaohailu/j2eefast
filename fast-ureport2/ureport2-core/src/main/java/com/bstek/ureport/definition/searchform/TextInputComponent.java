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
 * @since 2017年10月23日
 */
public class TextInputComponent extends InputComponent {
	@Override
	String inputHtml(RenderContext context) {
		String name=getBindParameter();
		Object pvalue=context.getParameter(name)==null ? "" : context.getParameter(name);
//		return "<input type='text' value=\""+pvalue+"\" style=\"padding:3px;height:28px\" id='"+context.buildComponentId(this)+"' name='"+getBindParameter()+"' class='form-control'>";
		return "<input type='text' value=\""+pvalue+"\" id='"+context.buildComponentId(this)+"' name='"+getBindParameter()+"' class='form-control'>";
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
		sb.append("alert('文本框未绑定查询参数名，不能进行查询操作!');");
		sb.append("throw '文本框未绑定查询参数名，不能进行查询操作!'");
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
}
