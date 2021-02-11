/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.wizard.QuestionsPage;

public class QuestionDisplayComposite extends ScrolledComposite {

	QuestionsPage questionsPage;
	
	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS;
	
	/**
	 * Create the composite.
	 *
	 * @param parent
	 */
	public QuestionDisplayComposite(final Composite parent, QuestionsPage questionsPage) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);

		this.questionsPage = questionsPage;

		setExpandHorizontal(true);
		setExpandVertical(true);
		setLayout(new GridLayout(2, false));

		// All the granular UI elements will be added to this composite for the ScrolledComposite to work.
		final Composite contentComposite = new Composite(this, SWT.NONE);
		final GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 30;
		contentComposite.setLayout(gridLayout);
		setContent(contentComposite);
		setMinSize(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	
	public void updateClaferContainer() {
		final Composite compositeContentOfThisScrolledComposite = (Composite) getContent();

		// first dispose all the granular UI elements (which includes the deleted one).
		for (final Control uiRepresentationOfClaferFeatures : compositeContentOfThisScrolledComposite.getChildren()) {
			uiRepresentationOfClaferFeatures.dispose();
		}

		// update the size values.
		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());

		((Composite) getContent()).layout();
	}

	

	public void addQuestionUIElements(final Question question) {

		new QuestionInformationComposite((Composite) getContent(), // the content composite of ScrolledComposite.
				SWT.NONE, question);
		setMinSize(((Composite) getContent()).computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	/**
	 * Moves the question up in the list
	 *
	 * @param question the question that is to be move up in the list
	 */
	public void moveUpTheQuestion(final Question question) {
		int questionToBeMoveUp = 0;
		int questionToBeMoveDown = 0;
		for (final Question qstn : IntegratorModel.getInstance().getQuestions()) {
			if (qstn.getId() == question.getId()) {
				questionToBeMoveUp = qstn.getId();
			} else if (qstn.getId() == question.getId() - 1) {
				questionToBeMoveDown = qstn.getId();
			}
		}
		Collections.swap(IntegratorModel.getInstance().getQuestions(), questionToBeMoveUp, questionToBeMoveDown);
		updateQuestionsID();
		updateQuestionContainer();
	}

	/**
	 * Moves the question down in the list
	 *
	 * @param question the question that is to be move down in the list
	 */
	public void moveDownTheQuestion(final Question question) {
		int questionToBeMoveUp = 0;
		int questionToBeMoveDown = 0;
		for (final Question qstn : IntegratorModel.getInstance().getQuestions()) {
			if (qstn.getId() == question.getId()) {
				questionToBeMoveUp = qstn.getId();
			} else if (qstn.getId() == question.getId() + 1) {
				questionToBeMoveDown = qstn.getId();
			}
		}
		Collections.swap(IntegratorModel.getInstance().getQuestions(), questionToBeMoveUp, questionToBeMoveDown);
		updateQuestionsID();
		updateQuestionContainer();
	}

	/**
	 * Deletes the question
	 *
	 * @param questionToBeDeleted the question to be deleted
	 */
	public void deleteQuestion(final Question questionToBeDeleted) {

		IntegratorModel.getInstance().getQuestions().remove(questionToBeDeleted);
		updateQuestionsID();
		updateQuestionContainer();
	}

	/**
	 * updates the question Id everytime when a question is deleted
	 */

	private void updateQuestionsID() {
		int qID = 0;
		for (final Question qstn : IntegratorModel.getInstance().getQuestions()) {
			qstn.setId(qID++);
		}

	}


	/**
	 * Updates the question container
	 */
	public void updateQuestionContainer() {
		final Composite compositeContentOfThisScrolledComposite = (Composite) getContent();

		// first dispose all the granular UI elements (which includes the deleted one).
		for (final Control uiRepresentationOfQuestions : compositeContentOfThisScrolledComposite.getChildren()) {
			uiRepresentationOfQuestions.dispose();
		}

		// update the size values.
		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());

		// add all the clafer features excluding the deleted one.
		for (final Question questionUnderConsideration : IntegratorModel.getInstance().getQuestions()) {
			addQuestionUIElements(questionUnderConsideration);
		}
		updateLayout();
		
		questionsPage.checkPageComplete();
	}

	/**
	 * Updates the layout of the page when user resizes the wizard pages
	 */
	public void updateLayout() {
		((Composite) getContent()).layout();
	}

	/**
	 * Modifies the question details
	 *
	 * @param originalQuestion the original question
	 * @param modifiedQuestion the modified question
	 */

	public void modifyHighLevelQuestion(final Question originalQuestion, final Question modifiedQuestion) {
		for (final Question questionUnderConsideration : IntegratorModel.getInstance().getQuestions()) {
			if (questionUnderConsideration.equals(originalQuestion)) {
				questionUnderConsideration.setQuestionText(modifiedQuestion.getQuestionText());
				questionUnderConsideration.setElement(modifiedQuestion.getElement());
				questionUnderConsideration.getAnswers().clear();
				questionUnderConsideration.setAnswers(modifiedQuestion.getAnswers());
				questionUnderConsideration.setHelpText(modifiedQuestion.getHelpText());
				if (modifiedQuestion.getElement().equals(Constants.GUIElements.text)) {
					questionUnderConsideration.setTooltip(modifiedQuestion.getTooltip());
					questionUnderConsideration.setTextType(modifiedQuestion.getTextType());
				}
				break;
			}
		}
		updateQuestionContainer();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the lowestWidgetYAxisValue
	 */
	public int getLowestWidgetYAxisValue() {
		return this.lowestWidgetYAxisValue;
	}

	/**
	 * @param lowestWidgetYAxisValue the lowestWidgetYAxisValue to set
	 */
	public void setLowestWidgetYAxisValue(final int lowestWidgetYAxisValue) {
		this.lowestWidgetYAxisValue = lowestWidgetYAxisValue + Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS;
	}
}
