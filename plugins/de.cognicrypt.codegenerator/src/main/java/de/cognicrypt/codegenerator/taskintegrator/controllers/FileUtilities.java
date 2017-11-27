package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.utilities.Utils;

public class FileUtilities {

	private String taskName;	
	
	public FileUtilities(String taskName) {
		super();
		this.setTaskName(taskName);
	}

	public void writeFiles(ArrayList<ClaferFeature> claferFeatures, ArrayList<Question> questions, String xslFileContents, File customLibLocation) {
		writeCFRFile(claferFeatures);
		writeJSONFile(questions);
		writeXSLFile(xslFileContents);
		writeFileFromPath(customLibLocation);
	}
	
	public void writeFiles(File cfrFileLocation, File jsonFileLocation, File xslFileLocation, File customLibLocation) {
		writeFileFromPath(cfrFileLocation);
		writeFileFromPath(jsonFileLocation);
		writeFileFromPath(xslFileLocation);
		writeFileFromPath(customLibLocation);
	}
	
	private void writeCFRFile(ArrayList<ClaferFeature> claferFeatures) {
		
	}
	
	private void writeFileFromPath(File existingFileLocation) {
		
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
	
	private void writeJSONFile(ArrayList<Question> questions) {
		
	}
	
	private void writeXSLFile(String xslFileContents) {
		
	}
	
	private String getTaskName() {
		return taskName;
	}

	private void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
