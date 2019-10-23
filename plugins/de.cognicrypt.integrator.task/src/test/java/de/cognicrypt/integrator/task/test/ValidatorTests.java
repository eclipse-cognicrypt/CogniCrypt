/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import de.cognicrypt.integrator.task.controllers.Validator;

public class ValidatorTests {

	@Test
	public void testCheckIfTaskNameAlreadyExists() {
		assertTrue(Validator.checkIfTaskNameAlreadyExists("Test"));
		assertFalse(Validator.checkIfTaskNameAlreadyExists("Encrypt Data Using a Secret Key"));
		assertFalse(Validator.checkIfTaskNameAlreadyExists("SymmetricEncryption"));
	}

	@Test
	public void testGetValidXMLString() {
		assertTrue(Validator.getValidXMLString("<>\'\"").equals(""));
	}

}
