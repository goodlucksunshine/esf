package com.laile.esf.common.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlUtil {
    public static Document doc(InputStream is) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setIgnoringComments(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    public static Element root(InputStream is) throws Exception {
        return doc(is).getDocumentElement();
    }

    public static Element element(Element parent, String tagName) {
        if ((parent == null) || (!parent.hasChildNodes())) {
            return null;
        }

        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1) {
                Element childElement = (Element) child;
                String childTagName = childElement.getLocalName();

                if (tagName.equals(childTagName)) {
                    return childElement;
                }
            }
        }

        return null;
    }

    public static List<Element> elements(Element parent, String tagName) {
        if ((parent == null) || (!parent.hasChildNodes())) {
            return Collections.emptyList();
        }

        List<Element> elements = new ArrayList();
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == 1) {
                Element childElement = (Element) child;
                String childTagName = childElement.getLocalName();

                if (tagName.equals(childTagName)) {
                    elements.add(childElement);
                }
            }
        }
        return elements;
    }

    public static String getContentText(Element element, String defaultValue) {
        if (element == null) {
            return defaultValue;
        }
        return element.getNodeValue();
    }

    public static String attribute(Element element, String attributeName, String defaultValue) {
        if (element == null) {
            return defaultValue;
        }
        Attr attribute = element.getAttributeNode(attributeName);
        if (attribute == null) {
            return defaultValue;
        }
        return attribute.getValue();
    }

    public static String attribute(Element element, String attributeName) {
        return attribute(element, attributeName, null);
    }

    public static Integer attributeInteger(Element element, String attributeName, Integer defaultValue) {
        if (element == null) {
            return defaultValue;
        }
        Attr attribute = element.getAttributeNode(attributeName);
        if (attribute == null) {
            return defaultValue;
        }
        return Integer.valueOf(Integer.parseInt(attribute.getValue()));
    }

    public static Boolean attributeBoolean(Element element, String attributeName, Boolean defaultValue) {
        if (element == null) {
            return defaultValue;
        }
        Attr attribute = element.getAttributeNode(attributeName);
        if (attribute == null) {
            return defaultValue;
        }
        Boolean value = parseBooleanValue(attribute.getValue());
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static Boolean parseBooleanValue(String valueText) {
        if (valueText != null) {
            if (("true".equals(valueText)) || ("enabled".equals(valueText)) || ("on".equals(valueText))) {
                return Boolean.TRUE;
            }
            if (("false".equals(valueText)) || ("disabled".equals(valueText)) || ("off".equals(valueText))) {
                return Boolean.FALSE;
            }
        }
        return Boolean.FALSE;
    }
}

/*
 * Location:
 * /Users/sunshine/学习资料/hqd/111/hqd-web/WEB-INF/lib/lsf-common-1.5.3-RELEASE.jar
 * !/com/lz/lsf/util/XmlUtil.class Java compiler version: 6 (50.0) JD-Core
 * Version: 0.7.1
 */