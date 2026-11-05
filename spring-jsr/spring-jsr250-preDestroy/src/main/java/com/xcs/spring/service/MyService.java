package com.xcs.spring.service;

import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class MyService {

    public MyService(){
        System.out.println("Executing MyService constructor");
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println("Executing @PreDestroy method");
    }
}
