/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.controller;

import cn.hutool.core.util.StrUtil;
import com.j2eefast.common.core.utils.CookieUtil;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ServletUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;

/**
 * <p>控制器基础类</p>
 *
 * @author: zhouzhou
 * @date: 2019-04-01 19:49
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
public class BaseController {
	
	/**
	 * 登录
	 */
	@Value("#{ @environment['shiro.loginUrl'] ?: '/login' }")
	protected String loginUrl;
	
	@Value("#{ @environment['shiro.successUrl'] ?: '/index' }")
	protected String successUrl;
	
    protected final String                  REDIRECT                        = "redirect:";
    protected final String                  FORWARD                         = "forward:";


    protected HttpServletRequest getHttpServletRequest() {
        return ServletUtil.getRequest();
    }

    protected HttpServletResponse getHttpServletResponse() {
        return ServletUtil.getResponse();
    }

    protected HttpSession getSession() {
        return Objects.requireNonNull(ServletUtil.getRequest()).getSession();
    }

    protected HttpSession getSession(Boolean flag) {
        return Objects.requireNonNull(ServletUtil.getRequest()).getSession(flag);
    }

    protected String getPara(String name) {
        return StrUtil.nullToDefault(ServletUtil.getRequest().getParameter(name),"");
    }

    protected String getPara(String name,String default1) {
        return StrUtil.nullToDefault(ServletUtil.getRequest().getParameter(name),default1);
    }

    protected String getHeader(String name){
        return StrUtil.nullToDefault(getHttpServletRequest().getHeader(name),StrUtil.EMPTY);
    }

    protected void setAttr(String name, Object value) {
        Objects.requireNonNull(ServletUtil.getRequest()).setAttribute(name, value);
    }

    /**
     * 删除cookie
     */
    protected void deleteCookieByName(String cookieName) {
        Cookie[] cookies = this.getHttpServletRequest().getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                Cookie temp = new Cookie(cookie.getName(), "");
                temp.setMaxAge(0);
                this.getHttpServletResponse().addCookie(temp);
            }
        }
    }

    /**
     * 删除所有cookie
     */
    protected void deleteAllCookie() {
        Cookie[] cookies = this.getHttpServletRequest().getCookies();
        for (Cookie cookie : cookies) {
            Cookie temp = new Cookie(cookie.getName(), "");
            temp.setMaxAge(0);
            this.getHttpServletResponse().addCookie(temp);
        }
    }

    /**
     * 获取并且删除
     * @param name
     * @return
     */
    protected String getCookieToDel(String name){
		return CookieUtil.getCookie(this.getHttpServletRequest(),
                this.getHttpServletResponse(),name);
    }

    protected String getCookie(String name){
        return CookieUtil.getCookie(this.getHttpServletRequest(),
                name);
    }


    /**
     * 返回页面错误
     * @param msg
     * @return
     */
    protected ResponseData error(String msg){
        return ResponseData.error(msg);
    }

    protected ResponseData error(String code, String msg){
        return ResponseData.error(code,msg);
    }

    /**
     * 返回成功消息
     */
    protected ResponseData success(String msg){
        return ResponseData.success(msg);
    }

    protected ResponseData success(){
        return ResponseData.success();
    }

    protected ResponseData success(Object data){
        return ResponseData.success(data);
    }

    protected ResponseData success(String msg,Object data){
        return ResponseData.success(msg,data);
    }

}
