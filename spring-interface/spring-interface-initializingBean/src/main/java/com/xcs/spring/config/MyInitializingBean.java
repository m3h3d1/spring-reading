package com.xcs.spring.config;

import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xcs
 * @date 2023-09-19 16:42
 **/
public class MyInitializingBean implements InitializingBean {

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    @Override
    public void afterPropertiesSet() {
        // In this method, we simulate loading data
        data = new ArrayList<>();
        data.add("Data 1");
        data.add("Data 2");
        data.add("Data 3");
        System.out.println("MyInitializingBean initialized, data loaded!");
    }
}
