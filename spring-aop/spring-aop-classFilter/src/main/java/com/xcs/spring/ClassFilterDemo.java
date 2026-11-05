package com.xcs.spring;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.TypePatternClassFilter;
import org.springframework.aop.support.RootClassFilter;
import org.springframework.aop.support.annotation.AnnotationClassFilter;

public class ClassFilterDemo {
    public static void main(String[] args) {
        // Create an AnnotationClassFilter instance to match classes with the MyAnnotation annotation
        ClassFilter annotationClassFilter = new AnnotationClassFilter(MyClassAnnotation.class);
        System.out.println("annotationClassFilter matches =" + annotationClassFilter.matches(MyService.class));

        // Create a TypePatternClassFilter instance to match classes by class name
        ClassFilter typePatternClassFilter = new TypePatternClassFilter("com.xcs.spring.MyService");
        System.out.println("typePatternClassFilter matches =" + typePatternClassFilter.matches(MyService.class));

        // Create a RootClassFilter instance to match the root class of the given class
        ClassFilter rootClassFilter = new RootClassFilter(MyService.class);
        System.out.println("rootClassFilter matches = " + rootClassFilter.matches(MySubService.class));

        // Create an AspectJExpressionPointcut instance to match classes and methods by an AspectJ expression
        AspectJExpressionPointcut aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression("execution(* com.xcs.spring.MyService.*(..))");
        System.out.println("aspectJExpressionPointcut matches = " + aspectJExpressionPointcut.matches(MyService.class));
    }
}
