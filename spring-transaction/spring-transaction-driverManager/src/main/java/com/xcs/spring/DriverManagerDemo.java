package com.xcs.spring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
