## PropertiesBeanDefinitionReader

- [PropertiesBeanDefinitionReader](#propertiesbeandefinitionreader)
  - [1. Background Knowledge](#1-background-knowledge)
  - [2. Basic Description](#2-basic-description)
  - [3. Main Features](#3-main-features)
  - [4. Best Practices](#4-best-practices)
  - [5. Sequence Diagram](#5-sequence-diagram)
  - [6. Source Code Analysis](#6-source-code-analysis)
  - [7. Relationship with Other Components](#7-relationship-with-other-components)
  - [8. FAQ](#8-faq)


### 1. Background Knowledge

1. `Resource`
   - `Resource` stands for a resource. It can be a file, a file on the classpath, a URL and so on. It gives an abstraction and access methods for resources.
   - [View the Resource interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-resources/spring-resource)
2. `BeanDefinition`
   - `BeanDefinition` is the core Spring concept that describes and manages bean configuration. It has information about a bean, such as class name, scope, dependencies and initialization method. One of the main tasks of `AnnotatedBeanDefinitionReader` is to turn annotated classes into `BeanDefinition`s and register them in the Spring container.
   - [View the BeanDefinition interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-beans/spring-bean-beanDefinition)

### 2. Basic Description

`PropertiesBeanDefinitionReader` is a Spring component. Its main job is to load bean configuration from a properties file, then parse it and turn it into bean definitions in the Spring container. A properties file is usually made of key-value pairs. The key is the bean name. The value is the bean class name or another configuration property. With this, bean configuration is separate from the application code and from XML files. Bean definitions can be loaded and managed dynamically. This makes the application more extensible and its configuration more flexible.

### 3. Main Features

1. **Load the properties file**
   + `PropertiesBeanDefinitionReader` loads the properties file with the `loadBeanDefinitions(Resource resource)` method. The `Resource` can be a file path, a classpath location or another resource identifier. It points to the properties file that holds the bean definitions.
2. **Parse the properties file**
   + After the properties file is loaded, `PropertiesBeanDefinitionReader` parses its content. It finds the bean names, class names and other configuration properties.
3. **Create bean definitions**
   + From the configuration in the properties file, `PropertiesBeanDefinitionReader` creates the matching `BeanDefinition` objects. They hold the bean definition information, including name, class, properties and dependencies.
4. **Register bean definitions**
   + `PropertiesBeanDefinitionReader` registers the parsed bean definitions in the Spring IoC container. The beans can then be instantiated and used in the application.

### 4. Best Practices

We create a Spring bean factory (`DefaultListableBeanFactory`) and a properties file reader (`PropertiesBeanDefinitionReader`). Then we load the bean definitions from the properties file, and get the bean objects from the bean factory. This loads and manages bean configuration dynamically.

```java
public class PropertiesBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(beanFactory);

        // Load bean definitions from the properties file
        reader.loadBeanDefinitions(new ClassPathResource("bean-definitions.properties"));

        // Get the bean
        System.out.println("myBean = " + beanFactory.getBean("myBean"));
        System.out.println("myBean = " + beanFactory.getBean("myBean"));
    }
}
```

The content of the properties file "`bean-definitions.properties`" is as follows.

```properties
myBean.(class)=com.xcs.spring.bean.MyBean
myBean.message=hello world
myBean.(lazy-init)=true
myBean.(scope)=prototype
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

    @Override
    public String toString() {
        return "MyBean{" +
                "message='" + message + '\'' +
                ", hashCode='0x" + Integer.toHexString(System.identityHashCode(this)).toUpperCase() + '\'' +
                '}';
    }
}
```

The result shows that the bean configuration is loaded dynamically from the properties file, with lazy initialization and scope controlled at the same time. In this case, every request for "`myBeanA`" returns a new instance, because the scope is set to "`prototype`".

```java
myBean = MyBean{message='hello world', hashCode='0x6646153'}
myBean = MyBean{message='hello world', hashCode='0x45DD4EDA'}
```

### 5. Sequence Diagram

~~~mermaid
sequenceDiagram
autonumber
Title: PropertiesBeanDefinitionReader Sequence Diagram

PropertiesBeanDefinitionReaderDemo->>PropertiesBeanDefinitionReader:loadBeanDefinitions(resource)
Note right of PropertiesBeanDefinitionReaderDemo: Load bean definitions from the given properties file
PropertiesBeanDefinitionReader->>PropertiesBeanDefinitionReader:loadBeanDefinitions(encodedResource,prefix)
Note over PropertiesBeanDefinitionReader: Load bean definitions from the given properties file
PropertiesBeanDefinitionReader->>PropertiesBeanDefinitionReader:registerBeanDefinitions(map,prefix,resourceDescription)
Note over PropertiesBeanDefinitionReader: Register the bean definitions in the Map
PropertiesBeanDefinitionReader->>DefaultListableBeanFactory:registerBeanDefinition(beanName,beanDefinition)
Note over PropertiesBeanDefinitionReader,DefaultListableBeanFactory: Register the bean definitions in the container

~~~

### 6. Source Code Analysis

The `org.springframework.beans.factory.support.PropertiesBeanDefinitionReader#loadBeanDefinitions(resource)` method calls the `loadBeanDefinitions(encodedResource,prefix)` method to actually load the bean definitions.

```java
@Override
public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
    return loadBeanDefinitions(new EncodedResource(resource), null);
}
```

The `org.springframework.beans.factory.support.PropertiesBeanDefinitionReader#loadBeanDefinitions(encodedResource,prefix)` method loads and manages bean configuration from a properties file. It parses and registers the properties file in Spring.

```java
public int loadBeanDefinitions(EncodedResource encodedResource, @Nullable String prefix)
			throws BeanDefinitionStoreException {

    // If trace logging is enabled, log that the properties file is being loaded
    if (logger.isTraceEnabled()) {
        logger.trace("Loading properties bean definitions from " + encodedResource);
    }

    // Create a Properties object to hold the key-value configuration of the properties file
    Properties props = new Properties();
    try {
        try (InputStream is = encodedResource.getResource().getInputStream()) {
            // If an encoding is given, read the properties file with it
            if (encodedResource.getEncoding() != null) {
                getPropertiesPersister().load(props, new InputStreamReader(is, encodedResource.getEncoding()));
            }
            // Otherwise, read the properties file with the default encoding
            else {
                getPropertiesPersister().load(props, is);
            }
        }

        // Register bean definitions: turn the configuration of the properties file into bean definitions and register them in the container
        int count = registerBeanDefinitions(props, prefix, encodedResource.getResource().getDescription());

        // If debug logging is enabled, log the number of loaded bean definitions
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded " + count + " bean definitions from " + encodedResource);
        }

        return count;
    }
    catch (IOException ex) {
        // Handle exceptions. If an error happens while reading or registering, throw BeanDefinitionStoreException
        throw new BeanDefinitionStoreException("Could not parse properties from " + encodedResource.getResource(), ex);
    }
}
```

The `org.springframework.beans.factory.support.PropertiesBeanDefinitionReader#registerBeanDefinitions(map, prefix,resourceDescription)` method first goes through the key-value pairs of the properties file. Using the configured prefix and the key format, it registers valid bean definitions in the container. Keys that do not match the format, or whose bean is already registered, are ignored. At the end, the method returns the number of registered bean definitions, to track how many beans were registered.

```java
public int registerBeanDefinitions(Map<?, ?> map, @Nullable String prefix, String resourceDescription)
			throws BeansException {

    if (prefix == null) {
        prefix = "";
    }
    int beanCount = 0;

    // Loop over the key-value pairs of the properties file
    for (Object key : map.keySet()) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException("Illegal key [" + key + "]: only Strings allowed");
        }
        String keyString = (String) key;
        if (keyString.startsWith(prefix)) {
            // The key format is: prefix<name>.property
            String nameAndProperty = keyString.substring(prefix.length());
            // Find the dot before the property name. Ignore dots inside the property key.
            int sepIdx ;
            int propKeyIdx = nameAndProperty.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX);
            if (propKeyIdx != -1) {
                sepIdx = nameAndProperty.lastIndexOf(SEPARATOR, propKeyIdx);
            }
            else {
                sepIdx = nameAndProperty.lastIndexOf(SEPARATOR);
            }
            if (sepIdx != -1) {
                String beanName = nameAndProperty.substring(0, sepIdx);
                if (logger.isTraceEnabled()) {
                    logger.trace("Found bean name '" + beanName + "'");
                }
                if (!getRegistry().containsBeanDefinition(beanName)) {
                    // If the bean is not registered yet...
                    // Register the bean definition
                    registerBeanDefinition(beanName, map, prefix + beanName, resourceDescription);
                    ++beanCount;
                }
            }
            else {
                // Ignore this key: it is not a valid bean name and property,
                // even though it starts with the required prefix.
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid bean name and property [" + nameAndProperty + "]");
                }
            }
        }
    }

    // Return the number of registered bean definitions
    return beanCount;
}
```

The `org.springframework.beans.factory.support.PropertiesBeanDefinitionReader#registerBeanDefinition(beanName,map,prefix, resourceDescription)` method handles the configuration in the properties file. It turns it into a Spring bean definition and registers it in the container.

```java
protected void registerBeanDefinition(String beanName, Map<?, ?> map, String prefix, String resourceDescription)
			throws BeansException {

    // ... [code omitted for brevity]

    try {
        AbstractBeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(
            parent, className, getBeanClassLoader());
        bd.setScope(scope);
        bd.setAbstract(isAbstract);
        bd.setLazyInit(lazyInit);
        bd.setConstructorArgumentValues(cas);
        bd.setPropertyValues(pvs);
        getRegistry().registerBeanDefinition(beanName, bd);
    }
    catch (ClassNotFoundException ex) {
        throw new CannotLoadBeanClassException(resourceDescription, beanName, className, ex);
    }
    catch (LinkageError err) {
        throw new CannotLoadBeanClassException(resourceDescription, beanName, className, err);
    }
}
```

### 7. Relationship with Other Components

1. **Standalone applications**
   + In some standalone Spring applications, we can use `PropertiesBeanDefinitionReader` to load bean definitions dynamically. This makes the configuration of the application more flexible. The application can configure and manage beans as needed.
2. **Properties-based configuration**
   + Some applications keep the configuration of various beans in properties files. `PropertiesBeanDefinitionReader` can load these files and turn the configuration into Spring bean definitions.

### 8. FAQ

1. **How do I use `PropertiesBeanDefinitionReader`?**
   + Create an instance of `PropertiesBeanDefinitionReader` and use it with a Spring bean factory (such as `DefaultListableBeanFactory`). Then use the `loadBeanDefinitions` method to load the configuration from the properties file and register the bean definitions in the container.
2. **What is the format of the properties file?**
   + The properties file usually uses key-value pairs (key=value). The key is the bean name and the value is the bean configuration. The file can hold the bean class, scope, property values and so on.
3. **How do I configure lazy initialization and scope?**
   + In the properties file, use `lazy-init` and `scope`. For example, `myBean.(lazy-init)=true` makes a bean lazy, and `myBean.(scope)=prototype` sets the bean scope to prototype.
4. **What is a bean name in the properties file?**
   + The bean name in the properties file is usually the same as the key. For example, in `myBeanA=(class)=com.example.MyBean` the bean name is "myBeanA".
5. **Why use `PropertiesBeanDefinitionReader`?**
   + `PropertiesBeanDefinitionReader` makes the application more flexible. It lets us move configuration out of the code, so the application can be configured. It also supports loading and managing beans dynamically, which suits cases that need dynamic configuration.