package com.root.mybatis.builder;

import com.root.mybatis.session.Configuration;
import com.root.mybatis.type.TypeAliasRegistry;

/**
 * @author root
 * @description
 * @date 2023/10/1
 */
public class BaseBuilder {

    /**
     *  final修饰局部变量时，在使用之前必须被赋值一次才能使用；
     *  final修饰成员变量时，如果在声明时没有赋值，则叫做“空白final变量”，空白final变量必须在构造方法或静态代码块中进行初始化。
     * 根据修饰变量的数据类型，比如在修饰基本类型和引用类型的变量时，final也有不同的特性：
     *  ● final修饰基本类型的变量时，不能把基本类型的值重新赋值，因此基本类型的变量值不能被改变。
     *  ● final修饰引用类型的变量时，final只会保证引用类型的变量所引用的地址不会改变，即保证该变量会一直引用同一个对象。
     *  因为引用类型的变量保存的仅仅是一个引用地址，所以final修饰引用类型的变量时，该变量会一直引用同一个对象，
     * 但这个对象本身的成员和数据是完全可以发生改变的。
     */

    protected  final Configuration configuration;

    protected final TypeAliasRegistry typeAliasRegistry;


    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
