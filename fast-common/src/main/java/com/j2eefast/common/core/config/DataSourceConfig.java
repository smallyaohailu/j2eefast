/*
 * All content copyright http://www.j2eefast.com, unless 
 * otherwise indicated. All rights reserved.
 * No deletion without permission
 */
package com.j2eefast.common.core.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.j2eefast.common.core.config.properties.DruidProperties;
import com.j2eefast.common.core.io.PropertiesUtils;
import com.j2eefast.common.core.mutidatasource.annotaion.aop.MultiSourceAop;
import com.j2eefast.common.core.utils.ToolUtil;
import com.j2eefast.common.db.context.DataSourceContext;
import com.j2eefast.common.db.factory.AtomikosFactory;
import org.apache.shardingsphere.api.config.masterslave.LoadBalanceStrategyConfiguration;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p>基于数据库多数据源配置</p>
 *
 * @author: zhouzhou
 * @date: 2020-04-15 13:52
 * @web: http://www.j2eefast.com
 * @version: 1.0.1
 */
@Configuration
public class DataSourceConfig {

	@Value("#{ @environment['fast.jta.enabled'] ?: false }")
	private boolean enabled;

	// ------------------- 读写分离配置 ------ //
	@Value("#{ @environment['spring.datasource.slaveDb.names'] ?: null }")
	private String slaveNames;
	@Value("#{ @environment['spring.datasource.slaveDb.sqlShow'] ?: null }")
	private String sqlshow;
	@Value("#{ @environment['spring.datasource.slaveDb.loadBalanceAlgorithmType'] ?: null }")
	private String loadBalanceAlgorithmType;
	// ----------------------------------------//

	/**
	 * 默认数据库配置
	 */
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.master")
    public DruidProperties defaultProperties() {
        return new DruidProperties();
    }


	/**
	 * 主数据源实例
	 */
	@Primary
	@Bean("dataSourcePrimary")
	public DataSource dataSourcePrimary(@Qualifier("defaultProperties") DruidProperties druidProperties) throws SQLException {
		if(enabled){
			return AtomikosFactory.create(DataSourceContext.MASTER_DATASOURCE_NAME, druidProperties);
		}else {

			DruidDataSource dataSource0 = new DruidDataSource();
			druidProperties.config(dataSource0);

			if(ToolUtil.isNotEmpty(slaveNames)){
				// 配置真实数据源
				Map<String, DataSource> dataSourceMap = new HashMap<>();
				dataSourceMap.put("MASTER",dataSource0);
				List<String> slaveDataSourceNames = StrUtil.splitTrim(slaveNames, StrUtil.C_COMMA);
				slaveDataSourceNames.forEach(e->{
					DruidDataSource slavedataSource = new DruidDataSource();
					DruidProperties tempDruidProperties = new DruidProperties();
					tempDruidProperties.setUsername(PropertiesUtils.getInstance().getProperty("spring.datasource."+e+".username"));
					tempDruidProperties.setUrl(PropertiesUtils.getInstance().getProperty("spring.datasource."+e+".url"));
					tempDruidProperties.setDriverClassName(PropertiesUtils.getInstance().getProperty("spring.datasource."+e+".driverClassName"));
					tempDruidProperties.setPassword(PropertiesUtils.getInstance().getProperty("spring.datasource."+e+".password"));
					tempDruidProperties.setFilters(PropertiesUtils.getInstance().getProperty("spring.datasource."+e+".filters"));
					tempDruidProperties.setDataSourceName(e);
					tempDruidProperties.config(slavedataSource);
					dataSourceMap.put(e,slavedataSource);
				});
				// 配置读写分离规则
				MasterSlaveRuleConfiguration masterSlaveRuleConfig = new MasterSlaveRuleConfiguration("DB_MASTER", "MASTER", slaveDataSourceNames,
						ToolUtil.isEmpty(loadBalanceAlgorithmType)? null: new LoadBalanceStrategyConfiguration(loadBalanceAlgorithmType));
				Properties prop = new Properties();
				prop.put("sql.show",StrUtil.nullToDefault(sqlshow,String.valueOf(Boolean.FALSE)));
				// 获取数据源对象
				return MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveRuleConfig, prop);

			}
			return dataSource0;
		}
	}

//
//	/**
//	 * flowable 数据库
//	 * @return
//	 */
//	@Bean
//	@ConditionalOnProperty(prefix = "fast.flowable", name = "enabled", havingValue = "true")
//	@ConfigurationProperties(prefix = "spring.datasource.flowable")
//	public DruidProperties flowableProperties() {
//		return new DruidProperties();
//	}
//
//
//	/**
//	 * flowable数据源实例
//	 */
//	@Bean("flowableSourcePrimary")
//	@ConditionalOnProperty(prefix = "fast.flowable", name = "enabled", havingValue = "true")
//	public DataSource flowableSourcePrimary(@Qualifier("flowableProperties") DruidProperties flowableProperties) {
//		if(enabled){
//			return AtomikosFactory.create(DataSourceContext.FLOWABLE_DATASOURCE_NAME, flowableProperties);
//		}else{
//			DruidDataSource dataSource = new DruidDataSource();
//			flowableProperties.config(dataSource);
//			return dataSource;
//		}
//	}


	/**
	 * 多数据源切换的aop
	 */
	@Bean
	public MultiSourceAop multiSourceExAop() {
		return new MultiSourceAop();
	}

}
