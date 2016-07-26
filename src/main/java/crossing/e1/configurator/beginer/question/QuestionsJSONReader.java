package crossing.e1.configurator.beginer.question;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.utilities.Utilities;

public class QuestionsJSONReader {
	private List<Question> questions;

	public List<Question> getQuestions(Task task) {

		if (questions == null) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(
						Utilities.getAbsolutePath(task.getXmlFile())));
				Gson gson = new Gson();

				questions = gson.fromJson(reader, new TypeToken<List<Question>>() {
				}.getType());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		return questions;

	}
	
	public List<Question> getQuestions(String filePath) {

		if (questions == null) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(Utilities.getAbsolutePath(filePath)));
				Gson gson = new Gson();

				questions = gson.fromJson(reader, new TypeToken<List<Question>>() {
				}.getType());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		return questions;

	}
	
	public static void main(String args[]){
		QuestionsJSONReader reader = new QuestionsJSONReader();
		System.out.println(reader.getQuestions("src/main/resources/TaskDesc/LongTermArchivingQuestions.json"));
	}
}
