/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.j2eefast.common.core.base.entity.LoginUserEntity;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.i18n.MessageSource;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.utils.CookieUtil;
import com.j2eefast.common.core.utils.Global;
import com.j2eefast.common.core.utils.SpringUtil;
import com.j2eefast.common.core.utils.ToolUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * 系统常量参数配置\前端国际化文本设置
 * 前端get JSON 
 * fast/static/config.js?_p=ajax
 * 
 * @author huanzhou
 */
@Slf4j
public class ResourceLoaderServlet extends HttpServlet {

	//__ajax=json
	private static final long serialVersionUID = 1L;
	private static String I18N = "/i18n";
	private static String PROPERTIES = ".properties";
	private static String LANG = "lang";
	private static String AJAX = "json";
	
	@Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String __ajax = req.getParameter("__ajax");
		String url = req.getServletPath();
		byte[] outBytes = StrUtil.EMPTY.getBytes();
		// 前端JS国际化文件请求
		if(url.equals(I18N)){
			String pathInfo = req.getPathInfo();
			if(StrUtil.endWith(pathInfo,PROPERTIES)){
				pathInfo = StrUtil.replaceIgnoreCase(pathInfo,".properties",StrUtil.EMPTY);
				pathInfo = StrUtil.replaceIgnoreCase(pathInfo,"/",StrUtil.EMPTY);
				String locale = StrUtil.replaceIgnoreCase(pathInfo,"i18n_",StrUtil.EMPTY);
				Map<String, String> map = SpringUtil.getBean(MessageSource.class).getLocaleMap(locale);
				StringBuffer sb = new StringBuffer();
				sb.append("#\n" + 
						"# Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.\n" + 
						"# No deletion without permission\n" + 
						"# Author ZhouHuan\n" + 
						"# Date 2021-10-12\n" + 
						"#\n");
				map.forEach((k,v)->{
					sb.append(k).append("=").append(v).append("\n");
				});
				resp.setContentType("application/octet-stream");
				// 转unicode输出
				//outBytes = UnicodeUtil.toUnicode(sb.toString()).getBytes();
				outBytes = sb.toString().getBytes();
				log.debug("压缩前:{}" ,outBytes.length);
				//用GZIPOutputStream压缩
				ByteArrayOutputStream bout=new ByteArrayOutputStream();
				GZIPOutputStream gzip=new GZIPOutputStream(bout);
				gzip.write(outBytes);
				//一般输出都要flush
				gzip.flush();
				gzip.close();
				outBytes = bout.toByteArray();
				log.debug("压缩之后:{}" ,outBytes.length);
				//注明压缩类型
				resp.setHeader("content-encoding", "gzip");
				resp.setHeader("content-length",outBytes.length+"");
				
			}else if(StrUtil.endWith(pathInfo,LANG)) {
				// 不同参数处理
				if(__ajax != null) {
					resp.setContentType("application/json; charset=utf-8");
					resp.setCharacterEncoding("UTF-8");
					JSONObject paramIn = new JSONObject();
					Map<String, String> map = SpringUtil.getBean(MessageSource.class).getLocaleMap(__ajax);
					if(map != null) {
						map.forEach((k,v)->{
							paramIn.set(k, v);
						});
					}
					outBytes = paramIn.toString().getBytes();
				}
			}
		}else{
			// 系统常量参数请求
			String scheme = req.getScheme();
			String serverName = req.getServerName();
			int port = req.getServerPort();
			port = (port == 443 || port == 80) ? 0 : port;
			String path = req.getContextPath();
			String basePath = scheme + "://" + serverName  + (port == 0? "": (":" + port)) + path;
			Subject subject = SecurityUtils.getSubject();
			LoginUserEntity loginUserEntity = (LoginUserEntity) subject
					.getPrincipal();
			String userName = loginUserEntity == null? "":loginUserEntity.getUsername();
			String name = loginUserEntity == null? "":loginUserEntity.getName();
			String userId = loginUserEntity == null? "":Convert.toStr(loginUserEntity.getId());
			// 不同参数处理
			if(__ajax != null) {
				// AJAX返回JSON
				if(AJAX.equals(__ajax)) {
					resp.setContentType("application/json; charset=utf-8");
					resp.setCharacterEncoding("UTF-8");
					JSONObject paramIn = new JSONObject();
					paramIn.set("baseURL", path + "/");
					paramIn.set("ctx", path);
					paramIn.set("_lang", StrUtil.nullToDefault(CookieUtil.getCookie(req,ConfigConstant.LANGUAGE)
							,LocaleContextHolder.getLocale().toString()));
					paramIn.set("_username", userName);
					paramIn.set("__NAME__", name);
					paramIn.set("userId", userId);
					paramIn.set("_secretkey", ConfigConstant.PUBKEY);
					paramIn.set("_i18n_tag", ConfigConstant.I18N_ATG);
					paramIn.set("__DICTTAG__", ConfigConstant.DICT_TAG);
					paramIn.set("_VERSION", ConfigConstant.F_VSRION);
					paramIn.set("title", Global.getConfig("SYS_CONFIG_TITLE"));
					paramIn.set("sysConfig", JSONUtil.parseObj(Global.getConfig(ConfigConstant.SYS_CONFIG_KEY)));
					paramIn.set("tenantModel",PropertiesUtils.getInstance().get("fast.tenantModel.enabled"));
					paramIn.set("valideLogin",Global.isValidCode());
					paramIn.set("isValidationCode",Global.isValidationCode());
					outBytes = paramIn.toString().getBytes();
				}
			}else {
				StringBuffer _default = _default(path, StrUtil.nullToDefault(CookieUtil.getCookie(req,ConfigConstant.LANGUAGE)
						,LocaleContextHolder.getLocale().toString()),userName,name,userId)
						.append(StrUtil.format(",basePath='{}';",basePath));
				// 无头像设置默认
				_default.append("function imgUserError(){var img=event.srcElement;img.src=baseURL+\"static/img/user2-160x160.jpg\"; img.onerror=null;};");
				if(!ConfigConstant.LIC_TAG.equals("SUCCESS")){
					_default.append(";$(function() {" +
							"var lic = $('<div id=\""+ RandomUtil.randomString(5) +"\" style=\"position: fixed;right: 100px;overflow: hidden;z-index: 19920219;" +
							"pointer-events: none;opacity: 0.2;font-size: 14px;font-family: Microsoft YaHei UI,Microsoft YaHei;color: rgb(255 0 0);" +
							"text-align: center;display: block;bottom: 7px;font-weight: bold;\">"+UnicodeUtil.toUnicode(ToolUtil.message("软件未注册"))+"<span style=\"font-size: 12px;\">("
							+StrUtil.join(StrUtil.EMPTY,ConfigConstant.J_2__E_E)+")</span></div>');" +
							"if(!(self.frameElement && self.frameElement.tagName == \"IFRAME\")){" +
							"if($('.wrapper > footer').length == 0){" +
							"lic.css('bottom','');"+
							"lic.css('top','10px');"+
							"lic.css('right','10px');"+
							"}"+
							"var __index = Math.floor((Math.random()* $('body').children().length));"+
							"window.setTimeout(function(){$($('body').children()[__index]).after(lic);},200);"+
							"}"+
							"});");
				}
				//outBytes = UnicodeUtil.toUnicode(_default.toString()).getBytes();
				outBytes = _default.toString().getBytes();
				log.debug("压缩前:{}", outBytes.length);
				// 用GZIPOutputStream压缩
				ByteArrayOutputStream bout=new ByteArrayOutputStream();
				GZIPOutputStream gzip=new GZIPOutputStream(bout);
				gzip.write(outBytes);
				// 一般输出都要flush
				gzip.flush();
				gzip.close();
				outBytes = bout.toByteArray();
				log.debug("压缩之后:{}" + outBytes.length);
				resp.setContentType("text/javascript");
				// 注明压缩类型
				resp.setHeader("content-encoding", "gzip");
				resp.setHeader("content-length",outBytes.length+"");
			}
		}
		
