## @Autowired

- [@Autowired](#autowired)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133833938) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [@Autowired source](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-autowired)

### 2. Annotation Description

The `@Autowired` annotation is used for dependency injection (DI). When one bean needs to use another bean in Spring, `@Autowired` injects the needed bean automatically. We do not have to look it up or configure it by hand.

### 3. Annotation Source

`@Autowired` is a core annotation introduced in Spring 2.5. It tells Spring's dependency injection machinery to inject the needed dependency automatically.

```java
/**
 * Marks a constructor, field, setter method, or config method to be autowired
 * by Spring's dependency injection facilities.
 * This is an alternative to the JSR-330 Inject annotation, adding required-vs-optional semantics.
 *
 * Autowired constructors:
 * Only one constructor of any given bean class may declare this annotation with the
 * required attribute set to true. It marks the constructor to autowire when used as a
 * Spring bean. If required is true, only one constructor can be marked as Autowired.
 * If multiple non-required constructors declare the annotation,
 * they are all considered candidates for autowiring. Spring chooses the constructor
 * whose dependencies can be satisfied the most. If none of the candidates qualify,
 * the default constructor is used. If a class declares multiple constructors but none
 * is annotated with Autowired, the default constructor is used.
 * If a class declares only a single constructor from the start, it is always used, even if not annotated.
 * An annotated constructor does not have to be public.
 *
 * Autowired fields:
 * Fields are injected right after construction of a bean, before any config methods are invoked.
 * Such a config field does not have to be public.
 *
 * Autowired methods:
 * Config methods may have an arbitrary name and any number of arguments. Each argument is
 * autowired with a matching bean from the Spring container.
 * Bean property setter methods are just a special case of such a general config method.
 * Such config methods do not have to be public.
 *
 * Autowired parameters:
 * Although Autowired can be declared on individual method or constructor parameters
 * since Spring 5.0, most parts of the framework ignore such declarations.
 * The only part of the core Spring Framework that actively supports autowired parameters is
 * the JUnit Jupiter support in the spring-test module.
 *
 * Multiple arguments and 'required' semantics:
 * For a constructor or method with multiple arguments, the required attribute applies to all arguments.
 *
 * Autowiring arrays, collections, and maps:
 * For an array, Collection, or Map dependency type, the container autowires all beans
 * matching the declared value type. For such purposes, the map keys must be declared
 * as type String, which are resolved to the corresponding bean names.
 *
 * Not supported in BeanPostProcessor or BeanFactoryPostProcessor:
 * Note that actual injection is performed through a BeanPostProcessor, which means
 * you cannot use Autowired to inject references into
 * BeanPostProcessor or BeanFactoryPostProcessor types.
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Sam Brannen
 * @since 2.5
 * @see AutowiredAnnotationBeanPostProcessor
 * @see Qualifier
 * @see Value
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    /**
     * Declares whether the annotated dependency is required.
     * The default is true.
     */
    boolean required() default true;
}
```

### 4. Main Features

1. **Inject dependencies automatically**
   + You do not need to state the relations between beans. Spring finds and injects the needed dependencies.
2. **Field injection**
   + You can put it directly on a field. The field is injected when the bean is initialized.
3. **Constructor injection**
   + When placed on a constructor, that constructor is used to create the bean instance and inject the dependencies.
4. **Method injection**
   + When placed on a setter or another method, the method is called during bean initialization to inject the dependencies.
5. **Set whether a dependency is required**
   + The `required` attribute says whether a dependency is required. If it is required and no matching dependency is found, Spring throws an exception.

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations). We pass the `MyConfiguration` class to its constructor. Then we get a `MyController` bean from the context and call its `showService` method.

```java
public class AutowiredApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyController controller = context.getBean(MyController.class);
        controller.showService();
    }
}
```

The `MyConfiguration` class uses `@ComponentScan("com.xcs.spring")`. It tells Spring to search the given package (here `com.xcs.spring`) and its sub-packages for classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, and so on, and to register them as beans. So we do not need to write a bean definition for each component. Spring finds and registers them.

```java
@Configuration
@ComponentScan("com.xcs.spring")
public class MyConfiguration {

}
```

When the Spring container initializes `MyController`, it injects a bean of type `MyService` into the `myService` field.

