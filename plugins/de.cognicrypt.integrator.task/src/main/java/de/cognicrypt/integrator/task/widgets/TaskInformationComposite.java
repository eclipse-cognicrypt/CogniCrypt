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
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.models.IntegratorModel;
import de.cognicrypt.integrator.task.wizard.TaskIntegratorWizardPage;
import de.cognicrypt.utils.Utils;

public class TaskInformationComposite extends Composite {

	private final TaskIntegratorWizardPage wizardPage;

	private final Label lblTaskName;
	private final ControlDecoration templatesDec;

	private final List templateList;
	private final FileBrowserComposite compJSON;
	private final FileBrowserComposite compPNG;
	private final FileBrowserComposite compImport;
	
	private final Button btnRemoveTemplate;

	/**
	 * Create the composite.
	 *
	 * @param parent
	 * @param style
	 * @param wizardPage
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


		final Text txtTaskDescription = new Text(compositeTaskInfo, SWT.BORDER);
		txtTaskDescription.setMessage(Constants.TASK_DESCRIPTION_MESSAGE);
		final GridData gdTaskDescription= new GridData(SWT.FILL, SWT.CENTER, true, true);
		gdTaskDescription.widthHint = 0;
		txtTaskDescription.setLayoutData(gdTaskDescription);

		// Modify the task description
		txtTaskDescription.addModifyListener(e -> IntegratorModel.getInstance().setTaskDescription(txtTaskDescription.getText().trim()));


		/* Template Section */
		final Label spacer = new Label(compositeTaskInfo, SWT.HORIZONTAL);
		spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, 30));

		final Label lblTemplateList = new Label(compositeTaskInfo, SWT.NONE);
		lblTemplateList.setText(Constants.TEMPLATES);

		// Initialize the decorator for the label for the text box with initial error state
		templatesDec = new ControlDecoration(lblTemplateList, SWT.TOP | SWT.RIGHT);
		templatesDec.setShowOnlyOnFocus(false);
		checkTemplatesDec();

		templateList = new List(compositeTaskInfo, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		final GridData templateListGrid = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		templateListGrid.heightHint = 25*5;
		templateList.setLayoutData(templateListGrid);

		templateList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (templateList.getSelectionCount() != 0) {
					btnRemoveTemplate.setEnabled(true); // enable remove button if a template is selected
				}
			}
		});

		final Composite compositeTemplateBtns = new Composite(compositeTaskInfo, SWT.NONE);
		compositeTemplateBtns.setVisible(true);
		compositeTemplateBtns.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Button btnAddTemplate = new Button(compositeTemplateBtns, SWT.NONE);
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		btnAddTemplate.setImage(sharedImages.getImage(ISharedImages.IMG_OBJ_ADD));
		btnAddTemplate.setFocus();

		btnRemoveTemplate = new Button(compositeTemplateBtns, SWT.NONE);
		btnRemoveTemplate.setImage(sharedImages.getImage(ISharedImages.IMG_TOOL_DELETE));
		btnRemoveTemplate.setEnabled(false);

		btnAddTemplate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				addTemplate();
			}
		});

		btnRemoveTemplate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				String[] templateIds = templateList.getSelection(); 

				for(int i=0; i < templateIds.length; i++) {
					// template identifiers are displayed with their task name as prefix to make the empty standard identifier look less weird
					// the prefix has to be removed from the GUI selection to get the actual template identifier
					templateIds[i] = templateIds[i].replace(IntegratorModel.getInstance().getTaskName(), ""); 
				}
					
				removeTemplates(templateIds);
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
				Constants.ICON_DESCRIPTION,
				wizardPage);
		compPNG.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		String defaultIconPath = Utils.getResourceFromWithin(Constants.DEFAULT_ICON_PATH, "de.cognicrypt.core").getAbsolutePath();
		compPNG.setPathText(defaultIconPath);


		Label spacerBeforeGuidedMode = new Label(compositeFileImports, SWT.HORIZONTAL);
		spacerBeforeGuidedMode.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, 30));

		final Button btnGuidedMode = new Button(compositeFileImports, SWT.CHECK);
		btnGuidedMode.setText(Constants.GUIDED_MODE_DESCRIPTION);
		btnGuidedMode.setSelection(true);
		IntegratorModel.getInstance().setGuidedModeChosen(true);

		final Composite compositeNonguidedMode = new Composite(compositeFileImports, SWT.NONE);
		compositeNonguidedMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeNonguidedMode.setVisible(false);
		compositeNonguidedMode.setLayout(new GridLayout(1, false));

		compJSON = new FileBrowserComposite(compositeNonguidedMode, SWT.NONE,
				Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE, new String[] { "*.json" },
				Constants.JSON_DESCRIPTION, wizardPage);
		compJSON.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Button btnImportMode = new Button(this, SWT.CHECK);
		btnImportMode.setText("Import Mode");

		compImport = new FileBrowserComposite(this, SWT.NONE,
				Constants.WIDGET_DATA_LOCATION_OF_IMPORT_FILE, new String[] { "*.zip" },
				Constants.ZIP_DESCRIPTION, wizardPage);
		compImport.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		compImport.setVisible(false);

		requestLayout();

		btnGuidedMode.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean tempSelectionStatus = btnGuidedMode.getSelection();
				// If the guided mode is selected, hide the widgets to get the location of the files required for the task.
				compositeNonguidedMode.setVisible(!tempSelectionStatus);
				IntegratorModel.getInstance().setGuidedModeChosen(tempSelectionStatus);

				// If the guided mode is not selected, the rest of the pages are set to completed
				// This is to allow the finish button to be enabled on the first page.
				for (final IWizardPage page : wizardPage.getWizard().getPages()) {
					if (!page.getName().equals(Constants.PAGE_TASK_INFORMATION)) {
						((WizardPage) page).setPageComplete(!tempSelectionStatus);
					}
				}
			}
		});

		btnImportMode.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean tempSelectionStatus = btnImportMode.getSelection();
				
				// If the import mode is selected, hide the other UI elements
				compImport.setVisible(tempSelectionStatus);
				compositeTaskInfo.setVisible(!tempSelectionStatus);
				compositeFileImports.setVisible(!tempSelectionStatus);

				IntegratorModel.getInstance().setImportModeChosen(tempSelectionStatus);
			}
		});

	}

	/**
	 * Opens a file dialog and adds the selected template
	 */
	public void addTemplate() {
		final FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE);
		fileDialog.setFilterExtensions(new String[] { "*.java" });
		fileDialog.setText(Constants.WIDGET_DATA_LOCATION_OF_CRYSLTEMPLATE_FILE);

		IntegratorModel im  = IntegratorModel.getInstance();

		try{
			if (im.addTemplate(fileDialog.open())) {
				lblTaskName.setText(im.getTaskName());
				lblTaskName.getParent().requestLayout();
			}

			checkTemplatesDec();
			wizardPage.checkPageComplete();

			redrawTemplateList();

		}catch(Exception e) {
			MessageDialog.openError(getShell(), "Warning", e.getMessage());
		}		
	}

	/**
	 * 
	 * @param identifiers to be removed
	 */
	public void removeTemplates(String[] identifiers) {

		for(String id : identifiers) {
			try {
				if (IntegratorModel.getInstance().removeTemplate(id)) {
					lblTaskName.setText("");
				}
			}catch(Exception e){
				MessageDialog.openError(getShell(), "Warning", e.getMessage());
			}
		}

		checkTemplatesDec();
		wizardPage.checkPageComplete();

		redrawTemplateList();
	}

	/**
	 * Check if the template decorators have to signal error or not
	 */
	private void checkTemplatesDec() {
		try {
			IntegratorModel.getInstance().checkTemplatesDec();

			templatesDec.setImage(Constants.DEC_REQUIRED);
			templatesDec.setDescriptionText(Constants.MESSAGE_REQUIRED_FIELD);
		}catch(Exception e) {
			templatesDec.setImage(Constants.DEC_ERROR);
			templatesDec.setDescriptionText(Constants.ERROR + e.getMessage());
		}
	}
	
	/**
	 * Redraws the template list after add or remove
	 */
	private void redrawTemplateList() {		
		templateList.removeAll();
		btnRemoveTemplate.setEnabled(false);

		Map<String, File> templates = IntegratorModel.getInstance().getCryslTemplateFiles();

		for(Entry<String, File> e : templates.entrySet()) {
			templateList.add(IntegratorModel.getInstance().getTaskName() + e.getKey());	
		}
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

	public FileBrowserComposite getImportZIP() {
		return compImport;
	}

	public TaskIntegratorWizardPage getLocalContainerPage() {
		return wizardPage;
	}

	public ControlDecoration getDecTemplates() {
		return templatesDec;
	}
}
