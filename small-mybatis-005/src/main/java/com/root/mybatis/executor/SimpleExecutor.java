package com.root.mybatis.executor;

import com.root.mybatis.executor.resultset.ResultSetHandler;
import com.root.mybatis.executor.statement.StatementHandler;
import com.root.mybatis.mapping.BoundSql;
import com.root.mybatis.mapping.MappedStatement;
import com.root.mybatis.session.Configuration;
import com.root.mybatis.session.ResultHandler;
import com.root.mybatis.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author root
 * @description
 * @date 2023/12/17
 */
public class SimpleExecutor extends BaseExecutor{
    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this,ms,parameter,resultHandler,boundSql);
            Connection connection = this.transaction.getConnection();
            Statement stmt = handler.prepare(connection);
            handler.parameterize(stmt);
            return handler.query(stmt, resultHandler);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
