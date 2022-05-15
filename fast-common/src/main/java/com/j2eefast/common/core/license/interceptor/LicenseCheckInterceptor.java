/**
 * Copyright (c) 2016-Now http://www.j2eefast.com All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.license.interceptor;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.j2eefast.common.core.config.LicenseCheckListener;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.license.LicenseVerify;
import com.j2eefast.common.core.license.annotation.FastLicense;
import com.j2eefast.common.core.shiro.RedisSessionDAO;
import com.j2eefast.common.core.utils.ResponseData;
import com.j2eefast.common.core.utils.ServletUtil;
import com.j2eefast.common.core.utils.ToolUtil;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p>证书连接器</p>
 *
 * @author: zhouzhou Emall:loveingowp@163.com
 * @date: 2020-03-18 09:15
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Component
public class LicenseCheckInterceptor implements HandlerInterceptor {
    @Autowired
    private LicenseCheckListener listener;
    @Lazy
    @Resource
    private RedisSessionDAO sessionDAO;

    public LicenseCheckInterceptor(){
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	
    	LicenseVerify licenseVerify = new LicenseVerify();
    	
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            FastLicense annotation = method.getAnnotation(FastLicense.class);
            if (annotation != null) {
               String[] vertifys =  annotation.vertifys();
               for(String vertify: vertifys){
                   if(vertify.equals("detection")){
                       //校验证书是否有效
                       boolean verifyResult = licenseVerify.verify(listener.getVerifyParam());
                       if (!verifyResult) {
                           response.setCharacterEncoding("utf-8");
                           response.setContentType("text/html;charset=utf-8");
                           response.getWriter().write(JSONUtil.parse(ResponseData.error("99998","您的证书无效，请核查服务器是否取得授权或重新申请证书!")).toString());
                           return false;
                       }
                   }
                   if(vertify.equals("online")){
                       int online = licenseVerify.onlineNumVerify(listener.getVerifyParam());
                       if (!sessionDAO.checkNumber(online)) {
                           response.setCharacterEncoding("utf-8");
                           response.setContentType("text/html;charset=utf-8");
                           response.getWriter().write(JSONUtil.parse(ResponseData.error("99997","在线用户已达最大,限制登录!!")).toString());
                           return false;
                       }
                   }
               }
           }
        }
        
        //校验证书是否有效
//        boolean verifyResult = licenseVerify.verify(listener.getVerifyParam());
//        if (!verifyResult) {
//        	if(ServletUtil.isAjaxRequest(request)) {
//        		response.setCharacterEncoding("utf-8");
//                response.setContentType("text/html;charset=utf-8");
//                response.getWriter().write(JSONUtil.parse(ResponseData.error("99998","您的证书无效，请核查服务器是否取得授权或重新申请证书!")).toString());
//                return false;
//        	}else {
//                request.getRequestDispatcher("/authInsu").forward(request, response);
//                return false;
//        	}
//        }
//        String url = UrlPathHelper.defaultInstance.getOriginatingRequestUri(request);
//        if(StrUtil.endWith(url,"login")) {
//        	int online = licenseVerify.onlineNumVerify(listener.getVerifyParam());
//            if (!sessionDAO.checkNumber(online)) {
//                response.setCharacterEncoding("utf-8");
//                response.setContentType("text/html;charset=utf-8");
//                response.getWriter().write(JSONUtil.parse(ResponseData.error("99997","在线用户已达最大,限制登录!!")).toString());
//                return false;
//            }
//        }


        return true;
    }
}
