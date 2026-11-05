## AutowireCapableBeanFactory

- [AutowireCapableBeanFactory](#autowirecapablebeanfactory)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementation](#5-main-implementation)
  - [6. Best Practices](#6-best-practices)
    - [createBean](#createbean)
    - [configureBean](#configurebean)
    - [autowireBean](#autowirebean)
    - [autowire](#autowire)
    - [autowireBeanProperties](#autowirebeanproperties)
    - [applyBeanPropertyValues](#applybeanpropertyvalues)
    - [initializeBean](#initializebean)
    - [destroyBean](#destroybean)
    - [resolveDependency](#resolvedependency)
  - [7. Common Problems](#7-common-problems)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

`AutowireCapableBeanFactory` is a key interface in the `org.springframework.beans.factory.config` package of Spring. It extends `BeanFactory`. It gives advanced features to autowire and create beans at runtime. Its core method `createBean` creates a bean instance dynamically and autowires it, so the dependencies between beans are resolved. Other methods, such as `autowireBean` and `applyBeanPostProcessorsBeforeInitialization`, give finer control and more extension points. They let you step in at different stages of the bean lifecycle for more flexible bean management and configuration. This interface makes the Spring IoC container stronger, so it can better fit the needs of complex systems.

### 3. Main Features

1. **Create and initialize beans** 

   + The `createBean` method creates a new bean instance. It runs the full initialization, including the callbacks of all applicable `BeanPostProcessor` objects.

2. **Autowiring** 

   + It supports different autowire modes, such as by name, by type, and by constructor. The `autowire` and `autowireBeanProperties` methods inject the bean properties.

3. **Bean configuration and post-processor handling** 

   + The `configureBean` method configures an existing bean instance. It applies property values and factory callbacks, and runs the callbacks of all `BeanPostProcessor` objects.

4. **Custom initialization and destruction** 

   + The `initializeBean` method applies custom actions during bean initialization, such as initialization callbacks and post-processors. The `destroyBean` method destroys a bean instance.

5. **Resolve dependencies** 

   + The `resolveDependency` method resolves a given dependency. It supports fields, methods, constructors, and other injection points.

### 4. Interface Source

The source of the `AutowireCapableBeanFactory` interface shows that it creates, configures, and manages the lifecycle of bean instances. With its constants and methods, it gives fine control. This includes specific autowire strategies, the initialization process, property injection, post-processor handling, and the destruction phase.

```java
/**
 * Extension of the {@link org.springframework.beans.factory.BeanFactory}
 * interface to be implemented by bean factories that are capable of
 * autowiring, provided that they want to expose this functionality for
 * existing bean instances.
 *
 * <p>This subinterface of BeanFactory is not meant to be used in normal
 * application code: stick to {@link org.springframework.beans.factory.BeanFactory}
 * or {@link org.springframework.beans.factory.ListableBeanFactory} for
 * typical use cases.
 *
 * <p>Integration code for other frameworks can leverage this interface to
 * wire and populate existing bean instances that Spring does not control
 * the lifecycle of. This is particularly useful for WebWork Actions and
 * Tapestry Page objects, for example.
 *
 * <p>Note that this interface is not implemented by
 * {@link org.springframework.context.ApplicationContext} facades,
 * as it is hardly ever used by application code. That said, it is available
 * from an application context too, accessible through ApplicationContext's
 * {@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()}
 * method.
 *
 * <p>You may also implement the {@link org.springframework.beans.factory.BeanFactoryAware}
 * interface, which exposes the internal BeanFactory even when running in an
 * ApplicationContext, to get access to an AutowireCapableBeanFactory:
 * simply cast the passed-in BeanFactory to AutowireCapableBeanFactory.
 *
 * @author Juergen Hoeller
 * @since 04.12.2003
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    /**
     * Constant that indicates no externally defined autowiring. Note that
     * BeanFactoryAware etc and annotation-driven injection will still be applied.
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_NO = 0;

    /**
     * Constant that indicates autowiring bean properties by name
     * (applying to all bean property setters).
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_BY_NAME = 1;

    /**
     * Constant that indicates autowiring bean properties by type
     * (applying to all bean property setters).
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_BY_TYPE = 2;

    /**
     * Constant that indicates autowiring the greediest constructor that
     * can be satisfied (involves resolving the appropriate constructor).
     * @see #createBean
     * @see #autowire
     */
    int AUTOWIRE_CONSTRUCTOR = 3;

    /**
     * Constant that indicates determining an appropriate autowire strategy
     * through introspection of the bean class.
     * @see #createBean
     * @see #autowire
     * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
     * prefer annotation-based autowiring for clearer demarcation of autowiring needs.
     */
    @Deprecated
    int AUTOWIRE_AUTODETECT = 4;

    /**
     * Suffix for the "original instance" convention when initializing an existing
     * bean instance: to be appended to the fully-qualified bean class name,
     * e.g. "com.mypackage.MyClass.ORIGINAL", in order to enforce the given instance
     * to be returned, i.e. no proxies etc.
     * @since 5.1
     * @see #initializeBean(Object, String)
     * @see #applyBeanPostProcessorsBeforeInitialization(Object, String)
     * @see #applyBeanPostProcessorsAfterInitialization(Object, String)
     */
    String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";

    /**
     * Fully create a new bean instance of the given class.
     * Performs full initialization of the bean, including all applicable
     * BeanPostProcessors.
     * Note: This is intended for creating a fresh instance, populating annotated
     * fields and methods as well as applying all standard bean initialization callbacks.
     * It does not imply traditional by-name or by-type autowiring of properties;
     * use #createBean(Class, int, boolean) for those purposes.
     * @param beanClass the class of the bean to create
     * @return the new bean instance
     * @throws BeansException if instantiation or wiring failed
     */
    <T> T createBean(Class<T> beanClass) throws BeansException;

    /**
     * Populate the given bean instance through applying after-instantiation callbacks
     * and bean property post-processing (e.g. for annotation-driven injection).
     * Note: This is essentially intended for (re-)populating annotated fields and
     * methods, either for new instances or for deserialized instances. It does
     * not imply traditional by-name or by-type autowiring of properties;
     * use #autowireBeanProperties for those purposes.
     * @param existingBean the existing bean instance
     * @throws BeansException if wiring failed
     */
    void autowireBean(Object existingBean) throws BeansException;

    /**
     * Configure the given raw bean: autowiring bean properties, applying
     * bean property values, applying factory callbacks such as {@code setBeanName}
     * and {@code setBeanFactory}, and also applying all bean post processors
     * (including ones which might wrap the given raw bean).
     * This is effectively a superset of what #initializeBean provides,
     * fully applying the configuration specified by the corresponding bean definition.
     * Note: This method requires a bean definition for the given name!
     * @param existingBean the existing bean instance
     * @param beanName the name of the bean, to be passed to it if necessary
     * (a bean definition of that name has to be available)
     * @return the bean instance to use, either the original or a wrapped one
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     * if there is no bean definition with the given name
     * @throws BeansException if the initialization failed
     * @see #initializeBean
     */
    Object configureBean(Object existingBean, String beanName) throws BeansException;

    /**
     * Fully create a new bean instance of the given class with the specified
     * autowire strategy. All constants defined in this interface are supported here.
     * Performs full initialization of the bean, including all applicable
     * BeanPostProcessors.
     * This is effectively a superset of what #autowire provides, adding
     * #initializeBean behavior.
     * @param beanClass the class of the bean to create
     * @param autowireMode by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for objects
     * (not applicable to autowiring a constructor, thus ignored there)
     * @return the new bean instance
     * @throws BeansException if instantiation or wiring failed
     * @see #AUTOWIRE_NO
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_CONSTRUCTOR
     */
    Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    /**
     * Instantiate a new bean instance of the given class with the specified autowire
     * strategy. All constants defined in this interface are supported here.
     * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply
     * before-instantiation callbacks (e.g. for annotation-driven injection).
     * Does <i>not</i> apply standard BeanPostProcessors callbacks or perform
     * any further initialization of the bean. This interface offers distinct,
     * fine-grained operations for those purposes, for example #initializeBean.
     * However, InstantiationAwareBeanPostProcessor callbacks are applied, if applicable
     * to the construction of the instance.
     * @param beanClass the class of the bean to instantiate
     * @param autowireMode by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for object
     * references in the bean instance (not applicable to autowiring a constructor,
     * thus ignored there)
     * @return the new bean instance
     * @throws BeansException if instantiation or wiring failed
     * @see #AUTOWIRE_NO
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_CONSTRUCTOR
     * @see #AUTOWIRE_AUTODETECT
     * @see #initializeBean
     * @see #applyBeanPostProcessorsBeforeInitialization
     * @see #applyBeanPostProcessorsAfterInitialization
     */
    Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    /**
     * Autowire the bean properties of the given bean instance by name or type.
     * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply
     * after-instantiation callbacks (e.g. for annotation-driven injection).
     * Does <i>not</i> apply standard BeanPostProcessors callbacks or perform
     * any further initialization of the bean. This interface offers distinct,
     * fine-grained operations for those purposes, for example #initializeBean.
     * However, InstantiationAwareBeanPostProcessor callbacks are applied, if applicable
     * to the configuration of the instance.
     * @param existingBean the existing bean instance
     * @param autowireMode by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for object
     * references in the bean instance
     * @throws BeansException if wiring failed
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_NO
     */
    void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
            throws BeansException;


    /**
     * Apply the property values of the bean definition with the given name to
     * the given bean instance. The bean definition can either define a fully
     * self-contained bean, reusing its property values, or just property values
     * meant to be used for existing bean instances.
     * This method does <i>not</i> autowire bean properties; it just applies
     * explicitly defined property values. Use the #autowireBeanProperties method
     * to autowire an existing bean instance.
     * Note: This method requires a bean definition for the given name!
     * Does <i>not</i> apply standard BeanPostProcessors callbacks or perform
     * any further initialization of the bean. This interface offers distinct,
     * fine-grained operations for those purposes, for example #initializeBean.
     * However, InstantiationAwareBeanPostProcessor callbacks are applied, if applicable
     * to the configuration of the instance.
     * @param existingBean the existing bean instance
     * @param beanName the name of the bean definition in the bean factory
     * (a bean definition of that name has to be available)
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     * if there is no bean definition with the given name
     * @throws BeansException if applying the property values failed
     * @see #autowireBeanProperties
     */
    void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

    /**
     * Initialize the given raw bean, applying factory callbacks
     * such as {@code setBeanName} and {@code setBeanFactory},
     * also applying all bean post processors (including ones which
     * might wrap the given raw bean).
     * <p>Note that no bean definition of the given name has to exist
     * in the bean factory. The passed-in bean name will simply be used
     * for callbacks but not checked against the registered bean definitions.
     * @param existingBean the existing bean instance
     * @param beanName the name of the bean, to be passed to it if necessary
     * (only passed to BeanPostProcessors;
     * can follow the #ORIGINAL_INSTANCE_SUFFIX convention in order to
     * enforce the given instance to be returned, i.e. no proxies etc)
     * @return the bean instance to use, either the original or a wrapped one
     * @throws BeansException if the initialization failed
     * @see #ORIGINAL_INSTANCE_SUFFIX
     */
    Object initializeBean(Object existingBean, String beanName) throws BeansException;

    /**
     * Apply BeanPostProcessors to the given existing bean instance,
     * invoking their {@code postProcessBeforeInitialization} methods.
     * The returned bean instance may be a wrapper around the original.
     * @param existingBean the existing bean instance
     * @param beanName the name of the bean, to be passed to it if necessary
     * (only passed to BeanPostProcessors;
     * can follow the #ORIGINAL_INSTANCE_SUFFIX convention in order to
     * enforce the given instance to be returned, i.e. no proxies etc)
     * @return the bean instance to use, either the original or a wrapped one
     * @throws BeansException if any post-processing failed
     * @see BeanPostProcessor#postProcessBeforeInitialization
     * @see #ORIGINAL_INSTANCE_SUFFIX
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;

    /**
     * Apply BeanPostProcessors to the given existing bean instance,
     * invoking their {@code postProcessAfterInitialization} methods.
     * The returned bean instance may be a wrapper around the original.
     * @param existingBean the existing bean instance
     * @param beanName the name of the bean, to be passed to it if necessary
     * (only passed to BeanPostProcessors;
     * can follow the #ORIGINAL_INSTANCE_SUFFIX convention in order to
     * enforce the given instance to be returned, i.e. no proxies etc)
     * @return the bean instance to use, either the original or a wrapped one
     * @throws BeansException if any post-processing failed
     * @see BeanPostProcessor#postProcessAfterInitialization
     * @see #ORIGINAL_INSTANCE_SUFFIX
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException;

    /**
     * Destroy the given bean instance (typically coming from #createBean),
     * applying the org.springframework.beans.factory.DisposableBean contract as well as
     * registered DestructionAwareBeanPostProcessors.
     * Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     * @param existingBean the bean instance to destroy
     */
    void destroyBean(Object existingBean);

    /**
     * Resolve the bean instance that uniquely matches the given object type, if any,
     * including its bean name.
     * This is effectively a variant of #getBean(Class) which preserves the
     * bean name of the matching instance.
     * @param requiredType type the bean must match; can be an interface or superclass
     * @return the bean name plus bean instance
     * @throws NoSuchBeanDefinitionException if no matching bean was found
     * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
     * @throws BeansException if the bean could not be created
     * @since 4.3.3
     * @see #getBean(Class)
     */
    <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

    /**
     * Resolve a bean instance for the given bean name, providing a dependency descriptor
     * for exposure to target factory methods.
     * This is effectively a variant of #getBean(String, Class) which supports
     * factory methods with an org.springframework.beans.factory.InjectionPoint
     * argument.
     * @param name the name of the bean to look up
     * @param descriptor the dependency descriptor for the requesting injection point
     * @return the corresponding bean instance
     * @throws NoSuchBeanDefinitionException if there is no bean with the specified name
     * @throws BeansException if the bean could not be created
     * @since 5.1.5
     * @see #getBean(String, Class)
     */
    Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException;

    /**
     * Resolve the specified dependency against the beans defined in this factory.
     * @param descriptor the descriptor for the dependency (field/method/constructor)
     * @param requestingBeanName the name of the bean which declares the given dependency
     * @return the resolved object, or {@code null} if none found
     * @throws NoSuchBeanDefinitionException if no matching bean was found
     * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
     * @throws BeansException if dependency resolution failed for any other reason
     * @since 2.5
     * @see #resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
     */
    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;

    /**
     * Resolve the specified dependency against the beans defined in this factory.
     * @param descriptor the descriptor for the dependency (field/method/constructor)
     * @param requestingBeanName the name of the bean which declares the given dependency
     * @param autowiredBeanNames a Set that all names of autowired beans (used for
     * resolving the given dependency) are supposed to be added to
     * @param typeConverter the TypeConverter to use for populating arrays and collections
     * @return the resolved object, or {@code null} if none found
     * @throws NoSuchBeanDefinitionException if no matching bean was found
     * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
     * @throws BeansException if dependency resolution failed for any other reason
     * @since 2.5
     * @see DependencyDescriptor
     */
    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
            @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;
			
}
```

### 5. Main Implementation

+ `AbstractAutowireCapableBeanFactory`
  + `AbstractAutowireCapableBeanFactory` is the abstract implementation of the `AutowireCapableBeanFactory` interface. It gives Spring the core features to create, initialize, and destroy beans. It implements the `createBean` method. It supports dependency injection, applying property values, applying post-processors, and lifecycle management in the initialization and destruction phases.

~~~mermaid
classDiagram
    direction BT
    class BeanFactory {
    	<<interface>>
    }

    class AutowireCapableBeanFactory {
    	<<interface>>
    }

    class AbstractAutowireCapableBeanFactory {
    }

    AutowireCapableBeanFactory --|> BeanFactory
    AbstractAutowireCapableBeanFactory --|> AutowireCapableBeanFactory

~~~

### 6. Best Practices

This example creates a Spring application context with `AnnotationConfigApplicationContext`. It registers a post-processor (`MyBeanPostProcessor`) and a singleton bean (`MyRepository`) by hand. Then it gets the `AutowireCapableBeanFactory`.

```java
public static void main(String[] args) {
    // Create the ApplicationContext
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);

    // Add a post-processor to print messages before and after bean initialization
    applicationContext.getBeanFactory().addBeanPostProcessor(new MyBeanPostProcessor());
    // Register a MyRepository singleton bean
    applicationContext.getBeanFactory().registerSingleton("myRepository", new MyRepository());

    // Get the AutowireCapableBeanFactory
    AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
}
```

`MyService` is a typical Spring bean class. It uses `@Autowired` and `@Value` to inject other beans and configuration properties. It implements `BeanNameAware`, `InitializingBean`, and `DisposableBean`. Their lifecycle methods run when the bean gets its name, after the properties are set, and when the bean is destroyed. Finally, `toString()` gives an easy way to see the state of the bean.

```java
public class MyService implements BeanNameAware, InitializingBean, DisposableBean {

    @Autowired
    private MyRepository myRepository;

    @Value("${java.home}")
    private String javaHome;

    @Override
    public void setBeanName(String name) {
        System.out.println("MyService.setBeanName method was called");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("MyService.afterPropertiesSet method was called");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("MyService.destroy method was called");
    }

    @Override
    public String toString() {
        return "MyService{" +
                "myRepository=" + myRepository +
                ", javaHome='" + javaHome + '\'' +
                '}';
    }
}
```

`MyBeanPostProcessor` is a custom bean post-processor. It implements the Spring `BeanPostProcessor` interface. Before and after bean initialization, the `postProcessBeforeInitialization` and `postProcessAfterInitialization` methods are called. In this example, they only print a log line with the name of the bean being processed.

```java
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor#postProcessBeforeInitialization method was called, bean name = " + beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor#postProcessBeforeInitialization method was called, bean name = " + beanName);
        return bean;
    }
}
```

#### createBean

The `createBean` method of `AutowireCapableBeanFactory` creates a `MyService` bean instance by hand.

```java
private static void createBean(AutowireCapableBeanFactory beanFactory) {
    MyService myService = beanFactory.createBean(MyService.class);
    System.out.println("Called createBean, created bean = " + myService);
}
```

The output shows that the callbacks in the `MyService` lifecycle, such as `setBeanName` and `afterPropertiesSet`, are all triggered. So the bean initialization runs as expected. The `MyBeanPostProcessor` also steps in before and after initialization and prints the bean name. Most important, both `myRepository` and the `javaHome` property (injected with `@Value`) are injected. So dependency injection works too.

```java
MyService.setBeanName method was called
MyBeanPostProcessor#postProcessBeforeInitialization method was called, bean name = com.xcs.spring.service.MyService
MyService.afterPropertiesSet method was called
MyBeanPostProcessor#postProcessBeforeInitialization method was called, bean name = com.xcs.spring.service.MyService
Called createBean, created bean = MyService{myRepository=com.xcs.spring.repository.MyRepository@5b03b9fe, javaHome='D:\install\jdk-11'}
```

#### configureBean

The `configureBean` method of `AutowireCapableBeanFactory` configures a bean by hand. First, the `registerBeanDefinition` method registers a `RootBeanDefinition` named "myService". This is the definition of the bean to configure. Next, a new `MyService` instance is created. The `configureBean` method configures it with the "myService" bean definition registered before. The code prints the `MyService` instance before and after the call, so you can see if the configuration worked.

```java
private static void configureBean(AutowireCapableBeanFactory beanFactory) {
    // Register a RootBeanDefinition
    ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("myService", new RootBeanDefinition(MyService.class));

    MyService myService = new MyService();
    System.out.println("Before configureBean, MyService = " + myService);
    beanFactory.configureBean(myService, "myService");
    System.out.println("After configureBean, MyService = " + myService);
}
```

The output shows that configuring a `MyService` instance with `configureBean` gives a result similar to `createBean`. Before `configureBean`, the properties of `MyService` are null. Then the callbacks `setBeanName`, `postProcessBeforeInitialization`, and `afterPropertiesSet` run in order. This means the bean initialization runs as expected. After `configureBean`, `myRepository` and the `javaHome` property (injected with `@Value`) are both injected.

```java
Before configureBean, MyService = MyService{myRepository=null, javaHome='null'}
MyService.setBeanName method was called
MyBeanPostProcessor#postProcessBeforeInitialization method was called, bean name = myService
MyService.afterPropertiesSet method was called
MyBeanPostProcessor#postProcessBeforeInitialization method was called, bean name = myService
After configureBean, MyService = MyService{myRepository=com.xcs.spring.repository.MyRepository@5b03b9fe, javaHome='D:\install\jdk-11'}
```

#### autowireBean

The `autowireBean` method of `AutowireCapableBeanFactory` autowires a bean by hand. First, a `MyService` instance is created. Then `autowireBean` autowires it. The code prints the `MyService` instance before and after the call, so you can see if the autowiring worked.

```java
private static void autowireBean(AutowireCapableBeanFactory beanFactory) {
    MyService myService = new MyService();
    System.out.println("Before autowireBean, MyService = " + myService);
    beanFactory.autowireBean(myService);
    System.out.println("After autowireBean, MyService = " + myService);
}
```

The output shows that `autowireBean` autowires a newly created `MyService` instance by hand. Before `autowireBean`, the properties of `MyService` are null. After the call, `myRepository` is injected with an instance of `com.xcs.spring.repository.MyRepository`, and the `javaHome` property (injected with `@Value`) has the value 'D:\install\jdk-11'.

Note that `autowireBean` does not call `setBeanName` of `BeanNameAware`, `afterPropertiesSet` of `InitializingBean`, or the callbacks of the custom `MyBeanPostProcessor`. This is because `autowireBean` only does dependency injection. It does not manage the full bean lifecycle.

```java
Before autowireBean, MyService = MyService{myRepository=null, javaHome='null'}
After autowireBean, MyService = MyService{myRepository=com.xcs.spring.repository.MyRepository@5b03b9fe, javaHome='D:\install\jdk-11'}
```

#### autowire

The `autowire` method of `AutowireCapableBeanFactory` creates and autowires a bean of type `MyService`. The `AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE` argument means autowiring by type. After the call, the code prints the `MyService` instance created by `autowire`.

```java
private static void autowire(AutowireCapableBeanFactory beanFactory) {
    Object myService = beanFactory.autowire(MyService.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
    System.out.println("Called autowire, created bean = " + myService);
}
```

The output shows that `autowire` and `autowireBean` give a similar result.

But the two methods are used in slightly different cases. `autowireBean` autowires an existing instance. `autowire` creates a new bean of the given class and autowires it. So which one to use depends on what you need.

```java
Called autowire, created bean = MyService{myRepository=com.xcs.spring.repository.MyRepository@4145bad8, javaHome='D:\install\jdk-11'}
```

#### autowireBeanProperties

The `autowireBeanProperties` method of `AutowireCapableBeanFactory` autowires the properties of a newly created `MyService` instance. First, a `MyService` instance is created and its initial state is printed. Then `autowireBeanProperties` autowires the properties with the `AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE` rule. Finally, the code prints the `MyService` instance after the call, so you can see if the autowiring worked.

```java
private static void autowireBeanProperties(AutowireCapableBeanFactory beanFactory) {
    MyService myService = new MyService();
    System.out.println("Before autowireBeanProperties, MyService = " + myService);
    beanFactory.autowireBeanProperties(myService, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
    System.out.println("After autowireBeanProperties, MyService = " + myService);
}
```

The output of `autowireBeanProperties` is similar to the earlier `autowireBean` and `autowire`. Before the call, the properties of `MyService` are null. After the call, `myRepository` is injected with an instance of `com.xcs.spring.repository.MyRepository`, and the `javaHome` property (injected with `@Value`) has the value 'D:\install\jdk-11'.

```
Before autowireBeanProperties, MyService = MyService{myRepository=null, javaHome='null'}
After autowireBeanProperties, MyService = MyService{myRepository=com.xcs.spring.repository.MyRepository@4145bad8, javaHome='D:\install\jdk-11'}
```

#### applyBeanPropertyValues

The `applyBeanPropertyValues` method of `AutowireCapableBeanFactory` sets custom property values on a `MyService` bean by hand. First, a `PropertyValue` is created. The property name is "javaHome" and the value is "My custom javaHome path". Next, a `MutablePropertyValues` collection is built and the `PropertyValue` is added to it. Then a `RootBeanDefinition` is created and the property values are set on it. Finally, the `registerBeanDefinition` method registers this bean definition under the name "myService". Before calling `applyBeanPropertyValues`, a new `MyService` instance is created and its initial state is printed. After the call, the code prints the `MyService` instance again, so you can see if the custom property values were applied.

```java
private static void applyBeanPropertyValues(AutowireCapableBeanFactory beanFactory) {
    PropertyValue propertyValue = new PropertyValue("javaHome", "My custom javaHome path");
    MutablePropertyValues propertyValues = new MutablePropertyValues();
    propertyValues.addPropertyValue(propertyValue);

    RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(MyService.class);
    rootBeanDefinition.setPropertyValues(propertyValues);
    // Register a RootBeanDefinition
    ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition("myService", rootBeanDefinition);

    MyService myService = new MyService();
    System.out.println("Before applyBeanPropertyValues, MyService = " + myService);
    beanFactory.applyBeanPropertyValues(myService, "myService");
    System.out.println("After applyBeanPropertyValues, MyService = " + myService);
}
```

The output shows that `applyBeanPropertyValues` does not call `setBeanName` of `BeanNameAware`, `afterPropertiesSet` of `InitializingBean`, or the callbacks of the custom `MyBeanPostProcessor`. This is because `applyBeanPropertyValues` only applies property values. It does not run the full bean initialization and lifecycle. The `myRepository` property stays null. So `applyBeanPropertyValues` does not do dependency injection.

```java
Before applyBeanPropertyValues, MyService = MyService{myRepository=null, javaHome='null'}
After applyBeanPropertyValues, MyService = MyService{myRepository=null, javaHome='My custom javaHome path'}
```

#### initializeBean

The `initializeBean` method of `AutowireCapableBeanFactory` initializes a `MyService` bean by hand. First, a new `MyService` instance is created and its initial state is printed. Then `initializeBean` initializes it with the bean name "myService". After the call, the code prints the `MyService` instance, so you can see if the initialization worked.

```java
private static void initializeBean(AutowireCapableBeanFactory beanFactory) {
    MyService myService = new MyService();
    System.out.println("Before initializeBean, MyService = " + myService);
    beanFactory.initializeBean(myService, "myService");
    System.out.println("After initializeBean, MyService = " + myService);
}
```

The output shows that `myRepository` and `javaHome` are still `null`. This is because `initializeBean` does not inject any properties. The `initializeBean` method triggers the bean initialization phase by hand. This includes calling `afterPropertiesSet` and applying the bean post-processors. It does not inject properties.

```java
Before initializeBean, MyService = MyService{myRepository=null, javaHome='null'}
MyService.setBeanName method was called
MyBeanPostProcessor#postProcessBeforeInitialization method was called, bean name = myService
MyService.afterPropertiesSet method was called
MyBeanPostProcessor#postProcessBeforeInitialization method was called, bean name = myService
After initializeBean, MyService = MyService{myRepository=null, javaHome='null'}
```

#### destroyBean

The `destroyBean` method of `AutowireCapableBeanFactory` destroys a `MyService` bean instance by hand. A newly created `MyService` instance is passed to `destroyBean`.

```java
private static void destroyBean(AutowireCapableBeanFactory beanFactory) {
    beanFactory.destroyBean(new MyService());
}
```

The output shows that after calling `destroyBean`, the `destroy` method of the `MyService` instance is called. This means `destroyBean` triggers the destruction phase of the bean and runs the `destroy` method from the `DisposableBean` interface.

```java
MyService.destroy method was called
```

#### resolveDependency

The `resolveDependency` method of `AutowireCapableBeanFactory` resolves a dependency by hand. A `DependencyDescriptor` is created for the `myRepository` field of the `MyService` class. Then `resolveDependency` tries to resolve this dependency. Finally, the code prints the resolved object.

```java
private static void resolveDependency(AutowireCapableBeanFactory beanFactory) {
    try {
        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(MyService.class.getDeclaredField("myRepository"), false);
        Object resolveDependency = beanFactory.resolveDependency(dependencyDescriptor, "myRepository");
        System.out.println("resolveDependency = " + resolveDependency);
    } catch (NoSuchFieldException e) {
        e.printStackTrace();
    }
}
```

The output shows that `resolveDependency` resolves the dependency. The `myRepository` dependency is resolved to a `MyRepository` instance.

```java
resolveDependency = com.xcs.spring.repository.MyRepository@37654521
```

### 7. Common Problems

1. **createBean() and configureBean()**

   - `createBean()` creates a bean instance, that is, it instantiates the bean. It is the first step of bean creation.
   - `configureBean()` configures a bean instance that already exists. For example, it applies BeanPostProcessors. It works after the bean is instantiated and before it is fully initialized.

2. **autowireBean() and autowire()**

   - `autowireBean()` autowires an existing bean instance. It injects the dependencies into the bean.
   - `autowire()` creates a new bean instance with the given autowire mode.

3. **autowireBeanProperties() and applyBeanPropertyValues()**

   - `autowireBeanProperties()` autowires the properties of a bean instance.
   - `applyBeanPropertyValues()` applies property values to a bean instance. This includes the property values set in XML or annotations.

4. **initializeBean(), applyBeanPostProcessorsBeforeInitialization() and applyBeanPostProcessorsAfterInitialization()**

   - `initializeBean()` is the last step of the bean lifecycle. It includes initialization and applying BeanPostProcessors.
   - `applyBeanPostProcessorsBeforeInitialization()` applies BeanPostProcessors before initialization.
   - `applyBeanPostProcessorsAfterInitialization()` applies BeanPostProcessors after initialization.

5. **destroyBean()**

   - `destroyBean()` destroys the given bean instance and releases resources. It is usually called when the container shuts down.

6. **resolveNamedBean() and resolveBeanByName()**

   - `resolveNamedBean()` resolves a bean of the given type and returns the bean instance together with its name.
   - `resolveBeanByName()` resolves the bean with the given name. It uses a dependency descriptor for the injection point.

7. **resolveDependency()**

   - `resolveDependency()` resolves the dependencies between beans, especially during autowiring. It is called in the `doResolveDependency()` method of `AbstractAutowireCapableBeanFactory`.
