package com.xcs.spring;

import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

/**
 * @author xcs
 * @date 2023-11-30 18:15
 **/
public class EnvironmentDemo {

    public static void main(String[] args) {
        // Set a system property to simulate Spring profile behavior
        System.setProperty("spring.profiles.default", "dev");
        System.setProperty("spring.profiles.active", "test");

        // Create a StandardEnvironment instance to access properties and profile information
        Environment environment = new StandardEnvironment();

        // Use getProperty to read a system property. Here it reads the Java version
        String javaVersion = environment.getProperty("java.version");
        System.out.println("java.version: " + javaVersion);

        // Get the active profiles
        String[] activeProfiles = environment.getActiveProfiles();
        System.out.println("activeProfiles = " + String.join(",", activeProfiles));

        // Get the default profiles (used when no profile is active)
        String[] defaultProfiles = environment.getDefaultProfiles();
        System.out.println("defaultProfiles = " + String.join(",", defaultProfiles));

        // Check whether a given profile is active. Here it checks the 'test' profile
        boolean isDevProfileActive = environment.acceptsProfiles("test");
        System.out.println("acceptsProfiles('test'): " + isDevProfileActive);
    }
}
