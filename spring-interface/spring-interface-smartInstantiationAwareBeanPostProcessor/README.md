## SmartInstantiationAwareBeanPostProcessor

- [SmartInstantiationAwareBeanPostProcessor](#smartinstantiationawarebeanpostprocessor)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133845401) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [SmartInstantiationAwareBeanPostProcessor source](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-smartInstantiationAwareBeanPostProcessor)

### 2. Interface Description

The `SmartInstantiationAwareBeanPostProcessor` interface gives finer control over the beans the Spring container creates. It is most useful for proxies and other advanced cases.

### 3. Interface Source

`SmartInstantiationAwareBeanPostProcessor` is a core Spring interface, introduced in Spring 2.0.3. It is mainly used inside the framework. Normally, implementing `BeanPostProcessor` or `InstantiationAwareBeanPostProcessorAdapter` is enough for custom needs.

```java
/**
 * Extension of the InstantiationAwareBeanPostProcessor interface,
 * adding a callback for predicting the eventual type of a processed bean.
 *
 * Note: This is a special-purpose interface, mainly for
 * internal use within the framework. In general, application-provided
 * post-processors should simply implement the plain BeanPostProcessor
 * interface or extend the InstantiationAwareBeanPostProcessorAdapter class.
 * New methods may be added to this interface even in point releases.
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see InstantiationAwareBeanPostProcessorAdapter
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

	/**
	 * Predict the type of the bean to be eventually returned from this processor's #postProcessBeforeInstantiation callback.
	 * The default implementation returns null.
	 * @param beanClass the raw class of the bean
	 * @param beanName the name of the bean
	 * @return the type of the bean, or null if not predictable
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	@Nullable
	default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	/**
	 * Determine the candidate constructors to use for the given bean.
	 * The default implementation returns null.
	 * @param beanClass the raw class of the bean (never null)
	 * @param beanName the name of the bean
	 * @return the candidate constructors, or null if none specified
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	@Nullable
	default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
			throws BeansException {

		return null;
	}

	/**
	 * Obtain a reference for early access to the specified bean, typically to resolve a circular reference.
	 * This callback gives post-processors a chance to expose a wrapper early, before the target bean instance is fully initialized.
	 * The exposed object should be equivalent to what #postProcessBeforeInitialization /
     * #postProcessAfterInitialization would expose otherwise. Note that the object returned by this method
     * will be used as the bean reference, unless the post-processor returns a different wrapper from those post-process callbacks.
	 * The default implementation returns the given bean as-is.
	 * @param bean the raw bean instance
	 * @param beanName the name of the bean
	 * @return the object to expose as bean reference (typically with the passed-in bean instance as default)
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
```

### 4. Main Features

1. **Predict the bean type (predictBeanType)**
   + This method predicts the final type of a bean before the bean is instantiated. It is useful with proxies and other type changes. For example, an AOP proxy may wrap a bean. This method can return the expected proxy type instead of the real target type. This helps Spring make better choices when it creates and wires beans, especially for type matching such as autowiring.

2. **Determine candidate constructors (determineCandidateConstructors)**
   + Before a bean is instantiated, this method decides which constructors to use for the bean. It lets us customize or change the default constructor selection of Spring. For example, when there are several constructors and we want to pick one with our own logic.

3. **Get an early bean reference (getEarlyBeanReference)**
   + This method gives a chance to expose a wrapper or proxy before a bean is fully initialized. It is especially useful for circular dependencies. When a bean is not fully initialized yet but another bean needs a reference to it, this method is called. We can then expose an early bean reference, possibly a proxy. The proxy stays valid after all initialization steps finish.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a context that configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor.

```java
public class SmartInstantiationAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

The `@Bean` annotation defines a bean here. This makes sure the Spring container handles `MySmartInstantiationAwareBeanPostProcessor`.

```java
@Configuration
@ComponentScan("com.xcs.spring")
public class MyConfiguration {

    @Bean
    public static MySmartInstantiationAwareBeanPostProcessor mySmartInstantiationAwareBeanPostProcessor(){
        return new MySmartInstantiationAwareBeanPostProcessor();
    }
}
```

This is our own `SmartInstantiationAwareBeanPostProcessor`. It overrides `determineCandidateConstructors`. If the class has one or more constructors with the `@MyAutowired` annotation, they are returned as candidates. If none is found, the post-processor looks for the default (no-argument) constructor. If there is no `@MyAutowired` constructor and no default constructor, all available constructors are returned as candidates. Then Spring can pick the most specific one.

```java
public class MySmartInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Override
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        // First, look for constructors annotated with @MyAutowired
        List<Constructor<?>> myAutowiredConstructors = Arrays.stream(beanClass.getConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(MyAutowired.class))
                .collect(Collectors.toList());

        if (!myAutowiredConstructors.isEmpty()) {
            return myAutowiredConstructors.toArray(new Constructor<?>[0]);
        }

        // Second, check for a default constructor
        try {
            Constructor<?> defaultConstructor = beanClass.getDeclaredConstructor();
            return new Constructor<?>[]{defaultConstructor};
        } catch (NoSuchMethodException e) {
            // No default constructor found, so continue to pick a suitable constructor
        }

        // Return all constructors and let Spring pick the most specific one
        return beanClass.getConstructors();
    }
}
```

Custom annotation

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface MyAutowired {
    
}
```

