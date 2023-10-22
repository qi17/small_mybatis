import com.root.mybatis.io.Resources;
import com.root.mybatis.session.SqlSession;
import com.root.mybatis.session.SqlSessionFactory;
import com.root.mybatis.session.SqlSessionFactoryBuilder;
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
        String res = userDao.queryUserInfoById("10001");
        logger.info("测试结果：{}", res);

    }
}
