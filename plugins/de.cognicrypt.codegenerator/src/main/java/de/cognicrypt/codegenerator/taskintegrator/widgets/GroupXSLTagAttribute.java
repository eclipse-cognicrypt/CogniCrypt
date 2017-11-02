package de.cognicrypt.codegenerator.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.taskintegrator.models.XSLAttribute;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;


public class GroupXSLTagAttribute extends Group {
	private Text txtAttributeName;
	private XSLAttribute selectedAttribute;	
	private Combo cmbAttributeType;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GroupXSLTagAttribute(Composite parent, int style, boolean showRemoveButton, XSLAttribute attributeParam) {
		super(parent, style);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		// Set the attribute object first.
		setSelectedAttribute(attributeParam);
		
		cmbAttributeType = new Combo(this, SWT.READ_ONLY);
		cmbAttributeType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getSelectedAttribute().setXSLAttributeName(cmbAttributeType.getText());
			}
		});
		cmbAttributeType.setLayoutData(new RowData(148, SWT.DEFAULT));
				
		txtAttributeName = new Text(this, SWT.BORDER);
		txtAttributeName.setText(getSelectedAttribute().getXSLAttributeData());
		txtAttributeName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				// Update the data for the attribute on loosing focus.
				selectedAttribute.setXSLAttributeData(txtAttributeName.getText());
			}
		});
		txtAttributeName.setLayoutData(new RowData(167, SWT.DEFAULT));
		
		if(showRemoveButton){
			Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					((CompositeToHoldSmallerUIElements) getParent().getParent()).removeXSLAttribute((getSelectedAttribute()));
					((CompositeToHoldSmallerUIElements) getParent().getParent()).updateClaferContainer(); 
				}
			});
			btnRemove.setText("Remove");
		}
		

	}
	
	/**
	 * The attribute drop down needs to be updated after changes have been made to keep them consistent.
	 * @param listOfPossibleAttributes represents the possible attributes based on the attributes already selected.
	 */
	public void updateAttributeDropDown(ArrayList<String> listOfPossibleAttributes){
		if(listOfPossibleAttributes != null){
			cmbAttributeType.removeAll();
			
			// If the current selected attribute does not have an empty name, add the attribute to the dropdown.
			if(!getSelectedAttribute().getXSLAttributeName().equals("")){
				cmbAttributeType.add(getSelectedAttribute().getXSLAttributeName());
			}
			
			// Add the list of possible attributes to the drop down.
			for(String attribute : listOfPossibleAttributes){
				cmbAttributeType.add(attribute);
			}
			
			// If the current selection is empty select the first one by default. 
			if(getSelectedAttribute().getXSLAttributeName().equals("")){
				cmbAttributeType.select(0);
			} else {
				// Otherwise get the index of the stored attribute name from the list of elements and select it.
				for(int i=0; i<cmbAttributeType.getItems().length;i++){
					if(cmbAttributeType.getItems()[i].equals(getSelectedAttribute().getXSLAttributeName())){
						cmbAttributeType.select(i);
					}
				}
			}
			
			cmbAttributeType.notifyListeners(SWT.Selection, new Event());
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the selectedAttribute
	 */
	public XSLAttribute getSelectedAttribute() {
		return selectedAttribute;
	}

	/**
	 * @param selectedAttribute the selectedAttribute to set
	 */
	private void setSelectedAttribute(XSLAttribute selectedAttribute) {
		this.selectedAttribute = selectedAttribute;
	}
}
