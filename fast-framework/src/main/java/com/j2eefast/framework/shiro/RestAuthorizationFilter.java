package com.j2eefast.framework.shiro;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.j2eefast.common.core.utils.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author huanzhou
 */
@Slf4j
public class RestAuthorizationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        if (isLoginRequest(request, response)){
            if (isLoginSubmission(request, response)){
                if (log.isTraceEnabled()){
                    log.trace("Login submission detected.  Attempting to execute login.");
                }
                return executeLogin(request, response);
            } else {
                if (log.isTraceEnabled()){
                    log.trace("Login page view.");
                }
                return true;
            }
        } else {
            if (log.isTraceEnabled()){
                log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                        "Authentication url [" + getLoginUrl() + "]");
            }
            saveRequestAndRedirectToLogin(request, response);
            return false;
        }
    }

    @Override
    protected void saveRequestAndRedirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        try{
            saveRequest(request);
        }catch (Exception e){}
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        response.getWriter().print(JSONUtil.parse(ResponseData.error("A0001","登录失败")).toString());
    }
}
