package com.xcs.spring;

import com.mysql.jdbc.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.util.List;

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
        // Create a JdbcTemplate to run SQL statements
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        List<Scores> scoresList = jdbcTemplate.query("select * from scores ", (rs, rowNum) -> {
            Scores scores = new Scores();
            scores.setId(rs.getLong("id"));
            scores.setScore(rs.getLong("score"));
            return scores;
        });

        scoresList.forEach(System.out::println);
    }
}
