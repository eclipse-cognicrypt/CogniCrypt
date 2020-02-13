package de.cognicrypt.staticanalyzer.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.ini4j.Profile.Section;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

public class DefaultRulePreferences {
	
	public static void addDefaults() {
		Preferences rulePreferences = InstanceScope.INSTANCE.getNode(de.cognicrypt.core.Activator.PLUGIN_ID);
		String[] listOfNodes;
		try {
			listOfNodes = rulePreferences.childrenNames();
			if (listOfNodes.length == 0) {
				Section ini = Utils.getConfig().get(Constants.INI_URL_HEADER);
				List<Ruleset> listOfRulesets = new ArrayList<Ruleset>() {
			 		private static final long serialVersionUID = 1L;
			 		{
			 			add(new Ruleset(ini.get(Constants.INI_JCA_NEXUS), true));
			 			add(new Ruleset(ini.get(Constants.INI_BC_NEXUS)));
			 			add(new Ruleset(ini.get(Constants.INI_TINK_NEXUS)));
			 			add(new Ruleset(ini.get(Constants.INI_BCJCA_NEXUS)));
			 		}
			 	};
			 	for (Iterator<Ruleset> itr = listOfRulesets.iterator(); itr.hasNext();) {
		 			Ruleset ruleset = (Ruleset) itr.next();
		
		 			Preferences subPref = rulePreferences.node(ruleset.getFolderName());
		 			subPref.putBoolean("CheckboxState", ruleset.isChecked());
		 			subPref.put("FolderName", ruleset.getFolderName());
		 			subPref.put("SelectedVersion", ruleset.getSelectedVersion());
		 			subPref.put("Url", ruleset.getUrl());
		 		}
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
