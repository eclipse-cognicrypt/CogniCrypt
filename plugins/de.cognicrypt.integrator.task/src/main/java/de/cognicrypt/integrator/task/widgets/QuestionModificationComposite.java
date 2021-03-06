/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
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
import de.cognicrypt.integrator.task.wizard.QuestionsPage;

public class QuestionModificationComposite extends ScrolledComposite {

	private QuestionsPage questionsPage;
	
	private final Composite composite;

	private final int questionIndex;
	
	private final ArrayList<Button> btnList;

	/**
	 * Create the composite. Warnings suppressed for casting array lists.
	 *
	 * @param parent
	 * @param style
	 * @param targetArrayListOfDataToBeDisplayed
	 */
	public QuestionModificationComposite(final Composite parent, final int style, int questionIndex, QuestionsPage questionsPage) {
		super(parent, style | SWT.V_SCROLL);

		this.questionsPage = questionsPage;
		
		this.questionIndex = questionIndex;
		btnList = new ArrayList<Button>();

		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setContent(composite);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
	}
	

	/**
	 * Creates the widgets in which user can give answer details
	 *
	 * @param answerIndex
	 */
	private void addAnswerUIElements(final int answerIndex) {
		AnswerGroup a = new AnswerGroup(composite, SWT.NONE, answerIndex, questionIndex, questionsPage);
		a.setLayout(new GridLayout(5, false));
		a.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Point size = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		composite.setSize((int) (2 * size.x), size.y);
	}

	/**
	 * To delete the answer
	 *
	 * @param answerIndex index of the answer to delete
	 */
	public void removeAnswer(final int answerIndex) {
		((QuestionInformationComposite) getParent()).removeAnswer(answerIndex);
		
		btnList.clear();
		updateAnswerContainer();
	}

	public void updateAnswerContainer() {
		for (final Control answerToDelete : composite.getChildren()) {
			answerToDelete.dispose();
		}

		for(int i=0; i < IntegratorModel.getInstance().getQuestion(questionIndex).getAnswers().size(); i++) {
			addAnswerUIElements(i);
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
}
