package com.root.mybatis.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.root.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author root
 * @description druid数据源
 * @date 2023/10/12
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    private Properties properties;

    @Override
    public void setProperties(Properties properties) {
            this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        //jdbc的操作
        druidDataSource.setDriverClassName(properties.getProperty("driver"));
        druidDataSource.setUrl(properties.getProperty("url"));
        druidDataSource.setUsername(properties.getProperty("username"));
        druidDataSource.setPassword(properties.getProperty("password"));
        return druidDataSource;
    }
}
