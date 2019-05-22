package de.cognicrypt.codegenerator.crysl;
import java.util.List;

public class CrySLCodeGenerator {

	private RuleGenConfig last = null;
	private List<RuleGenConfig> ruleConfigs;
	
	private CrySLCodeGenerator() {
	}
	
	public static CrySLCodeGenerator getInstance() {
		return new CrySLCodeGenerator();
	}
	
	public CrySLCodeGenerator considerCrySLRule(String rule) {
		resolveLast();
		last = new RuleGenConfig(rule);
		return this;
	}

	public CrySLCodeGenerator addParameter(Object par) {
		last.addParameter(par);
		return this;
	}
	
	public CrySLCodeGenerator addReturnObject(Object par) {
		last.setReturnObject(par);
		resolveLast();
		return this;
	}
	
	public boolean generate() {
		resolveLast();
		return true;
	}
	
	private void resolveLast() {
		if (last != null) {
			ruleConfigs.add(last);
			last = null;
		}
	}
}
