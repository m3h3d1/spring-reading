## EnvironmentAware

- [EnvironmentAware](#environmentaware)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133915522) 📚 **Contents** - [All Articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [EnvironmentAware source code](https://github.com/xuchengsheng/spring-reading/tree/master/spring-aware/spring-aware-environmentAware)

### 2. Interface Description

The `EnvironmentAware` interface lets beans access the `Environment` object. It is a callback interface. When a bean that implements it is managed by the Spring container, the container sets the `Environment` object on that bean.

### 3. Interface Source

`EnvironmentAware` is a core Spring interface, available since 3.1. Spring automatically injects an `Environment` instance into any object that implements `EnvironmentAware`.

```java
/**
 * Interface to be implemented by any bean that wishes to be notified of the Environment it runs in.
 * 
 * @author Chris Beams
 * @since 3.1
 * @see org.springframework.core.env.EnvironmentCapable
 */
public interface EnvironmentAware extends Aware {

    /**
     * Set the Environment that this component runs in.
     */
    void setEnvironment(Environment environment);
}
```

### 4. Main Features

1. **Access environment properties**
   + By implementing `EnvironmentAware`, beans can access the `Environment` object of the application context directly. They can read environment properties, which may come from many sources, such as system properties, JVM arguments, operating system environment variables, and property files.

2. **Identify the runtime environment**
   + A bean can use the `Environment` object to check the currently active Spring profiles. This lets the bean act or configure itself differently for different environments (such as development, test, and production).

3. **Automatic callback**
   + When the Spring container sees that a bean implements `EnvironmentAware`, it calls `setEnvironment` automatically and passes in the current `Environment` object. You do not need to set or get it yourself.

4. **Framework-level integration**
   + This interface gives a standard way for framework-level code (such as other Spring components and third-party libraries) to access and integrate with the `Environment` object, without relying on a specific injection strategy or other mechanism.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyEnvironmentAware` from the Spring context, calls the `getAppProperty` method, and prints the result.

```java
public class EnvironmentAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyEnvironmentAware environmentAware = context.getBean(MyEnvironmentAware.class);
        System.out.println("AppProperty = " + environmentAware.getAppProperty());
    }
}
```

The `@Bean` annotation defines a bean here, to make sure the Spring container creates `MyEnvironmentAware`. The `@PropertySource` annotation loads properties from the `application.properties` file on the classpath. So we can define properties in this file and read them through the `Environment` object in the application.

```java
@Configuration
@PropertySource("classpath:application.properties")
public class MyConfiguration {

    @Bean
    public MyEnvironmentAware myEnvironmentAware(){
        return new MyEnvironmentAware();
    }

}
```

The `MyEnvironmentAware` class implements `EnvironmentAware` and overrides `setEnvironment`, so it gets the `Environment` object when the Spring container initializes it. After that, we can use the `getPropertyValue` method to query any property in `application.properties`.

```java
public class MyEnvironmentAware implements EnvironmentAware {

    private String appProperty;

    @Override
    public void setEnvironment(Environment environment) {
        this.appProperty = environment.getProperty("app.xcs.property");
    }

    public String getAppProperty() {
        return appProperty;
    }
}
```

The output shows that the `EnvironmentAware` interface works together with the `application.properties` file. We read configuration properties through the Spring environment.

```java
AppProperty = Hello from EnvironmentAware!
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: EnvironmentAware sequence diagram
    participant EnvironmentAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ApplicationContextAwareProcessor
    participant MyEnvironmentAware
    
    EnvironmentAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
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
    ApplicationContextAwareProcessor->>MyEnvironmentAware:setEnvironment(environment)<br>Set the environment
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>EnvironmentAwareApplication:Initialization complete
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `MyEnvironmentAware` from the Spring context, calls the `getAppProperty` method, and prints the result.

```java
public class EnvironmentAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyEnvironmentAware environmentAware = context.getBean(MyEnvironmentAware.class);
        System.out.println("AppProperty = " + environmentAware.getAppProperty());
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
	if (bean instanceof EnvironmentAware) {
        ((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
    }
    // ... [code omitted for brevity]
}
```

