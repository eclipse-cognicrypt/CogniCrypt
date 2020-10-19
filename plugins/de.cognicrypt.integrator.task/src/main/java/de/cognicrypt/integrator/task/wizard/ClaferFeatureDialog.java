/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.util.function.Predicate;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.FeatureType;
import de.cognicrypt.integrator.task.controllers.ClaferValidation;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ClaferProperty;
import de.cognicrypt.integrator.task.widgets.CompositeToHoldSmallerUIElements;

public class ClaferFeatureDialog extends Dialog {

	private Text txtFeatureName;
	private CompositeToHoldSmallerUIElements featuresComposite;
	private CompositeToHoldSmallerUIElements constraintsComposite;
	private Button btnRadioAbstract;
	private Button btnRadioConcrete;

	private Label lblInheritance;
	private Combo comboInheritance;

	private final ClaferFeature resultClafer;
	private final ClaferModel claferModel;

	private ControlDecoration decorationName;
	private ControlDecoration decorationInheritance;

	public ClaferFeatureDialog(final Shell parentShell, final ClaferFeature modifiableClaferFeature, final ClaferModel claferModel) {
		super(parentShell);
		setShellStyle(SWT.RESIZE | SWT.CLOSE);

		this.resultClafer = modifiableClaferFeature;

		this.claferModel = new ClaferModel();

		// get abstract Clafer features that have already been created
		// exclude the feature currently being modified
		for (final ClaferFeature cfr : claferModel) {
			// TODO reconsider which Clafers have to be listed where
			if (!cfr.equals(this.resultClafer)) {
				this.claferModel.add(cfr);
			}
		}

		create();
	}

