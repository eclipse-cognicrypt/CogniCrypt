/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.cognicrypt.codegenerator.primitive.providerUtils.XsltWriter;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;

public class XsltWriterTest {

	XsltWriter xslt;
	DocumentBuilderFactory docFactory;
	DocumentBuilder docBuilder;
	Document doc;
	Element rootElement;
	File xmlFile;
	File xslFile = CodeGenUtils.getResourceFromWithin(Constants.testPrimitverFolder + "xslTest.xsl");
	File resultTest;

	@Before
	public void setUp() {
		try {
			xmlFile = new File(CodeGenUtils.getResourceFromWithin(Constants.testPrimitverFolder) + "xmlTestFile.xml");
			xslt = new XsltWriter();
			xslt.createDocument();
			xslt.setRoot("testRoot");
			xslt.addElement("test", "this test");

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void xmlTransformTest() throws TransformerException, ParserConfigurationException, TransformerFactoryConfigurationError, SAXException, IOException {
		xslt.transformXml(xmlFile);
		xslt.transformXsl(xslFile, xmlFile);
		resultTest = CodeGenUtils.getResourceFromWithin(Constants.primitivesPath + Constants.innerFileSeparator + "transformedFile.txt");
		String content = readFile(resultTest.getAbsolutePath());
		assertEquals(content, "this test is a success.");
		assertNotEquals(xmlFile.length(), 0);

	}

	@After
	public void deleteFiles() {
		xmlFile.delete();
		resultTest.delete();
	}

	static String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}
}
