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
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.wizard.QuestionDialog;

public class QuestionModifyDeleteComposite extends Composite {

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	// private ArrayList<ClaferFeature> claferFeatures;

	public QuestionModifyDeleteComposite(final Composite parent, final Question questionParam) {
		super(parent, SWT.RIGHT_TO_LEFT);

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
					((QuestionDisplayComposite) btnDelete.getParent().getParent().getParent().getParent())
							.deleteQuestion(((QuestionInformationComposite) btnDelete.getParent().getParent()).getQuestion());
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
				final ArrayList<Question> listOfAllQuestions = IntegratorModel.getInstance().getQuestions();
				final QuestionDialog qstnDialog = new QuestionDialog(parent.getShell(), questionParam, listOfAllQuestions); //change later
				final int response = qstnDialog.open();
				if (response == Window.OK) {
					final Question modifiedQuestion = qstnDialog.getQuestionDetails();
					((QuestionDisplayComposite) btnModify.getParent().getParent().getParent().getParent()).modifyHighLevelQuestion(questionParam, modifiedQuestion);
				}
			}
		});

	}
}
