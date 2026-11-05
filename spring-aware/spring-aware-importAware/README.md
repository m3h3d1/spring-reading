## ImportAware

- [ImportAware](#importaware)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133915616) 📚 **Contents** - [All Articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [ImportAware source code]()

### 2. Interface Description

The `ImportAware` interface gives an imported class access to the class that imported it. When a class implements `ImportAware` and is imported into another configuration class with the @Import annotation, it can access the `AnnotationMetadata` of the class that imported it.

### 3. Interface Source

`ImportAware` is a core Spring interface, available since 3.1. Spring automatically injects an `AnnotationMetadata` instance into any object that implements `ImportAware`.

```java
/**
 * Interface to be implemented by any @Configuration class that wishes to be injected with the AnnotationMetadata of the @Configuration class that imported it.
 * Useful in conjunction with annotations that use Import as a meta-annotation.
 * 
 * @author Chris Beams
 * @since 3.1
 */
public interface ImportAware extends Aware {

	/**
	 * Set the annotation metadata of the importing @Configuration class.
	 */
	void setImportMetadata(AnnotationMetadata importMetadata);

}
```

### 4. Main Features

1. **Access the annotation metadata of the importing class**
   + When a class implements `ImportAware` and is imported with `@Import` or in another specific way, the Spring container calls its `setImportMetadata` method automatically and passes in the `AnnotationMetadata` of the class that imported it.

2. **Conditional behavior**
   + With access to the annotation metadata of the importing class, you can act on specific conditions. For example, depending on the annotation attributes on the importing class, you can decide whether to register a bean, or set specific property values on a bean.

3. **Framework and library development**
   + `ImportAware` is used inside the Spring framework and in some third-party libraries for specific initialization and configuration. For example, the auto-configuration of some features can be adjusted by the annotation attributes on the configuration class that imports them.

4. **Better diagnostics and debugging**
   + The metadata of the importing class gives more context, which can help when you diagnose complex configuration problems.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `String` from the Spring context and prints it.

```java
public class ImportAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        String customBean  = context.getBean(String.class);
        System.out.println(customBean);
    }
}
```

 `MyConfiguration` is a Spring configuration class. It enables the `ImportAware` feature through the `@EnableXcs` annotation.

```java
@Configuration
@EnableXcs
public class MyConfiguration {

}
```

`EnableXcs` is an annotation. `@Import(MyImportAware.class)` configures or changes the Spring context in some way.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MyImportAware.class)
public @interface EnableXcs {
    
}
```

`MyImportAware` implements `ImportAware`, so it checks whether the configuration class that imported it has the `@EnableXcs` annotation. If it does, it continues and registers a `String` bean named `customBean` in the Spring context. If the `@EnableXcs` annotation is missing, it throws an exception.

```java
public class MyImportAware implements ImportAware {

    private AnnotationAttributes enableXcs;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableXcs = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableXcs.class.getName(), false));
        if (this.enableXcs == null) {
            throw new IllegalArgumentException(
                    "@EnableXcs is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean
    public String customBean() {
        return "This is a custom bean!";
    }
}
```

The output shows that when we use the `@EnableXcs` annotation in the Spring context and run the program, the `MyImportAware` class is imported and processed. In the end, a `String` bean with the value "This is a custom bean!" is registered in the Spring container.

```java
This is a custom bean!
```

Now let's look at another case. We do not import `MyImportAware` through the `@EnableXcs` annotation. Instead, we import it directly in `MyConfiguration` with `@Import(MyImportAware.class)`.

```java
@Configuration
@Import(MyImportAware.class)
public class MyConfiguration {

}
```

The output shows that when we import `MyImportAware` directly with `@Import(MyImportAware.class)` and do not use `@EnableXcs`, `MyConfiguration` has no `@EnableXcs` annotation, so the value of `enableXcs` is null. Because `enableXcs` is null, `MyImportAware` throws an `IllegalArgumentException`.

```java
Caused by: java.lang.IllegalArgumentException: @EnableXcs is not present on importing class com.xcs.spring.config.MyConfiguration
	at com.xcs.spring.config.MyImportAware.setImportMetadata(MyImportAware.java:19)
	at org.springframework.context.annotation.ConfigurationClassPostProcessor$ImportAwareBeanPostProcessor.postProcessBeforeInitialization(ConfigurationClassPostProcessor.java:484)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization(AbstractAutowireCapableBeanFactory.java:440)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1796)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:620)
	... 10 more
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: ImportAware sequence diagram
    participant ImportAwareApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant ImportAwareBeanPostProcessor
    participant MyImportAware
    
    ImportAwareApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName,bean,mbd)
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInitialization(existingBean,beanName)
    AbstractAutowireCapableBeanFactory->>ImportAwareBeanPostProcessor: postProcessBeforeInitialization(bean,beanName)
    ImportAwareBeanPostProcessor->>MyImportAware:setImportMetadata(importMetadata)<br>Set importMetadata
    ImportAwareBeanPostProcessor-->>AbstractAutowireCapableBeanFactory: Return the bean
	AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
	AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
	AnnotationConfigApplicationContext->>ImportAwareApplication: Initialization complete
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). The constructor receives one configuration class, `MyConfiguration`. Then it gets a bean of type `String` from the Spring context and prints it.

