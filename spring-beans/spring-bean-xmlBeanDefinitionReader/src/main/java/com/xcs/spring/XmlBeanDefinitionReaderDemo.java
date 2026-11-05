package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

/**
 * @author xcs
 * @date 2023-11-04 15:51
 **/
public class XmlBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        // Create a DefaultListableBeanFactory as the Spring container
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // Create an XmlBeanDefinitionReader to parse the XML configuration
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        // Load the XML configuration file
        reader.loadBeanDefinitions(new ClassPathResource("beans.xml"));

        // Get and use the bean
        MyBean myBean = factory.getBean("myBean", MyBean.class);
        System.out.println("myBean = " + myBean);
    }
}
