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
