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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.wizard.QuestionsPage;

public class AnswerGroup extends Group {

	private Text txtAnswer;
	private Combo possibleIdentifiers;
	
	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public AnswerGroup(final Composite parent, final int style, final int answerIndex, final int questionIndex, QuestionsPage questionsPage) {
		super(parent, style);
		
		setLayout(new GridLayout(4, true));
		
		Answer answer = IntegratorModel.getInstance().getAnswer(questionIndex, answerIndex);
		
		txtAnswer = new Text(this, SWT.BORDER);
		txtAnswer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		if (answer.getValue() != null) {
			txtAnswer.setText(answer.getValue());
		}
		
		txtAnswer.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				answer.setValue(txtAnswer.getText());
				((QuestionInformationComposite) parent.getParent().getParent()).checkAnswersDec();
			}
			
		});

		possibleIdentifiers = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		possibleIdentifiers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		possibleIdentifiers.setItems(IntegratorModel.getInstance().getIdentifiers().toArray(new String[0])); 
		
		if (answer.getOption() != null) {
			String selected = answer.getOption();
			possibleIdentifiers.select(IntegratorModel.getInstance().getIdentifiers().indexOf(selected));
		}

		possibleIdentifiers.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				answer.setOption(possibleIdentifiers.getText());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				answer.setOption(possibleIdentifiers.getText());
			}
		});
		
		
		final Button btnDefaultAnswer = new Button(this, SWT.RADIO);
		btnDefaultAnswer.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, true, true));
	
		final ArrayList<Button> btnList = ((QuestionModificationComposite) parent.getParent()).getDefaulAnswerBtnList();
		btnList.add(btnDefaultAnswer);
		btnDefaultAnswer.setText("Default Answer");
		if (answer.isDefaultAnswer()) {
			btnDefaultAnswer.setSelection(true);
		}
		btnDefaultAnswer.setToolTipText("The answer that will be automatically selected when question appears for the first time");

		btnDefaultAnswer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				/**
				 * When user changes the default answer the following loop removes the previous selection and then current selected default answer value is set to true
				 */
				for (final Button btn : ((QuestionModificationComposite) parent.getParent()).getDefaulAnswerBtnList()) {
					btn.setSelection(false);
				}
				btnDefaultAnswer.setSelection(true);

				/**
				 * sets the default answer to true for the current answer and for all other answer to false
				 */
				for (final Answer a : IntegratorModel.getInstance().getQuestions().get(questionIndex).getAnswers()) {
					if (a.equals(answer)) {
						answer.setDefaultAnswer(true);
					} else {
						a.setDefaultAnswer(false);
					}
				}

			}
		});
		
		final Button btnRemove = new Button(this, SWT.NONE);
		btnRemove.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		btnRemove.setImage(sharedImages.getImage(ISharedImages.IMG_TOOL_DELETE));
		btnRemove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				((QuestionModificationComposite) parent.getParent()).removeAnswer(answerIndex);
				btnList.remove(btnDefaultAnswer);
			}
		});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
