/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.question;

import java.util.ArrayList;

import de.cognicrypt.core.Constants;

public class Answer {

	private String value;
	
	private Boolean defaultAnswer;
	private ArrayList<ClaferDependency> claferDependencies;
	private ArrayList<CodeDependency> codeDependencies;
	private ArrayList<UIDependency> uiDependencies;
	
	private int nextID = Constants.ANSWER_NO_NEXT_ID;

	public ArrayList<ClaferDependency> getClaferDependencies() {
		return this.claferDependencies;
	}

	public ArrayList<CodeDependency> getCodeDependencies() {
		return this.codeDependencies;
	}

	public int getNextID() {
		return this.nextID;
	}

	public String getValue() {
		return this.value;
	}
	
	public Boolean isDefaultAnswer() {
		return this.defaultAnswer == null ? false : this.defaultAnswer;
	}

	public void setClaferDependencies(final ArrayList<ClaferDependency> claferDependencies) {
		this.claferDependencies = claferDependencies;
	}

	public void setCodeDependencies(final ArrayList<CodeDependency> codeDependencies) {
		this.codeDependencies = codeDependencies;
	}

	public void setDefaultAnswer(final Boolean defaultAnswer) {
		this.defaultAnswer = defaultAnswer;
	}

	public void setNextID(final int prevID) {
		this.nextID = prevID;
	}

	public void setValue(final String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		//the combo viewer calls the toString() method so just display the value
		return this.value;
	}

	public ArrayList<UIDependency> getUiDependencies() {
		return uiDependencies;
	}

	public void setUiDependencies(ArrayList<UIDependency> uiDependenies) {
		this.uiDependencies = uiDependenies;
	}

	public boolean hasUiDependencies() {
		return uiDependencies != null && uiDependencies.size() > 0;
	}
	
	/**
	 * Retrieves the value from a specific UIDependency.
	 * 
	 * @param option The ui property you want to query.
	 * @return The option's value if available, else null.
	 */
	public String getUIDependency(String option) {
		if(!hasUiDependencies())
			return null;
		
		String value = null;
		for(UIDependency dep : uiDependencies) {
			if(dep.getOption().equals(option)) {
				value = dep.getValue();
			}
		}
		
		return value;
	}

	public Answer combineWith(Answer a) {
		Answer combined = new Answer();
		combined.setValue(this.value + " + " + a.getValue());
		
		ArrayList<CodeDependency> cpD = new ArrayList<CodeDependency>();
		if (this.codeDependencies != null) {
			cpD.addAll(this.codeDependencies);
		}
		if (a.getCodeDependencies() != null) {
			cpD.addAll(a.getCodeDependencies());
		}
		combined.setCodeDependencies(cpD);

		ArrayList<ClaferDependency> clD = new ArrayList<ClaferDependency>();
		if (this.claferDependencies != null) {
			clD.addAll(this.claferDependencies);
		}
		if (a.getClaferDependencies() != null) {
			clD.addAll(a.getClaferDependencies());
		}
		combined.setClaferDependencies(clD);
		
		return combined;
	}

}
