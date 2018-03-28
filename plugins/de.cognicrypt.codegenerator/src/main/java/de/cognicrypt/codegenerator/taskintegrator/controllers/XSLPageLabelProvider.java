package de.cognicrypt.codegenerator.taskintegrator.controllers;

import de.cognicrypt.codegenerator.question.CodeDependency;

public class XSLPageLabelProvider extends ClaferModelLabelProvider {

	@Override
	public String getText(Object inputElement) {
		if (inputElement instanceof CodeDependency) {
			CodeDependency codeDependency = (CodeDependency) inputElement;
			return codeDependency.getOption() + " = " + codeDependency.getValue();
		}
		return super.getText(inputElement);
	}
}
