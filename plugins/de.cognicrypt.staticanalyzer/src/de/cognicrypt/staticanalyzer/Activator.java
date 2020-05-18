/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import de.cognicrypt.core.properties.CogniCryptPreferencePage;
import de.cognicrypt.staticanalyzer.handlers.ShutDownHandler;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;
import de.cognicrypt.staticanalyzer.utilities.ArtifactUtils;
import de.cognicrypt.staticanalyzer.utilities.DefaultRulePreferences;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.cognicrypt.staticanalyzer"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static List<ResultsCCUIListener> resReporters;

	/**
	 * The constructor
	 */
	public Activator() {}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		Activator.plugin = this;
		resReporters = new ArrayList<ResultsCCUIListener>();
		PlatformUI.getWorkbench().addWorkbenchListener(new ShutDownHandler());

		CogniCryptPreferencePage.registerPreferenceListener(new StaticAnalyzerPreferences());
		
		if(ArtifactUtils.downloadRulesets()) {
 			getDefault().logInfo("Rulesets updated.");
 			DefaultRulePreferences.addDefaults();
		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		Activator.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return Activator.plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(Activator.PLUGIN_ID, path);
	}

	private void log(final int severity, final String message, final Exception ex) {
		getLog().log(new Status(severity, Activator.PLUGIN_ID, message, ex));
	}

	public void logError(final Exception ex) {
		logError(ex, ex.getMessage());
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

	public static void registerResultsListener(final ResultsCCUIListener gen) {
		resReporters.add(gen);
	}

	public static List<ResultsCCUIListener> getResultsReporters() {
		return resReporters;
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return de.cognicrypt.core.Activator.getDefault().getPreferenceStore();
	}

}
