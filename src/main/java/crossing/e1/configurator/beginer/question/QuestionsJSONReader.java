/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package crossing.e1.configurator.beginer.question;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crossing.e1.configurator.Activator;
import crossing.e1.configurator.tasks.Task;
import crossing.e1.configurator.utilities.Utils;

/**
 * This class reads all questions and answers of one task.
 * 
 * @author Sarah Nadi
 * @author Stefan Krueger
 *
 */
public class QuestionsJSONReader {

	/***
	 * This method reads all questions of one task using the file path to the question file.
	 * 
	 * @param filePath
	 *        path to the file that contains all questions for one task.
	 * @return questions
	 */
	public List<Question> getQuestions(final String filePath) {
		List<Question> questions = new ArrayList<Question>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(Utils.getResourceFromWithin(filePath)));
			final Gson gson = new Gson();

			questions = gson.fromJson(reader, new TypeToken<List<Question>>() {}.getType());

			checkReadQuestions(questions);
		} catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
			return null;
		}
		return questions;
	}
	
	
	/***
	 * This method reads all pages of one task using the file path to the JSON file.
	 * 
	 * @param filePath 
	 * 			Path to the file that contains all questions for one task.
	 * @return pages 
	 * 			Return a list of all the pages in the JSON file.
	 */
	public List<Page> getPages(final String filePath) {
		List<Page> pages = new ArrayList<Page>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(Utils.getResourceFromWithin(filePath)));
			final Gson gson = new Gson();

			pages = gson.fromJson(reader, new TypeToken<List<Page>>() {}.getType());

			checkReadPages(pages);
		} catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		return pages;
	}

	/***
	 * This method reads all questions of one task.
	 * 
	 * @param task
	 *        task whose questions should be read
	 * @return Questions
	 */
	public List<Question> getQuestions(final Task task) {
		return getQuestions(task.getXmlFile());
	}
	
	/***
	 * This method reads all pages of one task.
	 * 
	 * @param task
	 *        task whose questions should be read
	 * @return Pages
	 */
	public List<Page> getPages(final Task task) {
		return getPages(task.getXmlFile());
	}
	
	/**
	 * Check the validity of the pages and the questions contained in them.
	 * @param pages
	 */
	private void checkReadPages(List<Page> pages) {
		final Set<Integer> ids = new HashSet<>();
		if (pages.size() < 1) {
			throw new IllegalArgumentException("There are no pages for this task.");
		}
		for (final Page page : pages) {
			if (!ids.add(page.getId())) {
				throw new IllegalArgumentException("Each page must have a unique ID.");
			}
			
			// Check the validity of questions for each page.
			checkReadQuestions(page.getContent());
		}
	}

	private void checkReadQuestions(List<Question> questions) {
		final Set<Integer> ids = new HashSet<>();
		if (questions.size() < 1) {
			throw new IllegalArgumentException("There are no questions for this task.");
		}
		for (final Question question : questions) {
			if (!ids.add(question.getId())) {
				throw new IllegalArgumentException("Each question must have a unique ID.");
			}

			if (question.getDefaultAnswer() == null) {
				throw new IllegalArgumentException("Each question must have a default answer.");
			}
			/* TODO : Look for a better alternative to commenting this check. I want to maintain the legacy code as 
			 * it is. Current implementation does not follow this.			

			for (final Answer answer : question.getAnswers()) {
				
				if (answer.getNextID() == -2) {
					throw new IllegalArgumentException("Each answer must point to the following question.");
				}
			}*/
		}
	}
}
