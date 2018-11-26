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

public class Validation_ipAddress {

	@Test
	public void test() {

		Validation ipAddress = new Validation();
		int Output1 = ipAddress.validationIpAddress("255.255.255.255");
		assertEquals(0, Output1);
		int Output2 = ipAddress.validationIpAddress("256.255.255.255.255");
		assertEquals(2, Output2);
		int Output3 = ipAddress.validationIpAddress("0.0.0.0");
		assertEquals(0, Output3);
		int Output4 = ipAddress.validationIpAddress("a.a.a.a");
		assertEquals(1, Output4);
		int Output5 = ipAddress.validationIpAddress(".");
		assertEquals(2, Output5);
		int Output6 = ipAddress.validationIpAddress("2.0.0.0");
		assertEquals(0, Output6);
		int Output7 = ipAddress.validationIpAddress(".255.255.255");
		assertEquals(1, Output7);
		int Output8 = ipAddress.validationIpAddress("255.255.255.");
		assertEquals(1, Output8);
		int Output9 = ipAddress.validationIpAddress("-2.255.255.255");
		assertEquals(1, Output9);
		int Output10 = ipAddress.validationIpAddress("2.255.255.255");
		assertEquals(0, Output10);

	}

}
