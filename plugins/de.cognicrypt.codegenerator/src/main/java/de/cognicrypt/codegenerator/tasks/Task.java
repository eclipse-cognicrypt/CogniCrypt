/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.tasks;

import de.cognicrypt.core.Constants.CodeGenerators;

public class Task {

	private String name;
	private String description;
	private String taskDescription;
	private String image;
	private CodeGenerators codeGen;

	private String additionalResources;
	private String questionsJSONFile;

	private String modelFile;
	private String codeTemplate;

	private boolean isSelected;

	public String getAdditionalResources() {
		return this.additionalResources;
	}

	public String getDescription() {
		return this.description;
	}

	public String getTaskDescription() {
		return this.taskDescription;
	}

	public String getModelFile() {
		return this.modelFile;
	}

	public String getName() {
		return this.name;
	}

	public String getQuestionsJSONFile() {
		return this.questionsJSONFile;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setAdditionalResources(final String additionalResources) {
		this.additionalResources = additionalResources;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setTaskDescription(final String taskDescription) {
		this.taskDescription = taskDescription;
	}

	public void setModelFile(final String modelFile) {
		this.modelFile = modelFile;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSelected(final boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setQuestionsJSONFile(final String questionsJSONFile) {
		this.questionsJSONFile = questionsJSONFile;
	}

	public String getCodeTemplate() {
		return this.codeTemplate;
	}

	public void setCodeTemplate(final String codeTemplate) {
		this.codeTemplate = codeTemplate;
	}

	public CodeGenerators getCodeGen() {
		return codeGen;
	}

	public void setCodeGen(CodeGenerators codeGen) {
		this.codeGen = codeGen;
	}

}
