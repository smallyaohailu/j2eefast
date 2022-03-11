/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.utils;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Map;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author zhouzhou
 * @date 2020-03-12 14:34
 */
@Component
@Slf4j
public class SpringUtil implements BeanFactoryPostProcessor, ApplicationContextAware {

	private static ConfigurableListableBeanFactory beanFactory;
	
	/**
	 * Spring应用上下文环境
	 */
	private static ApplicationContext applicationContext;
	
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		SpringUtil.beanFactory = beanFactory;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtil.applicationContext = applicationContext;
	}
	
	/**
	 * 获取{@link ApplicationContext}
	 *
	 * @return {@link ApplicationContext}
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	/**
	 * 获取{@link ListableBeanFactory}，可能为{@link ConfigurableListableBeanFactory} 或 {@link ApplicationContextAware}
	 *
	 * @return {@link ListableBeanFactory}
	 */
	public static ListableBeanFactory getBeanFactory() {
		return null == beanFactory ? applicationContext : beanFactory;
	}
	
	/**
	 * 获取{@link ConfigurableListableBeanFactory}
	 *
	 * @return {@link ConfigurableListableBeanFactory}
	 * @throws UtilException 当上下文非ConfigurableListableBeanFactory抛出异常
	 */
	public static ConfigurableListableBeanFactory getConfigurableBeanFactory() throws UtilException{
		final ConfigurableListableBeanFactory factory;
		if (null != beanFactory) {
			factory = beanFactory;
		} else if (applicationContext instanceof ConfigurableApplicationContext) {
			factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
		} else {
			throw new UtilException("No ConfigurableListableBeanFactory from context!");
		}
		return factory;
	}
	
	/**
	 * 通过name获取 Bean
	 *
	 * @param <T>  Bean类型
	 * @param name Bean名称
	 * @return Bean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) getBeanFactory().getBean(name);
	}
	
	/**
	 * 通过class获取Bean
	 *
	 * @param <T>   Bean类型
	 * @param clazz Bean类
	 * @return Bean对象
	 */
	public static <T> T getBean(Class<T> clazz) {
		return getBeanFactory().getBean(clazz);
	}
	
	/**
	 * 通过name,以及Clazz返回指定的Bean
	 *
	 * @param <T>   bean类型
	 * @param name  Bean名称
	 * @param clazz bean类型
	 * @return Bean对象
	 */
	public static <T> T getBean(String name, Class<T> clazz) {
		return getBeanFactory().getBean(name, clazz);
	}
	
	/**
	 * 通过类型参考返回带泛型参数的Bean
	 *
	 * @param reference 类型参考，用于持有转换后的泛型类型
	 * @param <T>       Bean类型
	 * @return 带泛型参数的Bean
	 * @since 5.4.0
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(TypeReference<T> reference) {
		final ParameterizedType parameterizedType = (ParameterizedType) reference.getType();
		final Class<T> rawType = (Class<T>) parameterizedType.getRawType();
		final Class<?>[] genericTypes = Arrays.stream(parameterizedType.getActualTypeArguments()).map(type -> (Class<?>) type).toArray(Class[]::new);
		final String[] beanNames = getBeanFactory().getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
		return getBean(beanNames[0], rawType);
	}

	/**
	 * 获取指定类型对应的所有Bean，包括子类
	 *
	 * @param <T>  Bean类型
	 * @param type 类、接口，null表示获取所有bean
	 * @return 类型对应的bean，key是bean注册的name，value是Bean
	 * @since 5.3.3
	 */
	public static <T> Map<String, T> getBeansOfType(Class<T> type) {
		return getBeanFactory().getBeansOfType(type);
	}

	/**
	 * 获取指定类型对应的Bean名称，包括子类
	 *
	 * @param type 类、接口，null表示获取所有bean名称
	 * @return bean名称
	 * @since 5.3.3
	 */
	public static String[] getBeanNamesForType(Class<?> type) {
		return getBeanFactory().getBeanNamesForType(type);
	}

	/**
	 * 获取配置文件配置项的值
	 *
	 * @param key 配置项key
	 * @return 属性值
	 * @since 5.3.3
	 */
	public static String getProperty(String key) {
		if(null == applicationContext){
			return null;
		}
		return applicationContext.getEnvironment().getProperty(key);
	}
	
	/**
	 * 获取当前的环境配置，无配置返回null
	 *
	 * @return 当前的环境配置
	 * @since 5.3.3
	 */
	public static String[] getActiveProfiles() {
		if(null == applicationContext){
			return null;
		}
		return applicationContext.getEnvironment().getActiveProfiles();
	}

	/**
	 * 获取当前的环境配置，当有多个环境配置时，只获取第一个
	 *
	 * @return 当前的环境配置
	 * @since 5.3.3
	 */
	public static String getActiveProfile() {
		final String[] activeProfiles = getActiveProfiles();
		return ArrayUtil.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
	}

	/**
	 * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
	 * @author zhouzhou
	 * @date 2020-03-12 14:39
	 */
	public static boolean containsBean(String name){
	    return beanFactory.containsBean(name);
	}
	
	/**
	 * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 
	 * </P>
	 * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
	 * @author zhouzhou
	 * @date 2020-03-12 14:39
	 */
	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException{
	    return beanFactory.isSingleton(name);
	}
	

	public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
	    return beanFactory.getType(name);
	}
	
	/**
	 * 如果给定的bean名字在bean定义中有别名，则返回这些别名
	 * @author zhouzhou
	 * @date 2020-03-12 14:39
	 */
	public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
	    return beanFactory.getAliases(name);
	}

	
	/**
	 * 动态向Spring注册Bean
	 * <p>
	 * 由{@link org.springframework.beans.factory.BeanFactory} 实现，通过工具开放API
	 * <p>
	 * 更新: shadow 2021-07-29 17:20:44 增加自动注入，修复注册bean无法反向注入的问题
	 *
	 * @param <T>      Bean类型
	 * @param beanName 名称
	 * @param bean     bean
	 * @author shadow
	 * @since 5.4.2
	 */
	public static <T> void registerBean(String beanName, T bean) {
		final ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
		factory.autowireBean(bean);
		factory.registerSingleton(beanName, bean);
	}

	/**
	 * 主动向Spring容器中注册bean
	 *
	 * @param name               BeanName
	 * @param clazz              注册的bean的类性
	 * @param args               构造方法的必要参数，顺序和类型要求和clazz中定义的一致
	 * @param <T>
	 * @return 返回注册到容器中的bean对象
	 */
	public static <T> T registerBean(String name, Class<T> clazz,
									 Object... args) {
		if(applicationContext.containsBean(name)) {
			Object bean = applicationContext.getBean(name);
			if (bean.getClass().isAssignableFrom(clazz)) {
				return (T) bean;
			} else {
				throw new RuntimeException("BeanName 重复 " + name);
			}
		}
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
		for (Object arg : args) {
			beanDefinitionBuilder.addConstructorArgValue(arg);
		}
		BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

		DefaultListableBeanFactory factory = (DefaultListableBeanFactory)applicationContext
				.getAutowireCapableBeanFactory();
		factory.registerBeanDefinition(name, beanDefinition);
		return applicationContext.getBean(name, clazz);
	}

	/**
	 * 注销系统自动注入bean
	 * <p>
	 * 将Spring中的bean注销，请谨慎使用
	 *
	 * @param beanName bean名称
	 * @author shadow
	 * @since 5.7.7
	 */
	public static void unregisterBean(String beanName) {
		try{
			DefaultListableBeanFactory factory = (DefaultListableBeanFactory)applicationContext
					.getAutowireCapableBeanFactory();
			factory.removeBeanDefinition(beanName);
		}catch (Exception e){
			log.error("注销Bean失败!",e);
			//throw new UtilException("Can not unregister bean, the factory is not a DefaultSingletonBeanRegistry!");
		}
	}
	
	/**
	 * 注销手动注入bean
	 * <p>
	 * 将Spring中的bean注销，请谨慎使用
	 *
	 * @param beanName bean名称
	 * @author shadow
	 * @since 5.7.7
	 */
	public static void destroyBean(String beanName) {
		final ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
		if(factory instanceof DefaultSingletonBeanRegistry){
			DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) factory;
			registry.destroySingleton(beanName);
		} else {
			throw new UtilException("Can not unregister bean, the factory is not a DefaultSingletonBeanRegistry!");
		}
	}
	
	/**
	 * 通过SPring AOP 代理获取
	 * @param invoker
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAopProxy(T invoker){
		return (T) AopContext.currentProxy();
	}
}
