/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 *
 */
package de.cognicrypt.integrator.task.widgets;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.container.namespaces.EclipsePlatformNamespace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.UIConstants;
import de.cognicrypt.integrator.task.controllers.Validator;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.wizard.TaskIntegratorWizardPage;
import de.cognicrypt.utils.Utils;

public class TaskInformationComposite extends Composite {

	private final TaskIntegratorWizardPage wizardPage;

	private final Label lblTaskName;
	private final ControlDecoration decTemplates;
	private final List templateList;
	private final FileBrowserComposite compJSON, compPNG;
	
	final Button btnModifyTemplate;
	final Button btnRemoveTemplate;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 */
	public TaskInformationComposite(final Composite parent, final int style,
			final TaskIntegratorWizardPage wizardPage) {
		super(parent, style);

		this.wizardPage = wizardPage;
		
		IntegratorModel.resetInstance();
		
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(1, false));

		/* Task Info Section */
		final Composite compositeTaskInfo = new Composite(this, SWT.NONE);
		compositeTaskInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compositeTaskInfo.setLayout(new GridLayout(1, false));

		lblTaskName = new Label(compositeTaskInfo, SWT.NONE);
		lblTaskName.setText("");
		
		final Label lblTaskDescription = new Label(compositeTaskInfo, SWT.NONE);
		lblTaskDescription.setText("Description");

