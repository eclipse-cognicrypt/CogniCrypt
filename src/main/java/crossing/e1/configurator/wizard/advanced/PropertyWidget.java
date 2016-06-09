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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import crossing.e1.configurator.utilities.Labels;
import crossing.e1.configurator.utilities.Utilities;
import crossing.e1.featuremodel.clafer.ClaferModelUtils;

public class PropertyWidget {
	private Spinner valueSpinner;
	private AstConcreteClafer parentClafer;
	private AstConcreteClafer childClafer;
	private ComboViewer operatorComboViewer;
	private boolean isGroupConstraint = false;
	private AstAbstractClafer abstarctParentClafer;

	// TODO THIS IS A WORKAROUND TO STOP INSTANCE GENERATION ON PAGE LOAD, NEEDS
	// TO BE FIXED
	public static boolean status = false;

	/**
	 * Method to create a widget for specific properties, task level constraints
	 * 
	 * @param container
	 * @param parentClafer
	 * @param childClafer
	 * @param label
	 * @param selection
	 * @param min
	 * @param max
	 * @param digits
	 * @param increment
	 * @param pageincrement
	 */
	public PropertyWidget(Composite container, AstConcreteClafer parentClafer, AstConcreteClafer childClafer,
			String label, int selection, int min, int max, int digits, int increment, int pageincrement) {
		this.setChildClafer(childClafer);
		this.setParentClafer(parentClafer);
		List<String> values = new ArrayList<String>();
		values.add("<");
		values.add(">");
		values.add("=");
		values.add("<=");
		values.add(">=");

		// To create a tab in the first column
		Label empty = new Label(container, SWT.NONE);
		empty.setText("	");
		Label title = new Label(container, SWT.NONE);
		title.setText(label);

		operatorComboViewer = new ComboViewer(container, SWT.NONE);
		operatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		operatorComboViewer.setInput(values);
		operatorComboViewer.setSelection(new StructuredSelection(values.get(2)));

		valueSpinner = new Spinner(container, SWT.BORDER | SWT.SINGLE);
		valueSpinner.setValues(selection, min, max, digits, increment, pageincrement);

	}

	/**
	 * Method to create a widget for group properties, clafer level constraints
	 * 
	 * @param container
	 * @param claferMain
	 * @param claferProperty
	 */
	public PropertyWidget(Composite container, AstAbstractClafer claferMain, List<AstClafer> claferProperty) {
		setGroupConstraint(true);
		setAbstarctParentClafer(claferMain);
		setChildClafer((AstConcreteClafer) claferProperty.get(0));
		List<String> values = new ArrayList<String>();
		values.add("<");
		values.add(">");
		values.add("=");
		values.add("<=");
		values.add(">=");
		Label label5 = new Label(container, SWT.NONE);
		label5.setText("	");

		Label label1 = new Label(container, SWT.NONE);
		label1.setText(ClaferModelUtils.removeScopePrefix(claferMain.getName()));

		operatorComboViewer = new ComboViewer(container, SWT.NONE);
		operatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		operatorComboViewer.setInput(values);
		
		ArrayList<String> comboValues = new ArrayList<String>();
		for (AstClafer comboValue : claferProperty) {
			comboValues.add(ClaferModelUtils.removeScopePrefix(comboValue.getName()));
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
		valuesCombo.setInput(comboValues);
		
		valuesCombo.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				status = true;
				String selection = valuesCombo.getSelection().toString();
				for (AstClafer property : claferProperty) {
					if (selection.equals(ClaferModelUtils.removeScopePrefix(property.getName()))) {
						setChildClafer((AstConcreteClafer) property);
					}
				}
			}
		});
		
		valuesCombo.setSelection(new StructuredSelection(comboValues.get(0)));

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
	public AstConcreteClafer getParentClafer() {
		return parentClafer;
	}

	/**
	 * @param parentClafer
	 *            the parentClafer to set
	 */
	public void setParentClafer(AstConcreteClafer parentClafer) {
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
}
