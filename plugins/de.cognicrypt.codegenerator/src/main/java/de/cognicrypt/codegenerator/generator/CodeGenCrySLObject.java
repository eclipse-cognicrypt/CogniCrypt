/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.generator;

import crypto.rules.CrySLObject;

public class CodeGenCrySLObject extends CrySLObject {

	private final String crySLVariable;
	private String method = "";
	private int pos = -1;

	public CodeGenCrySLObject(String name, String type, String crySLVariable) {
		super(name, type);
		this.crySLVariable = crySLVariable;
	}

	public CodeGenCrySLObject(CrySLObject obj, String crySLVariable) {
		this(obj.getVarName(), obj.getJavaType(), crySLVariable);
	}

	public String getMethod() {
		return this.method;
	}

	public int getPosition() {
		return this.pos;
	}

	public void setMethod(String methodName, int position) {
		this.method = methodName;
		this.pos = position;
	}

	public String getCrySLVariable() {
		return this.crySLVariable;
	}

}
