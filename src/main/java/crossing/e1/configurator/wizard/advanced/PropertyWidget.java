package crossing.e1.configurator.wizard.advanced;


import java.util.ArrayList;
import java.util.List;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;


import crossing.e1.featuremodel.clafer.ClaferModelUtils;

public class PropertyWidget {
	private Spinner valueSpinner;
	private AstClafer parentClafer;
	private AstConcreteClafer childClafer;
	private ComboViewer operatorComboViewer;
	private boolean isGroupConstraint = false;
	private AstAbstractClafer abstarctParentClafer;
	private Button enablePropertyCheckBox;

	// TODO THIS IS A WORKAROUND TO STOP INSTANCE GENERATION ON PAGE LOAD, NEEDS
	// TO BE FIXED
	public static boolean status = false;

	/**
	 * Method to create a widget for specific properties, task level constraints
	 * 
	 * @param container
	 * @param parentClafer
	 * @param childClafer
	 * @param propertyName
	 * @param selection
	 * @param min
	 * @param max
	 * @param digits
	 * @param increment
	 * @param pageincrement
	 */
	public PropertyWidget(Composite container, AstClafer parentClafer, AstConcreteClafer childClafer,
			String propertyName, int selection, int min, int max, int digits, int increment, int pageincrement) {
		this.setChildClafer(childClafer);
		this.setParentClafer(parentClafer);
		List<String> values = new ArrayList<String>();
		values.add("<");
		values.add(">");
		values.add("=");
		values.add("<=");
		values.add(">=");

		// To create a tab in the first column
		Label emptySpace = new Label(container, SWT.NONE);
		emptySpace.setText("	");
		
		enablePropertyCheckBox = new Button(container, SWT.CHECK);
		enablePropertyCheckBox.setSelection(false);
		
		enablePropertyCheckBox.addSelectionListener(new SelectionAdapter()
		{
		    @Override
		    public void widgetSelected(SelectionEvent e)
		    {
		        Button button = (Button) e.widget;
		        if (button.getSelection()){
		        	valueSpinner.setEnabled(true);
		        }		            
		        else
		        	valueSpinner.setEnabled(false);
		    }
		});
		
		
		Label propertyNameLabel = new Label(container, SWT.NONE);
		propertyNameLabel.setText(propertyName);
	

		operatorComboViewer = new ComboViewer(container, SWT.NONE);
		operatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		operatorComboViewer.setInput(values);
		
		operatorComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent arg0) {
				operatorComboViewer.refresh();
			}
		});
		
		operatorComboViewer.setSelection(new StructuredSelection(values.get(2)));

		valueSpinner = new Spinner(container, SWT.BORDER | SWT.SINGLE);
		valueSpinner.setValues(selection, min, max, digits, increment, pageincrement);
		valueSpinner.setEnabled(false);
	}

	/**
	 * Method to create a widget for group properties, clafer level constraints
	 * 
	 * @param container
	 * @param claferMain
	 * @param claferProperties
	 */
	public PropertyWidget(Composite container, AstAbstractClafer claferMain, List<AstClafer> claferProperties) {
		setGroupConstraint(true);
		setAbstarctParentClafer(claferMain);
		setChildClafer((AstConcreteClafer) claferProperties.get(0));
		List<String> values = new ArrayList<String>();
		values.add("<");
		values.add(">");
		values.add("=");
		values.add("<=");
		values.add(">=");
		Label label5 = new Label(container, SWT.NONE);
		label5.setText("	");

		Label groupName = new Label(container, SWT.NONE);
		groupName.setText(ClaferModelUtils.removeScopePrefix(claferMain.getName()));

		operatorComboViewer = new ComboViewer(container, SWT.NONE);
		operatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		operatorComboViewer.setInput(values);
		
		ArrayList<String> propertyNames = new ArrayList<String>();
		for (AstClafer propertyClafer : claferProperties) {
			propertyNames.add(ClaferModelUtils.removeScopePrefix(propertyClafer.getName()));
		}
		operatorComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				status = true;

			}
		});
		
		operatorComboViewer.setSelection(new StructuredSelection(values.get(2)));
		
		ComboViewer valuesCombo = new ComboViewer(container, SWT.NONE);
		valuesCombo.setContentProvider(ArrayContentProvider.getInstance());
		valuesCombo.setInput(propertyNames);
		
		valuesCombo.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				status = true;
				String selection = valuesCombo.getSelection().toString();
				for (AstClafer property : claferProperties) {
					if (selection.equals(ClaferModelUtils.removeScopePrefix(property.getName()))) {
						setChildClafer((AstConcreteClafer) property);
					}
				}
			}
		});
		
		valuesCombo.setSelection(new StructuredSelection(propertyNames.get(0)));

	}

	public AstAbstractClafer getAbstarctParentClafer() {
		return abstarctParentClafer;
	}

	public void setAbstarctParentClafer(AstAbstractClafer abstarctParentClafer) {
		this.abstarctParentClafer = abstarctParentClafer;
	}

	/**
	 * @return the childClafer
	 */
	public AstConcreteClafer getChildClafer() {
		return childClafer;
	}

	/**
	 * @param childClafer
	 *            the childClafer to set
	 */
	public void setChildClafer(AstConcreteClafer childClafer) {
		this.childClafer = childClafer;
	}

	/**
	 * @return the parentClafer
	 */
	public AstClafer getParentClafer() {
		return parentClafer;
	}

	/**
	 * @param parentClafer
	 *            the parentClafer to set
	 */
	public void setParentClafer(AstClafer parentClafer) {
		this.parentClafer = parentClafer;
	}

	public String getOperator() {
		return ((IStructuredSelection) operatorComboViewer.getSelection()).getFirstElement().toString();
	}

	public Integer getValue() {
		return valueSpinner.getSelection();
	}

	/**
	 * @return the isGroupConstraint
	 */
	public boolean isGroupConstraint() {
		return isGroupConstraint;
	}

	/**
	 * @param isGroupConstraint
	 *            the isGroupConstraint to set
	 */
	public void setGroupConstraint(boolean isGroupConstraint) {
		this.isGroupConstraint = isGroupConstraint;
	}
	
	public boolean isEnabled(){
		return valueSpinner.isEnabled();
	}
	
	@Override
	public String toString(){
		return "[parent:" + parentClafer.getName() + ", child: " + childClafer.getName() + ", operator: " + getOperator() + ", value:" + getValue() + ", isGroupConstraint: "+ isGroupConstraint + "]";		
	}
}
