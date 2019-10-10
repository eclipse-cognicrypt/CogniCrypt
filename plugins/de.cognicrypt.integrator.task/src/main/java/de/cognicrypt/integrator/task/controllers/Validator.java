/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.controllers;

import java.util.List;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;

public class Validator {

	public static boolean checkIfTaskNameAlreadyExists(final String taskName) {
		final List<Task> existingTasks = TaskJSONReader.getTasks(); // Required to validate the task name that is chosen by the user.
		boolean validString = true;

		// Validation : check whether the name already exists.
		for (final Task task : existingTasks) {
			if (task.getName().toLowerCase().equals(taskName.toLowerCase()) || task.getDescription().toLowerCase().equals(taskName.toLowerCase())) {
				validString = false;
				break;
			}
		}

		return validString;
	}

	public static String getValidXMLString(final String stringData) {
		return stringData.replace("<", "").replace(">", "").replace("&", "").replace("\'", "").replace("\"", "");
	}

}
