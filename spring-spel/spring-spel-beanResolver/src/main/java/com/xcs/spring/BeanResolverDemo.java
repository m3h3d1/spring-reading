package com.xcs.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class BeanResolverDemo {
    public static void main(String[] args) {
        // Create a BeanFactory
        // Here the BeanFactory is created from annotation configuration
        BeanFactory beanFactory = new AnnotationConfigApplicationContext(MyBean.class).getBeanFactory();

        // Create a SpEL expression parser
        ExpressionParser parser = new SpelExpressionParser();

        // Create a standard evaluation context
        StandardEvaluationContext context = new StandardEvaluationContext();
        // Set BeanFactoryResolver as the BeanResolver of the context
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));

        // Parse the SpEL expression to get the bean instance
        Object myBean = parser.parseExpression("@myBean").getValue(context);

        // Print the bean instance
        System.out.println("myBean = " + myBean);
    }
}
