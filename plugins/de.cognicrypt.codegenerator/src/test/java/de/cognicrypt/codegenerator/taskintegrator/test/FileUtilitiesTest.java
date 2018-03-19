/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities;
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

	@Before
	public void setLocations() throws IOException {
		testResourceLocation = "src" + Constants.innerFileSeparator + "test" + Constants.innerFileSeparator + "resources" + Constants.innerFileSeparator + "taskintegrator" + Constants.innerFileSeparator + "FileUtilitiesTest" + Constants.innerFileSeparator;
		validXSLFileLocation = new File(testResourceLocation + "TestValidXSL.xsl");
		validJSONFileLocation = new File(testResourceLocation + "TestValidJSON.json");
		validCFRFileLocation = new File(testResourceLocation + "TestValidCFR.cfr");
		validHelpFileLocation = new File(testResourceLocation + "TestValidHelp.xml");
		validAdditionalResource = new File(testResourceLocation + "TestValidAdditionalResources");
		tmpLocation = new File(testResourceLocation + "tmpLocation");
		File locationOfTasksFile = Utils.getResourceFromWithin(Constants.jsonTaskFile);
		File tmpLocationOfTasksFile = new File(tmpLocation.toPath().toString() + Constants.innerFileSeparator + "tasks.json");
		File locationOfPluginFile = Utils.getResourceFromWithin(Constants.PLUGIN_XML_FILE);
		File tmpLocationOfPluginFile = new File(tmpLocation.toPath().toString() + Constants.innerFileSeparator + "plugin.xml");
		//Files.copy(locationOfTasksFile.toPath(), tmpLocationOfTasksFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		//Files.copy(locationOfPluginFile.toPath(), tmpLocationOfPluginFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
	}


	/**
	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeFiles(java.io.File, java.io.File, java.io.File, java.io.File, java.io.File)}.
	 */
	@Test
	public void testWriteFilesMinusAdditionalResources() {
		if (validXSLFileLocation.exists() && validJSONFileLocation.exists() && validCFRFileLocation.exists() && validHelpFileLocation.exists()) {
			FileUtilities fileUtilities = new FileUtilities("Test");
			fileUtilities.writeFiles(validCFRFileLocation, validJSONFileLocation, validXSLFileLocation, null, validHelpFileLocation);
			copiedCFRFIle = new File(Constants.CFR_FILE_DIRECTORY_PATH + "Test.cfr");
			generatedJSFIle = new File(Constants.CFR_FILE_DIRECTORY_PATH + "Test.js");
			copiedJSONFIle = new File(Constants.JSON_FILE_DIRECTORY_PATH + "Test.json");
			copiedXSLFIle = new File(Constants.XSL_FILE_DIRECTORY_PATH + "Test.xsl");
			copiedHelpFIle = new File(Constants.HELP_FILE_DIRECTORY_PATH + "Test.xml");
			assertTrue(copiedCFRFIle.exists());
			assertTrue(generatedJSFIle.exists());
			assertTrue(copiedJSONFIle.exists());
			assertTrue(copiedXSLFIle.exists());
			assertTrue(copiedHelpFIle.exists());
		}
	}

	//	@Test
//	public void testWriteFilesWithAdditionalResources() {
//		if (validXSLFileLocation.exists() && validJSONFileLocation.exists() && validCFRFileLocation.exists() && validHelpFileLocation.exists()) {
//			FileUtilities fileUtilities = new FileUtilities("Test");
//			fileUtilities.writeFiles(validCFRFileLocation, validJSONFileLocation, validXSLFileLocation, validAdditionalResource, validHelpFileLocation);
//		}
	//	}
	//
	//	/**
	//	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeTaskToJSONFile(de.cognicrypt.codegenerator.tasks.Task)}.
	//	 */
	//	@Test
	//	public void testWriteTaskToJSONFile() {
	//		fail("Not yet implemented");
	//	}
	//
	//	/**
	//	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#updateThePluginXMLFileWithHelpData(java.lang.String)}.
	//	 */
	//	@Test
	//	public void testUpdateThePluginXMLFileWithHelpData() {
	//		fail("Not yet implemented");
	//	}
	//
	//	/**
	//	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#getErrors()}.
	//	 */
	//	@Test
	//	public void testGetErrors() {
	//		fail("Not yet implemented");
	//	}
	//
	//	/**
	//	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#setErrors(java.lang.StringBuilder)}.
	//	 */
	//	@Test
	//	public void testSetErrors() {
	//		fail("Not yet implemented");
	//	}
	//
	//	/**
	//	 * Test method for {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#FileUtilities(java.lang.String)}.
	//	 */
	//	@Test
	//	public void testFileUtilities() {
	//		fail("Not yet implemented");
	//	}
	//
	//	/**
	//	 * Test method for
	//	 * {@link de.cognicrypt.codegenerator.taskintegrator.controllers.FileUtilities#writeFiles(de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel, java.util.ArrayList, java.lang.String, java.io.File)}.
	//	 */
	//	@Test
	//	public void testWriteFilesClaferModelArrayListOfQuestionStringFile() {
	//		fail("Not yet implemented");
	//	}

	@After
	public void restoreFiles() {
		if (copiedCFRFIle.exists()) {
			copiedCFRFIle.delete();
		}
		if (generatedJSFIle.exists()) {
			generatedJSFIle.delete();
		}
		if (copiedJSONFIle.exists()) {
			copiedJSONFIle.delete();
		}
		if (copiedXSLFIle.exists()) {
			copiedXSLFIle.delete();
		}
		if (copiedHelpFIle.exists()) {
			copiedHelpFIle.delete();
		}

	}

}
