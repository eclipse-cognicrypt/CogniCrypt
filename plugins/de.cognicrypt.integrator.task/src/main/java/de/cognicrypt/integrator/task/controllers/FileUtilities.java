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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.Activator;
import de.cognicrypt.integrator.task.models.IntegratorModel;

/**
 * This class is used to copy the necessary files for Task Integration to the correct destinations
 * where the Code Generator can use them
 * 
 */
public class FileUtilities {

	private StringBuilder errors; // Maintain all the errors to display them on the wizard
	IntegratorModel integratorModel;
	
	/**
	 * Set local attributes and build Directory Structure for custom tasks if it doesn't exist
	 */
	public FileUtilities() {
		super();
		errors = new StringBuilder();
		integratorModel = IntegratorModel.getInstance();
		
		File ressourceFolder = new File(Constants.ECLIPSE_CogniCrypt_RESOURCE_DIR);

		if (!ressourceFolder.exists()) {
			// make resource directory for Code Generation Templates if it doesn't exist
			ressourceFolder.mkdirs();
			initLocalResourceDir(); // initialize needed sub-directories
		}
	}
	
	/**
	 * Creates the local resource directory for custom tasks and its subdirectories
	 */
	public void initLocalResourceDir() {
		File resourceCCTemp = new File(Constants.ECLIPSE_LOC_TEMP_DIR); 
		File resourceCCres = new File(Constants.ECLIPSE_LOC_RES_DIR);
		
		resourceCCTemp.mkdir(); // make local directory for Code Generation Templates
		resourceCCres.mkdir();  //// make local directory for Resources for Code Generation Templates
		
		File resourceCCaddres = new File(Constants.ECLIPSE_LOC_ADDRES_DIR);
		File resourceCCcla = new File(Constants.ECLIPSE_LOC_CLA_DIR);
		File resourceCCimg = new File(Constants.ECLIPSE_LOC_IMG_DIR);
		File resourceCCtaskdesc = new File(Constants.ECLIPSE_LOC_TASKDESC_DIR);
		File resourceCCtasks = new File(Constants.ECLIPSE_LOC_TASKS_DIR);
		File resourceCCXSL = new File(Constants.ECLIPSE_LOC_XSL_DIR);
		File resourceCCtasksjson = new File(Constants.customjsonTaskFile);
		File resourceExport = new File(Constants.ECLIPSE_LOC_EXPORT_DIR);
		
		resourceCCaddres.mkdir();
		resourceCCcla.mkdir();
		resourceCCimg.mkdir();
		resourceCCtaskdesc.mkdir();
		resourceCCtasks.mkdir();
		resourceCCXSL.mkdir();
		resourceExport.mkdir();
		try {
			resourceCCtasksjson.createNewFile();
			FileWriter fileWriter = new FileWriter(resourceCCtasksjson);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			writer.write("[]");
			writer.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * copy given Template Files and given Image File to local resource directory for custom tasks 
	 * (only used in Guided Mode Integration)
	 * @return String with the error messages ("" if no errors happend)
	 */
	public String writeData() {
		
		final HashMap<String, File> cryslTemplateFile = integratorModel.getCryslTemplateFiles();
		
		copyImage(integratorModel.getIconFile());
		for (String key : cryslTemplateFile.keySet()) {
			try {
				copyTemplate(cryslTemplateFile.get(key), key);
			} catch (IOException e) {
				errors.append("There was a problem copying file " + cryslTemplateFile.get(key).toString() + "\n");
			}
		}
		return errors.toString();
	}

	/**
	 * copy given Template Files, given Image File and given QuestionJSONFile to local resource directory for custom tasks 
	 * (only used in Non-Guided Mode Integration)
	 * @return String with the error messages ("" if no erros happend)
	 */
	public String writeDataNonGuidedMode() {
		
		final HashMap<String, File> cryslTemplateFile = integratorModel.getCryslTemplateFiles();
		
		copyImage(integratorModel.getIconFile());
		copyJSON(integratorModel.getJSONFile());
		for (String key : cryslTemplateFile.keySet()) {
			try {
				copyTemplate(cryslTemplateFile.get(key), key);
			} catch (IOException e) {
				errors.append(Constants.ERROR_FILE_COPY + cryslTemplateFile.get(key).toString() + "\n");
			}
		}
		return errors.toString();
	}
	

	
	/**
	 * Copy the template file to the appropriate location for code generator + exportable zip.
	 * @param existingFileLocation one of the existing template files choosen by the user
	 * @param option identifier for given template file
	 * @throws IOException
	 */
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
	 * Copy the image file to the appropriate location for code generator + exportable zip.
	 * @param existingFileLocation the existing image file choosen by the user
	 */
	private void copyImage(final File existingFileLocation) {
			File targetDirectory = null;
			File targetDirectory2 = null;
			try {
				if (existingFileLocation.getPath().endsWith(Constants.PNG_EXTENSION)) {
					targetDirectory = new File(Constants.ECLIPSE_LOC_IMG_DIR, integratorModel.getTrimmedTaskName() + Constants.PNG_EXTENSION);
					targetDirectory2 = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + integratorModel.getTaskName() + "/res", integratorModel.getTrimmedTaskName() + Constants.PNG_EXTENSION);
				} else {
					throw new Exception(Constants.ERROR_UNKNOWN_FILE_TYPE);
				}
				Activator.getDefault().logError("CopyNonCustom " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
				Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used by the code generator
				Files.copy(existingFileLocation.toPath(), targetDirectory2.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used to make the exportable ZIP

			} catch (final Exception e) {
				Activator.getDefault().logError(e);
				errors.append(Constants.ERROR_FILE_COPY + existingFileLocation.getName() + "\n");
			}
	}

	/**
	 * Copy the questionJSON file to the appropriate location for code generator + exportable zip.
	 * @param existingFileLocation the existing questionJSON file choosen by the user (only Non-Guided Mode)
	 */
	private void copyJSON(final File existingFileLocation) {
		File targetDirectory = null;
		File targetDirectory2 = null;
		try {
			if (existingFileLocation.getPath().endsWith(Constants.JSON_EXTENSION)) {
				targetDirectory = new File(Constants.ECLIPSE_LOC_TASKDESC_DIR, integratorModel.getTrimmedTaskName() + Constants.JSON_EXTENSION);
				targetDirectory2 = new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + integratorModel.getTaskName() + "/res", integratorModel.getTrimmedTaskName() + Constants.JSON_EXTENSION);
			} else {
				throw new Exception(Constants.ERROR_UNKNOWN_FILE_TYPE);
			}
			Activator.getDefault().logError("CopyNonCustom " + existingFileLocation.getAbsolutePath() + " to " + targetDirectory.getAbsolutePath());
			Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used by the code generator	
			Files.copy(existingFileLocation.toPath(), targetDirectory2.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES); //copy to folder structure which is used to make the exportable ZIP	

		} catch (final Exception e) {
			Activator.getDefault().logError(e);
			errors.append(Constants.ERROR_FILE_COPY + existingFileLocation.getName() + "\n");
		}
	}

	

	/**
	 * Update the task.json file with the new Task in the local resource directory for custom tasks and 
	 * write it to appropriate location for the exportable ZIP
	 *
	 * @param task the Task to be added.
	 */
	public void writeTaskToJSONFile(final Task task) {
		BufferedReader reader;
		BufferedWriter writer;
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
					new FileWriter(new File(Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + integratorModel.getTaskName() + "/res/task.json")));
			gson.toJson(tasks, new TypeToken<List<Task>>() {
			}.getType(), writer);
			writer.close();

		} catch (final IOException e) {
			Activator.getDefault().logError(e);
			errors.append(Constants.ERROR_TASK_UPDATE);
		}
	}