	public ClaferFeatureDialog(final Shell shell, final ClaferModel claferModel) {
		this(shell, new ClaferFeature(FeatureType.CONCRETE, "", ""), claferModel);
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, true));

		// restrict resizing the dialog below a minimum
		getShell().setMinimumSize(520, 600);

		final Label lblType = new Label(container, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblType.setText("Select the type");

		this.btnRadioAbstract = new Button(container, SWT.RADIO);
		this.btnRadioAbstract.setSelection(true);
		this.btnRadioAbstract.setText("Class");

		this.btnRadioAbstract.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				ClaferFeatureDialog.this.resultClafer.setFeatureType(FeatureType.ABSTRACT);
				validate();
				super.widgetSelected(e);
			}
		});

		this.btnRadioConcrete = new Button(container, SWT.RADIO);
		this.btnRadioConcrete.setText("Instance");
		this.btnRadioConcrete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				ClaferFeatureDialog.this.resultClafer.setFeatureType(FeatureType.CONCRETE);
				validate();
				super.widgetSelected(e);
			}
		});

		if (this.resultClafer.getFeatureType() == FeatureType.ABSTRACT) {
			this.btnRadioAbstract.setSelection(true);
			this.btnRadioConcrete.setSelection(false);
		} else {
			this.btnRadioAbstract.setSelection(false);
			this.btnRadioConcrete.setSelection(true);
		}

		final Label lblFeatureName = new Label(container, SWT.NONE);
		lblFeatureName.setText("Type in the name");
		this.decorationName = new ControlDecoration(lblFeatureName, SWT.TOP | SWT.RIGHT);

		this.txtFeatureName = new Text(container, SWT.BORDER);
		this.txtFeatureName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		this.txtFeatureName.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				ClaferFeatureDialog.this.resultClafer.setFeatureName(ClaferFeatureDialog.this.txtFeatureName.getText());
				super.focusLost(e);
			}
		});

		this.txtFeatureName.addModifyListener(arg0 -> validate());

		this.txtFeatureName.setText(this.resultClafer.getFeatureName());

		this.lblInheritance = new Label(container, SWT.NONE);
		this.lblInheritance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.decorationInheritance = new ControlDecoration(this.lblInheritance, SWT.TOP | SWT.RIGHT);

		this.comboInheritance = new Combo(container, SWT.NONE);
		this.comboInheritance.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		this.comboInheritance.add("");

		// add existing abstract features to inheritance combo
		final Predicate<? super ClaferFeature> isInheritanceCandidate = ftr -> ftr != this.resultClafer && ftr.getFeatureType() != Constants.FeatureType.CONCRETE;
		for (final ClaferFeature cfr : this.claferModel.getIf(isInheritanceCandidate)) {
			this.comboInheritance.add(cfr.getFeatureName().toString());
		}

		this.comboInheritance.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				ClaferFeatureDialog.this.resultClafer.setFeatureInheritance(ClaferFeatureDialog.this.comboInheritance.getText());
				super.focusLost(e);
			}
		});

		this.comboInheritance.addModifyListener(arg0 -> validate());

		this.comboInheritance.setText(this.resultClafer.getFeatureInheritance());

		final Button btnAddProperty = new Button(container, SWT.NONE);
		btnAddProperty.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				addClaferProperty();
				validate();
			}
		});
		btnAddProperty.setText("Add property");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		this.featuresComposite = new CompositeToHoldSmallerUIElements(container, SWT.BORDER, this.resultClafer.getFeatureProperties(), true, this.claferModel, this.resultClafer);
		final GridData gdPropertiesComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gdPropertiesComposite.minimumHeight = 100;
		this.featuresComposite.setLayoutData(gdPropertiesComposite);

		final Button btnAddConstraint = new Button(container, SWT.NONE);
		btnAddConstraint.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				addClaferConstraint();
			}
		});
		btnAddConstraint.setText("Add constraint");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		this.constraintsComposite = new CompositeToHoldSmallerUIElements(container, SWT.BORDER, this.resultClafer.getFeatureConstraints(), true, this.claferModel, this.resultClafer);
		final GridData gdConstraintsComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gdConstraintsComposite.minimumHeight = 100;
		this.constraintsComposite.setLayoutData(gdConstraintsComposite);

		final Listener validationListener = arg0 -> validate();

		this.featuresComposite.addListener(SWT.Selection, validationListener);

		validate();
		container.layout();

		return container;
	}

	private void validate() {

		boolean valid = true;

		if (this.txtFeatureName != null && this.comboInheritance != null) {
			valid &= ClaferValidation.validateClaferName(this.txtFeatureName.getText(), true, this.decorationName);
			valid &= ClaferValidation.validateClaferInheritance(this.comboInheritance.getText(), false, this.decorationInheritance);
		}

		if (this.featuresComposite != null) {
			valid &= this.featuresComposite.validate();
		}

		if (this.lblInheritance != null) {
			if (this.btnRadioAbstract.getSelection()) {
				this.lblInheritance.setText("Inherits from");

			} else if (this.btnRadioConcrete.getSelection()) {
				this.lblInheritance.setText("Implements");
			}
		}

		if (getButton(IDialogConstants.OK_ID) != null) {
			getButton(IDialogConstants.OK_ID).setEnabled(valid);
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
		validate();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 700);
	}

	private void addClaferProperty() {
		final ClaferProperty featureProperty = new ClaferProperty("", "");
		this.featuresComposite.addFeatureProperty(featureProperty, true, this.claferModel);
		this.resultClafer.setFeatureProperties(this.featuresComposite.getFeatureProperties());
	}

	private void addClaferConstraint() {
		final ClaferConstraintDialog cfrConstraintDialog = new ClaferConstraintDialog(getShell(), this.resultClafer, this.claferModel);

		// blocking call to Dialog.open() the dialog
		// it returns 0 on success
		if (cfrConstraintDialog.open() == 0) {
			this.constraintsComposite.addFeatureConstraint(cfrConstraintDialog.getResult(), true);
		}
	}

	@Override
	protected void okPressed() {
		this.resultClafer.setFeatureName(this.txtFeatureName.getText());
		this.resultClafer.setFeatureInheritance(this.comboInheritance.getText());
		super.okPressed();
	}

	public ClaferFeature getResult() {
		// remove empty properties and constraints
		this.featuresComposite.getFeatureProperties().removeIf(featureProp -> featureProp.getPropertyName().equals("") && featureProp.getPropertyType().equals(""));
		this.constraintsComposite.getFeatureConstraints().removeIf(constraint -> constraint.getConstraint().equals(""));

		this.resultClafer.setFeatureProperties(this.featuresComposite.getFeatureProperties());
		this.resultClafer.setFeatureConstraints(this.constraintsComposite.getFeatureConstraints());

		return this.resultClafer;
	}
}
