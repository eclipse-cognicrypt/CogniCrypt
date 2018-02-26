/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.jdt.internal.compiler.impl.Constant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.question.QuestionsJSONReader;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.utilities.Utils;

/**
 * @author rajiv
 *
 */

public class FileUtilities {

	private String taskName;	
	private int pageId=0;
	private ArrayList<Question> listOfAllQuestions;
	private List<Page> pages;
	
	public FileUtilities(String taskName) {
		super();
		this.setTaskName(taskName);
	}
	
	private boolean compileCFRFile() {
		// try to compile the Clafer file
		// TODO error handling missing
		String claferFilename = Constants.CFR_FILE_DIRECTORY_PATH + getTrimmedTaskName() + Constants.CFR_EXTENSION;
		return ClaferModel.compile(claferFilename);
	}

	/**
	 * Write the data from the pages to target location in the plugin.
	 * @param claferModel
	 * @param questions
	 * @param xslFileContents
	 * @param customLibLocation
	 */
	public void writeFiles(ClaferModel claferModel, ArrayList<Question> questions, String xslFileContents, File customLibLocation) {
		writeCFRFile(claferModel);
		compileCFRFile();
		try {
			writeJSONFile(questions);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeXSLFile(xslFileContents);
		if (customLibLocation != null) {
			copyFileFromPath(customLibLocation);
		}
	}
	
	/**
	 * Copy the selected files to target location in the plugin.
	 * @param cfrFileLocation
	 * @param jsonFileLocation
	 * @param xslFileLocation
	 * @param customLibLocation
	 */
	public boolean writeFiles(File cfrFileLocation, File jsonFileLocation, File xslFileLocation, File customLibLocation) {
		
		if (validateCFRFile(cfrFileLocation) && validateJSONFile(jsonFileLocation) && validateXSLFile(xslFileLocation)) {

			// custom library location is optional.
			if (customLibLocation != null) {
				if (!validateJARFile(customLibLocation)) {
					return false;
				} else {
					copyFileFromPath(customLibLocation);
				}
			}

			copyFileFromPath(cfrFileLocation);
			copyFileFromPath(jsonFileLocation);

			String cfrFilename = cfrFileLocation.getAbsolutePath();
			String jsFilename = cfrFilename.substring(0, cfrFilename.lastIndexOf(".")) + Constants.JS_EXTENSION;
			copyFileFromPath(new File(jsFilename));

			copyFileFromPath(xslFileLocation);

			return true;
		}

		return false;
	}
	
	/**
	 * Validate the provided JAR file before copying it to the target location.
	 * @param customLibLocation
	 * @return a boolean value for the validity of the file.
	 */
	private boolean validateJARFile(File customLibLocation) {
		
	        ZipFile customLib;
	        boolean validFile = false;
			try {
				customLib = new ZipFile(customLibLocation);
				Enumeration<? extends ZipEntry> e = customLib.entries();
				validFile = true;
		        customLib.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				return false;
			} 
	        
	    return validFile;
		
	}

	/**
	 * Validate the provided XSL file before copying it to the target location.
	 * @param xslFileLocation
	 * @return a boolean value for the validity of the file.
	 */
	private boolean validateXSLFile(File xslFileLocation) {
		try {
			TransformerFactory.newInstance().newTransformer(new StreamSource(xslFileLocation));			
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Validate the provided JSON file before copying it to the target location.
	 * @param jsonFileLocation
	 * @return a boolean value for the validity of the file.
	 */
	private boolean validateJSONFile(File jsonFileLocation) {			
		Gson gson = new Gson();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(jsonFileLocation));
            gson.fromJson(reader, Object.class);
            reader.close();            
            return true;
        } catch (com.google.gson.JsonSyntaxException | IOException ex) {
        	ex.printStackTrace();
            return false;
        }
	}

	/**
	 * Validate the provided CFR file before copying it to the target location.
	 * @param cfrFileLocation
	 * @return a boolean value for the validity of the file.
	 */
	private boolean validateCFRFile(File cfrFileLocation) {
		//return ClaferModel.compile(cfrFileLocation.getAbsolutePath());
		
		//for checking the JSON file creation commented out the above line
		return true;
	}

	/**
	 * Copy the given file to the appropriate location. 
	 * @param existingFileLocation
	 */	
	private void copyFileFromPath(File existingFileLocation) {
		if(existingFileLocation.exists() && !existingFileLocation.isDirectory()) {		
			File targetDirectory = null;
			try {
				
				if(existingFileLocation.getPath().endsWith(Constants.CFR_EXTENSION)) {
					targetDirectory = new File(Utils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH), getTrimmedTaskName() + Constants.CFR_EXTENSION);
				} else if (existingFileLocation.getPath().endsWith(Constants.JS_EXTENSION)) {
					targetDirectory = new File(Utils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH), getTrimmedTaskName() + Constants.JS_EXTENSION);
				} else if(existingFileLocation.getPath().endsWith(Constants.JSON_EXTENSION)) {
					targetDirectory = new File(Utils.getResourceFromWithin(Constants.JSON_FILE_DIRECTORY_PATH), getTrimmedTaskName() + Constants.JSON_EXTENSION);
				} else if(existingFileLocation.getPath().endsWith(Constants.XSL_EXTENSION)) {
					targetDirectory = new File(Utils.getResourceFromWithin(Constants.XSL_FILE_DIRECTORY_PATH), getTrimmedTaskName() + Constants.XSL_EXTENSION);
				} else if(existingFileLocation.getPath().endsWith(Constants.JAR_EXTENSION)) {
					File tempDirectory = new File(Utils.getResourceFromWithin(Constants.JAR_FILE_DIRECTORY_PATH), getTrimmedTaskName() + Constants.innerFileSeparator);
					tempDirectory.mkdir();
					targetDirectory = new File(tempDirectory, getTrimmedTaskName() + Constants.JAR_EXTENSION);
				} else {
					throw new Exception("Unknown file type.");
				}
			
				Files.copy(existingFileLocation.toPath(), targetDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.COPY_ATTRIBUTES);
				
			} catch (Exception e) {				
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Update the task.json file with the new Task.
	 * @param task the Task to be added.
	 */
	public void writeTaskToJSONFile(Task task){
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			reader = new BufferedReader(new FileReader(Utils.getResourceFromWithin(Constants.jsonTaskFile)));
			List<Task> tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {}.getType());	
			// Add the new task to the list.
			tasks.add(task);
			reader.close();
			
			writer = new BufferedWriter(new FileWriter(Utils.getResourceFromWithin(Constants.jsonTaskFile)));			
			gson.toJson(tasks, new TypeToken<List<Task>>() {}.getType(), writer);
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * 
	 * @param claferModel
	 */
	private void writeCFRFile(ClaferModel claferModel) {
		File cfrFile = new File(Utils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH), getTrimmedTaskName() + Constants.CFR_EXTENSION);
		try {
			FileWriter writer = new FileWriter(cfrFile);
			writer.write(claferModel.toString());
			writer.close();
		} catch (IOException e) {
			Activator.getDefault().logError(e);
		}
	}
		
	/**
	 * 
	 * @param questions
	 * 			listOfAllQuestions
	 * @throws IOException 
	 */
	private void writeJSONFile(ArrayList<Question> questions) throws IOException {
		
		SegregatesQuestionsIntoPages pageContent = new SegregatesQuestionsIntoPages(questions);
		ArrayList<Page> pages = pageContent.getPages();
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		File jsonFileTargetDirectory = new File(Utils.getResourceFromWithin(Constants.JSON_FILE_DIRECTORY_PATH), getTaskName() + Constants.JSON_EXTENSION);
		
		//creates the file
		jsonFileTargetDirectory.createNewFile();

		//creates the writer object for json file  
		FileWriter writerForJsonFile = new FileWriter(jsonFileTargetDirectory);

		try{
		//write the data into the .json file  
				writerForJsonFile.write(gson.toJson(pages));
		}
		finally{
		writerForJsonFile.flush();
		writerForJsonFile.close();
		}
	}
	
	/**
	 * 
	 * @param xslFileContents
	 */
	private void writeXSLFile(String xslFileContents) {
		File xslFile = new File(Utils.getResourceFromWithin(Constants.XSL_FILE_DIRECTORY_PATH), getTrimmedTaskName() + Constants.XSL_EXTENSION);
		
		try {
			PrintWriter writer = new PrintWriter(xslFile);
			writer.println(xslFileContents);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*if (!validateXSLFile(xslFile)) {
			xslFile.delete();
			//TODO a better way to handle the exception.			
		}*/
	}
	
	/**
	 * Return the name of that task that is set for the file writes..
	 * @return
	 */
	private String getTaskName() {
		return taskName;
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
	 * 
	 * Set the name of the task that is being written to File. The names of the result files are set based on the provided task name.
	 * @param taskName
	 */
	private void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	
		
	
	

}


