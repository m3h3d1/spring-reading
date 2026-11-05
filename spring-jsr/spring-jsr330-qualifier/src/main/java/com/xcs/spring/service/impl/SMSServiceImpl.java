package com.xcs.spring.service.impl;

import com.xcs.spring.annotation.SMS;
import com.xcs.spring.service.MessageService;

import javax.inject.Named;

/**
 * @author xcs
 * @date 2023-10-20 14:57
 **/
@SMS
@Named
public class SMSServiceImpl implements MessageService {
    @Override
    public String getMessage() {
        return "SMS message";
    }
}
