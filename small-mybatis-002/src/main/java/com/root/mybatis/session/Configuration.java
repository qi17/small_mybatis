package com.root.mybatis.session;

import com.root.mybatis.binding.MapperRegistry;
import com.root.mybatis.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author root
 * @description
 * @date 2023/10/1
 */
public class Configuration {

    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    protected final  Map<String, MappedStatement> mappedStatementMap = new HashMap<>();


    public void addMappers(String packageName){
        mapperRegistry.addMappers(packageName);
    }

    public <T> void addMapper(Class<T> type){
        mapperRegistry.addMapper(type);
    }

    public <T> T  getMapper(Class<T> type,SqlSession sqlSession){
        return mapperRegistry.getMapper(type,sqlSession);
    }

    public <T>  boolean hasMapper(Class<T> type){
      return   mapperRegistry.hasMapper(type);
    }

    /**
     * mappedStatementMap其id是[方法名] value是mappedStatement
     * @param ms
     */
    public void addMappedStatement(MappedStatement ms) {
        mappedStatementMap.put(ms.getId(), ms);
    }


    public MappedStatement getMappedStatement(String id ){
        return mappedStatementMap.get(id);
    }
}
