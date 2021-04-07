/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 *
 */
package de.cognicrypt.integrator.task.widgets;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.UIConstants;
import de.cognicrypt.integrator.task.controllers.Validator;
import de.cognicrypt.integrator.task.models.ModelAdvancedMode;
import de.cognicrypt.integrator.task.wizard.PageForTaskIntegratorWizard;

public class CompositeChoiceForModeOfWizard extends Composite {

	private ModelAdvancedMode objectForDataInNonGuidedMode;
	private final Text txtTaskName;
	private Text txtTaskDescription;
	private ControlDecoration decNameOfTheTask; // Decoration variable to be able to access it in the events.
	private PageForTaskIntegratorWizard theLocalContainerPage; // this is needed to set whether the page has been
																// completed yet or not.
	private ArrayList<CompositeBrowseForFile> lstCryslTemplates = new ArrayList<CompositeBrowseForFile>();

	public ArrayList<CompositeBrowseForFile> getLstCryslTemplates() {
		return lstCryslTemplates;
	}

	public void setLstCryslTemplates(ArrayList<CompositeBrowseForFile> lstCryslTemplates) {
		this.lstCryslTemplates = lstCryslTemplates;
	}

	private CompositeBrowseForFile compCfr;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public CompositeChoiceForModeOfWizard(final Composite parent, final int style,
			final PageForTaskIntegratorWizard theContainerPageForValidation) {
		super(parent, style);

		setTheLocalContainerPage(theContainerPageForValidation);

		setObjectForDataInNonGuidedMode(new ModelAdvancedMode());
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(1, false));

		// All the UI widgets
		final Composite compositeChooseTheMode = new Composite(this, SWT.NONE);
		compositeChooseTheMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compositeChooseTheMode.setLayout(new GridLayout(1, false));

		final Label lblNameOfTheTask = new Label(compositeChooseTheMode, SWT.NONE);
		lblNameOfTheTask.setText("Name of the Task ");

