/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.List;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.UIConstants;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;
import de.cognicrypt.codegenerator.taskintegrator.wizard.PageForTaskIntegratorWizard;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;

/**
 * @author rajiv
 *
 */
public class CompositeChoiceForModeOfWizard extends Composite {
	private ModelAdvancedMode objectForDataInNonGuidedMode;
	private Text txtDescriptionOfTask;
	private List<Task> existingTasks; // retuired to validate the task name that is chosen by the user.
	private ControlDecoration decNameOfTheTask; // Decoration variable to be able to access it in the events.
	private PageForTaskIntegratorWizard theLocalContainerPage; // this is needed to set whether the page has been completed yet or not.

	private CompositeBrowseForFile compCfr;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeChoiceForModeOfWizard(Composite parent, int style, PageForTaskIntegratorWizard theContainerPageForValidation) {		
		super(parent, style);
		
		// these tasks are required for validation of the new task that is being added.
		setExistingTasks(TaskJSONReader.getTasks());
		setTheLocalContainerPage(theContainerPageForValidation);
		
		setObjectForDataInNonGuidedMode(new ModelAdvancedMode());
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(1, false));
		
		// All the UI widgets
		Group grpChooseTheMode = new Group(this, SWT.NONE);
		grpChooseTheMode.setText("Choose the mode of this Wizard");
		grpChooseTheMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		grpChooseTheMode.setLayout(new GridLayout(1, false));
		
		Label lblNameOfTheTask = new Label(grpChooseTheMode, SWT.NONE);
		lblNameOfTheTask.setText("Name of the Task ");
		
