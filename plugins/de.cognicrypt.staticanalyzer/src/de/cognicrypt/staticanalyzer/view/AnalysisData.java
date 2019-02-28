package de.cognicrypt.staticanalyzer.view;

import java.util.ArrayList;
import crypto.analysis.IAnalysisSeed;
import crypto.analysis.errors.AbstractError;

/**
 * This class contains the seeds, errors and health information from all the classes that were analysed and reached ResultsCCUIListener.
 * 
 * @author Adnan Manzoor
 */
public class AnalysisData {
	private final ArrayList<IAnalysisSeed> seeds;
	private final ArrayList<AbstractError> errors;
	private boolean isHealthy;

	public AnalysisData() {
		this.seeds = new ArrayList<>();
		this.errors = new ArrayList<>();
		this.isHealthy = true;
	}

	public void addSeed(IAnalysisSeed seed) {
		seeds.add(seed);
	}

	public ArrayList<IAnalysisSeed> getSeeds() {
		return seeds;
	}

	public void addError(AbstractError error) {
		errors.add(error);
	}

	public ArrayList<AbstractError> getErrors() {
		return errors;
	}

	public void setHealth(boolean isHealthy) {
		this.isHealthy = isHealthy;
	}

	public boolean getHealth() {
		return isHealthy;
	}

}
