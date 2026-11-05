## @Resource

- [@Resource](#resource)
  - [1. Overview](#1-overview)
  - [2. Interface Description](#2-interface-description)
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

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133887864) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Interface Description

The `@Resource` annotation is defined by the JSR-250 specification, Common Annotations for the Java Platform. This specification defines a set of common annotations used across several Java technologies (such as Java EE and Java SE).

### 3. Interface Source

The `@Resource` annotation declares and injects the external resources an application needs. The container then provides these resources to application components at runtime.

```java
/**
 * Annotation that marks a resource to be injected.
 * It can be used on classes, fields, and methods. It tells the container to inject the resource.
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Resources.class)
public @interface Resource {

    /**
     * The JNDI name of the resource.
     *
     * @return the resource name.
     */
    String name() default "";

    /**
     * The JNDI lookup name, used to find the resource at runtime.
     *
     * @return the JNDI lookup name.
     */
    String lookup() default "";

    /**
     * The expected type of the resource. The default is Object, which means no specific type.
     *
     * @return the resource type.
     */
    Class<?> type() default Object.class;

    /**
     * The authentication type of the resource.
     *
     * @return the authentication type.
     */
    Resource.AuthenticationType authenticationType() default Resource.AuthenticationType.CONTAINER;

    /**
     * Shows whether the resource can be shared by several clients.
     *
     * @return true if the resource can be shared, otherwise false.
     */
    boolean shareable() default true;

    /**
     * A product-specific name linked to the resource environment reference.
     *
     * @return the mapped name.
     */
    String mappedName() default "";

    /**
     * A short description of the resource.
     *
     * @return the resource description.
     */
    String description() default "";

    /**
     * Enum of authentication types.
     * CONTAINER: authentication is managed by the container.
     * APPLICATION: authentication is managed by the application.
     */
    public static enum AuthenticationType {
        CONTAINER,
        APPLICATION;
    }
}
```

### 4. Main Features

1. **Resource lookup**
   + With the `name` and `lookup` attributes, `@Resource` can find a specific resource, such as a database connection in JNDI.
2. **Type setting**
   + The `type` attribute sets the exact Java type of the resource. This makes sure the injected resource matches the expected type, so it is type safe.
3. **Authentication strategy**
   + The `authenticationType` attribute lets you choose how the resource is authenticated: by the container or by the application.
4. **Sharing strategy**
   + The `shareable` attribute sets whether the resource can be shared between several clients or components.
5. **Vendor-specific name**
   + The `mappedName` attribute can give a vendor-specific or platform-specific name for the resource. This makes deployment more flexible.
6. **Description**
   + The `description` attribute gives a short description of the resource. It helps developers and system administrators understand what it is for.

### 5. Best Practices

First, look at the entry class. The context is `AnnotationConfigApplicationContext` (this class configures the Spring container with Java annotations). We pass the `MyConfiguration` component class as the constructor argument. Then we get a bean of type `MyController` from the Spring context and call its `showService` method.

```java
public class ResourceApplication {

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

When the Spring container initializes `MyController`, the `@Resource` annotation injects a bean of type `MyService` into the `myService` field.

```java
@Controller
public class MyController {

    @Resource(name="myService")
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

The result shows that `@Resource` works well in our Spring context. It injects the required dependency successfully.

```java
myService = com.xcs.spring.service.MyService@f0c8a99
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram
Title: @Resource annotation sequence diagram
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd,beanType,beanName)<br>Apply the bean definition post-processors
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:postProcessMergedBeanDefinition(beanDefinition,beanType,beanName)<br>Handle the merged bean definition
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:findResourceMetadata(beanName,clazz,pvs)
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:buildResourceMetadata(clazz)
CommonAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalFields(clazz,fc)<br>Process the local fields of the class
ReflectionUtils->>CommonAnnotationBeanPostProcessor:Find fields with the @Resource annotation
CommonAnnotationBeanPostProcessor->>ResourceElement:ResourceElement(member,ae,pd)
CommonAnnotationBeanPostProcessor->>ReflectionUtils:doWithLocalMethods(clazz,fc)<br>Process the local methods of the class
ReflectionUtils->>CommonAnnotationBeanPostProcessor:Find methods with the @Resource annotation
CommonAnnotationBeanPostProcessor->>ResourceElement:ResourceElement(member,ae,pd)
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:injectionMetadataCache.put(cacheKey, metadata)<br>Put the metadata into the cache
AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName,mbd,bw)<br>Fill in the property values of the bean
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:postProcessProperties(pvs,bean,beanName)<br>Post-process the properties of the bean
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:findResourceMetadata(beanName,clazz,pvs)
Note right of CommonAnnotationBeanPostProcessor:<br>Get the injection metadata from the cache
CommonAnnotationBeanPostProcessor->>InjectionMetadata:inject(target,beanName,pvs)
InjectionMetadata->>InjectedElement:inject(target,requestingBeanName,pvs)
InjectedElement->>ResourceElement:getResourceToInject(target,requestingBeanName)
ResourceElement->>CommonAnnotationBeanPostProcessor:getResource(element,requestingBeanName)
CommonAnnotationBeanPostProcessor->>CommonAnnotationBeanPostProcessor:autowireResource(factory,element,requestingBeanName)
CommonAnnotationBeanPostProcessor->>AbstractAutowireCapableBeanFactory:resolveBeanByName(name,descriptor)
AbstractAutowireCapableBeanFactory->>CommonAnnotationBeanPostProcessor:Return the dependent bean
CommonAnnotationBeanPostProcessor->>ResourceElement:Return the dependent bean
ResourceElement->>InjectedElement:Return the dependent bean
InjectedElement->>Field:field.set(target, dependent bean)
~~~

### 7. Source Code Analysis

#### Prerequisites

In Spring, `CommonAnnotationBeanPostProcessor` is the key class that handles annotations such as `@Inject`. It implements the two interfaces below. To understand how `@Inject` works, you need to know these interfaces. They let you step into key stages of the bean lifecycle, so property injection and other related work can be done.

1. `MergedBeanDefinitionPostProcessor` interface
   - Its `postProcessMergedBeanDefinition` method lets a post-processor change the merged bean definition. A merged bean definition already includes all properties from parent bean definitions. For `@Inject`, this step usually collects the `@Inject` information to parse and prepares it for later processing.
   - 🔗 [MergedBeanDefinitionPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor)
2. `InstantiationAwareBeanPostProcessor` interface
   - This interface has several callback methods. They let a post-processor step into the bean creation process before and after instantiation. The `postProcessProperties` method lets a post-processor work on the properties of the bean. For `@Inject`, the bean usually needs to be processed during property setting or dependency injection, and the resolved value is injected into the bean.
   - 🔗 [InstantiationAwareBeanPostProcessor interface link](https://github.com/xuchengsheng/spring-reading/tree/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor)

#### Collection Phase

The `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition` method handles the resource injection metadata related to `@Resource`. After the bean definition is merged, it processes or checks this metadata. This is the entry point where Spring handles the JSR-250 `@Resource` annotation.

```java
@Override
public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    // Call the parent postProcessMergedBeanDefinition method, so the inherited logic also runs
    super.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);
    
    // Find the resource injection metadata for the given bean name and type
    InjectionMetadata metadata = findResourceMetadata(beanName, beanType, null);
    
    // Use the resource injection metadata to process or check the bean definition
    metadata.checkConfigMembers(beanDefinition);
}
```

In the `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#findResourceMetadata` method, it first tries to get the `InjectionMetadata` from the cache. If it does not exist or needs a refresh, it creates a new `InjectionMetadata` and stores it in the cache. This cache avoids building the injection metadata again and again for the same class.

```java
private InjectionMetadata findResourceMetadata(String beanName, final Class<?> clazz, @Nullable PropertyValues pvs) {
    
    // If beanName has a value, use it as the cache key. Otherwise, use the class name as the cache key.
    // This also keeps backward compatibility for custom calls.
    String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    
    // First, do a quick check in the concurrent map cache, to keep locking to a minimum.
    InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
    
    // Check if the current metadata needs a refresh, for example if it is out of date or no longer fits.
    if (InjectionMetadata.needsRefresh(metadata, clazz)) {
         // Synchronize on the cache to be thread safe
        synchronized (this.injectionMetadataCache) { 
            metadata = this.injectionMetadataCache.get(cacheKey);
            if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                if (metadata != null) {
                    // Clear the old metadata
                    metadata.clear(pvs);  
                }
                // Build the new resource injection metadata
                metadata = buildResourceMetadata(clazz);
                // Put the new metadata into the cache
                this.injectionMetadataCache.put(cacheKey, metadata);
            }
        }
    }
    // Return the resource injection metadata that was found or created
    return metadata;  
}
```

The `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#buildResourceMetadata` method checks all fields and methods of the given class and all its superclasses. It looks for elements marked with `@Resource` and other related annotations, and creates an `InjectionMetadata` object from them. This object holds `ResourceElement` objects. Each one has all the information needed to inject the resource.

```java
private InjectionMetadata buildResourceMetadata(final Class<?> clazz) {
    
    // Check if the given class may contain any resource annotations.
    if (!AnnotationUtils.isCandidateClass(clazz, resourceAnnotationTypes)) {
        return InjectionMetadata.EMPTY;  // If not, return empty metadata.
    }

    List<InjectionMetadata.InjectedElement> elements = new ArrayList<>();
    Class<?> targetClass = clazz;

    // Start going through the given class and all its superclasses
    do {
        final List<InjectionMetadata.InjectedElement> currElements = new ArrayList<>();

        // Check the annotations on all local fields of the class
        ReflectionUtils.doWithLocalFields(targetClass, field -> {
            // ... (this code checks several annotations, such as @WebServiceRef, @EJB, and @Resource, and creates the matching elements)
            
            // ... [code omitted for brevity]
            
            if (field.isAnnotationPresent(Resource.class)) {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("@Resource annotation is not supported on static fields");
                }
                if (!this.ignoredResourceTypes.contains(field.getType().getName())) {
                    currElements.add(new ResourceElement(field, field, null));
                }
            }
        });

        // Check the annotations on all local methods of the class
        ReflectionUtils.doWithLocalMethods(targetClass, method -> {
            // ... (like for fields, this code checks several annotations and creates the matching elements)
            
            // ... [code omitted for brevity]
            if (bridgedMethod.isAnnotationPresent(Resource.class)) {
                if (Modifier.isStatic(method.getModifiers())) {
                    throw new IllegalStateException("@Resource annotation is not supported on static methods");
                }
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length != 1) {
                    throw new IllegalStateException("@Resource annotation requires a single-arg method: " + method);
                }
                if (!this.ignoredResourceTypes.contains(paramTypes[0].getName())) {
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                    currElements.add(new ResourceElement(method, bridgedMethod, pd));
                }
            }
        });

        // Add the elements found in the current class to the start of the total list
        elements.addAll(0, currElements);
        // Move to the next superclass
        targetClass = targetClass.getSuperclass();
    }
    // Keep going until there is no superclass or the Object class is reached
    while (targetClass != null && targetClass != Object.class);
    // Create and return an InjectionMetadata instance from the collected injection elements
    return InjectionMetadata.forElements(elements, clazz);
}
```

The `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.ResourceElement#ResourceElement` constructor creates a `ResourceElement` instance. It uses the `@Resource` annotation and other information (such as the field or method name and the `@Lazy` annotation). The instance holds all the information needed to find and inject a specific resource.

PS: In the `ResourceElement` implementation, Spring only uses four attributes of `@Resource`: `name`, `type`, `lookup`, and `mappedName`. This is because these attributes are the most closely related to Spring's DI (dependency injection) mechanism. The other attributes, such as `authenticationType`, `shareable`, and `description`, have no real use in Spring or are not implemented.

```java
public ResourceElement(Member member, AnnotatedElement ae, @Nullable PropertyDescriptor pd) {
    
    // Call the parent constructor with the member and the property descriptor
    super(member, pd);

    // Get the @Resource annotation from the given annotated element (field or method)
    Resource resource = ae.getAnnotation(Resource.class);

    // Get the name and type of the resource
    String resourceName = resource.name();
    Class<?> resourceType = resource.type();

    // Check if the resource name is the default name (not set explicitly)
    this.isDefaultName = !StringUtils.hasLength(resourceName);
    
    if (this.isDefaultName) {
        // If the resource name is the default, use the name of the member as the resource name
        resourceName = this.member.getName();
        // If this is a setter method, the property name may be taken as the resource name
        if (this.member instanceof Method && resourceName.startsWith("set") && resourceName.length() > 3) {
            resourceName = Introspector.decapitalize(resourceName.substring(3));
        }
    }
    // Resolve possible placeholders or expressions
    else if (embeddedValueResolver != null) {
        resourceName = embeddedValueResolver.resolveStringValue(resourceName);
    }

    // If the resource type is set explicitly, check that type
    if (Object.class != resourceType) {
        checkResourceType(resourceType);
    }
    else {
        // If the resource type is not set explicitly, work it out from the type of the member
        resourceType = getResourceType();
    }
    
    this.name = (resourceName != null ? resourceName : "");
    this.lookupType = resourceType;
    
    // Get the lookup value or the mapped name
    String lookupValue = resource.lookup();
    this.mappedName = (StringUtils.hasLength(lookupValue) ? lookupValue : resource.mappedName());

    // Check if the @Lazy annotation exists, and set the lazyLookup attribute from it
    Lazy lazy = ae.getAnnotation(Lazy.class);
    this.lazyLookup = (lazy != null && lazy.value());
}
```

#### Injection Phase

The `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#postProcessProperties` method is part of Spring's `InstantiationAwareBeanPostProcessor` interface. It works after the bean is instantiated and before the properties are injected. This implementation handles the `@Resource` annotation.

```java
@Override
public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) {
    
    // Find the resource injection metadata from the bean name and class
    InjectionMetadata metadata = findResourceMetadata(beanName, bean.getClass(), pvs);

    try {
        // Try to inject into the given bean with the metadata that was found
        metadata.inject(bean, beanName, pvs);
    }
    // If anything goes wrong during injection, throw a bean creation exception
    catch (Throwable ex) {
        throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
    }

    // Return the processed property values
    return pvs;
}
```

In the `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#findResourceMetadata` method, the first call happens in the metadata collection phase, `CommonAnnotationBeanPostProcessor#postProcessMergedBeanDefinition`. There, `findResourceMetadata` processes and caches the `InjectionMetadata` for `@Resource` and other related annotations. So when `findResourceMetadata` is called again in later lifecycle steps such as `postProcessProperties`, it returns the cached `InjectionMetadata`. It does not need to build it again.

```java
private InjectionMetadata findResourceMetadata(String beanName, final Class<?> clazz, @Nullable PropertyValues pvs) {
    
    // If beanName exists, use it as the cache key. Otherwise, use the class name. This also keeps backward compatibility for custom callers.
    String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
    
    // First, do a quick check in the concurrent map, to keep locking to a minimum.
    InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
    
    // Check if the current metadata needs a refresh. For example, it may be out of date or no longer fit.
    if (InjectionMetadata.needsRefresh(metadata, clazz)) {
        synchronized (this.injectionMetadataCache) {  // Synchronize on the cache to be thread safe
            metadata = this.injectionMetadataCache.get(cacheKey);
            if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                if (metadata != null) {
                    metadata.clear(pvs);  // Clear the old metadata
                }
                // Build the new resource injection metadata
                metadata = buildResourceMetadata(clazz);
                // Put the new metadata into the cache
                this.injectionMetadataCache.put(cacheKey, metadata);
            }
        }
    }
    return metadata;  // Return the resource injection metadata that was found or created
}
```

The `org.springframework.beans.factory.annotation.InjectionMetadata#inject` method goes through the `InjectedElement` collection and does the actual injection into the given target object.

```java
public void inject(Object target, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
    Collection<InjectedElement> checkedElements = this.checkedElements;
    Collection<InjectedElement> elementsToIterate =
        (checkedElements != null ? checkedElements : this.injectedElements);
    if (!elementsToIterate.isEmpty()) {
        for (InjectedElement element : elementsToIterate) {
            element.inject(target, beanName, pvs);
        }
    }
}
```

The `org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement#inject` method does the actual resource injection, depending on the type of the injection point (field or method). For a field, it sets the field value directly. For a method, it calls the method and passes the resource as the argument.

```java
protected void inject(Object target, @Nullable String requestingBeanName, @Nullable PropertyValues pvs)
				throws Throwable {

    // If the injection point is a field
    if (this.isField) {
        Field field = (Field) this.member; // Get the field
        ReflectionUtils.makeAccessible(field); // Make sure the field is accessible, even if it is private
        // Actually set/inject the resource into the field of the target object
        field.set(target, getResourceToInject(target, requestingBeanName));
    }
    else { // If the injection point is a method (for example a setter method)
        // Check if the property injection should be skipped, possibly based on the given property values (pvs)
        if (checkPropertySkipping(pvs)) {
            return;
        }
        try {
            Method method = (Method) this.member; // Get the method
            ReflectionUtils.makeAccessible(method); // Make sure the method is accessible, even if it is private
            // Actually inject the resource into the target object by calling the method
            method.invoke(target, getResourceToInject(target, requestingBeanName));
        }
        catch (InvocationTargetException ex) {
            // If an exception happens when calling the method, throw the real target exception
            throw ex.getTargetException();
        }
    }
}
```

In the `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.ResourceElement#getResourceToInject` method, the `lazyLookup` attribute decides whether to build a lazy proxy for the resource. If `lazyLookup` is `true`, it returns a proxy object for the lazy resource. Otherwise, it returns the resource instance directly.

```java
@Override
protected Object getResourceToInject(Object target, @Nullable String requestingBeanName) {
    // Check if the resource should be loaded lazily
    return (this.lazyLookup ? 
            // If lazy, build a lazy proxy for the resource
            // If not lazy, get the resource directly
            getResource(this, requestingBeanName));
}
```

The `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#getResource` method first tries to get the resource from JNDI. If JNDI is not available or does not apply, it tries to autowire the resource from the Spring context. If neither works, it throws an exception.

```java
protected Object getResource(LookupElement element, @Nullable String requestingBeanName)
			throws NoSuchBeanDefinitionException {

    // If the 'mappedName' attribute of the LookupElement has a value
    if (StringUtils.hasLength(element.mappedName)) {
        // Get the resource that matches 'mappedName' from JNDI
        return this.jndiFactory.getBean(element.mappedName, element.lookupType);
    }
    // If it is configured to always use JNDI lookup
    if (this.alwaysUseJndiLookup) {
        // Use the 'name' attribute of the LookupElement to get the resource from JNDI
        return this.jndiFactory.getBean(element.name, element.lookupType);
    }
    // If no resourceFactory is configured (for example, a Spring ApplicationContext)
    if (this.resourceFactory == null) {
        // Throw an exception, because the resource cannot be taken from the Spring context
        throw new NoSuchBeanDefinitionException(element.lookupType,
                                                "No resource factory configured - specify the 'resourceFactory' property");
    }
    // Autowire the resource from the Spring ApplicationContext
    return autowireResource(this.resourceFactory, element, requestingBeanName);
}
```

The `org.springframework.context.annotation.CommonAnnotationBeanPostProcessor#autowireResource` method first autowires the resource from the description given by the `LookupElement`. It can resolve the dependency by type match, or directly by name. For autowired resources, if the `BeanFactory` is a `ConfigurableBeanFactory`, it registers a dependency for each autowired bean name.

```java
protected Object autowireResource(BeanFactory factory, LookupElement element, @Nullable String requestingBeanName)
			throws NoSuchBeanDefinitionException {

    Object resource;
    Set<String> autowiredBeanNames;
    String name = element.name;

    // If the factory supports autowiring
    if (factory instanceof AutowireCapableBeanFactory) {
        AutowireCapableBeanFactory beanFactory = (AutowireCapableBeanFactory) factory;
        DependencyDescriptor descriptor = element.getDependencyDescriptor();
        
        // When the resource name is the default, and the BeanFactory has no bean with this name
        if (this.fallbackToDefaultTypeMatch && element.isDefaultName && !factory.containsBean(name)) {
            autowiredBeanNames = new LinkedHashSet<>();
            resource = beanFactory.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames, null);
            if (resource == null) {
                throw new NoSuchBeanDefinitionException(element.getLookupType(), "No resolvable resource object");
            }
        }
        // Otherwise, resolve the resource by name
        else {
            resource = beanFactory.resolveBeanByName(name, descriptor);
            autowiredBeanNames = Collections.singleton(name);
        }
    }
    // If autowiring is not supported, get the resource from the BeanFactory with the name and type
    else {
        resource = factory.getBean(name, element.lookupType);
        autowiredBeanNames = Collections.singleton(name);
    }

    // If the BeanFactory is configurable, register a dependent bean for each autowired bean name
    if (factory instanceof ConfigurableBeanFactory) {
        ConfigurableBeanFactory beanFactory = (ConfigurableBeanFactory) factory;
        for (String autowiredBeanName : autowiredBeanNames) {
            if (requestingBeanName != null && beanFactory.containsBean(autowiredBeanName)) {
                beanFactory.registerDependentBean(autowiredBeanName, requestingBeanName);
            }
        }
    }

    return resource;
}
```

### 8. Notes

1. **Source**
   + The `@Resource` annotation comes from the Java JSR-250 specification, not from Spring core. Still, Spring fully supports it.
2. **Default name**
   + If you do not give a name, `@Resource` looks for a matching bean by the field name or method name. For example, `@Resource private MyService myService;` looks for a bean named "myService".
3. **Type vs name**
   + `@Resource` autowires by name by default. If no bean with a matching name is found, it falls back to matching by type. This is different from `@Autowired`, which autowires by type by default.
4. **Set the name**
   + You can set the name of the bean to inject with the `name` attribute: `@Resource(name = "myService")`.
5. **Handle conflicts**
   + If a context has several beans of the same type, use the `name` attribute to say which bean you want. This avoids conflicts.
6. **Use with other annotations**
   + Do not use `@Resource` together with `@Autowired` or `@Inject` on the same field or setter.
7. **Static fields**
   + `@Resource` does not support static fields. A static field does not belong to any instance, so dependencies cannot be injected into it.
8. **Required**
   + By default, a dependency marked with `@Resource` is required. If no matching bean is found, an exception is thrown. If you want to allow a dependency to be null or missing, you need other configuration, for example `@Autowired(required = false)`.
9. **Lazy loading**
   + In Spring, if you want to delay the initialization of a resource and load it on the first request, use it together with the `@Lazy` annotation.

### 9. Summary

#### Best Practices Summary

1. **Entry class**
   + The `ResourceApplication` class is the entry point of the application. It creates an annotation-based Spring application context, `AnnotationConfigApplicationContext`, and gives it the configuration class `MyConfiguration`.
2. **Context initialization**
   + With `AnnotationConfigApplicationContext`, the Spring context starts, loads the configuration class, and scans the given package and its sub-packages for components.
3. **Component scan**
   + The `MyConfiguration` class uses `@ComponentScan` to tell Spring to search the "`com.xcs.spring`" package and its sub-packages. Spring looks for classes with annotations such as `@Component`, `@Service`, `@Repository`, and `@Controller`, and registers them as beans automatically.
4. **Dependency injection**
   + In the `MyController` class, a field named `myService` uses the `@Resource` annotation. This tells Spring that when it creates an instance of `MyController`, it must inject a bean of type `MyService` into the `myService` field. Because `name="myService"` is set, Spring injects by name, not by type.
5. **Service creation**
   + The `MyService` class is marked with `@Service`. Spring creates its instance automatically and registers it in the context. During dependency injection, it is injected into the `myService` field of `MyController`.
6. **Method call**
   + In the `main` method of `ResourceApplication`, the `MyController` bean is taken from the context and its `showService` method is called. This prints the `myService` instance. It shows that the injection worked.
7. **Output**
   + The application prints the `myService` instance. This shows that `@Resource` completed the dependency injection, and the whole process works well.

#### Source Code Analysis Summary

1. **Prerequisites**
   + `CommonAnnotationBeanPostProcessor` is the core class that handles `@Resource` and other related annotations. To understand how `@Resource` works, we looked at two interfaces: `MergedBeanDefinitionPostProcessor` and `InstantiationAwareBeanPostProcessor`. They let Spring step into key stages of the bean lifecycle, so property injection and other related work can be done.
2. **Collection phase**
   - `postProcessMergedBeanDefinition` method: after the bean definition is merged, it processes the resource injection metadata related to `@Resource`.
   - `findResourceMetadata` method: tries to get the `InjectionMetadata` for `@Resource` from the cache. If it does not exist or needs a refresh, it creates a new `InjectionMetadata` and adds it to the cache.
   - `buildResourceMetadata` method: checks the class and all its superclasses for fields and methods with `@Resource` and other related annotations, and creates a new `InjectionMetadata` object for these elements.
3. **Injection phase**
   - `postProcessProperties` method: works after the bean is instantiated and before the properties are injected. It handles the injection related to `@Resource`.
   - `inject` method: goes through the `InjectedElement` collection and does the actual injection into the given target object.
   - `getResourceToInject` method: uses the `lazyLookup` attribute to decide whether to build a lazy proxy for the resource or return the resource instance directly.
   - `getResource` method: first tries to get the resource from JNDI. If JNDI is not available or does not apply, it tries to autowire the resource from the Spring context.
   - `autowireResource` method: autowires the resource from the `LookupElement` description. It can resolve the dependency by type match, or directly by name.
