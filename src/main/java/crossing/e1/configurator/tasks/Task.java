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
package crossing.e1.configurator.tasks;


public class Task {

	private String name;
	private String description;
	private String taskDescription;
	private String modelFile;
	private String xmlFile;
	private boolean isSelected;
	private String additionalResources;

	public String getAdditionalResources() {
		return this.additionalResources;
	}

	public String getDescription() {
		return this.description;
	}

	public String getTaskDescription() {
		return this.taskDescription;
	}  
	
	public String getModelFile() {
		return this.modelFile;
	}

	public String getName() {
		return this.name;
	}

	public String getXmlFile() {
		return this.xmlFile;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public void setAdditionalResources(final String additionalResources) {
		this.additionalResources = additionalResources;
	}

	public void setDescription(final String description) {
		this.description = description;
	}
	
	public void setTaskDescription(final String taskDescription) {
		this.taskDescription = taskDescription;
	}  

	public void setModelFile(final String modelFile) {
		this.modelFile = modelFile;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSelected(final boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setXmlFile(final String xmlFile) {
		this.xmlFile = xmlFile;
	}

}
