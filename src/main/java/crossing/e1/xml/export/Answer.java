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

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import crossing.e1.configurator.Lables;

/**
 * @author Ram
 *
 */

public class Answer implements Lables{
	private ArrayList<Dependency> dependencies;
	boolean hasDependencies = false;
	private String value;
	private String ref;
	private String operator;

	Answer(Element answeritem) {
		if (answeritem.hasChildNodes()) {
			Element dependencies = null;
			if (answeritem.getElementsByTagName(Lables.DEPENDENCIES).getLength() > 0) {
				dependencies = (Element) answeritem.getElementsByTagName(Lables.DEPENDENCIES).item(0);
				NodeList dependenciesList = null;
				if (dependencies.hasChildNodes()) {
					setDependencies(true);
					dependenciesList = dependencies
							.getElementsByTagName(Lables.DEPENDENCY);
					for (int depIndex = 0; depIndex < dependenciesList
							.getLength(); depIndex++) {
						Element dependency = (Element) dependenciesList
								.item(depIndex);
						this.getDependencies().add(
								new Dependency(
										dependency.getAttribute(Lables.VALUE),
										dependency.getAttribute(Lables.OPERATOR),
										dependency.getAttribute(Lables.REF_CLAFER),
										Boolean.parseBoolean(dependency
												.getAttribute(Lables.IS_GROUP))));
					}
				}
			}
		}
		this.setOperator(answeritem.getAttribute(Lables.OPERATOR));
		this.setRef(answeritem.getAttribute(Lables.REF));
		this.setValue(answeritem.getAttribute(Lables.VALUE));
	}

	/**
	 * @return the hasDependencies
	 */
	public boolean hasDependencies() {
		return hasDependencies;
	}

	/**
	 * @param hasDependencies
	 *            the hasDependencies to set
	 */
	public void setDependencies(boolean hasDependencies) {
		this.hasDependencies = hasDependencies;
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
	 * @return the ref
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * @param ref
	 *            the ref to set
	 */
	public void setRef(String ref) {
		this.ref = ref;
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
	 * @return the dependencies
	 */
	public ArrayList<Dependency> getDependencies() {
		if (dependencies == null)
			dependencies = new ArrayList<Dependency>();
		return dependencies;
	}

	/**
	 * @param dependencies
	 *            the dependencies to set
	 */
	public void setDependencies(ArrayList<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

}
