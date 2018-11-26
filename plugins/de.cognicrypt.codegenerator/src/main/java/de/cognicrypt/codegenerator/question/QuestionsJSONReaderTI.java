/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.question;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;

public class QuestionsJSONReaderTI {

	/**
	 * This method reads all questions of the file
	 * 
	 * @param filePath
	 *        the path of the file
	 * @return list of all questions contained in the file
	 */
	public ArrayList<Question> readQuestionsFromFile(String filePath) {
		ArrayList<Question> originalQuestionList = new ArrayList<>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(CodeGenUtils.getResourceFromWithin(filePath)));
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			originalQuestionList = gson.fromJson(reader, new TypeToken<ArrayList<Question>>() {}.getType());
		} catch (FileNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		return originalQuestionList;
	}

	/**
	 * This method reads all pages of the file
	 * 
	 * @param filePath
	 *        the path of the file
	 * @return list of all pages contained in the file
	 */
	public ArrayList<Page> readPageFromFile(String filePath) {
		ArrayList<Page> originalPageList = new ArrayList<>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(CodeGenUtils.getResourceFromWithin(filePath)));
			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			originalPageList = gson.fromJson(reader, new TypeToken<ArrayList<Page>>() {}.getType());
		} catch (FileNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		return originalPageList;
	}

}
