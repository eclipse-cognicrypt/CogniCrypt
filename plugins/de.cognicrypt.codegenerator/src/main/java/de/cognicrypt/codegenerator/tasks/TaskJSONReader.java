/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;

public class TaskJSONReader {

	private static volatile List<Task> tasks;

	/**
	 * Getter method for tasks.
	 * 
	 * @return List of Tasks in JSON file
	 */
	public static List<Task> getTasks() {

		if (TaskJSONReader.tasks == null) {
			try {
				final BufferedReader reader = new BufferedReader(new FileReader(CodeGenUtils.getResourceFromWithin(Constants.jsonTaskFile)));
				final Gson gson = new Gson();
				TaskJSONReader.tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {}.getType());
				reader.close();

			} catch (final FileNotFoundException e) {
				Activator.getDefault().logError(e);
			} catch (IOException e) {
				Activator.getDefault().logError(e);
			}
		}

		return TaskJSONReader.tasks;
	}

}
