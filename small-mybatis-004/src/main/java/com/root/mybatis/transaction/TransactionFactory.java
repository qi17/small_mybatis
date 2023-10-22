package com.root.mybatis.transaction;

import cn.hutool.db.transaction.TransactionLevel;
import com.root.mybatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author root
 * @description 事务工厂
 * @date 2023/10/8
 */
public interface TransactionFactory {

    Transaction newTransaction(Connection connection);

    /**
     * 事务工厂创建事务
     * @param dataSource 数据源
     * @param level 事务隔离级别
     * @param autoCommit 自动提交
     * @return
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
}
