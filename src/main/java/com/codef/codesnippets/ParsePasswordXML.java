package com.codef.codesnippets;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.codef.xsalt.utils.XSaLTFileSystemUtils;

public class ParsePasswordXML {

	public static final String DATA_OUT_FOLDER = "E:\\SECURE_PASSWORDS\\";
	
//	public static final String DATA_FOLDER_OUT = "E:\\ENC_DATA_FOLDER\\";

	private static final String ALGORITHM = "AES";
	private static final int KEY_LENGTH = 128;

	public static Map<String, TreeMap<String, String>> passwordMap = new TreeMap<>();

	public static void main(String[] args) throws NoSuchAlgorithmException {
		copyXMLDocumentForExport("E:\\Site Passwords.xml", DATA_OUT_FOLDER + "Site Passwords SECURE.xml");
		fixXMLDocumentForExport(DATA_OUT_FOLDER + "Site Passwords SECURE.xml");
	}

	public static void copyXMLDocumentForExport(String sourceDoc, String targetDoc) {

		XSaLTFileSystemUtils.makeDirectory(DATA_OUT_FOLDER);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document originalDoc = builder.parse(sourceDoc);
			Document copiedDoc = builder.newDocument();

			Element newRootElement = copiedDoc.createElement("SitePasswords");
			copiedDoc.appendChild(newRootElement);

			NodeList nodeList = originalDoc.getElementsByTagName("Export_For_Phone");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getTextContent().equals("true")) {
					Element parentElement = (Element) node.getParentNode();
					if (!parentElement.getNodeName().equals("SitePasswords")) {
						Element copiedNode = copyElement(parentElement, copiedDoc);
						newRootElement.appendChild(copiedNode);
					}
				}
			}
			saveXMLDocument(copiedDoc, targetDoc);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static Element copyElement(Element originalElement, Document targetDocument) {
		Element copiedElement = targetDocument.createElement(originalElement.getTagName());

		// Copy the attributes
		NamedNodeMap attributes = originalElement.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attribute = (Attr) attributes.item(i);
			copiedElement.setAttribute(attribute.getName(), attribute.getValue());
		}

		// Copy the child nodes recursively
		NodeList childNodes = originalElement.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element copiedChildElement = copyElement((Element) child, targetDocument);
				copiedElement.appendChild(copiedChildElement);
			} else if (child.getNodeType() == Node.TEXT_NODE) {
				Text copiedText = targetDocument.createTextNode(child.getTextContent());
				copiedElement.appendChild(copiedText);
			}
		}

		return copiedElement;
	}

	public static void saveXMLDocument(Document document, String targetDoc) throws TransformerException {

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(targetDoc);
		transformer.transform(source, result);

	}

	public static void fixXMLDocumentForExport(String fileName) throws NoSuchAlgorithmException {

		String encFolder = DATA_OUT_FOLDER + "\\CRYPTS\\";
		XSaLTFileSystemUtils.makeDirectory(encFolder);

		String secretKey = generateAESKeyAsString();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(fileName);
			Element root = document.getDocumentElement();
			NodeList childNodes = root.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node child = childNodes.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) child;

					String nodeName = element.getNodeName();
					String url = "";
					String userName = "";
					String password = "";
					String email = "";

					NodeList testList = element.getElementsByTagName("URL");
					if (testList.getLength() > 0) {
						url = element.getElementsByTagName("URL").item(0).getTextContent();
					}

					testList = element.getElementsByTagName("Username");
					if (testList.getLength() > 0) {
						userName = element.getElementsByTagName("Username").item(0).getTextContent();
						element.getElementsByTagName("Username").item(0).setTextContent("ENCRYPTED");
					}

					testList = element.getElementsByTagName("Password");
					if (testList.getLength() > 0) {
						password = element.getElementsByTagName("Password").item(0).getTextContent();
						element.getElementsByTagName("Password").item(0).setTextContent("ENCRYPTED");
					}

					testList = element.getElementsByTagName("EMail_Address");
					if (testList.getLength() > 0) {
						email = element.getElementsByTagName("EMail_Address").item(0).getTextContent();
						element.getElementsByTagName("EMail_Address").item(0).setTextContent("ENCRYPTED");
					}

					StringBuilder sb = new StringBuilder();

					sb.append(nodeName + ":" + "\n");
					sb.append("\t" + "      URL: " + url + "\n");
					sb.append("\t" + "   E-Mail: " + email + "\n");
					sb.append("\t" + "User Name: " + userName + "\n");
					sb.append("\t" + " Password: " + password + "\n");

					XSaLTFileSystemUtils.writeStringToFile(sb.toString(), encFolder + nodeName + ".txt");
					encryptFile(encFolder + nodeName + ".txt", encFolder + nodeName + ".enc", secretKey);
//					decryptFile(encFolder + nodeName + ".enc", encFolder + nodeName + ".dec", secretKey);
					XSaLTFileSystemUtils.deleteFileNew(encFolder + nodeName + ".txt");

				}
			}

			saveXMLDocument(document, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void encryptFile(String inputFile, String outputFile, String key) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {

//		Cipher cipher = Cipher.getInstance(ALGORITHM);
//		cipher.init(Cipher.ENCRYPT_MODE, key);
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		try (InputStream inputStream = new FileInputStream(inputFile);
				OutputStream outputStream = new FileOutputStream(outputFile)) {

			CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);

			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				cipherOutputStream.write(buffer, 0, bytesRead);
			}

			cipherOutputStream.close();
		}
	}

	public static void decryptFile(String inputFile, String outputFile, String key) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {

//		Cipher cipher = Cipher.getInstance(ALGORITHM);
//		cipher.init(Cipher.DECRYPT_MODE, key);
		SecretKey secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);

		try (InputStream inputStream = new FileInputStream(inputFile);
				OutputStream outputStream = new FileOutputStream(outputFile)) {

			CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);

			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			cipherInputStream.close();
		}
	}

	public static String generateAESKeyAsString() throws NoSuchAlgorithmException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(KEY_LENGTH);
		SecretKey secretKey = keyGenerator.generateKey();
		byte[] keyBytes = secretKey.getEncoded();
		String newKey = Base64.getEncoder().encodeToString(keyBytes);
		System.out.println("key=" + newKey);
		return newKey;
	}

	// OLD CLEANUP STUFF

	/*
	
	
	*/

	// grabing the default username
	/*
	String userName = "PROBLEM_NO_USERNAME_FOUND";
	try {
		userName = element.getElementsByTagName("Username").item(0).getTextContent();
		if (!userName.contains("@")) {
			userName = "PROBLEM_NO_EMAIL_FOUND";
		}
	} catch (Exception e) {
	}
	*/

	// Make sure there is a EMail_Address node, if not, create node with possible
	// e-mail being the username
	/*
	try {
		element.getElementsByTagName("EMail_Address").item(0).getTextContent();
	} catch (Exception e) {
		Element textNodeElement = document.createElement("EMail_Address");
		Text textNode = document.createTextNode(userName);
		textNodeElement.appendChild(textNode);
		element.appendChild(textNodeElement);
	}
	*/

	// make sure it has a password node
	/*
	try {
		element.getElementsByTagName("Password").item(0).getTextContent();
	} catch (Exception e) {
		System.out.println("Password missing for " + nodeName);
	}
	*/

	// Add the export for vacation feature
	/*
	Element textNodeElement = document.createElement("Export_For_Phone");
	Text textNode = document.createTextNode("false");
	textNodeElement.appendChild(textNode);
	element.appendChild(textNodeElement);
	*/

}
