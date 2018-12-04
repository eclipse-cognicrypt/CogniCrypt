package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.jdt.core.IJavaProject;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;
import de.cognicrypt.staticanalyzer.sootbridge.SootRunner;

public class SootThread extends Thread {
	private boolean succ = false;
	private final IJavaProject curProj;
	private final ResultsCCUIListener resultsReporter;

	public SootThread(final IJavaProject curProject, final ResultsCCUIListener resultsListener) {
		this.curProj = curProject;
		this.resultsReporter = resultsListener;
	}

	public boolean isSucc() {
		return this.succ;
	}

	@Override
	public void run() {
		this.succ = SootRunner.runSoot(this.curProj, this.resultsReporter);
	}
}
