package de.cognicrypt.codegenerator.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.properties.PreferenceListener;
import de.cognicrypt.utils.Utils;

public class CodeGenPreferences extends PreferenceListener {

	private IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
	
	private Button persistConfig;
	
	@Override
	public void compileBasicPreferences(Composite parent) {

	}

	@Override
	public void compileAdvancedPreferences(Composite parent) {
		final Group codeGenGroup = Utils.addHeaderGroup(parent, "Code Generator");
		
		persistConfig = new Button(codeGenGroup, SWT.CHECK);
		persistConfig.setText("Persist Code-generation Configuration");
		
		preferences.setDefault(Constants.PERSIST_CONFIG, false);
		
		persistConfig.setSelection(preferences.getBoolean(Constants.PERSIST_CONFIG));
	}

	@Override
	public void setDefaultValues() {
		persistConfig.setSelection(preferences.getDefaultBoolean(Constants.PERSIST_CONFIG));
	}

	@Override
	protected void storeValues() {
		preferences.setValue(Constants.PERSIST_CONFIG, persistConfig.getSelection());
	}

}
