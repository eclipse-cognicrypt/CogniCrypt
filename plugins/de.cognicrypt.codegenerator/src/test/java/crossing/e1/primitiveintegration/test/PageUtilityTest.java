package crossing.e1.primitiveintegration.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.cognicrypt.codegenerator.primitive.wizard.questionnaire.PrimitiveQuestionPageUtility;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.primitive.wizard.questionnaire.*;

public class PageUtilityTest {

	PrimitiveQuestionPageUtility util = new PrimitiveQuestionPageUtility();
	PrimitiveQuestionsJSONReader pqjr = new PrimitiveQuestionsJSONReader();
	String testFileFolder = "src/test/resources/testQuestions/";
	String testFile1 = this.testFileFolder + "TestPrimitiveQuestion.json";

	@Test
	public void testGetIndex() {
		List<Question> questions = pqjr.getPages(this.testFile1).get(0).getContent();

		int index = util.getIndex(questions.get(0).getAnswers(), "OFB");
		assertEquals(index, 1);

	}

}
