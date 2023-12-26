package com.root.mybatis.datasource.unpooled;

import com.root.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author root
 * @description
 * @date 2023/10/15
 */
public class UnPooledDataSourceFactory implements DataSourceFactory {
    protected Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        UnpooledDataSource unpooledDataSource = new UnpooledDataSource();
        unpooledDataSource.setDriver(properties.getProperty("driver"));
        unpooledDataSource.setUrl(properties.getProperty("url"));
        unpooledDataSource.setUserName(properties.getProperty("username"));
        unpooledDataSource.setPassword(properties.getProperty("password"));
        return unpooledDataSource;
    }
}
