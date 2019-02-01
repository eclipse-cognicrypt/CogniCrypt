package de.cognicrypt.staticanalyzer.view;

import java.util.ArrayList;


/**
 * This class contains the seeds , errors and health information from all the classes that were analysed and reached ResultsCCUIListener.
 * @author Adnan Manzoor
 *
 */
public class AnalysisData {
	ArrayList<String> seeds;
	ArrayList<String> errors;
	String health;
	
	public AnalysisData(){
		this.seeds = new ArrayList<>();
		this.errors = new ArrayList<>();
		this.health = "Healthy";
	}
	
	public void addSeed(String seed) {
		seeds.add(seed);
	}
	
	public ArrayList<String> getSeeds(){
		return seeds;
	}
	
	public void addError(String error) {
		errors.add(error);
	}
	
	public ArrayList<String> getErrors() {
		return errors;
	}
	
	public void setHealth(String health) {
		this.health=health;
	}
	
	public String getHealth() {
		return health;
	}

}
