package de.cognicrypt.cryslhandler;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import de.cognicrypt.crysl.reader.CrySLReaderUtils;

public class PredicateConsistencyCheck {

	@Test
	public void predicateParameterNumberConsistencyTest() {
		List<CryptSLRule> rules = CrySLReaderUtils.readRuleFromBinaryFiles("../de.cognicrypt.core/resources/CrySLRules");
		Map<String, List<PredicateDetails>> predicates = new HashMap<String, List<PredicateDetails>>();
		for (CryptSLRule rule : rules) {

			for (CryptSLPredicate pred : rule.getPredicates()) {
				String predName = pred.getPredName();
				if (!predicates.containsKey(predName)) {
					predicates.put(predName, new ArrayList<PredicateDetails>());
				}
				List<PredicateDetails> predDetails = predicates.get(predName);
				predDetails.add(new PredicateDetails(rule.getClassName(), pred.getParameters().size()));
			}

			for (CryptSLPredicate pred : rule.getRequiredPredicates()) {
				String predName = pred.getPredName();
				if (!predicates.containsKey(predName)) {
					predicates.put(predName, new ArrayList<PredicateDetails>());
				}
				List<PredicateDetails> predDetails = predicates.get(predName);
				predDetails.add(new PredicateDetails(rule.getClassName(), pred.getParameters().size()));
			}

			for (String predName : predicates.keySet()) {
				PredicateDetails prev = null;
				for (PredicateDetails details : predicates.get(predName)) {
					if (prev == null) {
						prev = details;
						continue;
					}
					if (prev.parameterCount != details.parameterCount) {
						System.err.println("There is a predicate mismatch.");
						System.err.println("The predicate " + predName + " has " + prev.parameterCount + " parameters in " + prev.name + ", but " + details.parameterCount + " parameters in " + details.name);
					}
					assertEquals(prev.parameterCount, details.parameterCount);
				}
			}
		}
	}

	class PredicateDetails {

		final private String name;
		final private int parameterCount;

		public PredicateDetails(String ruleName, int numberOfParameters) {
			this.name = ruleName;
			this.parameterCount = numberOfParameters;
		}
	}
}
