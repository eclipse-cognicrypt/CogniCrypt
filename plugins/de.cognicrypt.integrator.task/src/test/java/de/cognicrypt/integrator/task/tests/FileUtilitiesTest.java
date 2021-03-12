package de.cognicrypt.integrator.task.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.exceptions.ErrorMessageException;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class FileUtilitiesTest {

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
	
	@Test
	public void testAddRemoveAnswer() {			
		try {
			im.addQuestion();
		} catch (ErrorMessageException e) {
			fail("Question should be added without errors");
		}
		
		assertEquals("Answers size should be 0 before adding an answer", 0, im.getQuestion(0).getAnswers().size());
		im.addAnswer(0);
		assertEquals("Answers size should be 1 after adding one answer", 1, im.getQuestion(0).getAnswers().size());
		assertTrue("First answer should be the default answer", im.getAnswer(0, 0).isDefaultAnswer());
		im.addAnswer(0);
		assertEquals("Answers size should be 2 after adding the second answer", 2, im.getQuestion(0).getAnswers().size());
		
		im.getAnswer(0, 0).setValue("First Answer");
		im.getAnswer(0, 1).setValue("Second Answer");
		
		im.removeAnswer(0, 0);
		assertEquals("Answers size should be 1 after removing one answer", 1, im.getQuestion(0).getAnswers().size());
		assertTrue("After removing the default answer, the first should be the default answer", im.getAnswer(0, 0).isDefaultAnswer()
																								&& im.getAnswer(0, 0).getValue().equals("Second Answer"));

		im.removeAnswer(0, 0);
		assertEquals("Answers size should be 0 after removing the last answer", 0, im.getQuestion(0).getAnswers().size());
	}
	
	@Test
	public void testRemoveTemplateUsedInAnswer() {			
		try {
			im.addQuestion();
			im.addAnswer(0);
		} catch (ErrorMessageException e) {
			fail("Question and answer should be added without errors");
		}
		
		im.getAnswer(0, 0).setOption("Int");
		
		try {
			im.removeTemplate("Int");
			fail("Template should not be removed as it is used in an answer");
		} catch (ErrorMessageException e) {
			assertEquals(Constants.ERROR_TEMPLATE_IS_USED_IN_ANSWER, e.getMessage());
		}
	}
	
	@Test
	public void testQuestionDecorator() {	
		try {
			im.addQuestion();
		} catch (ErrorMessageException e) {
			fail("Question and answer should be added without errors");
		}
		
		try {
			im.checkQuestionDec(0);
			fail("Question text is empty and should therefor throw an exception");
		} catch (ErrorMessageException e) {
			assertEquals(Constants.ERROR_MESSAGE_BLANK_QUESTION_NAME, e.getMessage());
		}
		
		im.getQuestion(0).setQuestionText("Is this the real life?");
		
		try {
			im.checkQuestionDec(0);
		} catch (ErrorMessageException e) {
			fail("Question text is not empty and should therefor not throw an exception");
		}
	}
	
	@Test
	public void testAnswersDecorator() {	

		try {
			im.addQuestion();
		} catch (ErrorMessageException e) {
			fail("Question should be added without errors");
		}
		
		try {
			im.checkAnswersDec(0);
			fail("Answers is empty and should therefor throw an exception");
		} catch (ErrorMessageException e) {
			assertEquals(Constants.ERROR_BLANK_ANSWERS_LIST, e.getMessage());
		}
		
		im.addAnswer(0);
		im.addAnswer(0);
		
		try {
			im.checkAnswersDec(0);
			fail("Answer text is empty and should therefor throw an exception");
		} catch (ErrorMessageException e) {
			assertEquals(Constants.ERROR_EMPTY_ANSWER_TEXT, e.getMessage());
		}
		
		im.getAnswer(0, 0).setValue("First Answer");
		
		try {
			im.checkAnswersDec(0);
			fail("Answer text is empty and should therefor throw an exception");
		} catch (ErrorMessageException e) {
			assertEquals(Constants.ERROR_EMPTY_ANSWER_TEXT, e.getMessage());
		}
		
		im.getAnswer(0, 1).setValue("Second Answer");
		
		try {
			im.checkAnswersDec(0);
		} catch (ErrorMessageException e) {
			fail("No answer text is empty and should therefor not throw an exception");
		}
	}
}
