## PlatformTransactionManager

- [PlatformTransactionManager](#platformtransactionmanager)
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

The `PlatformTransactionManager` interface is the core Spring
interface for managing transactions. It defines unified transaction methods: start, commit, roll back, and get the current transaction status. It gives one transaction management interface to different data access technologies, so you can switch between them smoothly.

### 3. Main Features

1. **Start a transaction**

    + `PlatformTransactionManager` lets you start a new transaction or get an existing one with `getTransaction(TransactionDefinition definition)`
      . The `TransactionDefinition` object defines transaction properties such as the propagation behavior, isolation level, timeout, and read-only flag.

2. **Commit a transaction**

    + If all operations in the transaction finish successfully, you can commit it with `commit(TransactionStatus status)`. This makes all changes in the transaction permanent.

3. **Roll back a transaction**

    + If any exception or error happens in the transaction, you can roll it back with `rollback(TransactionStatus status)`
      . This undoes all changes in the transaction and returns the database to the state before the transaction started.

4. **Get the transaction status**

    + The `TransactionStatus` object gives information about the current transaction, such as whether it is new, completed, or rolled back. You can use this information for conditional logic during transaction handling.

### 4. Interface Source

The `PlatformTransactionManager` interface is the core interface of Spring's imperative transaction infrastructure. It has three main functions: `getTransaction()`
gets the current active transaction or creates a new one, `commit()` commits the given transaction, and `rollback()`
rolls back the given transaction. With these, an application can manage transactions effectively and keep data consistent and complete.

```java
/**
 * This is the central interface in Spring's imperative transaction infrastructure.
 * Applications can use it directly, but it is not primarily meant as an API:
 * Typically, applications will work with either TransactionTemplate or declarative transaction demarcation through AOP.
 *
 * <p>For implementors, it is recommended to derive from the provided AbstractPlatformTransactionManager class,
 * which pre-implements the defined propagation behavior and takes care of transaction synchronization handling. Subclasses have to implement template methods for specific states of the underlying transaction,
 * for example: begin, suspend, resume, commit.
 *
 * <p>The default implementations of this strategy interface are JtaTransactionManager and DataSourceTransactionManager,
 * which can serve as an implementation guide for other transaction strategies.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16.05.2003
 * @see org.springframework.transaction.support.TransactionTemplate
 * @see org.springframework.transaction.interceptor.TransactionInterceptor
 * @see org.springframework.transaction.ReactiveTransactionManager
 */
public interface PlatformTransactionManager extends TransactionManager {

    /**
     * Return a currently active transaction or create a new one, according to the specified propagation behavior.
     * <p>Note that parameters like isolation level or timeout will only be applied to new transactions,
     * and thus be ignored when participating in an active transaction.
     * <p>Furthermore, not all transaction definition settings will be supported by every transaction manager:
     * A proper transaction manager implementation should throw an exception when unsupported settings are encountered.
     * <p>An exception to the above rule is the read-only flag,
     * which should be ignored if no explicit read-only mode is supported. Essentially, the read-only flag
     * is just a hint for potential optimization.
     * @param definition the TransactionDefinition instance (can be null for defaults),
     * describing propagation behavior, isolation level, timeout etc.
     * @return transaction status object representing the new or current transaction
     * @throws TransactionException in case of lookup, creation, or system errors
     * @throws IllegalTransactionStateException if the given transaction definition cannot be executed
     * (for example, if a currently active transaction is in conflict with the specified propagation behavior)
     * @see TransactionDefinition#getPropagationBehavior
     * @see TransactionDefinition#getIsolationLevel
     * @see TransactionDefinition#getTimeout
     * @see TransactionDefinition#isReadOnly
     */
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
            throws TransactionException;

    /**
     * Commit the given transaction, with regard to its status. If the transaction has been marked rollback-only programmatically, perform a rollback.
     * <p>If the transaction wasn't a new one, omit the commit for proper participation in the surrounding transaction.
     * If a previous transaction has been suspended to be able to create a new one, resume the previous transaction after committing the new one.
     * <p>Note that when the commit call completes, no matter if normally or throwing an exception, the transaction must be fully completed and cleaned up.
     * No rollback call should be expected in such a case.
     * <p>If this method throws an exception other than a TransactionException,
     * then some before-commit error caused the commit attempt to fail. For example,
     * an attempt to flush changes to the database may be made before commit,
     * and the transaction fails because of the resulting DataAccessException. In that case, the original exception is propagated to the caller of this commit method.
     * @param status object returned by the getTransaction method
     * @throws UnexpectedRollbackException in case of an unexpected rollback that the transaction coordinator initiated
     * @throws HeuristicCompletionException in case of a transaction failure caused by a heuristic decision on the side of the transaction coordinator
     * @throws TransactionSystemException in case of commit or system errors (typically caused by fundamental resource failures)
     * @throws IllegalTransactionStateException if the given transaction is already completed (that is, committed or rolled back)
     * @see TransactionStatus#setRollbackOnly
     */
    void commit(TransactionStatus status) throws TransactionException;

    /**
     * Perform a rollback of the given transaction.
     * <p>If the transaction wasn't a new one, just set it rollback-only for proper participation in the surrounding transaction.
     * If a previous transaction has been suspended to be able to create a new one, resume the previous transaction after rolling back the new one.
     * <p><b>Do not call rollback on a transaction if commit threw an exception.</b>
     * The transaction will already have been completed and cleaned up when commit returns, even in case of a commit exception.
     * Consequently, a rollback call after commit failure will lead to an IllegalTransactionStateException.
     * @param status object returned by the getTransaction method
     * @throws TransactionSystemException in case of rollback or system errors (typically caused by fundamental resource failures)
     * @throws IllegalTransactionStateException if the given transaction is already completed (that is, committed or rolled back)
     */
    void rollback(TransactionStatus status) throws TransactionException;

}
```

### 5. Main Implementations

1. **DataSourceTransactionManager**

    + For JDBC-based transaction management.

### 6. Class Diagram

~~~mermaid
classDiagram
direction BT
class AbstractPlatformTransactionManager
class DataSourceTransactionManager
class InitializingBean {
<<Interface>>

}
class PlatformTransactionManager {
<<Interface>>

}
class ResourceTransactionManager {
<<Interface>>

}
class TransactionManager {
<<Interface>>

}

AbstractPlatformTransactionManager  ..>  PlatformTransactionManager 
DataSourceTransactionManager  -->  AbstractPlatformTransactionManager 
DataSourceTransactionManager  ..>  InitializingBean 
DataSourceTransactionManager  ..>  ResourceTransactionManager 
PlatformTransactionManager  -->  TransactionManager 
ResourceTransactionManager  -->  PlatformTransactionManager 
~~~

### 7. Best Practices

This example uses Spring's transaction manager (`PlatformTransactionManager`) and the JDBC template (`JdbcTemplate`
) to work with a database in a Java application. The code first sets the database connection settings and creates the data source and the transaction manager. Then it uses `JdbcTemplate`
to run an SQL insert and puts a randomly generated score into the database. When the insert runs, a transaction is started, and it is committed if the operation succeeds. If an exception happens, the transaction is rolled back to keep the data consistent. At the end, it prints the number of affected rows.

