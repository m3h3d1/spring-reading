package com.xcs.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class ScoresServiceImpl implements ScoresService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void insertScore() {
        long id = System.currentTimeMillis();
        int score = new Random().nextInt(100);
        // Insert a random score into the database
        int row = jdbcTemplate.update("insert into scores(id,score) values(?,?)", id, score);
        // Simulate an exception to test transaction rollback
        // int i = 1 / 0;
        // Print the number of affected rows
        System.out.println("scores row = " + row);
    }
}
