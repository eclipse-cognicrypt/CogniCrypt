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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import crossing.e1.configurator.utilities.Labels;
import crossing.e1.featuremodel.clafer.ClaferModelUtils;

public class ComplexWidget {
	private Spinner taskComb;
	private AstConcreteClafer parentClafer;
	private AstConcreteClafer childClafer;
	private ComboViewer option;
	private boolean isGroupConstraint=false;

	public ComplexWidget(Composite container, AstConcreteClafer parentClafer, AstConcreteClafer childClafer,
			String label, int selection, int min, int max, int digits, int increment, int pageincrement) {
		this.setChildClafer(childClafer);
		this.setParentClafer(parentClafer);
		List<String> values = new ArrayList<String>();
		values.add(Labels.LESS_THAN);
		values.add(Labels.GREATER_THAN);
		values.add(Labels.EQUALS);
		values.add(Labels.LESS_THAN_EQUAL);
		values.add(Labels.GREATER_THAN_EQUAL);

		Label empty = new Label(container, SWT.NONE);
		empty.setText("	");
		Label title = new Label(container, SWT.NONE);
		title.setText(label);

		option = new ComboViewer(container, SWT.NONE);
		option.setContentProvider(ArrayContentProvider.getInstance());
		option.setInput(values);
		option.setSelection(new StructuredSelection(values.get(2)));

		taskComb = new Spinner(container, SWT.BORDER | SWT.SINGLE);
		taskComb.setValues(selection, min, max, digits, increment, pageincrement);
		

	}

	public ComplexWidget(Composite container, AstAbstractClafer claferMain, List<AstClafer> claferProperty) {
		setGroupConstraint(true);
		List<String> values = new ArrayList<String>();
		values.add(Labels.LESS_THAN);
		values.add(Labels.GREATER_THAN);
		values.add(Labels.EQUALS);
		values.add(Labels.LESS_THAN_EQUAL);
		values.add(Labels.GREATER_THAN_EQUAL);
		Label label5 = new Label(container, SWT.NONE);
		label5.setText("	");

		Label label1 = new Label(container, SWT.NONE);
		label1.setText(ClaferModelUtils.removeScopePrefix(claferMain.getName()));

		option = new ComboViewer(container, SWT.NONE);
		option.setContentProvider(ArrayContentProvider.getInstance());
		option.setInput(values);
		option.setSelection(new StructuredSelection(values.get(2)));
		ArrayList<String> comboValues = new ArrayList<String>();
		for (AstClafer comboValue : claferProperty) {
			comboValues.add(ClaferModelUtils.removeScopePrefix(comboValue.getName()));
		}

		ComboViewer valuesCombo = new ComboViewer(container, SWT.NONE);
		valuesCombo.setContentProvider(ArrayContentProvider.getInstance());
		valuesCombo.setInput(comboValues);
		valuesCombo.setSelection(new StructuredSelection(comboValues.get(0)));
		valuesCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				//TODO
				valuesCombo.getStructuredSelection();
				
			}
		});

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

	public String getOption() {
		return option.getSelection().toString();
	}
	public Integer getValue() {
		return taskComb.getSelection();
	}
	/**
	 * @return the isGroupConstraint
	 */
	public boolean isGroupConstraint() {
		return isGroupConstraint;
	}

	/**
	 * @param isGroupConstraint the isGroupConstraint to set
	 */
	public void setGroupConstraint(boolean isGroupConstraint) {
		this.isGroupConstraint = isGroupConstraint;
	}
}
