/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.util.ArrayList;
import java.util.Collections;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;

public class CompositeToHoldGranularUIElements extends ScrolledComposite {

	private String targetPageName;
	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS;

	private ArrayList<Question> listOfAllQuestions;
	int counter;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 */
	public CompositeToHoldGranularUIElements(final Composite parent, final String pageName) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);


		this.listOfAllQuestions = new ArrayList<Question>();

		setTargetPageName(pageName);

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

	

	public void addQuestionUIElements(final Question question, final boolean linkAnswerPage) {

		new CompositeGranularUIForHighLevelQuestions((Composite) getContent(), // the content composite of ScrolledComposite.
				SWT.NONE, question, linkAnswerPage);
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
		for (final Question qstn : this.listOfAllQuestions) {
			if (qstn.getId() == question.getId()) {
				questionToBeMoveUp = qstn.getId();
			} else if (qstn.getId() == question.getId() - 1) {
				questionToBeMoveDown = qstn.getId();
			}
		}
		Collections.swap(this.listOfAllQuestions, questionToBeMoveUp, questionToBeMoveDown);
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
		for (final Question qstn : this.listOfAllQuestions) {
			if (qstn.getId() == question.getId()) {
				questionToBeMoveUp = qstn.getId();
			} else if (qstn.getId() == question.getId() + 1) {
				questionToBeMoveDown = qstn.getId();
			}
		}
		Collections.swap(this.listOfAllQuestions, questionToBeMoveUp, questionToBeMoveDown);
		updateQuestionsID();
		updateQuestionContainer();
	}

	/**
	 * Deletes the question
	 *
	 * @param questionToBeDeleted the question to be deleted
	 */
	public void deleteQuestion(final Question questionToBeDeleted) {

		this.listOfAllQuestions.remove(questionToBeDeleted);
		updateQuestionsID();
		updateQuestionContainer();

	}

	/**
	 * updates the question Id everytime when a question is deleted
	 */

	private void updateQuestionsID() {
		int qID = 0;
		for (final Question qstn : this.listOfAllQuestions) {
			qstn.setId(qID++);
		}

	}

	/**
	 * executes when next button of "highLevelQuestion" is pressed, deletes listOfAllQuestions of "pageForLinkAnswers" to refresh the question list of "pagForLinkAnswers"
	 */
	public void deleteAllQuestion() {
		this.listOfAllQuestions.clear();
		updateQuestionContainer();
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
		for (final Question questionUnderConsideration : this.listOfAllQuestions) {
			addQuestionUIElements(questionUnderConsideration, false);
		}
		updateLayout();
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
		for (final Question questionUnderConsideration : this.listOfAllQuestions) {
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
	 * @return the targetPageName
	 */
	public String getTargetPageName() {
		return this.targetPageName;
	}

	/**
	 * @param targetPageName the targetPageName to set
	 */
	private void setTargetPageName(final String targetPageName) {
		this.targetPageName = targetPageName;
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



	/**
	 * @return the listOfAllQuestions
	 */
	public ArrayList<Question> getListOfAllQuestions() {
		return this.listOfAllQuestions;
	}

	/**
	 * @param listOfAllQuestions the listOfAllQuestions to set
	 */
	public void setListOfAllQuestions(final ArrayList<Question> listOfAllQuestions) {
		this.listOfAllQuestions = listOfAllQuestions;
	}

}
