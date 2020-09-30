/********************************************************************************
 * Copyright (c) 2015-2020 TU Darmstadt, Paderborn University
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
	 * Creates the PW hash.
	 *
	 * @param pwd the pwd.
	 * @return the java.lang. string.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchAlgorithmException.
	 */
	public static java.lang.String createPWHash(char[] pwd) throws GeneralSecurityException {
		byte[] salt = new byte[32];
		byte[] pwdHashBytes = null;
		int keysize = 160;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "next").includeClass("java.security.PBEKeySpec")
			.addParameter(pwd, "password").addParameter(salt, "salt").addParameter(keysize, "keylength").includeClass("javax.crypto.SecretKeyFactory").includeClass("java.security.SecretKey").addParameter(pwdHashBytes, "keyMaterial").generate();

		java.lang.String pwdHash = toBase64(salt) + ":" + toBase64(pwdHashBytes);
		return pwdHash;
	}

	/**
	 * Verify PW hash.
	 *
	 * @param pwd the pwd.
	 * @param pwdhash the pwdhash.
	 * @return true, if successful.
	 * @throws GeneralSecurityException the general security exception.
	 */
	public static boolean verifyPWHash(char[] pwd, java.lang.String pwdhash) throws GeneralSecurityException {
		java.lang.String[] parts = pwdhash.split(":");
		byte[] salt = fromBase64(parts[0]);
		byte[] res = null;
		int keysize = 160;

		CrySLCodeGenerator.getInstance().includeClass("java.security.PBEKeySpec").addParameter(pwd, "password").addParameter(salt, "salt").addParameter(keysize, "keylength")
			.includeClass("javax.crypto.SecretKeyFactory").includeClass("java.security.SecretKey").addParameter(res, "keyMaterial").generate();

		Boolean areEqual = slowEquals(res, fromBase64(parts[1]));
		return areEqual;
	}

	/**
	 * Slow equals.
	 *
	 * @param a the first value.
	 * @param b the second value.
	 * @return true, if successful.
	 */
	private static boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}
		return diff == 0;
	}

	/**
	 * To base 64. converts an array of bytes into a string.
	 *
	 * @param array of bytes.
	 * @return the java.lang. string.
	 */
	private static java.lang.String toBase64(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}

	/**
	 * From base 64. Converts a string into an array of bytes
	 *
	 * @param hash, a string containing lexical representation of xsd:base64Binary
	 * @return the byte[]
	 */
	private static byte[] fromBase64(java.lang.String hash) {
		return DatatypeConverter.parseBase64Binary(hash);
	}

}
