/**
 * Copyright 2015 Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package crossing.e1.configurator.beginer.question;

/**
 * @author Ram
 *
 */
public class Dependency {

	private String value;
	private String operator;
	private String refClafer;
	private boolean isGroup;

	Dependency(final String value, final String operator, final String refClafer, final boolean isGroup) {
		setGroup(isGroup);
		setOperator(operator);
		setRefClafer(refClafer);
		setValue(value);
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return this.operator;
	}

	/**
	 * @return the refClafer
	 */
	public String getRefClafer() {
		return this.refClafer;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @return the isGroup
	 */
	public boolean isGroup() {
		return this.isGroup;
	}

	/**
	 * @param isGroup
	 *            the isGroup to set
	 */
	public void setGroup(final boolean isGroup) {
		this.isGroup = isGroup;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(final String operator) {
		this.operator = operator;
	}

	/**
	 * @param refClafer
	 *            the refClafer to set
	 */
	public void setRefClafer(final String refClafer) {
		this.refClafer = refClafer;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

}
