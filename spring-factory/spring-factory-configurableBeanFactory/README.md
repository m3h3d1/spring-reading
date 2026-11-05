## ConfigurableBeanFactory

- [ConfigurableBeanFactory](#configurablebeanfactory)
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

`ConfigurableBeanFactory` is a sub-interface in Spring. It has methods to configure and customize a `BeanFactory` at runtime. With these methods, you can set the parent BeanFactory, the class loader, the expression resolver, the conversion service, and more, to fit the needs of your application.

### 3. Main Features

1. **Set the parent BeanFactory (`setParentBeanFactory`)**

   + Links the current `BeanFactory` to a parent `BeanFactory`. This gives bean inheritance and a hierarchy.

2. **Set the bean class loader (`setBeanClassLoader`)**

   + Sets the class loader used to load bean classes. Bean classes can then be loaded dynamically at runtime.

3. **Set a temporary class loader (`setTempClassLoader`)**

   + Sets a temporary class loader. It is used to resolve classes when needed and gives a more flexible class loading.

4. **Configure bean metadata caching (`setCacheBeanMetadata`)**

   + Sets whether to cache bean metadata, to improve performance.

5. **Set the bean expression resolver (`setBeanExpressionResolver`)**

   + Sets the resolver for SpEL expressions. This allows Spring Expression Language in bean definitions.

6. **Set the conversion service (`setConversionService`)**

   + Sets the ConversionService used for property type conversion. It affects type conversion during property injection.

7. **Register a PropertyEditorRegistrar (`addPropertyEditorRegistrar`)**

   + Registers a custom PropertyEditorRegistrar. It registers custom PropertyEditors that convert property values.

8. **Set the autowire candidate resolver (`setAutowireCandidateResolver`)**

   + Sets the resolver that decides which beans are autowire candidates. This allows custom autowiring rules.

9. **Set a scope alias (`setScopeAlias`)**

   + Sets an alias for a scope. This gives a more flexible scope configuration.

10. **Register a bean alias (`registerAlias`)**

    + Registers an alias for a bean name. The same bean can then be referred to by different names.

11. **Add a bean post-processor (`addBeanPostProcessor`)**

    + Registers a custom bean post-processor. It runs custom logic before and after bean initialization.

12. **Register a property editor (`registerCustomEditor`)**

    + Registers a custom property editor. It converts string values to a specific property type.

13. **Set the bean name generator (`setBeanNameGenerator`)**

    + Sets the generator used to create default bean names.

14. **Set the scope resolver (`setScopeResolver`)**

    + Sets the resolver used to resolve scopes. This allows custom scope handling.

15. **Ignore dependency interfaces (`ignoreDependencyInterface`)**

    + Sets the dependency interfaces to ignore, so that they are not autowired.

### 4. Interface Source

`ConfigurableBeanFactory` is a key interface in Spring to configure and customize bean factories. It can set the parent bean factory, configure the class loader, set the bean expression resolver, register custom property editors and post-processors, configure scopes, and more.

```java
/**
 * Configuration interface to be implemented by most bean factories. Provides
 * facilities to configure a bean factory, in addition to the bean factory
 * client methods in the {@link org.springframework.beans.factory.BeanFactory}
 * interface.
 *
 * <p>This bean factory interface is not meant to be used in normal application
 * code: Stick to {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * needs. This extended interface is just meant to allow for framework-internal
 * plug'n'play and for special access to bean factory configuration methods.
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.ListableBeanFactory
 * @see ConfigurableListableBeanFactory
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    /**
     * Scope identifier for the standard singleton scope: {@value}.
     * <p>Custom scopes can be added via {@code registerScope}.
     * @see #registerScope
     */
    String SCOPE_SINGLETON = "singleton";

    /**
     * Scope identifier for the standard prototype scope: {@value}.
     * <p>Custom scopes can be added via {@code registerScope}.
     * @see #registerScope
     */
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * Set the parent of this bean factory.
     * <p>Note that the parent cannot be changed: It should only be set outside
     * a constructor if it isn't available at the time of factory instantiation.
     * @param parentBeanFactory the parent BeanFactory
     * @throws IllegalStateException if this factory is already associated with
     * a parent BeanFactory
     * @see #getParentBeanFactory()
     */
    void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

    /**
     * Set the class loader to use for loading bean classes.
     * Default is the thread context class loader.
     * <p>Note that this class loader will only apply to bean definitions
     * that do not carry a resolved bean class yet. This is the case as of
     * Spring 2.0 by default: Bean definitions only carry bean class names,
     * to be resolved once the factory processes the bean definition.
     * @param beanClassLoader the class loader to use,
     * or {@code null} to suggest the default class loader
     */
    void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);

    /**
     * Return this factory's class loader for loading bean classes
     * (only {@code null} if even the system ClassLoader isn't accessible).
     * @see org.springframework.util.ClassUtils#forName(String, ClassLoader)
     */
    @Nullable
    ClassLoader getBeanClassLoader();

    /**
     * Specify a temporary ClassLoader to use for type matching purposes.
     * Default is none, simply using the standard bean ClassLoader.
     * <p>A temporary ClassLoader is usually just specified if
     * <i>load-time weaving</i> is involved, to make sure that actual bean
     * classes are loaded as lazily as possible. The temporary loader is
     * then removed once the BeanFactory completes its bootstrap phase.
     * @since 2.5
     */
    void setTempClassLoader(@Nullable ClassLoader tempClassLoader);

    /**
     * Return the temporary ClassLoader to use for type matching purposes,
     * if any.
     * @since 2.5
     */
    @Nullable
    ClassLoader getTempClassLoader();

    /**
     * Set whether to cache bean metadata such as given bean definitions
     * (in merged fashion) and resolved bean classes. Default is on.
     * <p>Turn this flag off to enable hot-refreshing of bean definition objects
     * and in particular bean classes. If this flag is off, any creation of a bean
     * instance will re-query the bean class loader for newly resolved classes.
     */
    void setCacheBeanMetadata(boolean cacheBeanMetadata);

    /**
     * Return whether to cache bean metadata such as given bean definitions
     * (in merged fashion) and resolved bean classes.
     */
    boolean isCacheBeanMetadata();

    /**
     * Specify the resolution strategy for expressions in bean definition values.
     * <p>There is no expression support active in a BeanFactory by default.
     * An ApplicationContext will typically set a standard expression strategy
     * here, supporting "#{...}" expressions in a Unified EL compatible style.
     * @since 3.0
     */
    void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver);

    /**
     * Return the resolution strategy for expressions in bean definition values.
     * @since 3.0
     */
    @Nullable
    BeanExpressionResolver getBeanExpressionResolver();

    /**
     * Specify a Spring 3.0 ConversionService to use for converting
     * property values, as an alternative to JavaBeans PropertyEditors.
     * @since 3.0
     */
    void setConversionService(@Nullable ConversionService conversionService);

    /**
     * Return the associated ConversionService, if any.
     * @since 3.0
     */
    @Nullable
    ConversionService getConversionService();

    /**
     * Add a PropertyEditorRegistrar to be applied to all bean creation processes.
     * <p>Such a registrar creates new PropertyEditor instances and registers them
     * on the given registry, fresh for each bean creation attempt. This avoids
     * the need for synchronization on custom editors; hence, it is generally
     * preferable to use this method instead of {@link #registerCustomEditor}.
     * @param registrar the PropertyEditorRegistrar to register
     */
    void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

    /**
     * Register the given custom property editor for all properties of the
     * given type. To be invoked during factory configuration.
     * <p>Note that this method will register a shared custom editor instance;
     * access to that instance will be synchronized for thread-safety. It is
     * generally preferable to use {@link #addPropertyEditorRegistrar} instead
     * of this method, to avoid for the need for synchronization on custom editors.
     * @param requiredType type of the property
     * @param propertyEditorClass the {@link PropertyEditor} class to register
     */
    void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

    /**
     * Initialize the given PropertyEditorRegistry with the custom editors
     * that have been registered with this BeanFactory.
     * @param registry the PropertyEditorRegistry to initialize
     */
    void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

    /**
     * Set a custom type converter that this BeanFactory should use for converting
     * bean property values, constructor argument values, etc.
     * <p>This will override the default PropertyEditor mechanism and hence make
     * any custom editors or custom editor registrars irrelevant.
     * @since 2.5
     * @see #addPropertyEditorRegistrar
     * @see #registerCustomEditor
     */
    void setTypeConverter(TypeConverter typeConverter);

    /**
     * Obtain a type converter as used by this BeanFactory. This may be a fresh
     * instance for each call, since TypeConverters are usually <i>not</i> thread-safe.
     * <p>If the default PropertyEditor mechanism is active, the returned
     * TypeConverter will be aware of all custom editors that have been registered.
     * @since 2.5
     */
    TypeConverter getTypeConverter();

    /**
     * Add a String resolver for embedded values such as annotation attributes.
     * @param valueResolver the String resolver to apply to embedded values
     * @since 3.0
     */
    void addEmbeddedValueResolver(StringValueResolver valueResolver);

    /**
     * Determine whether an embedded value resolver has been registered with this
     * bean factory, to be applied through {@link #resolveEmbeddedValue(String)}.
     * @since 4.3
     */
    boolean hasEmbeddedValueResolver();

    /**
     * Resolve the given embedded value, e.g. an annotation attribute.
     * @param value the value to resolve
     * @return the resolved value (may be the original value as-is)
     * @since 3.0
     */
    @Nullable
    String resolveEmbeddedValue(String value);

    /**
     * Add a new BeanPostProcessor that will get applied to beans created
     * by this factory. To be invoked during factory configuration.
     * <p>Note: Post-processors submitted here will be applied in the order of
     * registration; any ordering semantics expressed through implementing the
     * {@link org.springframework.core.Ordered} interface will be ignored. Note
     * that autodetected post-processors (e.g. as beans in an ApplicationContext)
     * will always be applied after programmatically registered ones.
     * @param beanPostProcessor the post-processor to register
     */
    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    /**
     * Return the current number of registered BeanPostProcessors, if any.
     */
    int getBeanPostProcessorCount();

    /**
     * Register the given scope, backed by the given Scope implementation.
     * @param scopeName the scope identifier
     * @param scope the backing Scope implementation
     */
    void registerScope(String scopeName, Scope scope);

    /**
     * Return the names of all currently registered scopes.
     * <p>This will only return the names of explicitly registered scopes.
     * Built-in scopes such as "singleton" and "prototype" won't be exposed.
     * @return the array of scope names, or an empty array if none
     * @see #registerScope
     */
    String[] getRegisteredScopeNames();

    /**
     * Return the Scope implementation for the given scope name, if any.
     * <p>This will only return explicitly registered scopes.
     * Built-in scopes such as "singleton" and "prototype" won't be exposed.
     * @param scopeName the name of the scope
     * @return the registered Scope implementation, or {@code null} if none
     * @see #registerScope
     */
    @Nullable
    Scope getRegisteredScope(String scopeName);

    /**
     * Set the {@code ApplicationStartup} for this bean factory.
     * <p>This allows the application context to record metrics
     * during application startup.
     * @param applicationStartup the new application startup
     * @since 5.3
     */
    void setApplicationStartup(ApplicationStartup applicationStartup);

    /**
     * Return the {@code ApplicationStartup} for this bean factory.
     * @since 5.3
     */
    ApplicationStartup getApplicationStartup();

    /**
     * Provides a security access control context relevant to this factory.
     * @return the applicable AccessControlContext (never {@code null})
     * @since 3.0
     */
    AccessControlContext getAccessControlContext();

    /**
     * Copy all relevant configuration from the given other factory.
     * <p>Should include all standard configuration settings as well as
     * BeanPostProcessors, Scopes, and factory-specific internal settings.
     * Should not include any metadata of actual bean definitions,
     * such as BeanDefinition objects and bean name aliases.
     * @param otherFactory the other BeanFactory to copy from
     */
    void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

   
	/**
     * Given a bean name, create an alias. We typically use this method to
     * support names that are illegal within XML ids (used for bean names).
     * <p>Typically invoked during factory configuration, but can also be
     * used for runtime registration of aliases. Therefore, a factory
     * implementation should synchronize alias access.
     *
     * @param beanName the canonical name of the target bean
     * @param alias the alias to be registered for the bean
     * @throws BeanDefinitionStoreException if the alias is already in use
     */
    void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

    /**
     * Resolve all alias target names and aliases registered in this
     * factory, applying the given StringValueResolver to them.
     * <p>The value resolver may for example resolve placeholders
     * in target bean names and even in alias names.
     *
     * @param valueResolver the StringValueResolver to apply
     * @since 2.5
     */
    void resolveAliases(StringValueResolver valueResolver);

    /**
     * Return a merged BeanDefinition for the given bean name,
     * merging a child bean definition with its parent if necessary.
     * Considers bean definitions in ancestor factories as well.
     *
     * @param beanName the name of the bean to retrieve the merged definition for
     * @return a (potentially merged) BeanDefinition for the given bean
     * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
     * @since 2.5
     */
    BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    /**
     * Determine whether the bean with the given name is a FactoryBean.
     *
     * @param name the name of the bean to check
     * @return whether the bean is a FactoryBean
     * ({@code false} means the bean exists but is not a FactoryBean)
     * @throws NoSuchBeanDefinitionException if there is no bean with the given name
     * @since 2.5
     */
    boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

    /**
     * Explicitly control the current in-creation status of the specified bean.
     * For container-internal use only.
     *
     * @param beanName the name of the bean
     * @param inCreation whether the bean is currently in creation
     * @since 3.1
     */
    void setCurrentlyInCreation(String beanName, boolean inCreation);

    /**
     * Determine whether the specified bean is currently in creation.
     *
     * @param beanName the name of the bean
     * @return whether the bean is currently in creation
     * @since 2.5
     */
    boolean isCurrentlyInCreation(String beanName);

    /**
     * Register a dependent bean for the given bean,
     * to be destroyed before the given bean is destroyed.
     *
     * @param beanName the name of the bean
     * @param dependentBeanName the name of the dependent bean
     * @since 2.5
     */
    void registerDependentBean(String beanName, String dependentBeanName);

    /**
     * Return the names of all beans which depend on the specified bean, if any.
     *
     * @param beanName the name of the bean
     * @return the array of dependent bean names, or an empty array if none
     * @since 2.5
     */
    String[] getDependentBeans(String beanName);

    /**
     * Return the names of all beans that the specified bean depends on, if any.
     *
     * @param beanName the name of the bean
     * @return the array of names of beans which the bean depends on,
     * or an empty array if none
     * @since 2.5
     */
    String[] getDependenciesForBean(String beanName);

    /**
     * Destroy the given bean instance (usually a prototype instance
     * obtained from this factory) according to its bean definition.
     * <p>Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     *
     * @param beanName the name of the bean definition
     * @param beanInstance the bean instance to destroy
     */
    void destroyBean(String beanName, Object beanInstance);

    /**
     * Destroy the specified scoped bean in the current target scope, if any.
     * <p>Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     *
     * @param beanName the name of the scoped bean
     */
    void destroyScopedBean(String beanName);

    /**
     * Destroy all singleton beans in this factory, including inner beans that have
     * been registered as disposable. To be called on shutdown of a factory.
     * <p>Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     */
    void destroySingletons();
    
}
```

### 5. Best Practices

This example shows some common methods of the `ConfigurableBeanFactory` interface. It covers setting the parent BeanFactory, getting the number of BeanPostProcessors, registering aliases, handling dependencies, and more.

```java
public class ConfigurableBeanFactoryDemo {

    public static void main(String[] args) {
        // Create the ConfigurableBeanFactory
        ConfigurableBeanFactory configurableBeanFactory = new AnnotationConfigApplicationContext(MyConfiguration.class).getBeanFactory();

        // Set the parent BeanFactory
        configurableBeanFactory.setParentBeanFactory(new DefaultListableBeanFactory());

        // Get the number of BeanPostProcessors
        int beanPostProcessorCount = configurableBeanFactory.getBeanPostProcessorCount();
        System.out.println("BeanPostProcessor count: " + beanPostProcessorCount);

        // Get the names of all registered scopes
        String[] scopeNames = configurableBeanFactory.getRegisteredScopeNames();
        System.out.println("Registered scope names: " + String.join(", ", scopeNames));

        // Get a registered scope
        Scope customScope = configurableBeanFactory.getRegisteredScope("customScope");
        System.out.println("Registered scope: " + customScope);

        // Get the ApplicationStartup
        ApplicationStartup applicationStartup = configurableBeanFactory.getApplicationStartup();
        System.out.println("ApplicationStartup: " + applicationStartup);

        // Get the AccessControlContext
        AccessControlContext accessControlContext = configurableBeanFactory.getAccessControlContext();
        System.out.println("AccessControlContext: " + accessControlContext);

        // Copy the configuration
        ConfigurableListableBeanFactory otherFactory = new DefaultListableBeanFactory();
        configurableBeanFactory.copyConfigurationFrom(otherFactory);
        System.out.println("copyConfigurationFrom: " + otherFactory);

        // Register an alias
        String beanName = "myService";
        String alias = "helloService";
        configurableBeanFactory.registerAlias(beanName, alias);
        System.out.println("registerAlias, beanName: " + beanName + ", alias: " + alias);

        // Get the merged BeanDefinition
        BeanDefinition mergedBeanDefinition = configurableBeanFactory.getMergedBeanDefinition("myService");
        System.out.println("Merged BeanDefinition: " + mergedBeanDefinition);

        // Check if the bean is a FactoryBean
        String factoryBeanName = "myService";
        boolean isFactoryBean = configurableBeanFactory.isFactoryBean(factoryBeanName);
        System.out.println("Is FactoryBean: " + isFactoryBean);

        // Mark the bean as currently in creation
        String currentBeanName = "myService";
        boolean inCreation = true;
        configurableBeanFactory.setCurrentlyInCreation(currentBeanName, inCreation);
        System.out.println("setCurrentlyInCreation: " + currentBeanName);

        // Check if the bean is currently in creation
        boolean isCurrentlyInCreation = configurableBeanFactory.isCurrentlyInCreation(currentBeanName);
        System.out.println("Is currently in creation: " + isCurrentlyInCreation);

        // Register a dependency
        String dependentBeanName = "dependentBean";
        configurableBeanFactory.registerDependentBean(beanName, dependentBeanName);
        System.out.println("registerDependentBean, beanName: " + beanName + ", dependentBeanName: " + dependentBeanName);

        // Get the names of all beans that depend on the bean
        String[] dependentBeans = configurableBeanFactory.getDependentBeans(beanName);
        System.out.println("Dependent beans: " + String.join(", ", dependentBeans));

        // Get the names of all beans the bean depends on
        String[] dependencies = configurableBeanFactory.getDependenciesForBean(beanName);
        System.out.println("Dependencies: " + String.join(", ", dependencies));

        // Destroy the bean instance
        Object beanInstance = configurableBeanFactory.getBean(beanName);
        configurableBeanFactory.destroyBean(beanName, beanInstance);
        System.out.println("destroyBean: " + beanName);

        // Destroy all singleton beans
        configurableBeanFactory.destroySingletons();
        System.out.println("destroySingletons");
    }

}
```

### 6. Relation to Other Components

1. **BeanFactory**

   - `ConfigurableBeanFactory` extends the `BeanFactory` interface. So it has the basic features of `BeanFactory`. It adds more configuration and management features.

2. **HierarchicalBeanFactory**

   - `ConfigurableBeanFactory` extends the `HierarchicalBeanFactory` interface. So it supports a hierarchy. You can set a parent `BeanFactory` and build a container hierarchy.

3. **SingletonBeanRegistry**

   - `ConfigurableBeanFactory` extends the `SingletonBeanRegistry` interface. So it can register and manage singleton objects. This includes creating, getting, and destroying singletons.

4. **BeanPostProcessor**

   - `ConfigurableBeanFactory` lets you register `BeanPostProcessor` implementations. They can step in when bean instances are created and initialized in the container. With the `addBeanPostProcessor` method, you register your own `BeanPostProcessor`.

5. **Scope**

   - `ConfigurableBeanFactory` has methods to register and get scopes. You register a custom scope with `registerScope`, and get a registered scope with `getRegisteredScope`.

6. **ConversionService**

   - `ConfigurableBeanFactory` lets you set and get a `ConversionService`. It converts the types of property values. With `setConversionService` and `getConversionService`, you configure and get your own conversion service.

7. **BeanExpressionResolver**

   - `ConfigurableBeanFactory` lets you set and get a `BeanExpressionResolver`. It resolves expressions in bean definitions. With `setBeanExpressionResolver` and `getBeanExpressionResolver`, you configure and get the expression resolver.

8. **TypeConverter**

   - `ConfigurableBeanFactory` lets you set and get a `TypeConverter`. It converts the types of bean property values. With `setTypeConverter` and `getTypeConverter`, you configure and get the type converter.

9. **StringValueResolver**

   - `ConfigurableBeanFactory` lets you add and check string value resolvers. They resolve embedded values in bean definitions. You use the `addEmbeddedValueResolver` and `hasEmbeddedValueResolver` methods.

10. **PropertyEditorRegistrar**

    - `ConfigurableBeanFactory` lets you add a `PropertyEditorRegistrar`. It registers custom `PropertyEditor` objects. You use the `addPropertyEditorRegistrar` method.

11. **ApplicationStartup**

    - `ConfigurableBeanFactory` lets you set and get an `ApplicationStartup`. It records metrics during application startup. With `setApplicationStartup` and `getApplicationStartup`, you configure and get the startup metrics instance.

12. **AccessControlContext**

    - `ConfigurableBeanFactory` can return the security access control context of the factory. You use the `getAccessControlContext` method.

13. **AliasRegistry**

    - `ConfigurableBeanFactory` extends the `AliasRegistry` interface. So it can also register and resolve aliases. You use the `registerAlias` and `resolveAliases` methods.

14. **ConfigurableListableBeanFactory**

    - `ConfigurableBeanFactory` is a super-interface of `ConfigurableListableBeanFactory`. Besides configuration and management, `ConfigurableListableBeanFactory` also lets you query bean definitions, including merged bean definitions.

### 7. Common Problems

1. **What does the ConfigurableBeanFactory interface do?**

   - `ConfigurableBeanFactory` is one of the configuration interfaces of the IoC container in Spring. It extends `BeanFactory` and has many methods to configure and manage the IoC container, such as setting the parent container, registering scopes, and adding a BeanPostProcessor.

2. **What is the difference between ConfigurableBeanFactory and BeanFactory?**

   - `ConfigurableBeanFactory` extends `BeanFactory` and has more configuration and management methods. With `ConfigurableBeanFactory`, you can set the parent container, register scopes, add a BeanPostProcessor, and more. `BeanFactory` only gives basic bean access and type checks.

3. **How do I set the parent container of a ConfigurableBeanFactory?**

   - Use the `setParentBeanFactory` method. It builds a container hierarchy, so parent and child containers can share resources and manage dependencies.

4. **What is a Scope in ConfigurableBeanFactory used for?**

   - A Scope defines and manages the scope of beans. The scope decides the lifecycle and visibility of a bean. For example, the singleton scope means one bean instance in the container. The prototype scope means a new instance every time you get the bean.

5. **How do I register a custom Scope?**

   - Use the `registerScope` method. It adds your own scope implementation, and you can use it in the container.

6. **What is a BeanPostProcessor in ConfigurableBeanFactory used for?**

   - `BeanPostProcessor` works when a bean is created and initialized. It lets you customize and step in. Use the `addBeanPostProcessor` method to register your own `BeanPostProcessor` implementation.

7. **How do I set the ClassLoader of a ConfigurableBeanFactory?**

   - Use the `setBeanClassLoader` method. This ClassLoader loads the bean classes. You can set a specific ClassLoader or use the default thread context ClassLoader.

8. **What is the ConversionService in ConfigurableBeanFactory used for?**

   - `ConversionService` converts the types of bean property values. Use the `setConversionService` method to set your own conversion service.

9. **How do I configure the BeanExpressionResolver in ConfigurableBeanFactory?**

   - Use the `setBeanExpressionResolver` method. This resolver resolves expressions in bean definitions, such as Spring EL expressions.

10. **What is the TypeConverter in ConfigurableBeanFactory used for?**

    - `TypeConverter` converts the types of bean property values. Use the `setTypeConverter` method to set your own type converter.

11. **How do I register a PropertyEditorRegistrar in ConfigurableBeanFactory?**

    - Use the `addPropertyEditorRegistrar` method. This registrar registers custom `PropertyEditor` objects. It handles the creation and registration of property editors.

12. **What is the ApplicationStartup in ConfigurableBeanFactory used for?**

    - `ApplicationStartup` records metrics during application startup. Use the `setApplicationStartup` method to set your own `ApplicationStartup` instance.

13. **What does the destroySingletons method in ConfigurableBeanFactory do?**

    - The `destroySingletons` method destroys all singleton beans, including inner beans registered as disposable. It is usually called when the container shuts down, to release resources and clean up.
