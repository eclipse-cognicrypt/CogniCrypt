/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MessageBox;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.XSLTags;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class QuestionModificationComposite extends ScrolledComposite {

	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	private final Composite composite;

	private final ArrayList<Answer> answers;

	private final ArrayList<Button> btnList;


	/**
	 * Create the composite. Warnings suppressed for casting array lists.
	 *
	 * @param parent
	 * @param style
	 * @param targetArrayListOfDataToBeDisplayed
	 * @param showRemoveButton
	 */
	public QuestionModificationComposite(final Composite parent, final int style) {
		super(parent, style | SWT.V_SCROLL);

		setExpandVertical(true);
		setExpandHorizontal(true);

		this.answers = new ArrayList<Answer>();
		this.btnList = new ArrayList<Button>();

		this.composite = new Composite(this, SWT.NONE);
		this.composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		this.composite.setLayout(new GridLayout(1, false));

		setContent(this.composite);
		setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	

	/**
	 * Creates the widgets in which user can give answer details
	 *
	 * @param answer
	 * @param isEditable
	 * @param templateIdentifier
	 */
	public void addAnswer(final Answer answer, final boolean isEditable) {
		AnswerGroup groupForAnswer = new AnswerGroup((Composite) getContent(), SWT.NONE, answer, isEditable);
		groupForAnswer.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), 850, 50);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 50);

		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * To delete the answer
	 *
	 * @param answerToBeDeleted
	 */
	public void deleteAnswer(final Answer answerToBeDeleted) {
		this.answers.remove(answerToBeDeleted);
		this.btnList.clear();
		updateAnswerContainer();
	}

	public void updateAnswerContainer() {
		final Composite contentOfThisScrolledComposite = (Composite) getContent();

		for (final Control answerToDelete : contentOfThisScrolledComposite.getChildren()) {
			answerToDelete.dispose();
		}

		setLowestWidgetYAxisValue(0);
		setMinHeight(getLowestWidgetYAxisValue());

		for (final Answer answer : this.answers) {
			addAnswer(answer, true); //change later
		}
	}

	/**
	 * @return the btnList List of radio buttons for default Answer field
	 */
	public ArrayList<Button> getDefaulAnswerBtnList() {
		return this.btnList;
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
		this.lowestWidgetYAxisValue = lowestWidgetYAxisValue + Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	}

	

	/**
	 * @return the listOfAllAnswer
	 */
	public ArrayList<Answer> getAnswers() {
		return this.answers;
	}
}
