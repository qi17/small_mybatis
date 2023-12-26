package com.root.mybatis.type;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author root
 * @description 类型别名注册器
 * @date 2023/10/8
 */
public class TypeAliasRegistry {

    private static final Map<String, Class<?>> TYPE_ALIAS = new HashMap<>();

    public TypeAliasRegistry() {
        // 构造函数里注册系统内置的类型别名
        registerTypeAlias("string", String.class);
        // 基本包装类型
        registerTypeAlias("byte", Byte.class);
        registerTypeAlias("long", Long.class);
        registerTypeAlias("short", Short.class);
        registerTypeAlias("int", Integer.class);
        registerTypeAlias("integer", Integer.class);
        registerTypeAlias("double", Double.class);
        registerTypeAlias("float", Float.class);
        registerTypeAlias("boolean", Boolean.class);

    }

    public void registerTypeAlias(String typeAlia, Class<?> clazz) {
        String lowerCaseAlia = typeAlia.toLowerCase(Locale.ENGLISH);
        TYPE_ALIAS.put(lowerCaseAlia, clazz);
    }

    public <T> Class<T> resolveTypeByAlias(String typeAlia) {
        String key = typeAlia.toLowerCase(Locale.ENGLISH);
        return (Class<T>) TYPE_ALIAS.get(key);

    }
}
