## InstantiationAwareBeanPostProcessor

- [InstantiationAwareBeanPostProcessor](#instantiationawarebeanpostprocessor)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133845204) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [InstantiationAwareBeanPostProcessor source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor)

### 2. Interface Description

`InstantiationAwareBeanPostProcessor` gives callbacks before and after bean instantiation. This lets us return a proxy instead of the real target bean instance, or change how the bean is constructed.

### 3. Interface Source

`InstantiationAwareBeanPostProcessor` is a core Spring interface, available since version 1.2. **`postProcessBeforeInstantiation`**: called before the bean is instantiated. It can return another instance of the bean, such as a proxy. This stops Spring from instantiating the target bean. **`postProcessAfterInstantiation`**: called after the bean is instantiated but before any properties are set. It can be used for field-based dependency injection or other custom initialization tasks. **`postProcessPropertyValues`**: called before property values are set on the bean. It lets us change the properties, add new ones, or return a completely different set.

```java
/**
 * Subinterface of BeanPostProcessor that adds a before-instantiation callback,
 * and a callback after instantiation but before explicit properties are set or autowiring occurs.
 *
 * Typically used to suppress default instantiation for specific target beans,
 * for example to create proxies with special TargetSources (pooling targets, lazily initializing targets, etc.),
 * or to implement additional injection strategies such as field injection.
 *
 * NOTE: This is a special-purpose interface, mainly for internal use within the framework.
 * It is recommended to implement the plain BeanPostProcessor interface as far as possible,
 * or to derive from InstantiationAwareBeanPostProcessorAdapter,
 * to be shielded from extensions to this interface.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @since 1.2
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#setCustomTargetSourceCreators
 * @see org.springframework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * Apply this BeanPostProcessor before the target bean gets instantiated. The returned bean object may be a proxy
     * to use instead of the target bean, effectively suppressing default instantiation of the target bean.
     * If a non-null object is returned, the bean creation process is short-circuited.
     * 
     * @param beanClass the class of the bean to be instantiated
     * @param beanName the name of the bean
     * @return the bean object to expose instead of a default instance of the target bean, or {@code null} to proceed with default instantiation
     * @throws org.springframework.beans.BeansException in case of errors
     */
    @Nullable
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    /**
     * Perform operations after the bean has been instantiated, via a constructor or factory method, but before Spring property population (from explicit properties or autowiring) occurs.
     * This is the ideal callback for custom field injection on the given bean instance, right before Spring's autowiring starts.
     * 
     * @param bean the bean instance created, with properties not having been set yet
     * @param beanName the name of the bean
     * @return {@code true} if properties should be set on the bean; {@code false} if property population should be skipped.
     * @throws org.springframework.beans.BeansException in case of errors
     */
    default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    /**
     * Post-process the given property values before the factory applies them to the given bean, without any need for property descriptors.
     * 
     * @param pvs the property values that the factory is about to apply (never {@code null})
     * @param bean the bean instance created, but whose properties have not yet been set
     * @param beanName the name of the bean
     * @return the actual property values to apply to the given bean (can be the passed-in PropertyValues instance), or {@code null}
     * @throws org.springframework.beans.BeansException in case of errors
     */
    @Nullable
    default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
            throws BeansException {

        return null;
    }

    /**
     * Post-process the given property values before the factory applies them to the given bean. Allows for checking whether all dependencies have been satisfied,
     * for example based on a "Required" annotation on bean property setters.
     * 
     * @param pvs the property values that the factory is about to apply (never {@code null})
     * @param pds the relevant property descriptors for the target bean
     * @param bean the bean instance created, but whose properties have not yet been set
     * @param beanName the name of the bean
     * @return the actual property values to apply to the given bean (can be the passed-in PropertyValues instance), or {@code null}
     * @throws org.springframework.beans.BeansException in case of errors
     */
    @Deprecated
    @Nullable
    default PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

        return pvs;
    }
}
```

### 4. Main Features

1. **Handling before instantiation**
   + Called before the bean is actually instantiated. It can return a proxy to replace the real target bean, so the default instantiation is skipped. This is a very useful step in AOP and proxy creation.

2. **Handling after instantiation**
   + Called after the bean is instantiated but before property injection. This callback lets you customize the bean before Spring autowiring or property setting.

3. **Property handling**
   + Lets you process or replace the property values of the bean before Spring injects them. This is a good place for custom property injection or for validating bean properties.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a context that configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we get a bean of type `DataBase` from the Spring context and print some of its properties. This lets us check the state of the bean.

```java
public class InstantiationAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        DataBase userService = context.getBean(DataBase.class);
        System.out.println("username = " + userService.getUsername());
        System.out.println("password = " + userService.getPassword());
        System.out.println("postInstantiationFlag = " + userService.isPostInstantiationFlag());
    }
}
```

The `@Bean` annotation defines two beans here. This makes sure the Spring container handles `DataBase` and `MyInstantiationAwareBeanPostProcessor`.

```java
@Configuration
public class MyConfiguration {

    @Bean
    public static MyInstantiationAwareBeanPostProcessor myInstantiationAwareBeanPostProcessor() {
        return new MyInstantiationAwareBeanPostProcessor();
    }

    @Bean
    public DataBase dataBase() {
        return new DataBaseImpl();
    }
}
```

The `MyInstantiationAwareBeanPostProcessor` class implements Spring's `InstantiationAwareBeanPostProcessor` interface. In `postProcessBeforeInstantiation`, it prints a message before a bean of type `DataBase` starts to be instantiated, to show that we are preparing to instantiate it. In `postProcessAfterInstantiation`, after the `DataBase` bean is instantiated, it sets a flag property and prints a message. In `postProcessProperties`, before properties are injected into the `DataBase` bean, it masks the password and prints a message saying the password was masked.

```java
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass == DataBase.class) {
            System.out.println("Preparing to instantiate: " + beanName);
        }
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataBase) {
            ((DataBase) bean).setPostInstantiationFlag(true);
            System.out.println("Bean " + beanName + " instantiated!");
            return true;
        }
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if (bean instanceof DataBase) {
            MutablePropertyValues mpvs = (MutablePropertyValues) pvs;
            mpvs.addPropertyValue("password", "******");
            System.out.println(beanName + " password has been masked:");
        }
        return pvs;
    }
}
```

A simple interface definition.

```java
public interface DataBase {
    String getUsername();
    void setUsername(String username);
    String getPassword();
    void setPassword(String password);
    boolean isPostInstantiationFlag();
    void setPostInstantiationFlag(boolean flag);
}
```

The `@Value` annotation gives default values to the properties in the Spring context.

```java
public class DataBaseImpl implements DataBase {

    @Value("root")
    private String username;

    @Value("123456")
    private String password;

    private boolean postInstantiationFlag;
	
    // get and set
}
```

When we run it, the logs show the steps from "preparing to instantiate" to "instantiated" for `dataBase`. Most importantly, the `password` field was masked.

```java
Preparing to instantiate: dataBase
Bean dataBase instantiated!
dataBase password has been masked:
username = root
password = ******
postInstantiationFlag = true
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
    Title: InstantiationAwareBeanPostProcessor Sequence Diagram
    participant InstantiationAwareBeanPostProcessorApplication
    participant AnnotationConfigApplicationContext
    participant AbstractApplicationContext
    participant DefaultListableBeanFactory
    participant AbstractBeanFactory
    participant DefaultSingletonBeanRegistry
    participant AbstractAutowireCapableBeanFactory
    participant MyInstantiationAwareBeanPostProcessor
    
    InstantiationAwareBeanPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext<br>Start initialization
    AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh<br>Refresh the context
    AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization<br>Instantiate the non-lazy singleton beans
    AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons<br>Pre-instantiate singletons
    DefaultListableBeanFactory->>AbstractBeanFactory:getBean<br>Get the object by beanName
    AbstractBeanFactory->>AbstractBeanFactory:doGetBean<br>Return the instance of the given bean
    AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton<br>Get the singleton object
    DefaultSingletonBeanRegistry->>AbstractBeanFactory:getObject<br>Factory method of the ObjectFactory interface
    AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean<br>Create a bean instance, populate it, and apply post-processors
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:resolveBeforeInstantiation
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInstantiation
    AbstractAutowireCapableBeanFactory->>MyInstantiationAwareBeanPostProcessor:postProcessBeforeInstantiation<br>Handle before instantiation
    MyInstantiationAwareBeanPostProcessor->>AbstractAutowireCapableBeanFactory:Return the bean object
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory: If the bean object is not null, return it and skip the rest
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean
    AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean
    AbstractAutowireCapableBeanFactory->>MyInstantiationAwareBeanPostProcessor:postProcessAfterInstantiation<br>Handle after instantiation
    MyInstantiationAwareBeanPostProcessor-->>AbstractAutowireCapableBeanFactory:Return true or false <br>Whether to continue with property injection and later lifecycle steps
    AbstractAutowireCapableBeanFactory->>MyInstantiationAwareBeanPostProcessor:postProcessProperties<br>Handle properties
    AbstractAutowireCapableBeanFactory-->>AbstractBeanFactory: Return the bean object
    AbstractBeanFactory-->>DefaultListableBeanFactory:Return the bean object
    AnnotationConfigApplicationContext->>InstantiationAwareBeanPostProcessorApplication:Initialization done
~~~

### 7. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a context that configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then it calls `context.getBean(DataBase.class)` to get the `DataBase` bean from the Spring container, and prints the username, password, and flag.

```java
public class InstantiationAwareBeanPostProcessorApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        DataBase userService = context.getBean(DataBase.class);
        System.out.println("username = " + userService.getUsername());
        System.out.println("password = " + userService.getPassword());
        System.out.println("postInstantiationFlag = " + userService.isPostInstantiationFlag());
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
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Instantiate all remaining (non-lazy-init) singletons.
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
    // ... [code omitted for brevity]

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

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()`, the code first calls `resolveBeforeInstantiation`. This gives the `InstantiationAwareBeanPostProcessor` a chance to return a proxy instead of the target bean instance. If this step returns a non-null object (that is, an `InstantiationAwareBeanPostProcessor` created a proxy), the proxy is returned as the bean, and the normal bean creation is skipped. If it returns nothing, the code runs `doCreateBean`, which does the actual bean instantiation, property injection, and initialization.

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {
    
    // ... [code omitted for brevity]
    
    try {
        // 1. Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
        // If this step returns a non-null bean, that bean is returned and the normal instantiation is skipped.
        Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
        if (bean != null) {
            return bean;
        }
    }
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }

    try {
        // Normal bean instantiation, property injection, and initialization.
        // 2. This is where the bean is really created.
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

This is the first step in `createBean(beanName,mbd,args)`. In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`, the code first tries to create the bean before it is really instantiated. This is usually done to return a proxy. `applyBeanPostProcessorsBeforeInstantiation` tries to create the bean early with `postProcessBeforeInstantiation` of `InstantiationAwareBeanPostProcessor`. If that succeeds (for example, a proxy is returned), the bean also goes through `postProcessAfterInitialization` of all registered `BeanPostProcessor`s. This is the last processing step after bean initialization.

```java
protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
    Object bean = null;
    // Check whether this bean was already tried to be resolved before instantiation
    if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
        // If the bean is not synthetic and there are InstantiationAwareBeanPostProcessors
        if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            // Find the type of the target bean
            Class<?> targetType = determineTargetType(beanName, mbd);
            // If the target type is not null
            if (targetType != null) {
                // Try to create the bean early with BeanPostProcessors, before the real instantiation
                bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
                // If the bean instance was created
                if (bean != null) {
                    // Apply postProcessAfterInitialization to the bean instance
                    bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                }
            }
        }
        // Mark whether the bean was resolved before instantiation
        mbd.beforeInstantiationResolved = (bean != null);
    }
    // Return the created bean instance or null
    return bean;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation`, the code calls `postProcessBeforeInstantiation` of every `InstantiationAwareBeanPostProcessor`.

```java
protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
    for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
        Object result = bp.postProcessBeforeInstantiation(beanClass, beanName);
        if (result != null) {
            return result;
        }
    }
    return null;
}
```

`MyInstantiationAwareBeanPostProcessor` is a simple class that implements `InstantiationAwareBeanPostProcessor`. It overrides `postProcessBeforeInstantiation`, which is called before the bean is instantiated. Before instantiation, we can return a different bean instance to replace the bean that would be created. If this method returns a non-null object, Spring uses it as the bean and skips the standard instantiation. If it returns null, Spring continues the normal instantiation. In `postProcessBeforeInstantiation`, we only print a message that we are preparing to instantiate the bean, and return null. Returning null means the normal instantiation is not interrupted. That is the whole source analysis of `postProcessBeforeInstantiation` in `InstantiationAwareBeanPostProcessor`. The other two methods are covered below.

```java
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (beanClass == DataBase.class) {
            System.out.println("Preparing to instantiate: " + beanName);
        }
        return null;
    }
}
```

This is the second step in `createBean(beanName,mbd,args)`. `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` has two main steps. The first is property injection. The second is bean initialization. Together they make sure the bean is fully configured and ready.

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // Declare an object that may later hold the initialized bean or its proxy
    Object exposedObject = bean;

    try {
        // Property injection: this injects the configured property values into the bean instance. For example, properties defined in XML, or marked with @Autowired and @Value, are injected here
        populateBean(beanName, mbd, instanceWrapper);

        // Bean initialization: this runs the init methods of the bean, and also calls postProcessBeforeInitialization and postProcessAfterInitialization of BeanPostProcessor, which can process the bean further
        exposedObject = initializeBean(beanName, exposedObject, mbd);
    } 
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }

    // Return the created and initialized bean
    return exposedObject;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean`, the code first calls `postProcessAfterInstantiation` of all `InstantiationAwareBeanPostProcessors`. This gives them a chance to change the state of the bean before properties are set. If `postProcessAfterInstantiation` returns true, the code first tries `postProcessProperties` to handle the property values. If that returns `null`, it falls back to the older `postProcessPropertyValues`.

```java
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
    // ... [code omitted for brevity]

    // If the bean is not synthetic and there are InstantiationAwareBeanPostProcessors, give them a chance
    // to act before Spring populates the properties, for example to support different styles of field injection.
    if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
        for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
            // If it returns false, the properties of this bean should not be populated.
            if (!bp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                return;
            }
        }
    }

    // ... [code omitted for brevity]
    // Handle each InstantiationAwareBeanPostProcessor. They may change the property values of the bean.
    for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
        // Try the new method postProcessProperties first
        PropertyValues pvsToUse = bp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
        if (pvsToUse == null) {
            // If postProcessProperties returns null, try the old method postProcessPropertyValues
            if (filteredPds == null) {
                filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
            }
            pvsToUse = bp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
            // If the old method also returns null, the properties of this bean should not be populated.
            if (pvsToUse == null) {
                return;
            }
        }
        pvs = pvsToUse;
    }
    // ... [code omitted for brevity]
}
```

Finally, our own logic runs. In `postProcessAfterInstantiation`, we set the `postInstantiationFlag` property of the bean to `true`. This is a mark that the bean was instantiated. Then we return `true`, which allows Spring to continue with the bean initialization. In `postProcessProperties`, we change the value of the `password` property to `"******"`.

```java
public class MyInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataBase) {
            ((DataBase) bean).setPostInstantiationFlag(true);
            System.out.println("Bean " + beanName + " instantiated!");
            return true;
        }
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if (bean instanceof DataBase) {
            MutablePropertyValues mpvs = (MutablePropertyValues) pvs;
            mpvs.addPropertyValue("password", "******");
            System.out.println(beanName + " password has been masked:");
        }
        return pvs;
    }
}
```

### 8. Notes

1. **Performance impact**
   + This post-processor is called several times while each bean is created. Keep its logic simple and fast to reduce the impact on application performance. If you do heavy work in `postProcessBeforeInstantiation`, `postProcessAfterInstantiation`, or `postProcessProperties`, such as database queries, remote calls, or other IO, these methods run for every bean. This can badly slow down application startup and bean creation.

2. **Returning a non-null bean**
   + If `postProcessBeforeInstantiation` returns a non-null bean, the normal bean instantiation and property setting are short-circuited. Methods such as `postProcessAfterInstantiation` and `postProcessProperties` are not called. `postProcessBeforeInitialization` of `BeanPostProcessor` is not called either.

3. **Do not change beans that are not your target**
   + If our `InstantiationAwareBeanPostProcessor` only works on beans of a certain type or name, make sure to check this before doing anything.

### 9. Summary

#### Best Practices Summary

1. **Startup and context initialization**:

   - We start the application with `AnnotationConfigApplicationContext` and register the configuration class `MyConfiguration`.

   - We get a bean of type `DataBase` from the Spring context and print its properties. This checks that the bean state was changed.


2. **Configuration class and bean definitions**:
   - The `MyConfiguration` class defines two beans: `DataBase` and `MyInstantiationAwareBeanPostProcessor`. `MyInstantiationAwareBeanPostProcessor` is a post-processor. It is triggered when other beans in the Spring container are instantiated.


3. **Intercepting instantiation**:

   - `MyInstantiationAwareBeanPostProcessor` implements Spring's `InstantiationAwareBeanPostProcessor`. This lets it step into bean instantiation, initialization, and property setting.

   - In `postProcessBeforeInstantiation`, a message is printed before the `DataBase` bean starts to be instantiated.

   - In `postProcessAfterInstantiation`, the bean is already instantiated. It sets a flag property and prints a message.

   - In `postProcessProperties`, the password property of the `DataBase` bean is changed, and a message is printed.


4. **DataBase interface and implementation**:

   - The `DataBase` interface defines the basic properties of a database connection, with getters and setters.

   - The `DataBaseImpl` class implements this interface and uses `@Value` to set default values for the properties.


5. **Result**:
   - The output shows that `dataBase` was intercepted from "preparing to instantiate" to "instantiated", and the password was masked.


#### Source Code Analysis Summary

1. **Startup and getting the bean**

   - When the application starts, `AnnotationConfigApplicationContext` initializes the Spring context and registers the configuration class `MyConfiguration`.

   - Then the application gets the `DataBase` bean from the Spring context and prints some of its properties.


2. **Registering the bean and the post-processor**
   - The `MyConfiguration` class registers two beans. One is `MyInstantiationAwareBeanPostProcessor`, a post-processor that steps in during bean instantiation.


3. **Interception before instantiation**
   - Before the bean is instantiated, Spring first calls `postProcessBeforeInstantiation`. Here, we only print a message and return null, so Spring continues with the standard instantiation.


4. **Bean property injection**

   - After the bean is instantiated but before property injection, Spring calls `postProcessProperties`.

   - In this example, we change the `password` property to `"******"` and print a message.


5. **Handling after instantiation**
   - Right after that, `postProcessAfterInstantiation` is called. Here, we simply set the `postInstantiationFlag` property and print a message.


6. **Bean completion**

   - After all these interceptors run, Spring continues with property injection, bean initialization, and the other later steps.

   - After that, the bean is fully initialized and ready for the application to use.