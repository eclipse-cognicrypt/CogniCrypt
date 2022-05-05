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
	
	/**
	 * This class is used to copy the necessary files for Task Integration to the correct destinations
	 * where the Code Generator can use them (ImportMode)
	 * @author felix
	 * 
	 */
	public FileUtilitiesImportMode(){
		super();
		errors = new StringBuilder();
		integratorModel = IntegratorModel.getInstance();
	}

	/**
	 * copy given template files, given image file and given questionJSON file to local resource directory for custom tasks 
	 * (only used in Guided Mode Integration)
	 * @return String with the error messages ("" if no errors happened)
	 */
	public String writeDataImportMode() {
		String taskName = integratorModel.getImportFile().getName().replace(".zip", "");
		integratorModel.setTaskName(taskName);
		File iconLocation = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + taskName + "/res/" + taskName + ".png");
		if(iconLocation.exists()) {
			integratorModel.setLocationOfIconFile(iconLocation);
			copyImage(iconLocation);
		}else {
			errors.append(Constants.ERROR_ICON_FILE_NOT_FOUND);
		}
		File jsonLocation = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + taskName + "/res/" + taskName + ".json");
		if(iconLocation.exists()) {
			integratorModel.setLocationOfIconFile(jsonLocation);
			copyJSON(jsonLocation);
		}else {
			errors.append(Constants.ERROR_JSON_FILE_NOT_FOUND);
		}
		copyTemplatesImportMode();
		FileUtilities.deleteDirectory(new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + taskName));
		IntegratorModel.getInstance().setQuestionsJSONFile();
		return errors.toString();
	}	
	
	/**
	 * Copy the template files to the appropriate location for code generator
	 * 
	 * @throws IOException
	 */
	private void copyTemplatesImportMode(){
		File templateDir = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + integratorModel.getTaskName() + "/template");
		if(!templateDir.exists()) {
			errors.append("ZIP invalid (Template Dir not found) \n");
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
					errors.append(Constants.ERROR_FILE_COPY + f.toString() + "/n");
				}

			}
		}else {
			errors.append(Constants.ERROR_TEMPLATE_FILE_NOT_FOUND);
		}
	}
	

	/**
	 * Copy the image file to the appropriate location for code generator
	 * @param existingFileLocation the existing image file from the exportable ZIP
	 */
	private void copyImage(final File existingFileLocation) {
			File targetDirectory = null;
			try {
				if (existingFileLocation.getPath().endsWith(Constants.PNG_EXTENSION)) {
					targetDirectory = new File(Constants.ECLIPSE_LOC_IMG_DIR, IntegratorModel.getInstance().getTrimmedTaskName() + Constants.PNG_EXTENSION);
				} else {
					throw new Exception(Constants.ERROR_UNKNOWN_FILE_TYPE);
				}
				Activator.getDefault().logError("CopyNonCustom " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
				Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used by the code generato
				} catch (final Exception e) {
				Activator.getDefault().logError(e);
				errors.append(Constants.ERROR_FILE_COPY);
				errors.append(existingFileLocation.getName());
				errors.append("\n");
			}
	}

	/**
	 * Copy the questionJSON file to the appropriate location for code generator
	 * @param existingFileLocation the existing questionJSON file from the exportable ZIP
	 */
	private void copyJSON(final File existingFileLocation) {
		File targetDirectory = null;
		
		try {
			if (existingFileLocation.getPath().endsWith(Constants.JSON_EXTENSION)) {
				targetDirectory = new File(Constants.ECLIPSE_LOC_TASKDESC_DIR, IntegratorModel.getInstance().getTrimmedTaskName() + Constants.JSON_EXTENSION);
			} else {
				throw new Exception(Constants.ERROR_UNKNOWN_FILE_TYPE);
			}
			Activator.getDefault().logError("CopyNonCustom " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
			Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used by the code generator		

		} catch (final Exception e) {
			Activator.getDefault().logError(e);
			errors.append(Constants.ERROR_FILE_COPY);
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
			final List<Task> tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {}.getType());
			
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
			errors.append(Constants.ERROR_TASK_UPDATE);
		}
	}
}
