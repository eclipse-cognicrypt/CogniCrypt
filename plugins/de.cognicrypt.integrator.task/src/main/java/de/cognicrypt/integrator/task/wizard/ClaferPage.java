/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

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
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.ClaferConstraint;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.widgets.CompositeClaferFeedback;
import de.cognicrypt.integrator.task.widgets.CompositeToHoldGranularUIElements;
import de.cognicrypt.utils.Utils;

public class ClaferPage extends PageForTaskIntegratorWizard {

	private CompositeClaferFeedback feedbackComposite;

	public ClaferPage() {
		super(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION, Constants.PAGE_TITLE_FOR_CLAFER_FILE_CREATION, Constants.PAGE_DESCRIPTION_FOR_CLAFER_FILE_CREATION);
	}

	@Override
	public void createControl(final Composite parent) {

		final Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayout(new GridLayout(2, false));

		setCompositeToHoldGranularUIElements(new CompositeToHoldGranularUIElements(container, getName()));
		getCompositeToHoldGranularUIElements().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));

		final Button btnAddFeature = new Button(container, SWT.NONE);
		btnAddFeature.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		btnAddFeature.setText("Add Feature");
		btnAddFeature.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				final Point orig = ClaferPage.this.compositeToHoldGranularUIElements.getOrigin();

				final ClaferFeatureDialog cfrFeatureDialog = new ClaferFeatureDialog(getShell(), ClaferPage.this.compositeToHoldGranularUIElements.getClaferModel());
				if (cfrFeatureDialog.open() == 0) {
					final ClaferFeature tempFeature = cfrFeatureDialog.getResult();

					// if features are missing, ask the user whether to implement them
					final ClaferModel missingFeatures = ClaferPage.this.compositeToHoldGranularUIElements.getClaferModel().getMissingFeatures(tempFeature);

					if (!missingFeatures.getClaferModel().isEmpty()) {
						final MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
						dialog.setText("Additional features can be created");
						dialog.setMessage("Some of the used features don't exist yet. Should we create them for you?");

						if (dialog.open() == SWT.YES) {
							ClaferPage.this.compositeToHoldGranularUIElements.getClaferModel().implementMissingFeatures(tempFeature);
						}
					}

					ClaferPage.this.compositeToHoldGranularUIElements.getClaferModel().add(tempFeature);
					ClaferPage.this.compositeToHoldGranularUIElements.addGranularClaferUIElements(tempFeature);
					ClaferPage.this.compositeToHoldGranularUIElements.updateClaferContainer();

					ClaferPage.this.compositeToHoldGranularUIElements.setOrigin(orig);

					checkModel();
				}

			}

		});

		final Button btnAddPattern = new Button(container, SWT.NONE);
		btnAddPattern.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		btnAddPattern.setText("Add Pattern");
		btnAddPattern.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final ClaferFeaturePatternDialog patternDialog = new ClaferFeaturePatternDialog(getShell());
				if (patternDialog.open() == 0) {
					for (final ClaferFeature cfrFeature : patternDialog.getResultModel()) {
						ClaferPage.this.compositeToHoldGranularUIElements.getClaferModel().add(cfrFeature);
						ClaferPage.this.compositeToHoldGranularUIElements.addGranularClaferUIElements(cfrFeature);
						ClaferPage.this.compositeToHoldGranularUIElements.updateClaferContainer();
					}
				}

				checkModel();

				super.widgetSelected(e);
			}
		});

		final Button importFeatures = new Button(container, SWT.NONE);
		importFeatures.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		importFeatures.setText("Import Features");
		importFeatures.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Point orig = ClaferPage.this.compositeToHoldGranularUIElements.getOrigin();

				final ClaferImportDialog claferImportDialog = new ClaferImportDialog(getShell());
				if (claferImportDialog.open() == 0) {
					final ClaferModel currentModel = ClaferPage.this.compositeToHoldGranularUIElements.getClaferModel();
					currentModel.add(claferImportDialog.getResult());
					ClaferPage.this.compositeToHoldGranularUIElements.updateClaferContainer();
				}

				ClaferPage.this.compositeToHoldGranularUIElements.setOrigin(orig);

				ClaferPage.this.feedbackComposite.setFeedback("Features imported");
				ClaferPage.this.feedbackComposite.layout();

				checkModel();

				super.widgetSelected(e);
			}
		});

		final Button exportFeatures = new Button(container, SWT.NONE);
		exportFeatures.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		exportFeatures.setText("Export Features");
		exportFeatures.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final FileDialog saveDialog = new FileDialog(getShell(), SWT.SAVE);
				final LocalDateTime date = LocalDateTime.now();
				final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmm");
				saveDialog.setFileName(date.format(formatter) + ".dat");
				final String[] filterNames = new String[] {"CogniCrypt binary Clafer model (*.dat)", "Human-readable non-importable Clafer file (*.cfr)"};
				final String[] filterExtensions = new String[] {"*.dat", "*.cfr"};
				saveDialog.setFilterNames(filterNames);
				saveDialog.setFilterExtensions(filterExtensions);
				final String targetFilename = saveDialog.open();
				if (targetFilename != null) {
					if (targetFilename.endsWith(".dat")) {
						ClaferPage.this.compositeToHoldGranularUIElements.getClaferModel().toBinary(targetFilename);
					} else if (targetFilename.endsWith(".cfr")) {
						ClaferPage.this.compositeToHoldGranularUIElements.getClaferModel().toFile(targetFilename);
					}
				}
				super.widgetSelected(e);
			}
		});

		this.feedbackComposite = new CompositeClaferFeedback(container, SWT.BORDER);
		getCompositeToHoldGranularUIElements().setCompositeClaferFeedback(this.feedbackComposite);
		this.feedbackComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

	}

	public boolean checkModel() {

		final Job compileJob = Job.create("Compile Clafer model", (ICoreRunnable) monitor -> {
			// UI updates can only be run in the display thread,
			// so do them via Display.getDefault()
			Display.getDefault().asyncExec(() -> {

				ClaferPage.this.feedbackComposite.setFeedback(" (compiling...)");

				// do the tedious work
				final File cfrFile = new File(Utils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH), "temporaryModel" + Constants.CFR_EXTENSION);
				ClaferPage.this.compositeToHoldGranularUIElements.getClaferModel().toFile(cfrFile.getAbsolutePath());
				if (ClaferModel.compile(cfrFile.getAbsolutePath())) {
					ClaferPage.this.feedbackComposite.setFeedback("Compilation successful");
				} else {
					ClaferPage.this.feedbackComposite.setFeedback("Compilation error");
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
	 */
	public String getCompiledClaferModelPath() {
		final File cfrFile = new File(Utils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH), "currentClaferModel" + Constants.CFR_EXTENSION);
		this.compositeToHoldGranularUIElements.getClaferModel().toFile(cfrFile.getAbsolutePath());

		if (ClaferModel.compile(cfrFile.getAbsolutePath())) {
			return new File(Utils.getResourceFromWithin(Constants.CFR_FILE_DIRECTORY_PATH), "currentClaferModel" + Constants.JS_EXTENSION).getAbsolutePath();
		}

		return null;
	}

	public void initializeClaferModel() {
		final PageForTaskIntegratorWizard page = ((TaskIntegrationWizard) getWizard()).getTIPageByName(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD);
		if (page.getCompositeChoiceForModeOfWizard() != null) {
			final String taskName = page.getCompositeChoiceForModeOfWizard().getTxtForTaskName().getText();
			final String taskDescription = page.getCompositeChoiceForModeOfWizard().getTxtTaskDescription().getText();
			final String descriptionConstraint = "description = \"" + taskDescription + "\"";

			if (this.compositeToHoldGranularUIElements.getClaferModel().getClaferModel().isEmpty()) {
				final String defaultFeatureSetPath =
						Utils.getResourceFromWithin(Constants.CFR_BIN_FILE_DIRECTORY_PATH + Constants.DEFAULT_FEATURE_SET_FILE + Constants.CFR_BIN_EXTENSION).getAbsolutePath();
				final ClaferModel defaultFeaturesModel = ClaferModel.createFromBinaries(defaultFeatureSetPath);

				final ClaferFeature taskFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, taskName, "Task");

				final ArrayList<ClaferConstraint> constraints = new ArrayList<>();
				constraints.add(new ClaferConstraint(descriptionConstraint));

				taskFeature.setFeatureConstraints(constraints);

				this.compositeToHoldGranularUIElements.getClaferModel().add(defaultFeaturesModel);
				this.compositeToHoldGranularUIElements.getClaferModel().add(taskFeature);

				this.compositeToHoldGranularUIElements.getClaferModel().implementMissingFeatures(taskFeature);
				this.compositeToHoldGranularUIElements.updateClaferContainer();
			} else {
				for (final ClaferFeature claferFeature : this.compositeToHoldGranularUIElements.getClaferModel()) {
					if (claferFeature.getFeatureInheritance().equals("Task")) {
						claferFeature.setFeatureName(taskName);

						for (final ClaferConstraint constraint : claferFeature.getFeatureConstraints()) {
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
