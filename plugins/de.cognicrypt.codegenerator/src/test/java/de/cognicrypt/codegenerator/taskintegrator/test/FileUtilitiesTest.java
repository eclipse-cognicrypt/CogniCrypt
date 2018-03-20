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

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;
import de.cognicrypt.codegenerator.utilities.Utils;

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
		validXSLFileLocation = new File(testResourceLocation + "TestValidXSL.xsl");
		validJSONFileLocation = new File(testResourceLocation + "TestValidJSON.json");
		validCFRFileLocation = new File(testResourceLocation + "TestValidCFR.cfr");
		validJsFileFromValidCRFFile = new File(testResourceLocation + "TestValidCFR.js");
		validHelpFileLocation = new File(testResourceLocation + "TestValidHelp.xml");
		validAdditionalResource = new File(testResourceLocation + "TestValidAdditionalResources");
		tmpLocation = new File(testResourceLocation + "tmpLocation");
		locationOfTasksFile = Utils.getResourceFromWithin(Constants.jsonTaskFile);
		tmpLocationOfTasksFile = new File(tmpLocation.toPath().toString() + Constants.innerFileSeparator + "tasks.json");
		locationOfPluginFile = Utils.getResourceFromWithin("src" + Constants.innerFileSeparator + ".." + Constants.innerFileSeparator + Constants.PLUGIN_XML_FILE);

		tmpLocationOfPluginFile = new File(tmpLocation.getAbsolutePath() + Constants.innerFileSeparator + "plugin.xml");
		Files.copy(locationOfTasksFile.toPath(), tmpLocationOfTasksFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		Files.copy(locationOfPluginFile.toPath().toAbsolutePath(), tmpLocationOfPluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING,
			StandardCopyOption.COPY_ATTRIBUTES);

		invalidHelpFileLocation = new File(testResourceLocation + "TestInvalidHelp.xml");
		invalidAdditionalResource = new File(testResourceLocation + "TestInvalidAdditionalResources");
		invalidXSLFileLocation = new File(testResourceLocation + "TestInvalidXSL.xsl");
	}


	/**
	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeFiles(java.io.File, java.io.File, java.io.File, java.io.File, java.io.File)}.
	 */
	@Test
	public void testWriteFilesMinusAdditionalResources() {
		if (validXSLFileLocation.exists() && validJSONFileLocation.exists() && validCFRFileLocation.exists() && validHelpFileLocation.exists()) {
			FileUtilities fileUtilities = new FileUtilities(tempTaskName);
			String result = fileUtilities.writeFiles(validCFRFileLocation, validJSONFileLocation, validXSLFileLocation, null, validHelpFileLocation);
			assertFileCreation(result);
		}
	}

	@Test
	public void testWriteFilesWithAdditionalResources() {
		if (validXSLFileLocation.exists() && validJSONFileLocation.exists() && validCFRFileLocation.exists() && validHelpFileLocation.exists() && validAdditionalResource
			.exists()) {
			FileUtilities fileUtilities = new FileUtilities(tempTaskName);
			String result = fileUtilities.writeFiles(validCFRFileLocation, validJSONFileLocation, validXSLFileLocation, validAdditionalResource, validHelpFileLocation);
			assertFileCreation(result);
		}
	}

	@Test
	public void testWriteFilesWithAdditionalResourcesInvalidFiles() {
		File invalidJSONFileLocation = new File(testResourceLocation + "TestInvalidJSON.json");
		File invalidCFRFileLocation = new File(testResourceLocation + "TestInvalidCFR.cfr");
		if (invalidXSLFileLocation.exists() && invalidJSONFileLocation.exists() && invalidCFRFileLocation.exists() && invalidHelpFileLocation.exists() && invalidAdditionalResource
			.exists()) {
			FileUtilities fileUtilities = new FileUtilities(tempTaskName);
			String result = fileUtilities.writeFiles(invalidCFRFileLocation, invalidJSONFileLocation, invalidXSLFileLocation, invalidAdditionalResource, invalidHelpFileLocation);
			File[] filesAtAdditionalResources = invalidAdditionalResource.listFiles();
			assertTrue(result.contains(
				"Either the compilation failed, or the provided name for the Task does not match the one in the Clafer model. Please note : the name must be capitalized."));
			assertTrue(result.contains("The contents of the file " + invalidJSONFileLocation.getName() + " are invalid."));
			assertTrue(result.contains("The contents of the file " + invalidXSLFileLocation.getName() + " are invalid."));
			assertTrue(result.contains("The contents of the file " + filesAtAdditionalResources[0].getName() + " are invalid."));
			assertTrue(result.contains("The contents of the file " + invalidHelpFileLocation.getName() + " are invalid."));
		}
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

		File pluginXMLFile = Utils.getResourceFromWithin(Constants.PLUGIN_XML_FILE);
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
		String result = fileUtilities.writeFiles(getListClaferModel(), getListOfQuestions(), getXSLString(true), validAdditionalResource);
		assertFileCreation(result);
	}

	/**
	 * Test method for
	 * {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeFiles(de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel, java.util.ArrayList, java.lang.String, java.io.File)}.
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	@Test
	public void testWriteFilesWithDataInvalidResourceInvalidXSL() throws IOException {
		FileUtilities fileUtilities = new FileUtilities(tempTaskName);
		String result = fileUtilities.writeFiles(getListClaferModel(), getListOfQuestions(), getXSLString(false), invalidAdditionalResource);
		assertTrue(result.contains("The contents of the file " + invalidXSLFileLocation.getName() + " are invalid."));
		File[] filesAtAdditionalResources = invalidAdditionalResource.listFiles();
		assertTrue(result.contains("The contents of the file " + filesAtAdditionalResources[0].getName() + " are invalid."));
	}

	/**
	 * Check if all the expected files are created.
	 * 
	 * @param result
	 */
	private void assertFileCreation(String result) {
		copiedCFRFIle = new File(Constants.CFR_FILE_DIRECTORY_PATH + tempTaskName + ".cfr");
		generatedJSFIle = new File(Constants.CFR_FILE_DIRECTORY_PATH + tempTaskName + ".js");
		copiedJSONFIle = new File(Constants.JSON_FILE_DIRECTORY_PATH + tempTaskName + ".json");
		copiedXSLFIle = new File(Constants.XSL_FILE_DIRECTORY_PATH + tempTaskName + ".xsl");
		copiedHelpFIle = new File(Constants.HELP_FILE_DIRECTORY_PATH + tempTaskName + ".xml");
		File copiedAdditionalResourcesDirectory = Utils.getResourceFromWithin(Constants.JAR_FILE_DIRECTORY_PATH + tempTaskName);
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
		return ClaferModel.createFromBinaries(testResourceLocation + "PointModel.dat");
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

		/*
		 * SAXReader reader = new SAXReader(); reader.setValidation(false); if (isValid) { reader.read(validHelpFileLocation); } else { reader.read(invalidHelpFileLocation); }
		 * return reader.toString();
		 */
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
		File copiedAdditionalResourcesDirectory = Utils.getResourceFromWithin(Constants.JAR_FILE_DIRECTORY_PATH + tempTaskName);
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
