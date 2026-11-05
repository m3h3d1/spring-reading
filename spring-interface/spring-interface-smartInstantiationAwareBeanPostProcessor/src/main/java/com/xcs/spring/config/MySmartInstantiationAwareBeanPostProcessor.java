package com.xcs.spring.config;

import com.xcs.spring.annotation.MyAutowired;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xcs
 * @date 2023-09-19 16:42
 **/
public class MySmartInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Override
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        // First, look for constructors annotated with @MyAutowired
        List<Constructor<?>> myAutowiredConstructors = Arrays.stream(beanClass.getConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(MyAutowired.class))
                .collect(Collectors.toList());

        if (!myAutowiredConstructors.isEmpty()) {
            return myAutowiredConstructors.toArray(new Constructor<?>[0]);
        }

        // Second, check for a default constructor
        try {
            Constructor<?> defaultConstructor = beanClass.getDeclaredConstructor();
            return new Constructor<?>[]{defaultConstructor};
        } catch (NoSuchMethodException e) {
            // No default constructor found, so continue to pick a suitable constructor
        }

        // Return all constructors and let Spring pick the most specific one
        return beanClass.getConstructors();
    }
}
