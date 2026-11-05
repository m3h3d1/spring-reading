package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-11-23 20:22
 **/
public class HierarchicalBeanFactoryDemo {

    public static void main(String[] args) {
        // Create the parent container
        AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext(MyBean.class);
        // Create the child container
        AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
        childContext.setParent(parentContext);

        // Get the bean from the child BeanFactory
        HierarchicalBeanFactory childHierarchicalBeanFactory = childContext.getBeanFactory();
        System.out.println("Get bean from child BeanFactory: " + childHierarchicalBeanFactory.getBean(MyBean.class));

        // Get the bean from the parent BeanFactory
        HierarchicalBeanFactory parentHierarchicalBeanFactory = parentContext.getBeanFactory();
        System.out.println("Get bean from parent BeanFactory: " + parentHierarchicalBeanFactory.getBean(MyBean.class));

        // Get the parent BeanFactory
        BeanFactory parentBeanFactory = childHierarchicalBeanFactory.getParentBeanFactory();
        System.out.println("Get parent BeanFactory: " + parentBeanFactory);

        // Check if the local BeanFactory contains a bean with the given name
        boolean containsLocalBean = childHierarchicalBeanFactory.containsLocalBean("myBean");
        System.out.println("Local BeanFactory contains the bean: " + containsLocalBean);

        // Check if the whole BeanFactory hierarchy contains a bean with the given name
        boolean containsBean = childHierarchicalBeanFactory.containsBean("myBean");
        System.out.println("Whole BeanFactory contains the bean: " + containsBean);
    }
}
