## TransactionInterceptor

- [TransactionInterceptor](#transactioninterceptor)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Best Practices](#4-best-practices)
    - [5. Source Code Analysis](#5-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `TransactionInterceptor` class is a core part of Spring
for declarative transaction management. It intercepts method calls and, based on the transaction attributes (such as the propagation behavior and isolation level), controls when a transaction begins, commits, and rolls back. This keeps the transaction consistent and complete while the method runs.

### 3. Main Features

1. **Get the transaction attributes**

    + Get the transaction attributes (such as the propagation behavior and isolation level) from the metadata of the method or class.

2. **Transaction manager decision**

    + From the transaction attributes and the current transaction context (for example, whether an active transaction exists), decide whether to create a new transaction, join an existing one, or run without a transaction.

3. **Transaction control**

    + Control the start, commit, and rollback of the transaction through the `TransactionManager`.

4. **Exception handling**

    + If an exception is caught while the method runs, decide whether to roll back the transaction from the transaction attribute configuration.

### 4. Best Practices

This example creates a database data source with `SimpleDriverDataSource` and uses `DataSourceTransactionManager`
for transaction management. It runs SQL with `JdbcTemplate`, and uses `AnnotationTransactionAttributeSource` and `TransactionInterceptor`
to define the transaction attributes and intercept method calls, so the method runs inside a transaction. Finally, it creates a proxy object with `ProxyFactory` and calls its method, so the method runs under declarative transaction control.

```java
public class TransactionInterceptorDemo {

    public static void main(String[] args) throws SQLException {
        // Database URL, format: jdbc:driver-name://host:port/database-name
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // Database username
        String username = "root";
        // Database password
        String password = "123456";

        // Create a SimpleDriverDataSource to manage the data source
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(new Driver(), url, username, password);
        // Create a DataSourceTransactionManager to manage transactions
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        // Create a JdbcTemplate to run SQL statements
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // Create an AnnotationTransactionAttributeSource to get transaction attributes
        AnnotationTransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();

        // Create a TransactionInterceptor to intercept method calls and manage transactions
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        transactionInterceptor.setTransactionAttributeSource(transactionAttributeSource);
        transactionInterceptor.setTransactionManager(transactionManager);

        // Create a BeanFactoryTransactionAttributeSourceAdvisor to set up the transaction interceptor
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(transactionAttributeSource);
        advisor.setAdvice(transactionInterceptor);

        // Create a ProxyFactory to create the proxy
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addAdvisor(advisor);
        proxyFactory.setTarget(new ScoresServiceImpl(jdbcTemplate));

        // Get the proxy and call its method
        ScoresService scoresService = (ScoresService) proxyFactory.getProxy();
        scoresService.insertScore();
    }
}
```

### 5. Source Code Analysis

In the method `org.springframework.transaction.interceptor.TransactionInterceptor#invoke`, the `TransactionInterceptor`
class implements the `invoke` method of `MethodInterceptor`. This is the core of how Spring AOP
handles transactions. It intercepts method calls and, based on the transaction configuration, makes sure a transaction is opened before the method runs and is committed or rolled back after it, depending on the result. This keeps data operations consistent and complete.

```java

@Override
@Nullable
public Object invoke(MethodInvocation invocation) throws Throwable {
    // Work out the target class: may be {@code null}.
    // The TransactionAttributeSource should be passed the target class as well as the method, which may be from an interface.
    Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

    // Adapt to TransactionAspectSupport's invokeWithinTransaction...
    return invokeWithinTransaction(invocation.getMethod(), targetClass, new CoroutinesInvocationCallback() {

        @Override
        @Nullable
        public Object proceedWithInvocation() throws Throwable {
            return invocation.proceed();
        }

        @Override
        public Object getTarget() {
            return invocation.getThis();
        }

        @Override
        public Object[] getArguments() {
            return invocation.getArguments();
        }
    });
}
```

The method `org.springframework.transaction.interceptor.TransactionAspectSupport#invokeWithinTransaction`
uses the transaction attribute of the method to decide whether transaction management is needed. Then it picks a suitable transaction manager to run the transaction operations: begin, commit, and rollback. If the method throws an exception, it rolls back according to the transaction status and rethrows the exception. When the method returns normally, it sets the rollback flag by the configured rollback rules and commits the transaction.

```java
/**
 * General delegate for around-advice-based subclasses, delegating to several other template methods on this class.
 * Able to handle {@link CallbackPreferringPlatformTransactionManager} as well as regular {@link PlatformTransactionManager} implementations
 * and {@link ReactiveTransactionManager} implementations for reactive return types.
 *
 * @param method the Method being invoked
 * @param targetClass the target class that we're invoking the method on
 * @param invocation the callback to use for proceeding with the target invocation
 * @return the return value of the method, if any
 * @throws Throwable propagated from the target invocation
 */
@Nullable
protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass,
                                         final InvocationCallback invocation) throws Throwable {

    // If the transaction attribute is null, the method is non-transactional.
    TransactionAttributeSource tas = getTransactionAttributeSource();
    final TransactionAttribute txAttr = (tas != null ? tas.getTransactionAttribute(method, targetClass) : null);
    final TransactionManager tm = determineTransactionManager(txAttr);

    // If a reactive adapter is present and tm is a ReactiveTransactionManager, then...
    if (this.reactiveAdapterRegistry != null && tm instanceof ReactiveTransactionManager) {
        // ... [code omitted for brevity]
    }

    // Cast tm to PlatformTransactionManager
    PlatformTransactionManager ptm = asPlatformTransactionManager(tm);
    final String joinpointIdentification = methodIdentification(method, targetClass, txAttr);

    // If txAttr is null or ptm is not a CallbackPreferringPlatformTransactionManager
    if (txAttr == null || !(ptm instanceof CallbackPreferringPlatformTransactionManager)) {
        // Standard transaction demarcation with getTransaction and commit/rollback calls.
        TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);

        Object retVal;
        try {
            // This is an around advice: invoke the next interceptor in the chain.
            // This will normally result in a target object being invoked.
            retVal = invocation.proceedWithInvocation();
        } catch (Throwable ex) {
            // Target invocation exception
            completeTransactionAfterThrowing(txInfo, ex);
            throw ex;
        } finally {
            cleanupTransactionInfo(txInfo);
        }

        // If retVal is not null, Vavr is present, and retVal is a Vavr Try
        if (retVal != null && vavrPresent && VavrDelegate.isVavrTry(retVal)) {
            // Set rollback-only on Vavr failure according to the rollback rules...
            TransactionStatus status = txInfo.getTransactionStatus();
            if (status != null && txAttr != null) {
                retVal = VavrDelegate.evaluateTryFailure(retVal, txAttr, status);
            }
        }

        // Commit the transaction after the method returns normally
        commitTransactionAfterReturning(txInfo);
        return retVal;
    } else {
        // ... [code omitted for brevity]
    }
}
```

The method `org.springframework.transaction.interceptor.TransactionAspectSupport#createTransactionIfNecessary`
uses the method identification as the transaction name if the transaction attribute exists and has no name. Then, if both the transaction attribute and the transaction manager exist, it gets the transaction status from the transaction manager. If no transaction manager is configured, it logs a debug message that the transaction join point is skipped. Finally, it calls `prepareTransactionInfo`
to prepare and return a `TransactionInfo` object that holds the transaction information. It returns this object whether or not a transaction was created.

```java
/**
 * Create a transaction if necessary, based on the given TransactionAttribute.
 * <p>Allows callers to perform custom TransactionAttribute lookups through the TransactionAttributeSource.
 *
 * @param tm the transaction manager (may be {@code null})
 * @param txAttr the transaction attribute (may be {@code null})
 * @param joinpointIdentification the fully qualified method name (used for monitoring and logging purposes)
 * @return a TransactionInfo object, whether or not a transaction was created.
 * The {@code hasTransaction()} method on TransactionInfo can be used to tell if there was a transaction created.
 * @see #getTransactionAttributeSource()
 */
@SuppressWarnings("serial")
protected TransactionInfo createTransactionIfNecessary(@Nullable PlatformTransactionManager tm,
                                                       @Nullable TransactionAttribute txAttr, final String joinpointIdentification) {

    // If the transaction attribute is not null and the transaction name is null, use the method identification as the transaction name.
    if (txAttr != null && txAttr.getName() == null) {
        txAttr = new DelegatingTransactionAttribute(txAttr) {
            @Override
            public String getName() {
                return joinpointIdentification;
            }
        };
    }

    TransactionStatus status = null;
    if (txAttr != null) {
        if (tm != null) {
            // If the transaction attribute is not null and the transaction manager is not null, get the transaction status from the transaction manager.
            status = tm.getTransaction(txAttr);
        } else {
            // If the transaction manager is null, log a debug message that the transaction join point is skipped.
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping transactional joinpoint [" + joinpointIdentification +
                        "] because no transaction manager has been configured");
            }
        }
    }
    // Prepare and return the TransactionInfo object.
    return prepareTransactionInfo(tm, txAttr, joinpointIdentification, status);
}
```

The method `org.springframework.transaction.interceptor.TransactionAspectSupport#prepareTransactionInfo`
creates and prepares a `TransactionInfo` object from the given transaction attribute and status object. If the transaction attribute is not
null, the method needs a transaction. It logs the transaction that was obtained in the debug log, and sets the transaction status with the `newTransactionStatus` method. If the transaction attribute is
null, the method does not need a transaction. It only creates a `TransactionInfo`
object to keep the thread-local variables intact. Whether or not a new transaction was created, it always binds the `TransactionInfo` to the thread, so the `TransactionInfo` stack is managed correctly.

```java
/**
 * Prepare a TransactionInfo for the given transaction attribute and status object.
 * @param tm the transaction manager (may be {@code null})
 * @param txAttr the transaction attribute (may be {@code null})
 * @param joinpointIdentification the fully qualified method name (used for monitoring and logging purposes)
 * @param status the TransactionStatus for the current transaction
 * @return the prepared TransactionInfo object
 */
protected TransactionInfo prepareTransactionInfo(@Nullable PlatformTransactionManager tm,
                                                 @Nullable TransactionAttribute txAttr, String joinpointIdentification,
                                                 @Nullable TransactionStatus status) {

    // Create a TransactionInfo object
    TransactionInfo txInfo = new TransactionInfo(tm, txAttr, joinpointIdentification);
    if (txAttr != null) {
        // If the transaction attribute is not null, the method needs a transaction
        if (logger.isTraceEnabled()) {
            logger.trace("Getting transaction for [" + txInfo.getJoinpointIdentification() + "]");
        }
        // The transaction manager will flag an error if an incompatible transaction already exists
        txInfo.newTransactionStatus(status);
    } else {
        // If the transaction attribute is null, the method needs no transaction, so only create a TransactionInfo to keep the thread-local variables intact
        if (logger.isTraceEnabled()) {
            logger.trace("No need to create transaction for [" + joinpointIdentification +
                    "]: This method is not transactional.");
        }
    }

    // We always bind the TransactionInfo to the thread, even if we did not create a new transaction here.
    // This guarantees that the TransactionInfo stack will be managed correctly even if no transaction was created by this aspect.
    txInfo.bindToThread();
    return txInfo;
}
```

The method `org.springframework.transaction.interceptor.TransactionAspectSupport.TransactionInfo#bindToThread`
binds the current `TransactionInfo` object to the thread context. Before binding, it saves the old `TransactionInfo`
object of the current thread, so it can restore the saved old `TransactionInfo` object after the transaction completes.

```java
/**
 * Bind the current TransactionInfo object to the thread context.
 * Before binding, the old TransactionInfo object of the current thread is saved,
 * and it is restored after the transaction completes.
 */
private void bindToThread() {
    // Expose the current TransactionStatus, preserving any existing TransactionStatus for restoration after this transaction is complete.
    this.oldTransactionInfo = transactionInfoHolder.get();
    transactionInfoHolder.set(this);
}
```

The method `org.springframework.transaction.interceptor.TransactionAspectSupport#commitTransactionAfterReturning`
gets the current transaction information, including the transaction status and the transaction manager, from the `txInfo` parameter. Then it calls the `commit` method of the transaction manager to commit the transaction.

```java
/**
 * Execute after successful completion of call, but not after an exception was handled.
 * Do nothing if we didn't create a transaction.
 *
 * @param txInfo information about the current transaction
 */
protected void commitTransactionAfterReturning(@Nullable TransactionInfo txInfo) {
    // If txInfo is not null and the transaction status is not null, commit the transaction
    if (txInfo != null && txInfo.getTransactionStatus() != null) {
        // If trace logging is enabled, log that the transaction is being completed
        if (logger.isTraceEnabled()) {
            logger.trace("Completing transaction for [" + txInfo.getJoinpointIdentification() + "]");
        }
        // Call the commit method of the transaction manager to commit the transaction
        txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
    }
}
```

The method `org.springframework.transaction.interceptor.TransactionAspectSupport#completeTransactionAfterThrowing`
commits or rolls back the transaction, depending on the transaction attribute configuration. If the transaction attribute says to roll back on this exception, it calls the rollback method of the transaction manager. Otherwise, it calls the commit method of the transaction manager.

```java
/**
 * Handle a throwable, completing the transaction.
 * We may commit or roll back, depending on the configuration.
 *
 * @param txInfo information about the current transaction
 * @param ex the throwable encountered
 */
protected void completeTransactionAfterThrowing(@Nullable TransactionInfo txInfo, Throwable ex) {
    // If txInfo is not null and the transaction status is not null, complete the transaction
    if (txInfo != null && txInfo.getTransactionStatus() != null) {
        // If trace logging is enabled, log that the transaction is being completed after the exception, together with the exception
        if (logger.isTraceEnabled()) {
            logger.trace("Completing transaction for [" + txInfo.getJoinpointIdentification() +
                    "] after exception: " + ex);
        }
        // If the transaction attribute is not null and it says to roll back on this exception
        if (txInfo.transactionAttribute != null && txInfo.transactionAttribute.rollbackOn(ex)) {
            try {
                // Roll back the transaction with the transaction manager
                txInfo.getTransactionManager().rollback(txInfo.getTransactionStatus());
            } catch (TransactionSystemException ex2) {
                // If an exception happens during the rollback, log the error and throw the exception
                logger.error("Application exception overridden by rollback exception", ex);
                ex2.initApplicationException(ex);
                throw ex2;
            } catch (RuntimeException | Error ex2) {
                // If a runtime exception or error happens during the rollback, log the error and throw the exception
                logger.error("Application exception overridden by rollback exception", ex);
                throw ex2;
            }
        } else {
            // If the transaction should not be rolled back on this exception, go on to commit it
            // If the transaction status is RollbackOnly, the transaction will still be rolled back in the end
            try {
                txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
            } catch (TransactionSystemException ex2) {
                // If an exception happens during the commit, log the error and throw the exception
                logger.error("Application exception overridden by commit exception", ex);
                ex2.initApplicationException(ex);
                throw ex2;
            } catch (RuntimeException | Error ex2) {
                // If a runtime exception or error happens during the commit, log the error and throw the exception
                logger.error("Application exception overridden by commit exception", ex);
                throw ex2;
            }
        }
    }
}
```