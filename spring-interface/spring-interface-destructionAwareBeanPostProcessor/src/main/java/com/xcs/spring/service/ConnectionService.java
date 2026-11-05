package com.xcs.spring.service;

/**
 * @author xcs
 * @date 2023-09-18 17:29
 **/
public interface ConnectionService {

    void openConnection();

    void closeConnection();

    boolean isConnected();
}
