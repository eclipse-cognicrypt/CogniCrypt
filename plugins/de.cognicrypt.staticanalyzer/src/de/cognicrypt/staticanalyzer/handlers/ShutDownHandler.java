/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;

public class ShutDownHandler implements IWorkbenchListener {

	@Override
	public boolean preShutdown(final IWorkbench workbench, final boolean forced) {
		for (final ResultsCCUIListener res : Activator.getResultsReporters()) {
			res.getMarkerGenerator().clearMarkers();
		}
		return true;
	}

	@Override
	public void postShutdown(final IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
