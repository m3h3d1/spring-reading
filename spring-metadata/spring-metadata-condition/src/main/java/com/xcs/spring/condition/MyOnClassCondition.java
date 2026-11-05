package com.xcs.spring.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author xcs
 * @date 2023-11-20 15:24
 **/
public class MyOnClassCondition implements Condition {

    private final String className;

    public MyOnClassCondition(String className) {
        this.className = className;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            // Try to load the class
            getClass().getClassLoader().loadClass(className);
            // The class exists, so the condition matches
            return true;
        } catch (ClassNotFoundException e) {
            // The class does not exist, so the condition does not match
            return false;
        }
    }
}
