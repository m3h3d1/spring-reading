## @Value

- [@Value](#value)
  - [1. Overview](#1-overview)
  - [2. Annotation Description](#2-annotation-description)
  - [3. Annotation Source](#3-annotation-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [Prerequisites](#prerequisites)
    - [Collection Phase](#collection-phase)
    - [Injection Phase](#injection-phase)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133815435) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [@Value source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-value)

### 2. Annotation Description

The `@Value` annotation is very useful. It injects property values from configuration files into fields or method parameters of Java classes. This separates configuration from code, so the application is easier to configure and maintain.

### 3. Annotation Source

`@Value` has been a core Spring annotation since version 3.1. Its main goal is to inject values from many sources (such as properties files and system properties) directly into Spring-managed beans. We do not need to write code to resolve these values.

```java
/**
 * Annotation used at the field or method/constructor parameter level
 * that indicates a default value expression for the annotated element.
 *
 * Typically used for expression-driven or property-driven dependency injection.
 * Also supported for dynamic resolution of handler method parameters, for example in Spring MVC.
 *
 * A common use case is to assign default field values using SpEL (Spring Expression Language)
 * expressions such as `#{systemProperties.myProp}`.
 * Alternatively, values can be injected using property placeholders such as `${my.app.myProp}`.
 *
 * Note that actual processing of the @Value annotation is performed by a BeanPostProcessor,
 * which means that you **cannot** use @Value within BeanPostProcessor or BeanFactoryPostProcessor types.
 * Please consult the javadoc for the AutowiredAnnotationBeanPostProcessor class
 * (which, by default, checks for the presence of this annotation).
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see AutowiredAnnotationBeanPostProcessor
 * @see Autowired
 * @see org.springframework.beans.factory.config.BeanExpressionResolver
 * @see org.springframework.beans.factory.support.AutowireCandidateResolver#getSuggestedValue
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    /**
     * The actual value expression such as `#{systemProperties.myProp}`
     * or property placeholder such as `${my.app.myProp}`.
     */
    String value();
}
```

### 4. Main Features

1. **Property injection**
   + Injects values from different sources (such as properties files and system properties) directly into Spring-managed beans.
2. **Expression support**:
   - SpEL (Spring Expression Language) expressions: for example, `#{systemProperties.myProp}` reads the value named `myProp` from the system properties.
   - Property placeholders: for example, `${my.app.myProp}` reads the property `my.app.myProp` from a configured source such as `application.properties` or `application.yml`.
3. **Dynamic value resolution**
   + A static value can only be set at startup. `@Value` can resolve dynamic expressions, so it gives dynamic values to fields or constructor parameters.
4. **Fields, method parameters, constructor parameters, and annotations**
   + It can be applied to all of these elements to provide the values they need.
5. **Works with other annotations**
   + `@Value` only injects values. It is often used with other Spring annotations such as `@Component`, `@Service`, and `@Controller` to create beans that are fully managed and configured by Spring.
6. **Needs a property resolver**
   + To resolve expressions in `@Value` correctly, the Spring application context needs a property resolver such as `PropertySourcesPlaceholderConfigurer`. Spring Boot projects configure this by default.

### 5. Best Practices

First, look at the entry class. It uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass the `MyConfiguration` component class to the constructor.

```java
public class ValueApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
    }
}
```

Here we use `@Bean` to define one bean, so that `MyService` is run by the Spring container. We also use `@PropertySource` to load properties from the `application.properties` file on the classpath. We can define properties in this file and read them in the application, for example through the `Environment` object.

```java
@Configuration
@PropertySource("classpath:application.properties")
public class MyConfiguration {

    @Bean
    public MyService myService(){
        return new MyService();
    }
}
```

The `application.properties` file is in the `src/main/resources` directory. Add the following content.

```properties
app.name=My Spring Application
app.servers=server1,server2,server3
app.val1=10
app.val2=20
```

The `MyService` class shows five different ways to inject properties with `@Value`.

```java
public class MyService implements InitializingBean {

    /**
     * Inject a value directly
     */
    @Value("Some String Value")
    private String someString;

    /**
     * Inject a value from the properties file
     */
    @Value("${app.name}")
    private String appName;

    /**
     * Use a default value
     */
    @Value("${app.description:I am the default value}")
    private String appDescription;

    /**
     * Inject a list
     */
    @Value("#{'${app.servers}'.split(',')}")
    private List<String> servers;

    /**
     * Use Spring SpEL
     */
    @Value("#{${app.val1} + ${app.val2}}")
    private int sumOfValues;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Direct value: " + someString);
        System.out.println("Value from properties file: " + appName);
        System.out.println("Default value: " + appDescription);
        System.out.println("List and property injection: " + servers);
        System.out.println("Spring SpEL: " + sumOfValues);
    }
}
```

The output shows that all `@Value` annotations are resolved and the expected values are injected.

```java
Direct value: Some String Value
Value from properties file: My Spring Application
Default value: I am the default value
List and property injection: [server1, server2, server3]
Spring SpEL: 30
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @Value annotation sequence diagram
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>Apply the bean definition post processors
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>Process the merged bean definition
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)<br>Find the autowiring metadata
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:buildAutowiringMetadata(clazz)<br>Build the autowiring metadata
AutowiredAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalFields(clazz,fc)<br>Process the local fields of the class
ReflectionUtils->>AutowiredAnnotationBeanPostProcessor:Parse the fields with the @Value annotation
AutowiredAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>Process the local methods of the class
ReflectionUtils->>AutowiredAnnotationBeanPostProcessor:Parse the methods with the @Value annotation
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:injectionMetadataCache.put(cacheKey, metadata)<br>Put the metadata into the cache
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,bw)<br>Populate the bean properties
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessProperties(pvs,bean,beanName)<br>Post process the bean properties
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)<br>Find the autowiring metadata again
Note right of AutowiredAnnotationBeanPostProcessor:<br>Get the injection metadata from the cache
AutowiredAnnotationBeanPostProcessor->>InjectionMetadata:inject(bean, beanName, pvs)<br>Run the real property injection
InjectionMetadata->>AutowiredFieldElement:inject(target, beanName, pvs)<br>Inject the specific field element
AutowiredFieldElement->>AutowiredFieldElement:resolveFieldValue(field,bean,beanName)<br>Resolve the field value
AutowiredFieldElement->>DefaultListableBeanFactory:resolveDependency(desc, beanName, autowiredBeanNames, typeConverter)<br>Resolve the field dependency
DefaultListableBeanFactory->>AutowiredFieldElement:Return the resolved value<br>Return the resolved property value
AutowiredFieldElement->>Field:field.set(bean, value)<br>Set the value of the bean field
~~~

### 7. Source Code Analysis

#### Prerequisites

In Spring, `AutowiredAnnotationBeanPostProcessor` is the key class that handles annotations such as `@Value`. It implements the two interfaces below. To understand how `@Value` works, it helps to study this class and these interfaces. They let us step into important stages of the bean lifecycle, which allows property injection and other related work.

1. **`MergedBeanDefinitionPostProcessor` interface**
   + Its `postProcessMergedBeanDefinition` method lets a post processor change the merged bean definition. A merged bean definition already includes the properties of all parent bean definitions. For `@Value`, this step usually collects the `@Value` information that must be resolved and prepares it for later processing.
   + 🔗  [MergedBeanDefinitionPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. **`InstantiationAwareBeanPostProcessor` interface**
   + This interface has several callback methods. They let a post processor step into bean creation before and after instantiation. The `postProcessProperties` method lets the post processor work on the bean properties. For `@Value`, this usually means resolving the expression or property placeholder in the annotation and injecting the resolved value into the bean.
   + 🔗  [InstantiationAwareBeanPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor)

#### Collection Phase

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessMergedBeanDefinition` method makes sure the bean definition matches its expected autowiring metadata.

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    // For the given bean name and type, first try to find the related InjectionMetadata, which may hold the injection info of the fields and methods of this bean
    InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
    
    // Use the found InjectionMetadata to check that the configured members in the bean definition match the expected injection metadata.
    metadata.checkConfigMembers(beanDefinition);
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#findAutowiringMetadata` method always returns up-to-date `InjectionMetadata` for the given bean name and class. It uses a cache to improve performance.

```java
private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
    // If beanName is empty, use the class name as the cache key.
    String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    // First try to get the InjectionMetadata from the concurrent cache.
    InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
    // Check whether the metadata needs a refresh.
    if (InjectionMetadata.needsRefresh(metadata, clazz)) {
        // Use double-checked locking to stay thread safe.
        synchronized (this.injectionMetadataCache) {
            metadata = this.injectionMetadataCache.get(cacheKey);
            if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                // If there is old metadata, clear it.
                if (metadata != null) {
                    metadata.clear(pvs);
                }
                // Build new InjectionMetadata for the given class.
                metadata = buildAutowiringMetadata(clazz);
                // Put the new metadata into the cache.
                this.injectionMetadataCache.put(cacheKey, metadata);
            }
        }
    }
    // Return the found or newly built metadata.
    return metadata;
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#buildAutowiringMetadata` method scans the fields and methods of the class and all its parent classes. It finds every field and method with an autowiring annotation and creates one `InjectionMetadata` object for them.

```java
private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
    // Check whether the class has autowiring annotations. If not, return empty InjectionMetadata.
    if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
        return InjectionMetadata.EMPTY;
    }

    // Initialize the list that holds the injected elements.
    List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
    Class<?> targetClass = clazz;

    do {
        // The list of elements to inject in the current class.
        final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

        // Process all fields of the class.
        ReflectionUtils.doWithLocalFields(targetClass, field -> {
            // Look for an autowiring annotation on the field.
            MergedAnnotation<?> ann = findAutowiredAnnotation(field);
            if (ann != null) {
                // Ignore static fields.
                if (Modifier.isStatic(field.getModifiers())) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Autowired annotation is not supported on static fields: " + field);
                    }
                    return;
                }
                boolean required = determineRequiredStatus(ann);
                // Create a new AutowiredFieldElement and add it to the list.
                currElements.add(new AutowiredFieldElement(field, required));
            }
        });

        // Process all methods of the class.
        ReflectionUtils.doWithLocalMethods(targetClass, method -> {
            Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
            if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                return;
            }
            // Look for an autowiring annotation on the method.
            MergedAnnotation<?> ann = findAutowiredAnnotation(bridgedMethod);
            if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                // Ignore static methods.
                if (Modifier.isStatic(method.getModifiers())) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Autowired annotation is not supported on static methods: " + method);
                    }
                    return;
                }
                // Only methods with parameters are processed.
                if (method.getParameterCount() == 0) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Autowired annotation should only be used on methods with parameters: " +
                                    method);
                    }
                }
                boolean required = determineRequiredStatus(ann);
                PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                // Create a new AutowiredMethodElement and add it to the list.
                currElements.add(new AutowiredMethodElement(method, required, pd));
            }
        });

        // Add the injected elements of the current class to the start of the total list.
        elements.addAll(0, currElements);
        // Process the parent class.
        targetClass = targetClass.getSuperclass();
    }
    // Loop until the Object class.
    while (targetClass != null && targetClass != Object.class);

    // Return the new InjectionMetadata created for the element list.
    return InjectionMetadata.forElements(elements, clazz);
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#autowiredAnnotationTypes` field tells `AutowiredAnnotationBeanPostProcessor` which annotations it should handle. When the Spring container reads a bean definition and creates the bean, `AutowiredAnnotationBeanPostProcessor` processes any annotation on a field, method, or constructor of the bean that is in the `autowiredAnnotationTypes` set.

```java
public AutowiredAnnotationBeanPostProcessor() {
   this.autowiredAnnotationTypes.add(Value.class);
   // ... [code omitted for brevity]
}
```

#### Injection Phase

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties` method post processes the bean properties. It handles property injection done through annotations such as `@Value`.

```java
@Override
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
    // Get the InjectionMetadata for the bean name and class.
    // It holds all fields and methods of this bean that need injection.
    InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
    
    try {
        // Use the InjectionMetadata to do the real property injection.
        metadata.inject(bean, beanName, pvs);
    }
    // If a BeanCreationException happens during injection, rethrow it.
    catch (BeanCreationException ex) {
        throw ex;
    }
    // Catch other exceptions and throw them as BeanCreationException with detailed error info.
    catch (Throwable ex) {
        throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
    }
    // Return the original PropertyValues, because this method focuses on dependency injection, not on changing properties.
    return pvs;
}
```

The `org.springframework.beans.factory.annotation.InjectionMetadata#inject` method injects all elements that need injection (for example, fields or methods with `@Value`) into the target bean.

```java
public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // Get the checked elements. Usually, all elements are checked once during the initialization phase.
    Collection<InjectedElement> checkedElements = this.checkedElements;

    // If there are checked elements, use them. Otherwise, use all injected elements.
    Collection<InjectedElement> elementsToIterate =
        (checkedElements != null ? checkedElements : this.injectedElements);

    // If there are elements to inject...
    if (!elementsToIterate.isEmpty()) {
        // Loop over each element and inject it into the target bean.
        for (InjectedElement element : elementsToIterate) {
            // Run the injection for each element (field or method).
            element.inject(target, beanName, pvs);
        }
    }
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject` method first checks whether the field value is cached. If it is, the value comes from the cache. If not, the value is resolved again. Then the method makes sure the field is accessible (especially for private fields) and sets the resolved value on the field of the target bean.

```java
@Override
protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // Step 1. Get the Field object that represents the field with the @Autowired annotation.
    Field field = (Field) this.member;

    Object value;
    // Step 2. If the field value is already cached (resolved earlier), try to get it from the cache.
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
        // Step 3. If the field value is not cached, resolve it directly.
        value = resolveFieldValue(field, bean, beanName);
    }

    // Step 4. If the resolved value is not null...
    if (value != null) {
        // Step 4.1. Make the field accessible. This is needed especially when the field is private.
        ReflectionUtils.makeAccessible(field);
        // Step 4.2. Inject the resolved value into the field of the target bean.
        field.set(bean, value);
    }
}
```

Let us start with step 3 of the `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject` method. The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#resolveFieldValue` method resolves the field value from the Spring bean factory with `beanFactory.resolveDependency`.

```java
@Nullable
private Object resolveFieldValue(Field field, Object bean, @Nullable String beanName) {
    // ... [code omitted for brevity]
    Object value;
    try {
        // Resolve the field value from the Spring bean factory with `beanFactory.resolveDependency`
        value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
    }
    catch (BeansException ex) {
        throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
    }
    // ... [code omitted for brevity]
    return value;
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency` method first tries to get a lazy resolution proxy. If there is none, it resolves the dependency directly. `doResolveDependency` does the real resolving work.

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

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method first gets the annotation value from the `DependencyDescriptor`. Then it handles string property placeholders and SpEL expressions in it. Finally, it converts the value to the type of the target field or parameter and injects it.

```java
@Nullable
public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName,
                                  @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

    // ... [other code omitted for brevity]

    try {
        // Try to resolve the dependency with a shortcut
        Object shortcut = descriptor.resolveShortcut(this);
        if (shortcut != null) {
            return shortcut;
        }

        // Get the type of the dependency
        Class<?> type = descriptor.getDependencyType();
        
        // Step 1. Get the suggested value of the dependency, such as the value of the @Value annotation
        Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);

        // If the suggested value is a String
        if (value instanceof String) {
            // Step 2. Resolve embedded values, such as property placeholders
            String strVal = resolveEmbeddedValue((String) value);
            
            // Get the BeanDefinition related to the bean name
            BeanDefinition bd = (beanName != null && containsBean(beanName) ?
                                     getMergedBeanDefinition(beanName) : null);
                                     
            // Step 3. Evaluate the bean definition string, such as SpEL expressions
            value = evaluateBeanDefinitionString(strVal, bd);
        }
        
        TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
        try {
            // Step 4. Get the type converter and convert the type if needed
            return converter.convertIfNecessary(value, type, descriptor.getTypeDescriptor());
        }
        catch (UnsupportedOperationException ex) {
            // ... [other code omitted for brevity]
        }

        // ... [other code omitted for brevity]

    }
    // ... [other code omitted for brevity]
}
```

Now step 1 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method. The `org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#getSuggestedValue` method resolves the annotation value related to the `DependencyDescriptor`, especially the `@Value` annotation. If a field or method parameter has `@Value`, it takes the value or expression from the annotation.

```java
@Override
@Nullable
public Object getSuggestedValue(DependencyDescriptor descriptor) {
    // Look for the value given by the @Value annotation in the annotations of the descriptor
    Object value = findValue(descriptor.getAnnotations());
    
    // If nothing is found, check whether a method parameter is linked to this descriptor
    if (value == null) {
        MethodParameter methodParam = descriptor.getMethodParameter();
        
        if (methodParam != null) {
            // If there is a method parameter, look for the @Value value in its annotations
            value = findValue(methodParam.getMethodAnnotations());
        }
    }
    // Return the found value, or null if nothing is found
    return value;
}
```

The `org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#findValue` method finds the value of the `@Value` annotation in the given annotations and returns it. If it finds nothing, it returns null.

```java
protected Object findValue(Annotation[] annotationsToSearch) {
    if (annotationsToSearch.length > 0) {   // qualifier annotations have to be local
        AnnotationAttributes attr = AnnotatedElementUtils.getMergedAnnotationAttributes(
            AnnotatedElementUtils.forAnnotations(annotationsToSearch), this.valueAnnotationType);
        if (attr != null) {
            return extractValue(attr);
        }
    }
    return null;
}
```

The `org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#extractValue` method takes the value of the `@Value` annotation straight from the `AnnotationAttributes` object. If there is no value, it throws an exception.

```java
protected Object extractValue(AnnotationAttributes attr) {
    Object value = attr.get(AnnotationUtils.VALUE);
    if (value == null) {
        throw new IllegalStateException("Value annotation must have a value attribute");
    }
    return value;
}
```

When we use `@Value("${app.description:I am the default value}")` on a field, Spring tries to resolve this property placeholder at runtime. When the container injects this field, it uses `QualifierAnnotationAutowireCandidateResolver` (or other related post processors) to get and resolve the value. In our best practice example, the `extractValue` method takes the placeholder out of the annotation attributes. The return value is `"${app.description:I am the default value}"`. Spring's property resolver then processes this value. It finds the real value or uses the default value, and finally injects it into the `appDescription` field.

```java
${app.description:I am the default value}
```

Now step 2 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method. The `org.springframework.beans.factory.support.AbstractBeanFactory#resolveEmbeddedValue` method resolves embedded values in the given string. It loops over all registered `StringValueResolver` resolvers and resolves the string value one after another. This handles multiple embedded values or references. For example, a property such as `${property}` in the string is resolved by a registered resolver into the real property value.

```java
@Override
@Nullable
public String resolveEmbeddedValue(@Nullable String value) {
    // Initial check: if the given value is null, return null
    if (value == null) {
        return null;
    }
    
    String result = value;
    
    // Loop over all embedded value resolvers
    for (StringValueResolver resolver : this.embeddedValueResolvers) {
        // Use the current resolver to resolve the value in result
        result = resolver.resolveStringValue(result);
        
        // If the resolved value is null, return null
        if (result == null) {
            return null;
        }
    }
    
    // Return the final value after all resolvers
    return result;
}
```

Now step 3 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method. The `org.springframework.beans.factory.support.AbstractBeanFactory#evaluateBeanDefinitionString` method evaluates the given string value. It mainly handles strings that may contain Spring Expression Language (SpEL) expressions. First, it checks whether a `beanExpressionResolver` is available to resolve SpEL. If so, it may get the scope of the bean definition (if a bean definition is given). Then it uses the `beanExpressionResolver` to evaluate the string value with the related scope context.

```java
@Nullable
protected Object evaluateBeanDefinitionString(@Nullable String value, @Nullable BeanDefinition beanDefinition) {
    // If no bean expression resolver is set, return the original value
    if (this.beanExpressionResolver == null) {
        return value;
    }

    Scope scope = null;

    // If a bean definition is given
    if (beanDefinition != null) {
        // Get the scope of the bean
        String scopeName = beanDefinition.getScope();

        // If the scope name is not null, try to get the scope from the registered scopes
        if (scopeName != null) {
            scope = getRegisteredScope(scopeName);
        }
    }

    // Use the bean expression resolver to evaluate the given value and return the result
    // This handles cases such as Spring EL
    return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
}
```

The `org.springframework.context.expression.StandardBeanExpressionResolver#evaluate` method parses and evaluates the given value (which may be a Spring EL expression). To improve performance, it uses caches for expressions parsed earlier and for evaluation contexts. It first gets or parses the expression from the cache. Then it prepares an evaluation context and evaluates the expression with it. This context can access many things related to the Spring container, such as beans and environment properties.

```java
@Override
@Nullable
public Object evaluate(@Nullable String value, BeanExpressionContext evalContext) throws BeansException {
    // If the given value is empty or has no content, return it directly
    if (!StringUtils.hasLength(value)) {
        return value;
    }
    try {
        // Try to get the expression from the cache
        Expression expr = this.expressionCache.get(value);
        // If the expression is not in the cache, parse the value with the expression parser and put it into the cache
        if (expr == null) {
            expr = this.expressionParser.parseExpression(value, this.beanExpressionParserContext);
            this.expressionCache.put(value, expr);
        }
        
        // Try to get the evaluation context from the cache
        StandardEvaluationContext sec = this.evaluationCache.get(evalContext);
        // If the evaluation context is not in the cache, create a new one and configure it
        if (sec == null) {
            sec = new StandardEvaluationContext(evalContext);
            // Add property accessors to support access to properties of specific types
            sec.addPropertyAccessor(new BeanExpressionContextAccessor());
            sec.addPropertyAccessor(new BeanFactoryAccessor());
            sec.addPropertyAccessor(new MapAccessor());
            sec.addPropertyAccessor(new EnvironmentAccessor());
            // Set the bean resolver and the type locator
            sec.setBeanResolver(new BeanFactoryResolver(evalContext.getBeanFactory()));
            sec.setTypeLocator(new StandardTypeLocator(evalContext.getBeanFactory().getBeanClassLoader()));
            // If a conversion service is available, set the type converter
            ConversionService conversionService = evalContext.getBeanFactory().getConversionService();
            if (conversionService != null) {
                sec.setTypeConverter(new StandardTypeConverter(conversionService));
            }
            // Customize the evaluation context. Subclasses can add more configuration
            customizeEvaluationContext(sec);
            // Put the created evaluation context into the cache
            this.evaluationCache.put(evalContext, sec);
        }

        // Evaluate the expression with the prepared evaluation context and return the result
        return expr.getValue(sec);
    }
    catch (Throwable ex) {
        // If any exception happens during parsing or evaluation, throw BeanExpressionException
        throw new BeanExpressionException("Expression parsing failed", ex);
    }
}
```

Now step 4 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method. The `org.springframework.beans.TypeConverterSupport#convertIfNecessary(value,requiredType,typeDescriptor)` method hands the real conversion work to `typeConverterDelegate`.

```java
@Nullable
@Override
public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType,
                                @Nullable TypeDescriptor typeDescriptor) throws TypeMismatchException {
    Assert.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");

    try {
        // Delegate the real conversion work to typeConverterDelegate
        return this.typeConverterDelegate.convertIfNecessary(null, null, value, requiredType, typeDescriptor);
    }
    catch (ConverterNotFoundException | IllegalStateException ex) {
        throw new ConversionNotSupportedException(value, requiredType, ex);
    }
    catch (ConversionException | IllegalArgumentException ex) {
        throw new TypeMismatchException(value, requiredType, ex);
    }
}
```

The `org.springframework.beans.TypeConverterDelegate#convertIfNecessary(propertyName,oldValue,newValue,requiredType,typeDescriptor)` method converts a value to the required type. First, it looks for a matching custom editor. If there is no editor but a custom conversion service is set, it tries to convert with that service. If both steps fail, the method tries some standard conversion rules, such as String to enum or one number type to another. If every attempt fails, the method throws an exception that says the required conversion cannot be done.

```java
public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue,
                                @Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor) throws IllegalArgumentException {

    // Look for a custom editor for this type
    PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);

    // Used to catch the exception when the ConversionService attempt fails
    ConversionFailedException conversionAttemptEx = null;

    // No custom editor, but is a custom ConversionService specified?
    ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
    if (editor == null && conversionService != null && newValue != null && typeDescriptor != null) {
        TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
        if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
            try {
                return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
            }
            catch (ConversionFailedException ex) {
                conversionAttemptEx = ex;  // Record the failed conversion attempt
            }
        }
    }

    Object convertedValue = newValue;

    // If the value is not of the required type, convert it
    if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
        if (typeDescriptor != null && requiredType != null && Collection.class.isAssignableFrom(requiredType) &&
            convertedValue instanceof String) {
            TypeDescriptor elementTypeDesc = typeDescriptor.getElementTypeDescriptor();
            if (elementTypeDesc != null) {
                Class<?> elementType = elementTypeDesc.getType();
                if (Class.class == elementType || Enum.class.isAssignableFrom(elementType)) {
                    convertedValue = StringUtils.commaDelimitedListToStringArray((String) convertedValue);
                }
            }
        }
        if (editor == null) {
            editor = findDefaultEditor(requiredType);  // If there is no custom editor, find the default one
        }
        convertedValue = doConvertValue(oldValue, convertedValue, requiredType, editor);  // Do the conversion
    }

    // Try standard type conversion for special cases, such as String to enum and number conversion
    boolean standardConversion = false;
    if (requiredType != null) {
        if (convertedValue != null) {
            // If the target type is Object, return the converted value directly
            if (Object.class == requiredType) {
                return (T) convertedValue;
            }
            // If the target type is an array, do array conversion
            else if (requiredType.isArray()) {
                if (convertedValue instanceof String && Enum.class.isAssignableFrom(requiredType.getComponentType())) {
                    convertedValue = StringUtils.commaDelimitedListToStringArray((String) convertedValue);
                }
                return (T) convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
            }
            // If it is a Collection or Map, try to convert the content of the collection or map
            else if (convertedValue instanceof Collection) {
                convertedValue = convertToTypedCollection((Collection<?>) convertedValue, propertyName, requiredType, typeDescriptor);
                standardConversion = true;
            }
            else if (convertedValue instanceof Map) {
                convertedValue = convertToTypedMap((Map<?, ?>) convertedValue, propertyName, requiredType, typeDescriptor);
                standardConversion = true;
            }
            if (convertedValue.getClass().isArray() && Array.getLength(convertedValue) == 1) {
                convertedValue = Array.get(convertedValue, 0);
                standardConversion = true;
            }
            if (String.class == requiredType && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                return (T) convertedValue.toString();
            }
            else if (convertedValue instanceof String && !requiredType.isInstance(convertedValue)) {
                String trimmedValue = ((String) convertedValue).trim();
                if (requiredType.isEnum() && trimmedValue.isEmpty()) {
                    return null;
                }
                convertedValue = attemptToConvertStringToEnum(requiredType, trimmedValue, convertedValue);
                standardConversion = true;
            }
            else if (convertedValue instanceof Number && Number.class.isAssignableFrom(requiredType)) {
                convertedValue = NumberUtils.convertNumberToTargetClass((Number) convertedValue, (Class<Number>) requiredType);
                standardConversion = true;
            }
        }
        else if (requiredType == Optional.class) {
            convertedValue = Optional.empty();
        }

        // If the value still does not match the target type after all conversions above, throw an exception
        if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
            if (conversionAttemptEx != null) {
                throw conversionAttemptEx;
            }
            else if (conversionService != null && typeDescriptor != null) {
                TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
                if (conversionService.canConvert(sourceTypeDesc, typeDescriptor)) {
                    return (T) conversionService.convert(newValue, sourceTypeDesc, typeDescriptor);
                }
            }
            throw new IllegalStateException("Conversion failed");  // The real exception message is more detailed. Simplified here
        }
    }

    if (conversionAttemptEx != null) {
        throw conversionAttemptEx;
    }

    return (T) convertedValue;
}
```

### 8. Notes

1. **SpEL expressions**
   + `@Value` can resolve Spring Expression Language (SpEL) expressions. Make sure the expressions are correct to avoid runtime errors.
2. **Default values**
   + We can give `@Value` a default value in case a property is not defined in the properties file. For example: `@Value("${some.property:default}")`.
3. **Type conversion**
   + Make sure the value given by `@Value` can be converted to the type of the field or method parameter. Spring tries to convert it automatically, but this may not always work.
4. **Not for complex types**
   + `@Value` works for simple types (such as strings, integers, and enums). Do not use it to inject complex beans. Use `@Autowired` or `@Inject` instead.
5. **Not usable in `BeanPostProcessor` or `BeanFactoryPostProcessor`**
   + `@Value` does not work in `BeanPostProcessor` or `BeanFactoryPostProcessor` implementations. They are handled earlier in the Spring container lifecycle than `@Value`.
6. **Placeholder resolver configuration**
   + To use property placeholders (such as `${property.name}`), make sure `PropertySourcesPlaceholderConfigurer` or `PropertyPlaceholderConfigurer` is configured.
7. **Environment variables and system properties**
   + We can use `@Value` to refer to environment variables or system properties, for example: `@Value("${JAVA_HOME}")`.
8. **Do not inject sensitive information**
   + Do not use `@Value` to inject sensitive information such as passwords, unless it is properly encrypted. Consider a dedicated solution, such as the Vault integration of Spring Cloud Config.
9. **Circular dependencies**
   + `@Value` is different from `@Autowired`. But note that using `@Value` may cause a circular dependency indirectly, especially when the injected value is a property of another bean.
10. **Performance**
    + Heavy use of SpEL expressions may slightly affect performance, because the expressions must be parsed at runtime.

### 9. Summary

#### Best Practices Summary

1. **Entry class**:
   - Use `AnnotationConfigApplicationContext` to start the Spring context. It supports configuration based on Java annotations.
   - When creating the context, we pass `MyConfiguration` as the configuration class.
2. **Configuration class**:
   - The `MyConfiguration` class is marked with `@Configuration`. It provides bean definitions.
   - `@PropertySource` names the properties file `application.properties`, which loads properties into the context.
   - It defines one bean, `MyService`, so it is created and initialized in the Spring container.
3. **Properties file**:
   - The `application.properties` file defines several properties that the application can use.
4. **Property injection**:
   - The `MyService` class shows five ways to inject properties with `@Value`, from a plain string value to a SpEL expression.
5. **Checking the injection result**:
   - The class implements `InitializingBean` and overrides `afterPropertiesSet` to check the injected values.
   - After the application runs, this method prints all injected values. This confirms that `@Value` resolved and injected the expected values.

#### Source Code Analysis Summary

1. **Core post processor**:
   + `AutowiredAnnotationBeanPostProcessor` is the main post processor for annotations such as `@Value`. It implements two key interfaces, `MergedBeanDefinitionPostProcessor` and `InstantiationAwareBeanPostProcessor`. They allow us to step into key stages of the bean lifecycle, which makes property injection possible.
2. **Collection phase**:
   - In `postProcessMergedBeanDefinition`, `AutowiredAnnotationBeanPostProcessor` makes sure the bean definition matches the expected autowiring metadata.
   - `findAutowiringMetadata` returns the related `InjectionMetadata` for the given bean name and class. It uses a cache to improve performance.
   - `buildAutowiringMetadata` checks the class and all its parent classes. It finds fields and methods with `@Autowired`, `@Value`, and similar annotations, and creates one `InjectionMetadata` object for them.
3. **Injection phase**:
   + `postProcessProperties` post processes the bean properties. It injects the properties marked by annotations such as `@Value`.
   + `InjectionMetadata#inject` injects all elements that need injection (for example, fields with `@Value`) into the target bean.
   + `AutowiredFieldElement#inject` handles the injection of a single field, including resolving the value in the `@Value` annotation.
   + `DefaultListableBeanFactory#resolveDependency` resolves the field value from the Spring bean factory.
