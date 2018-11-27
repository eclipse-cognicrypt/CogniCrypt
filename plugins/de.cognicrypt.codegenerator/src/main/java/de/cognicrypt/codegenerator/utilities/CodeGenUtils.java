/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.utilities;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.Utils;

public class CodeGenUtils extends Utils {

	/**
	 * Retrieves the current project. There are several options for what counts as the 'current' project. First, if CogniCrypt was started through context menu, the project
	 * right-clicked is the current project. Second, if the currently opened file is a Java file, its project is returned. Third, if the currently selected project, is a Java
	 * project, it is returned. If none of these conditions is fulfilled, <code>null</code> is returned.
	 * 
	 * @return Current project/<code>null</code> if project could be retrieved succesfully.
	 */
	public static IProject getCurrentProject() {
		if (Constants.WizardActionFromContextMenuFlag) {
			final IProject selectedProject = CodeGenUtils.getCurrentlySelectedIProject();
			if (selectedProject != null) {
				return selectedProject;
			}
		}
		return Utils.getCurrentProject();
	}

	public static File getResourceFromWithin(final String inputPath) {
		return Utils.getResourceFromWithin(inputPath, Activator.PLUGIN_ID);
	}

	public static File getFinalClaferFile(final String inputPath) {
		try {
			final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);

			if (bundle == null) {
				// running as application
				return new File(inputPath);
			} else {
				final URL fileURL = bundle.getEntry(inputPath);
				final URL resolvedURL = FileLocator.toFileURL(fileURL);
				final URI uri = new URI(resolvedURL.getProtocol(), resolvedURL.getPath(), null);
				SimpleDateFormat f = new SimpleDateFormat("ddMMyyyyHHmmss");
				String filename = uri.getPath().replace("Clafer/", "Clafer/FinalClafer" + f.format(new Date()));
				File file = new File(filename + ".cfr");
				file.createNewFile();
				return file;
			}
		} catch (final Exception ex) {
			Activator.getDefault().logError(ex);
		}

		return null;
	}
}
