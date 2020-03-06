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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.properties.PreferenceListener;
import de.cognicrypt.staticanalyzer.utilities.AddNewRulesetDialog;
import de.cognicrypt.staticanalyzer.utilities.ArtifactUtils;
import de.cognicrypt.staticanalyzer.utilities.Ruleset;
import de.cognicrypt.utils.CrySLUtils;
import de.cognicrypt.utils.UIUtils;

public class StaticAnalyzerPreferences extends PreferenceListener {

	private IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
	private Preferences rulePreferences = InstanceScope.INSTANCE.getNode(de.cognicrypt.core.Activator.PLUGIN_ID);

	private Button automatedAnalysisCheckBox;
	private Button providerDetectionCheckBox;
	private Button secureObjectsCheckBox;
	private Button analyseDependenciesCheckBox;
	private Button addNewRulesetButton, selectCustomRulesCheckBox;
 	private CheckboxTableViewer table;

	private Combo CGSelection;
	private Combo forbidden;
	private Combo reqPred;
	private Combo constraint;
	private Combo neverType;
	private Combo incompleteOp;
	private Combo typestate;
		
	private List<Ruleset> listOfRulesets = new ArrayList<Ruleset>();

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
		providerDetectionCheckBox.setSelection(preferences.getBoolean(Constants.PROVIDER_DETECTION_ANALYSIS));
		secureObjectsCheckBox.setSelection(preferences.getBoolean(Constants.SHOW_SECURE_OBJECTS));
		analyseDependenciesCheckBox.setSelection(preferences.getBoolean(Constants.ANALYSE_DEPENDENCIES));
		selectCustomRulesCheckBox.setSelection(preferences.getBoolean(Constants.SELECT_CUSTOM_RULES));
	}

	private void performBasicDefaults() {
		preferences.setDefault(Constants.RULE_SELECTION, 0);
		preferences.setDefault(Constants.AUTOMATED_ANALYSIS, true);
		preferences.setDefault(Constants.PROVIDER_DETECTION_ANALYSIS, false);
		preferences.setDefault(Constants.SHOW_SECURE_OBJECTS, false);
		preferences.setDefault(Constants.ANALYSE_DEPENDENCIES, true);
		preferences.setDefault(Constants.CALL_GRAPH_SELECTION, 0);
	}
	
	/***
 	 * This method creates a row for each of the rule set with a drop-down list of
 	 * versions passed.
 	 * 
 	 * @param ruleset rule set to be added
 	 */
 	private void createRulesTableRow(Ruleset ruleset) {

 		TableEditor editor = new TableEditor(table.getTable());
 		TableItem rulesRow = new TableItem(table.getTable(), SWT.NONE);
 		rulesRow.setText(0, ruleset.getFolderName());
 		editor.grabHorizontal = true;
 		editor.setEditor(ruleset.getVersions(), rulesRow, 1);
 		rulesRow.setText(2, ruleset.getUrl());
 		rulesRow.setChecked(ruleset.isChecked());
 		ruleset.setRulesRow(rulesRow);
 	}

 	/**
 	 * This method fetches the list of rule sets which are stored in preference file
 	 * 
 	 * @return list of rule sets
 	 */
 	private List<Ruleset> getRulesetsFromPrefs() {
 		List<Ruleset> ruleSets = new ArrayList<Ruleset>();

 		try {
 			String[] listOfNodes = rulePreferences.childrenNames();

 			for (String currentNode : listOfNodes) {
 				Ruleset loadedRuleset = new Ruleset(currentNode);
 				Preferences subPref = rulePreferences.node(currentNode);
 				String[] keys = subPref.keys();
 				for (String key : keys) {
 					switch (key) {
 					case "FolderName":
 						loadedRuleset.setFolderName(subPref.get(key, ""));
 						break;
 					case "CheckboxState":
 						loadedRuleset.setChecked(subPref.getBoolean(key, false));
 						break;
 					case "SelectedVersion":
 						loadedRuleset.setSelectedVersion(subPref.get(key, ""));
 						break;
 					case "Url":
 						loadedRuleset.setUrl(subPref.get(key, ""));
 						break;
 					default:
 						break;
 					}
 				}
 				ruleSets.add(loadedRuleset);
 			}
 		} catch (BackingStoreException e) {
 			Activator.getDefault().logError(e);
 		}
 		return ruleSets;
 	}

 	/***
 	 * This method creates a table with check boxes for the CrySL rule sets.
 	 */
 	private void createRulesTable() {
 		TableViewerColumn rulesColumn = new TableViewerColumn(table, SWT.FILL);
 		TableViewerColumn versionsColumn = new TableViewerColumn(table, SWT.FILL);
 		TableViewerColumn rulesURL = new TableViewerColumn(table, SWT.FILL);
 		rulesColumn.getColumn().setText(Constants.TABLE_HEADER_RULES);
 		versionsColumn.getColumn().setText(Constants.TABLE_HEADER_VERSION);
 		rulesURL.getColumn().setText(Constants.TABLE_HEADER_URL);
 		rulesColumn.getColumn().setWidth(200);
 		versionsColumn.getColumn().setWidth(100);
 		rulesURL.getColumn().setWidth(200);

		listOfRulesets = getRulesetsFromPrefs();

 		for (Iterator<Ruleset> itr = listOfRulesets.iterator(); itr.hasNext();) {
 			Ruleset ruleset = (Ruleset) itr.next();
 			ruleset.setVersions(new CCombo(table.getTable(), SWT.NONE));
 			String[] items = CrySLUtils.getRuleVersions(ruleset.getFolderName());
 			if (items != null) {
				ruleset.getVersions().setItems(items);
 			ruleset.getVersions()
 					.setItems(CrySLUtils.getRuleVersions(ruleset.getFolderName()));
 			ruleset.setSelectedVersion((ruleset.getSelectedVersion().length() > 0) ? ruleset.getSelectedVersion()
 					: ruleset.getVersions().getItem(ruleset.getVersions().getItemCount() - 1));
 			ruleset.getVersions().select(ruleset.getVersions().indexOf(ruleset.getSelectedVersion()));
			}
			createRulesTableRow(ruleset);
 			ruleset.getVersions().addSelectionListener(new SelectionAdapter() {
 				@Override
 				public void widgetSelected(SelectionEvent e) {
 					super.widgetSelected(e);

 					ruleset.setSelectedVersion(
 							ruleset.getVersions().getItem(ruleset.getVersions().getSelectionIndex()));
 				}
 			});
 		}
 	}

 	/***
 	 * This method modifies the rule set table by adding a new rule set entry
 	 * @param newRuleset The new rule set which is added to the table
 	 */
 	private void modifyRulesTable(Ruleset newRuleset) {
 		newRuleset.setVersions(new CCombo(table.getTable(), SWT.NONE));
 		newRuleset.getVersions()
 				.setItems(CrySLUtils.getRuleVersions(newRuleset.getFolderName()));
 		newRuleset.setSelectedVersion(newRuleset.getVersions().getItem(newRuleset.getVersions().getItemCount() - 1));
 		newRuleset.getVersions().select(newRuleset.getVersions().getItemCount() - 1);
 		createRulesTableRow(newRuleset);
 		newRuleset.getVersions().addSelectionListener(new SelectionAdapter() {
 			@Override
 			public void widgetSelected(SelectionEvent e) {
 				super.widgetSelected(e);
 				newRuleset.setSelectedVersion(
 						newRuleset.getVersions().getItem(newRuleset.getVersions().getSelectionIndex()));
 			}
 		});
 	}

 	/***
 	 * This method creates the UI for the preference page.
 	 * 
 	 * @param parent Instance of the eclipse preference window on which UI widgets
 	 *               for CogniCrypt are added.
 	 */
	private void createBasicContents(Composite parent) {
		final Group staticAnalysisGroup = UIUtils.addHeaderGroup(parent, "Analysis");

		final Composite source = new Composite(staticAnalysisGroup, SWT.FILL);
		source.setLayout(new GridLayout(3, true));
		final Label ruleSource = new Label(source, SWT.NONE);
		ruleSource.setText("Source of CrySL rules: ");
		
		table = CheckboxTableViewer.newCheckList(staticAnalysisGroup, SWT.CHECK);
 		table.getTable().setHeaderVisible(true);
 		table.getTable().setLinesVisible(true);
 		createRulesTable();
 		table.getTable().addSelectionListener(new SelectionAdapter() {
 			@Override
 			public void widgetSelected(SelectionEvent e) {
 				super.widgetSelected(e);
 				if (e.detail == SWT.CHECK) {
 					TableItem item = (TableItem) e.item;

 					for (Iterator<Ruleset> itr = listOfRulesets.iterator(); itr.hasNext();) {
 						Ruleset ruleset = (Ruleset) itr.next();
 						if (item.getText(0) == ruleset.getFolderName())
 							ruleset.setChecked(item.getChecked());
 					}
 				}
 			}
 		});

 		selectCustomRulesCheckBox = new Button(staticAnalysisGroup, SWT.CHECK);
 		selectCustomRulesCheckBox.setText("Select custom rules");
		
		automatedAnalysisCheckBox = new Button(staticAnalysisGroup, SWT.CHECK);
		automatedAnalysisCheckBox.setText("Enable automated analysis when saving");
		
		providerDetectionCheckBox = new Button(staticAnalysisGroup, SWT.CHECK);
		providerDetectionCheckBox.setText("Enable provider detection analysis");

		secureObjectsCheckBox = new Button(staticAnalysisGroup, SWT.CHECK);
		secureObjectsCheckBox.setText("Show secure objects");

		analyseDependenciesCheckBox = new Button(staticAnalysisGroup, SWT.CHECK);
		analyseDependenciesCheckBox.setText("Include dependencies to projects analysis");
		analyseDependenciesCheckBox.setSelection(preferences.getBoolean(Constants.ANALYSE_DEPENDENCIES));
		
		addNewRulesetButton = new Button(staticAnalysisGroup, SWT.PUSH);
 		addNewRulesetButton.setText("Add ruleset");
 		addNewRulesetButton.addListener(SWT.Selection, new Listener() {

 			@Override
 			public void handleEvent(Event e) {
 				addNewRuleset();
 			}
 		});
	}
	
	protected void addNewRuleset() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		AddNewRulesetDialog dialog = new AddNewRulesetDialog(window.getShell());
		dialog.create();
		if (dialog.open() == Window.OK) {
			if(ifExists(dialog.getRulesetUrl())) {
				MessageDialog.openError(window.getShell(), "Duplicate Ruleset", "You are trying to add an existing ruleset!");
				return;
			}
			else {
				if(ArtifactUtils.downloadRulesets(dialog.getRulesetUrl())) {
					Activator.getDefault().logInfo("Rulesets updated.");
				}
				Ruleset newRuleset = new Ruleset(dialog.getRulesetUrl());
				modifyRulesTable(newRuleset);
				listOfRulesets.add(newRuleset);
			}
		}
	}
	
	boolean ifExists(String url) {
		List<Ruleset> existingRulesets = getRulesetsFromPrefs();
		for (Ruleset ruleset : existingRulesets) {
			if(ruleset.getUrl().equals(url)) {
				return true;
			}
		}
		return false;
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
		final Group staticAnalysisGroup = UIUtils.addHeaderGroup(parent, "Analysis");

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

	/***
 	 * This method assigns default values for each of the preference options and is
 	 * invoked when Restore defaults is clicked.
 	 */
	@Override
	public void setDefaultValues() {
		selectCustomRulesCheckBox.setSelection(true);
		automatedAnalysisCheckBox.setSelection(preferences.getDefaultBoolean(Constants.AUTOMATED_ANALYSIS));
		providerDetectionCheckBox.setSelection(preferences.getDefaultBoolean(Constants.PROVIDER_DETECTION_ANALYSIS));
		secureObjectsCheckBox.setSelection(preferences.getDefaultBoolean(Constants.SHOW_SECURE_OBJECTS));
		analyseDependenciesCheckBox.setSelection(preferences.getDefaultBoolean(Constants.ANALYSE_DEPENDENCIES));
		
		for (Iterator<Ruleset> itr = listOfRulesets.iterator(); itr.hasNext();) {
 			Ruleset ruleset = (Ruleset) itr.next();
 			ruleset.getVersions().select(ruleset.getVersions().getItemCount() - 1);
 			if (ruleset.getFolderName().equals("JavaCryptographicArchitecture"))
 				ruleset.getRulesRow().setChecked(true);
 			else
 				ruleset.getRulesRow().setChecked(false);
 			ruleset.setSelectedVersion(ruleset.getVersions().getItem(ruleset.getVersions().getItemCount() - 1));
 			ruleset.setChecked(ruleset.getRulesRow().getChecked());
 		}
		
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
		preferences.setValue(Constants.PROVIDER_DETECTION_ANALYSIS, providerDetectionCheckBox.getSelection());
		preferences.setValue(Constants.SHOW_SECURE_OBJECTS, secureObjectsCheckBox.getSelection());
		preferences.setValue(Constants.ANALYSE_DEPENDENCIES, analyseDependenciesCheckBox.getSelection());
		preferences.setValue(Constants.CALL_GRAPH_SELECTION, CGSelection.getSelectionIndex());
		preferences.setValue(Constants.FORBIDDEN_METHOD_MARKER_TYPE, forbidden.getSelectionIndex());
		preferences.setValue(Constants.CONSTRAINT_ERROR_MARKER_TYPE, constraint.getSelectionIndex());
		preferences.setValue(Constants.INCOMPLETE_OPERATION_MARKER_TYPE, incompleteOp.getSelectionIndex());
		preferences.setValue(Constants.NEVER_TYPEOF_MARKER_TYPE, neverType.getSelectionIndex());
		preferences.setValue(Constants.REQUIRED_PREDICATE_MARKER_TYPE, reqPred.getSelectionIndex());
		preferences.setValue(Constants.TYPESTATE_ERROR_MARKER_TYPE, typestate.getSelectionIndex());
		
		for (Iterator<Ruleset> itr = listOfRulesets.iterator(); itr.hasNext();) {
 			Ruleset ruleset = (Ruleset) itr.next();

 			Preferences subPref = rulePreferences.node(ruleset.getFolderName());
 			subPref.putBoolean("CheckboxState", ruleset.isChecked());
 			subPref.put("FolderName", ruleset.getFolderName());
 			subPref.put("SelectedVersion", ruleset.getSelectedVersion());
 			subPref.put("Url", ruleset.getUrl());
 		}
	}

}
