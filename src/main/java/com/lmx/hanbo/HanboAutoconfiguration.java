package com.lmx.hanbo;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(AutoSelectivePrimaryService.class)
@EnableConfigurationProperties(HanboProperties.class)
public class HanboAutoconfiguration {
    @Autowired
    private HanboProperties hanboProperties;

    @Bean
    @ConditionalOnMissingBean
    AutoSelectivePrimaryService autoSelectivePrimaryService() {
        return new AutoSelectivePrimaryService(hanboProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    DruidDataSource datasource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(hanboProperties.getDriverClass());
        druidDataSource.setUsername(hanboProperties.getUserName());
        druidDataSource.setPassword(hanboProperties.getPassWord());
        druidDataSource.setUrl(hanboProperties.getReplicationUrl());
        druidDataSource.setMaxActive(8);
        return druidDataSource;
    }

}
