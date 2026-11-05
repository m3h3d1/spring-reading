package com.xcs.spring.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MyService {

    public MyService(){
        System.out.println("Executing MyService constructor");
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("Executing @PostConstruct method");
    }
}
