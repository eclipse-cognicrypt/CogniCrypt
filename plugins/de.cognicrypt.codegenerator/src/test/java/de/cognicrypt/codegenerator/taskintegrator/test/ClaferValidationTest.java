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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import de.cognicrypt.codegenerator.taskintegrator.controllers.ClaferValidation;

public class ClaferValidationTest {

	@Test
	public void testGetValidationMessage() {
		HashMap<String, Boolean> expectedMap = new HashMap<String, Boolean>();
		expectedMap.put("", false);
		expectedMap.put("keysize", true);
		expectedMap.put("1keysize", false);
		expectedMap.put("keysize$", false);
		expectedMap.put("key size", false);

		for (String key : expectedMap.keySet()) {
			Boolean actual = ClaferValidation.getNameValidationMessage(key, true).isEmpty();
			assertEquals(expectedMap.get(key), actual);
		}
	}

}
