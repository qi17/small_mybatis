package com.root.mybatis.session.defaults;

import com.root.mybatis.binding.MapperRegistry;
import com.root.mybatis.mapping.MappedStatement;
import com.root.mybatis.session.Configuration;
import com.root.mybatis.session.SqlSession;

/**
 * @author root
 * @description 默认的SqlSession
 * @date 2023/6/10
 */
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;


    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了！" + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        //根据方法名获取mappedstatement
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        return (T) ("你被代理了！" + "方法：" + statement + " 入参：" + parameter + "\n待执行SQL：" + mappedStatement.getSql());
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }


}
