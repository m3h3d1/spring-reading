## getBean

- [getBean](#getbean)
  - [1. Overview](#1-overview)
  - [2. Method Source](#2-method-source)
  - [3. Main Features](#3-main-features)
  - [4. Best Practices](#4-best-practices)
  - [5. Sequence Diagram](#5-sequence-diagram)
  - [6. Source Code Analysis](#6-source-code-analysis)
  - [7. Notes](#7-notes)
  - [8. Summary](#8-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

In Spring, `getBean` is a core method of the `ApplicationContext` interface. It retrieves a bean from the Spring container. Spring is built on Inversion of Control (IoC) and Dependency Injection (DI), and `getBean` is one of the main ways to use them.

### 2. Method Source

This method shows how the Spring IoC container works. When you ask for a bean, Spring looks it up, resolves any alias, checks its scope (for example singleton or prototype), and returns the right bean instance to the caller.

```java
/**
 * Return an instance, which may be shared or independent, of the specified bean.
 * This method allows a Spring BeanFactory to be used as a replacement for the
 * Singleton or Prototype design pattern. For singleton beans, the caller may
 * keep a reference to the returned object.
 * Translates aliases back to the corresponding canonical bean name.
 * Will ask the parent factory if the bean cannot be found in this factory instance.
 * 
 * @param name the name of the bean to retrieve
 * @return an instance of the bean
 * @throws NoSuchBeanDefinitionException if there is no bean with the specified name
 * @throws BeansException if the bean could not be obtained
 */
Object getBean(String name) throws BeansException;
```

### 3. Main Features

1. **Retrieve a bean**
   + Returns the bean instance with the given name or type from the Spring container.
2. **Scope handling**
   + Depending on the bean configuration and scope (for example "singleton" or "prototype"), `getBean` returns the shared singleton instance or creates a new instance on every call.
3. **Alias handling**
   + If a bean has aliases, `getBean` resolves them and returns the matching bean instance.
4. **Parent container lookup**
   + If the bean is not found in the current container and the container has a parent, `getBean` looks for the bean in the parent container.
5. **Type conversion**
   + An overloaded version of `getBean` lets you specify the required type. This avoids an explicit cast when you use the bean.
6. **Exception handling**
   + If the container has no bean with the given name, it throws `NoSuchBeanDefinitionException`. If anything else goes wrong while creating or retrieving the bean, it throws `BeansException`.
7. **Manual dependency lookup**
   + Spring mainly provides dependencies through dependency injection, but `getBean` lets you look up a dependency by hand.
8. **Bean initialization**
   + If the bean is not initialized yet (for example a singleton on its first request), `getBean` triggers its initialization.

### 4. Best Practices

First, the entry class. It uses `AnnotationConfigApplicationContext`, which configures the Spring container with Java annotations. We pass the `MyConfiguration` component class to the constructor, then get two beans from the Spring context: `myServiceA` and `myServiceB`.

```java
public class GetBeanApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfiguration.class);
        System.out.println("myServiceA = " + context.getBean("myServiceA"));
        System.out.println("myServiceB = " + context.getBean("myServiceB"));
    }
}
```

In the configuration class, the `@ComponentScan` annotation tells Spring to scan the `com.xcs.spring.service` package and its sub-packages. Any class found there with a stereotype annotation (such as `@Component`, `@Service`, `@Repository`, or `@Controller`) is detected automatically and added to the container as a managed bean.

```java
@Configuration
@ComponentScan("com.xcs.spring.service")
public class MyConfiguration {
    
}
```

`@ComponentScan` is enabled in `MyConfiguration` with the correct package path, so these two classes are detected and registered in the Spring container automatically.

```java
package com.xcs.spring.service;

@Component
public class MyServiceA {

}

@Component
public class MyServiceB {

}
```

The output shows the two service classes we defined. Both are marked with `@Component`, so the Spring container creates one bean instance for each class.

```java
myServiceA = com.xcs.spring.service.MyServiceA@23c30a20
myServiceB = com.xcs.spring.service.MyServiceB@1e1a0406
```

### 5. Sequence Diagram

~~~mermaid
sequenceDiagram
DefaultListableBeanFactory->>AbstractBeanFactory:getBean(name)
note over AbstractBeanFactory: Request a bean

AbstractBeanFactory->>AbstractBeanFactory:doGetBean(name,requiredType,args,typeCheckOnly)
note over AbstractBeanFactory: Run the actual bean lookup logic

AbstractBeanFactory->>AbstractBeanFactory:transformedBeanName(name)
note over AbstractBeanFactory: Get the real bean name

AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName)
note over DefaultSingletonBeanRegistry: Check whether the bean is a singleton

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:getSingleton(beanName,allowEarlyReference)
note over DefaultSingletonBeanRegistry: If allowed, check for an early singleton reference

DefaultSingletonBeanRegistry->>AbstractBeanFactory:Return the registered singleton object
note over AbstractBeanFactory: If it is registered, return this singleton bean

AbstractBeanFactory->>DefaultListableBeanFactory:Return the bean object
note over DefaultListableBeanFactory: Return to the original caller

note over AbstractBeanFactory: The bean is not in the cache and must be created

AbstractBeanFactory->>AbstractBeanFactory:getParentBeanFactory()
note over AbstractBeanFactory: Check whether there is a parent bean factory

AbstractBeanFactory->>DefaultListableBeanFactory:parentBeanFactory.getBean(name)
note over DefaultListableBeanFactory: Request the bean from the parent factory

AbstractBeanFactory->>AbstractBeanFactory:markBeanAsCreated(beanName)
note over AbstractBeanFactory: Mark the bean as created

AbstractBeanFactory->>AbstractBeanFactory:getMergedLocalBeanDefinition(beanName)
note over AbstractBeanFactory: Get the merged bean definition

AbstractBeanFactory->>AbstractBeanFactory:checkMergedBeanDefinition(mbd, beanName, args)
note over AbstractBeanFactory: Check that the merged bean definition is valid

AbstractBeanFactory->>AbstractBeanDefinition:getDependsOn()
note over AbstractBeanFactory: Get the beans this bean depends on

AbstractBeanFactory->>DefaultSingletonBeanRegistry:isDependent(beanName, dep)
note over DefaultSingletonBeanRegistry: Check whether a dependency exists

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:isDependent(beanName, dependentBeanName, null)
note over DefaultSingletonBeanRegistry: Check the dependency

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:canonicalName(beanName)
note over DefaultSingletonBeanRegistry: Get the canonical bean name

DefaultSingletonBeanRegistry->>AbstractBeanFactory:Return whether there is a circular dependency
note over AbstractBeanFactory: Return the circular dependency check result

note over AbstractBeanFactory: If there is a circular dependency, throw an exception: throw new BeanCreationException("Circular depends-on relationship between")

AbstractBeanFactory->>DefaultSingletonBeanRegistry:registerDependentBean(dep, beanName)
note over DefaultSingletonBeanRegistry: Register the dependency

AbstractBeanFactory->>DefaultListableBeanFactory:getBean(name)
note over DefaultListableBeanFactory: Get the bean this bean depends on

AbstractBeanFactory->>DefaultSingletonBeanRegistry:getSingleton(beanName,singletonFactory)
note over DefaultSingletonBeanRegistry: Get or create the singleton bean

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:beforeSingletonCreation(beanName)
note over DefaultSingletonBeanRegistry: Prepare before the singleton is created

DefaultSingletonBeanRegistry->>AbstractBeanFactory:singletonFactory.getObject()
note over AbstractBeanFactory: Create the bean with the singleton factory

AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:createBean(beanName, mbd, args)
note over AbstractAutowireCapableBeanFactory: Create a new bean instance

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:resolveBeanClass(mbd, beanName)
note over AbstractAutowireCapableBeanFactory: Resolve the bean class

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:resolveBeforeInstantiation(beanName, mbdToUse)
note over AbstractAutowireCapableBeanFactory: Try to resolve the bean before instantiation

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:doCreateBean(beanName,mbd,args)
note over AbstractAutowireCapableBeanFactory: Run the actual bean creation

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:createBeanInstance(beanName, mbd, args)
note over AbstractAutowireCapableBeanFactory: Create the bean instance

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:determineConstructorsFromBeanPostProcessors(beanClass, beanName)
note over AbstractAutowireCapableBeanFactory: Determine constructors from SmartInstantiationAwareBeanPostProcessor

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:instantiateBean(beanName, mbd)
note over AbstractAutowireCapableBeanFactory: Instantiate the bean

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName)
note over AbstractAutowireCapableBeanFactory: Apply the merged bean definition post-processors

AbstractAutowireCapableBeanFactory->>DefaultSingletonBeanRegistry:addSingletonFactory(beanName,singletonFactory)
note over DefaultSingletonBeanRegistry: Add the singleton factory

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:populateBean(beanName, mbd, instanceWrapper)
note over AbstractAutowireCapableBeanFactory: Populate the bean properties

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:postProcessAfterInstantiation(bean,beanName)
note over AbstractAutowireCapableBeanFactory: Post-process after instantiation

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:postProcessProperties(pvs,bean,beanName)
note over AbstractAutowireCapableBeanFactory: Post-process properties

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:postProcessPropertyValues(pvs,pds,bean,beanName)
note over AbstractAutowireCapableBeanFactory: Post-process property values

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyPropertyValues(beanName,mbd,bw,pvs)
note over AbstractAutowireCapableBeanFactory: Apply the property values

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:initializeBean(beanName, exposedObject, mbd)
note over AbstractAutowireCapableBeanFactory: Initialize the bean

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeAwareMethods(beanName, bean)
note over AbstractAutowireCapableBeanFactory: Call the Aware interface methods

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName)
note over AbstractAutowireCapableBeanFactory: Apply BeanPostProcessors before initialization

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:invokeInitMethods(beanName, wrappedBean, mbd)
note over AbstractAutowireCapableBeanFactory: Call the init methods

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName)
note over AbstractAutowireCapableBeanFactory: Apply BeanPostProcessors after initialization

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:registerDisposableBeanIfNecessary(beanName, bean, mbd)
note over AbstractAutowireCapableBeanFactory: Register the bean as disposable if needed

AbstractAutowireCapableBeanFactory->>AbstractBeanFactory:Return the created singleton bean
AbstractBeanFactory->>DefaultSingletonBeanRegistry:Return the created singleton bean

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:afterSingletonCreation(beanName)
note over DefaultSingletonBeanRegistry: Follow-up work after the singleton is created

DefaultSingletonBeanRegistry->>DefaultSingletonBeanRegistry:addSingleton(beanName, singletonObject)
note over DefaultSingletonBeanRegistry: Add the new singleton bean to the registry

DefaultSingletonBeanRegistry->>AbstractBeanFactory:Return the created singleton bean

AbstractBeanFactory->>AbstractAutowireCapableBeanFactory:getObjectForBeanInstance(sharedInstance, name, beanName, mbd)
note over AbstractAutowireCapableBeanFactory: Get the object for the bean instance

AbstractAutowireCapableBeanFactory->>AbstractAutowireCapableBeanFactory:Handle FactoryBean
note over AbstractAutowireCapableBeanFactory: If it is a FactoryBean, handle it

AbstractAutowireCapableBeanFactory->>AbstractBeanFactory:Return the real bean object
note over AbstractBeanFactory: Return the real bean object, not the FactoryBean

AbstractBeanFactory->>AbstractBeanFactory:adaptBeanInstance(name, beanInstance, requiredType)
note over AbstractBeanFactory: Adapt the bean instance to the required type

AbstractBeanFactory->>DefaultListableBeanFactory:Return the real bean object
note over DefaultListableBeanFactory: Return to the original caller
~~~

### 6. Source Code Analysis

In `org.springframework.beans.factory.support.AbstractBeanFactory#getBean(name)`, Spring gives callers a simple way to get a bean from the IoC container by name only, with no extra context or arguments.

```java
@Override
public Object getBean(String name) throws BeansException {
    // Call doGetBean to actually get the bean.
    // Parameters:
    // 1. name: the name of the bean to get.
    // 2. null: the required type of the bean. null means no specific type.
    // 3. null: arguments for the constructor or factory method. null means use the default.
    // 4. false: whether to only check the type. false means the bean must be instantiated.
    return doGetBean(name, null, null, false);
}
```

`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean` is the core method of `AbstractBeanFactory` for getting a bean instance. It handles singleton, prototype, and custom-scoped beans, and it also deals with bean definitions, circular references, and dependencies.

```java
protected <T> T doGetBean(
			String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly)
			throws BeansException {

    // Step 1: transform the bean name
    String beanName = transformedBeanName(name);

    // Step 2: try to get the singleton bean from the cache
    Object sharedInstance = getSingleton(beanName);
    Object beanInstance;

    if (sharedInstance != null && args == null) {
        // Get the bean instance itself
        beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, null);
    }
    else {
        // Step 3: handle prototype beans and check whether the bean is already being created
        if (isPrototypeCurrentlyInCreation(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }

        // Step 4: try to find the bean definition in the parent bean factory
        BeanFactory parentBeanFactory = getParentBeanFactory();

        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            String nameToLookup = originalBeanName(name);
            // ... [code omitted for brevity]
            return (T) parentBeanFactory.getBean(nameToLookup);
        }

        // Step 5: mark the bean as created
        if (!typeCheckOnly) {
            markBeanAsCreated(beanName);
        }

        try {
            // Step 6: get the merged bean definition
            RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
            checkMergedBeanDefinition(mbd, beanName, args);

            // Step 7: make sure the bean's dependencies are initialized
            String[] dependsOn = mbd.getDependsOn();
            if (dependsOn != null) {
                for (String dep : dependsOn) {
                    // Step 7.1: check for a circular dependency
                    if (isDependent(beanName, dep)) {
                       // ... [code omitted for brevity]
                    }
                    // Step 7.2: register the dependency between the beans
                    registerDependentBean(dep, beanName);
                    // Step 7.3: get the bean this bean depends on
                    getBean(dep);
                }
            }

            // Step 8: create or get the bean instance, depending on its scope
            if (mbd.isSingleton()) {
                // Step 8.1: handle the singleton scope
                sharedInstance = getSingleton(beanName, () -> {
                    try {
                        // Step 8.2: create the bean
                        return createBean(beanName, mbd, args);
                    }
                    catch (BeansException ex) {
                        // ... [code omitted for brevity]
                    }
                });
                // Step 8.3: get the bean instance itself
                beanInstance = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
            }
            else if (mbd.isPrototype()) {
                // Handle the prototype scope
                // ... [code omitted for brevity]
            }
            else {
                // Handle other scopes
                // ... [code omitted for brevity]
            }
        }
        catch (BeansException ex) {
            // Handle bean creation failure
            // ... [code omitted for brevity]
        }
        finally {
            // ... [code omitted for brevity]
        }
    }

    // Step 9: adapt the bean instance
    return adaptBeanInstance(name, beanInstance, requiredType);
}
```

> Step 1 of `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`.

`org.springframework.beans.factory.support.AbstractBeanFactory#transformedBeanName` converts the given bean name into its canonical form. It strips any prefix and resolves aliases.

```java
protected String transformedBeanName(String name) {
    // First, BeanFactoryUtils.transformedBeanName handles the incoming name.
    // It usually removes the bean name prefix, such as "&", which is used for factory beans.
    // Then canonicalName maps any alias to the real bean name.
    return canonicalName(BeanFactoryUtils.transformedBeanName(name));
}
```

`org.springframework.core.SimpleAliasRegistry#canonicalName` keeps looking up the alias map until no more aliases are found. This guarantees that the result is the real bean name, not an alias.

```java
public String canonicalName(String name) {
    // Start with the incoming name
    String canonicalName = name; 

    // Loop through the alias map
    String resolvedName;
    do {
        // Look up the real bean name in the alias map
        resolvedName = this.aliasMap.get(canonicalName);
        
        // If a name was found (resolvedName is not null), use it as the new canonicalName
        if (resolvedName != null) {
            canonicalName = resolvedName;
        }
    } 
    // If resolvedName itself is an alias, keep looping
    while (resolvedName != null);

    // Return the final bean name
    return canonicalName;
}
```

> Step 2 of `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`.

`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName)` simplifies getting a singleton bean. By default it allows an early bean reference to be returned while the bean is still being created. This helps resolve circular dependencies.

```java
@Override
@Nullable
public Object getSingleton(String beanName) {
    // Call the overloaded getSingleton method to get the singleton bean.
    // Parameters:
    // 1. beanName: the name of the singleton bean to get.
    // 2. true: allow an early singleton reference if the bean is currently being created
    //    (for example, when handling circular references).
    return getSingleton(beanName, true);
}
```

`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,allowEarlyReference)` returns the singleton object for the given bean name. It checks several caches: the fully initialized singleton cache, the early singleton cache, and the singleton factory cache. It also handles the case where the bean is still being created, which can happen with circular references.

```java
@Nullable
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    // Quick check for an existing instance in the cache, without full singleton locking
    Object singletonObject = this.singletonObjects.get(beanName);
    
    // If no instance is found and the bean is currently being created (for example, a circular reference)
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        singletonObject = this.earlySingletonObjects.get(beanName);
        
        // If early references are allowed and the early singleton cache has no entry either
        if (singletonObject == null && allowEarlyReference) {
            synchronized (this.singletonObjects) {
                // Inside the full singleton lock, make sure the early reference is created consistently
                singletonObject = this.singletonObjects.get(beanName);
                if (singletonObject == null) {
                    singletonObject = this.earlySingletonObjects.get(beanName);
                    
                    // If it is still not in the early cache and a singleton factory exists for this bean
                    if (singletonObject == null) {
                        ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                        if (singletonFactory != null) {
                            // Create the bean with the singleton factory
                            singletonObject = singletonFactory.getObject();
                            // Put the new bean into the early singleton cache
                            this.earlySingletonObjects.put(beanName, singletonObject);
                            // Remove the factory from the singleton factory cache
                            this.singletonFactories.remove(beanName);
                        }
                    }
                }
            }
        }
    }
    // Return the singleton bean, or null if none was found
    return singletonObject;
}
```

> Step 3 of `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`.

`org.springframework.beans.factory.support.AbstractBeanFactory#isPrototypeCurrentlyInCreation` checks whether a bean name is in the list of prototype beans currently being created. This handles possible circular references between prototype beans.

```java
protected boolean isPrototypeCurrentlyInCreation(String beanName) {
    // Get the prototype bean(s) currently being created
    Object curVal = this.prototypesCurrentlyInCreation.get();

    // The value must not be null, and either
    // 1) it equals the given bean name, or
    // 2) it is a Set that contains the given bean name
    return (curVal != null &&
            (curVal.equals(beanName) || (curVal instanceof Set && ((Set<?>) curVal).contains(beanName))));
}
```

> Step 5 of `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`.

`org.springframework.beans.factory.support.AbstractBeanFactory#markBeanAsCreated` marks the given bean as created or being created. It matters in the bean lifecycle when a bean must be created only once or when its state needs to be checked.

```java
protected void markBeanAsCreated(String beanName) {
    // 1. First check whether the bean is already marked as created
    if (!this.alreadyCreated.contains(beanName)) {
        
        // 2. Synchronize to stay thread-safe in a multi-threaded environment
        synchronized (this.mergedBeanDefinitions) {
            
            // 3. Double-checked locking: check again whether the bean is marked as created
            if (!this.alreadyCreated.contains(beanName)) {
                
                // 4. Clear the merged definition so it is merged again on the next access
                clearMergedBeanDefinition(beanName);
                
                // 5. Mark the bean as created in the set
                this.alreadyCreated.add(beanName);
            }
        }
    }
}
```

> Step 6 of `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`.

`org.springframework.beans.factory.support.AbstractBeanFactory#getMergedLocalBeanDefinition` returns the merged bean definition for the given bean name. A merged bean definition combines the parent and child definitions, if there are any.

```java
protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
    // 1. Quick check in the concurrent map to keep locking to a minimum.
    RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
    
    // 2. If the merged bean definition exists and is not stale, return it.
    if (mbd != null && !mbd.stale) {
        return mbd;
    }
    
    // 3. Otherwise, get and return the merged bean definition.
    return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
}
```

`org.springframework.beans.factory.support.DefaultListableBeanFactory#getBeanDefinition` gets the bean definition with the given name from this bean factory's bean definition map. If no definition is found, it throws `NoSuchBeanDefinitionException`.

```java
@Override
public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
    // 1. Get the bean definition from beanDefinitionMap
    BeanDefinition bd = this.beanDefinitionMap.get(beanName);
    
    // 2. If there is no BeanDefinition, log a trace message and throw an exception
    if (bd == null) {
        // Log a message if trace logging is enabled
        if (logger.isTraceEnabled()) {
            logger.trace("No bean named '" + beanName + "' found in " + this);
        }
        // Throw the exception for a missing BeanDefinition
        throw new NoSuchBeanDefinitionException(beanName);
    }
    
    // 3. Return the BeanDefinition that was found
    return bd;
}
```

`org.springframework.beans.factory.support.AbstractBeanFactory#getMergedBeanDefinition(beanName, bd)` calls another version of `getMergedBeanDefinition` to get a merged `RootBeanDefinition` for the given bean name and bean definition.

```java
protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd)
			throws BeanDefinitionStoreException {

    // Get a merged RootBeanDefinition for the given bean name and bean definition.
    // This version has no containing bean definition, so we pass null as the third argument.
    return getMergedBeanDefinition(beanName, bd, null);
}
```

`org.springframework.beans.factory.support.AbstractBeanFactory#getMergedBeanDefinition(beanName,bd,containingBd)` returns the merged bean definition for the given bean name. It handles the parent and child relationship between bean definitions and related settings, then returns the merged definition.

```java
protected RootBeanDefinition getMergedBeanDefinition(
        String beanName, BeanDefinition bd, @Nullable BeanDefinition containingBd)
        throws BeanDefinitionStoreException {

    // 1. Synchronize on mergedBeanDefinitions to stay thread-safe.
    synchronized (this.mergedBeanDefinitions) {
        RootBeanDefinition mbd = null;
        RootBeanDefinition previous = null;

        // 2. Check again inside the full lock so the same merged instance is used.
        if (containingBd == null) {
            mbd = this.mergedBeanDefinitions.get(beanName);
        }

        // 3. If the bean definition is not merged yet or is stale, merge it.
        if (mbd == null || mbd.stale) {
            previous = mbd;

            // 4. Handle the case where there is no parent definition.
            if (bd.getParentName() == null) {
                if (bd instanceof RootBeanDefinition) {
                    mbd = ((RootBeanDefinition) bd).cloneBeanDefinition();
                }
                else {
                    mbd = new RootBeanDefinition(bd);
                }
            }
            // 5. Handle the case where there is a parent definition: merge with it.
            else {
                BeanDefinition pbd;
                try {
                    String parentBeanName = transformedBeanName(bd.getParentName());
                    if (!beanName.equals(parentBeanName)) {
                        pbd = getMergedBeanDefinition(parentBeanName);
                    }
                    else {
                        BeanFactory parent = getParentBeanFactory();
                        if (parent instanceof ConfigurableBeanFactory) {
                            pbd = ((ConfigurableBeanFactory) parent).getMergedBeanDefinition(parentBeanName);
                        }
                        else {
                            // ... [code omitted for brevity]
                        }
                    }
                }
                catch (NoSuchBeanDefinitionException ex) {
                    // ... [code omitted for brevity]
                }
                mbd = new RootBeanDefinition(pbd);
                mbd.overrideFrom(bd);
            }

            // 6. If the scope of the bean definition is not set explicitly, default to singleton.
            if (!StringUtils.hasLength(mbd.getScope())) {
                mbd.setScope(SCOPE_SINGLETON);
            }

            // 7. A bean contained in a non-singleton bean cannot be a singleton itself. Fix that here.
            if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
                mbd.setScope(containingBd.getScope());
            }

            // 8. Cache the merged bean definition if needed.
            if (containingBd == null && isCacheBeanMetadata()) {
                this.mergedBeanDefinitions.put(beanName, mbd);
            }
        }
        // 9. If there was a previous bean definition, copy the relevant caches.
        if (previous != null) {
            copyRelevantMergedBeanDefinitionCaches(previous, mbd);
        }

        // 10. Return the merged bean definition.
        return mbd;
    }
}
```

> Step 7.1 of `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`.

In Spring's bean initialization, the `@DependsOn` annotation makes sure a bean is initialized after the other beans it names. The code below shows how this annotation is handled. To learn more about how `@DependsOn` works, see this article: [**Initialization order with @DependsOn**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-annotation/spring-annotation-dependsOn) - control the loading order of Spring beans precisely. It analyzes the annotation source code and explains its role in Spring.

```java
String[] dependsOn = mbd.getDependsOn();
if (dependsOn != null) {
    for (String dep : dependsOn) {
        // Step 7.1: check for a circular dependency
        // This means bean A depends on bean B, and bean B depends on bean A.
        // If that happens, a BeanCreationException is thrown.
        if (isDependent(beanName, dep)) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                            "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
        }
        // Step 7.2: register the dependency between the beans
        // The current bean factory records the dependency, so Spring can keep the right order
        // when it gets or destroys beans.
        registerDependentBean(dep, beanName);
        // Step 7.3: get the bean this bean depends on
        // This makes sure every dependency has been created. It calls getBean directly,
        // which initializes and returns the given bean.
        getBean(dep);
    }
}
```

> Step 8.1 of `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`.

`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,singletonFactory)` first tries to get the bean from the cache. If it is not there, the method uses the given `singletonFactory` to create it, with pre- and post-processing during creation to handle problems such as circular references. The new bean is added to the cache. The method also handles the exceptions that can occur during creation and stays thread-safe. Finally, it returns the singleton bean.

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    synchronized (this.singletonObjects) {
        // First try to get the singleton from the cache
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            // ... [code omitted for brevity]
            
            // Step 1: pre-processing, for example marking this bean as in creation to handle circular references.
            beforeSingletonCreation(beanName);
            
            // ... [code omitted for brevity]
            try {
                // Step 2: create the singleton object with the singletonFactory
                singletonObject = singletonFactory.getObject();
                newSingleton = true;
            }
            catch (IllegalStateException ex) {
                // ... [code omitted for brevity]
            }
            catch (BeanCreationException ex) {
                // ... [code omitted for brevity]
            }
            finally {
                // ... [code omitted for brevity]
                // Step 3: callback after the singleton is created
                afterSingletonCreation(beanName);
            }
            // Step 4: if a new singleton bean was created, add it to the cache
            if (newSingleton) {
                addSingleton(beanName, singletonObject);
            }
        }
        // Return the existing or newly created singleton bean
        return singletonObject;
    }
}
```

> Step 1 of `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,singletonFactory)`.

`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#beforeSingletonCreation` is called before Spring tries to create a singleton bean. It makes sure the same bean is not created more than once at the same time, which avoids problems caused by circular references. If the bean is already being created, this method throws an exception.

```java
protected void beforeSingletonCreation(String beanName) {
    // Check whether beanName is in the exclusion list or already in the set of beans in creation.
    // If the bean is not excluded and cannot be added to the set, it is already being created.
    if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
        throw new BeanCurrentlyInCreationException(beanName);
    }
}
```

> Step 8.2 of `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`.

`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)` creates a bean instance from the given bean definition. It takes care of details such as factory methods, constructor injection, and the pre- and post-processors.

```java
@Override
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
    throws BeanCreationException {

    // Do some preparation on mbd. This may include cloning the bean definition if mbd is a non-shared prototype.
    RootBeanDefinition mbdToUse = mbd;

    // Step 1: try to instantiate the bean with InstantiationAwareBeanPostProcessors.
    // If a post-processor returns a bean instance (for example an AOP proxy), return it directly.
    try {
        Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
        if (bean != null) {
            return bean;
        }
    }
    catch (Throwable ex) {
        // If resolution fails, record the exception and continue with normal bean creation.
        // ... [code omitted for brevity]
    }

    // If the pre-processing did not return a bean instance, go on with normal bean creation.
    try {
        // Step 2: create the bean instance. This may use a factory method, constructor injection, and so on.
        Object beanInstance = doCreateBean(beanName, mbdToUse, args);
        return beanInstance;
    }
    // Catch and handle exceptions that can occur during creation.
    catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
        // ... [code omitted for brevity]
    }
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }
}
```

> Step 1 of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)`.

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation`, an `InstantiationAwareBeanPostProcessor` can intercept bean creation before the standard instantiation. To understand how it works, read: [**Intercepting bean instantiation with InstantiationAwareBeanPostProcessor**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-instantiationAwareBeanPostProcessor). That article explains the core role of this interface in Spring.

```java
@Nullable
protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
    // A bean variable that later steps may fill in
    Object bean = null;
    
    // If 'beforeInstantiationResolved' is FALSE, skip the processing below
    if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
        
        // First make sure the bean class has been resolved.
        // Then, for non-synthetic beans, if there are any InstantiationAwareBeanPostProcessors, try the pre-processing.
        if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            
            // Determine the target type. This may involve class resolution and other initialization.
            Class<?> targetType = determineTargetType(beanName, mbd);
            
            if (targetType != null) {
                // With a target type, first apply the BeanPostProcessors' pre-instantiation callbacks.
                // They may return a bean instance, which lets us skip the standard instantiation.
                bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
                
                // If a bean instance was created above, also apply the BeanPostProcessors after initialization.
                if (bean != null) {
                    bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                }
            }
        }
        
        // Update 'mbd.beforeInstantiationResolved': true if a bean was created above, otherwise false.
        mbd.beforeInstantiationResolved = (bean != null);
    }
    
    // Return the bean instance created above, or null if no bean was created.
    return bean;
}
```

> Step 2 of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)`.

`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean` handles the main stages of the bean lifecycle: instantiation, property injection, initialization, and registration for cleanup.

```java
protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {

    BeanWrapper instanceWrapper = null;
    
    // ... [code omitted for brevity]
    
    // Step 1: try to instantiate the bean
    if (instanceWrapper == null) {
        instanceWrapper = createBeanInstance(beanName, mbd, args);
    }
    
    // ... [code omitted for brevity]

    // Step 2: post-process the merged bean definition
    synchronized (mbd.postProcessingLock) {
        if (!mbd.postProcessed) {
            try {
                applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
            } catch (Throwable ex) {
                // ... [code omitted for brevity]
            }
            mbd.postProcessed = true;
        }
    }

    // Step 3: handle possible circular references by exposing the bean reference early
    boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
            isSingletonCurrentlyInCreation(beanName));
    if (earlySingletonExposure) {
        // ... [code omitted for brevity]
        // Step 3.1: register an `ObjectFactory`
        addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
    }

    // Step 4: initialize the bean instance, populate its properties, and apply post-processors
    Object exposedObject = bean;
    try {
        // Step 4.1: populate the properties
        populateBean(beanName, mbd, instanceWrapper);
        // Step 4.2: initialize the bean
        exposedObject = initializeBean(beanName, exposedObject, mbd);  
    } catch (Throwable ex) {
        // ... [code omitted for brevity]
    }

    // Step 5: if needed, register the bean for cleanup when the container shuts down
    try {
        registerDisposableBeanIfNecessary(beanName, bean, mbd);
    } catch (BeanDefinitionValidationException ex) {
        // ... [code omitted for brevity]
    }

    return exposedObject;
}
```

> Step 1 of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`.

