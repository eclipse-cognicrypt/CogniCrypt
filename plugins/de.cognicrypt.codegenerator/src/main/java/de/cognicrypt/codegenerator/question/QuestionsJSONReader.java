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

package de.cognicrypt.codegenerator.question;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;

/**
 * This class reads all questions and answers of one task.
 *
 * @author Sarah Nadi
 * @author Stefan Krueger
 */
public class QuestionsJSONReader {

	/**
	 * This method reads all questions of one task using the file path to the question file.
	 *
	 * @param filePath
	 *        path to the file that contains all questions for one task.
	 * @return questions
	 */
	public List<Question> getQuestions(final String filePath) {
		List<Question> questions = new ArrayList<>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(CodeGenUtils.getResourceFromWithin(filePath)));
			final Gson gson = new Gson();
			questions = gson.fromJson(reader, new TypeToken<List<Question>>() {}.getType());
			checkReadQuestions(questions);
		} catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
			return null;
		}
		return questions;
	}

	/**
	 * This method reads all pages of one task using the file path to the JSON file.
	 *
	 * @param filePath
	 *        Path to the file that contains all questions for one task.
	 * @return pages Return a list of all the pages in the JSON file.
	 */
	public List<Page> getPages(final String filePath) {
		List<Page> pages = new ArrayList<>();
		try {
			Activator.getDefault().logError("JSON File " + filePath);
			final BufferedReader reader = new BufferedReader(new FileReader(CodeGenUtils.getResourceFromWithin(filePath)));
			final Gson gson = new Gson();

			pages = gson.fromJson(reader, new TypeToken<List<Page>>() {}.getType());
			// For some tasks, we don't have questions. So, we don't need to check them.
			if (pages.size() > 0) {
				checkReadPages(pages);
				checkNextIDs(pages);
			}
		} catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		return pages;
	}
	
	/**
	 * This method reads all questions of one task using the file path to the question file.
	 *
	 * @param filePath
	 *        path to the file that contains all questions for one task.
	 * @return questions
	 */
	public List<Question> getCustomQuestions(final String filePath) {
		List<Question> questions = new ArrayList<>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.ECLIPSE_CogniCrypt_RESOURCE_DIR + Constants.innerFileSeparator + filePath)));
			final Gson gson = new Gson();
			questions = gson.fromJson(reader, new TypeToken<List<Question>>() {}.getType());
			checkReadQuestions(questions);
		} catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
			return null;
		}
		return questions;
	}

	/**
	 * This method reads all pages of one task using the file path to the JSON file.
	 *
	 * @param filePath
	 *        Path to the file that contains all questions for one task.
	 * @return pages Return a list of all the pages in the JSON file.
	 */
	public List<Page> getCustomPages(final String filePath) {
		List<Page> pages = new ArrayList<>();
		try {
			Activator.getDefault().logError("JSON File " + filePath);
			final BufferedReader reader = new BufferedReader(new FileReader(new File(Constants.ECLIPSE_CogniCrypt_RESOURCE_DIR + Constants.innerFileSeparator + filePath)));
			final Gson gson = new Gson();

			pages = gson.fromJson(reader, new TypeToken<List<Page>>() {}.getType());
			// For some tasks, we don't have questions. So, we don't need to check them.
			if (pages.size() > 0) {
				checkReadPages(pages);
				checkNextIDs(pages);
			}
		} catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		return pages;
	}


	/**
	 * This method reads all questions of one task.
	 *
	 * @param task
	 *        task whose questions should be read
	 * @return Questions
	 */
	public List<Question> getQuestions(final Task task) {
		return getQuestions(task.getQuestionsJSONFile());
	}
	
	/**
	 * This method reads all custom questions of one task.
	 *
	 * @param task
	 *        task whose questions should be read
	 * @return Questions
	 */
	public List<Question> getCustomQuestions(final Task task) {
		return getCustomQuestions(task.getQuestionsJSONFile());
	}


	/**
	 * This method reads all pages of one task.
	 *
	 * @param task
	 *        task whose questions should be read
	 * @return Pages
	 */
	public List<Page> getPages(final Task task) {
		return getPages(task.getQuestionsJSONFile());
	}
	
	/**
	 * This method reads all custom pages of one task.
	 *
	 * @param task
	 *        task whose questions should be read
	 * @return Pages
	 */
	public List<Page> getCustomPages(final Task task) {
		return getCustomPages(task.getQuestionsJSONFile());
	}

	/**
	 * Check the validity of the pages and the questions contained in them.
	 *
	 * @param pages
	 *        List of all read pages
	 */
	public void checkReadPages(final List<Page> pages) {
		final Set<Integer> ids = new HashSet<>();
		if (pages.size() < 0) {
			throw new IllegalArgumentException("There are a negative number of pages for this task.");
		}
		// A BeginnerQuestionaire is now allowed to have no questions, e.g., password task.
		// As no questions exists, we can simply return at this point as nothing need to be checked.
		if (pages.size() == 0) {
			return;
		}
		for (final Page page : pages) {
			if (!ids.add(page.getId())) {
				throw new IllegalArgumentException("Each page must have a unique ID.");
			}

			// Check the validity of questions for each page.
			checkReadQuestions(page.getContent());
		}
	}

	private void checkReadQuestions(final List<Question> questions) {
		final Set<Integer> ids = new HashSet<>();
		if (questions.size() > 1) {
			for (final Question question : questions) {
				if (!ids.add(question.getId())) {
					throw new IllegalArgumentException("Each question must have a unique ID.");
				}

				if (question.getDefaultAnswer() == null) {
					throw new IllegalArgumentException("Each question must have a default answer.");
				}
			}
		}
	}

	private void checkNextIDs(final List<Page> pages) {
		for (final Page page : pages) {
			if (page.getNextID() == -2) {
				for (final Question question : page.getContent()) {
					for (final Answer answer : question.getAnswers()) {
						if (answer.getNextID() == -2) {
							throw new IllegalArgumentException("Each answer must point to the following question if the page does not have a nextID.");
						}
					}
				}
			}
		}
	}

}
