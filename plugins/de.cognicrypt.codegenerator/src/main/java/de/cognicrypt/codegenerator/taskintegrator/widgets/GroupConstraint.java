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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferConstraint;

public class GroupConstraint extends Composite {

	private ClaferConstraint constraint;
	private Text txtForFeatureConstraints;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 *        Composite that contains the constraint
	 * @param style
	 *        SWT style identifiers
	 * @param showRemoveButton
	 *        whether or not to show a remove button next to the constraint
	 */
	public GroupConstraint(Composite parent, int style, ClaferConstraint constraint, boolean showRemoveButton) {

		super(parent, style);
		// Set the model for use first.
		this.setConstraint(constraint);

		setLayout(new GridLayout(3, false));

		txtForFeatureConstraints = new Text(this, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		// do not claim space for all of the text if not available
		gridData.widthHint = 0;
		txtForFeatureConstraints.setLayoutData(gridData);
		txtForFeatureConstraints.setEditable(false);
		txtForFeatureConstraints.setText(constraint.getConstraint());

		txtForFeatureConstraints.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(org.eclipse.swt.events.FocusEvent e) {
				constraint.setConstraint(txtForFeatureConstraints.getText());
				super.focusLost(e);
			}
		});

		if (showRemoveButton) {
			Button btnModify = new Button(this, SWT.NONE);
			btnModify.setText("Modify");
			btnModify.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					((CompositeToHoldSmallerUIElements) getParent().getParent()).modifyFeature(constraint);
					((CompositeToHoldSmallerUIElements) getParent().getParent()).updateClaferContainer();
				}
			});

			Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setText("Remove");
			btnRemove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					((CompositeToHoldSmallerUIElements) getParent().getParent()).removeFeatureConstraint(constraint);
					((CompositeToHoldSmallerUIElements) getParent().getParent()).updateClaferContainer();
				}
			});
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public ClaferConstraint getConstraint() {
		return constraint;
	}

	public void setConstraint(ClaferConstraint constraint) {
		this.constraint = constraint;
	}

}
