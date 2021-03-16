package de.cognicrypt.integrator.task.plugintests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.exceptions.ErrorMessageException;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import junit.framework.AssertionFailedError;

public class FileUtilitiesTest {

	IntegratorModel im;
	
	String pathPrefix = "src/test/Templates/";
	String iconLocation = "src/test/CoffeeAPI/coffee.png";
	String jsonLocation = "src/test/MultiCopyReference/res/Tasks/tasks.json";
	
	String guidedModeMultiReference = "src/test/MultiCopyReference";
	String guidedModeSingleReference = "src/test/SingleCopyReference";
	String nonGuidedModeReference = "src/test/NonGuidedCopyReference";
	
	@Before
	public void setupTest() {
		try {
			FileUtils.deleteDirectory(new File(Constants.ECLIPSE_CogniCrypt_RESOURCE_DIR));
		} catch (IOException e1) {
			fail("Directory should be deleted without errors");
		}
		
		IntegratorModel.resetInstance();
		im = IntegratorModel.getInstance();
	}
	
	@Test
	public void testUnusedIdentifier() {
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
			im.addTemplate(pathPrefix + "TestString/Test.java");
			
			im.setGuidedModeChosen(true);
			im.setImportModeChosen(false);
			
			im.addQuestion();
			im.addAnswer(0);
			im.addAnswer(0);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Templates, question and answers should be added without errors");
		}

		assertFalse("Both templates should be used after adding two answers", im.checkForUnusedIdentifiers());
		
		im.getAnswer(0, 0).setOption(null);
		
		assertTrue("String should not be used", im.checkForUnusedIdentifiers());
		
		im.getAnswer(0, 0).setOption("Int");
		
		assertTrue("String should not be used", im.checkForUnusedIdentifiers());
		
		im.getAnswer(0, 0).setOption("String");
		
		assertFalse("Both templates should be used after setting option back to String", im.checkForUnusedIdentifiers());
	}
	
	@Test
	public void testGuidedModeMultiCopy() {
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
			im.addTemplate(pathPrefix + "TestString/Test.java");
			
			im.setGuidedModeChosen(true);
			im.setImportModeChosen(false);
			
			im.setLocationOfIconFile(new File(iconLocation));
			
			im.addQuestion();
			im.addAnswer(0);
			im.addAnswer(0);
			
		} catch (Exception e) {
			fail("Templates, question and answers should be added without errors");
		}
		
		try {
			im.copyTask();
			
			File copyReference = new File(guidedModeMultiReference);
			
			try {
				TestHelpers.checkDirectoriesAreEqual(Paths.get(Constants.ECLIPSE_CogniCrypt_RESOURCE_DIR), Paths.get(copyReference.getAbsolutePath()));
			} catch (IOException e) {
				fail("File operations should not fail");
			} catch (AssertionFailedError e) {
				fail(e.getMessage());
			}
		} catch (ErrorMessageException e) {
			fail("Copy should be successful but got " + e.getMessage());
		}
	}
	
	@Test
	public void testGuidedModeSingleCopy() {
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
			
			im.setGuidedModeChosen(true);
			im.setImportModeChosen(false);
			
			im.setLocationOfIconFile(new File(iconLocation));
		} catch (Exception e) {
			fail("Template should be added without errors");
		}
		
		try {
			im.copyTask();
			
			File copyReference = new File(guidedModeSingleReference);
			
			try {
				TestHelpers.checkDirectoriesAreEqual(Paths.get(Constants.ECLIPSE_CogniCrypt_RESOURCE_DIR), Paths.get(copyReference.getAbsolutePath()));
			} catch (IOException e) {
				fail("File operations should not fail");
			} catch (AssertionFailedError e) {
				fail(e.getMessage());
			}
		} catch (ErrorMessageException e) {
			fail("Copy should be successful but got " + e.getMessage());
		}
	}
	
	@Test
	public void testNonGuidedModeCopy() {
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
			im.addTemplate(pathPrefix + "TestString/Test.java");
			
			im.setGuidedModeChosen(false);
			im.setImportModeChosen(false);
			
			im.setJSONFile(new File(jsonLocation));
			im.setLocationOfIconFile(new File(iconLocation));
		} catch (Exception e) {
			fail("Template should be added without errors");
		}

		try {
			im.copyTask();

			File copyReference = new File(nonGuidedModeReference);

			try {
				TestHelpers.checkDirectoriesAreEqual(Paths.get(Constants.ECLIPSE_CogniCrypt_RESOURCE_DIR), Paths.get(copyReference.getAbsolutePath()));
			} catch (IOException e) {
				fail("File operations should not fail");
			} catch (AssertionFailedError e) {
				fail(e.getMessage());
			}
		} catch (ErrorMessageException e) {
			fail("Copy should be successful but got " + e.getMessage());
		}
	}
}
