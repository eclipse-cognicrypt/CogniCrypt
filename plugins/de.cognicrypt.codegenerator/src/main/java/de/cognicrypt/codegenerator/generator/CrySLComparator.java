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

import java.util.Comparator;

import crypto.rules.CrySLRule;
import de.cognicrypt.utils.CrySLUtils;

public class CrySLComparator implements Comparator<CrySLRule> {

	RuleDependencyTree rdt;

	public CrySLComparator() {
		rdt = new RuleDependencyTree(CrySLUtils.readCrySLRules());
	}

	@Override
	public int compare(CrySLRule left, CrySLRule right) {
		if (rdt.hasPath(left, right)) {
			return -1;
		} else if (rdt.hasPath(right, left)) {
			return 1;
		} else {
			return 0;
		}
	}

}
