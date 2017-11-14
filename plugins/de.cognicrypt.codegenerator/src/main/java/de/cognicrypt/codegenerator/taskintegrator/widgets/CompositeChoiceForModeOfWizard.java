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
import org.eclipse.swt.layout.RowLayout;

/**
 * @author rajiv
 *
 */
public class CompositeChoiceForModeOfWizard extends Composite {
	private Text txtForTaskName;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeChoiceForModeOfWizard(Composite parent, int style) {
		super(parent, SWT.BORDER);
		this.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		// All the UI widgets
		Group grpChooseTheMode = new Group(this, SWT.NONE);
		grpChooseTheMode.setText("Choose the mode of this Wizard");
		//grpChooseTheMode.setBounds(10, 10, 508, 310);
		RowLayout rl_grpChooseTheMode = new RowLayout(SWT.VERTICAL);
		rl_grpChooseTheMode.center = true;
		rl_grpChooseTheMode.fill = true;
		grpChooseTheMode.setLayout(rl_grpChooseTheMode);
		
		Label lblNameOfTheTask = new Label(grpChooseTheMode, SWT.NONE);
		lblNameOfTheTask.setText("Name of the Task :");
		
		txtForTaskName = new Text(grpChooseTheMode, SWT.BORDER);
		
		Button btnCustomLibrary = new Button(grpChooseTheMode, SWT.CHECK);		
		btnCustomLibrary.setText("Do you use a custom library?");
				
		GroupBrowseForFile groupForLibraryFile = new GroupBrowseForFile(grpChooseTheMode,SWT.NONE,Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK,new String[] {"*.jar"},"Select file that contains the library");
		groupForLibraryFile.setVisible(false);
		
		Button btnDoYouWishToUseTheGuidedMode = new Button(grpChooseTheMode, SWT.CHECK);
		btnDoYouWishToUseTheGuidedMode.setText("Do you wish to use the guided mode?");
		// guided mode by default.
		btnDoYouWishToUseTheGuidedMode.setSelection(true);
		
		Group grpNonguidedMode = new Group(grpChooseTheMode, SWT.NONE);
		grpNonguidedMode.setText("Non-Guided mode");
		grpNonguidedMode.setVisible(false);
		RowLayout rl_grpNonguidedMode = new RowLayout(SWT.VERTICAL);
		rl_grpNonguidedMode.fill = true;
		grpNonguidedMode.setLayout(rl_grpNonguidedMode);
		
		
		
		new GroupBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE, new String[] {"*.cfr"}, "Select cfr file that contains the Clafer features");
		
		new GroupBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE, new String[] {"*.xsl"}, "Select xsl file that contains the code details");
		
		new GroupBrowseForFile(grpNonguidedMode, SWT.NONE, Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE, new String[] {"*.json"}, "Select json file that contains the high level questions");
		
		/* TODO removed for the user study.
		Button btnForceGuidedMode = new Button(grpNonguidedMode, SWT.CHECK);
		btnForceGuidedMode.setBounds(10, 118, 142, Constants.UI_WIDGET_HEIGHT_NORMAL);
		btnForceGuidedMode.setText("Force guided mode");*/	

		
		// Creating empty/default data key value pairs.
		this.setData(Constants.WIDGET_DATA_NAME_OF_THE_TASK, "");
		this.setData(Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE, "");
		this.setData(Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK, "");
		this.setData(Constants.WIDGET_DATA_LOCATION_OF_XSL_FILE, "");
		this.setData(Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE, "");
		// false by default since default selection is not using custom library.
		this.setData(Constants.WIDGET_DATA_IS_CUSTOM_LIBRARY_REQUIRED, btnCustomLibrary.getSelection());		
		// true by default since the guided mode is selected by default.
		this.setData(Constants.WIDGET_DATA_IS_GUIDED_MODE_CHOSEN, btnDoYouWishToUseTheGuidedMode.getSelection());
		// false by default since the guided mode is not forced by default.
		// TODO removed for the user study.
		//this.setData(Constants.WIDGET_DATA_IS_GUIDED_MODE_FORCED,btnForceGuidedMode.getSelection());
		
		
		// moved all the event listeners at the bottom.
		btnDoYouWishToUseTheGuidedMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				grpNonguidedMode.setVisible(!btnDoYouWishToUseTheGuidedMode.getSelection());
				btnDoYouWishToUseTheGuidedMode.getParent().getParent().setData(Constants.WIDGET_DATA_IS_GUIDED_MODE_CHOSEN, btnDoYouWishToUseTheGuidedMode.getSelection());
				}
			});
		btnCustomLibrary.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnCustomLibrary.getParent().getParent().setData(Constants.WIDGET_DATA_IS_CUSTOM_LIBRARY_REQUIRED, btnCustomLibrary.getSelection());				
				groupForLibraryFile.setVisible(btnCustomLibrary.getSelection());
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
				txtForTaskName.getParent().getParent().setData(Constants.WIDGET_DATA_NAME_OF_THE_TASK, txtForTaskName.getText());		
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
}
