## @Lazy

- [@Lazy](#lazy)
  - [1. Overview](#1-overview)
  - [2. Annotation Description](#2-annotation-description)
  - [3. Annotation Source](#3-annotation-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
    - [Bean Registration Sequence Diagram](#bean-registration-sequence-diagram)
    - [Lazy Bean Creation Sequence Diagram](#lazy-bean-creation-sequence-diagram)
    - [Lazy Injection Sequence Diagram](#lazy-injection-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [Lazy Initialization](#lazy-initialization)
    - [Lazy Injection](#lazy-injection)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133800805) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [@Lazy source code](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-lazy)

### 2. Annotation Description

The `@Lazy` annotation delays the initialization of a bean. Normally, when the ApplicationContext starts and refreshes, all singleton beans are initialized right away. Sometimes you want a bean to be initialized only when it is first used.

### 3. Annotation Source

`@Lazy` has been part of Spring since version 3.0. It controls whether a bean is created lazily. By default, when `@Lazy` is applied, the bean is not initialized when the Spring container starts. It is initialized when it is first referenced or requested. This works for beans defined with `@Component` or `@Bean`. `@Lazy` can also be placed on an injection point such as `@Autowired`. In that case it creates a lazy-resolution proxy, which gives you lazy injection. When used on a `@Configuration` class, it affects all `@Bean` definitions in that class.

```java
/**
 * Indicates whether a bean is to be lazily initialized.
 *
 * May be used on any class directly or indirectly annotated with
 * org.springframework.stereotype.Component @Component or on methods annotated with Bean @Bean.
 *
 * If this annotation is not present on a @Component or @Bean definition, eager initialization will occur.
 * If present and set to true, the @Bean or @Component will not be initialized until referenced
 * by another bean or explicitly retrieved from the enclosing org.springframework.beans.factory.BeanFactory BeanFactory.
 * If present and set to false, the bean will be instantiated on startup by bean factories that perform eager initialization of singletons.
 *
 * If Lazy is present on a Configuration @Configuration class, this indicates that all @Bean methods within
 * that @Configuration should be lazily initialized. If @Lazy is present and set to false on a @Bean method
 * of a @Lazy-annotated @Configuration class, this indicates overriding the "default lazy" behavior and that the bean should be eagerly initialized.
 *
 * In addition to its role for component initialization, this annotation may also be placed on injection points
 * marked with org.springframework.beans.factory.annotation.Autowired or javax.inject.Inject:
 * In that context, it leads to the creation of a lazy-resolution proxy for all affected dependencies,
 * as an alternative to using org.springframework.beans.factory.ObjectFactory or javax.inject.Provider.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Primary
 * @see Bean
 * @see Configuration
 * @see org.springframework.stereotype.Component
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lazy {

	/**
	 * Whether lazy initialization should occur.
	 */
	boolean value() default true;

}
```

### 4. Main Features

1. **Lazy initialization**
   + When `@Lazy` is applied to a `@Bean` or `@Component`, the bean is not initialized when the Spring container starts. It is initialized only when it is first referenced or requested.
2. **Lazy injection**
   + When `@Lazy` is applied to `@Autowired` or another injection point, Spring creates a lazy-resolution proxy for the injected dependency. The real bean is injected on first access.

### 5. Best Practices

First, the entry class. The context is `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass in the `MyConfiguration` class, then get a `MyService` bean from it and call its `show` method.

```java
public class LazyApplication {

    public static void main(String[] args) {
        System.out.println("Starting Spring ApplicationContext...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("Spring ApplicationContext started...");

        System.out.println("Getting MyService...");
        MyService myService = context.getBean(MyService.class);

        System.out.println("Calling show method...");
        myService.show();
    }
}
```

Here the `@Bean` annotation is used. `MyBean` is marked with `@Lazy`, so it is initialized only when it is first requested. `MyService` has no `@Lazy`, so it is initialized right away.

```java
@Configuration
public class MyConfiguration {

    @Bean
    @Lazy
    public MyBean myBean(){
        System.out.println("MyBean initialized!");
        return new MyBean();
    }

    @Bean
    public MyService myService(){
        return new MyService();
    }
}
```

The `MyBean` class is simple. Its constructor prints "MyBean constructor called!". It also has a `show` method that prints "hello world!".

```java
public class MyBean {

    public MyBean() {
        System.out.println("MyBean constructor called!");
    }

    public void show() {
        System.out.println("hello world!");
    }
}
```

The `MyService` class holds a reference to `MyBean`, injected with `@Autowired`. We also added `@Lazy` on this injection point. So the real injection of `myBean` is delayed until we first try to use it.

```java
public class MyService  {

    @Autowired
    @Lazy
    private MyBean myBean;

    public void show() {
        System.out.println("MyBean Class = " + myBean.getClass());
        myBean.show();
    }
}
```

The run result shows the following.

1. **Lazy initialization**:
   - `MyService` is available after the application context starts. But `MyBean` is not initialized at startup because of `@Lazy` on it.
   - `MyBean` is really initialized only when it is first accessed or used. You can see this in the output "MyBean initialized!" and "MyBean constructor called!".
2. **Lazy injection**:
   - When the `show` method of `MyService` is first called, Spring does not inject the real `MyBean` instance, because of the `@Autowired` plus `@Lazy` combination. It injects a proxy object instead.
   - The class name of this proxy is `com.xcs.spring.bean.MyBean$$EnhancerBySpringCGLIB$$2a517c55`. This shows that Spring generated it with CGLIB.
   - When a method such as `show` is called on the proxy, the proxy makes sure the real `MyBean` is initialized and then forwards the call.

```java
Starting Spring ApplicationContext...
Spring ApplicationContext started...
Getting MyService...
Calling show method...
MyBean Class = class com.xcs.spring.bean.MyBean$$EnhancerBySpringCGLIB$$2a517c55
MyBean initialized!
MyBean constructor called!
hello world!
```

### 6. Sequence Diagram

#### Bean Registration Sequence Diagram

~~~mermaid
sequenceDiagram 
LazyApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create application context
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>Refresh application context
AbstractApplicationContext->>AbstractApplicationContext:invokeBeanFactoryPostProcessors(beanFactory)<br>Call BFPP methods
AbstractApplicationContext->>PostProcessorRegistrationDelegate:invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>Delegate BFPP handling
PostProcessorRegistrationDelegate->>PostProcessorRegistrationDelegate:invokeBeanDefinitionRegistryPostProcessors(postProcessors,registry,applicationStartup)<br>Call BDRPP methods
PostProcessorRegistrationDelegate->>ConfigurationClassPostProcessor:postProcessBeanDefinitionRegistry(registry)<br>Process bean definitions
ConfigurationClassPostProcessor->>ConfigurationClassPostProcessor:processConfigBeanDefinitions(registry)<br>Process configuration class beans
ConfigurationClassPostProcessor->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitions(configurationModel)<br>Load bean definitions
ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitionsForConfigurationClass(configClass,trackedConditionEvaluator)<br>Load for the configuration class
ConfigurationClassBeanDefinitionReader->>ConfigurationClassBeanDefinitionReader:loadBeanDefinitionsForBeanMethod(beanMethod)<br>Load for the @Bean method
ConfigurationClassBeanDefinitionReader->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd,metadata)<br>Process annotation definitions
AnnotationConfigUtils->>AnnotationConfigUtils:attributesFor(metadata, Lazy.class)<br>Get annotation attributes
AnnotationConfigUtils->>AbstractBeanDefinition:setLazyInit(lazyInit)<br>Set the lazy-init attribute
~~~

#### Lazy Bean Creation Sequence Diagram

~~~mermaid
sequenceDiagram 
DependsOnApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create application context
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>Refresh application context
AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>Finish bean factory initialization
AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>Pre-instantiate singleton beans
DefaultListableBeanFactory->>AbstractBeanDefinition:isLazyInit()
AbstractBeanDefinition->>DefaultListableBeanFactory:Return whether the bean is lazy
DefaultListableBeanFactory->>AbstractBeanFactory:getBean(beanName)
alt Bean is lazy
	AbstractBeanFactory->>DefaultListableBeanFactory: Run bean initialization
else Bean is not lazy
	AbstractBeanFactory->>DefaultListableBeanFactory: Skip bean initialization, wait until it is really used
end
~~~

#### Lazy Injection Sequence Diagram

~~~mermaid
sequenceDiagram
Title: InstantiationAwareBeanPostProcessor sequence diagram
InstantiationAwareBeanPostProcessorApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext<br>Start initialization
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh<br>Refresh context
AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization<br>Instantiate non-lazy singleton beans
AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons<br>Pre-instantiate singletons
DefaultListableBeanFactory->>AbstractBeanFactory:getBean<br>Get the object by beanName
AbstractBeanFactory->>AbstractBeanFactory:doGetBean<br>Return the instance of the given bean
AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton<br>Get the singleton object
DefaultSingletonBeanRegistry->>AbstractBeanFactory:getObject<br>Factory method of the ObjectFactory interface
AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean<br>Create a bean instance, populate it, and apply post-processors
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,bw)
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessProperties(pvs,bean,beanName) 
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)
AutowiredAnnotationBeanPostProcessor->>InjectionMetadata:inject(target,beanName,pvs)
InjectionMetadata->>AutowiredFieldElement:inject(bean,beanName,pvs)
AutowiredFieldElement->>AutowiredFieldElement:resolveFieldValue(field,bean,beanName)
AutowiredFieldElement->>DefaultListableBeanFactory:resolveDependency(descriptor,requestingBeanName,autowiredBeanNames,typeConverter)
DefaultListableBeanFactory->>DefaultListableBeanFactory:getAutowireCandidateResolver()
DefaultListableBeanFactory->>ContextAnnotationAutowireCandidateResolver:getLazyResolutionProxyIfNecessary(descriptor,beanName)
ContextAnnotationAutowireCandidateResolver->>ContextAnnotationAutowireCandidateResolver:isLazy(descriptor)
ContextAnnotationAutowireCandidateResolver->>ContextAnnotationAutowireCandidateResolver:buildLazyResolutionProxy(descriptor, beanName)
ContextAnnotationAutowireCandidateResolver->>ProxyFactory:getProxy(classLoader)
ProxyFactory->>ContextAnnotationAutowireCandidateResolver:Return the proxied object
ContextAnnotationAutowireCandidateResolver->>DefaultListableBeanFactory:Return the proxied object
DefaultListableBeanFactory->>AutowiredFieldElement:Return the proxied object
AutowiredFieldElement->>Field:field.set(bean, value) injects the proxied object
~~~

### 7. Source Code Analysis

First, the entry class. The context is `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass in the `MyConfiguration` class, then get a `MyService` bean from it and call its `show` method.

```java
public class LazyApplication {

    public static void main(String[] args) {
        System.out.println("Starting Spring ApplicationContext...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("Spring ApplicationContext started...");

        System.out.println("Getting MyService...");
        MyService myService = context.getBean(MyService.class);

        System.out.println("Calling show method...");
        myService.show();
    }
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor does three steps.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

In the `org.springframework.context.support.AbstractApplicationContext#refresh` method, we focus on `finishBeanFactoryInitialization(beanFactory)`. It instantiates all remaining non-lazy singleton beans. The other methods are not the focus here, so we skip them.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [code omitted for brevity]
    // Instantiate all remaining non-lazy singleton beans
    finishBeanFactoryInitialization(beanFactory);
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization` method calls `preInstantiateSingletons` in `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

```java
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Finish creating all remaining non-lazy singleton beans.
    beanFactory.preInstantiateSingletons();
}
```

#### Lazy Initialization

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` method makes sure all non-lazy singleton beans are initialized when the container starts, unless they are explicitly marked as lazy. This is why `@Lazy` is useful when you want to delay bean initialization.

```java
public void preInstantiateSingletons() throws BeansException {
    // ... [code omitted for brevity]
    // Copy the list of bean names
    List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

    // Initialize non-lazy singletons
    for (String beanName : beanNames) {
        RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
        // Check bean attributes
        if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
            // Is it a FactoryBean?
            if (isFactoryBean(beanName)) {
                Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
                // Is it a FactoryBean instance?
                if (bean instanceof FactoryBean) {
                    FactoryBean<?> factory = (FactoryBean<?>) bean;
                    boolean isEagerInit;
                    // Security permission check
                    if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                        isEagerInit = AccessController.doPrivileged(
                            (PrivilegedAction<Boolean>) ((SmartFactoryBean<?>) factory)::isEagerInit,
                            getAccessControlContext());
                    }
                    else {
                        // Should it be initialized right away?
                        isEagerInit = (factory instanceof SmartFactoryBean &&
                                       ((SmartFactoryBean<?>) factory).isEagerInit());
                    }
                    // Initialize the bean right away
                    if (isEagerInit) {
                        getBean(beanName);
                    }
                }
            }
            else {
                // Get or create the bean
                getBean(beanName);
            }
        }
    }
    // ... [code omitted for brevity]
}
```

#### Lazy Injection

The `org.springframework.beans.factory.support.AbstractBeanFactory#getBean()` method calls `doGetBean` to do the real work of creating the bean. It passes the bean name and some default values. `doGetBean` does most of the work, such as finding the bean definition, creating the bean if it does not exist yet, and handling dependencies.

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

In the `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean` method, Spring first checks whether the requested bean is a singleton that already exists. If not, it creates a new instance. It also handles possible problems such as circular references, and makes sure the returned bean has the right type. This is a core part of bean lifecycle management in the Spring container.

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
        // For some beans (such as FactoryBeans), more processing may be needed to get the real bean instance
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
    }
    // ... [code omitted for brevity]

    // Make sure the returned bean instance matches the requested type
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

The `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton()` method gets an existing bean instance from the singleton cache. Or it creates a new one with the given `ObjectFactory`. This is the key part that keeps a bean a singleton in the Spring container.

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    // ... [code omitted for brevity]

    // Synchronize access to the singleton cache to keep it thread safe
    synchronized (this.singletonObjects) {
        // Get the singleton object from the cache
        Object singletonObject = this.singletonObjects.get(beanName);

        // If it is not found in the cache
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

In the `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean()` method, the main logic is to call `doCreateBean`. That is where the bean is instantiated, populated, and initialized. The method returns the newly created bean instance.

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

The `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` method injects the bean's properties.

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    // ... [code omitted for brevity]
    
    try {
        // Property injection: this step injects configured property values into the bean instance. For example, properties defined in XML or marked with @Autowired and @Value are injected here
        populateBean(beanName, mbd, instanceWrapper);

       	// ... [code omitted for brevity]
    } 
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }
    
	// ... [code omitted for brevity]
}
```

In the `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean` method, if a property is marked `@Autowired` together with `@Lazy`, the lazy logic is handled in a later part of this step (mainly by `AutowiredAnnotationBeanPostProcessor`).

```java
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
    // ... [code omitted for brevity]
    // Handle each InstantiationAwareBeanPostProcessor. These processors may change the bean's property values.
    for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
        // Try the newer method postProcessProperties
        PropertyValues pvsToUse = bp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
         // ... [code omitted for brevity]
    }
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties` method handles dependency injection for `@Autowired`.

```java
@Override
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
    // Find the @Autowired metadata by bean name and class
    InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
    try {
        // Do the actual dependency injection
        // Do the actual dependency injection
        metadata.inject(bean, beanName, pvs);
    }
    catch (BeanCreationException ex) {
        // Rethrow bean creation exception
        throw ex;
    }
    catch (Throwable ex) {
        // Convert other exceptions to BeanCreationException
        throw new BeanCreationException(beanName, "Autowiring of dependencies failed", ex);
    }
    // Return the property values
    return pvs;
}
```

In the `org.springframework.beans.factory.annotation.InjectionMetadata#inject` method, all these elements are visited and their `inject` method is called. This does the real injection into annotated fields or methods.

```java
public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // Get the checked injection elements
    Collection<InjectedElement> checkedElements = this.checkedElements;
    // If there are no checked elements, use all injection elements
    Collection<InjectedElement> elementsToIterate =
        (checkedElements != null ? checkedElements : this.injectedElements);
    
    if (!elementsToIterate.isEmpty()) {
        // Go through all elements to inject (fields or methods)
        for (InjectedElement element : elementsToIterate) {
            // Do the actual injection
            element.inject(target, beanName, pvs);
        }
    }
}
```

In the `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject` method, the core logic is to resolve the value of the `@Autowired` field (find the matching bean dependency) and inject it into the target bean. A cache is used to improve performance.

```java
@Override
protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // Get the field marked with @Autowired
    Field field = (Field) this.member;
    Object value;
    // If the field value is already cached, get it from the cache
    if (this.cached) {
        try {
            value = resolvedCachedArgument(beanName, this.cachedFieldValue);
        }
        catch (NoSuchBeanDefinitionException ex) {
            // The cached bean was removed unexpectedly -> resolve again
            value = resolveFieldValue(field, bean, beanName);
        }
    }
    else {
        // Resolve the field value (find the bean to inject)
        value = resolveFieldValue(field, bean, beanName);
    }
    if (value != null) {
        // Make the field accessible (for example, if it is private)
        ReflectionUtils.makeAccessible(field);
        // Inject the resolved value (bean) into the field of the target bean
        field.set(bean, value);
    }
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#resolveFieldValue` method resolves the value of an `@Autowired` field. It decides which bean should be injected into the target field.

```java
@Nullable
private Object resolveFieldValue(Field field, Object bean, @Nullable String beanName) {
	// ... [code omitted for brevity]

    Object value;
    try {
        // Resolve the dependency: the core logic is to ask the bean factory to resolve the field's dependency. It looks for a suitable bean to inject into this field.
        value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
    }
    catch (BeansException ex) {
        // Throw an exception when the dependency cannot be resolved
        throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
    }
    
    // ... [code omitted for brevity]

    // Return the resolved value (bean)
    return value; 
}
```

In the `org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency` method, the key logic is `getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary`. It tries to get a lazy proxy for the described dependency. If the dependency is marked `@Lazy` and referenced by `@Autowired`, it returns a lazy proxy instead of the real bean. So the real bean is initialized only when the application actually uses the dependency.

```java
@Override
@Nullable
public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                                @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
	// ... [code omitted for brevity]
    
    // Try to get a lazy proxy for the described dependency. If the dependency is lazy, this returns a proxy object.
    Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
        descriptor, requestingBeanName);

    // If no proxy was returned for a lazy dependency, resolve the dependency directly
    if (result == null) {
        result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
    }

    return result; // Return the resolved bean or the lazy proxy
}
```

In the `org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver#getLazyResolutionProxyIfNecessary` method, if the dependency is lazy, `buildLazyResolutionProxy` is called to create a proxy. When the proxy is first accessed, it triggers the real bean initialization.

```java
@Override
@Nullable
public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, @Nullable String beanName) {
    // Check whether the dependency descriptor is marked lazy
    // If it is lazy, build a lazy proxy for it
    // If it is not lazy, return null
    return (isLazy(descriptor) ? buildLazyResolutionProxy(descriptor, beanName) : null);
}
```

In the `org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver#buildLazyResolutionProxy` method, the key parts are `TargetSource` and `ProxyFactory`. When we access the lazy proxy, the `getTarget` method of the `TargetSource` is called. It resolves and returns the real bean (or other dependency). `ProxyFactory` creates a new proxy for the given `TargetSource`. This is the real implementation of `@Lazy` for field injection. It makes sure dependency resolution and bean initialization happen only on first access to the field.

```java
protected Object buildLazyResolutionProxy(final DependencyDescriptor descriptor, final @Nullable String beanName) {
    // Get the current BeanFactory
    BeanFactory beanFactory = getBeanFactory();
    // Make sure the BeanFactory is an instance of DefaultListableBeanFactory
    Assert.state(beanFactory instanceof DefaultListableBeanFactory,
                 "BeanFactory needs to be a DefaultListableBeanFactory");
    final DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;

    // Create a TargetSource for the lazy proxy
    TargetSource ts = new TargetSource() {
        // Get the dependency type
        @Override
        public Class<?> getTargetClass() {
            return descriptor.getDependencyType();
        }
        @Override
        public boolean isStatic() {
            return false;
        }
        // This method is called when the proxy is accessed, to resolve the real dependency
        @Override
        public Object getTarget() {
            // ... [code omitted for brevity]
            return target; // Return the resolved bean
        }
        @Override
        public void releaseTarget(Object target) {
        }
    };

    // Use Spring's ProxyFactory to create a new proxy
    ProxyFactory pf = new ProxyFactory();
    pf.setTargetSource(ts);
    Class<?> dependencyType = descriptor.getDependencyType();
    if (dependencyType.isInterface()) {
        pf.addInterface(dependencyType);
    }
    // Return the lazy proxy
    return pf.getProxy(dlbf.getBeanClassLoader());
}
```

### 8. Notes

1. **Default behavior**
   + Without `@Lazy`, the Spring container creates singleton beans right away at startup. With `@Lazy`, a bean is initialized only on first request.
2. **Constructor injection**
   + If a lazy bean is a dependency of a non-lazy bean, the lazy bean is initialized at container startup because the non-lazy bean needs it. This often happens with constructor injection.
3. **Context lifecycle**
   + `@Lazy` can make the application context start faster, because less work is done up front. But the first access to a bean may be slower.
4. **Using it with `@Autowired`**
   + If you use `@Autowired` and `@Lazy` together on a field or setter method, Spring injects a proxy object. The proxy triggers the real bean initialization on first access.
5. **Lazy proxy**
   + When used with `@Autowired`, make sure the bean type works with the proxy. If the injected type is an interface, Spring creates an interface-based proxy. If it is a class, Spring creates a class-based proxy.
6. **Error handling**
   + With lazy initialization, some errors, such as bean configuration mistakes, may not show up at startup. They are thrown only when the bean is actually initialized.
7. **On component classes**
   + For classes annotated directly or indirectly with `@Component`, `@Service`, `@Repository`, or `@Controller`, you can use `@Lazy` so the component is initialized only when it is first injected or looked up.

### 9. Summary

#### Best Practices Summary

1. **Context initialization**:
   - Using `AnnotationConfigApplicationContext` is the recommended way to start the application context for Java-based configuration.
   - Passing a configuration class (such as `MyConfiguration`) tells the context how to load and configure beans.
2. **Lazy configuration**:
   - Adding `@Lazy` to a `@Bean` method in a configuration class makes sure that bean is initialized only on first request, not when the application context starts.
   - This can speed up startup, especially for beans that use many resources or take long to initialize.
3. **Dependency injection**:
   - `@Autowired` is a core Spring feature. It injects bean dependencies automatically.
   - When combined with `@Lazy`, Spring injects a proxy object instead of the real bean instance. The proxy triggers the real bean initialization on first access.
4. **Understanding the proxy**:
   - Because of lazy injection, the object injected with `@Autowired` and `@Lazy` is a proxy generated by CGLIB.
   - This proxy initializes the real bean on first access.
5. **Output and verification**:
   - Add logs or print statements to bean initialization and method calls to check and observe lazy loading and proxy behavior.
   - This helps you confirm the expected behavior and tune performance.

#### Source Code Analysis Summary

1. **Startup and initialization**:
   - The application context is initialized with `AnnotationConfigApplicationContext`.
   - The constructor of `AnnotationConfigApplicationContext` calls `register` and `refresh`.
2. **Bean instantiation**:
   - During context refresh, `finishBeanFactoryInitialization(beanFactory)` makes sure all non-lazy singleton beans are instantiated.
   - `DefaultListableBeanFactory#preInstantiateSingletons` makes sure all non-lazy singleton beans are initialized when the container starts.
3. **Lazy initialization**:
   - A bean marked `@Lazy` is not initialized when the container starts. It is initialized only on first request.
   - In `DefaultListableBeanFactory#preInstantiateSingletons`, beans where `isLazyInit` returns `true` are not pre-initialized.
4. **Getting beans and dependency injection**:
   - `AbstractBeanFactory#getBean` is used to get a bean instance.
   - If the bean does not exist yet, `doGetBean` creates it. This includes resolving dependencies and handling circular references.
   - Singleton beans are cached, so the same instance is returned every time.
   - `AbstractAutowireCapableBeanFactory#createBean` does the real bean creation, and `populateBean` injects its properties.
5. **Lazy injection**:
   - If a field or property is marked `@Autowired` and also `@Lazy`, the lazy logic is handled in the property population step.
   - `AutowiredAnnotationBeanPostProcessor` handles injection for properties marked `@Autowired`.
   - In `AutowiredFieldElement#inject`, if the field is marked `@Lazy`, Spring does not inject the real bean. It injects a lazy proxy.
   - The lazy proxy triggers the real bean initialization on first access.
6. **Creating the lazy proxy**:
   - `ContextAnnotationAutowireCandidateResolver` checks whether a dependency needs to be lazy.
   - If so, it uses `buildLazyResolutionProxy` to create a proxy for that dependency.
   - On first access, the proxy resolves and returns the real bean or other dependency.
