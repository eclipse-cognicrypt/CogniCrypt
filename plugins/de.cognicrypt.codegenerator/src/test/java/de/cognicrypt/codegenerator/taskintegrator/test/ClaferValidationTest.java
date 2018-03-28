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
			Boolean actual = ClaferValidation.getValidationMessage(key).isEmpty();
			assertEquals(expectedMap.get(key), actual);
		}
	}

}
