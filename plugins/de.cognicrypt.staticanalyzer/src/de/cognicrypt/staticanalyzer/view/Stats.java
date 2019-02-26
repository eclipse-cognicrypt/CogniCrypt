package de.cognicrypt.staticanalyzer.view;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the data of the analysis from the ResultsCCUIListener.
 * 
 * @author Adnan Manzoor
 */

public class Stats {

	String projectname;
	String timeOfAnalysis;
	Map<String, AnalysisData> classesAnalysed = new HashMap<>();

	public void setProjectName(String projName) {
		projectname = projName;
	}

	public String getProjectName() {
		return projectname;
	}

	public void setTimeOfAnalysis(String time) {
		timeOfAnalysis = time;
	}

	public String getTimeOfAnalysis() {
		return timeOfAnalysis;
	}

	public void setClassesAnalysed(Map<String, AnalysisData> classAnalysed) {
		this.classesAnalysed = classAnalysed;
	}

	public Map<String, AnalysisData> getClassesAnalysed() {
		return classesAnalysed;
	}

}