```java
public class PlatformTransactionManagerDemo {

    private static PlatformTransactionManager transactionManager;
    private static JdbcTemplate jdbcTemplate;

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
        transactionManager = new DataSourceTransactionManager(dataSource);
        // Create a JdbcTemplate to run SQL statements
        jdbcTemplate = new JdbcTemplate(dataSource);

        insertScore();
    }

    private static void insertScore() {
        // Start a new transaction and return its status object
        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            long id = System.currentTimeMillis();
            int score = new Random().nextInt(100);
            // Insert a random score into the database
            int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
            // Simulate an exception to test transaction rollback
            // int i = 1 / 0;
            // Commit the transaction
            transactionManager.commit(transactionStatus);
            // Print the number of affected rows
            System.out.println("scores row = " + row);
        } catch (Exception e) {
            // Roll back the transaction on error
            transactionManager.rollback(transactionStatus);
            e.printStackTrace();
        }
    }
}
```

### 8. Source Code Analysis

**Start a transaction**

The method `org.springframework.transaction.support.AbstractPlatformTransactionManager#getTransaction`
implements `getTransaction()`
of the `PlatformTransactionManager` interface. It handles the propagation behavior of the transaction. Depending on the propagation behavior, it calls methods such as `doGetTransaction()`, `isExistingTransaction()`, and `doBegin()`
to get and check the existing transaction and to start a new one. If there is an existing transaction, the propagation behavior decides how to handle it. If there is none, the propagation behavior decides how to go on. When it creates a new transaction, it may suspend an existing one, and it restores the suspended resources if an exception happens. If the propagation behavior is "
mandatory"
mandatory" but no existing transaction is found, it throws an exception. If the given timeout is not valid, it throws a timeout exception. Finally, depending on the propagation behavior, it returns the matching transaction status object. This may be an "
empty" transaction: there is no real transaction, but there may be synchronization.

