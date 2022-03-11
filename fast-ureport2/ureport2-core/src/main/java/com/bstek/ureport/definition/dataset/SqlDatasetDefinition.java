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
package com.bstek.ureport.definition.dataset;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.bstek.ureport.utils.ToolUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.build.Dataset;
import com.bstek.ureport.definition.datasource.DataType;
import com.bstek.ureport.expression.ExpressionUtils;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.utils.ProcedureUtils;


/**
 * @author Jacky.gao
 * @since 2016年12月27日
 */
public class SqlDatasetDefinition implements DatasetDefinition {
	private static final long serialVersionUID = -1134526105416805870L;
	//数据集名称
	private String name;
	private String sql;
	//是否分页
	private boolean isPage = true;
	//分页大小
	private int pageSize = 50;
	private List<Parameter> parameters;
	private List<Field> fields;
	private Expression sqlExpression;
	public Dataset buildDataset(Map<String,Object> parameterMap,Connection conn){
		String sqlForUse=sql;
		Context context=new Context(null,parameterMap);
		if(sqlExpression!=null){
			sqlForUse=executeSqlExpr(sqlExpression, context);
		}else{
			Pattern pattern=Pattern.compile("\\$\\{.*?\\}");
			Matcher matcher=pattern.matcher(sqlForUse);
			while(matcher.find()){
				String substr=matcher.group();
				String sqlExpr=substr.substring(2,substr.length()-1);
				Expression expr=ExpressionUtils.parseExpression(sqlExpr);
				String result=executeSqlExpr(expr, context);
				sqlForUse=sqlForUse.replace(substr, result);
			}
		}
		//不分页
		Map<String, Object> pmap = buildParameters(parameterMap);
		// 自动添加分页
		if(isPage &&
				ToolUtils.isNotEmpty(parameterMap.get("__page"))
				&& !ProcedureUtils.isProcedure(sqlForUse)){
			// 分页
			// 1.查询总数
			String countSql = ToolUtils.autoCountSql(sqlForUse);
			Utils.logToConsole("FAST COUNT SQL:"+countSql);
			SingleConnectionDataSource datasource=new SingleConnectionDataSource(conn,false);
			NamedParameterJdbcTemplate jdbcTemplate=new NamedParameterJdbcTemplate(datasource);
			int count  = jdbcTemplate.queryForObject(countSql,pmap,Integer.class);
			Utils.logToConsole("FAST COUNT:"+count);
			// 2.计算分页查询
			// 当前页
			int page = (int) parameterMap.get("__page");
			//一共多少页
			long totalPage = (long) Math.ceil((double) count / pageSize);
			int start = 0;
			int end = pageSize;
			String LIMT = " LIMIT";
			if(page <= 1){
				end = pageSize > count? count: pageSize;
				LIMT += " "+end;
			}else {
				// 3  10 - 15
				start = pageSize * (page -1);
				LIMT += " "+start +","+end;
			}
			sqlForUse += LIMT;
			//4. 查询分页结果集
			Utils.logToConsole("FAST RUNTIME SQL:"+sqlForUse);
			List<Map<String,Object>> list= jdbcTemplate.queryForList(sqlForUse, pmap);
			parameterMap.put("__totalPage",totalPage);
//			parameterMap.put("__COUNT"+name,count);
//			parameterMap.put("__COUNT",count);
			parameterMap.put("__paging",true);
//			parameterMap.put("__pageSize",pageSize);
			return new Dataset(name,list,count,totalPage,pageSize);
		}else{
			if(ProcedureUtils.isProcedure(sqlForUse)){
				List<Map<String,Object>> result = ProcedureUtils.procedureQuery(sqlForUse,pmap,conn);
				return new Dataset(name,result);
			}
			SingleConnectionDataSource datasource=new SingleConnectionDataSource(conn,false);
			NamedParameterJdbcTemplate jdbcTemplate=new NamedParameterJdbcTemplate(datasource);
			List<Map<String,Object>> list= jdbcTemplate.queryForList(sqlForUse, pmap);
			return new Dataset(name,list);
		}
	}
	
