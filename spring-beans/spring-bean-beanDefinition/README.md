## BeanDefinition

- [BeanDefinition](#beandefinition)
  - [1. Background Knowledge](#1-background-knowledge)
  - [2. Basic Description](#2-basic-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementations](#5-main-implementations)
  - [6. Best Practices](#6-best-practices)
  - [7. Relationship with Other Components](#7-relationship-with-other-components)
  - [8. FAQ](#8-faq)


### 1. Background Knowledge

1. **`MetadataReader`**
   + `MetadataReader` is a Spring interface that reads the metadata of a class. It can scan class files and get basic information such as the class name and the class annotations. In annotation-driven development, `MetadataReader` is usually used to scan the classes in a package and extract annotation information from them, to configure Spring beans. [View](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-metadataReader)
2. **`AnnotationMetadata.introspect`**
   + The `introspect` method of the `AnnotationMetadata` interface analyzes the annotations of a class in depth. It helps us get the annotations on the class, on its methods, on its fields and so on. In Spring, `introspect` is usually used to parse classes marked with `@Component`, `@Configuration` and other annotations, to decide how they are instantiated and configured as Spring beans. [View](https://github.com/xuchengsheng/spring-reading/tree/master/spring-metadata/spring-metadata-annotationMetadata)

### 2. Basic Description

`BeanDefinition` is a key building block of the Spring Framework. It is configuration metadata that describes and defines the bean objects of an application in detail. This includes the class name, scope, dependencies, constructor arguments, property values, initialization method, destroy method and so on. With it, the Spring container can instantiate, configure and manage these beans exactly. With `BeanDefinition`, we can configure the components of an application flexibly. This supports core features such as dependency injection, AOP aspects and scope control. It promotes loosely coupled, maintainable and extensible application development.

### 3. Main Features

1. **Define the bean class**
   + Sets the class name of the bean to instantiate. It tells the Spring container which Java class to create an object of.
2. **Define the bean scope**
   + Lets us set the scope of the bean, such as singleton or prototype. This affects the lifecycle of the bean in the container.
3. **Constructor arguments and property values**
   + Lets us set the constructor arguments and property values of the bean, to pass arguments or set properties when the bean is instantiated.
4. **Define initialization and destroy methods**
   + Defines the initialization method and the destroy method of the bean, so that specific logic runs when the bean is created and destroyed.
5. **Lazy initialization of the bean**
   + Lets us set whether the bean is initialized lazily, that is, created on the first request.
6. **Dependencies**
   + Lets us set the dependencies between beans, so the other beans it depends on are injected correctly when the bean is created.
7. **Describe the bean role**
   + Lets us give the bean a role, usually an application bean, an infrastructure bean, a test bean and so on.
8. **Property overriding**
   + Lets us use the property overriding mechanism to override defined property values with different configuration sources (such as property files or environment variables).
9. **Bean annotations and metadata**
   + Can hold annotation information and metadata about the bean. This is useful for annotation-driven development.
10. **Create and register beans dynamically**
    + Lets us create and register beans dynamically at run time, not only through static configuration.

### 4. Interface Source

The `BeanDefinition` interface describes and configures the many aspects of a Spring bean. This includes the class name, scope, initialization and destroy methods, constructor arguments, property values and so on.

```java
/**
 * A BeanDefinition describes a bean instance, including its property values, constructor argument values and more information provided by concrete implementations.
 *
 * This is only a minimal interface: the main intent is to let a BeanFactoryPostProcessor inspect and change property values and other bean metadata.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2004-03-19
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

    /**
     * Scope identifier for the standard singleton scope: value.
     * Note that extended bean factories may support more scopes.
     * @see #setScope
     * @see ConfigurableBeanFactory#SCOPE_SINGLETON
     */
    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

    /**
     * Scope identifier for the standard prototype scope: value.
     * Note that extended bean factories may support more scopes.
     * @see #setScope
     * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
     */
    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

    /**
     * Role hint that a BeanDefinition is a major part of the application.
     * Usually it matches a user-defined bean.
     */
    int ROLE_APPLICATION = 0;

    /**
     * Role hint that a BeanDefinition is a supporting part of some larger configuration, usually an outer
     * org.springframework.beans.factory.parsing.ComponentDefinition.
     * When looked at in the context of a specific org.springframework.beans.factory.parsing.ComponentDefinition, these "SUPPORT" beans are considered important enough,
     * but they are not relevant when looking at the overall configuration of the application.
     */
    int ROLE_SUPPORT = 1;

    /**
     * Role hint that a BeanDefinition has a completely background role and is not relevant to the end user.
     * This hint is used when registering beans that belong entirely to the inner workings of a org.springframework.beans.factory.parsing.ComponentDefinition.
     */
    int ROLE_INFRASTRUCTURE = 2;

    /**
     * Set the name of the parent definition of this bean definition, if any.
     */
    void setParentName(@Nullable String parentName);

    /**
     * Return the name of the parent definition of this bean definition, if any.
     */
    @Nullable
    String getParentName();

    /**
     * Specify the bean class name of this bean definition.
     * The class name can be changed during bean factory post-processing, usually to replace the original class name with a resolved one.
     */
    void setBeanClassName(@Nullable String beanClassName);

    /**
     * Return the current bean class name of this bean definition.
     * Note that this is not necessarily the class name used at run time, for example when a child definition inherits the class name from its parent.
     * Also, the class called on a factory method may be empty.
     * So do not treat it as the definite bean type at run time. Use it only at the level of individual bean definitions, for resolution purposes.
     */
    @Nullable
    String getBeanClassName();

    /**
     * Override the target scope of this bean and specify a new scope name.
     * @see #SCOPE_SINGLETON
     * @see #SCOPE_PROTOTYPE
     */
    void setScope(@Nullable String scope);

    /**
     * Return the current target scope name of this bean, or null if it is not known yet.
     */
    @Nullable
    String getScope();

    /**
     * Set whether this bean should be lazily initialized.
     * If false, the bean is instantiated at startup by bean factories that handle singletons.
     */
    void setLazyInit(boolean lazyInit);

    /**
     * Return whether this bean should be lazily initialized, that is, not eagerly instantiated at startup. Only applies to singleton beans.
     */
    boolean isLazyInit();

    /**
     * Set the names of the beans that this bean depends on being initialized.
     * The bean factory guarantees that these beans are initialized first.
     */
    void setDependsOn(@Nullable String... dependsOn);

    /**
     * Return the names of the beans that this bean depends on.
     */
    @Nullable
    String[] getDependsOn();

    /**
     * Set whether this bean is a candidate for autowiring.
     * Note that this flag only affects type-based autowiring.
     * It does not affect explicit references by name. Name matching still injects the bean even if the specified bean is not marked as an autowire candidate.
     */
    void setAutowireCandidate(boolean autowireCandidate);

    /**
     * Return whether this bean is a candidate for autowiring.
     */
    boolean isAutowireCandidate();

    /**
     * Set whether this bean is a primary autowire candidate.
     * If exactly one of several matching candidates has this value set to true, it is used as a tie-breaker.
     */
    void setPrimary(boolean primary);

    /**
     * Return whether this bean is a primary autowire candidate.
     */
    boolean isPrimary();

    /**
     * Specify the factory bean to use, if any.
     * This is the name of the bean on which the specified factory method is called.
     * @see #setFactoryMethodName
     */
    void setFactoryBeanName(@Nullable String factoryBeanName);

    /**
     * Return the factory bean name, if any.
     */
    @Nullable
    String getFactoryBeanName();

    /**
     * Specify the factory method, if any. This method is called with the constructor arguments,
     * or with no arguments if none are specified.
     * The method is called on the specified factory bean, or on the local bean class if no factory bean is specified.
     * @see #setFactoryBeanName
     * @see #setBeanClassName
     */
    void setFactoryMethodName(@Nullable String factoryMethodName);

    /**
     * Return the factory method, if any.
     */
    @Nullable
    String getFactoryMethodName();

    /**
     * Return the constructor argument values of this bean.
     * The returned instance can be changed during bean factory post-processing.
     * @return the constructor argument values object (never null)
     */
    ConstructorArgumentValues getConstructorArgumentValues();

    /**
     * Return whether constructor argument values are defined for this bean definition.
     * @since 5.0.2
     */
    default boolean hasConstructorArgumentValues() {
        return !getConstructorArgumentValues().isEmpty();
    }

    /**
     * Return the property values to apply to a new instance of the bean.
     * The returned instance can be changed during bean factory post-processing.
     * @return the mutable property values object (never null)
     */
    MutablePropertyValues getPropertyValues();

    /**
     * Return whether property values are defined for this bean definition.
     * @since 5.0.2
     */
    default boolean hasPropertyValues() {
        return !getPropertyValues().isEmpty();
    }

    /**
     * Set the name of the initialization method.
     * @since 5.1
     */
    void setInitMethodName(@Nullable String initMethodName);

    /**
     * Return the name of the initialization method.
     * @since 5.1
     */
    @Nullable
    String getInitMethodName();

    /**
     * Set the name of the destroy method.
     * @since 5.1
     */
    void setDestroyMethodName(@Nullable String destroyMethodName);

    /**
     * Return the name of the destroy method.
     * @since 5.1
     */
    @Nullable
    String getDestroyMethodName();

    /**
     * Set the role hint of this BeanDefinition. The role hint gives frameworks and tools an indication of the role and importance of a particular BeanDefinition.
     * @since 5.1
     * @see #ROLE_APPLICATION
     * @see #ROLE_SUPPORT
     * @see #ROLE_INFRASTRUCTURE
     */
    void setRole(int role);

    /**
     * Get the role hint of this BeanDefinition. The role hint gives frameworks and tools an indication of the role and importance of a particular BeanDefinition.
     * @see #ROLE_APPLICATION
     * @see #ROLE_SUPPORT
     * @see #ROLE_INFRASTRUCTURE
     */
    int getRole();

    /**
     * Set a human-readable description of this BeanDefinition.
     * @since 5.1
     */
    void setDescription(@Nullable String description);

    /**
     * Return a human-readable description of this BeanDefinition.
     */
    @Nullable
    String getDescription();

    /**
     * Return the resolvable type of this bean definition, based on the bean class or other specific metadata.
     * This is usually fully resolved on a merged bean definition at run time, but not necessarily on an instance defined at configuration time.
     * @return the resolvable type (possibly ResolvableType#NONE)
     * @since 5.2
     * @see ConfigurableBeanFactory#getMergedBeanDefinition
     */
    ResolvableType getResolvableType();

    /**
     * Return whether this is a singleton, with a single shared instance returned on all calls.
     * @see #SCOPE_SINGLETON
     */
    boolean isSingleton();

    /**
     * Return whether this is a prototype, with an independent instance returned on every call.
     * @since 3.0
     * @see #SCOPE_PROTOTYPE
     */
    boolean isPrototype();

    /**
     * Return whether this bean is "abstract", that is, not meant to be instantiated.
     */
    boolean isAbstract();

    /**
 	 * Return a description of the resource that this bean definition came from (to show context when an error occurs).
	 */
    @Nullable
    String getResourceDescription();

    /**
     * Return the original bean definition, or {@code null} if there is none.
     * Allows retrieving the decorated bean definition, if any.
     * Note that this method returns the most direct origin. Walk the origin chain to find the original user-defined bean definition.
     */
    @Nullable
    BeanDefinition getOriginatingBeanDefinition();
    
}
```

### 5. Main Implementations

1. **`GenericBeanDefinition`**
   - Describes a general bean definition. It can be used for most kinds of beans.
   - Has flexible property configuration. You can set the class name, scope, initialization and destroy methods, constructor arguments, property values and so on.
   - Usually used when you configure beans by hand or need a custom bean definition.
2. **`RootBeanDefinition`**
   - Stands for an independent root-level bean definition. It usually defines a bean directly.
   - Can extend `GenericBeanDefinition`, and supports configuring other information about the bean class.
   - Usually used to define independent beans in the application.
3. **`ChildBeanDefinition`**
   - Stands for a derived child bean definition that inherits the configuration of its parent bean definition.
   - Usually used to create a variant of a bean. It inherits the configuration of the parent bean and overrides or changes part of it.
   - Has a reference to the parent bean.
4. **`AnnotatedGenericBeanDefinition`**
   - Used for annotation-based bean definitions, usually for scanning components and configuration classes.
   - Can stand for a bean defined with annotations. It supports class-level annotation configuration.
   - Usually used to scan and register components automatically.
5. **`ConfigurationClassBeanDefinition`**
   - Stands for the bean definition of a configuration class (`@Configuration` annotation). It is usually used in Spring configuration.
   - Stands for a configuration class as a bean definition. It supports configuration classes that contain other bean definitions.
   - Usually created automatically by the Spring container to support `@Configuration` configuration.
6. **`ScannedGenericBeanDefinition`**
   - Stands for the bean definition of a scanned bean. It is usually used for automatic component scanning.
   - Usually created during component scanning, to represent a component class that was found.
   - Usually used to register components automatically, such as classes with the `@Component` annotation.

~~~mermaid
classDiagram
    direction BT
    
    class BeanDefinition {
    	<<interface>>
    }
    
    class AnnotatedBeanDefinition {
    	<<interface>>
    }
    
    class AbstractBeanDefinition {
    	<<Abstract>>
    }

    class GenericBeanDefinition {
    }
    
    class RootBeanDefinition {
    }
    
    class ChildBeanDefinition {
    }
    
    class AnnotatedGenericBeanDefinition {
    }
    
    class ScannedGenericBeanDefinition {
    }
    
    class ConfigurationClassBeanDefinition {
    }
    
    
    
    AnnotatedBeanDefinition ..|> BeanDefinition
    AbstractBeanDefinition --|> BeanDefinition
    GenericBeanDefinition ..|> AbstractBeanDefinition
    
    RootBeanDefinition ..|> AbstractBeanDefinition
    ChildBeanDefinition ..|> AbstractBeanDefinition
	
	AnnotatedGenericBeanDefinition ..|> GenericBeanDefinition
	ScannedGenericBeanDefinition ..|> GenericBeanDefinition
	ConfigurationClassBeanDefinition ..|> RootBeanDefinition
~~~



### 6. Best Practices

This example creates a Spring container (`DefaultListableBeanFactory`). Then the `createBeanDefinition()` method creates and configures a custom bean definition (`ScannedGenericBeanDefinition`), with the scope, initialization method, destroy method, property values and so on. Next, it registers the bean definition in the container, gets the bean instance from the container, and prints the bean. At the end, it destroys the bean through the container, which calls its destroy method.

```java
public class BeanDefinitionDemo {
    public static void main(String[] args) throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("myBean", createBeanDefinition());

        // Get MyBean
        MyBean myChildBean = beanFactory.getBean("myBean", MyBean.class);
        // Print the bean
        System.out.println("MyBean = " + myChildBean);
        // Destroy myBean
        beanFactory.destroySingleton("myBean");
    }

    private static BeanDefinition createBeanDefinition() throws IOException {
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(MyBean.class.getName());

        ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition(metadataReader);
        beanDefinition.setScope("singleton");
        beanDefinition.setLazyInit(true);
        beanDefinition.setPrimary(true);
        beanDefinition.setAbstract(false);
        beanDefinition.setInitMethodName("init");
        beanDefinition.setDestroyMethodName("destroy");
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
        beanDefinition.setDescription("This is a custom bean definition");
        beanDefinition.setResourceDescription("com.xcs.spring.BeanDefinitionDemo");
        beanDefinition.getPropertyValues().add("name", "lex");
        beanDefinition.getPropertyValues().add("age", "18");
        return beanDefinition;
    }
}
```

`MyBean` is a simple Java class that represents a plain Java bean.

```java
public class MyBean {

    private String name;

    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void init(){
        System.out.println("execute com.xcs.spring.bean.MyBean.init");
    }

    public void destroy(){
        System.out.println("execute com.xcs.spring.bean.MyBean.destroy");
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
```

The result shows that the calls of the initialization and destroy methods and the property values depend on the matching settings in the `BeanDefinition`. `BeanDefinition` defines the metadata of a bean, so the Spring container can manage the lifecycle and properties of the bean correctly.

```java
execute com.xcs.spring.bean.MyBean.init
MyBean = MyBean{name='lex', age='18'}
execute com.xcs.spring.bean.MyBean.destroy
```

### 7. Relationship with Other Components

1. **`DefaultListableBeanFactory`**
   + It manages the creation, initialization and destruction of beans. `BeanDefinition` describes the metadata of a bean. `DefaultListableBeanFactory` uses `BeanDefinition` to create and manage bean instances.
2. **`BeanPostProcessor`**
   + An interface that intercepts the bean initialization process. It can process the bean after it is created and before and after initialization. The information in the `BeanDefinition` can be used in a `BeanPostProcessor`, for example to change the property values of the bean before initialization.
3. **`BeanDefinitionRegistry`**
   + An interface that registers and manages `BeanDefinition`s. It defines the methods to register and access them. `BeanFactory` and `ApplicationContext` implement the `BeanDefinitionRegistry` interface, so `BeanDefinition`s can be registered and retrieved through them.
4. **`BeanDefinitionReader`**
   + A tool that reads `BeanDefinition`s from external configuration files (such as `XML, YAML, Properties` files). It parses the external configuration into `BeanDefinition`s and registers them in the `BeanFactory`.

### 8. FAQ

1. **What is the role of `BeanDefinition`?**
   + Its main role is to define and configure the properties and behavior of a bean, so the Spring container can create, initialize and manage bean instances dynamically from this information.
2. **What is the lifecycle of `BeanDefinition`?**
   + It has the same lifecycle as the Spring container. It is registered and parsed when the container starts, and then used to create and manage the lifecycle of bean instances.
3. **How is a `BeanDefinition` registered?**
   + It is registered through an implementation of the `BeanDefinitionRegistry` interface (such as `DefaultListableBeanFactory`), or loaded and registered from an external configuration file through a `BeanDefinitionReader`.
4. **What properties does a `BeanDefinition` have?**
   + The properties include the class name, scope, initialization method, destroy method, property values and so on. The exact properties depend on the configuration needs of the bean.
5. **How can a `BeanDefinition` be changed?**
   + It can be changed by code after registration, for example by changing the property values, scope, initialization method or destroy method.
6. **What scopes does a `BeanDefinition` support?**
   + Singleton, Prototype, Request, Session and so on. Choose the right scope for your needs.
7. **What is the difference between registering and loading a `BeanDefinition`?**
   + Registering adds an already created `BeanDefinition` to the container. Loading reads the metadata of a bean from an external configuration file and registers it in the container.
8. **How do I use `BeanDefinition` for dependency injection?**
   + Set properties in the `BeanDefinition`, such as constructor arguments, property values and references to other beans. This gives you dependency injection.
9. **Can a `BeanDefinition` be created dynamically?**
   + Yes. It can be created at run time and registered in the container. This is useful in some complex cases, such as conditional bean registration.