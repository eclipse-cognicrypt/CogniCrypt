/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.ui1718.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Validation_portNumber {

	@Test
	public void test() {

		Validation portNumber = new Validation();
		int Output1 = portNumber.validationPortNumber("6534");
		assertEquals(0, Output1);
		int Output2 = portNumber.validationPortNumber("65536");
		assertEquals(1, Output2);
		int Output3 = portNumber.validationPortNumber("a2");
		assertEquals(1, Output3);
		int Output4 = portNumber.validationPortNumber("12a");
		assertEquals(1, Output4);
		int Output5 = portNumber.validationPortNumber(" ");
		assertEquals(1, Output5);
	}

}
