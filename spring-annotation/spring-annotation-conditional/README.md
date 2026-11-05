## @Conditional

- [@Conditional](#conditional)
  - [1. Overview](#1-overview)
  - [2. Annotation Description](#2-annotation-description)
  - [3. Annotation Source](#3-annotation-source)
  - [4. Main Features](#4-main-features)
  - [5. Best Practices](#5-best-practices)
    - [Use on @Bean](#use-on-bean)
    - [Use on @Configuration](#use-on-configuration)
    - [Custom Composed Annotation](#custom-composed-annotation)
  - [6. Sequence Diagram](#6-sequence-diagram)
  - [7. Source Code Analysis](#7-source-code-analysis)
  - [8. Notes](#8-notes)
  - [9. Summary](#9-summary)
    - [Best Practices Summary](#best-practices-summary)
    - [Source Code Analysis Summary](#source-code-analysis-summary)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN](https://blog.csdn.net/duzhuang2399/article/details/133800722) 📚 **Contents** - [All articles](https://github.com/xuchengsheng/spring-reading) 🔗 **Source code** - [@Conditional source code](https://github.com/xuchengsheng/spring-reading/tree/master/spring-annotation/spring-annotation-conditional)

### 2. Annotation Description

The `@Conditional` annotation decides whether a bean is registered in the Spring container, based on some condition. It gives a flexible way to turn a bean on or off depending on the environment, configuration, or other factors.

### 3. Annotation Source

`@Conditional` has been part of Spring since version 3.0. It says a component can be registered only when all the given conditions match.

```java
/**
 * Indicates that a component is only eligible for registration when all specified conditions match.
 *
 * A condition is any state that can be determined programmatically before the bean definition
 * is due to be registered (see Condition for details).
 *
 * The @Conditional annotation may be used in any of the following ways:
 * 
 * as a type-level annotation on any class directly or indirectly annotated with @Component, including Configuration @Configuration classes
 * as a meta-annotation, for the purpose of composing custom stereotype annotations
 * as a method-level annotation on any Bean @Bean method
 * 
 *
 * If a @Configuration class is marked with @Conditional, all of the @Bean methods, Import @Import annotations,
 * and ComponentScan @ComponentScan annotations associated with that class will be subject to the conditions.
 *
 * NOTE: Inheritance of @Conditional annotations is not supported; any conditions from superclasses or from overridden methods will not be considered.
 * In order to enforce these semantics, @Conditional itself is not declared as a java.lang.annotation.Inherited @Inherited;
 * furthermore, any custom composed annotation that is meta-annotated with @Conditional must not be declared as @Inherited.
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 4.0
 * @see Condition
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {

	/**
	 * All {@link Condition} classes that must match in order for the component to be registered.
	 */
	Class<? extends Condition>[] value();
}
```

### 4. Main Features

1. **Conditional bean registration**
   + You can decide whether to create and register a bean based on specific conditions. This lets you choose which beans are instantiated depending on the environment, configuration, or other factors.
2. **Conditional configuration classes**
   + It works on a single bean and also on a whole configuration class. If the condition on a configuration class is not met, none of the beans defined in it are registered.
3. **Flexibility**
   + It is used with a class that implements the `Condition` interface. This interface lets you write your own condition logic, so you can decide whether to register a bean in many different situations.
4. **Combining with other annotations**
   + Besides `@Bean` and `@Configuration`, `@Conditional` can be used as a meta-annotation to build your own composed annotations. These annotations use `@Conditional` inside to apply the condition logic.
5. **Effect on a whole configuration class**
   + When `@Conditional` is used on a configuration class, the condition affects the class itself and also its `@Bean` methods, `@Import` annotations, and `@ComponentScan` annotations.
6. **No inheritance**
   + `@Conditional` itself is not inherited. So conditions from a parent class or interface are not considered by subclasses.

### 5. Best Practices

#### Use on @Bean

First, the entry class. It sets the system property `enable.bean` to `true`. Then it uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations) with the `MyConfiguration` class as the argument. Finally, it prints the names of all bean definitions in the Spring context.

```java
public class ConditionBeanApplication {

    public static void main(String[] args) {
        System.setProperty("enable.bean","true");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyBeanConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
```

`MyBeanConfiguration` defines two beans: `user1` and `user2`. The `user1` bean is created only if `BeanPropertyCondition` matches. The `user2` bean is always created.

```java
@Configuration
public class MyBeanConfiguration {

    @Bean
    @Conditional(BeanPropertyCondition.class)
    public User1 user1() {
        return new User1();
    }

    @Bean
    public User2 user2() {
        return new User2();
    }
}
```

`BeanPropertyCondition` decides whether the condition is met from the value of `enable.bean`. If the environment property `enable.bean` is `true`, the `user1` bean is created and added to the Spring container. If `enable.bean` is not `true` (or is not set), the `user1` bean is not created.

```java
public class BeanPropertyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "true".equals(context.getEnvironment().getProperty("enable.bean"));
    }
}
```

Two simple Java classes: `User1` and `User2`.

```java
public class User1 {

}

public class User2 {

}
```

When `enable.bean` is `true`, the result shows that the `user1` bean is registered because of the property value. The `user2` bean is not affected by this property.

```java
beanDefinitionName = myBeanConfiguration
beanDefinitionName = user1
beanDefinitionName = user2
```

When `enable.bean` is `false`, the condition is not met. So the `user1` bean is not registered. The `user2` bean is not affected by any condition.

```java
beanDefinitionName = myBeanConfiguration
beanDefinitionName = user2
```

#### Use on @Configuration

First, the entry class. It sets the system property `enable.config` to `true`. Then it uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations) with the `MyConfiguration` class as the argument. Finally, it prints the names of all bean definitions in the Spring context.

```java
public class ConditionConfigurationApplication {

    public static void main(String[] args) {
        System.setProperty("enable.config","true");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfigConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
```

`MyConfigConfiguration` defines two beans: `user3` and `user4`. When `ConfigPropertyCondition` is not met, the `MyConfigConfiguration` class is not activated. Then `user3` and `user4` are not registered.

```java
@Configuration
@Conditional(ConfigPropertyCondition.class)
public class MyConfigConfiguration {

    @Bean
    public User3 user3() {
        return new User3();
    }

    @Bean
    public User4 user4() {
        return new User4();
    }
}
```

`ConfigPropertyCondition` decides whether the condition is met from the value of `enable.config`. When `enable.config` is `true`, `ConfigPropertyCondition` matches. The `MyConfigConfiguration` class is activated, and both `user3` and `user4` are registered in the Spring context. When `enable.config` is `false` or not set, `ConfigPropertyCondition` does not match. The `MyConfigConfiguration` class is not activated, and neither `user3` nor `user4` is registered.

```java
public class ConfigPropertyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "true".equals(System.getProperty("enable.config"));
    }
}
```

Two simple Java classes: `User3` and `User4`.

```java
public class User3 {
    
}

public class User4 {
    
}
```

When `enable.config` is `true`, the result shows that both `user3` and `user4` in `MyConfigConfiguration` are registered.

```java
beanDefinitionName = myConfigConfiguration
beanDefinitionName = user3
beanDefinitionName = user4
```

When `enable.config` is `false`, no bean related to `MyConfigConfiguration` (including itself) is registered.

```java
No beans
```

#### Custom Composed Annotation

First, the entry class. It sets the system property `enable.custom` to `true`. Then it uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations) with the `MyConfiguration` class as the argument. Finally, it prints the names of all bean definitions in the Spring context.

```java
public class ConditionCustomApplication {

    public static void main(String[] args) {
        System.setProperty("enable.custom","true");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyCustomConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
```

`MyCustomConfiguration` defines two beans: `user5` and `user6`. If the condition of `@ConditionalOnCustomActive` is met, the `MyCustomConfiguration` class is activated, and `user5` and `user6` are registered in the Spring container. If the condition is not met, the `MyCustomConfiguration` class is not activated. Neither `user5` nor `user6` is registered.

```java
@Configuration
@ConditionalOnCustomActive
public class MyCustomConfiguration {

    @Bean
    public User5 user5() {
        return new User5();
    }

    @Bean
    public User6 user6() {
        return new User6();
    }
}
```

`@ConditionalOnCustomActive` is a composed annotation. It uses the `@Conditional` meta-annotation to link to `CustomActiveCondition`.

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(CustomActiveCondition.class)
public @interface ConditionalOnCustomActive {

}
```

`CustomActiveCondition` decides whether the condition is met from the value of `enable.custom`. When `enable.custom` is `true`, `CustomActiveCondition` matches because the `matches` method returns `true`. The `MyCustomConfiguration` class has `@ConditionalOnCustomActive` (which refers to `CustomActiveCondition`), so it is activated, and `user5` and `user6` are registered in the Spring container. When `enable.custom` is `false` or not set, `CustomActiveCondition` does not match because `matches` returns `false`. Since `MyCustomConfiguration` has `@ConditionalOnCustomActive`, the class is not activated. Neither `user5` nor `user6` is registered.

```java
public class CustomActiveCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return "true".equals(System.getProperty("enable.custom"));
    }
}
```

Two simple Java classes: `User5` and `User6`.

```java
public class User5 {
    
}

public class User6 {
    
}
```

When `enable.custom` is `true`, the result shows that our composed annotation `@ConditionalOnCustomActive` and its condition `CustomActiveCondition` work. They activate the `MyCustomConfiguration` class based on the `enable.custom` system property.

```java
beanDefinitionName = myCustomConfiguration
beanDefinitionName = user5
beanDefinitionName = user6
```

When `enable.custom` is `false`, no bean related to `MyCustomConfiguration` (including itself) is registered in the Spring container.

```java
No beans
```

### 6. Sequence Diagram

~~~mermaid
sequenceDiagram 
ConditionCustomApplication->>AnnotationConfigApplicationContext: AnnotationConfigApplicationContext(componentClasses)<br>Start context
AnnotationConfigApplicationContext-->>ConfigurationApplication: Return context<br>Return the context instance
AnnotationConfigApplicationContext->>AnnotationConfigApplicationContext: register(componentClasses)<br>Register component classes
AnnotationConfigApplicationContext->>AnnotatedBeanDefinitionReader: register(componentClasses)<br>Reader registers classes
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: registerBean(beanClass)<br>Register bean class
AnnotatedBeanDefinitionReader-->>AnnotatedBeanDefinitionReader: doRegisterBean(beanClass,name,qualifiers, supplier,customizers)<br>Do bean registration
AnnotatedBeanDefinitionReader->>ConditionEvaluator:shouldSkip(metadata)
ConditionEvaluator->>ConditionEvaluator:shouldSkip(metadata,phase)
ConditionEvaluator->>ConditionEvaluator:getConditionClasses(metadata)
Note right of ConditionEvaluator: Returns List<String[]>
ConditionEvaluator->>ConditionEvaluator:getCondition(conditionClassName,classloader)
ConditionEvaluator->>AnnotationAwareOrderComparator:sort(conditions)
ConditionEvaluator->>CustomActiveCondition:matches(context,metadata)
CustomActiveCondition->>ConditionEvaluator:Return true or false
ConditionEvaluator->>AnnotatedBeanDefinitionReader:Return true or false
AnnotatedBeanDefinitionReader->>AnnotatedBeanDefinitionReader:If shouldSkip returns true, skip bean registration
~~~

### 7. Source Code Analysis

First, the entry class. It sets the system property `enable.custom` to `true`. Then it uses `AnnotationConfigApplicationContext` (a way to configure the Spring container with Java annotations) with the `MyConfiguration` class as the argument. Finally, it prints the names of all bean definitions in the Spring context.

```java
public class ConditionCustomApplication {

    public static void main(String[] args) {
        System.setProperty("enable.custom","true");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyCustomConfiguration.class);
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            System.out.println("beanDefinitionName = " + beanDefinitionName);
        }
    }
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext` constructor does three steps. We focus on the `refresh()` method.

```java
public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
    this();
    register(componentClasses);
    refresh();
}
```

The `org.springframework.context.annotation.AnnotationConfigApplicationContext#register` method lets us register one or more component classes (for example, classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`, or `@Configuration`) in the Spring container.

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

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#register` method goes through each component class and calls another method to do the real registration.

```java
public void register(Class<?>... componentClasses) {
    for (Class<?> componentClass : componentClasses) {
        registerBean(componentClass);
    }
}
```

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#registerBean(beanClass)` method is a quick way to register a bean type without any other detailed settings or arguments.

```java
public void registerBean(Class<?> beanClass) {
    doRegisterBean(beanClass, null, null, null, null);
}
```

The `org.springframework.context.annotation.AnnotatedBeanDefinitionReader#doRegisterBean` method holds the logic for conditional registration. A bean is registered only when its conditions are met.

```java
private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
			@Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
			@Nullable BeanDefinitionCustomizer[] customizers) {

    // Create an annotated bean definition from the given bean class.
    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);

    // Use the condition evaluator to check whether registration of this bean should be skipped.
    // If the bean does not meet the conditions, return right away and skip the rest of the registration.
    if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
        return;
    }

	// ... [code omitted for brevity]
}
```

The `org.springframework.context.annotation.ConditionEvaluator#shouldSkip(metadata)` method calls the other version of `shouldSkip` and passes `null` as the second argument.

```java
public boolean shouldSkip(AnnotatedTypeMetadata metadata) {
    return shouldSkip(metadata, null);
}
```

The `org.springframework.context.annotation.ConditionEvaluator#shouldSkip(metadata,phase)` method decides whether to skip the registration of a configuration class or bean, based on the given conditions (usually defined by `@Conditional`).

```java
/**
 * Decide whether to skip an operation or logic, based on the given metadata and configuration phase.
 *
 * @param metadata The metadata, related to annotations.
 * @param phase    The current configuration phase, may be null.
 * @return true if it should be skipped; false otherwise.
 */
public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationPhase phase) {
    // If the metadata is null or has no @Conditional annotation, do not skip.
    if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
        return false;
    }

    // If no configuration phase is given, work out the right one.
    if (phase == null) {
        // If the metadata is annotation metadata and is a configuration candidate, use the PARSE_CONFIGURATION phase.
        if (metadata instanceof AnnotationMetadata &&
            ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata) metadata)) {
            return shouldSkip(metadata, ConfigurationPhase.PARSE_CONFIGURATION);
        }
        // Otherwise use the REGISTER_BEAN phase.
        return shouldSkip(metadata, ConfigurationPhase.REGISTER_BEAN);
    }

    // Get all conditions and instantiate them from the related condition classes.
    List<Condition> conditions = new ArrayList<>();
    for (String[] conditionClasses : getConditionClasses(metadata)) {
        for (String conditionClass : conditionClasses) {
            Condition condition = getCondition(conditionClass, this.context.getClassLoader());
            conditions.add(condition);
        }
    }

    // Sort the conditions.
    AnnotationAwareOrderComparator.sort(conditions);

    // Go through all conditions and check whether they match the current configuration phase.
    for (Condition condition : conditions) {
        ConfigurationPhase requiredPhase = null;
        if (condition instanceof ConfigurationCondition) {
            requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
        }
        // If the condition does not match the context and metadata, skip.
        if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
            return true;
        }
    }

    return false;
}
```

The `org.springframework.context.annotation.ConditionEvaluator#getConditionClasses` method gets the names of the condition classes linked to the `@Conditional` annotation from the given annotation metadata.

```java
/**
 * Get the names of the condition classes linked to the @Conditional annotation from the given annotation metadata.
 *
 * @param metadata The metadata, usually related to the annotations of a bean or configuration class.
 * @return A list with the names of the condition classes linked to @Conditional. If there are none, an empty list is returned.
 */
private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
    // Get all attribute values of the @Conditional annotation.
    MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
    
    // Try to get "value" from the attributes. It should point to the condition class names.
    Object values = (attributes != null ? attributes.get("value") : null);

    // Return the list of condition class names, or an empty list if there are none.
    return (List<String[]>) (values != null ? values : Collections.emptyList());
}
```

The `org.springframework.context.annotation.ConditionEvaluator#getCondition` method creates a `Condition` object from the given condition class name and class loader.

```java
/**
 * Create a Condition object from the given condition class name and class loader.
 *
 * @param conditionClassName The fully qualified name of the condition class.
 * @param classloader The class loader used to load the condition class, may be null.
 * @return The created Condition object.
 */
private Condition getCondition(String conditionClassName, @Nullable ClassLoader classloader) {
    // Use the class loader to resolve and load the given condition class.
    Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
    
    // Create an instance of the resolved condition class and return it.
    return (Condition) BeanUtils.instantiateClass(conditionClass);
}
```

### 8. Notes

1. **Implement the `Condition` interface**
   + To use `@Conditional`, we need to implement the `Condition` interface. It has one method, `matches(ConditionContext context, AnnotatedTypeMetadata metadata)`. Our condition logic goes there.
2. **Class level and method level**:
   - `@Conditional` can be applied to a `@Bean` method to control the creation of a specific bean.
   - It can also be applied to a `@Configuration` class to control the loading of the whole configuration class.
3. **Combining several conditions**
   + You can pass several conditions to `@Conditional` as an array. All conditions must be met for the bean to be created.
4. **Combining with other annotations**
   + `@Conditional` can be used with other annotations such as `@Profile`. But watch how they interact. For example, if a bean has a specific `@Profile` and also `@Conditional`, both must be true for the bean to be created.
5. **Avoid complex logic**
   + `Condition` lets us write any condition logic, but it is best to keep it simple. Simple logic is easier to understand and maintain.
6. **Performance**
   + The `matches` method may be called many times during the application lifecycle. Make sure it runs fast, and avoid expensive work in it.
7. **Using `ConditionContext`**:
   + `ConditionContext` gives information about the current application context, environment properties, system properties, and more. This makes our conditions more specific and powerful.
8. **Custom condition annotations**
   + To reuse or combine several conditions, we can create our own condition annotations. For example, we can create a `@ConditionalOnCustomActive` annotation that wraps the check of `enable.custom`.
9. **Difference from `@Profile`**
   + `@Conditional` and `@Profile` can give the same result in some cases, but they have different purposes. `@Profile` is based on the environment. `@Conditional` is more general and lets us create beans based on any condition.

### 9. Summary

#### Best Practices Summary

1. **Conditional configuration with `@Bean`**

   - **Scenario**
  - Apply a condition to the creation of a single bean.
   - **How**
  - Use `@Conditional` directly on the `@Bean` method.
   - **Result**:
     - When the condition is met (for example, `enable.bean` is `true`), the specific bean (such as `user1`) is registered.
     - When the condition is not met, the bean is not registered.

2. **Conditional configuration with `@Configuration`**

   - **Scenario**
  - Control whether a whole configuration class is active, which affects all beans defined in it.
   - **How**
  - Use `@Conditional` directly on the `@Configuration` class.
   - **Result**:
     - When the condition is met (for example, `enable.config` is `true`), the configuration class is active and all beans inside it (such as `user3` and `user4`) are registered.
     - When the condition is not met, neither the configuration class nor the beans defined in it are registered.

3. **Using a custom composed annotation**

   - **Scenario**
     - Create your own condition annotation to get a clearer and shorter syntax, or to wrap condition logic for specific business needs.
   - **How**
     - Define a new annotation (such as `@ConditionalOnCustomActive`) and use the `@Conditional` meta-annotation to link it to a specific condition class.
   - **Result**:
     - When the condition is met (for example, `enable.custom` is `true`), the configuration class or bean with `@ConditionalOnCustomActive` is registered.
     - When the condition is not met, they are not registered.

#### Source Code Analysis Summary

1. **Initialization and startup**
   - The Spring context is initialized through the `AnnotationConfigApplicationContext` constructor. The `register` and `refresh` methods register the beans and refresh the container.

2. **Registering component classes**
   + `AnnotatedBeanDefinitionReader` registers the component classes. In its `register` method, each component class is registered through `registerBean`.

3. **Condition check**
   + The core condition check runs in `doRegisterBean`. It uses `ConditionEvaluator` to check whether the conditions linked to a bean or configuration class are met.

4. **Metadata check**
   + `ConditionEvaluator` first checks whether the given metadata (usually linked to the annotations of a specific bean or configuration class) has a `@Conditional` annotation. If not, it moves on. If it does, it goes on to check the conditions.

5. **Condition matching**
   + `ConditionEvaluator` gets all condition classes linked to `@Conditional` and creates an instance of each. Then it goes through all conditions and checks each one by calling its `matches` method. If any condition is not met, the whole check returns `false`. This means the bean or configuration class with `@Conditional` should not be registered.

6. **Condition instantiation**
   + `ConditionEvaluator` creates a `Condition` object in the `getCondition` method, from the given condition class name and class loader.
