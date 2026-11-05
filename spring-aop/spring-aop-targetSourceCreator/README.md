## TargetSourceCreator

- [TargetSourceCreator](#targetsourcecreator)
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

The `TargetSourceCreator` interface is mainly used to create the target source for the proxy of a target object. By implementing this interface, you can write your own logic for creating proxy objects, for example return different proxy objects under different conditions. This is very useful in AOP (aspect-oriented programming) and the proxy pattern, and gives flexible control over how proxy objects are created.

### 3. Main Features

1. **Create the target source (TargetSource) of a proxy object**

   + This interface lets us define the logic for creating proxy objects, including when to create a proxy object and how to create its target source.

2. **Customize how proxy objects are created**

   + By implementing this interface, you can customize how proxy objects are created. For example, return different target sources under different conditions, or run specific logic before or after the proxy object is created.

3. **Support flexible AOP configuration**

   + In the Spring Framework, AOP (aspect-oriented programming) often uses proxy objects to handle cross-cutting concerns. The `TargetSourceCreator` interface lets us control how proxy objects are created, which makes AOP more customizable and flexible.

### 4. Interface Source

The `TargetSourceCreator` interface lets an implementation create a special target source (TargetSource), such as a pooling target source, for specific beans. An implementation can choose the type of target source from the properties of the target class, such as pooling properties. `AbstractAutoProxyCreator` can support several `TargetSourceCreators`, which are applied in order. This makes the creation of proxy objects in the Spring Framework flexible and customizable.

```java
/**
 * Implementations can create special target sources, such as pooling target sources, for specific beans. For example, they can choose the target source based on attributes of the target class (such as pooling attributes).
 *
 * <p>AbstractAutoProxyCreator can support several TargetSourceCreators, which are applied in order.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
@FunctionalInterface
public interface TargetSourceCreator {

    /**
     * Create a special target source for the given bean, if any.
     * @param beanClass the class of the bean
     * @param beanName the name of the bean
     * @return a special target source, or {@code null} if this TargetSourceCreator is not interested in the specific bean
     */
    @Nullable
    TargetSource getTargetSource(Class<?> beanClass, String beanName);

}
```

### 5. Main Implementations

1. **QuickTargetSourceCreator**

   + Used to create the target source quickly. It fits cases that do not need lazy loading. With specific conditions or strategies, it can create the target source quickly, to improve performance or meet other needs.

2. **LazyInitTargetSourceCreator**

   + Used to create the target source lazily. It fits cases that need lazy loading, to reduce startup time or resource use. With specific conditions or strategies, it creates the target source later, and loads it only when it is requested.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AbstractBeanFactoryBasedTargetSourceCreator
class LazyInitTargetSourceCreator
class QuickTargetSourceCreator
class TargetSourceCreator {
<<Interface>>

}

AbstractBeanFactoryBasedTargetSourceCreator  ..>  TargetSourceCreator 
LazyInitTargetSourceCreator  -->  AbstractBeanFactoryBasedTargetSourceCreator 
QuickTargetSourceCreator  -->  AbstractBeanFactoryBasedTargetSourceCreator 
~~~

### 7. Best Practices

This example uses the annotation configuration of the Spring Framework to create the application context, and gets the `MyConnection` bean from the context. Then it prints the class name of the `MyConnection` instance, and calls the `getName()` method of the `MyConnection` instance in a loop to get the name of the instance and print it.

```java
public class TargetSourceCreatorDemo {
    public static void main(String[] args) {
        // Create an annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // Get the MyConnection bean from the context
        MyConnection myConnection = context.getBean(MyConnection.class);
        // Print the class name of the MyConnection instance
        System.out.println("MyConnection Class = " + myConnection.getClass());
        // Call the getName() method of the MyConnection instance in a loop
        for (int i = 0; i < 10; i++) {
            // Print the name of the MyConnection instance
            System.out.println("MyConnection Name = " + myConnection.getName());
        }
    }
}
```

The `@EnableAspectJAutoProxy` annotation turns on AspectJ auto proxy support, which lets the Spring Framework create and manage aspects (Aspects). The `@Configuration` annotation marks this as a configuration class, and `@ComponentScan("com.xcs.spring")` sets the package to scan, so Spring can autowire beans and find components.

```java
@EnableAspectJAutoProxy
@Configuration
@ComponentScan("com.xcs.spring")
public class AppConfig {

}
```

The `SetMyTargetSourceCreator` class implements the Spring `BeanPostProcessor` interface and the `PriorityOrdered` interface. In the `postProcessAfterInitialization` method, it checks whether the bean is an instance of `AbstractAutoProxyCreator`, and then sets the custom target source creator `MyTargetSourceCreator` on it. By implementing `PriorityOrdered` and overriding `getOrder`, it makes sure this post processor has the highest priority and runs before other post processors.

```java
@Component
public class SetMyTargetSourceCreator implements BeanPostProcessor , PriorityOrdered {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractAutoProxyCreator) {
            ((AbstractAutoProxyCreator) bean).setCustomTargetSourceCreators(new MyTargetSourceCreator());
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
```

The `MyTargetSourceCreator` class implements the `TargetSourceCreator` interface. In the `getTargetSource` method, it uses the given bean class and bean name to decide whether to create a target source for the specific bean. If the given bean class is the `MyConnection` class or a subclass, it returns a target source with connection pool support, `ConnectionPoolTargetSource`, and sets the pool size to 3.

```java
public class MyTargetSourceCreator implements TargetSourceCreator {
    @Override
    public TargetSource getTargetSource(Class<?> beanClass, String beanName) {
        if (MyConnection.class.isAssignableFrom(beanClass)) {
            return new ConnectionPoolTargetSource(3);
        }
        return null;
    }
}
```

The `ConnectionPoolTargetSource` class implements the Spring `TargetSource` interface. It manages a connection pool of custom connection objects. In the constructor, it creates a blocking queue of fixed size as the connection pool and fills it with connection objects. With the `getTarget()` method, it gets a connection object from the pool, and with the `releaseTarget()` method, it releases the connection object.

```java
/**
 * Connection pool target source that manages a connection pool of custom connection objects.
 *
 * @author xcs
 * @date 2024-04-09 15:26
 */
public class ConnectionPoolTargetSource implements TargetSource {

    /**
     * Connection pool
     */
    private final BlockingQueue<MyConnection> connectionPool;

    /**
     * Constructor that initializes the connection pool.
     *
     * @param poolSize the pool size
     */
    public ConnectionPoolTargetSource(int poolSize) {
        this.connectionPool = new ArrayBlockingQueue<>(poolSize);
        initializeConnectionPool(poolSize);
    }

    /**
     * Initializes the connection pool and fills it with connection objects.
     *
     * @param poolSize the pool size
     */
    private void initializeConnectionPool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            MyConnection connection = new MyConnection("Connection" + i);
            connectionPool.offer(connection);
        }
    }

    /**
     * Gets the type of the target class.
     *
     * @return the type of the target class
     */
    @Override
    public Class<?> getTargetClass() {
        return MyConnection.class;
    }

    /**
     * Checks whether the target object is static.
     *
     * @return true if the target object is static, otherwise false
     */
    @Override
    public boolean isStatic() {
        return false;
    }

    /**
     * Gets a connection object.
     *
     * @return the connection object
     * @throws Exception if an exception occurs while getting the connection object
     */
    @Override
    public Object getTarget() throws Exception {
        return connectionPool.take();
    }

    /**
     * Releases a connection object.
     *
     * @param target the connection object to release
     * @throws Exception if an exception occurs while releasing the connection object
     */
    @Override
    public void releaseTarget(Object target) throws Exception {
        if (target instanceof MyConnection) {
            connectionPool.offer((MyConnection) target);
        }
    }
}
```

`MyConnection` class stands for a custom connection object.

```java
public class MyConnection {

    private String name;

    public MyConnection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyConnection{" +
                "name='" + name + '\'' +
                '}';
    }
}
```

In the run result, the `MyTargetSourceCreator` class creates a connection pool target source for the `MyConnection` class. The Spring Framework enhances the `MyConnection` class with a CGLIB dynamic proxy, so it can use the connection pool. Each call of the `getName()` method gets a connection from the pool and returns the strings "Connection0", "Connection1", and "Connection2" in turn. This shows that the pool size is 3 and the connection names are used in a cycle.

```java
MyConnection Class = class com.xcs.spring.MyConnection$$EnhancerBySpringCGLIB$$fb2fa879
MyConnection Name = Connection0
MyConnection Name = Connection1
MyConnection Name = Connection2
MyConnection Name = Connection0
MyConnection Name = Connection1
MyConnection Name = Connection2
MyConnection Name = Connection0
MyConnection Name = Connection1
MyConnection Name = Connection2
MyConnection Name = Connection0
```

### 8. Source Code Analysis

The `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessBeforeInstantiation` method works before the bean is instantiated. First, it checks whether the cache has information about the target bean. If so, it returns null at once. Otherwise it continues. Then it checks whether the bean is an infrastructure class or should be skipped. If so, it marks the bean as not needing advice and returns null. Finally, if there is a custom target source (TargetSource), it creates the proxy object and lets the custom target source handle the target instance. This avoids the unneeded default instantiation.

```java
@Override
public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
    // Get the cache key
    Object cacheKey = getCacheKey(beanClass, beanName);

    // If the bean name is empty or not in the target source bean list, and the cache has this key, return null
    if (!StringUtils.hasLength(beanName) || !this.targetSourcedBeans.contains(beanName)) {
        if (this.advisedBeans.containsKey(cacheKey)) {
            return null;
        }
        // If the bean class is an infrastructure class or should be skipped, mark it as not needing advice and return null
        if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
            this.advisedBeans.put(cacheKey, Boolean.FALSE);
            return null;
        }
    }

    // If there is a custom target source, create the proxy here:
    // Avoid unneeded default instantiation of the target bean:
    // the target source will handle the target instance in a custom way.
    TargetSource targetSource = getCustomTargetSource(beanClass, beanName);
    if (targetSource != null) {
        // If the bean name is not empty, add it to the target source bean list
        if (StringUtils.hasLength(beanName)) {
            this.targetSourcedBeans.add(beanName);
        }
        // Get the specific interceptors and advisors for the bean
        Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
        // Create the proxy object
        Object proxy = createProxy(beanClass, beanName, specificInterceptors, targetSource);
        // Link the class of the proxy object with the cache key
        this.proxyTypes.put(cacheKey, proxy.getClass());
        return proxy;
    }

    return null;
}
```

The `org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#getCustomTargetSource` method creates the target source (TargetSource) from the class and name of the bean. If custom TargetSourceCreators are set and the bean factory contains a bean with the given name, it tries to create the target source with these TargetSourceCreators. The method loops over all TargetSourceCreators and calls their getTargetSource method to get the target source. If a matching target source is found, it returns it. Otherwise it returns null.

```java
/**
 * Create the target source for a bean instance. If any TargetSourceCreators are set, they are used.
 * Return {@code null} if no custom TargetSource should be used.
 * <p>This implementation uses the "customTargetSourceCreators" property.
 * Subclasses can override this method to use a different mechanism.
 * @param beanClass the class of the bean
 * @param beanName the name of the bean
 * @return the target source to use for this bean
 * @see #setCustomTargetSourceCreators
 */
@Nullable
protected TargetSource getCustomTargetSource(Class<?> beanClass, String beanName) {
    // We cannot create a complex target source for a directly registered singleton bean.
    if (this.customTargetSourceCreators != null &&
        this.beanFactory != null && this.beanFactory.containsBean(beanName)) {
        // Loop over all TargetSourceCreators
        for (TargetSourceCreator tsc : this.customTargetSourceCreators) {
            // Get the target source through the TargetSourceCreator
            TargetSource ts = tsc.getTargetSource(beanClass, beanName);
            // If a matching target source is found, return it
            if (ts != null) {
                // A matching target source was found.
                if (logger.isTraceEnabled()) {
                    logger.trace("TargetSourceCreator [" + tsc +
                            "] found custom TargetSource for bean with name '" + beanName + "'");
                }
                return ts;
            }
        }
    }

    // No custom target source was found.
    return null;
}
```
