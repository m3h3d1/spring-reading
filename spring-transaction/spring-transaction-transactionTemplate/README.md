## TransactionTemplate

- [TransactionTemplate](#transactiontemplate)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Best Practices](#4-best-practices)
    - [5. Source Code Analysis](#5-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

`TransactionTemplate` is a utility class in the Spring Framework that manages transactions programmatically. It simplifies starting a transaction, commit/rollback, and exception handling. It also lets you configure transaction properties flexibly, and it provides callbacks to run specific operations.

### 3. Main Features

1. **Start and commit/roll back transactions**

    + It lets us start a transaction in code, and commit or roll it back when needed. This way we can define the boundary of a transaction clearly in a specific part of the code, without relying on the container to manage it.

2. **Exception handling**

    + It provides a way to handle exceptions. We can configure what should happen when an exception occurs, for example rolling back the transaction.

3. **Flexible transaction properties**

    + We can use `TransactionTemplate` to configure transaction properties such as the isolation level and the propagation behavior. This lets us set up the transaction behavior flexibly for different cases.

4. **Callbacks**

    + It lets us define callback interfaces. With these callbacks we can run specific operations at different stages of the transaction. This gives more flexibility for more complex transaction cases.

### 4. Best Practices

This example uses `TransactionTemplate` to manage a transaction. It first creates a database connection and creates the `TransactionTemplate` with a `DataSourceTransactionManager`
. In the `execute`
method of `TransactionTemplate`, it defines a transaction callback. The database operation runs in the `doInTransaction` method of that callback. This way, either all operations are committed successfully, or all are rolled back. This keeps the data consistent and complete.

```java
public class TransactionTemplateDemo {

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
        // Create the transaction template
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        Boolean insertSuccess = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                // Primary key id
                long id = System.currentTimeMillis();
                // Score
                int score = new Random().nextInt(100);
                // Save the data
                int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
                // Simulate an exception to test transaction rollback
                // int i = 1 / 0;
                // We can also roll back with setRollbackOnly
                // status.setRollbackOnly();
                // Return whether the insert succeeded
                return row >= 1;
            }
        });
        System.out.println("Inserted into scores table: " + insertSuccess);
    }
}
```

In the result, the database operation finished successfully and the transaction was committed.

```java
Inserted into scores table: true
```

### 5. Source Code Analysis

The method `org.springframework.transaction.support.TransactionTemplate#execute` first makes sure the `PlatformTransactionManager`
is set. Then it picks how to run depending on the type of the transaction manager. If the transaction manager is an instance of `CallbackPreferringPlatformTransactionManager`
, it calls its `execute` method to run the transaction. Otherwise, it gets the transaction status, runs the transaction callback, and handles any exception during the operation. At the end, it commits the transaction if there is no failure.

```java

@Override
@Nullable
public <T> T execute(TransactionCallback<T> action) throws TransactionException {
    // Assert that the PlatformTransactionManager is set
    Assert.state(this.transactionManager != null, "No PlatformTransactionManager set");

    // If the transaction manager is an instance of CallbackPreferringPlatformTransactionManager
    if (this.transactionManager instanceof CallbackPreferringPlatformTransactionManager) {
        // Run the transaction with CallbackPreferringPlatformTransactionManager
        return ((CallbackPreferringPlatformTransactionManager) this.transactionManager).execute(this, action);
    } else {
        // Get the transaction status
        TransactionStatus status = this.transactionManager.getTransaction(this);
        T result;
        try {
            // Run the transaction callback
            result = action.doInTransaction(status);
        } catch (RuntimeException | Error ex) {
            // Transactional code threw application exception -> rollback
            rollbackOnException(status, ex);
            throw ex;
        } catch (Throwable ex) {
            // Transactional code threw unexpected exception -> rollback
            rollbackOnException(status, ex);
            throw new UndeclaredThrowableException(ex, "TransactionCallback threw undeclared checked exception");
        }
        // Commit the transaction
        this.transactionManager.commit(status);
        return result;
    }
}
```

The method `org.springframework.transaction.support.TransactionTemplate#rollbackOnException`
first makes sure the `PlatformTransactionManager` is set
. Then it logs a debug message that a rollback is starting because of the application exception. Next it tries to roll back. If the rollback throws an exception, it logs an error and sets the original exception as the application exception of the rollback exception. Finally, if a runtime exception or error happens, it rethrows it.

```java
/**
 * Perform a rollback, handling rollback exceptions properly.
 * @param status the object that represents the transaction
 * @param ex the thrown application exception or error
 * @throws TransactionException in case of a rollback error
 */
private void rollbackOnException(TransactionStatus status, Throwable ex) throws TransactionException {
    // Assert that the PlatformTransactionManager is set
    Assert.state(this.transactionManager != null, "No PlatformTransactionManager set");

    // Log a debug message that a rollback is starting because of the application exception
    logger.debug("Initiating transaction rollback on application exception", ex);
    try {
        // Run the rollback
        this.transactionManager.rollback(status);
    } catch (TransactionSystemException ex2) {
        // Log an error that the application exception is overridden by the rollback exception
        logger.error("Application exception overridden by rollback exception", ex);
        // Initialize the application exception
        ex2.initApplicationException(ex);
        throw ex2;
    } catch (RuntimeException | Error ex2) {
        // Log an error that the application exception is overridden by the rollback exception
        logger.error("Application exception overridden by rollback exception", ex);
        throw ex2;
    }
}
```