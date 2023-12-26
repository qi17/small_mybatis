package com.root.mybatis.session;

/**
 * @author root
 * @description
 * @date 2023/6/10
 */
public interface SqlSessionFactory {

    /**
     * 打开 sqlSession
     * @return sqlSession
     */
    SqlSession openSqlSession();
}
