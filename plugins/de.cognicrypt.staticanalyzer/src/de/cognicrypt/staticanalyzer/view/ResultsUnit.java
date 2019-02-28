package de.cognicrypt.staticanalyzer.view;

/**
 * This class contains the analysis Data to be shown in the Statistics View.
 * 
 * @author Adnan Manzoor
 */

public class ResultsUnit {

	private String className;
	private String seed;
	private String error;
	private boolean isHealthy;

	public ResultsUnit(String className, String seed, String error, boolean isHealthy) {
		this.className = className;
		this.seed = seed;
		this.error = error;
		this.isHealthy = isHealthy;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isHealthy() {
		return isHealthy;
	}

	public void setHealthStatus(boolean healthStatus) {
		this.isHealthy = healthStatus;
	}

}
