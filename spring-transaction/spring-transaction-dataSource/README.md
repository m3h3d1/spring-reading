## DataSource

- [DataSource](#datasource)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [5. Best Practices](#5-best-practices)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `DataSource` interface is the standard Java interface
for managing database connections. It gives an abstract way to get a database connection. An application can work with different databases without changing code. By configuring the data source, you can handle connection pooling, transaction management, and connection allocation more flexibly. This improves the performance and maintainability of the application.

### 3. Main Features

1. **Provides database connections**

    + It defines methods to get a database connection. You call them to get a connection and then work with the database.

2. **Connection pool management**

    + It can be implemented as a connection pool. This avoids creating and destroying connections too often and improves performance.

3. **Connection configuration**

    + It lets you configure connection properties such as the database address, username, and password. You can change them easily for different environments.

4. **Resource management**

    + It manages connection resources, including allocation, recycling, and timeout control, so connections stay valid and available.

5. **Transparent data source**

    + The application gets connections through `DataSource`. It does not need to care about the database type or configuration details. This makes the code more portable and easier to extend.

### 4. Interface Source

`DataSource` is used to get a connection to a physical data source. `DataSource` has two methods to get a connection: `getConnection()`
and `getConnection(String username, String password)`
. They get a connection with the default user and password, and a connection with a given user and password. The interface also defines some methods to manage connections, such as getting and setting the log writer and setting the login timeout. The interface describes what `DataSource`
does and how it is implemented, and compares it with `DriverManager`. It also has a default method `createConnectionBuilder()`
that creates a connection builder instance.

```java
/**
 * <p>A factory for connections to the physical data source that this {@code DataSource} object represents. An alternative to the {@code DriverManager} facility, a {@code DataSource} object is the preferred means of getting a connection. An object that implements the {@code DataSource} interface will typically be registered with a naming service based on the Java&trade; Naming and Directory (JNDI) API.</p>
 * <p>The {@code DataSource} interface is implemented by a driver vendor. There are three types of implementations:</p>
 * <ol>
 *   <li>Basic implementation -- produces a standard {@code Connection} object</li>
 *   <li>Connection pooling implementation -- produces a {@code Connection} object that will automatically participate in connection pooling. This implementation works with a middle-tier connection pooling manager.</li>
 *   <li>Distributed transaction implementation -- produces a {@code Connection} object that may be used for distributed transactions and almost always participates in connection pooling. This implementation works with a middle-tier transaction manager and almost always with a connection pooling manager.</li>
 * </ol>
 * <p>A {@code DataSource} object has properties that can be modified when necessary. For example, if the data source is moved to a different server, the property for the server can be changed. The benefit is that because the data source's properties can be changed, any code accessing that data source does not need to be changed.</p>
 * <p>A driver that is accessed via a {@code DataSource} object does not register itself with the {@code DriverManager}. Rather, a {@code DataSource} object is retrieved through a lookup operation and then used to create a {@code Connection} object. With a basic implementation, the connection obtained through a {@code DataSource} object is identical to a connection obtained through the {@code DriverManager} facility.</p>
 * <p>An implementation of {@code DataSource} must include a public no-arg constructor.</p>
 *
 * @since 1.4
 */
public interface DataSource extends CommonDataSource, Wrapper {

  /**
   * <p>Attempts to establish a connection with the data source that this {@code DataSource} object represents.</p>
   *
   * @return a connection to the data source
   * @exception SQLException if a database access error occurs
   * @throws java.sql.SQLTimeoutException  when the driver has determined that the timeout value specified by the {@code setLoginTimeout} method has been exceeded and has at least tried to cancel the current database connection attempt
   */
  Connection getConnection() throws SQLException;

  /**
   * <p>Attempts to establish a connection with the data source that this {@code DataSource} object represents, using the given user and password.</p>
   *
   * @param username the database user on whose behalf the connection is being made
   * @param password the user's password
   * @return a connection to the data source
   * @exception SQLException if a database access error occurs
   * @throws java.sql.SQLTimeoutException  when the driver has determined that the timeout value specified by the {@code setLoginTimeout} method has been exceeded and has at least tried to cancel the current database connection attempt
   * @since 1.4
   */
  Connection getConnection(String username, String password) throws SQLException;

  /**
   * {@inheritDoc}
   * @since 1.4
   */
  @Override
  java.io.PrintWriter getLogWriter() throws SQLException;

  /**
   * {@inheritDoc}
   * @since 1.4
   */
  @Override
  void setLogWriter(java.io.PrintWriter out) throws SQLException;

  /**
   * {@inheritDoc}
   * @since 1.4
   */
  @Override
  void setLoginTimeout(int seconds) throws SQLException;

  /**
   * {@inheritDoc}
   * @since 1.4
   */
  @Override
  int getLoginTimeout() throws SQLException;

  // JDBC 4.3

  /**
   * Creates a new {@code ConnectionBuilder} instance
   * @implSpec The default implementation will throw a {@code SQLFeatureNotSupportedException}
   * @return the created ConnectionBuilder instance
   * @throws SQLException if an error occurs creating the builder
   * @throws SQLFeatureNotSupportedException if the driver does not support sharding
   * @since 9
   * @see ConnectionBuilder
   */
  default ConnectionBuilder createConnectionBuilder() throws SQLException {
        throw new SQLFeatureNotSupportedException("createConnectionBuilder not implemented");
  };

}
```

### 5. Best Practices

Use `DataSource` to open a database connection. This follows the high cohesion principle and makes it easy to change the database later. Set the URL, username, and password with the `MysqlDataSource` class,
then get the connection. Next, run an SQL query and print the result to the console. Finally, close the result set, the PreparedStatement,
and the connection to free the resources.

```java
public class DataSourceDemo {

    public static void main(String[] args) throws Exception {
        // Using a DataSource makes it easy to change the database later
        MysqlDataSource dataSource = new MysqlDataSource();
        // Database URL, format: jdbc:driver-name://host:port/database-name
        dataSource.setUrl("jdbc:mysql://localhost:3306/spring-reading");
        // Database username
        dataSource.setUser("root");
        // Database password
        dataSource.setPassword("123456");

        // Open the database connection
        Connection connection = dataSource.getConnection();
        // SQL query
        String sql = "SELECT * FROM scores";
        // Create a PreparedStatement to run the SQL query
        PreparedStatement statement = connection.prepareStatement(sql);
        // Run the query and get the result set
        ResultSet resultSet = statement.executeQuery();

        // Loop over the result set
        while (resultSet.next()) {
            // Get the value of the id column
            int id = resultSet.getInt("id");
            // Get the value of the score column
            String score = resultSet.getString("score");
            // Print the result
            System.out.println("id: " + id + ", score: " + score);
        }
        // Close the result set, the PreparedStatement and the connection
        resultSet.close();
        statement.close();
        connection.close();
    }
}
```

This is the data read from the "scores" table of the "spring-reading" database. Each row has an "id" column and a "score" column.

```java
id: 1, score: 3.50
id: 2, score: 3.65
id: 3, score: 4.00
id: 4, score: 3.85
id: 5, score: 4.00
id: 6, score: 3.65
```

