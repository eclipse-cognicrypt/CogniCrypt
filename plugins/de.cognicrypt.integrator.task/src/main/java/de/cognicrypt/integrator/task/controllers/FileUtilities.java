/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 *
 */
package de.cognicrypt.integrator.task.controllers;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.widgets.TaskInformationComposite;
import de.cognicrypt.utils.Utils;

public class FileUtilities {

	private StringBuilder errors; // Maintain all the errors to display them on the wizard.
	IntegratorModel integratorModel;
	
	/**
	 * The class needs to be initialized with a task name, as it is used extensively
	 * in the methods.
	 *
	 * @param taskName
	 */
	public FileUtilities() {
		super();
		setErrors(new StringBuilder());
		integratorModel = IntegratorModel.getInstance();
	}
	
	public String writeData() {
		
		final HashMap<String, File> cryslTemplateFile = integratorModel.getCryslTemplateFiles();
		
		copyImage(integratorModel.getIconFile());
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

	public String writeDataNonGuidedMode() {
		
		final HashMap<String, File> cryslTemplateFile = integratorModel.getCryslTemplateFiles();
		
		copyImage(integratorModel.getIconFile());
		copyJSON(integratorModel.getJSONFile());
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
	

	
	
	private void copyTemplate(final File existingFileLocation, String option) throws IOException {
		File parentFolder1 = new File(Constants.ECLIPSE_LOC_TEMP_DIR);
		File parentFolder2 = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + integratorModel.getTaskName() + "/template");
		File templateFolder1 = new File(parentFolder1, integratorModel.getTrimmedTaskName() + option);
		File templateFolder2 = new File(parentFolder2, integratorModel.getTrimmedTaskName() + option);
		
		if (!templateFolder1.isDirectory()) {
			templateFolder1.mkdir();
		}
		if (!templateFolder2.isDirectory()) {
		templateFolder2.mkdir();
		}

		File targetDirectory1 = new File(templateFolder1, integratorModel.getTrimmedTaskName() + Constants.JAVA_EXTENSION);
		File targetDirectory2 = new File(templateFolder2, integratorModel.getTrimmedTaskName() + Constants.JAVA_EXTENSION);

		
		Path path1 = existingFileLocation.toPath();
		Path path2 = targetDirectory1.toPath();
		Path path3 = targetDirectory2.toPath();
		
		Activator.getDefault().logError("Copy " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory1.getAbsolutePath());
			
		Files.copy(path1, path2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used by the code generator
		
		Files.copy(path1, path3, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used to make the exportable ZIP	
	}
	
	/**
	 * Copy the image file to the appropriate location.
	 *
	 * @param existingFileLocation
	 */
	private void copyImage(final File existingFileLocation) {
			File targetDirectory = null;
			File targetDirectory2 = null;
			try {
				if (existingFileLocation.getPath().endsWith(Constants.PNG_EXTENSION)) {
					targetDirectory = new File(Constants.ECLIPSE_LOC_IMG_DIR, IntegratorModel.getInstance().getTrimmedTaskName() + Constants.PNG_EXTENSION);
					targetDirectory2 = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + IntegratorModel.getInstance().getTaskName() + "/res", IntegratorModel.getInstance().getTrimmedTaskName() + Constants.PNG_EXTENSION);
				} else {
					throw new Exception("Unknown file type.");
				}
				Activator.getDefault().logError("CopyNonCustom " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
				Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used by the code generator
				Files.copy(existingFileLocation.toPath(), targetDirectory2.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used to make the exportable ZIP

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
	private void copyJSON(final File existingFileLocation) {
		File targetDirectory = null;
		File targetDirectory2 = null;
		try {
			if (existingFileLocation.getPath().endsWith(Constants.JSON_EXTENSION)) {
				targetDirectory = new File(Constants.ECLIPSE_LOC_TASKDESC_DIR, IntegratorModel.getInstance().getTrimmedTaskName() + Constants.JSON_EXTENSION);
				targetDirectory2 = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + IntegratorModel.getInstance().getTaskName() + "/res", IntegratorModel.getInstance().getTrimmedTaskName() + Constants.JSON_EXTENSION);
			} else {
				throw new Exception("Unknown file type.");
			}
			Activator.getDefault().logError("CopyNonCustom " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
			Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used by the code generator	
			Files.copy(existingFileLocation.toPath(), targetDirectory2.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used to make the exportable ZIP	

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
					new FileReader(new File(Constants.customjsonTaskFile)));
			final List<Task> tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {
			}.getType());
			// Add the new task to the list.
			tasks.add(task);
			reader.close();

			writer = new BufferedWriter(
					new FileWriter(new File(Constants.customjsonTaskFile)));
			gson.toJson(tasks, new TypeToken<List<Task>>() {
			}.getType(), writer);
			writer.close();
			
			writer = new BufferedWriter(
					new FileWriter(new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + IntegratorModel.getInstance().getTaskName() + "/res/task.json")));
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
				IntegratorModel.getInstance().getTrimmedTaskName() + Constants.JSON_EXTENSION);
		
		final File jsonFile2 = new File(
				Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + IntegratorModel.getInstance().getTaskName() + "/res",
				IntegratorModel.getInstance().getTrimmedTaskName() + Constants.JSON_EXTENSION);

		try {
			final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
			// creates the file
			jsonFile.createNewFile();

			// creates the writer object for json file
			final FileWriter writerForJsonFile = new FileWriter(jsonFile);

			// write the data into the .json file
			writerForJsonFile.write(gson.toJson(pages));
			writerForJsonFile.close();
			
			final FileWriter writerForJsonFile2 = new FileWriter(jsonFile2);

			// write the data into the .json file
			writerForJsonFile2.write(gson.toJson(pages));
			writerForJsonFile2.close();
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

	public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}

	public static void unzipFile() {
		String fileZip = IntegratorModel.getInstance().getImportFile().toString();
		File destDir = new File(Constants.ECLIPSE_LOC_EXPORT_DIR);
		byte[] buffer = new byte[1024];
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				File newFile = newFile(destDir, zipEntry);
				if (zipEntry.isDirectory()) {
					if (!newFile.isDirectory() && !newFile.mkdirs()) {
						throw new IOException("Failed to create directory " + newFile);
					}
				} else {
					// fix for Windows-created archives
					File parent = newFile.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("Failed to create directory " + parent);
					}

					// write file content
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	public static boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}
	
	
}
