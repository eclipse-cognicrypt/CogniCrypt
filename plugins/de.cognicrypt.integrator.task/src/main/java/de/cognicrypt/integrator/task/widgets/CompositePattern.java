/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import de.cognicrypt.integrator.task.UIConstants;

public class CompositePattern extends Composite {

	protected Composite compositeOptions;
	protected ScrolledComposite compositeScrolledOptions;

	protected Text txtName;
	protected ControlDecoration decorationName;

	protected String patternName;

	public CompositePattern(final Composite parent) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout(2, false));

		this.patternName = "";

		final Label lblName = new Label(this, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblName.setText("Name");

		this.txtName = new Text(this, SWT.BORDER);
		this.txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.txtName.addModifyListener(arg0 -> {
			CompositePattern.this.patternName = CompositePattern.this.txtName.getText();
			CompositePattern.this.notifyListeners(SWT.Selection, null);

		});

		this.decorationName = new ControlDecoration(lblName, SWT.TOP | SWT.RIGHT);
	}


	/**
	 * validate the contents of the composite and return the success
	 *
	 * @return <code>true</code> if content valid, <code>false</code> otherwise
	 */
	public boolean validate() {
		if (this.txtName.getText().isEmpty()) {
			this.decorationName.setImage(UIConstants.DEC_ERROR);
			this.decorationName.setDescriptionText("The name cannot be empty");
			this.decorationName.show();

			return false;
		} else {
			this.decorationName.hide();
		}

		return true;
	}

}
