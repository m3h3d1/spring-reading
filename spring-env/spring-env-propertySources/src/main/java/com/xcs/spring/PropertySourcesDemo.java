package com.xcs.spring;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xcs
 * @date 2023-12-02 10:29
 **/
public class PropertySourcesDemo {

    public static void main(String[] args) {
        // Create a MutablePropertySources object
        MutablePropertySources propertySources = new MutablePropertySources();

        // Create two MapPropertySource objects
        Map<String, Object> config1 = new HashMap<>();
        config1.put("key1", "value1");
        PropertySource<?> mapPropertySource1 = new MapPropertySource("config1", config1);

        Map<String, Object> config2 = new HashMap<>();
        config2.put("key2", "value2");
        PropertySource<?> mapPropertySource2 = new MapPropertySource("config2", config2);

        // Add a property source at the start
        propertySources.addFirst(mapPropertySource1);
        // Add a property source at the end
        propertySources.addLast(mapPropertySource2);

        // Print
        System.out.println("Print property sources");
        for (PropertySource<?> ps : propertySources) {
            System.out.printf("Name: %-10s || Source: %s%n", ps.getName(), ps.getSource());
        }
        System.out.println();

        // Replace a property source
        Map<String, Object> newConfig = new HashMap<>();
        newConfig.put("app.name", "Spring-Reading");
        newConfig.put("app.version", "1.0.0");
        PropertySource<?> newMapPropertySource = new MapPropertySource("config1", newConfig);
        propertySources.replace("config1", newMapPropertySource);

        // Print after replacing
        System.out.println("Print property sources after replacing");
        for (PropertySource<?> ps : propertySources) {
            System.out.printf("Name: %-10s || Source: %s%n", ps.getName(), ps.getSource());
        }
        System.out.println();

        // Check whether a property source exists
        System.out.println("Contains property source config2: " + propertySources.contains("config2"));

        // Remove a property source
        System.out.println("Remove property source config2: " + propertySources.remove("config2"));

        // Check again whether the property source exists
        System.out.println("Contains property source config2 after removal: " + propertySources.contains("config2"));
    }
}
