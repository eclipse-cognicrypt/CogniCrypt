package de.cognicrypt.integrator.task.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.exceptions.ErrorMessageException;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class ImportExportTest {

	IntegratorModel im;
	
	String pathPrefix = "src/test/Templates/";
	
	@Before
	public void setupTest() {
		IntegratorModel.resetInstance();
		im = IntegratorModel.getInstance();
		im.setDebug(true);
		
		try {
			im.addTemplate(pathPrefix + "TestInt/Test.java");
			im.addTemplate(pathPrefix + "TestString/Test.java");
		} catch (Exception e) {
			fail("Templates should be added without errors");
		}
	}
	
	@Test
	public void testAddRemoveQuestion() {
		assertEquals("Questions size should be 0 before adding questions", 0, im.getQuestions().size());
		
		try {
			im.addQuestion();
			assertEquals("Questions size should be 1 after adding one question", 1, im.getQuestions().size());
			assertEquals("First question's id should be 0", 0, im.getQuestion(0).getId());
		} catch (ErrorMessageException e) {
			fail("Question should be added without errors but got error " + e.getMessage());
		}
		
		try {
			im.removeQuestion(5);
			fail("Removing questions that don't exist should cause an exception");
		}catch(Exception e) {
		}
		
		try {
			im.removeQuestion(0);
			assertEquals("Questions size should be 0 after removing all questions", 0, im.getQuestions().size());
		}catch(Exception e) {
			fail("Removing existing question should not cause an exception but got " + e.getMessage());
		}
		
	}
}
