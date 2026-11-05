## ConfigurableListableBeanFactory

- [ConfigurableListableBeanFactory](#configurablelistablebeanfactory)
	- [1. Overview](#1-overview)
	- [2. Description](#2-description)
	- [3. Main Features](#3-main-features)
	- [4. Interface Source](#4-interface-source)
	- [5. Best Practices](#5-best-practices)
	- [6. Relation to Other Components](#6-relation-to-other-components)
	- [7. Common Problems](#7-common-problems)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

`ConfigurableListableBeanFactory` is a key interface in Spring. It extends `ListableBeanFactory` and `ConfigurableBeanFactory`, and gives more methods to configure and manage beans. With this interface, you can register singleton objects, register custom scopes, freeze the configuration, get `BeanDefinition` information, set the parent `BeanFactory`, and more.

### 3. Main Features

1. **Register a singleton object** 

   + You can register a singleton object under a bean name with the `registerSingleton(String beanName, Object singletonObject)` method.

2. **Register a scope** 

   + The `registerScope(String scopeName, Scope scope)` method registers a custom scope. This extends the default scopes of Spring, such as singleton and prototype.

3. **Freeze the configuration** 

   + The `freezeConfiguration()` method freezes the configuration of the bean factory. This stops later changes to it. In some cases, this makes the application safer.

4. **Get registered BeanDefinition objects** 

   + The `getBeanDefinition(String beanName)` method returns the `BeanDefinition` object of the given bean name. It holds the configuration of the bean, such as scope and dependencies.

5. **Get the names of all registered BeanDefinition objects** 

   + The `getBeanDefinitionNames()` method returns an array with the names of all registered bean definitions. This makes it easy to go through and check them.

6. **Get the names of all registered singleton beans** 

   + The `getSingletonNames()` method returns an array with the names of all registered singleton beans. This makes it easy to see and manage the beans that are already instantiated.

7. **Set the parent BeanFactory** 

   + The `setParentBeanFactory(BeanFactory parentBeanFactory)` method sets a parent `BeanFactory`. Bean definitions are then also looked up in the parent factory.

8. **Get the class loader** 

   + The `getBeanClassLoader()` method returns the class loader used to load bean classes. This is useful when classes are loaded dynamically.

### 4. Interface Source

```java
/**
 * Configuration interface to be implemented by most listable bean factories.
 * In addition to {@link ConfigurableBeanFactory}, it provides facilities to
 * analyze and modify bean definitions, and to pre-instantiate singletons.
 *
 * <p>This subinterface of {@link org.springframework.beans.factory.BeanFactory}
 * is not meant to be used in normal application code: Stick to
 * {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * use cases. This interface is just meant to allow for framework-internal
 * plug'n'play even when needing access to bean factory configuration methods.
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.context.support.AbstractApplicationContext#getBeanFactory()
 */
public interface ConfigurableListableBeanFactory
		extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

	/**
	 * Ignore the given dependency type for autowiring:
	 * for example, String. Default is none.
	 * @param type the dependency type to ignore
	 */
	void ignoreDependencyType(Class<?> type);

	/**
	 * Ignore the given dependency interface for autowiring.
	 * <p>This will typically be used by application contexts to register
	 * dependencies that are resolved in other ways, like BeanFactory through
	 * BeanFactoryAware or ApplicationContext through ApplicationContextAware.
	 * <p>By default, only the BeanFactoryAware interface is ignored.
	 * For further types to ignore, invoke this method for each type.
	 * @param ifc the dependency interface to ignore
	 * @see org.springframework.beans.factory.BeanFactoryAware
	 * @see org.springframework.context.ApplicationContextAware
	 */
	void ignoreDependencyInterface(Class<?> ifc);

	/**
	 * Register a special dependency type with corresponding autowired value.
	 * <p>This is intended for factory/context references that are supposed
	 * to be autowirable but are not defined as beans in the factory:
	 * e.g. a dependency of type ApplicationContext resolved to the
	 * ApplicationContext instance that the bean is living in.
	 * <p>Note: There are no such default types registered in a plain BeanFactory,
	 * not even for the BeanFactory interface itself.
	 * @param dependencyType the dependency type to register. This will typically
	 * be a base interface such as BeanFactory, with extensions of it resolved
	 * as well if declared as an autowiring dependency (e.g. ListableBeanFactory),
	 * as long as the given value actually implements the extended interface.
	 * @param autowiredValue the corresponding autowired value. This may also be an
	 * implementation of the {@link org.springframework.beans.factory.ObjectFactory}
	 * interface, which allows for lazy resolution of the actual target value.
	 */
	void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue);

	/**
	 * Determine whether the specified bean qualifies as an autowire candidate,
	 * to be injected into other beans which declare a dependency of matching type.
	 * <p>This method checks ancestor factories as well.
	 * @param beanName the name of the bean to check
	 * @param descriptor the descriptor of the dependency to resolve
	 * @return whether the bean should be considered as autowire candidate
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 */
	boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
			throws NoSuchBeanDefinitionException;

	/**
	 * Return the registered BeanDefinition for the specified bean, allowing access
	 * to its property values and constructor argument value (which can be
	 * modified during bean factory post-processing).
	 * <p>A returned BeanDefinition object should not be a copy but the original
	 * definition object as registered in the factory. This means that it should
	 * be castable to a more specific implementation type, if necessary.
	 * <p><b>NOTE:</b> This method does <i>not</i> consider ancestor factories.
	 * It is only meant for accessing local bean definitions of this factory.
	 * @param beanName the name of the bean
	 * @return the registered BeanDefinition
	 * @throws NoSuchBeanDefinitionException if there is no bean definition
	 * with the given name defined in this factory
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Return a unified view over all bean names managed by this factory.
	 * <p>Includes bean definition names as well as names of manually registered
	 * singleton instances, with bean definition names consistently coming first,
	 * analogous to how type/annotation specific retrieval of bean names works.
	 * @return the composite iterator for the bean names view
	 * @since 4.1.2
	 * @see #containsBeanDefinition
	 * @see #registerSingleton
	 * @see #getBeanNamesForType
	 * @see #getBeanNamesForAnnotation
	 */
	Iterator<String> getBeanNamesIterator();

	/**
	 * Clear the merged bean definition cache, removing entries for beans
	 * which are not considered eligible for full metadata caching yet.
	 * <p>Typically triggered after changes to the original bean definitions,
	 * e.g. after applying a {@link BeanFactoryPostProcessor}. Note that metadata
	 * for beans which have already been created at this point will be kept around.
	 * @since 4.2
	 * @see #getBeanDefinition
	 * @see #getMergedBeanDefinition
	 */
	void clearMetadataCache();

	/**
	 * Freeze all bean definitions, signalling that the registered bean definitions
	 * will not be modified or post-processed any further.
	 * <p>This allows the factory to aggressively cache bean definition metadata.
	 */
	void freezeConfiguration();

	/**
	 * Return whether this factory's bean definitions are frozen,
	 * i.e. are not supposed to be modified or post-processed any further.
	 * @return {@code true} if the factory's configuration is considered frozen
	 */
	boolean isConfigurationFrozen();

	/**
	 * Ensure that all non-lazy-init singletons are instantiated, also considering
	 * {@link org.springframework.beans.factory.FactoryBean FactoryBeans}.
	 * Typically invoked at the end of factory setup, if desired.
	 * @throws BeansException if one of the singleton beans could not be created.
	 * Note: This may have left the factory with some beans already initialized!
	 * Call {@link #destroySingletons()} for full cleanup in this case.
	 * @see #destroySingletons()
	 */
	void preInstantiateSingletons() throws BeansException;
}

```

### 5. Best Practices

This example shows some common methods of the `ConfigurableListableBeanFactory` interface. It covers ignoring dependency types, registering resolvable dependencies, checking if a bean is an autowire candidate, getting a `BeanDefinition`, and more.

```java
public class ConfigurableListableBeanFactoryDemo {

    public static void main(String[] args) throws NoSuchFieldException {
        // Create the ApplicationContext
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);

        // Get the ConfigurableListableBeanFactory
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        // Ignore dependencies of the given type
        beanFactory.ignoreDependencyType(String.class);

        // Ignore dependencies of the given interface
        beanFactory.ignoreDependencyInterface(BeanFactory.class);

        // Register a resolvable dependency
        beanFactory.registerResolvableDependency(ApplicationContext.class, applicationContext);

        // Check if the bean is a candidate for autowiring
        String beanName = "myService";

        DependencyDescriptor dependencyDescriptor = new DependencyDescriptor(MyController.class.getDeclaredField("myService"), false);
        boolean isAutowireCandidate = beanFactory.isAutowireCandidate(beanName, dependencyDescriptor);
        System.out.println(beanName + " is an autowire candidate: " + isAutowireCandidate);

        // Get the BeanDefinition of the bean
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        System.out.println(beanName + " BeanDefinition: " + beanDefinition);

        // Get an iterator over all bean names
        Iterator<String> beanNamesIterator = beanFactory.getBeanNamesIterator();
        System.out.print("All bean names: ");
        beanNamesIterator.forEachRemaining(System.out::print);

        // Clear the metadata cache
        beanFactory.clearMetadataCache();

        // Freeze the configuration
        beanFactory.freezeConfiguration();

        // Check if the configuration is frozen
        boolean isConfigurationFrozen = beanFactory.isConfigurationFrozen();
        System.out.println("Configuration frozen: " + isConfigurationFrozen);

        // Pre-instantiate all non-lazy singleton beans
        beanFactory.preInstantiateSingletons();
    }
}
```

### 6. Relation to Other Components

1. **BeanFactory**

   - `ConfigurableListableBeanFactory` extends `BeanFactory`. So it inherits the basic features of `BeanFactory`, such as the `getBean` methods.

   - `ConfigurableListableBeanFactory` combines `ListableBeanFactory` and `ConfigurableBeanFactory`. It can manage the list of beans and their configuration.

2. **ListableBeanFactory**

   - `ConfigurableListableBeanFactory` extends `ListableBeanFactory`. So besides the features of `ListableBeanFactory`, it also allows more flexible configuration and management.

3. **AutowireCapableBeanFactory**

   - `ConfigurableListableBeanFactory` extends `AutowireCapableBeanFactory`. So it supports more advanced autowiring, such as ignoring dependency types and checking if a bean is an autowire candidate.

4. **ConfigurableBeanFactory**

   - `ConfigurableListableBeanFactory` extends `ConfigurableBeanFactory`. It provides many methods to configure the bean factory, such as setting the bean class loader, registering singletons, and setting scopes.

5. **ApplicationContext**

   - `ConfigurableListableBeanFactory` is part of the Spring application context (`ApplicationContext`). From an `ApplicationContext` instance, you can get a reference to the `ConfigurableListableBeanFactory` and use it to configure and manage beans.

   - Concrete classes such as `GenericApplicationContext` or `AnnotationConfigApplicationContext` hold a `ConfigurableListableBeanFactory` instance inside. They use it to manage bean definitions and instances.

6. **BeanPostProcessor**

   - `ConfigurableListableBeanFactory` works together with the `BeanPostProcessor` interface. `BeanPostProcessor` lets you add custom handling when a bean is instantiated and initialized. `ConfigurableListableBeanFactory` customizes beans by adding `BeanPostProcessor` objects.

7. **BeanDefinition**

   - `ConfigurableListableBeanFactory` has a method to get a `BeanDefinition`. With `getBeanDefinition(String beanName)`, you get the definition of the given bean. This is very useful to view and change the configuration of a bean.

### 7. Common Problems

1. **Circular dependency between beans** 

   - When two or more beans depend on each other in a circle, the application may fail to start or become unstable. Try to design the beans to avoid circular dependencies. You can use constructor injection, setter injection, or the `@Lazy` annotation to solve them.

2. **Wrong bean configuration** 

   - A bean definition in the configuration has an error, so the bean cannot be created correctly. Check the configuration carefully. Make sure the XML or Java configuration is correct, and the property values and references are right.

3. **Dependency injection fails** 

   - Dependency injection fails and some properties of a bean are null. Check the dependencies of the bean. Make sure the names and types of the injected properties are correct. When you use `@Autowired` or `@Resource`, check that autowiring works.

4. **Bean lifecycle problems** 

   - You want to run some actions when a bean is initialized or destroyed, but the actions do not run. Use the `InitializingBean` and `DisposableBean` interfaces, or use the `@PostConstruct` and `@PreDestroy` annotations on methods to run the initialization and destruction logic.

5. **Bean scope problems** 

   - You want to use a different scope (such as singleton or prototype), but it has no effect. Set the bean scope clearly in the configuration file or in the Java configuration. Make sure the scope is used as you expect.

6. **Lazy loading problems** 

   - You want to make a bean lazy, but the setting has no effect. Use the `@Lazy` annotation, or set `lazy-init="true"` in the XML configuration. Make sure the lazy configuration is correct.

7. **Conditional registration problems** 

   - You want to register a bean dynamically based on a condition, but the condition has no effect. Use conditional annotations (such as `@Conditional`), or check the condition in code before you register the bean in `ConfigurableListableBeanFactory`.

8. **Bean post-processor problems** 

   - You want to add extra handling for all beans, but your own `BeanPostProcessor` has no effect. Make sure your `BeanPostProcessor` is registered correctly. You can add it with the `ConfigurableListableBeanFactory.addBeanPostProcessor` method.
