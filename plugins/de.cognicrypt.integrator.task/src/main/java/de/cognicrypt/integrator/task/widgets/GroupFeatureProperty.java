/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.ClaferValidation;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ClaferProperty;

public class GroupFeatureProperty extends Composite {

	private ClaferProperty featureProperty;
	private final Text txtPropertyName;
	private Text txtPropertyType;
	private Combo comboPropertyType;

	private final ControlDecoration decorationName;
	private ControlDecoration decorationType;

	/**
	 * Create the composite.
	 *
	 * @param parent Composite that contains the feature property
	 * @param style SWT style identifiers
	 * @param featurePropertyParam
	 * @param showRemoveButton whether or not to show a remove button next to the feature property
	 * @param editable
	 * @param claferModel
	 */
	public GroupFeatureProperty(final Composite parent, final int style, final ClaferProperty featurePropertyParam, final boolean showRemoveButton, final ClaferModel claferModel) {
		super(parent, style);
		// Set the model for use first.
		setFeatureProperty(featurePropertyParam);

		setLayout(new GridLayout(5, false));

		final Label lblName = new Label(this, SWT.NONE);
		lblName.setText(Constants.FEATURE_PROPERTY_NAME);

		this.decorationName = new ControlDecoration(lblName, SWT.TOP | SWT.RIGHT);

		this.txtPropertyName = new Text(this, SWT.BORDER);
		this.txtPropertyName.setEditable(showRemoveButton);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		// do not claim space for all of the text if not available
		gridData.widthHint = 0;
		this.txtPropertyName.setLayoutData(gridData);
		this.txtPropertyName.setText(this.featureProperty.getPropertyName());
		this.txtPropertyName.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				GroupFeatureProperty.this.featureProperty.setPropertyName(GroupFeatureProperty.this.txtPropertyName.getText());
				super.focusLost(e);
			}
		});

		this.txtPropertyName.addModifyListener(arg0 -> ((CompositeToHoldSmallerUIElements) getParent().getParent()).notifyListeners(SWT.Selection, null));

		final Label lblNewLabel = new Label(this, SWT.NONE);
		if (this.featureProperty.getPropertyType().contains("=")) {
			lblNewLabel.setText(Constants.FEATURE_PROPERTY_TYPE_REFERENCE_RELATION);
		} else {
			lblNewLabel.setText(Constants.FEATURE_PROPERTY_TYPE_RELATION);
		}

		if (!showRemoveButton) {

			this.txtPropertyType = new Text(this, SWT.BORDER);
			this.txtPropertyType.setEditable(showRemoveButton);
			this.txtPropertyType.setLayoutData(gridData);
			this.txtPropertyType.setText(this.featureProperty.getPropertyType());
			this.txtPropertyType.addFocusListener(new FocusAdapter() {

				@Override
				public void focusLost(final FocusEvent e) {
					GroupFeatureProperty.this.featureProperty.setPropertyType(GroupFeatureProperty.this.txtPropertyType.getText());
					super.focusLost(e);
				}
			});
		} else {

			this.comboPropertyType = new Combo(this, SWT.NONE);
			this.comboPropertyType.setLayoutData(gridData);
			this.comboPropertyType.setText(this.featureProperty.getPropertyType());
			this.comboPropertyType.addFocusListener(new FocusAdapter() {

				@Override
				public void focusLost(final FocusEvent e) {
					GroupFeatureProperty.this.featureProperty.setPropertyType(GroupFeatureProperty.this.comboPropertyType.getText());
					super.focusLost(e);

				}
			});
			this.comboPropertyType.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent arg0) {
					// suggest a name if possible (the type in lower case if no primitve type)
					if (GroupFeatureProperty.this.txtPropertyName.getText().isEmpty()) {

						boolean isPrimitive = false;

						for (final String primitive : Constants.CLAFER_PRIMITIVE_TYPES) {
							if (primitive.equals(GroupFeatureProperty.this.comboPropertyType.getText())) {
								isPrimitive = true;
								break;
							}
						}

						if (!isPrimitive) {
							GroupFeatureProperty.this.txtPropertyName.setText(GroupFeatureProperty.this.comboPropertyType.getText().toLowerCase());
							GroupFeatureProperty.this.featureProperty.setPropertyName(GroupFeatureProperty.this.txtPropertyName.getText());
							((CompositeToHoldSmallerUIElements) getParent().getParent()).notifyListeners(SWT.Selection, null);
						}
					}
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {}
			});

			this.decorationType = new ControlDecoration(lblNewLabel, SWT.TOP | SWT.RIGHT);

			this.comboPropertyType.addModifyListener(arg0 -> ((CompositeToHoldSmallerUIElements) getParent().getParent()).notifyListeners(SWT.Selection, null));

			// suggest Clafer primitives as as type
			for (final String primitive : Constants.CLAFER_PRIMITIVE_TYPES) {
				this.comboPropertyType.add(primitive);
			}

			for (final ClaferFeature cfr : claferModel.getIf(ftr -> ftr.getFeatureType().equals(Constants.FeatureType.ABSTRACT))) {
				this.comboPropertyType.add(cfr.getFeatureName().toString());
			}

		}

		if (showRemoveButton) {

			final Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.setText(Constants.FEATURE_PROPERTY_REMOVE);
			btnRemove.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
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
		return this.featureProperty;
	}

	/**
	 * @param featureProperty the featureProperty to set
	 */
	private void setFeatureProperty(final ClaferProperty featureProperty) {
		this.featureProperty = featureProperty;
	}

	public boolean validate() {
		boolean valid = true;

		valid &= ClaferValidation.validateClaferName(this.txtPropertyName.getText(), true, this.decorationName);
		if (this.comboPropertyType != null) {
			valid &= ClaferValidation.validateClaferName(this.comboPropertyType.getText(), true, this.decorationType);
		}

		return valid;
	}

}
