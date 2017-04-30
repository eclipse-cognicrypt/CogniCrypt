/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
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

public class CodeDependency {

	private String option;
	private String value;

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CodeDependency)) {
			return false;
		} else {
			final CodeDependency comp = (CodeDependency) obj;
			return comp.getOption().equals(getOption()) && comp.getValue().equals(getValue());
		}
	}

	public String getOption() {
		return this.option;
	}

	public String getValue() {
		return this.value;
	}

	public void setOption(final String option) {
		this.option = option;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Code Dependency [option=" + this.option + ", value=" + this.value + "]";
	}

}
