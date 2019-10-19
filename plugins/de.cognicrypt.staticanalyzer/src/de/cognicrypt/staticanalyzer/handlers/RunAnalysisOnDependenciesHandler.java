/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.Utils;

public class RunAnalysisOnDependenciesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		IProject ip = Utils.getCurrentlySelectedIProject();
		IJavaProject javaProject = JavaCore.create(ip);
		if (javaProject == null) {
			Activator.getDefault().logInfo("JavaCore could not create IJavaProject for project " + ip.getName() + ".");
			return false;
		}

		final AnalysisKickOff akf = new AnalysisKickOff();
		akf.analyzeDependenciesOnly(true);
		if (akf.setUp(javaProject)) {
			akf.run();
		}

		return null;
	}

}
