/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.controllers;

import java.util.List;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;

public class Validator {

	public static boolean checkIfTaskNameAlreadyExists(final String taskName) {
		final List<Task> existingTasks = TaskJSONReader.getTasks(); // Required to validate the task name that is chosen by the user.
		boolean taskNameAlreadyExists = false;

		// Validation : check whether the name already exists.
		for (final Task task : existingTasks) {
			if (task.getName().toLowerCase().equals(taskName.toLowerCase()) || task.getDescription().toLowerCase().equals(taskName.toLowerCase())) {
				taskNameAlreadyExists = true;
				break;
			}
		}

		return taskNameAlreadyExists;
	}

	public static String getValidXMLString(final String stringData) {
		return stringData.replace("<", "").replace(">", "").replace("&", "").replace("\'", "").replace("\"", "");
	}

}
