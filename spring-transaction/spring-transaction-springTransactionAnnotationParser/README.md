## TransactionAnnotationParser

- [TransactionAnnotationParser](#transactionannotationparser)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [5. Main Implementations](#5-main-implementations)
    - [6. Class Diagram](#6-class-diagram)
    - [7. Best Practices](#7-best-practices)
    - [8. Source Code Analysis](#8-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `TransactionAnnotationParser` interface in the Spring Framework defines the standard way to parse transaction-related annotations. You can implement it to write your own logic for parsing a specific transaction annotation and convert the result into
Spring's internal transaction configuration object. This allows more flexible transaction management.

### 3. Main Features

1. **Parse transaction annotations**

    + The interface defines a method to parse transaction-related annotations. You can implement it to read the attribute values of the annotation.

2. **Support many annotations**

    + An implementation can support many transaction-related annotations, not only `@Transactional`. This makes the interface more flexible and general.

3. **Custom extension**

    + Implement the interface for your own needs and write your own parsing logic. This meets special transaction needs and extends the transaction management of the Spring Framework.

### 4. Interface Source

It parses known transaction annotation types. It has a default method that decides whether a given class is a candidate for transaction annotations, and a method that parses the transaction annotation on a given method or class and converts it into
Spring's transaction attribute object. Implementations of this interface can support specific transaction annotation types, such as Spring's `@Transactional`, JTA 1.2's
`javax.transaction.Transactional`, or EJB3's `javax.ejb.TransactionAttribute`.

```java
/**
 * Strategy interface for parsing known transaction annotation types.
 * {@link AnnotationTransactionAttributeSource} delegates to such parsers for supporting specific annotation types such as Spring's {@link Transactional}, JTA 1.2's {@link javax.transaction.Transactional} or EJB3's {@link javax.ejb.TransactionAttribute}.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see AnnotationTransactionAttributeSource
 * @see SpringTransactionAnnotationParser
 * @see Ejb3TransactionAnnotationParser
 * @see JtaTransactionAnnotationParser
 */
public interface TransactionAnnotationParser {

    /**
     * Determine whether the given class is a candidate for transaction attributes in the annotation format of this {@code TransactionAnnotationParser}.
     * <p>If this method returns {@code false}, the methods on the given class will not get traversed for {@code #parseTransactionAnnotation} introspection.
     * Returning {@code false} is an optimization for unaffected classes, whereas {@code true} simply means that the class needs to get fully introspected for each method on the given class.
     * @param targetClass the class to introspect
     * @return {@code false} if the class is known to have no transaction annotations at class or method level; {@code true} otherwise. The default implementation returns {@code true}, leading to regular introspection.
     * @since 5.2
     */
    default boolean isCandidateClass(Class<?> targetClass) {
        return true;
    }

    /**
     * Parse the transaction attribute for the given method or class, based on an annotation type understood by this parser.
     * <p>This essentially parses a known transaction annotation into Spring's metadata attribute class. Returns {@code null} if the method/class is not transactional.
     * @param element the annotated method or class
     * @return the configured transaction attribute, or {@code null} if none found
     * @see AnnotationTransactionAttributeSource#determineTransactionAttribute
     */
    @Nullable
    TransactionAttribute parseTransactionAnnotation(AnnotatedElement element);

}
```

### 5. Main Implementations

1. **SpringTransactionAnnotationParser**

    + Parses the `@Transactional` annotation of the Spring Framework. It reads the attributes of `@Transactional` and converts them into
      Spring's internal transaction configuration object. In Spring applications, transactions are usually declared with `@Transactional`, so this parser is used very often. It makes transaction management simpler and more flexible.

2. **Ejb3TransactionAnnotationParser**

    + Parses the `javax.ejb.TransactionAttribute` annotation from the EJB3 specification. EJB3 is part of the Java EE
      specification and is used to build enterprise applications. `Ejb3TransactionAnnotationParser` parses the `javax.ejb.TransactionAttribute`
      annotation and converts it into Spring's internal transaction configuration object. This lets Spring work with EJB3 and gives unified transaction management.

3. **JtaTransactionAnnotationParser**

    + Parses the `javax.transaction.Transactional` annotation defined in the JTA (Java Transaction API) specification. JTA is part of the Java platform and offers a set of
      APIs to manage distributed transactions. `JtaTransactionAnnotationParser` parses the `javax.transaction.Transactional` annotation and converts it into
      Spring's internal transaction configuration object. This lets Spring work with distributed transaction technologies and gives full transaction management support.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class Ejb3TransactionAnnotationParser
class JtaTransactionAnnotationParser
class SpringTransactionAnnotationParser
class TransactionAnnotationParser {
<<Interface>>

}

Ejb3TransactionAnnotationParser  ..>  TransactionAnnotationParser 
JtaTransactionAnnotationParser  ..>  TransactionAnnotationParser 
SpringTransactionAnnotationParser  ..>  TransactionAnnotationParser 
~~~

### 7. Best Practices

This example uses the `SpringTransactionAnnotationParser`
class to parse the transaction annotation on a method and convert it into a transaction attribute object. In this example, reflection gets the `insertScore`
method of the `ScoresServiceImpl` class. Then `SpringTransactionAnnotationParser` parses the transaction annotation on that method, and the result is printed to the console.

```java
public class SpringTransactionAnnotationParserDemo {

    public static void main(String[] args) throws NoSuchMethodException {
        // Get the insertScore method of ScoresServiceImpl
        Method insertScoreMethod = ScoresServiceImpl.class.getMethod("insertScore");
        // Create a SpringTransactionAnnotationParser instance
        SpringTransactionAnnotationParser parser = new SpringTransactionAnnotationParser();
        // Parse the transaction annotation on insertScore into a transaction attribute
        TransactionAttribute transactionAttribute = parser.parseTransactionAnnotation(insertScoreMethod);
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

### 8. Source Code Analysis

The method `org.springframework.transaction.annotation.SpringTransactionAnnotationParser#parseTransactionAnnotation(java.lang.reflect.AnnotatedElement)`
first uses `AnnotatedElementUtils` to find the merged attributes of the `@Transactional`
annotation on the element. If the annotation is found, it calls another method to parse it and returns the transaction attribute object. If not, it returns null.

```java

@Override
@Nullable
public TransactionAttribute parseTransactionAnnotation(AnnotatedElement element) {
    // Find the merged attributes of the @Transactional annotation on the element
    AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
            element, Transactional.class, false, false);
    // If the attributes are not null, parse the transaction annotation and return the transaction attribute object
    if (attributes != null) {
        return parseTransactionAnnotation(attributes);
    }
    // If the attributes are null, return null
    else {
        return null;
    }
}
```

The method `org.springframework.transaction.annotation.SpringTransactionAnnotationParser#parseTransactionAnnotation(org.springframework.core.annotation.AnnotationAttributes)`
parses the given annotation attributes and converts them into a transaction attribute object. From the annotation attributes, it sets the propagation behavior, isolation level, timeout, read-only flag, qualifier, labels, and rollback rules of the transaction. At the end, it returns a rule-based transaction attribute object.

```java
protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {
    // Create a rule-based transaction attribute object
    RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();

    // Set the propagation behavior
    Propagation propagation = attributes.getEnum("propagation");
    rbta.setPropagationBehavior(propagation.value());
    // Set the isolation level
    Isolation isolation = attributes.getEnum("isolation");
    rbta.setIsolationLevel(isolation.value());

    // Set the timeout
    rbta.setTimeout(attributes.getNumber("timeout").intValue());
    // Set the timeout string
    String timeoutString = attributes.getString("timeoutString");
    // Check that the timeout and the timeout string are not both set
    Assert.isTrue(!StringUtils.hasText(timeoutString) || rbta.getTimeout() < 0,
            "Specify 'timeout' or 'timeoutString', not both");
    rbta.setTimeoutString(timeoutString);

    // Set whether the transaction is read-only
    rbta.setReadOnly(attributes.getBoolean("readOnly"));
    // Set the qualifier
    rbta.setQualifier(attributes.getString("value"));
    // Set the labels
    rbta.setLabels(Arrays.asList(attributes.getStringArray("label")));

    // Parse the rollback rules
    List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
    // Parse the exception classes that trigger a rollback
    for (Class<?> rbRule : attributes.getClassArray("rollbackFor")) {
        rollbackRules.add(new RollbackRuleAttribute(rbRule));
    }
    // Parse the exception class names that trigger a rollback
    for (String rbRule : attributes.getStringArray("rollbackForClassName")) {
        rollbackRules.add(new RollbackRuleAttribute(rbRule));
    }
    // Parse the exception classes that do not trigger a rollback
    for (Class<?> rbRule : attributes.getClassArray("noRollbackFor")) {
        rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
    }
    // Parse the exception class names that do not trigger a rollback
    for (String rbRule : attributes.getStringArray("noRollbackForClassName")) {
        rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
    }
    // Set the rollback rules
    rbta.setRollbackRules(rollbackRules);

    // Return the parsed transaction attribute object
    return rbta;
}
```