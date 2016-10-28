package crossing.e1.featuremodel.clafer.test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import crossing.e1.configurator.beginer.question.QuestionsJSONReader;

public class QuestionReaderTest {

	static QuestionsJSONReader qjr;
	String testFile1 = "src/main/resources/testFiles/TestQuestions1.json";
	String testFile2 = "src/main/resources/testFiles/TestQuestions2.json";
	String testFile3 = "src/main/resources/testFiles/TestQuestions3.json";
	String testFile4 = "src/main/resources/testFiles/TestQuestions4.json";
	String testFile5 = "src/main/resources/testFiles/TestQuestions5.json";

	@BeforeClass
	public static void setUpBeforeClass() {
		qjr = new QuestionsJSONReader();
	}

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
