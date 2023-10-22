package dao;

import po.User;

/**
 * @author root
 * @description
 * @date 2023/6/4
 */
public interface IUserDao {

    User queryUserInfoById(String uId);
}
