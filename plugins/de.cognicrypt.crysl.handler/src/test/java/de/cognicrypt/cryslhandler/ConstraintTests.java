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

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CrySLArithmeticConstraint;
import crypto.rules.CrySLArithmeticConstraint.ArithOp;
import crypto.rules.CrySLComparisonConstraint;
import crypto.rules.CrySLComparisonConstraint.CompOp;
import crypto.rules.CrySLConstraint;
import crypto.rules.CrySLConstraint.LogOps;
import crypto.rules.CrySLLiteral;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLValueConstraint;
import de.cognicrypt.crysl.reader.CrySLParser;

public class ConstraintTests {

	private static CrySLParser csmr;

	@BeforeClass
	public static void setup() throws MalformedURLException {
		csmr = new CrySLParser();
	}

	private CrySLRule readRuleFromFuleName(String ruleName) {
		return csmr.readRule(new File("src/test/resources/" + ruleName + ".crysl"));
	}

	@Test
	public void inConstraintTest() {
		CrySLRule rule = readRuleFromFuleName("InConstraintTestRule");
		for (ISLConstraint constraint : rule.getConstraints()) {
			if (constraint instanceof CrySLConstraint && !(constraint instanceof CrySLLiteral)) {
				// mode in {1} => mode2 in {2,4}
				CrySLConstraint constraintAsComplexConstraint = (CrySLConstraint) constraint;
				CrySLValueConstraint modeInOne = (CrySLValueConstraint) constraintAsComplexConstraint.getLeft();
				assertEquals("mode", modeInOne.getVarName());
				assertEquals(Arrays.asList(new String[] {"1"}), modeInOne.getValueRange());

				CrySLValueConstraint modeTwoInTwoFour = (CrySLValueConstraint) constraintAsComplexConstraint.getRight();
				assertEquals("mode2", modeTwoInTwoFour.getVarName());
				assertEquals(Arrays.asList(new String[] {"2", "4"}), modeTwoInTwoFour.getValueRange());

				assertEquals(LogOps.implies, constraintAsComplexConstraint.getOperator());

			} else if (constraint instanceof CrySLValueConstraint) {
				// mode in {1, 2, 3}
				CrySLValueConstraint constraintAsValueConstraint = (CrySLValueConstraint) constraint;
				assertEquals("mode", constraintAsValueConstraint.getVarName());
				assertEquals(Arrays.asList(new String[] {"1", "2", "3"}), constraintAsValueConstraint.getValueRange());
			}
		}

	}

	@Test
	public void cmpConstraintTest() {
		CrySLRule rule = readRuleFromFuleName("CompConstraintTestRule");
		List<ISLConstraint> constraints = rule.getConstraints();
		// mode > mode2
		CrySLComparisonConstraint modeGreater = (CrySLComparisonConstraint) constraints.get(0);
		assertEquals("mode", modeGreater.getLeft().getLeft().getName());
		assertEquals(CompOp.g, modeGreater.getOperator());
		assertEquals("mode2", modeGreater.getRight().getLeft().getName());

		// mode < mode2
		CrySLComparisonConstraint modeLesser = (CrySLComparisonConstraint) constraints.get(1);
		assertEquals("mode", modeLesser.getLeft().getLeft().getName());
		assertEquals(CompOp.l, modeLesser.getOperator());
		assertEquals("mode2", modeLesser.getRight().getLeft().getName());

		// mode >= mode2
		CrySLComparisonConstraint modeGreaterEq = (CrySLComparisonConstraint) constraints.get(2);
		assertEquals("mode", modeGreaterEq.getLeft().getLeft().getName());
		assertEquals(CompOp.ge, modeGreaterEq.getOperator());
		assertEquals("mode2", modeGreaterEq.getRight().getLeft().getName());

		// mode <= mode2
		CrySLComparisonConstraint modeLesserEq = (CrySLComparisonConstraint) constraints.get(3);
		assertEquals("mode", modeLesserEq.getLeft().getLeft().getName());
		assertEquals(CompOp.le, modeLesserEq.getOperator());
		assertEquals("mode2", modeLesserEq.getRight().getLeft().getName());

		// mode != mode2
		CrySLComparisonConstraint modeUnEq = (CrySLComparisonConstraint) constraints.get(4);
		assertEquals("mode", modeUnEq.getLeft().getLeft().getName());
		assertEquals(CompOp.neq, modeUnEq.getOperator());
		assertEquals("mode2", modeUnEq.getRight().getLeft().getName());
	}

	@Test
	public void arithConstraintTest() {
		CrySLRule rule = readRuleFromFuleName("ArithConstraintTestRule");
		List<ISLConstraint> constraints = rule.getConstraints();
		// mode + mode2
		CrySLArithmeticConstraint modePlusMode2 = (CrySLArithmeticConstraint) constraints.get(0);
		assertEquals("mode", modePlusMode2.getLeft().getName());
		assertEquals(ArithOp.p, modePlusMode2.getOperator());
		assertEquals("mode2", modePlusMode2.getRight().getName());

		// mode - mode2
		CrySLArithmeticConstraint modeMinusMode2 = (CrySLArithmeticConstraint) constraints.get(1);
		assertEquals("mode", modeMinusMode2.getLeft().getName());
		assertEquals(ArithOp.n, modeMinusMode2.getOperator());
		assertEquals("mode2", modeMinusMode2.getRight().getName());

	}

	@Test
	public void ComplexConstraintTest() {
		CrySLRule rule = readRuleFromFuleName("ComplexConstraintTestRule");
		List<ISLConstraint> constraints = rule.getConstraints();

		CrySLConstraint constraintAsImpliesConstraint = (CrySLConstraint) constraints.get(0);
		assertEquals(LogOps.implies, constraintAsImpliesConstraint.getOperator());

		CrySLConstraint constraintAsAndConstraint = (CrySLConstraint) constraints.get(1);
		assertEquals(LogOps.and, constraintAsAndConstraint.getOperator());

		CrySLConstraint constraintAsOrConstraint = (CrySLConstraint) constraints.get(2);
		assertEquals(LogOps.or, constraintAsOrConstraint.getOperator());
	}

}
