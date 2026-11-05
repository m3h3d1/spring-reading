## @Configuration

- [@Configuration](#configuration)
  - [1. Overview](#1-overview)
  - [2. Annotation Description](#2-annotation-description)
  - [3. Annotation Source](#3-annotation-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
    - [proxyBeanMethods set to true](#proxybeanmethods-set-to-true)
    - [proxyBeanMethods set to false](#proxybeanmethods-set-to-false)
  - [6. Sequence Diagram](#6-sequence-diagram)
    - [Initialization Flow](#initialization-flow)
    - [Registration Flow](#registration-flow)
    - [Enhancement Flow](#enhancement-flow)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [Initialization Flow](#initialization-flow-1)
    - [Registration Flow](#registration-flow-1)
    - [Enhancement Flow](#enhancement-flow-1)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)
  - [10. FAQ](#10-faq)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/132212963) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [@Configuration source](https://github.com/xuchengsheng/spring-reading/blob/master/spring-annotation/spring-annotation-configuration/README.md) 

### 2. Annotation Description

`@Configuration` is a core annotation in Spring. It marks a class as one that declares one or more `@Bean` methods. The Spring container processes these methods at runtime to instantiate, configure, and initialize beans.

### 3. Annotation Source

`@Configuration` was added in Spring 3.0. It marks a class as a Spring configuration class. Such a class can hold one or more `@Bean` methods that define and configure Spring beans. The `value` attribute lets you set the name of the bean definition for the `@Configuration` class. If you leave it out, a name is generated automatically. The `proxyBeanMethods` attribute decides whether `@Bean` methods are proxied to enforce bean lifecycle behavior, such as always returning the same singleton instance.

```java
/**
 * @Configuration is a core annotation that marks a class as a Spring
 * configuration class. Such a class can hold one or more @Bean methods.
 * It is equivalent to XML configuration, but gives more type safety and
 * flexibility. It is often used with @Bean, @ComponentScan, and other
 * annotations to define beans and configuration for the application context.
 * 
 * Because it is meta-annotated with @Component, a @Configuration class is
 * also a component, so Spring's component scanning can detect and process it.
 * 
 * For example, when a @Configuration class is registered in the application
 * context, its @Bean methods are parsed and the matching beans are registered.
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Bean
 * @see Profile
 * @see Import
 * @see ImportResource
 * @see ComponentScan
 * @see Lazy
 * @see PropertySource
 * @see AnnotationConfigApplicationContext
 * @see ConfigurationClassPostProcessor
 * @see org.springframework.core.env.Environment
 * @see org.springframework.test.context.ContextConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

	/**
	 * Explicitly specifies the name of the Spring bean definition
	 * for this @Configuration class.
	 * If not specified (the common case), a bean name is generated automatically.
	 * The custom name only applies if the @Configuration class is detected by
	 * component scanning or supplied directly to an AnnotationConfigApplicationContext.
	 * If the @Configuration class is registered as a traditional XML bean
	 * definition, the name/id of the bean element takes precedence.
	 */
	@AliasFor(annotation = Component.class)
	String value() default "";

	/**
	 * Specifies whether @Bean methods should be proxied to enforce bean
	 * lifecycle behavior. For example, a shared singleton bean instance is
	 * returned even when a @Bean method is called directly in user code.
	 * This needs method interception, done through a CGLIB subclass generated
	 * at runtime. It has some limits, such as the configuration class and its
	 * methods not being allowed to be final.
	 * 
	 * The default is true. It allows "inter-bean references" through direct method
	 * calls inside the configuration class, and external calls to this
	 * configuration's @Bean methods from another configuration class.
	 * If this is not needed, because each @Bean method of this configuration
	 * is self-contained and designed as a plain factory method for the container,
	 * you can set this flag to false to avoid CGLIB subclass processing.
	 * 
	 * Turning off bean method interception handles @Bean methods on their own,
	 * as if they were declared on a non-@Configuration class, that is, in
	 * "@Bean lite mode". In behavior, it is the same as removing the
	 * @Configuration annotation.
	 */
	boolean proxyBeanMethods() default true;
}
```

### 4. Main Features

1. **Bean definition methods**
   + A `@Configuration` class can hold one or more methods annotated with `@Bean`. These methods create and configure beans in the application context.

1. **Proxy support**
   + When `proxyBeanMethods` in `@Configuration` is `true` (the default), `@Bean` methods are proxied to keep the bean lifecycle correct. This lets one `@Bean` method call another `@Bean` method in the same configuration class and still get the singleton bean instance.

1. **Component scanning**
   + `@Configuration` is itself annotated with `@Component`, so Spring's component scanning can detect it. In an application context with component scanning on, you only need to declare the `@Configuration` class. You do not need to register it by hand.

1. **Modularity and composition**
   + With the `@Import` annotation, you can combine several `@Configuration` classes and keep your configuration modular. You can also use `@Profile` with `@Configuration` to give configuration based on the environment or other conditions.

1. **Property sources and placeholders**
   + A `@Configuration` class can be used with `@PropertySource` to load values from a properties file into the Spring environment. You can inject these values into beans with `@Value` or read them through the `Environment` API.

1. **Use with other annotations**:
   + `@Configuration` classes are often used with other Spring annotations (such as `@ComponentScan` and `@PropertySource`) to build a full configuration.

### 5. Best Practices

#### proxyBeanMethods set to true

Let's start with the main class. The context is `AnnotationConfigApplicationContext` (it configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we get a bean of type `MyConfiguration` from the context. Finally we call the `myBean` method twice and print the results.

```java
public class ConfigurationApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyConfiguration configuration = context.getBean(MyConfiguration.class);

        System.out.println(configuration.myBean());
        System.out.println(configuration.myBean());
    }
}
```

The `MyConfiguration` class defines a bean named `myBean` of type `MyBean`. Each time this bean is requested from the Spring container, you get the same `MyBean` instance.

```java
// proxyBeanMethods is true by default, so it is not set here
@Configuration
public class MyConfiguration {

    @Bean
    public MyBean myBean(){
        return new MyBean();
    }
}
```

`MyBean` is a simple class with no fields or methods.

```java
public class MyBean {

}
```

In the output, both references to the `MyBean` object have the same hashcode (`@f736069`). So they point to the same object. The `myBean()` method in `MyConfiguration` is marked with `@Bean`, which returns a singleton by default. When we call `myBean()` twice in the `main` method of `ConfigurationApplication`, the Spring container returns the same `MyBean` instance both times.

```java
com.xcs.spring.bean.MyBean@f736069
com.xcs.spring.bean.MyBean@f736069
```

#### proxyBeanMethods set to false

When `proxyBeanMethods` is set to `false`, this proxy behavior is turned off. If we call the same `@Bean` method several times from the configuration class, a new instance is created each time.

```java
@Configuration(proxyBeanMethods = false)
public class MyConfiguration {

    @Bean
    public MyBean myBean(){
        return new MyBean();
    }
}
```

In the output, the two references to the `MyBean` object have different hashcodes (`@3b69e7d1` and `@815b41f`). So they point to different objects. We set `proxyBeanMethods = false` on `@Configuration` and call `myBean()` twice in the `main` method of `ConfigurationApplication`. Each call creates a new `MyBean` instance. That is why we see two different hashcodes.

```
com.xcs.spring.bean.MyBean@3b69e7d1
com.xcs.spring.bean.MyBean@815b41f
```

### 6. Sequence Diagram

The sequence diagram has three key parts.

#### Initialization Flow

- When `AnnotationConfigApplicationContext` is instantiated, it starts initialization.
- During this step, an `AnnotatedBeanDefinitionReader` is created. This reader parses annotated classes and turns them into `BeanDefinition` objects that Spring understands.
- Then Spring's core utility class `AnnotationConfigUtils` registers some default processors. The key one is `ConfigurationClassPostProcessor`, the core class that handles the `@Configuration` annotation.

~~~mermaid
sequenceDiagram 
ConfigurationApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>Start the context
AnnotationConfigApplicationContext-->>ConfigurationApplication: Return context<br>Return the instance
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext()<br>Constructor
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader: AnnotatedBeanDefinitionReader(registry)<br>Create the reader
AnnotatedBeanDefinitionReader-->>AnnotationConfigApplicationContext: Return reader
AnnotatedBeanDefinitionReader-->>AnnotationConfigUtils: registerAnnotationConfigProcessors(registry)<br>Register processors
AnnotationConfigUtils-->>AnnotationConfigUtils: registerAnnotationConfigProcessors(registry,source)<br>Register processors
AnnotationConfigUtils-->>AnnotationConfigUtils: registerPostProcessor(registry,definition,beanName)<br>Post processor
AnnotationConfigUtils-->>DefaultListableBeanFactory: registerBeanDefinition(beanName, beanDefinition)<br>Register bean definition
~~~

#### Registration Flow

- The `register` method of `AnnotationConfigApplicationContext` registers the configuration class (a class with `@Bean` methods) in the Spring context.
- `AnnotatedBeanDefinitionReader` parses these configuration classes and creates the matching `BeanDefinition` objects.
- These `BeanDefinition` objects are finally registered in `DefaultListableBeanFactory`. This factory is the core of the Spring IoC container. It manages all beans and their definitions.

~~~mermaid
sequenceDiagram 
ConfigurationApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>Start the context
AnnotationConfigApplicationContext-->>ConfigurationApplication: Return context<br>Return the context instance
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: register(componentClasses)<br>Register component classes
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader: register(componentClasses)<br>Reader registers classes
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: registerBean(beanClass)<br>Register bean class
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: doRegisterBean(beanClass,name,qualifiers, supplier,customizers)<br>Do the bean registration
AnnotatedBeanDefinitionReader-->>BeanDefinitionReaderUtils: registerBeanDefinition(definitionHolder,registry)<br>Register bean definition
BeanDefinitionReaderUtils-->>DefaultListableBeanFactory: registerBeanDefinition(beanName,beanDefinition)<br>Factory stores bean definition
~~~

#### Enhancement Flow

- When the container starts to refresh (through the `refresh` method), it starts creating and initializing beans.
- During this step, all `BeanFactoryPostProcessor` objects are called, especially `ConfigurationClassPostProcessor`.
- The main job of `ConfigurationClassPostProcessor` is to enhance `@Configuration` classes so that `@Bean` methods are proxied correctly. It uses `ConfigurationClassEnhancer` to enhance the class. This lets the class manage the bean lifecycle correctly and, for example, makes sure singleton beans are created only once.

~~~mermaid
sequenceDiagram 
ConfigurationApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>Start the context
AnnotationConfigApplicationContext-->>ConfigurationApplication: Return context<br>Return the context instance
AnnotationConfigApplicationContext-->>AnnotationConfigApplicationContext: refresh<br>Refresh the container
AnnotationConfigApplicationContext-->>AnnotationConfigApplicationContext: invokeBeanFactoryPostProcessors<br>Call post processors
AnnotationConfigApplicationContext-->>PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors(beanFactory,beanFactoryPostProcessors)<br>Delegate post processing
PostProcessorRegistrationDelegate-->>PostProcessorRegistrationDelegate: invokeBeanFactoryPostProcessors(postProcessors,beanFactory)<br>Run post processing
PostProcessorRegistrationDelegate-->>ConfigurationClassPostProcessor: postProcessBeanFactory(beanFactory)<br>Process configuration classes
ConfigurationClassPostProcessor-->>ConfigurationClassPostProcessor: enhanceConfigurationClasses(beanFactory)<br>Enhance configuration classes
ConfigurationClassPostProcessor-->>ConfigurationClassEnhancer: enhance(configClass,classLoader)<br>Run the enhancement
ConfigurationClassEnhancer-->>ConfigurationClassEnhancer: createClass(enhancer)<br>Create the enhanced class
ConfigurationClassEnhancer-->>ConfigurationClassPostProcessor: The enhanced Class
~~~

### 7. Source Code Analysis

Let's start with the main class. The context is `AnnotationConfigApplicationContext` (it configures the Spring container with Java annotations). We pass the `MyConfiguration` component class to its constructor. Then we get a bean of type `MyConfiguration` from the context. Finally we call the `myBean` method twice and print the results.

```java
public class ConfigurationApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyConfiguration configuration = context.getBean(MyConfiguration.class);

        System.out.println(configuration.myBean());
        System.out.println(configuration.myBean());
    }
}
```

The constructor `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` runs three steps.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    // Step 1. This constructor sets up the basic annotation reader and the classpath scanner
    this();
    // Step 2. This method registers the classes in the Spring context so they can be recognized and processed further.
    register(componentClasses);
    // Step 3. This method triggers the startup of the whole Spring container
    refresh();
}
```

#### Initialization Flow

First, step 1 in the `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor. The no-argument constructor of `AnnotationConfigApplicationContext` creates two core components: `AnnotatedBeanDefinitionReader` and `ClassPathBeanDefinitionScanner`.

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

In `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader`, it first gets the `Environment` (an abstraction of the configuration environment) from the registry. If there is none, it creates a new one. Then it calls another constructor to finish creating the `AnnotatedBeanDefinitionReader`.

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
    this(registry, getOrCreateEnvironment(registry));
}
```

In `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(registry,environment)`, it first sets the internal condition evaluator (`conditionEvaluator`). The condition evaluator handles annotations such as `@Conditional`. Then it calls the `registerAnnotationConfigProcessors` method of the utility class `AnnotationConfigUtils` to register the annotation config processors in the registry. One of them is `ConfigurationClassPostProcessor`, the core class that handles the `@Configuration` annotation.

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
    Assert.notNull(environment, "Environment must not be null");
    this.registry = registry;
    this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
    AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
}
```

In `org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry)`, the method simply calls another overload of `registerAnnotationConfigProcessors`. It passes the `registry` and a `null` value as the second argument.

```java
public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
    registerAnnotationConfigProcessors(registry, null);
}
```

In `org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry,source)`, the main job is to make sure `ConfigurationClassPostProcessor` is registered in the given registry. This way the `@Configuration` annotation and related features are handled correctly.

```java
public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
			BeanDefinitionRegistry registry, @Nullable Object source) {

    // ... [code omitted for brevity]

    Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);
    
    if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // ... [code omitted for brevity]

    return beanDefs;
}
```

#### Registration Flow

Next is step 2 in the `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor. The `org.springframework.context.annotation.AnnotationConfigApplicationContext#register` method lets us register one or more component classes (for example, classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, or `@Configuration`) in the Spring container.

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

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#register` method loops over each component class and calls another method to do the actual registration.

```java
public void register(Class<?>... componentClasses) {
    for (Class<?> componentClass : componentClasses) {
        registerBean(componentClass);
    }
}
```

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#registerBean(beanClass)` method is a quick way to register a bean class without any other settings or arguments.

```java
public void registerBean(Class<?> beanClass) {
    doRegisterBean(beanClass, null, null, null, null);
}
```

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean` method registers the given bean class and its settings in the Spring container. It generates the bean name, creates and registers the bean definition, and applies any needed proxy mode.

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
			@Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
			@Nullable BeanDefinitionCustomizer[] customizers) {

    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
    
    // ... [code omitted for brevity]
    
    String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));

    // ... [code omitted for brevity]

    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
}
```

The `org.springframework.beans.factory.support.BeanDefinitionReaderUtils#registerBeanDefinition` method registers the bean definition and all its aliases in the given `BeanDefinitionRegistry`. The Spring container uses this utility method internally. It makes sure the bean definition and its aliases are stored correctly, so they can be used later in the container lifecycle.

```java
public static void registerBeanDefinition(
    	BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
    	throws BeanDefinitionStoreException {

    // Register bean definition under primary name.
    String beanName = definitionHolder.getBeanName();
    registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

    // Register aliases for bean name, if any.
    String[] aliases = definitionHolder.getAliases();
    if (aliases != null) {
        for (String alias : aliases) {
            registry.registerAlias(beanName, alias);
        }
    }
}
```

#### Enhancement Flow

Next is step 3 in the `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor. In `org.springframework.context.support.AbstractApplicationContext#refresh`, we focus on `finishBeanFactoryInitialization(beanFactory)`. It instantiates all remaining non-lazy singleton beans. The other methods are not the focus of this reading, so we skip them.

```java
@Override
public void refresh() throws BeansException, IllegalStateException {
     // ... [code omitted for brevity]
     // Call factory processors registered as beans in the context
     invokeBeanFactoryPostProcessors(beanFactory);
     // ... [code omitted for brevity]
}
```

The `org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors` method hands the work to `PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors()`.

```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` method holds the core logic for how the Spring container handles `BeanFactoryPostProcessor` objects at startup.

```java
public static void invokeBeanFactoryPostProcessors(
      ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
	// ... [code omitted for brevity]
   	// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
	invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
    // ... [code omitted for brevity]
}
```

This overload of `org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` loops over the given `BeanFactoryPostProcessor` objects. Their `postProcessBeanFactory` method is an extension point that lets us step in and change the `BeanFactory`. Here, the classes defined with `@Configuration` are enhanced.

```java
private static void invokeBeanFactoryPostProcessors(
			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {

    for (BeanFactoryPostProcessor postProcessor : postProcessors) {
        StartupStep postProcessBeanFactory = beanFactory.getApplicationStartup().start("spring.context.bean-factory.post-process")
            .tag("postProcessor", postProcessor::toString);
        postProcessor.postProcessBeanFactory(beanFactory);
        postProcessBeanFactory.end();
    }
}
```

The `org.springframework.context.annotation.ConfigurationClassPostProcessor#postProcessBeanFactory` method enhances the classes marked with `@Configuration`.

```java
@Override
public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]
    enhanceConfigurationClasses(beanFactory);
    // ... [code omitted for brevity]
}
```

The `org.springframework.context.annotation.ConfigurationClassPostProcessor#enhanceConfigurationClasses` method enhances the classes marked with `@Configuration`. It makes sure their `@Bean` methods return the same instance on every call (unless they are prototype scoped). It does this by creating a proxy class with `ConfigurationClassEnhancer`. This proxy keeps the Spring IoC container behaving correctly, especially for configuration classes.

```java
public void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
    // ... [code omitted for brevity]

    ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
    for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
        AbstractBeanDefinition beanDef = entry.getValue();
        // If a @Configuration class gets proxied, always proxy the target class
        beanDef.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
        // Set enhanced subclass of the user-specified bean class
        Class<?> configClass = beanDef.getBeanClass();
        Class<?> enhancedClass = enhancer.enhance(configClass, this.beanClassLoader);
        if (configClass != enhancedClass) {
            // ... [code omitted for brevity]
            beanDef.setBeanClass(enhancedClass);
        }
    }
    // ... [code omitted for brevity]
}
```

In the `org.springframework.context.annotation.ConfigurationClassEnhancer#enhance` method, it first checks whether the class is already enhanced. If not, it creates a new enhancer and generates a proxy class. If it is already enhanced, it returns the original class. This enhancement makes sure the `@Bean` methods in a `@Configuration` class return the same instance on every call.

```java
public Class<?> enhance(Class<?> configClass, @Nullable ClassLoader classLoader) {
    if (EnhancedConfiguration.class.isAssignableFrom(configClass)) {
        // ... [code omitted for brevity]
        return configClass;
    }
    Class<?> enhancedClass = createClass(newEnhancer(configClass, classLoader));
    // ... [code omitted for brevity]
    return enhancedClass;
}
```

The `org.springframework.context.annotation.ConfigurationClassEnhancer#newEnhancer` method creates the `Enhancer` object that generates the proxy class for the given configuration class.

```java
private Enhancer newEnhancer(Class<?> configSuperClass, @Nullable ClassLoader classLoader) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(configSuperClass);
    enhancer.setInterfaces(new Class<?>[] {EnhancedConfiguration.class});
    enhancer.setUseFactory(false);
    enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
    enhancer.setStrategy(new BeanFactoryAwareGeneratorStrategy(classLoader));
    enhancer.setCallbackFilter(CALLBACK_FILTER);
    enhancer.setCallbackTypes(CALLBACK_FILTER.getCallbackTypes());
    return enhancer;
}
```

The `org.springframework.context.annotation.ConfigurationClassEnhancer#createClass` method uses the given `Enhancer` object to create the enhanced class and registers static callbacks for it.

```java
private Class<?> createClass(Enhancer enhancer) {
    Class<?> subclass = enhancer.createClass();
    // Registering callbacks statically (as opposed to thread-local)
    // is critical for usage in an OSGi environment (SPR-5932)...
    Enhancer.registerStaticCallbacks(subclass, CALLBACKS);
    return subclass;
}
```

The `org.springframework.context.annotation.ConfigurationClassEnhancer#createClass` method uses a static constant array named `CALLBACKS`. It holds three callback objects. The CGLIB library uses these callbacks to intercept and handle method calls on the enhanced (proxy) class.

```java
private static final Callback[] CALLBACKS = new Callback[] {
    new BeanMethodInterceptor(),
    new BeanFactoryAwareMethodInterceptor(),
    NoOp.INSTANCE
};
```

The `org.springframework.context.annotation.ConfigurationClassEnhancer.BeanMethodInterceptor#intercept` method holds the interception logic for `@Bean` methods. It makes sure they always return the right bean instance when called. It does this by checking the current method, resolving the bean name, handling special cases (such as `FactoryBeans` or scoped proxies), and resolving the real bean reference from the bean factory.

```java
public Object intercept(Object enhancedConfigInstance, Method beanMethod, Object[] beanMethodArgs,
				MethodProxy cglibMethodProxy) throws Throwable {

	// Get the related BeanFactory by reading the $$beanFactory field of the proxy class through reflection
	ConfigurableBeanFactory beanFactory = getBeanFactory(enhancedConfigInstance);
	// Work out the bean name for the current @Bean method
	String beanName = BeanAnnotationHelper.determineBeanNameFor(beanMethod);

	// Check whether the current @Bean method defines a scoped proxy
	if (BeanAnnotationHelper.isScopedProxy(beanMethod)) {
		String scopedBeanName = ScopedProxyCreator.getTargetBeanName(beanName);
		if (beanFactory.isCurrentlyInCreation(scopedBeanName)) {
			beanName = scopedBeanName;
		}
	}
    // FactoryBeans are special beans in Spring. They do not produce bean instances of themselves,
    // but produce other beans.
    // This block handles the case when a FactoryBean is requested,
    // and makes sure the actual bean created by the FactoryBean is returned,
    // not the FactoryBean itself.
	if (factoryContainsBean(beanFactory, BeanFactory.FACTORY_BEAN_PREFIX + beanName) &&
		factoryContainsBean(beanFactory, beanName)) {
		// This part is omitted. It handles returning and enhancing the bean created by the FactoryBean
	}

	// Check whether the current method is the factory method being invoked by the factory
	if (isCurrentlyInvokedFactoryMethod(beanMethod)) {
		// If so, call the original implementation of the method directly
		return cglibMethodProxy.invokeSuper(enhancedConfigInstance, beanMethodArgs);
	}

	// Try to resolve and return the bean reference from the bean factory
	return resolveBeanReference(beanMethod, beanMethodArgs, beanFactory, beanName);
}
```

The `org.springframework.context.annotation.ConfigurationClassEnhancer.BeanMethodInterceptor#resolveBeanReference` method makes sure the bean instance is fetched from the `BeanFactory` safely and correctly. It also handles all related edge cases and possible exceptions.

```java
private Object resolveBeanReference(Method beanMethod, Object[] beanMethodArgs,
				ConfigurableBeanFactory beanFactory, String beanName) {
    
    // Check whether the bean is currently being created
    boolean alreadyInCreation = beanFactory.isCurrentlyInCreation(beanName);
    try {
        // If the bean is being created, mark it as not in creation for now to avoid an exception
        if (alreadyInCreation) {
            beanFactory.setCurrentlyInCreation(beanName, false);
        }
        boolean useArgs = !ObjectUtils.isEmpty(beanMethodArgs);
        // For singleton beans, if the bean method arguments contain null, they are probably expected to be autowired
        if (useArgs && beanFactory.isSingleton(beanName)) {
            for (Object arg : beanMethodArgs) {
                if (arg == null) {
                    useArgs = false;
                    break;
                }
            }
        }
        // Based on the checks above, get the bean instance from the BeanFactory
        Object beanInstance = (useArgs ? beanFactory.getBean(beanName, beanMethodArgs) :
                               beanFactory.getBean(beanName));
        // Check whether the bean instance is compatible with the return type of the @Bean method
        if (!ClassUtils.isAssignableValue(beanMethod.getReturnType(), beanInstance)) {
            if (beanInstance.equals(null)) {
                // If the special NullBean instance is returned, handle it accordingly
                // ... [logging code]
                beanInstance = null;
            }
            else {
                // Throw an exception: a bean with the same name but an incompatible type overrides the current bean
                String msg = String.format("@Bean method %s.%s ...", 
                                           // ... [code omitted for brevity]
                                           );
                throw new IllegalStateException(msg);
            }
        }
        // If another @Bean method is being invoked, handle its dependency
        Method currentlyInvoked = SimpleInstantiationStrategy.getCurrentlyInvokedFactoryMethod();
        if (currentlyInvoked != null) {
            String outerBeanName = BeanAnnotationHelper.determineBeanNameFor(currentlyInvoked);
            beanFactory.registerDependentBean(beanName, outerBeanName);
        }
        return beanInstance;
    }
    finally {
        // Cleanup: restore the creation state of the bean
        if (alreadyInCreation) {
            beanFactory.setCurrentlyInCreation(beanName, true);
        }
    }
}
```

### 8. Notes

1. **Singleton guarantee**:
   - In a `@Configuration` class, if a method is marked with `@Bean` and called several times, it does not create the bean several times. It returns the same instance. This is because CGLIB enhances the `@Configuration` class to keep the bean a singleton.
2. **The proxyBeanMethods attribute**:
   - `@Configuration(proxyBeanMethods = false)` turns off CGLIB proxy generation. This can improve performance. But singleton bean references may become inconsistent, especially when you call other `@Bean` methods directly in the same configuration class.
3. **Avoid circular references**:
   - Avoid circular dependencies in `@Configuration` classes. They can cause problems when beans are created.
4. **Using `@Profile`**:
   - You can use `@Profile` with `@Configuration` to decide whether to load a configuration based on the current environment.
5. **Avoid `final`**:
   - A `@Configuration` class is enhanced by CGLIB, so it cannot be `final`. Its methods cannot be declared `final` either.

### 9. Summary

#### Best Practices Summary

1. **`proxyBeanMethods = true` (default)**:
   - When you call a method annotated with `@Bean` inside a `@Configuration` class, the Spring container makes sure the same bean instance is returned each time. A CGLIB proxy does this. It intercepts every call to the method and returns the singleton instance of the bean. This is why the two calls to `myBean()` in the `main` method of `ConfigurationApplication` return `MyBean` instances with the same hashcode.
2. **`proxyBeanMethods = false`**:
   - With this setting, the methods in the `@Configuration` class are not proxied. So if you call the same `@Bean` method several times inside the configuration class, a new bean instance is created each time. In the `main` method of `ConfigurationApplication`, the two calls to `myBean()` return `MyBean` instances with different hashcodes. This shows the two calls returned two different objects.

#### Source Code Analysis Summary

1. **Initialization flow**:
   - When a Spring application starts with `AnnotationConfigApplicationContext`, its constructor runs three main steps: initialization, registration, and refresh.
   - During initialization, the Spring context creates an `AnnotatedBeanDefinitionReader` and a `ClassPathBeanDefinitionScanner`. `AnnotatedBeanDefinitionReader` registers beans defined by annotations.
   - The `AnnotationConfigUtils.registerAnnotationConfigProcessors` method registers the needed post processors (such as `ConfigurationClassPostProcessor`) in the Spring container. This lets it recognize and process `@Configuration` classes.
2. **Registration flow**:
   - `AnnotatedBeanDefinitionReader` registers the configuration class (such as `MyConfiguration`) in the Spring container.
   - For each `@Bean` method, a `BeanDefinition` (bean definition) is created and registered in the `BeanDefinitionRegistry`.
3. **Enhancement flow**:
   - During container refresh, the `invokeBeanFactoryPostProcessors` method is called to run all `BeanFactoryPostProcessor` implementations.
   - `ConfigurationClassPostProcessor` is a key post processor. It finds configuration classes and enhances them.
   - For each class marked with `@Configuration`, a CGLIB proxy class is created through `ConfigurationClassEnhancer`.
   - This proxy makes sure every call to a `@Bean` method in the configuration class returns the same bean instance, unless it is prototype scoped.
   - After the application context has started, for any requested bean, the proxied `@Bean` method returns the existing bean instance from the Spring container instead of creating a new one.

### 10. FAQ

1. **How do I choose between full mode and lite mode in @Configuration?**

   `@Configuration` has two modes: `full` and `lite`. They differ in features and performance. Knowing their pros and cons helps you pick the right one for your case.

   + Full mode

     - How to enable: do not set `proxyBeanMethods` in `@Configuration`, or set it to `true`.

     - Behavior: when a `@Bean` method calls another `@Bean` method inside the configuration class, Spring makes sure the singleton bean from the container is returned, not a new instance. CGLIB proxies do this.

     - Pros: keeps singleton behavior. Singleton beans in the container stay singletons when called inside the configuration class.

     - Cons: CGLIB must create a subclass of the configuration class. This can cost some performance and add startup time. It may not work with libraries that expect the real class instead of its CGLIB proxy.

   + Lite mode

     - How to enable: set `proxyBeanMethods` to `false` in `@Configuration`.

     - Behavior: CGLIB proxies are turned off. Calls between `@Bean` methods work like normal Java method calls, and a new instance is created each time.

     - Pros: faster startup, because the configuration class does not need CGLIB enhancement. For simple injection, this mode can be shorter and more direct.

     - Cons: does not keep singleton behavior. If one `@Bean` method calls another `@Bean` method, a new bean instance is created.


   + How to choose

     - If your configuration must make sure that beans called inside the configuration class are always the singleton beans in the Spring container, choose full mode.

     - If your configuration class only defines beans and injects dependencies, and does not need to share singleton instances between its methods, choose lite mode.

     - If you care about startup performance, especially in cloud or microservice environments, lite mode may fit better, because it avoids the extra CGLIB work.
