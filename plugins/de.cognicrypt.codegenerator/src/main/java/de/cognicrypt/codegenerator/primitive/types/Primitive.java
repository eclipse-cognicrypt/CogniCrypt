/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.types;

public class Primitive {

	private String name;
	private String xmlFile;
	private boolean isSelected;
	private String xslFile;

	public String getName() {
		return this.name;
	}

	public String getXslFile() {
		return this.xslFile;
	}

	public String getXmlFile() {
		return this.xmlFile;
	}

	public void setXmlFile(String xml) {
		this.xmlFile = xml;
	}

	public void setXslFile(String xsl) {
		this.xslFile = xsl;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
