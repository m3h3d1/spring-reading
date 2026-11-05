<div align="center">
    <img alt="logo" src="image/banner.png" style="height: 80px">
</div>
<div align="center">
    <h2>Dive into Spring, starting from the source code!</h2>
    <h4>Explore the most popular Java framework, understand how it works inside, and go from beginner to expert.</h4>
</div>
<p align="center">
    <a href="https://github.com/xuchengsheng/spring-reading/stargazers"><img src="https://img.shields.io/github/stars/xuchengsheng/spring-reading?logo=github&logoColor=%23EF2D5E&label=Stars&labelColor=%23000000&color=%23EF2D5E&cacheSeconds=3600" alt="Stars Badge"/></a>
    <a href="https://github.com/xuchengsheng"><img src="https://img.shields.io/github/followers/xuchengsheng?label=Followers&logo=github&logoColor=%23FC521F&labelColor=%231A2477&color=%23FC521F&cacheSeconds=3600" alt="Follow Badge"></a>
    <a href="https://github.com/xuchengsheng/spring-reading/fork"><img src="https://img.shields.io/github/forks/xuchengsheng/spring-reading?label=Forks&logo=github&logoColor=%23F2BB13&labelColor=%23BE2323&color=%23F2BB13" alt="Fork Badge"></a>
    <a href="https://github.com/xuchengsheng/spring-reading/watchers"><img src="https://img.shields.io/github/watchers/xuchengsheng/spring-reading?label=Watchers&logo=github&logoColor=%23FF4655&labelColor=%234169E1&color=%23FF4655&cacheSeconds=3600" alt="Watchers Badge"></a>
</p>
<p align="center">
    <img src="https://visitor-badge.lithub.cc/badge?page_id=github.com/xuchengsheng&left_text=Visitors" alt="Visitor Badge"/>
    <img src="https://img.shields.io/badge/WeChat-spring_reading-%2307C160?logo=wechat" alt="Wechat Badge"/>
    <a href="https://blog.csdn.net/duzhuang2399"><img src="https://img.shields.io/badge/dynamic/xml?url=https%3A%2F%2Fblog.csdn.net%2Fduzhuang2399&query=%2F%2F*%5B%40id%3D%22userSkin%22%5D%2Fdiv%5B1%5D%2Fdiv%5B2%5D%2Fdiv%5B1%5D%2Fdiv%2Fdiv%5B2%5D%2Fdiv%5B1%5D%2Fdiv%5B1%5D%2Fdiv%5B2%5D%2Fspan&logo=C&logoColor=red&label=CSDN&color=red&cacheSeconds=3600" alt="CSDN Badge"></a>
        <a href="https://hellogithub.com/repository/f43b683fa175499ca3af4e9b1684e88b" target="_blank"><img src="https://api.hellogithub.com/v1/widgets/recommend.svg?rid=f43b683fa175499ca3af4e9b1684e88b&claim_uid=AVv4KeNnZs2Ig3a&theme=small" alt="Featured｜HelloGitHub"/></a>
</p>
<p align="center">
    ⚡ <a href="#tech-stack">Tech Stack</a>
    |
    👋 <a href="#introduction">Introduction</a>
    |
    🍵 <a href="#why-analyze-the-spring-source-code">Why</a>
    |
    🙏 <a href="#give-it-a-star">Star</a>
    |
    🌱 <a href="#spring-source-code-reading-series">Spring Source</a>
    |
    💬 <a href="#contact-me">Contact</a>
    |
    ⛵ <a href="#contributing">Contributing</a>
    |
    🔄 <a href="#ongoing-updates">Updates</a>
    |
    💻 <a href="#star-history">Stats</a>
</p>

