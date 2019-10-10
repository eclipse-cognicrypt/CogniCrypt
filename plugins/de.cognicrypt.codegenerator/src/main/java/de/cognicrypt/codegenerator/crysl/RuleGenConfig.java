/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl;

import java.util.List;

public class RuleGenConfig {

	private String rule;
	private List<Object> parameters;
	private Object returnObject;

	public RuleGenConfig(String rule2) {
		this.rule = rule2;
	}

	public List<Object> getParameters() {
		return parameters;
	}

	public void addParameter(Object parameter) {
		this.parameters.add(parameter);
	}

	public Object getReturnObject() {
		return returnObject;
	}

	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	public String getRule() {
		return rule;
	}

}
