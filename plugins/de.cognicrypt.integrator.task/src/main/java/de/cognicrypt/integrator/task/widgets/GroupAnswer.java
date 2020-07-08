/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

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
	public Text txtOption;
	private Answer answer;
	public ArrayList<Answer> answers;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public GroupAnswer(final Composite parent, final int style, final Answer answerParam, final boolean showRemoveButton) {
		super(parent, style);
		setAnswer(answerParam);

		this.txtAnswer = new Text(this, SWT.BORDER);
		this.txtOption = new Text(this, SWT.BORDER);
		if (this.answer.getValue() != null) {
			this.txtAnswer.setText(this.answer.getValue());
		}
		this.txtAnswer.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				GroupAnswer.this.answer.setValue(GroupAnswer.this.txtAnswer.getText());
			}
		});
		this.txtAnswer.setEditable(showRemoveButton);
		
		if (this.answer.getOption() != null) {
			this.txtOption.setText(this.answer.getOption());
		}
		this.txtOption.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				GroupAnswer.this.answer.setOption(GroupAnswer.this.txtOption.getText());
			}
		});
		this.txtOption.setEditable(showRemoveButton);

		final Button btnDefaultAnswer = new Button(this, SWT.RADIO);
		/**
		 * executes when GroupAnswer is called by Question Dialog class
		 */
		if (showRemoveButton) {
			this.txtAnswer.setBounds(3, 3, 586, 29);
			this.txtOption.setBounds(590, 3, 20, 29);
			btnDefaultAnswer.setBounds(613, 3, 128, 31);

		}
		/**
		 * executes when GroupAnswer is called by CompositeGranularUIForHighLevelQuestions class
		 */
		else {
			this.txtAnswer.setBounds(3, 3, 195, 29);
			this.txtOption.setBounds(200, 3, 20, 29);
			btnDefaultAnswer.setBounds(232, 3, 128, 31);
		}
		final ArrayList<Button> btnList = ((CompositeToHoldSmallerUIElements) btnDefaultAnswer.getParent().getParent().getParent()).getDefaulAnswerBtnList();
		btnList.add(btnDefaultAnswer);
		btnDefaultAnswer.setText("Default Answer");
		if (this.answer.isDefaultAnswer()) {
			btnDefaultAnswer.setSelection(true);
		}
		btnDefaultAnswer.setToolTipText("the answer that will be automatically selected when question appears for the first time");

		btnDefaultAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				/**
				 * When user changes the default answer the following loop removes the previous selection and then current selected default answer value is set to true
				 */
				for (final Button btn : ((CompositeToHoldSmallerUIElements) btnDefaultAnswer.getParent().getParent().getParent()).getDefaulAnswerBtnList()) {
					btn.setSelection(false);
				}
				btnDefaultAnswer.setSelection(true);

				/**
				 * sets the default answer to true for the current answer and for all other answer to false
				 */
				for (final Answer ans : ((CompositeToHoldSmallerUIElements) btnDefaultAnswer.getParent().getParent().getParent()).getListOfAllAnswer()) {
					if (ans.equals(GroupAnswer.this.answer)) {
						GroupAnswer.this.answer.setDefaultAnswer(true);
					} else {
						ans.setDefaultAnswer(false);
					}
				}

			}
		});

		btnDefaultAnswer.setEnabled(showRemoveButton);
		if (showRemoveButton) {
			final Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setBounds(746, 3, 80, 31);
			btnRemove.setText("Remove");
			btnRemove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					final MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
					confirmationMessageBox.setMessage("This information will be lost. Do you really want to delete?");
					confirmationMessageBox.setText("Deleting answer");
					final int response = confirmationMessageBox.open();
					if (response == SWT.YES) {
						((CompositeToHoldSmallerUIElements) btnRemove.getParent().getParent().getParent()).deleteAnswer(GroupAnswer.this.answer);
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
		return this.answer;
	}

	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(final Answer answer) {
		this.answer = answer;
	}
}