```java
/**
 * This implementation handles propagation behavior. It delegates to doGetTransaction, isExistingTransaction, and doBegin.
 * @see #doGetTransaction
 * @see #isExistingTransaction
 * @see #doBegin
 */
@Override
public final TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
        throws TransactionException {

    // Use defaults if no transaction definition was given.
    TransactionDefinition def = (definition != null ? definition : TransactionDefinition.withDefaults());

    // Get the transaction object
    Object transaction = doGetTransaction();
    // Whether debug logging is enabled
    boolean debugEnabled = logger.isDebugEnabled();

    if (isExistingTransaction(transaction)) {
        // Existing transaction found -> check the propagation behavior to decide what to do.
        return handleExistingTransaction(def, transaction, debugEnabled);
    }

    // Check the definition settings for the new transaction.
    if (def.getTimeout() < TransactionDefinition.TIMEOUT_DEFAULT) {
        throw new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout());
    }

    // No existing transaction found -> check the propagation behavior to decide how to go on.
    if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_MANDATORY) {
        throw new IllegalTransactionStateException(
                "No existing transaction found for transaction marked with propagation 'mandatory'");
    } else if (def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRED ||
            def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW ||
            def.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
        // Suspend the existing transaction (if any), then create the new one.
        SuspendedResourcesHolder suspendedResources = suspend(null);
        if (debugEnabled) {
            logger.debug("Creating new transaction with name [" + def.getName() + "]: " + def);
        }
        try {
            // Start the new transaction
            return startTransaction(def, transaction, debugEnabled, suspendedResources);
        } catch (RuntimeException | Error ex) {
            // If starting the new transaction fails, restore the suspended resources and throw the exception.
            resume(null, suspendedResources);
            throw ex;
        }
    } else {
        // Create an "empty" transaction: no real transaction, but possibly synchronization.
        if (def.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT && logger.isWarnEnabled()) {
            logger.warn("Custom isolation level specified but no actual transaction initiated; " +
                    "isolation level will effectively be ignored: " + def);
        }
        // Return the prepared transaction status: no real transaction, but possibly synchronization.
        boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
        return prepareTransactionStatus(def, null, true, newSynchronization, debugEnabled, null);
    }
}
```

The method `org.springframework.jdbc.datasource.DataSourceTransactionManager#doGetTransaction`
first creates a `DataSourceTransactionObject`
object to manage the data source transaction. Then it sets whether savepoints are allowed, depending on whether nested transactions are allowed. Next it gets the connection holder bound to the current thread with `TransactionSynchronizationManager.getResource()`
and sets it on the transaction object. Finally, it returns the transaction object.

```java

@Override
protected Object doGetTransaction() {
    // Create a DataSourceTransactionObject to manage the data source transaction
    DataSourceTransactionObject txObject = new DataSourceTransactionObject();
    // Set whether savepoints are allowed
    txObject.setSavepointAllowed(isNestedTransactionAllowed());
    // Get the connection holder bound to the current thread for the data source
    ConnectionHolder conHolder =
            (ConnectionHolder) TransactionSynchronizationManager.getResource(obtainDataSource());
    // Set the connection holder on the transaction object
    txObject.setConnectionHolder(conHolder, false);
    return txObject;
}
```

The method `org.springframework.jdbc.datasource.DataSourceTransactionManager#isExistingTransaction`
checks whether the given transaction object stands for an existing active transaction. It first casts the given transaction object to `DataSourceTransactionObject`
. Then it checks whether the object has a connection holder and whether the transaction in that holder is active. Finally, it returns a boolean that says whether an active transaction exists.

```java

@Override
protected boolean isExistingTransaction(Object transaction) {
    // Cast the transaction object to DataSourceTransactionObject
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) transaction;
    // Check whether the transaction object has a connection holder and whether the transaction in the holder is active
    return (txObject.hasConnectionHolder() && txObject.getConnectionHolder().isTransactionActive());
}
```

