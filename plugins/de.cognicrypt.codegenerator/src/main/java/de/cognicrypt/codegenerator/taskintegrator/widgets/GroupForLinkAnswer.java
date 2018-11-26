/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;

public class GroupForLinkAnswer extends Group {

	private Question currentQuestion;
	private ArrayList<Question> listOfAllQuestions;

	public GroupForLinkAnswer(Composite parent, int style, Answer answer, Question currentQuestion, ArrayList<Question> listOfAllQuestions) {
		super(parent, style);
		setCurrentQuestion(currentQuestion);
		setListOfAllQuestion(listOfAllQuestions);

		/**
		 * Non-editable text field containing the answer
		 */
		Text answerTxt = new Text(this, SWT.BORDER);
		answerTxt.setBounds(5, 5, 155, 25);
		answerTxt.setEditable(false);
		answerTxt.setText(answer.getValue());

		/**
		 * Combo containing the list of all questions that can be linked to when selecting a particular answer
		 */
		Combo comboForLinkAnswers = new Combo(this, SWT.READ_ONLY);
		comboForLinkAnswers.setBounds(165, 5, 460, 25);

		if (listOfAllQuestions.size() == 1) {
			comboForLinkAnswers.removeAll();
			comboForLinkAnswers.add("Please add more questions to link the answers");
		} else {
			if (currentQuestion.getId() != listOfAllQuestions.size() - 1) {
				/**
				 * Adds Default option to the combo when the current question is not the last question in the list Upon Default selection means the answer points to the next
				 * question in the list
				 */
				comboForLinkAnswers.add("Default");
			}
			comboForLinkAnswers.add("End Questionnaire");
			for (Question qstn : listOfAllQuestions) {
				if (currentQuestion.getId() < qstn.getId()) {
					comboForLinkAnswers.add(qstn.getQuestionText());
				}
			}

		}

		/**
		 * Executes when the link answer dialog is opened for the second time to edit the stored data
		 */
		if (answer.getNextID() != Constants.ANSWER_NO_NEXT_ID) {

			if (answer.getNextID() == Constants.ANSWER_NO_FOLLOWING_QUESTION_NEXT_ID) {
				comboForLinkAnswers.setText("End Questionnaire");
			} else {
				for (Question question : listOfAllQuestions) {
					if (question.getId() == answer.getNextID()) {
						comboForLinkAnswers.setText(question.getQuestionText());
					}
				}
			}
		}

		/**
		 * sets the answer nextID to the ID of selected question form the combo box
		 */
		comboForLinkAnswers.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (comboForLinkAnswers.getText().equalsIgnoreCase("End Questionnaire")) {
					answer.setNextID(Constants.ANSWER_NO_FOLLOWING_QUESTION_NEXT_ID);
				} else if (comboForLinkAnswers.getText().equalsIgnoreCase("Default")) {
					answer.setNextID(currentQuestion.getId() + 1);
				} else {
					for (Question question : listOfAllQuestions) {
						if (question.getQuestionText().equalsIgnoreCase(comboForLinkAnswers.getText())) {
							answer.setNextID(question.getId());
						}
					}
				}
			}

		});

	}

	/**
	 * sets the currentQuestions
	 * 
	 * @param currentQuestion
	 */
	public void setCurrentQuestion(Question currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	/**
	 * sets the list of all questions
	 * 
	 * @param listOfAllQuestion
	 */
	public void setListOfAllQuestion(ArrayList<Question> listOfAllQuestions) {
		this.listOfAllQuestions = listOfAllQuestions;
	}

	@Override
	protected void checkSubclass() {
		// To disable the check that prevents subclassing of SWT components
	}

}
