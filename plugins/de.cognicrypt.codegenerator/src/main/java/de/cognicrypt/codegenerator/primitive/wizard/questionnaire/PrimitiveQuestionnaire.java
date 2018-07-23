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

import java.util.List;

import de.cognicrypt.codegenerator.primitive.types.Primitive;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;

public class PrimitiveQuestionnaire {

	private final List<Question> questionList;
	private final List<Page> pageList;
	private Primitive primitive;
	private int pageID;

	/**
	 * 
	 * @param task
	 * @param filePath
	 */
	public PrimitiveQuestionnaire(final Primitive primitive, final String filePath) {
		this.primitive = primitive;
		this.pageList = (new PrimitiveQuestionsJSONReader()).getPages(filePath);
		this.pageID = 0;

		this.questionList = null;
	}

	/**
	 * Added this method to get specific questions. This functionality was created to replace the code when handling buttons as 'Questions'.
	 * 
	 * @param questionID
	 * @return The requested question.
	 */
	public Question getQuestionByID(final int questionID) {

		for (Page page : pageList) {
			for (Question question : page.getContent()) {
				if (question.getId() == questionID) {
					return question;
				}
			}
		}
		return null;
	}

	public List<Page> getQuestionnaire() throws NullPointerException {
		return this.pageList;
	}

	public List<Question> getQuestionList() {
		return this.questionList;
	}

	public Primitive getPrimitive() {
		return this.primitive;
	}

	public void setPrimitive(final Primitive primitive) {
		this.primitive = primitive;
	}

	/**
	 * 
	 * @param pageID
	 * @return Return the page at pageID.
	 */
	public Page getPageByID(final int pageID) {
		return this.pageList.get(pageID);
	}

	/**
	 * 
	 * @return Return the list of pages.
	 * @throws NullPointerException
	 */
	public List<Page> getPages() throws NullPointerException {
		return this.pageList;
	}

	/**
	 * 
	 * @return Return the next page.
	 */
	public Page nextPage() {
		return this.pageList.get(this.pageID++);
	}

	/**
	 * 
	 * @return Return the previous page.
	 */
	public Page previousPage() {
		return this.pageList.get(--this.pageID);
	}

	/**
	 * 
	 * @param pageID
	 * @return Return the page that has been set.
	 */
	public Page setPageByID(final int pageID) {
		this.pageID = pageID;
		return this.pageList.get(this.pageID);
	}

	/**
	 * 
	 * @return Whether this is the first page.
	 */
	public boolean isFirstPage() {
		return this.pageID == 0;
	}

	/**
	 * 
	 * @return Return whether there are more pages.
	 */
	public boolean hasMorePages() {
		return this.pageID < getPages().size();
	}

	/**
	 * 
	 * @return Return the current pageID.
	 */
	public int getCurrentPageID() {
		return this.pageID;
	}

}