Two plain bean classes

```java
@Component
public class MyServiceA {

    public void execute() {
        System.out.println("MyServiceA executed");
    }
}

@Component
public class MyServiceB {

    public void execute() {
        System.out.println("MyServiceB executed");
    }
}
```

We define four constructors: a default constructor, one that takes only `MyServiceA`, one that takes only `MyServiceB`, and one that takes both `MyServiceA` and `MyServiceB`. Look at the constructor selection process defined by `MySmartInstantiationAwareBeanPostProcessor`. When the Spring container tries to create a `MyService` instance, one constructor is marked with `@MyAutowired`, so Spring tries it first. If the container already has beans of `MyServiceA` and `MyServiceB`, both are injected, and `"Constructor with ServiceA and ServiceB used"` is printed. If `@MyAutowired` is missing, or there are no suitable beans for the `@MyAutowired` constructor, Spring tries the default constructor. If there is no default constructor, Spring tries the other constructors and looks for beans that match. If both `MyServiceA` and `MyServiceB` are available, Spring picks the constructor with the most parameters, because it counts as the most specific one.

```java
@Component
public class MyService {

    private final MyServiceA myServiceA;
    private final MyServiceB myServiceB;

    public MyService() {
        System.out.println("Default constructor used");
        this.myServiceA = null;
        this.myServiceB = null;
    }

    public MyService(MyServiceA myServiceA) {
        System.out.println("Constructor with ServiceA used");
        this.myServiceA = myServiceA;
        this.myServiceB = null;
    }

    public MyService(MyServiceB serviceB) {
        System.out.println("Constructor with ServiceB used");
        this.myServiceA = null;
        this.myServiceB = serviceB;
    }

    @MyAutowired
    public MyService(MyServiceA serviceA, MyServiceB serviceB) {
        System.out.println("Constructor with ServiceA and ServiceB used");
        this.myServiceA = serviceA;
        this.myServiceB = serviceB;
    }
}
```

When we run it, the Spring container creates `MyService` with the `@MyAutowired` constructor. It injects both dependencies, `MyServiceA` and `MyServiceB`.

```java
Constructor with ServiceA and ServiceB used
```

<span style="color:red">Note: `predictBeanType` and `getEarlyBeanReference` are used inside the Spring framework, so their effect cannot be shown here. We do not demo these two methods.</span>

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: SmartInstantiationAwareBeanPostProcessor Sequence Diagram
    participant SmartInstantiationAwareBeanPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MySmartInstantiationAwareBeanPostProcessor
    
    SmartInstantiationAwareBeanPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the context
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>Refresh the context
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>Initialize the bean factory
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>Instantiate singletons
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)<br>Get the bean
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>Do the work of getting the bean
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)<br>Get the singleton bean
    DefaultSingletonBeanRegistry-->>AbstractBeanFactory:getObject()<br>Get the bean instance
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName,mbd,args)<br>Create the bean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)<br>Do the work of creating the bean
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:createBeanInstance(beanName,mbd,args)<br>Create the bean instance
	AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:determineConstructorsFromBeanPostProcessors(beanClass, beanName)<br>Determine the constructors
	AbstractAutowireCapableBeanFactory->>MySmartInstantiationAwareBeanPostProcessor:determineCandidateConstructors(beanClass, beanName)<br>Callback for candidate constructors
    MySmartInstantiationAwareBeanPostProcessor-->>AbstractAutowireCapableBeanFactory:Return the constructors
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory:Return the bean
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean
    AnnotationConfigApplicationContext-->>SmartInstantiationAwareBeanPostProcessorApplication:Initialization done
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a context that configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor.

