/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferProperty;
import de.cognicrypt.core.Constants;

public class CompositeForClaferTab extends Composite {

	private String featureSelected;
	private ArrayList<String> operandItems;
	private ClaferModel claferModel;
	private int lowestWidgetYAxisValue = 5;
	private ArrayList<ClaferDependency> claferDependencies;

	public CompositeForClaferTab(Composite parent, int style, Answer answer, ClaferModel claferModel, boolean showClaferWidgets) {
		super(parent, style);
		setClaferModel(claferModel);

		//Non-editable Text Box shows the answer value
		Text txtBoxCurrentAnswer = new Text(this, SWT.BORDER);
		txtBoxCurrentAnswer.setBounds(5, 5, 120, 25);
		txtBoxCurrentAnswer.setEditable(false);
		txtBoxCurrentAnswer.setText(answer.getValue());

		//Add Clafer Dependencies button
		Button addMore = new Button(this, SWT.None);
		addMore.setBounds(130, 5, 100, 25);
		addMore.setText("Add");

		addMore.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ClaferDependency cd = new ClaferDependency();
				if (!showClaferWidgets && answer.getClaferDependencies() == null) {
					ArrayList<ClaferDependency> claferDependencies = new ArrayList<>();
					answer.setClaferDependencies(claferDependencies);
				}
				answer.getClaferDependencies().add(cd);

				((CompositeToHoldSmallerUIElements) addMore.getParent().getParent().getParent()).updateClaferTab(claferModel, answer);
			}
		});

		/**
		 * executes when showClaferWidgets value is true or the list of clafer dependencies is not null
		 */
		if (showClaferWidgets || answer.getClaferDependencies() != null) {

			for (ClaferDependency claferDependency : answer.getClaferDependencies()) {
				Composite claferWidgets = new Composite(this, SWT.NONE);
				claferWidgets.setBounds(235, getLowestWidgetYAxisValue(), 655, 34);

				//Combo for displaying all the features created in Clafer page
				Combo comboForAlgorithm = new Combo(claferWidgets, SWT.READ_ONLY);
				comboForAlgorithm.setBounds(0, 0, 135, 25);

				if (claferModel != null) {
					for (ClaferFeature claferFeature : claferModel) {
						//add the claferFeature only if it has properties
						if (claferFeature.hasProperties()) {
							comboForAlgorithm.add(claferFeature.getFeatureName());
						}

						if (claferFeature.getFeatureInheritance().equals("Task")) {
							for (ClaferProperty subClafer : claferFeature.getFeatureProperties()) {
								comboForAlgorithm.add(subClafer.getPropertyName());
							}
						}
					}
				}

				// Shows list of properties specific to selected feature 
				Combo comboForOperand = new Combo(claferWidgets, SWT.NONE);
				comboForOperand.setVisible(true);
				comboForOperand.setBounds(140, 0, 130, 25);

				//Combo containing all the operators
				Combo comboForOperator = new Combo(claferWidgets, SWT.READ_ONLY);
				comboForOperator.setBounds(275, 0, 110, 25);
				comboForOperator.setItems(Constants.FeatureConstraintRelationship.EQUAL.toString(), Constants.FeatureConstraintRelationship.NOTEQUAL.toString(),
					Constants.FeatureConstraintRelationship.LESSTHAN.toString(), Constants.FeatureConstraintRelationship.GREATERTHAN.toString(),
					Constants.FeatureConstraintRelationship.LESSTHANEQUALTO.toString(), Constants.FeatureConstraintRelationship.GREATERTHANEQUALTO.toString());

				//To retrieve value from the user
				Text txtBoxValue = new Text(claferWidgets, SWT.BORDER);
				txtBoxValue.setBounds(390, 0, 120, 25);

				//Upon click the button removes the current ClaferDependency
				Button btnRemove = new Button(claferWidgets, SWT.None);
				btnRemove.setBounds(515, 0, 100, 25);
				btnRemove.setText("Remove");
				btnRemove.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						//	answer.getClaferDependencies().remove(claferDependency);
						((CompositeToHoldSmallerUIElements) btnRemove.getParent().getParent().getParent().getParent()).deleteClaferDependency(answer, claferDependency,
							claferModel);
					}
				});

				/*
				 * To display the details of clafer dependencies stored for each answer
				 */
				if (claferDependency != null) {
					if (claferDependency.getAlgorithm() != null) {
						comboForAlgorithm.setText(claferDependency.getAlgorithm());
						claferDependency.setAlgorithm(comboForAlgorithm.getText());
					}
					if (claferDependency.getOperand() != null) {
						comboForOperand.setText(claferDependency.getOperand());
						claferDependency.setOperand(comboForOperand.getText());
					}
					if (claferDependency.getOperator() != null) {
						comboForOperator.setText(claferDependency.getOperator());
						claferDependency.setOperator(comboForOperator.getText());
					}
					if (claferDependency.getValue() != null) {
						txtBoxValue.setText(claferDependency.getValue());
						claferDependency.setValue(txtBoxValue.getText());
					}

				}

				//adding the items to comboForOperand box depending on the comboForAlgorithm box value 
				if (comboForAlgorithm.getText() != null) {
					operandItems = new ArrayList<String>();
					ArrayList<String> operandToAdd = itemsToAdd(comboForAlgorithm.getText());
					for (int i = 0; i < operandToAdd.size(); i++) {
						comboForOperand.add(operandToAdd.get(i));
					}

				}

				//set the clafer dependency operand field depending upon the selected feature
				comboForAlgorithm.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						featureSelected = comboForAlgorithm.getText();
						comboForOperand.removeAll();
						operandItems = new ArrayList<String>();
						ArrayList<String> operandToAdd = itemsToAdd(featureSelected);
						for (int i = 0; i < operandToAdd.size(); i++) {
							comboForOperand.add(operandToAdd.get(i));
						}
						//to remove the previous operand selected as value of comboForlgorithm is changed
						if (answer.getClaferDependencies() != null) {
							comboForOperand.setText("");
							claferDependency.setOperand(comboForOperand.getText());

						}
						claferDependency.setAlgorithm(featureSelected);
					}
				});

				//set the clafer dependency operand field depending upon the operand/property selected
				comboForOperand.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						claferDependency.setOperand(comboForOperand.getText());
					}
				});
				//set the clafer dependency operator field 
				comboForOperator.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						claferDependency.setOperator(comboForOperator.getText());
					}
				});
				//set the clafer dependency value field
				txtBoxValue.addFocusListener(new FocusAdapter() {

					@Override
					public void focusLost(FocusEvent e) {
						claferDependency.setValue(txtBoxValue.getText());
					}
				});

				setLowestWidgetYAxisValue(getLowestWidgetYAxisValue() + 34);
			}

		}

	}

	/**
	 * @param featureSelected
	 *        feature selected in the comboForFeatures
	 * @return the list of operand to be added in the comboForOperand box depending on the featureSelected
	 */

	private ArrayList<String> itemsToAdd(String featureSelected) {
		// FIXME handle the claferFeatures == null case properly
		if (claferModel == null) {
			return new ArrayList<>();
		}

		ClaferFeature selectedCfr = claferModel.getFeature(featureSelected);

		if (selectedCfr != null) {
			for (ClaferProperty featureProperty : selectedCfr.getFeatureProperties()) {
				operandItems.add(featureProperty.getPropertyName());
			}

			if (selectedCfr.getFeatureInheritance() != null) {
				featureSelected = selectedCfr.getFeatureInheritance();
				itemsToAdd(featureSelected);
			}

		} else {
			for (ClaferFeature cfrFeature : claferModel) {
				if (cfrFeature.getFeatureInheritance().equals("Task")) {
					for (ClaferProperty claferProperty : cfrFeature.getFeatureProperties()) {
						if (claferProperty.getPropertyName().equals(featureSelected)) {
							ClaferFeature parentClafer = claferModel.getFeature(claferProperty.getPropertyType());
							if (parentClafer != null && parentClafer.getFeatureType().equals(Constants.FeatureType.ABSTRACT)) {
								for (ClaferProperty inheritedFeature : parentClafer.getFeatureProperties()) {
									operandItems.add(inheritedFeature.getPropertyName());
								}
							}
						}
					}
				}
			}
		}

		return operandItems;
	}

	/**
	 * @param claferFeatures
	 *        list of all clafer features created in the clafer page
	 */
	private void setClaferModel(ClaferModel claferModel) {
		this.claferModel = claferModel;
	}

	@Override
	protected void checkSubclass() {
		// To disable the check that prevents subclassing of SWT components
	}

	public int getLowestWidgetYAxisValue() {
		return lowestWidgetYAxisValue;
	}

	public void setLowestWidgetYAxisValue(int lowestWidgetYAxisValue) {
		this.lowestWidgetYAxisValue = lowestWidgetYAxisValue + 5;
	}

	public ArrayList<ClaferDependency> getClaferDependencies() {
		return claferDependencies;
	}

	public void setClaferDependencies(ArrayList<ClaferDependency> claferDependencies) {
		this.claferDependencies = claferDependencies;
	}

}
