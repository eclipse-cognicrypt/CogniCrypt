/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.models;

import java.io.Serializable;

public class ClaferConstraint implements Serializable {

	private static final long serialVersionUID = 466282011383992518L;

	private String constraint;

	public ClaferConstraint(String constraint) {
		this.constraint = constraint;
	}

	public ClaferConstraint() {
		this("");
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	@Override
	public String toString() {
		return "[ " + getConstraint() + " ]";
	}

}
