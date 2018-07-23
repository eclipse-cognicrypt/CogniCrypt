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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.taskintegrator.controllers.ClaferValidation;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferProperty;
import de.cognicrypt.core.Constants;

public class GroupFeatureProperty extends Composite {

	private ClaferProperty featureProperty;
	private Text txtPropertyName;
	private Text txtPropertyType;
	private Combo comboPropertyType;

	private ControlDecoration decorationName;
	private ControlDecoration decorationType;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 *        Composite that contains the feature property
	 * @param style
	 *        SWT style identifiers
	 * @param featurePropertyParam
	 * @param showRemoveButton
	 *        whether or not to show a remove button next to the feature property
	 * @param editable
	 * @param claferModel
	 */
	public GroupFeatureProperty(Composite parent, int style, ClaferProperty featurePropertyParam, boolean showRemoveButton, ClaferModel claferModel) {
		super(parent, style);
		// Set the model for use first.
		this.setFeatureProperty(featurePropertyParam);

		setLayout(new GridLayout(5, false));

		Label lblName = new Label(this, SWT.NONE);
		lblName.setText(Constants.FEATURE_PROPERTY_NAME);

		decorationName = new ControlDecoration(lblName, SWT.TOP | SWT.RIGHT);

		txtPropertyName = new Text(this, SWT.BORDER);
		txtPropertyName.setEditable(showRemoveButton);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		// do not claim space for all of the text if not available
		gridData.widthHint = 0;
		txtPropertyName.setLayoutData(gridData);
		txtPropertyName.setText(featureProperty.getPropertyName());
		txtPropertyName.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				featureProperty.setPropertyName(txtPropertyName.getText());
				super.focusLost(e);
			}
		});

		txtPropertyName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				((CompositeToHoldSmallerUIElements) getParent().getParent()).notifyListeners(SWT.Selection, null);
			}
		});

		Label lblNewLabel = new Label(this, SWT.NONE);
		if (featureProperty.getPropertyType().contains("=")) {
			lblNewLabel.setText(Constants.FEATURE_PROPERTY_TYPE_REFERENCE_RELATION);
		} else {
			lblNewLabel.setText(Constants.FEATURE_PROPERTY_TYPE_RELATION);
		}

		if (!showRemoveButton) {

			txtPropertyType = new Text(this, SWT.BORDER);
			txtPropertyType.setEditable(showRemoveButton);
			txtPropertyType.setLayoutData(gridData);
			txtPropertyType.setText(featureProperty.getPropertyType());
			txtPropertyType.addFocusListener(new FocusAdapter() {

				@Override
				public void focusLost(FocusEvent e) {
					featureProperty.setPropertyType(txtPropertyType.getText());
					super.focusLost(e);
				}
			});
		} else {

			comboPropertyType = new Combo(this, SWT.NONE);
			comboPropertyType.setLayoutData(gridData);
			comboPropertyType.setText(featureProperty.getPropertyType());
			comboPropertyType.addFocusListener(new FocusAdapter() {

				@Override
				public void focusLost(FocusEvent e) {
					featureProperty.setPropertyType(comboPropertyType.getText());
					super.focusLost(e);

				}
			});
			comboPropertyType.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					// suggest a name if possible (the type in lower case if no primitve type)
					if (txtPropertyName.getText().isEmpty()) {

						boolean isPrimitive = false;

						for (String primitive : Constants.CLAFER_PRIMITIVE_TYPES) {
							if (primitive.equals(comboPropertyType.getText())) {
								isPrimitive = true;
								break;
							}
						}

						if (!isPrimitive) {
							txtPropertyName.setText(comboPropertyType.getText().toLowerCase());
							featureProperty.setPropertyName(txtPropertyName.getText());
							((CompositeToHoldSmallerUIElements) getParent().getParent()).notifyListeners(SWT.Selection, null);
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {}
			});

			decorationType = new ControlDecoration(lblNewLabel, SWT.TOP | SWT.RIGHT);

			comboPropertyType.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent arg0) {
					((CompositeToHoldSmallerUIElements) getParent().getParent()).notifyListeners(SWT.Selection, null);
				}
			});

			// suggest Clafer primitives as as type
			for (String primitive : Constants.CLAFER_PRIMITIVE_TYPES) {
				comboPropertyType.add(primitive);
			}

			for (ClaferFeature cfr : claferModel.getIf(ftr -> ftr.getFeatureType().equals(Constants.FeatureType.ABSTRACT))) {
				comboPropertyType.add(cfr.getFeatureName().toString());
			}

		}

		if (showRemoveButton) {

			Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setText(Constants.FEATURE_PROPERTY_REMOVE);
			btnRemove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					((CompositeToHoldSmallerUIElements) getParent().getParent()).removeFeatureProperty(getFeatureProperty());
					((CompositeToHoldSmallerUIElements) getParent().getParent()).notifyListeners(SWT.Selection, null);
					((CompositeToHoldSmallerUIElements) getParent().getParent()).updateClaferContainer();
				}
			});
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the featureProperty
	 */
	public ClaferProperty getFeatureProperty() {
		return featureProperty;
	}

	/**
	 * @param featureProperty
	 *        the featureProperty to set
	 */
	private void setFeatureProperty(ClaferProperty featureProperty) {
		this.featureProperty = featureProperty;
	}

	public boolean validate() {
		boolean valid = true;

		valid &= ClaferValidation.validateClaferName(txtPropertyName.getText(), true, decorationName);
		if (comboPropertyType != null) {
			valid &= ClaferValidation.validateClaferName(comboPropertyType.getText(), true, decorationType);
		}

		return valid;
	}

}
