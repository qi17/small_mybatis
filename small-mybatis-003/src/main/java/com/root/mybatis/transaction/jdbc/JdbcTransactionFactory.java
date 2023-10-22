package com.root.mybatis.transaction.jdbc;

import cn.hutool.db.transaction.TransactionLevel;
import com.root.mybatis.session.TransactionIsolationLevel;
import com.root.mybatis.transaction.Transaction;
import com.root.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author root
 * @description
 * @date 2023/10/8
 */
public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection connection) {
        return new JdbcTransaction(connection);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource,level,autoCommit);
    }
}
