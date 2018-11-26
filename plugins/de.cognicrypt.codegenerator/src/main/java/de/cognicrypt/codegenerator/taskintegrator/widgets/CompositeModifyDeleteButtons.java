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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;

import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.wizard.QuestionDialog;

public class CompositeModifyDeleteButtons extends Composite {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	int counter = 0;
	//private ArrayList<ClaferFeature> claferFeatures;

	public CompositeModifyDeleteButtons(Composite parent, Question questionParam) {
		super(parent, SWT.RIGHT_TO_LEFT);
		//setClaferFeatures(claferFeatures);
		/*
		 * RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL); setLayout(rowLayout);
		 */
		setLayout(new GridLayout(2, false));

		Button btnDelete = new Button(this, SWT.NONE);
		btnDelete.setToolTipText("Click to delete the question");
		btnDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
				confirmationMessageBox.setMessage("This information will be lost. Do you really want to delete?");
				confirmationMessageBox.setText("Deleting Question");
				int response = confirmationMessageBox.open();
				if (response == SWT.YES) {
					((CompositeToHoldGranularUIElements) btnDelete.getParent().getParent().getParent().getParent())
						.deleteQuestion(((CompositeGranularUIForHighLevelQuestions) btnDelete.getParent().getParent()).getQuestion());// (1) CompositeGranularUIForClaferFeature, (2) composite inside (3) CompositeToHoldGranularUIElements
				}
			}
		});
		btnDelete.setText("Delete");

		Button btnModify = new Button(this, SWT.NONE);
		btnModify.setText("Modify");
		btnModify.setToolTipText("Click to modify the question details");

		btnModify.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ArrayList<Question> listOfAllQuestions = ((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent().getParent()).getListOfAllQuestions();
				ClaferModel claferModel = ((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent().getParent()).getClaferModel();
				QuestionDialog qstnDialog = new QuestionDialog(parent.getShell(), questionParam, claferModel, listOfAllQuestions);
				int response = qstnDialog.open();
				if (response == Window.OK) {
					Question modifiedQuestion = qstnDialog.getQuestionDetails();
					((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent().getParent()).modifyHighLevelQuestion(questionParam, modifiedQuestion);
				}
			}
		});

	}

	/*
	 * private void setClaferFeatures(ArrayList<ClaferFeature> claferFeatures) { this.claferFeatures = claferFeatures; }
	 */

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
