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

public class AnswerGroup extends Group {

	public Text txtAnswer;
	public Combo possibleIdentifiers;
	private Answer answer;
	public ArrayList<Answer> answers;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public AnswerGroup(final Composite parent, final int style, final Answer answerParam, final boolean isEditable) {
		super(parent, style);
		setAnswer(answerParam);

		this.txtAnswer = new Text(this, SWT.BORDER);

		this.possibleIdentifiers = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.possibleIdentifiers.setItems(IntegratorModel.getInstance().getIdentifiers().toArray(new String[0])); 
		if (this.answer.getValue() != null) {
			this.txtAnswer.setText(this.answer.getValue());
		}
		this.txtAnswer.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				AnswerGroup.this.answer.setValue(AnswerGroup.this.txtAnswer.getText());
			}
		});
		this.txtAnswer.setEditable(isEditable);
		
		if (this.answer.getOption() != null) {
			String selected = this.answer.getOption();
			this.possibleIdentifiers.select(IntegratorModel.getInstance().getIdentifiers().indexOf(selected));
		}

		this.possibleIdentifiers.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				AnswerGroup.this.answer.setOption(possibleIdentifiers.getText());
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				AnswerGroup.this.answer.setOption(possibleIdentifiers.getText());
			}
		});

		this.possibleIdentifiers.setEnabled(isEditable);
		
		final Button btnDefaultAnswer = new Button(this, SWT.RADIO);
		
	
		// executes when GroupAnswer is called by QuestionDialog class
		if (isEditable) {
			this.txtAnswer.setBounds(13, 9, 486, 29);
			//this.txtOption.setBounds(590, 3, 20, 29);
			this.possibleIdentifiers.setBounds(511, 6, 100, 29);
			btnDefaultAnswer.setBounds(623, 14, 128, 27);

		}
		/**
		 * executes when GroupAnswer is called by CompositeGranularUIForHighLevelQuestions class
		 */
		// Executes when GroupAnswer is called  by
		else {
			this.txtAnswer.setBounds(3, 3, 195, 29);
			//this.txtOption.setBounds(200, 3, 20, 29);
			this.possibleIdentifiers.setBounds(210, 3, 100, 25);
			btnDefaultAnswer.setBounds(322, 7, 128, 29);
		}
		final ArrayList<Button> btnList = ((QuestionModificationComposite) btnDefaultAnswer.getParent().getParent().getParent()).getDefaulAnswerBtnList();
		btnList.add(btnDefaultAnswer);
		btnDefaultAnswer.setText("Default Answer");
		if (this.answer.isDefaultAnswer()) {
			btnDefaultAnswer.setSelection(true);
		}
		btnDefaultAnswer.setToolTipText("The answer that will be automatically selected when question appears for the first time");

		btnDefaultAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				/**
				 * When user changes the default answer the following loop removes the previous selection and then current selected default answer value is set to true
				 */
				for (final Button btn : ((QuestionModificationComposite) btnDefaultAnswer.getParent().getParent().getParent()).getDefaulAnswerBtnList()) {
					btn.setSelection(false);
				}
				btnDefaultAnswer.setSelection(true);

				/**
				 * sets the default answer to true for the current answer and for all other answer to false
				 */
				for (final Answer ans : ((QuestionModificationComposite) btnDefaultAnswer.getParent().getParent().getParent()).getAnswers()) {
					if (ans.equals(AnswerGroup.this.answer)) {
						AnswerGroup.this.answer.setDefaultAnswer(true);
					} else {
						ans.setDefaultAnswer(false);
					}
				}

			}
		});

		btnDefaultAnswer.setEnabled(isEditable);
		if (isEditable) {
			final Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setBounds(763, 6, 80, 31);
			btnRemove.setText("Remove");
			btnRemove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
						((QuestionModificationComposite) btnRemove.getParent().getParent().getParent()).deleteAnswer(AnswerGroup.this.answer);
						btnList.remove(btnDefaultAnswer);
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
