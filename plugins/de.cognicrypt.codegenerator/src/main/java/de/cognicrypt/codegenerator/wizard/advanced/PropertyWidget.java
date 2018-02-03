/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.Constants;

public class PropertyWidget {

	// TODO THIS IS A WORKAROUND TO STOP INSTANCE GENERATION ON PAGE LOAD, NEEDS
	// TO BE FIXED
	protected static boolean status = false;
	private Spinner valueSpinner;
	private AstClafer parentClafer;
	private AstConcreteClafer childClafer;
	private final ComboViewer operatorComboViewer;
	private Combo operatorCombo;
	private Combo valuesComboBox;
	private boolean isGroupConstraint = false;
	private AstAbstractClafer abstarctParentClafer;

	private Button enablePropertyCheckBox;
	private Constants constant;
	/**
	 * Method to create a widget for group properties, clafer level constraints
	 *
	 * @param container
	 * @param claferMain
	 * @param claferProperties
	 */
	public PropertyWidget(final Composite container, final AstAbstractClafer claferMain, final List<AstClafer> claferProperties) {
		setGroupConstraint(true);
		setAbstarctParentClafer(claferMain);
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
		operatorCombo = operatorComboViewer.getCombo();
		operatorCombo.setEnabled(false);

		final ArrayList<String> propertyNames = new ArrayList<>();
		for (final AstClafer propertyClafer : claferProperties) {
			propertyNames.add(ClaferModelUtils.removeScopePrefix(propertyClafer.getName()));
		}
		this.operatorComboViewer.addSelectionChangedListener(arg0 -> status = true);

		this.operatorComboViewer.setSelection(new StructuredSelection(values.get(2)));

		final ComboViewer valuesCombo = new ComboViewer(container, SWT.NONE);
		valuesCombo.setContentProvider(ArrayContentProvider.getInstance());
		valuesCombo.setInput(propertyNames);
		valuesComboBox = valuesCombo.getCombo();

		valuesCombo.addSelectionChangedListener(arg0 -> {
			status = true;
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
//					operatorCombo.setEnabled(true);
//					valuesComboBox.setEnabled(true);
				} else {
					PropertyWidget.this.valueSpinner.setEnabled(false);
//					operatorCombo.setEnabled(false);
//					valuesComboBox.setEnabled(false);
				}
			}
		});

		final Label propertyNameLabel = new Label(container, SWT.NONE);
		propertyNameLabel.setText(propertyName);
		
		if(propertyName.equals(Constants.Security) || propertyName.equals(Constants.Performance) || propertyName.equals(Constants.CipherSecurity))
		{
		this.operatorComboViewer = new ComboViewer(container, SWT.NONE);
		this.operatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		this.operatorComboViewer.setInput(values1);

		this.operatorComboViewer.addSelectionChangedListener(arg0 -> PropertyWidget.this.operatorComboViewer.refresh());

		this.operatorComboViewer.setSelection(new StructuredSelection(values1.get(2)));
		}
		else
		{
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
		return this.abstarctParentClafer;
	}

	/**
	 * @return the childClafer
	 */
	public AstConcreteClafer getChildClafer() {
		return this.childClafer;
	}

	public String getOperator() {
		return ((IStructuredSelection) this.operatorComboViewer.getSelection()).getFirstElement().toString();
	}

	/**
	 * @return the parentClafer
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
	 * @return the isGroupConstraint
	 */
	public boolean isGroupConstraint() {
		return this.isGroupConstraint;
	}

	public void setAbstarctParentClafer(final AstAbstractClafer abstarctParentClafer) {
		this.abstarctParentClafer = abstarctParentClafer;
	}

	/**
	 * @param childClafer
	 *        the childClafer to set
	 */
	public void setChildClafer(final AstConcreteClafer childClafer) {
		this.childClafer = childClafer;
	}

	/**
	 * @param isGroupConstraint
	 *        the isGroupConstraint to set
	 */
	public void setGroupConstraint(final boolean isGroupConstraint) {
		this.isGroupConstraint = isGroupConstraint;
	}

	/**
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
