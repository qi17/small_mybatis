package com.root.mybatis.mapping;

import com.root.mybatis.session.Configuration;
import com.root.mybatis.type.JdbcType;

/**
 * @author root
 * @description
 * @date 2023/10/13
 */
public class ParameterMapping {

    private Configuration configuration;

    private String property;

    private Class<?> javaType = Object.class;

    private JdbcType jdbcType;

    public  static  class Builder{

        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration,String property){
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
        }
        public Builder javaType(Class<?> javaType) {
            parameterMapping.javaType = javaType;
            return this;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            parameterMapping.jdbcType = jdbcType;
            return this;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }
    public String getProperty() {
        return property;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }


}
