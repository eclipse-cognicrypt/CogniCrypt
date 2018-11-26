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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.cognicrypt.codegenerator.question.Question;

/**
 * 
 * @author ravi This class enables the user to rearrange the question order using up and down buttons created by this class
 *
 */
public class CompositeUpDownButtons extends Composite {

	/**
	 * Creates the composite
	 * 
	 * @param parent
	 * @param currentQuestion
	 */
	public CompositeUpDownButtons(Composite parent, Question currentQuestion) {
		super(parent, SWT.LEFT_TO_RIGHT);
		setLayout(new GridLayout(2, false));

		ArrayList<Question> listOfAllQuestions = ((CompositeToHoldGranularUIElements) this.getParent().getParent().getParent()).getListOfAllQuestions();
		Button upBtn = new Button(this, SWT.None);
		upBtn.setText("Up");
		upBtn.setToolTipText("Click on this button to move this question up in the list");
		upBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				//Only executes if the currentQuestion is not the first question in the list
				if (listOfAllQuestions.size() != 1) {
					((CompositeToHoldGranularUIElements) upBtn.getParent().getParent().getParent().getParent()).moveUpTheQuestion(currentQuestion);
				}
			}
		});

		Button downBtn = new Button(this, SWT.None);
		downBtn.setText("Down");
		downBtn.setToolTipText("Click on this button to move this question down in the list ");
		downBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				//Only executes if the currentQuestion is not the last question in the list
				if (currentQuestion.getId() != listOfAllQuestions.size() - 1) {
					((CompositeToHoldGranularUIElements) upBtn.getParent().getParent().getParent().getParent()).moveDownTheQuestion(currentQuestion);
				}

			}
		});
		/**
		 * Both up and down buttons are disables if the list has only one question
		 */
		if (listOfAllQuestions.size() == 1) {
			upBtn.setEnabled(false);
			downBtn.setEnabled(false);
		} else {
			/**
			 * disables the up button if current question is the first question
			 */
			if (currentQuestion.getId() == 0) {
				upBtn.setEnabled(false);
			}
			/**
			 * disables the down button if current question is the last question
			 */
			else if (currentQuestion.getId() == listOfAllQuestions.size() - 1) {
				downBtn.setEnabled(false);
			}
		}

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
