package com.root.mybatis.executor;

import com.root.mybatis.executor.resultset.ResultSetHandler;
import com.root.mybatis.mapping.BoundSql;
import com.root.mybatis.mapping.MappedStatement;
import com.root.mybatis.session.Configuration;
import com.root.mybatis.session.ResultHandler;
import com.root.mybatis.transaction.Transaction;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * @author root
 * @description
 * @date 2023/12/17
 */
public abstract class  BaseExecutor  implements Executor{
    private org.slf4j.Logger logger = LoggerFactory.getLogger(BaseExecutor.class);

    protected Configuration configuration;

    protected Transaction transaction;

    private Executor wrapper;

    private boolean closed;

    protected BaseExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        wrapper= this;
    }


    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
      if(closed){
          throw new RuntimeException("Executor was closed.");
      }
      return doQuery(ms,parameter,resultHandler,boundSql);
    }

    @Override
    public Transaction getTransaction() {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        return transaction;
    }

    /**
     * 由子类实现模板方法
     * @param ms
     * @param parameter
     * @param resultHandler
     * @param boundSql
     * @return
     * @param <E>
     */
    protected abstract   <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) ;

    @Override
    public void commit(boolean required) throws SQLException {
        if(closed){
            throw new RuntimeException("Executor was closed.");
        }
        if(required){
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if (!closed) {
            if (required) {
                transaction.rollback();
            }
        }

    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try {
                rollback(forceRollback);
            } finally {
                transaction.close();
            }
        } catch (SQLException e) {
            logger.warn("Unexpected exception on closing transaction.  Cause: " + e);
        } finally {
            transaction = null;
            closed = true;
        }
    }
}
