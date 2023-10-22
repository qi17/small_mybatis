package dao;

/**
 * @author root
 * @description
 * @date 2023/6/4
 */
public interface IUserDao {
    String queryUserName(String uId);

    Integer queryUserAge(String uId);

}
