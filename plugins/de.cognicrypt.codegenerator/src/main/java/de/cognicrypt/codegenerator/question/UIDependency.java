/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.question;

/**
 *
 * This class provides an additional data container for answers that allows to give additional information to the UI in order to provide a UI that fits a specific questions. To
 * keep it as generic as possible, we use Strings to encode additional values.
 *
 * For example, it can be used to for checkbox groups where some values should be handled exclusively.
 *
 * @author Michael Reif
 */
public class UIDependency {

	private String option;
	private String value;

	public String getOption() {
		return this.option;
	}

	public void setOption(final String option) {
		this.option = option;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.option == null) ? 0 : this.option.hashCode());
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final UIDependency other = (UIDependency) obj;
		if (this.option == null) {
			if (other.option != null) {
				return false;
			}
		} else if (!this.option.equals(other.option)) {
			return false;
		}
		if (this.value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!this.value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UIDependency [option=" + this.option + ", value=" + this.value + "]";
	}
}
