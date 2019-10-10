/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.core.properties;

import org.eclipse.swt.widgets.Composite;

public abstract class PreferenceListener {

	public abstract void compileBasicPreferences(Composite parent);

	public abstract void compileAdvancedPreferences(Composite parent);

	public abstract void setDefaultValues();

	protected abstract void storeValues();
}
