/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.wizard.ClaferFeatureDialog;

public class CompositeClaferFeature extends Composite {

	private ClaferFeature claferFeature;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 */
	public CompositeClaferFeature(final Composite parent, final ClaferFeature claferFeatureParam) {
		super(parent, SWT.BORDER);
		// set the clafer feature first.
		setClaferFeature(claferFeatureParam);

		final GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.minimumWidth = 300;
		setLayoutData(gridData);

		setLayout(new GridLayout(2, false));

		final Group grpClaferFeature = new Group(this, SWT.NONE);
		grpClaferFeature.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		grpClaferFeature.setText("Variability Construct");
		grpClaferFeature.setLayout(new GridLayout(4, false));

		final Button btnModify = new Button(this, SWT.NONE);
		final Button btnDelete = new Button(this, SWT.NONE);

		final Label lblType = new Label(grpClaferFeature, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		if (this.claferFeature.getFeatureType().toString().equals(Constants.FeatureType.ABSTRACT.toString().toLowerCase())) {
			lblType.setText("Class");
		} else if (this.claferFeature.getFeatureType().toString().toLowerCase().equals(Constants.FeatureType.CONCRETE.toString().toLowerCase())) {
			lblType.setText("Instance");
		}

		Text txtFeatureName;
		txtFeatureName = new Text(grpClaferFeature, SWT.BORDER);
		txtFeatureName.setEditable(false);
		final GridData gdFeatureName = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdFeatureName.widthHint = 0;
		txtFeatureName.setLayoutData(gdFeatureName);
		txtFeatureName.setText(this.claferFeature.getFeatureName());

		if (!this.claferFeature.getFeatureInheritance().isEmpty()) {
			final Label lblInheritsFrom = new Label(grpClaferFeature, SWT.NONE);

			if (this.claferFeature.getFeatureInheritance().contains("->")) {
				lblInheritsFrom.setText("assigns");
			} else if (this.claferFeature.getFeatureType() == Constants.FeatureType.ABSTRACT) {
				lblInheritsFrom.setText("inherits from class");
			} else if (this.claferFeature.getFeatureType() == Constants.FeatureType.CONCRETE) {
				lblInheritsFrom.setText("implements");
			}

			Text txtFeatureInheritance;
			txtFeatureInheritance = new Text(grpClaferFeature, SWT.BORDER);
			txtFeatureInheritance.setEditable(false);
			final GridData gdFeatureInheritance = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gdFeatureInheritance.widthHint = 0;
			txtFeatureInheritance.setLayoutData(gdFeatureInheritance);
			txtFeatureInheritance.setText(this.claferFeature.getFeatureInheritance());
		}

		if (this.claferFeature.getFeatureProperties().size() != 0) {
			final Group grpClaferFeatureProperties = new Group(this, SWT.NONE);
			grpClaferFeatureProperties.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			grpClaferFeatureProperties.setLayout(new GridLayout(1, false));
			grpClaferFeatureProperties.setText("Clafer feature properties");
			final CompositeToHoldGranularUIElements comp = ((CompositeToHoldGranularUIElements) getParent().getParent());
			final CompositeToHoldSmallerUIElements smallerElements =
					new CompositeToHoldSmallerUIElements(grpClaferFeatureProperties, SWT.NONE, this.claferFeature.getFeatureProperties(), false, comp.getClaferModel());
			final GridData gdPropertiesComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
			smallerElements.setLayoutData(gdPropertiesComposite);
		}

		if (this.claferFeature.getFeatureConstraints().size() != 0) {
			final Group grpClaferFeatureConstraints = new Group(this, SWT.NONE);
			grpClaferFeatureConstraints.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			grpClaferFeatureConstraints.setText("Clafer feature constraints");
			grpClaferFeatureConstraints.setLayout(new GridLayout(1, false));
			final CompositeToHoldGranularUIElements comp = ((CompositeToHoldGranularUIElements) getParent().getParent());
			final CompositeToHoldSmallerUIElements smallerElements =
					new CompositeToHoldSmallerUIElements(grpClaferFeatureConstraints, SWT.NONE, this.claferFeature.getFeatureConstraints(), false, comp.getClaferModel());
			final GridData gdConstraintsComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
			smallerElements.setLayoutData(gdConstraintsComposite);
		}

		btnModify.setText("Modify");
		btnModify.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final CompositeToHoldGranularUIElements comp = ((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent());
				final ClaferModel claferModel = comp.getClaferModel();
				final ClaferFeatureDialog cfrFeatureDialog = new ClaferFeatureDialog(getShell(), CompositeClaferFeature.this.claferFeature, claferModel);

				// remember parent for widgets will be disposed
				final Composite parent = getParent();

				if (cfrFeatureDialog.open() == 0) {
					final ClaferFeature resultFeature = cfrFeatureDialog.getResult();
					((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent()).modifyClaferFeature(CompositeClaferFeature.this.claferFeature, resultFeature);

					// if features are missing, ask the user whether to implement them
					final ClaferModel missingFeatures = claferModel.getMissingFeatures(resultFeature);

					if (!missingFeatures.getClaferModel().isEmpty()) {
						final MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
						dialog.setText("Additional features can be created");
						dialog.setMessage("Some of the used features don't exist yet. Should we create them for you?");

						if (dialog.open() == SWT.YES) {
							claferModel.implementMissingFeatures(resultFeature);
						}
					}

					// rebuild the UI
					comp.updateClaferContainer();
				}

			}
		});

		btnDelete.setText("Delete");
		btnDelete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				final MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
				confirmationMessageBox.setMessage("This information will be lost. Do you really want to delete?");
				confirmationMessageBox.setText("Deleting Clafer Feature");
				final int response = confirmationMessageBox.open();
				if (response == SWT.YES) {
					((CompositeToHoldGranularUIElements) btnDelete.getParent().getParent().getParent()).deleteClaferFeature(CompositeClaferFeature.this.claferFeature);
				}

			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the claferFeature
	 */
	public ClaferFeature getClaferFeature() {
		return this.claferFeature;
	}

	/**
	 * @param claferFeature the claferFeature to set
	 */
	private void setClaferFeature(final ClaferFeature claferFeature) {
		this.claferFeature = claferFeature;
	}
}
