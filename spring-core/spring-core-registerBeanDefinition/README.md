## Bean Definition Registration

- [Bean Definition Registration](#bean-definition-registration)
  - [1. Overview](#1-overview)
  - [2. Background Knowledge](#2-background-knowledge)
  - [3. Best Practices](#3-best-practices)
  - [4. Source Code Analysis](#4-source-code-analysis)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

+ **ClassPathBeanDefinitionScanner**
  + The [ClassPathBeanDefinitionScanner](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-classPathBeanDefinitionScanner/README.md) class scans a package and its sub-packages on the classpath. It finds matching classes and registers them as bean definitions. This gives you component scanning and autowiring. You can set filters to decide which classes become beans. This makes the application easier to maintain and extend.
+ **AnnotatedBeanDefinitionReader**
  + The [AnnotatedBeanDefinitionReader](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-annotatedBeanDefinitionReader/README.md) class reads and parses annotated bean definitions. It is used for annotation-based configuration. You mark a Java class as a Spring component, and the container scans and registers it. You do not need to configure the bean definition by hand.

### 3. Best Practices

With the `AnnotationConfigApplicationContext` container, bean definitions are registered in two ways: by hand and by package scanning. One bean (the `MyBean` class) is registered by hand. Package scanning registers all classes in the given package that have `@Component` or one of its derived annotations. At the end, the names of all registered bean definitions are printed.

```java
public class RegisterBeanDefinitionApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // Register the bean
        context.register(MyBean.class);
        // Scan the package
        context.scan("com.xcs.spring");
        // Print the bean definitions
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
```

`MyBean` is a simple Java class. It has no annotations or other markers.

```java
public class MyBean {
    
}
```

These classes use `@Controller`, `@Service` and `@Repository`. These are special Spring annotations that mark components in different layers.

```java
@Controller
public class MyController {
    
}

@Service
public class MyService {

}

@Repository
public class MyRepository {

}
```

The result shows that the Spring application context loaded the configuration and registered all bean definitions, both the manual one and the annotated ones.

PS: The first four are bean definitions that Spring registers internally.

```java
beanDefinitionName = org.springframework.context.annotation.internalConfigurationAnnotationProcessor
beanDefinitionName = org.springframework.context.annotation.internalAutowiredAnnotationProcessor
beanDefinitionName = org.springframework.context.event.internalEventListenerProcessor
beanDefinitionName = org.springframework.context.event.internalEventListenerFactory
beanDefinitionName = myBean
beanDefinitionName = myController
beanDefinitionName = myRepository
beanDefinitionName = myService
```

### 4. Source Code Analysis

In the `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext()` method, two key components of the `AnnotationConfigApplicationContext` instance are created: `AnnotatedBeanDefinitionReader` and `ClassPathBeanDefinitionScanner`. The first handles annotated bean definitions. The second registers bean definitions by scanning the classpath.

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#register` method uses `AnnotatedBeanDefinitionReader` to register a set of component classes. It parses them into bean definitions in the Spring container.

>  **The detailed source analysis is in another article about the [AnnotatedBeanDefinitionReader](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-annotatedBeanDefinitionReader/README.md) class.**

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

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#scan` method uses `ClassPathBeanDefinitionScanner` to scan the given base packages. It parses the classes it finds into bean definitions in the Spring container.

>  **The detailed source analysis is in another article about the [ClassPathBeanDefinitionScanner](https://github.com/xuchengsheng/spring-reading/blob/master/spring-beans/spring-bean-classPathBeanDefinitionScanner/README.md) class.**

```java
@Override
public void scan(String... basePackages) {
    Assert.notEmpty(basePackages, "At least one base package must be specified");
    StartupStep scanPackages = this.getApplicationStartup().start("spring.context.base-packages.scan")
        .tag("packages", () -> Arrays.toString(basePackages));
    this.scanner.scan(basePackages);
    scanPackages.end();
}
```
