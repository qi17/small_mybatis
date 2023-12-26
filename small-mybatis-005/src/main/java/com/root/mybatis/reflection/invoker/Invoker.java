package com.root.mybatis.reflection.invoker;

/**
 * @author root
 * @description
 * @date 2023/12/27
 */
public interface Invoker {

    Object invoke(Object target, Object[] args) throws Exception;

    Class<?> getType();
}