		InputStream input= new ByteArrayInputStream(outBytes);
		OutputStream output= resp.getOutputStream();
		try{
			IoUtil.copy(input,output);
		}finally{
			IoUtil.close(input);
			IoUtil.close(output);
		}
    }
	
	/**
	 * 默认返回信息
	 * @return
	 */
	public StringBuffer _default(String baseURL,String lang, String _username,String name, String userId) {
		StringBuffer sb = new StringBuffer();
		sb.append("/*!\n" + 
				" * Copyright (c) 2020-Now http://www.j2eefast.com All rights reserved.\n" + 
				" * No deletion without permission\n" + 
				" * @author ZhouZhou\n" + 
				" * @date 2021-09-02\n" + 
				" */\n");
		sb.append(StrUtil.format("var baseURL='{}/',ctx='{}',__LANG__='{}',", baseURL,baseURL,
				lang));
		sb.append(StrUtil.format("__USERNAME__='{}',__NAME__='{}',__USERID__='{}',", _username,UnicodeUtil.toUnicode(name),userId));
		sb.append(StrUtil.format("__SECRETKEY__='{}',", ConfigConstant.PUBKEY));
		sb.append(StrUtil.format("__LOCKSCREEN__='{}',", Global.getDbKey("SYS_LOCK_SCEREEN")));
		sb.append(StrUtil.format("__I18NTAG__='{}',", ConfigConstant.I18N_ATG));
		sb.append(StrUtil.format("__DICTTAG__='{}',", ConfigConstant.DICT_TAG));
		sb.append(StrUtil.format("__VERSION__='{}',", ConfigConstant.F_VSRION));
		sb.append(StrUtil.format("__ISCSRF__={}", PropertiesUtils.getInstance().get("fast.csrf.enabled")));
		return sb;
	}
	
	
}
