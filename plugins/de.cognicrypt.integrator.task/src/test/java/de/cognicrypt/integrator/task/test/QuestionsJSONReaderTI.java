/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.integrator.task.Activator;

public class QuestionsJSONReaderTI {

	/**
	 * This method reads all questions of the file
	 *
	 * @param filePath the path of the file
	 * @return list of all questions contained in the file
	 */
	public ArrayList<Question> readQuestionsFromFile(final String filePath) {
		ArrayList<Question> originalQuestionList = new ArrayList<>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(CodeGenUtils.getResourceFromWithin(filePath)));
			final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			originalQuestionList = gson.fromJson(reader, new TypeToken<ArrayList<Question>>() {}.getType());
		}
		catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		return originalQuestionList;
	}

	/**
	 * This method reads all pages of the file
	 *
	 * @param filePath the path of the file
	 * @return list of all pages contained in the file
	 */
	public ArrayList<Page> readPageFromFile(final String filePath) {
		ArrayList<Page> originalPageList = new ArrayList<>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(CodeGenUtils.getResourceFromWithin(filePath)));
			final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			originalPageList = gson.fromJson(reader, new TypeToken<ArrayList<Page>>() {}.getType());
		}
		catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		return originalPageList;
	}

}
