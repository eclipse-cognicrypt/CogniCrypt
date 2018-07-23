/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package crossing.e1.featuremodel.clafer.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import de.cognicrypt.codegenerator.question.QuestionsJSONReader;

public class QuestionReaderTest {

	static QuestionsJSONReader qjr;

	@BeforeClass
	public static void setUpBeforeClass() {
		QuestionReaderTest.qjr = new QuestionsJSONReader();
	}

	String testFileFolder = "src/test/resources/testQuestions/";
	String testFile1 = this.testFileFolder + "TestQuestions1.json";
	String testFile2 = this.testFileFolder + "TestQuestions2.json";
	String testFile3 = this.testFileFolder + "TestQuestions3.json";
	String testFile4 = this.testFileFolder + "TestQuestions4.json";
	String testFile5 = this.testFileFolder + "TestQuestions5.json";

	@Test
	/***
	 * This test method should always perform a successful read. Create new methods for more failing test cases.
	 */
	public final void testGetQuestionsCorrect() {
		QuestionReaderTest.qjr.getPages(this.testFile1);
	}

	@Test(expected = JsonSyntaxException.class)
	public final void testGetQuestionsExceptionJSONSyntaxError() {
		QuestionReaderTest.qjr.getPages(this.testFile5);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetQuestionsExceptionMissingID() {
		QuestionReaderTest.qjr.getPages(this.testFile2);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetQuestionsExceptionMissingNextID() {
		QuestionReaderTest.qjr.getPages(this.testFile3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetQuestionsExceptionNoDefaultAnswer() {
		QuestionReaderTest.qjr.getPages(this.testFile4);
	}

}
