package com.xcs.spring;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xcs
 * @date 2023-11-30 15:27
 **/
public class SimplePropertyResolverDemo {

    public static void main(String[] args) {
        // Create the property source
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");
        properties.put("app.description", "This is a ${app.name} with version ${app.version}");

        MapPropertySource propertySource = new MapPropertySource("myPropertySource", properties);
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(propertySource);

        // Use PropertySourcesPropertyResolver
        PropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);

        // Get property
        String appName = propertyResolver.getProperty("app.name");
        String appVersion = propertyResolver.getProperty("app.version");
        System.out.println("Get property app.name: " + appName);
        System.out.println("Get property app.version: " + appVersion);

        // Check whether the properties exist
        boolean containsDescription = propertyResolver.containsProperty("app.description");
        boolean containsReleaseDate = propertyResolver.containsProperty("app.releaseDate");
        System.out.println("Contains 'app.description' property: " + containsDescription);
        System.out.println("Contains 'app.releaseDate' property: " + containsReleaseDate);

        // Get property with default value
        String appReleaseDate = propertyResolver.getProperty("app.releaseDate", "2023-11-30");
        System.out.println("Get property with default value app.releaseDate : " + appReleaseDate);

        // Get required property
        String requiredAppName = propertyResolver.getRequiredProperty("app.name");
        System.out.println("Get required property app.name: " + requiredAppName);

        // Resolve placeholders
        String appDescription = propertyResolver.resolvePlaceholders(properties.get("app.description").toString());
        System.out.println("Resolve placeholders app.description: " + appDescription);

        // Resolve required placeholders
        String requiredAppDescription = propertyResolver.resolveRequiredPlaceholders("App Description: ${app.description}");
        System.out.println("Resolve required placeholders : " + requiredAppDescription);
    }
}