> English translation of [xuchengsheng/spring-reading](https://github.com/xuchengsheng/spring-reading). The original project is written in Chinese.

---

## ⚡Tech Stack

<div align="left">
    <img src="https://img.shields.io/badge/Java-1.8%2B-%23437291?logo=openjdk&logoColor=%23437291"/>
    <img src="https://img.shields.io/badge/Spring-5.3.10-%23437291?logo=Spring&logoColor=%236DB33F&color=%236DB33F"/>
    <img src="https://img.shields.io/badge/SpringBoot-2.5.5-%23437291?logo=SpringBoot&logoColor=%236DB33F&color=%236DB33F"/>
    <img src="https://img.shields.io/badge/Maven-3.6.3-%23437291?logo=Apache%20Maven&logoColor=%23C71A36&color=%23C71A36"/>
    <img src="https://img.shields.io/badge/JSR-330-%2366CCFF?logo=OpenJDK&logoColor=%2366CCFF&color=%2366CCFF"/>
    <img src="https://img.shields.io/badge/JSR-250-%23FF9900?logo=OpenJDK&logoColor=%23FF9900&color=%23FF9900"/>
</div>

## 👋Introduction
Hi everyone, I'm Lex👨‍💻. I'm a Java backend developer👨‍💼 with 8 years of experience, and I'm passionate about the Spring framework❤️. I created this “Spring Source Code Reading Series”📖 to help developers🧑‍💻 who want to understand Spring in depth. With this series, I hope to explore how Spring works inside⚙️ together with you. If you share the same interest or have questions🤔, please contact me📩!

## 🍵**Why Analyze the Spring Source Code**
As a framework developer👨‍🔬, I often need to understand and adjust how a framework behaves🔧. This work is more than calling the framework API. It needs a detailed knowledge of how the framework works inside🔍. There are simplified versions of Spring on GitHub📦, and they help a lot when you are starting out✅. But in real projects they differ a lot from the real Spring framework❌. So I started to study the Spring source code, hoping to understand its inner workings more fully and apply them better in my daily work🧰. I share my source code analysis📝 to give some reference and help to developers who want to truly understand Spring, not just use it🙌.

## 🙏Give It a Star
Dear friends👥, I put a lot of thought💭 into researching and organizing this “Spring Source Code Reading Series”📘. If you find it good👍, or it helped you a little🤗, please give it a star🌟. It means a lot to me🎖. Every star✨ makes me feel that all the effort was worth it💪. I know it is a small thing, but your click🖱 is the best encouragement for me🎉. Either way, thank you for taking the time🕰 to read my content. I really appreciate it🙏!

## 🌱Spring Source Code Reading Series

+ Spring Core
  + Resource Loading and Access
    - [Resource](spring-resources/spring-resource/README.md): Abstract interface for files, classpath entries, and more. Used to access resources from different sources.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ResourceLoader](spring-resources/spring-resource-resourceLoader/README.md): Core interface for getting resources. Loads resources from different locations in a uniform way.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ResourcePatternResolver](spring-resources/spring-resource-resourcePatternResolver/README.md): Interface for resolving resource patterns. Loads many kinds of application resources flexibly.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [DocumentLoader](spring-resources/spring-resource-documentLoader/README.md): Core interface for loading and parsing XML documents. Supports automatic configuration of Spring applications.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>

  + Metadata and Filtering
    - [MetadataReader](spring-metadata/spring-metadata-metadataReader/README.md): Core of reading class metadata. Supports component scanning, conditional annotations, AOP, and other advanced features.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [AnnotationMetadata](spring-metadata/spring-metadata-annotationMetadata/README.md): Reads and works with class annotation information at runtime.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [TypeFilter](spring-metadata/spring-metadata-typeFilter/README.md): Custom class filtering during component scanning. Supports complex conditions and precise filtering.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [Condition](spring-metadata/spring-metadata-condition/README.md): Condition checks. A flexible way to decide whether a bean is created and configured.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>

  + Validation, Data Binding, and Type Conversion
    - [Validator](spring-dataops/spring-dataops-validator/README.md): Custom data validation logic. Makes sure model objects follow business rules.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [PropertyEditor](spring-dataops/spring-dataops-propertyEditor/README.md): Custom conversion logic for JavaBean properties. Handles property type conversion.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [Converter](spring-dataops/spring-dataops-converter/README.md): Converts between types. Defines simple source-to-target conversion rules.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ConverterFactory](spring-dataops/spring-dataops-converterFactory/README.md): Creates converters for a specific source type. Used for type conversion.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [GenericConverter](spring-dataops/spring-dataops-genericConverter/README.md): A more complex converter. Supports many source and target types.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ConditionalConverter](spring-dataops/spring-dataops-conditionalConverter/README.md): A converter that decides by condition whether to run the conversion.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ConversionService](spring-dataops/spring-dataops-conversionService/README.md): A uniform type conversion service interface. Manages converters.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [Printer](spring-dataops/spring-dataops-printer/README.md): Formats objects as text. Focuses on formatted output.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [Parser](spring-dataops/spring-dataops-parser/README.md): Parses text into objects. Focuses on parsing logic.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>

  + Spring Expression Language (SpEL)
    - [ExpressionParser](spring-spel/spring-spel-expressionParser/README.md): Parses a SpEL expression string, then creates and returns an Expression instance.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [Expression](spring-spel/spring-spel-expression/README.md): Evaluates an expression string. Supports type conversion, getting the original string, and more.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [EvaluationContext](spring-spel/spring-spel-evaluationContext/README.md): Manages the context information of SpEL expressions.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [PropertyAccessor](spring-spel/spring-spel-propertyAccessor/README.md): Reads and writes object properties. Can be used for custom property access logic.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ConstructorResolver](spring-spel/spring-spel-constructorResolver/README.md): Resolves constructors to decide how a bean is instantiated.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [MethodResolver](spring-spel/spring-spel-methodResolver/README.md): Resolves class methods. Makes sure they are called correctly and handles overloading and argument matching.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [BeanResolver](spring-spel/spring-spel-beanResolver/README.md): Resolves bean definitions, including dependencies and property settings, then instantiates and returns the bean.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [TypeLocator](spring-spel/spring-spel-typeLocator/README.md): Finds classes dynamically and returns Class objects. Used in expression parsing, type conversion, and more.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [TypeConverter](spring-spel/spring-spel-typeLocator/README.md): Type conversion. Converts data in an expression from one type to another.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [TypeComparator](spring-spel/spring-spel-typeLocator/README.md): Type comparison. Defines how to compare two objects for equality.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [OperatorOverloader](spring-spel/spring-spel-typeLocator/README.md): Operator overloading. Customizes how operators in an expression behave.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>

  + Bean Definition and Registration
    - [BeanDefinition](spring-beans/spring-bean-beanDefinition/README.md): Describes a bean in detail. Supports dependency injection, AOP, scope control, and other core features.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [BeanDefinitionHolder](spring-beans/spring-bean-beanDefinitionHolder/README.md): A key class for managing and working with a BeanDefinition.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [BeanDefinitionRegistry](spring-beans/spring-bean-beanDefinitionRegistry/README.md): A key interface for registering and managing bean definitions. Handles bean metadata.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>

  + Bean Definition Reading and Scanning
    - [XmlBeanDefinitionReader](spring-beans/spring-bean-xmlBeanDefinitionReader/README.md): Loads and parses XML configuration, builds the IoC container, and registers bean definitions.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [PropertiesBeanDefinitionReader](spring-beans/spring-bean-propertiesBeanDefinitionReader/README.md): Loads property files and parses them into bean definitions.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [GroovyBeanDefinitionReader](spring-beans/spring-bean-groovyBeanDefinitionReader/README.md): Parses Groovy scripts into bean definitions.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [AnnotatedBeanDefinitionReader](spring-beans/spring-bean-annotatedBeanDefinitionReader/README.md): Annotation-based configuration. Scans and registers Spring components automatically and simplifies bean definition setup.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [ClassPathBeanDefinitionScanner](spring-beans/spring-bean-classPathBeanDefinitionScanner/README.md): Scans the classpath and registers Spring beans. Supports autowiring.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>

  + Bean Factory
    - [BeanFactory](spring-factory/spring-factory-beanFactory/README.md): The core interface of Spring. Provides the basic features to configure, create, and manage beans.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [ListableBeanFactory](spring-factory/spring-factory-listableBeanFactory/README.md): Supports getting collections of beans by type.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [HierarchicalBeanFactory](spring-factory/spring-factory-hierarchicalBeanFactory/README.md): Supports parent and child containers. Builds a hierarchy of bean definitions.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [ConfigurableBeanFactory](spring-factory/spring-factory-configurableBeanFactory/README.md): Extends BeanFactory configuration, for example with property editors and scopes.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [AutowireCapableBeanFactory](spring-factory/spring-factory-autowireCapableBeanFactory/README.md): Core interface for creating, initializing, injecting, and destroying beans.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [ConfigurableListableBeanFactory](spring-factory/spring-factory-configurableListableBeanFactory/README.md): A configurable bean factory interface that supports both configuration and listing operations.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>

  + Container Context
    - [ClassPathXmlApplicationContext](spring-context/spring-context-classPathXmlApplicationContext/README.md): A context that loads XML configuration files from the classpath.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [AnnotationConfigApplicationContext](spring-context/spring-context-annotationConfigApplicationContext/README.md): A context that loads configuration from annotated configuration classes.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - GenericApplicationContext: A context that supports several configuration styles: XML, annotations, and manual registration.<img src="https://img.shields.io/badge/Coming%20Soon-339933"></img>

  + Bean Definition Import and Composition
    - ImportBeanDefinitionRegistrar: Registers beans dynamically at runtime. Gives flexible configuration and extends configuration classes.<img src="https://img.shields.io/badge/Coming%20Soon-339933"></img>
    - ImportSelector: Imports configuration classes dynamically at runtime. Supports conditional selection and flexible configuration.<img src="https://img.shields.io/badge/Coming%20Soon-339933"></img>
    - DeferredImportSelector: Imports configuration dynamically at runtime. Supports conditional selection and delayed loading by group.<img src="https://img.shields.io/badge/Coming%20Soon-339933"></img>

  + Bean Lifecycle
    - [Bean Definition Registration Process](spring-core/spring-core-registerBeanDefinition): Loads and parses configuration files, then registers the parsed bean definitions: class name, scope, properties, and more.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [Bean Initialization Process](spring-core/spring-core-getBean/README.md): Instantiation, property injection, Aware callbacks, post-processors, and init method calls.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [Bean Dependency Resolution Process](spring-core/spring-core-resolveDependency/README.md): Declares, finds, and injects dependencies. Handles circular dependencies and lazy dependency resolution.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [Bean Destruction Process](spring-core/spring-core-destroyBean/README.md): Destroy method calls, interface callbacks, post-processing cleanup, notifications, and GC reclaiming resources.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>

  + Property Resolution and Environment
    - [PropertySource](spring-env/spring-env-propertySource/README.md): Abstract class for managing configuration sources. Loads and accesses application configuration flexibly.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [PropertySources](spring-env/spring-env-propertySources/README.md): Manages and accesses several PropertySource instances in one place. Simplifies handling of configuration data.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [PropertyResolver](spring-env/spring-env-propertyResolver/README.md): General property resolution. Gets configuration values and handles missing properties in a simple, flexible way.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ConfigurablePropertyResolver](spring-env/spring-env-configurablePropertyResolver/README.md): Configures property resolution and placeholders to fit different configuration needs.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [Environment](spring-env/spring-env-environment/README.md): Represents the application environment. Gives access to properties, supports profiles, and enables dynamic configuration.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ConfigurableEnvironment](spring-env/spring-env-configurableEnvironment/README.md): Configures the application environment dynamically: active and default profiles. Makes the application more flexible.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>

  + Bean Initialization and Extension Points
    - [InitializingBean](spring-interface/spring-interface-initializingBean/README.md): Interface for running custom logic when a bean is initialized.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [DisposableBean](spring-interface/spring-interface-disposableBean/README.md): Interface for running cleanup before a bean is destroyed.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [BeanDefinitionRegistryPostProcessor](spring-interface/spring-interface-beanDefinitionRegistryPostProcessor/README.md): Changes or adds BeanDefinitions dynamically when the container starts.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [BeanFactoryPostProcessor](spring-interface/spring-interface-beanFactoryPostProcessor/README.md): Changes or configures the BeanFactory globally before beans are instantiated.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [BeanPostProcessor](spring-interface/spring-interface-beanPostProcessor/README.md): Custom processing before and after bean initialization. Can affect all beans.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [InstantiationAwareBeanPostProcessor](spring-interface/spring-interface-instantiationAwareBeanPostProcessor/README.md): Gives deeper control over instantiation and property injection.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [DestructionAwareBeanPostProcessor](spring-interface/spring-interface-destructionAwareBeanPostProcessor/README.md): Allows extra cleanup before a bean is destroyed.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [MergedBeanDefinitionPostProcessor](spring-interface/spring-interface-mergedBeanDefinitionPostProcessor/README.md): Processes the BeanDefinition when bean definitions are merged.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [SmartInstantiationAwareBeanPostProcessor](spring-interface/spring-interface-smartInstantiationAwareBeanPostProcessor/README.md): Gives smarter control over instantiation.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [SmartInitializingSingleton](spring-interface/spring-interface-smartInitializingSingleton/README.md): Runs custom logic after all singleton beans are initialized.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>

  + Aware Interfaces
    - [BeanNameAware](spring-aware/spring-aware-beanNameAware/README.md): Lets a bean get its own name in the container.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [BeanClassLoaderAware](spring-aware/spring-aware-beanClassLoaderAware/README.md): Lets a bean get its class loader.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [BeanFactoryAware](spring-aware/spring-aware-beanFactoryAware/README.md): Lets a bean get the BeanFactory it belongs to.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [EnvironmentAware](spring-aware/spring-aware-environmentAware/README.md): Lets a bean get the application environment.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [EmbeddedValueResolverAware](spring-aware/spring-aware-embeddedValueResolverAware/README.md): Lets a bean resolve embedded value placeholders.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [ResourceLoaderAware](spring-aware/spring-aware-beanClassLoaderAware/README.md): Lets a bean get the resource loader.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [ApplicationEventPublisherAware](spring-aware/spring-aware-applicationEventPublisherAware/README.md): Lets a bean publish application events.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [MessageSourceAware](spring-aware/spring-aware-messageSourceAware/README.md): Lets a bean get the message source.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [ApplicationContextAware](spring-aware/spring-aware-applicationContextAware/README.md): Lets a bean get the application context.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [ImportAware](spring-aware/spring-aware-importAware/README.md): Lets an imported configuration class get information about the class that imported it.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>

  + Core Annotations
    - [@Configuration](spring-annotation/spring-annotation-configuration/README.md): Declares a class as a configuration class. Defines beans and the dependencies between them.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [@ComponentScan](spring-annotation/spring-annotation-componentScan/README.md): Enables component scanning. Finds and registers classes marked as components automatically.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [@Bean](spring-annotation/spring-annotation-bean/README.md): Declares a method in a configuration class that returns a bean instance.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [@Import](spring-annotation/spring-annotation-import/README.md): Imports other configuration classes and merges their bean definitions into the current container.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [@PropertySource](spring-annotation/spring-annotation-propertySource/README.md): Names a property file and loads external configuration into the environment.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [@DependsOn](spring-annotation/spring-annotation-dependsOn/README.md): Sets the dependency order of beans. Makes sure certain beans are initialized before others.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [@Conditional](spring-annotation/spring-annotation-conditional/README.md): Decides by condition whether a bean is created.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [@Lazy](spring-annotation/spring-annotation-lazy/README.md): Makes a bean lazily initialized. It is created only when first used.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [@Value](spring-annotation/spring-annotation-value/README.md): Injects a simple value or an expression into a bean field or method parameter.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [@Autowired](spring-annotation/spring-annotation-autowired/README.md): Autowires bean dependencies.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - @Primary: Marks the preferred bean when there are several candidates.<img src="https://img.shields.io/badge/Coming%20Soon-339933"></img>
    - @Description: Gives descriptive information for a bean.<img src="https://img.shields.io/badge/Coming%20Soon-339933"></img>
    - @Role: Gives a role hint for a bean. Used to tell apart beans of similar types.<img src="https://img.shields.io/badge/Coming%20Soon-339933"></img>
    - @Indexed: Marks a bean for indexing.<img src="https://img.shields.io/badge/Coming%20Soon-339933"></img>
    - @Order: Sets the loading order of beans.<img src="https://img.shields.io/badge/Coming%20Soon-339933"></img>

  + JSR Standards
    - [@Inject](spring-jsr/spring-jsr330-inject/README.md): The dependency injection annotation of the JSR-330 standard.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [@Named](spring-jsr/spring-jsr330-named/README.md): The naming annotation of the JSR-330 standard.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [@Resource](spring-jsr/spring-jsr250-resource/README.md): The resource injection annotation of the Java EE standard.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [@Qualifier](spring-jsr/spring-jsr330-qualifier/README.md): Narrows down which bean is injected.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [@Scope](spring-jsr/spring-jsr330-scope/README.md): Sets the scope of a bean.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [@Singleton](spring-jsr/spring-jsr330-singleton/README.md): Makes a bean a singleton.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [@PostConstruct](spring-jsr/spring-jsr250-postConstruct/README.md): Marks the initialization method.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [@PreDestroy](spring-jsr/spring-jsr250-preDestroy/README.md): Marks the destroy method.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>
    - [Provider](spring-jsr/spring-jsr330-provider/README.md): A general bean factory interface from the Java standard library.<img src="https://img.shields.io/badge/Level-Medium-%23FF8C69"></img>

  + Spring AOP
    - [JDK Dynamic Proxy](spring-aop/spring-aop-jdkProxy/README.md): Based on interfaces. Generates proxy classes dynamically, handles method calls, and centralizes cross-cutting concerns.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [Cglib Dynamic Proxy](spring-aop/spring-aop-cglibProxy/README.md): A bytecode generation library. Needs no interface and can intercept and enhance class methods.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ClassFilter](spring-aop/spring-aop-classFilter/README.md): Decides whether a class matches the interception condition.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [MethodMatcher](spring-aop/spring-aop-methodMatcher/README.md): Decides whether a method matches the interception condition.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [Pointcut](spring-aop/spring-aop-pointcut/README.md): Defines the pointcut and matches the methods to intercept.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [Advice](spring-aop/spring-aop-advice/README.md): Core interface that defines the behavior of the different advice types in AOP.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
      - [MethodInterceptor](spring-aop/spring-aop-advice-methodInterceptor/README.md): Intercepts method execution and lets you add logic before and after it.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
      - [MethodBeforeAdvice](spring-aop/spring-aop-advice-methodBeforeAdvice/README.md): Lets you insert custom logic before a method call.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
      - [AfterReturningAdvice](spring-aop/spring-aop-advice-afterReturningAdvice/README.md): Lets you insert custom logic after a method call.<<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
      - [ThrowsAdvice](spring-aop/spring-aop-advice-throwsAdvice/README.md): Exception advice. Catches exceptions thrown by a method and runs extra logic.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
      - [IntroductionInterceptor](spring-aop/spring-aop-advice-introductionInterceptor/README.md): Adds new features or properties to the target object dynamically.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [Advisor](spring-aop/spring-aop-advisor/README.md): Combines advice and a pointcut to implement cross-cutting concerns in aspect-oriented programming.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [Advised](spring-aop/spring-aop-advised/README.md): Configures the advice, advisors, target, and more of an AOP proxy.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ProxyFactory](spring-aop/spring-aop-proxyFactory/README.md): A convenient way to create proxy objects.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [AopProxyFactory](spring-aop/spring-aop-aopProxyFactory/README.md): Creates AOP proxies. Supports JDK and CGLIB.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [AopProxy](spring-aop/spring-aop-aopProxy/README.md): Creates and manages AOP proxy objects.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [AdvisorChainFactory](spring-aop/spring-aop-advisorChainFactory/README.md): Factory interface that creates the Advisor chain.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [AdvisorAdapterRegistry](spring-aop/spring-aop-advisorAdapterRegistry/README.md): Adapts the different kinds of Advice to AOP interceptors. Registers and manages Advisor adapters.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [AdvisorAdapter](spring-aop/spring-aop-advisorAdapter/README.md): Adapts different advice types to the interceptor chain.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [ProxyMethodInvocation](spring-aop/spring-aop-proxyMethodInvocation/README.md): AOP method invocation proxy. Handles the interceptor chain and the method call.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [@EnableAspectJAutoProxy](spring-aop/spring-aop-enableAspectJAutoProxy/README.md): Enables automatic proxying for AspectJ aspects.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [AnnotationAwareAspectJAutoProxyCreator](spring-aop/spring-aop-annotationAwareAspectJAutoProxyCreator/README.md): Creates AOP proxies to apply AspectJ-style aspects.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [BeanFactoryAdvisorRetrievalHelper](spring-aop/spring-aop-beanFactoryAdvisorRetrievalHelper/README.md): Helps find and manage Advisor beans in Spring AOP.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [BeanFactoryAspectJAdvisorsBuilder](spring-aop/spring-aop-beanFactoryAspectJAdvisorsBuilder/README.md): Builds @AspectJ annotated aspects and generates Spring AOP Advisors.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [AspectInstanceFactory](spring-aop/spring-aop-aspectInstanceFactory/README.md): Creates aspect instances. Supports several implementations.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [MetadataAwareAspectInstanceFactory](spring-aop/spring-aop-metadataAwareAspectInstanceFactory/README.md): Manages aspect instances and metadata. Supports several instantiation strategies.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [AspectJAdvisorFactory](spring-aop/spring-aop-aspectJAdvisorFactory/README.md): Creates AspectJ advisor instances. Manages the creation and configuration of aspect advice.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
    - [TargetSource](spring-aop/spring-aop-targetSource/README.md): Manages getting and releasing the target of an AOP proxy.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [TargetSourceCreator](spring-aop/spring-aop-targetSourceCreator/README.md): Creates special target sources. Customizes how proxy objects are created and managed.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
    - [AopContext](spring-aop/spring-aop-aopContext/README.md): A utility for getting the current Spring AOP proxy object.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [ExposeInvocationInterceptor](spring-aop/spring-aop-exposeInvocationInterceptor/README.md): An interceptor that exposes the Spring AOP method invocation context.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
    - [@EnableLoadTimeWeaving](spring-aop/spring-aop-enableLoadTimeWeaving/README.md): Enables Spring load-time weaving.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
