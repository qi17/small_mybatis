import com.alibaba.fastjson.JSON;
import com.root.mybatis.builder.xml.XmlConfigBuilder;
import com.root.mybatis.io.Resources;
import com.root.mybatis.session.Configuration;
import com.root.mybatis.session.SqlSession;
import com.root.mybatis.session.SqlSessionFactory;
import com.root.mybatis.session.SqlSessionFactoryBuilder;
import com.root.mybatis.session.defaults.DefaultSqlSession;
import dao.IUserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import po.User;

import java.io.IOException;
import java.io.Reader;

/**
 * @author root
 * @description
 * @date 2023/10/3
 */
public class Test {
    private Logger logger =  LoggerFactory.getLogger(Test.class);


    @org.junit.Test
    public void test() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSqlSession();

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        User res = userDao.queryUserInfoById("10001");
        logger.info("测试结果：{}", res);

    }
    @org.junit.Test
    public void testSelectOne() throws IOException {
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder(reader);
        Configuration configuration = xmlConfigBuilder.parse();
        DefaultSqlSession sqlSession = new DefaultSqlSession(configuration);

        Object[] param = {1L};
        Object res = sqlSession.selectOne("dao.IUserDao.queryUserInfoById", param);
        logger.info("测试的结果是:{}", JSON.toJSON(res));
    }
}
