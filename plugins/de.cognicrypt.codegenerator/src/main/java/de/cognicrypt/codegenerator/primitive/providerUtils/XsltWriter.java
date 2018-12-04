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
		this.docBuilder = this.docFactory.newDocumentBuilder();
		this.doc = this.docBuilder.newDocument();
	}

	public void setRoot(final String root) {

		this.rootElement = this.doc.createElement(root);
		this.doc.appendChild(this.rootElement);
	}

	public void addElement(final String name, final String value) {
		final Element element = this.doc.createElement(name);
		element.appendChild(this.doc.createTextNode(value));
		this.rootElement.appendChild(element);
	}

	// write the content into xml file
	public void transformXml(final File xmlFile) throws TransformerException {
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		final DOMSource source = new DOMSource(this.doc);
		final StreamResult result = new StreamResult(xmlFile);

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);
		System.out.println("File saved!");
	}

	public void transformXsl(final File xslFile, final File xmlFile) throws ParserConfigurationException, TransformerFactoryConfigurationError, SAXException, IOException, TransformerException {
		this.docFactory = DocumentBuilderFactory.newInstance();
		final javax.xml.parsers.DocumentBuilder builder = this.docFactory.newDocumentBuilder();
		final StreamSource styleSource = new StreamSource(xslFile);
		final Transformer t = TransformerFactory.newInstance().newTransformer(styleSource);
		final Document xml = builder.parse(xmlFile);
		final File resultFile = new File(CodeGenUtils.getResourceFromWithin(Constants.primitivesPath) + Constants.innerFileSeparator + "test.txt");
		final StreamResult result = new StreamResult(resultFile);
		//transformation
		t.transform(new DOMSource(xml), result);
		resultFile.delete();
	}

}