+ Spring Transactions
  + [Connection](spring-transaction/spring-transaction-connection/README.md): Manages database connections, runs SQL, and handles transactions.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
  + [DataSource](spring-transaction/spring-transaction-dataSource/README.md): An interface for managing database connections efficiently.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
  + [DriverManager](spring-transaction/spring-transaction-driverManager/README.md): The core class for managing and creating database connections.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
  + [JdbcTemplate](spring-transaction/spring-transaction-jdbcTemplate/README.md): Simplifies JDBC operations and offers a convenient abstraction for database access.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
  + [TransactionDefinition](spring-transaction/spring-transaction-transactionDefinition/README.md): Defines the propagation behavior and isolation level of a transaction.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
  + [TransactionAttributeSource](spring-transaction/spring-transaction-transactionAttributeSource/README.md): Strategy interface for getting transaction attributes.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
  + [PlatformTransactionManager](spring-transaction/spring-transaction-platformTransactionManager/README.md): Manages and coordinates the lifecycle and execution of transactions.<img src="https://img.shields.io/badge/Level-Hard-%23FF3030"></img>
  + [TransactionTemplate](spring-transaction/spring-transaction-transactionTemplate/README.md): Simplifies transaction management. Supports programmatic transaction control and exception handling.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
  + [SpringTransactionAnnotationParser](spring-transaction/spring-transaction-springTransactionAnnotationParser/README.md): Parses the `@Transactional` annotation and turns it into transaction configuration.<img src="https://img.shields.io/badge/Level-Easy-0099ff"></img>
  + [TransactionInterceptor](spring-transaction/spring-transaction-transactionInterceptor/README.md): Transaction interceptor. Manages transactions at the method level.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>
  + [EnableTransactionManagement](spring-transaction/spring-transaction-enableTransactionManagement/README.md): Enables Spring's annotation-driven transaction management.<img src="https://img.shields.io/badge/Level-Medium-%23FF6347"></img>

