/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.primitive.test;

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
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.primitive.providerUtils.XsltWriter;
import de.cognicrypt.utils.Utils;

public class XsltWriterTest {

	XsltWriter xslt;
	DocumentBuilderFactory docFactory;
	DocumentBuilder docBuilder;
	Document doc;
	Element rootElement;
	File xmlFile;
	File xslFile = Utils.getResourceFromWithin(Constants.testPrimitverFolder + "xslTest.xsl");
	File resultTest;

	@Before
	public void setUp() {
		try {
			this.xmlFile = new File(Utils.getResourceFromWithin(Constants.testPrimitverFolder) + "xmlTestFile.xml");
			this.xslt = new XsltWriter();
			this.xslt.createDocument();
			this.xslt.setRoot("testRoot");
			this.xslt.addElement("test", "this test");

		}
		catch (final ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void xmlTransformTest() throws TransformerException, ParserConfigurationException, TransformerFactoryConfigurationError, SAXException, IOException {
		this.xslt.transformXml(this.xmlFile);
		this.xslt.transformXsl(this.xslFile, this.xmlFile);
		this.resultTest = Utils.getResourceFromWithin(Constants.primitivesPath + Constants.innerFileSeparator + "transformedFile.txt");
		final String content = readFile(this.resultTest.getAbsolutePath());
		assertEquals(content, "this test is a success.");
		assertNotEquals(this.xmlFile.length(), 0);

	}

	@After
	public void deleteFiles() {
		this.xmlFile.delete();
		this.resultTest.delete();
	}

	static String readFile(final String path) throws IOException {
		final byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}
}
