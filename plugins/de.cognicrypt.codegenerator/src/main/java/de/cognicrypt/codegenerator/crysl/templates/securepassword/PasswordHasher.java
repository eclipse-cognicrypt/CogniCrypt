/********************************************************************************
 * Copyright (c) 2015-2021 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v. 2.0 which is available at 
 * http://www.eclipse.org/legal/epl-2.0. 
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.securepassword;

import java.security.GeneralSecurityException;

import javax.xml.bind.DatatypeConverter;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;


/**
 * The Class PasswordHasher securely transforms and retrieves a password.
 */
public class PasswordHasher {

	/**
	 * This method creates hashed password with SHA512 and random salt.
	 *
	 * @param pwd The password to be hashed.
	 * @returns the salt and hashed password in string with a colon in between.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a SecretKeyFactorySpi or SecureRadnomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 */
	public static java.lang.String createPWHash(char[] pwd) throws GeneralSecurityException {
		byte[] salt = new byte[32];
		byte[] pwdHashBytes = null;
		int keysize = 160;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "next").includeClass("javax.crypto.spec.PBEKeySpec")
			.addParameter(pwd, "password").addParameter(salt, "salt").addParameter(keysize, "keylength").includeClass("javax.crypto.SecretKeyFactory").includeClass("javax.crypto.SecretKey").addParameter(pwdHashBytes, "keyMaterial").generate();

		java.lang.String pwdHash = toBase64(salt) + ":" + toBase64(pwdHashBytes);
		return pwdHash;
	}

	/**
	 * This method verifies if the input password generates the same hash using the hash algorithm and salt of the input hashed password.
	 *
	 * @param pwd the password.
	 * @param pwdhash the hashed password with its salt.
	 * @returns true, if the hashed input password is the equal to input hashed password.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a SecretKeyFactorySpi or SecureRadnomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 */
	public static boolean verifyPWHash(char[] pwd, java.lang.String pwdhash) throws GeneralSecurityException {
		java.lang.String[] parts = pwdhash.split(":");
		byte[] salt = fromBase64(parts[0]);
		byte[] res = null;
		int keysize = 160;

		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.PBEKeySpec").addParameter(pwd, "password").addParameter(salt, "salt").addParameter(keysize, "keylength")
			.includeClass("javax.crypto.SecretKeyFactory").includeClass("javax.crypto.SecretKey").addParameter(res, "keyMaterial").generate();

		Boolean areEqual = slowEquals(res, fromBase64(parts[1]));
		return areEqual;
	}

	/**
	 * This method shows the equality of two bytes.
	 *
	 * @param a the first value.
	 * @param b the second value.
	 * @returns true, if the two bytes are equal.
	 */
	private static boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}
		return diff == 0;
	}

	/**
	 * This method converts an array of bytes into a string.
	 *
	 * @param array of bytes.
	 * @returns string.
	 */
	private static java.lang.String toBase64(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}

	/**
	 * This method converts a string into an array of bytes.
	 *
	 * @param hash, a string containing lexical representation of xsd:base64Binary.
	 * @returns the byte[] created by Base64.
	 */
	private static byte[] fromBase64(java.lang.String hash) {
		return DatatypeConverter.parseBase64Binary(hash);
	}

}
