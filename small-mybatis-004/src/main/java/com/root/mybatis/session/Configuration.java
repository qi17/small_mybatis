package com.root.mybatis.session;

import com.root.mybatis.binding.MapperRegistry;
import com.root.mybatis.datasource.druid.DruidDataSourceFactory;
import com.root.mybatis.datasource.pooled.PooledDatasourceFactory;
import com.root.mybatis.datasource.unpooled.UnPooledDataSourceFactory;
import com.root.mybatis.mapping.Environment;
import com.root.mybatis.mapping.MappedStatement;
import com.root.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.root.mybatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author root
 * @description
 * @date 2023/10/1
 */
public class Configuration {

    protected Environment environment;

    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    protected final  Map<String, MappedStatement> mappedStatementMap = new HashMap<>();

    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration() {
        typeAliasRegistry.registerTypeAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerTypeAlias("DRUID", DruidDataSourceFactory.class);
        typeAliasRegistry.registerTypeAlias("UNPOOLED", UnPooledDataSourceFactory.class);
        typeAliasRegistry.registerTypeAlias("POOLED", PooledDatasourceFactory.class);
    }

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

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
