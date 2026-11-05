## ResourceLoaderAware

- [ResourceLoaderAware](#resourceloaderaware)
  - [1. Overview](#1-overview)
  - [2. Interface Description](#2-interface-description)
  - [3. Interface Source](#3-interface-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133915709) 📚 **Contents** - [All Articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [ResourceLoaderAware source code](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-resourceLoaderAware)

### 2. Interface Description

The `ResourceLoaderAware` interface gives a callback to beans that need access to a `ResourceLoader`. `ResourceLoader` is a simple strategy interface. It defines how to load low-level resources (such as classpath or file system resources).

### 3. Interface Source

`ResourceLoaderAware` is a core Spring interface, available since 10.03.2004. Spring automatically injects a `ResourceLoader` instance into any object that implements `ResourceLoaderAware`.

```java
/**
 * Interface to be implemented by any object that wishes to be notified of the ResourceLoader (typically the ApplicationContext) that it runs in.
 * This is an alternative to a full ApplicationContext dependency via the org.springframework.context.ApplicationContextAware interface.
 *
 * Note that org.springframework.core.io.Resource dependencies can also be exposed as bean properties of type Resource or Resource[],
 * populated via Strings with automatic type conversion by the bean factory. This removes the need for implementing any callback interface just for the purpose of accessing a specific file resource.
 *
 * You typically need a ResourceLoader when your application object needs to access a variety of file resources whose names are calculated.
 * A good strategy is to make the object use a org.springframework.core.io.DefaultResourceLoader but still implement ResourceLoaderAware,
 * to allow for overriding when running in an ApplicationContext. See org.springframework.context.support.ReloadableResourceBundleMessageSource for an example.
 *
 * The passed-in ResourceLoader can also be checked for the org.springframework.core.io.support.ResourcePatternResolver interface
 * and cast accordingly, to resolve resource patterns into arrays of Resource objects. This will always work when running in an ApplicationContext
 * (the context interface extends the ResourcePatternResolver interface). Use org.springframework.core.io.support.PathMatchingResourcePatternResolver as a default.
 * See also the ResourcePatternUtils.getResourcePatternResolver method.
 *
 * As an alternative to a ResourcePatternResolver dependency, consider exposing bean properties of type Resource[], populated via pattern Strings
 * with automatic type conversion by the bean factory at binding time.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 10.03.2004
 * @see ApplicationContextAware
 * @see org.springframework.core.io.Resource
 * @see org.springframework.core.io.ResourceLoader
 * @see org.springframework.core.io.support.ResourcePatternResolver
 */
public interface ResourceLoaderAware extends Aware {

    /**
     * Set the ResourceLoader that this object runs in.
     * This might be a ResourcePatternResolver, which can be checked through instanceof ResourcePatternResolver.
     * See also the ResourcePatternUtils.getResourcePatternResolver method.
     * Invoked after population of normal bean properties but before an init callback like InitializingBean's afterPropertiesSet or a custom init-method.
     * Invoked before ApplicationContextAware's setApplicationContext.
     * @param resourceLoader the ResourceLoader object to be used by this object
     * @see org.springframework.core.io.support.ResourcePatternResolver
     * @see org.springframework.core.io.support.ResourcePatternUtils#getResourcePatternResolver
     */
    void setResourceLoader(ResourceLoader resourceLoader);

}
```

### 4. Main Features

1. **Resource loading callback**
   + When a bean implements `ResourceLoaderAware`, the Spring container injects a `ResourceLoader` into the bean automatically when the bean is initialized. The bean can then load resources.

2. **A resource loading strategy**
   + With `ResourceLoader`, a bean can load many kinds of resources, such as classpath resources, file system resources, and URL resources. It gives one unified strategy for accessing resources.

3. **Less direct dependency on ApplicationContext**
   + `ApplicationContext` also extends the `ResourceLoader` features. But sometimes a bean only needs resource loading and none of the other ApplicationContext features. By implementing `ResourceLoaderAware`, the bean gets only resource loading, which lowers its coupling to the full `ApplicationContext`.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyResourceLoaderAware` from the Spring context and calls the `getResource` method with a path.

```java
public class ResourceLoaderAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyResourceLoaderAware resourceLoaderAware = context.getBean(MyResourceLoaderAware.class);
        resourceLoaderAware.getResource("classpath:xcs.txt");
    }
}
```

The `@Bean` annotation defines a bean here, to make sure the Spring container creates `MyResourceLoaderAware`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyResourceLoaderAware myResourceLoaderAware(){
        return new MyResourceLoaderAware();
    }
}
```

The `MyResourceLoaderAware` class is a simple utility. It uses the Spring `ResourceLoader` mechanism to load a resource and print its content.

```java
public class MyResourceLoaderAware implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void getResource(String location){
        try {
            Resource resource = resourceLoader.getResource(location);
            System.out.println("Resource content: " + new String(FileCopyUtils.copyToByteArray(resource.getInputStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

The output shows that the `MyResourceLoaderAware` class read the content of the resource file and printed it to the console.

```java
Resource content: hello world
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: EnvironmentAware sequence diagram
    participant ResourceLoaderAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyResourceLoaderAware
    
    ResourceLoaderAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>Refresh the context
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>Initialize the bean factory
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>Instantiate singletons
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)<br>Get the bean
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>Do the actual get
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)<br>Get the singleton bean
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()<br>Get the bean instance
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)<br>Create the bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)<br>Do the actual creation
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName,bean,mbd)<br>Initialize the bean
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInitialization(existingBean, beanName)<br>Call the before-initialization processors
    AbstractAutowireCapableBeanFactory->>ApplicationContextAwareProcessor:postProcessBeforeInitialization(bean,beanName)<br>Trigger Aware handling
    ApplicationContextAwareProcessor->>ApplicationContextAwareProcessor:invokeAwareInterfaces(bean)<br>Run the Aware callbacks
    ApplicationContextAwareProcessor->>MyResourceLoaderAware:setResourceLoader(resourceLoader)<br>Set the resourceLoader
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>ResourceLoaderAwareApplication:Initialization complete
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyResourceLoaderAware` from the Spring context and calls the `getResource` method with a path.

```java
public class ResourceLoaderAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyResourceLoaderAware resourceLoaderAware = context.getBean(MyResourceLoaderAware.class);
        resourceLoaderAware.getResource("classpath:xcs.txt");
    }
}
```

The constructor of `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` does three steps. We focus on the `refresh()` method.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
```

