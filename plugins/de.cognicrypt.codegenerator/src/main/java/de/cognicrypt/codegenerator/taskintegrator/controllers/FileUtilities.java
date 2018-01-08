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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.utilities.Utils;

/**
 * @author rajiv
 *
 */

public class FileUtilities {

	private String taskName;	
	
	public FileUtilities(String taskName) {
		super();
		this.setTaskName(taskName);
	}
	
	/**
	 * Write the data from the pages to target location in the plugin.
	 * @param claferFeatures
	 * @param questions
	 * @param xslFileContents
	 * @param customLibLocation
	 */
	public void writeFiles(ArrayList<ClaferFeature> claferFeatures, ArrayList<Question> questions, String xslFileContents, File customLibLocation) {
		writeCFRFile(claferFeatures);
		try {
			writeJSONFile(questions);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeXSLFile(xslFileContents);
		copyFileFromPath(customLibLocation);
	}
	
	/**
	 * Copy the selected files to target location in the plugin.
	 * @param cfrFileLocation
	 * @param jsonFileLocation
	 * @param xslFileLocation
	 * @param customLibLocation
	 */
	public void writeFiles(File cfrFileLocation, File jsonFileLocation, File xslFileLocation, File customLibLocation) {
		
		copyFileFromPath(cfrFileLocation);
		copyFileFromPath(jsonFileLocation);
		copyFileFromPath(xslFileLocation);
		copyFileFromPath(customLibLocation);
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
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
		
	/**
	 * 
	 * @param claferFeatures
	 */
	private void writeCFRFile(ArrayList<ClaferFeature> claferFeatures) {
		
	}
		
	/**
	 * 
	 * @param questions
	 * 			listOfAllQuestions
	 * @throws IOException 
	 */
	private void writeJSONFile(ArrayList<Question> questions) throws IOException {
		System.out.println(questions.size());

		File jsonFileTargetDirectory = new File(Utils.getResourceFromWithin(Constants.JSON_FILE_DIRECTORY_PATH), getTaskName() + Constants.JSON_EXTENSION);
		
		//creates the file
		jsonFileTargetDirectory.createNewFile();
		
		/*
		 * In following StringBuilder object all the informations required for creating the
		 * json file is appended 
		 */
		StringBuilder sb = new StringBuilder();
		
		sb.append(Constants.openSquareBracket + Constants.openCurlyBrace + Constants.lineSeparator);
		
		sb.append(Constants.quotationMark + Constants.taskIDField + Constants.quotationMark + Constants.colonOperator + " " + 
			Constants.quotationMark + Constants.taskIDValue + Constants.quotationMark);
		
		sb.append(Constants.lineSeparator);
		
		sb.append(Constants.quotationMark+Constants.helpIDField+Constants.quotationMark+Constants.colonOperator+" "+
		Constants.quotationMark + taskName + "_Page0"+Constants.quotationMark);
		
		sb.append(Constants.lineSeparator);
	
		sb.append(Constants.quotationMark+Constants.contentFieldName+Constants.quotationMark+Constants.colonOperator+" "+Constants.openSquareBracket);
		
		//Counter used for creating the question json object 
		int moreQuestions=0;
		for(Question question: questions){
			sb.append(Constants.openCurlyBrace+Constants.lineSeparator);
			sb.append(Constants.quotationMark+Constants.qstnIDField+Constants.quotationMark+Constants.colonOperator+" ");
			sb.append(Constants.quotationMark+question.getId()+Constants.quotationMark+Constants.commaOperator+Constants.lineSeparator);
			sb.append(Constants.quotationMark+Constants.elementField+Constants.quotationMark+Constants.colonOperator+" "+
			Constants.quotationMark+question.getQuestionType()+Constants.quotationMark+Constants.commaOperator+Constants.lineSeparator);
			sb.append(Constants.quotationMark+Constants.noteField+Constants.quotationMark+Constants.colonOperator+" "+Constants.quotationMark+" "+Constants.quotationMark
				+Constants.commaOperator+Constants.lineSeparator);
			sb.append(Constants.quotationMark+Constants.qstnTxtField+Constants.quotationMark+Constants.colonOperator+" "+
				Constants.quotationMark+question.getQuestionText()+Constants.quotationMark+Constants.commaOperator+Constants.lineSeparator);
			sb.append(Constants.quotationMark+Constants.answersField+Constants.quotationMark+Constants.colonOperator+" "+
				Constants.openSquareBracket);
			
		//to be use for creating the array of answersdetails as json object
			int i=0;
			for (Answer answer : question.getAnswers()) {
				sb.append(Constants.openCurlyBrace+Constants.lineSeparator);
				sb.append(Constants.quotationMark + Constants.valueField + Constants.quotationMark + Constants.colonOperator + " " + Constants.quotationMark + answer
					.getValue() + Constants.quotationMark);
				//Executes when clafer dependency list is not empty
				int claferCounter=0;
				if (answer.getClaferDependencies() != null) {
					sb.append(Constants.commaOperator + Constants.lineSeparator+Constants.quotationMark + Constants.claferDependenciesField + Constants.quotationMark + Constants.colonOperator + " " + Constants.openSquareBracket );
					for (ClaferDependency cd : answer.getClaferDependencies()) {
						claferCounter++;
						sb.append(Constants.openCurlyBrace + Constants.lineSeparator);
						sb.append(Constants.quotationMark + Constants.algorithmField + Constants.quotationMark + Constants.colonOperator + " " + Constants.quotationMark + cd
							.getAlgorithm() + Constants.quotationMark + Constants.commaOperator + Constants.lineSeparator);
						sb.append(Constants.quotationMark + Constants.operandField + Constants.quotationMark + Constants.colonOperator + " " + Constants.quotationMark + cd
							.getOperand() + Constants.quotationMark + Constants.commaOperator + Constants.lineSeparator);
						sb.append(Constants.quotationMark + Constants.valueField + Constants.quotationMark + Constants.colonOperator + " " + Constants.quotationMark + cd
							.getValue() + Constants.quotationMark + Constants.commaOperator + Constants.lineSeparator);
						sb.append(Constants.quotationMark + Constants.operatorField + Constants.quotationMark + Constants.colonOperator + " " + Constants.quotationMark + cd
							.getOperator() + Constants.quotationMark + Constants.lineSeparator);
						sb.append(Constants.closeCurlyBrace);
						if(answer.getClaferDependencies().size()<claferCounter){
							sb.append(Constants.commaOperator);
						}
					}
					sb.append(Constants.closeSquareBracket );
				}
				
				//Executes when code dependency list is not empty
				int codeCounter=0;
				if(answer.getCodeDependencies()!=null){
					
					sb.append(Constants.commaOperator+Constants.lineSeparator+Constants.quotationMark+Constants.codeDependenciesField+Constants.quotationMark+Constants.colonOperator+" "+
						Constants.openSquareBracket);
					for(CodeDependency cd: answer.getCodeDependencies()){
						codeCounter++;
						sb.append(Constants.openCurlyBrace+Constants.lineSeparator);
						sb.append(Constants.quotationMark+Constants.optionField+Constants.quotationMark+Constants.colonOperator+" "+
						Constants.quotationMark+cd.getOption()+Constants.quotationMark+Constants.commaOperator+Constants.lineSeparator);
					sb.append(Constants.quotationMark+Constants.valueField+Constants.quotationMark+Constants.colonOperator+""+
						Constants.quotationMark+cd.getValue()+Constants.quotationMark+Constants.lineSeparator+Constants.closeCurlyBrace);
					
					if(answer.getCodeDependencies().size()<codeCounter){
						sb.append(Constants.commaOperator);
					}
					}
				sb.append(Constants.closeSquareBracket);
				}
				
				//checks if current answer is default or not
				if(answer.isDefaultAnswer()){
					sb.append(Constants.commaOperator+Constants.lineSeparator+Constants.quotationMark+Constants.defaultAnswerField+Constants.quotationMark+Constants.colonOperator+" "+
						Constants.quotationMark+"true");
				}
				
				//checks if answer is linked to other question
				if(answer.getNextID()!=-2){
				sb.append(Constants.commaOperator+Constants.lineSeparator+Constants.quotationMark+Constants.nextIDField+Constants.quotationMark+Constants.colonOperator+" "+
					Constants.quotationMark+answer.getNextID()+Constants.quotationMark+Constants.lineSeparator);
				}
				sb.append(Constants.closeCurlyBrace);
				i++;
				//checks if more answers are there to be added 
				if(question.getAnswers().size()>i){
					sb.append(Constants.commaOperator+Constants.lineSeparator);
				}
			}
			
			sb.append(Constants.closeSquareBracket+Constants.lineSeparator+Constants.closeCurlyBrace);
			moreQuestions++;
			//checks if there are more questions to be added 
			if(questions.size()>moreQuestions){
				sb.append(Constants.commaOperator+Constants.lineSeparator);
			}
			
		}
		sb.append(Constants.closeSquareBracket+Constants.lineSeparator+Constants.closeCurlyBrace+Constants.closeSquareBracket);
		
		//creates the writer object for json file  
		FileWriter writerForJsonFile = new FileWriter(jsonFileTargetDirectory);
		String jsonData= sb +"";
		
		try{
		//write the data into the .json file  
			writerForJsonFile.write(jsonData);
		}
		finally{
		writerForJsonFile.flush();
		writerForJsonFile.close();
		}

		/*//creates a FileReader object for json file
		FileReader readerForJsonFile = new FileReader(jsonFileTargetDirectory);
		char[] r = new char[10];
		readerForJsonFile.read(r);
		
		for(char a : r ){
			System.out.println(a);
			readerForJsonFile.close();
		}
		System.out.println(questions.size());*/
	}
	
	/**
	 * 
	 * @param xslFileContents
	 */
	private void writeXSLFile(String xslFileContents) {
		
	}
	
	/**
	 * 
	 * @return
	 */
	private String getTaskName() {
		return taskName;
	}
	
	/**
	 * 
	 * @param taskName
	 */
	private void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
