package com.root.mybatis.builder.xml;

import com.root.mybatis.builder.BaseBuilder;
import com.root.mybatis.datasource.DataSourceFactory;
import com.root.mybatis.io.Resources;
import com.root.mybatis.mapping.BoundSql;
import com.root.mybatis.mapping.Environment;
import com.root.mybatis.mapping.MappedStatement;
import com.root.mybatis.mapping.SqlCommandType;
import com.root.mybatis.session.Configuration;
import com.root.mybatis.transaction.TransactionFactory;
import com.root.mybatis.type.TypeAliasRegistry;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author root
 * @description
 * @date 2023/10/1
 */
public class XmlConfigBuilder extends BaseBuilder {

    private Element rootElement;


    public XmlConfigBuilder(Reader reader) {
        //初始化Configuration
        super(new Configuration());
        //使用dom4j 解析
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(reader);
            rootElement = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    public Configuration parse() {
        try {
            environmentsElement(rootElement.element("environments"));

            mapperElement(rootElement.element("mappers"));

        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
        return configuration;

    }

    private void environmentsElement(Element context) throws Exception {
        String env = context.attributeValue("default");

        List<Element> elementList = context.elements("environment");
        for (Element e : elementList) {
            //环境的id属性值 一般用于标识为开发？测试？生产？
            String id = e.attributeValue("id");
            if (env.equals(id)) {
                TransactionFactory transactionFactory = (TransactionFactory) typeAliasRegistry.resolveTypeByAlias
                                (e.element("transactionManager").attributeValue("type")).newInstance();
                Element datasource = e.element("dataSource");
                DataSourceFactory datasourceFactory = (DataSourceFactory) typeAliasRegistry.resolveTypeByAlias(datasource.attributeValue("type")).newInstance();
                Properties props = new Properties();
                List<Element> propList = datasource.elements("property");
                for (Element prop : propList) {
                    props.setProperty(prop.attributeValue("name"), prop.attributeValue("value"));
                }
                datasourceFactory.setProperties(props);
                DataSource dataSource = datasourceFactory.getDataSource();
                Environment.Builder builder = new Environment.Builder(id).transactionFactory(transactionFactory).dataSource(dataSource);
                configuration.setEnvironment(builder.build());
            }
        }
    }

    private void mapperElement(Element mappers) throws IOException, DocumentException, ClassNotFoundException {
        List<Element> mapper = mappers.elements("mapper");
        for (Element e : mapper) {
            String resource = e.attributeValue("resource");
            Reader reader = Resources.getResourceAsReader(resource);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new InputSource(reader));
            Element root = document.getRootElement();
            String namespace = root.attributeValue("namespace");
            // SELECT
            List<Element> selectNodes = root.elements("select");
            for (Element node : selectNodes) {
                String methodId = node.attributeValue("id");
                String parameterType = node.attributeValue("parameterType");
                String resultType = node.attributeValue("resultType");
                // ? 匹配
                Map<Integer, String> parameter = new HashMap<>();

                String sql = node.getText();
                Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                Matcher matcher = pattern.matcher(sql);
                //处理占位符
                for (int i = 1; matcher.find(); i++) {
                    String g1 = matcher.group(1);
                    String g2 = matcher.group(2);
                    parameter.put(i, g2);
                    sql = sql.replace(g1, "?");
                }

                String msId = namespace + "." + methodId;
                String nodeName = node.getName();
                SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
                BoundSql boundSql = new BoundSql(sql, parameter, parameterType, resultType);
                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType, boundSql).build();
                // 添加解析 SQL
                configuration.addMappedStatement(mappedStatement);
            }
            // 注册Mapper映射器
            configuration.addMapper(Resources.classForName(namespace));
        }
    }

}
