/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.util.ArrayList;
import java.util.SortedSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import de.cognicrypt.integrator.task.controllers.Validator;
import de.cognicrypt.integrator.task.models.XSLAttribute;

public class GroupXSLTagAttribute extends Group {

	private XSLAttribute selectedAttribute;
	private final Combo cmbAttributeType;
	private final Combo cmbAttributeName;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public GroupXSLTagAttribute(final Composite parent, final int style, final boolean showRemoveButton, final XSLAttribute attributeParam, final SortedSet<String> possibleCfrFeatures) {
		super(parent, style);

		// Set the attribute object first.
		setSelectedAttribute(attributeParam);
		setLayout(new GridLayout(3, false));

		this.cmbAttributeType = new Combo(this, SWT.READ_ONLY);
		final GridData gd_cmbAttributeType = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_cmbAttributeType.minimumWidth = 200;
		this.cmbAttributeType.setLayoutData(gd_cmbAttributeType);
		this.cmbAttributeType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getSelectedAttribute().setXSLAttributeName(GroupXSLTagAttribute.this.cmbAttributeType.getText());
			}
		});

		// initialize the dropdown with possible features

		this.cmbAttributeName = new Combo(this, SWT.BORDER);
		final GridData gd_cmbAttributeName = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_cmbAttributeName.minimumWidth = 200;
		this.cmbAttributeName.setLayoutData(gd_cmbAttributeName);
		if (possibleCfrFeatures != null) {
			for (final String currentCfrFeature : possibleCfrFeatures) {
				this.cmbAttributeName.add(currentCfrFeature);
			}
		}

		if (getSelectedAttribute().getXSLAttributeName().equals("")) {
			this.cmbAttributeName.select(0);
		} else {
			for (int i = 0; i < this.cmbAttributeName.getItems().length; i++) {
				if (this.cmbAttributeName.getItems()[i].equals(getSelectedAttribute().getXSLAttributeData())) {
					this.cmbAttributeName.select(i);
				}
			}
		}
		this.cmbAttributeName.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				getSelectedAttribute().setXSLAttributeData(Validator.getValidXMLString(GroupXSLTagAttribute.this.cmbAttributeName.getText()));
			}
		});

		if (showRemoveButton) {
			final Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					((CompositeToHoldSmallerUIElements) getParent().getParent()).removeXSLAttribute((getSelectedAttribute()));
					((CompositeToHoldSmallerUIElements) getParent().getParent()).updateClaferContainer();
				}
			});
			btnRemove.setText("Remove");
		}

	}

	/**
	 * The attribute drop down needs to be updated after changes have been made to keep them consistent.
	 *
	 * @param listOfPossibleAttributes represents the possible attributes based on the attributes already selected.
	 */
	public void updateAttributeDropDown(final ArrayList<String> listOfPossibleAttributes) {
		if (listOfPossibleAttributes != null) {
			this.cmbAttributeType.removeAll();

			// If the current selected attribute does not have an empty name, add the attribute to the dropdown.
			if (!getSelectedAttribute().getXSLAttributeName().equals("")) {
				this.cmbAttributeType.add(getSelectedAttribute().getXSLAttributeName());
			}

			// Add the list of possible attributes to the drop down.
			for (final String attribute : listOfPossibleAttributes) {
				this.cmbAttributeType.add(attribute);
			}

			// If the current selection is empty select the first one by default.
			if (getSelectedAttribute().getXSLAttributeName().equals("")) {
				this.cmbAttributeType.select(0);
			} else {
				// Otherwise get the index of the stored attribute name from the list of elements and select it.
				for (int i = 0; i < this.cmbAttributeType.getItems().length; i++) {
					final String eq = getSelectedAttribute().getXSLAttributeName();
					if (this.cmbAttributeType.getItems()[i].equals(eq)) {
						this.cmbAttributeType.select(i);
					}
				}
			}

			this.cmbAttributeType.notifyListeners(SWT.Selection, new Event());
		}

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the selectedAttribute
	 */
	public XSLAttribute getSelectedAttribute() {
		return this.selectedAttribute;
	}

	/**
	 * @param selectedAttribute the selectedAttribute to set
	 */
	private void setSelectedAttribute(final XSLAttribute selectedAttribute) {
		this.selectedAttribute = selectedAttribute;
	}

}
