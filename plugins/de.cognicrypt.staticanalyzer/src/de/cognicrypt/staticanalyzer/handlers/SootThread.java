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

import org.eclipse.jdt.core.IJavaProject;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;
import de.cognicrypt.staticanalyzer.sootbridge.SootRunner;

public class SootThread extends Thread {
	private boolean succ = false;
	private final IJavaProject curProj;
	private final ResultsCCUIListener resultsReporter;
	private final Boolean dependencyAnalyser;

	public SootThread(final IJavaProject curProject, final ResultsCCUIListener resultsListener, final Boolean dependencyAnalyser) {
		this.curProj = curProject;
		this.resultsReporter = resultsListener;
		this.dependencyAnalyser = dependencyAnalyser;
	}

	public boolean isSucc() {
		return this.succ;
	}

	@Override
	public void run() {
		this.succ = SootRunner.runSoot(this.curProj, this.resultsReporter, this.dependencyAnalyser);
	}
}
