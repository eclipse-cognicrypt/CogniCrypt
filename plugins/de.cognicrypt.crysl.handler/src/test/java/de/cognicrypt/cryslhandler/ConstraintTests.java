package de.cognicrypt.cryslhandler;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import crypto.interfaces.ISLConstraint;
import crypto.rules.CryptSLArithmeticConstraint;
import crypto.rules.CryptSLArithmeticConstraint.ArithOp;
import crypto.rules.CryptSLComparisonConstraint;
import crypto.rules.CryptSLComparisonConstraint.CompOp;
import crypto.rules.CryptSLConstraint;
import crypto.rules.CryptSLConstraint.LogOps;
import crypto.rules.CryptSLLiteral;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLValueConstraint;
import de.cognicrypt.crysl.reader.CrySLModelReader;

public class ConstraintTests {

	private static CrySLModelReader csmr;

	@BeforeClass
	public static void setup() throws MalformedURLException {
		csmr = new CrySLModelReader();
	}

	private CryptSLRule readRuleFromFuleName(String ruleName) {
		return csmr.readRule(new File("src/test/resources/" + ruleName + ".cryptsl"));
	}

	@Test
	public void inConstraintTest() {
		CryptSLRule rule = readRuleFromFuleName("InConstraintTestRule");
		for (ISLConstraint constraint : rule.getConstraints()) {
			if (constraint instanceof CryptSLConstraint && !(constraint instanceof CryptSLLiteral)) {
				// mode in {1} => mode2 in {2,4}
				CryptSLConstraint constraintAsComplexConstraint = (CryptSLConstraint) constraint;
				CryptSLValueConstraint modeInOne = (CryptSLValueConstraint) constraintAsComplexConstraint.getLeft();
				assertEquals("mode", modeInOne.getVarName());
				assertEquals(Arrays.asList(new String[] {"1"}), modeInOne.getValueRange());

				CryptSLValueConstraint modeTwoInTwoFour = (CryptSLValueConstraint) constraintAsComplexConstraint.getRight();
				assertEquals("mode2", modeTwoInTwoFour.getVarName());
				assertEquals(Arrays.asList(new String[] {"2", "4"}), modeTwoInTwoFour.getValueRange());

				assertEquals(LogOps.implies, constraintAsComplexConstraint.getOperator());

			} else if (constraint instanceof CryptSLValueConstraint) {
				// mode in {1, 2, 3}
				CryptSLValueConstraint constraintAsValueConstraint = (CryptSLValueConstraint) constraint;
				assertEquals("mode", constraintAsValueConstraint.getVarName());
				assertEquals(Arrays.asList(new String[] {"1", "2", "3"}), constraintAsValueConstraint.getValueRange());
			}
		}

	}

	@Test
	public void cmpConstraintTest() {
		CryptSLRule rule = readRuleFromFuleName("CompConstraintTestRule");
		List<ISLConstraint> constraints = rule.getConstraints();
		// mode > mode2
		CryptSLComparisonConstraint modeGreater = (CryptSLComparisonConstraint) constraints.get(0);
		assertEquals("mode", modeGreater.getLeft().getLeft().getName());
		assertEquals(CompOp.g, modeGreater.getOperator());
		assertEquals("mode2", modeGreater.getRight().getLeft().getName());

		// mode < mode2
		CryptSLComparisonConstraint modeLesser = (CryptSLComparisonConstraint) constraints.get(1);
		assertEquals("mode", modeLesser.getLeft().getLeft().getName());
		assertEquals(CompOp.l, modeLesser.getOperator());
		assertEquals("mode2", modeLesser.getRight().getLeft().getName());

		// mode >= mode2
		CryptSLComparisonConstraint modeGreaterEq = (CryptSLComparisonConstraint) constraints.get(2);
		assertEquals("mode", modeGreaterEq.getLeft().getLeft().getName());
		assertEquals(CompOp.ge, modeGreaterEq.getOperator());
		assertEquals("mode2", modeGreaterEq.getRight().getLeft().getName());

		// mode <= mode2
		CryptSLComparisonConstraint modeLesserEq = (CryptSLComparisonConstraint) constraints.get(3);
		assertEquals("mode", modeLesserEq.getLeft().getLeft().getName());
		assertEquals(CompOp.le, modeLesserEq.getOperator());
		assertEquals("mode2", modeLesserEq.getRight().getLeft().getName());

		// mode != mode2
		CryptSLComparisonConstraint modeUnEq = (CryptSLComparisonConstraint) constraints.get(4);
		assertEquals("mode", modeUnEq.getLeft().getLeft().getName());
		assertEquals(CompOp.neq, modeUnEq.getOperator());
		assertEquals("mode2", modeUnEq.getRight().getLeft().getName());
	}
	
	@Test
	public void arithConstraintTest() {
		CryptSLRule rule = readRuleFromFuleName("ArithConstraintTestRule");
		List<ISLConstraint> constraints = rule.getConstraints();
		// mode + mode2
		CryptSLArithmeticConstraint modePlusMode2 = (CryptSLArithmeticConstraint) constraints.get(0);
		assertEquals("mode", modePlusMode2.getLeft().getName());
		assertEquals(ArithOp.p, modePlusMode2.getOperator());
		assertEquals("mode2", modePlusMode2.getRight().getName());

		// mode - mode2
		CryptSLArithmeticConstraint modeMinusMode2 = (CryptSLArithmeticConstraint) constraints.get(1);
		assertEquals("mode", modeMinusMode2.getLeft().getName());
		assertEquals(ArithOp.n, modeMinusMode2.getOperator());
		assertEquals("mode2", modeMinusMode2.getRight().getName());

	}
	
	@Test
	public void ComplexConstraintTest() {
		CryptSLRule rule = readRuleFromFuleName("ComplexConstraintTestRule");
		List<ISLConstraint> constraints = rule.getConstraints();
		
		CryptSLConstraint constraintAsImpliesConstraint = (CryptSLConstraint) constraints.get(0);
		assertEquals(LogOps.implies, constraintAsImpliesConstraint.getOperator());
		
		CryptSLConstraint constraintAsAndConstraint = (CryptSLConstraint) constraints.get(1);
		assertEquals(LogOps.and, constraintAsAndConstraint.getOperator());
		
		CryptSLConstraint constraintAsOrConstraint = (CryptSLConstraint) constraints.get(2);
		assertEquals(LogOps.or, constraintAsOrConstraint.getOperator());
	}

}
