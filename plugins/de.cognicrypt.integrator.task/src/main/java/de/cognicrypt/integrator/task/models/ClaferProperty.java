/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

/**
 *
 */
package de.cognicrypt.integrator.task.models;

import java.io.Serializable;

public class ClaferProperty implements Serializable {

	private static final long serialVersionUID = -5360875525100852395L;

	private String propertyName;
	private String propertyType;

	/**
	 * @param propertyName
	 * @param propertyType
	 */
	public ClaferProperty(final String propertyName, final String propertyType) {
		super();
		setPropertyName(propertyName);
		setPropertyType(propertyType);
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return the propertyType
	 */
	public String getPropertyType() {
		return this.propertyType;
	}

	/**
	 * @param propertyType the propertyType to set
	 */
	public void setPropertyType(final String propertyType) {
		this.propertyType = propertyType;
	}

	@Override
	public String toString() {
		return this.propertyName + " -> " + this.propertyType;
	}
}