In `org.springframework.context.support.AbstractApplicationContext#refresh`, we focus on `finishBeanFactoryInitialization(beanFactory)`. It instantiates all remaining non-lazy singleton beans. The other methods are not the focus of this reading, so we skip them.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [code omitted for brevity]
    // Instantiate all remaining (non-lazy-init) singletons.
    finishBeanFactoryInitialization(beanFactory);
    // ... [code omitted for brevity]
}
```

The method `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization` then calls `preInstantiateSingletons` in `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

```java
/**
 * Finish the initialization of this factory by instantiating all remaining non-lazy-init singleton beans.
 * 
 * @param beanFactory the bean factory to initialize
 */
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Instantiate all remaining non-lazy singleton beans.
    beanFactory.preInstantiateSingletons();
}
```

The main purpose of `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` is to instantiate all non-lazy singleton beans up front. It runs when the Spring context finishes initializing, so that all singleton beans are created and initialized. Its core operation is `getBean(beanName)`. For each singleton bean defined in the container, it calls `getBean`, which triggers instantiation, initialization, and dependency injection. If the bean has not been created yet, this call creates and initializes it.

```java
public void preInstantiateSingletons() throws BeansException {
    // ... [code omitted for brevity]
    // Loop over all bean names
    for (String beanName : beanNames) {
        getBean(beanName);
    }
    // ... [code omitted for brevity]
}
```

The method `org.springframework.beans.factory.support.AbstractBeanFactory#getBean()` calls `doGetBean` to do the real work of creating the bean. It passes the bean name and some default argument values. `doGetBean` does most of the work, such as finding the bean definition, creating the bean (if it does not exist yet), and handling dependencies.

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

In `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`, Spring first checks whether the requested bean is a singleton that has already been created. If not, it creates a new instance. Along the way it handles possible problems such as circular references, and makes sure the returned bean has the right type. This is a core part of bean lifecycle management in the Spring container.

