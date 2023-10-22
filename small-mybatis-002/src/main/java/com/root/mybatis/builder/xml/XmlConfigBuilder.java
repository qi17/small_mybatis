package com.root.mybatis.builder.xml;

import com.root.mybatis.builder.BaseBuilder;
import com.root.mybatis.io.Resources;
import com.root.mybatis.mapping.MappedStatement;
import com.root.mybatis.mapping.SqlCommandType;
import com.root.mybatis.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author root
 * @description
 * @date 2023/10/1
 */
public class XmlConfigBuilder extends BaseBuilder {

    private Element rootElement;

    public XmlConfigBuilder(Reader reader){
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


    public Configuration parse()   {
        try {
             mapperElement(rootElement.element("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
        return configuration;

    }

    private void mapperElement(Element mappers ) throws IOException, DocumentException, ClassNotFoundException {
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
                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType,
                        parameterType, resultType, sql, parameter).build();
                // 添加解析 SQL
                configuration.addMappedStatement(mappedStatement);

            }
            // 注册Mapper映射器
            configuration.addMapper(Resources.classForName(namespace));
        }
    }

}
