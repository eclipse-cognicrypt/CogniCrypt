package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.taskintegrator.models.ModelAdvancedMode;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;


public class GroupBrowseForFile extends Group {
	private ModelAdvancedMode objectForDataInNonGuidedMode;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GroupBrowseForFile(Composite parent, int style, String labelText, String[] fileTypes, String stringOnFileDialog) {
		super(parent, style);
		// this object is required in the text box listener. Should not be called too often.
		setObjectForDataInNonGuidedMode(((CompositeChoiceForModeOfWizard) getParent().getParent().getParent()).getObjectForDataInNonGuidedMode());
		setLayout(new GridLayout(3, false));
		
		Label label = new Label(this, SWT.NONE);
		label.setText(labelText);		
		Text textBox = new Text(this, SWT.BORDER);			 
		textBox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		Button browseButton = new Button(this, SWT.NONE);	
		browseButton.setText(Constants.LABEL_BROWSE_BUTTON);
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {						        
				textBox.setText(openFileDialog(fileTypes, stringOnFileDialog));
			}
		});
	
		textBox.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {				
				switch(labelText) {
					case Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK :
						getObjectForDataInNonGuidedMode().setLocationOfCustomLibrary(new File(textBox.getText()));
						break;
					case Constants.WIDGET_DATA_LOCATION_OF_CLAFER_FILE: 
						getObjectForDataInNonGuidedMode().setLocationOfClaferFile(new File(textBox.getText()));
						break;
					case Constants.WIDGET_DATA_LOCATION_OF_XSL_FILE: 
						getObjectForDataInNonGuidedMode().setLocationOfXSLFile(new File(textBox.getText()));
						break;
					case Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE: 
						getObjectForDataInNonGuidedMode().setLocationOfJSONFile(new File(textBox.getText()));
						break;
				}
				
				// This is needed to refresh the size of the controls.
				getShell().layout(true, true);
				final Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
				getShell().setSize(newSize);		
				// TODO Validate!
			}
		});
	}
	
	/**
	 * Open the file dialog and return the file path as a string.
	 * @param fileTypes
	 * @param stringOnFileDialog
	 * @return The path selected
	 */
	private String openFileDialog(String[] fileTypes, String stringOnFileDialog){
		FileDialog fileDialog = new FileDialog(getShell(),SWT.OPEN);		
        fileDialog.setFilterExtensions(fileTypes);
        fileDialog.setText(stringOnFileDialog);
		return fileDialog.open();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the objectForDataInNonGuidedMode
	 */
	private ModelAdvancedMode getObjectForDataInNonGuidedMode() {
		return objectForDataInNonGuidedMode;
	}

	/**
	 * @param objectForDataInNonGuidedMode the objectForDataInNonGuidedMode to set
	 */
	private void setObjectForDataInNonGuidedMode(ModelAdvancedMode objectForDataInNonGuidedMode) {
		this.objectForDataInNonGuidedMode = objectForDataInNonGuidedMode;
	}

}
