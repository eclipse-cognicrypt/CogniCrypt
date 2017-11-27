/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

/**
 * @author rajiv
 *
 */
public class CompositeChoiceForModeOfWizard extends Composite {
	private ModelAdvancedMode objectForDataInNonGuidedMode;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeChoiceForModeOfWizard(Composite parent, int style) {
		super(parent, SWT.BORDER);
		objectForDataInNonGuidedMode = new ModelAdvancedMode();
		this.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// All the UI widgets
		Group grpChooseTheMode = new Group(this, SWT.NONE);
		grpChooseTheMode.setText("Choose the mode of this Wizard");
		grpChooseTheMode.setLayout(new FormLayout());
		
		Label lblNameOfTheTask = new Label(grpChooseTheMode, SWT.NONE);
		FormData fd_lblNameOfTheTask = new FormData();
		fd_lblNameOfTheTask.right = new FormAttachment(100, -3);
		fd_lblNameOfTheTask.top = new FormAttachment(0, 3);
		fd_lblNameOfTheTask.left = new FormAttachment(0, 3);
		lblNameOfTheTask.setLayoutData(fd_lblNameOfTheTask);
		lblNameOfTheTask.setText("Name of the Task :");
		
		Text txtForTaskName = new Text(grpChooseTheMode, SWT.BORDER);
		FormData fd_txtForTaskName = new FormData();
		fd_txtForTaskName.right = new FormAttachment(100, -3);
		fd_txtForTaskName.top = new FormAttachment(0, 23);
		fd_txtForTaskName.left = new FormAttachment(0, 3);
		txtForTaskName.setLayoutData(fd_txtForTaskName);
		
		Button btnCustomLibrary = new Button(grpChooseTheMode, SWT.CHECK);		
		FormData fd_btnCustomLibrary = new FormData();
		fd_btnCustomLibrary.right = new FormAttachment(100, -3);
		fd_btnCustomLibrary.top = new FormAttachment(0, 55);
		fd_btnCustomLibrary.left = new FormAttachment(0, 3);
		btnCustomLibrary.setLayoutData(fd_btnCustomLibrary);
		btnCustomLibrary.setText("Do you wish to use a custom library?");
				
		
		Group grpContainerGroupForLibrary = new Group(grpChooseTheMode, SWT.NONE);
		FormData fd_grpContainerGroupForLibrary = new FormData();
		fd_grpContainerGroupForLibrary.right = new FormAttachment(100, -3);
		fd_grpContainerGroupForLibrary.top = new FormAttachment(0, 81);
		fd_grpContainerGroupForLibrary.left = new FormAttachment(0, 3);
		grpContainerGroupForLibrary.setLayoutData(fd_grpContainerGroupForLibrary);
		grpContainerGroupForLibrary.setText("Custom Library");
		grpContainerGroupForLibrary.setVisible(false);
		RowLayout rl_grpContainerGroupForLibrary = new RowLayout(SWT.VERTICAL);
		rl_grpContainerGroupForLibrary.fill = true;
		grpContainerGroupForLibrary.setLayout(rl_grpContainerGroupForLibrary);
		grpContainerGroupForLibrary.setVisible(false);
		
		new GroupBrowseForFile(grpContainerGroupForLibrary,SWT.NONE,Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK,new String[] {"*.jar"},"Select file that contains the library");
		
		
		Button btnDoYouWishToUseTheGuidedMode = new Button(grpChooseTheMode, SWT.CHECK);
		FormData fd_btnDoYouWishToUseTheGuidedMode = new FormData();
		fd_btnDoYouWishToUseTheGuidedMode.right = new FormAttachment(100, -3);
		fd_btnDoYouWishToUseTheGuidedMode.top = new FormAttachment(0, 159);
		fd_btnDoYouWishToUseTheGuidedMode.left = new FormAttachment(0, 3);
		btnDoYouWishToUseTheGuidedMode.setLayoutData(fd_btnDoYouWishToUseTheGuidedMode);
		btnDoYouWishToUseTheGuidedMode.setText("Do you wish to use the guided mode?");
		// guided mode by default.
		btnDoYouWishToUseTheGuidedMode.setSelection(true);
		
		Group grpNonguidedMode = new Group(grpChooseTheMode, SWT.NONE);
		FormData fd_grpNonguidedMode = new FormData();
		fd_grpNonguidedMode.right = new FormAttachment(100, -3);
		fd_grpNonguidedMode.top = new FormAttachment(0, 189);
		fd_grpNonguidedMode.left = new FormAttachment(0, 3);
		grpNonguidedMode.setLayoutData(fd_grpNonguidedMode);
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
				//btnDoYouWishToUseTheGuidedMode.getParent().getParent().setData(Constants.WIDGET_DATA_IS_GUIDED_MODE_CHOSEN, btnDoYouWishToUseTheGuidedMode.getSelection());
				
				getObjectForDataInNonGuidedMode().setGuidedModeChosen(btnDoYouWishToUseTheGuidedMode.getSelection());
				}
			});
		btnCustomLibrary.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//btnCustomLibrary.getParent().getParent().setData(Constants.WIDGET_DATA_IS_CUSTOM_LIBRARY_REQUIRED, btnCustomLibrary.getSelection());
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
				// First getParent() gives the group, the second getParent() gives the composite.
				//txtForTaskName.getParent().getParent().setData(Constants.WIDGET_DATA_NAME_OF_THE_TASK, txtForTaskName.getText());
				getObjectForDataInNonGuidedMode().setNameOfTheTask(txtForTaskName.getText());
				
				// TODO Validate!
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
