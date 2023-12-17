package com.root.mybatis.session.defaults;

import com.root.mybatis.executor.Executor;
import com.root.mybatis.mapping.BoundSql;
import com.root.mybatis.mapping.Environment;
import com.root.mybatis.mapping.MappedStatement;
import com.root.mybatis.session.Configuration;
import com.root.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author root
 * @description 默认的SqlSession
 * @date 2023/6/10
 */
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;
    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }


    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return this.selectOne(statement,null);
    }

    @Override
    public <T> T selectOne(String statementId, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statementId);
        List<T> list = executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getBoundSql());
        return list.get(0);
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
