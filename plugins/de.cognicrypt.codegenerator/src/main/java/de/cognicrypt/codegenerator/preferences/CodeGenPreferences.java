/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.properties.PreferenceListener;
import de.cognicrypt.utils.UIUtils;

public class CodeGenPreferences extends PreferenceListener {

	private IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();

	private Button persistConfig;

	@Override
	public void compileBasicPreferences(Composite parent) {

	}

	@Override
	public void compileAdvancedPreferences(Composite parent) {
		final Group codeGenGroup = UIUtils.addHeaderGroup(parent, "Code Generator");

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
