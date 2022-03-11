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
package com.j2eefast.common.core.ureport;

import com.bstek.ureport.Utils;
import com.bstek.ureport.build.Context;
import com.bstek.ureport.console.RenderPageServletAction;
import com.bstek.ureport.console.designer.DataResult;
import com.bstek.ureport.console.exception.ReportDesignException;
import com.bstek.ureport.definition.dataset.Field;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import com.bstek.ureport.definition.datasource.DataType;
import com.bstek.ureport.expression.ExpressionUtils;
import com.bstek.ureport.expression.model.Expression;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.utils.ProcedureUtils;
import com.j2eefast.common.core.utils.SpringUtil;

import cn.hutool.db.meta.MetaUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.JdbcUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 修改支持数据库动态拔插
 * @author Jacky.gao
 * @since 2017年2月6日
 * @author ZhouZhou
 * @date 2021-09-16
 */
@Slf4j
public class FastDatasourceServletAction extends RenderPageServletAction {

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method=retriveMethod(req);
        if(method!=null){
            invokeMethod(method, req, resp);
        }
    }

    //J2eeFAST 修改实时获取数据源情况
    public static Collection<BuildinDatasource> getBuildinDatasources(){
        Collection<BuildinDatasource> buildinDatasources = new ArrayList<>(5);
        buildinDatasources.addAll(SpringUtil.getBeansOfType(BuildinDatasource.class).values());
        return buildinDatasources;
    }

    public void loadBuildinDatasources(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> datasources=new ArrayList<String>();
        // 修改获取实时数据
        for(BuildinDatasource datasource: getBuildinDatasources()){
            datasources.add(datasource.name());
        }
        writeObjectToJson(resp, datasources);
    }

    public void loadMethods(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String beanId=req.getParameter("beanId");
        Object obj=applicationContext.getBean(beanId);
        Class<?> clazz=obj.getClass();
        Method[] methods=clazz.getMethods();
        List<String> result=new ArrayList<String>();
        for(Method method:methods){
            Class<?>[] types=method.getParameterTypes();
            if(types.length!=3){
                continue;
            }
            Class<?> typeClass1=types[0];
            Class<?> typeClass2=types[1];
            Class<?> typeClass3=types[2];
            if(!String.class.isAssignableFrom(typeClass1)){
                continue;
            }
            if(!String.class.isAssignableFrom(typeClass2)){
                continue;
            }
            if(!Map.class.isAssignableFrom(typeClass3)){
                continue;
            }
            result.add(method.getName());
        }
        writeObjectToJson(resp, result);
    }

    public void buildClass(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String clazz=req.getParameter("clazz");
        List<Field> result=new ArrayList<Field>();
        try{
            Class<?> targetClass=Class.forName(clazz);
            PropertyDescriptor[] propertyDescriptors= PropertyUtils.getPropertyDescriptors(targetClass);
            for(PropertyDescriptor pd:propertyDescriptors){
                String name=pd.getName();
                if("class".equals(name)){
                    continue;
                }
                result.add(new Field(name));
            }
            writeObjectToJson(resp, result);
        }catch(Exception ex){
            throw new ReportDesignException(ex);
        }
    }

    public void buildDatabaseTables(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn=null;
        ResultSet rs = null;
        try{
            conn=buildConnection(req);
            DatabaseMetaData metaData = conn.getMetaData();
            String url = metaData.getURL();
            String schema = null;
            if (url.toLowerCase().contains("oracle")) {
                schema = metaData.getUserName();
            }
            List<Map<String,String>> tables = new ArrayList<>();
            rs = metaData.getTables(conn.getCatalog(), schema, "%", new String[] { "TABLE","VIEW" });
            while (rs.next()) {
                Map<String,String> table = new HashMap<String,String>();
                table.put("name",rs.getString("TABLE_NAME"));
                table.put("type",rs.getString("TABLE_TYPE"));
                tables.add(table);
            }
            writeObjectToJson(resp, tables);
        }catch(Exception ex){
            throw new ServletException(ex);
        }finally{
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeConnection(conn);
        }
    }

    /**
     * 页面确定按钮执行解析语句获取查询字段
     * @author ZhouZhou
     * @date 2021-09-29
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void buildFields(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sql=req.getParameter("sql");
        ///isPage
        String isPage =req.getParameter("isPage");

        if(ToolUtil.isNotEmpty(isPage) && isPage.equals("true")){
            String pagesize = req.getParameter("pagesize");
            if(ToolUtil.isEmpty(pagesize)){
                throw new ReportDesignException(new Exception("分页大小设置不能为空!"));
            }
        }
        String parameters=req.getParameter("parameters");
        Connection conn=null;
        final List<Field> fields=new ArrayList<Field>();
        try{
            conn=buildConnection(req);
            Map<String, Object> map = buildParameters(parameters);
            sql=parseSql(sql, map);
            log.info("UP-SQL:{}",sql);
            if(ProcedureUtils.isProcedure(sql)){
                List<Field> fieldsList = ProcedureUtils.procedureColumnsQuery(sql, map, conn);
                fields.addAll(fieldsList);
            }else{

                DataSource dataSource=new SingleConnectionDataSource(conn,false);
                NamedParameterJdbcTemplate jdbc=new NamedParameterJdbcTemplate(dataSource);
                PreparedStatementCreator statementCreator=getPreparedStatementCreator(sql,new MapSqlParameterSource(map));
                jdbc.getJdbcOperations().execute(statementCreator, new PreparedStatementCallback<Object>() {
                    @Override
                    public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                        ResultSet rs = null;
                        try {
                            rs = ps.executeQuery();
                            ResultSetMetaData metadata=rs.getMetaData();
                            int columnCount=metadata.getColumnCount();
                            for(int i=0;i<columnCount;i++){
                                String columnName=metadata.getColumnLabel(i+1);
                                fields.add(new Field(columnName));
                            }
                            return null;
                        }finally {
                            JdbcUtils.closeResultSet(rs);
                        }
                    }
                });
            }
            writeObjectToJson(resp, fields);
        }catch(Exception ex){
            throw new ReportDesignException(ex);
        }finally{
            JdbcUtils.closeConnection(conn);
        }
    }

    protected PreparedStatementCreator getPreparedStatementCreator(String sql, SqlParameterSource paramSource) {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
        List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);
        return pscf.newPreparedStatementCreator(params);
    }

    /**
     * 预览数据
     * @author ZhouZhou
     * @date 2021-09-29
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void previewData(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sql=req.getParameter("sql");
        //查询参数
        String parameters=req.getParameter("parameters");
        Map<String, Object> map = buildParameters(parameters);
        sql=parseSql(sql, map);
        log.info("UP-SQL:{}",sql);
        Connection conn=null;
        try{
            conn=buildConnection(req);
            List<Map<String,Object>> list=null;
            //判断是否为存储过程
            if(ProcedureUtils.isProcedure(sql)){
                list=ProcedureUtils.procedureQuery(sql, map, conn);
            }else{
                // 只取前50条数据,防止数据过大导致过慢问题  PaginationInnerInterceptor 后期优化支持多数据库分页语句 目前只支持mysql
                StringBuilder limitSql = new StringBuilder(sql).append(" LIMIT ").append("50");
                DataSource dataSource=new SingleConnectionDataSource(conn,false);
                NamedParameterJdbcTemplate jdbc=new NamedParameterJdbcTemplate(dataSource);
                list=jdbc.queryForList(limitSql.toString(), map);
            }
            int size=list.size();
            int currentTotal=size;
            if(currentTotal>500){
                currentTotal=500;
            }
            List<Map<String,Object>> ls=new ArrayList<Map<String,Object>>();
            for(int i=0;i<currentTotal;i++){
                ls.add(list.get(i));
            }
            DataResult result=new DataResult();
            List<String> fields=new ArrayList<String>();
            if(size>0){
                Map<String,Object> item=list.get(0);
                for(String name:item.keySet()){
                    fields.add(name);
                }
            }
            result.setFields(fields);
            result.setCurrentTotal(currentTotal);
            result.setData(ls);
            result.setTotal(size);
            writeObjectToJson(resp, result);
        }catch(Exception ex){
            throw new ServletException(ex);
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String parseSql(String sql,Map<String,Object> parameters){
        sql=sql.trim();
        Context context=new Context(applicationContext, parameters);
        if(sql.startsWith(ExpressionUtils.EXPR_PREFIX) && sql.endsWith(ExpressionUtils.EXPR_SUFFIX)){
            sql=sql.substring(2, sql.length()-1);
            Expression expr=ExpressionUtils.parseExpression(sql);
            sql=executeSqlExpr(expr,context);
            return sql;
        }else{
            String sqlForUse=sql;
            Pattern pattern= Pattern.compile("\\$\\{.*?\\}");
            Matcher matcher=pattern.matcher(sqlForUse);
            while(matcher.find()){
                String substr=matcher.group();
                String sqlExpr=substr.substring(2,substr.length()-1);
                Expression expr=ExpressionUtils.parseExpression(sqlExpr);
                String result=executeSqlExpr(expr, context);
                sqlForUse=sqlForUse.replace(substr, result);
            }
            Utils.logToConsole("DESIGN SQL:"+sqlForUse);
            return sqlForUse;
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

    public void testConnection(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username=req.getParameter("username");
        String password=req.getParameter("password");
        String driver=req.getParameter("driver");
        String url=req.getParameter("url");
        Connection conn=null;
        Map<String,Object> map=new HashMap<String,Object>();
        try{
            Class.forName(driver);
            conn=DriverManager.getConnection(url, username, password);
            map.put("result", true);
        }catch(Exception ex){
            map.put("error", ex.toString());
            map.put("result", false);
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        writeObjectToJson(resp, map);
    }

    /**
     * 获取查询参数
     * @author ZhouZhou
     * @date 2021-09-27
     * @param parameters
     * @return
     * @throws IOException
     * @throws JsonParseException
     * @throws JsonMappingException
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> buildParameters(String parameters) throws IOException, JsonParseException, JsonMappingException {
        Map<String,Object> map=new HashMap<String,Object>();
        if(StringUtils.isBlank(parameters)){
            return map;
        }
        ObjectMapper mapper=new ObjectMapper();
        List<Map<String,Object>> list=mapper.readValue(parameters, ArrayList.class);
        for(Map<String,Object> param:list){
            String name=param.get("name").toString();
            if(param.get("type").toString().equals("数据权限")){
                map.put(name, "");
            }else{
                DataType type=DataType.valueOf(param.get("type").toString());
                String defaultValue=(String)param.get("defaultValue");
                if(defaultValue==null || defaultValue.equals("")){
                    switch(type){
                        case Boolean:
                            map.put(name, false);
                        case Date:
                            map.put(name, new Date());
                        case Float:
                            map.put(name, new Float(0));
                        case Integer:
                            map.put(name, 0);
                        case String:
                            if(defaultValue!=null && defaultValue.equals("")){
                                map.put(name, "");
                            }else{
                                map.put(name, "null");
                            }
                            break;
                        case List:
                            map.put(name, new ArrayList<Object>());
                    }
                }else{
                    map.put(name, type.parse(defaultValue));
                }
            }
        }
        return map;
    }

    //获取数据库连接会话
    private Connection buildConnection(HttpServletRequest req) throws Exception{
        String type=req.getParameter("type");
        if(type.equals("jdbc")){
            String username=req.getParameter("username");
            String password=req.getParameter("password");
            String driver=req.getParameter("driver");
            String url=req.getParameter("url");

            Class.forName(driver);
            Connection conn=DriverManager.getConnection(url, username, password);
            return conn;
        }else{
            String name=req.getParameter("name");
            Connection conn=Utils.getBuildinConnection(name);
            if(conn==null){
                throw new ReportDesignException("Buildin datasource ["+name+"] not exist.");
            }
            return conn;
        }
    }

    @Override
    public String url() {
        return "/datasource";
    }
}