	private String executeSqlExpr(Expression sqlExpr,Context context){
		String sqlForUse=null;
		ExpressionData<?> exprData=sqlExpr.execute(null, null, context);
		if(exprData instanceof ObjectExpressionData){
			ObjectExpressionData data=(ObjectExpressionData)exprData;
			Object obj=data.getData();
			if(obj!=null){
				String s=obj.toString();
				s=s.replaceAll("\\\\", "");
				sqlForUse=s;
			}
		}
		return sqlForUse;
	}

//	public Dataset buildDatasetCount(Map<String,Object> parameterMap, Connection conn){
//		String sqlForUse=sql;
//		Context context=new Context(null,parameterMap);
//		if(sqlExpression!=null){
//			sqlForUse=executeSqlExpr(sqlExpression, context);
//		}else{
//			Pattern pattern= Pattern.compile("\\$\\{.*?\\}");
//			Matcher matcher=pattern.matcher(sqlForUse);
//			while(matcher.find()){
//				String substr=matcher.group();
//				String sqlExpr=substr.substring(2,substr.length()-1);
//				Expression expr= ExpressionUtils.parseExpression(sqlExpr);
//				String result=executeSqlExpr(expr, context);
//				sqlForUse=sqlForUse.replace(substr, result);
//			}
//		}
//		Map<String, Object> pmap = buildParameters(parameterMap);
//		if(ProcedureUtils.isProcedure(sqlForUse)){
//			List<Map<String,Object>> result = ProcedureUtils.procedureQuery(sqlForUse,pmap,conn);
//			return new Dataset(name,result);
//		}
//		SingleConnectionDataSource datasource=new SingleConnectionDataSource(conn,false);
//		NamedParameterJdbcTemplate jdbcTemplate=new NamedParameterJdbcTemplate(datasource);
//		String countSql = ToolUtils.autoCountSql(sqlForUse);
//		List<Integer> counts = new ArrayList<>();
//		int count  = jdbcTemplate.queryForObject(countSql,pmap,Integer.class);
//		Utils.logToConsole("FAST COUNT SQL:"+countSql);
//		Utils.logToConsole("FAST COUNT:"+count);
//		counts.add(count);
//		parameterMap.put("__COUNT"+name,count);
//		parameterMap.put("__COUNT",count);
//		return new Dataset("__COUNT_"+name,counts);
//	}

	public String getSqlKey(Map<String,Object> parameterMap){
		String sqlForUse=sql;
		Context context=new Context(null,parameterMap);
		if(sqlExpression!=null){
			sqlForUse=executeSqlExpr(sqlExpression, context);
		}else{
			Pattern pattern= Pattern.compile("\\$\\{.*?\\}");
			Matcher matcher=pattern.matcher(sqlForUse);
			while(matcher.find()){
				String substr=matcher.group();
				String sqlExpr=substr.substring(2,substr.length()-1);
				Expression expr= ExpressionUtils.parseExpression(sqlExpr);
				String result=executeSqlExpr(expr, context);
				sqlForUse=sqlForUse.replace(substr, result);
			}
		}
		return ToolUtils.hash(sqlForUse);
	}

	
	private Map<String,Object> buildParameters(Map<String,Object> params){
		Map<String,Object> map=new HashMap<String,Object>();
		for(Parameter param:parameters){
			String name=param.getName();
			DataType datatype=param.getType();
			Object value=param.getDefaultValue();
			if(params!=null && params.containsKey(name)){
				value=params.get(name);
			}
			map.put(name, datatype.parse(value));
		}
		return map;
	}
	
	@Override
	public List<Field> getFields() {
		return fields;
	}
	
	public void setSqlExpression(Expression sqlExpression) {
		this.sqlExpression = sqlExpression;
	}
	
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public List<Parameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getSql() {
		return sql;
	}

	public boolean isPage() {
		return isPage;
	}

	public void setPage(boolean page) {
		isPage = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
