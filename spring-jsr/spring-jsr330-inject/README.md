## @Inject

- [@Inject](#inject)
  - [1. Overview](#1-overview)
  - [2. Annotation Description](#2-annotation-description)
  - [3. Interface Source](#3-interface-source)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133880436) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Annotation Description

The `@Inject` annotation comes from JSR-330. Its full name is `javax.inject.Inject`. It is a standard specification for dependency injection in Java. Spring supports this annotation. So you can use `@Inject` in Spring applications to do dependency injection, similar to Spring's own `@Autowired`. Unlike `@Autowired`, `@Inject` has no built-in "`required`" attribute. If you want an optional dependency, you can use the Java 8 `java.util.Optional` type to get a similar effect.

### 3. Interface Source

The source shows that `@Inject` supports several injection styles: field injection, setter method injection, and constructor injection.

```java
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inject {
}
```

### 4. Main Features

1. **Automatic dependency injection**
   - With `@Inject`, you ask the framework to inject a dependency into a field, constructor, or method for you. You do not need to create and manage object instances by hand.
2. **Several injection points**
   - **Field injection**
     - You can use `@Inject` directly on a field of a class. It asks the framework to provide the matching bean for that field.
   - **Constructor injection**
     - Put `@Inject` on a constructor of the class. It means you want to inject dependencies through this constructor.
   - **Method injection**
     - You can use `@Inject` on a setter method or any other method. It means you want the framework to inject dependencies by calling that method.
3. **Works with other annotations**
   - Especially with `@Named`, to remove ambiguity. For example, if several beans implement the same interface, you can use `@Named` to say which bean you want to inject.
4. **Works across frameworks**
   - `@Inject` is part of the JSR-330 standard, so using it makes your code more portable. In theory, code that uses `@Inject` should run in any framework that supports JSR-330 (such as Spring, Java EE, and Google Guice).

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (this class configures the Spring container with Java annotations). We pass the `MyConfiguration` component class as the constructor argument. Then we get a bean of type `MyController` from the Spring context and call its `showService` method.

```java
public class InjectApplication {

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

When the Spring container initializes `MyController`, the `@Inject` annotation injects a bean of type `MyService` into the `myService` field.

```java
@Controller
public class MyController {

    @Inject
    private MyService myService;

    public void showService(){
        System.out.println("myService = " + myService);
    }
}
```

`MyService` is a simple service class. It has no methods or features.

```java
@Service
public class MyService {
    
}
```

The result shows that `@Inject` works well in our Spring context. It injects the required dependency successfully.

```java
myService = com.xcs.spring.service.MyService@6e535154
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @Inject annotation sequence diagram
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>Apply the bean definition post-processors
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>Handle the merged bean definition
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)<br>Find the autowiring metadata
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:buildAutowiringMetadata(clazz)<br>Build the autowiring metadata
AutowiredAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalFields(clazz,fc)<br>Process the local fields of the class
ReflectionUtils->>AutowiredAnnotationBeanPostProcessor:Find fields with the @Inject annotation
AutowiredAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>Process the local methods of the class
ReflectionUtils->>AutowiredAnnotationBeanPostProcessor:Find methods with the @Inject annotation
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:injectionMetadataCache.put(cacheKey, metadata)<br>Put the metadata into the cache
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,bw)<br>Fill in the property values of the bean
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessProperties(pvs,bean,beanName)<br>Post-process the properties of the bean
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)<br>Find the autowiring metadata again
Note right of AutowiredAnnotationBeanPostProcessor:<br>Get the injection metadata from the cache
AutowiredAnnotationBeanPostProcessor->>InjectionMetadata:inject(bean, beanName, pvs)<br>Do the actual property injection
InjectionMetadata->>AutowiredFieldElement:inject(target, beanName, pvs)<br>Inject the specific field element
AutowiredFieldElement->>AutowiredFieldElement:resolveFieldValue(field,bean,beanName)<br>Resolve the value of the field
AutowiredFieldElement->>DefaultListableBeanFactory:resolveDependency(desc, beanName, autowiredBeanNames, typeConverter)<br>Resolve the dependency of the field
DefaultListableBeanFactory->>DefaultListableBeanFactory:doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter)<br>Resolve the given dependency
DefaultListableBeanFactory->>DefaultListableBeanFactory:findAutowireCandidates(beanName, type, descriptor)<br>Find candidate beans that match the autowiring conditions
DefaultListableBeanFactory->>DefaultListableBeanFactory:addCandidateEntry(result, candidate, descriptor, requiredType)<br>Add a candidate bean to the result
DefaultListableBeanFactory->>AbstractBeanFactory:getType(name)<br>Get the type of the given bean
AbstractBeanFactory->>DefaultListableBeanFactory:Return the class of the dependent bean<br>Return the actual class of the dependent bean
DefaultListableBeanFactory->>DependencyDescriptor:resolveCandidate(beanName, requiredType, beanFactory)<br>Resolve the candidate dependent bean
DependencyDescriptor->>AbstractBeanFactory:getBean(name)<br>Get the given bean instance
AbstractBeanFactory->>DependencyDescriptor:<br>Return the specific dependent bean instance
DependencyDescriptor->>DefaultListableBeanFactory:<br>Return the dependent bean instance to the factory
DefaultListableBeanFactory->>AutowiredFieldElement:<br>Return the dependent bean to the field injector
AutowiredFieldElement->>Field:field.set(bean, value)<br>Actually set the field value of the bean
~~~

