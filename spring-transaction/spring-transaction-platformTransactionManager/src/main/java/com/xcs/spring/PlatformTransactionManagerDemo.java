package com.xcs.spring;

import com.mysql.jdbc.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Random;

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
        // Transaction definition
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        // Start a new transaction and return its status object
        TransactionStatus transactionStatus = transactionManager.getTransaction(definition);
        try {
            long id = System.currentTimeMillis();
            int score = new Random().nextInt(100);
            // Insert a random score into the database
            int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
            insertScoreLog(id);
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

    private static void insertScoreLog(long scoreId) {
        // Transaction definition
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        // Start a new transaction and return its status object
        TransactionStatus transactionStatus = transactionManager.getTransaction(definition);
        try {
            long id = System.currentTimeMillis();
            LocalDateTime createTime = LocalDateTime.now();
            // Insert a random score into the database
            int row = jdbcTemplate.update("insert into scores_log(id,score_id,create_time) values(?,?,?)", id, scoreId, createTime);
            // Simulate an exception to test transaction rollback
            // int i = 1 / 0;
            // Commit the transaction
            transactionManager.commit(transactionStatus);
            // Print the number of affected rows
            System.out.println("scores_log row = " + row);
        } catch (Exception e) {
            // Roll back the transaction on error
            transactionManager.rollback(transactionStatus);
            e.printStackTrace();
        }
    }
}
