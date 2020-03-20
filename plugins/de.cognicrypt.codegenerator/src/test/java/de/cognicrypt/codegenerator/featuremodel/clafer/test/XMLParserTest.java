/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.featuremodel.clafer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.clafer.instance.InstanceClafer;
import org.dom4j.DocumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModel;
import de.cognicrypt.codegenerator.featuremodel.clafer.InstanceGenerator;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.XMLClaferParser;
import de.cognicrypt.utils.FileUtils;

@RunWith(value = Parameterized.class)
public class XMLParserTest {

	ClaferModel claferModel;
	InstanceGenerator instGen;
	HashMap<Question, Answer> constraints;
	InstanceClafer inst;

	// parameterized by junit.runners.Parameterized (see the constructor)
	private final String taskName;
	private final String jsFilePath;
	private final String validFilePath;

	// temporary output file
	// common for all tests (among different parameterizations)
	String xmlTestFilePath = "src/test/testXMLwriteInstance.xml";

	public XMLParserTest(final String taskName, final String jsFile, final String xmlFile) {
		this.taskName = "c0_" + taskName;
		this.jsFilePath = jsFile;
		this.validFilePath = xmlFile;
	}

	@Before
	public void setUp() throws Exception {
		this.claferModel = new ClaferModel(this.jsFilePath);
		this.instGen = new InstanceGenerator(this.jsFilePath, this.taskName, "description");
		this.constraints = new HashMap<>();
		this.inst = this.instGen.generateInstances(this.constraints).get(0);
	}

	@After
	public void tearDown() {
		FileUtils.deleteFile(this.xmlTestFilePath);
	}

	@Parameters(name = "testXmlParser{index}({0},{1})")
	public static Collection<Object[]> data() {
		return Arrays.asList(
			new Object[][] { { "PasswordStoring", "src/test/resources/hashing.js", "src/test/resources/validHashing.xml" }, { "SecurityTestTask", "src/test/resources/security.js", "src/test/resources/validSecurity.xml" } });
	}

	@Test
	public void testWriteToFile() throws IOException, DocumentException {
		final byte[] validBytes = new byte[2000];
		final byte[] generatedBytes = new byte[2000];

		final FileInputStream validFile = new FileInputStream(this.validFilePath);
		validFile.read(validBytes);
		validFile.close();

		final XMLClaferParser xmlparser = new XMLClaferParser();
		xmlparser.displayInstanceValues(this.inst, this.constraints);
		xmlparser.writeXMLToFile(this.xmlTestFilePath);

		final FileInputStream testFile = new FileInputStream(this.xmlTestFilePath);
		testFile.read(generatedBytes);
		testFile.close();

		assertTrue(uglifyXML(new String(validBytes)).trim().contentEquals(uglifyXML(new String(generatedBytes)).trim()));
	}

	@Test
	public void testXMLValidity() throws DocumentException, IOException {
		final String encoding = "UTF-8";
		final byte[] encoded = Files.readAllBytes(Paths.get(this.validFilePath));
		final String validXML = new String(encoded, encoding);
		final XMLClaferParser xmlparser = new XMLClaferParser();

		final String xml = xmlparser.displayInstanceValues(this.inst, this.constraints).asXML();
		assertEquals(uglifyXML(validXML), uglifyXML(xml));
	}

	/**
	 * move all tags together and remove newlines
	 */
	public String uglifyXML(final String input) {
		return input.replaceAll(">\\s*<", "><").replace("\n", "").trim();
	}
}
