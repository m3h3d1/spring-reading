package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.controller.MessageController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-10-20 14:56
 **/
public class QualifierApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MessageController messageController = context.getBean(MessageController.class);
        messageController.showMessage();
    }
}
