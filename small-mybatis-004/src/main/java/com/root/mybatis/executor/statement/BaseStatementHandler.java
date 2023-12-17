package com.root.mybatis.executor.statement;

import com.root.mybatis.executor.Executor;
import com.root.mybatis.executor.resultset.ResultSetHandler;
import com.root.mybatis.mapping.BoundSql;
import com.root.mybatis.mapping.MappedStatement;
import com.root.mybatis.session.Configuration;
import com.root.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author root
 * @description
 * @date 2023/12/17
 */
public abstract class BaseStatementHandler implements StatementHandler {
    protected final Configuration configuration;
    protected final Executor executor;
    protected final MappedStatement mappedStatement;

    protected final Object parameterObject;
    protected final ResultSetHandler resultSetHandler;

    protected BoundSql boundSql;

    public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler, BoundSql boundSql) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.boundSql = boundSql;

        this.parameterObject = parameterObject;
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, boundSql);
    }


    @Override
    public Statement prepare(Connection connection) throws SQLException {
        Statement statement = null;
        try {
            //实例化
            statement = instantiateStatement(connection);
            //参数配置组装
            statement.setQueryTimeout(350);
            statement.setFetchSize(10000);
            return statement;
        } catch (Exception e) {
            throw new RuntimeException("Error preparing statement.  Cause: " + e, e);
        }
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;


}
