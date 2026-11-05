package com.xcs.spring.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author xcs
 * @date 2023-09-27 10:36
 **/
@Service
public class MyService {

    /**
     * Java's Timer is used here to simulate a scheduled task. In a real application, a more advanced scheduling tool may be used.
     */
    public void startScheduledTask() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        System.out.println(getDate() + " hello world ");
                    }
                },
                0,
                2000
        );
    }

    /**
     * Get the current time
     *
     * @return
     */
    public String getDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
