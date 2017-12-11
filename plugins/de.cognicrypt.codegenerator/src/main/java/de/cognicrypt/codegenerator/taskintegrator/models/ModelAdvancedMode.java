/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.models;

import java.io.File;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.tasks.Task;

/**
 * @author rajiv
 *
 */
public class ModelAdvancedMode {
	private String nameOfTheTask;	
	private File locationOfCustomLibrary;
	private File locationOfClaferFile;
	private File locationOfXSLFile;
	private File locationOfJSONFile;
	private boolean isCustomLibraryRequired;
	private boolean isGuidedModeChosen;
	//private boolean isGuidedModeForced;
	private Task task;
	private String description;
	private String taskDescription;
	

	public ModelAdvancedMode() {
		super();
		this.task = new Task();
	}
	/**
	 * @return the nameOfTheTask
	 */
	public String getNameOfTheTask() {
		return nameOfTheTask;
	}
	/**
	 * @param nameOfTheTask the nameOfTheTask to set
	 */
	public void setNameOfTheTask(String nameOfTheTask) {
		this.nameOfTheTask = nameOfTheTask;
	}
	/**
	 * @return the locationOfCustomLibrary
	 */
	public File getLocationOfCustomLibrary() {
		return locationOfCustomLibrary;
	}
	/**
	 * @param locationOfCustomLibrary the locationOfCustomLibrary to set
	 */
	public void setLocationOfCustomLibrary(File locationOfCustomLibrary) {
		this.locationOfCustomLibrary = locationOfCustomLibrary;
	}
	/**
	 * @return the locationOfClaferFile
	 */
	public File getLocationOfClaferFile() {
		return locationOfClaferFile;
	}
	/**
	 * @param locationOfClaferFile the locationOfClaferFile to set
	 */
	public void setLocationOfClaferFile(File locationOfClaferFile) {
		this.locationOfClaferFile = locationOfClaferFile;
	}
	/**
	 * @return the locationOfXSLFile
	 */
	public File getLocationOfXSLFile() {
		return locationOfXSLFile;
	}
	/**
	 * @param locationOfXSLFile the locationOfXSLFile to set
	 */
	public void setLocationOfXSLFile(File locationOfXSLFile) {
		this.locationOfXSLFile = locationOfXSLFile;
	}
	/**
	 * @return the locationOfJSONFile
	 */
	public File getLocationOfJSONFile() {
		return locationOfJSONFile;
	}
	/**
	 * @param locationOfJSONFile the locationOfJSONFile to set
	 */
	public void setLocationOfJSONFile(File locationOfJSONFile) {
		this.locationOfJSONFile = locationOfJSONFile;
	}
	/**
	 * @return the isGuidedModeChosen
	 */
	public boolean isGuidedModeChosen() {
		return isGuidedModeChosen;
	}
	/**
	 * @param isGuidedModeChosen the isGuidedModeChosen to set
	 */
	public void setGuidedModeChosen(boolean isGuidedModeChosen) {
		this.isGuidedModeChosen = isGuidedModeChosen;
	}
/*	*//**
	 * @return the isGuidedModeForced
	 *//*
	public boolean isGuidedModeForced() {
		return isGuidedModeForced;
	}
	*//**
	 * @param isGuidedModeForced the isGuidedModeForced to set
	 *//*
	private void setGuidedModeForced(boolean isGuidedModeForced) {
		this.isGuidedModeForced = isGuidedModeForced;
	}*/

	/**
	 * @return the isCustomLibraryRequired
	 */
	public boolean isCustomLibraryRequired() {
		return isCustomLibraryRequired;
	}

	/**
	 * @param isCustomLibraryRequired the isCustomLibraryRequired to set
	 */
	public void setCustomLibraryRequired(boolean isCustomLibraryRequired) {
		this.isCustomLibraryRequired = isCustomLibraryRequired;
	}
	/**
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}
	/**
	 * Generate the Task instance from the advanced mode model.
	 */
	public void setTask() {
		task.setName(getNameOfTheTask());
		task.setDescription(getDescription());
		task.setModelFile(Constants.CFR_FILE_DIRECTORY_PATH + getNameOfTheTask() + Constants.CFR_EXTENSION);
		task.setQuestionsJSONFile(Constants.JSON_FILE_DIRECTORY_PATH + getNameOfTheTask() + Constants.JSON_EXTENSION);
		task.setTaskDescription(getTaskDescription());
		task.setXslFile(Constants.XSL_FILE_DIRECTORY_PATH + getNameOfTheTask() + Constants.XSL_EXTENSION);
		task.setAdditionalResources(Constants.JAR_FILE_DIRECTORY_PATH + getNameOfTheTask() + Constants.innerFileSeparator + getNameOfTheTask() + Constants.JAR_EXTENSION);
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the taskDescryption
	 */
	public String getTaskDescription() {
		return taskDescription;
	}
	/**
	 * @param taskDescription the taskDescryption to set
	 */
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
}
