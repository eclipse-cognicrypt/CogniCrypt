package crossing.e1.configurator.CryptSL;

import java.util.List;

import crypto.rules.StateMachineGraph;
import de.darmstadt.tu.crossing.cryptSL.Method;

public class CryptSLRule {

	private final String className;
	
	private List<Method> forbiddenMethods;
	
	private StateMachineGraph usagePattern;
	
	public CryptSLRule(String _className) {
		className = _className;
		usagePattern = null;
	}
	
	
}
