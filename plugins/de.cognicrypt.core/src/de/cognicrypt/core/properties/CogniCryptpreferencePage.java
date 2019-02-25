package de.cognicrypt.core.properties;

import java.util.Arrays;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
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
	// Button advCombo2;
	// Combo advCombo3;

	@Override
	public void init(IWorkbench CogniWorkbench) {
	}

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
		secureObjectsCheckBox.setEnabled(store.getBoolean(ICogniCryptConstants.PRE_CHECKBOX3));

		new Label(container, SWT.NONE);
		final ExpandableComposite collap = new ExpandableComposite(container, SWT.Collapse);
		collap.setText("Advanced Options");
		
		final Composite advancedOptions = new Composite(collap, SWT.None);
		collap.setClient(advancedOptions);
		advancedOptions.setLayout(new RowLayout(SWT.VERTICAL));

		final Label label1 = new Label(advancedOptions, SWT.SHADOW_IN);
		label1.setText("Call-graph construction algorithm");

		CGSelection = new Combo(advancedOptions, SWT.DROP_DOWN | SWT.READ_ONLY);
		CGSelection.setItems(Arrays.stream(Constants.CG.values()).map(Enum::name).toArray(String[]::new));
		
		/*
		 * final Label label2 = new Label(group2, SWT.SHADOW_IN); label2.setText("Entry point"); String[] choices3 = {"getImageDescriptor","copyClaferHeader","printClafer"}; advCombo2
		 * = new Combo(group2, SWT.DROP_DOWN); advCombo2.setItems(choices3); advCombo2.select(0);
		 * 
		 * final Label label3 = new Label(group2, SWT.SHADOW_IN); label3.setText("Error-marker types");
		 * 
		 * String[] choices4 = {"Error", "Warning", "Info", "None"}; advCombo3 = new Combo(group2, SWT.DROP_DOWN); advCombo3.setItems(choices4);
		 */
		initializeValues();
		return container;
	}

	@Override
	public boolean performOk() {
		storeValues();
		Activator.getDefault().savePluginPreferences();
		return true;
	}

	private void storeValues() {
		IPreferenceStore store = getPreferenceStore();
		// store.setValue(ICogniCryptConstants.PRE_CHECKBOX1, checkBox1.getSelection());
		// store.setValue(ICogniCryptConstants.PRE_CHECKBOX2, checkBox2.getSelection());
		store.setValue(ICogniCryptConstants.PRE_CHECKBOX3, automatedAnalysisCheckBox.getSelection());
		store.setValue(ICogniCryptConstants.PRE_CHECKBOX4, secureObjectsCheckBox.getSelection());
		store.setValue(ICogniCryptConstants.PRE_COMBO, ruleSelection.getSelectionIndex());
		store.setValue(ICogniCryptConstants.PRE_ADV_COMBO1, CGSelection.getSelectionIndex());
		// store.setValue(ICogniCryptConstants.PRE_ADV_COMBO3, advCombo3.getSelectionIndex());
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	private void initializeValues() {
		IPreferenceStore store = getPreferenceStore();
		ruleSelection.select(store.getInt(ICogniCryptConstants.PRE_COMBO));
		// checkBox1.setSelection(store.getBoolean(ICogniCryptConstants.PRE_CHECKBOX1));
		// checkBox2.setSelection(store.getBoolean(ICogniCryptConstants.PRE_CHECKBOX2));
		automatedAnalysisCheckBox.setSelection(store.getBoolean(ICogniCryptConstants.PRE_CHECKBOX3));
		secureObjectsCheckBox.setSelection(store.getBoolean(ICogniCryptConstants.PRE_CHECKBOX4));
		CGSelection.select(store.getInt(ICogniCryptConstants.PRE_ADV_COMBO1));
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
		automatedAnalysisCheckBox.setSelection(store.getDefaultBoolean(ICogniCryptConstants.PRE_CHECKBOX3));
		secureObjectsCheckBox.setSelection(store.getDefaultBoolean(ICogniCryptConstants.PRE_CHECKBOX4));
		ruleSelection.select(store.getDefaultInt(ICogniCryptConstants.PRE_COMBO));
		CGSelection.select(store.getDefaultInt(ICogniCryptConstants.PRE_ADV_COMBO1));
		// advCombo3.select(store.getDefaultInt(ICogniCryptConstants.PRE_ADV_COMBO3));
	}

}
