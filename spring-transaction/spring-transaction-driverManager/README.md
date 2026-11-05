## DriverManager

- [DriverManager](#drivermanager)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Best Practices](#4-best-practices)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

`DriverManager` is a class in the Java standard library that manages JDBC drivers. It has static methods to load drivers and open database connections. This lets a Java
application talk to many kinds of databases easily.

### 3. Main Features

1. **Load database drivers**

    + Register a database driver with `registerDriver()`, so `DriverManager` can recognize and load the driver of a specific database.

2. **Open database connections**

    + With `getConnection()`, get a connection object from the given database URL, username, and password, so you can work with the database.

3. **Manage database connections**

    + `DriverManager` manages connection objects. It keeps connections safe and available, and has methods to get and close them.

4. **Unload drivers**

    + Unload a registered driver with `deregisterDriver()` to free the related resources.

### 4. Best Practices

This example uses the `java.sql.DriverManager` class to connect to a MySQL database and run a simple query. It opens a connection to the database "spring-reading" with the given
URL, username, and password. Then it runs a query to read data from the table "scores".
Finally, it loops over the result set and prints the "id" and "score" columns of each row. Then it closes the result set, the prepared statement, and the connection.

```java
public class DriverManagerDemo {

    public static void main(String[] args) throws Exception {
        // Database URL, format: jdbc:driver-name://host:port/database-name
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // Database username
        String username = "root";
        // Database password
        String password = "123456";

        // Open the database connection
        Connection connection = DriverManager.getConnection(url, username, password);
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
id:1,score:3.50
id:2,score:3.65
id:3,score:4.00
id:4,score:3.85
id:5,score:4.00
id:6,score:3.65
```

