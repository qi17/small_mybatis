package com.root.mybatis.reflection.invoker;

import java.lang.reflect.Field;

/**
 * getter 方法的调用者处理
 * @author root
 * @description
 * @date 2023/12/27
 */
public class SetFieldInvoker implements Invoker{
    private Class<?> type;
    private Field field;

    public SetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
         field.set(target,args[0]);
         return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
