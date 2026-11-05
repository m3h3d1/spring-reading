## BeanFactory

- [BeanFactory](#beanfactory)
  - [1. Overview](#1-overview)
  - [2. Description](#2-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementation](#5-main-implementation)
  - [6. Best Practices](#6-best-practices)
  - [7. Relation to Other Components](#7-relation-to-other-components)
  - [8. Common Problems](#8-common-problems)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `BeanFactory` interface is the core interface of the IoC container in Spring. It defines a standard way to manage and get Java object instances. The `getBean` method gets a bean instance from the container by name. The `containsBean` method checks if the container has a bean with the given name. The `getType` method returns the type of a bean. The `isSingleton` method checks if a bean is a singleton. The interface supports lazy loading, which helps performance. `BeanFactory` is the base of the IoC container, but in real applications you usually use `ApplicationContext`. It extends `BeanFactory` and adds more features, such as event publishing, AOP, and internationalization. This makes it easier to build flexible and powerful applications.

### 3. Main Features

1. **Get a bean**

   +  `BeanFactory` provides the `getBean(String name)` method. It gets the bean instance with the given name from the container. You can define beans in the container with configuration files or annotations, and get them when you need them.

2. **Check if a bean exists**

   + The `containsBean(String name)` method checks if the container contains a bean with the given name. This is useful to avoid defining beans with the same name twice, and to check if a bean is already registered.

3. **Get the bean type** 

   + The `getType(String name)` method returns the type of the bean with the given name. This lets you learn the type of a bean at runtime and handle it in the right way.

4. **Check if a bean is a singleton** 

   + The `isSingleton(String name)` method checks if the bean with the given name is a singleton. This is important to know the scope of a bean and whether the container shares one instance.

### 4. Interface Source

The `BeanFactory` interface is the root interface in Spring that manages and gets Java objects (beans). It is the basic client view of a bean container. It holds the configuration of application components in one place. It supports different kinds of bean instances, bean lifecycle management, and dependency injection. It also supports hierarchies of factories. With this interface, Spring implements inversion of control (IoC) and dependency injection. This is the base for building flexible and maintainable applications.

```java
/**
 * The root interface for accessing a Spring bean container.
 *
 * <p>This is the basic client view of a bean container;
 * further interfaces such as {@link ListableBeanFactory} and
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * are available for specific purposes.
 *
 * <p>This interface is implemented by objects that hold a number of bean definitions,
 * each uniquely identified by a String name. Depending on the bean definition,
 * the factory will return either an independent instance of a contained object
 * (the Prototype design pattern), or a single shared instance (a superior
 * alternative to the Singleton design pattern, in which the instance is a
 * singleton in the scope of the factory). Which type of instance will be returned
 * depends on the bean factory configuration: the API is the same. Since Spring
 * 2.0, further scopes are available depending on the concrete application
 * context (e.g. "request" and "session" scopes in a web environment).
 *
 * <p>The point of this approach is that the BeanFactory is a central registry
 * of application components, and centralizes configuration of application
 * components (no more do individual objects need to read properties files,
 * for example). See chapters 4 and 11 of "Expert One-on-One J2EE Design and
 * Development" for a discussion of the benefits of this approach.
 *
 * <p>Note that it is generally better to rely on Dependency Injection
 * ("push" configuration) to configure application objects through setters
 * or constructors, rather than use any form of "pull" configuration like a
 * BeanFactory lookup. Spring's Dependency Injection functionality is
 * implemented using this BeanFactory interface and its subinterfaces.
 *
 * <p>Normally a BeanFactory will load bean definitions stored in a configuration
 * source (such as an XML document), and use the {@code org.springframework.beans}
 * package to configure the beans. However, an implementation could simply return
 * Java objects it creates as necessary directly in Java code. There are no
 * constraints on how the definitions could be stored: LDAP, RDBMS, XML,
 * properties file, etc. Implementations are encouraged to support references
 * amongst beans (Dependency Injection).
 *
 * <p>In contrast to the methods in {@link ListableBeanFactory}, all of the
 * operations in this interface will also check parent factories if this is a
 * {@link HierarchicalBeanFactory}. If a bean is not found in this factory instance,
 * the immediate parent factory will be asked. Beans in this factory instance
 * are supposed to override beans of the same name in any parent factory.
 *
 * <p>Bean factory implementations should support the standard bean lifecycle interfaces
 * as far as possible. The full set of initialization methods and their standard order is:
 * <ol>
 * <li>BeanNameAware's {@code setBeanName}
 * <li>BeanClassLoaderAware's {@code setBeanClassLoader}
 * <li>BeanFactoryAware's {@code setBeanFactory}
 * <li>EnvironmentAware's {@code setEnvironment}
 * <li>EmbeddedValueResolverAware's {@code setEmbeddedValueResolver}
 * <li>ResourceLoaderAware's {@code setResourceLoader}
 * (only applicable when running in an application context)
 * <li>ApplicationEventPublisherAware's {@code setApplicationEventPublisher}
 * (only applicable when running in an application context)
 * <li>MessageSourceAware's {@code setMessageSource}
 * (only applicable when running in an application context)
 * <li>ApplicationContextAware's {@code setApplicationContext}
 * (only applicable when running in an application context)
 * <li>ServletContextAware's {@code setServletContext}
 * (only applicable when running in a web application context)
 * <li>{@code postProcessBeforeInitialization} methods of BeanPostProcessors
 * <li>InitializingBean's {@code afterPropertiesSet}
 * <li>a custom {@code init-method} definition
 * <li>{@code postProcessAfterInitialization} methods of BeanPostProcessors
 * </ol>
 *
 * <p>On shutdown of a bean factory, the following lifecycle methods apply:
 * <ol>
 * <li>{@code postProcessBeforeDestruction} methods of DestructionAwareBeanPostProcessors
 * <li>DisposableBean's {@code destroy}
 * <li>a custom {@code destroy-method} definition
 * </ol>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.EnvironmentAware#setEnvironment
 * @see org.springframework.context.EmbeddedValueResolverAware#setEmbeddedValueResolver
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor#postProcessBeforeDestruction
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

	/**
	 * Used to dereference a {@link FactoryBean} instance and distinguish it from
	 * beans <i>created</i> by the FactoryBean. For example, if the bean named
	 * {@code myJndiObject} is a FactoryBean, getting {@code &myJndiObject}
	 * will return the factory, not the instance returned by the factory.
	 */
	String FACTORY_BEAN_PREFIX = "&";

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>This method allows a Spring BeanFactory to be used as a replacement for the
	 * Singleton or Prototype design pattern. Callers may retain references to
	 * returned objects in the case of Singleton beans.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to retrieve
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no bean with the specified name
	 * @throws BeansException if the bean could not be obtained
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>Behaves the same as {@link #getBean(String)}, but provides a measure of type
	 * safety by throwing a BeanNotOfRequiredTypeException if the bean is not of the
	 * required type. This means that ClassCastException can't be thrown on casting
	 * the result correctly, as can happen with {@link #getBean(String)}.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to retrieve
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * @param name the name of the bean to retrieve
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 2.5
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 4.1
	 */
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	/**
	 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see #getBeanProvider(ResolvableType)
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	/**
	 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * @param requiredType type the bean must match; can be a generic type declaration.
	 * Note that collection types are not supported here, in contrast to reflective
	 * injection points. For programmatically retrieving a list of beans matching a
	 * specific type, specify the actual bean type as an argument here and subsequently
	 * use {@link ObjectProvider#orderedStream()} or its lazy streaming/iteration options.
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see ObjectProvider#iterator()
	 * @see ObjectProvider#stream()
	 * @see ObjectProvider#orderedStream()
	 */
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);
	
	/**
     * Does this bean factory contain a bean definition or externally registered singleton
     * instance with the given name?
     * <p>If the given name is an alias, it will be translated back to the corresponding
     * canonical bean name.
     * <p>If this factory is hierarchical, will ask any parent factory if the bean cannot
     * be found in this factory instance.
     * <p>If a bean definition or singleton instance matching the given name is found,
     * this method will return {@code true} whether the named bean definition is concrete
     * or abstract, lazy or eager, in scope or not. Therefore, note that a {@code true}
     * return value from this method does not necessarily indicate that {@link #getBean}
     * will be able to obtain an instance for the same name.
     * @param name the name of the bean to query
     * @return whether a bean with the given name is present
     */
    boolean containsBean(String name);

    /**
     * Is this bean a shared singleton? That is, will {@link #getBean} always
     * return the same instance?
     * <p>Note: This method returning {@code false} does not clearly indicate
     * independent instances. It indicates non-singleton instances, which may correspond
     * to a scoped bean as well. Use the {@link #isPrototype} operation to explicitly
     * check for independent instances.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @return whether this bean corresponds to a singleton instance
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @see #getBean
     * @see #isPrototype
     */
    boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

    /**
     * Is this bean a prototype? That is, will {@link #getBean} always return
     * independent instances?
     * <p>Note: This method returning {@code false} does not clearly indicate
     * a singleton object. It indicates non-independent instances, which may correspond
     * to a scoped bean as well. Use the {@link #isSingleton} operation to explicitly
     * check for a shared singleton instance.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @return whether this bean will always deliver independent instances
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 2.0.3
     * @see #getBean
     * @see #isSingleton
     */
    boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

    /**
     * Check whether the bean with the given name matches the specified type.
     * More specifically, check whether a {@link #getBean} call for the given name
     * would return an object that is assignable to the specified target type.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @param typeToMatch the type to match against (as a {@code ResolvableType})
     * @return {@code true} if the bean type matches,
     * {@code false} if it doesn't match or cannot be determined yet
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 4.2
     * @see #getBean
     * @see #getType
     */
    boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

    /**
     * Check whether the bean with the given name matches the specified type.
     * More specifically, check whether a {@link #getBean} call for the given name
     * would return an object that is assignable to the specified target type.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @param typeToMatch the type to match against (as a {@code Class})
     * @return {@code true} if the bean type matches,
     * {@code false} if it doesn't match or cannot be determined yet
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 2.0.1
     * @see #getBean
     * @see #getType
     */
    boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

    /**
     * Determine the type of the bean with the given name. More specifically,
     * determine the type of object that {@link #getBean} would return for the given name.
     * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
     * as exposed by {@link FactoryBean#getObjectType()}. This may lead to the initialization
     * of a previously uninitialized {@code FactoryBean} (see {@link #getType(String, boolean)}).
     * <p>Translates aliases back to the corresponding canonical bean name.
     * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @return the type of the bean, or {@code null} if not determinable
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 1.1.2
     * @see #getBean
     * @see #isTypeMatch
     */
    @Nullable
    Class<?> getType(String name) throws NoSuchBeanDefinitionException;

    /**
     * Determine the type of the bean with the given name. More specifically,
     * determine the type of object that {@link #getBean} would return for the given name.
     * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
     * as exposed by {@link FactoryBean#getObjectType()}. Depending on the
     * {@code allowFactoryBeanInit} flag, this may lead to the initialization of a previously
     * uninitialized {@code FactoryBean} if no early type information is available.
     * <p>Translates aliases back to the corresponding canonical bean name.
     * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the name of the bean to query
     * @param allowFactoryBeanInit whether a {@code FactoryBean} may get initialized
     * just for the purpose of determining its object type
     * @return the type of the bean, or {@code null} if not determinable
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 5.2
     * @see #getBean
     * @see #isTypeMatch
     */
    @Nullable
    Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException;

    /**
     * Return the aliases for the given bean name, if any.
     * <p>All of those aliases point to the same bean when used in a {@link #getBean} call.
     * <p>If the given name is an alias, the corresponding original bean name
     * and other aliases (if any) will be returned, with the original bean name
     * being the first element in the array.
     * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
     * @param name the bean name to check for aliases
     * @return the aliases, or an empty array if none
     * @see #getBean
     */
    String[] getAliases(String name);

}
```

### 5. Main Implementation

+ **DefaultListableBeanFactory**
  + `DefaultListableBeanFactory` is one of the key classes that implement the `BeanFactory` interface in Spring. It registers, manages, and initializes all bean definitions in an application. It supports dependency injection, beans in different scopes, `FactoryBean` handling, container hierarchies, and lifecycle callbacks. It is the core implementation of the Spring IoC container and gives flexible and powerful bean management and configuration.

### 6. Best Practices

This example uses different methods of the `BeanFactory` interface to work with and query beans in the Spring container. It covers getting beans, checking types, and looking up aliases.

```java
public class BeanFactoryDemo {

    public static void main(String[] args) {
        // Create the BeanFactory
        BeanFactory beanFactory = new AnnotationConfigApplicationContext(MyBean.class).getBeanFactory();

        // Get a bean by name
        Object bean = beanFactory.getBean("myBean");
        System.out.println("Get bean by name: " + bean);

        // Get the ObjectProvider of a bean
        ObjectProvider<MyBean> objectProvider = beanFactory.getBeanProvider(MyBean.class);
        System.out.println("Get ObjectProvider of bean: " + objectProvider);

        // Get the type of a bean
        Class<?> beanType = beanFactory.getType("myBean");
        System.out.println("Get bean type: " + beanType);

        // Check if a bean exists
        boolean containsBean = beanFactory.containsBean("myBean");
        System.out.println("Contains bean: " + containsBean);

        // Check if a bean is a singleton
        boolean isSingleton = beanFactory.isSingleton("myBean");
        System.out.println("Is singleton: " + isSingleton);

        // Check if a bean is a prototype
        boolean isPrototype = beanFactory.isPrototype("myBean");
        System.out.println("Is prototype: " + isPrototype);

        // Check if a bean matches the given type
        boolean isTypeMatch = beanFactory.isTypeMatch("myBean", ResolvableType.forClass(MyBean.class));
        System.out.println("Type matches: " + isTypeMatch);

        // Get all aliases of a bean
        String[] aliases = beanFactory.getAliases("myBean");
        System.out.println("Get all aliases of bean: " + String.join(", ", aliases));
    }
}
```

The output shows that the container created and manages the `MyBean` instance named `myBean`. It also gives the type information, aliases, and more.

```java
Get bean by name: com.xcs.spring.bean.MyBean@7b9a4292
Get ObjectProvider of bean: org.springframework.beans.factory.support.DefaultListableBeanFactory$1@1aa7ecca
Get bean type: class com.xcs.spring.bean.MyBean
Contains bean: true
Is singleton: true
Is prototype: false
Type matches: true
Get all aliases of bean: 
```

### 7. Relation to Other Components

1. **ApplicationContext**

   - `ApplicationContext` is a sub-interface of `BeanFactory`. It adds more enterprise features and is the context container of a Spring application. It extends `BeanFactory` and adds event publishing, internationalization support, AOP support, and more.

2. **ListableBeanFactory**
   + `ListableBeanFactory` is a sub-interface of `BeanFactory`. It extends `BeanFactory` with more methods to list beans. You can list all beans, find beans by type or by name, and get the names of all beans.

3. **ConfigurableBeanFactory**
   - `ConfigurableBeanFactory` is a sub-interface of `HierarchicalBeanFactory`, and so it extends `BeanFactory` too. It provides more configuration methods. It lets you configure property editors, scopes, class loaders, and more.
   
4. **AutowireCapableBeanFactory**
   - `AutowireCapableBeanFactory` is a sub-interface of `BeanFactory`. It adds more autowiring methods. It lets you autowire beans by constructor injection, property injection, and so on.
   
5. **SingletonBeanRegistry**

   + `SingletonBeanRegistry` is an interface to register and get singleton beans. It defines methods to register and get singleton beans in the container.

6. **BeanDefinitionRegistry**

   + `BeanDefinitionRegistry` is an interface to register and get bean definitions. It lets you register and get bean definitions in the container, including registering beans by name and type.

7. **BeanPostProcessor**

   + `BeanPostProcessor` is an interface that lets you run custom logic before and after bean initialization. `BeanFactory` customizes the bean lifecycle by registering `BeanPostProcessor` objects.

8. **BeanFactoryPostProcessor**

   - `BeanFactoryPostProcessor` is an interface to change the configuration of the container before any bean is instantiated. It lets you change the `BeanFactory` when the container starts. For example, you can change property values or register new beans.

### 8. Common Problems

1. **NoSuchBeanDefinitionException**

   - When you get a bean with `getBean`, `NoSuchBeanDefinitionException` is thrown. It means no bean with the given name was found. Possible causes: the bean name is misspelled, the bean is not registered correctly, or the bean scope is not what you expect. Check the bean definition and the name, and make sure the bean is registered in the container.

2. **BeanCreationException**

   - When the container initializes or gets a bean, `BeanCreationException` is thrown. It means the bean could not be created. Possible causes: a dependency of the bean cannot be satisfied, the bean constructor throws an exception, or some other initialization problem. Check the bean dependencies, and make sure the constructor and the initialization methods do not throw exceptions.

3. **CircularDependencyException**

   - The Spring container detects a circular dependency and throws `CircularDependencyException`. Possible cause: classes depend on each other, for example class A depends on class B and class B depends on class A. Redesign the dependencies between classes. Use constructor injection or the `@Autowired` annotation carefully to avoid circular dependencies.

4. **BeanNotOfRequiredTypeException**

   - When you get a bean, `BeanNotOfRequiredTypeException` is thrown. It means the type of the bean does not match the expected type. Possible cause: the type you passed when getting the bean is different from the real type. Check the code that gets the bean and make sure the type is correct.

5. **BeanInitializationException**

   - During bean initialization, `BeanInitializationException` is thrown. It means an error happened during initialization. Possible causes: an exception in the initialization method, an unsatisfied dependency, or other reasons. Check the initialization method. Make sure it does not throw exceptions, or handle the exceptions.

6. **BeanCurrentlyInCreationException**

   - When you try to get a bean that is still being created, `BeanCurrentlyInCreationException` is thrown. Possible cause: a circular dependency, so the bean being created is not finished yet. Check the dependencies between classes and avoid circular dependencies.

7. **NoUniqueBeanDefinitionException**

   - When you get a bean by type and more than one bean matches, `NoUniqueBeanDefinitionException` is thrown. Possible cause: there are several beans of the same type and no bean name is given. Give the bean name, or use the `@Qualifier` annotation to remove the ambiguity.

8. **BeanDefinitionStoreException**

   - When bean definitions are loaded, `BeanDefinitionStoreException` is thrown. It means the bean definition could not be loaded correctly. Possible causes: a wrong format in the bean definition file, a path problem, and so on. Check the bean definition file and make sure the format and the path are correct.
