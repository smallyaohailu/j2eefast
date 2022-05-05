/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import com.bstek.ureport.console.UReportServlet;
/**
 * 集成ureport2 报表功能
 * @author huanzhou
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "fast.ureport", name = "enabled", havingValue="true")
@ImportResource("classpath:ureport-console-context.xml")
public class Ureport2Config {

	/**
	 * 配置报表
	 * @author ZhouZhou
	 * @return
	 */
    @Bean
    public ServletRegistrationBean<UReportServlet> initUReport() {
        return new ServletRegistrationBean<>(new UReportServlet(), "/ureport/*");
    }
    
}