<span style="color:red">PS: `predictBeanType`, `determineCandidateConstructors`, and `getEarlyBeanReference` all belong to `SmartInstantiationAwareBeanPostProcessor`, but they deal with different concerns and have different purposes. In this source analysis, we only show `determineCandidateConstructors`.</span>

```java
public class SmartInstantiationAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`, if the bean is a singleton, the code tries to get it from the factory bean instance cache. If there is no instance in the cache, it creates a new one.

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {
    // Instantiate the bean.
    BeanWrapper instanceWrapper = null;
    if (mbd.isSingleton()) {
        // If it is a singleton, try to get it from the factory bean instance cache.
        instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
    }
    if (instanceWrapper == null) {
        // If there is no instance in the cache, create a new one.
        instanceWrapper = createBeanInstance(beanName, mbd, args);
    }
    
    // ... [code omitted for brevity]

    // Return the created and initialized bean.
    return exposedObject;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance`, the code first tries to find the constructors for the bean from a `SmartInstantiationAwareBeanPostProcessor`. Next, it checks whether some constructors were found, whether the bean definition asks for constructor autowiring, whether constructor argument values are given, or whether explicit constructor arguments exist. If any of these is true, it calls `autowireConstructor`, which instantiates the bean with the chosen constructor (or picks one). Then, if no constructor was chosen yet, it checks for a preferred default constructor. Such constructors can be set explicitly by the user or decided earlier by other parts of the framework. If there is one, the framework tries `autowireConstructor` again. Finally, if none of the earlier steps gives a constructor, Spring uses the no-argument constructor of the bean.

```java
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
    // ... [code omitted for brevity]

    // Are there candidate constructors for autowiring?
    Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
    // If constructors were found by post-processors, or the bean definition asks for constructor autowiring, or constructor argument values exist, or specific constructor arguments are given
    if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
        mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
        // Autowire with the determined constructors
        return autowireConstructor(beanName, mbd, ctors, args);
    }

    // Is there a preferred default constructor?
    ctors = mbd.getPreferredConstructors();
    if (ctors != null) {
        // Autowire with the preferred constructor
        return autowireConstructor(beanName, mbd, ctors, null);
    }

    // No special handling: instantiate with the no-argument constructor
    return instantiateBean(beanName, mbd);
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#determineConstructorsFromBeanPostProcessors`, the code calls every `SmartInstantiationAwareBeanPostProcessor` and uses its `determineCandidateConstructors` method to find the constructors of the bean. If candidate constructors are found, they are returned. If several `SmartInstantiationAwareBeanPostProcessor` beans return constructors, only the first result is used.

```java
protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName)
			throws BeansException {

    if (beanClass != null && hasInstantiationAwareBeanPostProcessors()) {
        for (SmartInstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().smartInstantiationAware) {
            Constructor<?>[] ctors = bp.determineCandidateConstructors(beanClass, beanName);
            if (ctors != null) {
                return ctors;
            }
        }
    }
    return null;
}
```

Finally, our own logic runs. If the class has one or more constructors with the `@MyAutowired` annotation, they are returned as candidates. If none is found, the post-processor looks for the default (no-argument) constructor. If there is no `@MyAutowired` constructor and no default constructor, all available constructors are returned as candidates. Then Spring can pick the most specific one.

```java
public class MySmartInstantiationAwareBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Override
    public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
        // First, look for constructors annotated with @MyAutowired
        List<Constructor<?>> myAutowiredConstructors = Arrays.stream(beanClass.getConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(MyAutowired.class))
                .collect(Collectors.toList());

        if (!myAutowiredConstructors.isEmpty()) {
            return myAutowiredConstructors.toArray(new Constructor<?>[0]);
        }

        // Second, check for a default constructor
        try {
            Constructor<?> defaultConstructor = beanClass.getDeclaredConstructor();
            return new Constructor<?>[]{defaultConstructor};
        } catch (NoSuchMethodException e) {
        // No default constructor found, so continue to pick a suitable constructor
        }

        // Return all constructors and let Spring pick the most specific one
        return beanClass.getConstructors();
    }
}
```

