/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 *
 */
package de.cognicrypt.integrator.task.models;

import java.io.File;
import java.util.HashMap;

import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;

public class ModelAdvancedMode {

	private String nameOfTheTask;
	private File locationOfCustomLibrary;
	private File locationOfClaferFile;
	private File locationOfXSLFile;
	private File locationOfCryslTemplate;
	private File locationOfIconFile;
	private File locationOfJSONFile;
	private File locationOfHelpXMLFile;
	private boolean isCustomLibraryRequired;
	private boolean isGuidedModeChosen;
	// private boolean isGuidedModeForced;
	private final Task task;
	private String description;
	private String taskDescription;
	private HashMap<String, File> crylTemplatesWithOption;

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
	private String getMachineReadableName(final String stringTaskName) {
		final String[] split = stringTaskName.split(" ");
		final StringBuilder machineReadable = new StringBuilder();
		if (stringTaskName.length() > 0) {
			for (final String string : split) {
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
		return this.nameOfTheTask;
	}

	/**
	 * @param nameOfTheTask the nameOfTheTask to set
	 */
	public void setNameOfTheTask(final String nameOfTheTask) {
		this.nameOfTheTask = getMachineReadableName(nameOfTheTask); // generate the task name that will be used as the machine readable identifier for the task.
		setDescription(nameOfTheTask); // This is the human readable name entered by the user.
	}

	public File getLocationOfIconFile() {
		return locationOfIconFile;
	}

	public void setLocationOfIconFile(File locationOfIconFile) {
		this.locationOfIconFile = locationOfIconFile;
	}

	/**
	 * @return the locationOfCustomLibrary
	 */
	public File getLocationOfCustomLibrary() {
		return this.locationOfCustomLibrary;
	}

	/**
	 * @param locationOfCustomLibrary the locationOfCustomLibrary to set
	 */
	public void setLocationOfCustomLibrary(final File locationOfCustomLibrary) {
		this.locationOfCustomLibrary = locationOfCustomLibrary;
	}

	/**
	 * @return the locationOfClaferFile
	 */
	public File getLocationOfClaferFile() {
		return this.locationOfClaferFile;
	}

	/**
	 * @param locationOfClaferFile the locationOfClaferFile to set
	 */
	public void setLocationOfClaferFile(final File locationOfClaferFile) {
		this.locationOfClaferFile = locationOfClaferFile;
	}

	
	/**
	 * @return the locationOfXSLFile
	 */
	public File getLocationOfXSLFile() {
		return this.locationOfXSLFile;
	}

	/**
	 * @param locationOfXSLFile the locationOfXSLFile to set
	 */
	public void setLocationOfXSLFile(final File locationOfXSLFile) {
		this.locationOfXSLFile = locationOfXSLFile;
	}

	/**
	 * @return the locationOfJSONFile
	 */
	public File getLocationOfJSONFile() {
		return this.locationOfJSONFile;
	}

	/**
	 * @param locationOfJSONFile the locationOfJSONFile to set
	 */
	public void setLocationOfJSONFile(final File locationOfJSONFile) {
		this.locationOfJSONFile = locationOfJSONFile;
	}

	/**
	 * @return the isGuidedModeChosen
	 */
	public boolean isGuidedModeChosen() {
		return this.isGuidedModeChosen;
	}

	/**
	 * @param isGuidedModeChosen the isGuidedModeChosen to set
	 */
	public void setGuidedModeChosen(final boolean isGuidedModeChosen) {
		this.isGuidedModeChosen = isGuidedModeChosen;
	}
	/*	*//**
				 * @return the isGuidedModeForced
				 */
	/*
	 * public boolean isGuidedModeForced() { return isGuidedModeForced; }
	 *//**
			 * @param isGuidedModeForced the isGuidedModeForced to set
			 *//*
					 * private void setGuidedModeForced(boolean isGuidedModeForced) { this.isGuidedModeForced = isGuidedModeForced; }
					 */

	/**
	 * @return the isCustomLibraryRequired
	 */
	public boolean isCustomLibraryRequired() {
		return this.isCustomLibraryRequired;
	}

	/**
	 * @param isCustomLibraryRequired the isCustomLibraryRequired to set
	 */
	public void setCustomLibraryRequired(final boolean isCustomLibraryRequired) {
		this.isCustomLibraryRequired = isCustomLibraryRequired;
	}

	/**
	 * @return the task
	 */
	public Task getTask() {
		return this.task;
	}

	/**
	 * Generate the Task instance from the advanced mode model.
	 */
	public void setTask() {
		this.task.setName(getNameOfTheTask());
		this.task.setDescription(getDescription());
		this.task.setModelFile(Constants.CFR_FILE_DIRECTORY_PATH + getNameOfTheTask() + Constants.JS_EXTENSION);
		this.task.setQuestionsJSONFile(Constants.JSON_FILE_DIRECTORY_PATH + getNameOfTheTask() + Constants.JSON_EXTENSION);
		this.task.setTaskDescription(getTaskDescription() == null ? "" : getTaskDescription());
		this.task.setCodeTemplate(Constants.XSL_FILE_DIRECTORY_PATH + getNameOfTheTask() + Constants.XSL_EXTENSION);
		this.task.setAdditionalResources(Constants.JAR_FILE_DIRECTORY_PATH + getNameOfTheTask());
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the taskDescryption
	 */
	public String getTaskDescription() {
		return this.taskDescription;
	}

	/**
	 * @param taskDescription the taskDescryption to set
	 */
	public void setTaskDescription(final String taskDescription) {
		this.taskDescription = taskDescription;
	}

	/**
	 * @return the locationOfHelpXMLFile
	 */
	public File getLocationOfHelpXMLFile() {
		return this.locationOfHelpXMLFile;
	}

	/**
	 * @param locationOfHelpXMLFile the locationOfHelpXMLFile to set
	 */
	public void setLocationOfHelpXMLFile(final File locationOfHelpXMLFile) {
		this.locationOfHelpXMLFile = locationOfHelpXMLFile;
	}

	public File getLocationOfCryslTemplate() {
		return locationOfCryslTemplate;
	}

	public void setLocationOfCryslTemplate(File locationOfCryslTemplate) {
		this.locationOfCryslTemplate = locationOfCryslTemplate;
	}

	public HashMap<String, File> getCrylTemplatesWithOption() {
		return crylTemplatesWithOption;
	}

	public void setCrylTemplatesWithOption(HashMap<String, File> crylTemplatesWithOption) {
		this.crylTemplatesWithOption = crylTemplatesWithOption;
	}
}
