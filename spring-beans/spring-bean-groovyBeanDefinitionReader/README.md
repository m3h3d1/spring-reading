## GroovyBeanDefinitionReader

- [GroovyBeanDefinitionReader](#groovybeandefinitionreader)
  - [1. Background Knowledge](#1-background-knowledge)
  - [2. Basic Description](#2-basic-description)
  - [3. Main Features](#3-main-features)
  - [4. Best Practices](#4-best-practices)
  - [5. Source Code Analysis](#5-source-code-analysis)
  - [6. Relationship with Other Components](#6-relationship-with-other-components)
  - [7. FAQ](#7-faq)

### 1. Background Knowledge

1. **The Groovy language**
   + It is important to know the basic syntax and features of Groovy, because `GroovyBeanDefinitionReader` parses Groovy script files. You should understand Groovy closures, dynamic typing, metaprogramming and so on.
2. **Spring configuration**
   + Know how to configure a Spring application context: XML configuration, annotation configuration and configuration with Groovy script files. You should know how to define and configure Spring beans, and understand bean scopes, lifecycles and so on.
3. **Writing Groovy scripts**
   + Learn how to write Groovy scripts that define and configure Spring beans. This may include creating bean definitions, importing other classes and resources, running custom logic and so on.
4. **Spring script support**
   + Know how Spring supports different kinds of configuration, including XML, annotations and Groovy scripts, and the differences, pros and cons between them.

### 2. Basic Description

`GroovyBeanDefinitionReader` is a component that reads Groovy script files and parses them into Spring bean definitions. It is part of the Spring Framework. It is mainly used to support Groovy scripts for configuring the beans in an application context.

### 3. Main Features

1. **Load Groovy script files**
   + `GroovyBeanDefinitionReader` lets us load Groovy script files. These files can hold Spring bean definitions and other configuration.
2. **Parse bean definitions**
   + It parses the beans defined in a Groovy script, including their type, properties and dependencies, and turns them into Spring bean definitions.
3. **Register bean definitions**
   + The parsed bean definitions are registered in the Spring container, so the beans can be managed and used in the application.
4. **Support dependency injection**
   + `GroovyBeanDefinitionReader` supports dependency injection in Groovy scripts. We can refer to other beans or resources and inject them into the bean being created.
5. **Support custom logic**
   + We can write custom logic in a Groovy script to compute bean property values dynamically. This allows more complex configuration.
6. **Support bean scope and lifecycle**
   + In a Groovy script, we can set the scope of a bean (for example singleton or prototype) and its lifecycle callback methods (for example the initialization method and the destroy method).

### 4. Best Practices

First, the bean definitions are loaded and registered with `GroovyBeanDefinitionReader`. The `MyService` bean is created from the `my-beans.groovy` Groovy file, and the bean prints a message.

```java
public class GroovyBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        // Create a Spring IoC container
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // Create a GroovyBeanDefinitionReader
        GroovyBeanDefinitionReader reader = new GroovyBeanDefinitionReader(factory);

        // Load the Groovy file and register bean definitions
        reader.loadBeanDefinitions(new ClassPathResource("my-beans.groovy"));

        // Get MyService
        MyService myService = factory.getBean(MyService.class);
        // Print the message
        myService.showMessage();
    }
}
```

Define the `MyService` interface.

```java
public interface MyService {
    void showMessage();
}
```

In the `classpath:my-beans.groovy` file, we define a Groovy class named `MyServiceImpl`. It implements the `MyService` interface and the `InitializingBean` interface. It has a `message` field with its setter and getter, and an `afterPropertiesSet` method that is called on initialization. With the `beans` closure of the `Groovy DSL`, we define a Spring bean named `myServiceImpl`, set its type to `MyServiceImpl`, and set the `message` property to "`hello world`".

```groovy
import com.xcs.spring.service.MyService
import org.springframework.beans.factory.InitializingBean

class MyServiceImpl implements MyService, InitializingBean {
    private String message

    void setMessage(String message) {
        this.message = message
    }

    String getMessage() {
        return message
    }

    @Override
    void afterPropertiesSet() throws Exception {
        System.out.println("MyServiceImpl.afterPropertiesSet")
    }

    @Override
    void showMessage() {
        System.out.println(message)
    }
}

beans {
    myServiceImpl(MyServiceImpl) {
        message = "hello world"
    }
}
```

The result shows that the `myServiceImpl` bean was created and the `afterPropertiesSet` method was called on initialization. At the end, the `myServiceImpl` bean printed the message "`hello world`", which is the message we set in the configuration file. This shows that our `Groovy DSL` configuration and the Spring container setup work.

```java
MyServiceImpl.afterPropertiesSet
hello world
```

### 5. Source Code Analysis

The `org.springframework.beans.factory.support.AbstractBeanDefinitionReader#loadBeanDefinitions(location)` method calls the `loadBeanDefinitions(encodedResource)` method, and wraps the `resource` in an `EncodedResource` object.

```java
@Override
public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
    return loadBeanDefinitions(new EncodedResource(resource));
}
```

The `org.springframework.beans.factory.support.AbstractBeanDefinitionReader#loadBeanDefinitions(location,actualResources)` method loads the bean definitions from a Groovy configuration file. If the file extension is "`.xml`", it delegates to the standard `XmlBeanDefinitionReader`. Otherwise, it loads the bean definitions with a Groovy script: it creates a `GroovyShell` and a `Binding` object and runs the script.

```java
public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
    // Check whether it is an XML file. If so, redirect it to the "standard" XmlBeanDefinitionReader.
    String filename = encodedResource.getResource().getFilename();
    if (StringUtils.endsWithIgnoreCase(filename, ".xml")) {
        return this.standardXmlBeanDefinitionReader.loadBeanDefinitions(encodedResource);
    }

    // If it is not an XML file, load the bean definitions with Groovy.
    if (logger.isTraceEnabled()) {
        logger.trace("Loading Groovy bean definitions from " + encodedResource);
    }

    // Create the Closure object "beans" to handle bean definitions.
    @SuppressWarnings("serial")
    Closure<Object> beans = new Closure<Object>(this) {
        @Override
        public Object call(Object... args) {
            // Call invokeBeanDefiningClosure to handle the bean definitions.
            invokeBeanDefiningClosure((Closure<?>) args[0]);
            return null;
        }
    };

    // Create a Binding object to bind variables to the Groovy script.
    Binding binding = new Binding() {
        @Override
        public void setVariable(String name, Object value) {
            if (currentBeanDefinition != null) {
                // If there is a current bean definition, apply the property to it.
                applyPropertyToBeanDefinition(name, value);
            }
            else {
                super.setVariable(name, value);
            }
        }
    };
    binding.setVariable("beans", beans);

    // Record the number of bean definitions before loading.
    int countBefore = getRegistry().getBeanDefinitionCount();

    try {
        // Create a GroovyShell and bind variables with the Binding.
        GroovyShell shell = new GroovyShell(getBeanClassLoader(), binding);
        // Evaluate the Groovy script to load the bean definitions.
        shell.evaluate(encodedResource.getReader(), "beans");
    }
    catch (Throwable ex) {
        throw new BeanDefinitionParsingException(new Problem("Error evaluating Groovy script: " + ex.getMessage(),
                new Location(encodedResource.getResource()), null, ex));
    }

    // Count the bean definitions after loading.
    int count = getRegistry().getBeanDefinitionCount() - countBefore;
    if (logger.isDebugEnabled()) {
        logger.debug("Loaded " + count + " bean definitions from " + encodedResource);
    }
    return count;
}
```

The `org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader#invokeMethod` method handles method calls in the `Groovy DSL`. It does different things depending on the method name and arguments, including creating bean definitions, handling references and other operations.

```java
@Override
public Object invokeMethod(String name, Object arg) {
    // Convert the arguments to an Object array.
    Object[] args = (Object[]) arg;

    if ("beans".equals(name) && args.length == 1 && args[0] instanceof Closure) {
        // If the method name is "beans" and the only argument is a closure, call the beans method.
        return beans((Closure<?>) args[0]);
    }
    else if ("ref".equals(name)) {
        // If the method name is "ref", handle a bean reference.
        String refName;
        if (args[0] == null) {
            throw new IllegalArgumentException("Argument to ref() is not a valid bean or was not found");
        }
        if (args[0] instanceof RuntimeBeanReference) {
            refName = ((RuntimeBeanReference) args[0]).getBeanName();
        }
        else {
            refName = args[0].toString();
        }
        boolean parentRef = false;
        if (args.length > 1 && args[1] instanceof Boolean) {
            parentRef = (Boolean) args[1];
        }
        return new RuntimeBeanReference(refName, parentRef);
    }
    else if (this.namespaces.containsKey(name) && args.length > 0 && args[0] instanceof Closure) {
        // If the method name matches a known namespace and the arguments contain a closure, handle a dynamic element.
        GroovyDynamicElementReader reader = createDynamicElementReader(name);
        reader.invokeMethod("doCall", args);
    }
    else if (args.length > 0 && args[0] instanceof Closure) {
        // If the arguments contain a closure, handle an abstract bean definition.
        return invokeBeanDefiningMethod(name, args);
    }
    else if (args.length > 0 &&
             (args[0] instanceof Class || args[0] instanceof RuntimeBeanReference || args[0] instanceof Map)) {
        // If the arguments contain a class, a RuntimeBeanReference object or a map, handle a bean definition.
        return invokeBeanDefiningMethod(name, args);
    }
    else if (args.length > 1 && args[args.length - 1] instanceof Closure) {
        // If the arguments contain a closure and it is the last argument, handle a bean definition.
        return invokeBeanDefiningMethod(name, args);
    }
    MetaClass mc = DefaultGroovyMethods.getMetaClass(getRegistry());
    if (!mc.respondsTo(getRegistry(), name, args).isEmpty()) {
        // If none of the above matches, try to call the matching method in the Groovy MetaClass.
        return mc.invokeMethod(getRegistry(), name, args);
    }
    return this;
}
```

The `org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader#invokeBeanDefiningMethod` method creates or updates a bean definition from the given arguments and registers it in the `BeanFactory`.

```java
private GroovyBeanDefinitionWrapper invokeBeanDefiningMethod(String beanName, Object[] args) {
    boolean hasClosureArgument = (args[args.length - 1] instanceof Closure);

    if (args[0] instanceof Class) {
        Class<?> beanClass = (Class<?>) args[0];
        if (hasClosureArgument) {
            // If the arguments contain a closure, create a GroovyBeanDefinitionWrapper and resolve the constructor arguments.
            if (args.length - 1 != 1) {
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(
                    beanName, beanClass, resolveConstructorArguments(args, 1, args.length - 1));
            }
            else {
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, beanClass);
            }
        }
        else  {
            // If there is no closure argument, create a GroovyBeanDefinitionWrapper and resolve the constructor arguments.
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(
                beanName, beanClass, resolveConstructorArguments(args, 1, args.length));
        }
    }
    else if (args[0] instanceof RuntimeBeanReference) {
        // If the argument is a RuntimeBeanReference, create a GroovyBeanDefinitionWrapper that stands for a reference to another bean.
        this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
        this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(((RuntimeBeanReference) args[0]).getBeanName());
    }
    else if (args[0] instanceof Map) {
        // If the argument is a map, it may stand for named constructor arguments or a factory method.
        if (args.length > 1 && args[1] instanceof Class) {
            // Named constructor arguments case: resolve the constructor arguments and set the properties.
            List<Object> constructorArgs =
                resolveConstructorArguments(args, 2, hasClosureArgument ? args.length - 1 : args.length);
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, (Class<?>) args[1], constructorArgs);
            Map<?, ?> namedArgs = (Map<?, ?>) args[0];
            for (Map.Entry<?, ?> entity : namedArgs.entrySet()) {
                String propName = (String) entity.getKey();
                setProperty(propName, entity.getValue());
            }
        }
        else {
            // Factory method case: resolve the arguments and set the factory-related properties.
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
            Map.Entry<?, ?> factoryBeanEntry = ((Map<?, ?>) args[0]).entrySet().iterator().next();
            int constructorArgsTest = (hasClosureArgument ? 2 : 1);
            if (args.length > constructorArgsTest){
                // If there are constructor arguments, resolve them.
                int endOfConstructArgs = (hasClosureArgument ? args.length - 1 : args.length);
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null,
                                                                             resolveConstructorArguments(args, 1, endOfConstructArgs));
            }
            else {
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
            }
            this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(factoryBeanEntry.getKey().toString());
            this.currentBeanDefinition.getBeanDefinition().setFactoryMethodName(factoryBeanEntry.getValue().toString());
        }
    }
    else if (args[0] instanceof Closure) {
        // If the argument is a closure, create a GroovyBeanDefinitionWrapper that stands for an abstract bean definition.
        this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
        this.currentBeanDefinition.getBeanDefinition().setAbstract(true);
    }
    else {
        // Other cases: resolve the constructor arguments.
        List<Object> constructorArgs =
            resolveConstructorArguments(args, 0, hasClosureArgument ? args.length - 1 : args.length);
        this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null, constructorArgs);
    }

    if (hasClosureArgument) {
        // If there is a closure argument, set the delegate and resolve strategy of the closure, and call it to handle the bean definition.
        Closure<?> callable = (Closure<?>) args[args.length - 1];
        callable.setDelegate(this);
        callable.setResolveStrategy(Closure.DELEGATE_FIRST);
        callable.call(this.currentBeanDefinition);
    }

    // Get the bean definition and register it in the BeanFactory.
    GroovyBeanDefinitionWrapper beanDefinition = this.currentBeanDefinition;
    this.currentBeanDefinition = null;
    beanDefinition.getBeanDefinition().setAttribute(GroovyBeanDefinitionWrapper.class.getName(), beanDefinition);
    getRegistry().registerBeanDefinition(beanName, beanDefinition.getBeanDefinition());
    return beanDefinition;
}
```

### 6. Relationship with Other Components

1. **`GenericGroovyApplicationContext`**
   + This is a Spring application context class that implements the `org.springframework.context.ApplicationContext` interface. It lets you load Groovy configuration files and uses `GroovyBeanDefinitionReader` to parse the `Groovy DSL`.
2. **`GroovyScriptFactory`**
   + This class lets you load Groovy scripts as Spring bean definitions. It uses `GroovyBeanDefinitionReader` to parse the Groovy script.

### 7. FAQ

1. **Class or bean definition not found**
   - A class or bean definition that does not exist is referenced in the `Groovy DSL` file, which causes a class-not-found error. Make sure the names and paths of classes and bean definitions are correct, and that the references in the Groovy file match the real class names.
2. **Groovy syntax errors**
   - The `Groovy DSL` file has a syntax error, so loading fails. Check the syntax in the `Groovy DSL` file carefully. You can use a `Groovy IDE` or an editor to help check it.
3. **Classpath problems**
   - A class referenced in the `Groovy DSL` file is not on the classpath, so loading fails. Make sure the referenced classes are on the classpath. You can change the classpath or put the class files in the right place.
4. **Groovy version compatibility**
   - The Groovy version is not compatible with the Spring version, so loading fails. Make sure the Groovy version works with your Spring version. Check the Spring documentation for the supported Groovy versions.