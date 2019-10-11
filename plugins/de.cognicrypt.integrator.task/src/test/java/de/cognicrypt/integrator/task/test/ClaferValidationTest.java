/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.test;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import org.junit.Test;
import de.cognicrypt.integrator.task.controllers.ClaferValidation;

public class ClaferValidationTest {

	@Test
	public void testGetValidationMessage() {
		final HashMap<String, Boolean> expectedMap = new HashMap<String, Boolean>();
		expectedMap.put("", false);
		expectedMap.put("keysize", true);
		expectedMap.put("1keysize", false);
		expectedMap.put("keysize$", false);
		expectedMap.put("key size", false);

		for (final String key : expectedMap.keySet()) {
			final Boolean actual = ClaferValidation.getNameValidationMessage(key, true).isEmpty();
			assertEquals(expectedMap.get(key), actual);
		}
	}

}
