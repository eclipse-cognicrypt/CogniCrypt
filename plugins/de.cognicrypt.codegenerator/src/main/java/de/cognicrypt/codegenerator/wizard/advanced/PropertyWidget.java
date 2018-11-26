/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard.advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.core.Constants;

public class PropertyWidget {

	protected static boolean status = false;
	private Spinner valueSpinner;
	private Button enablePropertyCheckBox;
	private AstClafer parentClafer;
	private AstConcreteClafer childClafer;
	private final ComboViewer operatorComboViewer;
	private boolean isGroupConstraint = false;
	private AstAbstractClafer abstractParentClafer;

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
		GridLayout layout = new GridLayout(5, false);
		container.setLayout(layout);
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

		// To create indentation before the check boxes
		final Label emptySpace = new Label(container, SWT.NONE);
		emptySpace.setText("	");
		Composite temp = container.getParent();
		// TODO: count the total number of parents of the outermost group using some function
		// Now, the outermost group has 11 parents. So, a for loop from 0 to 10 is used. 
		//(inner groups have one additional parent)
		for (int i = 0; i < 10; i++) {
			if (temp != null) {
				temp = temp.getParent();
			} else {
				//if the checkbox belongs to outermost group, then it needs more indentation 
				//to align properly with the combo boxes of inner groups 
				emptySpace.setText("	  ");
			}
		}

		this.enablePropertyCheckBox = new Button(container, SWT.CHECK);
		this.enablePropertyCheckBox.setSelection(false);

		final Label propertyNameLabel = new Label(container, SWT.NONE);
		propertyNameLabel.setText(propertyName.replaceAll("([a-z0-9])([A-Z])", "$1 $2"));
		propertyNameLabel.setLayoutData(new GridData(120, 20));

		this.operatorComboViewer = new ComboViewer(container, SWT.FILL);
		Combo operatorCombo = this.operatorComboViewer.getCombo();
		operatorCombo.setEnabled(false);
		operatorCombo.setLayoutData(new GridData(45, 15));

		this.enablePropertyCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Button button = (Button) e.widget;
				if (button.getSelection()) {
					if (PropertyWidget.this.valueSpinner != null) {
						PropertyWidget.this.valueSpinner.setEnabled(true);
					}
					operatorCombo.setEnabled(true);
				} else {
					if (PropertyWidget.this.valueSpinner != null) {
						PropertyWidget.this.valueSpinner.setEnabled(false);
					}
					operatorCombo.setEnabled(false);
				}
			}
		});

		// Identify if the label of the check box contains a word 'Security'
		String labelPatternSecurity = "\\b" + Constants.Security + "\\b";
		Pattern patternSecurity = Pattern.compile(labelPatternSecurity);
		Matcher matchSecurity = patternSecurity.matcher(propertyName.replaceAll("([a-z0-9])([A-Z])", "$1 $2"));

		// Identify if the label of the check box contains a word 'Performance'
		String labelPatternPerformance = "\\b" + Constants.Performance + "\\b";
		Pattern patternPerformance = Pattern.compile(labelPatternPerformance);
		Matcher matchPerformance = patternPerformance.matcher(propertyName.replaceAll("([a-z0-9])([A-Z])", "$1 $2"));
		System.out.println(propertyName.replaceAll("([a-z0-9])([A-Z])", "$1 $2"));

		//If the label has Security or Performance, then spinner is not added and adds only a combo box with items high, medium and low
		if (matchSecurity.find() == true | matchPerformance.find() == true) {
			this.operatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
			this.operatorComboViewer.setInput(values1);
			this.operatorComboViewer.addSelectionChangedListener(arg0 -> PropertyWidget.this.operatorComboViewer.refresh());
			this.operatorComboViewer.setSelection(new StructuredSelection(values1.get(2)));
		} else {
			this.operatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
			this.operatorComboViewer.setInput(values);
			this.operatorComboViewer.addSelectionChangedListener(arg0 -> PropertyWidget.this.operatorComboViewer.refresh());
			this.operatorComboViewer.setSelection(new StructuredSelection(values.get(2)));

			this.valueSpinner = new Spinner(container, SWT.BORDER | SWT.SINGLE | SWT.FILL);
			this.valueSpinner.setEnabled(false);
			this.valueSpinner.setLayoutData(new GridData(40, 15));
			this.valueSpinner.setValues(selection, min, max, digits, increment, pageincrement);
		}
	}

	public AstAbstractClafer getAbstarctParentClafer() {
		return this.abstractParentClafer;
	}

	/**
	 * Getter for child clafer.
	 * 
	 * @return Child clafer
	 */
	public AstConcreteClafer getChildClafer() {
		return this.childClafer;
	}

	public String getOperator() {
		String comboSelection = ((IStructuredSelection) this.operatorComboViewer.getSelection()).getFirstElement().toString();
		//TODO: assign proper operators for High, Medium, Low
		if (comboSelection.equals("High") | comboSelection.equals("Medium") | comboSelection.equals("Low")) {
			return "=";
		} else {
			return comboSelection;
		}
	}

	/**
	 * Getter method for parent clafer.
	 * 
	 * @return Parent clafer
	 */
	public AstClafer getParentClafer() {
		return this.parentClafer;
	}

	public String getValue() {
		String comboSelection = ((IStructuredSelection) this.operatorComboViewer.getSelection()).getFirstElement().toString();
		////TODO: assign proper spinner values for High, Medium, Low
		if (comboSelection.equals("High")) {
			return "4";
		} else if (comboSelection.equals("Medium")) {
			return "3";
		} else if (comboSelection.equals("Low")) {
			return "2";
		} else {
			return String.valueOf(this.valueSpinner.getSelection());
		}
	}

	public boolean isEnabled() {
		return this.valueSpinner.isEnabled();
	}

	/**
	 * Getter method for isGroupConstraint
	 * 
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
	 * 
	 * @param childClafer
	 *        the childClafer to set
	 */
	public void setChildClafer(final AstConcreteClafer childClafer) {
		this.childClafer = childClafer;
	}

	/**
	 * Setter method for whether property is a group constraint.
	 * 
	 * @param isGroupConstraint
	 *        the isGroupConstraint to set
	 */
	public void setGroupConstraint(final boolean isGroupConstraint) {
		this.isGroupConstraint = isGroupConstraint;
	}

	/**
	 * Setter method for parent clafer.
	 * 
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
