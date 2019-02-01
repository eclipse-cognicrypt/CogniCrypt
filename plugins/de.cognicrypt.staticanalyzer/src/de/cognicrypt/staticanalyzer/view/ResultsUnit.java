package de.cognicrypt.staticanalyzer.view;

/**
 * This class contains the analysis Data to be shown in the Statistics View.
 * @author Adnan Manzoor
 *
 */

public class ResultsUnit {
	
	private String className;
	private String seed;
	private String error;
	private String healthStatus;
	
	public ResultsUnit(String className, String seed, String error, String healthStatus ) {
		// TODO Auto-generated constructor stub
		this.className =className;
		this.seed = seed;
		this.error = error;
		this. healthStatus = healthStatus;
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

	public String getHealthStatus() {
		return healthStatus;
	}

	public void setHealthStatus(String healthStatus) {
		this.healthStatus = healthStatus;
	}

}
