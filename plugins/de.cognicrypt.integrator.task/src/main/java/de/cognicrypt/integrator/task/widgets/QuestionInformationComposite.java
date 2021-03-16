/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.wizard.QuestionsPage;

/**
 * This class creates widgets to display the overview of the question details and creates button to add clafer/code dependency to the question
 */

public class QuestionInformationComposite extends Composite {

	private Text questionText;

	private final int questionIndex;
	private final QuestionModificationComposite answersComposite;
	
	public final ControlDecoration questionDec;
	public final ControlDecoration answersDec;

	private QuestionsPage questionsPage;
	
	/**
	 * Create the composite.
	 *
	 * @param parent
	 */
	public QuestionInformationComposite(final Composite parent, final int questionIndex, QuestionsDisplayComposite questionsDisplayComposite, QuestionsPage questionsPage) {
		super(parent, SWT.BORDER);

		this.questionsPage = questionsPage;
		
		this.questionIndex = questionIndex;
		Question q = IntegratorModel.getInstance().getQuestions().get(questionIndex);

		final GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		setLayoutData(gridData);
		setLayout(new GridLayout(1, true));

		final Composite questionsAndDelete = new Composite(this, SWT.NONE);
		questionsAndDelete.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		questionsAndDelete.setLayout(new GridLayout(2, true));	

		final Label lblQuestion = new Label(questionsAndDelete, SWT.NONE);
		lblQuestion.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, true));
		lblQuestion.setText(Constants.QUESTION_LABEL);
		
		final Button btnDeleteQuestion = new Button(questionsAndDelete, SWT.NONE);
		btnDeleteQuestion.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		btnDeleteQuestion.setImage(sharedImages.getImage(ISharedImages.IMG_TOOL_DELETE));
		btnDeleteQuestion.setToolTipText(Constants.DELTETE_BTN_TOOLTIP);
		btnDeleteQuestion.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
				confirmationMessageBox.setMessage(Constants.DELETE_CONFIRMATION);
				confirmationMessageBox.setText(Constants.REMOVE_QUESTION);
				final int response = confirmationMessageBox.open();
				if (response == SWT.YES) {
					questionsDisplayComposite.removeQuestion(questionIndex);
				}
			}
		});
		
		
		questionText = new Text(this, SWT.BORDER);
		final GridData gdTxtQuestion = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gdTxtQuestion.widthHint = 0;
		questionText.setText(q.getQuestionText());
		questionText.setLayoutData(gdTxtQuestion);

		// Initialize the decorator for the label for the text box with initial error state
		questionDec = new ControlDecoration(lblQuestion, SWT.TOP | SWT.RIGHT);
		questionDec.setShowOnlyOnFocus(false);
		checkQuestionDec();
		
		questionText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				IntegratorModel.getInstance().getQuestion(questionIndex).setQuestionText(questionText.getText().trim());
				checkQuestionDec();
			}
		});
		
		final Text txtDescription = new Text(this, SWT.BORDER);
		final GridData gdTxtDescription= new GridData(SWT.FILL, SWT.CENTER, true, true);
		gdTxtDescription.widthHint = 0;
		txtDescription.setMessage(Constants.QUESTION_DESCRIPTION);
		txtDescription.setText(q.getHelpText());
		txtDescription.setLayoutData(gdTxtDescription);
		
		txtDescription.addModifyListener(
				e -> IntegratorModel.getInstance().getQuestion(questionIndex).setHelpText(txtDescription.getText().trim()));
		
		final Label spacer2 = new Label(this, SWT.HORIZONTAL);
	    spacer2.setLayoutData(new GridData(GridData.FILL, 15));
		
		
	    final Composite answersAndAdd = new Composite(this, SWT.NONE);
	    answersAndAdd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
	    answersAndAdd.setLayout(new GridLayout(2, true));	
	    
		final Label lblAnswers = new Label(answersAndAdd, SWT.NONE);
		lblAnswers.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, true));
		lblAnswers.setText(Constants.ANSWERS_LABEL);
		
		final Button btnAddAnswer = new Button(answersAndAdd, SWT.NONE);
		btnAddAnswer.setToolTipText(Constants.ADD_ANSWER_TOOLTIP);
		btnAddAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				addAnswer();
			}
		});
		btnAddAnswer.setText(Constants.ADD_ANSWER);
		btnAddAnswer.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		

		answersComposite = new QuestionModificationComposite(this, SWT.NONE, questionIndex);
		GridData answersGrid = new GridData(SWT.FILL, SWT.FILL, true, true);
		answersGrid.heightHint = 99;
		answersComposite.setLayoutData(answersGrid);
		
		answersComposite.updateAnswerContainer();
		
		answersDec = new ControlDecoration(lblAnswers, SWT.TOP | SWT.RIGHT);
		answersDec.setShowOnlyOnFocus(false);
		checkAnswersDec();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


	public void setTextQuestion(final String txtQuestion) {
		this.questionText.setText(txtQuestion);
	}

	public String getTextQuestion() {
		return questionText.getText();
	}
	
	/**
	 * Creates a new answer and updates the GUIe
	 */
	public void addAnswer() {
		IntegratorModel.getInstance().addAnswer(questionIndex);
		
		answersComposite.updateAnswerContainer();
		checkAnswersDec();
	}
	
	/**
	 * Removes an answer and updates the GUI
	 * @param answerIndex to be removed
	 */
	public void removeAnswer(int answerIndex) {
		IntegratorModel.getInstance().removeAnswer(answerIndex, answerIndex);
		
		checkAnswersDec();
	}
	
	/**
	 * Updates the question decorator and checks if the page is complete
	 */
	private void checkQuestionDec() {
		try {
			IntegratorModel.getInstance().checkQuestionDec(questionIndex);
			
			questionDec.setImage(Constants.DEC_REQUIRED);
			questionDec.setDescriptionText(Constants.MESSAGE_REQUIRED_FIELD);
		}catch(Exception e) {
			questionDec.setImage(Constants.DEC_ERROR);
			questionDec.setDescriptionText(Constants.ERROR + e.getMessage());
		}
		
		questionsPage.checkPageComplete();
	}
	
	/**
	 * Updates the answers decorator and checks if the page is complete
	 */
	public void checkAnswersDec() {
		try {
			IntegratorModel.getInstance().checkAnswersDec(questionIndex);
			
			answersDec.setImage(Constants.DEC_REQUIRED);
			answersDec.setDescriptionText(Constants.MESSAGE_REQUIRED_FIELD);
		}catch (Exception e) {
			answersDec.setImage(Constants.DEC_ERROR);
			answersDec.setDescriptionText(Constants.ERROR + e.getMessage());
		}
		
		questionsPage.checkPageComplete();
	}
}
