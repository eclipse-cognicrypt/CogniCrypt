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
package crossing.e1.configurator.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.Constants;
import crossing.e1.configurator.utilities.Utils;

public class TaskJSONReader {

	private volatile static List<Task> tasks;

	public static List<Task> getTasks() {

		if (tasks == null) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(Utils.getResourceFromWithin(Constants.jsonTaskFile)));
				final Gson gson = new Gson();
				tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {}.getType());

			} catch (final FileNotFoundException e) {
				Activator.getDefault().logError(e);
			}
		}

		return tasks;
	}
	public static List<Task> getPrimitiveTypes() {
		if (tasks == null) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(Utils.getResourceFromWithin(Constants.jsonPrimitiveTypesFile)));
				final Gson gson = new Gson();
				tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {}.getType());

			} catch (final FileNotFoundException e) {
				Activator.getDefault().logError(e);
			}
		}
		return tasks;
	}
	

}