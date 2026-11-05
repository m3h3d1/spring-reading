package com.xcs.spring;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

public class ScoresServiceImpl implements ScoresService {

    private JdbcTemplate jdbcTemplate;

    public ScoresServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void insertScore() throws Exception {
        // Primary key id
        long id = System.currentTimeMillis();
        // Score
        int score = new Random().nextInt(100);
        // Save the data
        int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
        // Print the number of affected rows
        System.out.println("scores row = " + row);
        // Simulate an exception
        // throw new Exception("Test exception");
    }
}
