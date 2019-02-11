package de.cognicrypt.cryslhandler;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import org.junit.BeforeClass;
import org.junit.Test;
import crypto.interfaces.ISLConstraint;
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
				//mode in {1, 2, 3}
				CryptSLValueConstraint constraintAsValueConstraint = (CryptSLValueConstraint) constraint;
				assertEquals("mode", constraintAsValueConstraint.getVarName());
				assertEquals(Arrays.asList(new String[] {"1", "2", "3"}), constraintAsValueConstraint.getValueRange());
			}
		}
		
	}
	
}
