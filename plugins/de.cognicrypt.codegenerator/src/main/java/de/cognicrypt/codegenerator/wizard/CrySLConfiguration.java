package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cognicrypt.codegenerator.generator.CodeGenCrySLRule;
import de.cognicrypt.codegenerator.generator.GeneratorClass;


public class CrySLConfiguration extends Configuration {

	private final GeneratorClass template;

	public CrySLConfiguration(String pathOnDisk, GeneratorClass templateClass) {
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