The method `org.springframework.transaction.support.AbstractPlatformTransactionManager#handleExistingTransaction`
depends on the propagation behavior in the transaction definition. It may suspend the current transaction and create a new one, create a nested transaction, or join the existing transaction. In the end, it creates and returns a new
TransactionStatus object that stands for the existing transaction.

```java
/**
 * Create a TransactionStatus for an existing transaction.
 *
 * @param definition     the transaction definition
 * @param transaction    the transaction object
 * @param debugEnabled   whether debug logging is enabled
 * @return a TransactionStatus object with information about the existing transaction
 * @throws TransactionException if an error occurs while handling the existing transaction
 */
private TransactionStatus handleExistingTransaction(
        TransactionDefinition definition, Object transaction, boolean debugEnabled)
        throws TransactionException {

    // If the propagation behavior is PROPAGATION_NEVER, an existing transaction is not allowed
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NEVER) {
        throw new IllegalTransactionStateException(
                "Existing transaction found for transaction marked with propagation 'never'");
    }

    // If the propagation behavior is PROPAGATION_NOT_SUPPORTED, suspend the current transaction
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NOT_SUPPORTED) {
        if (debugEnabled) {
            logger.debug("Suspending current transaction");
        }
        // Suspend the current transaction and get the suspended resources
        Object suspendedResources = suspend(transaction);
        // Decide whether a new transaction synchronization is needed
        boolean newSynchronization = (getTransactionSynchronization() == SYNCHRONIZATION_ALWAYS);
        // Prepare the transaction status and return it
        return prepareTransactionStatus(
                definition, null, false, newSynchronization, debugEnabled, suspendedResources);
    }

    // If the propagation behavior is PROPAGATION_REQUIRES_NEW, suspend the current transaction and create a new one
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
        if (debugEnabled) {
            logger.debug("Suspending current transaction, creating new transaction with name [" +
                    definition.getName() + "]");
        }
        // Suspend the current transaction and get the suspended resources
        SuspendedResourcesHolder suspendedResources = suspend(transaction);
        try {
            // Start the new transaction
            return startTransaction(definition, transaction, debugEnabled, suspendedResources);
        } catch (RuntimeException | Error beginEx) {
            // If starting the transaction fails, restore the suspended resources and throw the exception
            resumeAfterBeginException(transaction, suspendedResources, beginEx);
            throw beginEx;
        }
    }

    // If the propagation behavior is PROPAGATION_NESTED, create a nested transaction
    if (definition.getPropagationBehavior() == TransactionDefinition.PROPAGATION_NESTED) {
        // Check whether nested transactions are allowed
        if (!isNestedTransactionAllowed()) {
            throw new NestedTransactionNotSupportedException(
                    "Transaction manager does not allow nested transactions by default - " +
                            "specify 'nestedTransactionAllowed' property with value 'true'");
        }
        if (debugEnabled) {
            logger.debug("Creating nested transaction with name [" + definition.getName() + "]");
        }
        // If savepoints are used for nested transactions, create a savepoint in the existing Spring-managed transaction
        if (useSavepointForNestedTransaction()) {
            // Create a savepoint within the existing Spring-managed transaction through the SavepointManager API implemented by TransactionStatus
            // Usually uses a JDBC 3.0 savepoint. Never activates Spring synchronization.
            DefaultTransactionStatus status =
                    prepareTransactionStatus(definition, transaction, false, false, debugEnabled, null);
            status.createAndHoldSavepoint();
            return status;
        } else {
            // Create a nested transaction through nested begin and commit/rollback calls
            // Usually only for JTA: Spring synchronization might get activated here in case of a pre-existing JTA transaction.
            return startTransaction(definition, transaction, debugEnabled, null);
        }
    }

    // Assumably PROPAGATION_SUPPORTS or PROPAGATION_REQUIRED.
    // If the propagation behavior is PROPAGATION_SUPPORTS or PROPAGATION_REQUIRED, join the existing transaction
    if (debugEnabled) {
        logger.debug("Participating in existing transaction");
    }
    // If validation of existing transactions is on, check that the definition is compatible with the existing transaction
    if (isValidateExistingTransaction()) {
        if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
            // Check that the isolation level is compatible with the existing transaction
            Integer currentIsolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
            if (currentIsolationLevel == null || currentIsolationLevel != definition.getIsolationLevel()) {
                Constants isoConstants = DefaultTransactionDefinition.constants;
                throw new IllegalTransactionStateException("Participating transaction with definition [" +
                        definition + "] specifies isolation level which is incompatible with existing transaction: " +
                        (currentIsolationLevel != null ?
                                isoConstants.toCode(currentIsolationLevel, DefaultTransactionDefinition.PREFIX_ISOLATION) :
                                "(unknown)"));
            }
        }
        // Check that the read-only attribute is compatible with the existing transaction
        if (!definition.isReadOnly()) {
            if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                throw new IllegalTransactionStateException("Participating transaction with definition [" +
                        definition + "] is not marked as read-only but existing transaction is");
            }
        }
    }
    // Create a new transaction synchronization if needed
    boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
    return prepareTransactionStatus(definition, transaction, false, newSynchronization, debugEnabled, null);
}
```

