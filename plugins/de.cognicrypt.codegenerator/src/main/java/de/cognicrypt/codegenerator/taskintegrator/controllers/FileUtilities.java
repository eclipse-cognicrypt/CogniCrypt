package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;

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
		writeFileFromPath(customLibLocation, taskName);
	}
	
	public void writeFiles(File cfrFileLocation, File jsonFileLocation, File xslFileLocation, File customLibLocation) {
		writeFileFromPath(cfrFileLocation, taskName);
		writeFileFromPath(jsonFileLocation, taskName);
		writeFileFromPath(xslFileLocation, taskName);
		writeFileFromPath(customLibLocation, taskName);
	}
	
	private void writeCFRFile(ArrayList<ClaferFeature> claferFeatures) {
		
	}
	
	private void writeFileFromPath(File existingFileLocation, String taskName) {
		
		if(existingFileLocation.exists() && !existingFileLocation.isDirectory()) {		
			File targetDirectory = null;
			try {
				
				if(existingFileLocation.getPath().endsWith(Constants.CFR_EXTENSION)) {
					targetDirectory= new File(evaluateTargetDirectoryPath(Constants.CFR_FILE_DIRECTORY_PATH), taskName + Constants.CFR_EXTENSION);
				} else if(existingFileLocation.getPath().endsWith(Constants.JSON_EXTENSION)) {
					targetDirectory = new File(evaluateTargetDirectoryPath(Constants.JSON_FILE_DIRECTORY_PATH), taskName + Constants.JSON_EXTENSION);
				} else if(existingFileLocation.getPath().endsWith(Constants.XSL_EXTENSION)) {
					targetDirectory = new File(evaluateTargetDirectoryPath(Constants.XSL_FILE_DIRECTORY_PATH), taskName + Constants.XSL_EXTENSION);
				} else if(existingFileLocation.getPath().endsWith(Constants.JAR_EXTENSION)) {
					File tempDirectory = new File(evaluateTargetDirectoryPath(Constants.JAR_FILE_DIRECTORY_PATH), taskName + Constants.innerFileSeparator);
					tempDirectory.mkdir();
					targetDirectory = new File(tempDirectory, taskName + Constants.JAR_EXTENSION);
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

	private File evaluateTargetDirectoryPath(String directoryLocation) {
		
		try {
			final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);

			if (bundle == null) {
				// running as application
				return new File(directoryLocation);
			} else {
				final URL fileURL = bundle.getEntry(directoryLocation);
				final URL resolvedURL = FileLocator.toFileURL(fileURL);
				final URI uri = new URI(resolvedURL.getProtocol(), resolvedURL.getPath(), null);
				return new File(uri);
			}
		} catch (final Exception ex) {
			Activator.getDefault().logError(ex);
		}

		return null;
		
	}
	
	private String getTaskName() {
		return taskName;
	}

	private void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
