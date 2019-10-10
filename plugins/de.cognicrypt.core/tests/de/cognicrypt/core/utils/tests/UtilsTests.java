/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.core.utils.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import de.cognicrypt.utils.Utils;

public class UtilsTests {

	@Test
	public void testIsCompatibleJavaVersionCheck() {
		// This test case should fail on machines with >= Java 9. That is on purpose til the resolution of issue 202.
		assertFalse(Utils.isIncompatibleJavaVersion());
	}

	@Test
	public void testIsCompatibleJavaVersionCheckoneeight() {
		assertFalse(Utils.isIncompatibleJavaVersion("1.8"));
	}

	@Test
	public void testIsCompatibleJavaVersionCheckonenine() {
		assertTrue(Utils.isIncompatibleJavaVersion("9"));
	}
}
