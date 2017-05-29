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

import java.lang.reflect.Method;
import java.util.ArrayList;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.Constants.GUIElements;

public class Question {

	private int id;
	private GUIElements element;
	private String selectionClafer;
	private String questionText;
	private ArrayList<Answer> answers;
	private Answer defaultAnswer;
	private ArrayList<String> method;
	private ArrayList<Integer> methodParamIds;
	private ArrayList<Integer> methodReferIds;

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

	public GUIElements getElement() {
		if (this.element == null || this.element.name().isEmpty()) {
			setElement(Constants.GUIElements.combo);
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
	
	public ArrayList<String> getMethod() {
		return this.method;
	}

	public ArrayList<Integer> getMethodParamIds() {
		return methodParamIds;
	}
	
	public ArrayList<Integer> getMethodReferIds() {
		return methodReferIds;
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

	public void setQuestionText(final String questionText) {
		this.questionText = questionText;
	}
	
	public ArrayList<String> setMethod(final ArrayList<String> method) {
		return this.method;
	}

	public void setMethodParamIds(ArrayList<Integer> methodParamIds) {
		this.methodParamIds = methodParamIds;
	}
	
	public void setMethodReferIds(ArrayList<Integer> methodReferIds) {
		this.methodReferIds = methodReferIds;
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
