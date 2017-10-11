package crossing.e1.taskintegrator.wizard;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import crossing.e1.configurator.Constants;
import crossing.e1.configurator.Constants.XSLTags;
import crossing.e1.taskintegrator.widgets.CompositeToHoldGranularUIElements;
import crossing.e1.taskintegrator.widgets.CompositeToHoldSmallerUIElements;
import crossing.e1.taskintegrator.widgets.GroupXSLTagAttribute;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class XSLTagDialog extends Dialog {
	private CompositeToHoldSmallerUIElements compositeForXSLAttributes;
	private Button btnAddAttribute;
	private Combo comboXSLTags;
	private String currentSelectionStringOncomboXSLTags;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public XSLTagDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		comboXSLTags = new Combo(container, SWT.NONE);
		
		GridData gd_comboXSLTags = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_comboXSLTags.widthHint = 430;
		comboXSLTags.setLayoutData(gd_comboXSLTags);
		
		for(XSLTags tag : Constants.XSLTags.values()){
			comboXSLTags.add(tag.getXSLTagFaceName());			
		}
		
		
		
		btnAddAttribute = new Button(container, SWT.NONE);
		
		btnAddAttribute.setText("Add Attribute");
		
		compositeForXSLAttributes = new CompositeToHoldSmallerUIElements(container, SWT.NONE, null, true);
		GridData gd_compositeForProperties = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_compositeForProperties.widthHint = 417;
		gd_compositeForProperties.heightHint = 150;
		compositeForXSLAttributes.setLayoutData(gd_compositeForProperties);
		
		
		// for the default selection. Moving it below all of the controls.
		comboXSLTags.select(0);
		// Exception, need to set the value now, before notifying the listeners.
		setCurrentSelectionStringOncomboXSLTags(comboXSLTags.getText());
		comboXSLTags.notifyListeners(SWT.Selection, new Event());
		
		btnAddAttribute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ArrayList<String> possibleAttributes = getListOfPossibleAttributes(comboXSLTags.getText());
				// If no more attributes possible.
				if(possibleAttributes.size()>0){
					compositeForXSLAttributes.addXSLAttributeUI(comboXSLTags.getText(), true, possibleAttributes);
				} else{
					MessageBox headsUpMessageBox = new MessageBox(getShell(), SWT.ICON_INFORMATION
			            | SWT.OK);
					headsUpMessageBox.setMessage("All possible attributes have been used up.");
					headsUpMessageBox.setText("Cannot add attibutes");				
			        headsUpMessageBox.open();
				}
				// TODO The dropdown for the attributes remains in an inconsistent state as we add new attributes.
				// after the addition of each attribute, update all the groups with the current state of available attributes.
			}
			
		});
		
		// Disable the add button if there are no attributes possible. E.g. the choose tag.
		comboXSLTags.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {		
				// At this point the value of the selection has already been altered.
				
				if(((Composite)compositeForXSLAttributes.getContent()).getChildren().length > 0){
					MessageBox confirmationMessageBox = new MessageBox(getShell(), SWT.ICON_WARNING
			            | SWT.YES | SWT.NO);
					confirmationMessageBox.setMessage("Are you sure you wish to change the tag? All attibutes will be lost.");
					confirmationMessageBox.setText("Changing the XSL tag");				
			        int response = confirmationMessageBox.open();
			        if (response == SWT.YES){
						disposeAllAttributes();		
						setCurrentSelectionStringOncomboXSLTags(comboXSLTags.getText());	
			        } else{
			        	// If the user opts out of the change, replace the already changed value with the old one.
			        	for(int i=0; i< comboXSLTags.getItemCount();i++){
			        		if(comboXSLTags.getItems()[i].equals(getCurrentSelectionStringOncomboXSLTags())){
			        			comboXSLTags.select(i);
			        		}
			        	}		        	
			        }
				}
				setEnabledForAddAttributeButton();		
			}
			
		});
		
		return container;
	}
	
	private ArrayList<String> getListOfPossibleAttributes(String selectionOnComboXSLTags) {
		
		ArrayList<String> listOfPossibleAttributes = new ArrayList<String>();
		
		for(XSLTags XSLTag : Constants.XSLTags.values()){
			if(XSLTag.getXSLTagFaceName().equals(selectionOnComboXSLTags)){
				for(String attribute : XSLTag.getXSLAttributes()){
					listOfPossibleAttributes.add(attribute);
				}
			}
		}
				
		
		 for(Control attribute : ((Composite)compositeForXSLAttributes.getContent()).getChildren()){			 
			 if(listOfPossibleAttributes.contains(((GroupXSLTagAttribute) attribute).getSelectedAttributeName())){
				 listOfPossibleAttributes.remove(((GroupXSLTagAttribute) attribute).getSelectedAttributeName());
			 }
		 }
		
		 //setEnabledForAddAttributeButton();
		return listOfPossibleAttributes;
	}
	
	private void disposeAllAttributes() {		
		for(Control uiRepresentationOfXSLAttributes : ((Composite)compositeForXSLAttributes.getContent()).getChildren()){
			uiRepresentationOfXSLAttributes.dispose();
		}
		
		
		compositeForXSLAttributes.setLowestWidgetYAxisValue(0);
	}
	
	private void setEnabledForAddAttributeButton(){
		
		for(XSLTags xslTag: Constants.XSLTags.values()){
			if(comboXSLTags.getText().equals(xslTag.getXSLTagFaceName())){
				if(xslTag.getXSLAttributes() == null){
					btnAddAttribute.setEnabled(false);
				} else{
					btnAddAttribute.setEnabled(true);
				}						
				break;
			}
		}		
		
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);		
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	/**
	 * @return the currentSelectionStringOncomboXSLTags
	 */
	public String getCurrentSelectionStringOncomboXSLTags() {
		return currentSelectionStringOncomboXSLTags;
	}

	/**
	 * @param currentSelectionStringOncomboXSLTags the currentSelectionStringOncomboXSLTags to set
	 */
	private void setCurrentSelectionStringOncomboXSLTags(String currentSelectionStringOncomboXSLTags) {
		this.currentSelectionStringOncomboXSLTags = currentSelectionStringOncomboXSLTags;
	}

}
