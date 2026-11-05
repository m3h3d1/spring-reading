package com.xcs.spring;

import com.xcs.spring.config.MyBeanPostProcessor;
import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.repository.MyRepository;
import com.xcs.spring.service.MyService;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author xcs
 * @date 2023-11-24 15:02
 **/
public class AutowireCapableBeanFactoryDemo {

    public static void main(String[] args) {
        // Create the ApplicationContext
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);

        // Add a post-processor to print messages before and after bean initialization
        applicationContext.getBeanFactory().addBeanPostProcessor(new MyBeanPostProcessor());
        // Register a MyRepository singleton bean
        applicationContext.getBeanFactory().registerSingleton("myRepository", new MyRepository());

        // Get the AutowireCapableBeanFactory
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();

        // Create a bean instance of the given class
        // createBean(beanFactory);

        // Configure the given bean instance
        // configureBean(beanFactory);

        // Autowire the given bean instance
        // autowireBean(beanFactory);

        // Create a bean instance with the given autowire mode
        // autowire(beanFactory);

        // Autowire the properties of the given bean instance
        // autowireBeanProperties(beanFactory);

        // Apply property values to the given bean instance
        // applyBeanPropertyValues(beanFactory);

        // Initialize the given bean instance
        // initializeBean(beanFactory);

        // Destroy the given bean instance
        // destroyBean(beanFactory);

        // Resolve a dependency between beans
        // resolveDependency(beanFactory);
    }

    private static void createBean(AutowireCapableBeanFactory beanFactory) {
        MyService myService = beanFactory.createBean(MyService.class);
        System.out.println("Called createBean, created bean = " + myService);
    }

    private static void configureBean(AutowireCapableBeanFactory beanFactory) {
        // Register a RootBeanDefinition
        ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("myService", new RootBeanDefinition(MyService.class));

        MyService myService = new MyService();
        System.out.println("Before configureBean, MyService = " + myService);
        beanFactory.configureBean(myService, "myService");
        System.out.println("After configureBean, MyService = " + myService);
    }

    private static void autowireBean(AutowireCapableBeanFactory beanFactory) {
        MyService myService = new MyService();
        System.out.println("Before autowireBean, MyService = " + myService);
        beanFactory.autowireBean(myService);
        System.out.println("After autowireBean, MyService = " + myService);
    }

    private static void autowire(AutowireCapableBeanFactory beanFactory) {
        Object myService = beanFactory.autowire(MyService.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
        System.out.println("Called autowire, created bean = " + myService);
    }

    private static void autowireBeanProperties(AutowireCapableBeanFactory beanFactory) {
        MyService myService = new MyService();
        System.out.println("Before autowireBeanProperties, MyService = " + myService);
        beanFactory.autowireBeanProperties(myService, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
        System.out.println("After autowireBeanProperties, MyService = " + myService);
    }

    private static void applyBeanPropertyValues(AutowireCapableBeanFactory beanFactory) {
        PropertyValue propertyValue = new PropertyValue("javaHome", "My custom javaHome path");
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.addPropertyValue(propertyValue);

        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(MyService.class);
        rootBeanDefinition.setPropertyValues(propertyValues);
        // Register a RootBeanDefinition
        ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("myService", rootBeanDefinition);

        MyService myService = new MyService();
        System.out.println("Before applyBeanPropertyValues, MyService = " + myService);
        beanFactory.applyBeanPropertyValues(myService, "myService");
        System.out.println("After applyBeanPropertyValues, MyService = " + myService);
    }

    private static void initializeBean(AutowireCapableBeanFactory beanFactory) {
        MyService myService = new MyService();
        System.out.println("Before initializeBean, MyService = " + myService);
        beanFactory.initializeBean(myService, "myService");
        System.out.println("After initializeBean, MyService = " + myService);
    }

    private static void destroyBean(AutowireCapableBeanFactory beanFactory) {
        beanFactory.destroyBean(new MyService());
    }

    private static void resolveDependency(AutowireCapableBeanFactory beanFactory) {
        try {
            DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(MyService.class.getDeclaredField("myRepository"), false);
            Object resolveDependency = beanFactory.resolveDependency(dependencyDescriptor, "myRepository");
            System.out.println("resolveDependency = " + resolveDependency);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
