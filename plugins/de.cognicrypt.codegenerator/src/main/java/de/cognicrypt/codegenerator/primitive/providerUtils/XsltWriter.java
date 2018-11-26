/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.providerUtils;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;

public class XsltWriter {

	DocumentBuilderFactory docFactory;
	DocumentBuilder docBuilder;
	Document doc;
	Element rootElement;

	/**
	 * Generate the xml file related to the xsl
	 */
	public XsltWriter() {

	}

	public void createDocument() throws ParserConfigurationException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docBuilder = docFactory.newDocumentBuilder();
		this.doc = docBuilder.newDocument();
	}

	public void setRoot(String root) {

		this.rootElement = this.doc.createElement(root);
		doc.appendChild(rootElement);
	}

	public void addElement(String name, String value) {
		Element element = doc.createElement(name);
		element.appendChild(doc.createTextNode(value));
		this.rootElement.appendChild(element);
	}

	// write the content into xml file
	public void transformXml(File xmlFile) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(this.doc);
		StreamResult result = new StreamResult(xmlFile);

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);
		System.out.println("File saved!");
	}

	public void transformXsl(File xslFile, File xmlFile) throws ParserConfigurationException, TransformerFactoryConfigurationError, SAXException, IOException, TransformerException {
		docFactory = DocumentBuilderFactory.newInstance();
		javax.xml.parsers.DocumentBuilder builder = docFactory.newDocumentBuilder();
		StreamSource styleSource = new StreamSource(xslFile);
		Transformer t = TransformerFactory.newInstance().newTransformer(styleSource);
		Document xml = builder.parse(xmlFile);
		File resultFile = new File(CodeGenUtils.getResourceFromWithin(Constants.primitivesPath) + Constants.innerFileSeparator + "test.txt");
		StreamResult result = new StreamResult(resultFile);
		//transformation 
		t.transform(new DOMSource(xml), result);
		resultFile.delete();
	}

}
