package com.xcs.spring;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.List;

public class PropertyAccessorDemo {
    public static void main(String[] args)  {
        // Create a SpEL expression parser
        ExpressionParser parser = new SpelExpressionParser();

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("myBean",new MyBean("spring-reading"));

        // Parse the SpEL expression and create the object with the constructor
        String name = parser.parseExpression("#myBean.name").getValue(context, String.class);

        System.out.println("name = " + name);
    }
}
