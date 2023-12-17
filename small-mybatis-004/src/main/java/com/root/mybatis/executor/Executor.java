package com.root.mybatis.executor;

import com.root.mybatis.executor.resultset.ResultSetHandler;
import com.root.mybatis.mapping.BoundSql;
import com.root.mybatis.mapping.MappedStatement;
import com.root.mybatis.session.ResultHandler;
import com.root.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * @author root
 * @description
 * @date 2023/12/17
 */
public interface Executor {


   ResultHandler NO_RESULT_HANDLER = null;

   <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql);

   Transaction getTransaction();
   void commit(boolean required) throws SQLException;
   void rollback(boolean required) throws SQLException;
   void close(boolean forceRollback) ;
}
