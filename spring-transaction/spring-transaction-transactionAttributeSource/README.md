## TransactionAttributeSource

- [TransactionAttributeSource](#TransactionAttributeSource)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [5. Main Implementations](#5-main-implementations)
    - [6. Best Practices](#6-best-practices)
    - [7. Source Code Analysis](#7-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `TransactionAttributeSource` interface is a key interface in the Spring Framework
that provides the configuration for transaction management. It analyzes a given method and target class to find the transaction attributes, such as the propagation behavior and the isolation level. This gives declarative transactions flexibility and customization.

### 3. Main Features

1. **Provides transaction attributes**

    + From the given method and target class, it finds the transaction attributes, including the propagation behavior, isolation level, timeout, and read-only flag.

2. **Extensible**

    + Spring provides many classes that implement the `TransactionAttributeSource`
      interface, such as `NameMatchTransactionAttributeSource` and `AnnotationTransactionAttributeSource`
      . They support different parsing strategies, for example matching by method name or configuring with annotations.

### 4. Interface Source

The `TransactionAttributeSource` interface is a strategy interface mainly used by `TransactionInterceptor`
for metadata retrieval. Implementations know how to get transaction attributes, whether from configuration, source-level metadata attributes, or anywhere else. It has two methods: one decides whether a given class is a candidate for transaction attributes, and the other returns the transaction attribute of a given method.

```java
/**
 * Strategy interface used by {@link TransactionInterceptor} for metadata retrieval.
 * Implementations know how to source transaction attributes, whether from configuration, metadata attributes at source level (such as Java 5 annotations), or anywhere else.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 15.04.2003
 * @see TransactionInterceptor#setTransactionAttributeSource
 * @see TransactionProxyFactoryBean#setTransactionAttributeSource
 * @see org.springframework.transaction.annotation.AnnotationTransactionAttributeSource
 */
public interface TransactionAttributeSource {

    /**
     * Determine whether the given class is a candidate for transaction attributes in the metadata format of this {@code TransactionAttributeSource}.
     * <p>If this method returns {@code false}, the methods on the given class will not get traversed for {@link #getTransactionAttribute} introspection.
     * Returning {@code false} is therefore an optimization for non-affected classes, whereas {@code true} simply means that the class needs to get fully introspected for each method on the given class.
     * @param targetClass the class to introspect
     * @return {@code false} if the class is known to have no transaction attributes at class or method level; {@code true} otherwise.
     * The default implementation returns {@code true}, leading to regular introspection.
     * @since 5.2
     */
    default boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    /**
     * Return the transaction attribute for the given method, or {@code null} if the method is non-transactional.
     * @param method the method to introspect
     * @param targetClass the target class (may be {@code null}, in which case the declaring class of the method must be used)
     * @return the matching transaction attribute, or {@code null} if none found
     */
    @Nullable
    TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass);

}
```

### 5. Main Implementations

1. **AnnotationTransactionAttributeSource**

    + An implementation that parses annotation-based transaction configuration. It parses the `@Transactional`
      annotation at class level and method level, and converts the transaction attributes defined in it into a `TransactionAttribute` object.

2. **CompositeTransactionAttributeSource**

    + Combines several `TransactionAttributeSource`s. By combining several `TransactionAttributeSource`
      objects, you can mix different parsing strategies for transaction attributes. This adds flexibility and customization.

3. **MatchAlwaysTransactionAttributeSource**

    + A simple `TransactionAttributeSource`
      implementation that always returns the same transaction attribute. It is usually used in simple cases, or as the default fallback of other, more complex `TransactionAttributeSource` implementations.

4. **MethodMapTransactionAttributeSource**

    + A `TransactionAttributeSource` implementation that matches by method name. You configure a map from method names to transaction attributes, and it finds the transaction attribute from the method name.

5. **NameMatchTransactionAttributeSource**

    + A `TransactionAttributeSource` implementation that matches by method name. It matches the target method against the configured method name patterns and returns the matching transaction attribute.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AbstractFallbackTransactionAttributeSource
class AnnotationTransactionAttributeSource
class CompositeTransactionAttributeSource
class MatchAlwaysTransactionAttributeSource
class MethodMapTransactionAttributeSource
class NameMatchTransactionAttributeSource
class TransactionAttributeSource {
<<Interface>>

}

AbstractFallbackTransactionAttributeSource  ..>  TransactionAttributeSource 
AnnotationTransactionAttributeSource  -->  AbstractFallbackTransactionAttributeSource 
CompositeTransactionAttributeSource  ..>  TransactionAttributeSource 
MatchAlwaysTransactionAttributeSource  ..>  TransactionAttributeSource 
MethodMapTransactionAttributeSource  ..>  TransactionAttributeSource 
NameMatchTransactionAttributeSource  ..>  TransactionAttributeSource 
~~~

### 6. Best Practices

This example uses the `AnnotationTransactionAttributeSource` class to parse annotation-based transaction configuration. It gets the `insertScore` method of the `ScoresServiceImpl`
class, uses an `AnnotationTransactionAttributeSource`
object to parse the transaction attribute of that method, and prints the result to the console. This helps us see the transaction configuration of a given method, for debugging and tuning.

```java
public class TransactionAttributeSourceDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // Get the insertScore method of ScoresServiceImpl
        Method insertScoreMethod = ScoresServiceImpl.class.getMethod("insertScore");
        // Create an annotation-based transaction attribute source
        TransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();
        // Parse the transaction attribute of insertScore
        TransactionAttribute transactionAttribute = transactionAttributeSource.getTransactionAttribute(insertScoreMethod, ScoresServiceImpl.class);
        // Print the transaction attribute
        System.out.println(transactionAttribute);
    }
}
```

The `ScoresServiceImpl` class implements the `ScoresService` interface. Its `insertScore` method is marked with `@Transactional`
, which declares a transaction. The transaction has these properties: read-only (readOnly = true), roll back on any exception (rollbackFor =
Exception.class), isolation level repeatable read (isolation = Isolation.REPEATABLE_READ), timeout of 30 seconds (timeout =
30), and the labels "tx1" and "tx2".

```java
public class ScoresServiceImpl implements ScoresService {

    @Override
    @Transactional(
            readOnly = true,
            rollbackFor = Exception.class,
            isolation = Isolation.REPEATABLE_READ,
            timeout = 30,
            label = {"tx1", "tx2"}
    )
    public void insertScore() {
        // TODO
    }
}
```

In the result, the propagation behavior is `PROPAGATION_REQUIRED`, the isolation level is `ISOLATION_REPEATABLE_READ`, the timeout is 30
seconds, read-only mode is on, the labels are "tx1" and "tx2", and the transaction rolls back on `java.lang.Exception` and its subclasses.

```java
PROPAGATION_REQUIRED,ISOLATION_REPEATABLE_READ,timeout_30,readOnly; [tx1,tx2],-java.lang.Exception
```

### 7. Source Code Analysis

The method `org.springframework.transaction.interceptor.AbstractFallbackTransactionAttributeSource#getTransactionAttribute`
finds the transaction attribute for a method call. If no attribute is found at method level, it falls back to the class-level transaction attribute. First, it checks for a cached value. If there is one, it returns it. Otherwise, it computes the transaction attribute of the method and puts it in the cache. When computing, the method is identified by its qualified name. If the transaction attribute is of type `DefaultTransactionAttribute`
, it sets the descriptor and resolves the attribute strings. Finally, depending on the log level, it logs the added transactional method and its attribute, and puts the computed attribute in the cache.

```java
/**
 * Determine the transaction attribute for this method invocation.
 * <p>Defaults to the class's transaction attribute if no method attribute is found.
 * @param method the method for the current invocation (never {@code null})
 * @param targetClass the target class for this invocation (may be {@code null})
 * @return a TransactionAttribute for this method, or {@code null} if the method is not transactional
 */
@Override
@Nullable
public TransactionAttribute getTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
    // If the method is declared on Object, return null directly, because such methods should not be transactional.
    if (method.getDeclaringClass() == Object.class) {
        return null;
    }

    // First, see if we have a cached value.
    Object cacheKey = getCacheKey(method, targetClass);
    TransactionAttribute cached = this.attributeCache.get(cacheKey);
    if (cached != null) {
        // The value will be either a canonical value indicating there is no transaction attribute, or an actual transaction attribute.
        if (cached == NULL_TRANSACTION_ATTRIBUTE) {
            return null;
        }
        else {
            return cached;
        }
    }
    else {
        // We need to work it out.
        TransactionAttribute txAttr = computeTransactionAttribute(method, targetClass);
        // Put it in the cache.
        if (txAttr == null) {
            this.attributeCache.put(cacheKey, NULL_TRANSACTION_ATTRIBUTE);
        }
        else {
            // Get the qualified name of the method, to identify it in the log.
            String methodIdentification = ClassUtils.getQualifiedMethodName(method, targetClass);
            // If the transaction attribute is a DefaultTransactionAttribute, set the descriptor and resolve the attribute strings.
            if (txAttr instanceof DefaultTransactionAttribute) {
                DefaultTransactionAttribute dta = (DefaultTransactionAttribute) txAttr;
                dta.setDescriptor(methodIdentification);
                dta.resolveAttributeStrings(this.embeddedValueResolver);
            }
            // If the log level is TRACE, log the added transactional method and its attribute.
            if (logger.isTraceEnabled()) {
                logger.trace("Adding transactional method '" + methodIdentification + "' with attribute: " + txAttr);
            }
            this.attributeCache.put(cacheKey, txAttr);
        }
        return txAttr;
    }
}
```

The method `org.springframework.transaction.interceptor.AbstractFallbackTransactionAttributeSource#computeTransactionAttribute`
first checks whether the method is public. Depending on the case, it looks for the transaction attribute on the target class or on the original method. If it finds one, it returns it. Otherwise, it returns
null。

```java
/**
 * Same signature as {@link #getTransactionAttribute}, but doesn't cache the result.
 * {@link #getTransactionAttribute} is effectively a caching decorator for this method.
 * <p>As of 4.1.8, this method can be overridden.
 * @since 4.1.8
 * @see #getTransactionAttribute
 */
@Nullable
protected TransactionAttribute computeTransactionAttribute(Method method, @Nullable Class<?> targetClass) {
	// If only public methods are allowed and the method is not public, it is not allowed.
	if (allowPublicMethodsOnly() && !Modifier.isPublic(method.getModifiers())) {
		return null;
	}

	// The method may be on an interface, but we need attributes from the target class.
	// If the target class is null, the method is unchanged.
	Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

	// First try is the method in the target class.
	TransactionAttribute txAttr = findTransactionAttribute(specificMethod);
	if (txAttr != null) {
		return txAttr;
	}

	// Second try is the transaction attribute on the target class.
	txAttr = findTransactionAttribute(specificMethod.getDeclaringClass());
	if (txAttr != null && ClassUtils.isUserLevelMethod(method)) {
		return txAttr;
	}

	if (specificMethod != method) {
		// Fall back to the original method.
		txAttr = findTransactionAttribute(method);
		if (txAttr != null) {
			return txAttr;
		}
		// Last fallback is the class of the original method.
		txAttr = findTransactionAttribute(method.getDeclaringClass());
		if (txAttr != null && ClassUtils.isUserLevelMethod(method)) {
			return txAttr;
		}
	}

	return null;
}
```

The method `org.springframework.transaction.annotation.AnnotationTransactionAttributeSource#findTransactionAttribute(java.lang.reflect.Method)`
calls the `determineTransactionAttribute` method to find the transaction attribute of the given method and returns it. If it cannot find one, it returns null.

```java
@Override
@Nullable
protected TransactionAttribute findTransactionAttribute(Method method) {
    return determineTransactionAttribute(method);
}
```

The method `org.springframework.transaction.annotation.AnnotationTransactionAttributeSource#determineTransactionAttribute`
finds the transaction attribute of a given method or class. It uses the configured `TransactionAnnotationParsers` to parse known annotations and convert them into Spring's
metadata attribute class. If no transaction attribute is found, it returns null. You can override this method to support custom annotations that carry transaction metadata.

```java
/**
 * Determine the transaction attribute for the given method or class.
 * <p>This implementation delegates to the configured {@link TransactionAnnotationParser TransactionAnnotationParsers}
 * for parsing known annotations into Spring's metadata attribute class.
 * Returns {@code null} if it's not transactional.
 * <p>Can be overridden to support custom annotations that carry transaction metadata.
 * @param element the annotated method or class
 * @return the configured transaction attribute, or {@code null} if none was found
 */
@Nullable
protected TransactionAttribute determineTransactionAttribute(AnnotatedElement element) {
    // Loop over all TransactionAnnotationParser instances
    for (TransactionAnnotationParser parser : this.annotationParsers) {
        // Parse the annotation to get the transaction attribute
        TransactionAttribute attr = parser.parseTransactionAnnotation(element);
        // If a transaction attribute is found, return it
        if (attr != null) {
            return attr;
        }
    }
    // If no transaction attribute is found, return null
    return null;
}
```

