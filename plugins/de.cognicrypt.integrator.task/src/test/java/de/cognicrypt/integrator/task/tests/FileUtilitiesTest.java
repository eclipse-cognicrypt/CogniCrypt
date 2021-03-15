package de.cognicrypt.integrator.task.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.FileUtilities;
import de.cognicrypt.integrator.task.exceptions.ErrorMessageException;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class FileUtilitiesTest {

	IntegratorModel im;
	FileUtilities fu;
	
	String pathPrefix = "src/test/Templates/";
	
	@Before
	public void setupTest() {
		IntegratorModel.resetInstance();
		im = IntegratorModel.getInstance();
		im.setDebug(true);
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
			fail("Templates, question and answers should be added without errors");
		}

		assertFalse(im.checkForUnusedIdentifiers());
		
		im.getAnswer(0, 0).setOption(null);
		
		assertTrue(im.checkForUnusedIdentifiers());
		
		im.getAnswer(0, 0).setOption("Int");
		
		assertTrue(im.checkForUnusedIdentifiers());
		
		im.getAnswer(0, 0).setOption("String");
		
		assertFalse(im.checkForUnusedIdentifiers());
	}
	
	@Test
	public void testGuidedModeMultiCopy() {
		
	}
	
	@Test
	public void testGuidedModeSingleCopy() {
		
	}
	
	@Test
	public void testNonGuidedModeCopy() {
		
	}
}
