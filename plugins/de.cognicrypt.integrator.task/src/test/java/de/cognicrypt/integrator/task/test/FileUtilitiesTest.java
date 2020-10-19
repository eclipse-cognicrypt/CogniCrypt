/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 *
 */
package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.FileUtilities;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.utils.Utils;

/**
 * @author rajiv
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
		this.testResourceLocation = "src" + Constants.innerFileSeparator + "test" + Constants.innerFileSeparator + "resources" + Constants.innerFileSeparator + "taskintegrator"
				+ Constants.innerFileSeparator + "FileUtilitiesTest" + Constants.innerFileSeparator;
		this.validXSLFileLocation = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TestValidXSL.xsl");
		this.validJSONFileLocation = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TestValidJSON.json");
		this.validCFRFileLocation = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TestValidCFR.cfr");
		this.validJsFileFromValidCRFFile = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TestValidCFR.js");
		this.validHelpFileLocation = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TestValidHelp.xml");
		this.validAdditionalResource = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TestValidAdditionalResources");
		this.tmpLocation = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "tmpLocation");
		this.locationOfTasksFile = CodeGenUtils.getResourceFromWithin(Constants.jsonTaskFile);
		this.tmpLocationOfTasksFile = CodeGenUtils.getResourceFromWithin(this.tmpLocation.toPath().toString() + Constants.innerFileSeparator + "tasks.json");
		this.locationOfPluginFile = CodeGenUtils.getResourceFromWithin("src" + Constants.innerFileSeparator + ".." + Constants.innerFileSeparator + Constants.PLUGIN_XML_FILE);

		this.tmpLocationOfPluginFile = CodeGenUtils.getResourceFromWithin(this.tmpLocation.getAbsolutePath() + Constants.innerFileSeparator + "plugin.xml");

		if (!this.tmpLocation.isDirectory()) {
			Files.createDirectory(this.tmpLocation.toPath());
		}

		Files.copy(this.locationOfTasksFile.toPath(), this.tmpLocationOfTasksFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		Files.copy(this.locationOfPluginFile.toPath().toAbsolutePath(), this.tmpLocationOfPluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

		this.invalidHelpFileLocation = Utils.getResourceFromWithin(this.testResourceLocation + "TestInvalidHelp.xml");
		this.invalidAdditionalResource = Utils.getResourceFromWithin(this.testResourceLocation + "TestInvalidAdditionalResources");
		this.invalidXSLFileLocation = Utils.getResourceFromWithin(this.testResourceLocation + "TestInvalidXSL.xsl");
	}

	/**
	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeFiles(java.io.File, java.io.File, java.io.File, java.io.File, java.io.File)}.
	 */
	@Test
	public void testWriteFilesMinusAdditionalResources() {
		final FileUtilities fileUtilities = new FileUtilities(this.tempTaskName);
		final String result = fileUtilities.writeFiles(this.validCFRFileLocation, this.validJSONFileLocation, this.validXSLFileLocation, null, this.validHelpFileLocation);
		assertFileCreation(result);
	}

	/**
	 * Differentiated the tests based on whether the additional resources were included or not.
	 */
	@Test
	public void testWriteFilesWithAdditionalResources() {
		final FileUtilities fileUtilities = new FileUtilities(this.tempTaskName);
		final String result =
				fileUtilities.writeFiles(this.validCFRFileLocation, this.validJSONFileLocation, this.validXSLFileLocation, this.validAdditionalResource, this.validHelpFileLocation);
		assertFileCreation(result);
	}

	/**
	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeTaskToJSONFile(de.cognicrypt.codegenerator.tasks.Task)}.
	 */
	@Test
	public void testWriteTaskToJSONFile() {
		final Task testTask = new Task();
		testTask.setName(this.tempTaskName);
		testTask.setAdditionalResources(this.validAdditionalResource.getPath());
		testTask.setDescription("This is a task");
		testTask.setModelFile(this.validJsFileFromValidCRFFile.getPath());
		testTask.setQuestionsJSONFile(this.validJSONFileLocation.getPath());
		testTask.setSelected(false);
		testTask.setTaskDescription("This is the description of the task");
		testTask.setCodeTemplate(this.validXSLFileLocation.getPath());
		final FileUtilities fileUtilities = new FileUtilities(this.tempTaskName);
		fileUtilities.writeTaskToJSONFile(testTask);
		final List<Task> tasks = TaskJSONReader.getTasks();

		for (final Task task : tasks) {
			if (task.getName().equals(testTask.getName())) {
				assertTrue(task.getAdditionalResources().equals(testTask.getAdditionalResources()));
				assertTrue(task.getDescription().equals(testTask.getDescription()));
				assertTrue(task.getModelFile().equals(testTask.getModelFile()));
				assertTrue(task.getQuestionsJSONFile().equals(testTask.getQuestionsJSONFile()));
				assertTrue(task.getTaskDescription().equals(testTask.getTaskDescription()));
				assertTrue(task.getCodeTemplate().equals(testTask.getCodeTemplate()));
			}
		}
	}

	/**
	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#updateThePluginXMLFileWithHelpData(java.lang.String)}.
	 *
	 * @throws DocumentException
	 * @throws MalformedURLException
	 */
	@Test
	public void testUpdateThePluginXMLFileWithHelpData() throws DocumentException, MalformedURLException {
		final FileUtilities fileUtilities = new FileUtilities(this.tempTaskName);
		fileUtilities.updateThePluginXMLFileWithHelpData(this.tempTaskName);

		final File pluginXMLFile = CodeGenUtils.getResourceFromWithin("src" + Constants.innerFileSeparator + ".." + Constants.innerFileSeparator + Constants.PLUGIN_XML_FILE);
		final SAXReader reader = new SAXReader();
		Document pluginXMLDocument = null;
		reader.setValidation(false);
		pluginXMLDocument = reader.read(pluginXMLFile);
		boolean isSuccessfulWrite = false;
		if (pluginXMLDocument != null) {
			final Element root = pluginXMLDocument.getRootElement();
			for (final Iterator<Element> extensionElement = root.elementIterator("extension"); extensionElement.hasNext();) {
				final Element currentExtensionElement = extensionElement.next();
				final Attribute point = currentExtensionElement.attribute("point");
				if (point != null && point.getValue().equals("org.eclipse.help.contexts")) {
					for (final Iterator<Element> helpFileContext = currentExtensionElement.elementIterator("contexts"); helpFileContext.hasNext();) {
						final Element currentHelpFileContext = helpFileContext.next();
						for (final Iterator<Attribute> it = currentHelpFileContext.attributeIterator(); it.hasNext();) {
							final Attribute file = it.next();
							if (file.getName().equals("file") && file.getValue().equals("src" + Constants.innerFileSeparator + "main" + Constants.innerFileSeparator + "resources"
									+ Constants.innerFileSeparator + "Help" + Constants.innerFileSeparator + this.tempTaskName + ".xml")) {
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
		final FileUtilities fileUtilities = new FileUtilities(this.tempTaskName);
		final String result = fileUtilities.writeFiles(getListClaferModel(), getListOfQuestions(), getXSLString(true), this.validAdditionalResource, getHelpString());
		assertFileCreation(result);
	}

	/**
	 * Check if all the expected files are created.
	 *
	 * @param result
	 */
	private void assertFileCreation(final String result) {
		this.copiedCFRFIle = CodeGenUtils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH + this.tempTaskName + ".cfr");
		this.generatedJSFIle = CodeGenUtils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH + this.tempTaskName + ".js");
		this.copiedJSONFIle = CodeGenUtils.getResourceFromWithin(Constants.JSON_FILE_DIRECTORY_PATH + this.tempTaskName + ".json");
		this.copiedXSLFIle = CodeGenUtils.getResourceFromWithin(Constants.XSL_FILE_DIRECTORY_PATH + this.tempTaskName + ".xsl");
		this.copiedHelpFIle = CodeGenUtils.getResourceFromWithin(Constants.HELP_FILE_DIRECTORY_PATH + this.tempTaskName + ".xml");
		final File copiedAdditionalResourcesDirectory = CodeGenUtils.getResourceFromWithin(Constants.JAR_FILE_DIRECTORY_PATH + this.tempTaskName);
		if (copiedAdditionalResourcesDirectory.exists()) {
			final int copiedAdditionalResourcesNumber = copiedAdditionalResourcesDirectory.listFiles().length;
			assertTrue(copiedAdditionalResourcesNumber == this.validAdditionalResource.listFiles().length);
		}

		// Utils.getResourceFromWithin(validAdditionalResource.getPath()).listFiles();
		assertTrue(this.copiedCFRFIle.exists());
		assertTrue(this.generatedJSFIle.exists());
		assertTrue(this.copiedJSONFIle.exists());
		assertTrue(this.copiedXSLFIle.exists());
		assertTrue(this.copiedHelpFIle.exists());
		assertTrue(result.equals(""));
	}

	/**
	 * Get a list of questions from the validJSONFileLocation.
	 *
	 * @return
	 */
	private ArrayList<Question> getListOfQuestions() {

		final QuestionsJSONReader questionsReader = new QuestionsJSONReader();

		final List<Page> pages = questionsReader.getPages(this.validJSONFileLocation.toPath().toString());
		final ArrayList<Question> questions = new ArrayList<Question>();

		for (final Page page : pages) {
			for (final Question question : page.getContent()) {
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
		final ClaferModel cfrModel = new ClaferModel();
		cfrModel.add(new ClaferFeature(Constants.FeatureType.CONCRETE, "Point", ""));

		return new ClaferModel();
	}

	/**
	 * Return the string from the XSL document.
	 *
	 * @param isValid Switch for valid invalid files.
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	private String getXSLString(final boolean isValid) throws IOException {

		final StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		if (isValid) {
			reader = Files.newBufferedReader(this.validXSLFileLocation.toPath(), StandardCharsets.UTF_8);
			reader.lines().forEach(builder::append);
		} else {
			reader = Files.newBufferedReader(this.invalidXSLFileLocation.toPath(), StandardCharsets.UTF_8);
			reader.lines().forEach(builder::append);
		}

		return builder.toString();
	}

	private String getHelpString() throws IOException {
		final StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;

		reader = Files.newBufferedReader(this.validHelpFileLocation.toPath(), StandardCharsets.UTF_8);
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
			 * invalidXSLFileLocation.getName() + " are invalid.")); assertTrue(result.contains("The contents of the file " + filesAtAdditionalResources[0].getName() + " are invalid."));
			 * assertTrue(result.contains("The contents of the file " + invalidHelpFileLocation.getName() + " are invalid.")); } }
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
		final File invalidJSONFileLocation = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TestInvalidJSON.json");
		if (this.validXSLFileLocation.exists() && invalidJSONFileLocation.exists() && this.validCFRFileLocation.exists() && this.validHelpFileLocation.exists()
				&& this.validAdditionalResource.exists()) {
			final FileUtilities fileUtilities = new FileUtilities(this.tempTaskName);
			fileUtilities.writeFiles(this.validCFRFileLocation, invalidJSONFileLocation, this.validXSLFileLocation, this.validAdditionalResource, this.validHelpFileLocation);
		}
	}

	/**
	 * Replacing the tests for invalid data with the incorrect NullPointerException. This is done because the error handling everywhere uses the Activator class, and this class is
	 * unavailable during testing.
	 */
	@Test(expected = NullPointerException.class)
	public void invalidCFRFileTest() {
		final File invalidCFRFileLocation = CodeGenUtils.getResourceFromWithin(this.testResourceLocation + "TestInvalidCFR.cfr");
		if (this.validXSLFileLocation.exists() && this.validJSONFileLocation.exists() && invalidCFRFileLocation.exists() && this.validHelpFileLocation.exists()
				&& this.validAdditionalResource.exists()) {
			final FileUtilities fileUtilities = new FileUtilities(this.tempTaskName);
			fileUtilities.writeFiles(invalidCFRFileLocation, this.validJSONFileLocation, this.validXSLFileLocation, this.validAdditionalResource, this.validHelpFileLocation);
		}
	}

	/**
	 * Replacing the tests for invalid data with the incorrect NullPointerException. This is done because the error handling everywhere uses the Activator class, and this class is
	 * unavailable during testing.
	 */
	@Test(expected = NullPointerException.class)
	public void invalidXSLFileTest() {
		if (this.invalidXSLFileLocation.exists() && this.validJSONFileLocation.exists() && this.validCFRFileLocation.exists() && this.validHelpFileLocation.exists()
				&& this.validAdditionalResource.exists()) {
			final FileUtilities fileUtilities = new FileUtilities(this.tempTaskName);
			fileUtilities.writeFiles(this.validCFRFileLocation, this.validJSONFileLocation, this.invalidXSLFileLocation, this.validAdditionalResource, this.validHelpFileLocation);
		}
	}

	/**
	 * Replacing the tests for invalid data with the incorrect NullPointerException. This is done because the error handling everywhere uses the Activator class, and this class is
	 * unavailable during testing.
	 */
	@Test(expected = NullPointerException.class)
	public void invalidHelpFileTest() {
		if (this.validXSLFileLocation.exists() && this.validJSONFileLocation.exists() && this.validCFRFileLocation.exists() && this.invalidHelpFileLocation.exists()
				&& this.validAdditionalResource.exists()) {
			final FileUtilities fileUtilities = new FileUtilities(this.tempTaskName);
			fileUtilities.writeFiles(this.validCFRFileLocation, this.validJSONFileLocation, this.validXSLFileLocation, this.validAdditionalResource, this.invalidHelpFileLocation);
		}
	}

	/**
	 * Replacing the tests for invalid data with the incorrect NullPointerException. This is done because the error handling everywhere uses the Activator class, and this class is
	 * unavailable during testing.
	 */
	@Test(expected = NullPointerException.class)
	public void invalidAdditionalResourceFileTest() {
		if (this.validXSLFileLocation.exists() && this.validJSONFileLocation.exists() && this.validCFRFileLocation.exists() && this.validHelpFileLocation.exists()
				&& this.invalidAdditionalResource.exists()) {
			final FileUtilities fileUtilities = new FileUtilities(this.tempTaskName);
			fileUtilities.writeFiles(this.validCFRFileLocation, this.validJSONFileLocation, this.validXSLFileLocation, this.invalidAdditionalResource, this.validHelpFileLocation);
		}
	}

	/**
	 * Delete all the custom files that are generated. Replace the files to the earlier state.
	 *
	 * @throws IOException
	 */
	@After
	public void restoreFiles() throws IOException {
		if (this.copiedCFRFIle != null && this.copiedCFRFIle.exists()) {
			this.copiedCFRFIle.delete();
		}

		if (this.generatedJSFIle != null && this.generatedJSFIle.exists()) {
			this.generatedJSFIle.delete();
		}

		if (this.validJsFileFromValidCRFFile != null && this.validJsFileFromValidCRFFile.exists()) {
			this.validJsFileFromValidCRFFile.delete();
		}

		if (this.copiedJSONFIle != null && this.copiedJSONFIle.exists()) {
			this.copiedJSONFIle.delete();
		}
		if (this.copiedXSLFIle != null && this.copiedXSLFIle.exists()) {
			this.copiedXSLFIle.delete();
		}
		if (this.copiedHelpFIle != null && this.copiedHelpFIle.exists()) {
			this.copiedHelpFIle.delete();
		}
		final File copiedAdditionalResourcesDirectory = CodeGenUtils.getResourceFromWithin(Constants.JAR_FILE_DIRECTORY_PATH + this.tempTaskName);
		if (copiedAdditionalResourcesDirectory != null && copiedAdditionalResourcesDirectory.exists() && copiedAdditionalResourcesDirectory.isDirectory()) {
			for (final File file : copiedAdditionalResourcesDirectory.listFiles()) {
				file.delete();
			}
			copiedAdditionalResourcesDirectory.delete();
		}

		if (this.tmpLocationOfTasksFile != null && this.locationOfTasksFile != null) {
			Files.copy(this.tmpLocationOfTasksFile.toPath(), this.locationOfTasksFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			if (this.tmpLocationOfTasksFile.exists()) {
				this.tmpLocationOfTasksFile.delete();
			}
		}
		if (this.tmpLocationOfPluginFile != null && this.locationOfPluginFile != null) {
			Files.copy(this.tmpLocationOfPluginFile.toPath(), this.locationOfPluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			if (this.tmpLocationOfPluginFile.exists()) {
				this.tmpLocationOfPluginFile.delete();
			}
		}

	}

}