### 7. Source Code Analysis

#### Prerequisites

In Spring, `AutowiredAnnotationBeanPostProcessor` is the key class that handles annotations such as `@Inject`. It implements the two interfaces below. To understand how `@Inject` works, you need to know these interfaces. They let you step into key stages of the bean lifecycle, so property injection and other related work can be done.

1. `MergedBeanDefinitionPostProcessor` interface
   - Its `postProcessMergedBeanDefinition` method lets a post-processor change the merged bean definition. A merged bean definition already includes all properties from parent bean definitions. For `@Inject`, this step usually collects the `@Inject` information to parse and prepares it for later processing.
   - 🔗 [MergedBeanDefinitionPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. `InstantiationAwareBeanPostProcessor` interface
   - This interface has several callback methods. They let a post-processor step into the bean creation process before and after instantiation. The `postProcessProperties` method lets a post-processor work on the properties of the bean. For `@Inject`, the bean usually needs to be processed during property setting or dependency injection, and the resolved value is injected into the bean.
   - 🔗 [InstantiationAwareBeanPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor)

#### Collection Phase

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessMergedBeanDefinition` method makes sure the given bean definition matches its expected autowiring metadata.

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    // For the given bean name and type, first try to find the related InjectionMetadata. It may hold the injection information of the fields and methods of the bean
    InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
    
    // Use the InjectionMetadata to check that the configured members in the bean definition match the expected injection metadata.
    metadata.checkConfigMembers(beanDefinition);
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#findAutowiringMetadata` method makes sure the latest and related `InjectionMetadata` is always found for the given bean name and class. It uses a cache to improve performance.

```java
private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz, @Nullable PropertyValues pvs) {
    // If beanName is empty, use the class name as the cache key.
    String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    // First try to get the InjectionMetadata from the concurrent cache.
    InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
    // Check if the metadata that was found needs a refresh.
    if (InjectionMetadata.needsRefresh(metadata, clazz)) {
        // Use double-checked locking to be thread safe.
        synchronized (this.injectionMetadataCache) {
            metadata = this.injectionMetadataCache.get(cacheKey);
            if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                // If there is old metadata, clear it.
                if (metadata != null) {
                    metadata.clear(pvs);
                }
                // Build new InjectionMetadata for the given class.
                metadata = buildAutowiringMetadata(clazz);
                // Update the cache with the newly built metadata.
                this.injectionMetadataCache.put(cacheKey, metadata);
            }
        }
    }
    // Return the metadata that was found or newly built.
    return metadata;
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#buildAutowiringMetadata` method looks at the fields and methods of the class and all its parent classes. It finds all fields and methods with autowiring annotations and creates one `InjectionMetadata` object for them.

```java
private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
    // Check if the class has autowiring annotations. If not, return empty InjectionMetadata directly.
    if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
        return InjectionMetadata.EMPTY;
    }

    // Initialize the list that holds the injection elements.
    List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
    Class<?> targetClass = clazz;

    do {
        // The list of elements to inject in the current class.
        final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

        // Process all fields of the class.
        ReflectionUtils.doWithLocalFields(targetClass, field -> {
            // Find the autowiring annotation on the field.
            MergedAnnotation<?> ann = findAutowiredAnnotation(field);
            if (ann != null) {
                
                // ... [code omitted for brevity]
                
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
            // Find the autowiring annotation on the method.
            MergedAnnotation<?> ann = findAutowiredAnnotation(bridgedMethod);
            if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                
                // ... [code omitted for brevity]
                
                boolean required = determineRequiredStatus(ann);
                PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                // Create a new AutowiredMethodElement and add it to the list.
                currElements.add(new AutowiredMethodElement(method, required, pd));
            }
        });

        // Add the injection elements of the current class to the start of the total list.
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

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#autowiredAnnotationTypes` field tells `AutowiredAnnotationBeanPostProcessor` which annotations it should handle. When the Spring container parses a bean definition and creates a bean instance, if an annotation on a field, method, or constructor of the bean is in the `autowiredAnnotationTypes` set, `AutowiredAnnotationBeanPostProcessor` handles it.

```java
public AutowiredAnnotationBeanPostProcessor() {
   // ... [code omitted for brevity]
   try {
      this.autowiredAnnotationTypes.add((Class<? extends Annotation>)
            ClassUtils.forName("javax.inject.Inject", AutowiredAnnotationBeanPostProcessor.class.getClassLoader()));
      logger.trace("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
   }
   catch (ClassNotFoundException ex) {
      // JSR-330 API not available - simply skip.
   }
}
```

#### Injection Phase

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties` method post-processes the properties of the bean. It handles property injection done with annotations such as `@Inject`.

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

The main goal of the `org.springframework.beans.factory.annotation.InjectionMetadata#inject` method is to inject all elements that need injection (for example fields or methods with annotations such as `@Inject`) into the target bean.

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
    // Step 1. Get the Field object that stands for the field with the @Inject annotation.
    Field field = (Field) this.member;

    Object value;
    // Step 2. If the value of the field is already cached (resolved before), try to get it from the cache.
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
        // Step 4.1. Make the field accessible. This is needed, especially when the field is private.
        ReflectionUtils.makeAccessible(field);
        // Step 4.2. Actually inject the resolved value into the field of the target bean.
        field.set(bean, value);
    }
}
```

Now go to step 3 of the `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject` method. In the `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#resolveFieldValue` method, the value of the field is resolved from the Spring bean factory with the `beanFactory.resolveDependency` method.

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

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method tries to resolve one specific dependency. It first finds all possible matching beans, and then picks the best match. If there are several matching beans, or none, it handles each case.

