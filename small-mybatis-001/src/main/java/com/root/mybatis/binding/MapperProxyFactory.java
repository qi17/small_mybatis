package com.root.mybatis.binding;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author root
 * @description
 * @date 2023/6/4
 */
public class MapperProxyFactory<T> {
    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(Map<String, String> sqlSession) {
        MapperProxy<T> invocationHandler = new MapperProxy<>(sqlSession, mapperInterface);
        //创建代理对象
        return (T)Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[]{mapperInterface},
                invocationHandler);

    }


}
