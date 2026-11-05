## @PostConstruct

- [@PostConstruct](#postconstruct)
  - [1. Overview](#1-overview)
  - [2. Interface Description](#2-interface-description)
  - [3. Interface Source](#3-interface-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [Prerequisites](#prerequisites)
    - [Collection Phase](#collection-phase)
    - [Execution Phase](#execution-phase)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133904802) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Interface Description

The `@PostConstruct` annotation comes from JSR-250 (Java Specification Request 250). This specification defines some common Java EE annotations. They give a platform-independent way to define lifecycle events, such as a method that runs after a bean is initialized.

### 3. Interface Source

`@PostConstruct` is a standard Java EE annotation. It marks a method that should run right after the object is instantiated. Spring supports it. Spring calls the method after the constructor has finished and all properties are set. It runs before the initialization callbacks (such as `InitializingBean.afterPropertiesSet()` or a custom `init` method).

```java
@Documented
@Retention (RUNTIME)
@Target(METHOD)
public @interface PostConstruct {

}
```

### 4. Main Features

1. **Initialization logic**
   + Lets you run initialization logic after the object is created and dependency injection is done.
2. **Resource setup**
   + For components that need a database, files, or other external resources, use `@PostConstruct` to make sure the resources are set up before use.
3. **Data preloading**
   + Can load required data or fill caches when the application starts.
4. **Validation**
   + Makes sure some properties or settings of the component are valid before the object is used.
5. **Platform independent**
   + `@PostConstruct` is a standard Java EE annotation. It behaves the same in different containers and frameworks.
6. **Execution order**
   + In Spring, `@PostConstruct` is called after the constructor and after all properties are set. It runs before any initialization callback (such as `InitializingBean.afterPropertiesSet()` or a specified init method).

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (this class configures the Spring container with Java annotations). We pass the `MyConfiguration` component class as the constructor argument.

```java
public class PostConstructApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

The `MyConfiguration` class uses `@ComponentScan("com.xcs.spring")`. This tells Spring to search the given package ("`com.xcs.spring`") and its sub-packages for classes with annotations such as `@Component`, `@Service`, `@Repository`, and `@Controller`. Spring registers them as beans automatically. So you do not need to write a bean definition for each component.

```java
@Configuration
@ComponentScan("com.xcs.spring")
public class MyConfiguration {

}
```

`MyService` is a Spring service class. It has a no-arg constructor and a method annotated with `@PostConstruct`.

```java
@Service
public class MyService {

    public MyService(){
        System.out.println("Executing MyService constructor");
    }

    @PostConstruct
    public void postConstruct(){
        System.out.println("Executing @PostConstruct method");
    }
}
```

When the Spring container initializes the `MyService` bean, the output shows the constructor first. The `@PostConstruct` method follows.

```
Executing MyService constructor
Executing @PostConstruct method
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @PostConstruct annotation sequence diagram
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>Start applying the BeanDefinition post-processors.
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>Handle the common annotations of the bean.
CommonAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName)<br>Handle the merged bean definition for lifecycle annotations (such as @PostConstruct).
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:findLifecycleMetadata(clazz)<br>Find the lifecycle metadata of the class.
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:buildLifecycleMetadata(clazz)<br>Build the lifecycle metadata of the class.
InitDestroyAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>Process all local methods of the class.
ReflectionUtils->>InitDestroyAnnotationBeanPostProcessor:Find methods with @PostConstruct<br>Find the methods that have the @PostConstruct annotation.
InitDestroyAnnotationBeanPostProcessor->>LifecycleElement:new LifecycleElement(member,ae,pd)<br>Create a new lifecycle element for the @PostConstruct method.
InitDestroyAnnotationBeanPostProcessor->>LifecycleMetadata:new LifecycleMetadata(clazz, initMethods, destroyMethods)<br>Create the metadata that holds the lifecycle methods (initialization and destruction).
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:this.lifecycleMetadataCache.put(clazz, metadata)<br>Cache the lifecycle metadata for later access.
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName, exposedObject, mbd)<br>Start initializing the bean.
AbstractAutowireCapableBeanFactory->>InitDestroyAnnotationBeanPostProcessor:postProcessBeforeInitialization(result, beanName)<br>Run the post-processing before bean initialization.
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:findResourceMetadata(beanName,clazz,pvs)<br>Find the metadata of the resources to inject.
Note right of InitDestroyAnnotationBeanPostProcessor:<br>Quickly get the lifecycle metadata parsed earlier from the cache.
InitDestroyAnnotationBeanPostProcessor->>LifecycleMetadata:invokeInitMethods(bean, beanName)<br>Call all initialization methods marked with @PostConstruct.
LifecycleMetadata->>LifecycleElement:invoke(target)<br>Run the specific @PostConstruct method.
LifecycleElement->>Method:this.method.invoke(target, (Object[]) null)<br>Use reflection to call the @PostConstruct method of the target bean.
~~~

### 7. Source Code Analysis

#### Prerequisites

In Spring, `InitDestroyAnnotationBeanPostProcessor` is the key class that handles annotations such as `@PostConstruct`. It implements the two interfaces below. To understand how `@PostConstruct` works, you need to know these interfaces. They let you step into key stages of the bean lifecycle, so methods can be run and other related work can be done.

1. `MergedBeanDefinitionPostProcessor` interface
   - Its `postProcessMergedBeanDefinition` method lets a post-processor change the merged bean definition. A merged bean definition already includes all properties from parent bean definitions. For `@PostConstruct`, this step usually collects the `@PostConstruct` information to parse and prepares it for later processing.
   - 🔗 [MergedBeanDefinitionPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. `BeanPostProcessor` interface
   - This interface lets you change a newly instantiated bean. It allows custom changes before and after the Spring container initializes any property of the bean.
   - For `@PostConstruct`, when the container calls `postProcessBeforeInitialization`, `CommonAnnotationBeanPostProcessor` checks whether the bean has methods marked with `@PostConstruct`. If it does, those methods are called at this stage.
   - 🔗 [BeanPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-beanPostProcessor)

#### Collection Phase

In the `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition` method, `super.postProcessMergedBeanDefinition` is called first. This calls the default implementation of the parent class or interface.

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessMergedBeanDefinition` method handles the metadata related to `@PostConstruct`. After the bean definition is merged, it processes or checks this metadata. This is the entry point where Spring handles the JSR-250 `@PostConstruct` annotation.

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    // Find the lifecycle metadata for the given bean type.
    LifecycleMetadata metadata = findLifecycleMetadata(beanType);
    
    // Use the lifecycle metadata to check (and possibly change) the given bean definition.
    metadata.checkConfigMembers(beanDefinition);
}
```

In the `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#findLifecycleMetadata` method, it first tries to get the `LifecycleMetadata` from the cache. If it does not exist or needs a refresh, it creates a new `LifecycleMetadata` and stores it in the cache. This cache avoids building the metadata again and again for the same class.

```java
private LifecycleMetadata findLifecycleMetadata(Class<?> clazz) {
    
    // Check if the lifecycle metadata cache is null. This can happen during deserialization or destruction.
    if (this.lifecycleMetadataCache == null) {
        return buildLifecycleMetadata(clazz);
    }
    
    // First, do a quick check in the concurrent map with minimal locking.
    LifecycleMetadata metadata = this.lifecycleMetadataCache.get(clazz);
    
    // If no metadata is found in the cache, build it and put it in the cache.
    if (metadata == null) {
        synchronized (this.lifecycleMetadataCache) {
            metadata = this.lifecycleMetadataCache.get(clazz);
            
            // Double-checked locking: make sure only one thread builds and caches the metadata.
            if (metadata == null) {
                // Build the lifecycle metadata for the given class.
                metadata = buildLifecycleMetadata(clazz);
                // Cache the new metadata so later requests can get it quickly.
                this.lifecycleMetadataCache.put(clazz, metadata);
            }
            return metadata;
        }
    }
    
    return metadata;
}
```

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#buildLifecycleMetadata` method first checks whether the class has the `@PostConstruct` annotation. Then it goes through the methods of the class to find those marked as lifecycle methods with `@PostConstruct`. The methods it finds are stored in a list. At the end, it builds a `LifecycleMetadata` object from them.

```java
private LifecycleMetadata buildLifecycleMetadata(final Class<?> clazz) {
    // Check if the given class has the @PostConstruct annotation.
    if (!AnnotationUtils.isCandidateClass(clazz, Arrays.asList(this.initAnnotationType, this.destroyAnnotationType))) {
        return this.emptyLifecycleMetadata;
    }

    // Lists to store the init and destroy methods that are found.
    List<LifecycleElement> initMethods = new ArrayList<>();
    // ... [code omitted for brevity]
    Class<?> targetClass = clazz;

    // Go through the target class and all its parent classes, until the Object class.
    do {
        final List<LifecycleElement> currInitMethods = new ArrayList<>();
        final List<LifecycleElement> currDestroyMethods = new ArrayList<>();

        // Use reflection to process all local methods of the class.
        ReflectionUtils.doWithLocalMethods(targetClass, method -> {
            // Find methods marked with the init annotation.
            if (this.initAnnotationType != null && method.isAnnotationPresent(this.initAnnotationType)) {
                LifecycleElement element = new LifecycleElement(method);
                currInitMethods.add(element);
                if (logger.isTraceEnabled()) {
                    logger.trace("Found init method on class [" + clazz.getName() + "]: " + method);
                }
            }
            // ... [code omitted for brevity]
        });

        // Add the lifecycle methods of the current class to the total list.
        initMethods.addAll(0, currInitMethods);
        // ... [code omitted for brevity]
        targetClass = targetClass.getSuperclass();
    }
    while (targetClass != null && targetClass != Object.class);

    // If no lifecycle methods are found, return an empty lifecycle metadata object. Otherwise, return the newly built one.
    return (initMethods.isEmpty() && destroyMethods.isEmpty() ? this.emptyLifecycleMetadata :
            new LifecycleMetadata(clazz, initMethods, destroyMethods));
}
```

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleElement#LifecycleElement` constructor wraps a lifecycle-related method (such as a method annotated with `@PostConstruct`).

```java
public LifecycleElement(Method method) {
    // Check that the given method has no parameters. Lifecycle methods (such as @PostConstruct) must be no-arg methods.
    if (method.getParameterCount() != 0) {
        throw new IllegalStateException("Lifecycle method annotation requires a no-arg method: " + method);
    }
    
    // Store the given method.
    this.method = method;

    // Decide the unique identifier from the method modifiers (such as private). For a private method, use the fully qualified name. Otherwise, use only the method name.
    this.identifier = (Modifier.isPrivate(method.getModifiers()) ?
                       ClassUtils.getQualifiedMethodName(method) : method.getName());
}
```

#### Execution Phase

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessBeforeInitialization` method is part of Spring's `BeanPostProcessor` interface. It lets you make custom changes before and after any property of the bean is initialized. This implementation handles the `@PostConstruct` annotation.

```java
@Override
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    // Find the lifecycle metadata of the bean class.
    LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
    
    try {
        // Call the init methods of the bean (such as methods annotated with @PostConstruct).
        metadata.invokeInitMethods(bean, beanName);
    }
    catch (InvocationTargetException ex) {
        // If the init method fails (for example, it throws an exception), throw a bean creation exception.
        throw new BeanCreationException(beanName, "Invocation of init method failed", ex.getTargetException());
    }
    catch (Throwable ex) {
        // For other errors, also throw a bean creation exception.
        throw new BeanCreationException(beanName, "Failed to invoke init method", ex);
    }
    
    // Return the original bean instance.
    return bean;
}
```

In the `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#findLifecycleMetadata` method, the first call happens in the metadata collection phase, `InitDestroyAnnotationBeanPostProcessor#postProcessMergedBeanDefinition`. There, `findLifecycleMetadata` processes and caches the `LifecycleMetadata` for `@PostConstruct` and other related annotations. So after that, in later lifecycle steps such as `postProcessBeforeInitialization`, calling `findLifecycleMetadata` again returns the cached `LifecycleMetadata`. It does not need to build it again.

```java
private LifecycleMetadata findLifecycleMetadata(Class<?> clazz) {
    
    // Check if the lifecycle metadata cache is null. This can happen during deserialization or destruction.
    if (this.lifecycleMetadataCache == null) {
        return buildLifecycleMetadata(clazz);
    }
    
    // First, do a quick check in the concurrent map with minimal locking.
    LifecycleMetadata metadata = this.lifecycleMetadataCache.get(clazz);
    
    // If no metadata is found in the cache, build it and put it in the cache.
    if (metadata == null) {
        synchronized (this.lifecycleMetadataCache) {
            metadata = this.lifecycleMetadataCache.get(clazz);
            
            // Double-checked locking: make sure only one thread builds and caches the metadata.
            if (metadata == null) {
                // Build the lifecycle metadata for the given class.
                metadata = buildLifecycleMetadata(clazz);
                // Cache the new metadata so later requests can get it quickly.
                this.lifecycleMetadataCache.put(clazz, metadata);
            }
            return metadata;
        }
    }
    
    return metadata;
}
```

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata#invokeInitMethods` method calls the initialization methods of the Spring bean. These are the methods marked with `@PostConstruct`.

```java
public void invokeInitMethods(Object target, String beanName) throws Throwable {
    // Get the collection of checked init methods.
    Collection<LifecycleElement> checkedInitMethods = this.checkedInitMethods;
    
    // If there are checked init methods, use them. Otherwise, use all init methods.
    Collection<LifecycleElement> initMethodsToIterate =
        (checkedInitMethods != null ? checkedInitMethods : this.initMethods);
    
    // If there are init methods, call them one by one.
    if (!initMethodsToIterate.isEmpty()) {
        for (LifecycleElement element : initMethodsToIterate) {
            // If trace logging is on, log the call of each init method.
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking init method on bean '" + beanName + "': " + element.getMethod());
            }
            
            // Actually call the init method on the target object.
            element.invoke(target);
        }
    }
}
```

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleElement#invoke` method uses reflection to call the specific method on the target object.

```java
public void invoke(Object target) throws Throwable {
    // Make sure the wrapped method is accessible, especially if it is private.
    ReflectionUtils.makeAccessible(this.method);

    // Call the method with reflection. The method has no parameters, so pass null as the argument list.
    this.method.invoke(target, (Object[]) null);
}
```

### 8. Notes

1. **No-arg method**
   + A method annotated with `@PostConstruct` must not take any parameters.
2. **Return type**
   + These methods should return nothing (`void`). Other return types are ignored.
3. **Access modifier**
   + `public` or `protected` is usually recommended, but it is not required. You can also use this annotation on a private method. Spring makes sure these methods are still called.
4. **Exception handling**
   + If a `@PostConstruct` method throws an unchecked exception, the initialization of the component fails. The Spring container may refuse to load the component.
5. **Multiple @PostConstruct methods**
   + More than one method can have `@PostConstruct`, but this is not good practice. Use the annotation on only one method. This avoids confusion and an unclear initialization order.
6. **Relation to other lifecycle methods**
   + If you use both `@PostConstruct` and the `InitializingBean` interface (which has the `afterPropertiesSet` method), the `@PostConstruct` method runs before `afterPropertiesSet`.
7. **Avoid multiple `@PostConstruct`**
   + Do not use `@PostConstruct` many times in the same bean. If several steps must run during bean initialization, put them in one `@PostConstruct` method and call them in the order you want.
8. **Cross-framework support**
   + `@PostConstruct` comes from Java EE, but many Java frameworks support it, including Spring. Still, always make sure your runtime environment supports this annotation.
9. **Avoid long-running operations**
   + A `@PostConstruct` method should not do slow work, because it blocks the bean initialization. If you really need long-running initialization, consider other ways, such as asynchronous execution.

### 9. Summary

#### Best Practices Summary

1. **Entry class**
   + In the `main` method of `PostConstructApplication`, `AnnotationConfigApplicationContext` initializes the Spring container. It uses Java annotations to configure Spring. We pass `MyConfiguration` as the argument, so Spring loads its configuration from this class.
2. **Configuration class**
   + `MyConfiguration` is marked as a configuration class (with `@Configuration`). The `@ComponentScan` annotation tells Spring which packages to search for components. In this example, Spring scans the "`com.xcs.spring`" package and its sub-packages for annotations such as `@Component`, `@Service`, `@Repository`, and `@Controller`, and registers beans automatically.
3. **Service class**
   + When Spring scans the "`com.xcs.spring`" package, it finds the `MyService` class, which is marked as a service (with `@Service`). So Spring creates a bean instance of this class.
4. **Lifecycle**:
   - When Spring creates an instance of `MyService`, it calls the constructor first. This is why we see "Executing MyService constructor" first.
   - After all bean properties are set, the `@PostConstruct` method is executed. In this example, it is `postConstruct`. So we see "Executing @PostConstruct method" next.

#### Source Code Analysis Summary

1. **Prerequisites**
   - The execution of `@PostConstruct` depends on two core interfaces: `MergedBeanDefinitionPostProcessor` and `BeanPostProcessor`.
   - These interfaces let Spring step into key stages of the bean lifecycle, such as after property injection, before initialization methods, and after initialization methods.
2. **Collection phase**
   - When Spring processes a bean definition and the bean may have `@PostConstruct`, the `postProcessMergedBeanDefinition` method of `InitDestroyAnnotationBeanPostProcessor` is called.
   - In this method, the `LifecycleMetadata` of the bean (including the `@PostConstruct` method information) is collected and cached for later use.
3. **Execution phase**
   - In the Spring bean lifecycle, a key point before initialization is the `postProcessBeforeInitialization` method. At this stage, if the bean has one or more methods annotated with `@PostConstruct`, those methods are executed.
   - Spring finds the `LifecycleMetadata` of the bean (built earlier in the collection phase), goes through the methods in it, and calls them with reflection.
4. **Actual method call**
   - When a specific `@PostConstruct` method must be called, the `invoke` method of the `LifecycleElement` class is used. It uses reflection again to make sure the method is accessible, and then calls it.