```java
public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName,
			@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {

    // ... [code omitted for brevity]

    try {
        // If there is a shortcut to resolve the dependency, use it
        Object shortcut = descriptor.resolveShortcut(this);
        if (shortcut != null) {
            return shortcut;
        }

        // Get the type of the dependency
        Class<?> type = descriptor.getDependencyType();
        
        // ... [code omitted for brevity]

        // Step 1. Find the matching beans from the dependency descriptor
        Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
        
        // If no matching bean is found
        if (matchingBeans.isEmpty()) {
            if (isRequired(descriptor)) {
                // If the dependency is required, throw an exception
                raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
            }
            return null;
        }

        String autowiredBeanName;
        Object instanceCandidate;

        // When several matching beans are found
        if (matchingBeans.size() > 1) {
            // Decide the best autowire candidate
            autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
            if (autowiredBeanName == null) {
                if (isRequired(descriptor) || !indicatesMultipleBeans(type)) {
                    // If a single bean cannot be decided, try to resolve the non-unique dependency
                    return descriptor.resolveNotUnique(descriptor.getResolvableType(), matchingBeans);
                }
                else {
                    return null;
                }
            }
            instanceCandidate = matchingBeans.get(autowiredBeanName);
        }
        else {
            // Only one matching bean is found
            Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
            autowiredBeanName = entry.getKey();
            instanceCandidate = entry.getValue();
        }

        // Add the autowired bean name to the set
        if (autowiredBeanNames != null) {
            autowiredBeanNames.add(autowiredBeanName);
        }

        // Step 2. If the candidate is a class, instantiate it
        if (instanceCandidate instanceof Class) {
            instanceCandidate = descriptor.resolveCandidate(autowiredBeanName, type, this);
        }
        
        Object result = instanceCandidate;
        // ... [code omitted for brevity]
        
        return result;
    }
    // ... [code omitted for brevity]
}
```

