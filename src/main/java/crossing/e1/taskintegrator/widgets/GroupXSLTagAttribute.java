package crossing.e1.taskintegrator.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;


public class GroupXSLTagAttribute extends Group {
	private Text txtAttributeName;
	private String selectedAttributeName;
	private String selectedAttributeData;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public GroupXSLTagAttribute(Composite parent, int style, boolean showRemoveButton, ArrayList<String> listOfPossibleAttributes) {
		super(parent, style);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Combo cmbAttributeType = new Combo(this, SWT.NONE);
		cmbAttributeType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectedAttributeName(cmbAttributeType.getText());
			}
		});
		cmbAttributeType.setLayoutData(new RowData(148, SWT.DEFAULT));
		
		for(String attribute : listOfPossibleAttributes){
			cmbAttributeType.add(attribute);
		}
		cmbAttributeType.select(0);
		cmbAttributeType.notifyListeners(SWT.Selection, new Event());
		//setSelectedAttributeName(cmbAttributeType.getText());
		
		txtAttributeName = new Text(this, SWT.BORDER);
		// Blank initial value.
		setSelectedAttributeData("");
		txtAttributeName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				setSelectedAttributeData(txtAttributeName.getText());
			}
		});
		txtAttributeName.setLayoutData(new RowData(167, SWT.DEFAULT));
		
		if(showRemoveButton){
			Button btnRemove = new Button(this, SWT.NONE);
			btnRemove.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO 
				}
			});
			btnRemove.setText("Remove");
		}
		

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the selectedAttribute
	 */
	public String getSelectedAttributeName() {
		return selectedAttributeName;
	}

	/**
	 * @param selectedAttribute the selectedAttribute to set
	 */
	private void setSelectedAttributeName(String selectedAttribute) {
		this.selectedAttributeName = selectedAttribute;
	}

	/**
	 * @return the selectedAttributeData
	 */
	public String getSelectedAttributeData() {
		return selectedAttributeData;
	}

	/**
	 * @param selectedAttributeData the selectedAttributeData to set
	 */
	private void setSelectedAttributeData(String selectedAttributeData) {
		this.selectedAttributeData = selectedAttributeData;
	}
}
