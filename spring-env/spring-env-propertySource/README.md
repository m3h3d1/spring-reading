## PropertySource

- [PropertySource](#propertysource)
  - [1. Overview](#1-overview)
  - [2. Background Knowledge](#2-background-knowledge)
  - [3. Description](#3-description)
  - [4. Main Features](#4-main-features)
  - [5. Class Source](#5-class-source)
  - [6. Main Implementations](#6-main-implementations)
  - [7. Best Practices](#7-best-practices)
  - [8. Relationship with Other Components](#8-relationship-with-other-components)
  - [9. FAQ](#9-faq)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. **Java I/O and file handling**

   + You should know Java file I/O, especially how to read and write files. Know the Java classes that handle `.properties` files, such as `Properties`. Also know how to use classpath and file system paths to find and load resource files. This is key to loading property files from the file system or the classpath.

2. **Spring resource abstraction**

   + You should know Spring's [Resource](/spring-resources/spring-resource/README.md) interface and its implementations, such as `ClassPathResource`, `FileSystemResource`, and `UrlResource`. This includes using Spring's resource loading to read external config files. It helps you load config files from many locations.

3. **Java collections framework**

   + You should know the Java collections framework well, especially the `Map` interface and its implementations such as `HashMap` and `LinkedHashMap`. This includes how to store, get, and update key-value pairs in a Map. It is key to loading properties from a collection of key-value pairs.

4. **System environment and Java system properties**

   + You should know how to access operating system environment variables and system properties in Java. This includes `System.getenv()` and `System.getProperties()`. Understand how these variables are set and changed on different operating systems. This is important for loading configuration from the operating system environment.

5. **Command-line argument parsing**

   + You should know how to parse command-line arguments in a Java program. This includes different argument formats (such as flags and key-value pairs) and how to use third-party libraries to make parsing simpler. This is key to loading configuration from command-line arguments.

### 3. Description

The `PropertySource` class is a key abstract class in Spring. It wraps configuration data from different sources, such as files, environment variables, and system properties. It gives one unified interface to these sources, so you can access different kinds of configuration data in the same way. Its core is the `getProperty(String name)` method, which returns the property value for a given name. In Spring's environment abstraction, `PropertySource` instances can be added to an `Environment` object. This lets you access and manage these properties easily in your application.

### 4. Main Features

1. **Unified property source interface**

   + `PropertySource` gives one interface to access properties from different sources, such as files, environment variables, and system properties. This makes it simpler to handle configuration data in different environments.

2. **Property lookup and access**

   + You can get a property value by its key (the property name). This is the most basic feature of `PropertySource`. It supports reading all kinds of configuration data.

3. **Property source priority and overrides**

   + In a Spring environment, you can register many `PropertySource` instances. They are searched in a certain priority order. So you can change their priority to control which properties override others.

4. **Environment integration**

   + `PropertySource` instances are usually part of Spring's `Environment` abstraction. This makes it easier to access properties across the whole application.

5. **Extensible**

   + `PropertySource` is abstract, so you can write your own implementation. This lets you load properties from almost any data source, such as a database, a remote service, or a file in a non-standard format.

6. **Many data formats**

   + Spring provides many `PropertySource` implementations. They can load properties from formats such as standard property files, JSON files, and YAML files.

7. **Dynamic property updates**

   + Some `PropertySource` implementations support updating properties dynamically. This is very useful for applications that must change configuration at runtime.

8. **Configuration isolation and modularity**

   + `PropertySource` helps keep configuration in separate modules. This makes configuration management more modular and clear.

### 5. Class Source

The `PropertySource` class is the abstract base class that wraps property sources in Spring. It gives one unified way to access configuration data from many sources (such as property files and environment variables). It defines some core methods: `getProperty` to get a property value, `containsProperty` to check whether a property exists, and methods to get the name and the source object of the property source.

```java
/**
 * Abstract base class representing a source of name/value property pairs.
 * A property source is a collection of key-value pairs that can be loaded
 * from various sources, such as properties files and environment variables.
 *
 * @param <T> the type of the property source
 */
public abstract class PropertySource<T> {

    protected final Log logger = LogFactory.getLog(getClass());

    // The name of the property source
    protected final String name; 

    // The source object of the property source
    protected final T source; 

    /**
     * Create a new PropertySource with the given name and source object.
     *
     * @param name the name of the property source
     * @param source the source object of the property source
     */
    public PropertySource(String name, T source) {
        Assert.hasText(name, "Property source name must contain at least one character");
        Assert.notNull(source, "Property source must not be null");
        this.name = name;
        this.source = source;
    }

    /**
     * Overloaded constructor. Create a PropertySource with the given name and a new
     * Object instance as the source.
     * Usually used in tests, when you create an anonymous implementation
     * that does not need to query an actual source.
     *
     * @param name the name of the property source
     */
    @SuppressWarnings("unchecked")
    public PropertySource(String name) {
        this(name, (T) new Object());
    }

    /**
     * Return the name of this PropertySource.
     *
     * @return the name of the property source
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the underlying source object for this PropertySource.
     *
     * @return the source object of the property source
     */
    public T getSource() {
        return this.source;
    }

    /**
     * Return whether this PropertySource contains a property with the given name.
     * The default implementation only checks whether the value returned by
     * getProperty(String) is null.
     * Subclasses may implement a more efficient algorithm.
     *
     * @param name the property name to look for
     * @return true if the property is contained
     */
    public boolean containsProperty(String name) {
        return (getProperty(name) != null);
    }

    /**
     * Return the value associated with the given name,
     * or null if not found.
     * 
     * @param name the property name to look for
     * @return the property value, or null
     */
    @Nullable
    public abstract Object getProperty(String name);

    // Other methods (equals, hashCode, toString) are omitted
}
```

### 6. Main Implementations

1. **`RopertiesPropertySource`**

   - Loads properties from `.properties` files. It is usually built from a `Properties` object.

2. **`ResourcePropertySource`**

   - Extends `PropertiesPropertySource`. It can load properties directly from a `Resource` object (such as a file system resource or a classpath resource).

3. **`MapPropertySource`**

   - Loads properties from a `Map<String, Object>`. It is very flexible and can take properties from any key-value map.

4. **`SystemEnvironmentPropertySource`**

   - Accesses operating system environment variables. This class handles the naming rules of environment variables (for example, it converts all characters to lower case and replaces non-alphanumeric characters with underscores).

5. **`CommandLinePropertySource`**

   - Handles command-line arguments. It supports simple named arguments and more complex key-value arguments.

6. **`CompositePropertySource`**

   - A composite class that can hold many `PropertySource` instances. It combines many `PropertySource` objects into one logical unit, so they can be handled together.

### 7. Best Practices

This example shows how to use Spring's `PropertySource` class in a Java application to load configuration data from different sources: a property file, a resource file, a Map object, system environment variables, and command-line arguments. At the end, it uses `CompositePropertySource` to combine all these property sources, so the configuration from different sources can be accessed and managed together.

```java
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
```

The output shows that each `PropertySource` instance has its own data source and content. For example, the config items read from a file, the full list of environment variables, and the wrapped command-line arguments.

```java
Name: properties      || Source: {app.name=Spring-Reading}
Name: resource        || Source: {app.name=Spring-Reading}
Name: mapSource       || Source: {app.name=Spring-Reading, app.version=1.0.0}
Name: systemEnv       || Source: {USERDOMAIN_ROAMINGPROFILE=DESKTOP-HRS3987, PROCESSOR_LEVEL=6, SESSIONNAME=Console, ALLUSERSPROFILE=C:\ProgramData, PROCESSOR_ARCHITECTURE=AMD64, PSModulePath=C:\Program Files\WindowsPowerShell\Modules;C:\WINDOWS\system32\WindowsPowerShell\v1.0\Modules, SystemDrive=C:, MAVEN_HOME=D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4, USERNAME=Lenovo, ProgramFiles(x86)=C:\Program Files (x86), FPS_BROWSER_USER_PROFILE_STRING=Default, PATHEXT=.COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC, DriverData=C:\Windows\System32\Drivers\DriverData, OneDriveConsumer=C:\Users\Lenovo\OneDrive, GOPATH=C:\Users\Lenovo\go, ProgramData=C:\ProgramData, ProgramW6432=C:\Program Files, HOMEPATH=\Users\Lenovo, MYSQL_HOME=C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0, PROCESSOR_IDENTIFIER=Intel64 Family 6 Model 165 Stepping 3, GenuineIntel, M2_HOME=D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4, ProgramFiles=C:\Program Files, PUBLIC=C:\Users\Public, windir=C:\WINDOWS, =::=::\, ZES_ENABLE_SYSMAN=1, LOCALAPPDATA=C:\Users\Lenovo\AppData\Local, ChocolateyLastPathUpdate=133456990830913519, USERDOMAIN=DESKTOP-HRS3987, FPS_BROWSER_APP_PROFILE_STRING=Internet Explorer, LOGONSERVER=\\DESKTOP-HRS3987, JAVA_HOME=D:\install\jdk, EFC_9756=1, OneDrive=C:\Users\Lenovo\OneDrive, APPDATA=C:\Users\Lenovo\AppData\Roaming, ChocolateyInstall=C:\ProgramData\chocolatey, CommonProgramFiles=C:\Program Files\Common Files, Path=D:\app\Lenovo\product\11.2.0\dbhome_1\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;D:\install\Git\cmd;D:\install\jdk\bin;D:\install\jdk\jre\bin;C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\install\go\bin;D:\install\Xshell\;D:\install\Xftp\;C:\Program Files\dotnet\;C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\;C:\Program Files\Microsoft SQL Server\150\Tools\Binn\;D:\tools\x86_64-8.1.0-release-win32-seh-rt_v6-rev0\mingw64\bin;;C:\Program Files\Docker\Docker\resources\bin;C:\Program Files\nodejs\;C:\Users\Lenovo\AppData\Local\Microsoft\WindowsApps;;C:\Users\Lenovo\AppData\Local\Programs\Fiddler;C:\WINDOWS\system32\config\systemprofile\go\bin;C:\WINDOWS\system32\config\systemprofile\AppData\Local\Microsoft\WindowsApps;D:\install\Microsoft VS Code\bin;C:\WINDOWS\system32\config\systemprofile\.dotnet\tools;D:\install\lens\resources\cli\bin;C:\Users\Lenovo\AppData\Roaming\npm, OS=Windows_NT, COMPUTERNAME=DESKTOP-HRS3987, PROCESSOR_REVISION=a503, CLASSPATH=.;D:\install\jdk\lib\dt.jar;D:\install\jdk\lib\tools.jar;, CommonProgramW6432=C:\Program Files\Common Files, ComSpec=C:\WINDOWS\system32\cmd.exe, SystemRoot=C:\WINDOWS, TEMP=C:\Users\Lenovo\AppData\Local\Temp, HOMEDRIVE=C:, USERPROFILE=C:\Users\Lenovo, TMP=C:\Users\Lenovo\AppData\Local\Temp, CommonProgramFiles(x86)=C:\Program Files (x86)\Common Files, NUMBER_OF_PROCESSORS=12, IDEA_INITIAL_DIRECTORY=D:\tools\ideaIU-2021.2.2.win\bin}
Name: commandLineArgs || Source: org.springframework.core.env.CommandLineArgs@6321e813
```

### 8. Relationship with Other Components

1. **Environment**

   + `PropertySource` gives configuration data to `Environment`. `Environment` is an interface that represents the runtime environment of the application and combines different property sources.

2. **ApplicationContext**

   + The properties in a `PropertySource` are loaded by the `ApplicationContext`. They affect and configure the beans and other configuration in the application context.

3. **@PropertySource annotation**

   + The `@PropertySource` annotation declares an external config file as a `PropertySource`. It is then added to Spring's `Environment`.

4. **PropertyResolver**

   + The `PropertyResolver` interface resolves properties from a `PropertySource`. It is the bridge between property sources and the way they are queried.

5. **@Value annotation**

   + The `@Value` annotation injects property values from a `PropertySource` directly into fields of Spring-managed beans.

6. **Profile**

   + `PropertySource` supports different configuration based on Spring Profiles. So you can load a different set of properties for the environment the application runs in.

7. **PlaceholderConfigurer classes**

   + Configurers such as `PropertySourcesPlaceholderConfigurer` resolve property placeholders in a `PropertySource` and apply them to Spring bean configuration.

### 9. FAQ

1. **Property value not found**

   + If reading a property that does not exist returns `null` or throws an exception, make sure the property file or other property source is loaded correctly. Also check that the property name is spelled correctly.

2. **Property override problems**

   + When several `PropertySource` objects have a property with the same name, it may be unclear which value is used. Make the priority order of the `PropertySource` objects clear. Learn how `Environment` handles the same property in many property sources.

3. **Property type conversion problems**

   + Property values from a `PropertySource` are strings by default. You may need to convert them to other types. Use suitable conversion logic (such as `ConversionService`) or convert the value by hand.

4. **Dynamic property update problems**

   + To update properties in a `PropertySource` at runtime and reflect the changes, use a `PropertySource` implementation that supports dynamic updates, such as Spring Cloud Config.

5. **Conflicts between environment variables and system properties**

   + A system environment variable and a Java system property may have the same key. This can cause unexpected overrides. Understand the priority of `SystemEnvironmentPropertySource` and `SystemPropertiesPropertySource`, and adjust it to avoid conflicts.

6. **`@Value` is not resolved**

   + When you inject properties with `@Value` and the value is not resolved, make sure there is an active `PropertySourcesPlaceholderConfigurer` in the context to resolve the placeholders.

7. **Config file path problems**

   + When you load an external config file and it is not loaded because of a path problem, check that the file path is correct. Make sure the config file is in a location that can be accessed.
