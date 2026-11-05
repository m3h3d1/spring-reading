package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.metrics.ApplicationStartup;

import java.security.AccessControlContext;

/**
 * @author xcs
 * @date 2023-11-24 13:56
 **/
public class ConfigurableBeanFactoryDemo {

    public static void main(String[] args) {
        // Create the ConfigurableBeanFactory
        ConfigurableBeanFactory configurableBeanFactory = new AnnotationConfigApplicationContext(MyConfiguration.class).getBeanFactory();

        // Set the parent BeanFactory
        configurableBeanFactory.setParentBeanFactory(new DefaultListableBeanFactory());

        // Get the number of BeanPostProcessors
        int beanPostProcessorCount = configurableBeanFactory.getBeanPostProcessorCount();
        System.out.println("BeanPostProcessor count: " + beanPostProcessorCount);

        // Get the names of all registered scopes
        String[] scopeNames = configurableBeanFactory.getRegisteredScopeNames();
        System.out.println("Registered scope names: " + String.join(", ", scopeNames));

        // Get a registered scope
        Scope customScope = configurableBeanFactory.getRegisteredScope("customScope");
        System.out.println("Registered scope: " + customScope);

        // Get the ApplicationStartup
        ApplicationStartup applicationStartup = configurableBeanFactory.getApplicationStartup();
        System.out.println("ApplicationStartup: " + applicationStartup);

        // Get the AccessControlContext
        AccessControlContext accessControlContext = configurableBeanFactory.getAccessControlContext();
        System.out.println("AccessControlContext: " + accessControlContext);

        // Copy the configuration
        ConfigurableListableBeanFactory otherFactory = new DefaultListableBeanFactory();
        configurableBeanFactory.copyConfigurationFrom(otherFactory);
        System.out.println("copyConfigurationFrom: " + otherFactory);

        // Register an alias
        String beanName = "myService";
        String alias = "helloService";
        configurableBeanFactory.registerAlias(beanName, alias);
        System.out.println("registerAlias, beanName: " + beanName + ", alias: " + alias);

        // Get the merged BeanDefinition
        BeanDefinition mergedBeanDefinition = configurableBeanFactory.getMergedBeanDefinition("myService");
        System.out.println("Merged BeanDefinition: " + mergedBeanDefinition);

        // Check if the bean is a FactoryBean
        String factoryBeanName = "myService";
        boolean isFactoryBean = configurableBeanFactory.isFactoryBean(factoryBeanName);
        System.out.println("Is FactoryBean: " + isFactoryBean);

        // Mark the bean as currently in creation
        String currentBeanName = "myService";
        boolean inCreation = true;
        configurableBeanFactory.setCurrentlyInCreation(currentBeanName, inCreation);
        System.out.println("setCurrentlyInCreation: " + currentBeanName);

        // Check if the bean is currently in creation
        boolean isCurrentlyInCreation = configurableBeanFactory.isCurrentlyInCreation(currentBeanName);
        System.out.println("Is currently in creation: " + isCurrentlyInCreation);

        // Register a dependency
        String dependentBeanName = "dependentBean";
        configurableBeanFactory.registerDependentBean(beanName, dependentBeanName);
        System.out.println("registerDependentBean, beanName: " + beanName + ", dependentBeanName: " + dependentBeanName);

        // Get the names of all beans that depend on the bean
        String[] dependentBeans = configurableBeanFactory.getDependentBeans(beanName);
        System.out.println("Dependent beans: " + String.join(", ", dependentBeans));

        // Get the names of all beans the bean depends on
        String[] dependencies = configurableBeanFactory.getDependenciesForBean(beanName);
        System.out.println("Dependencies: " + String.join(", ", dependencies));

        // Destroy the bean instance
        Object beanInstance = configurableBeanFactory.getBean(beanName);
        configurableBeanFactory.destroyBean(beanName, beanInstance);
        System.out.println("destroyBean: " + beanName);

        // Destroy all singleton beans
        configurableBeanFactory.destroySingletons();
        System.out.println("destroySingletons");
    }

}
