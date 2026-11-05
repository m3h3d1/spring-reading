## @PreDestroy

- [@PreDestroy](#predestroy)
  - [1. Overview](#1-overview)
  - [2. Interface Description](#2-interface-description)
  - [3. Interface Source](#3-interface-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [Prerequisites](#prerequisites)
    - [Collection Phase](#collection-phase)
    - [Destruction Phase](#destruction-phase)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133911656) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Interface Description

The `@PreDestroy` annotation comes from JSR-250 (Java Specification Request 250). This specification defines some common Java EE annotations. They give a platform-independent way to define lifecycle events, such as a method that runs when a bean is destroyed.

### 3. Interface Source

`@PreDestroy` is a standard Java EE annotation. It marks a method that should run right before the object is destroyed.

```java
@Documented
@Retention (RUNTIME)
@Target(METHOD)
public @interface PreDestroy {
    
}
```

### 4. Main Features

1. **Resource release**
   + For example, if your bean opens files, database connections, or network connections, you can close them in a `@PreDestroy` method. This makes sure the resources are released correctly.
2. **Cleanup**
   + If the bean creates temporary files or temporary data structures during its lifecycle, and they must be deleted or cleared before the bean is destroyed, you can do this cleanup in a `@PreDestroy` method.
3. **Logging and notification**
   + In some applications, you may want to write a log or send a notification when the bean's lifecycle ends. You can do this with a `@PreDestroy` method.
4. **Saving state**
   + If the bean has state, and you want to save it when the lifecycle ends, you can do this in a `@PreDestroy` method.
5. **Disconnect from other components**
   + If the bean registered itself with other components or services during its lifecycle, and it must unregister before it is destroyed, you can do this in a `@PreDestroy` method.
6. **No XML configuration needed**
   + Compared with the traditional `destroy-method` XML attribute, `@PreDestroy` makes the code clearer. The cleanup logic is in the same place as the bean code, so you do not need to look at an XML configuration file.

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (this class configures the Spring container with Java annotations). We pass the `MyConfiguration` component class as the constructor argument. At the end, we call `context.close()` to close the container.

```java
public class PreDestroyApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
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

`MyService` is a Spring service class. It has a no-arg constructor and a method annotated with `@PreDestroy`.

```java
@Service
public class MyService {

    public MyService(){
        System.out.println("Executing MyService constructor");
    }

    @PreDestroy
    public void preDestroy(){
        System.out.println("Executing @PreDestroy method");
    }
}
```

When the context is closed, the `@PreDestroy` method is called.

```java
Executing MyService constructor
Executing @PreDestroy method
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @PreDestroy annotation sequence diagram
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>Start applying the BeanDefinition post-processors.
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>Handle the common annotations of the bean.
CommonAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName)<br>Handle the merged bean definition for lifecycle annotations (such as @PreDestroy).
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:findLifecycleMetadata(clazz)<br>Find the lifecycle metadata of the class.
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:buildLifecycleMetadata(clazz)<br>Build the lifecycle metadata of the class.
InitDestroyAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>Process all local methods of the class.
ReflectionUtils->>InitDestroyAnnotationBeanPostProcessor:<br>Find the methods that have the @PreDestroy annotation.
InitDestroyAnnotationBeanPostProcessor->>LifecycleElement:new LifecycleElement(member,ae,pd)<br>Create a new lifecycle element for the @PreDestroy method.
InitDestroyAnnotationBeanPostProcessor->>LifecycleMetadata:new LifecycleMetadata(clazz, initMethods, destroyMethods)<br>Create the metadata that holds the lifecycle methods (initialization and destruction).
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:this.lifecycleMetadataCache.put(clazz, metadata)<br>Cache the lifecycle metadata for later access.
AbstractAutowireCapableBeanFactory->>InitDestroyAnnotationBeanPostProcessor:doClose()<br>The steps of closing the context are omitted here
InitDestroyAnnotationBeanPostProcessor->>InitDestroyAnnotationBeanPostProcessor:postProcessBeforeDestruction(bean,beanName)
InitDestroyAnnotationBeanPostProcessor->>LifecycleMetadata:invokeDestroyMethods(target,beanName)
LifecycleMetadata->>LifecycleElement:invoke(target)
LifecycleElement->>Method:this.method.invoke(target, (Object[]) null)
~~~

### 7. Source Code Analysis

#### Prerequisites

In Spring, `InitDestroyAnnotationBeanPostProcessor` is the key class that handles annotations such as `@PreDestroy`. It implements the two interfaces below. To understand how `@PreDestroy` works, you need to know these interfaces. They let you step into key stages of the bean lifecycle, so methods can be run and other related work can be done.

1. `MergedBeanDefinitionPostProcessor` interface
   - Its `postProcessMergedBeanDefinition` method lets a post-processor change the merged bean definition. A merged bean definition already includes all properties from parent bean definitions. For `@PreDestroy`, this step usually collects the `@PreDestroy` information to parse and prepares it for later processing.
   - 🔗 [MergedBeanDefinitionPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. `DestructionAwareBeanPostProcessor` interface
   - This interface handles the destruction stage of a bean.
   - For `@PreDestroy`, `InitDestroyAnnotationBeanPostProcessor` uses this interface to make sure the methods marked with `@PreDestroy` run before the bean is destroyed.
   - 🔗 [DestructionAwareBeanPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-destructionAwareBeanPostProcessor)

#### Collection Phase

In the `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition` method, `super.postProcessMergedBeanDefinition` is called first. This calls the default implementation of the parent class or interface.

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessMergedBeanDefinition` method handles the metadata related to `@PreDestroy`. After the bean definition is merged, it processes or checks this metadata. This is the entry point where Spring handles the JSR-250 `@PreDestroy` annotation.

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

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#buildLifecycleMetadata` method first checks whether the class has the `@PreDestroy` annotation. Then it goes through the methods of the class to find those marked as lifecycle methods with `@PreDestroy`. The methods it finds are stored in a list. At the end, it builds a `LifecycleMetadata` object from them.

```java
private LifecycleMetadata buildLifecycleMetadata(final Class<?> clazz) {
    // Check if the given class has the @PreDestroy annotation.
    if (!AnnotationUtils.isCandidateClass(clazz, Arrays.asList(this.initAnnotationType, this.destroyAnnotationType))) {
        return this.emptyLifecycleMetadata;
    }

    // Lists to store the init and destroy methods that are found.
	List<LifecycleElement> destroyMethods = new ArrayList<>();
    // ... [code omitted for brevity]
    Class<?> targetClass = clazz;

    // Go through the target class and all its parent classes, until the Object class.
    do {
        final List<LifecycleElement> currInitMethods = new ArrayList<>();
        final List<LifecycleElement> currDestroyMethods = new ArrayList<>();

        // Use reflection to process all local methods of the class.
        ReflectionUtils.doWithLocalMethods(targetClass, method -> {
            // ... [code omitted for brevity]
            
            // Find methods marked with the @PreDestroy annotation.
            if (this.destroyAnnotationType != null && method.isAnnotationPresent(this.destroyAnnotationType)) {
                currDestroyMethods.add(new LifecycleElement(method));
                if (logger.isTraceEnabled()) {
                    logger.trace("Found destroy method on class [" + clazz.getName() + "]: " + method);
                }
            }
        });

        // Add the lifecycle methods of the current class to the total list.
        destroyMethods.addAll(currDestroyMethods);

        // ... [code omitted for brevity]
        targetClass = targetClass.getSuperclass();
    }
    while (targetClass != null && targetClass != Object.class);

    // If no lifecycle methods are found, return an empty lifecycle metadata object. Otherwise, return the newly built one.
    return (initMethods.isEmpty() && destroyMethods.isEmpty() ? this.emptyLifecycleMetadata :
            new LifecycleMetadata(clazz, initMethods, destroyMethods));
}
```

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleElement#LifecycleElement` constructor wraps a lifecycle-related method (such as a method annotated with `@PreDestroy`).

```java
public LifecycleElement(Method method) {
    // Check that the given method has no parameters. Lifecycle methods (such as @PreDestroy) must be no-arg methods.
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

#### Destruction Phase

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#postProcessBeforeDestruction` method is part of Spring's `DestructionAwareBeanPostProcessor` interface. It runs the methods that must be called when a bean is destroyed. This implementation handles the `@PreDestroy` annotation.

```java
@Override
public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
    LifecycleMetadata metadata = findLifecycleMetadata(bean.getClass());
    try {
        metadata.invokeDestroyMethods(bean, beanName);
    }
    catch (InvocationTargetException ex) {
       // ... [code omitted for brevity]
    }
    catch (Throwable ex) {
       // ... [code omitted for brevity]
    }
}
```

In the `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor#findLifecycleMetadata` method, the first call happens in the metadata collection phase, `InitDestroyAnnotationBeanPostProcessor#postProcessMergedBeanDefinition`. There, `findLifecycleMetadata` processes and caches the `LifecycleMetadata` for `@PreDestroy` and other related annotations. So when `findLifecycleMetadata` is called again in the `postProcessBeforeDestruction` phase, it returns the cached `LifecycleMetadata`. It does not need to build it again.

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

The `org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata#invokeDestroyMethods` method calls the destroy methods on a bean instance. It goes through a predetermined collection of lifecycle elements (here, the destroy methods) and calls each element (method).

```java
public void invokeDestroyMethods(Object target, String beanName) throws Throwable {
    // Get the collection of destroy methods that were already checked.
    Collection<LifecycleElement> checkedDestroyMethods = this.checkedDestroyMethods;
    // Choose which destroy methods to use, depending on whether checked destroy methods exist.
    Collection<LifecycleElement> destroyMethodsToUse =
        (checkedDestroyMethods != null ? checkedDestroyMethods : this.destroyMethods);

    // Check if there are destroy methods to run.
    if (!destroyMethodsToUse.isEmpty()) {
        // Go through the destroy methods.
        for (LifecycleElement element : destroyMethodsToUse) {
            // If the log level is TRACE, log the destroy method that is being called.
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking destroy method on bean '" + beanName + "': " + element.getMethod());
            }
            // Call the current destroy method on the target bean.
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

1. **Method signature**
   + A method marked with `@PreDestroy` should take no parameters and should return nothing (`void`).
2. **Exception handling**
   + A method marked with `@PreDestroy` should not throw checked exceptions. If an error or exception can happen in the method, handle it inside the method. This avoids unexpected behavior.
3. **Access modifier**
   + You can use `@PreDestroy` on private, protected, or public methods. But it is better to make sure the method is accessible, especially in subclasses or in different packages.
4. **Limit of prototype beans**
   + For prototype-scoped beans, the Spring container does not call the `@PreDestroy` method, because Spring does not manage the full lifecycle of prototype beans. So do not rely on `@PreDestroy` to clean up resources of a prototype bean.
5. **Multiple destroy methods**
   + If a bean has both a `@PreDestroy` method and a method set by the XML `destroy-method` attribute, the `@PreDestroy` method is called first. Then the `destroy-method` method is called.
6. **Dependencies**
   + If your bean depends on other beans, and these dependencies still matter during destruction, make sure they are still available when the `@PreDestroy` method runs.
7. **Execution order**
   + Spring does not guarantee the order of `@PreDestroy` methods, especially across several beans. If the order of destroy methods matters, consider other ways to coordinate the destroy actions.
8. **JSR-250 dependency**
   + `@PreDestroy` is part of the JSR-250 specification. To use it, make sure you have the right library dependency (most modern Spring projects already have it).
9. **Works with the bean lifecycle**
   + `@PreDestroy` is called only when the Spring container really manages the bean lifecycle. For example, if you create a bean instance by hand (instead of getting it from the Spring container), the `@PreDestroy` method is not called automatically.

### 9. Summary

#### Best Practices Summary

1. **Entry class**
   - In the `main` method of `PreDestroyApplication`, `AnnotationConfigApplicationContext` initializes the Spring container. It uses Java annotations to configure Spring. We pass `MyConfiguration` as the argument, so Spring loads its configuration from this class.
2. **Configuration class**
   - `MyConfiguration` is marked as a configuration class (with `@Configuration`). The `@ComponentScan` annotation tells Spring which packages to search for components. In this example, Spring scans the "`com.xcs.spring`" package and its sub-packages for annotations such as `@Component`, `@Service`, `@Repository`, and `@Controller`, and registers beans automatically.
3. **Service class**
   - When Spring scans the "`com.xcs.spring`" package, it finds the `MyService` class, which is marked as a service (with `@Service`). So Spring creates a bean instance of this class.
4. **Closing the context**
   - When the application context is closed (`context.close()`), all singleton beans are destroyed. During this, every method with `@PreDestroy` is called. So the `preDestroy` method in `MyService` runs and prints "Executing @PreDestroy method".

#### Source Code Analysis Summary

1. **Prerequisites**
   - The execution of `@PreDestroy` depends on two core interfaces: `MergedBeanDefinitionPostProcessor` and `DestructionAwareBeanPostProcessor`.
   - These interfaces let Spring step into key stages of the bean lifecycle, such as after property injection and before destruction.

2. **Collection phase**

   - When Spring processes a bean definition and the bean may have `@PreDestroy`, the `postProcessMergedBeanDefinition` method of `InitDestroyAnnotationBeanPostProcessor` is called.

   - In this method, the `LifecycleMetadata` of the bean (including the `@PreDestroy` method information) is collected and cached for later use.

3. **Destruction phase**

   - When the Spring context is closed (for example by calling `context.close()`), all singleton beans are destroyed.
   - The `postProcessBeforeDestruction` method is part of the `DestructionAwareBeanPostProcessor` interface. It is called for every bean that must be destroyed.
   - In this method, `findLifecycleMetadata` is called again to get the `LifecycleMetadata` from the cache.
   - After that, the `invokeDestroyMethods` method goes through all destroy methods and calls them on the target bean.

4. **Actual method call**
   - When a specific `@PreDestroy` method must be called, the `invoke` method of the `LifecycleElement` class is used. It uses reflection again to make sure the method is accessible, and then calls it.
