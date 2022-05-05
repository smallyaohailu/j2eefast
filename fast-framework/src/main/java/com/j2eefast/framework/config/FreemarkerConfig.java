/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.framework.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.j2eefast.common.core.page.JFreeMarkerViewResolver;
import com.j2eefast.framework.utils.*;
import com.jagregory.shiro.freemarker.ShiroTags;
import freemarker.template.TemplateException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.web.servlet.view.freemarker.*;

/**
 * Freemarker 模板引擎配置
 * @author zhouzhou
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 11)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
		ValidationAutoConfiguration.class })
public class FreemarkerConfig {


	private final FreeMarkerProperties properties;


	protected final FreeMarkerProperties getProperties() {
		return this.properties;
	}

	protected void applyProperties(FreeMarkerConfigurationFactory factory) {
		factory.setTemplateLoaderPaths(this.properties.getTemplateLoaderPath());
		factory.setPreferFileSystemAccess(this.properties.isPreferFileSystemAccess());
		factory.setDefaultEncoding(this.properties.getCharsetName());
		Properties settings = new Properties();
		settings.put("recognize_standard_file_extensions", "true");
		settings.putAll(this.properties.getSettings());
		factory.setFreemarkerSettings(settings);
	}

	protected FreemarkerConfig(FreeMarkerProperties properties) {
		this.properties = properties;
	}

	@Bean
	JFreeMarkerViewResolver jfreeMarkerViewResolver() {
		JFreeMarkerViewResolver resolver = new JFreeMarkerViewResolver();
		getProperties().applyToMvcViewResolver(resolver);
		return resolver;
	}
	
	@Bean
	public FreeMarkerConfigurer freeMarkerConfigurer(
													 Tools tools,
													 SysConfig sysconfig,
													 DictConfig dict,
													 PermissionConfig permission)
			 										 throws IOException, TemplateException {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		configurer.setTemplateLoaderPath("classpath:/templates");
		Map<String, Object> variables = new HashMap<>(3);
		variables.put("config", sysconfig);
		variables.put("dict",dict);
		variables.put("permission",permission);
		variables.put("tools",tools);
		configurer.setFreemarkerVariables(variables);
		Properties settings = new Properties();
		settings.setProperty("default_encoding", "utf-8");
		settings.setProperty("number_format", "0.##");
		//扫描整个项目包括子项目
		configurer.setPreferFileSystemAccess(false);
		configurer.setFreemarkerSettings(settings);
		freemarker.template.Configuration configuration = configurer.createConfiguration();
		//shiro权限控制 前端使用宏获取判断
		configuration.setSharedVariable("shiro", new ShiroTags());
		configurer.setConfiguration(configuration);
		return configurer;
	}
}
