/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer;

import java.util.Arrays;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.properties.PreferenceListener;
import de.cognicrypt.utils.Utils;

public class StaticAnalyzerPreferences extends PreferenceListener {

	private IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();

	private Combo ruleSelection;
	private Button automatedAnalysisCheckBox;
	private Button secureObjectsCheckBox;
	private Button analyseDependenciesCheckBox;

	private Combo CGSelection;
	private Combo forbidden;
	private Combo reqPred;
	private Combo constraint;
	private Combo neverType;
	private Combo incompleteOp;
	private Combo typestate;

	@Override
	public void compileBasicPreferences(Composite parent) {
		createBasicContents(parent);
		performBasicDefaults();
		initializeBasicValues();
	}

	@Override
	public void compileAdvancedPreferences(Composite parent) {
		createAdvancedContents(parent);
		performAdvancedDefaults();
		initializeAdvancedValues();
	}

	private void initializeBasicValues() {
		automatedAnalysisCheckBox.setSelection(preferences.getBoolean(Constants.AUTOMATED_ANALYSIS));
		secureObjectsCheckBox.setSelection(preferences.getBoolean(Constants.SHOW_SECURE_OBJECTS));
		analyseDependenciesCheckBox.setSelection(preferences.getBoolean(Constants.ANALYSE_DEPENDENCIES));
		ruleSelection.select(preferences.getInt(Constants.RULE_SELECTION));
	}

	private void performBasicDefaults() {
		preferences.setDefault(Constants.RULE_SELECTION, 0);
		preferences.setDefault(Constants.AUTOMATED_ANALYSIS, true);
		preferences.setDefault(Constants.SHOW_SECURE_OBJECTS, false);
		preferences.setDefault(Constants.ANALYSE_DEPENDENCIES, true);
		preferences.setDefault(Constants.CALL_GRAPH_SELECTION, 0);
	}

	private void createBasicContents(Composite parent) {
		final Group staticAnalysisGroup = Utils.addHeaderGroup(parent, "Analysis");

		final Composite source = new Composite(staticAnalysisGroup, SWT.FILL);
		source.setLayout(new GridLayout(2, true));
		final Label ruleSource = new Label(source, SWT.NONE);
		ruleSource.setText("Source of CrySL rules: ");

		// other options: "Default JSSE rules","Default Tink rules"
		String[] choices = {"Default JCA Rules"};
		ruleSelection = new Combo(source, SWT.DROP_DOWN | SWT.READ_ONLY);
		ruleSelection.setItems(choices);

		automatedAnalysisCheckBox = new Button(staticAnalysisGroup, SWT.CHECK);
		automatedAnalysisCheckBox.setText("Enable automated analysis when saving");

		secureObjectsCheckBox = new Button(staticAnalysisGroup, SWT.CHECK);
		secureObjectsCheckBox.setText("Show secure objects");

		analyseDependenciesCheckBox = new Button(staticAnalysisGroup, SWT.CHECK);
		analyseDependenciesCheckBox.setText("Include dependencies to projects analysis");
		analyseDependenciesCheckBox.setSelection(preferences.getBoolean(Constants.ANALYSE_DEPENDENCIES));
	}

	private void initializeAdvancedValues() {
		int currentCG = preferences.getInt(Constants.CALL_GRAPH_SELECTION);
		CGSelection.select(currentCG > -1 ? currentCG : preferences.getDefaultInt(Constants.CALL_GRAPH_SELECTION));

		int errorType = preferences.getInt(Constants.FORBIDDEN_METHOD_MARKER_TYPE);
		forbidden.select(errorType > -1 ? errorType : preferences.getDefaultInt(Constants.FORBIDDEN_METHOD_MARKER_TYPE));

		errorType = preferences.getInt(Constants.CONSTRAINT_ERROR_MARKER_TYPE);
		constraint.select(errorType > -1 ? errorType : preferences.getDefaultInt(Constants.CONSTRAINT_ERROR_MARKER_TYPE));

		errorType = preferences.getInt(Constants.INCOMPLETE_OPERATION_MARKER_TYPE);
		incompleteOp.select(errorType > -1 ? errorType : preferences.getDefaultInt(Constants.INCOMPLETE_OPERATION_MARKER_TYPE));

		errorType = preferences.getInt(Constants.TYPESTATE_ERROR_MARKER_TYPE);
		typestate.select(errorType > -1 ? errorType : preferences.getDefaultInt(Constants.TYPESTATE_ERROR_MARKER_TYPE));

		errorType = preferences.getInt(Constants.NEVER_TYPEOF_MARKER_TYPE);
		neverType.select(errorType > -1 ? errorType : preferences.getDefaultInt(Constants.NEVER_TYPEOF_MARKER_TYPE));

		errorType = preferences.getInt(Constants.REQUIRED_PREDICATE_MARKER_TYPE);
		reqPred.select(errorType > -1 ? errorType : preferences.getDefaultInt(Constants.REQUIRED_PREDICATE_MARKER_TYPE));
	}

