package crossing.e1.configurator.beginer.question;

import java.util.ArrayList;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.Constants.guiElements;

public class Question {

	private int id;
	private guiElements element;
	private String selectionClafer;
	private String questionText;
	private ArrayList<Answer> answers;
	private Answer defaultAnswer;

	public ArrayList<Answer> getAnswers() {
		return this.answers;
	}

	public Answer getDefaultAnswer() {
		if (this.defaultAnswer == null) {
			for (final Answer answer : this.answers) {
				if (answer.isDefaultAnswer()) {
					this.defaultAnswer = answer;
					break;
				}
			}
		}

		return this.defaultAnswer;
	}

	public guiElements getElement() {
		if (this.element == null || this.element.name().isEmpty()) {
			setElement(Constants.guiElements.combo);
		}
		return this.element;
	}

	public int getId() {
		return this.id;
	}

	public String getQuestionText() {
		return this.questionText;
	}

	public String getSelectionClafer() {
		return this.selectionClafer;
	}

	public void setAnswers(final ArrayList<Answer> answers) {
		this.answers = answers;
	}

	public void setElement(final guiElements combo) {
		this.element = combo;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setQuestionText(final String questionText) {
		this.questionText = questionText;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ID: " + this.id + "\n");
		builder.append(this.questionText + "\n");
		for (final Answer answer : this.answers) {
			builder.append("\t" + answer.getValue() + " [dependencies=" + answer.getClaferDependencies() + "], defaultValue=" + answer
				.isDefaultAnswer() + ", next Question=" + answer.getNextID());
		}
		return builder.toString();
	}

}
