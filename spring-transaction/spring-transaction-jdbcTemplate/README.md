## JdbcTemplate

- [JdbcTemplate](#jdbctemplate)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Best Practices](#4-best-practices)
    - [4. Source Code Analysis](#4-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

JdbcTemplate is a key class in the Spring Framework that simplifies JDBC programming. It has simple methods to run SQL queries, updates, and batch operations. It also handles getting and releasing JDBC resources and handling exceptions. This makes database operations simpler, more efficient, and safer.

### 3. Main Features

1. **Simpler JDBC operations**

    + JdbcTemplate hides the complexity of JDBC. It has simple methods to run SQL queries, updates, and so on. You do not need to manage connections, statements, and result sets by hand.

2. **Exception handling**

    + JdbcTemplate catches JDBC exceptions and converts them to Spring's DataAccessException. This makes exception handling easier. You do not need to write long exception handling code.

3. **Parameterized queries**

    + It supports parameterized queries with placeholders. This helps prevent SQL injection and makes SQL statements easier to build and maintain.

4. **Batch operations**

    + It supports batch operations. You can run many SQL statements at once, which makes database operations more efficient.

5. **Callbacks**

    + It provides callbacks, so you can add custom logic before and after database operations, such as logging and performance monitoring.

6. **ORM framework support**

    + It can be used together with Spring's ORM frameworks (such as Spring Data JPA and Spring Data JDBC) to give a higher level of database access abstraction.

### 4. Best Practices

This example uses JdbcTemplate to run an SQL query. First, it creates a `SimpleDriverDataSource`
object to manage the data source from the database connection settings, and a `DataSourceTransactionManager` object for transaction management. Then it creates a `JdbcTemplate`
instance. Next, it runs a query with the `query` method and maps the result to a list of `Scores` objects. Finally, it prints the result.

```java
public class JdbcTemplateDemo {

    public static void main(String[] args) throws Exception {
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

        List<Scores> scoresList = jdbcTemplate.query("select * from scores ", new RowMapper<Scores>() {
            @Override
            public Scores mapRow(ResultSet rs, int rowNum) throws SQLException {
                Scores scores = new Scores();
                scores.setId(rs.getLong("id"));
                scores.setScore(rs.getLong("score"));
                return scores;
            }
        });

        scoresList.forEach(System.out::println);
    }
}
```

The `Scores` class is a simple Java bean.

```java
public class Scores {

    private long id;

    private long score;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Scores{" +
                "id=" + id +
                ", score=" + score +
                '}';
    }
}
```

This is the score records queried from the database.

```java
Scores {
    id = 1715586394553, score = 40
}

Scores {
    id = 1715587289293, score = 90
}

Scores {
    id = 1715588070751, score = 20
}

Scores {
    id = 1715668592566, score = 25
}
```

### 4. Source Code Analysis

The method `org.springframework.jdbc.core.JdbcTemplate#query(java.lang.String, org.springframework.jdbc.core.RowMapper<T>)`
runs an SQL query and maps the result to a list of objects. It takes an SQL query and a RowMapper implementation as arguments. It converts the query result to a list of objects with RowMapperResultSetExtractor and returns it.

```java
/**
 * Execute a query for the given SQL, mapping the result to a list of objects.
 *
 * @param sql the SQL query
 * @param rowMapper a RowMapper implementation that maps each row to an object
 * @param <T> the type of the result
 * @return the list of result objects
 * @throws DataAccessException if the query fails or converting the result fails
 */
@Override
public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
    // Call the overloaded query method and convert the result to a list of objects with RowMapperResultSetExtractor
    return result(query(sql, new RowMapperResultSetExtractor<>(rowMapper)));
}
```

The method `org.springframework.jdbc.core.JdbcTemplate#query(java.lang.String, org.springframework.jdbc.core.ResultSetExtractor<T>)`
runs an SQL query and extracts the result with a ResultSetExtractor. It takes an SQL query and a ResultSetExtractor implementation as arguments. It runs the query with a Statement object, hands the result set to the ResultSetExtractor, and returns what the ResultSetExtractor extracts.

```java
/**
 * Execute a query for the given SQL and extract the result with the given ResultSetExtractor.
 *
 * @param sql the SQL query
 * @param rse a ResultSetExtractor implementation that extracts the result
 * @param <T> the type of the result
 * @return the result, or null if there is no result
 * @throws DataAccessException if the query fails or extracting the result fails
 */
@Nullable
@Override
public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {
    // Make sure the SQL query and the ResultSetExtractor are not null
    Assert.notNull(sql, "SQL must not be null");
    Assert.notNull(rse, "ResultSetExtractor must not be null");

    // If debug logging is on, log the SQL query
    if (logger.isDebugEnabled()) {
        logger.debug("Executing SQL query [" + sql + "]");
    }

    /**
     * Inner class for the callback that runs the query.
     */
    class QueryStatementCallback implements StatementCallback<T>, SqlProvider {
        /**
         * Runs the query in a Statement and extracts the result.
         */
        @Override
        @Nullable
        public T doInStatement(Statement stmt) throws SQLException {
            ResultSet rs = null;
            try {
                // Run the query and get the result set
                rs = stmt.executeQuery(sql);
                // Extract the result with the ResultSetExtractor
                return rse.extractData(rs);
            } finally {
                // Close the result set
                JdbcUtils.closeResultSet(rs);
            }
        }

        /**
         * Get the SQL of the query.
         */
        @Override
        public String getSql() {
            return sql;
        }
    }

    // Run the query and return the result
    return execute(new QueryStatementCallback(), true);
}
```

The method `org.springframework.jdbc.core.JdbcTemplate#execute(org.springframework.jdbc.core.StatementCallback<T>, boolean)`
runs a database operation callback and closes resources if needed. It takes a database operation callback and a boolean that says whether to close resources after the operation. Inside the method, it first gets a database connection, then creates a Statement object and applies its settings. Next it runs the callback and handles warnings from the operation. If a SQLException happens, it releases the connection early and throws a DataAccessException. Finally, in the finally block, it closes the Statement and the connection if needed.

```java
/**
 * Execute the given database operation callback and close resources if needed.
 *
 * @param action the database operation callback
 * @param closeResources whether to close resources. If true, resources are closed after the operation; otherwise they are not
 * @param <T> the type of the result
 * @return the result of the operation
 * @throws DataAccessException if the operation fails
 */
@Nullable
private <T> T execute(StatementCallback<T> action, boolean closeResources) throws DataAccessException {
    // Make sure the callback is not null
    Assert.notNull(action, "Callback object must not be null");

    // Get the connection
    Connection con = DataSourceUtils.getConnection(obtainDataSource());
    Statement stmt = null;
    try {
        // Create the Statement
        stmt = con.createStatement();
        // Apply the settings of the Statement
        applyStatementSettings(stmt);
        // Run the database operation
        T result = action.doInStatement(stmt);
        // Handle warnings
        handleWarnings(stmt);
        return result;
    } catch (SQLException ex) {
        // Release the connection early to avoid a possible connection pool deadlock
        String sql = getSql(action);
        JdbcUtils.closeStatement(stmt);
        stmt = null;
        DataSourceUtils.releaseConnection(con, getDataSource());
        con = null;
        // Translate the exception and throw it
        throw translateException("StatementCallback", sql, ex);
    } finally {
        // If resources need to be closed, close the Statement and the connection in finally
        if (closeResources) {
            JdbcUtils.closeStatement(stmt);
            DataSourceUtils.releaseConnection(con, getDataSource());
        }
    }
}
```

