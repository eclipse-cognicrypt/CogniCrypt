/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

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