```java
@Controller
public class MyController {

    @Autowired
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

The result shows that `@Autowired` works in our Spring context. It injected the needed dependency.

```java
myService = com.xcs.spring.service.MyService@4a883b15
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @Autowired sequence diagram
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>Apply the bean definition post processors
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>Process the merged bean definition
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)<br>Find the autowiring metadata
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:buildAutowiringMetadata(clazz)<br>Build the autowiring metadata
AutowiredAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalFields(clazz,fc)<br>Process the local fields of the class
ReflectionUtils->>AutowiredAnnotationBeanPostProcessor:Resolve the fields with @Autowired
AutowiredAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>Process the local methods of the class
ReflectionUtils->>AutowiredAnnotationBeanPostProcessor:Resolve the methods with @Autowired
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:injectionMetadataCache.put(cacheKey, metadata)<br>Put the metadata into the cache
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,bw)<br>Populate the bean properties
AbstractAutowireCapableBeanFactory->>AutowiredAnnotationBeanPostProcessor:postProcessProperties(pvs,bean,beanName)<br>Post-process the bean properties
AutowiredAnnotationBeanPostProcessor->>AutowiredAnnotationBeanPostProcessor:findAutowiringMetadata(beanName,clazz,pvs)<br>Find the autowiring metadata again
Note right of AutowiredAnnotationBeanPostProcessor:<br>Get the injection metadata from the cache
AutowiredAnnotationBeanPostProcessor->>InjectionMetadata:inject(bean, beanName, pvs)<br>Perform the actual property injection
InjectionMetadata->>AutowiredFieldElement:inject(target, beanName, pvs)<br>Inject the specific field element
AutowiredFieldElement->>AutowiredFieldElement:resolveFieldValue(field,bean,beanName)<br>Resolve the field value
AutowiredFieldElement->>DefaultListableBeanFactory:resolveDependency(desc, beanName, autowiredBeanNames, typeConverter)<br>Resolve the field dependency
DefaultListableBeanFactory->>DefaultListableBeanFactory:doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter)<br>Resolve the given dependency
DefaultListableBeanFactory->>DefaultListableBeanFactory:findAutowireCandidates(beanName, type, descriptor)<br>Find candidate beans that qualify for autowiring
DefaultListableBeanFactory->>DefaultListableBeanFactory:addCandidateEntry(result, candidate, descriptor, requiredType)<br>Add a candidate bean to the result
DefaultListableBeanFactory->>AbstractBeanFactory:getType(name)<br>Get the type of the given bean
AbstractBeanFactory->>DefaultListableBeanFactory:Return the class of the dependent bean<br>Return the actual class of the dependent bean
DefaultListableBeanFactory->>DependencyDescriptor:resolveCandidate(beanName, requiredType, beanFactory)<br>Resolve the candidate dependent bean
DependencyDescriptor->>AbstractBeanFactory:getBean(name)<br>Get the given bean instance
AbstractBeanFactory->>DependencyDescriptor:<br>Return the dependent bean instance
DependencyDescriptor->>DefaultListableBeanFactory:<br>Return the dependent bean instance to the factory
DefaultListableBeanFactory->>AutowiredFieldElement:<br>Return the dependent bean to the field injector
AutowiredFieldElement->>Field:field.set(bean, value)<br>Set the bean into the field
~~~

### 7. Source Code Analysis

#### Prerequisites

In Spring, `AutowiredAnnotationBeanPostProcessor` is the key class that handles `@Autowired` and similar annotations. It implements the two interfaces below. To understand how `@Autowired` works, it helps to study this class and these interfaces. They let you step into key stages of the bean lifecycle, which makes property injection and related work possible.

1. `MergedBeanDefinitionPostProcessor` interface
   - Its `postProcessMergedBeanDefinition` method lets a post processor change the merged bean definition. A merged bean definition is one that already includes the properties of all parent bean definitions. For `@Autowired`, this step usually collects the `@Autowired` information that must be resolved and prepares it for later processing.
   - 🔗 [MergedBeanDefinitionPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. `InstantiationAwareBeanPostProcessor` interface
   - It provides several callback methods that let a post processor step into bean creation before and after instantiation. In particular, `postProcessProperties` lets the post processor work on the bean properties. For `@Autowired`, this is where the bean is handled during property setting or dependency injection, and the resolved values are injected into the bean.
   - 🔗 [InstantiationAwareBeanPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor)

#### Collection Phase

In `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessMergedBeanDefinition`, the main job is to make sure the bean definition matches its expected autowiring metadata.

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    // For the given bean name and type, first look for the related InjectionMetadata, which may hold the injection info of the bean's fields and methods
    InjectionMetadata metadata = findAutowiringMetadata(beanName, beanType, null);
    
    // Use the found InjectionMetadata to check that the config members in the bean definition match the expected injection metadata.
    metadata.checkConfigMembers(beanDefinition);
}
```

In `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#findAutowiringMetadata`, the method always gets up-to-date and relevant `InjectionMetadata` for the given bean name and class. It uses a cache to improve performance.

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
                // Put the newly built metadata into the cache.
                this.injectionMetadataCache.put(cacheKey, metadata);
            }
        }
    }
    // Return the found or newly built metadata.
    return metadata;
}
```

In `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#buildAutowiringMetadata`, the method scans the fields and methods of the class and all its parent classes. It finds those with autowiring annotations and creates one `InjectionMetadata` object for them.