The method `org.springframework.transaction.support.AbstractPlatformTransactionManager#startTransaction`
first checks whether transaction synchronization should be enabled, then creates a new transaction status object. Next it calls the `doBegin` method to begin the transaction and prepares the synchronization. Finally, it returns the created transaction status object.

```java
/**
 * Start a new transaction.
 */
private TransactionStatus startTransaction(TransactionDefinition definition, Object transaction,
                                           boolean debugEnabled, @Nullable SuspendedResourcesHolder suspendedResources) {

    // Decide whether to enable transaction synchronization
    boolean newSynchronization = (getTransactionSynchronization() != SYNCHRONIZATION_NEVER);
    // Create a new transaction status object
    DefaultTransactionStatus status = newTransactionStatus(
            definition, transaction, true, newSynchronization, debugEnabled, suspendedResources);
    // Begin the transaction
    doBegin(transaction, definition);
    // Prepare the synchronization
    prepareSynchronization(status, definition);
    return status;
}
```

The method `org.springframework.jdbc.datasource.DataSourceTransactionManager#doBegin`
first tries to get a database connection from the data source and stores it in the transaction object. Then it configures the connection with the properties in the transaction definition, such as the isolation level and the read-only flag. If the connection is in auto-commit mode, it switches it to manual commit to keep the transaction consistent. Next it prepares the transaction connection and marks the connection holder as having an active transaction. Finally, if the connection holder is new, it binds it to the current thread, so the lifecycle of the connection can be managed during the transaction. If anything goes wrong, it releases the connection and throws an exception that says the transaction could not be created.

```java

@Override
protected void doBegin(Object transaction, TransactionDefinition definition) {
    // Cast the transaction object to DataSourceTransactionObject
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) transaction;
    Connection con = null;

    try {
        // If the transaction object has no connection holder, or the holder is already synchronized with a transaction, get a new connection
        if (!txObject.hasConnectionHolder() ||
                txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
            // Get the data source and get a connection from it
            Connection newCon = obtainDataSource().getConnection();
            // If the log level is DEBUG, print the connection that was obtained
            if (logger.isDebugEnabled()) {
                logger.debug("Acquired Connection [" + newCon + "] for JDBC transaction");
            }
            // Set the connection holder to the new connection, and mark it as synchronized with the transaction
            txObject.setConnectionHolder(new ConnectionHolder(newCon), true);
        }

        // Set the synchronization flag of the connection to true
        txObject.getConnectionHolder().setSynchronizedWithTransaction(true);
        // Get the current connection
        con = txObject.getConnectionHolder().getConnection();

        // Prepare the transaction settings of the connection and record the previous isolation level
        Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(con, definition);
        txObject.setPreviousIsolationLevel(previousIsolationLevel);
        // Set the read-only flag
        txObject.setReadOnly(definition.isReadOnly());

        // If the connection is in auto-commit mode, switch to manual commit
        if (con.getAutoCommit()) {
            // Mark that the original auto-commit state must be restored
            txObject.setMustRestoreAutoCommit(true);
            // If the log level is DEBUG, print that auto-commit is switched to manual commit
            if (logger.isDebugEnabled()) {
                logger.debug("Switching JDBC Connection [" + con + "] to manual commit");
            }
            // Set manual commit
            con.setAutoCommit(false);
        }

        // Prepare the transaction connection and mark the holder as having an active transaction
        prepareTransactionalConnection(con, definition);
        txObject.getConnectionHolder().setTransactionActive(true);

        // Determine the transaction timeout and set it on the connection holder
        int timeout = determineTimeout(definition);
        if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
            txObject.getConnectionHolder().setTimeoutInSeconds(timeout);
        }

        // If the connection holder is new, bind it to the thread
        if (txObject.isNewConnectionHolder()) {
            TransactionSynchronizationManager.bindResource(obtainDataSource(), txObject.getConnectionHolder());
        }
    } catch (Throwable ex) {
        // If the connection holder is new, release the connection and set the holder to null
        if (txObject.isNewConnectionHolder()) {
            DataSourceUtils.releaseConnection(con, obtainDataSource());
            txObject.setConnectionHolder(null, false);
        }
        // Throw the exception that the transaction could not be created
        throw new CannotCreateTransactionException("Could not open JDBC Connection for transaction", ex);
    }
}
```

