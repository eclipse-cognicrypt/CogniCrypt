/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import de.cognicrypt.integrator.task.models.ClaferConstraint;

public class GroupConstraint extends Composite {

	private ClaferConstraint constraint;
	private final Text txtForFeatureConstraints;

	/**
	 * Create the composite.
	 *
	 * @param parent Composite that contains the constraint
	 * @param style SWT style identifiers
	 * @param showRemoveButton whether or not to show a remove button next to the constraint
	 */
	public GroupConstraint(final Composite parent, final int style, final ClaferConstraint constraint, final boolean showRemoveButton) {

		super(parent, style);
		// Set the model for use first.
		setConstraint(constraint);

		setLayout(new GridLayout(3, false));

		this.txtForFeatureConstraints = new Text(this, SWT.BORDER);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		// do not claim space for all of the text if not available
		gridData.widthHint = 0;
		this.txtForFeatureConstraints.setLayoutData(gridData);
		this.txtForFeatureConstraints.setEditable(false);
		this.txtForFeatureConstraints.setText(constraint.getConstraint());

		this.txtForFeatureConstraints.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final org.eclipse.swt.events.FocusEvent e) {
				constraint.setConstraint(GroupConstraint.this.txtForFeatureConstraints.getText());
				super.focusLost(e);
			}
		});

		if (showRemoveButton) {
			final Button btnModify = new Button(this, SWT.NONE);
			btnModify.setText("Modify");
			btnModify.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					((CompositeToHoldSmallerUIElements) getParent().getParent()).modifyFeature(constraint);
					((CompositeToHoldSmallerUIElements) getParent().getParent()).updateClaferContainer();
				}
			});

			final Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setText("Remove");
			btnRemove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
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
		return this.constraint;
	}

	public void setConstraint(final ClaferConstraint constraint) {
		this.constraint = constraint;
	}

}
