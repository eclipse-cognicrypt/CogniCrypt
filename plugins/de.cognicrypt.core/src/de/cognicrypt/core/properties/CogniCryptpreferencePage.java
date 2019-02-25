package de.cognicrypt.core.properties;

import java.util.Arrays;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.osgi.service.prefs.BackingStoreException;
import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;

public class CogniCryptpreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public CogniCryptpreferencePage() {}

	private Combo ruleSelection;
	// private Button checkBox1;
	// private Button checkBox2;
	private Button automatedAnalysisCheckBox;
	private Button secureObjectsCheckBox;
	private Combo CGSelection;
	// private Combo advCombo2;
	private Combo forbidden;
	private Combo reqPred;
	private Combo constraint;
	private Combo neverType;
	private Combo incompleteOp;
	private Combo typestate;

	@Override
	public void init(IWorkbench CogniWorkbench) {}

	@Override
	protected Control createContents(Composite parent) {

		IPreferenceStore store = getPreferenceStore();
		final Composite container = new Composite(parent, SWT.FILL);
		container.setLayout(new GridLayout(1, true));
		final Composite source = new Composite(container, SWT.FILL);
		source.setLayout(new GridLayout(2, true));

		final Label ruleSource = new Label(source, SWT.NONE);
		ruleSource.setText("Source of CrySL rules: ");

		// other options: "Default JSSE rules","Default Tink rules"
		String[] choices = {"Default JCA Rules"};
		ruleSelection = new Combo(source, SWT.DROP_DOWN | SWT.READ_ONLY);
		ruleSelection.setItems(choices);

		/*
		 * checkBox1 = new Button(group1,SWT.CHECK); checkBox1.setText("Enable automatic analysis of dependencies"); checkBox1.addSelectionListener(new SelectionAdapter() {
		 * 
		 * @Override public void widgetSelected(SelectionEvent event) {
		 * 
		 * checkBox2.setSelection(true); } });
		 * 
		 * checkBox2 = new Button(group1,SWT.CHECK); checkBox2.setText("Enable automatic analysis of dependencies on change");
		 */
		automatedAnalysisCheckBox = new Button(container, SWT.CHECK);
		automatedAnalysisCheckBox.setText("Enable automated analysis when saving");

		secureObjectsCheckBox = new Button(container, SWT.CHECK);
		secureObjectsCheckBox.setText("Show secure objects");
		secureObjectsCheckBox.setEnabled(store.getBoolean(ICogniCryptConstants.AUTOMATED_ANALYSIS));

		new Label(container, SWT.NONE);
		final ExpandableComposite collap = new ExpandableComposite(container, SWT.Collapse);
		collap.setText("Advanced Options");

		final Composite advancedOptions = new Composite(collap, SWT.None);
		collap.setClient(advancedOptions);
		advancedOptions.setLayout(new RowLayout(SWT.VERTICAL));

		final Composite callGraphContainer = new Composite(advancedOptions, SWT.None);
		callGraphContainer.setLayout(new GridLayout(2, true));
		final Label label1 = new Label(callGraphContainer, SWT.SHADOW_IN);
		label1.setText("Call-graph construction algorithm");

		CGSelection = new Combo(callGraphContainer, SWT.DROP_DOWN | SWT.READ_ONLY);
		CGSelection.setItems(Arrays.stream(Constants.CG.values()).map(Enum::name).toArray(String[]::new));

		// final Label label2 = new Label(advancedOptions, SWT.SHADOW_IN);
		// label2.setText("Entry point");
		// String[] choices3 = {"getImageDescriptor", "copyClaferHeader", "printClafer"};
		// advCombo2 = new Combo(advancedOptions, SWT.DROP_DOWN);
		// advCombo2.setItems(choices3);
		// advCombo2.select(0);

		final Group errorTypeGroup = new Group(advancedOptions, SWT.SHADOW_IN);
		errorTypeGroup.setText("Error-Marker Types");
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

		collap.setExpanded(true);

		initializeValues();
		return container;
	}

	@Override
	public boolean performOk() {
		storeValues();
		try {
			InstanceScope a = (InstanceScope) InstanceScope.INSTANCE;
			a.getNode(Activator.PLUGIN_ID).flush();
		}
		catch (BackingStoreException e) {
			Activator.getDefault().logError(e, "Failed to store preferences. Please report this bug to the maintainers.");
		}
		return true;
	}

	private void storeValues() {
		IPreferenceStore store = getPreferenceStore();
		// store.setValue(ICogniCryptConstants.PRE_CHECKBOX1, checkBox1.getSelection());
		// store.setValue(ICogniCryptConstants.PRE_CHECKBOX2, checkBox2.getSelection());
		store.setValue(ICogniCryptConstants.AUTOMATED_ANALYSIS, automatedAnalysisCheckBox.getSelection());
		store.setValue(ICogniCryptConstants.SHOW_SECURE_OBJECTS, secureObjectsCheckBox.getSelection());
		store.setValue(ICogniCryptConstants.RULE_SELECTION, ruleSelection.getSelectionIndex());
		store.setValue(ICogniCryptConstants.CALL_GRAPH_SELECTION, CGSelection.getSelectionIndex());

		store.setValue(Constants.FORBIDDEN_METHOD_MARKER_TYPE, forbidden.getSelectionIndex());
		store.setValue(Constants.CONSTRAINT_ERROR_MARKER_TYPE, constraint.getSelectionIndex());
		store.setValue(Constants.INCOMPLETE_OPERATION_MARKER_TYPE, incompleteOp.getSelectionIndex());
		store.setValue(Constants.NEVER_TYPEOF_MARKER_TYPE, neverType.getSelectionIndex());
		store.setValue(Constants.REQUIRED_PREDICATE_MARKER_TYPE, reqPred.getSelectionIndex());
		store.setValue(Constants.TYPESTATE_ERROR_MARKER_TYPE, typestate.getSelectionIndex());
		// store.setValue(ICogniCryptConstants.PRE_ADV_COMBO3, advCombo3.getSelectionIndex());
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	private void initializeValues() {
		IPreferenceStore store = getPreferenceStore();
		ruleSelection.select(store.getInt(ICogniCryptConstants.RULE_SELECTION));
		// checkBox1.setSelection(store.getBoolean(ICogniCryptConstants.PRE_CHECKBOX1));
		// checkBox2.setSelection(store.getBoolean(ICogniCryptConstants.PRE_CHECKBOX2));
		automatedAnalysisCheckBox.setSelection(store.getBoolean(ICogniCryptConstants.AUTOMATED_ANALYSIS));
		secureObjectsCheckBox.setSelection(store.getBoolean(ICogniCryptConstants.SHOW_SECURE_OBJECTS));

		int currentCG = store.getInt(ICogniCryptConstants.CALL_GRAPH_SELECTION);
		CGSelection.select(currentCG > -1 ? currentCG : store.getDefaultInt(ICogniCryptConstants.CALL_GRAPH_SELECTION));

		int errorType = store.getInt(Constants.FORBIDDEN_METHOD_MARKER_TYPE);
		forbidden.select(errorType > -1 ? errorType : store.getDefaultInt(Constants.FORBIDDEN_METHOD_MARKER_TYPE));

		errorType = store.getInt(Constants.CONSTRAINT_ERROR_MARKER_TYPE);
		constraint.select(errorType > -1 ? errorType : store.getDefaultInt(Constants.CONSTRAINT_ERROR_MARKER_TYPE));

		errorType = store.getInt(Constants.INCOMPLETE_OPERATION_MARKER_TYPE);
		incompleteOp.select(errorType > -1 ? errorType : store.getDefaultInt(Constants.INCOMPLETE_OPERATION_MARKER_TYPE));

		errorType = store.getInt(Constants.TYPESTATE_ERROR_MARKER_TYPE);
		typestate.select(errorType > -1 ? errorType : store.getDefaultInt(Constants.TYPESTATE_ERROR_MARKER_TYPE));

		errorType = store.getInt(Constants.NEVER_TYPEOF_MARKER_TYPE);
		neverType.select(errorType > -1 ? errorType : store.getDefaultInt(Constants.NEVER_TYPEOF_MARKER_TYPE));

		errorType = store.getInt(Constants.REQUIRED_PREDICATE_MARKER_TYPE);
		reqPred.select(errorType > -1 ? errorType : store.getDefaultInt(Constants.REQUIRED_PREDICATE_MARKER_TYPE));

		// advCombo2.setSelection(store.getBoolean(ICogniCryptconstants.PRE_ADV_COMBO2));
		// advCombo3.select(store.getInt(ICogniCryptConstants.PRE_ADV_COMBO3));
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		IPreferenceStore store = getPreferenceStore();
		// checkBox1.setSelection(store
		// .getDefaultBoolean(ICogniCryptConstants.PRE_CHECKBOX1));
		// checkBox2.setSelection(store
		// .getDefaultBoolean(ICogniCryptConstants.PRE_CHECKBOX2));
		automatedAnalysisCheckBox.setSelection(store.getDefaultBoolean(ICogniCryptConstants.AUTOMATED_ANALYSIS));
		secureObjectsCheckBox.setSelection(store.getDefaultBoolean(ICogniCryptConstants.SHOW_SECURE_OBJECTS));
		ruleSelection.select(store.getDefaultInt(ICogniCryptConstants.RULE_SELECTION));
		CGSelection.select(store.getDefaultInt(ICogniCryptConstants.CALL_GRAPH_SELECTION));

		forbidden.select(store.getDefaultInt(Constants.FORBIDDEN_METHOD_MARKER_TYPE));
		constraint.select(store.getDefaultInt(Constants.CONSTRAINT_ERROR_MARKER_TYPE));
		incompleteOp.select(store.getDefaultInt(Constants.INCOMPLETE_OPERATION_MARKER_TYPE));
		typestate.select(store.getDefaultInt(Constants.TYPESTATE_ERROR_MARKER_TYPE));
		neverType.select(store.getDefaultInt(Constants.NEVER_TYPEOF_MARKER_TYPE));
		reqPred.select(store.getDefaultInt(Constants.REQUIRED_PREDICATE_MARKER_TYPE));

		// advCombo3.select(store.getDefaultInt(ICogniCryptConstants.PRE_ADV_COMBO3));
	}

}