`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance` first tries to get constructors from the post-processors, then checks for preferred constructors, and finally falls back to the no-argument constructor.

```java
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
    // ... [code omitted for brevity]

    // Step 1: first try to determine constructors from the BeanPostProcessors, mainly to support cases such as @Autowired
    Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
    // If constructors were found, or the bean definition has a constructor autowire mode or constructor arguments,
    // create the bean instance by autowiring the constructor
    if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
        mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
        return autowireConstructor(beanName, mbd, ctors, args);
    }

    // Step 2: if the BeanDefinition has preferred constructors, use them
    ctors = mbd.getPreferredConstructors();
    if (ctors != null) {
        return autowireConstructor(beanName, mbd, ctors, null);
    }

    // Step 3: if none of the steps above returned a bean instance, use the no-argument constructor
    return instantiateBean(beanName, mbd);
}
```

> Step 1 of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance`.

`SmartInstantiationAwareBeanPostProcessor` offers a smarter instantiation strategy. It can change the constructor choice through `determineConstructorsFromBeanPostProcessors`. To understand its role, read: [**Adjusting the bean instantiation strategy with SmartInstantiationAwareBeanPostProcessor**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-smartInstantiationAwareBeanPostProcessor). That article analyzes its core role in Spring.

```java
@Nullable
protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName)
    throws BeansException {

    // Check that beanClass is not null and that there are InstantiationAwareBeanPostProcessors
    if (beanClass != null && hasInstantiationAwareBeanPostProcessors()) {

        // Loop over all SmartInstantiationAwareBeanPostProcessors
        for (SmartInstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().smartInstantiationAware) {
            
            // Ask the current BeanPostProcessor for candidate constructors
            Constructor<?>[] ctors = bp.determineCandidateConstructors(beanClass, beanName);
            
            // If suitable constructors were found, return them
            if (ctors != null) {
                return ctors;
            }
        }
    }
    
    // Return null if no suitable constructor was found, beanClass is null, or there is no matching BeanPostProcessor
    return null;
}
```

> Step 3 of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance`.

