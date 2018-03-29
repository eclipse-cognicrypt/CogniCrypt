package de.cognicrypt.codegenerator.question;

import java.util.ArrayList;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.Constants.GUIElements;

public class Question {

	private int id;
	private GUIElements element;
	private String selectionClafer;
	private String questionText;
	private ArrayList<Answer> answers;
	private Answer defaultAnswer;
	private ArrayList<String> method;
	private ArrayList<Integer> methodParamIds;
	private String questionType;
	private Answer enteredAnswer;
	private String extension;
	//Note
	private String note = "";
	//tooltip
	private String tooltip;
	//TextType
	private String textType;
	
	public ArrayList<Answer> getAnswers() {
		return this.answers;
	}

	/**
	 * Retrieves the default answer of the question.
	 * @return Default answer
	 */
	public Answer getDefaultAnswer() {
		if (this.defaultAnswer == null) {
			for (final Answer answer : this.answers) {
				if (answer.isDefaultAnswer()) {
					return this.defaultAnswer = answer;
				}
			}
		}
		return this.defaultAnswer;
	}

	/**
	 * Retrieves the widget the question should be displayed as.
	 * @return GUI widget associated with the question
	 */
	public GUIElements getElement() {
		if (this.element == null || this.element.name().isEmpty()) {
			setElement(Constants.GUIElements.combo);
		}
		return this.element;
	}

	public int getId() {
		return this.id;
	}

	//added get method for note
	public String getNote() {
		return this.note;
	}

	//added get method for tooltip
	public String getTooltip() {
		return this.tooltip;
	}
	
	//added the type of the text
	public String getTextType() {
		return this.textType;
	}
		
	public String getQuestionText() {
		return this.questionText;
	}

	public String getSelectionClafer() {
		return this.selectionClafer;
	}

	public ArrayList<String> getMethod() {
		return this.method;
	}

	public ArrayList<Integer> getMethodParamIds() {
		return this.methodParamIds;
	}

	public void setAnswers(final ArrayList<Answer> answers) {
		this.answers = answers;
	}

	public void setElement(final GUIElements combo) {
		this.element = combo;
	}

	public void setId(final int id) {
		this.id = id;
	}

	//added set method for note
	public void setNote(final String note) {
		this.note = note;
	}

	//added set method for tooltip
	public void setTooltip(final String tooltip) {
		this.tooltip = tooltip;
	}
	
	//added set method for the type of text
	public void setTextType(final String textType) {
		this.textType = textType;
	}
	
	public void setQuestionText(final String questionText) {
		this.questionText = questionText;
	}

	public void setMethod(final ArrayList<String> method) {
		this.method = method;
	}

	public void setMethodParamIds(final ArrayList<Integer> methodParamIds) {
		this.methodParamIds = methodParamIds;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ID: ");
		builder.append(this.id);
		builder.append(Constants.lineSeparator);
		builder.append(this.questionText);
		builder.append(Constants.lineSeparator);
		builder.append(this.note);
		builder.append(Constants.lineSeparator);

		for (final Answer answer : this.answers) {
			builder.append("\t" + answer.getValue() + " [dependencies=" + answer.getClaferDependencies() + "], defaultValue=" + answer
				.isDefaultAnswer() + ", next Question=" + answer.getNextID());
		}
		return builder.toString();
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public Answer setEnteredAnswer(Answer enteredAnswer) {
		return this.enteredAnswer = enteredAnswer;
	}

	public Answer getEnteredAnswer() {
		return this.enteredAnswer;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

}