		// Initialize the decorator for the label for the text box. 
		setDecNameOfTheTask(new ControlDecoration(lblNameOfTheTask, SWT.TOP | SWT.RIGHT));
		getDecNameOfTheTask().setShowOnlyOnFocus(false);
		// Set the initial error state.
		getDecNameOfTheTask().setImage(UIConstants.DEC_ERROR);
		getDecNameOfTheTask().setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_BLANK_TASK_NAME);
		getDecNameOfTheTask().showHoverText(getDecNameOfTheTask().getDescriptionText());
		
		Text txtForTaskName = new Text(grpChooseTheMode, SWT.BORDER);
		txtForTaskName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		txtForTaskName.setTextLimit(Constants.SINGLE_LINE_TEXT_BOX_LIMIT);
		
		Label lblDescriptionOfThe = new Label(grpChooseTheMode, SWT.NONE);
		lblDescriptionOfThe.setText("Description of the Task :");
		
		setTxtDescriptionOfTask(new Text(grpChooseTheMode, SWT.BORDER | SWT.WRAP | SWT.MULTI));		
		GridData gd_txtDescriptionOfTask = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtDescriptionOfTask.heightHint = 67;
		getTxtDescriptionOfTask().setLayoutData(gd_txtDescriptionOfTask);
		getTxtDescriptionOfTask().setTextLimit(Constants.MULTI_LINE_TEXT_BOX_LIMIT);
		
		Button btnCustomLibrary = new Button(grpChooseTheMode, SWT.CHECK);
		btnCustomLibrary.setText("Do you wish to use a custom library?");
				
		
		Group grpContainerGroupForLibrary = new Group(grpChooseTheMode, SWT.NONE);
		grpContainerGroupForLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpContainerGroupForLibrary.setText("Custom Library");
		grpContainerGroupForLibrary.setVisible(false);
		grpContainerGroupForLibrary.setLayout(new GridLayout(1, false));
		
		CompositeBrowseForFile compLib = new CompositeBrowseForFile(grpContainerGroupForLibrary, SWT.NONE, Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK, new String[] { "*.jar" }, "Select file that contains the library", getTheLocalContainerPage());
		compLib.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		
		Button btnDoYouWishToUseTheGuidedMode = new Button(grpChooseTheMode, SWT.CHECK);
		btnDoYouWishToUseTheGuidedMode.setText("Do you wish to use the guided mode?");
		// Guided mode set by default.
		btnDoYouWishToUseTheGuidedMode.setSelection(true);
		getObjectForDataInNonGuidedMode().setGuidedModeChosen(btnDoYouWishToUseTheGuidedMode.getSelection());
		
		Group grpNonguidedMode = new Group(grpChooseTheMode, SWT.NONE);
		grpNonguidedMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpNonguidedMode.setText("Non-Guided mode");
		grpNonguidedMode.setVisible(false);
		grpNonguidedMode.setLayout(new GridLayout(1, false));
		
		compCfr = new CompositeBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE, new String[] { "*.cfr" }, "Select cfr file that contains the Clafer features", getTheLocalContainerPage(), new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				System.out.println("Custom event handler triggered");

				Job compileJob = Job.create("Compile Clafer model", (ICoreRunnable) monitor -> {
					// UI updates can only be run in the display thread,
					// so do them via Display.getDefault()
					Display.getDefault().asyncExec(new Runnable() {

						public void run() {
							compCfr.getDecFilePath().setDescriptionText(" (compiling...)");
							compCfr.getDecFilePath().setImage(UIConstants.DEC_INFORMATION);

							// do the tedious work
							String fileToCompile = compCfr.getText();

							if (ClaferModel.compile(fileToCompile)) {
								compCfr.getDecFilePath().setDescriptionText("Compilation successful");
								compCfr.getDecFilePath().setImage(UIConstants.DEC_INFORMATION);
							} else {
								compCfr.getDecFilePath().setDescriptionText("Compilation error");
								compCfr.getDecFilePath().setImage(UIConstants.DEC_ERROR);
							}
						}
					});
				});
				// start the asynchronous task
				compileJob.schedule();

			}
		});

		compCfr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		CompositeBrowseForFile compXsl = new CompositeBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_XSL_FILE, new String[] { "*.xsl" }, "Select xsl file that contains the code details", getTheLocalContainerPage());
		compXsl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		CompositeBrowseForFile compJson = new CompositeBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE, new String[] { "*.json" }, "Select json file that contains the high level questions", getTheLocalContainerPage());
		compJson.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		layout();

		/* TODO removed for the user study.
		Button btnForceGuidedMode = new Button(grpNonguidedMode, SWT.CHECK);
		btnForceGuidedMode.setBounds(10, 118, 142, Constants.UI_WIDGET_HEIGHT_NORMAL);
		btnForceGuidedMode.setText("Force guided mode");*/	

		// TODO removed for the user study.
		//this.setData(Constants.WIDGET_DATA_IS_GUIDED_MODE_FORCED,btnForceGuidedMode.getSelection());
	
		
		// moved all the event listeners at the bottom.
		btnDoYouWishToUseTheGuidedMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean tempSelectionStatus = btnDoYouWishToUseTheGuidedMode.getSelection();
				// If the guided mode is selected, hide the widgets to get the location of the files required for the task.
				grpNonguidedMode.setVisible(!tempSelectionStatus);
				// Set the data value.
				getObjectForDataInNonGuidedMode().setGuidedModeChosen(tempSelectionStatus);				
				
				// If the guided mode is not selected, the rest of the pages are set to completed. This is to allow the finish button to be enabled on the first page.
				for (IWizardPage page : getTheLocalContainerPage().getWizard().getPages()) {
					if (!page.getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)) {
						((WizardPage)page).setPageComplete(!tempSelectionStatus);
						}
					}
				
				// Check if the page can be set to completed.
				getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
				}
			});
		
		btnCustomLibrary.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				boolean tempSelectionStatus = btnCustomLibrary.getSelection();
				// Show the widget to get the file data if the check box is selected.
				getObjectForDataInNonGuidedMode().setCustomLibraryRequired(tempSelectionStatus);
				// Set the data value.
				grpContainerGroupForLibrary.setVisible(tempSelectionStatus);
				
				// Check if the page can be completed.
				getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
			}
		});
		
		/* TODO removed for the user study.
		btnForceGuidedMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnForceGuidedMode.getParent().getParent().getParent().setData(Constants.WIDGET_DATA_IS_GUIDED_MODE_FORCED, btnForceGuidedMode.getSelection());
			}
		});*/
		
		txtForTaskName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				String tempName = txtForTaskName.getText().trim();
				if (validateTaskName(tempName)) {
					getObjectForDataInNonGuidedMode().setNameOfTheTask(tempName);
				}	
				
			}
		});
		
		getTxtDescriptionOfTask().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getObjectForDataInNonGuidedMode().setTaskDescription(getTxtDescriptionOfTask().getText().trim());
				//TODO Check if validation is required.
			}
		});
				
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public boolean validateTaskName(String tempName){
		boolean validString = true;
		
		// Validation : check whether the name already exists.
		for (Task task : getExistingTasks()) {
			if (task.getName().toLowerCase().equals(tempName.toLowerCase()) || task.getDescription().toLowerCase().equals(tempName.toLowerCase())) {
				validString = false;						
				break;
			}
		}
		
		if (tempName.equals("")) {
			getDecNameOfTheTask().setImage(UIConstants.DEC_ERROR);
			getDecNameOfTheTask().setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_BLANK_TASK_NAME);
			
			// Check if the page can be set to completed.
			getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
		}else if (validString) {
			getDecNameOfTheTask().setImage(UIConstants.DEC_REQUIRED);
			getDecNameOfTheTask().setDescriptionText(Constants.MESSAGE_REQUIRED_FIELD);
			
			// Check if the page can be set to completed.
			getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
		} else {
			getDecNameOfTheTask().setImage(UIConstants.DEC_ERROR);
			getDecNameOfTheTask().setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_DUPLICATE_TASK_NAME);
			getDecNameOfTheTask().showHoverText(getDecNameOfTheTask().getDescriptionText());
			
			// Check if the page can be set to completed.
			getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
		}
		
		return validString;
	}

	/**
	 * Return the basic data of the task.
	 * @return the objectForDataInNonGuidedMode
	 */
	public ModelAdvancedMode getObjectForDataInNonGuidedMode() {
		return objectForDataInNonGuidedMode;
	}

	/**
	 * This object contains the basic data of the task.
	 * @param objectForDataInNonGuidedMode the objectForDataInNonGuidedMode to set
	 */
	public void setObjectForDataInNonGuidedMode(ModelAdvancedMode objectForDataInNonGuidedMode) {
		this.objectForDataInNonGuidedMode = objectForDataInNonGuidedMode;
	}

	/**
	 * Get the local copy of the wizard page that is the parent container for this composite.
	 * @return
	 */
	public PageForTaskIntegratorWizard getTheLocalContainerPage() {
		return theLocalContainerPage;
	}

	/**
	 * Set the local copy of the wizard page that is the parent container for this composite.
	 * @param theLocalContainerPage
	 */
	public void setTheLocalContainerPage(PageForTaskIntegratorWizard theLocalContainerPage) {
		this.theLocalContainerPage = theLocalContainerPage;
	}

	/**
	 * @return the decNameOfTheTask
	 */
	public ControlDecoration getDecNameOfTheTask() {
		return decNameOfTheTask;
	}

	
	/**
	 * The decorator is is global variable to be accessible in event listners.
	 * @param decNameOfTheTask the decNameOfTheTask to set
	 */
	public void setDecNameOfTheTask(ControlDecoration decNameOfTheTask) {
		this.decNameOfTheTask = decNameOfTheTask;
	}

	
	/**
	 * @return the txtDescriptionOfTask
	 */
	public Text getTxtDescriptionOfTask() {
		return txtDescriptionOfTask;
	}

	
	/**
	 * @param txtDescriptionOfTask the txtDescriptionOfTask to set
	 */
	public void setTxtDescriptionOfTask(Text txtDescriptionOfTask) {
		this.txtDescriptionOfTask = txtDescriptionOfTask;
	}

	
	/**
	 * @return the existingTasks
	 */
	public List<Task> getExistingTasks() {
		return existingTasks;
	}

	
	/**
	 * @param existingTasks the existingTasks to set
	 */
	public void setExistingTasks(List<Task> existingTasks) {
		this.existingTasks = existingTasks;
	}
}
