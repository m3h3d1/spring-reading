## TransactionDefinition

- [TransactionDefinition](#transactiondefinition)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [5. Main Implementations](#5-main-implementations)
    - [6. Class Diagram](#6-class-diagram)
    - [7. Best Practices](#7-best-practices)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https//juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https//github.com/xuchengsheng/spring-reading)

### 2. Description

The `TransactionDefinition` interface in Spring defines transaction properties. It has the propagation behavior, isolation level, timeout, read-only flag, and so on. By configuring these properties, you can flexibly control database transaction behavior in your application.

### 3. Main Features

1. **Define the transaction propagation behavior**

    + Defines the propagation of the transaction: how a method should treat an existing transaction when it is called. For example, whether to join the existing transaction or create a new one.

2. **Set the transaction isolation level**

    + Defines the isolation level of the transaction: how much transactions are isolated from each other. Different isolation levels solve different concurrency problems, such as dirty reads, non-repeatable reads, and phantom reads.

3. **Set the transaction timeout**

    + Sets the timeout of the transaction: how long the transaction must finish in. If it does not finish in time, it is rolled back automatically.

4. **Configure the read-only flag**

    + Sets whether the transaction is read-only. A read-only transaction can improve database performance, because the database can skip some read lock operations.

### 4. Interface Source

The `TransactionDefinition` interface defines transaction properties that follow the Spring specification. Its propagation behavior definitions are based on those of EJB CMT
attributes. The interface has properties such as the propagation behavior, isolation level, timeout, and read-only flag. It gives default values and static builder methods to make it easy to create transaction definition objects.

```java
/**
 * Interface that defines Spring-compliant transaction properties.
 * Based on the propagation behavior definitions analogous to EJB CMT attributes.
 *
 * <p>Note that isolation level and timeout settings will not get applied unless an actual new transaction gets started.
 * As only {@link #PROPAGATION_REQUIRED}, {@link #PROPAGATION_REQUIRES_NEW} and {@link #PROPAGATION_NESTED} can cause that, it usually doesn't make sense to
 * specify those settings in other cases.
 * Furthermore, be aware that not all transaction managers will support those advanced features and thus might throw corresponding exceptions when given non-default values.
 *
 * <p>The {@link #isReadOnly() read-only flag} applies to any transaction context, whether backed by an actual resource transaction or operating non-transactionally at the resource level.
 * In the latter case, the flag will only apply to managed resources within the application, such as a Hibernate {@code Session}.
 *
 * @author Juergen Hoeller
 * @since 08.05.2003
 * @see PlatformTransactionManager#getTransaction(TransactionDefinition)
 * @see org.springframework.transaction.support.DefaultTransactionDefinition
 * @see org.springframework.transaction.interceptor.TransactionAttribute
 */
public interface TransactionDefinition {

    /**
     * Support a current transaction; create a new one if none exists.
     * Analogous to the EJB transaction attribute of the same name.
     * <p>This is typically the default setting of a transaction definition,
     * and typically defines a transaction synchronization scope.
     */
    int PROPAGATION_REQUIRED = 0;

    /**
     * Support a current transaction; execute non-transactionally if none exists.
     * Analogous to the EJB transaction attribute of the same name.
     * <p><b>NOTE:</b> For transaction managers with transaction synchronization,
     * {@code PROPAGATION_SUPPORTS} is slightly different from no transaction at all,
     * as it defines a transaction scope that synchronization might apply to.
     * As a consequence, the same resources (a JDBC {@code Connection}, a Hibernate {@code Session}, etc) will be shared for the entire specified scope.
     * Note that the exact behavior depends on the actual synchronization configuration of the transaction manager!
     * <p>In general, use {@code PROPAGATION_SUPPORTS} with care!
     * In particular, do not rely on {@code PROPAGATION_REQUIRED} or {@code PROPAGATION_REQUIRES_NEW} within a {@code PROPAGATION_SUPPORTS} scope
     * (which may lead to synchronization conflicts at runtime).
     * If such nesting is unavoidable, make sure to configure your transaction manager appropriately (typically switching to "synchronization on actual transaction").
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#SYNCHRONIZATION_ON_ACTUAL_TRANSACTION
     */
    int PROPAGATION_SUPPORTS = 1;

    /**
     * Support a current transaction; throw an exception if none exists.
     * Analogous to the EJB transaction attribute of the same name.
     * <p>Transaction synchronization within a {@code PROPAGATION_MANDATORY} scope will always be driven by the surrounding transaction.
     */
    int PROPAGATION_MANDATORY = 2;

    /**
     * Create a new transaction, suspending the current transaction if one exists.
     * Analogous to the EJB transaction attribute of the same name.
     * <p><b>NOTE:</b> Actual transaction suspension will not work out-of-the-box on all transaction managers.
     * This in particular applies to {@link org.springframework.transaction.jta.JtaTransactionManager},
     * which requires the {@code javax.transaction.TransactionManager} to be made available to it (which is server-specific in standard Java EE).
     * <p>A {@code PROPAGATION_REQUIRES_NEW} scope always defines its own transaction synchronizations.
     * Existing synchronizations will be suspended and resumed appropriately.
     * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
     */
    int PROPAGATION_REQUIRES_NEW = 3;

    /**
     * Do not support a current transaction; rather always execute non-transactionally.
     * Analogous to the EJB transaction attribute of the same name.
     * <p><b>NOTE:</b> Actual transaction suspension will not work out-of-the-box on all transaction managers.
     * This in particular applies to {@link org.springframework.transaction.jta.JtaTransactionManager},
     * which requires the {@code javax.transaction.TransactionManager} to be made available to it (which is server-specific in standard Java EE).
     * <p>Note that transaction synchronization is not available within a {@code PROPAGATION_NOT_SUPPORTED} scope.
     * Existing synchronizations will be suspended and resumed appropriately.
     * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
     */
    int PROPAGATION_NOT_SUPPORTED = 4;

    /**
     * Do not support a current transaction; throw an exception if a current transaction exists.
     * Analogous to the EJB transaction attribute of the same name.
     * <p>Note that transaction synchronization is not available within a {@code PROPAGATION_NEVER} scope.
     */
    int PROPAGATION_NEVER = 5;

    /**
     * Execute within a nested transaction if a current transaction exists, behave like {@link #PROPAGATION_REQUIRED} otherwise.
     * There is no analogous feature in EJB.
     * <p><b>NOTE:</b> Actual creation of a nested transaction will only work on specific transaction managers.
     * Out of the box, this only applies to the JDBC {@link org.springframework.jdbc.datasource.DataSourceTransactionManager}
     * when working on a JDBC 3.0 driver.
     * Some JTA providers might support nested transactions as well.
     * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
     */
    int PROPAGATION_NESTED = 6;

    /**
     * Use the default isolation level of the underlying datastore.
     * All other levels correspond to the JDBC isolation levels.
     * @see java.sql.Connection
     */
    int ISOLATION_DEFAULT = -1;

    /**
     * Indicates that dirty reads, non-repeatable reads and phantom reads can occur.
     * <p>This level allows a row changed by one transaction to be read by another transaction before any changes in that row have been committed (a "dirty read").
     * If any of the changes are rolled back, the second transaction will have retrieved an invalid row.
     * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
     */
    int ISOLATION_READ_UNCOMMITTED = 1;

    /**
     * Indicates that dirty reads are prevented; non-repeatable reads and phantom reads can occur.
     * <p>This level only prohibits a transaction from reading a row with uncommitted changes in it.
     * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
     */
    int ISOLATION_READ_COMMITTED = 2;

    /**
     * Indicates that dirty reads and non-repeatable reads are prevented; phantom reads can occur.
     * <p>This level prohibits a transaction from reading a row with uncommitted changes in it, and it also prohibits the situation where
     * one transaction reads a row, a second transaction alters the row, and the first transaction rereads the row, getting different values the second time (a "non-repeatable read").
     * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
     */
    int ISOLATION_REPEATABLE_READ = 4;

    /**
     * Indicates that dirty reads, non-repeatable reads and phantom reads are prevented.
     * <p>This level includes the prohibitions in {@link #ISOLATION_REPEATABLE_READ} and further prohibits the situation where
     * one transaction reads all rows that satisfy a {@code WHERE} condition, a second transaction inserts a row that satisfies that {@code WHERE} condition,
     * and the first transaction rereads for the same condition, retrieving the additional "phantom" row in the second read.
     * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
     */
    int ISOLATION_SERIALIZABLE = 8;

    /**
     * Use the default timeout of the underlying transaction system, or none if timeouts are not supported.
     */
    int TIMEOUT_DEFAULT = -1;

    /**
     * Return the propagation behavior.
     * <p>Must return one of the {@code PROPAGATION_XXX} constants defined on {@link TransactionDefinition this interface}.
     * <p>The default is {@link #PROPAGATION_REQUIRED}.
     * @return the propagation behavior
     * @see #PROPAGATION_REQUIRED
     * @see org.springframework.transaction.support.TransactionSynchronizationManager#isActualTransactionActive()
     */
    default int getPropagationBehavior() {
        return PROPAGATION_REQUIRED;
    }

    /**
     * Return the isolation level.
     * <p>Must return one of the {@code ISOLATION_XXX} constants defined on {@link TransactionDefinition this interface}.
     * Those constants are designed to match the values of the same constants on {@link java.sql.Connection}.
     * <p>Exclusively designed for use with {@link #PROPAGATION_REQUIRED} or {@link #PROPAGATION_REQUIRES_NEW}
     * since it only applies to newly started transactions.
     * Consider switching the "validateExistingTransactions" flag to "true" on your transaction manager if you'd like isolation level declarations to get rejected when participating in an existing transaction with a different isolation level.
     * <p>The default is {@link #ISOLATION_DEFAULT}.
     * Note that a transaction manager that does not support custom isolation levels will throw an exception when given any other level than {@link #ISOLATION_DEFAULT}.
     * @return the isolation level
     * @see #ISOLATION_DEFAULT
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
     */
    default int getIsolationLevel() {
        return ISOLATION_DEFAULT;
    }

    /**
     * Return the transaction timeout.
     * <p>Must return a number of seconds, or {@link #TIMEOUT_DEFAULT}.
     * <p>Exclusively designed for use with {@link #PROPAGATION_REQUIRED} or {@link #PROPAGATION_REQUIRES_NEW}
     * since it only applies to newly started transactions.
     * <p>Note that a transaction manager that does not support timeouts will throw an exception when given any other timeout than {@link #TIMEOUT_DEFAULT}.
     * <p>The default is {@link #TIMEOUT_DEFAULT}.
     * @return the transaction timeout
     */
    default int getTimeout() {
        return TIMEOUT_DEFAULT;
    }

    /**
     * Return whether to optimize as a read-only transaction.
     * <p>The read-only flag applies to any transaction context, whether backed by an actual resource transaction ({@link #PROPAGATION_REQUIRED}/
     * {@link #PROPAGATION_REQUIRES_NEW}) or operating non-transactionally at the resource level ({@link #PROPAGATION_SUPPORTS}).
     * In the latter case, the flag will only apply to managed resources within the application, such as a Hibernate {@code Session}.
     * <p>This just serves as a hint for the actual transaction subsystem;
     * it will <i>not necessarily</i> cause failure of write access attempts.
     * A transaction manager which cannot interpret the read-only hint will <i>not</i> throw an exception when asked for a read-only transaction.
     * @return {@code true} if the transaction is to be optimized as read-only ({@code false} by default)
     * @see org.springframework.transaction.support.TransactionSynchronization#beforeCommit(boolean)
     * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
     */
    default boolean isReadOnly() {
        return false;
    }

    /**
     * Return the name of this transaction. Can be {@code null}.
     * <p>This will be used as the transaction name to be shown in a transaction monitor, if applicable (for example, WebLogic's).
     * <p>In case of Spring's declarative transactions, the exposed name will be the {@code fully-qualified class name + "." + method name} (by default).
     * @return the name of this transaction ({@code null} by default)
     * @see org.springframework.transaction.interceptor.TransactionAspectSupport
     * @see org.springframework.transaction.support.TransactionSynchronizationManager#getCurrentTransactionName()
     */
    @Nullable
    default String getName() {
        return null;
    }

    /**
     * Return an unmodifiable {@code TransactionDefinition} with defaults.
     * <p>For customization purposes, use the modifiable {@link org.springframework.transaction.support.DefaultTransactionDefinition} instead.
     * @since 5.2
     */
    static TransactionDefinition withDefaults() {
        return StaticTransactionDefinition.INSTANCE;
    }
}
```

### 5. Main Implementations

1. **DefaultTransactionDefinition**

    - Spring
      The default implementation class in Spring that defines the basic transaction properties. It lets us set the propagation behavior, isolation level, timeout, read-only flag, and so on. By giving default values, it makes setting transaction properties simpler. It also has a static method `withDefaults()`
      that creates a transaction definition object with default properties.

2. **DefaultTransactionAttribute**

    - Spring
      The default implementation class in Spring that defines transaction properties. It holds the propagation behavior, isolation level, timeout, read-only flag, and so on, and has methods to change and read them. As the default implementation of the `TransactionAttribute`
      interface, it makes it easy to set and get the transaction attributes at method or class level.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class DefaultTransactionAttribute
class DefaultTransactionDefinition
class TransactionAttribute {
<<Interface>>

}
class TransactionDefinition {
<<Interface>>

}

DefaultTransactionAttribute  -->  DefaultTransactionDefinition 
DefaultTransactionAttribute  ..>  TransactionAttribute 
DefaultTransactionDefinition  ..>  TransactionDefinition 
TransactionAttribute  -->  TransactionDefinition 
~~~

### 7. Best Practices

This example uses the `DefaultTransactionDefinition` class to define transaction properties. It sets the propagation behavior, isolation level, timeout, read-only flag, and transaction name, then prints their values.

```java
public class TransactionDefinitionDemo {

    public static void main(String[] args) {
        // Create a DefaultTransactionDefinition instance
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();

        // Set the propagation behavior to PROPAGATION_REQUIRES_NEW
        transactionDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
        System.out.println("Propagation Behavior: " + transactionDefinition.getPropagationBehavior());

        // Set the isolation level to ISOLATION_REPEATABLE_READ
        transactionDefinition.setIsolationLevel(DefaultTransactionDefinition.ISOLATION_REPEATABLE_READ);
        System.out.println("Isolation Level: " + transactionDefinition.getIsolationLevel());

        // Set the transaction timeout to 30 seconds
        transactionDefinition.setTimeout(30);
        System.out.println("Timeout: " + transactionDefinition.getTimeout());

        // Set the transaction to read-only
        transactionDefinition.setReadOnly(true);
        System.out.println("Is Read Only: " + transactionDefinition.isReadOnly());

        // Set the transaction name to "DemoTransaction"
        transactionDefinition.setName("DemoTransaction");
        System.out.println("Transaction Name: " + transactionDefinition.getName());
    }
}
```

In the result, the transaction properties set with the `DefaultTransactionDefinition` class are: the propagation behavior is `PROPAGATION_REQUIRES_NEW`
, the isolation level is `ISOLATION_REPEATABLE_READ`, the timeout is 30 seconds, the transaction is read-only, and the transaction name is "DemoTransaction".

```java
Propagation Behavior:3
Isolation Level:4
Timeout:30
Is Read
Only:true
Transaction Name:DemoTransaction
```