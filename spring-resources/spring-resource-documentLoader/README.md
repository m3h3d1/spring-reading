## DocumentLoader

- [DocumentLoader](#documentloader)
  - [1. Overview](#1-overview)
  - [2. Prerequisites](#2-prerequisites)
  - [3. Description](#3-description)
  - [4. Main Features](#4-main-features)
  - [5. Interface Source](#5-interface-source)
  - [6. Main Implementations](#6-main-implementations)
  - [7. Best Practices](#7-best-practices)
  - [8. Relationship with Other Components](#8-relationship-with-other-components)
  - [9. FAQ](#9-faq)

### 1. Overview

✒️ **Author** - Lex 📝 - **Blog** [Juejin](https://juejin.cn/user/4251135018533068/posts) 📚 **Contents** - [Source code](https://github.com/xuchengsheng/spring-reading)

### 2. Prerequisites

1. **XML parsing**
   + Know XML parsing techniques such as DOM (Document Object Model) and SAX (Simple API for XML). `DocumentLoader` usually uses DOM to load and parse XML documents, so it helps to understand DOM operations.
2. **Resource loading**
   + Know how to use Java resource loading, for example [ResourceLoader](https://github.com/xuchengsheng/spring-reading/tree/master/spring-resources/spring-resource-resourceLoader) and [Resource](https://github.com/xuchengsheng/spring-reading/tree/master/spring-resources/spring-resource), to get XML configuration files. Spring uses these to load configuration files.

### 3. Description

The `DocumentLoader` interface is a core Spring interface that loads and parses XML documents. It is usually used to parse Spring configuration files. It defines a method that loads an XML document and parses it into an `org.w3c.dom.Document` object. A developer can then read and work with the content of the XML configuration file. This interface lets the loading and parsing of configuration files run automatically in the background, which supports the initialization and configuration of a Spring application.

### 4. Main Features

1. **Load XML documents**:
   + The `DocumentLoader` interface defines a method that loads an XML document. The XML content can come from different sources, such as files, resources, or URLs.
2. **Parse XML documents**
   + After the XML document is loaded, `DocumentLoader` parses it into an `org.w3c.dom.Document` object. This object represents the whole structure of the XML document, including elements, attributes, and text.
3. **Validation support**
   + `DocumentLoader` can usually support validation. By giving a validation mode (such as DTD or XML Schema validation), you make sure the structure and content of the document follow the rules.
4. **Entity reference handling**
   + It can also handle external entity references in XML documents. You give it an `EntityResolver` to resolve external entities, so loading and parsing go smoothly.
5. **Error handling**
   + `DocumentLoader` also takes an `ErrorHandler` to handle errors during XML parsing, so problems are caught and handled in time.

### 5. Interface Source

The `DocumentLoader` interface is the strategy for loading and parsing XML documents. It takes an `InputSource`, an `EntityResolver`, an `ErrorHandler`, a `validationMode` (DTD or XSD validation), and a boolean `namespaceAware` that says whether XML namespace support is on. The method returns the loaded `Document` object.

```java
/**
 * Strategy interface for loading an XML Document.
 *
 * @author Rob Harrop
 * @since 2.0
 * @see DefaultDocumentLoader
 */
public interface DocumentLoader {

    /**
     * Load a Document from the supplied InputSource.
     * @param inputSource the source of the document that is to be loaded
     * @param entityResolver the resolver that is to be used to resolve any entities
     * @param errorHandler used to report any errors during document loading
     * @param validationMode the type of validation
     * (org.springframework.util.xml.XmlValidationModeDetector#VALIDATION_DTD DTD
     * or org.springframework.util.xml.XmlValidationModeDetector#VALIDATION_XSD XSD)
     * @param namespaceAware true if support for XML namespaces is to be provided
     * @return the loaded Document
     * @throws Exception if an error occurs
     */
    Document loadDocument(
        InputSource inputSource, EntityResolver entityResolver,
        ErrorHandler errorHandler, int validationMode, boolean namespaceAware)
        throws Exception;

}
```

### 6. Main Implementations

1. **`DefaultDocumentLoader`**
   + `DefaultDocumentLoader` is the default implementation in Spring. It loads and parses XML configuration files, which supports the initialization and configuration of Spring applications. This implementation is flexible. If you need a different loading strategy or validation mode, you can set a different `DocumentLoader` implementation through configuration.

~~~mermaid
classDiagram
    direction BT
    class DocumentLoader {
    	<<interface>>
    }

    class DefaultDocumentLoader {
    }

	DefaultDocumentLoader --|> DocumentLoader

~~~

### 7. Best Practices

Use Spring's default document loader (`DefaultDocumentLoader`) to load and parse an XML configuration file, then print the details of the XML document recursively, including element names, attributes, and text. First create a `Resource` object for the XML file to load. Then load and parse it with `DefaultDocumentLoader`. At the end, print the content of the document recursively.

```java
public class DocumentLoaderDemo {
    
    public static void main(String[] args) {
        try {
            // Create the resource to load
            Resource resource = new ClassPathResource("sample.xml");

            // Create a DocumentLoader instance
            DefaultDocumentLoader documentLoader = new DefaultDocumentLoader();

            // Load and parse the XML document
            Document document = documentLoader.loadDocument(new InputSource(resource.getInputStream()), null, null, 0, true);

            // Print the content of the XML document
            printDetailedDocumentInfo(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints detailed XML document information, including elements, attributes and text.
     *
     * @param document the XML document to print
     */
    private static void printDetailedDocumentInfo(Document document) {
        Element rootElement = document.getDocumentElement();
        printElementInfo(rootElement, "");
    }

    /**
     * Prints XML element details recursively, including name, attributes and child nodes.
     *
     * @param element the XML element to print
     * @param indent  the current indentation
     */
    private static void printElementInfo(Element element, String indent) {
        // Print the element name
        System.out.println(indent + "Element: " + element.getNodeName());

        // Print the element attributes
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            System.out.println(indent + "  Attribute: " + attribute.getNodeName() + " = " + attribute.getNodeValue());
        }

        // Print the child nodes (elements or text)
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                // If the child is an element, print it recursively
                printElementInfo((Element) childNode, indent + "  ");
            } else if (childNode.getNodeType() == Node.TEXT_NODE) {
                // If the child is text, print its content
                System.out.println(indent + "  Text: " + childNode.getNodeValue().trim());
            }
        }
    }
}
```

`MyBean` is a Java class that stands for a simple Java bean.

```java
public class MyBean {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

`ClassPathResource("sample.xml")` loads the content of the resource file named "`sample.xml`" on the classpath. In this example, the resource file defines a Spring bean named "`myBean`". The bean has a property "message" whose value is set to "Hello World".

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="myBean" class="com.xcs.spring.bean.MyBean">
        <property name="message" value="Hello World"/>
    </bean>

</beans>
```

Running it shows that the structure and content of the XML configuration file are parsed correctly. This includes namespaces, elements, attributes, text, and the hierarchy between them.

```yaml
Element: beans
  Attribute: xmlns = http://www.springframework.org/schema/beans
  Attribute: xmlns:xsi = http://www.w3.org/2001/XMLSchema-instance
  Attribute: xsi:schemaLocation = http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
  Text: 
  Element: bean
    Attribute: class = com.xcs.spring.bean.MyBean
    Attribute: id = myBean
    Text: 
    Element: property
      Attribute: name = message
      Attribute: value = Hello World
    Text: 
  Text: 
```

### 8. Relationship with Other Components

1. **`XmlBeanDefinitionReader`**
   + `XmlBeanDefinitionReader` reads and parses XML configuration files. When the Spring application context initializes, it uses `DefaultDocumentLoader` to load and parse the configuration files. It is one of the key parts of the Spring IoC container, which registers and manages bean definitions.
2. **`ApplicationContext`** 
   + The different Spring application context implementations, such as `ClassPathXmlApplicationContext`, `FileSystemXmlApplicationContext`, and `XmlWebApplicationContext`, use `DefaultDocumentLoader` or a similar loader to load configuration files and create the application context.
3. **Spring Web MVC configuration**
   + In a Spring Web MVC application, the `<context-param>` element in the `DispatcherServlet` configuration is usually used to name the Spring configuration file. `DefaultDocumentLoader` parses this file to configure the web application context.

### 9. FAQ

1. **XML document path and resource location problems**
   + Make sure the XML document can be found and loaded. Check the path, check that the file exists, and check that the resource location is correct.
2. **XML validation and format problems**
   + Make sure the XML document follows a proper DTD or XSD, to avoid validation failures or format errors.
3. **Entity resolution problems**
   + Provide a valid `EntityResolver` to resolve external entities and avoid resolving problems.
4. **Error handling problems**
   + Use an `ErrorHandler` to handle errors during parsing. This makes debugging and handling problems easier.