The method `org.springframework.jdbc.datasource.DataSourceTransactionManager#prepareTransactionalConnection`
runs a "SET TRANSACTION READ ONLY" statement if the "enforceReadOnly" flag is true and the transaction definition says the transaction is read-only.

```java
/**
 * Prepare the transactional {@code Connection} right after transaction begin.
 * <p>The default implementation executes a "SET TRANSACTION READ ONLY" statement if the {@link #setEnforceReadOnly "enforceReadOnly"} flag is set to {@code true}
 * and the transaction definition indicates a read-only transaction.
 * <p>The "SET TRANSACTION READ ONLY" statement is understood by Oracle, MySQL and Postgres, and may work with other databases as well.
 * If you want to adapt this treatment, override this method accordingly.
 * @param con the transactional JDBC Connection
 * @param definition the current transaction definition
 * @throws SQLException if thrown by JDBC API
 * @since 4.3.7
 * @see #setEnforceReadOnly
 */
protected void prepareTransactionalConnection(Connection con, TransactionDefinition definition)
        throws SQLException {

    if (isEnforceReadOnly() && definition.isReadOnly()) {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate("SET TRANSACTION READ ONLY");
        }
    }
}
```

**Commit a transaction**

The method `org.springframework.transaction.support.AbstractPlatformTransactionManager#commit`
first checks whether the transaction status is already completed. If it is, it throws an illegal transaction state exception. Then it checks whether the transaction status is local rollback-only. If it is, it rolls back. Next, if the global transaction is marked rollback-only but the transaction code asks for a commit, it also rolls back. Finally, if none of these cases applies, it commits the transaction.

```java
/**
 * This implementation of commit handles participating in existing transactions and programmatic rollback requests. It delegates to {@code isRollbackOnly}, {@code doCommit} and {@code rollback}.
 * If the transaction is already completed, throw an exception.
 * If the transaction status is local rollback-only, roll back as needed.
 * If the commit should not happen on global rollback-only, and the transaction status is global rollback-only, roll back as needed.
 * Otherwise, perform the commit.
 * @param status the transaction status object
 * @throws TransactionException if a transaction exception occurs during commit
 * @see org.springframework.transaction.TransactionStatus#isRollbackOnly()
 * @see #doCommit
 * @see #rollback
 */
@Override
public final void commit(TransactionStatus status) throws TransactionException {
    // If the transaction is already completed, throw an exception
    if (status.isCompleted()) {
        throw new IllegalTransactionStateException(
                "Transaction is already completed - do not call commit or rollback more than once per transaction");
    }

    DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
    // If the transaction status is local rollback-only, roll back as needed
    if (defStatus.isLocalRollbackOnly()) {
        if (defStatus.isDebug()) {
            logger.debug("Transactional code has requested rollback");
        }
        processRollback(defStatus, false);
        return;
    }

    // If the commit should not happen on global rollback-only, and the transaction status is global rollback-only, roll back as needed
    if (!shouldCommitOnGlobalRollbackOnly() && defStatus.isGlobalRollbackOnly()) {
        if (defStatus.isDebug()) {
            logger.debug("Global transaction is marked as rollback-only but transactional code requested commit");
        }
        processRollback(defStatus, true);
        return;
    }

    // Otherwise, perform the commit
    processCommit(defStatus);
}
```

