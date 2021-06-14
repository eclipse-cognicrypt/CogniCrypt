/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl;

import java.util.List;

public class CrySLCodeGenerator implements RuleContext, BeforeRuleContext {

	private RuleGenConfig last = null;
	private List<RuleGenConfig> ruleConfigs;
	private String customMain;

	private CrySLCodeGenerator() {}

	public static BeforeRuleContext getInstance() {
		return new CrySLCodeGenerator();
	}

	public CrySLCodeGenerator includeClass(String rule) {
		resolveLast();
		last = new RuleGenConfig(rule);
		return this;
	}

	public CrySLCodeGenerator addParameter(Object par, String variableName) {
		last.addParameter(par);
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
	public CrySLCodeGenerator setCustomMain(String customMain) {
		this.customMain = customMain;
		return this;
	}
}