`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#instantiateBean` creates a new bean from the given bean definition and returns a `BeanWrapper` around the bean instance. This allows further work on the instance, such as property injection.

```java
protected BeanWrapper instantiateBean(String beanName, RootBeanDefinition mbd) {
    try {
        Object beanInstance;

        if (System.getSecurityManager() != null) {
            // ... [code omitted for brevity]
        }
        else {
            // Otherwise, use the instantiation strategy to create the bean instance
            beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, this);
        }

        // Create a BeanWrapper around the new bean instance
        BeanWrapper bw = new BeanWrapperImpl(beanInstance);
        // Initialize the BeanWrapper, for example with custom property editors
        initBeanWrapper(bw);
        // Return the BeanWrapper around the bean instance
        return bw;
    }
    catch (Throwable ex) {
        // Handle exceptions that can occur while creating the bean instance
        // ... [code omitted for brevity]
    }
}
```

> Step 2 of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`.

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors`, `MergedBeanDefinitionPostProcessor` is a key interface. It adjusts and merges bean definitions before the bean is instantiated. To learn more about this mechanism, read: [**Processing bean definitions with MergedBeanDefinitionPostProcessor**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-mergedBeanDefinitionPostProcessor). That article covers the source code and the core features of this interface.

```java
protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
    // Loop over all cached post-processors of type MergedBeanDefinitionPostProcessor
    for (MergedBeanDefinitionPostProcessor processor : getBeanPostProcessorCache().mergedDefinition) {
        // Call postProcessMergedBeanDefinition on each post-processor to process the merged bean definition
        processor.postProcessMergedBeanDefinition(mbd, beanType, beanName);
    }
}
```

