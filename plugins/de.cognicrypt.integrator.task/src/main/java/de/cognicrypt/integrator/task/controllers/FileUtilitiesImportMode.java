package de.cognicrypt.integrator.task.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.Activator;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class FileUtilitiesImportMode {

	private StringBuilder errors; // Maintain all the errors to display them on the wizard.
	IntegratorModel integratorModel;
	
	
	public FileUtilitiesImportMode(){
		super();
		errors = new StringBuilder();
		integratorModel = IntegratorModel.getInstance();
	}

	public String writeDataImportMode() {
		String taskName = integratorModel.getImportFile().getName().replace(".zip", "");
		integratorModel.setTaskName(taskName);
		File iconLocation = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + taskName + "/res/" + taskName + ".png");
		if(iconLocation.exists()) {
			integratorModel.setLocationOfIconFile(iconLocation);
			copyImage(iconLocation);
		}else {
			errors.append("ZIP invalide (Icon File not found) \n");
		}
		File jsonLocation = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + taskName + "/res/" + taskName + ".json");
		if(iconLocation.exists()) {
			integratorModel.setLocationOfIconFile(jsonLocation);
			copyJSON(jsonLocation);
		}else {
			errors.append("ZIP invalide (Question JSON File not found) \n");
		}
		copyTemplatesImportMode();
		FileUtilities.deleteDirectory(new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + taskName));
		integratorModel.setTask();
		return errors.toString();
	}	
	
	private void copyTemplatesImportMode(){
		File templateDir = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + integratorModel.getTaskName() + "/template");
		if(!templateDir.exists()) {
			errors.append("ZIP invalide (Template Dir not found) \n");
			return;
		}
		File[] templates = templateDir.listFiles();
		if (templates != null) {
			for (File f : templates) {
				String destName = f.getName();
				File destDir = new File(Constants.ECLIPSE_LOC_TEMP_DIR, destName);
				destDir.mkdir();
				File templateFile = f.listFiles()[0];
				File dest = new File(destDir, integratorModel.getTrimmedTaskName() + ".java");
				try {
					Files.copy(templateFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING,
							StandardCopyOption.COPY_ATTRIBUTES);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}else {
			errors.append("ZIP invalide (Template Files not found) \n");
		}
	}
	

	/**
	 * Copy the image file to the appropriate location.
	 *
	 * @param existingFileLocation
	 */
	private void copyImage(final File existingFileLocation) {
			File targetDirectory = null;
			try {
				if (existingFileLocation.getPath().endsWith(Constants.PNG_EXTENSION)) {
					targetDirectory = new File(Constants.ECLIPSE_LOC_IMG_DIR, IntegratorModel.getInstance().getTrimmedTaskName() + Constants.PNG_EXTENSION);
				} else {
					throw new Exception("Unknown file type.");
				}
				Activator.getDefault().logError("CopyNonCustom " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
				Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used by the code generato
				} catch (final Exception e) {
				Activator.getDefault().logError(e);
				errors.append("There was a problem copying file ");
				errors.append(existingFileLocation.getName());
				errors.append("\n");
			}
	}

	/**
	 * Copy the Question JSON file to the appropriate location.
	 *
	 * @param existingFileLocation
	 */
	private void copyJSON(final File existingFileLocation) {
		File targetDirectory = null;
		
		try {
			if (existingFileLocation.getPath().endsWith(Constants.JSON_EXTENSION)) {
				targetDirectory = new File(Constants.ECLIPSE_LOC_TASKDESC_DIR, IntegratorModel.getInstance().getTrimmedTaskName() + Constants.JSON_EXTENSION);
			} else {
				throw new Exception("Unknown file type.");
			}
			Activator.getDefault().logError("CopyNonCustom " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
			Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used by the code generator		

		} catch (final Exception e) {
			Activator.getDefault().logError(e);
			errors.append("There was a problem copying file ");
			errors.append(existingFileLocation.getName());
			errors.append("\n");
		}
	}


	/**
	 * Update the task.json file with the new Task.
	 *
	 * @param task the Task to be added.
	 */
	public void writeTaskToJSONFile(final Task task) {

		BufferedReader reader = null;
		BufferedWriter writer = null;
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			reader = new BufferedReader(
					new FileReader(new File(Constants.customjsonTaskFile)));
			final List<Task> tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {
			}.getType());
			// Add the new task to the list.
			tasks.add(task);
			reader.close();

			writer = new BufferedWriter(
					new FileWriter(new File(Constants.customjsonTaskFile)));
			gson.toJson(tasks, new TypeToken<List<Task>>() {
			}.getType(), writer);
			writer.close();

		} catch (final IOException e) {
			Activator.getDefault().logError(e);
			errors.append("There was a problem updating the task file.\n");
		}
	}
}
