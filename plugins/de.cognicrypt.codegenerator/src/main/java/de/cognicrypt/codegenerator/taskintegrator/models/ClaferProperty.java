/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.models;

import java.io.Serializable;

public class ClaferProperty implements Serializable {

	private static final long serialVersionUID = -5360875525100852395L;

	private String propertyName;
	private String propertyType;

	/**
	 * @param propertyName
	 * @param propertyType
	 */
	public ClaferProperty(String propertyName, String propertyType) {
		super();
		this.setPropertyName(propertyName);
		this.setPropertyType(propertyType);
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName
	 *        the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return the propertyType
	 */
	public String getPropertyType() {
		return propertyType;
	}

	/**
	 * @param propertyType
	 *        the propertyType to set
	 */
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	@Override
	public String toString() {
		return propertyName + " -> " + propertyType;
	}
}
