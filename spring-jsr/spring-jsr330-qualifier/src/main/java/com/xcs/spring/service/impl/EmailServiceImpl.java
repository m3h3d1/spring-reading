package com.xcs.spring.service.impl;

import com.xcs.spring.annotation.Email;
import com.xcs.spring.service.MessageService;

import javax.inject.Named;

/**
 * @author xcs
 * @date 2023-10-20 14:57
 **/
@Email
@Named
public class EmailServiceImpl implements MessageService {
    @Override
    public String getMessage() {
        return "Email message";
    }
}
