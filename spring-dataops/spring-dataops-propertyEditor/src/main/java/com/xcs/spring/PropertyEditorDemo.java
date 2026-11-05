package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Date;

public class PropertyEditorDemo {

    public static void main(String[] args) {
        // Create a BeanWrapperImpl instance to work with the MyBean class.
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(MyBean.class);

        // Register the custom property editor MyCustomDateEditor for Date properties.
        beanWrapper.overrideDefaultEditor(Date.class, new MyCustomDateEditor());

        // Set the "date" property of MyBean using the string "2023-12-5".
        // The registered MyCustomDateEditor converts the string to a Date.
        beanWrapper.setPropertyValue("date", "2023-12-5");

        // Set the "path" property of MyBean using the string "/opt/spring-reading".
        // No editor is registered for Path, so the default conversion logic is used.
        beanWrapper.setPropertyValue("path", "/opt/spring-reading");

        // Print the wrapped MyBean instance.
        System.out.println("MyBean = " + beanWrapper.getWrappedInstance());
    }
}
