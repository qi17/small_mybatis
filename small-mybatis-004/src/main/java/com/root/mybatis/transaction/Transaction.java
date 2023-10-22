package com.root.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author root
 * @description 定义标准的事务接口，链接、提交、回滚、关闭，具体可以由不同的事务方式进行实现
 *              包括：JDBC和托管事务，托管事务是交给 Spring 这样的容器来管理。
 * @date 2023/10/8
 */
public interface Transaction {
    /**
     * 获取连接
     * @return
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * 提交
     * @throws SQLException
     */
    void commit() throws SQLException;

    /**
     * 回滚
     * @throws SQLException
     */
    void rollback() throws SQLException;

    /**
     * 关闭连接
     * @throws SQLException
     */
    void close() throws SQLException;
}
