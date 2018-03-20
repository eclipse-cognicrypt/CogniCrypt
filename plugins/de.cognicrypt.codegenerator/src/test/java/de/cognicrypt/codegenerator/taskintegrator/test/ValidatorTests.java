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
