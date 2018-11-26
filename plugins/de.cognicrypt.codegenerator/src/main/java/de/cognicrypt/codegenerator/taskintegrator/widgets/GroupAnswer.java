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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.question.Answer;

public class GroupAnswer extends Group {

	public Text txtAnswer;
	private Answer answer;
	public ArrayList<Answer> answers;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public GroupAnswer(Composite parent, int style, Answer answerParam, boolean showRemoveButton) {
		super(parent, style);
		setAnswer(answerParam);

		txtAnswer = new Text(this, SWT.BORDER);
		if (answer.getValue() != null) {
			txtAnswer.setText(answer.getValue());
		}
		txtAnswer.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				answer.setValue(txtAnswer.getText());
			}
		});
		txtAnswer.setEditable(showRemoveButton);

		Button btnDefaultAnswer = new Button(this, SWT.RADIO);
		/**
		 * executes when GroupAnswer is called by Question Dialog class
		 */
		if (showRemoveButton) {
			txtAnswer.setBounds(3, 3, 606, 29);
			btnDefaultAnswer.setBounds(613, 3, 128, 31);

		}
		/**
		 * executes when GroupAnswer is called by CompositeGranularUIForHighLevelQuestions class
		 */
		else {
			txtAnswer.setBounds(3, 3, 225, 29);
			btnDefaultAnswer.setBounds(232, 3, 128, 31);
		}
		ArrayList<Button> btnList = ((CompositeToHoldSmallerUIElements) btnDefaultAnswer.getParent().getParent().getParent()).getDefaulAnswerBtnList();
		btnList.add(btnDefaultAnswer);
		btnDefaultAnswer.setText("Default Answer");
		if (answer.isDefaultAnswer()) {
			btnDefaultAnswer.setSelection(true);
		}
		btnDefaultAnswer.setToolTipText("the answer that will be automatically selected when question appears for the first time");

		btnDefaultAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				/**
				 * When user changes the default answer the following loop removes the previous selection and then current selected default answer value is set to true
				 */
				for (Button btn : ((CompositeToHoldSmallerUIElements) btnDefaultAnswer.getParent().getParent().getParent()).getDefaulAnswerBtnList()) {
					btn.setSelection(false);
				}
				btnDefaultAnswer.setSelection(true);

				/**
				 * sets the default answer to true for the current answer and for all other answer to false
				 */
				for (Answer ans : ((CompositeToHoldSmallerUIElements) btnDefaultAnswer.getParent().getParent().getParent()).getListOfAllAnswer()) {
					if (ans.equals(answer)) {
						answer.setDefaultAnswer(true);
					} else {
						ans.setDefaultAnswer(false);
					}
				}

			}
		});

		btnDefaultAnswer.setEnabled(showRemoveButton);
		if (showRemoveButton) {
			Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setBounds(746, 3, 80, 31);
			btnRemove.setText("Remove");
			btnRemove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
					confirmationMessageBox.setMessage("This information will be lost. Do you really want to delete?");
					confirmationMessageBox.setText("Deleting answer");
					int response = confirmationMessageBox.open();
					if (response == SWT.YES) {
						((CompositeToHoldSmallerUIElements) btnRemove.getParent().getParent().getParent()).deleteAnswer(answer);
						btnList.remove(btnDefaultAnswer);
					}

				}

			});
		}

	}

	/**
	 * @return the answer text
	 */
	/*
	 * public String retrieveAnswer(){ getAnswer=txtAnswer.getText(); return getAnswer; }
	 *//**
		 * set the answer text
		 *//*
			 * public void setAnswerValue(){ answer.setValue(this.retrieveAnswer()); }
			 */

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the answer
	 */
	public Answer getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *        the answer to set
	 */
	public void setAnswer(Answer answer) {
		this.answer = answer;
	}
}
