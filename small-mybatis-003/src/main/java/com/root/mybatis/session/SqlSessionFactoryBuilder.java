package com.root.mybatis.session;

import com.root.mybatis.builder.xml.XmlConfigBuilder;
import com.root.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * @author root
 * @description
 * @date 2023/10/1
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader){
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder(reader);
        return build( xmlConfigBuilder.parse());
    }

    //use default factory
    public SqlSessionFactory build(Configuration configuration){
       return new DefaultSqlSessionFactory(configuration);
    }
}
