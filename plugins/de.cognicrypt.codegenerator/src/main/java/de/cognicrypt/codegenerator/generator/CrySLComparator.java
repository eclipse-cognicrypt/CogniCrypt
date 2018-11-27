package de.cognicrypt.codegenerator.generator;

import java.util.Comparator;

import crypto.rules.CryptSLRule;
import de.cognicrypt.utils.Utils;


public class CrySLComparator implements Comparator<CryptSLRule> {
	RuleDependencyTree rdt;
	
	public CrySLComparator() {
		rdt  = new RuleDependencyTree(Utils.readCrySLRules());
	}
	
	@Override
	public int compare(CryptSLRule left, CryptSLRule right) {
		if (rdt.hasPath(left, right)) {
			return -1;
		} else if (rdt.hasPath(right, left)) {
			return 1;
		} else {
			return 0;
		}
	}

}
