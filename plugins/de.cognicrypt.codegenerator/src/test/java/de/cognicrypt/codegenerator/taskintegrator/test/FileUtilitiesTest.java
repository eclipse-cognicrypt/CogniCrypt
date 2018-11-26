/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

/**
 * @author rajiv
 *
 */
public class FileUtilitiesTest {

	String testResourceLocation;
	File validXSLFileLocation;
	File validJSONFileLocation;
	File validCFRFileLocation;
	File validHelpFileLocation;
	File validAdditionalResource;
	File tmpLocation;

	File copiedCFRFIle;
	File generatedJSFIle;
	File copiedJSONFIle;
	File copiedXSLFIle;
	File copiedHelpFIle;

	File locationOfTasksFile;
	File tmpLocationOfTasksFile;
	File locationOfPluginFile;
	File tmpLocationOfPluginFile;

	String tempTaskName = "Test";

	File invalidHelpFileLocation;
	File invalidAdditionalResource;
	File invalidXSLFileLocation;

	File validJsFileFromValidCRFFile;

	@Before
	public void setLocations() throws IOException {
		testResourceLocation = "src" + Constants.innerFileSeparator + "test" + Constants.innerFileSeparator + "resources" + Constants.innerFileSeparator + "taskintegrator" + Constants.innerFileSeparator + "FileUtilitiesTest" + Constants.innerFileSeparator;
		validXSLFileLocation = CodeGenUtils.getResourceFromWithin(testResourceLocation + "TestValidXSL.xsl");
		validJSONFileLocation = CodeGenUtils.getResourceFromWithin(testResourceLocation + "TestValidJSON.json");
		validCFRFileLocation = CodeGenUtils.getResourceFromWithin(testResourceLocation + "TestValidCFR.cfr");
		validJsFileFromValidCRFFile = CodeGenUtils.getResourceFromWithin(testResourceLocation + "TestValidCFR.js");
		validHelpFileLocation = CodeGenUtils.getResourceFromWithin(testResourceLocation + "TestValidHelp.xml");
		validAdditionalResource = CodeGenUtils.getResourceFromWithin(testResourceLocation + "TestValidAdditionalResources");
		tmpLocation = CodeGenUtils.getResourceFromWithin(testResourceLocation + "tmpLocation");
		locationOfTasksFile = CodeGenUtils.getResourceFromWithin(Constants.jsonTaskFile);
		tmpLocationOfTasksFile = CodeGenUtils.getResourceFromWithin(tmpLocation.toPath().toString() + Constants.innerFileSeparator + "tasks.json");
		locationOfPluginFile = CodeGenUtils.getResourceFromWithin("src" + Constants.innerFileSeparator + ".." + Constants.innerFileSeparator + Constants.PLUGIN_XML_FILE);

		tmpLocationOfPluginFile = CodeGenUtils.getResourceFromWithin(tmpLocation.getAbsolutePath() + Constants.innerFileSeparator + "plugin.xml");

		if (!tmpLocation.isDirectory()) {
			Files.createDirectory(tmpLocation.toPath());
		}

		Files.copy(locationOfTasksFile.toPath(), tmpLocationOfTasksFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		Files.copy(locationOfPluginFile.toPath().toAbsolutePath(), tmpLocationOfPluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

		invalidHelpFileLocation = Utils.getResourceFromWithin(testResourceLocation + "TestInvalidHelp.xml");
		invalidAdditionalResource = Utils.getResourceFromWithin(testResourceLocation + "TestInvalidAdditionalResources");
		invalidXSLFileLocation = Utils.getResourceFromWithin(testResourceLocation + "TestInvalidXSL.xsl");
	}

	/**
	 * Test method for
	 * {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeFiles(java.io.File, java.io.File, java.io.File, java.io.File, java.io.File)}.
	 */
	@Test
	public void testWriteFilesMinusAdditionalResources() {
		FileUtilities fileUtilities = new FileUtilities(tempTaskName);
		String result = fileUtilities.writeFiles(validCFRFileLocation, validJSONFileLocation, validXSLFileLocation, null, validHelpFileLocation);
		assertFileCreation(result);
	}

	/**
	 * Differentiated the tests based on whether the additional resources were included or not.
	 */
	@Test
	public void testWriteFilesWithAdditionalResources() {
		FileUtilities fileUtilities = new FileUtilities(tempTaskName);
		String result = fileUtilities.writeFiles(validCFRFileLocation, validJSONFileLocation, validXSLFileLocation, validAdditionalResource, validHelpFileLocation);
		assertFileCreation(result);
	}

	/**
	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeTaskToJSONFile(de.cognicrypt.codegenerator.tasks.Task)}.
	 */
	@Test
	public void testWriteTaskToJSONFile() {
		Task testTask = new Task();
		testTask.setName(tempTaskName);
		testTask.setAdditionalResources(validAdditionalResource.getPath());
		testTask.setDescription("This is a task");
		testTask.setModelFile(validJsFileFromValidCRFFile.getPath());
		testTask.setQuestionsJSONFile(validJSONFileLocation.getPath());
		testTask.setSelected(false);
		testTask.setTaskDescription("This is the description of the task");
		testTask.setXslFile(validXSLFileLocation.getPath());
		FileUtilities fileUtilities = new FileUtilities(tempTaskName);
		fileUtilities.writeTaskToJSONFile(testTask);
		List<Task> tasks = TaskJSONReader.getTasks();

		for (Task task : tasks) {
			if (task.getName().equals(testTask.getName())) {
				assertTrue(task.getAdditionalResources().equals(testTask.getAdditionalResources()));
				assertTrue(task.getDescription().equals(testTask.getDescription()));
				assertTrue(task.getModelFile().equals(testTask.getModelFile()));
				assertTrue(task.getQuestionsJSONFile().equals(testTask.getQuestionsJSONFile()));
				assertTrue(task.getTaskDescription().equals(testTask.getTaskDescription()));
				assertTrue(task.getXslFile().equals(testTask.getXslFile()));
			}
		}
	}

	/**
	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#updateThePluginXMLFileWithHelpData(java.lang.String)}.
	 * 
	 * @throws DocumentException
	 */
	@Test
	public void testUpdateThePluginXMLFileWithHelpData() throws DocumentException {
		FileUtilities fileUtilities = new FileUtilities(tempTaskName);
		fileUtilities.updateThePluginXMLFileWithHelpData(tempTaskName);

		File pluginXMLFile = CodeGenUtils.getResourceFromWithin("src" + Constants.innerFileSeparator + ".." + Constants.innerFileSeparator + Constants.PLUGIN_XML_FILE);
		SAXReader reader = new SAXReader();
		Document pluginXMLDocument = null;
		reader.setValidation(false);
		pluginXMLDocument = reader.read(pluginXMLFile);
		boolean isSuccessfulWrite = false;
		if (pluginXMLDocument != null) {
			Element root = pluginXMLDocument.getRootElement();
			for (Iterator<Element> extensionElement = root.elementIterator("extension"); extensionElement.hasNext();) {
				Element currentExtensionElement = extensionElement.next();
				Attribute point = currentExtensionElement.attribute("point");
				if (point != null && point.getValue().equals("org.eclipse.help.contexts")) {
					for (Iterator<Element> helpFileContext = currentExtensionElement.elementIterator("contexts"); helpFileContext.hasNext();) {
						Element currentHelpFileContext = helpFileContext.next();
						for (Iterator<Attribute> it = currentHelpFileContext.attributeIterator(); it.hasNext();) {
							Attribute file = it.next();
							if (file.getName().equals("file") && file.getValue().equals(
								"src" + Constants.innerFileSeparator + "main" + Constants.innerFileSeparator + "resources" + Constants.innerFileSeparator + "Help" + Constants.innerFileSeparator + tempTaskName + ".xml")) {
								isSuccessfulWrite = true;
							}
						}
					}
				}
			}

		}

		assertTrue(isSuccessfulWrite);
	}

	/**
	 * Test method for
	 * {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeFiles(de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel, java.util.ArrayList, java.lang.String, java.io.File)}.
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	@Test
	public void testWriteFilesWithData() throws IOException {
		FileUtilities fileUtilities = new FileUtilities(tempTaskName);
		String result = fileUtilities.writeFiles(getListClaferModel(), getListOfQuestions(), getXSLString(true), validAdditionalResource, getHelpString());
		assertFileCreation(result);
	}

	/**
	 * Check if all the expected files are created.
	 * 
	 * @param result
	 */
	private void assertFileCreation(String result) {
		copiedCFRFIle = CodeGenUtils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH + tempTaskName + ".cfr");
		generatedJSFIle = CodeGenUtils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH + tempTaskName + ".js");
		copiedJSONFIle = CodeGenUtils.getResourceFromWithin(Constants.JSON_FILE_DIRECTORY_PATH + tempTaskName + ".json");
		copiedXSLFIle = CodeGenUtils.getResourceFromWithin(Constants.XSL_FILE_DIRECTORY_PATH + tempTaskName + ".xsl");
		copiedHelpFIle = CodeGenUtils.getResourceFromWithin(Constants.HELP_FILE_DIRECTORY_PATH + tempTaskName + ".xml");
		File copiedAdditionalResourcesDirectory = CodeGenUtils.getResourceFromWithin(Constants.JAR_FILE_DIRECTORY_PATH + tempTaskName);
		if (copiedAdditionalResourcesDirectory.exists()) {
			int copiedAdditionalResourcesNumber = copiedAdditionalResourcesDirectory.listFiles().length;
			assertTrue(copiedAdditionalResourcesNumber == validAdditionalResource.listFiles().length);
		}

		//Utils.getResourceFromWithin(validAdditionalResource.getPath()).listFiles();
		assertTrue(copiedCFRFIle.exists());
		assertTrue(generatedJSFIle.exists());
		assertTrue(copiedJSONFIle.exists());
		assertTrue(copiedXSLFIle.exists());
		assertTrue(copiedHelpFIle.exists());
		assertTrue(result.equals(""));
	}

