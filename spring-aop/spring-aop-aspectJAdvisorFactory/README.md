## AspectJAdvisorFactory

- [AspectJAdvisorFactory](#aspectjadvisorfactory)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [5. Main Implementation](#5-main-implementation)
    - [6. Class Diagram](#6-class-diagram)
    - [7. Best Practices](#7-best-practices)
    - [8. Source Code Analysis](#8-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `AspectJAdvisorFactory` interface is a key interface in Spring AOP. It turns aspect classes marked with AspectJ annotations into Advisor objects. Its implementations parse the annotations and create Advisors. This lets Spring AOP work with the AspectJ annotation style and gives you flexible aspect-oriented programming.

### 3. Main Features

1. **Parse AspectJ annotations**
   + AspectJAdvisorFactory implementations parse AspectJ annotations such as @Aspect, @Before, and @After, and also the pointcut expressions.
   
2. **Create Advisor objects**

   + From the parsed AspectJ annotation data, AspectJAdvisorFactory implementations create the matching Advisor objects. Each one holds the advice and the pointcut of the aspect.
   
3. **Register Advisor objects**

   + The created Advisor objects can be registered in the Spring AOP framework, so the aspect logic works at runtime.
   
4. **Work with the AspectJ annotation style**

   + With AspectJAdvisorFactory, Spring AOP can be used together with the AspectJ annotation style. This gives developers a flexible and easy way to write AOP code.


### 4. Interface Source

 The `AspectJAdvisorFactory` interface creates Spring AOP Advisors from classes marked with AspectJ annotations. It has methods to check whether a class is an aspect, to validate an aspect class, to build the Advisors of an aspect instance, and to build a Spring AOP Advisor and Advice for a given AspectJ advice method.

```java
/**
 * Factory interface for creating Spring AOP Advisors from classes annotated with AspectJ annotation syntax.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see AspectMetadata
 * @see org.aspectj.lang.reflect.AjTypeSystem
 */
public interface AspectJAdvisorFactory {

    /**
     * Determine whether the given class is an aspect, as reported by AspectJ's {@link org.aspectj.lang.reflect.AjTypeSystem}.
     * <p>If the so-called aspect is invalid (for example it extends a concrete aspect class), this simply returns {@code false}.
     * It returns true for some aspects that Spring AOP cannot handle, for example an unsupported instantiation model.
     * Use the {@link #validate} method if you need to handle these cases.
     * @param clazz the so-called annotation-style AspectJ class
     * @return whether AspectJ recognizes this class as an aspect class
     */
    boolean isAspect(Class<?> clazz);

    /**
     * Is the given class a valid AspectJ aspect class?
     * @param aspectClass the so-called annotation-style AspectJ class to validate
     * @throws AopConfigException if the class is an invalid aspect (never valid)
     * @throws NotAnAtAspectException if the class is not an aspect at all (may or may not be valid, depending on the context)
     */
    void validate(Class<?> aspectClass) throws AopConfigException;

    /**
     * Build Spring AOP Advisors for all annotated At-AspectJ methods on the specified aspect instance.
     * @param aspectInstanceFactory the aspect instance factory
     * (not the aspect instance itself, to avoid early instantiation)
     * @return a list of advisors for this class
     */
    List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory);

    /**
     * Build a Spring AOP Advisor for the given AspectJ advice method.
     * @param candidateAdviceMethod the candidate advice method
     * @param aspectInstanceFactory the aspect instance factory
     * @param declarationOrder the declaration order within the aspect
     * @param aspectName the name of the aspect
     * @return {@code null} if the method is not an AspectJ advice method, or if it is a pointcut that will be used by other advice but will not create a Spring advice itself
     */
    @Nullable
    Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory,
          int declarationOrder, String aspectName);

    /**
     * Build a Spring AOP Advice for the given AspectJ advice method.
     * @param candidateAdviceMethod the candidate advice method
     * @param expressionPointcut the AspectJ expression pointcut
     * @param aspectInstanceFactory the aspect instance factory
     * @param declarationOrder the declaration order within the aspect
     * @param aspectName the name of the aspect
     * @return {@code null} if the method is not an AspectJ advice method, or if it is a pointcut that will be used by other advice but will not create a Spring advice itself
     * @see org.springframework.aop.aspectj.AspectJAroundAdvice
     * @see org.springframework.aop.aspectj.AspectJMethodBeforeAdvice
     * @see org.springframework.aop.aspectj.AspectJAfterAdvice
     * @see org.springframework.aop.aspectj.AspectJAfterReturningAdvice
     * @see org.springframework.aop.aspectj.AspectJAfterThrowingAdvice
     */
    @Nullable
    Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
          MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName);

}
```

### 5. Main Implementation

1. **ReflectiveAspectJAdvisorFactory**

   + The `ReflectiveAspectJAdvisorFactory` implementation uses reflection to parse AspectJ annotations and create the matching Advisor objects. It supports annotation-style AspectJ aspects and gives us flexible and powerful aspect-oriented programming.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AbstractAspectJAdvisorFactory
class AspectJAdvisorFactory {
<<Interface>>

}
class ReflectiveAspectJAdvisorFactory

AbstractAspectJAdvisorFactory  ..>  AspectJAdvisorFactory 
ReflectiveAspectJAdvisorFactory  -->  AbstractAspectJAdvisorFactory 

~~~

### 7. Best Practices

This example uses `ReflectiveAspectJAdvisorFactory`, an implementation of `AspectJAdvisorFactory`, to create Advisors and print them. First, `DefaultListableBeanFactory` creates a default bean factory, and a singleton bean named "myAspect" of type `MyAspect` is registered in it. Then a `MetadataAwareAspectInstanceFactory` named `factory` is created to instantiate the aspect. Next, a `ReflectiveAspectJAdvisorFactory` named `aspectJAdvisorFactory` is created and used to get the Advisors of all annotated AspectJ methods. Finally, the Advisors are printed in a loop.

```java
public class AspectJAdvisorFactoryDemo {

    public static void main(String[] args) {
        // Create a default bean factory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // Register a singleton bean named "myAspect" of type MyAspect in the bean factory
        beanFactory.registerSingleton("myAspect", new MyAspect());

        // Create an aspect instance factory to instantiate the aspect
        MetadataAwareAspectInstanceFactory factory = new BeanFactoryAspectInstanceFactory(beanFactory, "myAspect");
        // Create a ReflectiveAspectJAdvisorFactory instance to create Advisors
        ReflectiveAspectJAdvisorFactory aspectJAdvisorFactory = new ReflectiveAspectJAdvisorFactory(beanFactory);
        // Get the Advisors of all annotated AspectJ methods
        List<Advisor> advisors = aspectJAdvisorFactory.getAdvisors(factory);
        // Print the Advisors
        advisors.forEach(System.out::println);
    }
}
```

The aspect class is marked with the AspectJ annotation `@Aspect`. It has two advice methods, `before()` and `after()`, marked with `@Before` and `@After`. They run before and after the target method `com.xcs.spring.MyService.doSomething()` and print log messages.

```java
@Aspect
class MyAspect {

    @Before("execution(* com.xcs.spring.MyService.doSomething(..))")
    public void before() {
        System.out.println("Before executing the method..." );
    }

    @After("execution(* com.xcs.spring.MyService.doSomething(..))")
    public void after() {
        System.out.println("After executing the method..." );
    }
}
```

`MyService` is a simple Java class with one method, `doSomething()`. The method prints the log message "Doing something...". This class is the example target that the aspect logic is applied to.

```java
public class MyService {
    public void doSomething() {
        System.out.println("Doing something...");
    }
}
```

The run result shows two Advisor objects. They match the `before()` and `after()` methods of the `MyAspect` aspect class, and both use the same pointcut expression `execution(* com.xcs.spring.MyService.doSomething(..))`.

```java
InstantiationModelAwarePointcutAdvisor: expression [execution(* com.xcs.spring.MyService.doSomething(..))]; advice method [public void com.xcs.spring.MyAspect.before()]; perClauseKind=SINGLETON
InstantiationModelAwarePointcutAdvisor: expression [execution(* com.xcs.spring.MyService.doSomething(..))]; advice method [public void com.xcs.spring.MyAspect.after()]; perClauseKind=SINGLETON
```

### 8. Source Code Analysis

In the `org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisors` method, the list of Advisors of the aspect class is built from the given aspect instance factory. First, it validates the aspect class. Then it uses the metadata to decide whether the aspect instance factory must be lazily instantiated. Next, it loops over the methods of the aspect class, gets the Advisors, and adds them to the list. If the aspect is per-target and lazily instantiated, it adds a synthetic instantiation Advisor. Finally, it looks for introduction fields in the aspect class, gets their Advisors, adds them to the list, and returns the list.

```java
/**
 * Get the list of Advisors.
 * @param aspectInstanceFactory the aspect instance factory
 * @return the list of Advisors
 */
@Override
public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory aspectInstanceFactory) {
    // Get the aspect class and the aspect name
    Class<?> aspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
    String aspectName = aspectInstanceFactory.getAspectMetadata().getAspectName();
    // Validate the aspect class
    validate(aspectClass);

    // Wrap the MetadataAwareAspectInstanceFactory in a decorator so the aspect is instantiated only once
    MetadataAwareAspectInstanceFactory lazySingletonAspectInstanceFactory =
          new LazySingletonAspectInstanceFactoryDecorator(aspectInstanceFactory);

    // Create the list of Advisors
    List<Advisor> advisors = new ArrayList<>();
    // Loop over the methods of the aspect class and get the Advisors
    for (Method method : getAdvisorMethods(aspectClass)) {
       // The JDK no longer returns methods in source declaration order, so declarationOrderInAspect is fixed to 0 to keep the advice order reliable across JVM restarts
       Advisor advisor = getAdvisor(method, lazySingletonAspectInstanceFactory, 0, aspectName);
       if (advisor != null) {
          advisors.add(advisor);
       }
    }

    // For a per-target aspect, add a synthetic instantiation Advisor
    if (!advisors.isEmpty() && lazySingletonAspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
       Advisor instantiationAdvisor = new SyntheticInstantiationAdvisor(lazySingletonAspectInstanceFactory);
       advisors.add(0, instantiationAdvisor);
    }

    // Look for introduction fields in the aspect class
    for (Field field : aspectClass.getDeclaredFields()) {
       Advisor advisor = getDeclareParentsAdvisor(field);
       if (advisor != null) {
          advisors.add(advisor);
       }
    }

    return advisors;
}
```

The `org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisorMethods` method gets the list of advice methods of the aspect class. It loops over the methods of the aspect class by reflection, filters the advice methods with adviceMethodFilter, sorts them if there is more than one, and returns them.

```java
/**
 * Get the list of methods in the aspect class that are used as advice.
 * 
 * @param aspectClass the aspect class
 * @return the list of advice methods in the aspect class
 */
private List<Method> getAdvisorMethods(Class<?> aspectClass) {
    // Create an empty method list
    List<Method> methods = new ArrayList<>();
    // Use ReflectionUtils to loop over the methods of the aspect class and add the matching methods to the list
    ReflectionUtils.doWithMethods(aspectClass, methods::add, adviceMethodFilter);
    // If there is more than one method, that is, several advice methods, sort the list with the given comparator
    if (methods.size() > 1) {
        methods.sort(adviceMethodComparator);
    }
    // Return the method list
    return methods;
}
```

The `org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#adviceMethodFilter` field filters the advice methods of the aspect class. It uses ReflectionUtils.USER_DECLARED_METHODS to select the user-declared methods and excludes methods marked with @Pointcut.

```java
private static final MethodFilter adviceMethodFilter = ReflectionUtils.USER_DECLARED_METHODS
    .and(method -> (AnnotationUtils.getAnnotation(method, Pointcut.class) == null));
```

The `org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#adviceMethodComparator` field sorts the advice types in this order: `Around`, `Before`, `After`, `AfterReturning`, `AfterThrowing`. There is one special case. @After is sorted before `@AfterReturning` and `@AfterThrowing`, but the @After method is actually called after the `@AfterReturning` and `@AfterThrowing` methods. This is because `AspectJAfterAdvice.invoke(MethodInvocation)` calls `proceed()` in a `try` block, and calls the `@After` method only in the matching `finally` block.

```java
private static final Comparator<Method> adviceMethodComparator;

static {
    // Note: @After is sorted before @AfterReturning and @AfterThrowing,
    // but the @After advice method is actually called after the @AfterReturning and @AfterThrowing methods.
    // This is because the AspectJAfterAdvice.invoke(MethodInvocation) method calls proceed() in a `try` block,
    // and calls the @After method only in the matching `finally` block.

    // Define a method comparator that sorts the advice types in this order: Around, Before, After, AfterReturning, AfterThrowing.
    // It also covers the special case: even though @After is sorted before @AfterReturning and @AfterThrowing,
    // the @After method is actually called after the @AfterReturning and @AfterThrowing methods.
    Comparator<Method> adviceKindComparator = new ConvertingComparator<>(
        new InstanceComparator<>(
            Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class),
        (Converter<Method, Annotation>) method -> {
            // Use AspectJAnnotation to find the AspectJ annotation on the method
            AspectJAnnotation<?> ann = AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(method);
            return (ann != null ? ann.getAnnotation() : null);
        });
    
    // Define a method name comparator
    Comparator<Method> methodNameComparator = new ConvertingComparator<>(Method::getName);
    
    // Combine the two comparators in order: sort by advice type first, then by method name
    adviceMethodComparator = adviceKindComparator.thenComparing(methodNameComparator);
}
```

In the `org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisor` method, an Advisor object is built from a candidate advice method. It first validates the aspect class and then gets the pointcut expression. If the pointcut expression is null, it returns null. Otherwise it creates an InstantiationModelAwarePointcutAdvisorImpl object that wraps the pointcut expression, the advice method, and other data.

```java
/**
 * Get an Advisor object from the candidate advice method.
 * 
 * @param candidateAdviceMethod the candidate advice method
 * @param aspectInstanceFactory the aspect instance factory, used to create the aspect instance
 * @param declarationOrderInAspect the declaration order in the aspect
 * @param aspectName the name of the aspect
 * @return the Advisor object, or null if the candidate method is not a valid pointcut
 */
@Override
@Nullable
public Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aspectInstanceFactory,
       int declarationOrderInAspect, String aspectName) {

    // Validate the aspect class
    validate(aspectInstanceFactory.getAspectMetadata().getAspectClass());

    // Get the pointcut expression
    AspectJExpressionPointcut expressionPointcut = getPointcut(
          candidateAdviceMethod, aspectInstanceFactory.getAspectMetadata().getAspectClass());
    // If the pointcut expression is null, return null
    if (expressionPointcut == null) {
       return null;
    }

    // Create an InstantiationModelAwarePointcutAdvisorImpl object to manage the pointcut expression, the advice method, and other data
    return new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod,
          this, aspectInstanceFactory, declarationOrderInAspect, aspectName);
}
```

The `org.springframework.aop.aspectj.annotation.InstantiationModelAwarePointcutAdvisorImpl#InstantiationModelAwarePointcutAdvisorImpl` constructor sets up the aspect properties: the declared pointcut, the declaring class of the aspect method, the method name, the parameter types, the aspect advisor factory, the aspect instance factory, the declaration order, and the aspect name. If the aspect is lazily instantiated, it creates a dynamic pointcut that combines the static pointcut with the initial pointcut, so the pointcut can change from the pre-instantiation state to the post-instantiation state. If the aspect is not lazily instantiated, it uses the initial pointcut and instantiates the advice at once.

```java
public InstantiationModelAwarePointcutAdvisorImpl(AspectJExpressionPointcut declaredPointcut,
       Method aspectJAdviceMethod, AspectJAdvisorFactory aspectJAdvisorFactory,
       MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {

    // Declared pointcut
    this.declaredPointcut = declaredPointcut;
    // Get the class that declares the advice method
    this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
    // Get the name of the advice method
    this.methodName = aspectJAdviceMethod.getName();
    // Get the parameter types of the advice method
    this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
    // Set the AspectJ advice method
    this.aspectJAdviceMethod = aspectJAdviceMethod;
    // Set the AspectJ AdvisorFactory
    this.aspectJAdvisorFactory = aspectJAdvisorFactory;
    // Set the aspect instance factory
    this.aspectInstanceFactory = aspectInstanceFactory;
    // Set the declaration order
    this.declarationOrder = declarationOrder;
    // Set the aspect name
    this.aspectName = aspectName;

    // If the aspect is lazily instantiated
    if (aspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
       // The static part of the pointcut is a lazy type
       Pointcut preInstantiationPointcut = Pointcuts.union(
             aspectInstanceFactory.getAspectMetadata().getPerClausePointcut(), this.declaredPointcut);

       // Make it dynamic: it must change from the pre-instantiation state to the post-instantiation state
       // If it is not a dynamic pointcut, the Spring AOP infrastructure may optimize it away after the first evaluation
       this.pointcut = new PerTargetInstantiationModelPointcut(
             this.declaredPointcut, preInstantiationPointcut, aspectInstanceFactory);
       // Set to lazy
       this.lazy = true;
    }
    else {
       // Singleton aspect
       this.pointcut = this.declaredPointcut;
       this.lazy = false;
       // Instantiate the advice
       this.instantiatedAdvice = instantiateAdvice(this.declaredPointcut);
    }
}
```

The `org.springframework.aop.aspectj.annotation.InstantiationModelAwarePointcutAdvisorImpl#instantiateAdvice` method gets the advice instance with AspectJAdvisorFactory. Then it checks whether it is null. If it is null, it returns an empty advice.

```java
/**
 * Instantiate the advice for the given pointcut.
 *
 * @param pointcut the pointcut expression
 * @return the instantiated advice
 */
private Advice instantiateAdvice(AspectJExpressionPointcut pointcut) {
    // Get the advice instance with AspectJAdvisorFactory
    Advice advice = this.aspectJAdvisorFactory.getAdvice(this.aspectJAdviceMethod, pointcut,
            this.aspectInstanceFactory, this.declarationOrder, this.aspectName);
    // If the advice instance is null, return an empty advice
    return (advice != null ? advice : EMPTY_ADVICE);
}
```

The `org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvice` method gets the Spring AOP advice instance for the given candidate advice method, pointcut expression, aspect instance factory, and other data. It first validates the aspect class. Then, based on the AspectJ annotation type of the candidate advice method, it creates the matching Spring AOP advice, such as around advice, before advice, or after advice, and sets the advice properties. Finally, it returns the created Spring AOP advice.

```java
@Override
@Nullable
public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
       MetadataAwareAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {

    // Get the aspect class that holds the candidate advice method
    Class<?> candidateAspectClass = aspectInstanceFactory.getAspectMetadata().getAspectClass();
    // Validate the aspect class
    validate(candidateAspectClass);

    // Get the AspectJ annotation of the candidate advice method
    AspectJAnnotation<?> aspectJAnnotation =
          AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
    // If the candidate advice method has no AspectJ annotation, return null
    if (aspectJAnnotation == null) {
       return null;
    }

    // Check whether the aspect class is an AspectJ annotation class
    if (!isAspect(candidateAspectClass)) {
       // If not, throw an exception
       throw new AopConfigException("Advice must be declared inside an aspect type: " +
             "Offending method '" + candidateAdviceMethod + "' in class [" +
             candidateAspectClass.getName() + "]");
    }

    // If the log level is DEBUG, log the AspectJ method that was found
    if (logger.isDebugEnabled()) {
       logger.debug("Found AspectJ method: " + candidateAdviceMethod);
    }

    // Declare the Spring AOP advice instance
    AbstractAspectJAdvice springAdvice;

    // Create the matching Spring AOP advice by annotation type
    switch (aspectJAnnotation.getAnnotationType()) {
       // Handle the pointcut annotation
       case AtPointcut:
          if (logger.isDebugEnabled()) {
             logger.debug("Processing pointcut '" + candidateAdviceMethod.getName() + "'");
          }
          return null;
       // Handle around advice
       case AtAround:
          springAdvice = new AspectJAroundAdvice(
                candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
          break;
       // Handle before advice
       case AtBefore:
          springAdvice = new AspectJMethodBeforeAdvice(
                candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
          break;
       // Handle after advice
       case AtAfter:
          springAdvice = new AspectJAfterAdvice(
                candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
          break;
       // Handle after-returning advice
       case AtAfterReturning:
          springAdvice = new AspectJAfterReturningAdvice(
                candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
          AfterReturning afterReturningAnnotation = (AfterReturning) aspectJAnnotation.getAnnotation();
          if (StringUtils.hasText(afterReturningAnnotation.returning())) {
             springAdvice.setReturningName(afterReturningAnnotation.returning());
          }
          break;
       // Handle after-throwing advice
       case AtAfterThrowing:
          springAdvice = new AspectJAfterThrowingAdvice(
                candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
          AfterThrowing afterThrowingAnnotation = (AfterThrowing) aspectJAnnotation.getAnnotation();
          if (StringUtils.hasText(afterThrowingAnnotation.throwing())) {
             springAdvice.setThrowingName(afterThrowingAnnotation.throwing());
          }
          break;
       // Handle other advice types and throw an unsupported operation exception
       default:
          throw new UnsupportedOperationException(
                "Unsupported advice type on method: " + candidateAdviceMethod);
    }

    // Configure the advice properties
    springAdvice.setAspectName(aspectName);
    springAdvice.setDeclarationOrder(declarationOrder);
    // Get the array of advice method parameter names
    String[] argNames = this.parameterNameDiscoverer.getParameterNames(candidateAdviceMethod);
    // If the parameter name array is not null, set it on the advice
    if (argNames != null) {
       springAdvice.setArgumentNamesFromStringArray(argNames);
    }
    // Calculate argument binding
    springAdvice.calculateArgumentBindings();

    return springAdvice;
}
```
