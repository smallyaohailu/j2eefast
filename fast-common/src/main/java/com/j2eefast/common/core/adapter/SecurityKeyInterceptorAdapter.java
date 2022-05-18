/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.adapter;

import com.j2eefast.common.core.config.ResourceLoaderServlet;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.utils.CookieUtil;
import com.j2eefast.common.core.utils.ToolUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 安全key, 拦截器
 * @author zhouzhou
 */
@Component
public class SecurityKeyInterceptorAdapter implements HandlerInterceptor{

	/**
	 * 系统是否允许嵌入到外部网站iframe中
	 */
	@Value("#{ @environment['shiro.selfIframe.enabled'] ?: false }")
	private boolean enabled;

	/**
	 * XSS 过滤
	 */
	@Value("#{ @environment['fast.xss.enabled'] ?: true }")
	private boolean xssEnabled;
	

	public static final String X_FRAME_OPTIONS = "X-Frame-Options";
	public static final String X_XSS_PROTECTION = "X-XSS-Protection";
	//如果检测到攻击，浏览器直接阻止整个页面的加载
	public static final String X_XSS_HEADERVALUE = "1 ; mode=block";

	public enum Mode {
		/**
		 * A browser receiving content with this header field MUST NOT display
		 * this content in any frame.
		 */
		DENY,
		/**
		 * A browser receiving content with this header field MUST NOT display
		 * this content in any frame from a page of different origin than the
		 * content itself.
		 *
		 * If a browser or plugin cannot reliably determine whether or not the
		 * origin of the content and the frame are the same, this MUST be
		 * treated as "DENY".
		 */
		SAMEORIGIN
	}

	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String _secretKey = "";
		//获取登录安全Key
		if(request.getCookies() == null){
			_secretKey = ConfigConstant.PUBKEY;
			CookieUtil.setReadCookie(response,ConfigConstant.SECRETKEY, _secretKey,60*60*24*7);
		}else{
			_secretKey = CookieUtil.getCookie(request,ConfigConstant.SECRETKEY);
			//判断浏览器客户端公钥是否为后台的公钥如果不存在或者不对 立即更新
			if(ToolUtil.isEmpty(_secretKey) ||
					!ConfigConstant.PUBKEY.equals(_secretKey)){
				_secretKey = ConfigConstant.PUBKEY;
				CookieUtil.setReadCookie(response,ConfigConstant.SECRETKEY, _secretKey,60*60*24*7);
			}
		}
		request.setAttribute(ConfigConstant.SECRETKEY, _secretKey);
		
		String scheme = request.getScheme();
        String serverName = request.getServerName();
		int port = request.getServerPort();
		port = (port == 443 || port == 80) ? 0 : port;
        String path = request.getContextPath();
		String basePath = scheme + "://" + serverName  + (port == 0? "": (":" + port)) + path;
        //修改绝对地址
 	    //request.setAttribute(ConfigConstant.BASE_PATH, path);
		//request.setAttribute(ConfigConstant.CTX_STATIC, basePath);
        request.setAttribute(ConfigConstant.CTX_STATIC, path);
		request.setAttribute(ConfigConstant.BASE_PATH, basePath);
		
		if(!enabled) {
			response.addHeader(X_FRAME_OPTIONS, Mode.SAMEORIGIN.name());
		}
		
		if(xssEnabled) {
			response.addHeader(X_XSS_PROTECTION, X_XSS_HEADERVALUE);
		}
		
        return true;
	}

	
	/**
	 * 系统常量参数、国际化参数输出
	 * @return
	 */
	@Bean
    public ServletRegistrationBean<ResourceLoaderServlet> initStatic() {
        return new ServletRegistrationBean<>(new ResourceLoaderServlet(), "/static/config.js","/i18n/*");
    }
}
