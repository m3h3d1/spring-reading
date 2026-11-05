package com.xcs.spring.config;

import org.springframework.beans.factory.DisposableBean;

/**
 * @author xcs
 * @date 2023-09-19 16:42
 **/
public class MyDisposableBean implements DisposableBean {

    // Simulated database connection
    private String databaseConnection;

    public MyDisposableBean() {
        // Simulate opening a database connection in the constructor
        this.databaseConnection = "Database connection established";
        System.out.println(databaseConnection);
    }

    @Override
    public void destroy() throws Exception {
        // Simulate closing the database connection in the destroy method
        databaseConnection = null;
        System.out.println("Database connection closed");
    }
}
