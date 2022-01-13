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
import java.util.HashMap;
import java.util.List;

import de.cognicrypt.codegenerator.generator.GeneratorClass;
import de.cognicrypt.utils.DeveloperProject;

public class CrySLConfiguration extends Configuration {

	private final GeneratorClass template;

	public CrySLConfiguration(String pathOnDisk, GeneratorClass templateClass) throws IOException {
		super(new HashMap<>(), pathOnDisk);
		this.template = templateClass;
	}

	@Override
	public File persistConf() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getProviders() {
		throw new UnsupportedOperationException();
	}

	public GeneratorClass getTemplateClass() {
		return this.template;
	}
}
