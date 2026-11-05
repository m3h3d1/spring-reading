## XmlBeanDefinitionReader

- [XmlBeanDefinitionReader](#xmlbeandefinitionreader)
  - [1. Background Knowledge](#1-background-knowledge)
  - [2. Basic Description](#2-basic-description)
  - [3. Main Features](#3-main-features)
  - [4. Best Practices](#4-best-practices)
  - [5. Sequence Diagram](#5-sequence-diagram)
  - [6. Source Code Analysis](#6-source-code-analysis)
  - [7. Relationship with Other Components](#7-relationship-with-other-components)
  - [8. FAQ](#8-faq)

### 1. Background Knowledge

1. **`Resource`**
   - `Resource` stands for a resource. It can be a file, a file on the classpath, a URL and so on. It gives an abstraction and access methods for resources.
   - [View the Resource interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-resources/spring-resource)
2. **`ResourceLoader`**
   - `ResourceLoader` gets resources. They can be files, classpath resources, URLs or even remote resources. It gives one uniform way to load resources, wherever they are.
   - [View the ResourceLoader interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-resources/spring-resource-resourceLoader)
3. **`DocumentLoader`**
   + `XmlBeanDefinitionReader` relies on `DocumentLoader` to load and parse XML configuration files, so the bean definitions in the XML file can be turned into internal bean definition objects of the Spring container.
   + [View the DocumentLoader interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-resources/spring-resource-documentLoader)

### 2. Basic Description

`XmlBeanDefinitionReader` is a Spring Framework class that loads and parses XML bean definition files. It turns the bean metadata in the file into internal bean definition objects. This is how the IoC container is built and managed. The class reads the XML file, parses the bean definitions (ID, class name, properties, dependencies and so on), and registers them in the Spring application context. This makes it easy to configure and manage the beans of an application.

### 3. Main Features

1. **Load XML configuration files**
   + `XmlBeanDefinitionReader` can load Spring configuration files in XML format. A Resource object, such as `ClassPathResource` or `FileSystemResource`, usually gives the location of the file.
2. **Parse XML files**
   + It parses the XML file into internal data structures of the Spring container, using `DocumentLoader`. This turns XML elements and attributes into Spring bean definition information.
3. **Register bean definitions**
   + Once `XmlBeanDefinitionReader` has parsed the bean definitions in the XML file, it registers them in the bean factory of the Spring container, so the container can create and manage these bean instances.

### 4. Best Practices

First, a Spring container (`DefaultListableBeanFactory`) is created. Then `XmlBeanDefinitionReader` loads and parses the XML file "`beans.xml`" and registers the bean metadata in it into the container. After that, the bean named "`myBean`" is taken from the container and printed. This covers initializing the Spring container, parsing the XML configuration file, and getting and using a bean.

```java
public class XmlBeanDefinitionReaderDemo {

    public static void main(String[] args) {
        // Create a DefaultListableBeanFactory as the Spring container
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // Create an XmlBeanDefinitionReader to parse the XML configuration
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        // Load the XML configuration file
        reader.loadBeanDefinitions(new ClassPathResource("beans.xml"));

        // Get and use the bean
        MyBean myBean = factory.getBean("myBean", MyBean.class);
        System.out.println("myBean = " + myBean);
    }
}
```

`ClassPathResource("sample.xml")` loads the content of the resource file "`sample.xml`" on the classpath. In our example, the file defines a Spring bean named "`myBean`". The bean has a property "message" with the value "Hello World".

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="myBean" class="com.xcs.spring.bean.MyBean">
        <property name="message" value="Hello World"/>
    </bean>

</beans>
```

`MyBean` is a simple Java class that represents a plain Java bean.

```java
public class MyBean {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "message='" + message + '\'' +
                '}';
    }
}
```

### 5. Sequence Diagram

~~~mermaid
sequenceDiagram
autonumber
Title: XmlBeanDefinitionReader Sequence Diagram

XmlBeanDefinitionReaderDemo->>XmlBeanDefinitionReader:loadBeanDefinitions(resource)
XmlBeanDefinitionReader->>XmlBeanDefinitionReader:loadBeanDefinitions(encodedResource)
note over XmlBeanDefinitionReader: Decode the resource and load the bean definitions

XmlBeanDefinitionReader->>XmlBeanDefinitionReader:doLoadBeanDefinitions(inputSource,resource)
note over XmlBeanDefinitionReader: Load the bean definitions with the InputSource

XmlBeanDefinitionReader->>XmlBeanDefinitionReader:doLoadDocument(inputSource,resource)
note over XmlBeanDefinitionReader: Call doLoadDocument to parse the XML

XmlBeanDefinitionReader->>DefaultDocumentLoader:loadDocument(...)
note over XmlBeanDefinitionReader,DefaultDocumentLoader: Load the XML

DefaultDocumentLoader->>XmlBeanDefinitionReader:Return the Document

XmlBeanDefinitionReader->>XmlBeanDefinitionReader:registerBeanDefinitions(doc,resource)
note over XmlBeanDefinitionReader: Call registerBeanDefinitions to register the bean definitions


XmlBeanDefinitionReader->>DefaultBeanDefinitionDocumentReader:createBeanDefinitionDocumentReader()
note over XmlBeanDefinitionReader,DefaultBeanDefinitionDocumentReader: Create the BeanDefinitionDocumentReader
DefaultBeanDefinitionDocumentReader->>XmlBeanDefinitionReader:Return the documentReader

XmlBeanDefinitionReader->>XmlReaderContext:new XmlReaderContext()
note over XmlBeanDefinitionReader,XmlReaderContext: Create the XmlReaderContext
XmlReaderContext->>XmlBeanDefinitionReader:Return the readerContext

XmlBeanDefinitionReader->>DefaultBeanDefinitionDocumentReader:registerBeanDefinitions(doc,readerContext)
note over XmlBeanDefinitionReader,DefaultBeanDefinitionDocumentReader: Call registerBeanDefinitions to register the bean definitions

loop Every minute
    DefaultBeanDefinitionDocumentReader->>DefaultBeanDefinitionDocumentReader:doRegisterBeanDefinitions(root)
    note over DefaultBeanDefinitionDocumentReader,DefaultBeanDefinitionDocumentReader: Handle the beans tag
    
    DefaultBeanDefinitionDocumentReader->>DefaultBeanDefinitionDocumentReader:parseBeanDefinitions(root,delegate)
    note over DefaultBeanDefinitionDocumentReader,DefaultBeanDefinitionDocumentReader: Handle the "import", "alias" and "bean" tags
    
    DefaultBeanDefinitionDocumentReader->>DefaultBeanDefinitionDocumentReader:parseDefaultElement(ele,delegate)
    note over DefaultBeanDefinitionDocumentReader,DefaultBeanDefinitionDocumentReader: Parse the default element

    alt If it is an import tag
    DefaultBeanDefinitionDocumentReader->>DefaultBeanDefinitionDocumentReader:importBeanDefinitionResource(ele)
    note over DefaultBeanDefinitionDocumentReader,DefaultBeanDefinitionDocumentReader: Handle the import tag

    else If it is an alias tag
    DefaultBeanDefinitionDocumentReader->>DefaultBeanDefinitionDocumentReader:processAliasRegistration(ele)
    note over DefaultBeanDefinitionDocumentReader,DefaultBeanDefinitionDocumentReader: Handle the alias tag

    else If it is a bean tag
        rect rgb(207,207,207)
            DefaultBeanDefinitionDocumentReader->>DefaultBeanDefinitionDocumentReader:processBeanDefinition(ele, delegate)
            note over DefaultBeanDefinitionDocumentReader,DefaultBeanDefinitionDocumentReader: Handle the bean tag
            DefaultBeanDefinitionDocumentReader->>XmlBeanDefinitionReader:getRegistry()
            note over DefaultBeanDefinitionDocumentReader,XmlBeanDefinitionReader: Get the BeanDefinitionRegistry
            XmlBeanDefinitionReader->>DefaultBeanDefinitionDocumentReader:Return the BeanDefinitionRegistry
            DefaultBeanDefinitionDocumentReader->>BeanDefinitionReaderUtils:registerBeanDefinition(definitionHolder,registry)
            note over DefaultBeanDefinitionDocumentReader,BeanDefinitionReaderUtils: Register the bean definition
            BeanDefinitionReaderUtils->>BeanDefinitionReaderUtils:registerBeanDefinition(beanName,beanDefinition)
            note over BeanDefinitionReaderUtils: Register the bean definition in the container
		end
    else If it is a beans tag
    DefaultBeanDefinitionDocumentReader->>DefaultBeanDefinitionDocumentReader:doRegisterBeanDefinitions(ele)
    note over DefaultBeanDefinitionDocumentReader,DefaultBeanDefinitionDocumentReader: Handle the beans tag (recursively)
    end
end
~~~

### 6. Source Code Analysis

The `org.springframework.beans.factory.xml.XmlBeanDefinitionReader#loadBeanDefinitions(resource)` method calls the `loadBeanDefinitions(encodedResource)` method, and wraps the `resource` in an `EncodedResource` object.

```java
@Override
public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
    return loadBeanDefinitions(new EncodedResource(resource));
}
```

The `org.springframework.beans.factory.xml.XmlBeanDefinitionReader#loadBeanDefinitions(encodedResource)` method first creates an `InputSource` object to parse the XML. Finally it calls `doLoadBeanDefinitions` to do the actual XML parsing and bean definition loading.

```java
public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
    // ... [code omitted for brevity]

    try (InputStream inputStream = encodedResource.getResource().getInputStream()) {
        // Create an InputSource object to parse the XML
        InputSource inputSource = new InputSource(inputStream);
        
        // If encodedResource has character encoding information, set the encoding of the InputSource
        if (encodedResource.getEncoding() != null) {
            inputSource.setEncoding(encodedResource.getEncoding());
        }
        
        // Call doLoadBeanDefinitions to do the actual XML parsing and bean definition loading
        return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
    }
    catch (IOException ex) {
        // ... [code omitted for brevity]
    }
    finally {
        // ... [code omitted for brevity]
    }
}
```

The `org.springframework.beans.factory.xml.XmlBeanDefinitionReader#doLoadBeanDefinitions` method loads the XML configuration file, parses the bean definitions in it, and registers them in the Spring container.

```java
protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource)
            throws BeanDefinitionStoreException {
    try {
        // Parse the XML document
        Document doc = doLoadDocument(inputSource, resource);
        // Register the bean definitions and get the number of loaded beans
        int count = registerBeanDefinitions(doc, resource);
        if (logger.isDebugEnabled()) {
            logger.debug("Loaded " + count + " bean definitions from " + resource);
        }
        return count;
    } 
    
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.xml.XmlBeanDefinitionReader#doLoadDocument` method lets the `DocumentLoader` do the actual XML loading and parsing. It returns the parsed `Document` object.

+ [View the DocumentLoader interface](https://github.com/xuchengsheng/spring-reading/tree/master/spring-resources/spring-resource-documentLoader)

```java
protected Document doLoadDocument(InputSource inputSource, Resource resource) throws Exception {
    return this.documentLoader.loadDocument(inputSource, getEntityResolver(), this.errorHandler,
                                            getValidationModeForResource(resource), isNamespaceAware());
}
```

The `org.springframework.beans.factory.xml.XmlBeanDefinitionReader#registerBeanDefinitions` method first creates a `BeanDefinitionDocumentReader` instance. Then it calls its `registerBeanDefinitions` method to register the bean definitions in the XML document in the Spring container. At the end, it returns the number of beans that were registered successfully.

```java
public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
    BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
    int countBefore = getRegistry().getBeanDefinitionCount();
    documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
    return getRegistry().getBeanDefinitionCount() - countBefore;
}
```

The `org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#registerBeanDefinitions` method calls `doRegisterBeanDefinitions` to start the actual bean definition registration.

```java
@Override
public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
    this.readerContext = readerContext;
    doRegisterBeanDefinitions(doc.getDocumentElement());
}
```

The core of the `org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#doRegisterBeanDefinitions` method is: XML pre-processing, parsing the bean definitions, and XML post-processing.

```java
protected void doRegisterBeanDefinitions(Element root) {
    // ... [code omitted for brevity]
    
    // Call preProcessXml to run the XML pre-processing
    preProcessXml(root);

    // Call parseBeanDefinitions to parse the bean definitions
    parseBeanDefinitions(root, this.delegate);

    // Call postProcessXml to run the XML post-processing
    postProcessXml(root);
    
    // ... [code omitted for brevity]
}
```

The `org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#parseBeanDefinitions` method parses the bean definition elements in the XML document. Depending on the namespace of the element (default or custom), it calls the right method to parse it.

```java
protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
    if (delegate.isDefaultNamespace(root)) {
        // If the root element is in the default namespace, loop over the child elements
        NodeList nl = root.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                if (delegate.isDefaultNamespace(ele)) {
                    // If the child element is also in the default namespace, parse it as a default element
                    parseDefaultElement(ele, delegate);
                }
                else {
                    // Otherwise, parse it as a custom element
                    delegate.parseCustomElement(ele);
                }
            }
        }
    }
    else {
        // If the root element is not in the default namespace, parse it as a custom element
        delegate.parseCustomElement(root);
    }
}
```

The `org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#parseDefaultElement` method chooses the right method to handle each type of element. This parses the different elements in the XML configuration file and registers the bean definitions.

```java
private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
    // Handle by the name of the element
    if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
        // Step 1: If it is an <import> element, import other bean definition resources
        importBeanDefinitionResource(ele);
    }
    else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
        // Step 2: If it is an <alias> element, handle the alias registration of the bean
        processAliasRegistration(ele);
    }
    else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
        // Step 3: If it is a <bean> element, handle a normal bean definition
        processBeanDefinition(ele, delegate);
    }
    else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
        // Step 4: If it is a <beans> element, register the inner bean definitions recursively
        doRegisterBeanDefinitions(ele);
    }
}
```

> [`org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#parseDefaultElement` Step 1]

The `org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#importBeanDefinitionResource` method parses and loads the other resources given by the import element in the XML document. This merges and includes several XML configuration files.

```java
protected void importBeanDefinitionResource(Element ele) {
    // Get the value of the resource attribute of the import element
    String location = ele.getAttribute(RESOURCE_ATTRIBUTE);
    
    // Check whether the resource attribute value is an empty string
    if (!StringUtils.hasText(location)) {
        // Report an error
        getReaderContext().error("Resource location must not be empty", ele);
        // Return an empty result
        return;
    }
    
    // Resolve the placeholder variables in the resource attribute value
    location = getReaderContext().getEnvironment().resolveRequiredPlaceholders(location);
    
    // Create a new LinkedHashSet to hold all the resolved actual resources
    Set<Resource> actualResources = new LinkedHashSet<>(4);
    
    // Check whether the resource attribute value is an absolute path
    boolean absoluteLocation = false;
    try {
        // If the resource attribute value is in URL format, or can be converted to an absolute URI, it is considered an absolute path
        absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
    } catch (URISyntaxException ex) {
        // ... [code omitted for brevity]
    }
    
    // If the resource attribute value is an absolute path
    if (absoluteLocation) {
        try {
            // Load the resource with the loadBeanDefinitions method of the parent class, and add the result to the actualResources collection
            int importCount = getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
            
            // ... [code omitted for brevity]
        } catch (BeanDefinitionStoreException ex) {
            // ... [code omitted for brevity]
        }
    } else {
        // If the resource attribute value is not an absolute path
        try {
            int importCount;
            // Try to find the resource that the attribute value points to in the directory of the current file
            Resource relativeResource = getReaderContext().getResource().createRelative(location);
            if (relativeResource.exists()) {
                // Load the resource that was found, and add the result to the actualResources collection
                importCount = getReaderContext().getReader().loadBeanDefinitions(relativeResource);
                actualResources.add(relativeResource);
            } else {
                // If the resource cannot be found in the directory of the current file, the value is considered a path relative to the location of the current file
                String baseLocation = getReaderContext().getResource().getURL().toString();
                // Adjust the resource attribute value to make it an absolute path
                String adjustedLocation = StringUtils.applyRelativePath(baseLocation, location);
                
                // Load the adjusted resource, and add the result to the actualResources collection
                importCount = getReaderContext().getReader().loadBeanDefinitions(adjustedLocation, actualResources);
            }
            
            // ... [code omitted for brevity]
        } catch (IOException | BeanDefinitionStoreException ex) {
            // ... [code omitted for brevity]
        }
    }
    
    // Convert all resources in the actualResources collection to an array, and call fireImportProcessed to tell the listeners that an import operation is done
    Resource[] actResArray = actualResources.toArray(new Resource[0]);
    getReaderContext().fireImportProcessed(location, actResArray, extractSource(ele));
}
```

> [`org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#parseDefaultElement` Step 2]

The `org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#processAliasRegistration` method handles the alias element in the XML configuration file. The alias element creates an alias, that is, it gives an existing bean ID a new name. Then we can use the new alias instead of the original bean ID when we refer to the bean.

```java
protected void processAliasRegistration(Element ele) {
    // Get the name attribute value and the alias attribute value of the alias element
    String name = ele.getAttribute(NAME_ATTRIBUTE);
    String alias = ele.getAttribute(ALIAS_ATTRIBUTE);
    
    // Check whether both attribute values are valid
    boolean valid = true;
    if (!StringUtils.hasText(name)) {
        // Report an error
        getReaderContext().error("Name must not be empty", ele);
        // Set the flag that says this alias registration is invalid
        valid = false;
    }
    if (!StringUtils.hasText(alias)) {
        // Report an error
        getReaderContext().error("Alias must not be empty", ele);
        // Set the flag that says this alias registration is invalid
        valid = false;
    }
    
    // If both attribute values are valid
    if (valid) {
        try {
            // Call the registerAlias method of the Registry interface to register an alias for the original bean ID
            getReaderContext().getRegistry().registerAlias(name, alias);
        } catch (Exception ex) {
            // ... [code omitted for brevity]
        }
        
        // Call fireAliasRegistered to tell the listeners that an alias registration is done
        getReaderContext().fireAliasRegistered(name, alias, extractSource(ele));
    }
}
```

> [`org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#parseDefaultElement` Step 3]

The `org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader#processBeanDefinition` method parses the `<bean>` element and registers the bean definition in the Spring container.

```java
protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
    // Call parseBeanDefinitionElement of BeanDefinitionParserDelegate to parse the <bean> element
    BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
    
    if (bdHolder != null) {
        // If parsing succeeds, call delegate.decorateBeanDefinitionIfRequired to decorate the bean definition if needed
        bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);

        try {
            // Register the final decorated bean definition
            BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
        } catch (BeanDefinitionStoreException ex) {
            // Handle the exception: if registration fails, throw BeanDefinitionStoreException
            getReaderContext().error("Failed to register bean definition with name '" +
                                     bdHolder.getBeanName() + "'", ele, ex);
        }

        // Send the bean registration event
        getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
    }
}
```

The `org.springframework.beans.factory.support.BeanDefinitionReaderUtils#registerBeanDefinition` method registers the bean definition in the bean definition registry of the Spring container, and also handles the registration of aliases.

```java
public static void registerBeanDefinition(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
			throws BeanDefinitionStoreException {

    // Register bean definition under primary name.
    String beanName = definitionHolder.getBeanName();
    registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

    // Register aliases for bean name, if any.
    String[] aliases = definitionHolder.getAliases();
    if (aliases != null) {
        for (String alias : aliases) {
            registry.registerAlias(beanName, alias);
        }
    }
}
```

### 7. Relationship with Other Components

1. **Implementations of `ApplicationContext`**
   + The Spring `ApplicationContext` interface has several implementations. Some of them use `XmlBeanDefinitionReader` inside to handle XML configuration files. For example, `ClassPathXmlApplicationContext`, `FileSystemXmlApplicationContext` and `XmlWebApplicationContext` all use `XmlBeanDefinitionReader`.
2. **Custom XML configuration loading**
   + If we write our own Spring application or a custom Spring container, we can use `XmlBeanDefinitionReader` to implement custom XML configuration loading logic.
3. **Using `XmlBeanDefinitionReader` on its own**
   + Sometimes we need to create an `XmlBeanDefinitionReader` instance by hand in an application, and use it to load and register bean definitions.

### 8. FAQ

1. **Correct XML file path**
   + Make sure the path and name of the XML configuration file are correct. Check relative and absolute paths. Otherwise the file cannot be found or read.
2. **XML file format and encoding**
   + The XML file must follow the XML syntax rules, including tag nesting, attribute format and the XML declaration. Also make sure the file uses the right character encoding and that it matches the declared encoding. Otherwise parsing may fail or the text may be garbled.
3. **Correct bean definitions**
   + The bean definitions in the XML file must be correct, including the bean properties, name, class path and so on. Wrong bean definitions can make the Spring container fail to initialize.
4. **Version compatibility**
   + Make sure the Spring Framework version you use is compatible with the XML schema (XSD) version in the XML configuration file. Different versions may need different XSD versions.
5. **Error handling and logging**
   + When a problem happens, check the Spring error logs and exception stack traces to find and fix configuration problems faster. Timely error handling and logging help locate the problem and speed up troubleshooting.