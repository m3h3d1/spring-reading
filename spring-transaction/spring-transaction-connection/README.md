## Connection

- [Connection](#connection)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [6. Best Practices](#6-best-practices)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

The `Connection` interface is part of the Java JDBC API. It represents a connection to a database. It has methods to manage the connection, run SQL
statements, and handle transactions. It is one of the key interfaces for working with a database.

### 3. Main Features

1. **Run SQL statements**

    + Create a `Statement` with `createStatement()`, or create a `PreparedStatement` with `prepareStatement(String sql)`.
      It is used to run SQL queries, updates, deletes, and so on.

2. **Transaction management**

    + It supports transactions. You can commit with `commit()` or roll back with `rollback()`.

3. **Transaction settings**

    + You can set the isolation level, auto-commit, and other properties.

### 4. Interface Source

The `Connection` interface represents a connection to a specific database. It has methods to run SQL statements, manage transactions, commit or roll back changes, and close the connection.

```java
/**
 * <P>A connection (session) with a specific database. SQL statements are executed and results are returned within the context of a connection.
 * <P>
 * The database of a <code>Connection</code> object can give information that describes its tables, its supported SQL grammar, its stored procedures, the capabilities of this connection, and so on. This information is obtained with the <code>getMetaData</code> method.
 *
 * <P><B>Note:</B> When configuring a <code>Connection</code>, a JDBC application should use the appropriate <code>Connection</code> method, such as <code>setAutoCommit</code> or <code>setTransactionIsolation</code>. Applications should not invoke SQL commands directly to change the connection's configuration. They should use the JDBC methods instead. By default a <code>Connection</code> object is in auto-commit mode, which means that it automatically commits changes after executing each statement. If auto-commit mode has been disabled, the method <code>commit</code> must be called explicitly in order to commit changes; otherwise, database changes will not be saved.
 * <P>
 * A new <code>Connection</code> object created using the JDBC 2.1 core API has an initially empty type map associated with it. A user may enter a custom mapping for a user-defined type (UDT) in this type map.
 * When a UDT is retrieved from a data source with the method <code>ResultSet.getObject</code>, the method <code>getObject</code> will check the connection's type map to see if there is an entry for that UDT. If so, the <code>getObject</code> method will map the UDT to the class indicated. If there is no entry, the UDT will be mapped using the standard mapping.
 * <p>
 * A user may create a new type map, which is a <code>java.util.Map</code> object, make an entry in it, and pass it to the <code>java.sql</code> methods that can perform custom mapping. In this case, the method will use the given type map instead of the one associated with the connection.
 * <p>
 * For example, the following code fragment specifies that the SQL type <code>ATHLETES</code> will be mapped to the class <code>Athletes</code> in the Java programming language. The code fragment retrieves the type map for the <code>Connection</code> object <code>con</code>, inserts the entry into it, and then sets the type map with the new entry as the connection's type map.
 * <pre>
 *      java.util.Map map = con.getTypeMap();
 *      map.put("mySchemaName.ATHLETES", Class.forName("Athletes"));
 *      con.setTypeMap(map);
 * </pre>
 *
 * @see DriverManager#getConnection
 * @see Statement
 * @see ResultSet
 * @see DatabaseMetaData
 * @since 1.1
 */
public interface Connection extends Wrapper, AutoCloseable {

    /**
     * Creates a <code>Statement</code> object for sending SQL statements to the database.
     * SQL statements without parameters are normally executed using <code>Statement</code> objects. If the same SQL statement is executed many times, it may be more efficient to use a <code>PreparedStatement</code> object.
     * <P>
     * Result sets created using the returned <code>Statement</code> object will by default be type <code>TYPE_FORWARD_ONLY</code> and have a concurrency level of <code>CONCUR_READ_ONLY</code>. The holdability of the created result sets can be determined by calling {@link #getHoldability}.
     *
     * @return a new default <code>Statement</code> object
     * @exception SQLException if a database access error occurs or this method is called on a closed connection
     */
    Statement createStatement() throws SQLException;

    /**
     * Creates a <code>PreparedStatement</code> object for sending parameterized SQL statements to the database.
     * <P>
     * A SQL statement with or without IN parameters can be pre-compiled and stored in a <code>PreparedStatement</code> object. This object can then be used to efficiently execute this statement multiple times.
     * <P><B>Note:</B> This method is optimized for handling parametric SQL statements that benefit from precompilation. If the driver supports precompilation, the method <code>prepareStatement</code> will send the statement to the database for precompilation. Some drivers may not support precompilation. In this case, the statement may not be sent to the database until the <code>PreparedStatement</code> object is executed. This has no direct effect on users; however, it does affect which methods throw certain <code>SQLException</code> objects.
     * <P>
     * Result sets created using the returned <code>PreparedStatement</code> object will by default be type <code>TYPE_FORWARD_ONLY</code> and have a concurrency level of <code>CONCUR_READ_ONLY</code>. The holdability of the created result sets can be determined by calling {@link #getHoldability}.
     *
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @return a new default <code>PreparedStatement</code> object containing the pre-compiled SQL statement
     * @exception SQLException if a database access error occurs or this method is called on a closed connection
     */
    PreparedStatement prepareStatement(String sql) throws SQLException;

    /**
     * Makes all changes made since the previous commit/rollback permanent and releases any database locks currently held by this <code>Connection</code> object.
     * This method should be used only when auto-commit mode has been disabled.
     *
     * @exception SQLException if a database access error occurs, this method is called while participating in a distributed transaction, if this method is called on a closed connection or this <code>Connection</code> object is in auto-commit mode
     * @see #setAutoCommit
     */
    void commit() throws SQLException;

    /**
     * Undoes all changes made in the current transaction and releases any database locks currently held by this <code>Connection</code> object.
     * This method should be used only when auto-commit mode has been disabled.
     *
     * @exception SQLException if a database access error occurs, this method is called while participating in a distributed transaction, if this method is called on a closed connection or this <code>Connection</code> object is in auto-commit mode
     * @see #setAutoCommit
     */
    void rollback() throws SQLException;

    /**
     * Releases this <code>Connection</code> object's database and JDBC resources immediately instead of waiting for them to be automatically released.
     * <P>
     * Calling the method <code>close</code> on a <code>Connection</code> object that is already closed is a no-op.
     * <P>
     * It is <b>strongly recommended</b> that an application explicitly commits or rolls back an active transaction prior to calling the <code>close</code> method. If the <code>close</code> method is called and there is an active transaction, the results are implementation-defined.
     *
     * @exception SQLException if a database access error occurs
     */
    void close() throws SQLException;
    
    // .... 
}
```

### 6. Best Practices

This example uses the Java JDBC API to connect to a database and run an insert. First it sets up a `DataSource` object to connect to MySQL.
Then it gets a database connection from this data source, turns off auto-commit, and starts a transaction. Next it prepares an SQL
insert statement and creates a `PreparedStatement`
to run the insert. After the insert, it commits the transaction and prints the number of affected rows. If an exception happens, it rolls back the transaction and prints the exception. Finally, it handles the exception that may occur when the connection is closed.

```java
public class ConnectionDemo {

    public static void main(String[] args) {
        // Using a DataSource makes it easy to change the database later
        MysqlDataSource dataSource = new MysqlDataSource();
        // Database URL, format: jdbc:driver-name://host:port/database-name
        dataSource.setUrl("jdbc:mysql://localhost:3306/spring-reading");
        // Database username
        dataSource.setUser("root");
        // Database password
        dataSource.setPassword("123456");

        try (Connection connection = dataSource.getConnection()) {
            // Turn off auto-commit to start a transaction
            connection.setAutoCommit(false);
            // SQL insert statement
            String sql = "insert into scores(score) values(?)";
            // Create a PreparedStatement to run the SQL insert
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set the parameter
                statement.setInt(1, new Random().nextInt(100));
                // Run the insert
                int row = statement.executeUpdate();
                // Simulate an exception to test transaction rollback
                // int i = 1 / 0;
                // Commit the transaction
                connection.commit();
                // Print the number of affected rows
                System.out.println("row = " + row);
            } catch (Exception e) {
                // Roll back the transaction on error
                connection.rollback();
                // Print the exception
                e.printStackTrace();
            }
        } catch (SQLException e) {
            // Handle the exception from closing the connection
            e.printStackTrace();
        }
    }
}
```