Now go to step 1 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method. In the `org.springframework.beans.factory.support.DefaultListableBeanFactory#findAutowireCandidates` method, it first gets all possible bean names for the given type. Then, for each possible candidate bean, it checks whether the bean is a suitable autowire candidate. If it is, the bean is added to the result. At the end, the method returns all suitable candidate beans that were found.

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

The `org.springframework.beans.factory.support.DefaultListableBeanFactory#addCandidateEntry` method gets the type of the candidate bean and adds it to the collection of candidate beans.

```java
private void addCandidateEntry(Map<String, Object> candidates, String candidateName,
			DependencyDescriptor descriptor, Class<?> requiredType) {
	// ... [code omitted for brevity]
    candidates.put(candidateName, getType(candidateName));
}
```

The `org.springframework.beans.factory.support.AbstractBeanFactory#getType(name)` method gets the type of a bean from its name.

```java
public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
    return getType(name, true);
}
```

Now go to step 2 of the `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency` method. In the `org.springframework.beans.factory.config.DependencyDescriptor#resolveCandidate` method, we see that the whole `@Inject` flow in the end gets a bean instance from the Spring container and injects it into the matching field or constructor parameter.

```java
public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory)
			throws BeansException {

    return beanFactory.getBean(beanName);
}
```

Finally, go to step 4.2 of the `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject` method. Inside `AutowiredFieldElement#inject`, the `resolveFieldValue(field, bean, beanName)` method finds the right bean value for the field with `@Inject`. Then reflection sets this value. Specifically, it uses the `set` method of the `Field` class to set the value on this field of the target object. This is how Spring injects a value into a field that is marked with `@Inject`.

```java
// Step 4. If the resolved value is not null...
if (value != null) {
    // Step 4.1. Make the field accessible. This is needed, especially when the field is private.
    ReflectionUtils.makeAccessible(field);
    // Step 4.2. Actually inject the resolved value into the field of the target bean.
    field.set(bean, value);
}
```

### 8. Notes

1. **Dependency needed**:
   - `@Inject` is part of the JSR-330 specification, so you need to add the `javax.inject` dependency to your project. Without it, your code does not compile.
2. **No `required` attribute**:
   - Unlike Spring's `@Autowired`, `@Inject` has no `required` attribute. If no matching bean is found, it throws an exception by default.
3. **Use with other annotations**:
   - To pick a specific bean or to remove ambiguity between several candidate beans, use it together with the `@Named` annotation. For example: `@Inject @Named("specificBeanName")`.
4. **Not only for Spring**:
   - `@Inject` is well supported in Spring, but it is not specific to Spring. Other frameworks that implement JSR-330 (such as Google Guice) also support `@Inject`.
5. **Constructor injection is recommended**:
   - You can use `@Inject` on fields, methods, and constructors. But modern best practice is to use constructor injection. It keeps the bean immutable and makes testing easier.
6. **Circular dependency problem**:
   - If you create a circular dependency by mistake with field or method injection, the Spring container may throw an exception. With constructor injection, a circular dependency shows up more clearly.
7. **Do not mix**:
   - In one project, try not to use `@Inject` and `@Autowired` together, to stay consistent. Choose one and keep using it.
8. **Avoid overuse**:
   - Dependency injection is a powerful feature, but use it with care. Too much autowiring, especially in large projects, can make code hard to follow and maintain.
9. **Single responsibility principle**:
   - If a class needs too many dependencies, it may break the single responsibility principle. Consider refactoring or splitting the class.
10. **Compatibility with Java EE**:
    - If your application runs in a Java EE container, the container may already support `@Inject` natively, without Spring.

