/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldSmallerUIElements;
import de.cognicrypt.core.Constants;

public class LinkAnswerDialog extends Dialog {

	private Question question;
	private ArrayList<Question> listOfAllQuestions;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 * @param question
	 *        represents the current question
	 * @param listOfAllQuestions
	 */
	public LinkAnswerDialog(Shell parent, Question question, ArrayList<Question> listOfAllQuestions) {
		super(parent);
		setQuestion(question);
		setListOfAllQuestions(listOfAllQuestions);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));
		getShell().setMinimumSize(700, 400);

		//executes when the question type is text
		if (question.getElement().equals(Constants.GUIElements.text)) {
			Label lblLinkAnswersTabMessage = new Label(container, SWT.NONE);
			lblLinkAnswersTabMessage.setText("This type of question does not need to link answers");

		} else {

			Label lblQuestion_1 = new Label(container, SWT.NONE);
			lblQuestion_1.setText("Question:");

			Label qstnTxt = new Label(container, SWT.NONE);
			qstnTxt.setText(question.getQuestionText());

			//Group containing the headers
			Group groupHeaderLinkAnswer = new Group(container, SWT.NONE);
			GridData gd_groupHeaderLinkAnswer = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
			groupHeaderLinkAnswer.setLayoutData(gd_groupHeaderLinkAnswer);

			Label lblAnswers = new Label(groupHeaderLinkAnswer, SWT.NONE);
			lblAnswers.setBounds(5, 5, 150, 25);
			lblAnswers.setText("Answers");

			Label lblSelectQuestion = new Label(groupHeaderLinkAnswer, SWT.NONE);
			lblSelectQuestion.setBounds(180, 5, 530, 25);
			lblSelectQuestion.setText("Jump to question");

			// Scroll composite containing the needed widgets for linking the answers to other questions

			CompositeToHoldSmallerUIElements scrollCompositeForAnswers = new CompositeToHoldSmallerUIElements(container, SWT.NONE, null, false, null);
			GridData gd_LinkAns = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
			gd_LinkAns.heightHint = 200;
			gd_LinkAns.widthHint = 700;
			scrollCompositeForAnswers.setLayoutData(gd_LinkAns);
			scrollCompositeForAnswers.setLayout(new GridLayout(2, false));

			for (Answer answer : question.getAnswers()) {
				scrollCompositeForAnswers.addElementsOfLinkAnswer(answer, question, listOfAllQuestions);
			}
		}

		return container;
	}

	/**
	 * 
	 * @return the current question
	 */
	public Question getQuestion() {
		return question;
	}

	/**
	 * sets the current question
	 * 
	 * @param question
	 */
	public void setQuestion(Question question) {
		this.question = question;
	}

	/**
	 * 
	 * @return listOfAllQuestions
	 */
	public ArrayList<Question> getListOfAllQuestions() {
		return listOfAllQuestions;
	}

	/**
	 * sets the listOfAllQuestions
	 * 
	 * @param listOfAllQuestions
	 */
	public void setListOfAllQuestions(ArrayList<Question> listOfAllQuestions) {
		this.listOfAllQuestions = listOfAllQuestions;
	}
}
