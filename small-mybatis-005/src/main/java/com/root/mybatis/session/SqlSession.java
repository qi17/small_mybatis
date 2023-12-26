package com.root.mybatis.session;

/**
 * @author root
 * @description
 * @date 2023/6/10
 */
public interface SqlSession {

    /**
     * 根据指定的sqlId获取一条记录的封装对象
     * @param statement sqlId
     * @return 封装后的对象
     * @param <T> 封装之后的对象类型
     */
    <T> T selectOne(String statement);

    /**
     * 根据指定的sqlId和参数获取一条记录的封装对象
     * @param statement
     * @param parameter
     * @return
     * @param <T>
     */
    <T> T selectOne(String statement,Object parameter);

    /**
     * 获取mapper映射器
     */
    <T> T getMapper(Class<T> type );


     Configuration getConfiguration();
}
