package de.cognicrypt.staticanalyzer.handlers;

import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;

public class MonitorReporter extends Thread{

	private final ResultsCCUIListener resultsReporter;
	final SootThread sootThread;
	private int totalSeeds;
	private int processedSeeds; 
	private int workUnitsCompleted;
	private boolean cgGen;

	public MonitorReporter(final ResultsCCUIListener resultsListener ,final SootThread sootThread) {
		this.resultsReporter = resultsListener;
		this.sootThread = sootThread;
	}


	public int getWorkUnitsCompleted() {
		return workUnitsCompleted;
	}

	public void setWorkUnitsCompleted(int workUnitsCompleted) {
		this.workUnitsCompleted = workUnitsCompleted;
	}

	public int getTotalSeeds() {
		return totalSeeds;
	}


	public void setTotalSeeds(int totalSeeds) {
		this.totalSeeds = totalSeeds;
	}


	public int getProcessedSeeds() {
		return processedSeeds;
	}


	public void setProcessedSeeds(int processedSeeds) {
		this.processedSeeds = processedSeeds;
	}


	public boolean isCgGen() {
		return cgGen;
	}


	public void setCgGen(boolean cgGen) {
		this.cgGen = cgGen;
	}


	@Override
	public void run() {
		while(sootThread.isAlive()) {
			setCgGen(resultsReporter.isCgGenComplete());
			setProcessedSeeds(resultsReporter.getProcessedSeeds());
			setTotalSeeds(resultsReporter.getTotalSeeds());
			setWorkUnitsCompleted(resultsReporter.getWorkUnitsCompleted());
		}
	}
}