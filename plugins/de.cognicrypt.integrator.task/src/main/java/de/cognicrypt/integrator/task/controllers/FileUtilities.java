/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 *
 */
package de.cognicrypt.integrator.task.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.clafer.ast.AstClafer;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.xml.sax.SAXException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.Activator;
import de.cognicrypt.integrator.task.widgets.TaskInformationComposite;
import de.cognicrypt.utils.Utils;

public class FileUtilities {

	private String taskName;
	private StringBuilder errors; // Maintain all the errors to display them on the wizard.

	/**
	 * The class needs to be initialized with a task name, as it is used extensively
	 * in the methods.
	 *
	 * @param taskName
	 */
	public FileUtilities(final String taskName) {
		super();
		setTaskName(taskName);
		setErrors(new StringBuilder());
	}
	
	public String writeData(final HashMap<String, File> cryslTemplateFile, final File iconFile) {
		copyImage(iconFile);
		for (String key : cryslTemplateFile.keySet()) {
			try {
				copyTemplate(cryslTemplateFile.get(key), key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return getErrors().toString();
	}

	public String writeData(final HashMap<String, File> cryslTemplateFile, final File iconFile, File jsonFile) {
		copyImage(iconFile);
		copyJSON(jsonFile);
		for (String key : cryslTemplateFile.keySet()) {
			try {
				copyTemplate(cryslTemplateFile.get(key), key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return getErrors().toString();
	}
	

	/**
	 * For the sake of reusability.
	 *
	 * @param fileName
	 */
	private void appendFileErrors(final String fileName) {
		getErrors().append("The contents of the file ");
		getErrors().append(fileName);
		getErrors().append(" are invalid.");
		getErrors().append("\n");
	}	
	
	public void copyTemplate(final File existingFileLocation, String option) throws IOException {
		File parentFolder = new File(Constants.ECLIPSE_LOC_TEMP_DIR);
		File templateFolder = new File(parentFolder, getTrimmedTaskName() + option);
		
		if (!templateFolder.isDirectory()) {
			templateFolder.mkdir();
		}

		File targetDirectory = new File(templateFolder, getTrimmedTaskName() + Constants.JAVA_EXTENSION);
		
		Path path = existingFileLocation.toPath();
		Path path2 = targetDirectory.toPath();
		
		Activator.getDefault().logError("Copy " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
			
		Files.copy(path, path2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
	}
	
	

	/**
	 * Copy the image file to the appropriate location.
	 *
	 * @param existingFileLocation
	 */
	public void copyImage(final File existingFileLocation) {
			File targetDirectory = null;
			try {
				if (existingFileLocation.getPath().endsWith(Constants.PNG_EXTENSION)) {
					targetDirectory = new File(Constants.ECLIPSE_LOC_IMG_DIR, getTrimmedTaskName() + Constants.PNG_EXTENSION);
				} else {
					throw new Exception("Unknown file type.");
				}
				Activator.getDefault().logError("CopyNonCustom " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
				Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

			} catch (final Exception e) {
				Activator.getDefault().logError(e);
				getErrors().append("There was a problem copying file ");
				getErrors().append(existingFileLocation.getName());
				getErrors().append("\n");
			}
	}

	/**
	 * Copy the Question JSON file to the appropriate location.
	 *
	 * @param existingFileLocation
	 */
	public void copyJSON(final File existingFileLocation) {
			File targetDirectory = null;
			try {
				if (existingFileLocation.getPath().endsWith(Constants.JSON_EXTENSION)) {
					targetDirectory = new File(Constants.ECLIPSE_LOC_TASKDESC_DIR, getTrimmedTaskName() + Constants.JSON_EXTENSION);
				} else {
					throw new Exception("Unknown file type.");
				}
				Activator.getDefault().logError("CopyNonCustom " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
				Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

			} catch (final Exception e) {
				Activator.getDefault().logError(e);
				getErrors().append("There was a problem copying file ");
				getErrors().append(existingFileLocation.getName());
				getErrors().append("\n");
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
					new FileReader(new File(Constants.localjsonTaskFile)));
			final List<Task> tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {
			}.getType());
			// Add the new task to the list.
			tasks.add(task);
			reader.close();

			writer = new BufferedWriter(
					new FileWriter(new File(Constants.localjsonTaskFile)));
			gson.toJson(tasks, new TypeToken<List<Task>>() {
			}.getType(), writer);
			writer.close();

		} catch (final IOException e) {
			Activator.getDefault().logError(e);
			getErrors().append("There was a problem updating the task file.\n");
		}
	}



	/**
	 * @param questions listOfAllQuestions
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public void writeJSONFile(final ArrayList<Question> questions) {

		final SegregatesQuestionsIntoPages pageContent = new SegregatesQuestionsIntoPages();
		final ArrayList<Page> pages = pageContent.getPages();

		final File jsonFile = new File(
				Constants.ECLIPSE_LOC_TASKDESC_DIR,
				getTrimmedTaskName() + Constants.JSON_EXTENSION);

		try {
			final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
			// creates the file
			jsonFile.createNewFile();

			// creates the writer object for json file
			final FileWriter writerForJsonFile = new FileWriter(jsonFile);

			// write the data into the .json file
			writerForJsonFile.write(gson.toJson(pages));
			writerForJsonFile.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		if (!validateJSONFile(jsonFile)) {
			jsonFile.delete();
		}
	}

	/**
	 * Validate the provided JSON file before copying it to the target location.
	 *
	 * @param jsonFileLocation
	 * @return a boolean value for the validity of the file.
	 */
	private boolean validateJSONFile(final File jsonFileLocation) {
		try {
			final Gson gson = new Gson();
			final BufferedReader reader = new BufferedReader(new FileReader(jsonFileLocation));
			gson.fromJson(reader, Object.class);
			reader.close();
			return true;
		} catch (IOException e) {
			Activator.getDefault().logError(e);
			appendFileErrors(jsonFileLocation.getName());
			return false;
		}
	}
	
	
	/**
	 * Return the name of that task that is set for the file writes..
	 *
	 * @return
	 */
	private String getTaskName() {
		return this.taskName;
	}

	/**
	 * get machine-readable task name
	 *
	 * @return task name without non-alphanumerics
	 */
	private String getTrimmedTaskName() {
		return getTaskName().replaceAll("[^A-Za-z0-9]", "");
	}

	/**
	 * Set the name of the task that is being written to File. The names of the
	 * result files are set based on the provided task name.
	 *
	 * @param taskName
	 */
	private void setTaskName(final String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the list of errors.
	 */
	private StringBuilder getErrors() {
		return this.errors;
	}

	/**
	 * @param set the string builder to maintain the list of errors.
	 */
	private void setErrors(final StringBuilder errors) {
		this.errors = errors;
	}

}