### 9. Summary

#### Best Practices Summary

1. **Context initialization**
   - When we create `AnnotationConfigApplicationContext` and give the `MyConfiguration` class as the argument, Spring starts to initialize the context. It loads all bean definitions and gets ready to create instances.
2. **Component scan**
   - In the `MyConfiguration` class, we use `@ComponentScan` to set the package path to scan. Spring scans all classes in the given package and its sub-packages, and looks for classes with annotations such as `@Component`, `@Service`, `@Repository`, and `@Controller`. When it finds them, Spring registers these classes as beans automatically.
3. **Dependency resolution**
   - In the `MyController` class, we use the `Inject` annotation on the `myService` field. This tells Spring that when it creates the `MyController` bean, it must find a bean of type `MyService` and inject it into this field.
4. **Instantiation and injection**
   - When we ask the context for a bean of type `MyController`, Spring first creates an instance of `MyController`. Before that, it looks at all fields with `@Inject`, finds the matching beans for these fields, and injects them.
   - In our example, Spring finds the bean of type `MyService` and injects it into the `myService` field.
5. **Running the business logic**
   - When the `showService` method is called, it simply prints the `myService` field. This field was injected successfully, so we see the expected output. It shows that `@Inject` works.
6. **Result**
   - The final output shows that `myService` was injected into `MyController`, and it shows the memory address of the instance.

#### Source Code Analysis Summary

1. **Core post-processor**

   - `AutowiredAnnotationBeanPostProcessor` is the main post-processor that handles annotations such as `@Inject`. It implements two key interfaces, `MergedBeanDefinitionPostProcessor` and `InstantiationAwareBeanPostProcessor`. They let Spring step into key stages of the bean lifecycle and give a way to do property injection.

2. **Collection phase**
   + Get the Inject metadata

     - Spring first uses the `postProcessMergedBeanDefinition` method to make sure the given bean definition matches its expected autowiring metadata.

     - In this method, Spring tries to find the `InjectionMetadata` related to the given bean name and type. It may hold the injection information of the fields and methods of the bean.

   + Find the matching autowiring metadata
     - In `findAutowiringMetadata`, Spring makes sure the latest and related `InjectionMetadata` is always found for the given bean name and class. Spring also uses a cache to improve performance.

   + Build the autowiring metadata

     - In the `buildAutowiringMetadata` method, Spring looks at the fields and methods of the class and all its parent classes, and finds all fields and methods with autowiring annotations.

     - Then it creates one `InjectionMetadata` object for these fields and methods.

   + Check the annotation types
     - The constructor of `AutowiredAnnotationBeanPostProcessor` tells this post-processor which annotations it should handle. For example, `@Inject` is one of them.

3. **Injection phase**

   + Post-process the bean properties

     - In `postProcessProperties`, Spring post-processes the properties of the bean, especially the property injection done with `@Inject`.

     - This means actually injecting the resolved value into the bean.

   + The actual injection of the metadata

     - In the `InjectionMetadata#inject` method, the properties of the bean are actually injected.

     - Spring goes through each element that needs injection and does the actual injection.

   + Field injection

     - In `AutowiredFieldElement#inject`, Spring first checks whether the value of the field is already cached. If it is, the value comes from the cache. Otherwise, it is resolved again.

     - Then it makes sure the field is accessible, and sets the resolved value into the matching field of the target bean.

   + Resolve the dependency

     - In the `doResolveDependency` method, Spring starts to resolve one specific dependency.

     - First, Spring finds all matching beans for the given type.

     - If several matching beans are found, it tries to decide which one is the best autowire candidate.

   + Get the bean type

     - In the `addCandidateEntry` method, Spring gets the type of the candidate bean and adds it to the collection of candidate beans.

     - With the `getType` method, Spring can get the type of a bean from its name.

   + Get the bean instance from the Spring container
     - In `resolveCandidate`, a bean instance is taken from the Spring container and injected into the matching field or constructor parameter.
   + Injection by reflection
     + `field.set(bean, value)` is the step that does the actual field injection. It injects the resolved bean instance (value) into the matching field of the target bean. This is the last step of the whole `@Inject` flow.