Finally, execution reaches our own logic. The `MyEnvironmentAware` class implements `EnvironmentAware` and overrides `setEnvironment`, so it gets the `Environment` object when the Spring container initializes it. After that, we can use the `getPropertyValue` method to query any property in `application.properties`.

```java
public class MyEnvironmentAware implements EnvironmentAware {

    private String appProperty;

    @Override
    public void setEnvironment(Environment environment) {
        this.appProperty = environment.getProperty("app.xcs.property");
    }

    public String getAppProperty() {
        return appProperty;
    }
}
```

### 8. Notes

1. **Do not overuse it**
   + `EnvironmentAware` gives a bean direct access to the `Environment`, but that does not mean every bean should use it. When possible, first consider Spring property injection, such as `@Value`.

2. **Avoid hard-coded property keys**
   + When you read properties from the `Environment`, avoid hard-coding the keys in code. It is better to define the keys as constants or in external configuration.

3. **Handle missing properties**
   + When you read a property from the `Environment` and it does not exist, the `Environment` may return `null`. Handle this case properly in your code, or use the default-value methods that `Environment` provides.

4. **Remember the active profiles**
   + The `Environment` lets you query the currently active profiles. Make sure you know which profiles are active, especially when you use profile-specific properties.

5. **Understand the Environment hierarchy**
   + The `Environment` object may get properties from many sources (such as system properties, environment variables, and configuration files). Learn the priority and loading order of these sources, so you know which value wins when there is a conflict.

### 9. Summary

#### Best Practices Summary

1. **Startup process**
   + With `EnvironmentAwareApplication` as the main entry, we use `AnnotationConfigApplicationContext` to start the Spring context and load `MyConfiguration` as the configuration class.

2. **Loading properties**
   + In the `MyConfiguration` class, the `@PropertySource` annotation loads properties from the `application.properties` file on the classpath into the Spring environment.

3. **Registering the bean**
   + In the `MyConfiguration` class, we define a bean `MyEnvironmentAware`. This makes sure the `MyEnvironmentAware` object is created and managed by Spring when the container starts.

4. **Accessing environment properties**
   + The `MyEnvironmentAware` class implements `EnvironmentAware`. So when the Spring container initializes the bean, it calls `setEnvironment` automatically and injects the current `Environment` object. We use this method to read the `app.xcs.property` property and store its value in the private field `appProperty`.

5. **Showing the property**
   + Finally, in the `EnvironmentAwareApplication` main program, we get the `MyEnvironmentAware` bean from the Spring context, call `getAppProperty` to get the property value, and print it to the console.

6. **Output**
   + The result is "AppProperty = Hello from EnvironmentAware!". This shows that the `EnvironmentAware` interface works together with the `application.properties` file, and that we read configuration properties through the Spring environment.

#### Source Code Analysis Summary

1. **Application startup**
   + With `EnvironmentAwareApplication` as the entry, `AnnotationConfigApplicationContext` initializes the Spring context and loads `MyConfiguration` as the configuration class.

2. **Loading properties**
   + In the `MyConfiguration` class, the `@PropertySource` annotation loads properties from the `application.properties` file into the Spring environment.

3. **Bean registration and initialization**
   + In the context's `refresh()` method, `finishBeanFactoryInitialization()` makes sure all non-lazy singleton beans are instantiated. In `preInstantiateSingletons()`, this is done by calling `getBean()` in a loop. That call triggers bean creation, initialization, and dependency injection.

4. **Bean post-processing and "awareness"**
   + During bean initialization, `ApplicationContextAwareProcessor` checks for beans that implement Aware interfaces and calls their specific methods. For beans that implement `EnvironmentAware`, it calls `setEnvironment()` and passes in the current `Environment` object.

5. **Handling our own bean**
   + In its `setEnvironment()` method, `MyEnvironmentAware` reads the `app.xcs.property` property from the `Environment` object and stores it in its private field `appProperty`.

6. **Application output**
   + In the main method of `EnvironmentAwareApplication`, the `MyEnvironmentAware` bean is taken from the Spring context, its `getAppProperty()` method is called, and the property value is printed to the console.