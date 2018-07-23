/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.cognicrypt.codegenerator.taskintegrator.models.XSLAttribute;
import de.cognicrypt.codegenerator.taskintegrator.models.XSLTag;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldSmallerUIElements;
import de.cognicrypt.codegenerator.taskintegrator.widgets.GroupXSLTagAttribute;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.XSLTags;

public class XSLTagDialog extends Dialog {

	private CompositeToHoldSmallerUIElements compositeForXSLAttributes;
	private Button btnAddAttribute;
	private Combo comboXSLTags;
	private String currentSelectionStringOncomboXSLTags;

	private XSLTag tag;
	private SortedSet<String> cfrFeatures;
	private HashMap<String, String> valuesForTagData;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public XSLTagDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public XSLTagDialog(Shell parentShell, HashMap<String, String> valuesForTagData) {
		this(parentShell);
		// accept all the possible values to be displayed along with their corresponding tag data.
		this.setValuesForTagData(valuesForTagData);
		this.cfrFeatures = new TreeSet<String>(getValuesForTagData().keySet());
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		getShell().setMinimumSize(900, 400);
		comboXSLTags = new Combo(container, SWT.READ_ONLY);

		GridData gd_comboXSLTags = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		comboXSLTags.setLayoutData(gd_comboXSLTags);

		// Add all the available tags to the combo box.
		for (XSLTags tag : Constants.XSLTags.values()) {
			comboXSLTags.add(tag.getXSLTagFaceName());
		}

		btnAddAttribute = new Button(container, SWT.NONE);
		btnAddAttribute.setText("Add Attribute");

		compositeForXSLAttributes = new CompositeToHoldSmallerUIElements(container, SWT.NONE, null, true, null);
		GridData gd_compositeForProperties = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		compositeForXSLAttributes.setLayoutData(gd_compositeForProperties);

		// Select the first one by default
		comboXSLTags.select(0);

		btnAddAttribute.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Add the UI element for the attribute with the remove button, and pass the selected XSL tag.
				SortedSet<String> possibleCfrFeatures = cfrFeatures;
				compositeForXSLAttributes.addXSLAttribute(true, comboXSLTags.getText(), possibleCfrFeatures);
				// Update all the drop down menus for attribute UIs to keep them consistent after adding a new attribute.
				ArrayList<String> possAttributes = compositeForXSLAttributes.getListOfPossibleAttributes(comboXSLTags.getText());
				compositeForXSLAttributes.updateDropDownsForXSLAttributes(possAttributes);
			}

		});

		comboXSLTags.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// If attributes have been added to the tag, ask for confirmation from the user.
				if (compositeForXSLAttributes.getXSLAttributes().size() > 0) {
					MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
					confirmationMessageBox.setMessage("Are you sure you wish to change the tag? All attributes will be lost.");
					confirmationMessageBox.setText("Changing the XSL tag");
					int response = confirmationMessageBox.open();
					if (response == SWT.YES) {
						// If the response is positive, dispose all the attributes, change the current selection object,
						// and update all the drop downs to keep them consistent with the last added attribute.
						disposeAllAttributes();
						setCurrentSelectionStringOncomboXSLTags(comboXSLTags.getText());
						compositeForXSLAttributes.updateDropDownsForXSLAttributes(compositeForXSLAttributes.getListOfPossibleAttributes(comboXSLTags.getText()));
					} else {
						// If the user opts out of the change, replace the already changed value with the old one.
						for (int i = 0; i < comboXSLTags.getItemCount(); i++) {
							if (comboXSLTags.getItems()[i].equals(getCurrentSelectionStringOncomboXSLTags())) {
								comboXSLTags.select(i);
							}
						}
					}
				} else {
					// If there are no attributes added, update the selection of the tag in the object. 
					setCurrentSelectionStringOncomboXSLTags(comboXSLTags.getText());
				}

				// Disable the add button if there are no attributes possible. E.g. the choose tag.
				setEnabledForAddAttributeButton();

			}

		});

		// Adding the notification of the selection listener for the default selection after the listener has been added.
		comboXSLTags.notifyListeners(SWT.Selection, new Event());
		return container;
	}

	/**
	 * Remove all the UI representations of the attributes, clear the list of attributes and update the lowest Y axis value.
	 */
	private void disposeAllAttributes() {
		for (Control uiRepresentationOfXSLAttributes : ((Composite) compositeForXSLAttributes.getContent()).getChildren()) {
			uiRepresentationOfXSLAttributes.dispose();
		}

		compositeForXSLAttributes.getXSLAttributes().clear();
		compositeForXSLAttributes.setLowestWidgetYAxisValue(0);
	}

	/**
	 * Update the access to the add button based on the availability of attributes.
	 */
	private void setEnabledForAddAttributeButton() {

		for (XSLTags xslTag : Constants.XSLTags.values()) {
			if (comboXSLTags.getText().equals(xslTag.getXSLTagFaceName())) {
				if (xslTag.getXSLAttributes().length == 0) {
					btnAddAttribute.setEnabled(false);
				} else {
					btnAddAttribute.setEnabled(true);
				}
				break;
			}
		}

	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {

		Constants.XSLTags selectedTag = null;
		ArrayList<XSLAttribute> attributesOnThisTag = new ArrayList<XSLAttribute>();

		for (Constants.XSLTags tagUnderConsideration : Constants.XSLTags.values()) {
			if (tagUnderConsideration.getXSLTagFaceName().equals(getCurrentSelectionStringOncomboXSLTags())) {
				selectedTag = tagUnderConsideration;
			}
		}

		for (Control attribute : ((Composite) compositeForXSLAttributes.getContent()).getChildren()) {
			attributesOnThisTag.add(((GroupXSLTagAttribute) attribute).getSelectedAttribute());
		}

		// Replace all the place holder values with the appropriate tag data.

		for (XSLAttribute xslAttribute : attributesOnThisTag) {
			if (valuesForTagData != null) {
				xslAttribute.setXSLAttributeData((valuesForTagData.get(xslAttribute.getXSLAttributeData()) == null) ? xslAttribute.getXSLAttributeData()
					: valuesForTagData.get(xslAttribute.getXSLAttributeData()));
			}
		}

		setTag(new XSLTag(selectedTag, attributesOnThisTag));
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	/**
	 * @return the currentSelectionStringOncomboXSLTags
	 */
	public String getCurrentSelectionStringOncomboXSLTags() {
		return currentSelectionStringOncomboXSLTags;
	}

	/**
	 * @param currentSelectionStringOncomboXSLTags
	 *        the currentSelectionStringOncomboXSLTags to set
	 */
	private void setCurrentSelectionStringOncomboXSLTags(String currentSelectionStringOncomboXSLTags) {
		this.currentSelectionStringOncomboXSLTags = currentSelectionStringOncomboXSLTags;
	}

	/**
	 * @return the tag
	 */
	public XSLTag getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *        the tag to set
	 */
	private void setTag(XSLTag tag) {
		this.tag = tag;
	}

	/**
	 * @return the valuesForTagData
	 */
	public HashMap<String, String> getValuesForTagData() {
		return valuesForTagData;
	}

	/**
	 * @param valuesForTagData
	 *        the valuesForTagData to set
	 */
	public void setValuesForTagData(HashMap<String, String> valuesForTagData) {
		this.valuesForTagData = valuesForTagData;
	}

}
