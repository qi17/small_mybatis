package com.root.mybatis.session.defaults;

import com.root.mybatis.binding.MapperRegistry;
import com.root.mybatis.executor.Executor;
import com.root.mybatis.mapping.Environment;
import com.root.mybatis.session.Configuration;
import com.root.mybatis.session.SqlSession;
import com.root.mybatis.session.SqlSessionFactory;
import com.root.mybatis.session.TransactionIsolationLevel;
import com.root.mybatis.transaction.Transaction;
import com.root.mybatis.transaction.TransactionFactory;

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
        Environment environment = configuration.getEnvironment();
        TransactionFactory transactionFactory = environment.getTransactionFactory();
        Transaction tx = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, false);
        final Executor executor = configuration.newExecutor(tx);
        return  new DefaultSqlSession(configuration,executor);
    }
}
