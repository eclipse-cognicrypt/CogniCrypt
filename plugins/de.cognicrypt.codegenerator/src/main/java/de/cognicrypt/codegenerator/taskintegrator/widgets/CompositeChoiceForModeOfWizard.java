/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;

import java.util.List;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;
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
	private ControlDecoration decNameOfTheTask;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeChoiceForModeOfWizard(Composite parent, int style) {		
		super(parent, SWT.BORDER);
		
		// these tasks are required for validation of the new task that is being added.
		existingTasks = TaskJSONReader.getTasks();
		
		objectForDataInNonGuidedMode = new ModelAdvancedMode();
		this.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// All the UI widgets
		Group grpChooseTheMode = new Group(this, SWT.NONE);
		grpChooseTheMode.setText("Choose the mode of this Wizard");
		grpChooseTheMode.setLayout(new GridLayout(1, false));
		
		Label lblNameOfTheTask = new Label(grpChooseTheMode, SWT.NONE);
		lblNameOfTheTask.setText("Name of the Task ");
		decNameOfTheTask = new ControlDecoration(lblNameOfTheTask, SWT.TOP | SWT.RIGHT);
		//decNameOfTheTask.setDescriptionText(Constants.GUIDED_MODE_CHECKBOX_INFO);
		decNameOfTheTask.setImage(Constants.DEC_REQUIRED);
		decNameOfTheTask.setShowOnlyOnFocus(false);
		
		Text txtForTaskName = new Text(grpChooseTheMode, SWT.BORDER);
		txtForTaskName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		txtForTaskName.setTextLimit(Constants.SINGLE_LINE_TEXT_BOX_LIMIT);
		
		Label lblDescriptionOfThe = new Label(grpChooseTheMode, SWT.NONE);
		lblDescriptionOfThe.setText("Description of the Task :");
		
		txtDescriptionOfTask = new Text(grpChooseTheMode, SWT.BORDER | SWT.WRAP | SWT.MULTI);		
		GridData gd_txtDescriptionOfTask = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtDescriptionOfTask.heightHint = 67;
		txtDescriptionOfTask.setLayoutData(gd_txtDescriptionOfTask);
		txtDescriptionOfTask.setTextLimit(Constants.MULTI_LINE_TEXT_BOX_LIMIT);
		
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
		
		new GroupBrowseForFile(grpContainerGroupForLibrary,SWT.NONE,Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK,new String[] {"*.jar"},"Select file that contains the library");
		
		
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
		
		
		
		new GroupBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE, new String[] {"*.cfr"}, "Select cfr file that contains the Clafer features");
		
		new GroupBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_XSL_FILE, new String[] {"*.xsl"}, "Select xsl file that contains the code details");
		
		new GroupBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE, new String[] {"*.json"}, "Select json file that contains the high level questions");
		
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
				grpNonguidedMode.setVisible(!btnDoYouWishToUseTheGuidedMode.getSelection());
				getObjectForDataInNonGuidedMode().setGuidedModeChosen(btnDoYouWishToUseTheGuidedMode.getSelection());
				}
			});
		btnCustomLibrary.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				getObjectForDataInNonGuidedMode().setCustomLibraryRequired(btnCustomLibrary.getSelection());
				grpContainerGroupForLibrary.setVisible(btnCustomLibrary.getSelection());
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
				boolean validString = true;
				// Validation : check whether the name already exists.
				
				for (Task task : existingTasks) {
					if (task.getName().equals(tempName) || task.getDescription().equals(tempName)) {
						validString = false;						
						break;
					}
				}
				
				if (validString) {
					getObjectForDataInNonGuidedMode().setNameOfTheTask(tempName);
					decNameOfTheTask.setImage(Constants.DEC_REQUIRED);
					decNameOfTheTask.setDescriptionText("This is a required field.");
				} else {
					decNameOfTheTask.setImage(Constants.DEC_ERROR);
					decNameOfTheTask.setDescriptionText("A task with this name already exists.");
					decNameOfTheTask.showHoverText(decNameOfTheTask.getDescriptionText());					
				}
				
			}
		});
		
		txtDescriptionOfTask.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getObjectForDataInNonGuidedMode().setTaskDescription(txtDescriptionOfTask.getText().trim());
				//TODO Validate!
			}
		});
				
				
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public boolean validateTextBoxes(){
		return true;
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
}
