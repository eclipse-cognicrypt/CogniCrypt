package de.cognicrypt.codegenerator.crysl;

public interface BeforeRuleContext {

	public RuleContext includeClass(String rule);

	public boolean generate();
}
