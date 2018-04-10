package de.cognicrypt.codegenerator.wizard.advanced;

import java.util.ArrayList;
import java.util.List;

import org.clafer.ast.AstAbstractClafer;
import org.clafer.ast.AstClafer;
import org.clafer.ast.AstConcreteClafer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.core.Constants;

public class PropertyWidget {

	protected static boolean status = false;
	private Spinner valueSpinner;
	private AstClafer parentClafer;
	private AstConcreteClafer childClafer;
	private final ComboViewer operatorComboViewer;
	private boolean isGroupConstraint = false;
	private AstAbstractClafer abstractParentClafer;

	private Button enablePropertyCheckBox;

	/**
	 * Constructor to create a widget for group properties, clafer level constraints.
	 *
	 * @param container
	 * @param claferMain
	 * @param claferProperties
	 */
	public PropertyWidget(final Composite container, final AstAbstractClafer claferMain, final List<AstClafer> claferProperties) {
		setGroupConstraint(true);
		setAbstractParentClafer(claferMain);
		setChildClafer((AstConcreteClafer) claferProperties.get(0));
		final List<String> values = new ArrayList<>();
		values.add("<");
		values.add(">");
		values.add("=");
		values.add("<=");
		values.add(">=");
		final Label label5 = new Label(container, SWT.NONE);
		label5.setText("	");

		final Label groupName = new Label(container, SWT.NONE);
		groupName.setText(ClaferModelUtils.removeScopePrefix(claferMain.getName()));

		this.operatorComboViewer = new ComboViewer(container, SWT.NONE);
		this.operatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		this.operatorComboViewer.setInput(values);

		final ArrayList<String> propertyNames = new ArrayList<>();
		for (final AstClafer propertyClafer : claferProperties) {
			propertyNames.add(ClaferModelUtils.removeScopePrefix(propertyClafer.getName()));
		}
		this.operatorComboViewer.addSelectionChangedListener(arg0 -> PropertyWidget.status = true);

		this.operatorComboViewer.setSelection(new StructuredSelection(values.get(2)));

		final ComboViewer valuesCombo = new ComboViewer(container, SWT.NONE);
		valuesCombo.setContentProvider(ArrayContentProvider.getInstance());
		valuesCombo.setInput(propertyNames);

		valuesCombo.addSelectionChangedListener(arg0 -> {
			PropertyWidget.status = true;
			final String selection = valuesCombo.getSelection().toString();
			for (final AstClafer property : claferProperties) {
				if (selection.equals(ClaferModelUtils.removeScopePrefix(property.getName()))) {
					setChildClafer((AstConcreteClafer) property);
				}
			}
		});

		valuesCombo.setSelection(new StructuredSelection(propertyNames.get(0)));

	}

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
	public PropertyWidget(final Composite container, final AstClafer parentClafer, final AstConcreteClafer childClafer, final String propertyName, final int selection, final int min, final int max, final int digits, final int increment, final int pageincrement) {
		setChildClafer(childClafer);
		setParentClafer(parentClafer);
		final List<String> values = new ArrayList<>();
		values.add("<");
		values.add(">");
		values.add("=");
		values.add("<=");
		values.add(">=");

		//Security dropdown
		final List<String> values1 = new ArrayList<>();
		values1.add("Low");
		values1.add("Medium");
		values1.add("High");

		// To create a tab in the first column
		final Label emptySpace = new Label(container, SWT.NONE);
		emptySpace.setText("	");

		this.enablePropertyCheckBox = new Button(container, SWT.CHECK);
		this.enablePropertyCheckBox.setSelection(false);

		this.enablePropertyCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Button button = (Button) e.widget;
				if (button.getSelection()) {
					PropertyWidget.this.valueSpinner.setEnabled(true);
				} else {
					PropertyWidget.this.valueSpinner.setEnabled(false);
				}
			}
		});

		final Label propertyNameLabel = new Label(container, SWT.NONE);
		propertyNameLabel.setText(propertyName);

		if (propertyName.equals(Constants.Security)) {
			this.operatorComboViewer = new ComboViewer(container, SWT.NONE);
			this.operatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
			this.operatorComboViewer.setInput(values1);

			this.operatorComboViewer.addSelectionChangedListener(arg0 -> PropertyWidget.this.operatorComboViewer.refresh());

			this.operatorComboViewer.setSelection(new StructuredSelection(values1.get(2)));
		} else {
			this.operatorComboViewer = new ComboViewer(container, SWT.NONE);
			this.operatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
			this.operatorComboViewer.setInput(values);

			this.operatorComboViewer.addSelectionChangedListener(arg0 -> PropertyWidget.this.operatorComboViewer.refresh());

			this.operatorComboViewer.setSelection(new StructuredSelection(values.get(2)));

			this.valueSpinner = new Spinner(container, SWT.BORDER | SWT.SINGLE);
			this.valueSpinner.setValues(selection, min, max, digits, increment, pageincrement);
			this.valueSpinner.setEnabled(false);

		}
	}

	public AstAbstractClafer getAbstarctParentClafer() {
		return this.abstractParentClafer;
	}

	/**
	 * Getter for child clafer.
	 * @return Child clafer
	 */
	public AstConcreteClafer getChildClafer() {
		return this.childClafer;
	}

	public String getOperator() {
		return ((IStructuredSelection) this.operatorComboViewer.getSelection()).getFirstElement().toString();
	}

	/**
	 * Getter method for parent clafer.
	 * @return Parent clafer
	 */
	public AstClafer getParentClafer() {
		return this.parentClafer;
	}

	public String getValue() {
		return String.valueOf(this.valueSpinner.getSelection());
	}

	public boolean isEnabled() {
		return this.valueSpinner.isEnabled();
	}

	/**
	 * Getter method for isGroupConstraint
	 * @return <Code>true</code>/<code>false</code> if property is group constraint
	 */
	public boolean isGroupConstraint() {
		return this.isGroupConstraint;
	}

	public void setAbstractParentClafer(final AstAbstractClafer abstractParentClafer) {
		this.abstractParentClafer = abstractParentClafer;
	}

	/**
	 * Setter method for child clafer.
	 * @param childClafer
	 *        the childClafer to set
	 */
	public void setChildClafer(final AstConcreteClafer childClafer) {
		this.childClafer = childClafer;
	}

	/**
	 * Setter method for whether property is a group constraint.
	 * @param isGroupConstraint
	 *        the isGroupConstraint to set
	 */
	public void setGroupConstraint(final boolean isGroupConstraint) {
		this.isGroupConstraint = isGroupConstraint;
	}

	/**
	 * Setter method for parent clafer.
	 * @param parentClafer
	 *        the parentClafer to set
	 */
	public void setParentClafer(final AstClafer parentClafer) {
		this.parentClafer = parentClafer;
	}

	@Override
	public String toString() {
		return "[parent:" + this.parentClafer.getName() + ", child: " + this.childClafer
			.getName() + ", operator: " + getOperator() + ", value:" + getValue() + ", isGroupConstraint: " + this.isGroupConstraint + "]";
	}
}
