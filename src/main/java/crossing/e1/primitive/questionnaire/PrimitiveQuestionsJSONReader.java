package crossing.e1.primitive.questionnaire;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.beginer.question.Answer;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.utilities.Utils;

public class PrimitiveQuestionsJSONReader {

	public List<QuestionsList> getQuestions(final String filePath) {
		List<QuestionsList> questions = new ArrayList<QuestionsList>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(Utils.getResourceFromWithin(filePath)));
			final Gson gson = new Gson();

			questions = gson.fromJson(reader, new TypeToken<List<QuestionsList>>() {}.getType());

			checkReadQuestions(questions);
		} catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		return questions;
	}

	/***
	 * This method reads all questions of one task.
	 * 
	 * @param task
	 *        task whose questions should be read
	 * @return Questions
	 */
	public List<QuestionsList> getQuestions(final Task task) {
		return getQuestions(task.getXmlFile());
	}

	private void checkReadQuestions(List<QuestionsList> questionsList) {
		final Set<Integer> ids = new HashSet<>();
		if (questionsList.size() < 1) {
			throw new IllegalArgumentException("There are no questions for this task.");
		}
		for (final QuestionsList questionList : questionsList){
		for (final Questions question: questionList.getQuestions()) {
			if (!ids.add(question.getId())) {
				throw new IllegalArgumentException("Each question must have a unique ID.");
			}

			if (question.getDefaultAnswer() == null) {
				throw new IllegalArgumentException("Each question must have a default answer.");
			}

			for (final Answer answer : question.getAnswers()) {
				if (answer.getNextID() == -2) {
					throw new IllegalArgumentException("Each answer must point to the following question.");
				}
			}
		}
	} }
}
