## @DependsOn

- [@DependsOn](#dependson)
  - [1. Overview](#1-overview)
  - [2. Annotation Description](#2-annotation-description)
  - [3. Annotation Source](#3-annotation-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
    - [Bean Registration Sequence Diagram](#bean-registration-sequence-diagram)
    - [Bean Creation Sequence Diagram](#bean-creation-sequence-diagram)
    - [Bean Destruction Sequence Diagram](#bean-destruction-sequence-diagram)
  - [6. Source Code Analysis](#6-source-code-analysis)
    - [Bean Registration Source Code Analysis](#bean-registration-source-code-analysis)
    - [Bean Creation Source Code Analysis](#bean-creation-source-code-analysis)
    - [Bean Destruction Source Code Analysis](#bean-destruction-source-code-analysis)
  - [7. Notes](#7-notes)
  - [8. Summary](#8-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133800615) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [@DependsOn source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-dependsOn)

### 2. Annotation Description

The `@DependsOn` annotation defines the initialization order of beans. Sometimes one bean must be initialized before another. In this case, we can use `@DependsOn` to set the order explicitly.

### 3. Annotation Source

`@DependsOn` has been a core Spring annotation since version 3.0. Its main attribute is `value`. It lets us name the other beans that the current bean depends on.

```java
/**
 * Beans on which the current bean depends. Any beans specified are guaranteed to be
 * created by the container before this bean. Used infrequently in cases where a bean
 * does not explicitly depend on another through properties or constructor arguments,
 * but rather depends on the side effects of another bean's initialization.
 *
 * A depends-on declaration can specify both an initialization-time dependency and,
 * in the case of singleton beans only, a corresponding destruction-time dependency.
 * Dependent beans that define a depends-on relationship with a given bean are destroyed
 * first, prior to the given bean itself being destroyed.
 * So, a depends-on declaration can also control the shutdown order.
 *
 * May be used on any class directly or indirectly annotated with
 * org.springframework.stereotype.Component or on methods annotated with Bean.
 *
 * Using DependsOn at the class level has no effect unless component-scanning is being used.
 * If a DependsOn-annotated class is declared via XML, DependsOn annotation metadata is ignored,
 * and <bean depends-on="..."/> is respected instead.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DependsOn {

    // The names of the other beans that the current bean depends on.
	String[] value() default {};

}
```

### 4. Main Features

1. **Initialization order**
   + `@DependsOn` makes sure one or more beans are initialized before the current bean. This is useful when a bean's initialization relies on the side effects of another bean.
2. **Destruction order (singleton beans only)**
   + `@DependsOn` also affects the destruction order of singleton beans. A bean is destroyed before the beans it depends on.
3. **Multiple dependencies**
   + `@DependsOn` accepts several names. This makes sure many beans are initialized before the current bean.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass the `MyConfiguration` component class to the constructor. At the end, we call `context.close()` to close the container.

```java
public class DependsOnApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
    }
}
```

Here we use `@Bean` to define three beans. `BeanA` depends on `BeanB`, and `BeanB` depends on `BeanC`. `BeanC` has no explicit dependency.

```java
@Configuration
public class MyConfiguration {

    @Bean
    @DependsOn("beanB")
    public BeanA beanA() {
        return new BeanA();
    }

    @Bean
    @DependsOn("beanC")
    public BeanB beanB() {
        return new BeanB();
    }

    @Bean
    public BeanC beanC() {
        return new BeanC();
    }
}
```

`BeanA`, `BeanB`, and `BeanC` each have a constructor and implement the `DisposableBean` interface.

```java
public class BeanA implements DisposableBean {

    public BeanA() {
        System.out.println("BeanA Initialized");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("BeanA Destroyed");
    }
}

public class BeanB implements DisposableBean {

    public BeanB() {
        System.out.println("BeanB Initialized");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("BeanB Destroyed");
    }
}

public class BeanC implements DisposableBean {

    public BeanC() {
        System.out.println("BeanC Initialized");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("BeanC Destroyed");
    }
}
```

The output shows that with the `@DependsOn` annotation and the `destroy()` method of `DisposableBean`, we can control both the initialization order and the destruction order of beans.

```assembly
BeanC Initialized
BeanB Initialized
BeanA Initialized
BeanA Destroyed
BeanB Destroyed
BeanC Destroyed

PS
The initialization order is:
1. `BeanC` (`BeanC Initialized` is printed)
2. `BeanB` (`BeanB Initialized` is printed)
3. `BeanA` (`BeanA Initialized` is printed)

When the Spring container closes, the destruction order is the reverse of the initialization order:
1. `BeanA` (`BeanA Destroyed` is printed)
2. `BeanB` (`BeanB Destroyed` is printed)
3. `BeanC` (`BeanC Destroyed` is printed)
```

### 6. Sequence Diagram

#### Bean Registration Sequence Diagram

~~~mermaid
sequenceDiagram 
DependsOnApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>Start the context
AnnotationConfigApplicationContext-->>ConfigurationApplication: Return context<br>Return the context instance
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: register(componentClasses)<br>Register component classes
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader: register(componentClasses)<br>Reader registers the classes
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: registerBean(beanClass)<br>Register the bean class
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: doRegisterBean(beanClass,name,qualifiers, supplier,customizers)<br>Do the bean registration
AnnotatedBeanDefinitionReader->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd)<br>Process common definition annotations
AnnotationConfigUtils-->>AnnotationConfigUtils:processCommonDefinitionAnnotations(abd,metadata)<br>Parse the DependsOn annotation and store it in the BeanDefinition
AnnotatedBeanDefinitionReader->>BeanDefinitionReaderUtils: registerBeanDefinition(definitionHolder,registry)<br>Register the bean definition
BeanDefinitionReaderUtils->>DefaultListableBeanFactory: registerBeanDefinition(beanName,beanDefinition)<br>Factory stores the bean definition
~~~

#### Bean Creation Sequence Diagram

~~~mermaid
sequenceDiagram 
DependsOnApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)<br>Create the application context
AnnotationConfigApplicationContext->>AbstractApplicationContext:refresh()<br>Refresh the application context
AbstractApplicationContext->>AbstractApplicationContext:finishBeanFactoryInitialization(beanFactory)<br>Finish bean factory initialization
AbstractApplicationContext->>DefaultListableBeanFactory:preInstantiateSingletons()<br>Pre-instantiate singleton beans
DefaultListableBeanFactory->>+AbstractBeanFactory:getBean(name)<br>Get the bean instance
AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)<br>The actual logic to get the bean
AbstractBeanFactory->>AbstractBeanDefinition:Get the names of the beans this bean depends on
AbstractBeanDefinition->>AbstractBeanFactory:Return the dependency bean names
AbstractBeanFactory->>DefaultSingletonBeanRegistry:isDependent(beanName,dependentBeanName)<br>Check the dependency
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:isDependent(beanName,dependentBeanName,alreadySeen)<br>Check the dependency
DefaultSingletonBeanRegistry->>AbstractBeanFactory:Return whether a dependency exists, true or false
AbstractBeanFactory->>-AbstractBeanFactory:getBean(name)<br>Get the dependency bean instance (recursive)
~~~

#### Bean Destruction Sequence Diagram

~~~mermaid
sequenceDiagram
DisposableBeanApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses	)<br>The application starts initializing the context
AnnotationConfigApplicationContext-->>DisposableBeanApplication:Initialization done
DisposableBeanApplication->>AbstractApplicationContext:close()<br>Request to close the context
AbstractApplicationContext->>AbstractApplicationContext:doClose()<br>Run the close logic
AbstractApplicationContext->>AbstractApplicationContext:destroyBeans()<br>Start destroying beans
AbstractApplicationContext->>DefaultListableBeanFactory:destroySingletons()<br>Destroy singleton beans
DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingletons()<br>Call the parent destroy method
DefaultSingletonBeanRegistry-->>DefaultListableBeanFactory:destroySingleton(beanName)<br>Destroy a single bean
DefaultListableBeanFactory->>DefaultSingletonBeanRegistry:super.destroySingleton(beanName)<br>Call the parent destroy-bean method
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:destroyBean(beanName,bean)<br>Run the bean destruction
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:Remove the dependent mapping (dependentBeanMap)
DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:Remove the dependency mapping (dependenciesForBeanMap)
~~~

### 6. Source Code Analysis

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass the `MyConfiguration` component class to the constructor. At the end, we call `context.close()` to close the container.

```java
public class DependsOnApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        context.close();
    }
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor runs three steps.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    // Step 1. This constructor sets up the basic configuration reader and the classpath scanner
    this();
    // Step 2. This method registers the classes in the Spring context so they can be recognized and processed
    register(componentClasses);
    // Step 3. This method starts the whole Spring container
    refresh();
}
```

#### Bean Registration Source Code Analysis

Let us start with step 2 of the `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor. The `org.springframework.context.annotation.AnnotationConfigApplicationContext#register` method lets us register one or more component classes (for example, classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, or `@Configuration`) in the Spring container.

```java
@Override
public void register(Class<?>... componentClasses) {
    Assert.notEmpty(componentClasses, "At least one component class must be specified");
    StartupStep registerComponentClass = this.getApplicationStartup().start("spring.context.component-classes.register")
        .tag("classes", () -> Arrays.toString(componentClasses));
    this.reader.register(componentClasses);
    registerComponentClass.end();
}
```

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#register` method loops over each component class. It calls another method to do the real registration.

```java
public void register(Class<?>... componentClasses) {
    for (Class<?> componentClass : componentClasses) {
        registerBean(componentClass);
    }
}
```

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#registerBean(beanClass)` method is a quick way to register a bean type without any other settings.

```java
public void registerBean(Class<?> beanClass) {
    doRegisterBean(beanClass, null, null, null, null);
}
```

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean` method handles the `@DependsOn` annotation on the bean definition.

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
			@Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
			@Nullable BeanDefinitionCustomizer[] customizers) {
    // ... [code omitted for brevity]
    AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.annotation.AnnotationConfigUtils#processCommonDefinitionAnnotations` method passes the real work to another method.

```java
public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
    processCommonDefinitionAnnotations(abd, abd.getMetadata());
}
```

In the `org.springframework.context.annotation.AnnotationConfigUtils#processCommonDefinitionAnnotations(abd,metadata)` method, `attributesFor(metadata, DependsOn.class)` reads the `@DependsOn` attributes from `metadata`. It returns an `AnnotationAttributes` object, which gives easy access to the attribute values. If the `@DependsOn` annotation exists (`dependsOn` is not `null`), the method reads its `value` attribute. This is a string array of the names of the beans the current bean depends on. Then `abd.setDependsOn()` stores these names.

```java
static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
    // ... [code omitted for brevity]
    AnnotationAttributes dependsOn = attributesFor(metadata, DependsOn.class);
    if (dependsOn != null) {
        abd.setDependsOn(dependsOn.getStringArray("value"));
    }
	// ... [code omitted for brevity]
}
```

#### Bean Creation Source Code Analysis

Next, step 3 of the `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor. In the `org.springframework.context.support.AbstractApplicationContext#refresh` method, we focus on `finishBeanFactoryInitialization(beanFactory)`. It instantiates all remaining non-lazy singleton beans. We ignore the other methods here.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    // ... [code omitted for brevity]
    // Instantiate all remaining non-lazy singleton beans
    finishBeanFactoryInitialization(beanFactory);
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.support.AbstractApplicationContext#finishBeanFactoryInitialization` method calls `preInstantiateSingletons` in `DefaultListableBeanFactory` to create all remaining non-lazy singleton beans.

```java
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    // Finish creating all remaining non-lazy singleton beans.
    beanFactory.preInstantiateSingletons();
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons` method creates all non-lazy singleton beans ahead of time. It runs after the Spring context is set up, so all singleton beans are created and initialized correctly. The key call is `getBean(beanName)`. For every singleton bean in the container, it calls `getBean`. This triggers instantiation, initialization, and dependency injection. If the bean was not created before, this call creates and initializes it.

```java
public void preInstantiateSingletons() throws BeansException {
    // ... [code omitted for brevity]
    // Loop over all bean names
    for (String beanName : beanNames) {
        getBean(beanName);
    }
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.support.AbstractBeanFactory#getBean()` method calls `doGetBean` to create the bean. It passes the bean name and some default values. `doGetBean` does most of the work: it finds the bean definition, creates the bean if needed, and handles dependencies.

```java
@Override
public Object getBean(String name) throws BeansException {
    return doGetBean(name, null, null, false);
}
```

In the `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean` method, Spring first reads the dependencies defined by `@DependsOn` from the `BeanDefinition`. It stores them in the `dependsOn` string array. Then it loops over each dependency of the current bean. The `isDependent(beanName, dep)` method checks for a circular dependency. If the current bean is also a dependency of the `dep` bean, Spring throws `BeanCreationException`, because this is clearly a circular dependency. Next, `registerDependentBean(dep, beanName)` tells the container that `beanName` depends on another bean. Finally, `getBean(dep)` initializes the dependency bean and gets its instance.

```java
protected <T> T doGetBean(
        String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
        throws BeansException {
    // ... [code omitted for brevity]

    RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
	checkMergedBeanDefinition(mbd, beanName, args);
    
    // Guarantee initialization of beans that the current bean depends on.
    String[] dependsOn = mbd.getDependsOn();
    if (dependsOn != null) {
        for (String dep : dependsOn) {
            if (isDependent(beanName, dep)) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                                "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
            }
            registerDependentBean(dep, beanName);
            try {
                getBean(dep);
            }
            catch (NoSuchBeanDefinitionException ex) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                                "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
            }
        }
    }
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#isDependent` method checks whether a bean depends on another bean, directly or indirectly.

```java
protected boolean isDependent(String beanName, String dependentBeanName) {
    synchronized (this.dependentBeanMap) {
        return isDependent(beanName, dependentBeanName, null);
    }
}
```

The `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#isDependent(beanName, dependentBeanName, alreadySeen)` method checks recursively whether a bean depends on another bean. It follows both direct and indirect dependencies.

```java
private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
    // If the bean is already in the processed set, return false to avoid endless recursion
    if (alreadySeen != null && alreadySeen.contains(beanName)) {
        return false;
    }

    // Get the canonical bean name (this may involve alias resolution)
    String canonicalName = canonicalName(beanName);
    // Get the direct dependencies of the bean from the dependency map
    Set<String> dependentBeans = this.dependentBeanMap.get(canonicalName);
    // If the bean has no direct dependencies, return false
    if (dependentBeans == null) {
        return false;
    }

    // If the direct dependencies contain the target bean, return true
    if (dependentBeans.contains(dependentBeanName)) {
        return true;
    }

    // Check each direct dependency recursively to see if it depends on the target bean indirectly
    for (String transitiveDependency : dependentBeans) {
        // Create the processed set if it does not exist yet
        if (alreadySeen == null) {
            alreadySeen = new HashSet<>();
        }
        // Add the current bean to the processed set
        alreadySeen.add(beanName);
        // Check recursively
        if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
            return true;
        }
    }

    // If none of the checks above found a dependency, return false
    return false;
}
```

The `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#registerDependentBean` method records the dependency between two beans. It stores the relation in both directions: A depends on B, and B is depended on by A. This helps when resolving and managing complex dependencies between beans.

```java
public void registerDependentBean(String beanName, String dependentBeanName) {
    // Get the canonical bean name (this may involve alias resolution)
    String canonicalName = canonicalName(beanName);

    // Synchronized block to update the dependency map safely across threads
    synchronized (this.dependentBeanMap) {
        // Get or create the dependency set of the bean
        Set<String> dependentBeans =
            this.dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
        // Add the dependent bean name if it is not there yet; otherwise return
        if (!dependentBeans.add(dependentBeanName)) {
            return;
        }
    }

    // Synchronized block to update the reverse dependency map safely across threads
    synchronized (this.dependenciesForBeanMap) {
        // Get or create the reverse dependency set
        Set<String> dependenciesForBean =
            this.dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
        // Add the reverse dependency information
        dependenciesForBean.add(canonicalName);
    }
}
```

#### Bean Destruction Source Code Analysis

The `org.springframework.context.support.AbstractApplicationContext#close` method first enters a block synchronized on the `startupShutdownMonitor` object. This makes sure only one thread runs the code in the block at a time. It avoids resource conflicts and inconsistent data. Then it calls `doClose`. Finally, it registers a shutdown hook for the JVM.

```java
@Override
public void close() {
    synchronized (this.startupShutdownMonitor) {
        doClose();
        // ... [code omitted for brevity]
    }
}
```

The `org.springframework.context.support.AbstractApplicationContext#doClose` method calls `destroyBeans`.

```java
protected void doClose() {
    // ... [code omitted for brevity]
    // Destroy all cached singletons in the context's BeanFactory.
    destroyBeans();
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.support.AbstractApplicationContext#destroyBeans` method first calls `getBeanFactory()` to get the Spring `BeanFactory`. Then it calls `destroySingletons` on it. This method destroys all singleton beans cached in the `BeanFactory`.

```java
protected void destroyBeans() {
    getBeanFactory().destroySingletons();
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingletons` method first calls the parent `destroySingletons`. This makes sure the destruction logic of the parent class runs.

```java
@Override
public void destroySingletons() {
    super.destroySingletons();
    // ... [code omitted for brevity]
}
```

In the `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingletons` method, Spring first takes all bean names from the key set of the `disposableBeans` field and converts them to a string array. `disposableBeans` holds the beans that implement the `DisposableBean` interface. These beans need special handling when the container is destroyed. Then a reverse loop, starting from the last one, destroys all beans in the `disposableBeans` list. This handles dependencies correctly: a bean created first should be destroyed last.

```java
public void destroySingletons() {
    // ... [code omitted for brevity]
    String[] disposableBeanNames;
    synchronized (this.disposableBeans) {
        disposableBeanNames = StringUtils.toStringArray(this.disposableBeans.keySet());
    }
    for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
        destroySingleton(disposableBeanNames[i]);
    }
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#destroySingleton` method first calls the parent `destroySingleton`. This makes sure the destruction logic of the parent class runs.

```java
@Override
public void destroySingleton(String beanName) {
    super.destroySingleton(beanName);
    // ... [code omitted for brevity]
}
```

In the `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroySingleton` method, Spring first synchronizes on the `disposableBeans` object so it can be changed safely across threads. It removes the bean with the given name from the `disposableBeans` collection and casts it to `DisposableBean`. Finally, it calls `destroyBean` to do the real destruction.

```java
public void destroySingleton(String beanName) {
    // ... [code omitted for brevity]
    destroyBean(beanName, disposableBean);
}
```

In the `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#destroyBean` method, the bean is removed from the dependency lists of all other beans that depend on it. The prepared dependency information of this bean is also deleted.

```java
protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
    // ... [code omitted for brevity]
    
    // Remove the destroyed bean from the dependencies of other beans
    synchronized (this.dependentBeanMap) {
        // Loop over the map that stores the dependencies
        for (Iterator<Map.Entry<String, Set<String>>> it = this.dependentBeanMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Set<String>> entry = it.next();
            // Get the dependency list of the current bean
            Set<String> dependenciesToClean = entry.getValue();
            // Remove the destroyed bean from the dependency list
            dependenciesToClean.remove(beanName);
            // If the dependency list is empty, remove the entry from the map
            if (dependenciesToClean.isEmpty()) {
                it.remove();
            }
        }
    }

    // Remove the prepared dependency information of the destroyed bean
    this.dependenciesForBeanMap.remove(beanName);
}
```

### 7. Notes

1. **Avoid circular dependencies**
   + Do not create a circular dependency, such as Bean A depending on Bean B while Bean B depends on Bean A. The Spring container cannot initialize these two beans.
2. **Do not overuse it**
   + Use this annotation only when you really need to control the initialization order. Overuse makes the code harder to understand and maintain.
3. **Use it together with constructor/property injection**
   + Even if we use `@DependsOn`, a bean that needs another bean as a constructor argument or property should still get it through `@Autowired` or XML configuration.
4. **Destruction order**
   + `@DependsOn` also affects the destruction order of beans. If Bean A depends on Bean B, Bean A is destroyed before Bean B.
5. **Not for runtime dependencies**
   + `@DependsOn` only guarantees the initialization order. If our bean needs another bean at runtime, we should use other ways, such as injection.
6. **Use with `@Lazy`**
   + If a bean uses `@Lazy` (delayed initialization) and also sets dependencies with `@DependsOn`, the initialization order may be unexpected. A lazy bean may not be initialized in the expected order.
7. **Component scanning and explicit declaration**
   + If we use component scanning (through `@ComponentScan`) and put `@DependsOn` on a class, the annotation works. But if the bean is defined in XML and the class also has `@DependsOn`, the annotation is ignored. We should use the `depends-on` attribute in XML instead.
8. **Not for `@Bean` method parameters**
   + If we define beans with `@Bean` methods in a Java configuration class and inject dependencies through method parameters, `@DependsOn` has no effect on them. The method parameters already declare the initialization order.

### 8. Summary

#### Best Practices Summary

1. **Entry class setup**
   + We use `AnnotationConfigApplicationContext` to start the Spring container, with `MyConfiguration` as the configuration class. When the program finishes, we close the container.
2. **Configuration class and dependency declaration**
   + In the `MyConfiguration` class, we define three beans with `@Bean`: `BeanA`, `BeanB`, and `BeanC`. With `@DependsOn`, we state their dependencies clearly. `BeanA` depends on the initialization of `BeanB`, and `BeanB` depends on the initialization of `BeanC`.
3. **Bean declaration and destruction logic**
   + Each bean implements `DisposableBean`. Its constructor prints a message to show it was initialized. Its `destroy` method prints a message to show it was destroyed.
4. **Result and conclusion**
   + When we run the program, the initialization order follows the dependencies defined by `@DependsOn`. The destruction order is the reverse of the initialization order. This makes sure dependent beans are destroyed before the beans they depend on.

#### Source Code Analysis Summary

1. **Start and registration**
   + `AnnotationConfigApplicationContext` starts the Spring container and registers the configuration class in the Spring context.
2. **Bean registration analysis**
   - The `AnnotationConfigApplicationContext` constructor runs two key steps: registering the classes and starting the container.
   - The `register` method lets us register component classes (such as classes annotated with `@Component` or `@Configuration`) in the Spring container.
   - `AnnotatedBeanDefinitionReader` registers these classes. Then the `doRegisterBean` method creates a bean definition for the given `beanClass` and configures it.
3. **Handling the @DependsOn annotation**
   - While defining the bean, Spring parses the `@DependsOn` annotation and stores its dependencies.
   - These relations are used later in the bean lifecycle to create and destroy beans in the right order.
4. **Bean creation analysis**
   - In the `refresh` method of the container startup, all singleton beans are instantiated.
   - The `preInstantiateSingletons` method triggers the creation of all non-lazy singleton beans.
   - If a bean sets dependencies with `@DependsOn`, those dependencies are initialized first.
5. **Bean destruction analysis**
   - When the container closes, the `destroySingletons` method destroys all cached singleton beans.
   - Beans are destroyed in the reverse order of their creation. This makes sure all dependencies are handled correctly during destruction.
6. **Handling circular dependencies**
   - Spring checks whether the dependencies set by `@DependsOn` form a circular dependency. If they do, Spring throws an exception.
