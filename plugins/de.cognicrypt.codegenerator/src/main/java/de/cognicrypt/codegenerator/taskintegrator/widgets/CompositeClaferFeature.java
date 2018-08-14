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

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.wizard.ClaferFeatureDialog;
import de.cognicrypt.core.Constants;

public class CompositeClaferFeature extends Composite {

	private ClaferFeature claferFeature;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 */
	public CompositeClaferFeature(Composite parent, ClaferFeature claferFeatureParam) {
		super(parent, SWT.BORDER);
		// set the clafer feature first.
		setClaferFeature(claferFeatureParam);

		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.minimumWidth = 300;
		setLayoutData(gridData);

		setLayout(new GridLayout(2, false));

		Group grpClaferFeature = new Group(this, SWT.NONE);
		grpClaferFeature.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		grpClaferFeature.setText("Variability Construct");
		grpClaferFeature.setLayout(new GridLayout(4, false));

		Button btnModify = new Button(this, SWT.NONE);
		Button btnDelete = new Button(this, SWT.NONE);

		Label lblType = new Label(grpClaferFeature, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		if (claferFeature.getFeatureType().toString().equals(Constants.FeatureType.ABSTRACT.toString().toLowerCase())) {
			lblType.setText("Class");
		} else if (claferFeature.getFeatureType().toString().toLowerCase().equals(Constants.FeatureType.CONCRETE.toString().toLowerCase())) {
			lblType.setText("Instance");
		}

		Text txtFeatureName;
		txtFeatureName = new Text(grpClaferFeature, SWT.BORDER);
		txtFeatureName.setEditable(false);
		GridData gdFeatureName = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdFeatureName.widthHint = 0;
		txtFeatureName.setLayoutData(gdFeatureName);
		txtFeatureName.setText(claferFeature.getFeatureName());

		if (!claferFeature.getFeatureInheritance().isEmpty()) {
			Label lblInheritsFrom = new Label(grpClaferFeature, SWT.NONE);

			if (claferFeature.getFeatureInheritance().contains("->")) {
				lblInheritsFrom.setText("assigns");
			} else if (claferFeature.getFeatureType() == Constants.FeatureType.ABSTRACT) {
				lblInheritsFrom.setText("inherits from class");
			} else if (claferFeature.getFeatureType() == Constants.FeatureType.CONCRETE) {
				lblInheritsFrom.setText("implements");
			}

			Text txtFeatureInheritance;
			txtFeatureInheritance = new Text(grpClaferFeature, SWT.BORDER);
			txtFeatureInheritance.setEditable(false);
			GridData gdFeatureInheritance = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gdFeatureInheritance.widthHint = 0;
			txtFeatureInheritance.setLayoutData(gdFeatureInheritance);
			txtFeatureInheritance.setText(claferFeature.getFeatureInheritance());
		}

		if (claferFeature.getFeatureProperties().size() != 0) {
			Group grpClaferFeatureProperties = new Group(this, SWT.NONE);
			grpClaferFeatureProperties.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			grpClaferFeatureProperties.setLayout(new GridLayout(1, false));
			grpClaferFeatureProperties.setText("Clafer feature properties");
			CompositeToHoldGranularUIElements comp = ((CompositeToHoldGranularUIElements) getParent().getParent());
			CompositeToHoldSmallerUIElements smallerElements = new CompositeToHoldSmallerUIElements(grpClaferFeatureProperties, SWT.NONE, claferFeature
				.getFeatureProperties(), false, comp.getClaferModel());
			GridData gdPropertiesComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
			smallerElements.setLayoutData(gdPropertiesComposite);
		}

		if (claferFeature.getFeatureConstraints().size() != 0) {
			Group grpClaferFeatureConstraints = new Group(this, SWT.NONE);
			grpClaferFeatureConstraints.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			grpClaferFeatureConstraints.setText("Clafer feature constraints");
			grpClaferFeatureConstraints.setLayout(new GridLayout(1, false));
			CompositeToHoldGranularUIElements comp = ((CompositeToHoldGranularUIElements) getParent().getParent());
			CompositeToHoldSmallerUIElements smallerElements = new CompositeToHoldSmallerUIElements(grpClaferFeatureConstraints, SWT.NONE, claferFeature
				.getFeatureConstraints(), false, comp.getClaferModel());
			GridData gdConstraintsComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
			smallerElements.setLayoutData(gdConstraintsComposite);
		}

		btnModify.setText("Modify");
		btnModify.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CompositeToHoldGranularUIElements comp = ((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent());
				ClaferModel claferModel = comp.getClaferModel();
				ClaferFeatureDialog cfrFeatureDialog = new ClaferFeatureDialog(getShell(), claferFeature, claferModel);

				// remember parent for widgets will be disposed
				Composite parent = getParent();

				if (cfrFeatureDialog.open() == 0) {
					ClaferFeature resultFeature = cfrFeatureDialog.getResult();
					((CompositeToHoldGranularUIElements) btnModify.getParent().getParent().getParent()).modifyClaferFeature(claferFeature, resultFeature);

					// if features are missing, ask the user whether to implement them							
					ClaferModel missingFeatures = claferModel.getMissingFeatures(resultFeature);

					if (!missingFeatures.getClaferModel().isEmpty()) {
						MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
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
			public void widgetSelected(SelectionEvent e) {

				MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
				confirmationMessageBox.setMessage("This information will be lost. Do you really want to delete?");
				confirmationMessageBox.setText("Deleting Clafer Feature");
				int response = confirmationMessageBox.open();
				if (response == SWT.YES) {
					((CompositeToHoldGranularUIElements) btnDelete.getParent().getParent().getParent()).deleteClaferFeature(claferFeature);
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
		return claferFeature;
	}

	/**
	 * @param claferFeature
	 *        the claferFeature to set
	 */
	private void setClaferFeature(ClaferFeature claferFeature) {
		this.claferFeature = claferFeature;
	}
}
