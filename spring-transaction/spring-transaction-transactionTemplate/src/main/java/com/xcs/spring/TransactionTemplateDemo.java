package com.xcs.spring;

import com.mysql.jdbc.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;
import java.util.Random;

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
