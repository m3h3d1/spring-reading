## ClassPathXmlApplicationContext

- [ClassPathXmlApplicationContext](#classpathxmlapplicationcontext)
  - [1. Overview](#1-overview)
  - [2. Background Knowledge](#2-background-knowledge)
  - [3. Basic Description](#3-basic-description)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. **XmlBeanDefinitionReader**

   + The [XmlBeanDefinitionReader](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-xmlBeanDefinitionReader/README.md) is a Spring class that loads and parses XML bean definition files. It turns the bean metadata in the file into internal bean definition objects. This is how the IoC container is built. The reader reads the XML file, parses the bean definitions (ID, class name, properties, dependencies and so on), and registers them in the application context. This makes it easy to configure and manage the beans of an application.

### 3. Basic Description

`ClassPathXmlApplicationContext` loads XML configuration files from the classpath and initializes the Spring container. It is one of the implementations of the `ApplicationContext` interface. It reads the configuration files, parses the configuration, and then creates and manages the bean instances in the Spring container.

### 4. Main Features

1. **Load configuration files**

   + Loads the given XML configuration files. These files hold the definitions, dependencies and settings of the components (beans) in the application.

2. **Container initialization**

   + When `ClassPathXmlApplicationContext` is instantiated, it reads and parses the configuration files, and then initializes the Spring container. This includes creating and managing bean instances and resolving dependencies between beans.

3. **Get bean instances**

   + With the `getBean` method of the container, you can get the bean instances defined in the configuration files.

4. **IoC (inversion of control)**

   + `ClassPathXmlApplicationContext` is an implementation of the IoC container. It manages and controls the lifecycle of components. When the container is initialized, it instantiates and wires beans from the configuration, instead of the application code creating the objects directly.

5. **Dependency injection**

   + The container reads the configuration and resolves dependencies between beans automatically. A bean declared in the configuration file can get the other beans it depends on through property injection or constructor injection.

6. **AOP (aspect-oriented programming)**

   + `ClassPathXmlApplicationContext` supports defining aspects and advice in the configuration files. This separates cross-cutting concerns and keeps the application clearer and more modular.

7. **Event propagation**

   + The Spring container supports an event mechanism. `ClassPathXmlApplicationContext` can publish events that happen in the application, so other components can listen and respond.


### 5. Best Practices

The `ClassPathXmlApplicationContext` constructor creates a Spring container and loads the "beans.xml" configuration file from the classpath. Then the `getBean` method gets the bean defined in the file by its type (`MyBean.class`), and the program prints it.

```java
public class ClassPathXmlApplicationContextDemo {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:beans.xml");
        System.out.println("MyBean = " + context.getBean(MyBean.class));
    }
}
```

This loads the content of the resource file "`classpath:beans.xml`". In our example, the file defines a Spring bean named "`myBean`". The bean has a property "message" with the value "Hello World".

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="myBean" class="com.xcs.spring.bean.MyBean">
        <property name="message" value="Hello World"/>
    </bean>

</beans>
```

`MyBean` is a simple Java class that represents a plain Java bean.

```java
public class MyBean {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: ClassPathXmlApplicationContext Sequence Diagram
ClassPathXmlApplicationContextDemo->>ClassPathXmlApplicationContext:ClassPathXmlApplicationContext(configLocation)
Note over ClassPathXmlApplicationContextDemo,ClassPathXmlApplicationContext: Create a ClassPathXmlApplicationContext instance

ClassPathXmlApplicationContext->>ClassPathXmlApplicationContext:ClassPathXmlApplicationContext(configLocations,refresh,parent)
Note over ClassPathXmlApplicationContext: Construct the instance with config file paths, a refresh flag and a parent context

ClassPathXmlApplicationContext->>AbstractApplicationContext:refresh()
Note over ClassPathXmlApplicationContext,AbstractApplicationContext: Refresh the application context

AbstractApplicationContext->>AbstractApplicationContext:obtainFreshBeanFactory()
Note over AbstractApplicationContext: Get the refreshed bean factory

AbstractApplicationContext->>AbstractRefreshableApplicationContext:refreshBeanFactory()
Note over AbstractApplicationContext,AbstractRefreshableApplicationContext: Call refreshBeanFactory() of the subclass

AbstractRefreshableApplicationContext->>AbstractXmlApplicationContext:loadBeanDefinitions(beanFactory)
Note over AbstractRefreshableApplicationContext,AbstractXmlApplicationContext: Call loadBeanDefinitions of the subclass

AbstractXmlApplicationContext->>XmlBeanDefinitionReader:new XmlBeanDefinitionReader(beanFactory)
Note over AbstractXmlApplicationContext,XmlBeanDefinitionReader: Create an XmlBeanDefinitionReader instance

XmlBeanDefinitionReader->>AbstractXmlApplicationContext:Return the bean definition reader
Note over XmlBeanDefinitionReader,AbstractXmlApplicationContext: Return the bean definition reader

AbstractXmlApplicationContext->>AbstractXmlApplicationContext:loadBeanDefinitions(reader)
Note over AbstractXmlApplicationContext: Call loadBeanDefinitions of XmlBeanDefinitionReader

AbstractXmlApplicationContext->>XmlBeanDefinitionReader:reader.loadBeanDefinitions(configLocations)
Note over AbstractXmlApplicationContext,XmlBeanDefinitionReader: Load the bean definitions from the configuration file

AbstractApplicationContext->>AbstractRefreshableApplicationContext:getBeanFactory()
Note over AbstractApplicationContext,AbstractRefreshableApplicationContext: Get the bean factory

AbstractRefreshableApplicationContext->>AbstractApplicationContext:Return the bean factory
Note over AbstractApplicationContext: Return the bean factory

~~~

### 7. Source Code Analysis

The `org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(configLocation)` method calls another constructor.

```java
public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
    this(new String[] {configLocation}, true, null);
}
```

The `org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(configLocations, refresh,parent)` method first sets the parent application context and the configuration file locations. Then, depending on the refresh flag, it decides whether to refresh the application context right away.

```java
public ClassPathXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
			throws BeansException {

    super(parent);
    setConfigLocations(configLocations);
    if (refresh) {
        refresh();
    }
}
```

The `org.springframework.context.support.AbstractApplicationContext#refresh` method calls `obtainFreshBeanFactory` to refresh the internal bean factory. This triggers the initialization of the container.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [code omitted for brevity]
    
    // Implemented by subclasses to refresh the internal bean factory.
    ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
    
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.support.AbstractApplicationContext#obtainFreshBeanFactory` method first calls `refreshBeanFactory` to refresh the internal bean factory. Then it returns the refreshed bean factory.

```java
protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
    refreshBeanFactory();
    return getBeanFactory();
}
```

The `org.springframework.context.support.AbstractRefreshableApplicationContext#refreshBeanFactory` method first checks whether a bean factory already exists. If it does, it destroys the existing beans and closes the factory. Then it creates a new `DefaultListableBeanFactory` and sets its serialization ID to the container ID. It calls `customizeBeanFactory` to customize the factory. Finally, it calls `loadBeanDefinitions` to load the bean definitions, which includes reading the configuration files and parsing the definitions.

```java
@Override
protected final void refreshBeanFactory() throws BeansException {
   // If a bean factory already exists, destroy its beans and close it
   if (hasBeanFactory()) {
      destroyBeans();
      closeBeanFactory();
   }

   try {
      // Create a new DefaultListableBeanFactory instance
      DefaultListableBeanFactory beanFactory = createBeanFactory();
      // Set the serialization ID of the bean factory to the container ID
      beanFactory.setSerializationId(getId());
      // Customize the bean factory (implemented by subclasses)
      customizeBeanFactory(beanFactory);
      // Load the bean definitions, including reading the configuration files and parsing the definitions
      loadBeanDefinitions(beanFactory);
      // Assign the new bean factory to the container
      this.beanFactory = beanFactory;
   } catch (IOException ex) {
      // If an I/O error occurs while parsing the bean definitions, throw ApplicationContextException
      throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
   }
}
```

The `org.springframework.context.support.AbstractXmlApplicationContext#loadBeanDefinitions(beanFactory)` method first creates an `XmlBeanDefinitionReader` to read XML bean definitions. Then it sets the environment, resource loader and entity resolver of the reader. Next, it lets subclasses customize the reader with `initBeanDefinitionReader`. Finally, it calls the reader's load method, which reads the configuration files, parses the bean definitions and registers them in the bean factory.

```java
protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
    // Create a new XmlBeanDefinitionReader for the given BeanFactory
    XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

    // Configure the reader with the resource loading environment of this context
    beanDefinitionReader.setEnvironment(this.getEnvironment());
    // Set the resource loader to the current application context
    beanDefinitionReader.setResourceLoader(this);
    // Set the entity resolver to ResourceEntityResolver to resolve entity references in bean definitions
    beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));

    // Let subclasses customize the reader, then load the bean definitions
    initBeanDefinitionReader(beanDefinitionReader);
    // Call the reader's load method, which reads the configuration files, parses the bean definitions and registers them in the bean factory
    loadBeanDefinitions(beanDefinitionReader);
}

```

The `org.springframework.context.support.AbstractXmlApplicationContext#loadBeanDefinitions(reader)` method first gets the array of configuration resources (`Resource`), which are configuration files loaded from the classpath. If the array is not empty, it loads the bean definitions from these resources with the `XmlBeanDefinitionReader`. Next, it gets the array of configuration file paths, which are given as strings. If the array is not empty, it loads the bean definitions from these paths in the same way.

> **The detailed source analysis of the `reader.loadBeanDefinitions` method is in another article about the [XmlBeanDefinitionReader](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-xmlBeanDefinitionReader/README.md) class.**

```java
protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
    // Get the array of configuration resources (Resource), usually configuration files loaded from the classpath or elsewhere
    Resource[] configResources = getConfigResources();
    // If the array is not empty, load the bean definitions from these resources with the reader
    if (configResources != null) {
        reader.loadBeanDefinitions(configResources);
    }

    // Get the array of configuration file paths, usually given as strings
    String[] configLocations = getConfigLocations();
    // If the array is not empty, load the bean definitions from these paths with the reader
    if (configLocations != null) {
        reader.loadBeanDefinitions(configLocations);
    }
}
```