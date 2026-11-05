## MetadataAwareAspectInstanceFactory

- [MetadataAwareAspectInstanceFactory](#metadataawareaspectinstancefactory)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementations](#5-main-implementations)
  - [6. Class Diagram](#6-class-diagram)
  - [7. Best Practices](#7-best-practices)
  - [8. Source Code Analysis](#8-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `MetadataAwareAspectInstanceFactory` interface is a key interface in Spring AOP. It creates aspect instances and handles their metadata. It gives the Spring Framework support for AOP aspects written in the AspectJ annotation style.

### 3. Main Features

1. **Create aspect instances**

   + The `getAspectInstance()` method provides the aspect instance, so the advice of the aspect can be applied at runtime.
   
2. **Handle metadata**

   + The `getAspectMetadata()` method returns the metadata of the aspect class, such as the class name, the owning class, and the pointcut expression, so the aspect can be applied correctly at runtime.
   
3. **Support AspectJ annotation-style AOP**

   + With this interface, Spring AOP can create instances of AOP aspects written in the AspectJ annotation style and handle their metadata. So you can define aspects with AspectJ annotations in a Spring application.

### 4. Interface Source

The `MetadataAwareAspectInstanceFactory` interface is a sub-interface of `AspectInstanceFactory`. It returns the `AspectMetadata` linked to an AspectJ annotated class. `AspectMetadata` holds the metadata of the aspect. The interface also defines a method `getAspectCreationMutex()` that returns the best creation mutex for this factory. `AspectMetadata` uses the Java 5 specific `org.aspectj.lang.reflect.AjType`, so this method had to be split out into this sub-interface.

```java
/**
 * Sub-interface of {@link org.springframework.aop.aspectj.AspectInstanceFactory} that returns the {@link AspectMetadata} linked to an AspectJ annotated class.
 *
 * <p>Ideally, AspectInstanceFactory itself would include this method, but because AspectMetadata uses the Java 5 specific {@link org.aspectj.lang.reflect.AjType},
 * we need to split out this sub-interface.
 *
 * @author Rod Johnson
 * @since 2.0
 * @see AspectMetadata
 * @see org.aspectj.lang.reflect.AjType
 */
public interface MetadataAwareAspectInstanceFactory extends AspectInstanceFactory {

	/**
	 * Return the AspectJ AspectMetadata of the aspect of this factory.
	 * @return the aspect metadata
	 */
	AspectMetadata getAspectMetadata();

	/**
	 * Return the best creation mutex for this factory.
	 * @return the mutex object (may be {@code null} if no lock is needed)
	 * @since 4.3
	 */
	@Nullable
	Object getAspectCreationMutex();

}
```

### 5. Main Implementations

1. **SimpleMetadataAwareAspectInstanceFactory**

   - The simplest implementation. It creates aspect instances in singleton style. It just instantiates the aspect class and provides that instance as the aspect instance.

2. **SingletonMetadataAwareAspectInstanceFactory**

   - Like `SimpleMetadataAwareAspectInstanceFactory`, this implementation also creates singleton aspect instances. But it can work with the Spring container, so the aspect instance is managed as a singleton bean in the container.

3. **BeanFactoryAspectInstanceFactory**

   - This implementation works with the Spring BeanFactory. It creates aspect instances and can handle dependency injection into the aspect class. You can inject other Spring-managed beans into the aspect class, to build more complex business logic.

4. **PrototypeAspectInstanceFactory**

   - This implementation creates prototype aspect instances. Unlike a singleton, a prototype creates a new instance on every request. It fits cases where a new instance is needed each time.

5. **LazySingletonAspectInstanceFactoryDecorator**

   - This implementation is a decorator that lazily initializes a singleton aspect instance. It creates the instance only when the aspect instance is first requested, which improves performance and delays resource use.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AspectInstanceFactory {
<<Interface>>

}
class BeanFactoryAspectInstanceFactory
class LazySingletonAspectInstanceFactoryDecorator
class MetadataAwareAspectInstanceFactory {
<<Interface>>

}
class PrototypeAspectInstanceFactory
class SimpleAspectInstanceFactory
class SimpleMetadataAwareAspectInstanceFactory
class SingletonAspectInstanceFactory
class SingletonMetadataAwareAspectInstanceFactory

BeanFactoryAspectInstanceFactory  ..>  MetadataAwareAspectInstanceFactory 
LazySingletonAspectInstanceFactoryDecorator  ..>  MetadataAwareAspectInstanceFactory 
MetadataAwareAspectInstanceFactory  -->  AspectInstanceFactory 
PrototypeAspectInstanceFactory  -->  BeanFactoryAspectInstanceFactory 
SimpleAspectInstanceFactory  ..>  AspectInstanceFactory 
SimpleMetadataAwareAspectInstanceFactory  ..>  MetadataAwareAspectInstanceFactory 
SimpleMetadataAwareAspectInstanceFactory  -->  SimpleAspectInstanceFactory 
SingletonAspectInstanceFactory  ..>  AspectInstanceFactory 
SingletonMetadataAwareAspectInstanceFactory  ..>  MetadataAwareAspectInstanceFactory 
SingletonMetadataAwareAspectInstanceFactory  -->  SingletonAspectInstanceFactory 
~~~

### 7. Best Practices

This example uses different `MetadataAwareAspectInstanceFactory` implementations to create aspects and shows how they behave differently. First, `SimpleMetadataAwareAspectInstanceFactory` and `SingletonMetadataAwareAspectInstanceFactory` each create a singleton aspect instance. Then `BeanFactoryAspectInstanceFactory` registers and creates the aspect in a Spring bean factory. Finally, `LazySingletonAspectInstanceFactoryDecorator` lazily initializes the singleton aspect instance. At each step, the aspect instance and its metadata are printed.

```java
public class MetadataAwareAspectInstanceFactoryDemo {

    public static void main(String[] args) {
        // Create the aspect with SimpleMetadataAwareAspectInstanceFactory
        SimpleMetadataAwareAspectInstanceFactory simpleMetadataAwareAif = new SimpleMetadataAwareAspectInstanceFactory(MyAspect.class, "myAspect");
        System.out.println("SimpleMetadataAwareAspectInstanceFactory (1) = " + simpleMetadataAwareAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory (2) = " + simpleMetadataAwareAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory AspectMetadata = " + JSONUtil.toJsonStr(simpleMetadataAwareAif.getAspectMetadata()));
        System.out.println();

        // Create the aspect with SingletonMetadataAwareAspectInstanceFactory
        SingletonMetadataAwareAspectInstanceFactory singletonMetadataAwareAif = new SingletonMetadataAwareAspectInstanceFactory(new MyAspect(), "myAspect");
        System.out.println("SingletonMetadataAwareAspectInstanceFactory (1) = " + singletonMetadataAwareAif.getAspectInstance());
        System.out.println("SingletonMetadataAwareAspectInstanceFactory (2) = " + singletonMetadataAwareAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory AspectMetadata = " + JSONUtil.toJsonStr(singletonMetadataAwareAif.getAspectMetadata()));
        System.out.println();

        // Create the aspect with BeanFactoryAspectInstanceFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("myAspect", new MyAspect());
        BeanFactoryAspectInstanceFactory banFactoryAif = new BeanFactoryAspectInstanceFactory(beanFactory, "myAspect");
        System.out.println("BeanFactoryAspectInstanceFactory (1) = " + banFactoryAif.getAspectInstance());
        System.out.println("BeanFactoryAspectInstanceFactory (2) = " + banFactoryAif.getAspectInstance());
        System.out.println("SimpleMetadataAwareAspectInstanceFactory AspectMetadata = " + JSONUtil.toJsonStr(banFactoryAif.getAspectMetadata()));
        System.out.println();

        // Create the aspect with LazySingletonAspectInstanceFactoryDecorator
        LazySingletonAspectInstanceFactoryDecorator lazySingletonAifD = new LazySingletonAspectInstanceFactoryDecorator(banFactoryAif);
        System.out.println("LazySingletonAspectInstanceFactoryDecorator (1) = " + lazySingletonAifD.getAspectInstance());
        System.out.println("LazySingletonAspectInstanceFactoryDecorator (2) = " + lazySingletonAifD.getAspectInstance());
        System.out.println("LazySingletonAspectInstanceFactoryDecorator AspectCreationMutex = " + lazySingletonAifD.getAspectCreationMutex());
        System.out.println("LazySingletonAspectInstanceFactoryDecorator AspectMetadata = " + JSONUtil.toJsonStr(lazySingletonAifD.getAspectMetadata()));
        System.out.println();
    }
}
```

The run result shows how the different `MetadataAwareAspectInstanceFactory` implementations behave. `SimpleMetadataAwareAspectInstanceFactory` returns a different aspect instance each time, while `SingletonMetadataAwareAspectInstanceFactory` returns the same instance each time. This shows their non-singleton and singleton behavior. `BeanFactoryAspectInstanceFactory` registers and creates the aspect in a Spring bean factory, and behaves like the first two. `LazySingletonAspectInstanceFactoryDecorator` is a decorator of `BeanFactoryAspectInstanceFactory`. It lazily initializes the singleton aspect instance, but the final result is the same as `BeanFactoryAspectInstanceFactory`.

```java
SimpleMetadataAwareAspectInstanceFactory (1) = com.xcs.spring.MyAspect@5f341870
SimpleMetadataAwareAspectInstanceFactory (2) = com.xcs.spring.MyAspect@553f17c
SimpleMetadataAwareAspectInstanceFactory AspectMetadata = {"aspectName":"myAspect","aspectClass":"com.xcs.spring.MyAspect","perClausePointcut":{}}

SingletonMetadataAwareAspectInstanceFactory (1) = com.xcs.spring.MyAspect@1da51a35
SingletonMetadataAwareAspectInstanceFactory (2) = com.xcs.spring.MyAspect@1da51a35
SimpleMetadataAwareAspectInstanceFactory AspectMetadata = {"aspectName":"myAspect","aspectClass":"com.xcs.spring.MyAspect","perClausePointcut":{}}

BeanFactoryAspectInstanceFactory (1) = com.xcs.spring.MyAspect@6646153
BeanFactoryAspectInstanceFactory (2) = com.xcs.spring.MyAspect@6646153
SimpleMetadataAwareAspectInstanceFactory AspectMetadata = {"aspectName":"myAspect","aspectClass":"com.xcs.spring.MyAspect","perClausePointcut":{}}

LazySingletonAspectInstanceFactoryDecorator (1) = com.xcs.spring.MyAspect@6646153
LazySingletonAspectInstanceFactoryDecorator (2) = com.xcs.spring.MyAspect@6646153
LazySingletonAspectInstanceFactoryDecorator AspectCreationMutex = null
LazySingletonAspectInstanceFactoryDecorator AspectMetadata = {"aspectName":"myAspect","aspectClass":"com.xcs.spring.MyAspect","perClausePointcut":{}}
```

### 8. Source Code Analysis

**SimpleMetadataAwareAspectInstanceFactory**

`SimpleMetadataAwareAspectInstanceFactory` is a class that implements the `MetadataAwareAspectInstanceFactory` interface. It creates a new instance of the given aspect class each time the `getAspectInstance()` method is called. It manages the metadata of the aspect with an `AspectMetadata` object, and provides it through the `getAspectMetadata()` method. It also implements the `getAspectCreationMutex()` method to return the creation lock of the aspect instance, and the `getOrderForAspectClass()` method to decide the order of the aspect class.

```java
/**
 * A class that implements the {@link MetadataAwareAspectInstanceFactory} interface. Each call of the {@link #getAspectInstance()} method creates a new instance of the given aspect class.
 *
 * @author Juergen Hoeller
 * @since 2.0.4
 */
public class SimpleMetadataAwareAspectInstanceFactory extends SimpleAspectInstanceFactory
        implements MetadataAwareAspectInstanceFactory {

    private final AspectMetadata metadata; // The metadata of the aspect

    /**
     * Create a new SimpleMetadataAwareAspectInstanceFactory for the given aspect class.
     *
     * @param aspectClass the aspect class
     * @param aspectName  the aspect name
     */
    public SimpleMetadataAwareAspectInstanceFactory(Class<?> aspectClass, String aspectName) {
        super(aspectClass);
        this.metadata = new AspectMetadata(aspectClass, aspectName); // Create the metadata of the aspect
    }

    /**
     * Get the metadata of the aspect.
     *
     * @return the metadata of the aspect
     */
    @Override
    public final AspectMetadata getAspectMetadata() {
        return this.metadata;
    }

    /**
     * Get the creation lock of the aspect instance.
     *
     * @return the creation lock of the aspect instance
     */
    @Override
    public Object getAspectCreationMutex() {
        return this;
    }

    /**
     * Get the order of the aspect class.
     *
     * @param aspectClass the aspect class
     * @return the order of the aspect class
     */
    @Override
    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return OrderUtils.getOrder(aspectClass, Ordered.LOWEST_PRECEDENCE); // Get the order of the aspect class
    }

}
```

**SingletonMetadataAwareAspectInstanceFactory**

`SingletonMetadataAwareAspectInstanceFactory` is a class that implements the `MetadataAwareAspectInstanceFactory` interface. It supports creating aspect instances from a given singleton object, and returns the same instance each time the `getAspectInstance()` method is called. The class uses one singleton aspect instance and manages the metadata of the aspect with an `AspectMetadata` object. It also implements the `Serializable` interface to support serialization, and extends `SingletonAspectInstanceFactory`, which provides the methods and logic for getting the aspect instance.

```java
/**
 * A class that implements the {@link MetadataAwareAspectInstanceFactory} interface. It supports creating aspect instances from a given singleton object, and returns the same instance each time the {@link #getAspectInstance()} method is called.
 * 
 * The class manages the metadata of the aspect with an {@link AspectMetadata} object, and implements the {@link Serializable} interface to support serialization.
 * 
 * Authors: Rod Johnson, Juergen Hoeller
 * @since 2.0
 * @see SimpleMetadataAwareAspectInstanceFactory
 */
@SuppressWarnings("serial")
public class SingletonMetadataAwareAspectInstanceFactory extends SingletonAspectInstanceFactory
        implements MetadataAwareAspectInstanceFactory, Serializable {

    private final AspectMetadata metadata; // The metadata of the aspect

    /**
     * Create a new SingletonMetadataAwareAspectInstanceFactory for the given aspect.
     * 
     * @param aspectInstance the singleton instance of the aspect
     * @param aspectName the name of the aspect
     */
    public SingletonMetadataAwareAspectInstanceFactory(Object aspectInstance, String aspectName) {
        super(aspectInstance); // Call the superclass constructor with the singleton instance of the aspect
        this.metadata = new AspectMetadata(aspectInstance.getClass(), aspectName); // Create the metadata of the aspect
    }

    /**
     * Get the metadata of the aspect.
     * 
     * @return the metadata of the aspect
     */
    @Override
    public final AspectMetadata getAspectMetadata() {
        return this.metadata;
    }

    /**
     * Get the creation lock of the aspect instance.
     * 
     * @return the creation lock of the aspect instance
     */
    @Override
    public Object getAspectCreationMutex() {
        return this;
    }

    /**
     * Get the order of the aspect class.
     * 
     * @param aspectClass the aspect class
     * @return the order of the aspect class
     */
    @Override
    protected int getOrderForAspectClass(Class<?> aspectClass) {
        return OrderUtils.getOrder(aspectClass, Ordered.LOWEST_PRECEDENCE); // Get the order of the aspect class
    }

}
```

**BeanFactoryAspectInstanceFactory**

`BeanFactoryAspectInstanceFactory` is a class that implements the `MetadataAwareAspectInstanceFactory` interface. It supports creating aspect instances through the Spring `BeanFactory`. This factory gets the aspect instance from the `BeanFactory` by the given bean name, and can introspect the given type to create the AspectJ metadata. It handles both singleton and non-singleton cases, and can decide the order of the aspect. The order can be set with the `Ordered` interface or the `@Order` annotation.

```java
/**
 * Implementation of the {@link org.springframework.aop.aspectj.AspectInstanceFactory} interface,
 * backed by a Spring {@link org.springframework.beans.factory.BeanFactory}.
 *
 * <p>Note that this may instantiate the aspect multiple times if a prototype is used, which may not give the semantics you expect.
 * Use {@link LazySingletonAspectInstanceFactoryDecorator} to wrap this factory,
 * to make sure only one new aspect is returned.
 *
 * Authors: Rod Johnson, Juergen Hoeller
 * @since 2.0
 * @see org.springframework.beans.factory.BeanFactory
 * @see LazySingletonAspectInstanceFactoryDecorator
 */
@SuppressWarnings("serial")
public class BeanFactoryAspectInstanceFactory implements MetadataAwareAspectInstanceFactory, Serializable {

    private final BeanFactory beanFactory; // The bean factory
    private final String name; // The bean name
    private final AspectMetadata aspectMetadata; // The metadata of the aspect

    /**
     * Create a BeanFactoryAspectInstanceFactory. AspectJ will be called to introspect,
     * to create AJType metadata using the type returned from the BeanFactory for the given bean name.
     *
     * @param beanFactory the BeanFactory to get instances from
     * @param name the name of the bean
     */
    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name) {
        this(beanFactory, name, null);
    }

    /**
     * Create a BeanFactoryAspectInstanceFactory with a given type that AspectJ should introspect to create AJType metadata.
     * Use this if the BeanFactory may consider the type to be a subclass (for example with CGLIB) and the information should relate to the superclass.
     *
     * @param beanFactory the BeanFactory to get instances from
     * @param name the name of the bean
     * @param type the type AspectJ should introspect ({@code null} means resolve the type from the bean name through {@link BeanFactory#getType})
     */
    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name, @Nullable Class<?> type) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        Assert.notNull(name, "Bean name must not be null");
        this.beanFactory = beanFactory;
        this.name = name;
        Class<?> resolvedType = type;
        if (type == null) {
            resolvedType = beanFactory.getType(name);
            Assert.notNull(resolvedType, "Unresolvable bean type - explicitly specify the aspect class");
        }
        this.aspectMetadata = new AspectMetadata(resolvedType, name); // Create the metadata of the aspect
    }

    /**
     * Get the aspect instance.
     *
     * @return the aspect instance
     */
    @Override
    public Object getAspectInstance() {
        return this.beanFactory.getBean(this.name);
    }

    /**
     * Get the class loader of the aspect.
     *
     * @return the class loader of the aspect
     */
    @Override
    @Nullable
    public ClassLoader getAspectClassLoader() {
        return (this.beanFactory instanceof ConfigurableBeanFactory ?
                ((ConfigurableBeanFactory) this.beanFactory).getBeanClassLoader() :
                ClassUtils.getDefaultClassLoader());
    }

    /**
     * Get the metadata of the aspect.
     *
     * @return the metadata of the aspect
     */
    @Override
    public AspectMetadata getAspectMetadata() {
        return this.aspectMetadata;
    }

    /**
     * Get the creation lock of the aspect instance.
     *
     * @return the creation lock of the aspect instance
     */
    @Override
    @Nullable
    public Object getAspectCreationMutex() {
        if (this.beanFactory.isSingleton(this.name)) {
            // Rely on the singleton semantics provided by the factory -> no local lock.
            return null;
        } else if (this.beanFactory instanceof ConfigurableBeanFactory) {
            // No singleton guarantee from the factory -> let's lock locally, but reuse the factory's singleton lock, in case a lazy dependency of our advice bean
            // accidentally triggers the singleton lock implicitly...
            return ((ConfigurableBeanFactory) this.beanFactory).getSingletonMutex();
        } else {
            return this;
        }
    }

    /**
     * Determine the order of the target aspect of this factory. It can be expressed with an instance-specific order by implementing the {@link org.springframework.core.Ordered} interface
     * (checked only for singleton beans), or with the {@link org.springframework.core.annotation.Order} annotation at class level.
     *
     * @see org.springframework.core.Ordered
     * @see org.springframework.core.annotation.Order
     */
    @Override
    public int getOrder() {
        Class<?> type = this.beanFactory.getType(this.name);
        if (type != null) {
            if (Ordered.class.isAssignableFrom(type) && this.beanFactory.isSingleton(this.name)) {
                return ((Ordered) this.beanFactory.getBean(this.name)).getOrder();
            }
            return OrderUtils.getOrder(type, Ordered.LOWEST_PRECEDENCE);
        }
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": bean name '" + this.name + "'";
    }
}
```

**LazySingletonAspectInstanceFactoryDecorator**

The `LazySingletonAspectInstanceFactoryDecorator` class is a decorator that makes sure a `MetadataAwareAspectInstanceFactory` is instantiated only once. It wraps another `MetadataAwareAspectInstanceFactory` instance and creates the instance when the `getAspectInstance()` method is called for the first time. In later calls, it returns the instance that already exists and does not create it again.

```java
/**
 * Decorator that makes a {@link MetadataAwareAspectInstanceFactory} instantiate only once.
 * 
 * Authors: Rod Johnson, Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class LazySingletonAspectInstanceFactoryDecorator implements MetadataAwareAspectInstanceFactory, Serializable {

	private final MetadataAwareAspectInstanceFactory maaif; // The MetadataAwareAspectInstanceFactory instance to decorate

	@Nullable
	private volatile Object materialized; // The instantiated object

	/**
	 * Create a decorator that lazily initializes the given AspectInstanceFactory.
	 * @param maaif the MetadataAwareAspectInstanceFactory to decorate
	 */
	public LazySingletonAspectInstanceFactoryDecorator(MetadataAwareAspectInstanceFactory maaif) {
		Assert.notNull(maaif, "AspectInstanceFactory must not be null");
		this.maaif = maaif;
	}

	/**
     * Get the aspect instance.
     * If an instance already exists, return it directly;
     * otherwise, use the instantiation mutex (if there is one) to make sure it is instantiated only once in a multi-threaded environment.
     * If there is no mutex, instantiate the aspect object directly, assign it to the materialized variable, and return the instance.
     * If there is a mutex, use the lock to protect the instantiation, so only one thread can do the instantiation in a multi-threaded environment.
     * 
     * @return the aspect instance
     */
    @Override
    public Object getAspectInstance() {
        // Try to get the instance that already exists
        Object aspectInstance = this.materialized; 
        // If no instance exists yet
        if (aspectInstance == null) { 
            // Get the instantiation mutex
            Object mutex = this.maaif.getAspectCreationMutex(); 
            // If there is no mutex
            if (mutex == null) { 
                // Instantiate the aspect object directly
                aspectInstance = this.maaif.getAspectInstance(); 
                // Assign the instantiated object to the materialized variable
                this.materialized = aspectInstance; 
            } else { 
                // Use the mutex to protect the instantiation
                synchronized (mutex) {
                    // Try to get the instance that already exists again
                    aspectInstance = this.materialized; 
                    // Double check, to make sure it is instantiated only once inside the lock
                    if (aspectInstance == null) { 
                        // Instantiate the aspect object
                        aspectInstance = this.maaif.getAspectInstance(); 
                        // Assign the instantiated object to the materialized variable
                        this.materialized = aspectInstance; 
                    }
                }
            }
        }
        return aspectInstance; // Return the aspect instance
    }

	/**
	 * Return whether it has already been instantiated.
	 */
	public boolean isMaterialized() {
		return (this.materialized != null);
	}

	@Override
	@Nullable
	public ClassLoader getAspectClassLoader() {
		return this.maaif.getAspectClassLoader();
	}

	@Override
	public AspectMetadata getAspectMetadata() {
		return this.maaif.getAspectMetadata();
	}

	@Override
	@Nullable
	public Object getAspectCreationMutex() {
		return this.maaif.getAspectCreationMutex();
	}

	@Override
	public int getOrder() {
		return this.maaif.getOrder();
	}

	@Override
	public String toString() {
		return "LazySingletonAspectInstanceFactoryDecorator: decorating " + this.maaif;
	}

}
```
