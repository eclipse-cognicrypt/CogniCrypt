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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;

public class IntegratorModel {
	
	private String description;
	private String taskDescription;
	private File locationOfCryslTemplate;
	private File locationOfIconFile;
	private File locationOfJSONFile;
	private File locationExportFile;
	private File locationImportFile;
	private boolean isGuidedModeChosen;
	private boolean isImportModeChosen;
	private final Task task;
	
	private HashMap<String, File> cryslTemplateFiles;
	private final ArrayList<Question> questions;

	// Singleton
	private static IntegratorModel instance = new IntegratorModel();
	
	public static IntegratorModel getInstance() {
		return instance;
	}
	
	public static void resetInstance() {
		instance = new IntegratorModel();
	}
	
	private IntegratorModel() {
		super();
		task = new Task();
		cryslTemplateFiles = new HashMap<String, File>();
		questions = new ArrayList<>();
	}

	/**
	 * @return the nameOfTheTask
	 */
	public String getTaskName() {
		return task.getName();
	}
	
	public String getTrimmedTaskName() {
		return task.getName().replaceAll("[^A-Za-z0-9]", "");
	}

	/**
	 * @param nameOfTheTask the nameOfTheTask to set
	 */
	public void setTaskName(final String nameOfTheTask) {
		task.setName(nameOfTheTask);
		task.setDescription(nameOfTheTask);
	}

	public File getIconFile() {
		return locationOfIconFile;
	}

	public void setLocationOfIconFile(File locationOfIconFile) {
		this.locationOfIconFile = locationOfIconFile;
	}

	public File getImportFile() {
		return locationImportFile;
	}

	public void setLocationOfImportFile(File locationOfImportFile) {
		this.locationImportFile = locationOfImportFile;
	}

	/**
	 * @return the locationOfJSONFile
	 */
	public File getJSONFile() {
		return this.locationOfJSONFile;
	}

	/**
	 * @param locationOfJSONFile the locationOfJSONFile to set
	 */
	public void setLocationOfJSONFile(final File locationOfJSONFile) {
		this.locationOfJSONFile = locationOfJSONFile;
	}
	
	/**
	 * @return the locationExportFile
	 */
	public File getExportFile() {
		return this.locationExportFile;
	}
	
	/**
	 * @param locationExportFile the locationExportFile to set
	 */
	public void setLocationOfExportFile(final File locationExportFile) {
		this.locationExportFile = locationExportFile;
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

	/**
	 * @return the isGuidedModeChosen
	 */
	public boolean isImportModeChosen() {
		return this.isImportModeChosen;
	}

	/**
	 * @param isGuidedModeChosen the isGuidedModeChosen to set
	 */
	public void setImportModeChosen(final boolean isImportModeChosen) {
		this.isImportModeChosen = isImportModeChosen;
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
		task.setName(getTaskName());
		task.setDescription(getDescription() == null ? "" : getDescription());
		task.setQuestionsJSONFile(Constants.ECLIPSE_LOC_TASKDESC_DIR + "/" + getTaskName() + Constants.JSON_EXTENSION);
		task.setTaskDescription(getTaskDescription() == null ? "" : getTaskDescription());
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
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public File getLocationOfCryslTemplate() {
		return locationOfCryslTemplate;
	}

	public void setLocationOfCryslTemplate(File locationOfCryslTemplate) {
		this.locationOfCryslTemplate = locationOfCryslTemplate;
	}
	

	
	
	public HashMap<String, File> getCryslTemplateFiles() {
		return cryslTemplateFiles;
	}
	
	public boolean isTemplatesEmpty() {
		return cryslTemplateFiles.isEmpty();
	}
	
	public void addTemplate(String identifier, File path) {	
		cryslTemplateFiles.put(identifier, path);
	}
	
	public boolean contains(String identifier) {
		return cryslTemplateFiles.containsKey(identifier);
	}
	
	public void removeTemplate(String identifier) {
			cryslTemplateFiles.remove(identifier);
	}
	
	public List<String> getIdentifiers(){
		ArrayList<String> identifiers = new ArrayList<String>();
		identifiers.addAll(cryslTemplateFiles.keySet());
		return identifiers;
	}
	
	public File getTemplate(String identifier){
		return cryslTemplateFiles.get(identifier);
	}
	
	public ArrayList<Question> getQuestions() {
		return questions;
	}
	
	public Question getQuestion(int questionIndex) {
		return questions.get(questionIndex);
	}
	
	public Answer getAnswer(int questionIndex, int answerIndex) {
		return questions.get(questionIndex).getAnswers().get(answerIndex);
	}
}