+ Spring MVC
+ Spring OpenFeign

## 💬Contact Me

✉️ [Email](xuchengshengsuper@163.com) | 💬 [Issue](https://github.com/xuchengsheng/spring-reading/issues) | 🌐 [CSDN](https://blog.csdn.net/duzhuang2399?type=blog)  Me about everything!

## ⛵Contributing

If you find any mistake🔍 or have a suggestion for improvement🛠️, feel free to open an issue or a pull request. Your feedback📢 is very valuable to me💎!

## 🔄Ongoing Updates

To give you the latest🌱 and most valuable content💼, I will keep updating this repository every day⏳. Each day you can expect new content or improvements to existing content✨. If you have any suggestion or feedback📣, feel free to contact me at any time📞. I value every piece of feedback💌, because it keeps me improving🚀.

## ✨Star History

 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=xuchengsheng/spring-reading&type=Date&theme=dark" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=xuchengsheng/spring-reading&type=Date" />
   <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=xuchengsheng/spring-reading&type=Date" />
 </picture>

## 🎉Stargazers

[![Stargazers123 repo roster for @xuchengsheng/spring-reading](https://reporoster.com/stars/xuchengsheng/spring-reading)](https://github.com/xuchengsheng/spring-reading/stargazers)

## 🎉Forkers

[![Forkers repo roster for @xuchengsheng/spring-reading](https://reporoster.com/forks/xuchengsheng/spring-reading)](https://github.com/xuchengsheng/spring-reading/network/members)

## 🍱Buy Me a Lunch?

The author writes blog posts at night✍️ and works during the day💼. If this helped you, you can buy the author a lunch🥡
<div>
<img alt="logo" src="image/WeChatPay.png" style="width: 260px;height: 280px">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img alt="logo" src="image/Alipay.png" style="width: 260px;height: 280px">
</div>

## 👥**Follow the WeChat Official Account**

After you follow, reply with the keyword **“加群”** (join group) to join our technical discussion group and learn together with more developers.

<div>
<img alt="logo" src="image/wechat-mp.png" style="height: 220px">
</div>
