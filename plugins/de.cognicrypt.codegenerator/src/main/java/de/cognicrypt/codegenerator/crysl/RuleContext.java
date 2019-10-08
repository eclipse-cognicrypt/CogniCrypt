package de.cognicrypt.codegenerator.crysl;


public interface RuleContext {
	public RuleContext addParameter(Object par, String variableName);
	
	public BeforeRuleContext addReturnObject(Object par);
	
	public RuleContext includeClass(String rule);
	
	public boolean generate();
}