		// Initialize the decorator for the label for the text box.
		setDecNameOfTheTask(new ControlDecoration(lblNameOfTheTask, SWT.TOP | SWT.RIGHT));
		getDecNameOfTheTask().setShowOnlyOnFocus(false);
		// Set the initial error state.
		getDecNameOfTheTask().setImage(UIConstants.DEC_ERROR);
		getDecNameOfTheTask().setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_BLANK_TASK_NAME);
		getDecNameOfTheTask().showHoverText(getDecNameOfTheTask().getDescriptionText());

		this.txtTaskName = new Text(compositeChooseTheMode, SWT.BORDER);
		this.txtTaskName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		this.txtTaskName.setTextLimit(Constants.SINGLE_LINE_TEXT_BOX_LIMIT);

		final Label lblDescriptionOfThe = new Label(compositeChooseTheMode, SWT.NONE);
		lblDescriptionOfThe.setText("Description of the Task :");

		setTxtDescriptionOfTask(new Text(compositeChooseTheMode, SWT.BORDER | SWT.WRAP | SWT.MULTI));
		final GridData gd_txtDescriptionOfTask = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtDescriptionOfTask.heightHint = 67;
		getTxtTaskDescription().setLayoutData(gd_txtDescriptionOfTask);
		getTxtTaskDescription().setTextLimit(Constants.MULTI_LINE_TEXT_BOX_LIMIT);

		final Button btnCustomLibrary = new Button(compositeChooseTheMode, SWT.CHECK);
		btnCustomLibrary.setText("Include a custom library");

		final Composite compositeContainerGroupForLibrary = new Composite(compositeChooseTheMode, SWT.NONE);
		compositeContainerGroupForLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeContainerGroupForLibrary.setVisible(false);
		compositeContainerGroupForLibrary.setLayout(new GridLayout(1, false));

		// Updated the composite to deal with directory instead of a jar file for the
		// custom library.
		final CompositeBrowseForFile compLib = new CompositeBrowseForFile(compositeContainerGroupForLibrary, SWT.NONE,
				Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK, null, "Select file that contains the library",
				getTheLocalContainerPage());
		compLib.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Button btnDoYouWishToUseTheGuidedMode = new Button(compositeChooseTheMode, SWT.CHECK);
		btnDoYouWishToUseTheGuidedMode.setText("Use the guided mode");
		// Guided mode set by default.
		btnDoYouWishToUseTheGuidedMode.setSelection(true);
		getObjectForDataInNonGuidedMode().setGuidedModeChosen(btnDoYouWishToUseTheGuidedMode.getSelection());

		final Composite compositeNonguidedMode = new Composite(compositeChooseTheMode, SWT.NONE);
		compositeNonguidedMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeNonguidedMode.setVisible(false);
		compositeNonguidedMode.setLayout(new GridLayout(1, false));

		final Composite compositeAllModes = new Composite(compositeChooseTheMode, SWT.NONE);
		compositeAllModes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeAllModes.setVisible(true);
		compositeAllModes.setLayout(new GridLayout(1, false));

		final ScrolledComposite scrolledComposite = new ScrolledComposite(compositeAllModes,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinWidth(800);

		final Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		scrolledComposite.setContent(composite);
		scrolledComposite.setSize(composite.computeSize(800, 100));

		final Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));
		composite_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		final Composite composite_3 = new Composite(composite, SWT.NONE);
		composite_3.setLayout(new GridLayout());
		composite_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		final Button btnAdd = new Button(composite_3, SWT.NONE);
		btnAdd.setText("Add");

		final Button btnDone = new Button(composite_3, SWT.NONE);
		btnDone.setText("Done");
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final CompositeBrowseForFile compCryslTemplate = new CompositeBrowseForFile(composite_2, SWT.NONE,
						Constants.WIDGET_DATA_LOCATION_OF_CRYSLTEMPLATE_FILE, new String[] { "*.java" },
						"Select crysl tempplate file that contains the code details", getTheLocalContainerPage());
				compCryslTemplate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
				lstCryslTemplates.add(compCryslTemplate);

				scrolledComposite.layout(true, true);
				scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});

		btnDone.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				ModelAdvancedMode objectForDataInNonGuidedModeTmp = getObjectForDataInNonGuidedMode();
				HashMap<String, File> crylTemplatesWithOption = objectForDataInNonGuidedModeTmp
						.getCrylTemplatesWithOption();
				if (crylTemplatesWithOption == null) {
					objectForDataInNonGuidedModeTmp.setCrylTemplatesWithOption(new HashMap<String, File>());
					crylTemplatesWithOption = objectForDataInNonGuidedModeTmp.getCrylTemplatesWithOption();
				}
				for (CompositeBrowseForFile cryslTemplateOpts : lstCryslTemplates) {
					File locationOfCryslTemplate = new File(cryslTemplateOpts.getText());
					crylTemplatesWithOption.put(cryslTemplateOpts.getTxtBoxOption(), locationOfCryslTemplate);
				}
			}
		});

		/*
		 * final CompositeBrowseForFile compCryslTemplate = new
		 * CompositeBrowseForFile(compositeAllModes, SWT.NONE,
		 * Constants.WIDGET_DATA_LOCATION_OF_CRYSLTEMPLATE_FILE, new String[]
		 * {"*.java"}, "Select crysl tempplate file that contains the code details",
		 * getTheLocalContainerPage()); compCryslTemplate.setLayoutData(new
		 * GridData(SWT.FILL, SWT.CENTER, true, false));
		 */
		final CompositeBrowseForFile compJson = new CompositeBrowseForFile(compositeNonguidedMode, SWT.NONE,
				Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE, new String[] { "*.json" },
				"Select json file that contains the high level questions", getTheLocalContainerPage());
		compJson.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final CompositeBrowseForFile compPNG = new CompositeBrowseForFile(compositeAllModes, SWT.NONE,
				Constants.WIDGET_DATA_LOCATION_OF_PNG_FILE, new String[] { "*.png" },
				"Select png file that contains the icon for the task you wish to integrate",
				getTheLocalContainerPage());
		compPNG.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		layout();

		// moved all the event listeners at the bottom.
		btnDoYouWishToUseTheGuidedMode.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean tempSelectionStatus = btnDoYouWishToUseTheGuidedMode.getSelection();
				// If the guided mode is selected, hide the widgets to get the location of the
				// files required for the task.
				compositeNonguidedMode.setVisible(!tempSelectionStatus);
				// Set the data value.
				getObjectForDataInNonGuidedMode().setGuidedModeChosen(tempSelectionStatus);

				// If the guided mode is not selected, the rest of the pages are set to
				// completed. This is to allow the finish button to be enabled on the first
				// page.
				for (final IWizardPage page : getTheLocalContainerPage().getWizard().getPages()) {
					if (!page.getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)) {
						((WizardPage) page).setPageComplete(!tempSelectionStatus);
					}
				}

				// Check if the page can be set to completed.
				getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
			}
		});

		btnCustomLibrary.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean tempSelectionStatus = btnCustomLibrary.getSelection();
				// Show the widget to get the file data if the check box is selected.
				getObjectForDataInNonGuidedMode().setCustomLibraryRequired(tempSelectionStatus);
				// Set the data value.
				compositeContainerGroupForLibrary.setVisible(tempSelectionStatus);

				// Check if the page can be completed.
				getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
			}
		});

		/*
		 * TODO removed for the user study. btnForceGuidedMode.addSelectionListener(new
		 * SelectionAdapter() {
		 * 
		 * @Override public void widgetSelected(SelectionEvent e) {
		 * btnForceGuidedMode.getParent().getParent().getParent().setData(Constants.
		 * WIDGET_DATA_IS_GUIDED_MODE_FORCED, btnForceGuidedMode.getSelection()); } });
		 */

		this.txtTaskName.addModifyListener(e -> {

			final String tempName = CompositeChoiceForModeOfWizard.this.txtTaskName.getText().trim();
			final boolean validString = Validator.checkIfTaskNameAlreadyExists(tempName);
			if (validString) {
				getObjectForDataInNonGuidedMode().setNameOfTheTask(tempName);
			}

			if (tempName.isEmpty()) {
				getDecNameOfTheTask().setImage(UIConstants.DEC_ERROR);
				getDecNameOfTheTask().setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_BLANK_TASK_NAME);
			} else if (validString) {
				getDecNameOfTheTask().setImage(UIConstants.DEC_REQUIRED);
				getDecNameOfTheTask().setDescriptionText(Constants.MESSAGE_REQUIRED_FIELD);
			} else {
				getDecNameOfTheTask().setImage(UIConstants.DEC_ERROR);
				getDecNameOfTheTask().setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_DUPLICATE_TASK_NAME);
				getDecNameOfTheTask().showHoverText(getDecNameOfTheTask().getDescriptionText());
			}
			// Check if the page can be set to completed.
			getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();

		});

		getTxtTaskDescription().addModifyListener(
				e -> getObjectForDataInNonGuidedMode().setTaskDescription(getTxtTaskDescription().getText().trim()));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Return the basic data of the task.
	 *
	 * @return the objectForDataInNonGuidedMode
	 */
	public ModelAdvancedMode getObjectForDataInNonGuidedMode() {
		return this.objectForDataInNonGuidedMode;
	}

	/**
	 * This object contains the basic data of the task.
	 *
	 * @param objectForDataInNonGuidedMode the objectForDataInNonGuidedMode to set
	 */
	public void setObjectForDataInNonGuidedMode(final ModelAdvancedMode objectForDataInNonGuidedMode) {
		this.objectForDataInNonGuidedMode = objectForDataInNonGuidedMode;
	}

	/**
	 * Get the local copy of the wizard page that is the parent container for this
	 * composite.
	 *
	 * @return
	 */
	public PageForTaskIntegratorWizard getTheLocalContainerPage() {
		return this.theLocalContainerPage;
	}

	/**
	 * Set the local copy of the wizard page that is the parent container for this
	 * composite.
	 *
	 * @param theLocalContainerPage
	 */
	public void setTheLocalContainerPage(final PageForTaskIntegratorWizard theLocalContainerPage) {
		this.theLocalContainerPage = theLocalContainerPage;
	}

	/**
	 * @return the decNameOfTheTask
	 */
	public ControlDecoration getDecNameOfTheTask() {
		return this.decNameOfTheTask;
	}

	/**
	 * The decorator is is global variable to be accessible in event listners.
	 *
	 * @param decNameOfTheTask the decNameOfTheTask to set
	 */
	public void setDecNameOfTheTask(final ControlDecoration decNameOfTheTask) {
		this.decNameOfTheTask = decNameOfTheTask;
	}

	public Text getTxtForTaskName() {
		return this.txtTaskName;
	}

	/**
	 * @return the txtDescriptionOfTask
	 */
	public Text getTxtTaskDescription() {
		return this.txtTaskDescription;
	}

	/**
	 * @param txtDescriptionOfTask the txtDescriptionOfTask to set
	 */
	public void setTxtDescriptionOfTask(final Text txtDescriptionOfTask) {
		this.txtTaskDescription = txtDescriptionOfTask;
	}
}
