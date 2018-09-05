package de.cognicrypt.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
import org.xml.sax.SAXException;

import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;

/**
 * This class provides methods for XML file processing.
 * @author André Sonntag
 *
 */
public class XMLParser {

	/**
	 * Usage:
	 * 1) Constructor
	 * 2) useDocFromFile || createNewDoc
	 * 3) createRootElement
	 * 4) createChildElement*
	 * 5) createAttrForElement*
	 * 6) writeXML
	 */
	
	private File xmlFile;
	private Document doc;
	private Element root;
	private DocumentBuilder docBuilder;
	private DocumentBuilderFactory docFactory;

	/**
	 * Constructor
	 * @param xmlFile
	 */
	public XMLParser(File xmlFile) {
		this.xmlFile = xmlFile;
		docFactory = DocumentBuilderFactory.newInstance();
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * This methods extracts the {@link Document} object from the XML file, for further processing.
	 */
	public void useDocFromFile() {
		try {
			this.doc = docBuilder.parse(this.xmlFile);
			doc.getDocumentElement().normalize();
		} catch (SAXException e) {
			Activator.getDefault().logError(e);
		} catch (IOException e) {
			Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_FILE);
		}
	}

	/**
	 * This method creates a new {@link Document} object for the XML file.
	 */
	public void createNewDoc() {

		try {
			this.docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		this.doc = docBuilder.newDocument();
	}

	/**
	 * This method returns the root {@link Element} of the {@link Document}.
	 * @return
	 */
	public Element getRoot() {
		return doc.getDocumentElement();
	}
	
	/**
	 * This method creates and appends a new root {@link Element} to the {@link Document} structure.
	 * @param rootElementName
	 * @return the new created root element
	 */
	public Element createRootElement(String rootElementName) {
		this.root = doc.createElement(rootElementName);
		return createRootElement(root);
	}

	/**
	 * This method appends a root {@link Element} to the {@link Document} structure.
	 * @param root
	 * @return the inserted root element
	 */
	public Element createRootElement(Element root) {
		this.root = root; 
		this.doc.appendChild(root);
		return root;
	}
	
	/**
	 * This method creates and appends a new child {@link Element} with a value to a parent {@link Element}.
	 * @param parent
	 * @param childName
	 * @param childValue
	 * @return the new created child element
	 */
	public Element createChildElement(Element parent, String childName, String childValue) {
		Element child = doc.createElement(childName);
		child.appendChild(doc.createTextNode(childValue));
		return createChildElement(parent, child);
	}

	/**
	 * This method creates and appends a new Child {@link Element} without a value to a parent {@link Element}.
	 * @param parent
	 * @param childName
	 * @return the new created child element
	 */
	public Element createChildElement(Element parent, String childName) {
		Element child = doc.createElement(childName);
		return createChildElement(parent, child);
	}

	/**
	 * This method appends a child {@link Element} to a parent {@link Element}.
	 * @param parent
	 * @param child
	 * @return the inserted child element
	 */
	public Element createChildElement(Element parent, Element child) {
		parent.appendChild(child);
		return child;
	}

	/**
	 * This method creates a {@link Attr} with a value for an {@link Element} object.
	 * @param element
	 * @param attrName
	 * @param attrValue
	 */
	public void createAttrForElement(Element element, String attrName, String attrValue) {
		Attr attr = this.doc.createAttribute(attrName);
		attr.setValue(attrValue);
		element.setAttributeNode(attr);
	}

	/**
	 * This method writes the {@link Document} object structure to a XML File.
	 */
	public void writeXML() {

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(this.doc);
			StreamResult result = new StreamResult(this.xmlFile);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			Activator.getDefault().logError(e);
		} catch (TransformerException e) {
			Activator.getDefault().logError(e);
		}

	}

	/**
	 * This method removes {@link Node} with a specific {@link Attr} with a certain value.
	 * @param nodeName
	 * @param attrName
	 * @param attrValue
	 */
	public void removeNodeByAttrValue(String nodeName, String attrName, String attrValue) {
		NodeList nl = this.doc.getElementsByTagName(nodeName);
		for (int i = 0; i < nl.getLength(); i++) {
			NamedNodeMap map = nl.item(i).getAttributes();
			for (int j = 0; j < map.getLength(); j++) {
				if (map.item(j).getTextContent().equals(attrValue)) {
					nl.item(i).getParentNode().removeChild(nl.item(i));
				}
			}
		}
	}

	/**
	 * This method creates a {@link List} with all {@link Attr} values of a certain node name.
	 * @param nodeName
	 * @param attrName
	 * @return a list with all attribute values
	 */
	public ArrayList<String> getAttrValuesByAttrName(String nodeName, String attrName) {
		ArrayList<String> valueList = new ArrayList<>();
		NodeList nl = this.doc.getElementsByTagName(nodeName);
		for (int i = 0; i < nl.getLength(); i++) {
			NamedNodeMap map = nl.item(i).getAttributes();
			for (int j = 0; j < map.getLength(); j++) {
				valueList.add(map.item(j).getTextContent());
			}
		}
		return valueList;
	}

	/**
	 * This method filters a certain {@link Node}, on the basis of the {@link Node} name, {@link Attr} name and {@link Attr} value.
	 * @param nodeName
	 * @param attrName
	 * @param attrValue
	 * @return the filtered node
	 * @throws NoSuchElementException
	 */
	public Node getNodeByAttrValue(String nodeName, String attrName, String attrValue) throws NoSuchElementException{
		NodeList nl = this.doc.getElementsByTagName(nodeName);
		for (int i = 0; i < nl.getLength(); i++) {
			NamedNodeMap map = nl.item(i).getAttributes();
			for (int j = 0; j < map.getLength(); j++) {
				if (map.item(j).getTextContent().equals(attrValue)) {
					return nl.item(i);
				}
			}
		}
		throw new NoSuchElementException();
	}
	
	/**
	 * This method filters a certain child {@link Node} in a parent node, on the basis of the {@link Node} name.
	 * @param parent
	 * @param nodeName
	 * @return the filtered node
	 * @throws NoSuchElementException
	 */
	public Node getChildNodeByTagName(Node parent, String nodeName) throws NoSuchElementException {
		NodeList childList = parent.getChildNodes();
		for(int i = 0; i < childList.getLength(); i++) {
			if(childList.item(i).getNodeName().equals(nodeName)) {
				return childList.item(i);
			}
		}
		throw new NoSuchElementException();
	}
	
	/**
	 * This method updates the value of a certain {@link Node}.
	 * @param node
	 * @param newValue
	 */
	public void updateNodeValue(Node node, String newValue) {
		node.setTextContent(newValue);
		doc = node.getOwnerDocument();
	}
	
	public File getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	
}