```java
private InjectionMetadata buildAutowiringMetadata(final Class<?> clazz) {
    // Check whether the class has autowiring annotations. If not, return an empty InjectionMetadata.
    if (!AnnotationUtils.isCandidateClass(clazz, this.autowiredAnnotationTypes)) {
        return InjectionMetadata.EMPTY;
    }

    // Create the list that holds the injected elements.
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
            // Look for an autowiring annotation on the method.
            MergedAnnotation<?> ann = findAutowiredAnnotation(bridgedMethod);
            if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                
                // ... [code omitted for brevity]
                
                boolean required = determineRequiredStatus(ann);
                PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                // Create a new AutowiredMethodElement and add it to the list.
                currElements.add(new AutowiredMethodElement(method, required, pd));
            }
        });

        // Add the elements of the current class to the front of the overall list.
        elements.addAll(0, currElements);
        // Move on to the parent class.
        targetClass = targetClass.getSuperclass();
    }
    // Loop until the Object class.
    while (targetClass != null && targetClass != Object.class);

    // Return a new InjectionMetadata created for the element list.
    return InjectionMetadata.forElements(elements, clazz);
}
```

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#autowiredAnnotationTypes` field tells `AutowiredAnnotationBeanPostProcessor` which annotations to handle. When the Spring container parses a bean definition and creates the bean instance, `AutowiredAnnotationBeanPostProcessor` handles any field, method, or constructor annotation that is in the `autowiredAnnotationTypes` set.

```java
public AutowiredAnnotationBeanPostProcessor() {
   this.autowiredAnnotationTypes.add(Autowired.class);
   // ... [code omitted for brevity]
}
```

#### Injection Phase

The `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties` method post-processes bean properties. It is mainly used for property injection through annotations such as `@Autowired`.

```java
@Override
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
    // Get the InjectionMetadata for the bean name and class.
    // It holds all fields and methods of this bean that need injection.
    InjectionMetadata metadata = findAutowiringMetadata(beanName, bean.getClass(), pvs);
    
    try {
        // Use the InjectionMetadata to do the actual property injection.
        metadata.inject(bean, beanName, pvs);
    }
    // If a BeanCreationException occurs during injection, rethrow it.
    catch (BeanCreationException ex) {
        throw ex;
    }
    // Catch other exceptions and throw them as a BeanCreationException with a detailed message.
    catch (Throwable ex) {
        throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
    }
    // Return the original PropertyValues, because this method focuses on dependency injection, not on changing properties.
    return pvs;
}
```

The `org.springframework.beans.factory.annotation.InjectionMetadata#inject` method injects all elements that need injection (for example, fields or methods with `@Autowired`) into the target bean.

```java
public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // Get the checked elements. Usually all elements are checked once during initialization.
    Collection<InjectedElement> checkedElements = this.checkedElements;

    // If there are checked elements, use them. Otherwise use all injected elements.
    Collection<InjectedElement> elementsToIterate =
        (checkedElements != null ? checkedElements : this.injectedElements);

    // If there are elements to inject...
    if (!elementsToIterate.isEmpty()) {
        // Go through each element and inject it into the target bean.
        for (InjectedElement element : elementsToIterate) {
            // Inject each element (field or method).
            element.inject(target, beanName, pvs);
        }
    }
}
```

In `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject`, the method first checks whether the field value is cached. If it is, it takes the value from the cache. Otherwise it resolves the value again. Then it makes sure the field is accessible (especially for private fields) and sets the resolved value on the matching field of the target bean.

```java
@Override
protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    // Step 1. Get the Field object of the field annotated with @Autowired.
    Field field = (Field) this.member;

    Object value;
    // Step 2. If the field value is already cached (resolved before), try to get it from the cache.
    if (this.cached) {
        try {
            // Get the resolved field value from the cache.
            value = resolvedCachedArgument(beanName, this.cachedFieldValue);
        }
        catch (NoSuchBeanDefinitionException ex) {
            // The cached bean was removed unexpectedly -> resolve again.
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

Start with step 3 in `AutowiredFieldElement#inject`. In `org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#resolveFieldValue`, the field value is resolved from Spring's bean factory through `beanFactory.resolveDependency`.

