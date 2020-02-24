package de.cognicrypt.staticanalyzer.kotlin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.cognicrypt.staticanalyzer.kotlin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

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
}
