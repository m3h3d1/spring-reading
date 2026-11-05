## @EnableLoadTimeWeaving

- [@EnableLoadTimeWeaving](#enableloadtimeweaving)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Annotation Source](#4-annotation-source)
    - [5. Best Practices](#5-best-practices)
    - [6. Source Code Analysis](#6-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

`@EnableLoadTimeWeaving` is an annotation provided by the Spring Framework. It turns on Load Time Weaving (LTW). With LTW, aspect logic is woven in while classes are loaded, so you can add cross-cutting features such as logging and performance monitoring.

### 3. Main Features

1. **Enable Load Time Weaving (LTW)**

   + Weaves aspect logic in while classes are loaded, without changing the source code or using a special compiler.

2. **Support cross-cutting concerns**

   + With load time weaving, cross-cutting concerns are kept apart from the core business logic of the application. This makes the code more modular and easier to maintain.

3. **Flexible and configurable**

   + You can use the rich syntax and features of AspectJ to define pointcuts and aspect logic in a flexible way, for many kinds of applications.

4. **No dependence on source code**

   + Load time weaving does not need changes to the source code or a special compiler. So you can add aspect logic to an existing application easily, without refactoring the existing code.

### 4. Annotation Source

The `@EnableLoadTimeWeaving` annotation turns on Load Time Weaving in the Spring application context. With this annotation, you can configure load time weaving easily. It works like the `<context:load-time-weaver>` element in Spring XML configuration. You can also use the `aspectjWeaving()` attribute to control whether AspectJ-based weaving is enabled. This gives flexible configuration options.

```java
/**
 * Activates a Spring {@link LoadTimeWeaver} for this application context, available as a bean named "loadTimeWeaver",
 * similar to the {@code <context:load-time-weaver>} element in Spring XML.
 *
 * <p>To be used on a @{@link org.springframework.context.annotation.Configuration Configuration} class;
 * the simplest example is as follows
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving
 * public class AppConfig {
 *
 *     // application-specific &#064;Bean definitions...
 * }</pre>
 *
 * The example above is equivalent to the following Spring XML configuration
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;context:load-time-weaver/&gt;
 *
 *     &lt;!-- application-specific &lt;bean&gt; definitions --&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 *
 * <h2>The {@code LoadTimeWeaverAware} interface</h2>
 * Any bean that implements the {@link org.springframework.context.weaving.LoadTimeWeaverAware LoadTimeWeaverAware} interface
 * will automatically receive the {@code LoadTimeWeaver} reference; for example, Spring's JPA bootstrapping support.
 *
 * <h2>Customizing the {@code LoadTimeWeaver}</h2>
 * The default weaver is determined automatically, see {@link DefaultContextLoadTimeWeaver}.
 *
 * <p>To customize the weaver used, the {@code @Configuration} class can implement the {@link LoadTimeWeavingConfigurer} interface, and through the
 * {@code #getLoadTimeWeaver} method return a custom {@code LoadTimeWeaver} instance
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving
 * public class AppConfig implements LoadTimeWeavingConfigurer {
 *
 *     &#064;Override
 *     public LoadTimeWeaver getLoadTimeWeaver() {
 *         MyLoadTimeWeaver ltw = new MyLoadTimeWeaver();
 *         ltw.addClassTransformer(myClassFileTransformer);
 *         // ...
 *         return ltw;
 *     }
 * }</pre>
 *
 * The example above can be compared with the following Spring XML configuration
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;context:load-time-weaver weaverClass="com.acme.MyLoadTimeWeaver"/&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 *
 * The difference between the code example and the XML example is that it actually instantiates the {@code MyLoadTimeWeaver} type, which means it can also configure the instance,
 * for example by calling the {@code #addClassTransformer} method. This shows that the code-based configuration approach is more flexible through direct programmatic access.
 *
 * <h2>Enabling AspectJ-based weaving</h2>
 * AspectJ load-time weaving can be enabled with the {@link #aspectjWeaving()} attribute, which causes the
 * {@linkplain org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter AspectJ class transformer} to be registered through {@link LoadTimeWeaver#addTransformer}. AspectJ weaving is activated by default if a
 * "META-INF/aop.xml" resource is present on the classpath. Example
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving(aspectjWeaving=ENABLED)
 * public class AppConfig {
 * }</pre>
 *
 * The example above can be compared with the following Spring XML configuration
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;context:load-time-weaver aspectj-weaving="on"/&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 *
 * The two examples are equivalent, with one important exception: in the XML case, when {@code aspectj-weaving} is "on",
 * the functionality of {@code <context:spring-configured>} is enabled automatically. When using {@code @EnableLoadTimeWeaving(aspectjWeaving=ENABLED)}
 * this does not happen. Instead, you must add {@code @EnableSpringConfigured} explicitly (it is included in the {@code spring-aspects} module).
 *
 * @author Chris Beams
 * @since 3.1
 * @see LoadTimeWeaver
 * @see DefaultContextLoadTimeWeaver
 * @see org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LoadTimeWeavingConfiguration.class)
public @interface EnableLoadTimeWeaving {

    /**
     * Whether to enable AspectJ weaving.
     */
    AspectJWeaving aspectjWeaving() default AspectJWeaving.AUTODETECT;

    /**
     * Options for enabling AspectJ weaving.
     */
    enum AspectJWeaving {

        /**
         * Enables Spring-based AspectJ load-time weaving.
         */
        ENABLED,

        /**
         * Turns off Spring-based AspectJ load-time weaving (even if a "META-INF/aop.xml" resource is present on the classpath).
         */
        DISABLED,

        /**
         * Enables AspectJ load-time weaving if a "META-INF/aop.xml" resource is present on the classpath.
         * If there is no such resource, AspectJ load-time weaving is turned off.
         */
        AUTODETECT;
    }

}
```

### 5. Best Practices

This example uses Load Time Weaving. First, it creates an annotation-based Spring application context and configures the components of the application with the `AppConfig`
class. Then it creates a plain `MyService` instance and calls its `foo` method.

```java
public class EnableLoadTimeWeavingDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MyService myService = new MyService();
        myService.foo();
        context.close();
    }
}
```

The `@Configuration` annotation marks this as a configuration class, and the `@EnableLoadTimeWeaving` annotation turns on load time weaving.

```java
@Configuration
@EnableLoadTimeWeaving
public class AppConfig {

}
```

This defines an aspect class `MyLTWAspect` that implements Load Time Weaving. The `@Aspect`
annotation marks the class as an aspect, and it defines an around advice method `around`
that runs some logic before and after the target method call. In this method, it first prints the name of the target method, then calls the original method, and then prints the return value. The `@Pointcut`
annotation defines a pointcut `ltwPointcut()` that sets the target methods to weave, here all public methods of the `com.xcs.spring.MyService` class.

```java
@Aspect
public class MyLTWAspect {

    @Around("ltwPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // Logic that runs before the method call
        System.out.println("Before Method " + pjp.getSignature().getName());
        // Call the original method
        Object result = pjp.proceed();
        // Logic that runs after the method call
        System.out.println("After Method " + pjp.getSignature().getName());
        return result;
    }

    @Pointcut("execution(public * com.xcs.spring.MyService.*(..))")
    public void ltwPointcut() {
    }
}
```

This defines the rules and aspect settings of Load Time Weaving. The `<weaver>` element says that only classes in specific packages of the application are woven, here all classes in the `com.xcs.spring` package and its sub-packages. Then the `<aspects>` element says which aspect to weave, here `com.xcs.spring.MyLTWAspect`.

```java
<!DOCTYPE aspectj PUBLIC "-//AspectJ//DTD//EN" "https://www.eclipse.org/aspectj/dtd/aspectj.dtd">
<aspectj>

    <weaver>
        <!-- only weave classes in our application-specific packages -->
        <include within="com.xcs.spring..*"/>
    </weaver>

    <aspects>
        <!-- weave in just this aspect -->
        <aspect name="com.xcs.spring.MyLTWAspect"/>
    </aspects>

</aspectj>
```

The `MyService` class defines a simple method `foo()`.

```java
public class MyService {

    public void foo() {
        System.out.println("foo...");
    }
}
```

These are the startup arguments. They use the AspectJ Weaver and Spring Instrumentation to do load time weaving. They make sure load time weaving is on when the application starts, so the AspectJ aspect can intercept and handle method calls.

> If you keep the jar files in your own location (such as aspectjweaver-1.9.7.jar and spring-instrument-5.3.10.jar), make sure the paths are correct when you refer to these jar files. The paths in the startup arguments or configuration file must match the real location of the jar files. Otherwise load time weaving or other features may not work.

```shell
java -javaagent:D:\tools\repository\org\aspectj\aspectjweaver\1.9.7\aspectjweaver-1.9.7.jar -javaagent:D:\tools\repository\org\springframework\spring-instrument\5.3.10\spring-instrument-5.3.10.jar -Dfile.encoding=UTF-8 com.xcs.spring.EnableLoadTimeWeavingDemo
```

Run result: a `MyService` object created directly with the `new` operator. Each time the `foo()`
method is called, it first prints a message that the method was called, and then runs the original method logic (prints "foo..."). This shows that the logic defined in the aspect `MyLTWAspect`
runs before and after the target method call. Both the bean from the Spring container and the object created directly are intercepted.

```java
Before Method
foo
foo...
After Method
foo
```

### 6. Source Code Analysis

The `LoadTimeWeavingConfiguration` class registers a `LoadTimeWeaver` bean to turn on Load-Time Weaving. When you use the `@EnableLoadTimeWeaving` annotation in an application, this configuration class is imported automatically. It checks the attributes of the `EnableLoadTimeWeaving` annotation to decide whether to turn on AspectJ weaving, and creates the matching `LoadTimeWeaver` instance from the configuration. If the user provides a custom `LoadTimeWeavingConfigurer` instance, it uses that instance. Otherwise it creates a default `DefaultContextLoadTimeWeaver` instance as the `LoadTimeWeaver`. Based on the settings in the `EnableLoadTimeWeaving` annotation, it decides whether to turn on AspectJ weaving, and calls the methods in `AspectJWeavingEnabler` to do the weaving.

```java
/**
 * {@code @Configuration} class that registers a {@link LoadTimeWeaver} bean.
 *
 * <p>This configuration class is imported automatically when the {@link EnableLoadTimeWeaving} annotation is used.
 * See the javadoc of {@code @EnableLoadTimeWeaving} for full usage details.
 * 
 * <p>Author: Chris Beams
 * 
 * @since 3.1
 * @see LoadTimeWeavingConfigurer
 * @see ConfigurableApplicationContext#LOAD_TIME_WEAVER_BEAN_NAME
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class LoadTimeWeavingConfiguration implements ImportAware, BeanClassLoaderAware {

    @Nullable
    private AnnotationAttributes enableLTW;

    @Nullable
    private LoadTimeWeavingConfigurer ltwConfigurer;

    @Nullable
    private ClassLoader beanClassLoader;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        // Get the attributes of the @EnableLoadTimeWeaving annotation
        this.enableLTW = AnnotationConfigUtils.attributesFor(importMetadata, EnableLoadTimeWeaving.class);
        if (this.enableLTW == null) {
            throw new IllegalArgumentException(
                    "@EnableLoadTimeWeaving is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired(required = false)
    public void setLoadTimeWeavingConfigurer(LoadTimeWeavingConfigurer ltwConfigurer) {
        // Set the user-defined LoadTimeWeavingConfigurer instance
        this.ltwConfigurer = ltwConfigurer;
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        // Set the class loader
        this.beanClassLoader = beanClassLoader;
    }

    /**
     * Registers the LoadTimeWeaver bean.
     */
    @Bean(name = ConfigurableApplicationContext.LOAD_TIME_WEAVER_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LoadTimeWeaver loadTimeWeaver() {
        Assert.state(this.beanClassLoader != null, "No ClassLoader set");
        LoadTimeWeaver loadTimeWeaver = null;

        if (this.ltwConfigurer != null) {
            // The user provided a custom LoadTimeWeaver instance
            loadTimeWeaver = this.ltwConfigurer.getLoadTimeWeaver();
        }

        if (loadTimeWeaver == null) {
            // No custom LoadTimeWeaver was provided -> use the default one
            loadTimeWeaver = new DefaultContextLoadTimeWeaver(this.beanClassLoader);
        }

        if (this.enableLTW != null) {
            // Get the setting that enables AspectJ weaving
            AspectJWeaving aspectJWeaving = this.enableLTW.getEnum("aspectjWeaving");
            switch (aspectJWeaving) {
                case DISABLED:
                    // AspectJ weaving is disabled -> do nothing
                    break;
                case AUTODETECT:
                    if (this.beanClassLoader.getResource(AspectJWeavingEnabler.ASPECTJ_AOP_XML_RESOURCE) == null) {
                        // No aop.xml on the classpath -> treat as 'disabled'
                        break;
                    }
                    // aop.xml on the classpath -> enable weaving
                    AspectJWeavingEnabler.enableAspectJWeaving(loadTimeWeaver, this.beanClassLoader);
                    break;
                case ENABLED:
                    // Enable AspectJ weaving
                    AspectJWeavingEnabler.enableAspectJWeaving(loadTimeWeaver, this.beanClassLoader);
                    break;
            }
        }

        return loadTimeWeaver;
    }
}
```

This is a post processor of `AspectJWeavingEnabler`. It implements several interfaces, including `BeanFactoryPostProcessor`, `BeanClassLoaderAware`, `LoadTimeWeaverAware`, and `Ordered`. Its main job is to register the AspectJ `ClassPreProcessorAgentAdapter` in the Spring application context and link it with the default `LoadTimeWeaver`. The `enableAspectJWeaving` method turns on AspectJ weaving. The `AspectJClassBypassingClassFileTransformer` class is a decorator that skips AspectJ classes, to avoid possible linkage errors.

```java
/**
 * Post processor that registers the AspectJ {@link org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter}
 * in the default {@link org.springframework.instrument.classloading.LoadTimeWeaver} of the Spring application context.
 * Used to turn on AspectJ weaving.
 * 
 * <p>Authors: Juergen Hoeller, Ramnivas Laddad
 * 
 * @since 2.5
 */
public class AspectJWeavingEnabler
        implements BeanFactoryPostProcessor, BeanClassLoaderAware, LoadTimeWeaverAware, Ordered {

    /**
     * The location of the {@code aop.xml} resource.
     */
    public static final String ASPECTJ_AOP_XML_RESOURCE = "META-INF/aop.xml";

    @Nullable
    private ClassLoader beanClassLoader;

    @Nullable
    private LoadTimeWeaver loadTimeWeaver;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
        this.loadTimeWeaver = loadTimeWeaver;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // Enable AspectJ weaving
        enableAspectJWeaving(this.loadTimeWeaver, this.beanClassLoader);
    }

    /**
     * Enables AspectJ weaving with the given {@link LoadTimeWeaver}.
     * 
     * @param weaverToUse the LoadTimeWeaver to apply (or {@code null} to use the default weaver)
     * @param beanClassLoader the class loader to create a default weaver for, if needed
     */
    public static void enableAspectJWeaving(
            @Nullable LoadTimeWeaver weaverToUse, @Nullable ClassLoader beanClassLoader) {
        if (weaverToUse == null) {
            if (InstrumentationLoadTimeWeaver.isInstrumentationAvailable()) {
                // If Instrumentation can be used, create an InstrumentationLoadTimeWeaver
                weaverToUse = new InstrumentationLoadTimeWeaver(beanClassLoader);
            }
            else {
                throw new IllegalStateException("No LoadTimeWeaver available");
            }
        }
        // Add a ClassFileTransformer that bypasses the processing of AspectJ classes, to avoid possible LinkageErrors
        weaverToUse.addTransformer(
                new AspectJClassBypassingClassFileTransformer(new ClassPreProcessorAgentAdapter()));
    }

    /**
     * Decorator for ClassFileTransformer that disables the processing of AspectJ classes, to avoid possible LinkageErrors.
     * 
     * @see org.springframework.context.annotation.LoadTimeWeavingConfiguration
     */
    private static class AspectJClassBypassingClassFileTransformer implements ClassFileTransformer {

        private final ClassFileTransformer delegate;

        public AspectJClassBypassingClassFileTransformer(ClassFileTransformer delegate) {
            this.delegate = delegate;
        }

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (className.startsWith("org.aspectj") || className.startsWith("org/aspectj")) {
                // If it is an AspectJ class, return the original bytecode directly
                return classfileBuffer;
            }
            // Otherwise, call the delegate ClassFileTransformer to process the bytecode
            return this.delegate.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        }
    }
}
```