### 8. Notes

1. **Performance impact**
   + Every `SmartInstantiationAwareBeanPostProcessor` adds extra work to the creation of every bean. So avoid creating unneeded `SmartInstantiationAwareBeanPostProcessor` beans, and keep the implementation as efficient as possible.

2. **Interaction with other post-processors**
   + If there are several `SmartInstantiationAwareBeanPostProcessor` beans, they are called in the order they are registered. Make sure the order is right to avoid unexpected overrides or conflicts, because only the first returned constructors are used.

3. **Returning non-null values**
   + `determineCandidateConstructors`: when it returns a non-null value, the Spring container does not try other ways to pick a constructor. `predictBeanType`: the returned type should match, as closely as possible, the final bean type the post-processor expects. This keeps type matching and autowiring correct.

4. **Difference from `InstantiationAwareBeanPostProcessor`**
   + `SmartInstantiationAwareBeanPostProcessor` extends `InstantiationAwareBeanPostProcessor`, but it adds more callbacks and more complexity. Unless we really need these extra features, it is better to use only `InstantiationAwareBeanPostProcessor`.

### 9. Summary

#### Best Practices Summary

1. **Initialize the Spring container**
   + Initialize the Spring container with `AnnotationConfigApplicationContext`, and use `MyConfiguration` as the configuration class.

2. **Register the post-processor**
   + In `MyConfiguration`, we register our own `SmartInstantiationAwareBeanPostProcessor`, `MySmartInstantiationAwareBeanPostProcessor`. This makes sure the Spring container picks it up and runs it.

3. **What the custom post-processor does**
   + Our `MySmartInstantiationAwareBeanPostProcessor` overrides `determineCandidateConstructors`. This method returns a set of constructors that Spring can choose from to instantiate the bean.

4. **Look for `@MyAutowired`**
   + First, the post-processor looks for constructors with the `@MyAutowired` annotation.

5. **Use the default constructor**
   + If there is no `@MyAutowired` constructor, the post-processor looks for the default constructor.

6. **Return all constructors**
   + If neither of the two cases above finds a constructor, all constructors are returned as candidates.

7. **Bean instantiation**
   + When Spring instantiates the `MyService` bean, it uses the constructors given by `MySmartInstantiationAwareBeanPostProcessor`. In this example, one constructor has `@MyAutowired`, and both dependencies `MyServiceA` and `MyServiceB` are available. So this constructor is chosen and used, and `"Constructor with ServiceA and ServiceB used"` is printed.

8. **About the other methods**
   + `SmartInstantiationAwareBeanPostProcessor` has other methods, such as `predictBeanType` and `getEarlyBeanReference`. They are mostly for internal use in Spring. In most normal cases, we do not need to override or use them.

#### Source Code Analysis Summary

1. **Startup and initialization:**

   - Initialize the Spring context with `AnnotationConfigApplicationContext`.

   - Give it a configuration class that defines our own `SmartInstantiationAwareBeanPostProcessor`.


2. **Bean pre-instantiation:**

   - During context refresh, `finishBeanFactoryInitialization` pre-instantiates all non-lazy singleton beans.

   - `preInstantiateSingletons` loops over all bean names and instantiates the beans with `getBean`.

   - If a bean already exists and is a singleton, it is returned from the singleton cache. Otherwise, a new bean instance is created.


3. **Bean creation:**

   - The core creation logic is in `doCreateBean`. If the bean is a singleton and not in the cache, a new bean instance is created.

   - When creating the instance, the constructors for the bean are first taken from `SmartInstantiationAwareBeanPostProcessor`.

   - It first tries a constructor with a specific annotation (such as `@MyAutowired` in our example).

   - If there is none, the default constructor is chosen.

   - If there is no annotated constructor and no default constructor, all available constructors are returned, so Spring picks the most specific one.


4. **Custom logic:**

   - Our own `SmartInstantiationAwareBeanPostProcessor` first checks for constructors with the `@MyAutowired` annotation.
   - If there are any, they are returned as candidates.

   - If not, the post-processor checks for a default no-argument constructor.

   - If there is neither an `@MyAutowired` constructor nor a default constructor, all constructors are returned as candidates.