package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.cognicrypt.codegenerator.generator.CodeGenCrySLRule;
import de.cognicrypt.codegenerator.generator.GeneratorClass;


public class CrySLConfiguration extends Configuration {

	private final List<CodeGenCrySLRule> rules;
	private final GeneratorClass template;

	public CrySLConfiguration(List<CodeGenCrySLRule> rules, Map<CodeGenCrySLRule, ?> constraints, String pathOnDisk, GeneratorClass templateClass) {
		super(constraints, pathOnDisk);
		this.rules = rules;
		this.template = templateClass;
	}

	@Override
	public File persistConf() throws IOException {
		return null;
	}

	@Override
	public List<String> getProviders() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CodeGenCrySLRule> getRules() {
		return rules;
	}
	
	public GeneratorClass getTemplateClass() {
		return this.template;
	}
}