> Step 3.1 of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`.

`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#addSingletonFactory` registers an `ObjectFactory` for a bean name. This resolves circular references before the bean is fully created. When another bean asks for an early reference to this bean, it can use the `ObjectFactory` to get one.

```java
protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
    // Make sure the singletonFactory is not null
    Assert.notNull(singletonFactory, "Singleton factory must not be null");
    synchronized (this.singletonObjects) {
        // If the bean is not yet in the singletonObjects cache
        if (!this.singletonObjects.containsKey(beanName)) {
            // Add the singletonFactory to the singletonFactories cache
            this.singletonFactories.put(beanName, singletonFactory);
            // Remove the bean name from the earlySingletonObjects cache, because it now has a full ObjectFactory
            this.earlySingletonObjects.remove(beanName);
            // Add the bean name to the registeredSingletons set to mark it as registered
            this.registeredSingletons.add(beanName);
        }
    }
}
```

> Step 4.1 of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`.

`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean` fills in the bean properties. It loops over all `InstantiationAwareBeanPostProcessors` and calls their `postProcessAfterInstantiation` and `postProcessProperties` methods. If an `InstantiationAwareBeanPostProcessor` returns `false` or `null` property values, setting the bean properties stops early.

```java
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
    // ... [code omitted for brevity]

    // If the bean is not synthetic and there are InstantiationAwareBeanPostProcessors
    if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
        // Loop over all InstantiationAwareBeanPostProcessors
        for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
            // Call postProcessAfterInstantiation
            if (!bp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                // If it returns false, stop setting the bean properties early
                return;
            }
        }
    }

    // Get the property values from the bean definition
    PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);

    // ... [code omitted for brevity]

    // Check whether there are InstantiationAwareBeanPostProcessors
    boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
    // Decide whether a dependency check is needed
    boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);

    PropertyDescriptor[] filteredPds = null;
    if (hasInstAwareBpps) {
        // If there are no property values, get them from the bean definition
        if (pvs == null) {
            pvs = mbd.getPropertyValues();
        }
        // Loop over all InstantiationAwareBeanPostProcessors
        for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
            // Call postProcessProperties to process the property values
            PropertyValues pvsToUse = bp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
            if (pvsToUse == null) {
                if (filteredPds == null) {
                    filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
                }
                pvsToUse = bp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
                if (pvsToUse == null) {
                    return;
                }
            }
            pvs = pvsToUse;
        }
    }
    // If a dependency check is needed
    if (needsDepCheck) {
        if (filteredPds == null) {
            filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
        }
        checkDependencies(beanName, mbd, filteredPds, pvs);
    }

    // Finally, apply the processed property values to the bean instance
    if (pvs != null) {
        applyPropertyValues(beanName, mbd, bw, pvs);
    }
}
```

> Step 4.2 of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`.

