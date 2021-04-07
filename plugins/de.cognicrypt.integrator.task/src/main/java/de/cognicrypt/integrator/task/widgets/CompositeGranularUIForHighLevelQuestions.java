/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.wizard.AddDependenciesDialog;
import de.cognicrypt.integrator.task.wizard.LinkAnswerDialog;

/**
 * This class creates widgets to display the overview of the question details and creates button to add clafer/code dependency to the question
 */

public class CompositeGranularUIForHighLevelQuestions extends Composite {

	private final Text txtQuestionID;
	public Text txtQuestion;
	private final Text txtAnswerType;

	private Question question;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public CompositeGranularUIForHighLevelQuestions(final Composite parent, final int style, final Question questionParam, final boolean linkAnswerPage) {
		super(parent, SWT.BORDER);

		setQuestion(questionParam);

		final GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.minimumWidth = 300;
		setLayoutData(gridData);

		setLayout(new GridLayout(2, false));

		final CompositeModifyDeleteButtons grpModifyDeleteButtons = new CompositeModifyDeleteButtons(this, this.question);
		grpModifyDeleteButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// Only visible for "pageForHighLevelQuestions" page
		grpModifyDeleteButtons.setVisible(!linkAnswerPage);

		final CompositeUpDownButtons grpUpDownButtons = new CompositeUpDownButtons(this, this.question);
		grpUpDownButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		grpUpDownButtons.setVisible(!linkAnswerPage);

		final Group grpQuestionDetails = new Group(this, SWT.NONE);
		grpQuestionDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));
		grpQuestionDetails.setLayout(new GridLayout(4, false));



		grpQuestionDetails.setText("Question details");

		final Label lblQuestionId = new Label(grpQuestionDetails, SWT.NONE);
		lblQuestionId.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblQuestionId.setText("Question id:");

		this.txtQuestionID = new Text(grpQuestionDetails, SWT.BORDER);
		this.txtQuestionID.setEditable(false);
		final GridData gdTxtQuestionID = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdTxtQuestionID.widthHint = 0;
		this.txtQuestionID.setLayoutData(gdTxtQuestionID);
		this.txtQuestionID.setText(Integer.toString(this.question.getId()));

		final Label lblType = new Label(grpQuestionDetails, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblType.setText("Type:");

		this.txtAnswerType = new Text(grpQuestionDetails, SWT.BORDER);
		this.txtAnswerType.setEditable(false);
		final GridData gdTxtAnswerType = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdTxtAnswerType.widthHint = 0;
		this.txtAnswerType.setLayoutData(gdTxtAnswerType);

		final Label lblQuestion = new Label(grpQuestionDetails, SWT.NONE);
		lblQuestion.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblQuestion.setText("Question:");

		this.txtQuestion = new Text(grpQuestionDetails, SWT.BORDER);
		this.txtQuestion.setEditable(false);
		final GridData gdTxtQuestion = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gdTxtQuestion.widthHint = 0;
		this.txtQuestion.setLayoutData(gdTxtQuestion);

		setTextQuestion(this.question.getQuestionText());

		if (this.question.getElement().equals(Constants.GUIElements.combo)) {
			this.txtAnswerType.setText(Constants.dropDown);
		} else if (this.question.getElement().equals(Constants.GUIElements.text)) {
			this.txtAnswerType.setText(Constants.textBox);
		} else if (this.question.getElement().equals(Constants.GUIElements.radio)) {
			this.txtAnswerType.setText(Constants.radioButton);
		}

		if (!this.question.getElement().equals(Constants.GUIElements.text)) {
			final Label lblAnswers = new Label(grpQuestionDetails, SWT.NONE);
			lblAnswers.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			lblAnswers.setText("Answers:");

			final CompositeToHoldSmallerUIElements compositeForAnswers = new CompositeToHoldSmallerUIElements(grpQuestionDetails, SWT.None, null, false);
			final GridData gdCompositeForAnswers = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);
			gdCompositeForAnswers.heightHint = 100;
			compositeForAnswers.setLayoutData(gdCompositeForAnswers);
			for (final Answer answer : this.question.getAnswers()) {
				compositeForAnswers.addAnswer(answer, false);
			}
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the question
	 */
	public Question getQuestion() {
		return this.question;
	}

	/**
	 * @param question the question to set
	 */
	private void setQuestion(final Question question) {
		this.question = question;
	}

	public void setTextQuestion(final String txtQuestion) {
		this.txtQuestion.setText(txtQuestion);
	}

	public String getTextQuestion() {
		return this.txtQuestion.getText();
	}

	public String getAnswerType() {
		return this.txtAnswerType.getText();
	}
}
