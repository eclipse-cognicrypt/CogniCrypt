/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.wizard.questionnaire;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.primitive.types.Primitive;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;

public class PrimitiveQuestionsJSONReader {

	/***
	 * This method reads all questions of one primitive using the file path to the question file.
	 * 
	 * @param filePath
	 *        path to the file that contains all questions for one primitive.
	 * @return questions
	 */
	public List<Question> getQuestions(final String filePath) {
		List<Question> questions = new ArrayList<Question>();
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

	/***
	 * This method reads all pages of one primitive using the file path to the JSON file.
	 * 
	 * @param filePath
	 *        Path to the file that contains all questions for one primitive.
	 * @return pages Return a list of all the pages in the JSON file.
	 */
	public List<Page> getPages(final String filePath) {
		List<Page> pages = new ArrayList<Page>();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(CodeGenUtils.getResourceFromWithin(filePath)));
			final Gson gson = new Gson();

			pages = gson.fromJson(reader, new TypeToken<List<Page>>() {}.getType());

			checkReadPages(pages);
			checkNextIDs(pages);
		} catch (final FileNotFoundException e) {
			Activator.getDefault().logError(e);
		}
		return pages;
	}

	/***
	 * This method reads all questions of one primitive.
	 * 
	 * @param primitive
	 *        primitive whose questions should be read
	 * @return Questions
	 */
	public List<Question> getQuestions(final Primitive primitive) {
		return getQuestions(primitive.getXmlFile());
	}

	/***
	 * This method reads all pages of one primitive.
	 * 
	 * @param primitive
	 *        primitive whose questions should be read
	 * @return Pages
	 */
	public List<Page> getPages(final Primitive primitive) {
		return getPages(primitive.getXmlFile());
	}

	/**
	 * Check the validity of the pages and the questions contained in them.
	 * 
	 * @param pages
	 */
	private void checkReadPages(List<Page> pages) {
		final Set<Integer> ids = new HashSet<>();
		if (pages.size() < 1) {
			throw new IllegalArgumentException("There are no pages for this primitive.");
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
			throw new IllegalArgumentException("There are no questions for this primitive.");
		}
		for (final Question question : questions) {
			if (!ids.add(question.getId())) {
				throw new IllegalArgumentException("Each question must have a unique ID.");
			}

			//			if (question.getDefaultAnswer() == null) {
			//				throw new IllegalArgumentException("Each question must have a default answer.");
			//			}
		}
	}

	private void checkNextIDs(List<Page> pages) {
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