```java
protected <T> T doGetBean(
        String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
        throws BeansException {
    // ... [code omitted for brevity]

    // Start creating the bean instance
    if (mbd.isSingleton()) {
        // If the bean is a singleton, try to get it from the singleton cache
        // If it does not exist, create a new instance with a lambda
        sharedInstance = getSingleton(beanName, () -> {
            try {
                // Try to create the bean instance
                return createBean(beanName, mbd, args);
            }
            catch (BeansException ex) {
                // ... [code omitted for brevity]
            }
        });
        // For some beans (for example FactoryBeans), more work may be needed to get the real bean instance
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [code omitted for brevity]

    // Make sure the returned bean instance matches the requested type
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

The method `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()` gets an existing bean instance from the singleton cache, or creates a new one with the given `ObjectFactory`. This is the key part that keeps a bean a singleton in the Spring container.

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // Assert that the bean name is not null
    Assert.notNull(beanName, "Bean name must not be null");

    // Synchronize on the singleton cache for thread safety
    synchronized (this.singletonObjects) {
        // Get the singleton from the cache
        Object singletonObject = this.singletonObjects.get(beanName);

        // If it is not in the cache
        if (singletonObject == null) {
            // ... [code omitted for brevity]

            try {
                // Create a new singleton instance with the factory
                singletonObject = singletonFactory.getObject();
                newSingleton = true;
            }
            catch (IllegalStateException ex) {
                // ... [code omitted for brevity]
            }
            catch (BeanCreationException ex) {
                // ... [code omitted for brevity]
            }
            finally {
                // ... [code omitted for brevity]
            }

            // ... [code omitted for brevity]
        }

        // Return the singleton
        return singletonObject;
    }
}
```

The main logic of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()` is to call `doCreateBean`. That is where the bean is really instantiated, populated, and initialized. The method returns the newly created bean instance.

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {
    
    // ... [code omitted for brevity]
    
    try {
        // Normal bean instantiation, property injection, and initialization.
        // This is where the bean is really created.
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        // Log that the bean was created successfully
        if (logger.isTraceEnabled()) {
            logger.trace("Finished creating instance of bean '" + beanName + "'");
        }
        return beanInstance;
    }
    catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
        // ... [code omitted for brevity]
    }
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`, the `initializeBean` method initializes the bean and makes sure it is fully configured and ready.

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // Declare an object that may later hold the initialized bean or its proxy
    Object exposedObject = bean;

    // ... [code omitted for brevity]
    
    try {
        // ... [code omitted for brevity]
        
        // Initialize the bean
        exposedObject = initializeBean(beanName, exposedObject, mbd);
    } 
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }

    // Return the created and initialized bean
    return exposedObject;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean`, if the condition is met (the bean is not synthetic), it calls `applyBeanPostProcessorsBeforeInitialization`. This is a key point in the Spring lifecycle. It loops over all registered `BeanPostProcessor` implementations and calls their `postProcessBeforeInitialization` methods. This lets us, and Spring's internal processors, change the bean or do other work before it is initialized.

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {

    // ... [code omitted for brevity]
    
    Object wrappedBean = bean;
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }
    
    // ... [code omitted for brevity]

    return wrappedBean;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`, the `postProcessBeforeInitialization` method of each `BeanPostProcessor` gets a chance to change or enhance the bean.

```java
@Override
public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
    throws BeansException {

    Object result = existingBean;
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
        Object current = processor.postProcessBeforeInitialization(result, beanName);
        if (current == null) {
            return result;
        }
        result = current;
    }
    return result;
}
```

In `org.springframework.context.support.ApplicationContextAwareProcessor#postProcessBeforeInitialization`, the implementation looks mainly at beans that implement "aware" interfaces and gives them the runtime information they need.

```java
@Override
@Nullable
public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (!(bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware ||
          bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware ||
          bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware ||
          bean instanceof ApplicationStartupAware)) {
        return bean;
    }

    // ... [code omitted for brevity]
    
    invokeAwareInterfaces(bean);

    return bean;
}
```

The method `org.springframework.context.support.ApplicationContextAwareProcessor#invokeAwareInterfaces` handles beans that implement "Aware" interfaces. These interfaces let a bean be "aware" of its runtime environment or specific dependencies automatically, without an explicit lookup or injection.

```java
private void invokeAwareInterfaces(Object bean) {
    // ... [code omitted for brevity]
	if (bean instanceof ResourceLoaderAware) {
        ((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
    }
    // ... [code omitted for brevity]
}
```

Finally, execution reaches our own logic. The `MyResourceLoaderAware` class is a simple utility. It uses the Spring `ResourceLoader` mechanism to load a resource and print its content.

