package com.codef.applets;

import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlValidatorApp {
	
    public static void main(String[] args) {
        String xmlPath = "/home/sirfozzie/Documents/Online and Software/SitePasswords.xml";

        boolean isWellFormed = isXmlWellFormed(xmlPath);
        System.out.println("XML Structure Result: " + (isWellFormed ? "WELL-FORMED" : "MALFORMED"));
    }

    public static boolean isXmlWellFormed(String xmlPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();

            // Standard XXE protection compatible with all modern JDKs
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new File(xmlPath));
            return true;

        } catch (SAXParseException e) {
            System.err.printf("Syntax Error at line %d, column %d: %s%n",
                    e.getLineNumber(), e.getColumnNumber(), e.getMessage());
            return false;
        } catch (SAXException e) {
            System.err.println("XML Parsing Error: " + e.getMessage());
            return false;
        } catch (ParserConfigurationException | IOException e) {
            System.err.println("Configuration or File Error: " + e.getMessage());
            return false;
        }
    }

}
