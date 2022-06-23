/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dom4j.tree.DefaultText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import de.cognicrypt.codegenerator.generator.GeneratorClass;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.utils.DeveloperProject;
import de.cognicrypt.utils.FileUtils;
import de.cognicrypt.utils.XMLParser;

public class CrySLConfiguration extends Configuration {

	private final GeneratorClass template;

	public CrySLConfiguration(String pathOnDisk, GeneratorClass templateClass, Map<Question, Answer> constraints, String taskName, DeveloperProject developerProject) throws IOException {
		super(constraints, pathOnDisk, taskName, developerProject);
		this.template = templateClass;
	}

	@Override
	public File persistConf() throws IOException {
		File file = new File(getPath());
		
		XMLParser parser = new XMLParser(file);
		parser.createNewDoc();
		Element root = parser.createRootElement("task");
		root.setAttribute("description", this.taskName);
		
		if (this.getTemplateClass().getPackageName() != null) {
			parser.createChildElement(root, "Package").appendChild(parser.getDoc().createTextNode(this.getTemplateClass().getPackageName()));
		}
		
		Element imports = parser.createChildElement(root, "Imports");
		
		for (String importValue : this.getTemplateClass().getImports()) {
			parser.createChildElement(imports, "Import").appendChild(parser.getDoc().createTextNode(importValue));
		}
		
		parser.writeXML(); //Flush changes	
		return file;
	}

	@Override
	public List<String> getProviders() {
		throw new UnsupportedOperationException();
	}

	public GeneratorClass getTemplateClass() {
		return this.template;
	}
	
	public String getPath() {
		return this.developerProject.getProjectPath() + "/claferInstance.xml";
	}

	/**
	 * Deletes config file from hard disk.
	 */
	public void deleteConfFromDisk() {
		FileUtils.deleteFile(getPath());
	}
}
