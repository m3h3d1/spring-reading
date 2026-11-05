package com.xcs.spring.service;

/**
 * @author xcs
 * @date 2023-09-18 17:30
 **/
public class ConnectionServiceImpl implements ConnectionService {

    private boolean isConnected = false;

    @Override
    public void openConnection() {
        isConnected = true;
        System.out.println("connection opened.");
    }

    @Override
    public void closeConnection() {
        if (isConnected) {
            isConnected = false;
            System.out.println("connection closed.");
        }
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }
}
