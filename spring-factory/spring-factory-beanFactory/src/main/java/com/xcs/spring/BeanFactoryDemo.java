package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.ResolvableType;

/**
 * @author xcs
 * @date 2023-11-23 19:17
 **/
public class BeanFactoryDemo {

    public static void main(String[] args) {
        // Create the BeanFactory
        BeanFactory beanFactory = new AnnotationConfigApplicationContext(MyBean.class).getBeanFactory();

        // Get a bean by name
        Object bean = beanFactory.getBean("myBean");
        System.out.println("Get bean by name: " + bean);

        // Get the ObjectProvider of a bean
        ObjectProvider<MyBean> objectProvider = beanFactory.getBeanProvider(MyBean.class);
        System.out.println("Get ObjectProvider of bean: " + objectProvider);

        // Get the type of a bean
        Class<?> beanType = beanFactory.getType("myBean");
        System.out.println("Get bean type: " + beanType);

        // Check if a bean exists
        boolean containsBean = beanFactory.containsBean("myBean");
        System.out.println("Contains bean: " + containsBean);

        // Check if a bean is a singleton
        boolean isSingleton = beanFactory.isSingleton("myBean");
        System.out.println("Is singleton: " + isSingleton);

        // Check if a bean is a prototype
        boolean isPrototype = beanFactory.isPrototype("myBean");
        System.out.println("Is prototype: " + isPrototype);

        // Check if a bean matches the given type
        boolean isTypeMatch = beanFactory.isTypeMatch("myBean", ResolvableType.forClass(MyBean.class));
        System.out.println("Type matches: " + isTypeMatch);

        // Get all aliases of a bean
        String[] aliases = beanFactory.getAliases("myBean");
        System.out.println("Get all aliases of bean: " + String.join(", ", aliases));
    }
}
