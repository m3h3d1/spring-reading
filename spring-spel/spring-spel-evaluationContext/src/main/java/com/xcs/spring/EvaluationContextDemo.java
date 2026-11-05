package com.xcs.spring;

import org.springframework.expression.*;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

/**
 * @author xcs
 * @date 2024-03-12 11:28
 **/
public class EvaluationContextDemo {
    public static void main(String[] args) {
        // Create the evaluation context
        EvaluationContext context = new StandardEvaluationContext(new MyRootObject("Root Data"));

        // Get the root object
        TypedValue root = context.getRootObject();
        System.out.println("Root object: " + root.getValue());

        // Set a variable
        context.setVariable("name", "John");
        System.out.println("Value of variable 'name': " + context.lookupVariable("name"));

        // Get the property accessors
        List<PropertyAccessor> propertyAccessors = context.getPropertyAccessors();
        System.out.println("Property accessors: " + propertyAccessors);

        // Get the constructor resolvers
        List<ConstructorResolver> constructorResolvers = context.getConstructorResolvers();
        System.out.println("Constructor resolvers: " + constructorResolvers);

        // Get the method resolvers
        List<MethodResolver> methodResolvers = context.getMethodResolvers();
        System.out.println("Method resolvers: " + methodResolvers);

        // Get the bean resolver
        BeanResolver beanResolver = context.getBeanResolver();
        System.out.println("Bean resolver: " + beanResolver);

        // Get the type locator
        TypeLocator typeLocator = context.getTypeLocator();
        System.out.println("Type locator: " + typeLocator);

        // Get the type converter
        TypeConverter typeConverter = context.getTypeConverter();
        System.out.println("Type converter: " + typeConverter);

        // Get the type comparator
        TypeComparator typeComparator = context.getTypeComparator();
        System.out.println("Type comparator: " + typeComparator);

        // Get the operator overloader
        OperatorOverloader operatorOverloader = context.getOperatorOverloader();
        System.out.println("Operator overloader: " + operatorOverloader);
    }

    // Class of the root object
    static class MyRootObject {
        private String data;

        public MyRootObject(String data) {
            this.data = data;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
