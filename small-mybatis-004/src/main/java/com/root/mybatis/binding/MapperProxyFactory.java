package com.root.mybatis.binding;

import com.root.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author root
 * @description mapper代理对象工厂
 * @date 2023/6/4
 */
public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    private Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<Method, MapperMethod>();

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * 创建代理对象
     * @param sqlSession
     * @return
     */
    public T newInstance(SqlSession sqlSession) {
        MapperProxy<T> invocationHandler = new MapperProxy<>(sqlSession, mapperInterface,methodCache);
        //创建代理对象
        return (T)Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},
                invocationHandler);
    }


}
