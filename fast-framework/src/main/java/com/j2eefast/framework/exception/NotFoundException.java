/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.exception;

import cn.hutool.http.HtmlUtil;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.utils.ResponseData;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 系统404页面
 * @author huanzhou
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class NotFoundException extends BasicErrorController {

    private static final String HTTP_NOT_FOUND = "error/404";
    
    public NotFoundException(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    /**
     * 覆盖默认的HTML响应
     */
    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> model = new ModelMap();
        //去除XSS
        model.put(ConfigConstant.ERR_PAGE, HtmlUtil.escape(HtmlUtil.filter(UrlPathHelper.defaultInstance.getOriginatingRequestUri(request))));
        return  new ModelAndView(HTTP_NOT_FOUND, model);
    }


    /**
     * 覆盖默认JSON请求响应
     */
    @Override
    @RequestMapping
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		HttpStatus status = getStatus(request);
		if (status == HttpStatus.NO_CONTENT) {
			return new ResponseEntity<>(status);
		}
		Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));;
		return new ResponseEntity<>(new ResponseData("A0002", "请求不存在,请检查请求链接!",body), status);
	}
}
