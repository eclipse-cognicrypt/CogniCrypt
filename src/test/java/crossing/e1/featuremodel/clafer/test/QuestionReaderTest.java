/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package crossing.e1.featuremodel.clafer.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import crossing.e1.configurator.beginer.question.QuestionsJSONReader;

public class QuestionReaderTest {

	static QuestionsJSONReader qjr;

	@BeforeClass
	public static void setUpBeforeClass() {
		qjr = new QuestionsJSONReader();
	}

	String testFile1 = "src/main/resources/testFiles/TestQuestions1.json";
	String testFile2 = "src/main/resources/testFiles/TestQuestions2.json";
	String testFile3 = "src/main/resources/testFiles/TestQuestions3.json";
	String testFile4 = "src/main/resources/testFiles/TestQuestions4.json";

	String testFile5 = "src/main/resources/testFiles/TestQuestions5.json";

	@Test
	/***
	 * This test method should always perform a successfull read. Create new methods for more fail test cases.
	 */
	public final void testGetQuestionsCorrect() {
		qjr.getQuestions(this.testFile1);
	}

	@Test(expected = JsonSyntaxException.class)
	public final void testGetQuestionsExceptionJSONSyntaxError() {
		qjr.getQuestions(this.testFile5);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetQuestionsExceptionMissingID() {
		qjr.getQuestions(this.testFile2);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetQuestionsExceptionMissingNextID() {
		qjr.getQuestions(this.testFile3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetQuestionsExceptionNoDefaultAnswer() {
		qjr.getQuestions(this.testFile4);
	}

}
