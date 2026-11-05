package com.xcs.spring.controller;

import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xcs
 * @date 2023-11-24 14:50
 **/
public class MyController {

    @Autowired
    private MyService myService;
}
