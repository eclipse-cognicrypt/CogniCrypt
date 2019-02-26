package de.cognicrypt.core.properties;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;

public class CogniCryptPreferenceInitializer extends AbstractPreferenceInitializer {
	
	@Override
	public void initializeDefaultPreferences(){
        
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(ICogniCryptConstants.RULE_SELECTION, 0);
//		store.setDefault(ICogniCryptConstants.PRE_CHECKBOX1, false);
//		store.setDefault(ICogniCryptConstants.PRE_CHECKBOX2, false);
		store.setDefault(ICogniCryptConstants.AUTOMATED_ANALYSIS, false);
		store.setDefault(ICogniCryptConstants.SHOW_SECURE_OBJECTS, false);
		store.setDefault(ICogniCryptConstants.CALL_GRAPH_SELECTION,0);
		
		store.setDefault(Constants.FORBIDDEN_METHOD_MARKER_TYPE, 0);
		store.setDefault(Constants.TYPESTATE_ERROR_MARKER_TYPE, 0);
		store.setDefault(Constants.INCOMPLETE_OPERATION_MARKER_TYPE, 0);
		store.setDefault(Constants.NEVER_TYPEOF_MARKER_TYPE, 0);
		store.setDefault(Constants.REQUIRED_PREDICATE_MARKER_TYPE, 0);
		store.setDefault(Constants.CONSTRAINT_ERROR_MARKER_TYPE, 0);
		
		store.setDefault(Constants.PREDICATE_CONTRADICTION_MARKER_TYPE, 0);
		store.setDefault(Constants.IMPRECISE_VALUE_EXTRACTION_MARKER_TYPE, 1);
//		store.setDefault(ICogniCryptConstants.PRE_ADV_COMBO2,0);
//		store.setDefault(ICogniCryptConstants.PRE_ADV_COMBO3,0);

	}

}
