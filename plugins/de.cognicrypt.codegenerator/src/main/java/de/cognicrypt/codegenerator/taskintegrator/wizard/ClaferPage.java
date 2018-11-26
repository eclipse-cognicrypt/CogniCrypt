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

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

import de.cognicrypt.codegenerator.taskintegrator.models.ClaferConstraint;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeClaferFeedback;
import de.cognicrypt.codegenerator.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;

public class ClaferPage extends PageForTaskIntegratorWizard {

	private CompositeClaferFeedback feedbackComposite;

	public ClaferPage() {
		super(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION, Constants.PAGE_TITLE_FOR_CLAFER_FILE_CREATION, Constants.PAGE_DESCRIPTION_FOR_CLAFER_FILE_CREATION);
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayout(new GridLayout(2, false));

		setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, this.getName()));
		getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));

		Button btnAddFeature = new Button(container, SWT.NONE);
		btnAddFeature.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		btnAddFeature.setText("Add Feature");
		btnAddFeature.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Point orig = compositeToHoldGranularUIElements.getOrigin();

				ClaferFeatureDialog cfrFeatureDialog = new ClaferFeatureDialog(getShell(), compositeToHoldGranularUIElements.getClaferModel());
				if (cfrFeatureDialog.open() == 0) {
					ClaferFeature tempFeature = cfrFeatureDialog.getResult();

					// if features are missing, ask the user whether to implement them							
					ClaferModel missingFeatures = compositeToHoldGranularUIElements.getClaferModel().getMissingFeatures(tempFeature);

					if (!missingFeatures.getClaferModel().isEmpty()) {
						MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
						dialog.setText("Additional features can be created");
						dialog.setMessage("Some of the used features don't exist yet. Should we create them for you?");

						if (dialog.open() == SWT.YES) {
							compositeToHoldGranularUIElements.getClaferModel().implementMissingFeatures(tempFeature);
						}
					}

					compositeToHoldGranularUIElements.getClaferModel().add(tempFeature);
					compositeToHoldGranularUIElements.addGranularClaferUIElements(tempFeature);
					compositeToHoldGranularUIElements.updateClaferContainer();

					compositeToHoldGranularUIElements.setOrigin(orig);

					checkModel();
				}

			}

		});

		Button btnAddPattern = new Button(container, SWT.NONE);
		btnAddPattern.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		btnAddPattern.setText("Add Pattern");
		btnAddPattern.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ClaferFeaturePatternDialog patternDialog = new ClaferFeaturePatternDialog(getShell());
				if (patternDialog.open() == 0) {
					for (ClaferFeature cfrFeature : patternDialog.getResultModel()) {
						compositeToHoldGranularUIElements.getClaferModel().add(cfrFeature);
						compositeToHoldGranularUIElements.addGranularClaferUIElements(cfrFeature);
						compositeToHoldGranularUIElements.updateClaferContainer();
					}
				}

				checkModel();

				super.widgetSelected(e);
			}
		});

		Button importFeatures = new Button(container, SWT.NONE);
		importFeatures.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		importFeatures.setText("Import Features");
		importFeatures.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Point orig = compositeToHoldGranularUIElements.getOrigin();

				ClaferImportDialog claferImportDialog = new ClaferImportDialog(getShell());
				if (claferImportDialog.open() == 0) {
					ClaferModel currentModel = compositeToHoldGranularUIElements.getClaferModel();
					currentModel.add(claferImportDialog.getResult());
					compositeToHoldGranularUIElements.updateClaferContainer();
				}

				compositeToHoldGranularUIElements.setOrigin(orig);

				feedbackComposite.setFeedback("Features imported");
				feedbackComposite.layout();

				checkModel();

				super.widgetSelected(e);
			}
		});

		Button exportFeatures = new Button(container, SWT.NONE);
		exportFeatures.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		exportFeatures.setText("Export Features");
		exportFeatures.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog saveDialog = new FileDialog(getShell(), SWT.SAVE);
				LocalDateTime date = LocalDateTime.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmm");
				saveDialog.setFileName(date.format(formatter) + ".dat");
				String[] filterNames = new String[] { "CogniCrypt binary Clafer model (*.dat)", "Human-readable non-importable Clafer file (*.cfr)" };
				String[] filterExtensions = new String[] { "*.dat", "*.cfr" };
				saveDialog.setFilterNames(filterNames);
				saveDialog.setFilterExtensions(filterExtensions);
				String targetFilename = saveDialog.open();
				if (targetFilename != null) {
					if (targetFilename.endsWith(".dat")) {
						compositeToHoldGranularUIElements.getClaferModel().toBinary(targetFilename);
					} else if (targetFilename.endsWith(".cfr")) {
						compositeToHoldGranularUIElements.getClaferModel().toFile(targetFilename);
					}
				}
				super.widgetSelected(e);
			}
		});

		feedbackComposite = new CompositeClaferFeedback(container, SWT.BORDER);
		((CompositeToHoldGranularUIElements) getCompositeToHoldGranularUIElements()).setCompositeClaferFeedback(feedbackComposite);
		feedbackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

	}

	public boolean checkModel() {

		Job compileJob = Job.create("Compile Clafer model", (ICoreRunnable) monitor -> {
			// UI updates can only be run in the display thread, 
			// so do them via Display.getDefault() 
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {

					feedbackComposite.setFeedback(" (compiling...)");

					// do the tedious work
					File cfrFile = new File(CodeGenUtils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH), "temporaryModel" + Constants.CFR_EXTENSION);
					compositeToHoldGranularUIElements.getClaferModel().toFile(cfrFile.getAbsolutePath());
					if (ClaferModel.compile(cfrFile.getAbsolutePath())) {
						feedbackComposite.setFeedback("Compilation successful");
					} else {
						feedbackComposite.setFeedback("Compilation error");
					}
				}
			});
		});

		// start the asynchronous task 
		compileJob.schedule();

		return false;
	}

	/**
	 * get the path to the ClaferModel as a compiled javascript file
	 * 
	 * @return {@link String} object that contains the absolute path to the compiled Clafer model, <code>null</code> if compilation failed
	 *
	 */
	public String getCompiledClaferModelPath() {
		File cfrFile = new File(CodeGenUtils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH), "currentClaferModel" + Constants.CFR_EXTENSION);
		compositeToHoldGranularUIElements.getClaferModel().toFile(cfrFile.getAbsolutePath());

		if (ClaferModel.compile(cfrFile.getAbsolutePath())) {
			return new File(CodeGenUtils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH), "currentClaferModel" + Constants.JS_EXTENSION).getAbsolutePath();
		}

		return null;
	}

	public void initializeClaferModel() {
		PageForTaskIntegratorWizard page = (PageForTaskIntegratorWizard) ((TaskIntegrationWizard) getWizard()).getTIPageByName(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD);
		if (page.getCompositeChoiceForModeOfWizard() != null) {
			String taskName = page.getCompositeChoiceForModeOfWizard().getTxtForTaskName().getText();
			String taskDescription = page.getCompositeChoiceForModeOfWizard().getTxtTaskDescription().getText();
			String descriptionConstraint = "description = \"" + taskDescription + "\"";

			if (this.compositeToHoldGranularUIElements.getClaferModel().getClaferModel().isEmpty()) {
				String defaultFeatureSetPath = CodeGenUtils
					.getResourceFromWithin(Constants.CFR_BIN_FILE_DIRECTORY_PATH + Constants.DEFAULT_FEATURE_SET_FILE + Constants.CFR_BIN_EXTENSION).getAbsolutePath();
				ClaferModel defaultFeaturesModel = ClaferModel.createFromBinaries(defaultFeatureSetPath);

				ClaferFeature taskFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, taskName, "Task");

				ArrayList<ClaferConstraint> constraints = new ArrayList<>();
				constraints.add(new ClaferConstraint(descriptionConstraint));

				taskFeature.setFeatureConstraints(constraints);

				compositeToHoldGranularUIElements.getClaferModel().add(defaultFeaturesModel);
				compositeToHoldGranularUIElements.getClaferModel().add(taskFeature);

				compositeToHoldGranularUIElements.getClaferModel().implementMissingFeatures(taskFeature);
				compositeToHoldGranularUIElements.updateClaferContainer();
			} else {
				for (ClaferFeature claferFeature : compositeToHoldGranularUIElements.getClaferModel()) {
					if (claferFeature.getFeatureInheritance().equals("Task")) {
						claferFeature.setFeatureName(taskName);

						for (ClaferConstraint constraint : claferFeature.getFeatureConstraints()) {
							if (constraint.getConstraint().startsWith("description")) {
								constraint.setConstraint(descriptionConstraint);
							}
						}
					}
				}
				this.compositeToHoldGranularUIElements.updateClaferContainer();
			}
		}
	}

}
