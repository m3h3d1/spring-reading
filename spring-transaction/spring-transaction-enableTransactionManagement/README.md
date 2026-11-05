## EnableTransactionManagement

- [EnableTransactionManagement](#EnableTransactionManagement)
    - [1. Overview](#1-overview)
    - [2. Description](#2-description)
    - [3. Main Features](#3-main-features)
    - [4. Interface Source](#4-interface-source)
    - [5. Main Implementations](#5-main-implementations)
    - [6. Best Practices](#6-best-practices)
    - [7. Source Code Analysis](#7-source-code-analysis)

### 1. Overview

✒️ **Author** - Lex 📝 **Blog** - [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Description

`@EnableTransactionManagement` is a Spring annotation that turns on annotation-driven transaction management. When you put it on a configuration class, Spring
scans and processes the `@Transactional` annotations in the application. This gives you declarative transaction management. It is usually used with a `PlatformTransactionManager`
instance to manage and coordinate database transactions.

### 3. Main Features

1. **Enable transaction management**

    + When you add `@EnableTransactionManagement` to a configuration class, Spring
      scans all transaction annotations (such as `@Transactional`) in the application context and supports transaction management for them.

2. **Transaction manager**

    + `@EnableTransactionManagement` needs a `PlatformTransactionManager` instance to manage transactions. Usually, Spring
      detects the transaction manager in the configuration automatically. If there is none, Spring tries to create a default one.

### 4. Annotation Source

`@EnableTransactionManagement`
This annotation turns on annotation-driven transaction management in Spring. When you add it to a configuration class, Spring scans and processes the `@Transactional`
annotations in the application and gives you declarative transaction management. It supports both traditional imperative and reactive transaction management. It lets you configure the transaction manager flexibly. The `proxyTargetClass`
attribute sets the proxy type, and the `mode` attribute selects the proxy mode (the default is `PROXY`, and `ASPECTJ` is also possible
). You can also name the transaction manager by implementing the `TransactionManagementConfigurer` interface. This is an efficient solution for complex transaction needs, and the default behavior is well compatible with the XML configuration style.

```java
/**
 * Enables Spring's annotation-driven transaction management capability, similar to the support found in Spring's {@code <tx:*>} XML namespace.
 * To be used on {@link org.springframework.context.annotation.Configuration @Configuration} classes
 * to configure traditional, imperative transaction management or reactive transaction management.
 *
 * <p>The following example demonstrates imperative transaction management using a {@link org.springframework.transaction.PlatformTransactionManager
 * PlatformTransactionManager}. For reactive transaction management, configure a
 * {@link org.springframework.transaction.ReactiveTransactionManager
 * ReactiveTransactionManager} instead.
 *
 * <pre class="code">
 * @Configuration
 * @EnableTransactionManagement
 * public class AppConfig {
 *
 *     @Bean
 *     public FooRepository fooRepository() {
 *         // Configure and return a class that has @Transactional methods
 *         return new JdbcFooRepository(dataSource());
 *     }
 *
 *     @Bean
 *     public DataSource dataSource() {
 *         // Configure and return the needed JDBC DataSource
 *     }
 *
 *     @Bean
 *     public PlatformTransactionManager txManager() {
 *         return new DataSourceTransactionManager(dataSource());
 *     }
 * }</pre>
 *
 * <p>For reference, the example above can be compared to the following Spring XML configuration:
 *
 * <pre class="code">
 * <beans>
 *
 *     <tx:annotation-driven/>
 *
 *     <bean id="fooRepository" class="com.foo.JdbcFooRepository">
 *         <constructor-arg ref="dataSource"/>
 *     </bean>
 *
 *     <bean id="dataSource" class="com.vendor.VendorDataSource"/>
 *
 *     <bean id="transactionManager" class="org.sfwk...DataSourceTransactionManager">
 *         <constructor-arg ref="dataSource"/>
 *     </bean>
 *
 * </beans>
 * </pre>
 *
 * In both of the scenarios above, {@code @EnableTransactionManagement} and {@code
 * <tx:annotation-driven/>} are responsible for registering the necessary Spring components for annotation-driven transaction management,
 * such as the TransactionInterceptor and the proxy- or AspectJ-based advice that weaves the interceptor into the call stack
 * when {@code JdbcFooRepository}'s {@code @Transactional} methods are invoked.
 *
 * <p>A minor difference between the two examples lies in the naming of the {@code TransactionManager} bean:
 * In the {@code @Bean} case, the name is <em>"txManager"</em> (per the name of the method); in the XML case, the name is
 * <em>"transactionManager"</em>. {@code <tx:annotation-driven/>} is hard-wired to look for a bean named
 * "transactionManager" by default, whereas {@code @EnableTransactionManagement} is more flexible;
 * it will fall back to a by-type lookup for any {@code TransactionManager} bean in the container. Thus the name can be
 * "txManager", "transactionManager", or "tm": the name does not matter.
 *
 * <p>For those who want to establish a more direct relationship between {@code @EnableTransactionManagement} and the exact transaction manager bean to be used,
 * the {@link TransactionManagementConfigurer} callback interface may be implemented - notice the {@code implements} clause and the {@code @Override}-annotated method below:
 *
 * <pre class="code">
 * @Configuration
 * @EnableTransactionManagement
 * public class AppConfig implements TransactionManagementConfigurer {
 *
 *     @Bean
 *     public FooRepository fooRepository() {
 *         // Configure and return a class that has @Transactional methods
 *         return new JdbcFooRepository(dataSource());
 *     }
 *
 *     @Bean
 *     public DataSource dataSource() {
 *         // Configure and return the needed JDBC DataSource
 *     }
 *
 *     @Bean
 *     public PlatformTransactionManager txManager() {
 *         return new DataSourceTransactionManager(dataSource());
 *     }
 *
 *     @Override
 *     public PlatformTransactionManager annotationDrivenTransactionManager() {
 *         return txManager();
 *     }
 * }</pre>
 *
 * <p>This approach may be desirable simply because it is more explicit, or it may be necessary in order to distinguish between two {@code TransactionManager} beans present in the same container.
 * As the name suggests, the {@code annotationDrivenTransactionManager()} will be the one used for processing {@code @Transactional} methods.
 * See the {@link TransactionManagementConfigurer} Javadoc for further details.
 *
 * <p>The {@link #mode} attribute controls how advice is applied: If the mode is {@link AdviceMode#PROXY} (the default), then the other attributes control the behavior of the proxying.
 * Please note that proxy mode allows for interception of calls through the proxy only; local calls within the same class cannot get intercepted that way.
 *
 * <p>Note that if the {@linkplain #mode} is set to {@link AdviceMode#ASPECTJ}, then the value of the {@link #proxyTargetClass} attribute will be ignored.
 * Note also that in this case the {@code spring-aspects} module JAR must be present on the classpath, with compile-time weaving or load-time weaving applying the aspect to the affected classes.
 * There is no proxy involved in such a scenario; local calls will be intercepted as well.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see TransactionManagementConfigurer
 * @see TransactionManagementConfigurationSelector
 * @see ProxyTransactionManagementConfiguration
 * @see org.springframework.transaction.aspectj.AspectJTransactionManagementConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableTransactionManagement {

    /**
     * Indicate whether subclass-based (CGLIB) proxies are to be created ({@code true}) as opposed to standard Java interface-based proxies ({@code false}).
     * The default is {@code false}. <strong>Applicable only if {@link #mode()} is set to {@link AdviceMode#PROXY}</strong>.
     * <p>Note that setting this attribute to {@code true} will affect <em>all</em> Spring-managed beans requiring proxying, not just those marked with
     * {@code @Transactional}. For example, other beans marked with Spring's {@code @Async} annotation will be upgraded to subclass proxying at the same time.
     * This approach has no negative impact in practice unless one is explicitly expecting one type of proxy vs another, for example, in tests.
     */
    boolean proxyTargetClass() default false;

    /**
     * Indicate how transactional advice should be applied.
     * <p><b>The default is {@link AdviceMode#PROXY}.</b>
     * Please note that proxy mode allows for interception of calls through the proxy only. Local calls within the same class cannot get intercepted that way;
     * an interceptor on a method with the {@link Transactional} annotation will not even kick in for such a runtime scenario.
     * For a more advanced mode of interception, consider switching this to {@link AdviceMode#ASPECTJ}.
     */
    AdviceMode mode() default AdviceMode.PROXY;

    /**
     * Indicate the ordering of the execution of the transaction advisor when multiple advices are applied at a specific join point.
     * <p>The default is {@link Ordered#LOWEST_PRECEDENCE}.
     */
    int order() default Ordered.LOWEST_PRECEDENCE;

}
```

### 6. Best Practices

This example creates an annotation-based application context `AnnotationConfigApplicationContext` and loads the `AppConfig`
configuration class. Then it gets the bean `ScoresService` from the context and calls the `insertScore`
method of `ScoresService`. It shows how to configure and use a service class with annotations in a Spring application.

```java
public class EnableTransactionManagementDemo {

    public static void main(String[] args) {
        // Create an annotation-based application context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // Get the ScoresService bean from the context
        ScoresService scoresService = context.getBean(ScoresService.class);
        // Call the ScoresService method
        scoresService.insertScore();
    }
}
```

The Spring configuration class `AppConfig` uses the annotations `@Configuration`, `@ComponentScan`, and `@EnableTransactionManagement`
to turn on component scanning and annotation-driven transaction management. It defines several `@Bean`s: a `DataSourceTransactionManager`
to manage transactions, a `JdbcTemplate` to simplify JDBC operations, and a `SimpleDriverDataSource`
to configure the database connection. The database is MySQL, with the connection URL, username, and password set.

```java

@Configuration
@ComponentScan
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SimpleDriverDataSource dataSource() throws SQLException {
        // Database URL, format: jdbc:driver-name://host:port/database-name
        String url = "jdbc:mysql://localhost:3306/spring-reading";
        // Database username
        String username = "root";
        // Database password
        String password = "123456";
        // Initialize the data source
        return new SimpleDriverDataSource(new Driver(), url, username, password);
    }
}
```

It implements the `ScoresService` interface. The `@Service` annotation marks it as a Spring-managed service component, and `@Autowired` injects the `JdbcTemplate`
. In the `insertScore` method, the `@Transactional` annotation turns on transaction management. The method generates a random score, inserts it into the database, and prints the number of affected rows. It also has a commented-out piece of code that simulates an exception, to test transaction rollback.

```java

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
```

### 7. Source Code Analysis

The class `org.springframework.transaction.annotation.TransactionManagementConfigurationSelector`
extends `AdviceModeImportSelector<EnableTransactionManagement>`. It chooses the right transaction management configuration classes from the `mode`
attribute of `@EnableTransactionManagement`. If `mode` is `PROXY`, it returns the class names `AutoProxyRegistrar`
and `ProxyTransactionManagementConfiguration`. If it is `ASPECTJ`
, it checks whether the class `javax.transaction.Transactional` is on the classpath. Then it returns the JTA transaction aspect configuration class name or the non-JTA
transaction aspect configuration class name. This way, Spring can pick the right implementation for each transaction management mode.

```java
/**
 * Select the {@link AbstractTransactionManagementConfiguration} implementation that should be used, based on the value of {@link EnableTransactionManagement#mode}
 * on the importing {@code @Configuration} class.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see EnableTransactionManagement
 * @see ProxyTransactionManagementConfiguration
 * @see TransactionManagementConfigUtils#TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME
 * @see TransactionManagementConfigUtils#JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME
 */
public class TransactionManagementConfigurationSelector extends AdviceModeImportSelector<EnableTransactionManagement> {

    /**
     * Return the matching configuration classes for the transaction management mode.
     *
     * @param adviceMode the transaction management mode, either PROXY or ASPECTJ
     * @return an array with the names of the transaction management configuration classes
     */
    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                // For PROXY mode, return the class names AutoProxyRegistrar and ProxyTransactionManagementConfiguration
                return new String[]{AutoProxyRegistrar.class.getName(),
                        ProxyTransactionManagementConfiguration.class.getName()};
            case ASPECTJ:
                // For ASPECTJ mode, return the class name of the determined transaction aspect configuration
                return new String[]{determineTransactionAspectClass()};
            default:
                // If the mode is neither PROXY nor ASPECTJ, return null
                return null;
        }
    }

    /**
     * Determine the transaction aspect configuration class to use.
     *
     * @return the class name of the JTA or non-JTA transaction aspect configuration
     */
    private String determineTransactionAspectClass() {
        // Check whether javax.transaction.Transactional is on the classpath
        return (ClassUtils.isPresent("javax.transaction.Transactional", getClass().getClassLoader()) ?
                // If it is, return the JTA transaction aspect configuration class name
                TransactionManagementConfigUtils.JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME :
                // If it is not, return the non-JTA transaction aspect configuration class name
                TransactionManagementConfigUtils.TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME);
    }
}
```

The class `org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration` registers the
Spring infrastructure beans needed for proxy-based annotation-driven transaction management. It registers `BeanFactoryTransactionAttributeSourceAdvisor`
for transaction advice, `AnnotationTransactionAttributeSource` for the transaction attribute source, and `TransactionInterceptor` for the transaction interceptor. With these beans registered, Spring
can intercept calls to methods annotated with `@Transactional` and apply transaction management.

```java
/**
 * {@code @Configuration} class that registers the Spring infrastructure beans necessary to enable proxy-based annotation-driven transaction management.
 *
 * @author Chris Beams
 * @author Sebastien Deleuze
 * @since 3.1
 * @see EnableTransactionManagement
 * @see TransactionManagementConfigurationSelector
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyTransactionManagementConfiguration extends AbstractTransactionManagementConfiguration {

    /**
     * Register the bean for the transaction advisor.
     *
     * @param transactionAttributeSource the transaction attribute source
     * @param transactionInterceptor    the transaction interceptor
     * @return the BeanFactoryTransactionAttributeSourceAdvisor instance
     */
    @Bean(name = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor(
            TransactionAttributeSource transactionAttributeSource, TransactionInterceptor transactionInterceptor) {

        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(transactionAttributeSource);
        advisor.setAdvice(transactionInterceptor);
        if (this.enableTx != null) {
            advisor.setOrder(this.enableTx.<Integer>getNumber("order"));
        }
        return advisor;
    }

    /**
     * Register the bean for the transaction attribute source.
     *
     * @return the AnnotationTransactionAttributeSource instance
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TransactionAttributeSource transactionAttributeSource() {
        return new AnnotationTransactionAttributeSource();
    }

    /**
     * Register the bean for the transaction interceptor.
     *
     * @param transactionAttributeSource the transaction attribute source
     * @return the TransactionInterceptor instance
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public TransactionInterceptor transactionInterceptor(TransactionAttributeSource transactionAttributeSource) {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionAttributeSource(transactionAttributeSource);
        if (this.txManager != null) {
            interceptor.setTransactionManager(this.txManager);
        }
        return interceptor;
    }

}
```

The class `org.springframework.context.annotation.AutoProxyRegistrar` implements the `ImportBeanDefinitionRegistrar`
interface. It sets up the proper auto proxy creator from the `mode` and `proxyTargetClass`
attributes of an `@Enable*` annotation and registers it in the current `BeanDefinitionRegistry`. It looks for the nearest annotation with the right `mode` and `proxyTargetClass` attributes on the imported `@Configuration`
class. Then it registers and configures the auto proxy creator according to those settings.

```java
/**
 * Register, escalate, and configure the standard auto proxy creator (APC) against the current {@link BeanDefinitionRegistry}
 * as appropriate, based on an {@code @Enable*} annotation having {@code mode} and {@code proxyTargetClass}
 *
 * This class works by finding the nearest annotation declared on the importing {@code @Configuration} class that has both {@code mode} and {@code proxyTargetClass}
 * attributes. If {@code mode} is set to {@code PROXY}, the APC is registered; if
 * {@code proxyTargetClass} is set to {@code true}, then the APC is forced to use subclass (CGLIB) proxying.
 *
 * Several {@code @Enable*} annotations expose both {@code mode} and {@code proxyTargetClass} attributes. It is important
 * to note that most of these capabilities end up sharing a {@linkplain AopConfigUtils#AUTO_PROXY_CREATOR_BEAN_NAME single APC}.
 * For this reason, this implementation doesn't care exactly which annotation it finds, as long as it exposes the right {@code mode} and {@code proxyTargetClass}
 * attributes, the APC can be registered and configured all the same.
 *
 * @author Chris Beams
 * @since 3.1
 * @see org.springframework.cache.annotation.EnableCaching
 * @see org.springframework.transaction.annotation.EnableTransactionManagement
 */
public class AutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

    private final Log logger = LogFactory.getLog(getClass());

    /**
     * Register, escalate, and configure the standard auto proxy creator (APC) based on the metadata of the importing class and the bean definition registry.
     * It works by finding the nearest annotation declared on the importing {@code @Configuration} class that has both {@code mode} and {@code proxyTargetClass}
     * attributes. If {@code mode} is set to {@code PROXY}, the APC is registered; if
     * {@code proxyTargetClass} is set to {@code true}, then the APC is forced to use subclass (CGLIB) proxying.
     * <p>
     * Several {@code @Enable*} annotations expose both {@code mode} and {@code proxyTargetClass} attributes. It is important
     * to note that most of these capabilities end up sharing a {@linkplain AopConfigUtils#AUTO_PROXY_CREATOR_BEAN_NAME single APC}.
     * For this reason, this implementation doesn't care exactly which annotation it finds, as long as it exposes the right {@code mode} and {@code proxyTargetClass}
     * attributes, the APC can be registered and configured all the same.
     *
     * @param importingClassMetadata the metadata of the importing class
     * @param registry the bean definition registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // Whether a candidate annotation was found
        boolean candidateFound = false;
        // Get all annotation types of the importing class
        Set<String> annTypes = importingClassMetadata.getAnnotationTypes();
        // Loop over all annotation types
        for (String annType : annTypes) {
            // Get the attributes of the annotation
            AnnotationAttributes candidate = AnnotationConfigUtils.attributesFor(importingClassMetadata, annType);
            // If there are no attributes, go on to the next one
            if (candidate == null) {
                continue;
            }
            // Get the mode and proxyTargetClass attributes
            Object mode = candidate.get("mode");
            Object proxyTargetClass = candidate.get("proxyTargetClass");
            // If mode and proxyTargetClass are not null, and their types are AdviceMode and Boolean
            if (mode != null && proxyTargetClass != null && AdviceMode.class == mode.getClass() &&
                    Boolean.class == proxyTargetClass.getClass()) {
                candidateFound = true;
                // If mode is AdviceMode.PROXY
                if (mode == AdviceMode.PROXY) {
                    // Register the APC
                    AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
                    // If proxyTargetClass is true
                    if ((Boolean) proxyTargetClass) {
                        // Force the APC to use class-based proxying
                        AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
                        return;
                    }
                }
            }
        }
        // If no candidate annotation was found and the logger is enabled
        if (!candidateFound && logger.isInfoEnabled()) {
            String name = getClass().getSimpleName();
            logger.info(String.format("%s was imported but no annotations were found " +
                    "having both 'mode' and 'proxyTargetClass' attributes of type " +
                    "AdviceMode and boolean respectively. This means that auto proxy " +
                    "creator registration and configuration may not have occurred as " +
                    "intended, and components may not be proxied as expected. Check to " +
                    "ensure that %s has been @Import'ed on the same class where these " +
                    "annotations are declared; otherwise remove the import of %s " +
                    "altogether.", name, name, name));
        }
    }

}
```