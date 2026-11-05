## HierarchicalBeanFactory

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `HierarchicalBeanFactory` interface in Spring represents a `BeanFactory` that has a hierarchy. In other words, it supports the idea of parent and child containers. It extends `BeanFactory` and adds methods that let containers form a hierarchy. A child container can access the bean definitions of its parent container.

### 3. Main Features

1. **Get the parent container**

   - The `getParentBeanFactory()` method returns the parent `BeanFactory` of the current `BeanFactory`. This lets a child container access and use beans defined in the parent container.

2. **Check for local beans**

   - The `containsLocalBean(String name)` method checks if the current `BeanFactory` contains a local bean with the given name. A local bean is a bean defined in the current container. It is not inherited from a parent container.

### 4. Interface Source

`HierarchicalBeanFactory` is a sub-interface implemented by bean factories that can be part of a Spring container hierarchy. It defines methods to get the parent bean factory and to check if the local bean factory contains a bean with a given name.

```java
/**
 * Sub-interface implemented by bean factories that can be part
 * of a hierarchy.
 *
 * <p>The corresponding {@code setParentBeanFactory} method for bean
 * factories that allow setting the parent in a configurable
 * fashion can be found in the ConfigurableBeanFactory interface.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 07.07.2003
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
 */
public interface HierarchicalBeanFactory extends BeanFactory {

    /**
     * Return the parent bean factory, or {@code null} if there is none.
     */
    @Nullable
    BeanFactory getParentBeanFactory();

    /**
     * Return whether the local bean factory contains a bean of the given name,
     * ignoring beans defined in ancestor contexts.
     * <p>This is an alternative to {@code containsBean}, ignoring a bean
     * of the given name from an ancestor bean factory.
     * @param name the name of the bean to query
     * @return whether a bean with the given name is defined in the local factory
     * @see BeanFactory#containsBean
     */
    boolean containsLocalBean(String name);
}
```

### 5. Main Implementation

- `DefaultListableBeanFactory`
  - `DefaultListableBeanFactory` is one of the key classes that implement the `BeanFactory` interface in Spring. It registers, manages, and initializes all bean definitions in an application. It supports dependency injection, beans in different scopes, `FactoryBean` handling, container hierarchies, and lifecycle callbacks. It is the core implementation of the Spring IoC container and gives flexible and powerful bean management and configuration.

### 6. Best Practices

This example creates a Spring container hierarchy. The parent container holds a bean named `MyBean`. The child container uses the parent container and tries to get this bean. The code uses the methods of `HierarchicalBeanFactory` to show how to access a parent bean from the child container, how to check if the local `BeanFactory` or the whole hierarchy contains a bean with a given name, and how to get the parent `BeanFactory`.

```java
public class HierarchicalBeanFactoryDemo {

    public static void main(String[] args) {
        // Create the parent container
        AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext(MyBean.class);
        // Create the child container
        AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
        childContext.setParent(parentContext);

        // Get the bean from the child BeanFactory
        HierarchicalBeanFactory childHierarchicalBeanFactory = childContext.getBeanFactory();
        System.out.println("Get bean from child BeanFactory: " + childHierarchicalBeanFactory.getBean(MyBean.class));

        // Get the bean from the parent BeanFactory
        HierarchicalBeanFactory parentHierarchicalBeanFactory = parentContext.getBeanFactory();
        System.out.println("Get bean from parent BeanFactory: " + parentHierarchicalBeanFactory.getBean(MyBean.class));

        // Get the parent BeanFactory
        BeanFactory parentBeanFactory = childHierarchicalBeanFactory.getParentBeanFactory();
        System.out.println("Get parent BeanFactory: " + parentBeanFactory);

        // Check if the local BeanFactory contains a bean with the given name
        boolean containsLocalBean = childHierarchicalBeanFactory.containsLocalBean("myBean");
        System.out.println("Local BeanFactory contains the bean: " + containsLocalBean);

        // Check if the whole BeanFactory hierarchy contains a bean with the given name
        boolean containsBean = childHierarchicalBeanFactory.containsBean("myBean");
        System.out.println("Whole BeanFactory contains the bean: " + containsBean);
    }
}
```

The output shows how the container hierarchy works. It shows bean inheritance between parent and child containers, and how to check if a bean exists at different levels.

```java
Get bean from child BeanFactory: com.xcs.spring.bean.MyBean@6379eb
Get bean from parent BeanFactory: com.xcs.spring.bean.MyBean@6379eb
Get parent BeanFactory: org.springframework.beans.factory.support.DefaultListableBeanFactory@2f112965: defining beans [org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.event.internalEventListenerProcessor,org.springframework.context.event.internalEventListenerFactory,myBean]; root of factory hierarchy
Local BeanFactory contains the bean: false
Whole BeanFactory contains the bean: true
```

### 7. Relation to Other Components

1. **ApplicationContext interface**

   - `HierarchicalBeanFactory` is a super-interface of `ApplicationContext`. So any `ApplicationContext` is also a `HierarchicalBeanFactory`. `ApplicationContext` is the core interface for the application context in Spring. It adds more features, such as event publishing and internationalization support.

2. **ConfigurableApplicationContext interface**

   - `ConfigurableApplicationContext` is a sub-interface of `ApplicationContext`, and it also extends `HierarchicalBeanFactory`. It defines extra methods to configure the application context, such as setting the parent context and activating or deactivating profiles.

3. **BeanFactory interface**

   - `HierarchicalBeanFactory` extends the `BeanFactory` interface. So it inherits many methods from `BeanFactory` to get and manage bean instances.

4. **DefaultListableBeanFactory class**

   - `DefaultListableBeanFactory` is the default implementation of `HierarchicalBeanFactory` in Spring. It implements the `HierarchicalBeanFactory` interface and provides the standard `BeanFactory` features. It also implements `ConfigurableListableBeanFactory`, which adds more configuration options.

5. **BeanDefinition interface**

   - `HierarchicalBeanFactory` is closely related to `BeanDefinition`. `BeanDefinition` holds the metadata of a bean, such as the class name, property values, and constructor arguments. In a container hierarchy, the bean factory manages and maintains these `BeanDefinition` objects.

6. **ApplicationContext hierarchy**

   - `HierarchicalBeanFactory` supports the Spring container hierarchy. With a parent-child relation, a container can inherit and override bean definitions. This helps build modular and well-organized applications. Beans can be shared or isolated between containers at different levels.

### 8. Common Problems

1. **Bean name conflicts**

   - If the child and parent containers both define a bean with the same name, the names conflict. Avoid defining beans with the same name in both containers. If a conflict cannot be avoided, redefine the bean in the child container to override the parent definition.

2. **Initialization order of parent and child containers**

   - The initialization order may cause code to access a parent bean before the child container is initialized. Make sure the child container is fully initialized before you access its beans. You can use event listeners or similar tools to keep the initialization order correct.

3. **Updating beans in the parent container**

   - The child container reads the bean definitions of the parent container when it initializes. It does not update them again later. If you need to update the parent bean definitions at runtime, refresh the child container at a suitable time.

4. **Referencing beans in the parent container**

   - Beans in the child container can refer directly to beans in the parent container. Spring handles these references and injects the parent beans into the child container correctly.

5. **Depth of the hierarchy**

   - Spring supports container hierarchies, but think about how deep the hierarchy should be. A deep hierarchy makes container initialization and bean lookup more complex, and it may hurt performance.

6. **Configuration errors**

   - Review the configuration often to make sure the parent-child relation is set correctly. Use proper configuration and annotations to avoid mistakes.