```java
public class MyResourceLoaderAware implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void getResource(String location){
        try {
            Resource resource = resourceLoader.getResource(location);
            System.out.println("Resource content: " + new String(FileCopyUtils.copyToByteArray(resource.getInputStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 8. Notes

1. **Resource path**
   + When you get a resource with `ResourceLoader`, you must give the full path. For example, use the "classpath:" prefix to load a resource on the classpath. Make sure the path is correct. Otherwise `ResourceLoader` may not find the resource.

2. **Resource caching**
   + `ResourceLoader` itself does not cache resource content. Each call to `getResource` may return a new `Resource` instance. If you need to cache resource content, implement it yourself.

3. **Check that the resource exists**
   + A `Resource` returned by `ResourceLoader` is not guaranteed to exist. Before you access the content, use `Resource.exists()` to check.

4. **Different resource types**
   + Depending on the runtime environment and the `ResourceLoader` implementation, it can load many types of resources, such as classpath, file system, and URL resources. Know which types your environment supports, and use them correctly.

5. **Avoid overuse**
   + `ResourceLoaderAware` is a convenient way to access resources, but not every bean needs it. Implement this interface only when the bean really needs to load resources dynamically. Otherwise, a simpler way is to inject a property of type `Resource` directly.

6. **Lifecycle timing**
   + When a bean implements `ResourceLoaderAware`, `setResourceLoader` is called early in bean initialization. This makes sure later initialization and business logic can use the `ResourceLoader`. But do not access the `ResourceLoader` in the constructor, because it has not been set yet.

7. **Avoid overuse**
   + `ResourceLoaderAware` is a convenient way to access resources, but not every bean needs it. Implement this interface only when the bean really needs to load resources dynamically. Otherwise, a simpler way is to inject a property of type `Resource` directly.

8. **Difference from `ApplicationContextAware`**
   + `ApplicationContext` is itself a `ResourceLoader`, so implementing `ApplicationContextAware` also gives similar resource loading features. But if your bean only needs resource loading and none of the other `ApplicationContext` features, it is better to implement only `ResourceLoaderAware`, to reduce coupling.

### 9. Summary

#### Best Practices Summary

1. **Entry class**
   + It uses the `AnnotationConfigApplicationContext` class to start the Spring application. This is a way to configure the Spring container with Java-based annotations. The context is initialized with the `MyConfiguration` class as the configuration class. Then it gets a bean of type `MyResourceLoaderAware` from the Spring context. Finally, it calls the `getResource` method with a given path.

2. **Configuration class**
   + `MyConfiguration` is marked with `@Configuration`, so it is a Spring configuration class. In it, the `@Bean` annotation defines a bean of type `MyResourceLoaderAware`. This makes sure `MyResourceLoaderAware` is managed by the Spring container and the `ResourceLoader` is injected correctly.

3. **Resource loading implementation**
   + The `MyResourceLoaderAware` class implements `ResourceLoaderAware`, so the Spring container injects a `ResourceLoader` automatically when the bean is initialized. The `getResource` method uses the injected `ResourceLoader` to load the resource at the given path, then reads and prints its content.

4. **Result**
   + When the application runs, `MyResourceLoaderAware` loads the content from the given resource path and prints "hello world" to the console.

#### Source Code Analysis Summary

1. **Startup and context initialization**
   + `AnnotationConfigApplicationContext` creates a Java annotation-based Spring container, with `MyConfiguration` as the configuration. The `MyResourceLoaderAware` bean is taken from the context and its `getResource` method is called.

2. **Configuration class and bean registration**
   + In the `MyConfiguration` class, the `@Bean` annotation registers a bean of type `MyResourceLoaderAware`.

3. **Context refresh and bean instantiation**
   + In the context's `refresh` method, `finishBeanFactoryInitialization` is called to instantiate all remaining non-lazy singleton beans. In this method, `preInstantiateSingletons` is called to instantiate all non-lazy singleton beans up front.

4. **Getting and creating beans**
   + The `getBean` method is used to really get the bean, which may trigger bean creation. In `doGetBean`, if the bean has not been created yet, it tries to create a new instance, handles dependencies, and returns the right bean instance.

5. **Creating and caching singleton beans**
   + In `getSingleton`, it first tries to get the bean instance from the singleton cache. If it is not created yet, it creates a new instance with the given `ObjectFactory` and stores it in the cache.

6. **Bean initialization**
   + After the bean is created, it is initialized. In `initializeBean`, the `BeanPostProcessor` logic is applied to specific beans.

7. **Handling Aware interfaces**
   + `ApplicationContextAwareProcessor` handles beans that implement `Aware` interfaces. For beans that implement `ResourceLoaderAware`, a `ResourceLoader` instance is injected.

8. **Custom logic**
   + In the `MyResourceLoaderAware` class, the injected `ResourceLoader` is used to load the resource and print its content.