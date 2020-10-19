/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.controllers;

import de.cognicrypt.codegenerator.question.CodeDependency;

public class XSLPageLabelProvider extends ClaferModelLabelProvider {

	@Override
	public String getText(final Object inputElement) {
		if (inputElement instanceof CodeDependency) {
			final CodeDependency codeDependency = (CodeDependency) inputElement;
			return codeDependency.getOption() + " = " + codeDependency.getValue();
		}
		return super.getText(inputElement);
	}
}
