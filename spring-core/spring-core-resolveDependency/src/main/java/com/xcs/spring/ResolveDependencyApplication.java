package com.xcs.spring;

import com.xcs.spring.config.MyConfiguration;
import com.xcs.spring.service.MyServiceA;
import com.xcs.spring.service.MyServiceB;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author xcs
 * @date 2023-10-25 10:13
 **/
public class ResolveDependencyApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        // Get the bean factory
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        // Target object to inject into
        MyServiceB injectTarget = new MyServiceB();

        System.out.println("Before MyServiceB = " + injectTarget + "\n");

        methodResolveDependency(beanFactory, injectTarget, "setMethodMyServiceA");
        fieldResolveDependency(beanFactory, injectTarget, "fieldMyServiceA");
        fieldResolveDependency(beanFactory, injectTarget, "myPropertyValue");

        System.out.println("After MyServiceB = " + injectTarget + "\n");
    }

    /**
     * Resolve method dependency
     *
     * @param beanFactory
     * @param injectTarget
     */
    public static void methodResolveDependency(ConfigurableListableBeanFactory beanFactory, Object injectTarget, String name) {
        try {
            // 1. Get the setMyServiceA method of MyServiceB
            Method method = injectTarget.getClass().getMethod(name, MyServiceA.class);

            // 2. Create a DependencyDescriptor for the method parameter
            DependencyDescriptor desc = new DependencyDescriptor(new MethodParameter(method, 0), true);

            // 3. Use the BeanFactory to resolve the method parameter dependency
            Object value = beanFactory.resolveDependency(desc, null);

            System.out.println("Method dependency result:");
            System.out.println("---------------------------------------------");
            System.out.println(String.format("Name:   %s.%s",method.getDeclaringClass().getName(),method.getName()));
            System.out.println(String.format("Value:  %s", value));
            System.out.println("---------------------------------------------\n");

            // 4. Make the method accessible (especially if it is private)
            ReflectionUtils.makeAccessible(method);

            // 5. Call the method by reflection to inject the resolved dependency
            method.invoke(injectTarget, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resolve field dependency
     *
     * @param beanFactory
     * @param injectTarget
     */
    public static void fieldResolveDependency(ConfigurableListableBeanFactory beanFactory, Object injectTarget, String name) {
        try {
            // 1. Get the fieldMyServiceA field of MyServiceB
            Field field = injectTarget.getClass().getDeclaredField(name);

            // 2. Create a DependencyDescriptor for the field
            DependencyDescriptor desc = new DependencyDescriptor(field, true);

            // 3. Use the BeanFactory to resolve the field dependency
            Object value = beanFactory.resolveDependency(desc, null);

            System.out.println("Field dependency result:");
            System.out.println("---------------------------------------------");
            System.out.println(String.format("Name:   %s.%s", field.getDeclaringClass().getName(), field.getName()));
            System.out.println(String.format("Value:  %s", value));
            System.out.println("---------------------------------------------\n");

            // 4. Make the field accessible (especially if it is private)
            ReflectionUtils.makeAccessible(field);

            // 5. Set the field value by reflection to inject the resolved dependency
            field.set(injectTarget, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
