/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

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
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.XSLTags;
import de.cognicrypt.integrator.task.models.XSLAttribute;
import de.cognicrypt.integrator.task.models.XSLTag;
import de.cognicrypt.integrator.task.widgets.CompositeToHoldSmallerUIElements;
import de.cognicrypt.integrator.task.widgets.GroupXSLTagAttribute;

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
	public XSLTagDialog(final Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public XSLTagDialog(final Shell parentShell, final HashMap<String, String> valuesForTagData) {
		this(parentShell);
		// accept all the possible values to be displayed along with their corresponding tag data.
		setValuesForTagData(valuesForTagData);
		this.cfrFeatures = new TreeSet<String>(getValuesForTagData().keySet());
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		getShell().setMinimumSize(900, 400);
		this.comboXSLTags = new Combo(container, SWT.READ_ONLY);

		final GridData gd_comboXSLTags = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		this.comboXSLTags.setLayoutData(gd_comboXSLTags);

		// Add all the available tags to the combo box.
		for (final XSLTags tag : Constants.XSLTags.values()) {
			this.comboXSLTags.add(tag.getXSLTagFaceName());
		}

		this.btnAddAttribute = new Button(container, SWT.NONE);
		this.btnAddAttribute.setText("Add Attribute");

		this.compositeForXSLAttributes = new CompositeToHoldSmallerUIElements(container, SWT.NONE, null, true, null);
		final GridData gd_compositeForProperties = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		this.compositeForXSLAttributes.setLayoutData(gd_compositeForProperties);

		// Select the first one by default
		this.comboXSLTags.select(0);

		this.btnAddAttribute.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// Add the UI element for the attribute with the remove button, and pass the selected XSL tag.
				final SortedSet<String> possibleCfrFeatures = XSLTagDialog.this.cfrFeatures;
				XSLTagDialog.this.compositeForXSLAttributes.addXSLAttribute(true, XSLTagDialog.this.comboXSLTags.getText(), possibleCfrFeatures);
				// Update all the drop down menus for attribute UIs to keep them consistent after adding a new attribute.
				final ArrayList<String> possAttributes = XSLTagDialog.this.compositeForXSLAttributes.getListOfPossibleAttributes(XSLTagDialog.this.comboXSLTags.getText());
				XSLTagDialog.this.compositeForXSLAttributes.updateDropDownsForXSLAttributes(possAttributes);
			}

		});

		this.comboXSLTags.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// If attributes have been added to the tag, ask for confirmation from the user.
				if (XSLTagDialog.this.compositeForXSLAttributes.getXSLAttributes().size() > 0) {
					final MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
					confirmationMessageBox.setMessage("Are you sure you wish to change the tag? All attributes will be lost.");
					confirmationMessageBox.setText("Changing the XSL tag");
					final int response = confirmationMessageBox.open();
					if (response == SWT.YES) {
						// If the response is positive, dispose all the attributes, change the current selection object,
						// and update all the drop downs to keep them consistent with the last added attribute.
						disposeAllAttributes();
						setCurrentSelectionStringOncomboXSLTags(XSLTagDialog.this.comboXSLTags.getText());
						XSLTagDialog.this.compositeForXSLAttributes
								.updateDropDownsForXSLAttributes(XSLTagDialog.this.compositeForXSLAttributes.getListOfPossibleAttributes(XSLTagDialog.this.comboXSLTags.getText()));
					} else {
						// If the user opts out of the change, replace the already changed value with the old one.
						for (int i = 0; i < XSLTagDialog.this.comboXSLTags.getItemCount(); i++) {
							if (XSLTagDialog.this.comboXSLTags.getItems()[i].equals(getCurrentSelectionStringOncomboXSLTags())) {
								XSLTagDialog.this.comboXSLTags.select(i);
							}
						}
					}
				} else {
					// If there are no attributes added, update the selection of the tag in the object.
					setCurrentSelectionStringOncomboXSLTags(XSLTagDialog.this.comboXSLTags.getText());
				}

				// Disable the add button if there are no attributes possible. E.g. the choose tag.
				setEnabledForAddAttributeButton();

			}

		});

		// Adding the notification of the selection listener for the default selection after the listener has been added.
		this.comboXSLTags.notifyListeners(SWT.Selection, new Event());
		return container;
	}

	/**
	 * Remove all the UI representations of the attributes, clear the list of attributes and update the lowest Y axis value.
	 */
	private void disposeAllAttributes() {
		for (final Control uiRepresentationOfXSLAttributes : ((Composite) this.compositeForXSLAttributes.getContent()).getChildren()) {
			uiRepresentationOfXSLAttributes.dispose();
		}

		this.compositeForXSLAttributes.getXSLAttributes().clear();
		this.compositeForXSLAttributes.setLowestWidgetYAxisValue(0);
	}

	/**
	 * Update the access to the add button based on the availability of attributes.
	 */
	private void setEnabledForAddAttributeButton() {

		for (final XSLTags xslTag : Constants.XSLTags.values()) {
			if (this.comboXSLTags.getText().equals(xslTag.getXSLTagFaceName())) {
				if (xslTag.getXSLAttributes().length == 0) {
					this.btnAddAttribute.setEnabled(false);
				} else {
					this.btnAddAttribute.setEnabled(true);
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
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {

		Constants.XSLTags selectedTag = null;
		final ArrayList<XSLAttribute> attributesOnThisTag = new ArrayList<XSLAttribute>();

		for (final Constants.XSLTags tagUnderConsideration : Constants.XSLTags.values()) {
			if (tagUnderConsideration.getXSLTagFaceName().equals(getCurrentSelectionStringOncomboXSLTags())) {
				selectedTag = tagUnderConsideration;
			}
		}

		for (final Control attribute : ((Composite) this.compositeForXSLAttributes.getContent()).getChildren()) {
			attributesOnThisTag.add(((GroupXSLTagAttribute) attribute).getSelectedAttribute());
		}

		// Replace all the place holder values with the appropriate tag data.

		for (final XSLAttribute xslAttribute : attributesOnThisTag) {
			if (this.valuesForTagData != null) {
				xslAttribute.setXSLAttributeData((this.valuesForTagData.get(xslAttribute.getXSLAttributeData()) == null) ? xslAttribute.getXSLAttributeData()
						: this.valuesForTagData.get(xslAttribute.getXSLAttributeData()));
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
		return this.currentSelectionStringOncomboXSLTags;
	}

	/**
	 * @param currentSelectionStringOncomboXSLTags the currentSelectionStringOncomboXSLTags to set
	 */
	private void setCurrentSelectionStringOncomboXSLTags(final String currentSelectionStringOncomboXSLTags) {
		this.currentSelectionStringOncomboXSLTags = currentSelectionStringOncomboXSLTags;
	}

	/**
	 * @return the tag
	 */
	public XSLTag getTag() {
		return this.tag;
	}

	/**
	 * @param tag the tag to set
	 */
	private void setTag(final XSLTag tag) {
		this.tag = tag;
	}

	/**
	 * @return the valuesForTagData
	 */
	public HashMap<String, String> getValuesForTagData() {
		return this.valuesForTagData;
	}

	/**
	 * @param valuesForTagData the valuesForTagData to set
	 */
	public void setValuesForTagData(final HashMap<String, String> valuesForTagData) {
		this.valuesForTagData = valuesForTagData;
	}

}
