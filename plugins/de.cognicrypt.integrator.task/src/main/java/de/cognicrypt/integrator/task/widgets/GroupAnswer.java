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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.integrator.task.models.IntegratorModel;

public class GroupAnswer extends Group {

	public Text txtAnswer;
	//public Text txtOption;
	public Combo possibleIdentifiers;
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
		//this.txtOption = new Text(this, SWT.BORDER);
		this.possibleIdentifiers = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.possibleIdentifiers.setItems(IntegratorModel.getInstance().getIdentifiers().toArray(new String[0])); 
		if (this.answer.getValue() != null) {
			this.txtAnswer.setText(this.answer.getValue());
		}
		this.txtAnswer.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				GroupAnswer.this.answer.setValue(GroupAnswer.this.txtAnswer.getText());
			}
		});
		//this.txtAnswer.setEditable(showRemoveButton);
		
		if (this.answer.getOption() != null) {
			//this.txtOption.setText(this.answer.getOption());
			String selected = this.answer.getOption();
			this.possibleIdentifiers.select(IntegratorModel.getInstance().getIdentifiers().indexOf(selected));
		}
		/*this.txtOption.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				GroupAnswer.this.answer.setOption(GroupAnswer.this.txtOption.getText());
			}
		});*/
		//this.txtOption.setEditable(showRemoveButton);
		
		this.possibleIdentifiers.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				GroupAnswer.this.answer.setOption(possibleIdentifiers.getText());
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				GroupAnswer.this.answer.setOption(possibleIdentifiers.getText());
			}
		});

		//this.possibleIdentifiers.setEnabled(showRemoveButton);
		
		final Button btnDefaultAnswer = new Button(this, SWT.RADIO);
		/**
		 * executes when GroupAnswer is called by Question Dialog class
		 */
		if (showRemoveButton) {
			this.txtAnswer.setBounds(13, 9, 486, 29);
			//this.txtOption.setBounds(590, 3, 20, 29);
			this.possibleIdentifiers.setBounds(511, 6, 100, 29);
			btnDefaultAnswer.setBounds(623, 14, 128, 27);

		}
		/**
		 * executes when GroupAnswer is called by CompositeGranularUIForHighLevelQuestions class
		 */
		else {
			this.txtAnswer.setBounds(3, 3, 195, 29);
			//this.txtOption.setBounds(200, 3, 20, 29);
			this.possibleIdentifiers.setBounds(210, 3, 100, 25);
			btnDefaultAnswer.setBounds(322, 7, 128, 29);
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
			btnRemove.setBounds(763, 6, 80, 31);
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
