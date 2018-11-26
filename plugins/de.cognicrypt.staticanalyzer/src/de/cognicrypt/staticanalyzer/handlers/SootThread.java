package de.cognicrypt.staticanalyzer.handlers;

import org.eclipse.jdt.core.IJavaProject;

import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;
import de.cognicrypt.staticanalyzer.sootbridge.SootRunner;

public class SootThread extends Thread {
	private boolean succ = false;
	private IJavaProject curProj;
	private ResultsCCUIListener resultsReporter;
	
	public SootThread(IJavaProject curProject, ResultsCCUIListener resultsListener) {
		curProj = curProject;
		resultsReporter = resultsListener;
	}
	
	public boolean isSucc() {
		return succ;
	}

	@Override
	public void run() {
		succ = SootRunner.runSoot(curProj, this.resultsReporter);
	}
}
