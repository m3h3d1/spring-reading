## BeanDefinitionHolder

- [BeanDefinitionHolder](#beandefinitionholder)
  - [1. Background Knowledge](#1-background-knowledge)
  - [2. Basic Description](#2-basic-description)
  - [3. Main Features](#3-main-features)
  - [4. Interface Source](#4-interface-source)
  - [5. Main Implementations](#5-main-implementations)
  - [6. Best Practices](#6-best-practices)
  - [7. Relationship with Other Components](#7-relationship-with-other-components)
  - [8. FAQ](#8-faq)


### 1. Background Knowledge

1. **Spring Framework basics**
   + You should know the basic concepts of the Spring Framework. This includes what a bean is, what a `BeanDefinition` is, and how the Spring container manages and instantiates beans.
2. **Understanding `BeanDefinition`**
   + You should know what a `BeanDefinition` is and what it holds, such as the class name, scope, properties, initialization method and destroy method of a bean. This is the base for understanding `BeanDefinitionHolder`.
3. **Spring IoC container**
   + You should know the Spring IoC (Inversion of Control) container. This includes how to configure the container, how to load `BeanDefinition`s, and how to get bean instances from the container.

### 2. Basic Description

`BeanDefinitionHolder` is a class in the Spring Framework. It holds a `BeanDefinition` object and the names linked to it. It is mostly used inside Spring to manage and handle `BeanDefinition`s.

### 3. Main Features

1. **Holds a `BeanDefinition` object**
   + `BeanDefinitionHolder` mainly holds a `BeanDefinition` object. It has the metadata of a bean, such as class name, scope, properties and initialization method.
2. **Linked to names**
   + `BeanDefinitionHolder` is linked to the name of a bean, so a specific `BeanDefinition` can be identified and handled easily.
3. **Wraps the `BeanDefinition`**
   + It wraps the `BeanDefinition` and its names into one unit. This helps to manage and handle the `BeanDefinition` better.
4. **Provides extra metadata**
   + Besides the `BeanDefinition` and the name, `BeanDefinitionHolder` can hold other metadata, such as the original source of the `BeanDefinition` (usually a Resource object) and its description.
5. **Used to register and manage `BeanDefinition`s**
   + `BeanDefinitionHolder` can be used with the `BeanDefinitionRegistry` interface to register, change and manage `BeanDefinition`s in the Spring container.

### 4. Interface Source

 `BeanDefinitionHolder` is usually used inside Spring to register and handle `BeanDefinition`s. It provides methods to access the `BeanDefinition`, the bean name and the aliases.

```java
public class BeanDefinitionHolder implements BeanMetadataElement {

    // The wrapped BeanDefinition object
    private final BeanDefinition beanDefinition; 
    
    // The name of the bean
    private final String beanName;
    
    // The array of aliases
    @Nullable
    private final String[] aliases; 

    /**
     * Create a new BeanDefinitionHolder.
     * @param beanDefinition the BeanDefinition to wrap
     * @param beanName the name of the bean, as specified in the bean definition
     */
    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
        this(beanDefinition, beanName, null);
    }

    /**
     * Create a new BeanDefinitionHolder.
     * @param beanDefinition the BeanDefinition to wrap
     * @param beanName the name of the bean, as specified in the bean definition
     * @param aliases aliases of the bean, or null if there are none
     */
    public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, @Nullable String[] aliases) {
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        Assert.notNull(beanName, "Bean name must not be null");
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
        this.aliases = aliases;
    }

    /**
     * Copy constructor: create a new BeanDefinitionHolder with the same content as the given BeanDefinitionHolder instance.
     * Note: the wrapped BeanDefinition reference is kept as is. No deep copy is made.
     * @param beanDefinitionHolder the BeanDefinitionHolder instance to copy
     */
    public BeanDefinitionHolder(BeanDefinitionHolder beanDefinitionHolder) {
        Assert.notNull(beanDefinitionHolder, "BeanDefinitionHolder must not be null");
        this.beanDefinition = beanDefinitionHolder.getBeanDefinition();
        this.beanName = beanDefinitionHolder.getBeanName();
        this.aliases = beanDefinitionHolder.getAliases();
    }

    /**
     * Return the wrapped BeanDefinition.
     */
    public BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }

    /**
     * Return the primary name of the bean, as specified in the bean definition.
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Return the array of aliases of the bean, as specified directly in the bean definition.
     * @return the array of aliases, or null if there are none
     */
    @Nullable
    public String[] getAliases() {
        return this.aliases;
    }

    /**
     * Expose the source object of the bean definition.
     * @see BeanDefinition#getSource()
     */
    @Override
    @Nullable
    public Object getSource() {
        return this.beanDefinition.getSource();
    }

    /**
     * Determine whether the given candidate name matches the bean name or an alias stored in this bean definition.
     */
    public boolean matchesName(@Nullable String candidateName) {
        return (candidateName != null && (candidateName.equals(this.beanName) ||
                candidateName.equals(BeanFactoryUtils.transformedBeanName(this.beanName)) ||
                ObjectUtils.containsElement(this.aliases, candidateName)));
    }
    
    // ... [code omitted for brevity]
}
```

### 5. Main Implementations

`BeanDefinitionHolder` is not an interface and needs no other implementation class. It is a concrete class that wraps a `BeanDefinition` object, a bean name and aliases, so the `BeanDefinition` can be managed and handled better.

### 6. Best Practices

This example uses `BeanDefinitionHolder` and the Spring IoC container. It creates a `BeanDefinition` for a bean named "`myBean`", gives it the aliases "`myBeanX`" and "`myBeanY`", and registers the `BeanDefinition` in the container. Then the `getBean` method of the container retrieves the bean by these names. This checks that the registration and the aliases are correct.

```java
public class BeanDefinitionHolderDemo {

    public static void main(String[] args) {
        // Create a DefaultListableBeanFactory, an implementation of BeanDefinitionRegistry
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // Create a new BeanDefinition
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(MyBean.class);

        // Bean name
        String beanName = "myBean";
        // Set aliases
        String[] aliases = {"myBeanX", "myBeanY"};
        // Create a BeanDefinitionHolder to link the BeanDefinition with its name
        BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName, aliases);

        // Register the BeanDefinitionHolder with BeanDefinitionReaderUtils
        BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, beanFactory);

        System.out.println("myBean = " + beanFactory.getBean("myBean"));
        System.out.println("myBeanX = " + beanFactory.getBean("myBeanX"));
        System.out.println("myBeanY = " + beanFactory.getBean("myBeanY"));
    }
}
```

`MyBean` is a simple Java class that represents a plain Java bean.

```java
public class MyBean {

}
```

The result shows that several names can refer to the same bean definition, and in the end we get the same bean instance.

```java
myBean = com.xcs.spring.bean.MyBean@5bcea91b
myBeanX = com.xcs.spring.bean.MyBean@5bcea91b
myBeanY = com.xcs.spring.bean.MyBean@5bcea91b
```

### 7. Relationship with Other Components

1. **`BeanDefinitionReaderUtils`** 
   + Spring provides a utility class, `BeanDefinitionReaderUtils`, to help register `BeanDefinition`s. It is usually used together with `BeanDefinitionHolder`. The `BeanDefinitionReaderUtils.registerBeanDefinition` method can register a `BeanDefinitionHolder` in a `BeanDefinitionRegistry`.
2. **`BeanDefinitionParser` and custom parsers**
   + In Spring XML configuration, a `BeanDefinition` can be parsed and registered in the container by a custom `BeanDefinitionParser` or `NamespaceHandler`. These parsers usually wrap the `BeanDefinition` in a `BeanDefinitionHolder` and register it in the container.
3. **Component scanners**
   + Spring provides component scanners such as `ClassPathBeanDefinitionScanner` and `AnnotatedBeanDefinitionReader`. They scan the classpath or annotated classes and register them as `BeanDefinition`s. These scanners usually wrap the scanned `BeanDefinition` in a `BeanDefinitionHolder` and register it in the container.
4. **@Import and related mechanisms**
   + In Spring, the `@Import` annotation and the related `ImportBeanDefinitionRegistrar` and `ImportSelector` interfaces import the `BeanDefinition`s of other configuration classes. In this process, `BeanDefinitionHolder` may wrap the imported `BeanDefinition` and register it in the container.

### 8. FAQ

1. **How do I use `BeanDefinitionHolder`?**
   - `BeanDefinitionHolder` is usually used inside Spring to register and handle `BeanDefinition`s. Application developers normally do not need to use it directly (unless you build frameworks). It can be used automatically in Spring configuration, such as custom `BeanDefinition` parsers, component scanning and @Import.
2. **What is the relationship between `BeanDefinitionHolder` and `BeanDefinition`?**
   - `BeanDefinitionHolder` is a wrapper. It holds a `BeanDefinition` object and the related name and aliases. It links the `BeanDefinition` with its names, so the `BeanDefinition` can be managed and handled better.
3. **How do I set aliases for a `BeanDefinition`?**
   - Aliases are set by passing a string array to the `BeanDefinitionHolder` constructor. When the `BeanDefinitionHolder` is registered, the aliases are registered with the `BeanDefinition`. So different names refer to the same `BeanDefinition`.
4. **Can I change the aliases of a bean at run time?**
   - Usually, once the `BeanDefinition` is registered in the container, the aliases are fixed and cannot be changed at run time. To change them, you usually need to register the `BeanDefinition` again with different aliases.
5. **How do I check whether two beans have the same alias?**
   - Use the `matchesName` method of `BeanDefinitionHolder` to check whether a candidate name matches the name or an alias of the bean. The method checks the given name against the bean name and all aliases.
6. **What are aliases for?**
   - Aliases give more flexibility. You can refer to the same bean by different names. This is useful when different components, modules or configurations need to access the bean definition by different names.
7. **How do I handle several `BeanDefinition`s with the same name?**
   - If several `BeanDefinition`s have the same name, usually only the last registered one takes effect. This can cause bean overriding problems, so keep bean names unique in your configuration.