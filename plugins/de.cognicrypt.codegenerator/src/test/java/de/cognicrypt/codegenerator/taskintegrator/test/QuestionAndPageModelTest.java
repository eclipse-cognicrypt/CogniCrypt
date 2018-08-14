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

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReaderTI;
import de.cognicrypt.core.Constants;

public class QuestionAndPageModelTest {

	public static final String testQuestionFileFolder = "src/test/resources/taskintegrator/testQuestionsTI/testQuestion1.json";
	public static final String testPageFileFolder = "src/test/resources/taskintegrator/testQuestionsTI/testPage1.json";
	private static QuestionsJSONReaderTI qjrTI;
	private static Question question;
	private static Page page;
	private static ArrayList<Page> filePages;
	private static ArrayList<Question> fileQuestions;

	@BeforeClass
	public static void setUpBeforeClass() {
		QuestionAndPageModelTest.qjrTI = new QuestionsJSONReaderTI();
		filePages = new ArrayList<>();
		filePages = QuestionAndPageModelTest.qjrTI.readPageFromFile(testPageFileFolder);
		fileQuestions = new ArrayList<>();
		fileQuestions = QuestionAndPageModelTest.qjrTI.readQuestionsFromFile(testQuestionFileFolder);
		page = new Page();
		question = new Question();
		question.setId(0);
		question.setQuestionText("Q0?");
		question.setElement(Constants.GUIElements.combo);
		ArrayList<Answer> answers = new ArrayList<>();
		Answer ans1 = new Answer();
		Answer ans2 = new Answer();
		ans1.setValue("ans1");
		ans1.setDefaultAnswer(true);
		ans2.setValue("ans2");
		answers.add(ans1);
		answers.add(ans2);
		question.setAnswers(answers);

	}

	/**
	 * This test method compares the question details with file containing questions. This method should always assert success
	 */
	@Test
	public void testQuestionModel() {
		assertEquals(question.getQuestionText(), fileQuestions.get(0).getQuestionText());
		assertEquals(question.getId(), fileQuestions.get(0).getId());
		assertEquals(question.getElement(), fileQuestions.get(0).getElement());
		assertEquals(question.getAnswers().size(), fileQuestions.get(0).getAnswers().size());
		assertEquals(question.getAnswers().get(0).getValue(), fileQuestions.get(0).getAnswers().get(0).getValue());
	}

	/**
	 * This method tests claferDependency functionality. This method should always assert success.
	 */

	@Test
	public void testAnswerClaferDependency() {
		ArrayList<ClaferDependency> claferDependenciesForAns1 = new ArrayList<>();
		ClaferDependency claferDependencyForAns1 = new ClaferDependency();
		claferDependencyForAns1.setAlgorithm("algo1");
		claferDependencyForAns1.setOperand("prop1");
		claferDependencyForAns1.setOperator(">");
		claferDependencyForAns1.setValue("128");
		claferDependenciesForAns1.add(claferDependencyForAns1);

		ArrayList<ClaferDependency> claferDependenciesForAns2 = new ArrayList<>();
		ClaferDependency claferDependencyForAns2 = new ClaferDependency();
		claferDependencyForAns2.setAlgorithm("algo2");
		claferDependencyForAns2.setOperand("prop2");
		claferDependencyForAns2.setOperator(">=");
		claferDependencyForAns2.setValue("256");
		claferDependenciesForAns2.add(claferDependencyForAns2);
		// sets clafer Dependencies for ans1
		question.getAnswers().get(0).setClaferDependencies(claferDependenciesForAns1);
		// sets clafer Dependencies for ans2
		question.getAnswers().get(1).setClaferDependencies(claferDependenciesForAns2);

		assertEquals(question.getAnswers().get(0).getClaferDependencies(), fileQuestions.get(0).getAnswers().get(0).getClaferDependencies());
		assertEquals(question.getAnswers().get(1).getClaferDependencies(), fileQuestions.get(0).getAnswers().get(1).getClaferDependencies());

	}

	/**
	 * This method tests codeDependency functionality. This method should always assert success.
	 */

	@Test
	public void testAnswerCodeDependency() {
		ArrayList<CodeDependency> codeDependenciesForAns1 = new ArrayList<>();
		CodeDependency codeDependencyForAns1 = new CodeDependency();
		codeDependencyForAns1.setOption("variable1");
		codeDependencyForAns1.setValue("value1");
		codeDependenciesForAns1.add(codeDependencyForAns1);

		ArrayList<CodeDependency> codeDependenciesForAns2 = new ArrayList<>();
		CodeDependency codeDependencyForAns2 = new CodeDependency();
		codeDependencyForAns2.setOption("variable2");
		codeDependencyForAns2.setValue("value2");
		codeDependenciesForAns2.add(codeDependencyForAns2);

		// sets code Dependencies for ans1
		question.getAnswers().get(0).setCodeDependencies(codeDependenciesForAns1);
		// sets code Dependencies for ans2
		question.getAnswers().get(1).setCodeDependencies(codeDependenciesForAns2);

		assertEquals(question.getAnswers().get(0).getCodeDependencies(), fileQuestions.get(0).getAnswers().get(0).getCodeDependencies());
		assertEquals(question.getAnswers().get(1).getCodeDependencies(), fileQuestions.get(0).getAnswers().get(1).getCodeDependencies());
	}

	/**
	 * This method tests the Page model. This method should always assert success.
	 */

	@Test
	public void testPageModel() {
		page.setId(0);
		Question qstn = new Question();
		qstn.setId(0);
		qstn.setQuestionText("Q0?");
		qstn.setElement(Constants.GUIElements.combo);
		ArrayList<Answer> answers = new ArrayList<>();
		Answer ans1 = new Answer();
		Answer ans2 = new Answer();
		ans1.setValue("ans1");
		ans1.setDefaultAnswer(true);
		ans2.setValue("ans2");
		answers.add(ans1);
		answers.add(ans2);
		qstn.setAnswers(answers);
		ArrayList<Question> questions = new ArrayList<>();
		questions.add(qstn);
		page.setContent(questions);

		assertEquals(page.getId(), filePages.get(0).getId());
		assertEquals(page.getContent().get(0).getQuestionText(), filePages.get(0).getContent().get(0).getQuestionText());
		assertEquals(page.getContent().get(0).getId(), filePages.get(0).getContent().get(0).getId());
		assertEquals(page.getContent().get(0).getAnswers().size(), filePages.get(0).getContent().get(0).getAnswers().size());
	}

}