		final Text txtTaskDescription = new Text(compositeTaskInfo, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		final GridData gdTaskDescription = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTaskDescription.heightHint = 67;
		txtTaskDescription.setLayoutData(gdTaskDescription);
		txtTaskDescription.setTextLimit(Constants.MULTI_LINE_TEXT_BOX_LIMIT);
		txtTaskDescription.setFocus();
		
		txtTaskDescription.addModifyListener(
				e -> IntegratorModel.getInstance().setTaskDescription(txtTaskDescription.getText().trim()));
		
		
		/* Template Section */
		final Label spacer = new Label(compositeTaskInfo, SWT.HORIZONTAL);
	    spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, 30));
			
		final Label lblTemplateList = new Label(compositeTaskInfo, SWT.NONE);
		lblTemplateList.setText("Templates");
		
		// Initialize the decorator for the label for the text box with initial error state
		decTemplates = new ControlDecoration(lblTemplateList, SWT.TOP | SWT.RIGHT);
		decTemplates.setShowOnlyOnFocus(false);
		decTemplates.setImage(UIConstants.DEC_ERROR);
		decTemplates.setDescriptionText(Constants.ERROR + Constants.ERROR_BLANK_TEMPLATE_LIST);
		
		templateList = new List(compositeTaskInfo, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		final GridData gd_templateList = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_templateList.heightHint = 25*5;
		templateList.setLayoutData(gd_templateList);
		
		templateList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				btnModifyTemplate.setEnabled(true);
				btnRemoveTemplate.setEnabled(true);
			}
		});
		
		final Composite compositeTemplateBtns = new Composite(compositeTaskInfo, SWT.NONE);
		compositeTemplateBtns.setVisible(true);
		compositeTemplateBtns.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		final Button btnAddTemplate = new Button(compositeTemplateBtns, SWT.NONE);
		btnAddTemplate.setText("Add");
		
		btnModifyTemplate = new Button(compositeTemplateBtns, SWT.NONE);
		btnModifyTemplate.setText("Modify");
		btnModifyTemplate.setEnabled(false);
		
		btnRemoveTemplate = new Button(compositeTemplateBtns, SWT.NONE);
		btnRemoveTemplate.setText("Remove");
		btnRemoveTemplate.setEnabled(false);

		btnAddTemplate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				addTemplate();
			}
		});
		
		btnModifyTemplate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {	
				int selectionCount = templateList.getSelectionCount();
				removeTemplates(templateList.getSelection());
				
				for(int i=0; i < selectionCount; i++) {
					addTemplate();
				}
			}
		});
		
		btnRemoveTemplate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				removeTemplates(templateList.getSelection());
			}
		});
		
		
		/* File Import Section */
		Label spacerBeforeFileImports = new Label(this, SWT.HORIZONTAL);
		spacerBeforeFileImports.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, 30));
		
		final Composite compositeFileImports = new Composite(this, SWT.NONE);
		compositeFileImports.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeFileImports.setVisible(true);
		compositeFileImports.setLayout(new GridLayout(1, false));
		
		compPNG = new FileBrowserComposite(compositeFileImports, SWT.NONE,
				Constants.WIDGET_DATA_LOCATION_OF_PNG_FILE, new String[] { "*.png" },
				"Select PNG file that contains the task icon",
				wizardPage);
		compPNG.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		String defaultIconPath = Utils.getResourceFromWithin(Constants.DEFAULT_ICON_PATH, "de.cognicrypt.core").getAbsolutePath();
		compPNG.setPathText(defaultIconPath);
		
		
		Label spacerBeforeGuidedMode = new Label(compositeFileImports, SWT.HORIZONTAL);
		spacerBeforeGuidedMode.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, 30));
		
		final Button btnGuidedMode = new Button(compositeFileImports, SWT.CHECK);
		btnGuidedMode.setText("Guided Mode");
		// Guided mode set by default.
		btnGuidedMode.setSelection(true);
		IntegratorModel.getInstance().setGuidedModeChosen(btnGuidedMode.getSelection());

		final Composite compositeNonguidedMode = new Composite(compositeFileImports, SWT.NONE);
		compositeNonguidedMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeNonguidedMode.setVisible(false);
		compositeNonguidedMode.setLayout(new GridLayout(1, false));
		
		compJSON = new FileBrowserComposite(compositeNonguidedMode, SWT.NONE,
				Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE, new String[] { "*.json" },
				"Select JSON file that contains the high-level questions", wizardPage);
		compJSON.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		requestLayout();

		btnGuidedMode.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean tempSelectionStatus = btnGuidedMode.getSelection();
				// If the guided mode is selected, hide the widgets to get the location of the
				// files required for the task.
				compositeNonguidedMode.setVisible(!tempSelectionStatus);
				// Set the data value.
				IntegratorModel.getInstance().setGuidedModeChosen(tempSelectionStatus);

				// If the guided mode is not selected, the rest of the pages are set to
				// completed. This is to allow the finish button to be enabled on the first
				// page.
				for (final IWizardPage page : wizardPage.getWizard().getPages()) {
					if (!page.getName().equals(Constants.PAGE_TASK_INFORMATION)) {
						((WizardPage) page).setPageComplete(!tempSelectionStatus);
					}
				}

				// Check if the page can be set to completed.
				wizardPage.checkPageComplete();
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public FileBrowserComposite getCompPNG() {
		return compPNG;
	}

	public FileBrowserComposite getCompJSON() {
		return compJSON;
	}

	public TaskIntegratorWizardPage getLocalContainerPage() {
		return wizardPage;
	}
	

	public void redrawTable() {
		
		btnModifyTemplate.setEnabled(false);
		btnRemoveTemplate.setEnabled(false);
		
		HashMap<String, File> templates = IntegratorModel.getInstance().getCryslTemplateFiles();
		
		templateList.removeAll();
		
		for(Entry<String, File> e : templates.entrySet()) {
			templateList.add(e.getKey());	
		}
	}
	

	public void addTemplate() {
		
		final FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE);
		fileDialog.setFilterExtensions(new String[] { "*.java" });
		fileDialog.setText(Constants.WIDGET_DATA_LOCATION_OF_CRYSLTEMPLATE_FILE);

		String templateFilePath = fileDialog.open();
		if (templateFilePath == null) 
			return; // user canceled file dialog

		// Set the task name or verify that it's equal
		String[] filePathParts = templateFilePath.split("(\\/|\\\\)");
		String taskName = filePathParts[filePathParts.length - 1].replace(".java", "");
		
		if(Validator.checkIfTaskNameAlreadyExists(taskName)) {
			MessageDialog.openError(getShell(), "Warning", "The chosen template's associated task has been added before.");
			return;
		}
		
		if (IntegratorModel.getInstance().getTaskName() == null) {
			IntegratorModel.getInstance().setTaskName(taskName);
			lblTaskName.setText(taskName);
			lblTaskName.getParent().requestLayout();
		}else if (!taskName.contentEquals(IntegratorModel.getInstance().getTaskName())) {
			MessageDialog.openError(getShell(), "Warning", "The chosen template's file name does not match the task name of previously added templates and can therefor not be added.");
			return;
		}

		// Extract package line from the template's source code
		String packageLine = "";

		Scanner scanner;
		try {
			scanner = new Scanner(new File(templateFilePath));
		} catch (FileNotFoundException e1) {
			MessageDialog.openError(getShell(), "Warning", "The chosen template file could not be found.");
			return;
		}

		while (packageLine.contentEquals("")) {

			if(!scanner.hasNextLine()) {
				scanner.close();
				MessageDialog.openError(getShell(), "Warning", "The chosen template's source code has no package and can therefor not be added.");
				return;
			}

			String[] expr = scanner.nextLine().split(";");

			// Lines may contain multiple expressions
			for(String e : expr) {
				String line = e.trim();
				if (line.startsWith("package")) {
					packageLine = line;
					break;
				}
			}

		}
		scanner.close();

		// Extract identifier
		String[] packageParts = packageLine.split("\\.");
		String templateIdentifier = packageParts[packageParts.length - 1].replace(";", "");

		IntegratorModel.getInstance().addTemplate(templateIdentifier, new File(templateFilePath));

		wizardPage.checkPageComplete();

		redrawTable();

		decTemplates.setImage(UIConstants.DEC_REQUIRED);
		decTemplates.setDescriptionText(Constants.MESSAGE_REQUIRED_FIELD);
	}
	
	public void removeTemplates(String[] identifiers) {
		
		IntegratorModel.getInstance().removeTemplates(identifiers);
		
		wizardPage.checkPageComplete();
		
		redrawTable();
		
		if(IntegratorModel.getInstance().isTemplatesEmpty()) {
			IntegratorModel.getInstance().setTaskName(null);
			lblTaskName.setText("");
			
			decTemplates.setImage(UIConstants.DEC_ERROR);
			decTemplates.setDescriptionText(Constants.ERROR + Constants.ERROR_BLANK_TEMPLATE_LIST);
		}
	}
}
