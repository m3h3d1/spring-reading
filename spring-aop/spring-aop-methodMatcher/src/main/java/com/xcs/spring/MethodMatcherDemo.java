package com.xcs.spring;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;

import java.lang.reflect.Method;

public class MethodMatcherDemo {

    public static void main(String[] args) throws Exception {
        Class<MyService> target = MyService.class;
        Method setNameMethod = target.getDeclaredMethod("setName");

        // Use AnnotationMethodMatcher to check for a specific annotation
        AnnotationMethodMatcher annotationMethodMatcher = new AnnotationMethodMatcher(MyMethodAnnotation.class);
        System.out.println("annotationMethodMatcher matches = " + annotationMethodMatcher.matches(setNameMethod, target));

        // Use AspectJExpressionPointcut to match the method with an AspectJ expression
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression("execution(* com.xcs.spring.MyService.*(..))");
        System.out.println("aspectJExpressionPointcut matches = " + aspectJExpressionPointcut.matches(setNameMethod, target));

        // Use NameMatchMethodPointcut to match the method by name
        NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
        nameMatchMethodPointcut.setMappedName("setName");
        System.out.println("nameMatchMethodPointcut matches = " + nameMatchMethodPointcut.matches(setNameMethod, target));

        // Use JdkRegexpMethodPointcut to match the method with a regular expression
        JdkRegexpMethodPointcut jdkRegexpMethodPointcut = new JdkRegexpMethodPointcut();
        jdkRegexpMethodPointcut.setPattern(".*set.*");
        System.out.println("jdkRegexpMethodPointcut matches = " + jdkRegexpMethodPointcut.matches(setNameMethod, target));
    }
}
