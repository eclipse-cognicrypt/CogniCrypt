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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jface.dialogs.MessageDialog;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.Validator;

public class IntegratorModel {
	
	private String description;
	private String taskDescription;
	private File locationOfCryslTemplate;
	private File locationOfIconFile;
	private File locationOfJSONFile;
	private File locationImportFile;
	private boolean isGuidedModeChosen;
	private boolean isImportModeChosen;
	private final Task task;
	
	private boolean debug;
	
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
		task.setTaskDescription(getTaskDescription() == null ? "" : getTaskDescription());
		task.setQuestionsJSONFile(Constants.ECLIPSE_LOC_TASKDESC_DIR + "/" + getTaskName() + Constants.JSON_EXTENSION);
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
	
	
	/**
	 * 
	 * @param templateFilePath
	 * @return true if the added template was the first one
	 * @throws Exception if warning has to be shown
	 */
	public boolean addTemplate(String templateFilePath) throws Exception {
		
		boolean newTaskName = false;
		
		if (templateFilePath == null) 
			return false; // user canceled the file dialog
		
		// Set the task name or verify that it's equal
		String[] filePathParts = templateFilePath.split("(\\/|\\\\)");
		String taskName = filePathParts[filePathParts.length - 1].replace(".java", "");
		
		if(!debug && Validator.checkIfTaskNameAlreadyExists(taskName)) { // can not be tested because TaskJSONReader requires the plugin bundle
			throw new Exception(Constants.ERROR_TASK_ALREADY_INTEGRATED);
		}
		
		if (getTaskName() == null) {
			setTaskName(taskName);
			newTaskName = true;
		}else if (!taskName.contentEquals(IntegratorModel.getInstance().getTaskName())) {
			throw new Exception(Constants.ERROR_DIFFERENT_TASK_NAME);
		}
		
		// Extract package line from the template's source code
		String packageLine = "";

		Scanner scanner;
		try {
			scanner = new Scanner(new File(templateFilePath));
		} catch (FileNotFoundException e1) {
			throw new Exception(Constants.ERROR_FILE_NOT_FOUND);
		}
		
		while (packageLine.contentEquals("")) {

			if(!scanner.hasNextLine()) {
				scanner.close();
				throw new Exception(Constants.ERROR_NO_PACKAGE);
			}

			String[] expr = scanner.nextLine().split(";");

			// Lines may contain multiple expressions
			for(String e : expr) {
				String line = e.trim();
				if (line.startsWith("package")) {
					packageLine = line;
					break;
				}
				if (line.contains("class")) {
					break;
				}
			}

		}
		scanner.close();
		
		// Extract identifier
		String[] packageParts = packageLine.split("\\.");
		String templateIdentifier = packageParts[packageParts.length - 1].replace(taskName, "").replace(";", "");

		cryslTemplateFiles.put(templateIdentifier, new File(templateFilePath));
		
		return newTaskName;
	}
	
	public void checkTemplatesDec() throws Exception {
		// Template list is empty
		if(isTemplatesEmpty())
			throw new Exception(Constants.ERROR_BLANK_TEMPLATE_LIST);
				
		// Single template identifier does not match the task name
		if(getIdentifiers().size() == 1 && !getIdentifiers().get(0).isEmpty())
			throw new Exception(Constants.ERROR_SINGLE_TEMPLATE_ID);
	}
	
	
	public boolean contains(String identifier) {
		return cryslTemplateFiles.containsKey(identifier);
	}
	
	/**
	 * 
	 * @param templateIdentifier
	 * @return true if last template was removed
	 */
	public boolean removeTemplate(String templateIdentifier) throws Exception {
		
		for(Question q : questions) {
			for(Answer a : q.getAnswers()) {
				if(a.getOption().contentEquals(templateIdentifier)) {
					throw new Exception(Constants.ERROR_TEMPLATE_IS_USED_IN_ANSWER);
				}
			}
		}


		cryslTemplateFiles.remove(templateIdentifier);

		if(isTemplatesEmpty()) {
			setTaskName(null);
			return true;
		}

		return false;
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
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
