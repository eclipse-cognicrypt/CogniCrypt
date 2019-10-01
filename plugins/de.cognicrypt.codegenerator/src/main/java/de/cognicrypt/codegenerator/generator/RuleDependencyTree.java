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

import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import de.cognicrypt.utils.Utils;

public class RuleDependencyTree {

	List<CryptSLRule> cryslRules;

	List<CryptSLRule> nodes;

	Map<String, Set<CryptSLRule>> ruleToPred;
	List<Entry<Entry<CryptSLRule, CryptSLRule>, String>> edges;

	public RuleDependencyTree(List<CryptSLRule> rules) {
		nodes = new ArrayList<CryptSLRule>();
		edges = new ArrayList<Entry<Entry<CryptSLRule, CryptSLRule>, String>>();
		ruleToPred = new HashMap<String, Set<CryptSLRule>>();

		for (CryptSLRule rule : rules) {
			nodes.add(rule);
			for (CryptSLPredicate pred : rule.getPredicates()) {
				if (!pred.isNegated()) {
					Set<CryptSLRule> predsForRule = ruleToPred.get(pred.getPredName());
					if (predsForRule == null) {
						ruleToPred.put(pred.getPredName(), new HashSet<CryptSLRule>());
						predsForRule = ruleToPred.get(pred.getPredName());
					}
					predsForRule.add(rule);
				}
			}
		}

		for (CryptSLRule rule : rules) {
			for (CryptSLPredicate pred : rule.getRequiredPredicates()) {
				Set<CryptSLRule> allPreds = ruleToPred.get(pred.getPredName());
				if (allPreds == null) {
					continue;
				}
				for (CryptSLRule predicate : allPreds) {
					if (predicate != null) {
						Entry<CryptSLRule, CryptSLRule> rulePair = new SimpleEntry<CryptSLRule, CryptSLRule>(predicate, rule);
						SimpleEntry<Entry<CryptSLRule, CryptSLRule>, String> predRuleEntry = new SimpleEntry<Entry<CryptSLRule, CryptSLRule>, String>(rulePair, pred.getPredName());
						edges.add(predRuleEntry);
					}
				}
			}
		}
	}

	public void toDotFile(String rulesFolder) {
		StringBuilder dotFileSB = new StringBuilder("digraph F {\n");
		for (Entry<Entry<CryptSLRule, CryptSLRule>, String> edge : edges) {
			Entry<CryptSLRule, CryptSLRule> nodes = edge.getKey();
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

	public boolean hasPath(CryptSLRule start, CryptSLRule goal) {
		Set<CryptSLRule> visited = new HashSet<CryptSLRule>();
		visited.add(start);
		Set<CryptSLRule> toBeVisited = new HashSet<CryptSLRule>();
		toBeVisited.add(start);

		while (visited.size() != nodes.size()) {
			Set<CryptSLRule> rights = new HashSet<CryptSLRule>();
			for (CryptSLRule node : toBeVisited) {
				for (Entry<Entry<CryptSLRule, CryptSLRule>, String> edge : edges) {
					Entry<CryptSLRule, CryptSLRule> nodes = edge.getKey();
					CryptSLRule right = nodes.getValue();
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

	public boolean hasDirectPath(CryptSLRule start, CryptSLRule goal) {
		return getOutgoingEdges(start).stream().anyMatch(entry -> entry.getKey().getValue().equals(goal)) || start.getPredicates().stream().anyMatch(predicate -> {
			String predType = ((CryptSLObject) predicate.getParameters().get(0)).getJavaType();
			String goalType = goal.getClassName();
			return Utils.isSubType(predType, goalType) || Utils.isSubType(goalType, predType);
		});
	}

	public List<Entry<Entry<CryptSLRule, CryptSLRule>, String>> getOutgoingEdges(CryptSLRule node) {
		return edges.stream().filter(entry -> entry.getKey().getKey().equals(node)).collect(Collectors.toList());
	}
}
