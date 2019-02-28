package de.cognicrypt.core.properties;

import org.eclipse.swt.widgets.Composite;

public abstract class PreferenceListener {

	public abstract void compileBasicPreferences(Composite parent);

	public abstract void compileAdvancedPreferences(Composite parent);
	
	public abstract void setDefaultValues();

	protected abstract void storeValues();
}
