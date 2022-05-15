/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.j2eefast.common.core.io.PropertiesUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Web客户端工具
 * @author zhouzhou
 * @date 2020-03-12 14:56
 */
public class ServletUtil {
	
	
	/**
     * 获取请求的ip地址
     */
    public static String getIp() {
        HttpServletRequest request = getRequest();
        String ip = "127.0.0.1";
        if (request == null) {
            return ip;
        } else {

            Boolean forward = (Boolean) PropertiesUtils.getInstance()
                    .get("server.use-forward-headers");
            String xffName = PropertiesUtils.getInstance().getProperty("server.tomcat.remote-ip-header");
            if(forward && ToolUtil.isNotEmpty(xffName)){
                ip =  request.getHeader(xffName);
                if(ToolUtil.isNotEmpty(ip)){
                    return ip;
                }
            }

            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("x-forwarded-for");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteHost();
            }
            if(ToolUtil.isNotEmpty(ip)){
                return ip;
            }
        	return NetUtil.getMultistageReverseProxyIp(request.getRemoteAddr());
        }
    }
	
	/**
     * 获取所有请求的值
     */
    public static Map<String, String> getRequestParameters() {
        HashMap<String, String> values = new HashMap<>();
        HttpServletRequest request = getRequest();
        if (request == null) {
            return values;
        }
        @SuppressWarnings("rawtypes")
		Enumeration enums = request.getParameterNames();
        while (enums.hasMoreElements()) {
            String paramName = (String) enums.nextElement();
            String paramValue = request.getParameter(paramName);
            values.put(paramName, paramValue);
        }
        return values;
    }
	
	/**
     * 获取String参数
     */
    public static String getParameter(String name){
        return getRequest().getParameter(name);
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue){
        return StrUtil.blankToDefault(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name){
        return NumberUtil.parseNumber((getRequest().getParameter(name))).intValue();
    }


    /**
     * 获取request
     */
    public static HttpServletRequest getRequest(){
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse(){
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession(){
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes(){
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     * 
     */
    public static String renderString(HttpServletResponse response, String string){
    	String utf8			  = "utf-8";
    	String application	  = "application/json";
    	
        try{
            response.setContentType(application);
            response.setCharacterEncoding(utf8);
            response.getWriter().print(string);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
	 * 支持AJAX的页面跳转
	 */
	public static void redirectUrl(HttpServletRequest request, HttpServletResponse response, String url){
		try {
			if (isAjaxRequest(request)){
				//AJAX不支持Redirect改用Forward
				request.getRequestDispatcher(url).forward(request, response); 
			}else{
				response.sendRedirect(request.getContextPath() + url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * 是否是Ajax异步请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request){
    	
    	String acc 				= "accept";
    	String application		= "application/json";
    	String requested 		= "X-Requested-With";
    	String xmlHttpRequest   = "XMLHttpRequest";
    	String json		 		= ".json";
    	String xml		 		= ".xml";
    	String json1		 	= "json";
    	String xml1		 		= "xml";
    	String ajax1			= "__ajax";
    	
        String accept = request.getHeader(acc);
        if (accept != null && accept.indexOf(application) != -1){
            return true;
        }

        String xRequestedWith = request.getHeader(requested);
        if (xRequestedWith != null && xRequestedWith.indexOf(xmlHttpRequest) != -1){
            return true;
        }

        String uri = request.getRequestURI();
        if (StrUtil.containsAny(uri, json, xml)){
            return true;
        }

        String ajax = request.getParameter(ajax1);
        if (StrUtil.containsAny(ajax, json1, xml1)){
            return true;
        }

        return false;
    }

    /**
     * 设置客户端缓存过期时间 的Header.
     */
    public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
        // Http 1.0 header, set a fix expires date.
        response.setDateHeader(HttpHeaders.EXPIRES, System.currentTimeMillis() + expiresSeconds * 1000);
        // Http 1.1 header, set a time after now.
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private, max-age=" + expiresSeconds);
    }

    /**
     * 设置禁止客户端缓存的Header.
     */
    public static void setNoCacheHeader(HttpServletResponse response) {
        // Http 1.0 header
        response.setDateHeader(HttpHeaders.EXPIRES, 1L);
        response.addHeader(HttpHeaders.PRAGMA, "no-cache");
        // Http 1.1 header
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0");
    }

    /**
     * 设置LastModified Header.
     */
    public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModifiedDate);
    }
}