`org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean(beanName, bean,mbd)` runs the bean initialization. It calls the Aware interface methods, the `BeanPostProcessors` before and after initialization, and the bean's custom init methods.

```java
protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
    // If there is a SecurityManager, run the matching security code (omitted)
    if (System.getSecurityManager() != null) {
        // ... [code omitted for brevity]
    }
    else {
        // If the bean implements certain Aware interfaces (such as BeanNameAware or BeanFactoryAware), call the matching methods
        invokeAwareMethods(beanName, bean);
    }

    // Pre-processing before initialization
    // If the bean is not synthetic, call postProcessBeforeInitialization on all BeanPostProcessors
    Object wrappedBean = bean;
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    }

    try {
        // Call the bean's init methods, such as afterPropertiesSet and a custom init-method
        invokeInitMethods(beanName, wrappedBean, mbd);
    }
    catch (Throwable ex) {
        // ... [code omitted for brevity]
    }

    // Post-processing after initialization
    // If the bean is not synthetic, call postProcessAfterInitialization on all BeanPostProcessors
    if (mbd == null || !mbd.isSynthetic()) {
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }

    // Return the final bean instance, which may be wrapped by an AOP proxy or similar
    return wrappedBean;
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#invokeAwareMethods`, the `Aware` interfaces let beans interact with the container, for example to get their own name, the class loader, or the bean factory. These articles analyze how Spring implements each interface:

