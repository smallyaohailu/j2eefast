/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.xss;

import com.google.common.collect.Lists;
import com.j2eefast.common.core.utils.ToolUtil;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * XSS过滤
 */
public class XssFilter implements Filter {

	/**
	 * 排除链接 系统必须排除否则会有数据异常
	 */
	public List<String> excludes = Lists.newArrayList("/sys/config/*",
			"/sys/dict/*","/tool/gen/column/list",
			"/tool/gen/column/list","/tool/gen/*",
			"/sys/database/add","/sys/msg/*","/sys/area/load");

	/**
	 * xss过滤开关
	 */
	public boolean enabled = false;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String tempExcludes = filterConfig.getInitParameter("excludes");
		String tempEnabled = filterConfig.getInitParameter("enabled");
		if (ToolUtil.isNotEmpty(tempExcludes)) {
			String[] url = tempExcludes.split(",");
			for (int i = 0; url != null && i < url.length; i++) {
				if(excludes.indexOf(url[i]) == -1){
					excludes.add(url[i]);
				}
			}
		}
		if (ToolUtil.isNotEmpty(tempEnabled)) {
			enabled = Boolean.valueOf(tempEnabled);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		if (handleExcludeURL(req, resp)) {
			chain.doFilter(request, response);
			return;
		}
		XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
		chain.doFilter(xssRequest, response);
	}

	private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {
		if (!enabled) {
			return true;
		}
		if (excludes == null || excludes.isEmpty()) {
			return false;
		}
		String method = request.getMethod();
		// GET DELETE 不过滤
		if (method == null || method.matches("GET") || method.matches("DELETE")){
			return true;
		}
		String url = request.getServletPath();
		for (String pattern : excludes) {
			AntPathMatcher matcher = new AntPathMatcher();
			if (matcher.match(pattern, url)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy(){

	}

}