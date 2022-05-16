/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro;

import cn.hutool.json.JSONUtil;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ServletUtil;
import org.apache.http.HttpStatus;
import org.apache.shiro.web.util.WebUtils;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 针对系统未登录拦截处理
 * @author huanzhou
 */
public class UserFilter extends org.apache.shiro.web.filter.authc.UserFilter {

	/**
	 * 是否隐藏、未登录不直接跳转登录页面
	 */
	private boolean isHideLoginUrl;

	
	public UserFilter() {
		this.isHideLoginUrl = (Boolean) PropertiesUtils
				.getInstance().get("shiro.isHideLoginUrl");
	}
	
	
    /**
     * 未登录
     */
    @Override
    protected void saveRequestAndRedirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        //判断是否为浏览器直接访问
        if(!ServletUtil.isAjaxRequest(WebUtils.toHttp(request))) {
            try {
            	if(!isHideLoginUrl) {
            		WebUtils.issueRedirect(request, response, getLoginUrl());
            	}else {
            	    // 如果隐藏登录则直接让前端404
                    request.getRequestDispatcher(PropertiesUtils
                            .getInstance().getProperty("server.error.path","/error")).forward(request, response);
            	}
            } catch (Exception e) {
            }
        }else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            response.getWriter().print(JSONUtil.parse(ResponseData.error("A0001","未登录或者登录超时,请重新认证登录.")).toString());
        }
    }
}
