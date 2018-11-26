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

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.UIConstants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;

public class CompositePattern extends Composite {

	protected Composite compositeOptions;
	protected ScrolledComposite compositeScrolledOptions;

	protected Text txtName;
	protected ControlDecoration decorationName;

	protected String patternName;

	public CompositePattern(Composite parent) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout(2, false));

		patternName = "";

		Label lblName = new Label(this, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblName.setText("Name");

		txtName = new Text(this, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				patternName = txtName.getText();
				CompositePattern.this.notifyListeners(SWT.Selection, null);

			}
		});

		decorationName = new ControlDecoration(lblName, SWT.TOP | SWT.RIGHT);
	}

	public ClaferModel getResultModel() {
		return new ClaferModel();
	}

	/**
	 * validate the contents of the composite and return the success
	 * 
	 * @return <code>true</code> if content valid, <code>false</code> otherwise
	 */
	public boolean validate() {
		if (txtName.getText().isEmpty()) {
			decorationName.setImage(UIConstants.DEC_ERROR);
			decorationName.setDescriptionText("The name cannot be empty");
			decorationName.show();

			return false;
		} else {
			decorationName.hide();
		}

		return true;
	}

}
