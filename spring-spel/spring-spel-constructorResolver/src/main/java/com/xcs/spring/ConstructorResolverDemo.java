package com.xcs.spring;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author xcs
 * @date 2024-03-12 11:28
 **/
public class ConstructorResolverDemo {
    public static void main(String[] args) {
        // Create a SpEL expression parser
        ExpressionParser parser = new SpelExpressionParser();

        // Parse the SpEL expression and create the object with the constructor
        // The expression is a constructor call that creates a MyBean with the argument 'spring-reading'
        MyBean myBean = parser.parseExpression("new com.xcs.spring.MyBean('spring-reading')").getValue(MyBean.class);

        // Print the created MyBean object
        System.out.println(myBean);
    }
}
