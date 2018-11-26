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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CompositeClaferFeedback extends Composite {

	private Label lblFeedback;

	public CompositeClaferFeedback(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		lblFeedback = new Label(this, SWT.NONE);
		lblFeedback.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	public void setFeedback(String feedback) {
		lblFeedback.setText(feedback);
	}

}
