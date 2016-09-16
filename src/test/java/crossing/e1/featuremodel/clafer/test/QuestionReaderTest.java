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
	public static void setUpBeforeClass()  {
		qjr = new QuestionsJSONReader();
	}
	
	@Test
	/***
	 * This test method should always perform a successfull read. Create new methods for more fail test cases. 
	 */
	public final void testGetQuestionsCorrect() {
		qjr.getQuestions(testFile1);
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testGetQuestionsExceptionMissingID() {
		qjr.getQuestions(testFile2);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public final void testGetQuestionsExceptionMissingNextID() {
		qjr.getQuestions(testFile3);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public final void testGetQuestionsExceptionNoDefaultAnswer() {
		qjr.getQuestions(testFile4);
	}
	
	@Test (expected = JsonSyntaxException.class)
	public final void testGetQuestionsExceptionJSONSyntaxError() {
		qjr.getQuestions(testFile5);
	}
	
}