The method `org.springframework.transaction.support.AbstractPlatformTransactionManager#processCommit`
checks and applies the rollback flag, then runs the commit logic. During the commit, it fires several callbacks, such as before-commit and after-commit, so specific logic can run at different stages of the commit. If an unexpected rollback or a commit failure happens, it handles the error and runs the matching rollback. In the end, whether the commit succeeded or not, it cleans up so the transaction state is correct.

```java
/**
 * Process an actual commit.
 * The rollback-only flag has already been checked and applied.
 *
 * @param status the object that represents the transaction
 * @throws TransactionException if commit fails
 */
private void processCommit(DefaultTransactionStatus status) throws TransactionException {
    try {
        boolean beforeCompletionInvoked = false;

        try {
            boolean unexpectedRollback = false;
            // Prepare for the commit
            prepareForCommit(status);
            // Fire the before-commit callbacks
            triggerBeforeCommit(status);
            // Fire the before-completion callbacks
            triggerBeforeCompletion(status);
            beforeCompletionInvoked = true;

            // If there is a savepoint
            if (status.hasSavepoint()) {
                if (status.isDebug()) {
                    // Release the transaction savepoint
                    logger.debug("Releasing transaction savepoint");
                }
                // Check whether it is a global rollback
                unexpectedRollback = status.isGlobalRollbackOnly();
                // Release the held savepoint
                status.releaseHeldSavepoint();
            } else if (status.isNewTransaction()) {
                // If it is a new transaction
                if (status.isDebug()) {
                    // Start committing the transaction
                    logger.debug("Initiating transaction commit");
                }
                // Check whether it is a global rollback
                unexpectedRollback = status.isGlobalRollbackOnly();
                // Run the commit
                doCommit(status);
            } else if (isFailEarlyOnGlobalRollbackOnly()) {
                // If it is a global rollback
                // Check whether it is a global rollback
                unexpectedRollback = status.isGlobalRollbackOnly();
            }

            // If there is an unexpected rollback but we still did not get the matching exception from the commit, throw UnexpectedRollbackException
            if (unexpectedRollback) {
                throw new UnexpectedRollbackException(
                        "Transaction silently rolled back because it has been marked as rollback-only");
            }
        } catch (UnexpectedRollbackException ex) {
            // Can only be caused by doCommit
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_ROLLED_BACK);
            throw ex;
        } catch (TransactionException ex) {
            // Can only be caused by doCommit
            if (isRollbackOnCommitFailure()) {
                doRollbackOnCommitException(status, ex);
            } else {
                triggerAfterCompletion(status, TransactionSynchronization.STATUS_UNKNOWN);
            }
            throw ex;
        } catch (RuntimeException | Error ex) {
            if (!beforeCompletionInvoked) {
                triggerBeforeCompletion(status);
            }
            doRollbackOnCommitException(status, ex);
            throw ex;
        }

        // Fire the afterCommit callbacks. Exceptions thrown there are propagated to the caller, but the transaction is still considered as committed.
        try {
            triggerAfterCommit(status);
        } finally {
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_COMMITTED);
        }

    } finally {
        // Clean up after completion
        cleanupAfterCompletion(status);
    }
}
```

The method `org.springframework.jdbc.datasource.DataSourceTransactionManager#doCommit`
gets the data source transaction object from the transaction status, then gets the database connection from it. Next it tries to commit the database transaction. If an SQL
exception happens during the commit, it converts it to a Spring transaction exception and throws it.

```java

@Override
protected void doCommit(DefaultTransactionStatus status) {
    // Cast to DataSourceTransactionObject to get the transaction information
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) status.getTransaction();
    // Get the database connection from the transaction object
    Connection con = txObject.getConnectionHolder().getConnection();
    // In debug mode, log debug information
    if (status.isDebug()) {
        logger.debug("Committing JDBC transaction on Connection [" + con + "]");
    }
    try {
        // Commit the database transaction
        con.commit();
    } catch (SQLException ex) {
        // On an SQL exception, convert it to a Spring transaction exception and throw it
        throw translateException("JDBC commit", ex);
    }
}
```

**Roll back a transaction**

The method `org.springframework.transaction.support.AbstractPlatformTransactionManager#rollback`
handles the rollback of an existing transaction. It delegates to the `processRollback` method to do the rollback. It checks the transaction status to make sure commit or rollback is not called more than once.

