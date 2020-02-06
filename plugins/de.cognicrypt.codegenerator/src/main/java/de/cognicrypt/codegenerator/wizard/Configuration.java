/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IProject;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.FileHelper;
import de.cognicrypt.utils.Utils;

import org.json.simple.JSONObject;
/**
 * This class is a storage for the configuration chosen by the user.
 *
 * @author Stefan Krueger
 */
public abstract class Configuration {

	final protected Map<Question, Answer> options;
	final protected String pathOnDisk;
	protected Answer answr;
	final protected String taskName;

	@SuppressWarnings("unchecked")
	public Configuration(Map<?, ?> constraints, String pathOnDisk, String taskName) throws IOException {

		this.answr = new Answer();
		this.pathOnDisk = pathOnDisk;
		this.options = (Map<Question, Answer>) constraints;
		this.taskName = taskName;
		
		JSONObject obj = new JSONObject();
		
		int m = 1;
        for (Question i: this.options.keySet()) {
//        	System.out.println("finaally the msg--------" + i.getQuestionText());
        	obj.put("question" + m , i.getQuestionText());
        	m += 1;
        }
        m = 1;
        for (Answer j: this.options.values()) {
//        	System.out.println("finaally the msg answer--------" + j.getValue());
//        	answr = j;
        	obj.put("answer" + m,  j.getValue());
        	m +=1 ;
        	
        }

		String path2 = Utils.getCurrentProject().getLocation().toOSString() + "/" +Constants.pathToInstanceFile + taskName + ".json";

		File file=new File(path2);  
        file.createNewFile();  
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(obj.toJSONString());  
        fileWriter.flush();  
        fileWriter.close();
         
//	    XMLEncoder XMLencoder;
//		try {
//			XMLencoder = new XMLEncoder(
//			       new BufferedOutputStream(
//			           new FileOutputStream(path)));
//			XMLencoder.writeObject(this.options);
//			XMLencoder.close();
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	
	}

	/**
	 * Writes chosen configuration to hard disk.
	 *
	 * @return Written file.
	 * @throws IOException
	 *         see {@link FileWriter#FileWriter(String)) FileWriter} and {@link XMLWriter#write(String) XMLWriter.write()}
	 */
	public abstract File persistConf() throws IOException;

	public abstract List<String> getProviders();

	public String getPath() {
		return pathOnDisk;
	}

	/**
	 * Deletes config file from hard disk.
	 */
	public void deleteConfFromDisk() {
		FileHelper.deleteFile(this.pathOnDisk);
	}
}
