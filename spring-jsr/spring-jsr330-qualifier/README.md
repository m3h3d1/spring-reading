## @Qualifier

- [@Qualifier](#qualifier)
  - [1. Overview](#1-overview)
  - [2. Annotation Description](#2-annotation-description)
  - [3. Annotation Source](#3-annotation-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
    - [@Qualifier Registration](#qualifier-registration)
    - [@Qualifier Resolution](#qualifier-resolution)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [@Qualifier Registration](#qualifier-registration-1)
    - [@Qualifier Resolution](#qualifier-resolution-1)
      - [Prerequisites](#prerequisites)
      - [Resolution Entry](#resolution-entry)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Annotation Description

The `@Qualifier` annotation is a standard Java annotation from JSR 330: Dependency Injection for Java. It is mainly used to remove ambiguity in dependency injection. When there are several beans or instances of the same type to choose from, it tells the container clearly which one to inject.

### 3. Annotation Source

`@Qualifier` is a base annotation. It is mainly used to create other custom annotations. These new annotations are usually used to qualify an injection and remove ambiguity. This matches the definition and purpose of `@Qualifier` in Spring and JSR 330.

```java
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Qualifier {

}
```

### 4. Main Features

1. **Remove ambiguity**
   + When the container has several beans of the same type, `@Qualifier` says which specific bean should be injected.
2. **Use with `@Autowired` and `@Inject`**
   - In Spring, `org.springframework.beans.factory.annotation.Qualifier` is usually used with `org.springframework.beans.factory.annotation.Autowired`. This removes ambiguity when a bean is resolved from the Spring container.
   - In the JSR 330 specification (for example in CDI), `javax.inject.Qualifier` is a meta-annotation. It is used to define new annotations as qualifiers. These custom qualifier annotations can then be used with `javax.inject.Inject` to remove ambiguity.
3. **Custom qualifier annotations**
   + Especially in JSR 330, `@Qualifier` is used to create other custom annotations. These new annotations can then be used as qualifiers.
4. **Clearer code**
   + With `@Qualifier` or a custom annotation based on it, the code is more descriptive. It is easier to see which specific bean should be injected.

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (this class configures the Spring container with Java annotations). We pass the `MyConfiguration` component class as the constructor argument. Then we get a bean of type `MessageController` from the Spring context and call its `showMessage` method.

```java
public class QualifierApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MessageController messageController = context.getBean(MessageController.class);
        messageController.showMessage();
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

We define two custom annotations, `@Email` and `@SMS`. Both are marked with `@Qualifier`. This means both can be used as qualifier annotations.

```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {

}

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface SMS {

}
```

`EmailServiceImpl` and `SMSServiceImpl` both implement the `MessageService` interface. They have the qualifier annotations `@Email` and `@SMS`. So they can be told apart clearly when they are injected.

```java
public interface MessageService {
    String getMessage();
}

@Email
@Named
public class EmailServiceImpl implements MessageService {
    @Override
    public String getMessage() {
        return "Email message";
    }
}

@SMS
@Named
public class SMSServiceImpl implements MessageService {
    @Override
    public String getMessage() {
        return "SMS message";
    }
}
```

`MessageController` injects two implementations of `MessageService`: one for Email and one for SMS. The custom qualifier annotations `@Email` and `@SMS` make sure the right service implementation is injected into each field. The `showMessage` method shows the messages returned by these two service implementations.

```java
@Controller
public class MessageController {

    @Inject
    @Email
    private MessageService emailService;

    @Inject
    @SMS
    private MessageService smsService;

    public void showMessage() {
        System.out.println("EmailService: " + emailService.getMessage());
        System.out.println("SMSService: " + smsService.getMessage());
    }
}
```

The result shows that the `@Email` qualifier makes sure `EmailServiceImpl` is injected into the `emailService` field. The `@SMS` qualifier makes sure `SMSServiceImpl` is injected into the `smsService` field. The `@Qualifier` annotation plays a key role here. It makes sure the right service implementation is injected, so the result is as expected.

```java
EmailService: Email message
SMSService: SMS message
```

### 6. Sequence Diagram

#### @Qualifier Registration

~~~mermaid
sequenceDiagram
Title: @Qualifier registration sequence diagram

QualifierApplication->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext(componentClasses)
Note over AnnotationConfigApplicationContext: Context initialization starts, with some component classes passed in
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext:AnnotationConfigApplicationContext()
Note over AnnotationConfigApplicationContext: The context constructor is called
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader:AnnotatedBeanDefinitionReader(registry)
Note over AnnotatedBeanDefinitionReader: Create a bean definition reader for the given registry
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:AnnotatedBeanDefinitionReader(registry,environment)
Note over AnnotatedBeanDefinitionReader: The reader constructor uses a specific environment
AnnotatedBeanDefinitionReader->>AnnotationConfigUtils:registerAnnotationConfigProcessors(registry)
Note over AnnotationConfigUtils: Register the annotation config processors
AnnotationConfigUtils->>AnnotationConfigUtils:registerAnnotationConfigProcessors(registry,source)
Note right of AnnotationConfigUtils: Register the annotation config processors
AnnotationConfigUtils->>QualifierAnnotationAutowireCandidateResolver:QualifierAnnotationAutowireCandidateResolver()
Note over QualifierAnnotationAutowireCandidateResolver: Create a new qualifier autowire candidate resolver
QualifierAnnotationAutowireCandidateResolver->>QualifierAnnotationAutowireCandidateResolver:this.qualifierTypes.add("javax.inject.Qualifier");
Note right of QualifierAnnotationAutowireCandidateResolver: Add javax.inject.Qualifier to the list of qualifier types
QualifierAnnotationAutowireCandidateResolver->>AnnotationConfigUtils:Return Resolver
Note right of AnnotationConfigUtils: The resolver is created and is now returned to the caller
AnnotationConfigUtils->>DefaultListableBeanFactory:setAutowireCandidateResolver(autowireCandidateResolver)
Note over DefaultListableBeanFactory: Set the autowire candidate resolver in the bean factory
~~~

#### @Qualifier Resolution

~~~mermaid
sequenceDiagram
Title: @Qualifier resolution sequence diagram

InjectionMetadata->>AutowiredFieldElement:inject(bean,beanName,pvs)
Note over AutowiredFieldElement: Start injecting the field

AutowiredFieldElement->>AutowiredFieldElement:resolveFieldValue(field, bean, beanName)
Note over AutowiredFieldElement: Try to resolve the value of the field

AutowiredFieldElement->>DefaultListableBeanFactory:resolveDependency(desc,beanName,autowiredBeanNames,typeConverter)
Note over DefaultListableBeanFactory: Resolve the dependency of the field

DefaultListableBeanFactory->>DefaultListableBeanFactory:doResolveDependency(descriptor,requestingBeanName,autowiredBeanNames,typeConverter)
Note over DefaultListableBeanFactory: Do the dependency resolving

DefaultListableBeanFactory->>DefaultListableBeanFactory:findAutowireCandidates(beanName,type,descriptor)
Note over DefaultListableBeanFactory: Find possible autowire candidates

DefaultListableBeanFactory->>DefaultListableBeanFactory:isAutowireCandidate(candidate,descriptor)
Note over DefaultListableBeanFactory: Check if the candidate bean is a suitable autowire candidate

DefaultListableBeanFactory->>DefaultListableBeanFactory:isAutowireCandidate(beanName,descriptor,resolver)
Note right of DefaultListableBeanFactory: Check further, with the resolver

DefaultListableBeanFactory->>DefaultListableBeanFactory:isAutowireCandidate(beanName,mbd,descriptor,resolver)
Note right of DefaultListableBeanFactory: Check further, with the given bean definition

DefaultListableBeanFactory->>QualifierAnnotationAutowireCandidateResolver:isAutowireCandidate(holder, descriptor)
Note over QualifierAnnotationAutowireCandidateResolver: Decide whether it is an autowire candidate based on the @Qualifier annotation

QualifierAnnotationAutowireCandidateResolver->>QualifierAnnotationAutowireCandidateResolver:checkQualifiers(bdHolder,annotationsToSearch)
Note over QualifierAnnotationAutowireCandidateResolver: Check all related qualifier annotations

QualifierAnnotationAutowireCandidateResolver->>QualifierAnnotationAutowireCandidateResolver:isQualifier(annotationType)
Note over QualifierAnnotationAutowireCandidateResolver: Check if the given annotation type is a valid qualifier
~~~

### 7. Source Code Analysis

#### @Qualifier Registration

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (this class configures the Spring container with Java annotations). We pass the `MyConfiguration` component class as the constructor argument. Then we get a bean of type `MessageController` from the Spring context and call its `showMessage` method.

```java
public class QualifierApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MessageController messageController = context.getBean(MessageController.class);
        messageController.showMessage();
    }
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor does three steps. This time we focus on `this()`.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext()` method initializes two core components. One reads bean definitions from annotations (`AnnotatedBeanDefinitionReader`), which is the focus of this analysis. The other scans the classpath and detects bean components automatically (`ClassPathBeanDefinitionScanner`).

```java
public AnnotationConfigApplicationContext() {
    StartupStep createAnnotatedBeanDefReader = this.getApplicationStartup().start("spring.context.annotated-bean-reader.create");
    this.reader = new AnnotatedBeanDefinitionReader(this);
    createAnnotatedBeanDefReader.end();
    this.scanner = new ClassPathBeanDefinitionScanner(this);
}
```

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(registry)` method calls another constructor.

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
    this(registry, getOrCreateEnvironment(registry));
}
```

In the `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(registry,environment)` method, there is an important call, `registerAnnotationConfigProcessors`. It registers a series of post-processors in the container. These post-processors are key to handle various annotations (such as `@Inject` and `@Qualifier`).

```java
public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
    Assert.notNull(environment, "Environment must not be null");
    this.registry = registry;
    this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
    AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
}
```

The `org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry)` method calls another overloaded method.

```java
public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
    registerAnnotationConfigProcessors(registry, null);
}
```

In the `org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(registry,source)` method: in the Spring autowiring mechanism, when several candidate beans can be injected at one place, there must be a way to decide which bean is the best candidate. `AutowireCandidateResolver` makes this decision.

```java
public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
			BeanDefinitionRegistry registry, @Nullable Object source) {

    DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
    if (beanFactory != null) {
        // ... [code omitted for brevity]
        if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
            beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        }
    }

    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#QualifierAnnotationAutowireCandidateResolver()` constructor sets some basic configuration for the resolver. The most important part is that it tries to load the JSR-330 `@Qualifier` annotation and adds it to the `qualifierTypes` set. This way the annotation is handled correctly in later dependency injection. If JSR-330 is not available, the resolver simply skips this step.

```java
public QualifierAnnotationAutowireCandidateResolver() {
    // ... [code omitted for brevity]
    try {
        this.qualifierTypes.add((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Qualifier",
                 		QualifierAnnotationAutowireCandidateResolver.class.getClassLoader()));
    }
    catch (ClassNotFoundException ex) {
        // JSR-330 API not available - simply skip.
    }
}
```

#### @Qualifier Resolution

##### Prerequisites

In Spring, `AutowiredAnnotationBeanPostProcessor` handles many dependency injection annotations, including the JSR-330 `@Inject` annotation. To understand `@Inject` and how it works with `@Qualifier`, it is important to study this post-processor. In short, to fully understand how `@Inject` and `@Qualifier` work together, reading the `@Inject` article is necessary. It gives a deep understanding of how these annotations are handled at the key stages of the bean lifecycle.

1. **@Inject annotation source analysis**:
   - This article explains how the `@Inject` annotation works. It starts from the introduction of the JSR-330 specification, then shows how to use it correctly in Spring, and how it differs from other annotations such as `@Autowired`. It gives a full view.
   - 🔗 [@Inject annotation link](https://github.com/xuchengsheng/spring-reading/blob/master/spring-jsr/spring-jsr330-inject)

##### Resolution Entry

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency` method first tries to get a lazy resolution proxy. If it cannot, it goes on to resolve the dependency. `doResolveDependency` is the method that does the actual resolving.

```java
public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                                @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
	// ... [code omitted for brevity]
    
    Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
        descriptor, requestingBeanName);
    if (result == null) {
        result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
    }
    return result;
}
```

The main goal of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method is to resolve the given dependency descriptor (`DependencyDescriptor`) and try to find a suitable bean for this dependency.

```java
public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName,
			@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

    // ... [code omitted for brevity]

    try {
        // ... [code omitted for brevity]

        // Step 1. Find the matching beans from the dependency descriptor
        Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
        
        // ... [code omitted for brevity]
    }
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#findAutowireCandidates` method first gets all possible bean names for the given type. Then, for each possible candidate bean, it checks whether the bean is a suitable autowire candidate. If it is, the bean is added to the result. At the end, the method returns all suitable candidate beans that were found.

```java
protected Map<String, Object> findAutowireCandidates(
			@Nullable String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {

    // Get all possible bean names for the required type, including beans in all parent factories
    String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
        this, requiredType, true, descriptor.isEager());

    // ... [code omitted for brevity]

    // Go through all candidate bean names
    for (String candidate : candidateNames) {
        // If the candidate bean is not the bean being looked up itself, and it is a suitable autowire candidate
        if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, descriptor)) {
            // Add this candidate bean to the result
            addCandidateEntry(result, candidate, descriptor, requiredType);
        }
    }

    // ... [code omitted for brevity]

    // Return all candidate beans that were found
    return result; 
}
```

In the `org.springframework.beans.factory.support.DefaultListableBeanFactory#isAutowireCandidate(beanName, descriptor)` method, first `getAutowireCandidateResolver()` is called. It returns the resolver that was set earlier. The resolver decides which bean is an autowire candidate. Especially when there are several possible candidates, it takes `@Qualifier` and other related annotations into account. Then the method calls another overloaded version of `isAutowireCandidate` and passes the resolver.

```java
@Override
public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
    throws NoSuchBeanDefinitionException {
    return isAutowireCandidate(beanName, descriptor, getAutowireCandidateResolver());
}
```

In the `org.springframework.beans.factory.support.DefaultListableBeanFactory#isAutowireCandidate(beanName,descriptor,resolver)` method, first `getMergedLocalBeanDefinition(bdName)` is called to get the definition of the dependent bean. Then the method calls another overloaded version of `isAutowireCandidate` and passes the merged bean definition.

```java
protected boolean isAutowireCandidate(
			String beanName, DependencyDescriptor descriptor, AutowireCandidateResolver resolver)
			throws NoSuchBeanDefinitionException {

    String bdName = BeanFactoryUtils.transformedBeanName(beanName);
    if (containsBeanDefinition(bdName)) {
        return isAutowireCandidate(beanName, getMergedLocalBeanDefinition(bdName), descriptor, resolver);
    }
    // ... [code omitted for brevity]
}
```

In the `org.springframework.beans.factory.support.DefaultListableBeanFactory#isAutowireCandidate(beanName, mbd,descriptor, resolver)` method, first a `BeanDefinitionHolder` is created. It is a handy data structure that holds a `BeanDefinition`, the bean name, and the aliases at the same time. This is useful when resolving autowire candidates, because sometimes we need to know the name and aliases of a bean. Then the method calls the `isAutowireCandidate` method of the resolver, and passes the `BeanDefinitionHolder` and the dependency descriptor.

```java
protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd,
			DependencyDescriptor descriptor, AutowireCandidateResolver resolver) {

    // ... [code omitted for brevity]
    BeanDefinitionHolder holder = (beanName.equals(bdName) ?
                                   this.mergedBeanDefinitionHolders.computeIfAbsent(beanName,
                                                                                    key -> new BeanDefinitionHolder(mbd, beanName, getAliases(bdName))) :
                                   new BeanDefinitionHolder(mbd, beanName, getAliases(bdName)));
    return resolver.isAutowireCandidate(holder, descriptor);
}
```

The main goal of the `org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#isAutowireCandidate` method is to add stronger support for the `@Qualifier` annotation. If a bean or dependency has `@Qualifier` and it sets a bean name, only the bean with the matching name is seen as an autowire candidate. If no name is set but other attributes or meta-annotations are set, only the beans that match these attributes or meta-annotations are seen as autowire candidates.

```java
@Override
public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
    // First do the basic match check with the method of the parent class.
    boolean match = super.isAutowireCandidate(bdHolder, descriptor);
    
    // If the basic check passes, check further whether the annotations in bdHolder and descriptor match.
    if (match) {
        // Check if the qualifiers of the candidate bean match the annotations of the dependency descriptor.
        match = checkQualifiers(bdHolder, descriptor.getAnnotations());
        
        if (match) {
            // Get the method parameter related to the dependency (if any, for example when the dependency is a parameter of a setter method).
            MethodParameter methodParam = descriptor.getMethodParameter();
            
            // If there is a related method parameter, check further.
            if (methodParam != null) {
                Method method = methodParam.getMethod();
                
                // If the method returns void (such as a setter method), check if its annotations match the qualifiers of the candidate bean.
                if (method == null || void.class == method.getReturnType()) {
                    match = checkQualifiers(bdHolder, methodParam.getMethodAnnotations());
                }
            }
        }
    }
    
    // Return the final match result.
    return match;
}
```

The main goal of the `org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#checkQualifiers` method is to make sure the bean to inject matches all related `@Qualifier` annotations and meta-annotations. If one annotation or meta-annotation does not match the bean definition, the bean is not a suitable candidate for the current injection point.

```java
protected boolean checkQualifiers(BeanDefinitionHolder bdHolder, Annotation[] annotationsToSearch) {
    // If the annotation array is empty, return true directly
    if (ObjectUtils.isEmpty(annotationsToSearch)) {
        return true;
    }
    
    SimpleTypeConverter typeConverter = new SimpleTypeConverter();
    for (Annotation annotation : annotationsToSearch) {
        Class<? extends Annotation> type = annotation.annotationType();
        boolean checkMeta = true;
        boolean fallbackToMeta = false;
        
        // Check if the current annotation is @Qualifier or a custom qualifier annotation
        if (isQualifier(type)) {
            // If it is, and it matches the bean definition
            if (!checkQualifier(bdHolder, annotation, typeConverter)) {
                // If it does not match, the meta-annotations may need to be checked
                fallbackToMeta = true; 
            } else {
                // If it matches, no further check of the meta-annotations is needed
                checkMeta = false; 
            }
        }
        
        // If it is not a qualifier annotation or does not match the bean definition, but has a meta-annotation that is a qualifier annotation
        if (checkMeta) {
            boolean foundMeta = false;
            for (Annotation metaAnn : type.getAnnotations()) {
                Class<? extends Annotation> metaType = metaAnn.annotationType();
                // Check if the meta-annotation is a qualifier annotation
                if (isQualifier(metaType)) {
                    foundMeta = true;
                    // Accept the match of the meta-annotation only if the @Qualifier annotation has a value or other conditions are met
                    if ((fallbackToMeta && ObjectUtils.isEmpty(AnnotationUtils.getValue(metaAnn))) ||
                        !checkQualifier(bdHolder, metaAnn, typeConverter)) {
                        // The meta-annotation does not match
                        return false; 
                    }
                }
            }
            if (fallbackToMeta && !foundMeta) {
                return false; // A meta-annotation is needed, but none was found
            }
        }
    }
    
    // All annotations match
    return true;
}
```

The main goal of the `org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#isQualifier` method is to decide whether the given annotation is a "qualifier" annotation, such as `@Qualifier`. It does this by checking whether the annotation is in the known `qualifierTypes` set, or whether it carries one of these qualifier annotations.

```java
protected boolean isQualifier(Class<? extends Annotation> annotationType) {
    // Go through the known qualifier annotation types
    for (Class<? extends Annotation> qualifierType : this.qualifierTypes) {
        // If the given annotation is a known qualifier annotation, or it carries such a meta-annotation
        if (annotationType.equals(qualifierType) || annotationType.isAnnotationPresent(qualifierType)) {
            return true; // It is a qualifier annotation
        }
    }
    return false; // It is not a qualifier annotation
}
```

### 8. Notes

1. **Make sure the injection is correct**
   + `@Qualifier` is mainly used to solve the problem of several beans of the same type in the Spring container. Without `@Qualifier`, Spring cannot decide which bean to inject. When you use `@Qualifier`, make sure the given qualifier name really exists. Otherwise, Spring throws an exception.
2. **Use with other annotations**
   +  `@Qualifier` is usually used with `@Autowired` or `@Inject`. Make sure you use it in the right place (field, setter method, or constructor).
3. **Custom qualifier annotations**
   + You can create your own annotations and use `@Qualifier` as a meta-annotation. This way you can create qualifier annotations with a specific name or other meaning, which makes the code easier to read.
4. **String names**
   +  `@Qualifier` uses a string value to set the name of the bean by default. This means that if you rename a bean or change its name, you must also change all `@Qualifier` annotations that use this bean name.
5. **Use with Java configuration**
   + When you create beans with Java configuration, you can use the name of the `@Bean` method as the qualifier name. This makes Java configuration and `@Qualifier` more consistent.
6. **Relation to `@Primary`**
   + If you use both `@Primary` and `@Qualifier`, `@Qualifier` has the higher priority. That is, if a bean is marked with `@Primary` but an injection point uses `@Qualifier`, the bean set by `@Qualifier` is used.
7. **Name and type matching**
   + `@Qualifier` is mainly used to match by name, but Spring still checks the type of the matching bean. So if the bean name matches but the type does not, you still get an exception.
8. **Compatibility with JSR-330**
   + The Spring `@Qualifier` annotation is compatible with the `javax.inject.Qualifier` annotation in JSR-330 (the Java dependency injection specification). But when you use the JSR-330 standard, make sure the dependency injection provider (such as Spring) supports it correctly.

### 9. Summary

#### Best Practices Summary

1. **Clear configuration**
   + Use `AnnotationConfigApplicationContext` as the Spring container and give the configuration class as the constructor argument. This makes the container initialization clear.
2. **Use component scan**
   + With `@ComponentScan`, components in the given package and its sub-packages are scanned automatically. This reduces the work of registering beans by hand and makes the code shorter and more efficient.
3. **Custom qualifier annotations**
   + To solve the problem of several beans of the same type, two custom qualifier annotations are defined: `@Email` and `@SMS`. This makes the code easier to read and shows the injection intent more clearly.
4. **Accurate service injection**
   + In `MessageController`, the custom `@Email` and `@SMS` annotations are used together with `@Inject`. This makes sure the right `MessageService` implementation is injected into each field.
5. **Clear output**
   + The output of the `showMessage` method clearly shows the effect of `@Qualifier`. It makes sure the different service implementations are injected correctly.
6. **Make good use of Java configuration**
   + With `@Configuration` and `@ComponentScan` in Java configuration, the container initialization and bean registration are more direct and simple.
7. **Extensibility of annotations**
   + Using `@Qualifier` as a meta-annotation for custom annotations shows the flexibility of Spring and the extensibility of annotations.
8. **Keep the code clean and modular**
   + Each class and interface has a clear job, and the code is organized in a clear and modular way. This makes the code easier to maintain, read, and reuse.

#### Source Code Analysis Summary

1. **Application startup and context initialization**
   + When `AnnotationConfigApplicationContext` is created, it takes a configuration class (such as `MyConfiguration`) as the argument to initialize the Spring context. At startup, the beans are taken from the Spring context and used.
2. **Construction of AnnotationConfigApplicationContext**
   + In its constructor, `AnnotationConfigApplicationContext` does several key steps. The most important is `this()`. It initializes two components: `AnnotatedBeanDefinitionReader` (reads bean definitions from annotations) and `ClassPathBeanDefinitionScanner` (scans the classpath and detects bean components automatically).
3. **Initialization of AnnotatedBeanDefinitionReader**
   + In its constructor, `AnnotatedBeanDefinitionReader` calls `registerAnnotationConfigProcessors`. This method registers a series of post-processors in the container. These post-processors are key to handle various annotations (such as `@Inject` and `@Qualifier`).
4. **Registration of the Qualifier annotation**
   + When a `QualifierAnnotationAutowireCandidateResolver` instance is created, it tries to load the JSR-330 `@Qualifier` annotation and adds it to the `qualifierTypes` set. This way Spring can handle this annotation correctly in later dependency injection.
5. **Dependency resolving**
   + When Spring tries to resolve a dependency, it enters `DefaultListableBeanFactory#resolveDependency`. This method first tries to get a lazy resolution proxy. If it cannot, it tries to resolve the dependency. This involves the `doResolveDependency` method.
6. **Autowire candidate check**
   + In `findAutowireCandidates`, Spring first finds all possible bean names, and then checks each possible candidate bean to see if it is a suitable autowire candidate. This check involves the `isAutowireCandidate` method.
7. **Use AutowireCandidateResolver to decide on an autowire candidate**
   + The `isAutowireCandidate` method decides whether a bean is an autowire candidate. When there are several possible candidate beans, `AutowireCandidateResolver` (here, the instance is `QualifierAnnotationAutowireCandidateResolver`) makes the decision. It takes `@Qualifier` and other related annotations into account.
8. **Handling the @Qualifier annotation**
   + The main job of `QualifierAnnotationAutowireCandidateResolver` is to add stronger support for the `@Qualifier` annotation. It makes sure the bean to inject matches all related `@Qualifier` annotations and meta-annotations. If one annotation or meta-annotation does not match the bean definition, the bean is not a suitable candidate for the current injection point.
9. **Qualifier check**
   + The `isQualifier` method decides whether the given annotation is a qualifier annotation, such as `@Qualifier`. It does this by checking whether the annotation is in the known `qualifierTypes` set, or whether it carries one of these qualifier annotations.
