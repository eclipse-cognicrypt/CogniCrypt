package crossing.e1.configurator.beginer.question;

import java.util.ArrayList;

import crossing.e1.configurator.Constants;

public class Page {

	private int id;
	private ArrayList<Question> content;
	private int nextID = Constants.QUESTION_PAGE_NO_STATIC_NEXT_PAGE_ID;

	public int getId() {
		return id;
	}

	public ArrayList<Question> getContent() {
		return content;
	}

	public int getNextID() {
		return nextID;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setContent(ArrayList<Question> content) {
		this.content = content;
	}

	public void setNextID(int nextID) {
		this.nextID = nextID;
	}

}
