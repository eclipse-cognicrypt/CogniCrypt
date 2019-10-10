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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLForbiddenMethod;
import crypto.rules.CryptSLMethod;
import crypto.rules.CryptSLObject;
import crypto.rules.CryptSLPredicate;
import crypto.rules.CryptSLRule;
import crypto.rules.StateMachineGraph;

public class CodeGenCrySLRule extends CryptSLRule {

	private static final long serialVersionUID = -7488186084564628280L;
	private List<CryptSLMethod> requiredMethods;
	private List<CodeGenCrySLObject> requiredPars;
	private CryptSLObject requiredRetObj;

	public CodeGenCrySLRule(String _className, List<Entry<String, String>> defObjects, List<CryptSLForbiddenMethod> _forbiddenMethods, StateMachineGraph _usagePattern, List<ISLConstraint> _constraints, List<CryptSLPredicate> _predicates, List<CodeGenCrySLObject> pars, CryptSLObject reqRet) {
		super(_className, defObjects, _forbiddenMethods, _usagePattern, _constraints, _predicates);
		requiredPars = pars;
		requiredRetObj = reqRet;
	}

	public CodeGenCrySLRule(CryptSLRule rule, List<CodeGenCrySLObject> pars, CryptSLObject reqRet) {
		this(rule.getClassName(), rule.getObjects(), rule.getForbiddenMethods(), rule.getUsagePattern(), rule.getConstraints(), rule.getPredicates(), pars, reqRet);
	}

	public boolean addConstraint(ISLConstraint constraint) {
		return constraints.add(constraint);
	}

	public boolean addForbiddenMethod(CryptSLMethod method) {
		return forbiddenMethods.add(new CryptSLForbiddenMethod(method, false));
	}

	public boolean addRequiredMethod(CryptSLMethod method) {
		if (requiredMethods == null) {
			requiredMethods = new ArrayList<CryptSLMethod>();
		}
		return requiredMethods.add(method);
	}

	public List<CodeGenCrySLObject> getRequiredPars() {
		return requiredPars;
	}

	public CryptSLObject getRequiredRetObj() {
		return requiredRetObj;
	}

}