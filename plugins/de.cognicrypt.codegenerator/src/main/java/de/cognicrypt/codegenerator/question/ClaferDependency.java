/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.question;

public class ClaferDependency {

	private String algorithm;
	private String operand;
	private String value;
	private String operator;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ClaferDependency)) {
			return false;
		}
		final ClaferDependency other = (ClaferDependency) obj;
		if (this.algorithm == null) {
			if (other.algorithm != null) {
				return false;
			}
		} else if (!this.algorithm.equals(other.algorithm)) {
			return false;
		}
		if (this.operand == null) {
			if (other.operand != null) {
				return false;
			}
		} else if (!this.operand.equals(other.operand)) {
			return false;
		}
		if (this.operator == null) {
			if (other.operator != null) {
				return false;
			}
		} else if (!this.operator.equals(other.operator)) {
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

	public String getAlgorithm() {
		return this.algorithm;
	}

	public String getOperand() {
		return this.operand;
	}

	public String getOperator() {
		return this.operator;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.algorithm == null) ? 0 : this.algorithm.hashCode());
		result = prime * result + ((this.operand == null) ? 0 : this.operand.hashCode());
		result = prime * result + ((this.operator == null) ? 0 : this.operator.hashCode());
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	public void setAlgorithm(final String algorithm) {
		this.algorithm = algorithm;
	}

	public void setOperand(final String operand) {
		this.operand = operand;
	}

	public void setOperator(final String operator) {
		this.operator = operator;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "AnswerDependency [algorithm=" + this.algorithm + ", operand=" + this.operand + ", value=" + this.value + ", operator=" + this.operator + "]";
	}
}
