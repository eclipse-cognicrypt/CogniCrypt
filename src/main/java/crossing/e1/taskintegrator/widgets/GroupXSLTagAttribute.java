package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import crossing.e1.taskintegrator.models.XSLAttribute;

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
		
		setSelectedAttribute(attributeParam);
		
		cmbAttributeType = new Combo(this, SWT.NONE);
		cmbAttributeType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getSelectedAttribute().setXSLAttributeName(cmbAttributeType.getText());
			}
		});
		cmbAttributeType.setLayoutData(new RowData(148, SWT.DEFAULT));
		//cmbAttributeType.add(getSelectedAttribute().getXSLAttributeName());
		//cmbAttributeType.select(0);
		//cmbAttributeType.notifyListeners(SWT.Selection, new Event());
		
		/*if(listOfPossibleAttributes != null){
			for(String attribute : listOfPossibleAttributes){
				cmbAttributeType.add(attribute);
			}
			cmbAttributeType.select(0);
			cmbAttributeType.notifyListeners(SWT.Selection, new Event());
		}*/
		
		//setSelectedAttributeName(cmbAttributeType.getText());
		
		txtAttributeName = new Text(this, SWT.BORDER);
		// Blank initial value.
		//setSelectedAttributeData("");
		txtAttributeName.setText(getSelectedAttribute().getXSLAttributeData());
		txtAttributeName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
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
	
	public void updateAttributeDropDown(ArrayList<String> listOfPossibleAttributes){
		if(listOfPossibleAttributes != null){
			cmbAttributeType.removeAll();
			
			if(!getSelectedAttribute().getXSLAttributeName().equals("")){
				cmbAttributeType.add(getSelectedAttribute().getXSLAttributeName());
			}
			
			for(String attribute : listOfPossibleAttributes){
				cmbAttributeType.add(attribute);
			}
			
			if(getSelectedAttribute().getXSLAttributeName().equals("")){
				cmbAttributeType.select(0);
			} else {
				for(int i=0; i<cmbAttributeType.getItems().length;i++){
					if(cmbAttributeType.getItems()[i].equals(getSelectedAttribute().getXSLAttributeName())){
						cmbAttributeType.select(i);
					}
				}
			}
			
			
			//cmbAttributeType.select(0);
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
