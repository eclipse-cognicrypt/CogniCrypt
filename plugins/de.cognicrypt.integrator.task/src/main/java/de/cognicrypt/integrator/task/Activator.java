/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.cognicrypt.core.Constants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = Constants.PLUGIN_ID;

	// The shared instance
	private static Activator plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	private void log(final int severity, final String message, final Exception ex) {
		getLog().log(new Status(severity, Activator.PLUGIN_ID, message, ex));
	}

	public void logError(final Exception ex) {
		log(IStatus.ERROR, ex.getMessage(), ex);
	}

	public void logError(final Exception ex, final String message) {
		log(IStatus.ERROR, message, ex);
	}

	public void logError(final String message) {
		log(IStatus.ERROR, message, null);
	}

	public void logInfo(final String message) {
		log(IStatus.INFO, message, null);
	}

}
