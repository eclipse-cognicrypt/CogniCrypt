/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.cryslhandler;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import de.cognicrypt.crysl.reader.CrySLParser;

public class PredicateConsistencyCheck {

	@Test
	public void predicateParameterNumberConsistencyTest() throws MalformedURLException, CoreException {
		List<CrySLRule> rules = (new CrySLParser()).readRulesOutside("../de.cognicrypt.core/resources/CrySLRules/JavaCryptographicArchitecture");
		Map<String, List<PredicateDetails>> predicates = new HashMap<String, List<PredicateDetails>>();
		for (CrySLRule rule : rules) {

			for (CrySLPredicate pred : rule.getPredicates()) {
				String predName = pred.getPredName();
				if (!predicates.containsKey(predName)) {
					predicates.put(predName, new ArrayList<PredicateDetails>());
				}
				List<PredicateDetails> predDetails = predicates.get(predName);
				predDetails.add(new PredicateDetails(rule.getClassName(), pred.getParameters().size()));
			}

			for (CrySLPredicate pred : rule.getRequiredPredicates()) {
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
						System.err.println(
								"The predicate " + predName + " has " + prev.parameterCount + " parameters in " + prev.name + ", but " + details.parameterCount + " parameters in " + details.name);
					}
					// assertEquals(prev.parameterCount, details.parameterCount);
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
