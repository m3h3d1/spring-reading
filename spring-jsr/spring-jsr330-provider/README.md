## javax.inject.Provider

- [javax.inject.Provider](#javaxinjectprovider)
  - [1. Overview](#1-overview)
  - [2. Interface Description](#2-interface-description)
  - [3. Interface Source](#3-interface-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
    - [Provider Registration](#provider-registration)
    - [Provider Instantiation](#provider-instantiation)
    - [Provider Generic Type Extraction](#provider-generic-type-extraction)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [Provider Registration](#provider-registration-1)
    - [Provider Instantiation](#provider-instantiation-1)
    - [Provider Generic Type Extraction](#provider-generic-type-extraction-1)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Interface Description

`javax.inject.Provider<T>` is an interface defined in the JSR-330 "Dependency Injection for Java" specification. It is used to get an injected dependency lazily. The Spring framework supports JSR-330, so it also supports the `javax.inject.Provider` interface.

### 3. Interface Source

The main goal of the `Provider<T>` interface is to create and provide objects on demand, at the time you need them.

```java
/**
 * Provides instances of type T.
 * It is part of the JSR-330 "Dependency Injection for Java" specification.
 * An implementation can create a new instance on each call of the get method (like a factory method),
 * or return the same instance for several calls.
 * 
 * It is especially useful in these cases:
 * - Lazy or on-demand creation of objects.
 * - Getting several instances of a prototype-scoped bean.
 * - Solving circular dependencies in some cases.
 */
public interface Provider<T> {

    /**
     * Provides an instance of T. Depending on the implementation,
     * each call of this method may create a new instance,
     * or return the same instance for several calls.
     *
     * @return an instance of T
     * @throws RuntimeException if there is an error while providing the instance.
     * For example, an error during the injection or construction of T.
     * The caller should not catch these exceptions, because the behavior may differ between injector implementations or configurations.
     */
    T get();
}
```

### 4. Main Features

1. **Create and provide instances on demand**
   + When you ask for an instance of an object, `Provider<T>` can give you one, without instantiating or looking up the object directly. This is a way to create objects on demand.
2. **Handle prototype-scoped beans**
   + In Spring, if you have a prototype-scoped bean, `Provider<T>` makes sure you get a new bean instance each time you call `get()`.
3. **Solve circular dependencies**
   + In some cases, especially with prototype-scoped beans, `Provider<T>` can help solve the circular dependency problem caused by direct injection.
4. **More flexibility**
   + Compared with injecting the bean directly, `Provider<T>` lets you decide when and how to get the bean instance. This gives more flexibility to applications that decide at runtime, based on some condition, whether to create or get a bean.

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (this class configures the Spring container with Java annotations). We pass the `MyConfiguration` component class as the constructor argument. Then we get a bean of type `MyController` from the Spring context and call its `showService` method.

```java
public class ProviderApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyController controller = context.getBean(MyController.class);
        controller.showService();
    }
}
```

The `MyConfiguration` class uses `@ComponentScan("com.xcs.spring")`. This tells Spring to search the given package ("`com.xcs.spring`") and its sub-packages for classes with annotations such as `@Component`, `@Service`, `@Repository`, and `@Controller`. Spring registers them as beans automatically. So you do not need to write a bean definition for each component.

```java
@Configuration
@ComponentScan("com.xcs.spring")
public class MyConfiguration {

}
```

`MyController` uses `@Autowired` to inject a `Provider<MyService>`. Then the `showService` method of `MyController` uses `myServiceProvider` to get two instances of `MyService`.

```java
@Controller
public class MyController {

    @Autowired
    private Provider<MyService> myServiceProvider;

    public void showService(){
        System.out.println("myServiceProvider1 = " + myServiceProvider.get());
        System.out.println("myServiceProvider2 = " + myServiceProvider.get());
    }
}
```

`MyService` is a simple service class. It has no methods or features.

```java
@Service
public class MyService {
    
}
```

The result shows that `myServiceProvider1` and `myServiceProvider2` get `MyService` instances with the same object reference (`@235ecd9f`). This means the `MyService` bean has singleton scope.

| :warning: Note!                                              |
| :----------------------------------------------------------- |
| In Spring, the default scope is singleton. This means there is only one instance of a given bean definition in the whole Spring container. So no matter how many times we call `myServiceProvider.get()`, it returns the same `MyService` instance. If we want a new `MyService` instance each time, we need to define `MyService` with prototype scope. |

```java
myServiceProvider1 = com.xcs.spring.service.MyService@235ecd9f
myServiceProvider2 = com.xcs.spring.service.MyService@235ecd9f
```

### 6. Sequence Diagram

#### Provider Registration

~~~mermaid
sequenceDiagram
Title: Provider registration sequence diagram

NamedApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)
Note right of AnnotationConfigApplicationContext: Create an annotation-based application context

AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext()
Note right of AnnotationConfigApplicationContext: The AnnotationConfigApplicationContext constructor

AnnotationConfigApplicationContext->>GenericApplicationContext:GenericApplicationContext()
Note right of GenericApplicationContext: Construction based on GenericApplicationContext

GenericApplicationContext->>DefaultListableBeanFactory: new DefaultListableBeanFactory()
Note right of DefaultListableBeanFactory: Create a default listable bean factory

DefaultListableBeanFactory->>DefaultListableBeanFactory: ClassUtils.forName("javax.inject.Provider",classLoader)
Note right of DefaultListableBeanFactory: Run the static block in DefaultListableBeanFactory
~~~

#### Provider Instantiation

~~~mermaid
sequenceDiagram
Title: Provider instantiation sequence diagram

AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor: postProcessProperties(pvs,bean,beanName)
Note right of AutowiredAnnotationBeanPostProcessor: Handle the injection of bean properties

AutowiredAnnotationBeanPostProcessor->>InjectionMetadata: inject(target,beanName,pvs)
Note right of InjectionMetadata: Do dependency injection on the target bean

InjectionMetadata->>AutowiredFieldElement: inject(bean,beanName,pvs)
Note right of AutowiredFieldElement: Do dependency injection on the specific field

AutowiredFieldElement->>AutowiredFieldElement: resolveFieldValue(field,bean,beanName)
Note right of AutowiredFieldElement: Resolve the field value for injection

AutowiredFieldElement->>DefaultListableBeanFactory: resolveDependency(desc, beanName, autowiredBeanNames, typeConverter)
Note right of DefaultListableBeanFactory: Resolve the given dependency

DefaultListableBeanFactory->>Jsr330Factory: new Jsr330Factory()
Note right of Jsr330Factory: Create a new JSR-330 factory

Jsr330Factory->>DefaultListableBeanFactory: Return Factory
Note right of DefaultListableBeanFactory: Get the JSR-330 factory instance

DefaultListableBeanFactory->>Jsr330Factory: createDependencyProvider(descriptor,beanName)
Note right of Jsr330Factory: Create the dependency provider

Jsr330Factory->>Jsr330Provider: new Jsr330Provider(descriptor, beanName)
Note right of Jsr330Provider: Create a new JSR-330 provider

Jsr330Provider->>Jsr330Factory: Return Provider
Note right of Jsr330Factory: Get the JSR-330 provider instance

Jsr330Factory->>DefaultListableBeanFactory: Return Provider
Note right of DefaultListableBeanFactory: Get the JSR-330 provider

DefaultListableBeanFactory->>AutowiredFieldElement: Return Provider
Note right of AutowiredFieldElement: 

AutowiredFieldElement->>Field: field.set(bean, Provider)
Note right of Field: Set the field value with the Provider

~~~

#### Provider Generic Type Extraction

~~~mermaid
sequenceDiagram
Title: Provider generic type extraction sequence diagram

MyController->>Jsr330Provider:get()
Note over Jsr330Provider: MyController tries to get the bean.

Jsr330Provider->>DependencyObjectProvider:getValue()
Note over DependencyObjectProvider: Jsr330Provider hands over to DependencyObjectProvider to actually get the value.

DependencyObjectProvider->>DefaultListableBeanFactory:doResolveDependency(descriptor,beanName,autowiredBeanNames,typeConverter)
Note over DefaultListableBeanFactory: DependencyObjectProvider hands the actual dependency resolving to DefaultListableBeanFactory.

DefaultListableBeanFactory->>DependencyObjectProvider:Return Bean
Note over DependencyObjectProvider: DefaultListableBeanFactory finishes resolving and returns the bean.

DependencyObjectProvider->>Jsr330Provider:Return Bean
Note over Jsr330Provider: DependencyObjectProvider returns the bean to Jsr330Provider.

Jsr330Provider->>MyController:Return Bean
Note over MyController: Jsr330Provider returns the bean to MyController.
~~~

### 7. Source Code Analysis

#### Provider Registration

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (this class configures the Spring container with Java annotations). We pass the `MyConfiguration` component class as the constructor argument. Then we get a bean of type `MyController` from the Spring context and call its `showService` method.

```java
public class ProviderApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyController controller = context.getBean(MyController.class);
        controller.showService();
    }
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor does three steps. This time we focus on `this()`.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext()` method initializes two core components. One reads bean definitions from annotations (`AnnotatedBeanDefinitionReader`). The other scans the classpath and detects bean components automatically (`ClassPathBeanDefinitionScanner`). This is also the focus of this analysis.

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

The `org.springframework.context.support.GenericApplicationContext#GenericApplicationContext()` method creates a new `DefaultListableBeanFactory` instance and assigns it to the `beanFactory` property.

```java
public GenericApplicationContext() {
    this.beanFactory = new DefaultListableBeanFactory();
}
```

The static block of `org.springframework.beans.factory.support.DefaultListableBeanFactory` tries to load the `Provider` interface defined in JSR-330 (the `javax.inject` API). This interface is part of the Java dependency injection specification. If the interface is available, it is assigned to the `javaxInjectProviderClass` variable. If it is not available (for example, the runtime does not have the library), `javaxInjectProviderClass` is set to `null`.

```java
static {
    try {
        javaxInjectProviderClass =
            ClassUtils.forName("javax.inject.Provider", DefaultListableBeanFactory.class.getClassLoader());
    }
    catch (ClassNotFoundException ex) {
        // JSR-330 API not available - Provider interface simply not supported then.
        javaxInjectProviderClass = null;
    }
}
```

#### Provider Instantiation

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties` method post-processes the properties of the bean. It handles property injection done with annotations such as `@Autowired`.

```java
@Override
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
    // Get the InjectionMetadata related to the bean name and class.
    // It holds all fields and methods of this bean that need injection.
    InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
    
    try {
        // Use the InjectionMetadata to actually inject the properties.
        metadata.inject(bean, beanName, pvs);
    }
    // If a BeanCreationException happens during injection, throw it as it is.
    catch (BeanCreationException ex) {
        throw ex;
    }
    // Catch other exceptions and throw them as BeanCreationException, with detailed error information.
    catch (Throwable ex) {
        throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
    }
    // Return the original PropertyValues, because this method focuses on dependency injection, not on changing properties.
    return pvs;
}
```

The main goal of the `org.springframework.beans.factory.annotation.InjectionMetadata#inject` method is to inject all elements that need injection (for example fields or methods with annotations such as `@Autowired`) into the target bean.

```java
public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // Get the elements that were already checked. Usually, all elements are checked once during the initialization phase.
    Collection<InjectedElement> checkedElements = this.checkedElements;

    // If there are checked elements, use them. Otherwise, use all injected elements.
    Collection<InjectedElement> elementsToIterate =
        (checkedElements != null ? checkedElements : this.injectedElements);

    // If there are elements to inject...
    if (!elementsToIterate.isEmpty()) {
        // Go through each element and inject it into the target bean.
        for (InjectedElement element : elementsToIterate) {
            // Do the injection for each element (field or method).
            element.inject(target, beanName, pvs);
        }
    }
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject` method first checks whether the value of the field is already cached. If it is, the value comes from the cache. Otherwise, it is resolved again. Then the method makes sure the field is accessible (especially for private fields), and sets the resolved value into the matching field of the target bean.

```java
@Override
protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // Get the Field object that stands for the field with the @Autowired annotation.
    Field field = (Field) this.member;

    Object value;
    // If the value of the field is already cached (resolved before), try to get it from the cache.
    if (this.cached) {
        try {
            // Get the resolved field value from the cache.
            value = resolvedCachedArgument(beanName, this.cachedFieldValue);
        }
        catch (NoSuchBeanDefinitionException ex) {
            // The bean in the cache was removed unexpectedly -> resolve again.
            value = resolveFieldValue(field, bean, beanName);
        }
    }
    else {
        // If the field value is not cached, resolve it directly.
        value = resolveFieldValue(field, bean, beanName);
    }

    // If the resolved value is not null...
    if (value != null) {
        // Make the field accessible. This is needed, especially when the field is private.
        ReflectionUtils.makeAccessible(field);
        // Actually inject the resolved value into the field of the target bean.
        field.set(bean, value);
    }
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#resolveFieldValue` method resolves the value of the field from the Spring bean factory with the `beanFactory.resolveDependency` method.

```java
@Nullable
private Object resolveFieldValue(Field field, Object bean, @Nullable String beanName) {
    // ... [code omitted for brevity]
    Object value;
    try {
        // Resolve the field value from the Spring bean factory with the `beanFactory.resolveDependency` method
        value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
    }
    catch (BeansException ex) {
        throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
    }
    // ... [code omitted for brevity]
    return value;
}
```

In the `org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency` method, if the dependency type is `javax.inject.Provider`, it creates a `Jsr330Factory` instance and calls its `createDependencyProvider` method. This creates and returns a `Provider` instance that follows the JSR-330 specification.

```java
@Override
@Nullable
public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                                @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

    // ... [code omitted for brevity]
    if (javaxInjectProviderClass == descriptor.getDependencyType()) {
        return new Jsr330Factory().createDependencyProvider(descriptor, requestingBeanName);
    }
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory.Jsr330Factory#createDependencyProvider` method creates and returns a new instance of `Jsr330Provider`. The `Jsr330Provider` class is an adapter (or proxy) that implements the JSR-330 `Provider` interface. This lets the Spring framework work together with other dependency injection frameworks that follow JSR-330.

```java
public Object createDependencyProvider(DependencyDescriptor descriptor, @Nullable String beanName) {
    return new Jsr330Provider(descriptor, beanName);
}
```

#### Provider Generic Type Extraction

`MyController` uses `@Autowired` to inject a `Provider<MyService>`. Then the `showService` method of `MyController` uses `myServiceProvider` to get two instances of `MyService`.

```java
@Controller
public class MyController {

    @Autowired
    private Provider<MyService> myServiceProvider;

    public void showService(){
        System.out.println("myServiceProvider1 = " + myServiceProvider.get());
        System.out.println("myServiceProvider2 = " + myServiceProvider.get());
    }
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory.Jsr330Factory.Jsr330Provider#get` method simply calls the `getValue()` method to get and return an object.

```java
@Override
@Nullable
public Object get() throws BeansException {
    return getValue();
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory.DependencyObjectProvider#getValue` method resolves the dependency and returns it directly.

```java
@Nullable
protected Object getValue() throws BeansException {
    if (this.optional) {
        return createOptionalDependency(this.descriptor, this.beanName);
    }
    else {
        return doResolveDependency(this.descriptor, this.beanName, null, null);
    }
}
```

### 8. Notes

1. **Dependency check**
   + Unlike injecting a bean directly, a bean injected with `Provider` is not checked immediately at startup. If you want the dependency to be checked when the container starts, avoid `Provider`.
2. **Performance**
   + Each call of `Provider.get()` may create a new bean (if the scope of the bean is prototype). So calling `Provider.get()` very often may cause performance problems.
3. **Prototype scope**
   + If you use `Provider` to inject a prototype-scoped bean, each call of `Provider.get()` returns a new instance. Make sure this is the behavior you expect.
4. **Generic type**
   + When you use `Provider`, you must give it a generic type that shows the type of the bean you expect to inject.
5. **Use with other annotations**
   + `Provider` can be used with other Spring annotations (such as `@Qualifier`). But make sure the combination makes sense.
6. **Error handling**
   + The `Provider.get()` method may throw `BeansException`. Be ready to handle these exceptions when you use it.
7. **Compatibility with JSR-330**
   + Spring supports `Provider`. But if you use another container that supports JSR-330, make sure `Provider` behaves the same way in those containers.
8. **Circular dependencies**
   + Compared with direct bean injection, `Provider` may make it easier to solve some circular dependency problems. But still take care not to add needless complexity.

### 9. Summary

#### Best Practices Summary

1. **Entry class and context configuration**
   + `AnnotationConfigApplicationContext` gives Spring a configuration style based on Java annotations. Here, we use the `MyConfiguration` class as the configuration class to initialize the Spring context.
2. **Component scan**
   + By using `@ComponentScan` on the `MyConfiguration` class, we tell Spring to scan the given package and its sub-packages for components (classes with `@Component`, `@Service`, `@Repository`, and `@Controller`) and register them as beans. This reduces the need to define a bean for each component by hand.
3. **Service injection**
   + In the `MyController` class, we use `@Autowired` to inject a bean of type `Provider<MyService>`. This way, we can easily get `MyService` instances from the Provider.
4. **Service use**
   + In the `showService` method of `MyController`, we call `myServiceProvider.get()` twice to show how to get instances of `MyService` from the Provider.
5. **Singleton scope**
   + The result shows that the two `MyService` instances are the same. This means the `MyService` bean has singleton scope. In Spring, the default bean scope is singleton. This means there is only one instance of a given bean definition in the whole Spring container.
6. **Things to note**
   + The default Spring scope is singleton. But in some cases, you may want a new bean instance for each request. In that case, consider defining the bean with prototype scope.

#### Source Code Analysis Summary

1. **Initialization and context configuration**
   - At startup, we use `AnnotationConfigApplicationContext` to create a Java annotation-based configuration environment for Spring, and use the `MyConfiguration` component class as the constructor argument.
   - In its constructor, `this()` initializes two core components. One reads bean definitions from annotations (`AnnotatedBeanDefinitionReader`). The other scans the classpath and detects bean components automatically (`ClassPathBeanDefinitionScanner`).
2. **Loading the JSR-330 `Provider`**
   - In the static block of `DefaultListableBeanFactory`, it tries to load the `Provider` interface of the JSR-330 specification.
   - If the `Provider` interface is available, it is assigned to a specific class variable.
3. **Handling the `@Autowired` annotation**
   - `AutowiredAnnotationBeanPostProcessor` handles the bean properties, especially the property injection done with `@Autowired`.
   - During this, the value of the field is resolved and injected. This involves the use and handling of `Provider`.
4. **Resolving the `Provider` dependency**
   - When a field or property has the type `Provider<T>`, Spring handles it in a special way in `DefaultListableBeanFactory`.
   - For a dependency of the JSR-330 `Provider` type, Spring creates a specific `Provider` instance to satisfy it. This is done with `Jsr330Factory` and its inner class `Jsr330Provider`.
5. **Getting the value of the `Provider`**
   - When the `get` method of `Jsr330Provider` is called, it calls the `getValue` method.
   - The `getValue` method does the actual dependency resolving and returns the bean instance that is needed.
