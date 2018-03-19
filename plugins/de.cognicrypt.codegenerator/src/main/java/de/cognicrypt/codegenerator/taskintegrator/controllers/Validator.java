package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;

import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;

public class Validator {

	public static boolean checkIfTaskNameAlreadyExists(String taskName, ControlDecoration controlDecoration) {
		List<Task> existingTasks = TaskJSONReader.getTasks(); // Required to validate the task name that is chosen by the user.
		boolean validString = true;

		// Validation : check whether the name already exists.
		for (Task task : existingTasks) {
			if (task.getName().toLowerCase().equals(taskName.toLowerCase()) || task.getDescription().toLowerCase().equals(taskName.toLowerCase())) {
				validString = false;
				break;
			}
		}

		return validString;
	}

}
