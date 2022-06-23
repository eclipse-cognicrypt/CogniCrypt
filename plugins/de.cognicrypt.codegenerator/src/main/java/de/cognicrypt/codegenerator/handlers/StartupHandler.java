package de.cognicrypt.codegenerator.handlers;

import org.eclipse.ui.IStartup;

import de.cognicrypt.codegenerator.preferences.CodeGenPreferences;
import de.cognicrypt.core.properties.CogniCryptPreferencePage;

public class StartupHandler implements IStartup {

    @Override
    public void earlyStartup() {
        CogniCryptPreferencePage.registerPreferenceListener(new CodeGenPreferences());
    }

}