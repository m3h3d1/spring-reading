## BeanDefinitionRegistry

- [BeanDefinitionRegistry](#beandefinitionregistry)
  - [1. Background Knowledge](#1-background-knowledge)
  - [2. Basic Description](#2-basic-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementations](#5-main-implementations)
  - [6. Best Practices](#6-best-practices)
  - [7. Relationship with Other Components](#7-relationship-with-other-components)
  - [8. FAQ](#8-faq)

### 1. Background Knowledge

1. **Bean definition:**
   - Understand what a bean definition is. It includes the bean name, class name, scope, dependencies and so on.
   - Know how to define beans with XML configuration, annotations or Java configuration.
   - Know the related concepts in a bean definition, such as property settings, constructor injection and property injection.

### 2. Basic Description

The `BeanDefinitionRegistry` interface is a key interface in the Spring Framework. It manages and registers bean definitions. A bean definition is the metadata that describes an object (bean) in the Spring container. It includes the class name, dependencies, scope and so on.

### 3. Main Features

1. **Register bean definitions**
   + With the `BeanDefinitionRegistry` interface, we can register bean definitions in the Spring container. This means we can add the metadata of a bean (such as class name, scope and properties) to the container, so the container can instantiate and manage bean objects from these definitions.
2. **Remove bean definitions**
   + We can use the methods of the interface to remove bean definitions that are no longer needed. This can free resources and reduce the load on the container. It is usually used when some bean definitions are no longer needed at run time.
3. **Check whether a bean definition exists**
   + We can use the methods of the interface to check whether the container has a bean definition with a given name. This helps us avoid registering the same name twice.
4. **Get bean definition information**
   + Through the interface, we can get the details of registered bean definitions, including the class name, scope and property values. This is useful for viewing or changing bean definitions at run time.
5. **Batch operations**
   + Besides operations on a single bean definition, `BeanDefinitionRegistry` also supports batch registration and removal. This is useful when you register several bean definitions in one operation, because it reduces repeated work.
6. **Hierarchical registration**
   + The Spring container can be hierarchical, with several container levels. The `BeanDefinitionRegistry` interface supports registering bean definitions in these hierarchies. This is useful for modular applications.
7. **Custom extension**
   + The Spring Framework lets you implement the `BeanDefinitionRegistry` interface yourself to create a custom bean definition registry for special needs. This makes the Spring Framework highly extensible.

### 4. Interface Source

The `BeanDefinitionRegistry` interface manages and registers bean definitions. It lets us register, remove and retrieve bean definitions and do other operations on them. The interface is used to implement the Spring IoC container. It lets an application register and manage bean definitions so that dependency injection can work.

```java
/**
 * Interface for registries that hold bean definitions (for example RootBeanDefinition and ChildBeanDefinition instances).
 * Typically implemented by bean factories that work internally with the AbstractBeanDefinition hierarchy.
 *
 * This is the only interface in Spring's bean factory package that encapsulates the registration of bean definitions.
 * The standard BeanFactory interface only covers access to a fully configured factory instance.
 *
 * Spring's bean definition readers expect to work on an implementation of this interface.
 * Known implementors in Spring core include DefaultListableBeanFactory and GenericApplicationContext.
 *
 * @author Juergen Hoeller
 * @since 26.11.2003
 * @see org.springframework.beans.factory.config.BeanDefinition
 * @see AbstractBeanDefinition
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 * @see DefaultListableBeanFactory
 * @see org.springframework.context.support.GenericApplicationContext
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 * @see PropertiesBeanDefinitionReader
 */
public interface BeanDefinitionRegistry extends AliasRegistry {

    /**
     * Register a new bean definition with this registry.
     * Must support RootBeanDefinition and ChildBeanDefinition.
     *
     * @param beanName the name of the bean instance to register
     * @param beanDefinition the definition of the bean instance to register
     * @throws BeanDefinitionStoreException if the BeanDefinition is invalid
     * @throws BeanDefinitionOverrideException if a BeanDefinition with the given name already exists and overriding is not allowed
     * @see GenericBeanDefinition
     * @see RootBeanDefinition
     * @see ChildBeanDefinition
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException;

    /**
     * Remove the BeanDefinition with the given name.
     *
     * @param beanName the name of the bean instance to remove
     * @throws NoSuchBeanDefinitionException if there is no such bean definition
     */
    void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    /**
     * Return the BeanDefinition for the given bean name.
     *
     * @param beanName the name of the bean to find a definition for
     * @return the BeanDefinition for the given name (never {@code null})
     * @throws NoSuchBeanDefinitionException if there is no such bean definition
     */
    BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    /**
     * Check whether this registry contains a bean definition with the given name.
     *
     * @param beanName the name of the bean to look for
     * @return true if this registry contains a bean definition with the given name
     */
    boolean containsBeanDefinition(String beanName);

    /**
     * Return the names of all beans defined in this registry.
     *
     * @return the names of all beans defined in this registry, or an empty array if there are none
     */
    String[] getBeanDefinitionNames();

    /**
     * Return the number of beans defined in the registry.
     *
     * @return the number of beans defined in the registry
     */
    int getBeanDefinitionCount();

    /**
     * Determine whether the given bean name is already in use within this registry, that is, whether a local bean or alias is registered under this name.
     *
     * @param beanName the name to check
     * @return whether the given bean name is already in use
     */
    boolean isBeanNameInUse(String beanName);

}
```

### 5. Main Implementations

1. **`DefaultListableBeanFactory`**
   + This is one of the most commonly used implementations of the `BeanDefinitionRegistry` interface in Spring. It is the core Spring container. It manages bean definitions, instantiates bean objects and provides dependency injection. Usually, we can configure `DefaultListableBeanFactory` in an XML file to define and register beans.
2. **`GenericApplicationContext`** 
   + `GenericApplicationContext` is an implementation of the Spring application context and it implements the `BeanDefinitionRegistry` interface. It is a general-purpose application context. It can be used to register and manage bean definitions in code.
3. **`SimpleBeanDefinitionRegistry`**
   + `SimpleBeanDefinitionRegistry` provides basic `BeanDefinition` registration and management. It is fairly simple and does not support advanced features such as XML configuration parsing. It is usually used for quick prototypes, unit tests and some simple applications, to reduce configuration and complexity.

~~~mermaid
classDiagram
    direction BT
    
    class AliasRegistry {
    	<<interface>>
    }
    
    class BeanDefinitionRegistry {
    	<<interface>>
    }
    
    class DefaultListableBeanFactory {
    }

    class GenericApplicationContext {
    }
    
    class SimpleBeanDefinitionRegistry {
    }
    
    BeanDefinitionRegistry ..|> AliasRegistry
    DefaultListableBeanFactory --|> BeanDefinitionRegistry
    GenericApplicationContext --|> BeanDefinitionRegistry
    SimpleBeanDefinitionRegistry --|> BeanDefinitionRegistry

~~~

### 6. Best Practices

This example uses the Spring `DefaultListableBeanFactory` and the `BeanDefinitionRegistry` interface to register, manage and operate on bean definitions. The steps are: create a bean definition, register it in the container, get it, check whether it exists, get the names and the count of all definitions, check whether a name is in use, and remove the bean definition. This shows the core features of the Spring IoC container. An application can configure and manage beans dynamically at run time.

```java
public class BeanDefinitionRegistryDemo {

    public static void main(String[] args) {
        // Create a BeanFactory, which is a BeanDefinitionRegistry
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // Create a bean definition
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(MyBean.class);

        // Register the bean definition in the BeanFactory
        beanFactory.registerBeanDefinition("myBean", beanDefinition);

        // Get the BeanDefinition
        BeanDefinition retrievedBeanDefinition = beanFactory.getBeanDefinition("myBean");
        System.out.println("Bean definition class name = " + retrievedBeanDefinition.getBeanClassName());

        // Check whether the bean definition exists
        boolean containsBeanDefinition = beanFactory.containsBeanDefinition("myBean");
        System.out.println("Contains bean definition (myBean) = " + containsBeanDefinition);

        // Get the names of all bean definitions
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        System.out.println("Bean definition names = " + String.join(", ", beanDefinitionNames));

        // Get the number of bean definitions
        int beanDefinitionCount = beanFactory.getBeanDefinitionCount();
        System.out.println("Bean definition count = " + beanDefinitionCount);

        // Check whether the bean name is in use
        boolean isBeanNameInUse = beanFactory.isBeanNameInUse("myBean");
        System.out.println("Bean name (myBean) in use = " + isBeanNameInUse);

        // Remove the bean definition
        beanFactory.removeBeanDefinition("myBean");
        System.out.println("Bean definition removed (myBean)");
    }
}
```

`MyBean` is a simple Java class that represents a plain Java bean.

```java
public class MyBean {

}
```

The result shows that all our operations succeeded: registering, querying, operating on and removing bean definitions. This shows the core features of the Spring IoC container. We can manage bean definitions dynamically at run time.

```java
Bean definition class name = com.xcs.spring.bean.MyBean
Contains bean definition (myBean) = true
Bean definition names = myBean
Bean definition count = 1
Bean name (myBean) in use = true
Bean definition removed (myBean)
```

### 7. Relationship with Other Components

1. **`DefaultListableBeanFactory`**
   + `BeanDefinitionRegistry` is a main interface implemented by `DefaultListableBeanFactory`. It registers and manages bean definitions (`BeanDefinition`). `BeanFactory` is the Spring IoC container, which instantiates and manages bean objects. `DefaultListableBeanFactory` can register and get bean definitions through `BeanDefinitionRegistry`, and then use the definitions to create bean instances.
2. **`GenericApplicationContext`** 
   + `GenericApplicationContext` is a higher-level extension of `DefaultListableBeanFactory`. It has the features of `DefaultListableBeanFactory` and also gives application-level services such as internationalization, event publishing and resource loading. `DefaultListableBeanFactory` usually uses `BeanDefinitionRegistry` to register and manage bean definitions, so beans can be configured and managed in the application context.
3. **`BeanDefinition`**
   + `BeanDefinitionRegistry` mainly works with bean definitions (`BeanDefinition`). It registers, queries and manages bean definitions, which hold metadata such as class name, scope and dependencies. `BeanFactory` and `ApplicationContext` use these bean definitions to create and manage bean instances.
4. **`BeanPostProcessor`**
   + `BeanPostProcessor` is an interface that intercepts bean instantiation. It can be used in `DefaultListableBeanFactory` and `GenericApplicationContext`. `BeanDefinitionRegistry` can be combined with `BeanPostProcessor` to run custom logic before and after bean instantiation. This lets us step in during bean creation and customize its initialization and destruction.

### 8. FAQ

1. **What is `BeanDefinitionRegistry`?**
   - `BeanDefinitionRegistry` is an interface in the Spring Framework. It registers and manages bean definitions. It is a core part of the IoC container. It registers the bean definitions of the application in the container, for later instantiation and management.
2. **What is the relationship between `BeanDefinitionRegistry` and `DefaultListableBeanFactory`?**
   - `DefaultListableBeanFactory` is the Spring IoC container, and it is a concrete implementation of `BeanDefinitionRegistry`. This lets `DefaultListableBeanFactory` manage and access bean definitions internally. `DefaultListableBeanFactory` is in fact an IoC container that includes the `BeanDefinitionRegistry` features.
3. **What is the relationship between `BeanDefinition` and `BeanDefinitionRegistry`?**
   - `BeanDefinition` is the metadata of a bean. It describes how to create and configure the bean. `BeanDefinitionRegistry` registers and manages these `BeanDefinition`s. It lets us register bean definitions and instantiate these beans in the container.
4. **How do I register a bean definition with `BeanDefinitionRegistry`?**
   - We can create a container that implements the `BeanDefinitionRegistry` interface (such as `DefaultListableBeanFactory` or `GenericApplicationContext`) and use its `registerBeanDefinition` method. Usually, we need to give the bean name and the matching `BeanDefinition`.
5. **Can I register several beans with the same name?**
   - By default, no. If you try to register a bean with the same name, a `BeanDefinitionOverrideException` is usually thrown. In some cases, we can allow overriding existing bean definitions.
6. **How do I check whether a bean definition with a given name exists?**
   - Use the `containsBeanDefinition` method. It returns a boolean that tells whether the bean definition exists.
7. **How do I get the names and the number of registered bean definitions?**
   - The `getBeanDefinitionNames` method returns an array of the names of all registered bean definitions. The `getBeanDefinitionCount` method returns the number of registered bean definitions.
8. **How do I remove a registered bean definition?**
   - Use the `removeBeanDefinition` method. It removes the bean definition from the container, so it is no longer available.
9. **What is the relationship between `BeanDefinitionRegistry` and `BeanPostProcessor`?**
   - `BeanPostProcessor` is an interface that works during bean instantiation and initialization. `BeanDefinitionRegistry` can be combined with `BeanPostProcessor` to run custom logic while bean definitions are created and initialized. This lets us customize things when the bean is created.