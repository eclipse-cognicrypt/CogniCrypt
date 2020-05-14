/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

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
 * 
 * @author André Sonntag
 */
public class XMLParser {

	/**
	 * Usage: 1) Constructor 2) useDocFromFile || createNewDoc 3) createRootElement 4) createChildElement* 5) createAttrForElement* 6) writeXML
	 */

	private File xmlFile;
	private Document doc;
	private Element root;
	private DocumentBuilder docBuilder;
	private final DocumentBuilderFactory docFactory;

	/**
	 * Constructor
	 * 
	 * @param xmlFile
	 */
	public XMLParser(final File xmlFile) {
		this.xmlFile = xmlFile;
		this.docFactory = DocumentBuilderFactory.newInstance();
		try {
			this.docBuilder = this.docFactory.newDocumentBuilder();
		}
		catch (final ParserConfigurationException e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * This methods extracts the {@link Document} object from the XML file, for further processing.
	 */
	public void useDocFromFile() {
		try {
			this.doc = this.docBuilder.parse(this.xmlFile);
			this.doc.getDocumentElement().normalize();
		}
		catch (final SAXException e) {
			Activator.getDefault().logError(e);
		}
		catch (final IOException e) {
			Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_FILE);
		}
	}

	/**
	 * This method creates a new {@link Document} object for the XML file.
	 */
	public void createNewDoc() {

		try {
			this.docBuilder = this.docFactory.newDocumentBuilder();
		}
		catch (final ParserConfigurationException e) {
			Activator.getDefault().logError(e);
		}
		this.doc = this.docBuilder.newDocument();
	}

	/**
	 * This method returns the root {@link Element} of the {@link Document}.
	 * 
	 * @return
	 */
	public Element getRoot() {
		return this.doc.getDocumentElement();
	}

	/**
	 * This method creates and appends a new root {@link Element} to the {@link Document} structure.
	 * 
	 * @param rootElementName
	 * @return the new created root element
	 */
	public Element createRootElement(final String rootElementName) {
		this.root = this.doc.createElement(rootElementName);
		return createRootElement(this.root);
	}

	/**
	 * This method appends a root {@link Element} to the {@link Document} structure.
	 * 
	 * @param root
	 * @return the inserted root element
	 */
	public Element createRootElement(final Element root) {
		this.root = root;
		this.doc.appendChild(root);
		return root;
	}

	/**
	 * This method creates and appends a new child {@link Element} with a value to a parent {@link Element}.
	 * 
	 * @param parent
	 * @param childName
	 * @param childValue
	 * @return the new created child element
	 */
	public Element createChildElement(final Element parent, final String childName, final String childValue) {
		final Element child = this.doc.createElement(childName);
		child.appendChild(this.doc.createTextNode(childValue));
		return createChildElement(parent, child);
	}

	/**
	 * This method creates and appends a new Child {@link Element} without a value to a parent {@link Element}.
	 * 
	 * @param parent
	 * @param childName
	 * @return the new created child element
	 */
	public Element createChildElement(final Element parent, final String childName) {
		final Element child = this.doc.createElement(childName);
		return createChildElement(parent, child);
	}

	/**
	 * This method appends a child {@link Element} to a parent {@link Element}.
	 * 
	 * @param parent
	 * @param child
	 * @return the inserted child element
	 */
	public Element createChildElement(final Element parent, final Element child) {
		parent.appendChild(child);
		return child;
	}

	/**
	 * This method creates a {@link Attr} with a value for an {@link Element} object.
	 * 
	 * @param element
	 * @param attrName
	 * @param attrValue
	 */
	public void createAttrForElement(final Element element, final String attrName, final String attrValue) {
		final Attr attr = this.doc.createAttribute(attrName);
		attr.setValue(attrValue);
		element.setAttributeNode(attr);
	}

	/**
	 * This method writes the {@link Document} object structure to a XML File.
	 */
	public void writeXML() {

		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			final DOMSource source = new DOMSource(this.doc);
			final StreamResult result = new StreamResult(this.xmlFile);
			transformer.transform(source, result);
		}
		catch (final TransformerConfigurationException e) {
			Activator.getDefault().logError(e);
		}
		catch (final TransformerException e) {
			Activator.getDefault().logError(e);
		}

	}

	/**
	 * This method removes {@link Node} with a specific {@link Attr} with a certain value.
	 * 
	 * @param nodeName
	 * @param attrName
	 * @param attrValue
	 */
	public void removeNodeByAttrValue(final String nodeName, final String attrName, final String attrValue) {
		final NodeList nl = this.doc.getElementsByTagName(nodeName);
		for (int i = 0; i < nl.getLength(); i++) {
			final NamedNodeMap map = nl.item(i).getAttributes();
			for (int j = 0; j < map.getLength(); j++) {
				if (map.item(j).getTextContent().equals(attrValue)) {
					nl.item(i).getParentNode().removeChild(nl.item(i));
				}
			}
		}
	}

	/**
	 * This method creates a {@link List} with all {@link Attr} values of a certain node name.
	 * 
	 * @param nodeName
	 * @param attrName
	 * @return a list with all attribute values
	 */
	public ArrayList<String> getAttrValuesByAttrName(final String nodeName, final String attrName) {
		final ArrayList<String> valueList = new ArrayList<>();
		final NodeList nl = this.doc.getElementsByTagName(nodeName);
		for (int i = 0; i < nl.getLength(); i++) {
			final NamedNodeMap map = nl.item(i).getAttributes();
			for (int j = 0; j < map.getLength(); j++) {
				valueList.add(map.item(j).getTextContent());
			}
		}
		return valueList;
	}

	/**
	 * This method filters a certain {@link Node}, on the basis of the {@link Node} name, {@link Attr} name and {@link Attr} value.
	 * 
	 * @param nodeName
	 * @param attrName
	 * @param attrValue
	 * @return the filtered node
	 * @throws NoSuchElementException
	 */
	public Node getNodeByAttrValue(final String nodeName, final String attrName, final String attrValue) {
		final NodeList nl = this.doc.getElementsByTagName(nodeName);
		for (int i = 0; i < nl.getLength(); i++) {
			final NamedNodeMap map = nl.item(i).getAttributes();
			for (int j = 0; j < map.getLength(); j++) {
				if (map.item(j).getTextContent().equals(attrValue)) {
					return nl.item(i);
				}
			}
		}
		Activator.getDefault().logError(Constants.ERROR_CANNOT_FIND_NODE);
		return null;
	}

	/**
	 * This method filters a certain child {@link Node} in a parent node, on the basis of the {@link Node} name.
	 * 
	 * @param parent
	 * @param nodeName
	 * @return the filtered node
	 * @throws NoSuchElementException
	 */
	public Node getChildNodeByTagName(final Node parent, final String nodeName) {
		final NodeList childList = parent.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			if (childList.item(i).getNodeName().equals(nodeName)) {
				return childList.item(i);
			}
		}
		return null;
	}

	/**
	 * This method updates the value of a certain {@link Node}.
	 * 
	 * @param node
	 * @param newValue
	 */
	public void updateNodeValue(final Node node, final String newValue) {
		node.setTextContent(newValue);
		this.doc = node.getOwnerDocument();
	}

	public Element createElement(final String elementName) {
		return doc.createElement(elementName);
	}

	public File getXmlFile() {
		return this.xmlFile;
	}

	public void setXmlFile(final File xmlFile) {
		this.xmlFile = xmlFile;
	}

	public Document getDoc() {
		return this.doc;
	}

	public void setDoc(final Document doc) {
		this.doc = doc;
	}

}
