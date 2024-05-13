package com.atguigu.ioc_02;

/**
 * ClassName: UserService
 * Package: com.atguigu.ioc_02
 */
public class UserService {

    private UserDao userDao;
    private int age;
    private String userName;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserService(UserDao userDao, int age, String userName) {
        this.userDao = userDao;
        this.age = age;
        this.userName = userName;
    }
}