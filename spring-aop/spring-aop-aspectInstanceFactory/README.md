## AspectInstanceFactory

- [AspectInstanceFactory](#aspectinstancefactory)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementations](#5-main-implementations)
  - [6. Class Diagram](#6-class-diagram)
  - [7. Best Practices](#7-best-practices)
  - [8. Source Code Analysis](#8-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `AspectInstanceFactory` interface is a key interface in Spring AOP. It creates aspect instances at runtime, to fit different cases and needs. Its implementation classes provide the aspect instance through the `getAspectInstance()` method, and can decide how the aspect is created.

### 3. Main Features

1. **Create aspect instances dynamically**

   + Lets you create aspect instances at runtime, so that aspect features can be applied in the application.

2. **Provide aspect instances**

   + The `getAspectInstance()` method gets the aspect instance for Spring AOP to use.

3. **Manage the aspect lifecycle**

   + Controls the lifecycle of the aspect instance. For example, it can be a singleton (Singleton) or a prototype (Prototype).

4. **Fit different needs flexibly**

   + Lets you customize how aspect instances are created and managed to fit the needs of the application. This gives flexibility and extensibility.

### 4. Interface Source

 The `AspectInstanceFactory` interface provides instances of AspectJ aspects. It is decoupled from the Spring bean factory. It creates the aspect instance through `getAspectInstance()`, and exposes the aspect class loader through `getAspectClassLoader()`. The interface also extends the `Ordered` interface to express the order value of the aspect in a chain.

```java
/**
 * Interface that provides an AspectJ aspect instance, decoupled from the Spring bean factory.
 *
 * <p>Extends {@link org.springframework.core.Ordered} to express the order value of the underlying aspect in a chain.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.beans.factory.BeanFactory#getBean
 */
public interface AspectInstanceFactory extends Ordered {

	/**
	 * Create an instance of the aspect for this factory.
	 * @return the aspect instance (never {@code null})
	 */
	Object getAspectInstance();

	/**
	 * Expose the aspect class loader used by this factory.
	 * @return the aspect class loader ({@code null} for the bootstrap loader)
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 */
	@Nullable
	ClassLoader getAspectClassLoader();

}
```

### 5. Main Implementations

1. **SimpleAspectInstanceFactory**

   + A simple aspect instance factory that creates annotation-based aspect instances.

2. **SingletonAspectInstanceFactory**
   + A singleton aspect instance factory that creates singleton aspect instances.

3. **SimpleBeanFactoryAwareAspectInstanceFactory**

+ A simple bean-factory-aware aspect instance factory. It takes the context of the bean factory into account when creating aspect instances.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AspectInstanceFactory {
<<Interface>>

}
class SimpleAspectInstanceFactory
class SimpleBeanFactoryAwareAspectInstanceFactory
class SingletonAspectInstanceFactory

SimpleAspectInstanceFactory  ..>  AspectInstanceFactory 
SimpleBeanFactoryAwareAspectInstanceFactory  ..>  AspectInstanceFactory 
SingletonAspectInstanceFactory  ..>  AspectInstanceFactory 

~~~

### 7. Best Practices

This example uses different `AspectInstanceFactory` implementation classes to create and manage aspect instances. First, it creates aspects with `SimpleAspectInstanceFactory` and `SingletonAspectInstanceFactory` to get a simple instance and a singleton instance. Then it registers a singleton bean named "myAspect" and uses it to configure `SimpleBeanFactoryAwareAspectInstanceFactory`, which creates an aspect instance that depends on the bean factory. Finally, it gets the `SimpleBeanFactoryAwareAspectInstanceFactory` instance, gets its aspect object, and prints the result.

```java
public class AspectInstanceFactoryDemo {

    public static void main(String[] args) {
        // Create an aspect instance with SimpleAspectInstanceFactory
        SimpleAspectInstanceFactory sAif = new SimpleAspectInstanceFactory(MyAspect.class);
        System.out.println("SimpleAspectInstanceFactory (1): " + sAif.getAspectInstance());
        System.out.println("SimpleAspectInstanceFactory (2): " + sAif.getAspectInstance());

        // Create a singleton aspect instance with SingletonAspectInstanceFactory
        SingletonAspectInstanceFactory singletonAif = new SingletonAspectInstanceFactory(new MyAspect());
        System.out.println("SingletonAspectInstanceFactory (1): " + singletonAif.getAspectInstance());
        System.out.println("SingletonAspectInstanceFactory (2): " + singletonAif.getAspectInstance());

        // Create a DefaultListableBeanFactory instance to register and manage beans
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // Register a singleton bean named "myAspect" of type MyAspect
        beanFactory.registerSingleton("myAspect", new MyAspect());
        // Create a BeanDefinition for the aspect factory
        RootBeanDefinition aspectFactoryDef = new RootBeanDefinition(SimpleBeanFactoryAwareAspectInstanceFactory.class);
        // Set the property aspectBeanName of the aspect factory to "myAspect"
        aspectFactoryDef.getPropertyValues().add("aspectBeanName", "myAspect");
        // Mark the aspect factory as synthetic, so it is not exposed
        aspectFactoryDef.setSynthetic(true);
        // Register a bean named "simpleBeanFactoryAwareAspectInstanceFactory" with the BeanDefinition of the aspect factory
        beanFactory.registerBeanDefinition("simpleBeanFactoryAwareAspectInstanceFactory", aspectFactoryDef);
        // Get the SimpleBeanFactoryAwareAspectInstanceFactory instance from the BeanFactory
        SimpleBeanFactoryAwareAspectInstanceFactory simpleBeanFactoryAwareAif = beanFactory.getBean(SimpleBeanFactoryAwareAspectInstanceFactory.class);
        System.out.println("SimpleBeanFactoryAwareAspectInstanceFactory (1): " + simpleBeanFactoryAwareAif.getAspectInstance());
        System.out.println("SimpleBeanFactoryAwareAspectInstanceFactory (2): " + simpleBeanFactoryAwareAif.getAspectInstance());
    }
}
```

This is the result of creating aspect objects with the different aspect instance factories. `SimpleAspectInstanceFactory` creates a new aspect object on every call of `getAspectInstance()`, so the instances are different. `SingletonAspectInstanceFactory` returns a singleton object, so repeated calls of `getAspectInstance()` return the same instance. `SimpleBeanFactoryAwareAspectInstanceFactory` gets the bean with the given name from the `BeanFactory`. That bean is a singleton by default, so it also returns the same instance.

```java
SimpleAspectInstanceFactory (1): com.xcs.spring.MyAspect@6d8a00e3
SimpleAspectInstanceFactory (2): com.xcs.spring.MyAspect@548b7f67
SingletonAspectInstanceFactory (1): com.xcs.spring.MyAspect@5f375618
SingletonAspectInstanceFactory (2): com.xcs.spring.MyAspect@5f375618
SimpleBeanFactoryAwareAspectInstanceFactory (1): com.xcs.spring.MyAspect@41ee392b
SimpleBeanFactoryAwareAspectInstanceFactory (2): com.xcs.spring.MyAspect@41ee392b
```

### 8. Source Code Analysis

**SimpleAspectInstanceFactory**

 The `SimpleAspectInstanceFactory` class is an implementation of the `AspectInstanceFactory` interface. Every call of `getAspectInstance()` creates a new instance of the given aspect class. It creates the aspect class instance by reflection at runtime. It also has methods to get the aspect class, get the class loader of the aspect class, and decide the order of the aspect instance.

```java
/**
 * Implementation of {@link AspectInstanceFactory} that creates a new instance of the given aspect class on every call of {@link #getAspectInstance()}.
 * Aspect factory that creates new instances.
 *
 * @author Juergen Hoeller
 * @since 2.0.4
 */
public class SimpleAspectInstanceFactory implements AspectInstanceFactory {

    // Aspect class
    private final Class<?> aspectClass;

    /**
     * Create a new SimpleAspectInstanceFactory for the given aspect class.
     * @param aspectClass the aspect class
     */
    public SimpleAspectInstanceFactory(Class<?> aspectClass) {
        Assert.notNull(aspectClass, "Aspect class must not be null");
        this.aspectClass = aspectClass;
    }

    /**
     * Return the specified aspect class (never {@code null}).
     */
    public final Class<?> getAspectClass() {
        return this.aspectClass;
    }

    @Override
    public final Object getAspectInstance() {
        try {
            // Get the accessible constructor of the aspect class by reflection and create a new instance
            return ReflectionUtils.accessibleConstructor(this.aspectClass).newInstance();
        } catch (NoSuchMethodException ex) {
            throw new AopConfigException("No default constructor on aspect class: " + this.aspectClass.getName(), ex);
        } catch (InstantiationException ex) {
            throw new AopConfigException("Unable to instantiate aspect class: " + this.aspectClass.getName(), ex);
        } catch (IllegalAccessException ex) {
            throw new AopConfigException("Could not access aspect constructor: " + this.aspectClass.getName(), ex);
        } catch (InvocationTargetException ex) {
            throw new AopConfigException("Failed to invoke aspect constructor: " + this.aspectClass.getName(), ex.getTargetException());
        }
    }

    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        // Return the class loader of the aspect class
        return this.aspectClass.getClassLoader();
    }

    /**
     * Determine the order of the aspect instance of this factory,
     * which can express an instance-specific order by implementing {@link org.springframework.core.Ordered},
     * or use a default order.
     * @see org.springframework.core.Ordered
     * @see #getOrderForAspectClass
     */
    @Override
    public int getOrder() {
        return getOrderForAspectClass(this.aspectClass);
    }

    /**
     * Determine the fallback order for when the aspect instance does not express an instance-specific order by implementing {@link org.springframework.core.Ordered}.
     * <p>The default implementation simply returns {@code Ordered.LOWEST_PRECEDENCE}.
     * @param aspectClass the aspect class
     */
    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
```

**SingletonAspectInstanceFactory**

 The `SingletonAspectInstanceFactory` class is an implementation of the `AspectInstanceFactory` interface. It is backed by a given singleton object and returns the same instance on every call of `getAspectInstance()`. It also has methods to get the class loader of the aspect instance and decide the order of the aspect instance. It supports aspect instances that implement the `Ordered` interface.

```java
/**
 * Implementation of {@link AspectInstanceFactory} backed by a given singleton object,
 * which returns the same instance on every call of {@link #getAspectInstance()}.
 * Singleton aspect instance factory.
 *
 * Backed by a given singleton object, it returns the same instance on every call of getAspectInstance().
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see SimpleAspectInstanceFactory
 */
@SuppressWarnings("serial")
public class SingletonAspectInstanceFactory implements AspectInstanceFactory, Serializable {

    // Singleton aspect instance
    private final Object aspectInstance;

    /**
     * Create a new SingletonAspectInstanceFactory for the given aspect instance.
     * @param aspectInstance the singleton aspect instance
     */
    public SingletonAspectInstanceFactory(Object aspectInstance) {
        Assert.notNull(aspectInstance, "Aspect instance must not be null");
        this.aspectInstance = aspectInstance;
    }

    @Override
    public final Object getAspectInstance() {
        // Return the singleton aspect instance
        return this.aspectInstance;
    }

    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        // Return the class loader of the aspect instance
        return this.aspectInstance.getClass().getClassLoader();
    }

    /**
     * Determine the order of the aspect instance of this factory,
     * which can express an instance-specific order by implementing {@link org.springframework.core.Ordered},
     * or use a default order.
     * @see org.springframework.core.Ordered
     * @see #getOrderForAspectClass
     */
    @Override
    public int getOrder() {
        if (this.aspectInstance instanceof Ordered) {
            // If the aspect instance implements the Ordered interface, return its order
            return ((Ordered) this.aspectInstance).getOrder();
        }
        // Otherwise return the default order of the aspect instance class
        return getOrderForAspectClass(this.aspectInstance.getClass());
    }

    /**
     * Determine the fallback order for when the aspect instance does not express an instance-specific order by implementing {@link org.springframework.core.Ordered}.
     * <p>The default implementation simply returns {@code Ordered.LOWEST_PRECEDENCE}.
     * @param aspectClass the aspect class
     */
    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
```

**SimpleBeanFactoryAwareAspectInstanceFactory**

The `SimpleBeanFactoryAwareAspectInstanceFactory` class is an implementation of the `AspectInstanceFactory` interface. It finds the aspect instance in the `BeanFactory` by the configured bean name. Every call of `getAspectInstance()` looks up and returns the bean with that name. It also has methods to get the class loader of the aspect instance and decide the order of the aspect instance. It supports aspect instances that implement the `Ordered` interface.

```java
/**
 * Implementation of {@link AspectInstanceFactory} that locates the aspect in a {@link org.springframework.beans.factory.BeanFactory} by the configured bean name.
 * The SimpleBeanFactoryAwareAspectInstanceFactory class.
 * 
 * Locates the aspect in the BeanFactory by the configured bean name.
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public class SimpleBeanFactoryAwareAspectInstanceFactory implements AspectInstanceFactory, BeanFactoryAware {

    // Aspect bean name
    @Nullable
    private String aspectBeanName;

    // BeanFactory
    @Nullable
    private BeanFactory beanFactory;

    /**
     * Set the name of the aspect bean. This bean is returned when {@link #getAspectInstance()} is called.
     * @param aspectBeanName the aspect bean name
     */
    public void setAspectBeanName(String aspectBeanName) {
        this.aspectBeanName = aspectBeanName;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        Assert.notNull(this.aspectBeanName, "'aspectBeanName' is required");
    }

    /**
     * Look up the aspect bean in the BeanFactory and return it.
     * @see #setAspectBeanName
     */
    @Override
    public Object getAspectInstance() {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        Assert.state(this.aspectBeanName != null, "No 'aspectBeanName' set");
        return this.beanFactory.getBean(this.aspectBeanName);
    }

    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory) this.beanFactory).getBeanClassLoader();
        } else {
            return ClassUtils.getDefaultClassLoader();
        }
    }

    @Override
    public int getOrder() {
        if (this.beanFactory != null && this.aspectBeanName != null &&
                this.beanFactory.isSingleton(this.aspectBeanName) &&
                this.beanFactory.isTypeMatch(this.aspectBeanName, Ordered.class)) {
            return ((Ordered) this.beanFactory.getBean(this.aspectBeanName)).getOrder();
        }
        return Ordered.LOWEST_PRECEDENCE;
    }
}
```
