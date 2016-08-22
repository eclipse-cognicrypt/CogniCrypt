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

public class QuestionsJSONReader {

	public static void main(final String args[]) {
		final QuestionsJSONReader reader = new QuestionsJSONReader();
		System.out.println(reader.getQuestions("src/main/resources/TaskDesc/LongTermArchivingQuestions.json"));
	}

	private List<Question> questions;

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

	public List<Question> getQuestions(final Task task) {

		if (this.questions == null) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(Utils.getAbsolutePath(task.getXmlFile())));
				final Gson gson = new Gson();
				this.questions = gson.fromJson(reader, new TypeToken<List<Question>>() {}.getType());

			} catch (final FileNotFoundException e) {
				Activator.getDefault().logError(e);
			}
		}

		return this.questions;

	}
}
