/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.utilities.Utils;

/**
 * @author rajiv
 *
 */

public class FileUtilities {

	private String taskName;	
	
	public FileUtilities(String taskName) {
		super();
		this.setTaskName(taskName);
	}
	
	/**
	 * Write the data from the pages to target location in the plugin.
	 * @param claferFeatures
	 * @param questions
	 * @param xslFileContents
	 * @param customLibLocation
	 */
	public void writeFiles(ArrayList<ClaferFeature> claferFeatures, ArrayList<Question> questions, String xslFileContents, File customLibLocation) {
		writeCFRFile(claferFeatures);
		writeJSONFile(questions);
		writeXSLFile(xslFileContents);
		copyFileFromPath(customLibLocation);
	}
	
	/**
	 * Copy the selected files to target location in the plugin.
	 * @param cfrFileLocation
	 * @param jsonFileLocation
	 * @param xslFileLocation
	 * @param customLibLocation
	 */
	public void writeFiles(File cfrFileLocation, File jsonFileLocation, File xslFileLocation, File customLibLocation) {
		
		copyFileFromPath(cfrFileLocation);
		copyFileFromPath(jsonFileLocation);
		copyFileFromPath(xslFileLocation);
		copyFileFromPath(customLibLocation);
	}
	
	/**
	 * Copy the given file to the appropriate location. 
	 * @param existingFileLocation
	 */	
	private void copyFileFromPath(File existingFileLocation) {
		if(existingFileLocation.exists() && !existingFileLocation.isDirectory()) {		
			File targetDirectory = null;
			try {
				
				if(existingFileLocation.getPath().endsWith(Constants.CFR_EXTENSION)) {
					targetDirectory= new File(Utils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH), getTaskName() + Constants.CFR_EXTENSION);
				} else if(existingFileLocation.getPath().endsWith(Constants.JSON_EXTENSION)) {
					targetDirectory = new File(Utils.getResourceFromWithin(Constants.JSON_FILE_DIRECTORY_PATH), getTaskName() + Constants.JSON_EXTENSION);
				} else if(existingFileLocation.getPath().endsWith(Constants.XSL_EXTENSION)) {
					targetDirectory = new File(Utils.getResourceFromWithin(Constants.XSL_FILE_DIRECTORY_PATH), getTaskName() + Constants.XSL_EXTENSION);
				} else if(existingFileLocation.getPath().endsWith(Constants.JAR_EXTENSION)) {
					File tempDirectory = new File(Utils.getResourceFromWithin(Constants.JAR_FILE_DIRECTORY_PATH), getTaskName() + Constants.innerFileSeparator);
					tempDirectory.mkdir();
					targetDirectory = new File(tempDirectory, getTaskName() + Constants.JAR_EXTENSION);
				} else {
					throw new Exception("Unknown file type.");
				}
			
				Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.COPY_ATTRIBUTES);
				
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Update the task.json file with the new Task.
	 * @param task the Task to be added.
	 */
	public void writeTaskToJSONFile(Task task){
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			reader = new BufferedReader(new FileReader(Utils.getResourceFromWithin(Constants.jsonTaskFile)));
			List<Task> tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {}.getType());	
			// Add the new task to the list.
			tasks.add(task);
			reader.close();
			
			writer = new BufferedWriter(new FileWriter(Utils.getResourceFromWithin(Constants.jsonTaskFile)));			
			gson.toJson(tasks, new TypeToken<List<Task>>() {}.getType(), writer);
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
		
	/**
	 * 
	 * @param claferFeatures
	 */
	private void writeCFRFile(ArrayList<ClaferFeature> claferFeatures) {
		
	}
		
	/**
	 * 
	 * @param questions
	 */
	private void writeJSONFile(ArrayList<Question> questions) {
		
	}
	
	/**
	 * 
	 * @param xslFileContents
	 */
	private void writeXSLFile(String xslFileContents) {
		
	}
	
	/**
	 * 
	 * @return
	 */
	private String getTaskName() {
		return taskName;
	}
	
	/**
	 * 
	 * @param taskName
	 */
	private void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
