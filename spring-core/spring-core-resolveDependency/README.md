## resolveDependency

- [resolveDependency](#resolvedependency)
  - [1. Overview](#1-overview)
  - [2. Method Description](#2-method-description)
  - [3. Method Source](#3-method-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
  - [6. Sequence Diagram](#6-sequence-diagram)
    - [Resolve Environment Variables](#resolve-environment-variables)
    - [Resolve Bean Dependencies](#resolve-bean-dependencies)
  - [7. Source Code Analysis](#7-source-code-analysis)
    - [Resolve Environment Variables](#resolve-environment-variables-1)
    - [Resolve Bean Dependencies](#resolve-bean-dependencies-1)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Method Description

`resolveDependency` is a method defined in the `ConfigurableListableBeanFactory` interface of Spring. Its main job is to resolve and provide the dependencies of a bean. It works mainly in autowiring cases such as `@Autowired`, `@Inject` and `@Value`.

### 3. Method Source

This is a core method of the Spring IoC container. It resolves the dependencies between beans. The container calls it when it needs to know which other bean to inject into a given bean, field, constructor or method.

```java
/**
 * Resolve the specified dependency against the beans defined in this factory.
 * 
 * This method tries to find and return a suitable bean instance for the given dependency descriptor.
 * The dependency descriptor can describe a dependency of a field, a method or a constructor.
 * 
 * @param descriptor the descriptor of the dependency. It gives details such as the dependency type and qualifiers.
 * @param requestingBeanName the name of the bean that declares or requests the dependency. This is often used to handle circular dependencies between beans.
 * @return the matching bean instance that satisfies the dependency, or null if there is no suitable match.
 * 
 * @throws NoSuchBeanDefinitionException if no matching bean is found.
 * @throws NoUniqueBeanDefinitionException if there are several possible matches and no clear choice.
 * @throws BeansException if the resolution fails for any other reason.
 * 
 * @since 2.5
 * @see #resolveDependency(DependencyDescriptor, String, Set, TypeConverter) for more complex resolution cases. It allows passing excluded beans and a type converter.
 */
@Nullable
Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;
```

### 4. Main Features

1. **Dependency descriptor**
   + The method takes a `DependencyDescriptor` parameter that describes the required dependency. The descriptor can stand for the dependency of a field, a method parameter or a constructor parameter.
2. **Autowiring qualifiers**
   + If several beans match and the dependency has `@Autowired` and `@Qualifier` annotations, `resolveDependency` uses these annotations to pick the right bean.
3. **Circular dependencies**
   + The method also takes circular dependencies into account. For example, Bean A depends on Bean B, and Bean B depends on Bean A. This case is handled.
4. **Return the matching bean**
   + The method tries to return a bean instance that matches the descriptor. If no suitable bean is found, it may return null or throw an exception, depending on the descriptor settings.
5. **Exception handling**
   + If no suitable bean is found, or several suitable beans are found and there is no clear choice, the method throws the matching exception.
6. **Other considerations**:
   - Other factors can be considered, such as the `@Primary` annotation.
   - For properties of primitive or String type, the `@Value` annotation can be resolved to get the value from a properties file or an environment variable.

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext`, which configures the Spring container with Java annotations. We pass one component class, `MyConfiguration`, to the constructor. The code calls two methods (`methodResolveDependency` and `fieldResolveDependency`) to resolve the dependency of a method and of a field. In short, they use reflection to get the target method or field. They create a `DependencyDescriptor` that describes the method or field. They use `resolveDependency` to resolve the real dependency from the Spring container. They use reflection to inject the resolved dependency into the target object. After resolving, the code prints the result with formatted strings. For example, it shows the fully qualified name of the method or field and the value of the resolved dependency.

```java
public class ResolveDependencyApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        // Get the bean factory
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        // Target object to inject into
        MyServiceB injectTarget = new MyServiceB();

        System.out.println("Before MyServiceB = " + injectTarget + "\n");

        methodResolveDependency(beanFactory, injectTarget, "setMethodMyServiceA");
        fieldResolveDependency(beanFactory, injectTarget, "fieldMyServiceA");
        fieldResolveDependency(beanFactory, injectTarget, "myPropertyValue");

        System.out.println("After MyServiceB = " + injectTarget + "\n");
    }

    /**
     * Resolve method dependency
     *
     * @param beanFactory
     * @param injectTarget
     */
    public static void methodResolveDependency(ConfigurableListableBeanFactory beanFactory, Object injectTarget, String name) {
        try {
            // 1. Get the setMethodMyServiceA method of MyServiceB
            Method method = injectTarget.getClass().getMethod(name, MyServiceA.class);

            // 2. Create a DependencyDescriptor for the method parameter
            DependencyDescriptor desc = new DependencyDescriptor(new MethodParameter(method, 0), true);

            // 3. Use the BeanFactory to resolve the method parameter dependency
            Object value = beanFactory.resolveDependency(desc, null);

            System.out.println("Method dependency result:");
            System.out.println("---------------------------------------------");
            System.out.println(String.format("Name:   %s.%s",method.getDeclaringClass().getName(),method.getName()));
            System.out.println(String.format("Value:  %s", value));
            System.out.println("---------------------------------------------\n");

            // 4. Make the method accessible (especially if it is private)
            ReflectionUtils.makeAccessible(method);

            // 5. Call the method by reflection to inject the resolved dependency
            method.invoke(injectTarget, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resolve field dependency
     *
     * @param beanFactory
     * @param injectTarget
     */
    public static void fieldResolveDependency(ConfigurableListableBeanFactory beanFactory, Object injectTarget, String name) {
        try {
            // 1. Get the fieldMyServiceA field of MyServiceB
            Field field = injectTarget.getClass().getDeclaredField(name);

            // 2. Create a DependencyDescriptor for the field
            DependencyDescriptor desc = new DependencyDescriptor(field, true);

            // 3. Use the BeanFactory to resolve the field dependency
            Object value = beanFactory.resolveDependency(desc, null);

            System.out.println("Field dependency result:");
            System.out.println("---------------------------------------------");
            System.out.println(String.format("Name:   %s.%s", field.getDeclaringClass().getName(), field.getName()));
            System.out.println(String.format("Value:  %s", value));
            System.out.println("---------------------------------------------\n");

            // 4. Make the field accessible (especially if it is private)
            ReflectionUtils.makeAccessible(field);

            // 5. Set the field value by reflection to inject the resolved dependency
            field.set(injectTarget, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

The `MyConfiguration` class uses the `@ComponentScan("com.xcs.spring")` annotation. It tells Spring to search the given package (here "`com.xcs.spring`") and its sub-packages for classes with annotations such as `@Component`, `@Service`, `@Repository` and `@Controller`, and to register them as beans automatically. So you do not need to write a bean definition for every component. Spring finds and registers them. The `@PropertySource` annotation loads properties from the `application.properties` file on the classpath.

```java
@Configuration
@ComponentScan("com.xcs.spring")
@PropertySource("classpath:application.properties")
public class MyConfiguration {
    
}
```

Put the `application.properties` file in the `src/main/resources` directory and add the following content.

```properties
my.property.value = Hello from Environment!
```

`MyService` is a simple service class. We did not define any methods or features in it.

```java
@Service
public class MyServiceA {
    
}
```

`MyServiceB` is not managed by Spring, so in the code it behaves like a plain Java class. Although `MyServiceB` itself is not managed by Spring, the `ResolveDependencyApplication` class shows how to use the low-level Spring API to resolve and inject dependencies by hand.

- **Field `myPropertyValue`**
  + It has a `@Value` annotation. But `MyServiceB` is not a Spring-managed bean, so the annotation is not resolved automatically.
- **Fields `myServiceA` and `fieldMyServiceA`**
  + There is no automatic Spring dependency injection, so these two fields are `null` by default.
- **Method `setMyServiceA`**
  + This is a plain setter. Because `MyServiceB` is not managed by Spring, it is only a normal setter.
- **Method `toString`**
  + This method gives the class a custom string form. It has nothing to do with whether `MyServiceB` is managed by Spring.

```java
public class MyServiceB {

    /**
     * Method injection
     */
    private MyServiceA methodMyServiceA;

    /**
     * Field injection
     */
    private MyServiceA fieldMyServiceA;

    /**
     * Field injection (environment variable)
     */
    @Value("${my.property.value}")
    private String myPropertyValue;

    public void setMethodMyServiceA(MyServiceA methodMyServiceA){
        this.methodMyServiceA = methodMyServiceA;
    }

    @Override
    public String toString() {
        return "MyServiceB{" +
                "myPropertyValue='" + myPropertyValue + '\'' +
                ", methodMyServiceA=" + methodMyServiceA +
                ", fieldMyServiceA=" + fieldMyServiceA +
                '}';
    }
}
```

The result shows that the low-level Spring `resolveDependency` method was used to inject dependencies into the fields and the method of `MyServiceB` by hand. In normal Spring development we usually do not do this, because Spring provides higher-level tools for dependency injection. The purpose here is to show how Spring works at a low level, and to show a way to control dependency injection manually.

```java
Before MyServiceB = MyServiceB{myPropertyValue='null', myServiceA=null, fieldMyServiceA=null}

Method dependency result:
---------------------------------------------
Name:   com.xcs.spring.service.MyServiceB.setMyServiceA
Value:  com.xcs.spring.service.MyServiceA@202b0582
---------------------------------------------

Field dependency result:
---------------------------------------------
Name:   com.xcs.spring.service.MyServiceB.fieldMyServiceA
Value:  com.xcs.spring.service.MyServiceA@202b0582
---------------------------------------------

Field dependency result:
---------------------------------------------
Name:   com.xcs.spring.service.MyServiceB.myPropertyValue
Value:  Hello from Environment!
---------------------------------------------

After MyServiceB = MyServiceB{myPropertyValue='Hello from Environment!', myServiceA=com.xcs.spring.service.MyServiceA@202b0582, fieldMyServiceA=com.xcs.spring.service.MyServiceA@202b0582}

```

### 6. Sequence Diagram

#### Resolve Environment Variables

With the `@Value` annotation, we can ask for a specific environment property or variable. For example, we can ask for a property named `my.property.value`.

~~~mermaid
sequenceDiagram
Title: Sequence Diagram of resolveDependency Resolving Environment Properties and Variables

ResolveDependencyApplication-->>AbstractAutowireCapableBeanFactory:resolveDependency(descriptor,requestingBeanName)
note over AbstractAutowireCapableBeanFactory: Request to resolve the dependency

AbstractAutowireCapableBeanFactory->>DefaultListableBeanFactory:resolveDependency(descriptor, requestingBeanName, null, null)
note over DefaultListableBeanFactory: Go to the concrete bean factory to resolve it

DefaultListableBeanFactory->>DefaultListableBeanFactory:doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter)
note over DefaultListableBeanFactory: Do the actual dependency resolution

DefaultListableBeanFactory->>DefaultListableBeanFactory:getAutowireCandidateResolver()
note over DefaultListableBeanFactory: Get the current dependency resolution strategy

DefaultListableBeanFactory->>QualifierAnnotationAutowireCandidateResolver:getSuggestedValue(descriptor)
note over QualifierAnnotationAutowireCandidateResolver: Find the suggested value for the given descriptor

QualifierAnnotationAutowireCandidateResolver->>QualifierAnnotationAutowireCandidateResolver:findValue(annotationsToSearch)
note over QualifierAnnotationAutowireCandidateResolver: Look for the value in the given annotations

QualifierAnnotationAutowireCandidateResolver->>QualifierAnnotationAutowireCandidateResolver:extractValue(attr)
note over QualifierAnnotationAutowireCandidateResolver: Extract the value from the annotation attributes

QualifierAnnotationAutowireCandidateResolver->>DefaultListableBeanFactory:Return the expression of the @Value annotation
note over DefaultListableBeanFactory: Resolve the expression or value in the annotation

DefaultListableBeanFactory->>AbstractBeanFactory:resolveEmbeddedValue(value)
note over AbstractBeanFactory: Resolve embedded values (such as placeholders or SpEL expressions)

DefaultListableBeanFactory->>AbstractBeanFactory:evaluateBeanDefinitionString(strVal, bd)
note over AbstractBeanFactory: Evaluate the bean definition string (it may be an expression)

DefaultListableBeanFactory->>AbstractBeanFactory:getTypeConverter()
note over AbstractBeanFactory: Get the converter for type conversion

DefaultListableBeanFactory->>TypeConverterSupport:convertIfNecessary(value, type, typeDescriptor())
note over TypeConverterSupport: Convert the value to the required type if needed

TypeConverterSupport->>DefaultListableBeanFactory:Return the type conversion result
note over DefaultListableBeanFactory: Continue the resolution with the converted value

DefaultListableBeanFactory->>AbstractAutowireCapableBeanFactory:Return the environment property
AbstractAutowireCapableBeanFactory->>ResolveDependencyApplication:Return the environment property
~~~

#### Resolve Bean Dependencies

This is its main feature. When a bean needs an instance of another bean, `resolveDependency` finds and provides the bean for us. For example, if a `MyServiceB` class needs an instance of `MyServiceA`, `resolveDependency` can provide a `MyServiceA` instance to `MyServiceB`.

~~~mermaid
sequenceDiagram
Title: Sequence Diagram of resolveDependency Resolving Bean Dependencies

ResolveDependencyApplication-->>AbstractAutowireCapableBeanFactory:resolveDependency(descriptor,requestingBeanName)
note right of AbstractAutowireCapableBeanFactory: Start the dependency resolution request

AbstractAutowireCapableBeanFactory->>DefaultListableBeanFactory:resolveDependency(descriptor, requestingBeanName, null, null)
note right of DefaultListableBeanFactory: Delegate the dependency resolution to the concrete bean factory

DefaultListableBeanFactory->>DefaultListableBeanFactory:doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter)
note right of DefaultListableBeanFactory: Run the concrete dependency resolution logic

DefaultListableBeanFactory->>DefaultListableBeanFactory:resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter)
note right of DefaultListableBeanFactory: Resolve multiple matching beans

DefaultListableBeanFactory->>DefaultListableBeanFactory:findAutowireCandidates(beanName, type, descriptor)
note right of DefaultListableBeanFactory: Find candidate beans suitable for autowiring

DefaultListableBeanFactory->>DefaultListableBeanFactory:isSelfReference(beanName, candidate)
note right of DefaultListableBeanFactory: Check whether the bean refers to itself

DefaultListableBeanFactory->>DefaultListableBeanFactory:isAutowireCandidate(candidate, descriptor)
note right of DefaultListableBeanFactory: Check whether the bean is a suitable autowire candidate

DefaultListableBeanFactory->>DefaultListableBeanFactory:addCandidateEntry(result, candidate, descriptor, requiredType)
note right of DefaultListableBeanFactory: Add the found candidate to the result

DefaultListableBeanFactory->>DependencyDescriptor:resolveCandidate(candidateName, requiredType, beanFactory)
note right of DependencyDescriptor: Try to resolve and return the candidate bean

DependencyDescriptor->>AbstractBeanFactory:getBean(String name)
note right of AbstractBeanFactory: Request the bean with the given name

AbstractBeanFactory->>DependencyDescriptor:Return the candidate bean object
note right of DependencyDescriptor: Return the bean instance that was found

DependencyDescriptor->>DefaultListableBeanFactory:Return the candidate bean object
note right of DefaultListableBeanFactory: Return the bean instance to the bean factory

DefaultListableBeanFactory->>DefaultListableBeanFactory:determineAutowireCandidate(matchingBeans, descriptor)
note right of DefaultListableBeanFactory: Choose the autowire candidate from the matching beans

DefaultListableBeanFactory->>AbstractAutowireCapableBeanFactory:Return the bean
note right of AbstractAutowireCapableBeanFactory: Return the chosen bean to the upper bean factory

AbstractAutowireCapableBeanFactory->>ResolveDependencyApplication:Return the bean
note right of ResolveDependencyApplication: Return the bean to the requester of the dependency resolution

~~~

### 7. Source Code Analysis

#### Resolve Environment Variables

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency` method tries to resolve the dependency with a lazy-resolution proxy or directly. Which way it uses depends on the context and other settings (this time we study environment properties).

```java
@Override
@Nullable
public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                                @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
	// ... [code omitted for brevity]
    // Try to get a lazy-resolution proxy for the dependency
    Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
        descriptor, requestingBeanName);

    // If no lazy-resolution proxy is available, do the actual dependency resolution
    if (result == null) {
        result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
    }
    return result;
}
```

In the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method, this code tries to resolve a dependency. It first looks for a suggested value (the `@Value` annotation), and then does the needed type conversion.

```java
@Nullable
public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName,
                                  @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
    try {
        // ... [code omitted for brevity]
        
        // Get the dependency type defined in the dependency descriptor
        Class<?> type = descriptor.getDependencyType();

        // Step 1: Get the suggested value from the autowire candidate resolver
        Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);

        if (value != null) {
            // If the suggested value is a String
            if (value instanceof String) {
                // Step 2: Resolve values embedded in the string (for example, look up environment variables)
                String strVal = resolveEmbeddedValue((String) value);

                // Get the bean definition. If beanName exists and is already in the container, get its bean definition
                BeanDefinition bd = (beanName != null && containsBean(beanName) ? getMergedBeanDefinition(beanName) : null);
                
                // Step 3: Evaluate the string value, which may handle expressions
                value = evaluateBeanDefinitionString(strVal, bd);
            }

            // Get the type converter. Use the external one if given, otherwise the default one
            TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());

            try {
                // Step 4: Try to do the needed type conversion
                return converter.convertIfNecessary(value, type, descriptor.getTypeDescriptor());
            }
            catch (UnsupportedOperationException ex) {
                // Handle the unsupported operation exception (implementation omitted)
            }
        }

        // ... [code omitted for brevity]

    }
    finally {
        // ... [code omitted for brevity]
    }
}
```

> Step 1 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method.

The main goal of the `org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#getSuggestedValue` method is to get the suggested value from a `DependencyDescriptor`. A `DependencyDescriptor` can describe a bean property (a field or a method parameter).

```java
@Override
@Nullable
public Object getSuggestedValue(DependencyDescriptor descriptor) {
    // Try to find the suggested value in the annotations of the field
    Object value = findValue(descriptor.getAnnotations());
    
    // If it is not found on the field, try the annotations of the method
    if (value == null) {
        MethodParameter methodParam = descriptor.getMethodParameter();
        if (methodParam != null) {
            value = findValue(methodParam.getMethodAnnotations());
        }
    }
    
    // Return the suggested value, or null if none is found
    return value;
}
```

The main goal of the `org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#findValue` method is to find a specific annotation (`@Value`) in the given annotation array and get its value.

```java
@Nullable
protected Object findValue(Annotation[] annotationsToSearch) {
    // If the annotation array is not empty, enter the check
    if (annotationsToSearch.length > 0) {   
        // Get the merged attributes of the specific annotation from the array (here the annotation may be @Value)
        AnnotationAttributes attr = AnnotatedElementUtils.getMergedAnnotationAttributes(
            AnnotatedElementUtils.forAnnotations(annotationsToSearch), this.valueAnnotationType);
        // If the annotation attributes are found, get their value
        if (attr != null) {
            return extractValue(attr);
        }
    }
    // If no matching annotation attributes or value are found, return null
    return null;
}
```

The `org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver#extractValue` method gets the `value` attribute from the `@Value` annotation attributes.

```java
protected Object extractValue(AnnotationAttributes attr) {
    // Try to get the 'VALUE' attribute (usually "value") from the annotation attributes
    Object value = attr.get(AnnotationUtils.VALUE);
    
    // If the attribute value is null, throw an exception
    if (value == null) {
        throw new IllegalStateException("Value annotation must have a value attribute");
    }
    
    // Return the extracted value
    return value;
}
```

> Step 2 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method.

The `org.springframework.beans.factory.support.AbstractBeanFactory#resolveEmbeddedValue` method takes a string value and resolves it with each `StringValueResolver` in turn. Then it returns the final resolved string.

```java
@Override
@Nullable
public String resolveEmbeddedValue(@Nullable String value) {
    // If the given value is null, return null directly
    if (value == null) {
        return null;
    }
    // Start with the given value as the result
    String result = value; 
    
    // Loop over all string value resolvers of this object
    for (StringValueResolver resolver : this.embeddedValueResolvers) {
        // Resolve the current result with the resolver
        result = resolver.resolveStringValue(result);
        
        // If the resolved value is null, return null directly
        if (result == null) {
            return null;
        }
    }
    
    // Return the final resolved string
    return result;
}
```

> Step 3 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method.

The `org.springframework.beans.factory.support.AbstractBeanFactory#evaluateBeanDefinitionString` method evaluates a string value in a bean definition (it may be an expression such as `${my.property.value}`). If a `beanExpressionResolver` is configured, it is used for the evaluation. If not, the original value is returned.

```java
@Nullable
protected Object evaluateBeanDefinitionString(@Nullable String value, @Nullable BeanDefinition beanDefinition) {
    // If no beanExpressionResolver (the resolver for expressions) is set, return the original value
    if (this.beanExpressionResolver == null) {
        return value;
    }

    Scope scope = null; // Scope variable
    if (beanDefinition != null) {
        // Get the scope name from the bean definition
        String scopeName = beanDefinition.getScope(); 
        if (scopeName != null) {
            // Get the concrete instance of the scope
            scope = getRegisteredScope(scopeName);
        }
    }
    // Evaluate the string value with the beanExpressionResolver and return the result
    return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
}
```

> Step 4 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method.

The `org.springframework.beans.TypeConverterSupport#convertIfNecessary(value,requiredType,typeDescriptor)` method first makes sure a `typeConverterDelegate` is available to do the actual conversion. Then it tries to convert the given value to the required type.

```java
@Nullable
@Override
public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType,
                                @Nullable TypeDescriptor typeDescriptor) throws TypeMismatchException {

    // Assert that typeConverterDelegate (the type conversion delegate) is not null
    Assert.state(this.typeConverterDelegate != null, "No TypeConverterDelegate");
    try {
        // Convert the type through typeConverterDelegate
        return this.typeConverterDelegate.convertIfNecessary(null, null, value, requiredType, typeDescriptor);
    }
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.TypeConverterDelegate#convertIfNecessary(propertyName,  oldValue, newValue,requiredType, typeDescriptor)` method covers many conversion cases, so it can handle data of many types and structures.

```java
public <T> T convertIfNecessary(@Nullable String propertyName, @Nullable Object oldValue, @Nullable Object newValue,
			@Nullable Class<T> requiredType, @Nullable TypeDescriptor typeDescriptor) throws IllegalArgumentException {

    // Look for a custom editor for this type
    PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);

    ConversionFailedException conversionAttemptEx = null;

    // If there is no custom editor but a custom ConversionService is set, try to convert with it
    ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
    if (editor == null && conversionService != null && newValue != null && typeDescriptor != null) {
        // ... [code omitted for brevity]
    }

    Object convertedValue = newValue;

    // If the value is not of the required type, convert it
    if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
        // ... [code omitted for brevity]
    }

    boolean standardConversion = false;

    // Try the standard type conversion rules (if they apply)
    if (requiredType != null) {
		
        // Special conversion logic for different types
        if (convertedValue != null) {
            if (Object.class == requiredType) {
                return (T) convertedValue;
            }
            
            // Different conversions are done here depending on requiredType, for example arrays, collections, maps and enums.
            else if (requiredType.isArray()) {
                // ... [code omitted for brevity]
            }
            else if (convertedValue instanceof Collection) {
                // ... [code omitted for brevity]
            }
            else if (convertedValue instanceof Map) {
                // ... [code omitted for brevity]
            }
            if (convertedValue.getClass().isArray() && Array.getLength(convertedValue) == 1) {
                // ... [code omitted for brevity]
            }
            if (String.class == requiredType && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
                // ... [code omitted for brevity]
            }
            else if (convertedValue instanceof String && !requiredType.isInstance(convertedValue)) {
                // ... [code omitted for brevity]
            }
            else if (convertedValue instanceof Number && Number.class.isAssignableFrom(requiredType)) {
                // ... [code omitted for brevity]
            }
        }
        else {
            // convertedValue == null
            if (requiredType == Optional.class) {
                convertedValue = Optional.empty();
            }
        }

        if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
            // ... [code omitted for brevity]
        }
    }

    if (conversionAttemptEx != null) {
        // ... [code omitted for brevity]
    }

    return (T) convertedValue;
}
```

#### Resolve Bean Dependencies

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency` method tries to resolve the dependency with a lazy-resolution proxy or directly. Which way it uses depends on the context and other settings (this time we study bean dependencies).

```java
@Override
@Nullable
public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                                @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
	// ... [code omitted for brevity]
    // Try to get a lazy-resolution proxy for the dependency
    Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
        descriptor, requestingBeanName);

    // If no lazy-resolution proxy is available, do the actual dependency resolution
    if (result == null) {
        result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
    }
    return result;
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method resolves and injects the dependency. First, it tries to resolve multiple bean candidates (for example, when a List or Map must be injected). If no matching bean is found, it may throw an exception. If several beans match, it tries to find the best one to inject. If only one bean matches, it is injected directly.

```java
@Nullable
public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName,
                                  @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
    // ... [code omitted for brevity]
    try {
        // ... [code omitted for brevity]

        // Step 1: Try to resolve multiple bean candidates. For example, when a List or Map must be injected
        Object multipleBeans = resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter);
        if (multipleBeans != null) {
            return multipleBeans;
        }

        // Step 2: Find bean candidates that match the type
        Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
        if (matchingBeans.isEmpty()) {
            if (isRequired(descriptor)) {
                raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
            }
            return null;
        }

        String autowiredBeanName;
        Object instanceCandidate;

        // Step 3: If several beans match, try to decide which one is the best
        if (matchingBeans.size() > 1) {
            autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
            if (autowiredBeanName == null) {
                if (isRequired(descriptor) || !indicatesMultipleBeans(type)) {
                    return descriptor.resolveNotUnique(descriptor.getResolvableType(), matchingBeans);
                }
                else {
                    // For a non-required Collection/Map, ignore the non-unique case
                    return null;
                }
            }
            instanceCandidate = matchingBeans.get(autowiredBeanName);
        }
        else {
            // Only one matching bean
            Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
            autowiredBeanName = entry.getKey();
            instanceCandidate = entry.getValue();
        }

        // Record the names of the beans that were autowired
        if (autowiredBeanNames != null) {
            autowiredBeanNames.add(autowiredBeanName);
        }

        // If the candidate is a class, try to resolve it to the actual bean instance
        if (instanceCandidate instanceof Class) {
            instanceCandidate = descriptor.resolveCandidate(autowiredBeanName, type, this);
        }
        Object result = instanceCandidate;
        
        // ... [code omitted for brevity]
        
        return result;
    }
    finally {
        // ... [code omitted for brevity]
    }
}
```

> Step 1 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method.

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveMultipleBeans` method handles automatic injection of beans when the dependency is a multi-value type (such as `Stream`, array, `Collection` or `Map`). It first checks the type of the dependency and then resolves it by type. For each type, it finds all matching bean candidates, and converts and sorts them as needed.

```java
@Nullable
private Object resolveMultipleBeans(DependencyDescriptor descriptor, @Nullable String beanName,
        @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) {

    Class<?> type = descriptor.getDependencyType();

    // Check whether the dependency is a Java 8 Stream
    if (descriptor instanceof StreamDependencyDescriptor) {
       // ... [code omitted for brevity]
    } 
    // Check whether the dependency is an array
    else if (type.isArray()) {
        // ... [code omitted for brevity]
        return result;
    }
    // Check whether the dependency is a Collection interface
    else if (Collection.class.isAssignableFrom(type) && type.isInterface()) {
        // ... [code omitted for brevity]
        return result;
    }
    // Check whether the dependency is a Map
    else if (Map.class == type) {
        // ... [code omitted for brevity]
        return matchingBeans;
    }
    else {
        return null;
    }
}
```

> Step 2 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method.

The main goal of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#findAutowireCandidates` method is to decide which beans are suitable to be autowired into the given dependency. It first gets all possible candidate beans from the current `BeanFactory` and its ancestors. Then it filters them by conditions such as type match and qualifiers. If the first search finds no matching bean, it also tries fallback matching to find a suitable bean.

```java
protected Map<String, Object> findAutowireCandidates(
        @Nullable String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {

    // 1. Get the names of all possible candidate beans from the BeanFactory and its ancestors whose type matches the required type.
    String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
        this, requiredType, true, descriptor.isEager());

    // 2. Create an empty result map to hold the candidate beans.
    Map<String, Object> result = CollectionUtils.newLinkedHashMap(candidateNames.length);

    // 3. Loop over the predefined resolvable dependencies.
    for (Map.Entry<Class<?>, Object> classObjectEntry : this.resolvableDependencies.entrySet()) {
        Class<?> autowiringType = classObjectEntry.getKey();

        // 4. Check whether the current dependency is compatible with the required type.
        if (autowiringType.isAssignableFrom(requiredType)) {
            Object autowiringValue = classObjectEntry.getValue();
            // 5. Resolve the autowiring of the current value.
            autowiringValue = AutowireUtils.resolveAutowiringValue(autowiringValue, requiredType);
            // 6. If the resolved value matches the required type, add it to the result map.
            if (requiredType.isInstance(autowiringValue)) {
                result.put(ObjectUtils.identityToString(autowiringValue), autowiringValue);
                break;
            }
        }
    }

    // 7. Loop over all possible candidate bean names.
    for (String candidate : candidateNames) {
        // 8. If the bean name is not a self reference and it is a valid autowire candidate, add it to the result map.
        if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, descriptor)) {
            addCandidateEntry(result, candidate, descriptor, requiredType);
        }
    }

    // 9. If no matching candidate bean is found.
    if (result.isEmpty()) {
        boolean multiple = indicatesMultipleBeans(requiredType);

        // 10. Create a new descriptor for the fallback match.
        DependencyDescriptor fallbackDescriptor = descriptor.forFallbackMatch();

        // 11. Loop over all candidate bean names again.
        for (String candidate : candidateNames) {
            // 12. If the bean name is not a self reference and it is a valid fallback candidate, add it to the result map.
            if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, fallbackDescriptor) &&
                (!multiple || getAutowireCandidateResolver().hasQualifier(descriptor))) {
                addCandidateEntry(result, candidate, descriptor, requiredType);
            }
        }

        // 13. If still no matching candidate bean is found, consider self-referencing beans as candidates.
        if (result.isEmpty() && !multiple) {
            for (String candidate : candidateNames) {
                if (isSelfReference(beanName, candidate) &&
                    (!(descriptor instanceof MultiElementDescriptor) || !beanName.equals(candidate)) &&
                    isAutowireCandidate(candidate, fallbackDescriptor)) {
                    addCandidateEntry(result, candidate, descriptor, requiredType);
                }
            }
        }
    }
    // 14. Return the map of the candidates that were found.
    return result;
}
```

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#addCandidateEntry` method decides how to add a candidate bean, as an instance or as a type, to the given map. It depends on the `DependencyDescriptor` type and other conditions. For dependencies that need several elements (such as a collection or array), it adds all bean instances. For a singleton, or a stream descriptor that needs ordering, it adds the bean instance. In other cases it adds only the bean type.

```java
private void addCandidateEntry(Map<String, Object> candidates, String candidateName,
			DependencyDescriptor descriptor, Class<?> requiredType) {

    // If the DependencyDescriptor is a MultiElementDescriptor (the dependency may have several elements, such as a collection or array)
	if (descriptor instanceof MultiElementDescriptor) {
        // Resolve the instance of the candidate bean
		Object beanInstance = descriptor.resolveCandidate(candidateName, requiredType, this);
		// If the resolved instance is not a NullBean (a placeholder object that stands for a null value), add it to the candidate list
		if (!(beanInstance instanceof NullBean)) {
			candidates.put(candidateName, beanInstance);
		}
	}
	// If the current bean is a singleton, or the DependencyDescriptor is a StreamDependencyDescriptor and ordered
	else if (containsSingleton(candidateName) || (descriptor instanceof StreamDependencyDescriptor &&
			((StreamDependencyDescriptor) descriptor).isOrdered())) {
        // Resolve the instance of the candidate bean
		Object beanInstance = descriptor.resolveCandidate(candidateName, requiredType, this);
		// If the resolved instance is a NullBean, add null to the map. Otherwise add the real bean instance
		candidates.put(candidateName, (beanInstance instanceof NullBean ? null : beanInstance));
	}
	// In other cases, add only the bean type, not the instance, to the map
	else {
		candidates.put(candidateName, getType(candidateName));
	}
}
```

In the `org.springframework.beans.factory.config.DependencyDescriptor#resolveCandidate` method, we find that in Spring dependency injection, when one bean depends on another, the underlying implementation gets the dependent bean instance from the container with the `getBean` method.

```java
public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory)
    	throws BeansException {

    // Get and return the bean instance from the beanFactory by the given bean name
    return beanFactory.getBean(beanName);
}
```

> Step 3 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method.

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#determineAutowireCandidate` method decides which bean to inject when there are several candidates. It first checks whether a bean is marked "primary". Then it checks for the bean with the highest priority. If neither works, it falls back to finding a bean whose name matches the dependency descriptor, or whether the bean instance is in the resolvable dependencies. If it still finds no match, it returns null.

```java
@Nullable
protected String determineAutowireCandidate(Map<String, Object> candidates, DependencyDescriptor descriptor) {
    // Get the dependency type
    Class<?> requiredType = descriptor.getDependencyType();
    
    // Check whether a candidate bean is marked "primary"
    String primaryCandidate = determinePrimaryCandidate(candidates, requiredType);
    if (primaryCandidate != null) {
        // If there is a primary candidate, return its name
        return primaryCandidate;
    }
    
    // Check whether there is a candidate with the highest priority
    String priorityCandidate = determineHighestPriorityCandidate(candidates, requiredType);
    if (priorityCandidate != null) {
        // If there is a candidate with the highest priority, return its name
        return priorityCandidate;
    }
    
    // If neither works to pick a bean, use the fallback
    for (Map.Entry<String, Object> entry : candidates.entrySet()) {
        String candidateName = entry.getKey();
        Object beanInstance = entry.getValue();
        
        // If the candidate bean instance is in the resolvable dependencies, or the candidate bean name matches the dependency name in the descriptor, choose this candidate
        if ((beanInstance != null && this.resolvableDependencies.containsValue(beanInstance)) ||
                matchesBeanName(candidateName, descriptor.getDependencyName())) {
            return candidateName;
        }
    }
    
    // If no bean matches, return null
    return null;
}
```

### 8. Notes

1. **Circular dependencies**
   + Make sure you do not introduce circular dependencies. They make bean creation fail. Spring has some built-in detection, but you still need to be careful in custom logic.
2. **Choosing the candidate bean**
   + When several candidate beans can be injected, choosing one is an important question. Spring offers annotations such as `@Primary` and `@Priority` to help. But you may need to consider other factors.
3. **Custom type conversion**
   + If needed, provide the right type conversion logic to convert the bean to the required type.
4. **Performance**
   + Avoid unneeded repeated lookups or calculations. Where possible, cache the results, especially for frequent dependency lookups.
5. **Lifecycle and scope**
   + Consider the lifecycle and scope of the target bean. For example, do not inject a prototype-scoped bean into a singleton-scoped bean, unless you know the consequences.
6. **Unusual dependency sources**
   + `resolveDependency` may need to handle dependencies from unusual sources, such as `FactoryBeans`, `BeanFactory` or other special dependency providers.

### 9. Summary

#### Best Practices Summary

1. **Be clear about the environment and configuration**
   + The example uses `AnnotationConfigApplicationContext` as the Spring application context. It configures and initializes the Spring container with Java annotations. The `MyConfiguration` class is the configuration source. It bootstraps the whole application context.

2. **Simplify dependency resolution**
   + The resolution is wrapped in specific methods (`methodResolveDependency` and `fieldResolveDependency`). This makes the code clear and easy to maintain.

3. **Use the low-level Spring API**
   + Most of the time we rely on Spring automatic features for dependency injection. In this practice, we used the low-level Spring `resolveDependency` method to resolve and inject dependencies by hand. This shows how Spring works inside. It also gives an alternative when automatic injection does not apply.

4. **Use case for manual dependency injection**
   + Even if `MyServiceB` is not a Spring-managed bean, we can still use the Spring mechanism to resolve and inject the dependencies it needs. This can be useful in some cases, for example when you integrate Spring with code that Spring does not manage.

5. **Use reflection for flexibility**
   + With the reflection API, we can access and change the methods and fields of the target object at run time, whether or not they are public. This gives great flexibility, especially for classes in third-party libraries.

6. **Keep the configuration complete**
   + Make sure all needed configuration resources, such as the `application.properties` file, are in the right place and loaded correctly. In this example, it supplies a property value for `MyServiceB`.

7. **Verify and test**
   + At the end, the print methods make sure all dependencies are resolved and injected correctly. In a real application, we should have unit tests to verify this behavior.

#### Source Code Analysis Summary

**Resolve Environment Variables**

1. **Resolve the `@Value` annotation**
   + The `doResolveDependency` method first checks whether there is a suggested value, usually from the `@Value` annotation. If there is one, it first does string replacement (for example, replacing property placeholders). Then it may evaluate the value (if it is an expression). Finally it tries to convert the value to the target type.
2. **Type conversion**
   + If a suggested value is found, or a dependency was resolved but its type does not match the required type, the system tries a type conversion. Spring has a strong type conversion system that handles many cases.
3. **Deep type conversion**
   + If a more complex conversion is needed (for example collection to array, or string to number), Spring has many built-in conversion rules for these cases.

**Resolve Bean Dependencies**

1. **Starting point of dependency resolution**
   + `resolveDependency` is the main Spring method that resolves the dependencies of beans.
2. **Lazy-resolution proxy check**
   + First, it tries to satisfy the dependency with a lazy-resolution proxy. If it cannot use one, it goes on to resolve the dependency.
3. **Resolve the concrete dependency**
   + In `doResolveDependency`, depending on the dependency type, Spring may try to resolve multiple bean candidates, such as a List or Map. It searches for matching beans and picks a suitable one to inject by conditions such as type and qualifiers.
4. **Multi-value dependencies**
   + If the dependency is a multi-value type (such as a collection or map), Spring handles it in `resolveMultipleBeans`.
5. **Choose the bean to inject**
   + If there are several possible bean candidates, `determineAutowireCandidate` helps decide which one is best to inject. This may be based on the "primary" mark or the priority of the bean.
6. **Get the real bean instance**
   + After all these decisions, Spring finally gets and returns the dependent bean instance with the `getBean` method.