	private void performAdvancedDefaults() {
		preferences.setDefault(Constants.FORBIDDEN_METHOD_MARKER_TYPE, 0);
		preferences.setDefault(Constants.TYPESTATE_ERROR_MARKER_TYPE, 0);
		preferences.setDefault(Constants.INCOMPLETE_OPERATION_MARKER_TYPE, 0);
		preferences.setDefault(Constants.NEVER_TYPEOF_MARKER_TYPE, 0);
		preferences.setDefault(Constants.REQUIRED_PREDICATE_MARKER_TYPE, 0);
		preferences.setDefault(Constants.CONSTRAINT_ERROR_MARKER_TYPE, 0);

		preferences.setDefault(Constants.PREDICATE_CONTRADICTION_MARKER_TYPE, 0);
		preferences.setDefault(Constants.IMPRECISE_VALUE_EXTRACTION_MARKER_TYPE, 1);
	}

	private void createAdvancedContents(Composite parent) {
		final Group staticAnalysisGroup = Utils.addHeaderGroup(parent, "Analysis");

		final Composite callGraphContainer = new Composite(staticAnalysisGroup, SWT.None);
		callGraphContainer.setLayout(new GridLayout(2, true));
		final Label label1 = new Label(callGraphContainer, SWT.SHADOW_IN);
		label1.setText("Call-graph construction algorithm");

		CGSelection = new Combo(callGraphContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		CGSelection.setItems(Arrays.stream(Constants.CG.values()).map(Enum::name).toArray(String[]::new));

		final Group errorTypeGroup = new Group(staticAnalysisGroup, SWT.SHADOW_IN);
		errorTypeGroup.setText("Error-Warning Types");
		errorTypeGroup.setLayout(new GridLayout(1, true));

		// ConstraintError
		final Composite constraintContainer = new Composite(errorTypeGroup, SWT.None);
		constraintContainer.setLayout(new GridLayout(2, true));
		final Label constraintLabel = new Label(constraintContainer, SWT.None);
		constraintLabel.setText("Incorrect Parameter Problem:");
		constraint = new Combo(constraintContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		constraint.setItems(Arrays.stream(Constants.Severities.values()).map(Enum::name).toArray(String[]::new));

		// TypestateError
		final Composite typestateContainer = new Composite(errorTypeGroup, SWT.None);
		typestateContainer.setLayout(new GridLayout(2, true));
		final Label typestateLabel = new Label(typestateContainer, SWT.None);
		typestateLabel.setText("Incorrect Method Call Problem:");
		typestate = new Combo(typestateContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		typestate.setItems(Arrays.stream(Constants.Severities.values()).map(Enum::name).toArray(String[]::new));

		// IncompleteOperationError
		final Composite incompleteContainer = new Composite(errorTypeGroup, SWT.None);
		incompleteContainer.setLayout(new GridLayout(2, true));
		final Label incompleteLabel = new Label(incompleteContainer, SWT.None);
		incompleteLabel.setText("Missing Method Call Problem:");
		incompleteOp = new Combo(incompleteContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		incompleteOp.setItems(Arrays.stream(Constants.Severities.values()).map(Enum::name).toArray(String[]::new));

		final Composite forbiddenContainer = new Composite(errorTypeGroup, SWT.None);
		forbiddenContainer.setLayout(new GridLayout(2, true));
		final Label forbiddenLabel = new Label(forbiddenContainer, SWT.None);
		forbiddenLabel.setText("Forbidden Method Problem:");
		forbidden = new Combo(forbiddenContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		forbidden.setItems(Arrays.stream(Constants.Severities.values()).map(Enum::name).toArray(String[]::new));

		// RequiredPredicateError
		final Composite reqPredContainer = new Composite(errorTypeGroup, SWT.None);
		reqPredContainer.setLayout(new GridLayout(2, true));
		final Label reqPredLabel = new Label(reqPredContainer, SWT.None);
		reqPredLabel.setText("Insecure Class Composition Problem:");
		reqPred = new Combo(reqPredContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		reqPred.setItems(Arrays.stream(Constants.Severities.values()).map(Enum::name).toArray(String[]::new));

		// NeverTypeOfError
		final Composite neverTypeContainer = new Composite(errorTypeGroup, SWT.None);
		neverTypeContainer.setLayout(new GridLayout(2, true));
		final Label neverTypeLabel = new Label(neverTypeContainer, SWT.None);
		neverTypeLabel.setText("Wrong Type Problem:");
		neverType = new Combo(neverTypeContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		neverType.setItems(Arrays.stream(Constants.Severities.values()).map(Enum::name).toArray(String[]::new));
	}

	@Override
	public void setDefaultValues() {
		automatedAnalysisCheckBox.setSelection(preferences.getDefaultBoolean(Constants.AUTOMATED_ANALYSIS));
		secureObjectsCheckBox.setSelection(preferences.getDefaultBoolean(Constants.SHOW_SECURE_OBJECTS));
		analyseDependenciesCheckBox.setSelection(preferences.getDefaultBoolean(Constants.ANALYSE_DEPENDENCIES));
		ruleSelection.select(preferences.getDefaultInt(Constants.RULE_SELECTION));

		CGSelection.select(preferences.getDefaultInt(Constants.CALL_GRAPH_SELECTION));

		forbidden.select(preferences.getDefaultInt(Constants.FORBIDDEN_METHOD_MARKER_TYPE));
		constraint.select(preferences.getDefaultInt(Constants.CONSTRAINT_ERROR_MARKER_TYPE));
		incompleteOp.select(preferences.getDefaultInt(Constants.INCOMPLETE_OPERATION_MARKER_TYPE));
		typestate.select(preferences.getDefaultInt(Constants.TYPESTATE_ERROR_MARKER_TYPE));
		neverType.select(preferences.getDefaultInt(Constants.NEVER_TYPEOF_MARKER_TYPE));
		reqPred.select(preferences.getDefaultInt(Constants.REQUIRED_PREDICATE_MARKER_TYPE));
	}

	@Override
	protected void storeValues() {
		preferences.setValue(Constants.AUTOMATED_ANALYSIS, automatedAnalysisCheckBox.getSelection());
		preferences.setValue(Constants.SHOW_SECURE_OBJECTS, secureObjectsCheckBox.getSelection());
		preferences.setValue(Constants.ANALYSE_DEPENDENCIES, analyseDependenciesCheckBox.getSelection());
		preferences.setValue(Constants.RULE_SELECTION, ruleSelection.getSelectionIndex());
		preferences.setValue(Constants.CALL_GRAPH_SELECTION, CGSelection.getSelectionIndex());
		preferences.setValue(Constants.FORBIDDEN_METHOD_MARKER_TYPE, forbidden.getSelectionIndex());
		preferences.setValue(Constants.CONSTRAINT_ERROR_MARKER_TYPE, constraint.getSelectionIndex());
		preferences.setValue(Constants.INCOMPLETE_OPERATION_MARKER_TYPE, incompleteOp.getSelectionIndex());
		preferences.setValue(Constants.NEVER_TYPEOF_MARKER_TYPE, neverType.getSelectionIndex());
		preferences.setValue(Constants.REQUIRED_PREDICATE_MARKER_TYPE, reqPred.getSelectionIndex());
		preferences.setValue(Constants.TYPESTATE_ERROR_MARKER_TYPE, typestate.getSelectionIndex());
	}

}
