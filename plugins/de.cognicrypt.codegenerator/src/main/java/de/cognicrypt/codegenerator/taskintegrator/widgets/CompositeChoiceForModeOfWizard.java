/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;

import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;
import de.cognicrypt.codegenerator.taskintegrator.wizard.PageForTaskIntegratorWizard;
import de.cognicrypt.codegenerator.tasks.Task;
import de.cognicrypt.codegenerator.tasks.TaskJSONReader;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 * @author rajiv
 *
 */
public class CompositeChoiceForModeOfWizard extends Composite {
	private ModelAdvancedMode objectForDataInNonGuidedMode;
	private Text txtDescriptionOfTask;
	private List<Task> existingTasks;
	private ControlDecoration decNameOfTheTask; // Decoration variable to be able to access it in the events.
	

	private PageForTaskIntegratorWizard theLocalContainerPage; // this is needed to set whether the page has been completed yet or not.

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeChoiceForModeOfWizard(Composite parent, int style, PageForTaskIntegratorWizard theContainerPageForValidation) {		
		super(parent, SWT.BORDER);
		
		// these tasks are required for validation of the new task that is being added.
		setExistingTasks(TaskJSONReader.getTasks());
		setTheLocalContainerPage(theContainerPageForValidation);
		
		setObjectForDataInNonGuidedMode(new ModelAdvancedMode());
		this.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// All the UI widgets
		Group grpChooseTheMode = new Group(this, SWT.NONE);
		grpChooseTheMode.setText("Choose the mode of this Wizard");
		grpChooseTheMode.setLayout(new GridLayout(1, false));
		
		Label lblNameOfTheTask = new Label(grpChooseTheMode, SWT.NONE);
		lblNameOfTheTask.setText("Name of the Task ");
		
		// Initialize the decorator for the label for the text box. 
		setDecNameOfTheTask(new ControlDecoration(lblNameOfTheTask, SWT.TOP | SWT.RIGHT));
		getDecNameOfTheTask().setShowOnlyOnFocus(false);
		getDecNameOfTheTask().setImage(Constants.DEC_ERROR);
		getDecNameOfTheTask().setDescriptionText("ERROR: The Task name cannot be empty. Please enter a valid name for the Task.");
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
		grpContainerGroupForLibrary.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpContainerGroupForLibrary.setText("Custom Library");
		grpContainerGroupForLibrary.setVisible(false);
		RowLayout rl_grpContainerGroupForLibrary = new RowLayout(SWT.VERTICAL);
		rl_grpContainerGroupForLibrary.fill = true;
		grpContainerGroupForLibrary.setLayout(rl_grpContainerGroupForLibrary);
		grpContainerGroupForLibrary.setVisible(false);
		
		new GroupBrowseForFile(grpContainerGroupForLibrary,SWT.NONE,Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK,new String[] {"*.jar"},"Select file that contains the library", getTheLocalContainerPage());
		
		
		Button btnDoYouWishToUseTheGuidedMode = new Button(grpChooseTheMode, SWT.CHECK);
		btnDoYouWishToUseTheGuidedMode.setText("Do you wish to use the guided mode?");
		// guided mode by default.
		btnDoYouWishToUseTheGuidedMode.setSelection(true);
		
		Group grpNonguidedMode = new Group(grpChooseTheMode, SWT.NONE);
		grpNonguidedMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpNonguidedMode.setText("Non-Guided mode");
		grpNonguidedMode.setVisible(false);
		RowLayout rl_grpNonguidedMode = new RowLayout(SWT.VERTICAL);
		rl_grpNonguidedMode.fill = true;
		grpNonguidedMode.setLayout(rl_grpNonguidedMode);
		
				
		new GroupBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE, new String[] {"*.cfr"}, "Select cfr file that contains the Clafer features", getTheLocalContainerPage());
		
		new GroupBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_XSL_FILE, new String[] {"*.xsl"}, "Select xsl file that contains the code details", getTheLocalContainerPage());
		
		new GroupBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE, new String[] {"*.json"}, "Select json file that contains the high level questions", getTheLocalContainerPage());
		
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
				grpNonguidedMode.setVisible(!tempSelectionStatus);
				getObjectForDataInNonGuidedMode().setGuidedModeChosen(!tempSelectionStatus);
				
//				if (((GroupBrowseForFile)grpNonguidedMode.getChildren()[0]).getDecNameOfTheTask().getImage().equals(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage())) {
//					getTheLocalContainerPage().setPageComplete(tempSelectionStatus);
//				}
				getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
				
				
				
				if (!tempSelectionStatus) {
					for (IWizardPage page : getTheLocalContainerPage().getWizard().getPages()) {
						if (!page.getName().equals(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)) {
							((WizardPage)page).setPageComplete(!tempSelectionStatus);
						}
					}
				} 
				
//				getTheLocalContainerPage().getNextPage();
//				getTheLocalContainerPage().setPageComplete(getTheLocalContainerPage().isPageComplete());
				
				}
			});
		btnCustomLibrary.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				boolean tempSelectionStatus = btnCustomLibrary.getSelection();
				getObjectForDataInNonGuidedMode().setCustomLibraryRequired(tempSelectionStatus);
				grpContainerGroupForLibrary.setVisible(tempSelectionStatus);
				
				
//				if (!((GroupBrowseForFile)grpContainerGroupForLibrary.getChildren()[0]).getDecNameOfTheTask().getDescriptionText().contains("ERROR") && tempSelectionStatus 
//					&& !getDecNameOfTheTask().getDescriptionText().contains("ERROR")) {
//					getTheLocalContainerPage().setPageComplete(true);
//				} else {
//					getTheLocalContainerPage().setPageComplete(false);
//				}
					
				getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
				
//				if (((GroupBrowseForFile)grpContainerGroupForLibrary.getChildren()[0]).getDecNameOfTheTask().getDescriptionText().contains("ERROR") && grpContainerGroupForLibrary.isVisible()) {
//					getTheLocalContainerPage().setPageComplete(false);
//				} else if (!grpContainerGroupForLibrary.isVisible() && getDecNameOfTheTask().getDescriptionText().contains("ERROR")) {
//					
//				}
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
				//TODO Validate!
			}
		});
		
		
		//getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
				
				
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
			getDecNameOfTheTask().setImage(Constants.DEC_ERROR);
			getDecNameOfTheTask().setDescriptionText("ERROR: The Task name cannot be empty. Please enter a valid name for the Task.");
//			getTheLocalContainerPage().setPageComplete(true);
			getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
		}else if (validString) {
			getDecNameOfTheTask().setImage(Constants.DEC_REQUIRED);
			getDecNameOfTheTask().setDescriptionText("This is a required field.");
//			getTheLocalContainerPage().setPageComplete(true);
			getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
		} else {
			getDecNameOfTheTask().setImage(Constants.DEC_ERROR);
			getDecNameOfTheTask().setDescriptionText("ERROR: A task with this name already exists.");
			getDecNameOfTheTask().showHoverText(getDecNameOfTheTask().getDescriptionText());
//			getTheLocalContainerPage().setPageComplete(false);
			getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
		}
		
		return validString;
	}

	/**
	 * @return the objectForDataInNonGuidedMode
	 */
	public ModelAdvancedMode getObjectForDataInNonGuidedMode() {
		return objectForDataInNonGuidedMode;
	}

	/**
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
