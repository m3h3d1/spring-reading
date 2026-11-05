## TargetSource

- [TargetSource](#targetsource)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [5. Main Implementations](#5-main-implementations)
    - [6. Class Diagram](#6-class-diagram)
    - [7. Best Practices](#7-best-practices)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `TargetSource` interface is a key part of the Spring AOP framework. It defines the strategy for getting the target object, so you can manage the creation and handling of target objects in a flexible way. By implementing this interface, you can get target objects in many ways, such as singleton, prototype, and pooling. This makes AOP aspects more customizable and flexible.

### 3. Main Features

1. **Method to get the target object**

   + `getTarget()` gets the target object by a specific strategy, such as singleton, prototype, or pooling.

2. **Method to release the target object**

   + `releaseTarget(Object target)`: some implementations may need to release the target object, for example by returning it to an object pool.

3. **Flexible target object management**

   + You can write your own strategy for getting target objects to fit the needs of the application. This gives flexible management of target objects.

4. **Built-in target source implementations**

   + Spring AOP provides several built-in `TargetSource` implementations, such as `SingletonTargetSource`, `PrototypeTargetSource`, and `ThreadLocalTargetSource`. You can choose the one that fits your case.

5. **Extensibility and customization**

   + We can implement the `TargetSource` interface to write our own target source for special cases, such as thread-based object management and object pool management.

### 4. Interface Source

The `TargetSource` interface gets the target object of the current AOP call. The `getTarget()` method gets the target object, and the `releaseTarget(Object target)` method releases it. The interface defines the `getTargetClass()` method to return the type of the target object, and the `isStatic()` method to check whether all calls of `getTarget()` return the same object. The interface supports static and dynamic target sources. A static target source always returns the same target object. A dynamic target source can support pooling, hot swapping, and more.

```java
/**
 * The TargetSource interface is used to get the current target object of an AOP call. If no around advice chooses to end the interceptor chain, the target object is called by reflection.
 *
 * <p>If a TargetSource is "static", it always returns the same target object, which allows the AOP framework to optimize. Dynamic target sources can support pooling, hot swapping, and more.
 *
 * <p>Application developers usually do not need to use TargetSources directly: this is an AOP framework interface.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface TargetSource extends TargetClassAware {

    /**
     * Return the type of target returned by this TargetSource.
     * <p>Can return {@code null}, although certain uses of a TargetSource might just work with a predetermined target class.
     *
     * @return the type of target returned by this TargetSource
     */
    @Override
    @Nullable
    Class<?> getTargetClass();

    /**
     * Will all calls to getTarget() return the same object?
     * <p>In that case, there is no need to call releaseTarget(Object), and the AOP framework can cache the return value of getTarget().
     *
     * @return true if the target is immutable
     * @see #getTarget
     */
    boolean isStatic();

    /**
     * Return a target instance. Called immediately before the AOP framework calls the target of an AOP method invocation.
     *
     * @return the target object that contains the join point, or {@code null} if there is no actual target instance
     * @throws Exception if the target object cannot be resolved
     */
    @Nullable
    Object getTarget() throws Exception;

    /**
     * Release the given target object obtained from the getTarget() method, if any.
     *
     * @param target the object obtained from a call to getTarget()
     * @throws Exception if the object cannot be released
     */
    void releaseTarget(Object target) throws Exception;

}	
```

### 5. Main Implementations

1. **SingletonTargetSource**

   + A target source that manages a singleton object. This implementation returns the same singleton object on every `getTarget()` call. It fits cases where the target object is a singleton.

2. **PrototypeTargetSource**

   + A target source that creates a new object on every call. This implementation returns a new target object instance on every `getTarget()` call. It fits cases where the target object must be refreshed or reset often.

3. **ThreadLocalTargetSource**

   + A target source that keeps one target object per thread. This implementation keeps a copy of the target object in each thread. It fits cases where different target object instances are needed in a multi-threaded environment.

4. **CommonsPool2TargetSource**

   + A pooling target source that uses Apache Commons Pool to manage target objects. This implementation manages the creation and destruction of target objects through an object pool, to improve reuse and performance.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class CommonsPool2TargetSource
class PrototypeTargetSource
class SingletonTargetSource
class TargetClassAware {
<<Interface>>

}
class TargetSource {
<<Interface>>

}
class ThreadLocalTargetSource

CommonsPool2TargetSource  ..>  TargetSource 
PrototypeTargetSource  ..>  TargetSource 
SingletonTargetSource  ..>  TargetSource 
TargetSource  -->  TargetClassAware 
ThreadLocalTargetSource  ..>  TargetSource 
~~~

### 7. Best Practices

This example uses the Spring proxy factory (`ProxyFactory`) and a target source (`TargetSource`) to create a proxy object. We create a connection pool target source (`ConnectionPoolTargetSource`) and set the pool size to 3. Then we set this connection pool target source as the target source of the proxy factory, and get the proxy object from the proxy factory. Finally, we call a method on the proxy object 10 times.

```java
public class TargetSourceDemo {

    public static void main(String[] args) {
        // Create the proxy factory
        ProxyFactory proxyFactory = new ProxyFactory();
        // Set the target source to the connection pool target source, with a pool size of 3
        proxyFactory.setTargetSource(new ConnectionPoolTargetSource(3));
        // Get the proxy object
        MyConnection proxy = (MyConnection) proxyFactory.getProxy();

        // Call the method 10 times
        for (int i = 0; i < 10; i++) {
            System.out.println("MyConnection Name = " + proxy.getName());
        }
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

In the run result, the connection pool provides the connection objects in a cycle. After every connection object in the pool has been used once, it starts again from the first one. This matches the expected behavior of a connection pool, and shows that connection objects are reused and managed.

```java
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