	/**
	 * Build the questionJSON file to the appropriate location for code generator + exportable zip.
	 * @param questions
	 */
	public void writeJSONFile(final List<Question> questions) {

		final SegregatesQuestionsIntoPages pageContent = new SegregatesQuestionsIntoPages();
		final List<Page> pages = pageContent.getPages();

		final File jsonFile = new File(
				Constants.ECLIPSE_LOC_TASKDESC_DIR,
				integratorModel.getTrimmedTaskName() + Constants.JSON_EXTENSION);
		
		final File jsonFile2 = new File(
				Constants.ECLIPSE_LOC_EXPORT_DIR + "/" + integratorModel.getTaskName() + "/res",
				integratorModel.getTrimmedTaskName() + Constants.JSON_EXTENSION);

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
			FileReader fileReader = new FileReader(jsonFileLocation);
			final BufferedReader reader = new BufferedReader(fileReader);
			gson.fromJson(reader, Object.class);
			reader.close();
			fileReader.close();
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
		errors.append("The contents of the file ");
		errors.append(fileName);
		errors.append(" are invalid.");
		errors.append("\n");
	}
	
	/**
	 * convert a given File/Directory to a ZIP File (Used for creating the exportable ZIP in (Non-)Guided Mode) 
	 * @param fileToZip 
	 * @param fileName
	 * @param zipOut
	 * @throws IOException
	 */
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

	/**
	 * unzip the exportable ZIP file choosen by the user 
	 */
	public static void unzipFile(String zipFile, File destDir) {
		byte[] buffer = new byte[1024];
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				File newFile = newFile(destDir, zipEntry);
				if (zipEntry.isDirectory()) {
					if (!newFile.isDirectory() && !newFile.mkdirs()) {
						throw new IOException(Constants.ERROR_DIRECTORY_CREATION + newFile);
					}
				} else {
					// fix for Windows-created archives
					File parent = newFile.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException(Constants.ERROR_DIRECTORY_CREATION + parent);
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

	/**
	 * helper function for unzipFile
	 * @param destinationDir
	 * @param zipEntry
	 * @return
	 * @throws IOException
	 */
	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException(Constants.ERROR_ENTRY_OUTSIDE_TARGETDIR + zipEntry.getName());
		}

		return destFile;
	}

	/**
	 * delete the given directory (used for clean up in the local ExportableTask directory)
	 * @param directoryToBeDeleted
	 * @return true if direcotry succesfully deleted
	 */
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
