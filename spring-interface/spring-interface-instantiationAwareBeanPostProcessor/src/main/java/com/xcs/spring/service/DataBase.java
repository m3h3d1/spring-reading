package com.xcs.spring.service;

/**
 * @author xcs
 * @date 2023-09-16 16:10
 **/
public interface DataBase {
    String getUsername();
    void setUsername(String username);
    String getPassword();
    void setPassword(String password);
    boolean isPostInstantiationFlag();
    void setPostInstantiationFlag(boolean flag);
}
