package com.root.mybatis.reflection.invoker;

import java.lang.reflect.Method;

/**
 * @author root
 * @description
 * @date 2023/12/27
 */
public class MethodInvoker implements Invoker{

    private Class<?> type;

    private Method method;

    public MethodInvoker(Method method) {
        this.method = method;
        //如果方法的形参列表长度为1，表示当前方法是set方法，故其type为形参的类型
        if(method.getParameterTypes().length==1){
            type = method.getParameterTypes()[0];
        }else {
            //形参列表不为1则为get方法，其type为方法返回类型
            type = method.getReturnType();
        }

    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        return method.invoke(target,args);
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}
