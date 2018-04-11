package de.cognicrypt.codegenerator.question;

import java.util.ArrayList;

import de.cognicrypt.core.Constants;

public class Answer {

	private String value;

	private Boolean defaultAnswer;
	private ArrayList<ClaferDependency> claferDependencies;
	private ArrayList<CodeDependency> codeDependencies;
	private int nextID = Constants.ANSWER_NO_NEXT_ID;

	public ArrayList<ClaferDependency> getClaferDependencies() {
		return this.claferDependencies;
	}

	public ArrayList<CodeDependency> getCodeDependencies() {
		return this.codeDependencies;
	}

	public int getNextID() {
		return this.nextID;
	}

	public String getValue() {
		return this.value;
	}

	public Boolean isDefaultAnswer() {
		return this.defaultAnswer == null ? false : this.defaultAnswer;
	}

	public void setClaferDependencies(final ArrayList<ClaferDependency> claferDependencies) {
		this.claferDependencies = claferDependencies;
	}

	public void setCodeDependencies(final ArrayList<CodeDependency> codeDependencies) {
		this.codeDependencies = codeDependencies;
	}

	public void setDefaultAnswer(final Boolean defaultAnswer) {
		this.defaultAnswer = defaultAnswer;
	}

	public void setNextID(final int prevID) {
		this.nextID = prevID;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		//the combo viewer calls the toString() method so just display the value
		return this.value;
	}

}
