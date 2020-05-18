/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.generator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import crypto.rules.CrySLObject;
import crypto.rules.CrySLPredicate;
import crypto.rules.CrySLRule;
import de.cognicrypt.utils.Utils;

public class RuleDependencyTree {

	List<CrySLRule> cryslRules;

	List<CrySLRule> nodes;

	Map<String, Set<CrySLRule>> ruleToPred;
	List<Entry<Entry<CrySLRule, CrySLRule>, String>> edges;

	public RuleDependencyTree(List<CrySLRule> rules) {
		nodes = new ArrayList<CrySLRule>();
		edges = new ArrayList<Entry<Entry<CrySLRule, CrySLRule>, String>>();
		ruleToPred = new HashMap<String, Set<CrySLRule>>();

		for (CrySLRule rule : rules) {
			nodes.add(rule);
			for (CrySLPredicate pred : rule.getPredicates()) {
				if (!pred.isNegated()) {
					Set<CrySLRule> predsForRule = ruleToPred.get(pred.getPredName());
					if (predsForRule == null) {
						ruleToPred.put(pred.getPredName(), new HashSet<CrySLRule>());
						predsForRule = ruleToPred.get(pred.getPredName());
					}
					predsForRule.add(rule);
				}
			}
		}

		for (CrySLRule rule : rules) {
			for (CrySLPredicate pred : rule.getRequiredPredicates()) {
				Set<CrySLRule> allPreds = ruleToPred.get(pred.getPredName());
				if (allPreds == null) {
					continue;
				}
				for (CrySLRule predicate : allPreds) {
					if (predicate != null) {
						Entry<CrySLRule, CrySLRule> rulePair = new SimpleEntry<CrySLRule, CrySLRule>(predicate, rule);
						SimpleEntry<Entry<CrySLRule, CrySLRule>, String> predRuleEntry = new SimpleEntry<Entry<CrySLRule, CrySLRule>, String>(rulePair, pred.getPredName());
						edges.add(predRuleEntry);
					}
				}
			}
		}
	}

	public void toDotFile(String rulesFolder) {
		StringBuilder dotFileSB = new StringBuilder("digraph F {\n");
		for (Entry<Entry<CrySLRule, CrySLRule>, String> edge : edges) {
			Entry<CrySLRule, CrySLRule> nodes = edge.getKey();
			dotFileSB.append(nodes.getValue());
			dotFileSB.append(" -> ");
			dotFileSB.append(nodes.getKey());
			dotFileSB.append(" [ label=\"depends on\"];\n");
		}
		dotFileSB.append("}");
		File dotFile = new File(rulesFolder + "\\crysldependencies.dot");
		try {
			dotFile.createNewFile();
			Files.write(dotFile.toPath(), dotFileSB.toString().getBytes("UTF-8"));
		} catch (IOException e) {

		}
	}
	/*
	 * digraph F { pre_init[shape = rarrow] 2[shape = doublecircle] pre_init -> 0 [label="javax.crypto.KeyGenerator.getInstan"]; 0 -> 1
	 * [label="javax.crypto.KeyGenerator.init(int)"]; 1 -> 2 [label="javax.crypto.KeyGenerator.generateK"]; 0 -> 2 [label="javax.crypto.KeyGenerator.generateK"]; }
	 */

	public boolean hasPath(CrySLRule start, CrySLRule goal) {
		Set<CrySLRule> visited = new HashSet<CrySLRule>();
		visited.add(start);
		Set<CrySLRule> toBeVisited = new HashSet<CrySLRule>();
		toBeVisited.add(start);

		while (visited.size() != nodes.size()) {
			Set<CrySLRule> rights = new HashSet<CrySLRule>();
			for (CrySLRule node : toBeVisited) {
				for (Entry<Entry<CrySLRule, CrySLRule>, String> edge : edges) {
					Entry<CrySLRule, CrySLRule> nodes = edge.getKey();
					CrySLRule right = nodes.getValue();
					if (nodes.getKey().equals(node) && !visited.contains(right)) {
						rights.add(right);
					}
				}
				if (rights.contains(goal)) {
					return true;
				}
			}
			if (rights.isEmpty()) {
				break;
			}
			toBeVisited = rights;
			visited.addAll(rights);
		}

		return false;
	}

	public boolean hasDirectPath(CrySLRule start, CrySLRule goal) {
		return getOutgoingEdges(start).stream().anyMatch(entry -> entry.getKey().getValue().equals(goal)) || start.getPredicates().stream().anyMatch(predicate -> {
			String predType = ((CrySLObject) predicate.getParameters().get(0)).getJavaType();
			String goalType = goal.getClassName();
			return Utils.isSubType(predType, goalType) || Utils.isSubType(goalType, predType);
		});
	}

	public List<Entry<Entry<CrySLRule, CrySLRule>, String>> getOutgoingEdges(CrySLRule node) {
		return edges.stream().filter(entry -> entry.getKey().getKey().equals(node)).collect(Collectors.toList());
	}
}
