package com.root.mybatis.session.defaults;

import com.root.mybatis.binding.MapperRegistry;
import com.root.mybatis.session.Configuration;
import com.root.mybatis.session.SqlSession;
import com.root.mybatis.session.SqlSessionFactory;

/**
 * @author root
 * @description 默认的SqlSession工厂
 * @date 2023/6/10
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSqlSession() {
        return new DefaultSqlSession(configuration);
    }
}
