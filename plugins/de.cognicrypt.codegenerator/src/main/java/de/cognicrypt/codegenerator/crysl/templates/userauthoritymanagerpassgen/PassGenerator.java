/********************************************************************************
 * Copyright (c) 2015-2021 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.cognicrypt.codegenerator.crysl.templates.userauthoritymanagerpassgen;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class PassGenerator generates a secure password that meets all requirements
 *  for a standard password described in NIST (National Institute of Standards and Technology).
 */

public class PassGenerator {

	/**
	 * Generates a secure random password of length 12. This password consists of numbers, symbols, uppercase and lowercase 
	 * letters that are randomly distributed in the password. The length of the password must be equal to or more than 4, cause it must include
	 * one of each set of characters.
	 *
	 * @return the password.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a SecureRandomSpi implementation for the specified algorithm. {@link #generateRandomCharacter(String) generateRandomCharacter}
	 */
	public static String generateRandomPassword() throws NoSuchAlgorithmException, GeneralSecurityException
	{
		int length = 12;
		final String capitalAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String smallAlphabet = "abcdefghijklmnopqrstuvwxyz";
		final String numbers = "0123456789";
		//removed dot slashes and quotations- library 
		final String specialChars = "~!@#$%^&*()-_=+[{]};:,<>?";
		final String allCases = capitalAlphabet + smallAlphabet + numbers + specialChars;

		List<String> password = new ArrayList<String>();

		password.add(generateRandomCharacter(capitalAlphabet));
		password.add(generateRandomCharacter(smallAlphabet));
		password.add(generateRandomCharacter(numbers));
		password.add(generateRandomCharacter(specialChars));

		for (int i = 4; i < length; i++) {
			password.add(generateRandomCharacter(allCases));
		}
		// relocate each character in password
		Collections.shuffle(password);
		return String.join("", password);
	}

	/**
	 * Generates a random integer (randIndex) in range of length of characters (e.g. 10 in numbers)
	 * and returns the randIndex'th item of the list of characters.
	 *
	 * @param chars the character, e.g., upper case letters.
	 * @return the string one character from the character set that is randomly selected.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a SecureRandomSpi implementation for the specified algorithm.
	 */
	public static String generateRandomCharacter(String chars) throws NoSuchAlgorithmException, GeneralSecurityException{

		int length = chars.length();
		int randIndex = 0;
		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(length, "range").addParameter(randIndex, "randIntInRange").setCustomMain("generateRandomPassword").generate();
		return String.valueOf(chars.charAt(randIndex));
	}

}