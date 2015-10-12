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

package crossing.e1.xml.export;

/**
 * @author Ram
 *
 */
public class Dependency {

	String value;
	String operator;
	String refClafer;
	boolean isGroup;

	Dependency(String value, String operator, String refClafer, boolean isGroup) {
		setGroup(isGroup);
		setOperator(operator);
		setRefClafer(refClafer);
		setValue(value);
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @return the refClafer
	 */
	public String getRefClafer() {
		return refClafer;
	}

	/**
	 * @param refClafer
	 *            the refClafer to set
	 */
	public void setRefClafer(String refClafer) {
		this.refClafer = refClafer;
	}

	/**
	 * @return the isGroup
	 */
	public boolean isGroup() {
		return isGroup;
	}

	/**
	 * @param isGroup
	 *            the isGroup to set
	 */
	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

}