```java
public class ImportAwareApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        String customBean  = context.getBean(String.class);
        System.out.println(customBean);
    }
}
```

The constructor of `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` does three steps. We focus on the `refresh()` method.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`, the main job is bean initialization.

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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean()`, the core logic is to call back `postProcessBeforeInitialization` of the `BeanPostProcessors` interface.

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

The main job of `org.springframework.context.annotation.ConfigurationClassPostProcessor.ImportAwareBeanPostProcessor#postProcessBeforeInitialization` is to set the `AnnotationMetadata` of the importing class on beans that implement `ImportAware`. So any bean that implements `ImportAware` can know which class imported it, and see all the annotation information on that class.

```java
@Override
public Object postProcessBeforeInitialization(Object bean, String beanName) {
    if (bean instanceof ImportAware) {
        ImportRegistry ir = this.beanFactory.getBean(IMPORT_REGISTRY_BEAN_NAME, ImportRegistry.class);
        AnnotationMetadata importingClass = ir.getImportingClassFor(ClassUtils.getUserClass(bean).getName());
        if (importingClass != null) {
            ((ImportAware) bean).setImportMetadata(importingClass);
        }
    }
    return bean;
}
```

Finally, execution reaches our own logic. `MyImportAware` implements `ImportAware`, so it checks whether the configuration class that imported it has the `@EnableXcs` annotation. If it does, it continues and registers a `String` bean named `customBean` in the Spring context. If the `@EnableXcs` annotation is missing, it throws an exception.

```java
public class MyImportAware implements ImportAware {

    private AnnotationAttributes enableXcs;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableXcs = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableXcs.class.getName(), false));
        if (this.enableXcs == null) {
            throw new IllegalArgumentException(
                    "@EnableXcs is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Bean
    public String customBean() {
        return "This is a custom bean!";
    }
}
```

### 8. Notes

1. **Be clear about the need**
   + Before you implement `ImportAware`, make sure you really need to know which class imported your component, and that you need its annotation metadata. Avoid unnecessary complexity.

2. **The right context**
   + `ImportAware` only makes sense for classes imported with `@Import`. For beans registered in other ways (for example component scanning or XML configuration), `setImportMetadata` may not be called.

3. **Handle metadata with care**
   + When you access `AnnotationMetadata`, handle the case where an annotation or attribute does not exist, to avoid a null pointer exception.

4. **Watch the interaction with other `BeanPostProcessor`s**
   + Part of the `ImportAware` feature works through the `BeanPostProcessor` mechanism. If you use other `BeanPostProcessor`s in your application, make sure you understand how they interact and in what order they run.

5. **Do not overuse it**
   + `ImportAware` can add some flexibility, but do not use it where it is not needed. Overuse can make the configuration complex and hard to trace.

### 9. Summary

#### Best Practices Summary

1. **Initialization and run**
   + `AnnotationConfigApplicationContext` initializes a Spring context, loads the `MyConfiguration` class, and gets a bean of type `String` from the context.

2. **What the `@EnableXcs` annotation does**
   + `@EnableXcs` is a custom annotation. Its main job is to import the `MyImportAware` class through `@Import`, which turns on the `ImportAware` feature.

3. **The `MyImportAware` class and `ImportAware`**
   + `MyImportAware` implements `ImportAware`, so it can get the annotation information of the class that imported it. In `setImportMetadata`, `MyImportAware` checks whether the importing class has the `@EnableXcs` annotation. If it does, it continues and registers a `String` bean with the value "This is a custom bean!". If not, it throws an exception saying that `@EnableXcs` is missing on the importing class.

4. **Normal use**
   + When `MyConfiguration` uses the `@EnableXcs` annotation, the program runs normally, and the `String` bean taken from the context has the value "This is a custom bean!".

5. **Error case**
   + But if `MyConfiguration` imports `MyImportAware` directly with `@Import(MyImportAware.class)` and does not use `@EnableXcs`, `MyImportAware` does not find the `@EnableXcs` annotation and throws an exception.

#### Source Code Analysis Summary

1. **Application startup**
   + `AnnotationConfigApplicationContext` initializes the Spring context and loads the `MyConfiguration` class. The program tries to get a bean of type `String` from the Spring context.

2. **Context refresh**
   + When `AnnotationConfigApplicationContext` is constructed, it calls `refresh()`. This is the entry point of the initialization and refresh process of the Spring context.

3. **Instantiating beans**
   + It runs `finishBeanFactoryInitialization`, which pre-instantiates all non-lazy singleton beans in the context. For each bean, it calls `getBean`.

4. **Handling `ImportAware` beans**
   + If a bean implements `ImportAware`, the `postProcessBeforeInitialization` method sets the annotation metadata of the importing class on that bean. In our example, `MyImportAware` is such a bean.

5. **Checking `@EnableXcs`**
   + In the `setImportMetadata` method of `MyImportAware`, it checks whether the importing class has the `@EnableXcs` annotation. If it does, processing continues. If not, it throws an exception.

6. **Bean creation**
   + If the importing class has the `@EnableXcs` annotation, `MyImportAware` continues and defines a `String` bean. This is the bean we get from the context and print to the console.

7. **Exception handling**
   + If `MyImportAware` is imported directly with `@Import` and without `@EnableXcs`, an exception occurs, because `MyImportAware` expects the importing class to have the `@EnableXcs` annotation.