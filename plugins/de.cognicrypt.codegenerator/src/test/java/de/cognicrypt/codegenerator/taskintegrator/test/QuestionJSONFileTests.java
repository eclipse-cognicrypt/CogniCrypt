/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.codegenerator.question.QuestionsJSONReaderTI;
import de.cognicrypt.codegenerator.taskintegrator.controllers.SegregatesQuestionsIntoPages;

public class QuestionJSONFileTests {

	static QuestionsJSONReader qjr;
	static QuestionsJSONReaderTI qjrTI;

	@BeforeClass
	public static void setUpBeforeClass() {
		QuestionJSONFileTests.qjr = new QuestionsJSONReader();
		QuestionJSONFileTests.qjrTI = new QuestionsJSONReaderTI();
	}

	private String testFileFolder = "src/test/resources/taskintegrator/testQuestionsTI/";
	private String testQuestions3in1 = testFileFolder + "testQuestions3in1.json";
	private String testQuestions6in5 = testFileFolder + "testQuestions6in5.json";
	private String testQuestions6in2 = testFileFolder + "testQuestions6in2.json";
	private String testQuestions7in4 = testFileFolder + "testQuestions7in4.json";
	private String testQuestions10in10 = testFileFolder + "testQuestions10in10.json";
	private String testQuestions8in4 = testFileFolder + "testQuestions8in4.json";
	private String testQuestions6in3 = testFileFolder + "testQuestions6in3.json";

	private ArrayList<Question> originalQuestionList;
	private int expectedNumberOfPages;

	@Test
	/***
	 * This method should always performs a successful read and tests whether all questions are included in one page. This method should always assert success
	 */
	public void AllQuestionsInOnePage() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 1;
		originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(testQuestions3in1);
		//originalQuestionList = readQuestionsFromFile(testQuestions3in1);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/***
	 * This method should always performs a successful read and tests whether 6 questions are segregated into 5 pages. This method should always assert success
	 */
	public void OneOfThePagesContainsMultipleQuestion() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 5;
		originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(testQuestions6in5);
		//originalQuestionList = readQuestionsFromFile(testQuestions3in1);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 6 questions are segregated into 2 pages. This method should always assert success
	 */
	public void EachPageContainsThreeQuestions() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 2;
		originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(testQuestions6in2);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 6 questions are segregated into 3 pages. This method should always assert success
	 */
	public void SixQuestionsIntoThreePages() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 3;
		originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(testQuestions6in3);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 7 questions are segregated into 4 pages. This method should always assert success
	 */
	public void OneQuestionHasBranch() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 4;
		originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(testQuestions7in4);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 8 questions are segregated into 4 pages. This method should always assert success
	 */
	public void EightQuestionsIntoFourPages() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 4;
		originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(testQuestions8in4);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

	@Test
	/**
	 * This method should always performs a successful read and tests whether 10 questions are segregated into 10 pages. This method should always assert success
	 */
	public void FourQuestionsHasBranch() {
		originalQuestionList = new ArrayList<>();
		expectedNumberOfPages = 10;
		originalQuestionList = QuestionJSONFileTests.qjrTI.readQuestionsFromFile(testQuestions10in10);
		SegregatesQuestionsIntoPages questionToPages = new SegregatesQuestionsIntoPages(originalQuestionList);
		//checks whether the page array format is correct or not
		QuestionJSONFileTests.qjr.checkReadPages(questionToPages.getPages());
		assertEquals(expectedNumberOfPages, questionToPages.getPages().size());
	}

}
