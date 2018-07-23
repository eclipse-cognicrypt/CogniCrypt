/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.taskintegrator.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.cognicrypt.codegenerator.taskintegrator.controllers.Validator;

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
