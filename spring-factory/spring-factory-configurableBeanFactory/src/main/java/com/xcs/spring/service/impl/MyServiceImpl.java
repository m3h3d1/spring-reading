package com.xcs.spring.service.impl;

import com.xcs.spring.service.MyService;

/**
 * @author xcs
 * @date 2023-11-24 14:17
 **/
public class MyServiceImpl implements MyService {
    @Override
    public void greet() {
        System.out.println("Hello from MyService!");
    }
}
