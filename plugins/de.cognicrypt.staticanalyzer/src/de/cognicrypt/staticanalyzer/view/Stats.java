package de.cognicrypt.staticanalyzer.view;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IProject;

/**
 * This class contains the data of the analysis from the ResultsCCUIListener.
 * 
 * @author Adnan Manzoor
 */

public class Stats {

	private IProject project;
	private String timeOfAnalysis;
	private Map<String, AnalysisData> classesAnalysed = new HashMap<>();

	public void setProject(IProject project) {
		this.project = project;
	}

	public IProject getProject() {
		return project;
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
