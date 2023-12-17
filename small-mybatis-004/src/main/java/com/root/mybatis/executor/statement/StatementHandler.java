package com.root.mybatis.executor.statement;

import com.root.mybatis.executor.resultset.ResultSetHandler;
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
public interface StatementHandler {


    Statement prepare(Connection connection) throws SQLException;


    void parameterize(Statement statement) throws SQLException ;

    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

}
