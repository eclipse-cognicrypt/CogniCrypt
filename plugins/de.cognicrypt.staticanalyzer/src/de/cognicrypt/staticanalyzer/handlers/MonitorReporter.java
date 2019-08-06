package de.cognicrypt.staticanalyzer.handlers;

import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;

public class MonitorReporter extends Thread{

	private final ResultsCCUIListener resultsReporter;
	final SootThread sootThread;
	private int totalSeeds;
	private int processedSeeds; 
	private int workUnitsCompleted;

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


	@Override
	public void run() {
		int worked = 0;
		int percentCompleted = 0;
		int temp = 0;
		while(sootThread.isAlive()) {
			percentCompleted = resultsReporter.getPercentCompleted();
			setProcessedSeeds(resultsReporter.getProcessedSeeds());
			setTotalSeeds(resultsReporter.getTotalSeeds());
			temp = percentCompleted - worked;
		if(temp > 0) {
				setWorkUnitsCompleted(temp);
				worked = percentCompleted;
			}
			else
				setWorkUnitsCompleted(0);

		}
	}



}