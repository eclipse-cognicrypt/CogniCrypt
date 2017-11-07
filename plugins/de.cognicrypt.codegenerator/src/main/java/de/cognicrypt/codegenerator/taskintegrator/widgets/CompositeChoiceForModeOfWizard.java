/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

import de.cognicrypt.codegenerator.Constants;

/**
 * @author rajiv
 *
 */
public class CompositeChoiceForModeOfWizard extends Composite {
	private Text txtForTaskName;
	private Text txtLibraryLocation;
	private Text txtLocationOfClaferFile;
	private Text txtLocationOfXSLFile;
	private Text txtLocationOfJSONFile;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompositeChoiceForModeOfWizard(Composite parent, int style) {
		super(parent, SWT.BORDER);
		this.setBounds(Constants.RECTANGLE_FOR_COMPOSITES);
		setLayout(null);
		
		// All the UI widgets
		Group grpChooseTheMode = new Group(this, SWT.NONE);
		grpChooseTheMode.setText("Choose the mode of this Wizard");
		grpChooseTheMode.setBounds(10, 10, 508, 310);
		
		Label lblNameOfTheTask = new Label(grpChooseTheMode, SWT.NONE);
		lblNameOfTheTask.setBounds(10, 10, 104, Constants.UI_WIDGET_HEIGHT_NORMAL);
		lblNameOfTheTask.setText("Name of the Task :");
		
		txtForTaskName = new Text(grpChooseTheMode, SWT.NONE);
		txtForTaskName.setBounds(120, 10, 379, Constants.UI_WIDGET_HEIGHT_NORMAL);
				
		Combo cmbLibraryLocation = new Combo(grpChooseTheMode, SWT.NONE);		
		cmbLibraryLocation.setBounds(10, 46, 153, Constants.UI_WIDGET_HEIGHT_NORMAL);
		cmbLibraryLocation.setItems(new String[] {Constants.WIDGET_CONTENT_EXISTING_LIBRARY, Constants.WIDGET_CONTENT_CUSTOM_LIBRARY});
		// Choose existing library by default.
		cmbLibraryLocation.select(0);
		
		Group grpLibraryWidgets = new Group(grpChooseTheMode, SWT.NONE);
		grpLibraryWidgets.setBounds(169, 46, 330, 32);
		// Keep invisible since the default selection is existing library.
		grpLibraryWidgets.setVisible(false);
		
		Label lblLibraryLocation = new Label(grpLibraryWidgets, SWT.NONE);
		lblLibraryLocation.setLocation(10, 0);
		lblLibraryLocation.setSize(95, Constants.UI_WIDGET_HEIGHT_NORMAL);
		lblLibraryLocation.setText("Library Location :");
		
		txtLibraryLocation = new Text(grpLibraryWidgets, SWT.BORDER);
		txtLibraryLocation.setBounds(111, 0, 156, Constants.UI_WIDGET_HEIGHT_NORMAL);
		
		Button btnBrowseForLibraryLocation = new Button(grpLibraryWidgets, SWT.NONE);
		btnBrowseForLibraryLocation.setBounds(273, 0, 56, Constants.UI_WIDGET_HEIGHT_NORMAL);
		btnBrowseForLibraryLocation.setText(Constants.LABEL_BROWSE_BUTTON);
		
		Button btnDoYouWishToUseTheGuidedMode = new Button(grpChooseTheMode, SWT.CHECK);		
		btnDoYouWishToUseTheGuidedMode.setBounds(10, 82, 238, Constants.UI_WIDGET_HEIGHT_NORMAL);
		btnDoYouWishToUseTheGuidedMode.setText("Do you wish to use the guided mode?");
		// guided mode by default.
		btnDoYouWishToUseTheGuidedMode.setSelection(true);
		
		Group grpNonguidedMode = new Group(grpChooseTheMode, SWT.NONE);
		grpNonguidedMode.setBounds(10, 119, 489, 166);
		grpNonguidedMode.setText("Non-Guided mode");
		grpNonguidedMode.setVisible(false);
		
		Label lblLocationOfClaferFile = new Label(grpNonguidedMode, SWT.NONE);
		lblLocationOfClaferFile.setText("Location of the Clafer file :");
		lblLocationOfClaferFile.setBounds(10, 10, 150, Constants.UI_WIDGET_HEIGHT_NORMAL);
		
		txtLocationOfClaferFile = new Text(grpNonguidedMode, SWT.BORDER);
		txtLocationOfClaferFile.setBounds(163, 10, 255, Constants.UI_WIDGET_HEIGHT_NORMAL);
		
		Button btnLocationOfClaferFileBrowse = new Button(grpNonguidedMode, SWT.NONE);
		btnLocationOfClaferFileBrowse.setText(Constants.LABEL_BROWSE_BUTTON);
		btnLocationOfClaferFileBrowse.setBounds(421, 10, 56, Constants.UI_WIDGET_HEIGHT_NORMAL);
		
		Label lblLocationOfXSLFile = new Label(grpNonguidedMode, SWT.NONE);
		lblLocationOfXSLFile.setText("Location of the XSL file :");
		lblLocationOfXSLFile.setBounds(10, 46, 150, Constants.UI_WIDGET_HEIGHT_NORMAL);
		
		txtLocationOfXSLFile = new Text(grpNonguidedMode, SWT.BORDER);		
		txtLocationOfXSLFile.setBounds(162, 46, 256, Constants.UI_WIDGET_HEIGHT_NORMAL);
		
		Button btnLocationOfXSLFileBrowse = new Button(grpNonguidedMode, SWT.NONE);
		btnLocationOfXSLFileBrowse.setText(Constants.LABEL_BROWSE_BUTTON);
		btnLocationOfXSLFileBrowse.setBounds(421, 46, 56, Constants.UI_WIDGET_HEIGHT_NORMAL);
		
		Label lblLocationOfJSONFile = new Label(grpNonguidedMode, SWT.NONE);
		lblLocationOfJSONFile.setText("Location of the questions :");
		lblLocationOfJSONFile.setBounds(10, 82, 150, Constants.UI_WIDGET_HEIGHT_NORMAL);
		
		txtLocationOfJSONFile = new Text(grpNonguidedMode, SWT.BORDER);		
		txtLocationOfJSONFile.setBounds(162, 82, 256, Constants.UI_WIDGET_HEIGHT_NORMAL);
		
		Button btnLocationOfJSONFileBrowse = new Button(grpNonguidedMode, SWT.NONE);
		btnLocationOfJSONFileBrowse.setText(Constants.LABEL_BROWSE_BUTTON);
		btnLocationOfJSONFileBrowse.setBounds(421, 82, 56, Constants.UI_WIDGET_HEIGHT_NORMAL);
		/* TODO removed for the user study.
		Button btnForceGuidedMode = new Button(grpNonguidedMode, SWT.CHECK);
		btnForceGuidedMode.setBounds(10, 118, 142, Constants.UI_WIDGET_HEIGHT_NORMAL);
		btnForceGuidedMode.setText("Force guided mode");*/	

		
		// Creating empty/default data key value pairs.
		this.setData(Constants.WIDGET_DATA_NAME_OF_THE_TASK, txtForTaskName.getText());
		this.setData(Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE, txtLocationOfJSONFile.getText());
		this.setData(Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK, txtLibraryLocation.getText());
		this.setData(Constants.WIDGET_DATA_LOCATION_OF_XSL_FILE, txtLocationOfXSLFile.getText());
		this.setData(Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE, txtLocationOfClaferFile.getText());
		// false by default since default selection is not using custom library.
		this.setData(Constants.WIDGET_DATA_IS_CUSTOM_LIBRARY_REQUIRED, cmbLibraryLocation.getText().equals(Constants.WIDGET_CONTENT_CUSTOM_LIBRARY) ? true : false);		
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
		
		cmbLibraryLocation.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {				
				boolean isCustomLibraryRequired = cmbLibraryLocation.getText().equals(Constants.WIDGET_CONTENT_CUSTOM_LIBRARY) ? true : false;				
				cmbLibraryLocation.getParent().getParent().setData(Constants.WIDGET_DATA_IS_CUSTOM_LIBRARY_REQUIRED, isCustomLibraryRequired);				
				grpLibraryWidgets.setVisible(isCustomLibraryRequired);				
			}
		});
		/* TODO removed for the user study.
		btnForceGuidedMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnForceGuidedMode.getParent().getParent().getParent().setData(Constants.WIDGET_DATA_IS_GUIDED_MODE_FORCED, btnForceGuidedMode.getSelection());
			}
		});*/
		
		// All the text box listeners.
		txtLocationOfJSONFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				txtLocationOfJSONFile.getParent().getParent().getParent().setData(Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE, txtLocationOfJSONFile.getText());
				// TODO Validate!
			}
		});
		
		txtLocationOfXSLFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				txtLocationOfXSLFile.getParent().getParent().getParent().setData(Constants.WIDGET_DATA_LOCATION_OF_XSL_FILE, txtLocationOfXSLFile.getText());
				// TODO Validate!
			}
		});
		txtForTaskName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// First getParent() gives the group, the second getParent() gives the composite.
				txtForTaskName.getParent().getParent().setData(Constants.WIDGET_DATA_NAME_OF_THE_TASK, txtForTaskName.getText());		
				// TODO Validate!
			}
		});
				
		txtLibraryLocation.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				txtLibraryLocation.getParent().getParent().getParent().setData(Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK, txtLibraryLocation.getText());
				// TODO Validate!
			}
		});
		
		txtLocationOfClaferFile.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				txtLocationOfClaferFile.getParent().getParent().getParent().setData(Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE, txtLocationOfClaferFile.getText());
				// TODO Validate!
			}
		});
		
		// All the browse button listeners.
		btnBrowseForLibraryLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {						        
		        txtLibraryLocation.setText(openFileDialog(new String[] {"*.jar"},"Select file that contains the library"));
			}
		});
		
		btnLocationOfXSLFileBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtLocationOfXSLFile.setText(openFileDialog(new String[] {"*.xsl"},"Select xsl file that contains the code details:"));
			}
		});
		
		btnLocationOfJSONFileBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtLocationOfJSONFile.setText(openFileDialog(new String[] {"*.json"},"Select json file that contains the high level questions:"));
			}
		});
		
		btnLocationOfClaferFileBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtLocationOfClaferFile.setText(openFileDialog(new String[] {"*.cfr"},"Select cfr file that contains the Clafer features:"));
			}
		});
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private String openFileDialog(String[] constraints, String title){
		FileDialog fileDialog = new FileDialog(getShell(),SWT.OPEN);		
        fileDialog.setFilterExtensions(constraints);
        fileDialog.setText(title);
		return fileDialog.open();
	}
	
	public boolean validateTextBoxes(){
		return true;
	}
}
