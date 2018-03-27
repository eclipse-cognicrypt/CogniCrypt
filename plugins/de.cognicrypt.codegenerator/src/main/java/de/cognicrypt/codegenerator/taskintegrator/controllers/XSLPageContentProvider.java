package de.cognicrypt.codegenerator.taskintegrator.controllers;

import de.cognicrypt.codegenerator.question.CodeDependency;

public class XSLPageContentProvider extends ClaferModelContentProvider {

	@Override
	public Object[] getChildren(Object inputElement) {
		// code dependencies do not have children
		if (inputElement instanceof CodeDependency) {
			return null;
		}

		return super.getChildren(inputElement);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// check if the input is a list
		if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}

		// now, this is unlikely
		return super.getElements(inputElement);
	}

	@Override
	public boolean hasChildren(Object inputElement) {
		if (inputElement instanceof CodeDependency) {
			return false;
		}

		return super.hasChildren(inputElement);
	}

}
