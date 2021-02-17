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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.integrator.task.models.IntegratorModel;

/**
 * @author ravi This class enables the user to rearrange the question order using up and down buttons created by this class
 */
public class QuestionOrderingComposite extends Composite {

	/**
	 * Creates the composite
	 *
	 * @param parent
	 * @param currentQuestion
	 */
	public QuestionOrderingComposite(final Composite parent, final Question currentQuestion) {
		super(parent, SWT.LEFT_TO_RIGHT);
		setLayout(new GridLayout(2, false));

		final ArrayList<Question> questions = IntegratorModel.getInstance().getQuestions();
		final Button upBtn = new Button(this, SWT.None);
		upBtn.setText("Up");
		upBtn.setToolTipText("Click on this button to move this question up in the list");
		upBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// Only executes if the currentQuestion is not the first question in the list
				if (questions.size() != 1) {
					((QuestionDisplayComposite) upBtn.getParent().getParent().getParent().getParent()).moveUpTheQuestion(currentQuestion);
				}
			}
		});

		final Button downBtn = new Button(this, SWT.None);
		downBtn.setText("Down");
		downBtn.setToolTipText("Click on this button to move this question down in the list ");
		downBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// Only executes if the currentQuestion is not the last question in the list
				if (currentQuestion.getId() != questions.size() - 1) {
					((QuestionDisplayComposite) upBtn.getParent().getParent().getParent().getParent()).moveDownTheQuestion(currentQuestion);
				}

			}
		});
		

		// Both up and down buttons are disables if the list has only one question
		if (questions.size() == 1) {
			upBtn.setEnabled(false);
			downBtn.setEnabled(false);
		} else {
			// Disables the up button if current question is the first question
			if (currentQuestion.getId() == 0) {
				upBtn.setEnabled(false);
			}
			// Disables the down button if current question is the last question
			else if (currentQuestion.getId() == questions.size() - 1) {
				downBtn.setEnabled(false);
			}
		}

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
