/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Point;
import org.junit.Before;
import org.junit.Test;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.XSLStringGenerationAndManipulation;
import de.cognicrypt.integrator.task.controllers.XmlRegion;
import de.cognicrypt.integrator.task.controllers.XmlRegionAnalyzer;

public class XSLStringGenerationAndManipulationTests {

	File xslFilePath = null;
	File javaFilePath = null;
	File txtFilePath = null;
	File javaXSLFilePath = null;
	File txtXSLFilePath = null;
	File javaXSLFileWithExistingXSLPath = null;
	File txtXSLFileWithExistingXSLPath = null;
	String testResourceLocation = null;

	/**
	 * Fill the file paths.
	 */
	@Before
	public void getxslFileFromMainResources() {
		// get the first file from the list of active xsl files for the test.
		this.xslFilePath = CodeGenUtils.getResourceFromWithin(Constants.XSL_FILE_DIRECTORY_PATH).listFiles()[0];
		this.testResourceLocation = "src" + Constants.innerFileSeparator + "test" + Constants.innerFileSeparator + "resources" + Constants.innerFileSeparator + "taskintegrator"
				+ Constants.innerFileSeparator + "XSLTests" + Constants.innerFileSeparator;
		this.javaFilePath = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "JavaFileTest.java");
		this.txtFilePath = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TextFileTest.txt");
		this.javaXSLFilePath = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "JavaFileTest.xsl");
		this.txtXSLFilePath = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TextFileTest.xsl");
		this.javaXSLFileWithExistingXSLPath = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "JavaFileTestExistingXSL.xsl");
		this.txtXSLFileWithExistingXSLPath = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TextFileTestExistingXSL.xsl");

	}

	/**
	 * The test will fail if the string generated is invalid.
	 * <p>
	 * This test will read three types of files and attempt to add the same to an empty string. A valid XSL file should be generated.
	 *
	 * @throws DocumentException
	 */
	@Test
	public void testGenerateXSLStringFromPathWithNoText() throws DocumentException {
		boolean generatedFileValidity = false;
		final Document xslGenerated =
				DocumentHelper.parseText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(this.xslFilePath.getAbsolutePath(), "", new Point(0, 0), null));
		final Document javaGenerated =
				DocumentHelper.parseText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(this.javaFilePath.getAbsolutePath(), "", new Point(0, 0), null));
		final Document txtGenerated =
				DocumentHelper.parseText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(this.txtFilePath.getAbsolutePath(), "", new Point(0, 0), null));
		generatedFileValidity = true;
		assertTrue(generatedFileValidity);

		final Document xslExisting = readDocFomFile(this.xslFilePath);
		assertTrue(xslGenerated.getRootElement().getName().equals(xslExisting.getRootElement().getName()));
		assertTrue(xslGenerated.getXMLEncoding().equals(xslExisting.getXMLEncoding()));
		assertTrue(xslGenerated.asXML().equals(xslExisting.asXML()));
		final Document javaExisting = readDocFomFile(this.javaXSLFilePath);
		assertTrue(javaGenerated.getRootElement().getName().equals(javaExisting.getRootElement().getName()));
		assertTrue(javaGenerated.getXMLEncoding().equals(javaExisting.getXMLEncoding()));
		assertTrue(javaGenerated.asXML().equals(javaExisting.asXML()));
		final Document txtExisting = readDocFomFile(this.txtXSLFilePath);
		assertTrue(txtGenerated.getRootElement().getName().equals(txtExisting.getRootElement().getName()));
		assertTrue(txtGenerated.getXMLEncoding().equals(txtExisting.getXMLEncoding()));
		assertTrue(txtGenerated.asXML().equals(txtExisting.asXML()));

	}

	/**
	 * Generate a Document object from the given file location.
	 *
	 * @param file
	 * @return
	 * @throws DocumentException
	 */
	public Document readDocFomFile(final File file) throws DocumentException {
		final SAXReader reader = new SAXReader();
		reader.setValidation(false);
		return reader.read(file.getAbsolutePath());
	}

	/**
	 * In this test, there is some random text that already exists.
	 *
	 * @throws DocumentException
	 */
	@Test
	public void testGenerateXSLStringFromPathWithNormalText() throws DocumentException {
		boolean generatedFileValidity = false;
		final Document xslGenerated =
				DocumentHelper.parseText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(this.xslFilePath.getAbsolutePath(), "This is just a test", new Point(0, 0), null));
		final Document javaGenerated =
				DocumentHelper.parseText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(this.javaFilePath.getAbsolutePath(), "This is just a test", new Point(0, 0), null));
		final Document txtGenerated =
				DocumentHelper.parseText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(this.txtFilePath.getAbsolutePath(), "This is just a test", new Point(0, 0), null));
		generatedFileValidity = true;
		assertTrue(generatedFileValidity);

		final Document xslExisting = readDocFomFile(this.xslFilePath);
		assertTrue(xslGenerated.getRootElement().getName().equals(xslExisting.getRootElement().getName()));
		assertTrue(xslGenerated.getXMLEncoding().equals(xslExisting.getXMLEncoding()));
		assertTrue(xslGenerated.asXML().equals(xslExisting.asXML()));
		final Document javaExisting = readDocFomFile(this.javaXSLFilePath);
		assertTrue(javaGenerated.getRootElement().getName().equals(javaExisting.getRootElement().getName()));
		assertTrue(javaGenerated.getXMLEncoding().equals(javaExisting.getXMLEncoding()));
		assertTrue(javaGenerated.asXML().equals(javaExisting.asXML()));
		final Document txtExisting = readDocFomFile(this.txtXSLFilePath);
		assertTrue(txtGenerated.getRootElement().getName().equals(txtExisting.getRootElement().getName()));
		assertTrue(txtGenerated.getXMLEncoding().equals(txtExisting.getXMLEncoding()));
		assertTrue(txtGenerated.asXML().equals(txtExisting.asXML()));
	}

	/**
	 * In this test, the existing text is a complete xsl document. This document is generated the same way the text addition works, with the additional text as two new lines.
	 *
	 * @throws DocumentException
	 */
	@Test
	public void testGenerateXSLStringFromPathWithXSLText() throws DocumentException {
		boolean generatedFileValidity = false;
		final Document xslGenerated = DocumentHelper.parseText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(this.xslFilePath.getAbsolutePath(),
				XSLStringGenerationAndManipulation.generateXSLStringFromPath(null, "", new Point(0, 0), "\n\n"), new Point(266, 266), null));
		final Document javaGenerated = DocumentHelper.parseText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(this.javaFilePath.getAbsolutePath(),
				XSLStringGenerationAndManipulation.generateXSLStringFromPath(null, "", new Point(0, 0), "\n\n"), new Point(266, 266), null));
		final Document txtGenerated = DocumentHelper.parseText(XSLStringGenerationAndManipulation.generateXSLStringFromPath(this.txtFilePath.getAbsolutePath(),
				XSLStringGenerationAndManipulation.generateXSLStringFromPath(null, "", new Point(0, 0), "\n\n"), new Point(266, 266), null));
		generatedFileValidity = true;
		assertTrue(generatedFileValidity);

		final Document xslExisting = readDocFomFile(this.xslFilePath);
		assertTrue(xslGenerated.getRootElement().getName().equals(xslExisting.getRootElement().getName()));
		assertTrue(xslGenerated.getXMLEncoding().equals(xslExisting.getXMLEncoding()));
		assertTrue(xslGenerated.asXML().equals(xslExisting.asXML()));
		final Document javaExisting = readDocFomFile(this.javaXSLFileWithExistingXSLPath);
		assertTrue(javaGenerated.getRootElement().getName().equals(javaExisting.getRootElement().getName()));
		assertTrue(javaGenerated.getXMLEncoding().equals(javaExisting.getXMLEncoding()));
		assertTrue(javaGenerated.asXML().equals(javaExisting.asXML()));
		final Document txtExisting = readDocFomFile(this.txtXSLFileWithExistingXSLPath);
		assertTrue(txtGenerated.getRootElement().getName().equals(txtExisting.getRootElement().getName()));
		assertTrue(txtGenerated.getXMLEncoding().equals(txtExisting.getXMLEncoding()));
		assertTrue(txtGenerated.asXML().equals(txtExisting.asXML()));
	}

	/**
	 * Basic test for the region analyzer. The Functionality is tested in another test suite.
	 *
	 * @throws IOException
	 */
	@Test
	public void testComputeStyleForXMLRegions() throws IOException {
		final File testFile = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "StackOverflowExample.xml");

		final StringBuilder builder = new StringBuilder();
		final BufferedReader r = Files.newBufferedReader(testFile.toPath(), StandardCharsets.UTF_8);
		r.lines().forEach(builder::append);

		final List<XmlRegion> regions = new XmlRegionAnalyzer().analyzeXml(builder.toString());
		final List<StyleRange> ranges = XSLStringGenerationAndManipulation.computeStyleForXMLRegions(regions);
		assertTrue(ranges.size() == 26839);
	}

	@Test
	public void testXMLGenerationFromTasks() {
		final List<Task> tasks = TaskJSONReader.getTasks();
		for (final Task task : tasks) {
			final HashMap<String, String> tagValueTagData = new HashMap<>();
			final List<Question> questions = new ArrayList<>();
			final QuestionsJSONReader quesJSONReader = new QuestionsJSONReader();
			for (final Page page : quesJSONReader.getPages(task.getQuestionsJSONFile())) {
				for (final Question question : page.getContent()) {
					questions.add(question);
				}
			}

			XSLStringGenerationAndManipulation.getListOfValidSuggestionsForXSLTags(task.getModelFile(), task.getName(), task.getDescription(), questions, tagValueTagData);

			/*
			 * System.out.println(task.getName()); for (String key : tagValueTagData.keySet()) { System.out.println(key + " --> " + tagValueTagData.get(key)); }
			 */

			// Check if the generated XMLs contain the package node.
			assertTrue(tagValueTagData.values().contains("//task/Package"));
			// Check if the Task contains attribute description and a node description.
			assertTrue(tagValueTagData.values().contains("//task[@description='']"));
			// assertTrue(tagValueTagData.values().contains("//task/description"));
			// Check if there is at least one algorithm

			if (task.getName().equals("SymmetricEncryption") || task.getName().equals("SecurePassword") || task.getName().equals("HybridEncryption")) {
				assertTrue(tagValueTagData.values().contains("//task/algorithm[@type='']"));
				assertTrue(tagValueTagData.values().contains("//task/algorithm[@type='']/name"));
			}

			if (task.getName().equals("SecureCommunication") || task.getName().equals("LongTermArchiving") || task.getName().equals("SECMUPACOMP")
					|| task.getName().equals("CertainTrust")) {
				assertTrue(tagValueTagData.values().contains("//task/element[@type='']"));
			}

		}
		// an invalid check.
		final HashMap<String, String> tagValueTagData = null;
		XSLStringGenerationAndManipulation.getListOfValidSuggestionsForXSLTags(null, tasks.get(0).getName(), tasks.get(0).getDescription(), null, tagValueTagData);
		assertNull(tagValueTagData);
	}
}
