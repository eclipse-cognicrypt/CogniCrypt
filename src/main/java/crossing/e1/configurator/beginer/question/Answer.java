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

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import crossing.e1.configurator.utilities.Labels;

/**
 * @author Ram
 *
 */

public class Answer implements Labels {
	private ArrayList<Dependency> dependencies;
	private boolean hasDependencies = false;
	private String value;
	private String ref;
	private String operator;

	Answer(final Element answeritem) {
		if (answeritem.hasChildNodes()) {
			Element dependencies = null;
			if (answeritem.getElementsByTagName(Labels.DEPENDENCIES).getLength() > 0) {
				dependencies = (Element) answeritem.getElementsByTagName(Labels.DEPENDENCIES).item(0);
				NodeList dependenciesList = null;
				if (dependencies.hasChildNodes()) {
					setDependencies(true);
					dependenciesList = dependencies.getElementsByTagName(Labels.DEPENDENCY);
					for (int depIndex = 0; depIndex < dependenciesList.getLength(); depIndex++) {
						final Element dependency = (Element) dependenciesList.item(depIndex);
						getDependencies().add(new Dependency(dependency.getAttribute(Labels.VALUE),
								dependency.getAttribute(Labels.OPERATOR), dependency.getAttribute(Labels.REF_CLAFER),
								Boolean.parseBoolean(dependency.getAttribute(Labels.IS_GROUP))));
					}
				}
			}
		}
		setOperator(answeritem.getAttribute(Labels.OPERATOR));
		setRef(answeritem.getAttribute(Labels.REF));
		setValue(answeritem.getAttribute(Labels.VALUE));
	}

	/**
	 * @return the dependencies
	 */
	public ArrayList<Dependency> getDependencies() {
		if (this.dependencies == null) {
			this.dependencies = new ArrayList<Dependency>();
		}
		return this.dependencies;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return this.operator;
	}

	/**
	 * @return the ref
	 */
	public String getRef() {
		return this.ref;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @return the hasDependencies
	 */
	public boolean hasDependencies() {
		return this.hasDependencies;
	}

	/**
	 * @param dependencies
	 *            the dependencies to set
	 */
	public void setDependencies(final ArrayList<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * @param hasDependencies
	 *            the hasDependencies to set
	 */
	public void setDependencies(final boolean hasDependencies) {
		this.hasDependencies = hasDependencies;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(final String operator) {
		this.operator = operator;
	}

	/**
	 * @param ref
	 *            the ref to set
	 */
	public void setRef(final String ref) {
		this.ref = ref;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

}
