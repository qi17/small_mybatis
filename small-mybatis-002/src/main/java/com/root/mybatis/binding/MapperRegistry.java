package com.root.mybatis.binding;

import cn.hutool.core.lang.ClassScanner;
import com.root.mybatis.session.Configuration;
import com.root.mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author root
 * @description mapper注册器
 * @date 2023/6/10
 */
public class MapperRegistry {
    private Configuration config;

    public MapperRegistry(Configuration config) {
        this.config = config;
    }

    private final Map<Class<?>,  MapperProxyFactory<?> > registryMapper = new HashMap<>();

    public <T> T getMapper(Class<T> type, SqlSession sqlSession){
       final  MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) registryMapper.get(type);
        if (mapperProxyFactory==null) {
            throw new RuntimeException("type " + type +"is not known to the MapperRegistry");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    public <T> void addMapper(Class<T> type){
        if (type.isInterface()) {
            if(hasMapper(type)){
                // 如果重复添加了，报错
                throw new RuntimeException("Type " + type + " is already known to the MapperRegistry.");
            }
            registryMapper.put(type,new MapperProxyFactory<>(type));
        }

    }

    public void addMappers(String packageName){
        Set<Class<?>> mapperSet = ClassScanner.scanPackage(packageName);
        for (Class<?> clazz : mapperSet) {
            addMapper(clazz);
        }
    }

    public <T> boolean hasMapper(Class<T> type) {
        return registryMapper.containsKey(type);
    }
}
