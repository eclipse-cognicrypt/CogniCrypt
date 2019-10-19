/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

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

		final Validation portNumber = new Validation();
		final int Output1 = portNumber.validationPortNumber("6534");
		assertEquals(0, Output1);
		final int Output2 = portNumber.validationPortNumber("65536");
		assertEquals(1, Output2);
		final int Output3 = portNumber.validationPortNumber("a2");
		assertEquals(1, Output3);
		final int Output4 = portNumber.validationPortNumber("12a");
		assertEquals(1, Output4);
		final int Output5 = portNumber.validationPortNumber(" ");
		assertEquals(1, Output5);
	}

}
