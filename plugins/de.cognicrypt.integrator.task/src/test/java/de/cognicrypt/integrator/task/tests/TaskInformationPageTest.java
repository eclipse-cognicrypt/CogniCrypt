package de.cognicrypt.integrator.task.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class TaskInformationPageTest {

	IntegratorModel im;
	
	String pathPrefix = "src/test/Templates/";
	
	@Before
	public void setupTest() {
		IntegratorModel.resetInstance();
		im = IntegratorModel.getInstance();
		im.setDebug(true);
	}
	
	@Test
	public void testAddRemoveTemplate() {			
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
		} catch (Exception e) {
			fail("Template TestInt.java should be added without errors but got " + e.getMessage());
		}
		
		assertTrue("Template has not been added to CryslTemplateFiles HashMap", im.getCryslTemplateFiles().containsKey("Int"));
		assertTrue("Template was added with incorrect file path", im.getCryslTemplateFiles().get("Int").getAbsolutePath().contains(pathPrefix + "TestInt/Test.java"));
		
		try {
			im.addTemplate(pathPrefix + "TestString/Test.java");
		} catch (Exception e) {
			fail("Template TestInt.java should be added without errors but got " + e.getMessage());
		}
		
		assertTrue("Template has not been added to CryslTemplateFiles HashMap", im.getCryslTemplateFiles().containsKey("String"));
		assertTrue("Template was added with incorrect file path", im.getCryslTemplateFiles().get("String").getAbsolutePath().contains(pathPrefix + "TestString/Test.java"));
		
		try {
			im.removeTemplate("Int");
		} catch (Exception e) {
			fail("Template is not used in a question and should therefor be removed");
		}
		assertTrue("Template has not been removed from CryslTemplateFiles HashMap", im.getCryslTemplateFiles().containsKey("String"));
		assertFalse("Template has not been removed from CryslTemplateFiles HashMap", im.getCryslTemplateFiles().containsKey("Int"));
		
		try {
			im.removeTemplate("String");
		} catch (Exception e) {
			fail("Template is not used in a question and should therefor be removed");
		}
		assertFalse("Template has not been removed from CryslTemplateFiles HashMap", im.getCryslTemplateFiles().containsKey("String"));
	}
	
	@Test
	public void testAddInvalidFilePath() {	
		try {
			im.addTemplate(pathPrefix + "ThisFileDoesNotExist.java");
			fail("Template with invalid file path should not be added");
		} catch (Exception e) {
			assertEquals("Exception message should say that the specified file could not be found", Constants.ERROR_FILE_NOT_FOUND, e.getMessage());
		}
	}
	
	@Test
	public void testAddInvalidTemplate() {	
		try {
			im.addTemplate(pathPrefix + "NoPackage.java");
			fail("Template without package should not be added");
		} catch (Exception e) {
			assertEquals("Exception message should say that the specified template contains no package", Constants.ERROR_NO_PACKAGE, e.getMessage());
		}
	}
	
	@Test
	public void testAddDifferentTaskTemplate() {	
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
		} catch (Exception e) {
			fail("Template should be added without errors");
		}
		
		try {
			im.addTemplate(pathPrefix + "DifferentTask.java");
			fail("Template with different task name should not be added");
		} catch (Exception e) {
			assertEquals("Exception message should say that the template's task name does not match the existing one's", Constants.ERROR_DIFFERENT_TASK_NAME, e.getMessage());
		}
	}
	
	@Test
	public void testTaskName() {
		assertNull("Task name is expected to be null when no templates are added", im.getTaskName());
		
		try {
			assertFalse("Task name should not be modified if file dialog was cancelled by user", im.addTemplate(null));
		} catch (Exception e1) {
			fail("User cancelling file dialog should not cause an exception to be thrown");
		}
		
		try {
			assertTrue("Task name should be modified when first template is added", im.addTemplate(pathPrefix + "TestInt/Test.java"));
			assertEquals("Task name should be set to the task name from the package after a template was added", "Test", im.getTaskName());
		} catch (Exception e) {
			fail("Template should be added without errors");
		}
		
		try {
			assertFalse("Task name should not be modified when further templates are added", im.addTemplate(pathPrefix + "TestString/Test.java"));
			assertEquals("Task name should not be modified when a second template is added", "Test", im.getTaskName());
		} catch (Exception e) {
			fail("Template should be added without errors");
		}
		
		
		try {
			assertFalse("Task name should not be set to null when more templates exist", im.removeTemplate("String"));
			assertEquals("Task name should not be modified when a template is removed which is not the last template", "Test", im.getTaskName());
		} catch (Exception e) {
			fail("Template is not used in a question and should therefor be removed");
		}
		
		try {
			assertTrue("Task name should be set to null when the last template has been removed", im.removeTemplate("Int"));
			assertNull("Task name is expected to be null when the last template has been removed", im.getTaskName());
		} catch (Exception e) {
			fail("Template is not used in a question and should therefor be removed");
		}
	}

	@Test
	public void testDecoratorErrors() {
		try {
			im.checkTemplatesDec();
			fail("Check decorators should throw an exception if no templates were added");
		} catch (Exception e) {
			assertEquals("Exception message should say that the template list is empty", Constants.ERROR_BLANK_TEMPLATE_LIST, e.getMessage());
		}
		
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
		} catch (Exception e) {
			fail("Template should be added without errors");
		}
		
		try {
			im.checkTemplatesDec();
			fail("Check decorators should throw an exception if single template's identifier is not empty");
		} catch (Exception e) {
			assertEquals("Exception message should say that the template's package name does not match the task name", Constants.ERROR_SINGLE_TEMPLATE_ID, e.getMessage());
		}
	}
	
	@Test
	public void testDecoratorWithTwoTemplates() {
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
			im.addTemplate(pathPrefix + "TestString/Test.java");
		} catch (Exception e) {
			fail("Templates should be added without errors");
		}
		
		try {
			im.checkTemplatesDec();
		} catch (Exception e) {
			fail("Check decorators should not throw an exception if more than one template has been added");
		}
	}
	
	@Test
	public void testDecoratorWithSingleTemplate() {
		try {
			im.addTemplate(pathPrefix + "Test/Test.java");
		} catch (Exception e) {
			fail("Template should be added without errors");
		}
		
		try {
			im.checkTemplatesDec();
		} catch (Exception e) {
			fail("Check decorators should not throw an exception if the single template's package name matches the task name");
		}
	}
}
