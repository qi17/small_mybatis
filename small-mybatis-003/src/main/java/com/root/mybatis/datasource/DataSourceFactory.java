package com.root.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @author root
 * @description
 * @date 2023/10/12
 */
public interface DataSourceFactory {

    void setProperties(Properties properties);

    DataSource getDataSource();

}
