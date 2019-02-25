package de.cognicrypt.core.properties;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import de.cognicrypt.core.Activator;

public class CogniCryptPreferenceInitializer extends AbstractPreferenceInitializer {
	
	@Override
	public void initializeDefaultPreferences(){
        
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(ICogniCryptConstants.PRE_COMBO, 0);
//		store.setDefault(ICogniCryptConstants.PRE_CHECKBOX1, false);
//		store.setDefault(ICogniCryptConstants.PRE_CHECKBOX2, false);
		store.setDefault(ICogniCryptConstants.PRE_CHECKBOX3, false);
		store.setDefault(ICogniCryptConstants.PRE_CHECKBOX4, false);
		store.setDefault(ICogniCryptConstants.PRE_ADV_COMBO1,0);
//		store.setDefault(ICogniCryptConstants.PRE_ADV_COMBO2,0);
//		store.setDefault(ICogniCryptConstants.PRE_ADV_COMBO3,0);

	}

}
