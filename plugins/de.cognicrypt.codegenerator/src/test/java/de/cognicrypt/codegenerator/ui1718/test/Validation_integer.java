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

public class Validation_integer {

	@Test
	public void test() {
		final Validation integer = new Validation();
		final int Output1 = integer.validationInteger("3");
		assertEquals(0, Output1);
		final int Output2 = integer.validationInteger("abcd");
		assertEquals(4, Output2);
		final int Output3 = integer.validationInteger("3a");
		assertEquals(1, Output3);
		final int Output4 = integer.validationInteger("3a ");
		assertEquals(2, Output4);
	}

}
