package com.xcs.spring.config;

import com.xcs.spring.annotation.MyValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xcs
 * @date 2023-09-19 16:42
 **/
public class MyMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor {

    /**
     * Records the metadata
     */
    private Map<String, Map<Field, String>> metadata = new HashMap<>();

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        Field[] fields = beanType.getDeclaredFields();
        Map<Field, String> defaultValues = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyValue.class)) {
                MyValue annotation = field.getAnnotation(MyValue.class);
                defaultValues.put(field, annotation.value());
            }
        }
        if (!defaultValues.isEmpty()) {
            metadata.put(beanName, defaultValues);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (metadata.containsKey(beanName)) {
            Map<Field, String> defaultValues = metadata.get(beanName);
            for (Map.Entry<Field, String> entry : defaultValues.entrySet()) {
                Field field = entry.getKey();
                String value = entry.getValue();
                try {
                    field.setAccessible(true);
                    if (field.get(bean) == null) {
                        field.set(bean, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
