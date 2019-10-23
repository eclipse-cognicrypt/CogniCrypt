/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.integrator.task.controllers.SegregatesQuestionsIntoPages;

public class QuestionJSONFileTests {

	static QuestionsJSONReader qjr;
	static QuestionsJSONReaderTI qjrTI;

	@BeforeClass
	public static void setUpBeforeClass() {
		QuestionJSONFileTests.qjr = new QuestionsJSONReader();
		QuestionJSONFileTests.qjrTI = new QuestionsJSONReaderTI();
	}

	private final String testFileFolder = "src/test/resources/taskintegrator/testQuestionsTI/";
	private final String testQuestions3in1 = this.testFileFolder + "testQuestions3in1.json";
	private final String testQuestions6in5 = this.testFileFolder + "testQuestions6in5.json";
	private final String testQuestions6in2 = this.testFileFolder + "testQuestions6in2.json";
	private final String testQuestions7in4 = this.testFileFolder + "testQuestions7in4.json";
	private final String testQuestions10in10 = this.testFileFolder + "testQuestions10in10.json";
	private final String testQuestions8in4 = this.testFileFolder + "testQuestions8in4.json";
	private final String testQuestions6in3 = this.testFileFolder + "testQuestions6in3.json";

	private ArrayList<Question> originalQuestionList;
	private int expectedNumberOfPages;

	@Test
	/***
	 * This method should always performs a successful read and tests whether all questions are included in one page. This method should always assert success
	 */
	public void AllQuestionsInOnePage() {
		this.originalQuestionList = new ArrayList<>();
		this.expectedNumberOfPages = 1;
		this.originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(this.testQuestions3in1);
		// originalQuestionList = readQuestionsFromFile(testQuestions3in1);
		final SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(this.originalQuestionList);
		// checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(this.expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/***
	 * This method should always performs a successful read and tests whether 6 questions are segregated into 5 pages. This method should always assert success
	 */
	public void OneOfThePagesContainsMultipleQuestion() {
		this.originalQuestionList = new ArrayList<>();
		this.expectedNumberOfPages = 5;
		this.originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(this.testQuestions6in5);
		// originalQuestionList = readQuestionsFromFile(testQuestions3in1);
		final SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(this.originalQuestionList);
		// checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(this.expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 6 questions are segregated into 2 pages. This method should always assert success
	 */
	public void EachPageContainsThreeQuestions() {
		this.originalQuestionList = new ArrayList<>();
		this.expectedNumberOfPages = 2;
		this.originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(this.testQuestions6in2);
		final SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(this.originalQuestionList);
		// checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(this.expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 6 questions are segregated into 3 pages. This method should always assert success
	 */
	public void SixQuestionsIntoThreePages() {
		this.originalQuestionList = new ArrayList<>();
		this.expectedNumberOfPages = 3;
		this.originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(this.testQuestions6in3);
		final SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(this.originalQuestionList);
		// checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(this.expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 7 questions are segregated into 4 pages. This method should always assert success
	 */
	public void OneQuestionHasBranch() {
		this.originalQuestionList = new ArrayList<>();
		this.expectedNumberOfPages = 4;
		this.originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(this.testQuestions7in4);
		final SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(this.originalQuestionList);
		// checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(this.expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 8 questions are segregated into 4 pages. This method should always assert success
	 */
	public void EightQuestionsIntoFourPages() {
		this.originalQuestionList = new ArrayList<>();
		this.expectedNumberOfPages = 4;
		this.originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(this.testQuestions8in4);
		final SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(this.originalQuestionList);
		// checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(this.expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 10 questions are segregated into 10 pages. This method should always assert success
	 */
	public void FourQuestionsHasBranch() {
		this.originalQuestionList = new ArrayList<>();
		this.expectedNumberOfPages = 10;
		this.originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(this.testQuestions10in10);
		final SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(this.originalQuestionList);
		// checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(this.expectedNumberOfPages, questionToPages.getPages().size());
	}

}
