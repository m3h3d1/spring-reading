## Condition

- [Condition](#condition)
  - [1. Overview](#1-overview)
  - [2. Background Knowledge](#2-background-knowledge)
  - [3. Description](#3-description)
  - [4. Main Features](#4-main-features)
  - [5. Interface Source](#5-interface-source)
  - [6. Main Implementation](#6-main-implementation)
  - [7. Best Practices](#7-best-practices)
  - [8. Relation to Other Components](#8-relation-to-other-components)
  - [9. Common Problems](#9-common-problems)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. **Resource interface**
   - [Resource](https://github.com/xuchengsheng/spring-reading/blob/master/spring-resources/spring-resource) is an abstract interface to access resources. A resource can be a file, a file on the classpath, a URL, and so on. You need to know how to use the `Resource` interface to get the input stream, the file path, and other information of a resource.

2. **AnnotationMetadata interface**
   - [AnnotationMetadata](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-annotationMetadata) is an interface in the Spring Framework to work with the annotation information of classes. It has methods to access and use this information.

3. **MetadataReader interface**
   - [MetadataReader](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-metadataReader) is an interface in Spring to read the metadata of a class. It can be used to scan class files and get basic information such as the class name and the class annotations. In annotation-driven development, `MetadataReader` is usually used to scan the classes in packages and read their annotations, to configure Spring beans.


4. **Path and pattern resolution**
   - Path resolution in Spring, especially Ant-style path patterns such as `classpath*:com/xcs/spring/bean/**/*.xml`.


### 3. Description

Implementations of the `Condition` interface are usually used with the `@Conditional` annotation. This annotation can be put on a class or a method. It checks if a condition is met to decide if a bean should be created or a specific configuration should be applied. This makes Spring applications more flexible, because the configuration can be chosen based on different conditions.

### 4. Main Features

1. **Decide dynamically if a bean is created**
   + A class that implements `Condition` implements the `matches` method. The method uses runtime logic to decide if the condition is met, and so if a specific bean is created.


2. **Apply configuration classes conditionally** 
   + With the `@Conditional` annotation, a `Condition` implementation can be used on a configuration class or on a method in it. This applies or skips a configuration based on the condition.


3. **Use with environment variables and system properties** 
   + You can use the environment variables and system properties in `ConditionContext` to write more complex conditions. This makes bean creation and configuration more flexible.


4. **Support custom condition logic**
   + You can implement the `Condition` interface for the needs of your application. This gives flexible condition logic, for example based on the operating system or whether a class is on the classpath.


5. **Make the application more flexible**
   + With conditions, you can configure different beans or apply different configurations in different environments. This makes the application more flexible and configurable.

### 5. Interface Source

The `Condition` interface requires an implementation to check the condition in the `matches` method before a component is registered. It can decide at runtime if the component should be registered. Implementations of this interface can be used with the `@Conditional` annotation to control bean registration in a flexible way. They follow the same restrictions as `BeanFactoryPostProcessor`. In particular, a condition should not interact with bean instances. For finer control of conditions that work with `@Configuration` beans, consider implementing the `ConfigurationCondition` interface.

```java
/**
 * A single {@code condition} that must be {@linkplain #matches matched} in order
 * for a component to be registered.
 *
 * <p>Conditions are checked immediately before the bean-definition is due to be
 * registered and are free to veto registration based on any criteria that can
 * be determined at that point.
 *
 * <p>Conditions must follow the same restrictions as {@link BeanFactoryPostProcessor}
 * and take care to never interact with bean instances. For more fine-grained control
 * of conditions that interact with {@code @Configuration} beans consider implementing
 * the {@link ConfigurationCondition} interface.
 *
 * @author Phillip Webb
 * @since 4.0
 * @see ConfigurationCondition
 * @see Conditional
 * @see ConditionContext
 */
@FunctionalInterface
public interface Condition {

    /**
     * Determine if the condition matches.
     * @param context the condition context
     * @param metadata the metadata of the {@link org.springframework.core.type.AnnotationMetadata class}
     * or {@link org.springframework.core.type.MethodMetadata method} being checked
     * @return {@code true} if the condition matches and the component can be registered,
     * or {@code false} to veto the annotated component's registration
     */
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
```

### 6. Main Implementation

1. **ProfileCondition**
   + Decides if a component is registered based on the profile information at runtime. For example, it can register specific beans based on the active profiles.

2. **OnBeanCondition**
   + Checks if a bean of a specific type exists in the container to decide if a component is registered. This lets you create a bean based on whether other beans exist in the container.

3. **OnClassCondition**
   + Checks if a specific class exists on the classpath to decide if a component is registered. By checking the classpath, you control component registration based on whether a class exists.

4. **OnPropertyCondition** 
   + Checks the property values in the configuration file to decide if a component is registered. You can configure component registration flexibly with the properties in the configuration file.

5. **ResourceCondition**
   + Checks if a specific resource file exists on the classpath to decide if a component is registered. It is similar to `OnClassCondition`, but it can check any resource file.

~~~mermaid
classDiagram
    direction BT
    
    class Condition{
    	<<interface>>
    }
    
    class ConfigurationCondition{
    	<<interface>>
    }
    
    class SpringBootCondition {
    	<<Abstract>>
    }
    
    class FilteringSpringBootCondition {
    	<<Abstract>>
    }
  
    class OnBeanCondition {
    }
    
    class OnClassCondition {
    }
    
    class OnPropertyCondition {
    }
    
    class OnResourceCondition {
    }
    
    class ProfileCondition {
    }
    
    ConfigurationCondition ..|> Condition
    SpringBootCondition --|> Condition
    FilteringSpringBootCondition ..|> SpringBootCondition
    OnBeanCondition ..|> FilteringSpringBootCondition
    OnBeanCondition --|> ConfigurationCondition
    OnClassCondition ..|> FilteringSpringBootCondition
    OnPropertyCondition ..|> SpringBootCondition
    OnResourceCondition ..|> SpringBootCondition
    ProfileCondition --|> Condition
   
~~~

### 7. Best Practices

This example uses the custom condition class `MyOnClassCondition` to check if the resources on the classpath meet a condition. It creates a resource resolver and a metadata reader factory. Then it gets all class files from the path `classpath*:com/xcs/spring/**/*.class`. Finally, it uses the custom condition to check each resource and prints a message based on the result.

```java
public class ConditionDemo {

    public static void main(String[] args) throws IOException {
        // Create a resource resolver to get resources that match a pattern
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // Create a MetadataReader factory to read the metadata of classes
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

        // Get all resources that match the pattern
        Resource[] resources = resolver.getResources("classpath*:com/xcs/spring/bean/**/*.class");

        // Create the custom condition used for matching
        Condition condition = new MyOnClassCondition("com.xcs.spring.ConditionDemo");

        // Go through each resource and check the custom condition
        for (Resource resource : resources) {
            // Get the MetadataReader of the resource
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

            // Check if the resource meets the custom condition
            if (condition.matches(null, metadataReader.getAnnotationMetadata())) {
                System.out.println(resource.getFile().getName() + " meets the condition");
            } else {
                System.out.println(resource.getFile().getName() + " does not meet the condition");
            }
        }
    }
}
```

`MyOnClassCondition` is a custom condition class that implements the `Condition` interface of Spring. It tries to load the class with the given name at runtime. If the class loads, the condition matches and it returns `true`. Otherwise, it catches `ClassNotFoundException`, which means the class does not exist. The condition does not match and it returns `false`.

```java
public class MyOnClassCondition implements Condition {

    private final String className;

    public MyOnClassCondition(String className) {
        this.className = className;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            // Try to load the class
            getClass().getClassLoader().loadClass(className);
            // The class exists, so the condition matches
            return true;
        } catch (ClassNotFoundException e) {
            // The class does not exist, so the condition does not match
            return false;
        }
    }
}
```

`MyBeanA` and `MyBeanB` are two simple Java classes.

```java
package com.xcs.spring.bean;

public class MyBeanA {

}

public class MyBeanB {

}
```

The output shows the case where `com.xcs.spring.ConditionDemo` exists. The `matches` method of `MyOnClassCondition` tries to load the `com.xcs.spring.ConditionDemo` class. The class loads, so the condition matches and it returns `true`. So both `MyBeanA.class` and `MyBeanB.class` meet the condition and are registered.

```java
MyBeanA.class meets the condition
MyBeanB.class meets the condition
```

Now change the code to `new MyOnClassCondition("com.xcs.spring.ConditionDemo1")`. It tries to load the `com.xcs.spring.ConditionDemo1` class. The "does not meet the condition" in the output means the `matches` method of `MyOnClassCondition` returned `false`, because the class could not be loaded. So neither `MyBeanA.class` nor `MyBeanB.class` meets the condition.

```java
MyBeanA.class does not meet the condition
MyBeanB.class does not meet the condition
```

### 8. Relation to Other Components

1. **ConditionEvaluator**
   + `ConditionEvaluator` has a `shouldSkip` method. It takes a `Condition` object and a `ConditionContext` object. It decides if a specific component, such as a bean, should be skipped (not registered). It is mainly used to handle the `@Conditional` annotation. When `@Conditional` is on a class or a method, Spring uses `ConditionEvaluator` during component registration to check if the condition is met. The result decides if the component is registered. With this, Spring can decide dynamically, before the application context is initialized, if a specific component is registered. This gives Spring applications a more flexible way to register components.

### 9. Common Problems

1. **When is a condition matched?**
   + You may wonder when a condition is matched. A condition is checked right before the bean definition is registered. So conditions are matched before the Spring application context is initialized.

2. **Combining many conditions**
   + When you need to combine many conditions, you may wonder how to do it in a flexible way. Spring has some ways to combine conditions, such as `@ConditionalOnProperty` and `@ConditionalOnExpression`.

3. **Relation between conditions and configuration**
   + You may be confused about the relation between conditions and configuration. A condition decides if a component is registered. Configuration sets the properties and behavior of a component.

4. **Implementing a custom condition** 
   + You may want a custom condition for a specific business need. Implement the `Condition` interface and put the right condition logic in the `matches` method.

5. **Error handling in conditions** 
   + If a condition fails, for example the `matches` method throws an exception, the component registration may fail. Handle exceptions in the condition logic, so they do not break the startup of the whole application.

6. **Performance of conditions**
   + If the application has many conditions, the startup may be slower. Design the condition logic with care, so that the startup performance stays acceptable when there are many conditions.

7. **Dynamic conditions**
   + You may ask if conditions can change dynamically. A condition is checked once when the application starts. It usually does not change at runtime. If you need more dynamic conditions, use other tools, such as the properties in `Environment`.
