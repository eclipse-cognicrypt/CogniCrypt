/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CompositeClaferFeedback extends Composite {

	private final Label lblFeedback;

	public CompositeClaferFeedback(final Composite parent, final int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		this.lblFeedback = new Label(this, SWT.NONE);
		this.lblFeedback.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	public void setFeedback(final String feedback) {
		this.lblFeedback.setText(feedback);
	}

}
