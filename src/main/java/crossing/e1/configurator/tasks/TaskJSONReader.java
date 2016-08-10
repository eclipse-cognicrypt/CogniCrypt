package crossing.e1.configurator.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Utilities;

public class TaskJSONReader {

	private static List<Task> tasks;

	public static List<Task> getTasks() {

		if (tasks == null) {
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(
						Utilities.getAbsolutePath(Constants.jsonTaskFile)));
				Gson gson = new Gson();

				tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {
				}.getType());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		return tasks;

	}

}