/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.shiro;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ServletUtil;
import com.j2eefast.common.core.utils.ToolUtil;

import org.apache.http.HttpStatus;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 针对系统未登录拦截处理
 * @author huanzhou
 */
public class UserFilter extends org.apache.shiro.web.filter.authc.UserFilter {

	/**
	 * 是否隐藏、未登录不直接跳转登录页面
	 */
	private boolean isHideLoginUrl;
	private List<String> innerFilterIps;
	
	public UserFilter() {
		this.isHideLoginUrl = (Boolean) PropertiesUtils
				.getInstance().get("shiro.isHideLoginUrl");
		String ips = PropertiesUtils.getInstance().getProperty("shiro.innerFilterIps", StrUtil.EMPTY);
		if(ToolUtil.isEmpty(ips)) {
			innerFilterIps = new ArrayList<>();
		}else {
			innerFilterIps = StrUtil.split(ips, StrUtil.C_COMMA);
		}
	}
	
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		//是否限制IP访问
		if(ToolUtil.isNotEmpty(innerFilterIps)) {
			//获取IP
			String addr = ServletUtil.getIp();
			if(innerFilterIps.contains(addr)) {
				if (isLoginRequest(request, response)) {
		            return true;
		        } else {
		            Subject subject = getSubject(request, response);
		            // If principal is not null, then the user is known and should be allowed access.
		            return subject.getPrincipal() != null;
		        }
			}else {
				Subject subject = getSubject(request, response);
				if(subject != null) {
					subject.logout();
				}
                return false;
			}
		}
		if (isLoginRequest(request, response)) {
            return true;
        } else {
            Subject subject = getSubject(request, response);
            // If principal is not null, then the user is known and should be allowed access.
            return subject.getPrincipal() != null;
        }
	}
	
	
    /**
     * 未登录
     */
    @Override
    protected void saveRequestAndRedirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        //判断是否为浏览器直接访问
        if(!ServletUtil.isAjaxRequest(WebUtils.toHttp(request))) {
            try {
            	if(ToolUtil.isNotEmpty(innerFilterIps)) {
            		// 如果隐藏登录则直接让前端404
                    request.getRequestDispatcher(PropertiesUtils
							.getInstance().getProperty("server.error.path","/error")).forward(request, response);
                    return;
            	}
            	if(!isHideLoginUrl) {
            		WebUtils.issueRedirect(request, response, getLoginUrl());
            		return;
            	}else {
            	    // 如果隐藏登录则直接让前端404
                    request.getRequestDispatcher(PropertiesUtils
                            .getInstance().getProperty("server.error.path","/error")).forward(request, response);
                    return;
            	}
            } catch (Exception e) {
            }
        }else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            response.getWriter().print(JSONUtil.parse(ResponseData.error("A0001","未登录或者登录超时,请重新认证登录.")).toString());
            return;
        }
    }
}
