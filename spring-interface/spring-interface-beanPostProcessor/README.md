## BeanPostProcessor

- [BeanPostProcessor](#beanpostprocessor)
  - [1. Overview](#1-overview)
  - [2. Interface Source](#2-interface-source)
  - [3. Main Features](#3-main-features)
  - [4. Best Practices](#4-best-practices)
  - [5. Sequence Diagram](#5-sequence-diagram)
  - [6. Source Code Analysis](#6-source-code-analysis)
  - [7. Notes](#7-notes)
  - [8. Summary](#8-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

The `BeanPostProcessor` interface lets you work with and change newly created beans.

### 2. Interface Source

`BeanPostProcessor` is a core Spring interface, available since 10.10.2003. It defines two main methods: `postProcessBeforeInitialization` and `postProcessAfterInitialization`. They are called before and after a bean is initialized. The source shows that `BeanPostProcessor` supports registration and ordering.

What is registration? There are two ways. The first is automatic registration. When you start an `AnnotationConfigApplicationContext`, Spring scans all defined beans. If a bean implements `BeanPostProcessor`, Spring detects and registers it. The second is manual registration, by calling `addBeanPostProcessor` on `ConfigurableBeanFactory`.

What is ordering? If a `BeanPostProcessor` implements `org.springframework.core.PriorityOrdered` or `org.springframework.core.Ordered`, Spring sorts the processors by priority or order. In an `AnnotationConfigApplicationContext`, Spring uses these two interfaces to decide the execution order. But if you register a `BeanPostProcessor` manually in `ConfigurableBeanFactory`, Spring applies the processors in the order you registered them. In that case, any `PriorityOrdered` or `Ordered` ordering is ignored.

```java
/**
 * Factory hook that allows for custom modification of new bean instances.
 * For example, checking for marker interfaces or wrapping beans with proxies.
 *
 * Typically, post-processors that populate beans via marker interfaces
 * or the like will implement #postProcessBeforeInitialization,
 * while post-processors that wrap beans with proxies will normally
 * implement #postProcessAfterInitialization.
 *
 * Registration
 * An ApplicationContext can autodetect BeanPostProcessor beans in its bean definitions
 * and apply those post-processors to any beans subsequently created. A plain BeanFactory
 * allows for programmatic registration of post-processors, applying them to all beans
 * created through the bean factory.
 *
 * Ordering
 * BeanPostProcessor beans that are autodetected in an ApplicationContext will be ordered
 * according to org.springframework.core.PriorityOrdered and
 * org.springframework.core.Ordered semantics. In contrast, BeanPostProcessor
 * beans that are registered programmatically with a BeanFactory will be applied in the
 * order of registration; any ordering semantics expressed through implementing the
 * PriorityOrdered or Ordered interface will be ignored for programmatically registered
 * post-processors. Furthermore, the org.springframework.core.annotation.Order @Order
 * annotation is not taken into account for BeanPostProcessor beans.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor
 * @see DestructionAwareBeanPostProcessor
 * @see ConfigurableBeanFactory#addBeanPostProcessor
 * @see BeanFactoryPostProcessor
 */
public interface BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor to the given new bean instance before any bean
	 * initialization callbacks (like InitializingBean's afterPropertiesSet
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 * The default implementation returns the given bean as-is.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if null, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * Apply this BeanPostProcessor to the given new bean instance after any bean
	 * initialization callbacks (like InitializingBean's afterPropertiesSet
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 * In case of a FactoryBean, this callback will be invoked for both the FactoryBean
	 * instance and the objects created by the FactoryBean (as of Spring 2.0).
	 * The post-processor can decide whether to apply to either the FactoryBean or created
	 * objects or both through corresponding bean instanceof FactoryBean checks.
	 * This callback will also be invoked after a short-circuiting triggered by a
	 * InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation method,
	 * in contrast to all other BeanPostProcessor callbacks.
	 * The default implementation returns the given bean as-is.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if null, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.FactoryBean
	 */
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
```

### 3. Main Features

**Modify bean properties**: Before or after initialization, you can change the properties of a bean. For example, you can set default values for some properties based on conditions.

**Validate bean state**: After initialization, you can check the state of a bean and make sure it meets certain conditions or constraints.

**Return a proxy bean**: The most common use is to return a proxy that wraps the original bean. For example, Spring AOP uses this to create proxies for target beans, which makes aspects work.

**Change the type of the returned bean**: You can fully replace a bean instance and return an object of a different type. This is an advanced use, but it may be needed in some cases.

### 4. Best Practices

First, the entry class. The context is `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we get a bean of type `MyService` from the Spring context and print the value returned by its `show` method.

```java
public class BeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyService myService = context.getBean(MyService.class);
        System.out.println(myService.show());
        context.close();
    }
}
```

Here we use the `@Bean` annotation to define two beans, so that the Spring container handles both `MyService` and `MyBeanPostProcessor`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }

    @Bean
    public BeanPostProcessor myBeanPostProcessor() {
        return new MyBeanPostProcessor();
    }
}
```

In `postProcessBeforeInitialization`, we add the prefix `Prefix:` to the `message` property of `MyServiceImpl`. In `postProcessAfterInitialization`, we add the suffix `:Suffix` to the `message` property of `MyServiceImpl`.

```java
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyServiceImpl) {
            MyServiceImpl myService = (MyServiceImpl) bean;
            myService.setMessage("Prefix: " + myService.getMessage());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyServiceImpl) {
            MyServiceImpl myService = (MyServiceImpl) bean;
            myService.setMessage(myService.getMessage() + " :Suffix");
        }
        return bean;
    }
}
```

A simple interface.

```java
public interface MyService {
    String show();
}
```

A simple implementation class.

```java
public class MyServiceImpl implements MyService{

    private String message = "Hello from MyService";

    @Override
    public String show() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

The output shows that `MyBeanPostProcessor` changed the `message` property of the `MyServiceImpl` bean. It added the prefix before initialization and the suffix after initialization.

```java
Prefix: Hello from MyService :Suffix
```

### 5. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: BeanPostProcessor Sequence Diagram
    participant BeanPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MyBeanPostProcessor
    
    BeanPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)
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
    AbstractAutowireCapableBeanFactory->>MyBeanPostProcessor: postProcessBeforeInitialization(bean,beanName)
    MyBeanPostProcessor-->>AbstractAutowireCapableBeanFactory: Return bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsAfterInitialization(existingBean,beanName)
    AbstractAutowireCapableBeanFactory->>MyBeanPostProcessor: postProcessAfterInitialization(bean,beanName)
    MyBeanPostProcessor-->>AbstractAutowireCapableBeanFactory: Return bean
	AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return bean
	AbstractBeanFactory-->>DefaultListableBeanFactory:Return bean
	AnnotationConfigApplicationContext->>BeanPostProcessorApplication: Initialization complete
~~~

### 6. Source Code Analysis

First, the entry class. The context is `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we get a bean of type `MyService` from the Spring context. Finally, we call the `show` method of the `MyService` object and print the result to the console.

```java
public class BeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyService myService = context.getBean(MyService.class);
        System.out.println(myService.show());
        context.close();
    }
}
```

The constructor `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` runs three steps. We focus on the `refresh()` method.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

In `org.springframework.context.support.AbstractApplicationContext#refresh`, we focus on `finishBeanFactoryInitialization(beanFactory)`. It instantiates all remaining non-lazy singleton beans. The other methods are not the focus of this article, so we skip them.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [code omitted for brevity]
    // Instantiate all remaining (non-lazy-init) singletons.
    finishBeanFactoryInitialization(beanFactory);
    // ... [code omitted for brevity]
}
```

The method `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization` calls `preInstantiateSingletons` in `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

```java
/**
 * Finish the initialization of this factory, instantiating all remaining
 * non-lazy-init singleton beans.
 * 
 * @param beanFactory the bean factory to initialize
 */
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Create all remaining non-lazy singleton beans.
    beanFactory.preInstantiateSingletons();
}
```

The main goal of `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` is to instantiate all non-lazy singleton beans in advance. Spring calls it after the context is set up, so that every singleton bean is created and initialized. The key step is `getBean(beanName)`. For each singleton bean defined in the container, it calls `getBean`. This triggers instantiation, initialization, and dependency injection. If the bean was not created before, this call creates and initializes it.

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

The method `org.springframework.beans.factory.support.AbstractBeanFactory#getBean()` calls `doGetBean` to do the real work of creating the bean. It passes the bean name and some default values. `doGetBean` does most of the work, such as finding the bean definition, creating the bean (if it does not exist yet), and handling dependencies.

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

In `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`, Spring first checks whether the requested bean is a singleton that already exists. If not, it creates a new instance. Along the way, it handles possible problems such as circular references, and makes sure the returned bean has the right type. This is a core part of bean lifecycle management in the Spring container.

```java
protected <T> T doGetBean(
        String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
        throws BeansException {
    // ... [code omitted for brevity]

    // Start creating the bean instance
    if (mbd.isSingleton()) {
        // If the bean is a singleton, try to get it from the singleton cache.
        // If it does not exist, create a new instance with a lambda.
        sharedInstance = getSingleton(beanName, () -> {
            try {
                // Try to create the bean instance
                return createBean(beanName, mbd, args);
            }
            catch (BeansException ex) {
                // ... [code omitted for brevity]
            }
        });
        // For some beans (such as FactoryBeans), more work is needed to get the real bean instance
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [code omitted for brevity]

    // Make sure the returned bean instance matches the requested type
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

The method `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()` gets an existing bean instance from the singleton cache, or creates a new one with the given `ObjectFactory`. This is the key part that makes sure a bean exists as a singleton in the Spring container.

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // Assert that the bean name is not null
    Assert.notNull(beanName, "Bean name must not be null");

    // Synchronize on the singleton cache to be thread-safe
    synchronized (this.singletonObjects) {
        // Get the singleton object from the cache
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

        // Return the singleton object
        return singletonObject;
    }
}
```

The main job of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()` is to call `doCreateBean`. That is where the bean is really instantiated, populated, and initialized. The method returns the newly created bean instance.

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {
    
    // ... [code omitted for brevity]
    
    try {
        // Normal bean instantiation, property injection, and initialization.
        // This is where the bean is really created.
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        // Log that the bean was created
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

The method `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` has two main steps. The first is property injection. The second is bean initialization. Together they make sure the bean is fully configured and ready.

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // Declare an object that may later hold the initialized bean or its proxy
    Object exposedObject = bean;

    // ... [code omitted for brevity]
    
    try {
        // Property injection: this step injects configured property values into the bean instance. For example, properties defined in XML or marked with @Autowired and @Value are injected here
        populateBean(beanName, mbd, instanceWrapper);

        // Bean initialization: this step runs the bean's init methods. It also calls postProcessBeforeInitialization and postProcessAfterInitialization of the BeanPostProcessors, which can process the bean further
        exposedObject = initializeBean(beanName, exposedObject, mbd);
    } 
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }

    // Return the created and initialized bean
    return exposedObject;
}
```

The core of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean()` is calling the callbacks `postProcessBeforeInitialization` and `postProcessAfterInitialization` of the `BeanPostProcessor` interface.

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
    // ... [code omitted for brevity]

    Object wrappedBean = bean;
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }

    // ... [code omitted for brevity]
    
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }

    return wrappedBean;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`, the method loops over every `BeanPostProcessor`. Each `postProcessBeforeInitialization` method gets a chance to change or enhance the bean.

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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization`, the method loops over every `BeanPostProcessor`. Each `postProcessAfterInitialization` method gets a chance to change or enhance the bean.

```java
@Override
public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
    throws BeansException {

    Object result = existingBean;
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
        Object current = processor.postProcessAfterInitialization(result, beanName);
        if (current == null) {
            return result;
        }
        result = current;
    }
    return result;
}
```

Finally, our own logic runs. In `postProcessBeforeInitialization`, it adds the prefix `Prefix:` to the `message` property of `MyServiceImpl`. In `postProcessAfterInitialization`, it adds the suffix `:Suffix` to the `message` property of `MyServiceImpl`.

```java
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyServiceImpl) {
            MyServiceImpl myService = (MyServiceImpl) bean;
            myService.setMessage("Prefix: " + myService.getMessage());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof MyServiceImpl) {
            MyServiceImpl myService = (MyServiceImpl) bean;
            myService.setMessage(myService.getMessage() + " :Suffix");
        }
        return bean;
    }
}
```

### 7. Notes

**Execution order**: The Spring container may have many `BeanPostProcessor`s. Their order is decided by their `Ordered` value (if they implement the `Ordered` interface). If several `BeanPostProcessor`s work on the same bean, the order can change the result.

**Return value**: Both `postProcessBeforeInitialization` and `postProcessAfterInitialization` can return a new bean to replace the original one. If we return `null`, the following `BeanPostProcessor`s are not called.

**Watch performance**: The `BeanPostProcessor` methods are called for every bean in the container. They can slow down startup. Keep their code as fast as possible.

**Do not modify stateless beans**: Stateless beans (such as utility or helper classes) should not be changed in a `BeanPostProcessor`. It usually makes no sense and adds extra cost.

**Avoid throwing exceptions in `BeanPostProcessor`**: Do not throw exceptions in `BeanPostProcessor` methods. This can make the Spring container fail to start, or make other beans fail to initialize.

### 8. Summary

#### Best Practices Summary

**Entry class `BeanPostProcessorApplication`:**

This class first creates a Spring context based on Java configuration. After that, it gets a bean of type `MyService` from the context, calls its `show` method, and prints the returned value.

**Configuration class `MyConfiguration`:**

This is a Java-based configuration class. It defines two beans: `MyService` and `MyBeanPostProcessor`. This makes sure both beans are created and managed when the Spring container starts.

**Implementation class `MyBeanPostProcessor`:**

This class implements the `BeanPostProcessor` interface and overrides `postProcessBeforeInitialization` and `postProcessAfterInitialization`. In both methods, it checks whether the current bean is an instance of `MyServiceImpl`. If it is, it changes the `message` property of the bean:

- In `postProcessBeforeInitialization`, it adds the prefix `Prefix:` to `message`.
- In `postProcessAfterInitialization`, it adds the suffix `:Suffix` to `message`.

**Interface and implementation:**

The `MyService` interface defines a `show` method. `MyServiceImpl` is a simple implementation of it, with a `message` property.

**Output:**

When we run the main program `BeanPostProcessorApplication`, the output shows that the `message` property of the `MyServiceImpl` bean was changed. `MyBeanPostProcessor` did this. It added the prefix before initialization and the suffix after initialization.

#### Source Code Analysis Summary

**Spring context initialization**: The constructor of `AnnotationConfigApplicationContext` initializes the Spring context. The `refresh()` method is the core of this process.

**Instantiating singleton beans**: In `refresh()`, `finishBeanFactoryInitialization(beanFactory)` instantiates all remaining non-lazy singleton beans.

**Pre-instantiation**: `preInstantiateSingletons()` pre-instantiates all non-lazy singleton beans. It calls `getBean` for each bean.

**Getting a bean**: `getBean()` gets the bean instance. The core is `doGetBean`, which handles instantiation, initialization, and dependency injection.

**Singleton guarantee**: When getting a singleton bean, if it is already in the singleton cache, it is returned directly. Otherwise, a new instance is created.

**Bean creation**: `createBean` calls `doCreateBean` to do the real instantiation, property population, and initialization.

**Property population and initialization**: `doCreateBean` has two main steps:

- `populateBean`: property injection.
- `initializeBean`: runs the bean's init methods and calls the `BeanPostProcessor` methods.

**BeanPostProcessor callbacks**:

- `initializeBean` calls `applyBeanPostProcessorsBeforeInitialization` and `applyBeanPostProcessorsAfterInitialization`. They process the bean before and after initialization.
- Every `BeanPostProcessor` takes part in these callbacks, so it can change or enhance the bean.

**Custom processing**:

- Finally, our own `BeanPostProcessor` (`MyBeanPostProcessor`) does its work. It adds `Prefix:` to `message` before initialization, and `:Suffix` to `message` after initialization.