```java
@Nullable
private Object resolveFieldValue(Field field, Object bean, @Nullable String beanName) {
    // ... [code omitted for brevity]
    Object value;
    try {
        // Resolve the field value from Spring's bean factory through `beanFactory.resolveDependency`
        value = beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter);
    }
    catch (BeansException ex) {
        throw new UnsatisfiedDependencyException(null, beanName, new InjectionPoint(field), ex);
    }
    // ... [code omitted for brevity]
    return value;
}
```

In `org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency`, the method first tries to get a lazy resolution proxy. If it cannot, it goes on to resolve the dependency. `doResolveDependency` does the actual resolving.

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

In `org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency`, the method resolves one dependency. It first finds all possible matching beans, then picks the best match. If there are several matches, or none, it handles each case.

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

        // Step 1. Find matching beans for the dependency descriptor
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
            // Determine the best autowire candidate
            autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
            if (autowiredBeanName == null) {
                if (isRequired(descriptor) || !indicatesMultipleBeans(type)) {
                    // If a single bean cannot be determined, try to resolve the non-unique dependency
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

        // Add the autowired bean name to the collection
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

Now step 1 in `DefaultListableBeanFactory#doResolveDependency`. In `org.springframework.beans.factory.support.DefaultListableBeanFactory#findAutowireCandidates`, the method first gets all possible bean names for the given type. Then, for each candidate bean, it checks whether the bean is a suitable autowire candidate. If so, it adds the bean to the result. At the end, it returns all suitable candidate beans.

```java
protected Map<String, Object> findAutowireCandidates(
			@Nullable String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {

    // Get all possible bean names for the required type, including beans in parent factories
    String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
        this, requiredType, true, descriptor.isEager());

    // ... [code omitted for brevity]

    // Go through all candidate bean names
    for (String candidate : candidateNames) {
        // If the candidate is not the bean being looked up and it is a suitable autowire candidate
        if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, descriptor)) {
            // Add this candidate bean to the result
            addCandidateEntry(result, candidate, descriptor, requiredType);
        }
    }

    // ... [code omitted for brevity]

    // Return all candidate beans found
    return result; 
}
```

In `org.springframework.beans.factory.support.DefaultListableBeanFactory#addCandidateEntry`, the method gets the type of the candidate bean and adds it to the candidate collection.

```java
private void addCandidateEntry(Map<String, Object> candidates, String candidateName,
			DependencyDescriptor descriptor, Class<?> requiredType) {
	// ... [code omitted for brevity]
    candidates.put(candidateName, getType(candidateName));
}
```

In `org.springframework.beans.factory.support.AbstractBeanFactory#getType(name)`, the method gets the type of a bean from its name.

```java
public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
    return getType(name, true);
}
```

Now step 2 in `DefaultListableBeanFactory#doResolveDependency`. In `org.springframework.beans.factory.config.DependencyDescriptor#resolveCandidate`, we see that the whole `@Autowired` flow ends by getting a bean instance from the Spring container and injecting it into the matching field or constructor parameter.

```java
public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory)
			throws BeansException {

    return beanFactory.getBean(beanName);
}
```

Finally, we come back to step 4.2 in `AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject`. Inside `AutowiredFieldElement#inject`, `resolveFieldValue(field, bean, beanName)` finds the right bean value for the `@Autowired` field. Spring then sets the value by reflection, using the `set` method of the `Field` class. This is how `@Autowired` on a field makes Spring inject a value into it.

```java
// Step 4. If the resolved value is not null...
if (value != null) {
    // Step 4.1. Make the field accessible. This is needed especially when the field is private.
    ReflectionUtils.makeAccessible(field);
    // Step 4.2. Inject the resolved value into the field of the target bean.
    field.set(bean, value);
}
```

### 8. Notes

1. **Dependencies are required by default**
   - If Spring cannot find a matching bean to inject, it throws an exception. You can make the dependency optional by setting `required` to `false`: `@Autowired(required=false)`.
2. **Type matching**
   - By default, Spring resolves dependencies by type. If several beans match, it throws an exception.
3. **Use `@Qualifier`**
   - If there are several beans of the same type, use `@Qualifier` to give the bean name and remove the ambiguity.
4. **Circular dependency**
   - `@Autowired` can cause circular dependency problems. For example, A depends on B, and B depends on A. Spring has a way to handle circular dependencies of singleton beans, but for prototype beans a circular dependency causes an exception.
5. **Injection point**
   - You can use `@Autowired` on fields, constructors, or setter methods. The recommended way is the constructor. This makes sure all dependencies are injected when the object is created.
6. **Scope**
   - Be careful not to inject a bean with a short scope (for example, prototype) into a bean with a long scope (for example, singleton), unless you know exactly what you are doing.
7. **Consider constructor injection**
   - Constructor injection makes sure the bean is fully initialized when it is constructed, so the bean stays in an unchanging state. It also helps to mock dependencies in unit tests.
8. **Private field injection**
   - You can apply `@Autowired` to private fields. But then Spring uses reflection to skip the normal Java access control. This may not be a best practice.
9. **Static fields are not supported**
   + `@Autowired` cannot be used on static fields. A static field belongs to the class, not to an instance, and Spring injects dependencies into instances.
10. **Static methods are not supported**
    + `@Autowired` also cannot be used on static setters or other static methods.

### 9. Summary

#### Best Practices Summary

1. **Context initialization**
   - When we create `AnnotationConfigApplicationContext` with the `MyConfiguration` class, Spring starts to initialize the context. It loads all bean definitions and prepares to create instances.
2. **Component scanning**
   - In `MyConfiguration`, we use `@ComponentScan` to give the package to scan. Spring scans that package and its sub-packages for classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, and so on. It registers the classes it finds as beans.
3. **Dependency resolution**
   - In `MyController`, we put `@Autowired` on the `myService` field. This tells Spring that when it creates the `MyController` bean, it must find a bean of type `MyService` and inject it into the field.
4. **Instantiation and injection**
   - When we ask the context for a `MyController` bean, Spring first creates an instance of `MyController`. Before that, it looks at all fields with `@Autowired`, finds a matching bean for each, and injects it.
   - In our example, Spring finds the `MyService` bean and injects it into the `myService` field.
5. **Running the business logic**
   - When `showService` is called, it simply prints the `myService` field. The field was injected successfully, so we see the expected output. This shows that `@Autowired` works.
6. **Result**
   - The output shows that `myService` was injected into `MyController`. It also shows the memory address of the instance.

#### Source Code Analysis Summary

1. **Core post processor**

   - `AutowiredAnnotationBeanPostProcessor` is the main post processor that handles `@Autowired` and similar annotations. It implements two key interfaces, `MergedBeanDefinitionPostProcessor` and `InstantiationAwareBeanPostProcessor`. They let it step into key stages of the bean lifecycle and give it the means to inject properties.

2. **Collection phase**

   + Get the Autowired metadata

     - Spring first uses `postProcessMergedBeanDefinition` to make sure the bean definition matches its expected autowiring metadata.

     - In this method, Spring tries to find the `InjectionMetadata` for the given bean name and type. It may hold the injection info of the bean's fields and methods.

   + Find the matching autowiring metadata
     - In `findAutowiringMetadata`, Spring makes sure it always gets up-to-date and relevant `InjectionMetadata` for the given bean name and class. Spring also uses a cache to improve performance.

   + Build the autowiring metadata

     - In `buildAutowiringMetadata`, Spring scans the fields and methods of the class and all its parent classes, and finds those with autowiring annotations.

     - Then it creates one `InjectionMetadata` object for these fields and methods.

   + Check the annotation types
     - The constructor of `AutowiredAnnotationBeanPostProcessor` tells the post processor which annotations to handle. For example, `@Autowired` is one of them.

3. **Injection phase**

   + Post-process the bean properties

     - In `postProcessProperties`, Spring post-processes the bean properties, especially property injection through `@Autowired`.

     - This includes injecting the resolved values into the bean.

   + The actual injection with the injection metadata

     - The `InjectionMetadata#inject` method does the actual property injection into the bean.

     - Spring goes through each element that needs injection and injects it.

   + Field injection

     - In `AutowiredFieldElement#inject`, Spring first checks whether the field value is cached. If it is, Spring takes it from the cache. Otherwise it resolves the value again.

     - Then it makes sure the field is accessible and sets the resolved value on the matching field of the target bean.

   + Resolve the dependency

     - In `doResolveDependency`, Spring starts to resolve one dependency.

     - First, Spring finds all beans that match the given type.

     - If several beans match, it tries to decide which one is the best autowire candidate.

   + Get the bean type

     - In `addCandidateEntry`, Spring gets the type of the candidate bean and adds it to the candidate collection.

     - With the `getType` method, Spring gets the type of a bean from its name.

   + Get the bean instance from the Spring container
     - In `resolveCandidate`, a bean instance is taken from the Spring container and injected into the matching field or constructor parameter.
   + Injection by reflection
     + The actual field injection is done with `field.set(bean, value)`. It sets the resolved bean instance (value) on the matching field of the target bean. This is the final step of the whole `@Autowired` flow.