/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.config;

import cn.hutool.core.convert.Convert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * CORS跨域访问配置
 * @date 2021-08-21
 * @author zhouzhou
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "shiro.cors", name = "enabled", havingValue="true")
public class CorsConfig{

    /**
     * #设置允许的方法
     */
    @Value("#{ @environment['shiro.cors.allowedMethods'] ?: 'GET,POST,DELETE,PUT' }")
    private String allowedMethods;


    /**
     * 允许跨域请求的域名
     */
    @Value("#{ @environment['shiro.cors.allowedOrigins'] ?: '*' }")
    private String allowedOrigins;
    /**
     * 允许跨域访问的链接
     */
    @Value("#{ @environment['shiro.cors.urlPatterns'] ?: null }")
    private String urlPatterns;

    /**
     * 跨域允许时间
     */
    @Value("#{ @environment['shiro.cors.maxAge'] ?: 30 }")
    private int maxAge;

    /**
     * 设置允许的header属性
     */
    @Value("#{ @environment['shiro.cors.allowedHeaders'] ?: 'content-type,x-requested-with,ajax,token,Authorization,authorization' }")
    private String allowedHeaders;

    @Value("#{ @environment['shiro.cors.credentials'] ?: true }")
    private Boolean credentials;


    @Bean
    public FilterRegistrationBean carsFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new CorsConfig.CorsFilter());
        registration.setOrder(Integer.MAX_VALUE-90);
        registration.addUrlPatterns(urlPatterns.split(","));
        registration.setName("carsFilter");
        //传参
        Map<String, String> initParameters = new HashMap<String, String>(5);
        initParameters.put("maxAge", Convert.toStr(maxAge));
        initParameters.put("allowedHeaders", allowedHeaders);
        initParameters.put("urlPatterns", urlPatterns);
        initParameters.put("allowedOrigins", allowedOrigins);
        initParameters.put("allowedMethods", allowedMethods);
        initParameters.put("credentials", credentials? "true" : "false");
        registration.setInitParameters(initParameters);
        return registration;
    }

    class CorsFilter implements Filter{

        /**
         * #设置允许的方法
         */
        private String allowedMethods;

        /**
         * 允许跨域请求的域名
         */
        private String allowedOrigins;
        /**
         * 允许跨域访问的链接
         */
        private String urlPatterns;

        /**
         * 跨域允许时间
         */
        private String maxAge;

        /**
         * 设置允许的header属性
         */
        private String allowedHeaders;

        private String credentials;


        @Override
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain){

            HttpServletResponse response = (HttpServletResponse) res;
            HttpServletRequest request = (HttpServletRequest) req;

            if(log.isDebugEnabled()){
                log.debug("----------------CORS跨域访问信息----------------");
                log.debug("Origin:{}",request.getHeader("Origin"));
                log.debug("Referer:{}",request.getHeader("Referer"));
                log.debug("User-Agent:{}",request.getHeader("User-Agent"));
                log.debug("URL:{}",request.getRequestURI());
                log.debug("Method:{}",request.getMethod());
                log.debug("----------------CORS跨域访问信息----------------");
            }

            //放行所有,类似*,这里*无效
            response.setHeader("Access-Control-Allow-Origin", allowedOrigins);
            response.setHeader("Access-Control-Allow-Credentials", credentials);
            //允许请求方式
            response.setHeader("Access-Control-Allow-Methods", allowedMethods);
            response.setHeader("Access-Control-Max-Age", maxAge);
            //需要放行header头部字段 如需鉴权字段，自行添加，如Authorization
            response.setHeader("Access-Control-Allow-Headers", allowedHeaders);

            try {
                chain.doFilter(request, response);
            } catch (Exception e) {
                log.error("CORS过滤器放行异常:",e);
            }
        }

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            allowedMethods = filterConfig.getInitParameter("allowedMethods");
            allowedOrigins = filterConfig.getInitParameter("allowedOrigins");
            urlPatterns = filterConfig.getInitParameter("urlPatterns");
            maxAge = filterConfig.getInitParameter("maxAge");
            allowedHeaders = filterConfig.getInitParameter("allowedHeaders");
            credentials = filterConfig.getInitParameter("credentials");
        }

        @Override
        public void destroy() {
        }

    }
}
