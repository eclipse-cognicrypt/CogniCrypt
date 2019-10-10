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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.CodeDependency;

public class CompositeForCodeTab extends Composite {

	public CompositeForCodeTab(final Composite parent, final int style, final Answer answer) {
		super(parent, style);

		// Non-editable text box containing answer value
		final Text txtBoxAnswers = new Text(this, SWT.BORDER);
		txtBoxAnswers.setBounds(5, 5, 210, 25);
		txtBoxAnswers.setEditable(false);
		txtBoxAnswers.setText(answer.getValue());

		// Code dependency text field
		final Text txtValue = new Text(this, SWT.BORDER);
		txtValue.setBounds(220, 5, 200, 25);
		txtValue.setVisible(true);

		final CodeDependency codeDependency = new CodeDependency();

		if (answer.getCodeDependencies() != null) {
			for (final CodeDependency cd : answer.getCodeDependencies()) {
				if (cd.getValue() != null) {
					txtValue.setText(cd.getValue());
					codeDependency.setValue(txtValue.getText());
				}
			}
		}

		txtValue.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				codeDependency.setValue(txtValue.getText());
			}
		});

		final ArrayList<CodeDependency> codeDependencies = new ArrayList<CodeDependency>();
		codeDependencies.add(codeDependency);
		answer.setCodeDependencies(codeDependencies);

	}

	@Override
	protected void checkSubclass() {
		// To disable the check that prevents subclassing of SWT components
	}

}
