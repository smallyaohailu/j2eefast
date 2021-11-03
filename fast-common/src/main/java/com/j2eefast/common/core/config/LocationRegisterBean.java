/*
 * All content copyright http://www.j2eefast.com, unless
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.config;

import com.bstek.ureport.build.ReportBuilder;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import com.bstek.ureport.export.ExportManager;
import com.bstek.ureport.export.ReportRender;
import com.bstek.ureport.parser.ReportParser;
import com.j2eefast.common.core.constants.ConfigConstant;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.ureport.*;
import com.j2eefast.common.db.context.DataSourceContext;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

/**
 * 平台启动动态注入部分实体Bean
 * @author huanzhou
 */
@Component
public class LocationRegisterBean implements BeanFactoryAware {

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

        // 动态注入
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory)beanFactory;

        // 动态注入 如果ureport2报表 开启动态注入数据源
        if((Boolean)PropertiesUtils.getInstance().get(ConfigConstant.UREPORT_ENABLED_YML)){
        	
            Map<String, DataSource> dataSourceMap =  DataSourceContext.getDataSources();
            for (Map.Entry<String,DataSource> entry : dataSourceMap.entrySet()) {
                registerBean(listableBeanFactory,BuildinDatasource.class.getName() + StrUtil.DOT + entry.getKey(),new BuildinDatasource() {
                    @Override
                    public String name() {
                        return entry.getKey();
                    }
                    @SneakyThrows
                    @Override
                    public Connection getConnection() {
                        return entry.getValue().getConnection();
                    }
                });
            }

            //自定义导出管理器
            FastExportManagerImpl fastExportManager = new FastExportManagerImpl();
            fastExportManager.setReportRender(beanFactory.getBean(ReportRender.class));
            listableBeanFactory.registerSingleton("fastExportManager",fastExportManager);

            // 修改支持多源数据库热拔插
            FastDatasourceServletAction fastDatasourceServletAction = new FastDatasourceServletAction();
            listableBeanFactory.registerSingleton("fastDatasourceServletAction",fastDatasourceServletAction);

            // 新增导出报表连接支持多租户模式
            FastExportExcelServicetAction fastExportExcelServicetAction = new FastExportExcelServicetAction();
            fastExportExcelServicetAction.setExportManager((ExportManager) beanFactory.getBean("fastExportManager"));
            fastExportExcelServicetAction.setReportBuilder(beanFactory.getBean(ReportBuilder.class));
            listableBeanFactory.registerSingleton("fastExportExcelServicetAction",fastExportExcelServicetAction);

            FastHtmlPreviewServletAction fastHtmlPreviewServletAction = new FastHtmlPreviewServletAction();
            fastHtmlPreviewServletAction.setExportManager((ExportManager) beanFactory.getBean("fastExportManager"));
            fastHtmlPreviewServletAction.setReportBuilder(beanFactory.getBean(ReportBuilder.class));
            fastHtmlPreviewServletAction.setReportRender(beanFactory.getBean(ReportRender.class));
            listableBeanFactory.registerSingleton("fastHtmlPreviewServletAction",fastHtmlPreviewServletAction);

            FastDesignerServletAction fastDesignerServletAction = new FastDesignerServletAction();
            fastDesignerServletAction.setReportParser(beanFactory.getBean(ReportParser.class));
            fastDesignerServletAction.setReportRender(beanFactory.getBean(ReportRender.class));
            fastDesignerServletAction.initReportProviders();
            listableBeanFactory.registerSingleton("fastDesignerServletAction",fastDesignerServletAction);

        }
    }

    /**
     * 动态注入Bean
     * @author ZhouZhou
     * @date 2021-09-19 11:31
     * @param <T>
     * @param factory
     * @param beanName
     * @param bean
     */
    public static <T> void registerBean(DefaultListableBeanFactory factory,String beanName, T bean) {
		//factory.autowireBean(bean);
		factory.registerSingleton(beanName, bean);
	}
}
