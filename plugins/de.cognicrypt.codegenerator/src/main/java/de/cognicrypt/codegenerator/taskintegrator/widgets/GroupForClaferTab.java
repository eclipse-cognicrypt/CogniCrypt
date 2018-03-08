package de.cognicrypt.codegenerator.taskintegrator.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.ClaferDependency;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferFeature;
import de.cognicrypt.codegenerator.taskintegrator.models.ClaferModel;
import de.cognicrypt.codegenerator.taskintegrator.models.FeatureProperty;

public class GroupForClaferTab extends Group {

	private String featureSelected;
	private ArrayList<String> operandItems;
	ClaferModel claferModel;

	public GroupForClaferTab(Composite parent, int style, Answer answer, ClaferModel claferModel) {
		super(parent, style);
		setClaferModel(claferModel);
		//Non-editable Text Box shows the answer value
		Text txtBoxCurrentAnswer = new Text(this, SWT.BORDER);
		txtBoxCurrentAnswer.setBounds(5, 5, 120, 25);
		txtBoxCurrentAnswer.setEditable(false);
		txtBoxCurrentAnswer.setText(answer.getValue());

		//Combo for displaying all the features created in Clafer page
		Combo comboForAlgorithm = new Combo(this, SWT.READ_ONLY);
		comboForAlgorithm.setBounds(130, 5, 130, 25);
		comboForAlgorithm.add("none");
		// FIXME handle the claferFeatures == null case properly
		if (claferModel != null) {
			for (ClaferFeature claferFeature : claferModel) {
				comboForAlgorithm.add(claferFeature.getFeatureName());
			}
		}

		// Shows list of properties specific to selected feature or algo 

		Combo comboForOperand = new Combo(this, SWT.NONE);
		comboForOperand.setVisible(true);
		comboForOperand.setBounds(265, 5, 130, 25);

		//Combo containing all the operators
		Combo comboForOperator = new Combo(this, SWT.READ_ONLY);
		comboForOperator.setBounds(400, 5, 110, 25);
		comboForOperator.setItems("(" + Constants.FeatureConstraintRelationship.EQUAL.toString() + ")" + "Equal",
			"(" + Constants.FeatureConstraintRelationship.NOTEQUAL.toString() + ")" + "NOTEQUAL",
			"(" + Constants.FeatureConstraintRelationship.LESSTHAN.toString() + ")" + "LESSTHAN",
			"(" + Constants.FeatureConstraintRelationship.GREATERTHAN.toString() + ")" + "GREATERTHAN",
			"(" + Constants.FeatureConstraintRelationship.LESSTHANEQUALTO.toString() + ")" + "LESSTHANEQUALTO",
			"(" + Constants.FeatureConstraintRelationship.GREATERTHANEQUALTO.toString() + ")" + "GREATERTHANEQUALTO",
			"(" + Constants.FeatureConstraintRelationship.AND.toString() + ")" + "AND", "(" + Constants.FeatureConstraintRelationship.OR.toString() + ")" + "OR");

		//To retrieve value from the user
		Text txtBoxValue = new Text(this, SWT.BORDER);
		txtBoxValue.setBounds(515, 5, 120, 25);

		ClaferDependency claferDependency = new ClaferDependency();

		/*
		 * To display the details of clafer dependencies stored for each answer when the clafer tab is reselected
		 */
		if (answer.getClaferDependencies() != null) {
			for (ClaferDependency cf : answer.getClaferDependencies()) {
				if (cf.getAlgorithm() != null) {
					comboForAlgorithm.setText(cf.getAlgorithm());
					claferDependency.setAlgorithm(comboForAlgorithm.getText());
				}
				if (cf.getOperand() != null) {
					comboForOperand.setText(cf.getOperand());
					claferDependency.setOperand(comboForOperand.getText());
				}
				if (cf.getOperator() != null) {
					comboForOperator.setText(cf.getOperator());
					claferDependency.setOperator(comboForOperator.getText());
				}
				if (cf.getValue() != null) {
					txtBoxValue.setText(cf.getValue());
					claferDependency.setValue(txtBoxValue.getText());
				}
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

		ArrayList<ClaferDependency> listOfClaferDependencies = new ArrayList<ClaferDependency>();
		listOfClaferDependencies.add(claferDependency);
		answer.setClaferDependencies(listOfClaferDependencies);

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
		for (ClaferFeature claferFeature : claferModel) {
			if (claferFeature.getFeatureName().equalsIgnoreCase(featureSelected)) {
				for (FeatureProperty featureProperty : claferFeature.getFeatureProperties()) {
					operandItems.add(featureProperty.getPropertyName());
				}
				if (claferFeature.getFeatureInheritance() != null) {
					featureSelected = claferFeature.getFeatureInheritance();
					itemsToAdd(featureSelected);
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

}
