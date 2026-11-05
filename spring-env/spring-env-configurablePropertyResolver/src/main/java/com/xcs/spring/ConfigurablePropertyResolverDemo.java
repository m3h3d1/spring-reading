package com.xcs.spring;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySourcesPropertyResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xcs
 * @date 2023-11-30 16:40
 **/
public class ConfigurablePropertyResolverDemo {

    public static void main(String[] args) {
        // Create the property source
        Map<String, Object> properties = new HashMap<>();
        properties.put("app.name", "Spring-Reading");
        properties.put("app.version", "1.0.0");

        MapPropertySource propertySource = new MapPropertySource("myPropertySource", properties);
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(propertySource);

        // Create a ConfigurablePropertyResolver
        PropertySourcesPropertyResolver propertyResolver = new PropertySourcesPropertyResolver(propertySources);

        // Set and get the conversion service
        ConfigurableConversionService conversionService = new DefaultConversionService();
        propertyResolver.setConversionService(conversionService);

        // Set the placeholder prefix and suffix
        propertyResolver.setPlaceholderPrefix("${");
        propertyResolver.setPlaceholderSuffix("}");

        // Set the default value separator
        propertyResolver.setValueSeparator(":");

        // Set how unresolvable placeholders are handled
        propertyResolver.setIgnoreUnresolvableNestedPlaceholders(true);

        // Set and validate required properties
        propertyResolver.setRequiredProperties("app.name", "app.version");
        propertyResolver.validateRequiredProperties();

        // Read properties
        String appName = propertyResolver.getProperty("app.name");
        String appVersion = propertyResolver.getProperty("app.version", String.class, "Unknown Version");
        System.out.println("Get property app.name: " + appName);
        System.out.println("Get property app.version: " + appVersion);
    }
}