```java
/**
 * This implementation of rollback handles participating in existing transactions. It delegates to {@code doRollback} and {@code doSetRollbackOnly}.
 *
 * @see #doRollback
 * @see #doSetRollbackOnly
 */
@Override
public final void rollback(TransactionStatus status) throws TransactionException {
    // If the transaction is already completed, throw an exception
    if (status.isCompleted()) {
        throw new IllegalTransactionStateException(
                "Transaction is already completed - do not call commit or rollback more than once per transaction");
    }

    // Cast the transaction status to the default transaction status
    DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
    // Run the rollback
    processRollback(defStatus, false);
}
```

The method `org.springframework.transaction.support.AbstractPlatformTransactionManager#processRollback`
checks the completion flag of the transaction, then runs the matching rollback depending on the transaction status. If there is a savepoint, it rolls back to the savepoint. If it is a new transaction, it runs the rollback of the initialized transaction. If it takes part in a larger transaction, it handles it by conditions. During the process, it fires the matching transaction synchronization callbacks, and it uses the global rollback-only flag to decide whether to throw an unexpected rollback exception. Finally, whether an exception happened or not, it cleans up after completion.

```java
/**
 * Process an actual rollback.
 * The completed flag has already been checked.
 * @param status the object that represents the transaction
 * @param unexpected whether the rollback is unexpected
 * @throws TransactionException if rollback fails
 */
private void processRollback(DefaultTransactionStatus status, boolean unexpected) {
    try {
        boolean unexpectedRollback = unexpected;

        try {
            // Fire the before-completion callbacks
            triggerBeforeCompletion(status);

            // Roll back to the savepoint
            if (status.hasSavepoint()) {
                if (status.isDebug()) {
                    logger.debug("Rolling back transaction to savepoint");
                }
                status.rollbackToHeldSavepoint();
            }
            // Roll back the initialized transaction
            else if (status.isNewTransaction()) {
                if (status.isDebug()) {
                    logger.debug("Initiating transaction rollback");
                }
                doRollback(status);
            } else {
                // Take part in a larger transaction
                if (status.hasTransaction()) {
                    // If it is a local rollback, or the global rollback fails
                    if (status.isLocalRollbackOnly() || isGlobalRollbackOnParticipationFailure()) {
                        if (status.isDebug()) {
                            logger.debug("Participating transaction failed - marking existing transaction as rollback-only");
                        }
                        // Mark the transaction as rollback-only
                        doSetRollbackOnly(status);
                    } else {
                        if (status.isDebug()) {
                            logger.debug("Participating transaction failed - letting transaction originator decide on rollback");
                        }
                    }
                } else {
                    logger.debug("Should roll back transaction but cannot - no transaction available");
                }
                // If it is not global rollback-only, an unexpected rollback is not considered here
                if (!isFailEarlyOnGlobalRollbackOnly()) {
                    unexpectedRollback = false;
                }
            }
        } catch (RuntimeException | Error ex) {
            // Fire the after-completion callbacks with status unknown
            triggerAfterCompletion(status, TransactionSynchronization.STATUS_UNKNOWN);
            throw ex;
        }

        // Fire the after-completion callbacks with status rolled back
        triggerAfterCompletion(status, TransactionSynchronization.STATUS_ROLLED_BACK);

        // If there is a global rollback marker, throw UnexpectedRollbackException
        if (unexpectedRollback) {
            throw new UnexpectedRollbackException(
                    "Transaction rolled back because it has been marked as rollback-only");
        }
    } finally {
        // Clean up after completion
        cleanupAfterCompletion(status);
    }
}
```

The method `org.springframework.jdbc.datasource.DataSourceTransactionManager#doRollback`
gets the data source transaction object from the transaction status, then gets the connection from it. Next, it tries to roll back the connection.

```java
/**
 * Run the rollback.
 * @param status the object that represents the transaction
 */
@Override
protected void doRollback(DefaultTransactionStatus status) {
    // Get the data source transaction object
    DataSourceTransactionObject txObject = (DataSourceTransactionObject) status.getTransaction();
    // Get the connection
    Connection con = txObject.getConnectionHolder().getConnection();
    // If debug mode is on, log the rollback
    if (status.isDebug()) {
        logger.debug("Rolling back JDBC transaction on Connection [" + con + "]");
    }
    try {
        // Run the rollback
        con.rollback();
    } catch (SQLException ex) {
        // Throw the database exception
        throw translateException("JDBC rollback", ex);
    }
}
```