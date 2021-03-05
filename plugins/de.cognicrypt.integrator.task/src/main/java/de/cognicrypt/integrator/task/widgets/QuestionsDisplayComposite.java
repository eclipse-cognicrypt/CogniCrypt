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

public class QuestionsDisplayComposite extends ScrolledComposite {

	private QuestionsPage questionsPage;
	
	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_GRANULAR_UI_ELEMENTS;
	
	private ArrayList<QuestionInformationComposite> questionsInformationComposites;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 */
	public QuestionsDisplayComposite(final Composite parent, QuestionsPage questionsPage) {
		super(parent, SWT.BORDER | SWT.V_SCROLL);

		this.questionsPage = questionsPage;
		
		questionsInformationComposites = new ArrayList<>();

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
	

	public void addQuestionUIElements(final int questionIndex) {
		questionsInformationComposites.add(new QuestionInformationComposite((Composite) getContent(), SWT.NONE, questionIndex, this, questionsPage));
		setMinSize(((Composite) getContent()).computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}


	/**
	 * Deletes the question
	 *
	 * @param questionIndex index of the question to be deleted
	 */
	public void deleteQuestion(final int questionIndex) {

		IntegratorModel.getInstance().getQuestions().remove(questionIndex);
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
		
		questionsInformationComposites.clear();

		// update the size values.
		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());

		for(int i = 0; i < IntegratorModel.getInstance().getQuestions().size(); i++) {
			addQuestionUIElements(i);
		}
		
		updateLayout();
	}

	/**
	 * Updates the layout of the page when user resizes the wizard pages
	 */
	public void updateLayout() {
		((Composite) getContent()).layout();
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
	
	public ArrayList<QuestionInformationComposite> getQuestionsInformationComposites() {
		return questionsInformationComposites;
	}
}
