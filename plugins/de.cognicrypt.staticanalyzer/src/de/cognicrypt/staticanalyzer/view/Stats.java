/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

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
