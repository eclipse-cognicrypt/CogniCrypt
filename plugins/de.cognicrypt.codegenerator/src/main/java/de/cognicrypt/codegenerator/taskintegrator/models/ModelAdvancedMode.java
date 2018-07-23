/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.models;

import java.io.File;

import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;

public class ModelAdvancedMode {

	private String nameOfTheTask;
	private File locationOfCustomLibrary;
	private File locationOfClaferFile;
	private File locationOfXSLFile;
	private File locationOfJSONFile;
	private File locationOfHelpXMLFile;
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
	 * Generate a name for the task based on the input given by the user and return it.
	 * 
	 * @param stringTaskName
	 * @return
	 */
	private String getMachineReadableName(String stringTaskName) {
		String[] split = stringTaskName.split(" ");
		StringBuilder machineReadable = new StringBuilder();
		if (stringTaskName.length() > 0) {
			for (String string : split) {
				machineReadable.append(string.substring(0, 1).toUpperCase());
				machineReadable.append(string.substring(1));
			}
		}

		return machineReadable.toString();
	}

	/**
	 * @return the nameOfTheTask
	 */
	public String getNameOfTheTask() {
		return nameOfTheTask;
	}

	/**
	 * @param nameOfTheTask
	 *        the nameOfTheTask to set
	 */
	public void setNameOfTheTask(String nameOfTheTask) {
		this.nameOfTheTask = getMachineReadableName(nameOfTheTask); // generate the task name that will be used as the machine readable identifier for the task.
		this.setDescription(nameOfTheTask); // This is the human readable name entered by the user.
	}

	/**
	 * @return the locationOfCustomLibrary
	 */
	public File getLocationOfCustomLibrary() {
		return locationOfCustomLibrary;
	}

	/**
	 * @param locationOfCustomLibrary
	 *        the locationOfCustomLibrary to set
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
	 * @param locationOfClaferFile
	 *        the locationOfClaferFile to set
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
	 * @param locationOfXSLFile
	 *        the locationOfXSLFile to set
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
	 * @param locationOfJSONFile
	 *        the locationOfJSONFile to set
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
	 * @param isGuidedModeChosen
	 *        the isGuidedModeChosen to set
	 */
	public void setGuidedModeChosen(boolean isGuidedModeChosen) {
		this.isGuidedModeChosen = isGuidedModeChosen;
	}
	/*	*//**
			 * @return the isGuidedModeForced
			 */
	/*
	 * public boolean isGuidedModeForced() { return isGuidedModeForced; }
	 *//**
		 * @param isGuidedModeForced
		 *        the isGuidedModeForced to set
		 *//*
			 * private void setGuidedModeForced(boolean isGuidedModeForced) { this.isGuidedModeForced = isGuidedModeForced; }
			 */

	/**
	 * @return the isCustomLibraryRequired
	 */
	public boolean isCustomLibraryRequired() {
		return isCustomLibraryRequired;
	}

	/**
	 * @param isCustomLibraryRequired
	 *        the isCustomLibraryRequired to set
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
		task.setModelFile(Constants.CFR_FILE_DIRECTORY_PATH + getNameOfTheTask() + Constants.JS_EXTENSION);
		task.setQuestionsJSONFile(Constants.JSON_FILE_DIRECTORY_PATH + getNameOfTheTask() + Constants.JSON_EXTENSION);
		task.setTaskDescription(getTaskDescription() == null ? "" : getTaskDescription());
		task.setXslFile(Constants.XSL_FILE_DIRECTORY_PATH + getNameOfTheTask() + Constants.XSL_EXTENSION);
		task.setAdditionalResources(Constants.JAR_FILE_DIRECTORY_PATH + getNameOfTheTask());
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *        the description to set
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
	 * @param taskDescription
	 *        the taskDescryption to set
	 */
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	/**
	 * @return the locationOfHelpXMLFile
	 */
	public File getLocationOfHelpXMLFile() {
		return locationOfHelpXMLFile;
	}

	/**
	 * @param locationOfHelpXMLFile
	 *        the locationOfHelpXMLFile to set
	 */
	public void setLocationOfHelpXMLFile(File locationOfHelpXMLFile) {
		this.locationOfHelpXMLFile = locationOfHelpXMLFile;
	}
}
