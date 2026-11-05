package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.service.MyService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xcs
 * @date 2023-11-23 20:01
 **/
public class ListableBeanFactoryDemo {

    public static void main(String[] args) {
        // Create the ListableBeanFactory
        ListableBeanFactory beanFactory = new AnnotationConfigApplicationContext(MyConfiguration.class).getBeanFactory();

        // Check if a bean definition with the given name exists
        boolean containsBeanDefinition = beanFactory.containsBeanDefinition("myService");
        System.out.println("Contains bean definition: " + containsBeanDefinition);

        // Get the number of bean definitions in the factory
        int beanDefinitionCount = beanFactory.getBeanDefinitionCount();
        System.out.println("Bean definition count: " + beanDefinitionCount);

        // Get the names of all bean definitions in the factory
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        System.out.println("Bean definition names: " + String.join(", ", beanDefinitionNames));

        // Get an ObjectProvider and get the bean instance lazily
        ObjectProvider<MyService> objectProvider = beanFactory.getBeanProvider(MyService.class, true);
        System.out.println("Bean from ObjectProvider: " + objectProvider.getObject());

        // Get the names of all beans of a type
        String[] beanNamesForType = beanFactory.getBeanNamesForType(ResolvableType.forClass(MyService.class));
        System.out.println("Bean names for type: " + String.join(", ", beanNamesForType));

        // Get the names of all beans with an annotation
        String[] beanNamesForAnnotation = beanFactory.getBeanNamesForAnnotation(Service.class);
        System.out.println("Bean names for annotation: " + String.join(", ", beanNamesForAnnotation));

        // Get all bean instances with an annotation
        Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(Service.class);
        System.out.println("Beans with annotation: " + beansWithAnnotation);

        // Find an annotation of a given type on a bean
        Service annotation = beanFactory.findAnnotationOnBean("myService", Service.class);
        System.out.println("Annotation on bean: " + annotation);
    }
}
