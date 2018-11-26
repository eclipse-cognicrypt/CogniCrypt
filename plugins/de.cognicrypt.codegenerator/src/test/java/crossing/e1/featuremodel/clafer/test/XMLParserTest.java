/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package crossing.e1.featuremodel.clafer.test;

import static org.junit.Assert.assertEquals;

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
import de.cognicrypt.codegenerator.utilities.XMLParser;
import de.cognicrypt.utils.FileHelper;

@RunWith(value = Parameterized.class)
public class XMLParserTest {

	ClaferModel claferModel;
	InstanceGenerator instGen;
	HashMap<Question, Answer> constraints;
	InstanceClafer inst;

	// parameterized by junit.runners.Parameterized (see the constructor)
	private String taskName;
	private String jsFilePath;
	private String validFilePath;

	// temporary output file
	// common for all tests (among different parameterizations)
	String xmlTestFilePath = "src/test/testXMLwriteInstance.xml";

	public XMLParserTest(String taskName, String jsFile, String xmlFile) {
		this.taskName = "c0_" + taskName;
		this.jsFilePath = jsFile;
		this.validFilePath = xmlFile;
	}

	@Before
	public void setUp() throws Exception {
		this.claferModel = new ClaferModel(jsFilePath);
		this.instGen = new InstanceGenerator(jsFilePath, taskName, "description");
		this.constraints = new HashMap<>();
		this.inst = this.instGen.generateInstances(this.constraints).get(0);
	}

	@After
	public void tearDown() {
		FileHelper.deleteFile(this.xmlTestFilePath);
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

		final XMLParser xmlparser = new XMLParser();
		xmlparser.displayInstanceValues(this.inst, this.constraints);
		xmlparser.writeXMLToFile(this.xmlTestFilePath);

		final FileInputStream testFile = new FileInputStream(this.xmlTestFilePath);
		testFile.read(generatedBytes);
		testFile.close();

		assertEquals(new String(validBytes), new String(generatedBytes));
	}

	@Test
	public void testXMLValidity() throws DocumentException, IOException {
		final String encoding = "UTF-8";
		byte[] encoded = Files.readAllBytes(Paths.get(this.validFilePath));
		String validXML = new String(encoded, encoding);
		//		StringBuilder importBuilder = new StringBuilder();
		//		for (String importSt : Constants.xmlimportsarr) {
		//			importBuilder.append("<Import>");
		//			importBuilder.append(importSt);
		//			importBuilder.append("</Import>");
		//		}

		//		final String validXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<task description=\"PasswordStoring\"><Package>Crypto</Package><Imports>" + importBuilder
		//			.toString() + "</Imports><algorithm type=\"Digest\"><outputSize>384</outputSize><name>SHA-384</name><performance>3</performance><status>secure</status></algorithm><algorithm type=\"KeyDerivationAlgorithm\"><name>PBKDF</name><performance>2</performance><status>secure</status></algorithm><name>Password Storing</name><code/></task>";
		final XMLParser xmlparser = new XMLParser();

		final String xml = xmlparser.displayInstanceValues(this.inst, this.constraints).asXML();
		assertEquals(uglifyXML(validXML), uglifyXML(xml));
	}

	/**
	 * move all tags together and remove newlines
	 */
	public String uglifyXML(String input) {
		return input.replaceAll(">\\s*<", "><").replace("\n", "");
	}
}
