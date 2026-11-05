package com.xcs.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * @author xcs
 * @date 2023-10-31 10:52
 **/
public class MetadataReaderDemo {
    public static void main(String[] args) throws IOException {

        // Create the MetadataReaderFactory
        SimpleMetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory();
        // Get the MetadataReader (Spring usually creates it for you)
        MetadataReader metadataReader = readerFactory.getMetadataReader("com.xcs.spring.bean.MyBean");

        // Get the basic information of the class
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        System.out.println("Class Name = " + classMetadata.getClassName());
        System.out.println("Class IsInterface = " + classMetadata.isInterface());
        System.out.println("Class IsAnnotation = " + classMetadata.isAnnotation());
        System.out.println("Class IsAbstract = " + classMetadata.isAbstract());
        System.out.println("Class IsConcrete = " + classMetadata.isConcrete());
        System.out.println("Class IsFinal = " + classMetadata.isFinal());
        System.out.println("Class IsIndependent = " + classMetadata.isIndependent());
        System.out.println("Class HasEnclosingClass = " + classMetadata.hasEnclosingClass());
        System.out.println("Class EnclosingClassName = " + classMetadata.getEnclosingClassName());
        System.out.println("Class HasSuperClass = " + classMetadata.hasSuperClass());
        System.out.println("Class SuperClassName = " + classMetadata.getSuperClassName());
        System.out.println("Class InterfaceNames = " + Arrays.toString(classMetadata.getInterfaceNames()));
        System.out.println("Class MemberClassNames = " + Arrays.toString(classMetadata.getMemberClassNames()));
        System.out.println("Class Annotations: " +  metadataReader.getAnnotationMetadata().getAnnotationTypes());

        System.out.println();

        // Get the annotated methods
        for (MethodMetadata methodMetadata : metadataReader.getAnnotationMetadata().getAnnotatedMethods("com.xcs.spring.annotation.MyAnnotation")) {
            System.out.println("Method Name: " + methodMetadata.getMethodName());
            System.out.println("Method DeclaringClassName: " + methodMetadata.getDeclaringClassName());
            System.out.println("Method ReturnTypeName: " + methodMetadata.getReturnTypeName());
            System.out.println("Method IsAbstract: " + methodMetadata.isAbstract());
            System.out.println("Method IsStatic: " + methodMetadata.isStatic());
            System.out.println("Method IsFinal: " + methodMetadata.isFinal());
            System.out.println("Method IsOverridable: " + methodMetadata.isOverridable());
            System.out.println();
        }
    }
}
