package com.xcs.spring;

import cn.hutool.json.JSONUtil;
import org.springframework.aop.aspectj.annotation.*;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class MetadataAwareAspectInstanceFactoryDemo {

    public static void main(String[] args) {
        // Create the aspect with SimpleMetadataAwareAspectInstanceFactory
        SimpleMetadataAwareAspectInstanceFactory simpleMetadataAwareAif = new SimpleMetadataAwareAspectInstanceFactory(MyAspect.class, "myAspect");
        System.out.println("SimpleMetadataAwareAspectInstanceFactory (1) = " + simpleMetadataAwareAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory (2) = " + simpleMetadataAwareAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory AspectMetadata = " + JSONUtil.toJsonStr(simpleMetadataAwareAif.getAspectMetadata()));
        System.out.println();

        // Create the aspect with SingletonMetadataAwareAspectInstanceFactory
        SingletonMetadataAwareAspectInstanceFactory singletonMetadataAwareAif = new SingletonMetadataAwareAspectInstanceFactory(new MyAspect(), "myAspect");
        System.out.println("SingletonMetadataAwareAspectInstanceFactory (1) = " + singletonMetadataAwareAif.getAspectInstance());
        System.out.println("SingletonMetadataAwareAspectInstanceFactory (2) = " + singletonMetadataAwareAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory AspectMetadata = " + JSONUtil.toJsonStr(singletonMetadataAwareAif.getAspectMetadata()));
        System.out.println();

        // Create the aspect with BeanFactoryAspectInstanceFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("myAspect", new MyAspect());
        BeanFactoryAspectInstanceFactory banFactoryAif = new BeanFactoryAspectInstanceFactory(beanFactory, "myAspect");
        System.out.println("BeanFactoryAspectInstanceFactory (1) = " + banFactoryAif.getAspectInstance());
        System.out.println("BeanFactoryAspectInstanceFactory (2) = " + banFactoryAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory AspectMetadata = " + JSONUtil.toJsonStr(banFactoryAif.getAspectMetadata()));
        System.out.println();

        // Create the aspect with LazySingletonAspectInstanceFactoryDecorator
        LazySingletonAspectInstanceFactoryDecorator lazySingletonAifD = new LazySingletonAspectInstanceFactoryDecorator(banFactoryAif);
        System.out.println("LazySingletonAspectInstanceFactoryDecorator (1) = " + lazySingletonAifD.getAspectInstance());
        System.out.println("LazySingletonAspectInstanceFactoryDecorator (2) = " + lazySingletonAifD.getAspectInstance());
        System.out.println("LazySingletonAspectInstanceFactoryDecorator AspectCreationMutex = " + lazySingletonAifD.getAspectCreationMutex());
        System.out.println("LazySingletonAspectInstanceFactoryDecorator AspectMetadata = " + JSONUtil.toJsonStr(lazySingletonAifD.getAspectMetadata()));
        System.out.println();
    }
}
