package de.cognicrypt.staticanalyzer.view;

import java.util.ArrayList;

/**
 * This class contains the seeds, errors and health information from all the classes that were analysed and reached ResultsCCUIListener.
 * 
 * @author Adnan Manzoor
 */
public class AnalysisData {
	private final ArrayList<String> seeds;
	private final ArrayList<String> errors;
	private boolean isHealthy;

	public AnalysisData() {
		this.seeds = new ArrayList<>();
		this.errors = new ArrayList<>();
		this.isHealthy = true;
	}

	public void addSeed(String seed) {
		seeds.add(seed);
	}

	public ArrayList<String> getSeeds() {
		return seeds;
	}

	public void addError(String error) {
		errors.add(error);
	}

	public ArrayList<String> getErrors() {
		return errors;
	}

	public void setHealth(boolean isHealthy) {
		this.isHealthy = isHealthy;
	}

	public boolean getHealth() {
		return isHealthy;
	}

}
