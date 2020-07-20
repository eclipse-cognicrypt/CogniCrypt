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
import de.cognicrypt.integrator.task.models.XSLAttribute;

public class CompositeToHoldSmallerUIElements extends ScrolledComposite {

	private int lowestWidgetYAxisValue = Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS;
	private final Composite composite;
	public ArrayList<GroupAnswer> groupAnswers;
	private final ArrayList<XSLAttribute> XSLAttributes; // <attributeName, actualAttributeString>

	private final ArrayList<Answer> arrayAnswer;
	private SortedSet<String> possibleCfrFeatures;

	private final ArrayList<Button> btnList;


	/**
	 * Create the composite. Warnings suppressed for casting array lists.
	 *
	 * @param parent
	 * @param style TODO
	 * @param targetArrayListOfDataToBeDisplayed
	 * @param showRemoveButton
	 */
	public CompositeToHoldSmallerUIElements(final Composite parent, final int style, final ArrayList<?> targetArrayListOfDataToBeDisplayed, final boolean showRemoveButton) {
		super(parent, style | SWT.V_SCROLL);

		setExpandVertical(true);
		setExpandHorizontal(true);

		this.arrayAnswer = new ArrayList<Answer>();
		this.groupAnswers = new ArrayList<GroupAnswer>();
		this.btnList = new ArrayList<Button>();

		this.composite = new Composite(this, SWT.NONE);
		this.composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		this.composite.setLayout(new GridLayout(1, false));

		setContent(this.composite);
		setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		
		this.XSLAttributes = new ArrayList<XSLAttribute>();


	}

	

	/**
	 * If data is provided before hand, add it to the composite. This is specifically used for clafer.
	 *
	 * @param targetArrayListOfDataToBeDisplayed
	 * @param showRemoveButton
	 */
	@SuppressWarnings("unchecked")
	

	
	


	/**
	 * @param xslAttribute
	 */
	public void removeXSLAttribute(final XSLAttribute xslAttribute) {
		getXSLAttributes().remove(xslAttribute);
	}

	/**
	 * Creates the widgets in which user can give answer details
	 *
	 * @param answer
	 * @param showRemoveButton
	 */
	public void addAnswer(final Answer answer, final boolean showRemoveButton) {
		final GroupAnswer groupForAnswer = new GroupAnswer((Composite) getContent(), SWT.NONE, answer, showRemoveButton);
		groupForAnswer.setBounds(Constants.PADDING_BETWEEN_SMALLER_UI_ELEMENTS, getLowestWidgetYAxisValue(), 890, 39);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);

		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * To delete the answer
	 *
	 * @param answerToBeDeleted
	 */
	public void deleteAnswer(final Answer answerToBeDeleted) {
		this.arrayAnswer.remove(answerToBeDeleted);
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

		for (final Answer answer : this.arrayAnswer) {
			addAnswer(answer, true);
		}
	}

	/**
	 * @return the btnList List of radio buttons for default Answer field
	 */
	public ArrayList<Button> getDefaulAnswerBtnList() {
		return this.btnList;
	}

	/**
	 * Add the widgets and data inside the scrollable composite for Link code tab
	 *
	 * @param answer
	 */
	public void addELementsInCodeTabQuestionDialog(final Answer answer) {
		final CompositeForCodeTab group = new CompositeForCodeTab((Composite) getContent(), SWT.NONE, answer);
		group.setBounds(5, getLowestWidgetYAxisValue(), 690, 39);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);
		setMinHeight(getLowestWidgetYAxisValue());
	}

	/**
	 * Add the widgets and data inside the scrollable composite for clafer dependency tab
	 *
	 * @param answer
	 * @param claferFeatures list of all clafer features created in the clafer page
	 */
	

	/**
	 * Add the widgets and data inside the scrollable composite for Link Answer
	 *
	 * @param currentQuestion
	 * @param listOfAllQuestions
	 */
	public void addElementsOfLinkAnswer(final Answer answer, final Question currentQuestion, final ArrayList<Question> listOfAllQuestions) {
		final GroupForLinkAnswer group = new GroupForLinkAnswer((Composite) getContent(), SWT.NONE, answer, currentQuestion, listOfAllQuestions);
		group.setBounds(5, getLowestWidgetYAxisValue(), 690, 39);
		setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 39);
		setMinHeight(getLowestWidgetYAxisValue());
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
	public ArrayList<Answer> getListOfAllAnswer() {
		return this.arrayAnswer;
	}

	

	

	
	/**
	 * @param selectionOnComboXSLTags
	 * @return
	 */
	public ArrayList<String> getListOfPossibleAttributes(final String selectionOnComboXSLTags) {

		final ArrayList<String> listOfPossibleAttributes = new ArrayList<String>();

		// Populate with all the possible attributes first.
		for (final XSLTags XSLTag : Constants.XSLTags.values()) {
			if (XSLTag.getXSLTagFaceName().equals(selectionOnComboXSLTags)) {
				for (final String attribute : XSLTag.getXSLAttributes()) {
					listOfPossibleAttributes.add(attribute);
				}
			}
		}
		// Remove the attributes that already exist in the list.
		for (final XSLAttribute attribute : this.XSLAttributes) {
			if (listOfPossibleAttributes.contains(attribute.getXSLAttributeName())) {
				listOfPossibleAttributes.remove(attribute.getXSLAttributeName());
			}
		}

		return listOfPossibleAttributes;
	}

	/**
	 * @return the xSLAttributes
	 */
	public ArrayList<XSLAttribute> getXSLAttributes() {
		return this.XSLAttributes;
	}

}
