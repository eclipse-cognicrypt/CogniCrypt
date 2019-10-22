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

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.FileHelper;

/**
 * This class is a storage for the configuration chosen by the user.
 *
 * @author Stefan Krueger
 */
public abstract class Configuration {

	final protected Map<Question, Answer> options;
	final protected String pathOnDisk;

	public Configuration(Map<?, ?> constraints, String pathOnDisk) {
		this.pathOnDisk = pathOnDisk;
		this.options = (Map<Question, Answer>) constraints;
		String path = this.pathOnDisk.substring(0,this.pathOnDisk.lastIndexOf("/")) + "/" +Constants.pathToInstanceFile;
		
//		System.out.println("HERE IS THE OPTION ON DISK:......  " + this.options.values());
		System.out.println("path issssssss: " + path);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();  
		XMLEncoder xmlEncoder = new XMLEncoder(bos);  
		xmlEncoder.writeObject(this.options);  
		xmlEncoder.flush();  

		String serializedMap = bos.toString(); 
//		System.out.println("lets see IF IT WOOORKSSS:  " + serializedMap);  
//		File fiif = new File(serializedMap, path);
//		System.out.println("FILE HAS BEEN CREATED");
		final OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer;
		try {
			writer = new XMLWriter(new FileWriter(this.pathOnDisk), format);
			writer.write(serializedMap);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serializedMap = null;

	
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
