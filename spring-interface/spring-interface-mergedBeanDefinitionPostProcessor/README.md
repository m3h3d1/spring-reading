## MergedBeanDefinitionPostProcessor

- [MergedBeanDefinitionPostProcessor](#mergedbeandefinitionpostprocessor)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133845274) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [MergedBeanDefinitionPostProcessor source](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)

### 2. Interface Description

`MergedBeanDefinitionPostProcessor` is a Spring interface. It is used to post-process a bean definition after it has been merged, and before the bean is instantiated. It extends `BeanPostProcessor` and adds the ability to handle merged bean definitions.

### 3. Interface Source

`MergedBeanDefinitionPostProcessor` is a core Spring interface, available since version 2.5. Its main method is `postProcessMergedBeanDefinition`. It gives us a chance to customize or inspect the merged bean definition, for example to apply custom annotations, change bean metadata, or add custom behavior based on the merged bean definition.

```java
/**
 * Callback interface for post-processing the merged bean definition at runtime.
 * BeanPostProcessor implementations may implement this sub-interface so that, when the Spring BeanFactory
 * creates a bean instance, they can post-process the merged bean definition (a processed copy of the original bean definition).
 *
 * The #postProcessMergedBeanDefinition method can be used to introspect the bean definition,
 * for example to prepare some cached metadata before the post-processor bean is instantiated. It also allows
 * changing the bean definition, but only for definition properties that are actually meant for concurrent modification.
 * In essence, this applies only to operations defined on the RootBeanDefinition itself, not to the properties of its base classes.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#getMergedBeanDefinition
 */
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

	/**
	 * Post-process the given merged bean definition for the specified bean.
	 * @param beanDefinition the merged definition for the bean
	 * @param beanType the actual type of the managed bean instance
	 * @param beanName the name of the bean
	 * @see AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors
	 */
	void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);

	/**
	 * A bean definition with the specified name has been reset.
	 * This post-processor should clear any metadata for the affected bean.
	 * The default implementation is empty.
	 * @param beanName the name of the bean
	 * @since 5.1
	 * @see DefaultListableBeanFactory#resetBeanDefinition
	 */
	default void resetBeanDefinition(String beanName) {
	}
}
```

### 4. Main Features

1. **Handle the merged bean definition**
   + In Spring, a bean can inherit the configuration of another bean. This produces a "merged" bean definition. It includes the properties of the original bean definition and of any parent bean definitions. `MergedBeanDefinitionPostProcessor` lets us run custom logic based on this merged definition, before the bean is instantiated and initialized.

2. **Cache metadata**
   + This interface is often used to inspect bean definitions and cache related metadata. This speeds up later bean instantiation and initialization. For example, Spring's `AutowiredAnnotationBeanPostProcessor` uses it to cache information about `@Autowired` and `@Value` annotations.

3. **Change the merged bean definition**
   + This is not the main use case, but `MergedBeanDefinitionPostProcessor` also allows changing the merged bean definition. Do this with care. It is usually limited to definition properties that are meant for concurrent modification.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a context that configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we get a bean of type `MyBean` from the Spring context and print the value returned by `getMessage`.

```java
public class MergedBeanDefinitionPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBean bean = context.getBean(MyBean.class);
        System.out.println("message = " + bean.getMessage());
    }
}
```

The `@Bean` annotation defines two beans here. This makes sure the Spring container handles `MyBean` and `MergedBeanDefinitionPostProcessor`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MergedBeanDefinitionPostProcessor myBeanPostProcessor() {
        return new MyMergedBeanDefinitionPostProcessor();
    }

    @Bean
    public MyBean myBean() {
        return new MyBean();
    }
}
```

`MyMergedBeanDefinitionPostProcessor` implements `MergedBeanDefinitionPostProcessor`. It handles fields marked with the custom annotation `MyValue`. In `postProcessMergedBeanDefinition`, it checks every field for a `MyValue` annotation. If there is one, it reads the annotation value and stores the field and the value in the `defaultValues` map. In `postProcessAfterInitialization`, it checks whether `metadata` contains the name of this bean. If it does, the bean has fields to handle, so it checks the current value of each field. If the value is null, it sets the field to the value from the annotation.

```java
public class MyMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor {

