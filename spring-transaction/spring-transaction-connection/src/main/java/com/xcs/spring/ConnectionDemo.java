package com.xcs.spring;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

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
