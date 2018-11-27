package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.cognicrypt.codegenerator.generator.CodeGenCrySLRule;


public class CrySLConfiguration extends Configuration {

	private final List<List<CodeGenCrySLRule>> rules;

	public CrySLConfiguration(List<List<CodeGenCrySLRule>> rules, Map<CodeGenCrySLRule, ?> constraints, String pathOnDisk) {
		super(constraints, pathOnDisk);
		this.rules = rules;
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

	public List<List<CodeGenCrySLRule>> getRules() {
		return rules;
	}
}