- [**Getting the bean name with BeanNameAware**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-aware/spring-aware-beanNameAware) - lets a bean get its name in the Spring container.
- [**Getting the class loader with BeanClassLoaderAware**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-aware/spring-aware-beanClassLoaderAware) - gives a bean a reference to the class loader that loaded it.
- [**Interacting with the bean factory with BeanFactoryAware**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-aware/spring-aware-beanFactoryAware) - lets a bean interact with the bean factory or application context it lives in.

```java
private void invokeAwareMethods(String beanName, Object bean) {
    // Check whether the bean implements the Aware interface
    if (bean instanceof Aware) {
        // If the bean implements BeanNameAware, set the bean name
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) bean).setBeanName(beanName);
        }
        // If the bean implements BeanClassLoaderAware, set the bean class loader
        if (bean instanceof BeanClassLoaderAware) {
            ClassLoader bcl = getBeanClassLoader();
            if (bcl != null) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
            }
        }
        // If the bean implements BeanFactoryAware, set the bean factory
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
        }
    }
}
```

In `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization`, the `BeanPostProcessor` interface lets you intercept bean initialization. To understand how it works, read: [**Adjusting beans with BeanPostProcessor**](https://github.com/xuchengsheng/spring-reading/blob/master/spring-interface/spring-interface-beanPostProcessor). That article explains its key role in Spring.

```java
@Override
public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
    throws BeansException {
	// Start with the bean that was passed in
    Object result = existingBean;  

    // Loop over all BeanPostProcessors in the container
    for (BeanPostProcessor processor : getBeanPostProcessors()) {
        // Call postProcessBeforeInitialization on each BeanPostProcessor
        Object current = processor.postProcessBeforeInitialization(result, beanName);

        // If postProcessBeforeInitialization returns null, return the original bean
        if (current == null) {
            return result;
        }
        
        result = current;  // Update result with the bean returned by postProcessBeforeInitialization
    }

    // Return the bean after all BeanPostProcessors have run
    return result;
}
```

> Step 5 of `org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean`.

`org.springframework.beans.factory.support.AbstractBeanFactory#registerDisposableBeanIfNecessary` registers a destruction callback for a bean managed by the Spring container. The callback runs when the container shuts down and the bean needs to release resources or run other destruction logic.

```java
protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
    // ... [code omitted for brevity]
    registerDisposableBean(beanName, new DisposableBeanAdapter(
                bean, beanName, mbd, getBeanPostProcessorCache().destructionAware, acc));
    // ... [code omitted for brevity]
}
```

`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#registerDisposableBean` puts the bean name and its `DisposableBean` instance into the `disposableBeans` map. When the container shuts down, it loops over this map and calls `destroy` on every `DisposableBean`. This makes sure resources are released and beans are destroyed properly.

```java
public void registerDisposableBean(String beanName, DisposableBean bean) {
    synchronized (this.disposableBeans) {
        this.disposableBeans.put(beanName, bean);
    }
}
```

> Step 3 of `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,singletonFactory)`.

`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#afterSingletonCreation` keeps bean creation thread-safe. It protects the system from wrong concurrent access, especially when several threads try to read or change the state of the same bean at once.

```java
protected void afterSingletonCreation(String beanName) {
    // Check whether the bean name is in the exclusion list. If not, go on to check whether the bean is in creation
    if (!this.inCreationCheckExclusions.contains(beanName) 
        // Try to remove the bean name from the set of "singletons currently in creation"
        && !this.singletonsCurrentlyInCreation.remove(beanName)) {
        // If the name cannot be removed, the bean should not have been in creation at this point.
        // This may mean the creation went wrong or the bean was wrongly marked as "currently in creation", so throw an exception.
        throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
    }
}
```

> Step 4 of `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(beanName,singletonFactory)`.

`org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#addSingleton` updates the caches and collections that track the singleton bean lifecycle.

```java
protected void addSingleton(String beanName, Object singletonObject) {
    // Use a `synchronized` block to keep operations on singleton objects thread-safe
    synchronized (this.singletonObjects) {
        // Add the new singleton object to the `singletonObjects` cache
        this.singletonObjects.put(beanName, singletonObject);
        
        // Remove the bean name from `singletonFactories`, because the bean is now fully instantiated
        this.singletonFactories.remove(beanName);
        
        // Remove the bean name from `earlySingletonObjects`, because the bean is now fully initialized and stored in `singletonObjects`
        this.earlySingletonObjects.remove(beanName);
        
        // Add the bean name to the `registeredSingletons` set to show the bean is registered as a singleton
        this.registeredSingletons.add(beanName);
    }
}
```

> Step 8.3 of `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`.

`org.springframework.beans.factory.support.AbstractBeanFactory#getObjectForBeanInstance` takes a bean instance and a name. It returns either the bean instance itself or the object produced by a `FactoryBean`. It also handles the details of the factory bean cache.

```java
protected Object getObjectForBeanInstance(
        Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

    // Check whether the name has the factory dereference prefix (such as '&') and the bean instance is not a factory
    if (BeanFactoryUtils.isFactoryDereference(name)) {
        // If the bean instance is a NullBean, return it directly
        if (beanInstance instanceof NullBean) {
            return beanInstance;
        }
        // If the bean instance is not a FactoryBean, throw an exception
        if (!(beanInstance instanceof FactoryBean)) {
            throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
        }
        // If the bean definition is not null, mark it as a FactoryBean
        if (mbd != null) {
            mbd.isFactoryBean = true;
        }
        return beanInstance;
    }

    // If the bean instance is not a FactoryBean, return the bean instance directly
    if (!(beanInstance instanceof FactoryBean)) {
        return beanInstance;
    }

    Object object = null;
    // If the bean definition is not null, mark it as a FactoryBean
    if (mbd != null) {
        mbd.isFactoryBean = true;
    }
    // If no bean definition was given, try to get the object produced by the factory bean from the cache
    else {
        object = getCachedObjectForFactoryBean(beanName);
    }
    // If the cache has no object, get it from the FactoryBean
    if (object == null) {
        FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
        // If there is a bean definition but no cached object for the beanName, get the merged bean definition
        if (mbd == null && containsBeanDefinition(beanName)) {
            mbd = getMergedLocalBeanDefinition(beanName);
        }
        // Check whether the bean definition is synthetic (for example, created by infrastructure code)
        boolean synthetic = (mbd != null && mbd.isSynthetic());
        // Get the object from the FactoryBean
        object = getObjectFromFactoryBean(factory, beanName, !synthetic);
    }
    return object;
}
```

> Step 9 of `org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`.

`org.springframework.beans.factory.support.AbstractBeanFactory#adaptBeanInstance` makes sure the bean instance matches the required type. If it does not, the method tries to convert the instance with a type converter. If the conversion fails, it throws an exception.

```java
<T> T adaptBeanInstance(String name, Object bean, @Nullable Class<?> requiredType) {
    // Check whether the required type matches the type of the actual bean instance
    if (requiredType != null && !requiredType.isInstance(bean)) {
        try {
            // If it does not match, try to convert the bean instance to the required type
            Object convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
            // If the converted bean is null, throw an exception
            if (convertedBean == null) {
                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
            return (T) convertedBean;
        }
        catch (TypeMismatchException ex) {
            // If the type conversion fails, log a trace message and throw an exception
            if (logger.isTraceEnabled()) {
                logger.trace("Failed to convert bean '" + name + "' to required type '" +
                             ClassUtils.getQualifiedName(requiredType) + "'", ex);
            }
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
        }
    }
    // If the bean instance matches the required type, return it directly
    return (T) bean;
}
```

### 7. Notes

1. **Bean existence**
   + Make sure the bean is defined in the Spring context. If not, `NoSuchBeanDefinitionException` is thrown.
2. **Correct bean name**
   + Use the correct bean ID or name. By default, a Spring bean name is the unqualified class name with a lowercase first letter. If the bean definition gives a different name, use that one.
3. **Bean lifecycle**
   + `getBean()` may return a different instance or the same instance on each call, depending on the bean scope (singleton, prototype, and so on).
4. **Type safety**
   + `getBean(name, class)` makes sure the returned bean has the expected type. This avoids class cast exceptions at runtime.
5. **Circular dependencies**
   + If the bean dependencies contain a circular dependency, `getBean` can fail with `BeanCurrentlyInCreationException`.
6. **Lazy initialization**
   + For a lazily initialized bean, the first call to `getBean()` triggers its creation and initialization.
7. **Possible side effects**
   + `getBean()` can trigger bean creation and initialization, so it may have side effects such as opening database connections, file IO, or initializing other resources.
8. **Do not overuse it**
   + Calling `getBean()` often inside a Spring-managed bean is poor practice. It goes against inversion of control and makes code hard to test and maintain. Prefer dependency injection over looking up beans from the container.
9. **Thread safety**
   + `getBean()` is thread-safe, but the bean it returns may not be, unless you make sure it is.
10. **Lifecycle callbacks**
    + When you create a new bean instance with `getBean` (for example a prototype-scoped bean), Spring does not manage the complete lifecycle of that bean. In particular, Spring does not call the destroy methods of a prototype bean.

### 8. Summary

#### Best Practices Summary

1. **Use the right context**
   + Choose `AnnotationConfigApplicationContext`, the Spring container made for Java annotation configuration.
2. **Define a configuration class**
   + Create a configuration class such as `MyConfiguration` and mark it with `@Configuration`. This tells Spring the class holds bean configuration.
3. **Enable component scanning**
   + Put `@ComponentScan` on the configuration class and give it the packages to scan. Spring then detects classes with certain annotations and registers them as beans.
4. **Define beans**
   + Put `@Component` or a related annotation (such as `@Service`, `@Repository`, or `@Controller`) on the target classes, such as service classes. Spring then recognizes them and adds them to the container.
5. **Get and use beans**
   + In the application entry point, create the context and call `context.getBean()` to get beans from the Spring container.
6. **Check the result**
   + Run the application and check the output to confirm that Spring found and initialized the expected beans.

#### Source Code Analysis Summary

1. **Getting a bean**
   + With `getBean`, Spring lets the caller get a bean from the IoC container by its name.
2. **Resolving the bean name**
   + `transformedBeanName` converts the bean name into its canonical form and resolves aliases. Alias resolution is done by `canonicalName`, which keeps looking up the alias map until it finds the real bean name.
3. **Getting a singleton bean**
   + `getSingleton` tries to get the singleton bean from the caches. It considers fully initialized beans, early references, and beans cached as singleton factories. This is especially useful for resolving circular dependencies.
4. **Handling prototype beans**
   + `isPrototypeCurrentlyInCreation` checks whether a bean is in the list of prototype beans being created. This handles circular references between prototype beans.
5. **Marking a bean as created**
   + `markBeanAsCreated` marks the bean as created or being created. It plays a key role in the Spring bean lifecycle.
6. **Getting the merged bean definition**
   + `getMergedLocalBeanDefinition` returns the merged bean definition for the given bean name. A merged definition combines the parent and child bean definitions. `getMergedBeanDefinition` handles the parent and child relationship and returns the merged definition.
7. **Handling @DependsOn**
   + If a bean definition has `@DependsOn`, Spring makes sure the beans it depends on are created before the current bean. This includes checking for circular dependencies, registering the dependencies in the bean factory, and making sure every dependency has been created.
8. **Getting a singleton bean through the cache**
   - In `DefaultSingletonBeanRegistry#getSingleton(beanName,singletonFactory)`, Spring first tries to get the bean from the cache. If it is not there, Spring creates it with the given `singletonFactory`. The new bean is added to the cache, which keeps it a singleton.
9. **Handling circular references**
   - In `beforeSingletonCreation`, Spring makes sure the same bean is not created more than once at the same time. This avoids problems caused by circular references.
10. **Creating the bean instance**
    - `AbstractAutowireCapableBeanFactory#createBean(beanName, mbd, args)` is the core method of bean creation. Spring considers factory methods, constructor injection, and other ways to instantiate the bean. It also applies the pre- and post-processors here.
11. **Logic before bean instantiation**
    - In `resolveBeforeInstantiation`, an `InstantiationAwareBeanPostProcessor` may intercept the standard instantiation. This is mainly used for cases such as AOP.
12. **Instantiation, property injection, and initialization**
    - `doCreateBean` handles the main stages of the bean lifecycle: instantiation, property injection, initialization, and registration for cleanup.
13. **Choosing a constructor and instantiating the bean**
    - In `createBeanInstance`, Spring first tries to get constructors from the post-processors. If none fits, it may use the no-argument constructor or other logic such as preferred constructors.
14. **Smart instantiation strategy**
    - In `determineConstructorsFromBeanPostProcessors`, a `SmartInstantiationAwareBeanPostProcessor` lets Spring adjust the constructor choice and gives a smarter instantiation strategy.
15. **Instantiating a bean directly**
    - `instantiateBean` is the simple instantiation path, usually used for beans without a specific constructor or factory method.

16. **`MergedBeanDefinitionPostProcessor` handling**:
    - In `applyMergedBeanDefinitionPostProcessors`, the `MergedBeanDefinitionPostProcessor` interface processes and adjusts the bean definition before the bean is instantiated.

17. **Handling circular references**:
    - `addSingletonFactory` registers an `ObjectFactory` to resolve circular references before the bean is created.

18. **Populating bean properties**:
    - `populateBean` fills in the bean properties. It loops over all `InstantiationAwareBeanPostProcessors` and calls their methods to post-process the properties.

19. **Bean initialization**:

    - `initializeBean` runs the bean initialization. It calls the Aware interface methods, the `BeanPostProcessors` before and after initialization, and the bean's custom init methods.

    - `invokeAwareMethods` calls the Aware interfaces of the bean, so the bean can use some of the capabilities the Spring container provides.

20. **Registering the bean destruction method**:
    - `registerDisposableBeanIfNecessary` registers a destruction callback for the bean. It is triggered when the container shuts down and needs to release resources or run other destruction logic.

21. **Protecting concurrent bean creation**:
    - `afterSingletonCreation` keeps bean creation thread-safe and protects the system from wrong concurrent access.

22. **Handling the singleton bean lifecycle**:
    - `addSingleton` updates the caches and collections that track the singleton bean lifecycle.

23. **Getting or converting the bean instance**:

    - `getObjectForBeanInstance` takes a bean instance and a name. It returns either the bean instance itself or the object produced by a `FactoryBean`.

    - `adaptBeanInstance` makes sure the bean instance matches the required type. If it does not, it tries to convert the instance.
