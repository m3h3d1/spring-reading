## ConfigurableEnvironment

- [ConfigurableEnvironment](#configurableenvironment)
  - [1. Overview](#1-overview)
  - [2. Background Knowledge](#2-background-knowledge)
  - [3. Description](#3-description)
  - [4. Main Features](#4-main-features)
  - [5. Interface Source](#5-interface-source)
  - [6. Main Implementations](#6-main-implementations)
  - [7. Best Practices](#7-best-practices)
  - [8. Relationship with Other Components](#8-relationship-with-other-components)
  - [9. FAQ](#9-faq)


### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [My CSDN]() 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Background Knowledge

1. **PropertyResolver**

   + The [PropertyResolver](/spring-env/spring-env-propertyResolver/README.md) interface is a core component of Spring. It gives a flexible and powerful way to handle application configuration properties. It defines methods to access and work with property values from many sources, such as property files, environment variables, and JVM arguments.

2. **ConfigurablePropertyResolver**

   + The [ConfigurablePropertyResolver](/spring-env/spring-env-configurablePropertyResolver/README.md) interface gives flexible resolution of configuration properties in Spring. It reads and converts property values from many sources. It supports placeholder resolution to make configuration more dynamic. It converts types so that property values have the expected format. It can also check whether a property exists and handle default values, which makes code more robust.

3. **Environment**

   + The [Environment](/spring-env/spring-env-environment/README.md) interface is a core part of Spring. It gives one unified way to access external configuration data, such as environment variables, JVM system properties, command-line arguments, and application config files (such as properties or YAML files).

### 3. Description

`ConfigurableEnvironment` is a core Spring interface. It manages and accesses the configuration environment of the application in a flexible way. It gives one interface to handle configuration data from different sources (such as property files, environment variables, and command-line arguments). It also lets you add, remove, or change these property sources at runtime.

### 4. Main Features

1. **Property source management**

   + Manages and works with property sources (PropertySources). You can add, remove, and reorder them. This lets you set up and access properties from different sources (such as property files, environment variables, and command-line arguments) in a flexible way.

2. **Profile activation**

   + Supports profile activation. For example, you can activate different profiles for different environments (development, test, production), through the `@Profile` annotation or by setting `spring.profiles.active`.

3. **Property resolution**

   + Resolves property values. It can convert configuration values to many data types (such as strings, numbers, and booleans).

4. **Environment abstraction**

   + As part of the Spring environment, it gives an abstraction layer. This layer handles configuration data from different sources in one way.

5. **Property overrides and priority**

   + Supports property overrides. Some configuration (such as environment variables) can take priority over other configuration (such as application property files).

6. **Dynamic property changes**

   + You can add, remove, or change property sources while the application runs. This gives the application more flexibility and dynamic configuration.

### 5. Interface Source

`ConfigurableEnvironment` is the core Spring interface for environment configuration. It lets you set the active and default profiles. It also lets you work with the underlying property sources, so configuration is more flexible and fits the needs of different environments. The interface supports adding, removing, reordering, or replacing property sources, and adding new ones.

```java
/**
 * Configuration interface to be implemented by most if not all {@link Environment} types.
 * Provides facilities for setting active and default profiles and manipulating the
 * underlying property sources. Allows clients to set and validate required properties,
 * customize the conversion service and more through the {@link ConfigurablePropertyResolver}
 * superinterface.
 *
 * <h2>Manipulating property sources</h2>
 * <p>Property sources may be removed, reordered, or replaced; and additional
 * property sources may be added using the {@link MutablePropertySources}
 * instance returned from {@link #getPropertySources()}. The following examples
 * are against the {@link StandardEnvironment} implementation of
 * {@code ConfigurableEnvironment}, but are generally applicable to any implementation,
 * though particular default property sources may differ.
 *
 * <h4>Example: adding a new property source with highest search priority</h4>
 * <pre class="code">
 * ConfigurableEnvironment environment = new StandardEnvironment();
 * MutablePropertySources propertySources = environment.getPropertySources();
 * Map&lt;String, String&gt; myMap = new HashMap&lt;&gt;();
 * myMap.put("xyz", "myValue");
 * propertySources.addFirst(new MapPropertySource("MY_MAP", myMap));
 * </pre>
 *
 * <h4>Example: removing the default system properties property source</h4>
 * <pre class="code">
 * MutablePropertySources propertySources = environment.getPropertySources();
 * propertySources.remove(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME)
 * </pre>
 *
 * <h4>Example: mocking the system environment for testing purposes</h4>
 * <pre class="code">
 * MutablePropertySources propertySources = environment.getPropertySources();
 * MockPropertySource mockEnvVars = new MockPropertySource().withProperty("xyz", "myValue");
 * propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars);
 * </pre>
 *
 * When an {@link Environment} is being used by an {@code ApplicationContext}, it is
 * important that any such {@code PropertySource} manipulations be performed
 * <em>before</em> the context's {@link
 * org.springframework.context.support.AbstractApplicationContext#refresh() refresh()}
 * method is called. This ensures that all property sources are available during the
 * container bootstrap process, including use by {@linkplain
 * org.springframework.context.support.PropertySourcesPlaceholderConfigurer property
 * placeholder configurers}.
 *
 * @author Chris Beams
 * @since 3.1
 * @see StandardEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 */
public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyResolver {

    /**
     * Specify the set of profiles active for this {@code Environment}. Profiles are
     * evaluated during container bootstrap to determine whether bean definitions
     * should be registered with the container.
     * <p>Any existing active profiles will be replaced with the given arguments; call
     * with zero arguments to clear the current set of active profiles. Use
     * {@link #addActiveProfile} to add a profile while preserving the existing set.
     * @throws IllegalArgumentException if any profile is null, empty or whitespace-only
     */
    void setActiveProfiles(String... profiles);

    /**
     * Add a profile to the current set of active profiles.
     * @throws IllegalArgumentException if the profile is null, empty or whitespace-only
     */
    void addActiveProfile(String profile);

    /**
     * Specify the set of profiles to be made active by default if no other profiles
     * are explicitly made active through {@link #setActiveProfiles}.
     * @throws IllegalArgumentException if any profile is null, empty or whitespace-only
     */
    void setDefaultProfiles(String... profiles);

    /**
     * Return the {@link PropertySources} for this {@code Environment} in mutable form,
     * allowing for manipulation of the set of {@link PropertySource} objects that should
     * be searched when resolving properties against this {@code Environment} object.
     * The various {@link MutablePropertySources} methods such as
     * {@link MutablePropertySources#addFirst addFirst},
     * {@link MutablePropertySources#addLast addLast},
     * {@link MutablePropertySources#addBefore addBefore} and
     * {@link MutablePropertySources#addAfter addAfter} allow for fine-grained control
     * over property source ordering.
     */
    MutablePropertySources getPropertySources();

    /**
     * Return the value of {@link System#getProperties()} if allowed by the current
     * {@link SecurityManager}, otherwise return a map implementation that will attempt
     * to access individual keys using calls to {@link System#getProperty(String)}.
     */
    Map<String, Object> getSystemProperties();

    /**
     * Return the value of {@link System#getenv()} if allowed by the current
     * {@link SecurityManager}, otherwise return a map implementation that will attempt
     * to access individual keys using calls to {@link System#getenv(String)}.
     */
    Map<String, Object> getSystemEnvironment();

    /**
     * Append the given parent environment's active profiles, default profiles and
     * property sources to this (child) environment's respective collections of each.
     * <p>For any identically-named {@code PropertySource} instance existing in both
     * parent and child, the child instance is to be preserved and the parent instance
     * discarded. This has the effect of allowing overriding of property sources by the
     * child as well as avoiding redundant searches through common property source types,
     * e.g. system environment and system properties.
     * <p>Active and default profile names are also filtered for duplicates, to avoid
     * confusion and redundant storage.
     * <p>The parent environment remains unmodified in any case. Note that any changes to
     * the parent environment occurring after the call to {@code merge} will not be
     * reflected in the child. Therefore, care should be taken to configure parent
     * property sources and profile information prior to calling {@code merge}.
     */
    void merge(ConfigurableEnvironment parent);
}
```

### 6. Main Implementations

1. **AbstractEnvironment**

   - The abstract base class of the `Environment` interface. It gives the common implementation that other classes extend.   

2. **StandardEnvironment**

   - A general implementation. It handles system properties and environment variables. It suits most standard applications.

3. **StandardServletEnvironment**

   - For Servlet-based web applications. It adds support for the Servlet context and config parameters.

### 7. Best Practices

This example uses `ConfigurableEnvironment` to manage and work with the configuration environment of the application. It activates specific profiles, gets and merges system properties and environment variables, and gets and works with property sources.

```java
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
```

The output shows that `ConfigurableEnvironment` has strong features to manage and access the configuration data of the application. By setting the active and default profiles, getting system properties and environment variables, and working with the mutable property sources, you can configure the application flexibly for different needs and environments.

```java
Active Profiles: dev
Updated Active Profiles: dev, test
Default Profiles: default
System Properties: {sun.desktop=windows, awt.toolkit=sun.awt.windows.WToolkit, java.specification.version=11, sun.cpu.isalist=amd64, sun.jnu.encoding=GBK, java.class.path=D:\idea-work-space-xcs\spring-reading\spring-env\spring-env-configurableEnvironment\target\classes;D:\tools\repository\org\springframework\spring-context\5.3.10\spring-context-5.3.10.jar;D:\tools\repository\org\springframework\spring-beans\5.3.10\spring-beans-5.3.10.jar;D:\tools\repository\org\springframework\spring-core\5.3.10\spring-core-5.3.10.jar;D:\tools\repository\org\springframework\spring-jcl\5.3.10\spring-jcl-5.3.10.jar;D:\tools\repository\org\springframework\spring-expression\5.3.10\spring-expression-5.3.10.jar;D:\tools\repository\org\springframework\spring-aspects\5.3.10\spring-aspects-5.3.10.jar;D:\tools\repository\org\aspectj\aspectjweaver\1.9.7\aspectjweaver-1.9.7.jar;D:\tools\repository\org\springframework\spring-aop\5.3.10\spring-aop-5.3.10.jar;D:\tools\repository\org\springframework\spring-tx\5.3.10\spring-tx-5.3.10.jar;D:\tools\repository\org\springframework\spring-webmvc\5.3.10\spring-webmvc-5.3.10.jar;D:\tools\repository\org\springframework\spring-web\5.3.10\spring-web-5.3.10.jar, java.vm.vendor=Oracle Corporation, sun.arch.data.model=64, user.variant=, java.vendor.url=http://java.oracle.com/, user.timezone=, os.name=Windows 10, java.vm.specification.version=11, sun.java.launcher=SUN_STANDARD, user.country=CN, sun.boot.library.path=D:\install\jdk-11\bin, sun.java.command=com.xcs.spring.ConfigurableEnvironmentDemo, jdk.debug=release, sun.cpu.endian=little, user.home=C:\Users\Lenovo, user.language=zh, java.specification.vendor=Oracle Corporation, java.version.date=2018-09-25, java.home=D:\install\jdk-11, file.separator=\, java.vm.compressedOopsMode=Zero based, line.separator=
, java.specification.name=Java Platform API Specification, java.vm.specification.vendor=Oracle Corporation, java.awt.graphicsenv=sun.awt.Win32GraphicsEnvironment, user.script=, sun.management.compiler=HotSpot 64-Bit Tiered Compilers, java.runtime.version=11+28, user.name=Lenovo, path.separator=;, os.version=10.0, java.runtime.name=Java(TM) SE Runtime Environment, file.encoding=UTF-8, java.vm.name=Java HotSpot(TM) 64-Bit Server VM, java.vendor.version=18.9, java.vendor.url.bug=http://bugreport.java.com/bugreport/, java.io.tmpdir=C:\Users\Lenovo\AppData\Local\Temp\, java.version=11, user.dir=D:\idea-work-space-xcs\spring-reading, os.arch=amd64, java.vm.specification.name=Java Virtual Machine Specification, java.awt.printerjob=sun.awt.windows.WPrinterJob, sun.os.patch.level=, java.library.path=D:\install\jdk-11\bin;C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;D:\app\Lenovo\product\11.2.0\dbhome_1\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;D:\install\Git\cmd;D:\install\jdk\bin;D:\install\jdk\jre\bin;C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\install\go\bin;D:\install\Xshell\;D:\install\Xftp\;C:\Program Files\dotnet\;C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\;C:\Program Files\Microsoft SQL Server\150\Tools\Binn\;D:\tools\x86_64-8.1.0-release-win32-seh-rt_v6-rev0\mingw64\bin;;C:\Program Files\Docker\Docker\resources\bin;C:\Program Files\nodejs\;C:\Users\Lenovo\AppData\Local\Microsoft\WindowsApps;;C:\Users\Lenovo\AppData\Local\Programs\Fiddler;C:\WINDOWS\system32\config\systemprofile\go\bin;C:\WINDOWS\system32\config\systemprofile\AppData\Local\Microsoft\WindowsApps;D:\install\Microsoft VS Code\bin;C:\WINDOWS\system32\config\systemprofile\.dotnet\tools;D:\install\lens\resources\cli\bin;C:\Users\Lenovo\AppData\Roaming\npm;., java.vendor=Oracle Corporation, java.vm.info=mixed mode, java.vm.version=11+28, sun.io.unicode.encoding=UnicodeLittle, java.class.version=55.0}
System Environment: {USERDOMAIN_ROAMINGPROFILE=DESKTOP-HRS3987, PROCESSOR_LEVEL=6, SESSIONNAME=Console, ALLUSERSPROFILE=C:\ProgramData, PROCESSOR_ARCHITECTURE=AMD64, PSModulePath=C:\Program Files\WindowsPowerShell\Modules;C:\WINDOWS\system32\WindowsPowerShell\v1.0\Modules, SystemDrive=C:, MAVEN_HOME=D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4, USERNAME=Lenovo, ProgramFiles(x86)=C:\Program Files (x86), FPS_BROWSER_USER_PROFILE_STRING=Default, PATHEXT=.COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC, DriverData=C:\Windows\System32\Drivers\DriverData, OneDriveConsumer=C:\Users\Lenovo\OneDrive, GOPATH=C:\Users\Lenovo\go, ProgramData=C:\ProgramData, ProgramW6432=C:\Program Files, HOMEPATH=\Users\Lenovo, MYSQL_HOME=C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0, PROCESSOR_IDENTIFIER=Intel64 Family 6 Model 165 Stepping 3, GenuineIntel, M2_HOME=D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4, ProgramFiles=C:\Program Files, PUBLIC=C:\Users\Public, windir=C:\WINDOWS, =::=::\, ZES_ENABLE_SYSMAN=1, LOCALAPPDATA=C:\Users\Lenovo\AppData\Local, ChocolateyLastPathUpdate=133456990830913519, USERDOMAIN=DESKTOP-HRS3987, FPS_BROWSER_APP_PROFILE_STRING=Internet Explorer, LOGONSERVER=\\DESKTOP-HRS3987, JAVA_HOME=D:\install\jdk, EFC_9756=1, OneDrive=C:\Users\Lenovo\OneDrive, APPDATA=C:\Users\Lenovo\AppData\Roaming, ChocolateyInstall=C:\ProgramData\chocolatey, CommonProgramFiles=C:\Program Files\Common Files, Path=D:\app\Lenovo\product\11.2.0\dbhome_1\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;D:\install\Git\cmd;D:\install\jdk\bin;D:\install\jdk\jre\bin;C:\ProgramData\Microsoft\Windows\Start Menu\Programs\MySQL\MySQL Server 8.0\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\tools\apache-maven-3.8.4-bin\apache-maven-3.8.4\bin;D:\install\go\bin;D:\install\Xshell\;D:\install\Xftp\;C:\Program Files\dotnet\;C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\;C:\Program Files\Microsoft SQL Server\150\Tools\Binn\;D:\tools\x86_64-8.1.0-release-win32-seh-rt_v6-rev0\mingw64\bin;;C:\Program Files\Docker\Docker\resources\bin;C:\Program Files\nodejs\;C:\Users\Lenovo\AppData\Local\Microsoft\WindowsApps;;C:\Users\Lenovo\AppData\Local\Programs\Fiddler;C:\WINDOWS\system32\config\systemprofile\go\bin;C:\WINDOWS\system32\config\systemprofile\AppData\Local\Microsoft\WindowsApps;D:\install\Microsoft VS Code\bin;C:\WINDOWS\system32\config\systemprofile\.dotnet\tools;D:\install\lens\resources\cli\bin;C:\Users\Lenovo\AppData\Roaming\npm, OS=Windows_NT, COMPUTERNAME=DESKTOP-HRS3987, PROCESSOR_REVISION=a503, CLASSPATH=.;D:\install\jdk\lib\dt.jar;D:\install\jdk\lib\tools.jar;, CommonProgramW6432=C:\Program Files\Common Files, ComSpec=C:\WINDOWS\system32\cmd.exe, SystemRoot=C:\WINDOWS, TEMP=C:\Users\Lenovo\AppData\Local\Temp, HOMEDRIVE=C:, USERPROFILE=C:\Users\Lenovo, TMP=C:\Users\Lenovo\AppData\Local\Temp, CommonProgramFiles(x86)=C:\Program Files (x86)\Common Files, NUMBER_OF_PROCESSORS=12, IDEA_INITIAL_DIRECTORY=D:\tools\ideaIU-2021.2.2.win\bin}
MutablePropertySources: [PropertiesPropertySource {name='systemProperties'}, SystemEnvironmentPropertySource {name='systemEnvironment'}, MapPropertySource {name='myEnvironment'}]
```

### 8. Relationship with Other Components

1. **ApplicationContext**

   + In Spring's `ApplicationContext`, `ConfigurableEnvironment` is used to load and manage the configuration data of the application. The `ApplicationContext` uses `Environment` to resolve config files and properties. It supports configuration for different environments (development, test, production).

2. **PropertySource**

   + `ConfigurableEnvironment` is closely tied to `PropertySource` objects. `PropertySources` is a collection of property sources. They define where configuration data comes from, such as system environment variables, JVM properties, and config files. `ConfigurableEnvironment` lets you add, remove, or customize these property sources.

3. **Profile**

   + `ConfigurableEnvironment` manages Spring profiles (Profiles). You can activate or deactivate specific bean definitions based on the current environment (for example development, test, or production). This is useful when you build applications for many environments with different configuration needs.

4. **PropertyResolver**

   + `ConfigurableEnvironment` extends `ConfigurablePropertyResolver`. This lets it resolve property values. This includes looking up properties in property sources and converting property values to many data types.

5. **BeanFactory**

   + In the Spring bean lifecycle, `Environment` affects how beans are created and configured. For example, the `@Value` annotation injects environment properties into a bean. The `@Conditional` annotation uses environment conditions to decide whether to create a bean.

6. **PropertySourcesPlaceholderConfigurer**

   + A special bean that resolves placeholders in config files. It works with the `Environment` interface. It lets you use environment properties when you define beans.

7. **Spring Boot**

   + In a Spring Boot application, `ConfigurableEnvironment` matters even more. It supports many kinds of externalized configuration and complex configuration order, including flexible handling of config files and conditional configuration.

### 9. FAQ

1. **Property values are not read or resolved correctly**

   + If `environment.getProperty()` returns `null` or a wrong value, first make sure the property name is correct. Also check that the property source was added to the `ConfigurableEnvironment`. Check the order in which property sources were added too, because it can cause unexpected overrides.

2. **Active profiles do not take effect**

   + If you set active profiles but the application does not use them, make sure you set them before the Spring container fully starts. In some cases, you may need to set the active profiles in the application startup arguments.

3. **Problems when merging environments**

   + When you merge two environments with `environment.merge()` and property values differ or go missing, check the merge logic carefully. Understand the override rules for property sources with the same name in the parent and child environments. This makes sure the merged environment is what you expect.

4. **Conflicts between profiles**

   + When several profiles are active, the same bean may be defined in different profiles. This can cause unexpected behavior. To fix this, make the priority and scope of profiles clear. Reduce conflicts between profiles, and organize them to avoid duplicate definitions.

5. **Problems caused by property source order**

   + The order in which property sources are added may let a property with the same name in another source override the value you expect. To fix this, change the order in which property sources are added. Make sure sources with high priority are considered first.