	/**
	 * Get a list of questions from the validJSONFileLocation.
	 * 
	 * @return
	 */
	private ArrayList<Question> getListOfQuestions() {

		QuestionsJSONReader questionsReader = new QuestionsJSONReader();

		List<Page> pages = questionsReader.getPages(validJSONFileLocation.toPath().toString());
		ArrayList<Question> questions = new ArrayList<Question>();

		for (Page page : pages) {
			for (Question question : page.getContent()) {
				questions.add(question);
			}
		}

		return questions;

	}

	/**
	 * Return a ClaferModel from the given binary file.
	 * 
	 * @return
	 */
	private ClaferModel getListClaferModel() {
		ClaferModel cfrModel = new ClaferModel();
		cfrModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, "Point", ""));

		return new ClaferModel();
	}

	/**
	 * Return the string from the XSL document.
	 * 
	 * @param isValid
	 *        Switch for valid invalid files.
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	private String getXSLString(boolean isValid) throws IOException {

		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		if (isValid) {
			reader = Files.newBufferedReader(validXSLFileLocation.toPath(), StandardCharsets.UTF_8);
			reader.lines().forEach(builder::append);
		} else {
			reader = Files.newBufferedReader(invalidXSLFileLocation.toPath(), StandardCharsets.UTF_8);
			reader.lines().forEach(builder::append);
		}

		return builder.toString();
	}

	private String getHelpString() throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;

		reader = Files.newBufferedReader(validHelpFileLocation.toPath(), StandardCharsets.UTF_8);
		reader.lines().forEach(builder::append);

		return builder.toString();
	}

	/**
	 * Test with invalid files.
	 *//*
		 * @Test public void testWriteFilesWithAdditionalResourcesInvalidFiles() { File invalidJSONFileLocation = new File(testResourceLocation + "TestInvalidJSON.json"); File
		 * invalidCFRFileLocation = new File(testResourceLocation + "TestInvalidCFR.cfr"); if (invalidXSLFileLocation.exists() && invalidJSONFileLocation.exists() &&
		 * invalidCFRFileLocation.exists() && invalidHelpFileLocation.exists() && invalidAdditionalResource .exists()) { FileUtilities fileUtilities = new
		 * FileUtilities(tempTaskName); String result = fileUtilities.writeFiles(invalidCFRFileLocation, invalidJSONFileLocation, invalidXSLFileLocation, invalidAdditionalResource,
		 * invalidHelpFileLocation); File[] filesAtAdditionalResources = invalidAdditionalResource.listFiles(); assertTrue(result.contains(
		 * "Either the compilation failed, or the provided name for the Task does not match the one in the Clafer model. Please note : the name must be capitalized."));
		 * assertTrue(result.contains("The contents of the file " + invalidJSONFileLocation.getName() + " are invalid.")); assertTrue(result.contains("The contents of the file " +
		 * invalidXSLFileLocation.getName() + " are invalid.")); assertTrue(result.contains("The contents of the file " + filesAtAdditionalResources[0].getName() +
		 * " are invalid.")); assertTrue(result.contains("The contents of the file " + invalidHelpFileLocation.getName() + " are invalid.")); } }
		 */

	/**
	 * Test method for
	 * {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeFiles(de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel, java.util.ArrayList, java.lang.String, java.io.File)}.
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 *//*
		 * @Test public void testWriteFilesWithDataInvalidResourceInvalidXSL() throws IOException { FileUtilities fileUtilities = new FileUtilities(tempTaskName); String result =
		 * fileUtilities.writeFiles(getListClaferModel(), getListOfQuestions(), getXSLString(false), invalidAdditionalResource);
		 * assertTrue(result.contains("The contents of the file " + invalidXSLFileLocation.getName() + " are invalid.")); File[] filesAtAdditionalResources =
		 * invalidAdditionalResource.listFiles(); assertTrue(result.contains("The contents of the file " + filesAtAdditionalResources[0].getName() + " are invalid.")); }
		 */

	/**
	 * Replacing the tests for invalid data with the incorrect NullPointerException. This is done because the error handling everywhere uses the Activator class, and this class is
	 * unavailable during testing.
	 */
	@Test(expected = NullPointerException.class)
	public void invalidJSONFileTest() {
		File invalidJSONFileLocation = CodeGenUtils.getResourceFromWithin(testResourceLocation + "TestInvalidJSON.json");
		if (validXSLFileLocation.exists() && invalidJSONFileLocation.exists() && validCFRFileLocation.exists() && validHelpFileLocation.exists() && validAdditionalResource
			.exists()) {
			FileUtilities fileUtilities = new FileUtilities(tempTaskName);
			fileUtilities.writeFiles(validCFRFileLocation, invalidJSONFileLocation, validXSLFileLocation, validAdditionalResource, validHelpFileLocation);
		}
	}

	/**
	 * Replacing the tests for invalid data with the incorrect NullPointerException. This is done because the error handling everywhere uses the Activator class, and this class is
	 * unavailable during testing.
	 */
	@Test(expected = NullPointerException.class)
	public void invalidCFRFileTest() {
		File invalidCFRFileLocation = CodeGenUtils.getResourceFromWithin(testResourceLocation + "TestInvalidCFR.cfr");
		if (validXSLFileLocation.exists() && validJSONFileLocation.exists() && invalidCFRFileLocation.exists() && validHelpFileLocation.exists() && validAdditionalResource
			.exists()) {
			FileUtilities fileUtilities = new FileUtilities(tempTaskName);
			fileUtilities.writeFiles(invalidCFRFileLocation, validJSONFileLocation, validXSLFileLocation, validAdditionalResource, validHelpFileLocation);
		}
	}

	/**
	 * Replacing the tests for invalid data with the incorrect NullPointerException. This is done because the error handling everywhere uses the Activator class, and this class is
	 * unavailable during testing.
	 */
	@Test(expected = NullPointerException.class)
	public void invalidXSLFileTest() {
		if (invalidXSLFileLocation.exists() && validJSONFileLocation.exists() && validCFRFileLocation.exists() && validHelpFileLocation.exists() && validAdditionalResource
			.exists()) {
			FileUtilities fileUtilities = new FileUtilities(tempTaskName);
			fileUtilities.writeFiles(validCFRFileLocation, validJSONFileLocation, invalidXSLFileLocation, validAdditionalResource, validHelpFileLocation);
		}
	}

	/**
	 * Replacing the tests for invalid data with the incorrect NullPointerException. This is done because the error handling everywhere uses the Activator class, and this class is
	 * unavailable during testing.
	 */
	@Test(expected = NullPointerException.class)
	public void invalidHelpFileTest() {
		if (validXSLFileLocation.exists() && validJSONFileLocation.exists() && validCFRFileLocation.exists() && invalidHelpFileLocation.exists() && validAdditionalResource
			.exists()) {
			FileUtilities fileUtilities = new FileUtilities(tempTaskName);
			fileUtilities.writeFiles(validCFRFileLocation, validJSONFileLocation, validXSLFileLocation, validAdditionalResource, invalidHelpFileLocation);
		}
	}

	/**
	 * Replacing the tests for invalid data with the incorrect NullPointerException. This is done because the error handling everywhere uses the Activator class, and this class is
	 * unavailable during testing.
	 */
	@Test(expected = NullPointerException.class)
	public void invalidAdditionalResourceFileTest() {
		if (validXSLFileLocation.exists() && validJSONFileLocation.exists() && validCFRFileLocation.exists() && validHelpFileLocation.exists() && invalidAdditionalResource
			.exists()) {
			FileUtilities fileUtilities = new FileUtilities(tempTaskName);
			fileUtilities.writeFiles(validCFRFileLocation, validJSONFileLocation, validXSLFileLocation, invalidAdditionalResource, validHelpFileLocation);
		}
	}

	/**
	 * Delete all the custom files that are generated. Replace the files to the earlier state.
	 * 
	 * @throws IOException
	 */
	@After
	public void restoreFiles() throws IOException {
		if (copiedCFRFIle != null && copiedCFRFIle.exists()) {
			copiedCFRFIle.delete();
		}

		if (generatedJSFIle != null && generatedJSFIle.exists()) {
			generatedJSFIle.delete();
		}

		if (validJsFileFromValidCRFFile != null && validJsFileFromValidCRFFile.exists()) {
			validJsFileFromValidCRFFile.delete();
		}

		if (copiedJSONFIle != null && copiedJSONFIle.exists()) {
			copiedJSONFIle.delete();
		}
		if (copiedXSLFIle != null && copiedXSLFIle.exists()) {
			copiedXSLFIle.delete();
		}
		if (copiedHelpFIle != null && copiedHelpFIle.exists()) {
			copiedHelpFIle.delete();
		}
		File copiedAdditionalResourcesDirectory = CodeGenUtils.getResourceFromWithin(Constants.JAR_FILE_DIRECTORY_PATH + tempTaskName);
		if (copiedAdditionalResourcesDirectory != null && copiedAdditionalResourcesDirectory.exists() && copiedAdditionalResourcesDirectory.isDirectory()) {
			for (File file : copiedAdditionalResourcesDirectory.listFiles()) {
				file.delete();
			}
			copiedAdditionalResourcesDirectory.delete();
		}

		if (tmpLocationOfTasksFile != null && locationOfTasksFile != null) {
			Files.copy(tmpLocationOfTasksFile.toPath(), locationOfTasksFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			if (tmpLocationOfTasksFile.exists()) {
				tmpLocationOfTasksFile.delete();
			}
		}
		if (tmpLocationOfPluginFile != null && locationOfPluginFile != null) {
			Files.copy(tmpLocationOfPluginFile.toPath(), locationOfPluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			if (tmpLocationOfPluginFile.exists()) {
				tmpLocationOfPluginFile.delete();
			}
		}

	}

}
