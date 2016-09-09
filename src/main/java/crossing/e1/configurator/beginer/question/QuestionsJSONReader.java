package crossing.e1.configurator.beginer.question;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.utilities.Utils;

/**
 *  This class reads all questions and answers of one task.
 * @author Sarah Nadi
 *
 */
public class QuestionsJSONReader {

//	Test method so that OpenCCE does not have to be started.
//	public static void main(final String args[]) {
//		final QuestionsJSONReader reader = new QuestionsJSONReader();
//		System.out.println(reader.getQuestions("src/main/resources/TaskDesc/LongTermArchivingQuestions.json"));
//	}

	private List<Question> questions;

	/***
	 * This method reads all questions of one task using the file path to the question file.
	 * @param filePath path to the file that contains all questions for one task.
	 * @return questions
	 */
	public List<Question> getQuestions(final String filePath) {
		if (this.questions == null) {
			BufferedReader reader;
			try {

				reader = new BufferedReader(new FileReader(Utils.getAbsolutePath(filePath)));
				final Gson gson = new Gson();

				this.questions = gson.fromJson(reader, new TypeToken<List<Question>>() {}.getType());
			} catch (final FileNotFoundException e) {
				Activator.getDefault().logError(e);
			}
		}

		return this.questions;

	}

	/***
	 * This method reads all questions of one task.
	 * @param task task whose questions should be read
	 * @return Questions
	 */
	public List<Question> getQuestions(final Task task) {
		return getQuestions(task.getXmlFile());
	}
}
