package com.xcs.spring;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xcs
 * @date 2023-12-01 14:05
 **/
public class ConfigurableEnvironmentDemo {

    public static void main(String[] args) {
        // Create a StandardEnvironment instance to access properties and profile information
        ConfigurableEnvironment environment = new StandardEnvironment();

        // Set profiles
        environment.setActiveProfiles("dev");
        System.out.println("Active Profiles: " + String.join(", ", environment.getActiveProfiles()));

        // Add a profile
        environment.addActiveProfile("test");
        System.out.println("Updated Active Profiles: " + String.join(", ", environment.getActiveProfiles()));

        // Set default profiles
        environment.setDefaultProfiles("default");
        System.out.println("Default Profiles: " + String.join(", ", environment.getDefaultProfiles()));

        // Get system properties
        Map<String, Object> systemProperties = environment.getSystemProperties();
        System.out.println("System Properties: " + systemProperties);

        // Get system environment variables
        Map<String, Object> systemEnvironment = environment.getSystemEnvironment();
        System.out.println("System Environment: " + systemEnvironment);

        // Merge environment variables
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");
        StandardEnvironment standardEnvironment = new StandardEnvironment();
        standardEnvironment.getPropertySources().addFirst(new MapPropertySource("myEnvironment", properties));
        environment.merge(standardEnvironment);

        // Get the mutable property sources
        MutablePropertySources propertySources = environment.getPropertySources();
        System.out.println("MutablePropertySources: " + propertySources);
    }
}
