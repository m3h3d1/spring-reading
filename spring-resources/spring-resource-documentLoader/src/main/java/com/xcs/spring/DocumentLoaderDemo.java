package com.xcs.spring;

import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

/**
 * @author xcs
 * @date 2023-11-04 14:27
 **/
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
