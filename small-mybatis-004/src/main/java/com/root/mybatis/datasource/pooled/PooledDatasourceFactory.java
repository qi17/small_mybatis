package com.root.mybatis.datasource.pooled;

import com.root.mybatis.datasource.unpooled.UnPooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * @author root
 * @description
 * @date 2023/10/21
 */
public class PooledDatasourceFactory extends UnPooledDataSourceFactory {

    @Override
    public DataSource getDataSource() {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver(properties.getProperty("driver"));
        pooledDataSource.setUrl(properties.getProperty("url"));
        pooledDataSource.setUsername(properties.getProperty("username"));
        pooledDataSource.setPassword(properties.getProperty("password"));
        return pooledDataSource;
    }

}