    /**
     * Records the metadata
     */
    private Map<String, Map<Field, String>> metadata = new HashMap<>();

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        Field[] fields = beanType.getDeclaredFields();
        Map<Field, String> defaultValues = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyValue.class)) {
                MyValue annotation = field.getAnnotation(MyValue.class);
                defaultValues.put(field, annotation.value());
            }
        }
        if (!defaultValues.isEmpty()) {
            metadata.put(beanName, defaultValues);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (metadata.containsKey(beanName)) {
            Map<Field, String> defaultValues = metadata.get(beanName);
            for (Map.Entry<Field, String> entry : defaultValues.entrySet()) {
                Field field = entry.getKey();
                String value = entry.getValue();
                try {
                    field.setAccessible(true);
                    if (field.get(bean) == null) {
                        field.set(bean, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
```

A simple annotation class.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MyValue {
    String value();
}
```

A simple bean class.

```java
public class MyBean {

    @MyValue("hello world")
    private String message;

    public String getMessage() {
        return message;
    }
}
```

When we run it, `MyBean` is instantiated and initialized. The `message` field was not set explicitly, so `MyMergedBeanDefinitionPostProcessor` sets it to the default value from the `MyValue` annotation, "hello world".

```
message = hello world
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
        Title: MergedBeanDefinitionPostProcessor Sequence Diagram
    participant MergedBeanDefinitionPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MyMergedBeanDefinitionPostProcessor
    
    MergedBeanDefinitionPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>Refresh the context
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>Initialize the bean factory
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>Instantiate singletons
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)<br>Get the bean
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>Do the work of getting the bean
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)<br>Get the singleton bean
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()<br>Get the bean instance
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)<br>Create the bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)<br>Do the work of creating the bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>Apply the post-processors
    AbstractAutowireCapableBeanFactory->>MyMergedBeanDefinitionPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>Handle the merged definition
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>MergedBeanDefinitionPostProcessorApplication:Initialization done
~~~

### 7. Source Code Analysis

```java
public class MergedBeanDefinitionPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyBean bean = context.getBean(MyBean.class);
        System.out.println("message = " + bean.getMessage());
    }
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor runs three steps. We focus on the `refresh()` method.

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

In `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization`, the code calls `preInstantiateSingletons` of `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

```java
/**
 * Finish the initialization of this context's bean factory, initializing all remaining singleton beans.
 * 
 * @param beanFactory the bean factory to initialize
 */
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Instantiate all remaining non-lazy singleton beans.
    beanFactory.preInstantiateSingletons();
}
```

The main goal of `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` is to instantiate all non-lazy singleton beans ahead of time. It is triggered after the Spring context finishes its setup, so every singleton bean is created and initialized correctly. Its core action is `getBean(beanName)`. For every singleton bean defined in the container, it calls `getBean`. This triggers instantiation, initialization, and dependency injection of the bean. If the bean was not created before, this call instantiates and initializes it.

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

In `org.springframework.beans.factory.support.AbstractBeanFactory#getBean()`, the code calls `doGetBean` to do the actual work of creating the bean. It passes the bean name and some default values. `doGetBean` does most of the work, such as finding the bean definition, creating the bean (if it does not exist yet), and handling dependencies.

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

In `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`, the code first checks whether the requested bean is a singleton that already exists. If not, it creates a new instance. On the way, it handles possible problems such as circular references, and makes sure the returned bean has the right type. This is a core part of bean lifecycle management in the Spring container.

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
        // Some beans (for example FactoryBeans) may need extra processing to get the real bean instance
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [code omitted for brevity]

    // Make sure the returned bean matches the requested type
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

The main job of `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()` is to get an existing bean from the singleton cache, or to create a new one with the given `ObjectFactory`. This is the key part that makes a bean a singleton in the Spring container.

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // The bean name must not be null
    Assert.notNull(beanName, "Bean name must not be null");

    // Synchronize access to the singleton cache to stay thread-safe
    synchronized (this.singletonObjects) {
        // Get the singleton from the cache
        Object singletonObject = this.singletonObjects.get(beanName);

        // If it is not in the cache
        if (singletonObject == null) {
            // ... [code omitted for brevity]

            try {
                // Use the factory to create a new singleton instance
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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()`, the main logic calls `doCreateBean`. This is where the bean is really instantiated, populated, and initialized. The method returns the new bean instance.

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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`, the merged bean definition is processed before the bean is instantiated. This is where `MergedBeanDefinitionPostProcessors` can step in and change the merged bean definition. To make sure post-processing runs only once for each bean definition, there is a `postProcessed` flag. If the flag is `false`, `applyMergedBeanDefinitionPostProcessors` is called to apply all `MergedBeanDefinitionPostProcessors`. To be thread-safe, this runs in a synchronized block on `mbd.postProcessingLock`. This makes sure concurrent bean creation requests do not post-process the same bean definition twice.

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {
    // ... [code omitted for brevity]
    // Allow MergedBeanDefinitionPostProcessors to modify the merged bean definition
    synchronized (mbd.postProcessingLock) {
        if (!mbd.postProcessed) {
            try {
                applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
            }
            catch (Throwable ex) {
                // ... [code omitted for brevity]
            }
            mbd.postProcessed = true;
        }
    }
    
    // ... [code omitted for brevity]
    
    // Return the created and initialized bean
    return exposedObject;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`, the code loops over every `MergedBeanDefinitionPostProcessor` and calls its `postProcessMergedBeanDefinition`. This gives a chance to customize or inspect the merged bean definition.

```java
protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
    for (MergedBeanDefinitionPostProcessor processor : getBeanPostProcessorCache().mergedDefinition) {
        processor.postProcessMergedBeanDefinition(mbd, beanType, beanName);
    }
}
```

Finally, our own logic runs. For each field, it checks for a `MyValue` annotation. If there is one, it reads the `MyValue` annotation from the field and stores the field and the annotation value in the `defaultValues` `Map`. If `defaultValues` is not empty (at least one field has `MyValue`), the `Map` is stored in `metadata`, with the bean name as the key.

```java
public class MyMergedBeanDefinitionPostProcessor implements MergedBeanDefinitionPostProcessor {

    /**
     * Records the metadata
     */
    private Map<String, Map<Field, String>> metadata = new HashMap<>();

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        Field[] fields = beanType.getDeclaredFields();
        Map<Field, String> defaultValues = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(MyValue.class)) {
                MyValue annotation = field.getAnnotation(MyValue.class);
                defaultValues.put(field, annotation.value());
            }
        }
        if (!defaultValues.isEmpty()) {
            metadata.put(beanName, defaultValues);
        }
    }
}
```

### 8. Notes

1. **When it is called**
   + `postProcessMergedBeanDefinition` is called while the bean is in a "half-instantiated" state. More exactly, the bean instance has been created, but property injection and init methods have not run yet. So we should not try to access the bean instance in this method.

2. **Do not change immutable properties**
   + We can change the `RootBeanDefinition` in `postProcessMergedBeanDefinition`, but only change the properties that are meant to be mutable. For example: the bean class name, constructor argument values, lazy-init flag, dependency information, scope, and so on.

3. **Performance impact**
   + If `postProcessMergedBeanDefinition` does heavy work, it can slow down application startup, because it is called while every bean is created.

4. **Avoid infinite recursion**
   + If we try to get other beans in `postProcessMergedBeanDefinition`, that may trigger the creation of those beans, which calls `postProcessMergedBeanDefinition` again. Be careful to avoid this infinite recursion.

### 9. Summary

#### Best Practices Summary

1. **Entry class**
   + `MergedBeanDefinitionPostProcessorApplication` is the main entry of the application. It uses `AnnotationConfigApplicationContext` to initialize and configure the Spring container, and gives it the configuration class `MyConfiguration`.

2. **Configuration class**
   + In `MyConfiguration`, we define two beans: the custom `MyMergedBeanDefinitionPostProcessor` and an instance of the simple `MyBean` class.

3. **Custom post-processor**
   + `MyMergedBeanDefinitionPostProcessor` implements `MergedBeanDefinitionPostProcessor`. It lets us handle and change the bean definition before the bean is instantiated. In this example, we check whether the fields of the bean have a `MyValue` annotation. If they do, we store the field name and the annotation value in a map. After the bean is instantiated and initialized, we check whether a field needs a value. If the current value of the field is `null`, we set it to the value from the `MyValue` annotation.

4. **Custom annotation**
   + `MyValue` is a simple custom annotation. It sets the default value of a field.

5. **Target bean**
   + `MyBean` is a simple Java class. Its field `message` is marked with `MyValue("hello world")`. So if `message` is not set explicitly when the Spring container initializes this bean, it uses the default value "hello world" from the `MyValue` annotation.

6. **Result**
   + When the application runs, the Spring container instantiates and initializes `MyBean`. The `message` field was not set explicitly, so `MyMergedBeanDefinitionPostProcessor` sets it to the default value "hello world" from `MyValue`. Finally, the application prints "message = hello world".

#### Source Code Analysis Summary

1. **Entry class**
   + The main entry of the application is `MergedBeanDefinitionPostProcessorApplication`. It uses `AnnotationConfigApplicationContext` to initialize the Spring container and passes in the configuration class `MyConfiguration`.

2. **Initialize the Spring container**
   + In the constructor of `AnnotationConfigApplicationContext`, besides some basic setup, the main step is calling `refresh()` to initialize the container.

3. **Container refresh**
   + `refresh()` is defined in `AbstractApplicationContext` and finishes the container initialization. `finishBeanFactoryInitialization(beanFactory)` is used to instantiate all non-lazy singleton beans.

4. **Instantiate singleton beans**
   + `preInstantiateSingletons()` is called in `DefaultListableBeanFactory` to instantiate all non-lazy singleton beans ahead of time. It calls `getBean(beanName)` to instantiate and initialize each bean.

5. **Get a bean**
   + `getBean()` is defined in `AbstractBeanFactory`. It finally calls `doGetBean()` to do the actual bean creation.

6. **Create a bean**
   + `doGetBean()` handles bean lookup, creation, and dependencies. If the requested bean is a singleton that is not created yet, it uses `getSingleton()` to get it from the singleton cache or to create a new instance.

7. **Handle singleton beans**
   + In `DefaultSingletonBeanRegistry`, `getSingleton()` gets an existing bean from the singleton cache, or creates a new instance with an `ObjectFactory`.

8. **Actual bean creation**
   + In `AbstractAutowireCapableBeanFactory`, `createBean()` is the entry of bean creation. It mainly calls `doCreateBean()`. In `doCreateBean()`, `applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName)` lets `MergedBeanDefinitionPostProcessors` change the merged bean definition.

9. **Apply the merged bean definition post-processors**
   + `applyMergedBeanDefinitionPostProcessors()` loops over every `MergedBeanDefinitionPostProcessor` and calls its `postProcessMergedBeanDefinition()`. This gives a chance to customize or inspect each merged bean definition.

10. **Custom post-processor logic**
   + In our example, `MyMergedBeanDefinitionPostProcessor` handles the fields marked with `MyValue`. In `postProcessMergedBeanDefinition()`, it checks every field for a `MyValue` annotation and collects the default values for those fields.