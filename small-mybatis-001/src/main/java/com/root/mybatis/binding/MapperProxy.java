package com.root.mybatis.binding;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author root
 * @description
 * @date 2023/6/4
 */
public class MapperProxy<T> implements InvocationHandler , Serializable {
    public final Long  serialVersionUID = -6424540398559729838L;
    private Map<String,String > sqlSession;
    //代理的接口
    private Class<T> mapperInterface;

    public MapperProxy(Map<String, String> sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(proxy.getClass())){
            return method.invoke(this,args) ;
        }
       return "你的被代理了!" + sqlSession.get(mapperInterface.getName()+"."+method.getName());
    }
}
