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

import java.util.ArrayList;

import de.cognicrypt.core.Constants;

public class Page {

	private int id;
	private ArrayList<Question> content;
	private int nextID = Constants.QUESTION_PAGE_NO_STATIC_NEXT_PAGE_ID;
	private String helpID = "";

	public int getId() {
		return this.id;
	}

	public ArrayList<Question> getContent() {
		return this.content;
	}

	public int getNextID() {
		return this.nextID;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setContent(final ArrayList<Question> content) {
		this.content = content;
	}

	public void setNextID(final int nextID) {
		this.nextID = nextID;
	}

	public void setHelpID(final String helpID) {
		this.helpID = helpID;
	}

	public String getHelpID() {
		return this.helpID;
	}

}
