## MetadataReader

- [MetadataReader](#metadatareader)
  - [1. Background Knowledge](#1-background-knowledge)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementation](#5-main-implementation)
  - [6. Best Practices](#6-best-practices)
  - [7. Relation to Other Components](#7-relation-to-other-components)
  - [8. Common Problems](#8-common-problems)


### 1. Background Knowledge

1. **Resource interface**
   + Know the Spring `Resource` interface. It is an abstract interface to access resources. A resource can be a file, a file on the classpath, a URL, and so on. You need to know how to use `Resource` to get the input stream, the file path, and other information of a resource. [See the Resource interface](https://github.com/xuchengsheng/spring-reading/blob/master/spring-resources/spring-resource)
2. **ResourceLoader interface**
   + Know the Spring `ResourceLoader` interface. It is a factory to get `Resource` objects. You need to know how to use `ResourceLoader` to get a `Resource` object and load resources. [See the ResourceLoader interface](https://github.com/xuchengsheng/spring-reading/blob/master/spring-resources/spring-resource-resourceLoader)
3. **Classpath scanning**
   + Know how to use the `Resource` and `ResourceLoader` interfaces to scan resources on the classpath and get the metadata of classes.
4. **Spring configuration**
   + Know how to configure a Spring application context so that `Resource` and `ResourceLoader` can be used. This may include writing and loading configuration files (such as XML or Java configuration classes).
5. **ASM (optional)**
   + It is not required, but some basic knowledge of ASM (a bytecode library) helps to understand how `MetadataReader` works inside. The Spring `SimpleMetadataReader` uses ASM to parse class files.

### 2. Description

The `org.springframework.core.type.classreading.MetadataReader` interface is one of the core interfaces in the Spring Framework to read and parse the metadata of class files. It lets an application get metadata about a class, such as the class name, access modifiers, interfaces, super class, and annotations. This metadata can be used at runtime for many advanced features, such as component scanning, conditional annotation handling, and AOP (aspect-oriented programming).

### 3. Main Features

1. **Get basic class information**
   + `MetadataReader` gives basic information about a class, such as the class name and whether it is an interface, an abstract class, or an annotation.
2. **Get annotation information on a class**
   + You can use `MetadataReader` to get the annotation information on a class, such as the annotation types and attribute values.
3. **Get annotation information on methods**
   + With `MetadataReader`, you can get the metadata of the methods in a class, such as the method name, the return type, whether it is abstract, and the annotations on it.
4. **Get member class information**
   + `MetadataReader` has methods to get the names of the member classes (nested or inner classes) declared in a class.
5. **Get resource information of a class**
   + You can use `MetadataReader` to get the resource linked to a class, for example the path of the class file.
6. **Get super class information**
   + `MetadataReader` gives the super class information of a class, such as the name of the super class and whether there is one.

### 4. Interface Source

`MetadataReader` is a simple interface in the Spring Framework to access class metadata. It has methods to get the class file resource, the basic metadata of the class, and the annotation metadata of the class. This metadata is read by the ClassReader of ASM. The interface is mainly used in Spring to handle class-level metadata and annotations. It lets you access and use the information and annotations of a class.

```java
/**
 * Simple facade for accessing class metadata,
 * as read by an ASM {@link org.springframework.asm.ClassReader}.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public interface MetadataReader {

    /**
     * Return the resource reference for the class file.
     */
    Resource getResource();

    /**
     * Read basic class metadata for the underlying class.
     */
    ClassMetadata getClassMetadata();

    /**
     * Read full annotation metadata for the underlying class,
     * including metadata for annotated methods.
     */
    AnnotationMetadata getAnnotationMetadata();
}
```

### 5. Main Implementation

1. `SimpleMetadataReader`
   + Reads metadata from class files. It uses the `ClassReader` of the ASM library to parse the class file and get the metadata.

~~~mermaid
classDiagram
    direction BT
    class MetadataReader {
    	<<interface>>
        + getResource() : Resource
        + getClassMetadata() : ClassMetadata
        + getAnnotationMetadata() : AnnotationMetadata
    }

    class SimpleMetadataReader {
    }

    SimpleMetadataReader ..|> MetadataReader

~~~

### 6. Best Practices

This example first creates a `SimpleMetadataReaderFactory`. It is the factory that creates `MetadataReader` objects. Then it creates a `MetadataReader` with this factory. The `MetadataReader` gets the basic information of the target class, such as the class name and the kind of class (interface, annotation, abstract class, concrete class, and so on), to understand the structure of the class. Next, it gets the annotation information of the class, including the annotation types, to see if the class uses specific annotations. Finally, it goes through the methods of the class that have the given annotation. It gets details such as the method name, the declaring class name, and the return type. You can use these for custom annotation handling or logic for specific methods.

```java
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
```

This code defines a Java class `MyBean`. It extends the abstract class `MyAbstract` and has the class-level annotation `@MyClassAnnotation`. `MyBean` has the fields `key` and `value`, and three methods: `myMethod1` (a static method with the `@MyAnnotation` annotation), `myMethod2` (an instance method that returns a string, also with `@MyAnnotation`), and `myMethod3` (a plain instance method). `MyBean` also defines a static inner class `MyInnerClass`. This code shows different parts of a Java class: inheritance, interfaces, fields, methods, annotations, and inner classes.

```java
public abstract class MyAbstract {
    
}

@MyClassAnnotation
public final class MyBean extends MyAbstract implements Serializable {

    public String key;

    public String value;

    @MyAnnotation
    public static void myMethod1() {
        
    }

    @MyAnnotation
    public String myMethod2() {
        return "hello world";
    }

    public void myMethod3() {
        
    }

    public static class MyInnerClass {
        // Definition of the inner class
    }
}
```

Two custom annotations are defined: `MyAnnotation` and `MyClassAnnotation`.

```java
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    String value() default "";
}

@Retention(RetentionPolicy.RUNTIME)
public @interface MyClassAnnotation {
    String value() default "";
}
```

The output shows that `MetadataReader` can get the basic information and annotation metadata of classes and methods. You can use it for custom annotation handling, to find methods that have a given annotation, to understand the structure of a class, and to check class features at runtime. It is a strong tool to build custom frameworks, component scanning, and reflection work. Note that `MetadataReader` does not collect the methods that do not have the given annotation (for example `myMethod3`). Methods without the annotation are not part of the metadata.

```java
Class Name = com.xcs.spring.bean.MyBean
Class IsInterface = false
Class IsAnnotation = false
Class IsAbstract = false
Class IsConcrete = true
Class IsFinal = true
Class IsIndependent = true
Class HasEnclosingClass = false
Class EnclosingClassName = null
Class HasSuperClass = true
Class SuperClassName = com.xcs.spring.bean.MyAbstract
Class InterfaceNames = [java.io.Serializable]
Class MemberClassNames = [com.xcs.spring.bean.MyBean$MyInnerClass]
Class Annotations: [com.xcs.spring.annotation.MyClassAnnotation]

Method Name: myMethod1
Method DeclaringClassName: com.xcs.spring.bean.MyBean
Method ReturnTypeName: void
Method IsAbstract: false
Method IsStatic: true
Method IsFinal: false
Method IsOverridable: false

Method Name: myMethod2
Method DeclaringClassName: com.xcs.spring.bean.MyBean
Method ReturnTypeName: java.lang.String
Method IsAbstract: false
Method IsStatic: false
Method IsFinal: false
Method IsOverridable: true
```

### 7. Relation to Other Components

1. **`ClassPathBeanDefinitionScanner`**
   + `ClassPathBeanDefinitionScanner` uses `MetadataReader` for component scanning. Component scanning is a way to find and register the classes in an application automatically, so the Spring container can manage them. `MetadataReader` reads the metadata of classes to decide which classes should be registered as Spring components, for example classes with annotations such as `@Component`, `@Service`, and `@Repository`.
2. **`MetadataReaderFactory`**
   + `MetadataReaderFactory` is a factory that creates `MetadataReader` instances. `MetadataReader` is an interface to read and parse the metadata of a class, including class-level and method-level annotation information. `MetadataReaderFactory` handles class resources (such as class files or bytecode) and wraps them into a `MetadataReader` object, so the class metadata can be processed further.

### 8. Common Problems

1. **What is the relation between `MetadataReader` and `ClassPathBeanDefinitionScanner`?**
   - `ClassPathBeanDefinitionScanner` is the class Spring uses for component scanning. It uses `MetadataReader` to scan the classes in the given packages and find which classes should be registered as Spring beans.
2. **Where is `MetadataReader` used in Spring?**
   - `MetadataReader` is mainly used in component scanning, AOP, annotation handling, and bean post-processing. It reads and handles the metadata of classes.
3. **Why use `MetadataReader` instead of reflection?**
   - `MetadataReader` is usually faster than reflection. It reads the bytecode directly and does not need to load the whole class. It also allows more advanced metadata analysis and custom work.
4. **What are the common implementations of `MetadataReader`?**
   - Spring has several implementations of `MetadataReader`. The most common is `SimpleMetadataReader`. They read metadata from class files and annotation information.
5. **How do I customize the use of `MetadataReader`?**
   - You can write your own metadata reader that implements the `MetadataReader` interface for special needs, such as custom annotation handling or special class loading logic.
