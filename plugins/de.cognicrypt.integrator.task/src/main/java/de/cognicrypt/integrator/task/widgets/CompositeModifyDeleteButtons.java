/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

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
import de.cognicrypt.integrator.task.wizard.QuestionDialog;

public class CompositeModifyDeleteButtons extends Composite {

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	int counter = 0;
	// private ArrayList<ClaferFeature> claferFeatures;

	public CompositeModifyDeleteButtons(final Composite parent, final Question questionParam) {
		super(parent, SWT.RIGHT_TO_LEFT);
		// setClaferFeatures(claferFeatures);
		/*
		 * RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL); setLayout(rowLayout);
		 */
		setLayout(new GridLayout(2, false));

		final Button btnDelete = new Button(this, SWT.NONE);
		btnDelete.setToolTipText("Click to delete the question");
		btnDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
				confirmationMessageBox.setMessage("This information will be lost. Do you really want to delete?");
				confirmationMessageBox.setText("Deleting Question");
				final int response = confirmationMessageBox.open();
				if (response == SWT.YES) {
					((CompositeToHoldGranularUIElements) btnDelete.getParent().getParent().getParent().getParent())
							.deleteQuestion(((CompositeGranularUIForHighLevelQuestions) btnDelete.getParent().getParent()).getQuestion());// (1) CompositeGranularUIForClaferFeature, (2)
																																																														// composite inside (3)
																																																														// CompositeToHoldGranularUIElements
				}
			}
		});
		btnDelete.setText("Delete");

		final Button btnModify = new Button(this, SWT.NONE);
		btnModify.setText("Modify");
		btnModify.setToolTipText("Click to modify the question details");

		btnModify.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final ArrayList<Question> listOfAllQuestions = ((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent().getParent()).getListOfAllQuestions();
				final QuestionDialog qstnDialog = new QuestionDialog(parent.getShell(), questionParam, listOfAllQuestions);
				final int response = qstnDialog.open();
				if (response == Window.OK) {
					final Question modifiedQuestion = qstnDialog.getQuestionDetails();
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
