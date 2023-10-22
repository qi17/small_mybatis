package com.root.mybatis.transaction.jdbc;

import cn.hutool.db.transaction.TransactionLevel;
import com.root.mybatis.session.TransactionIsolationLevel;
import com.root.mybatis.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author root
 * @description jdbc的事务
 * @date 2023/10/8
 */
public class JdbcTransaction implements Transaction {

    protected Connection connection;

    protected DataSource dataSource;

    protected boolean autoCommit;

    protected TransactionIsolationLevel level = TransactionIsolationLevel.NONE;

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        this.level = level;
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        //根据数据源获取连接
        connection = dataSource.getConnection();
        //设置隔离级别和自动提交
        connection.setTransactionIsolation(level.getLevel());
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        //连接不是自动提交
        if(connection != null && !connection.getAutoCommit()){
            connection.commit();
        }

    }

    @Override
    public void rollback() throws SQLException {
        //连接不是自动提交
        if(connection != null && !connection.getAutoCommit()){
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        //连接不是自动提交
        if (connection != null && !connection.getAutoCommit()) {
            connection.close();
        }

    }
}
