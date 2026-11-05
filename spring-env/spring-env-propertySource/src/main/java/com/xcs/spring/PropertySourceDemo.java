package com.xcs.spring;

import org.springframework.core.env.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author xcs
 * @date 2023-12-01 16:12
 **/
public class PropertySourceDemo {

    public static void main(String[] args) throws Exception {
        // Load properties from a .properties file
        Properties source = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
        PropertySource propertySource = new PropertiesPropertySource("properties", source);

        // Load properties directly from a Resource
        ClassPathResource classPathResource = new ClassPathResource("application.properties");
        PropertySource resourcePropertySource = new ResourcePropertySource("resource", classPathResource);

        // Load properties from a Map
        Map<String, Object> map = new HashMap<>();
        map.put("app.name", "Spring-Reading");
        map.put("app.version", "1.0.0");
        PropertySource mapPropertySource = new MapPropertySource("mapSource", map);

        // Access system environment variables
        Map mapEnv = System.getenv();
        PropertySource envPropertySource = new SystemEnvironmentPropertySource("systemEnv", mapEnv);

        // Parse command-line arguments
        String[] myArgs = {"--appName=MyApplication", "--port=8080"};
        PropertySource commandLinePropertySource = new SimpleCommandLinePropertySource(myArgs);

        // Combine multiple PropertySource instances
        CompositePropertySource composite = new CompositePropertySource("composite");
        composite.addPropertySource(propertySource);
        composite.addPropertySource(resourcePropertySource);
        composite.addPropertySource(mapPropertySource);
        composite.addPropertySource(envPropertySource);
        composite.addPropertySource(commandLinePropertySource);

        // Print the result
        for (PropertySource<?> ps : composite.getPropertySources()) {
            System.out.printf("Name: %-15s || Source: %s%n", ps.getName(), ps.getSource());
        }
    }
}
