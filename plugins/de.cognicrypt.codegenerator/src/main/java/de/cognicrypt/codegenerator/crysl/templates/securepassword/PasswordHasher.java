/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.securepassword;

import java.security.GeneralSecurityException;

import javax.xml.bind.DatatypeConverter;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class PasswordHasher {

	public static java.lang.String createPWHash(char[] pwd) throws GeneralSecurityException {
		byte[] salt = new byte[32];
		byte[] pwdHashBytes = null;
		int keysize = 160;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "next").includeClass("java.security.PBEKeySpec")
			.addParameter(pwd, "password").addParameter(salt, "salt").addParameter(keysize, "keylength").includeClass("javax.crypto.SecretKeyFactory").includeClass("java.security.SecretKey").addReturnObject(pwdHashBytes).generate();

		java.lang.String pwdHash = toBase64(salt) + ":" + toBase64(pwdHashBytes);
		return pwdHash;
	}

	public static boolean verifyPWHash(char[] pwd, java.lang.String pwdhash) throws GeneralSecurityException {
		java.lang.String[] parts = pwdhash.split(":");
		byte[] salt = fromBase64(parts[0]);
		byte[] res = null;
		int keysize = 160;

		CrySLCodeGenerator.getInstance().includeClass("java.security.PBEKeySpec").addParameter(pwd, "password").addParameter(salt, "salt").addParameter(keysize, "keylength")
			.includeClass("javax.crypto.SecretKeyFactory").includeClass("java.security.SecretKey").addReturnObject(res).generate();

		Boolean areEqual = slowEquals(res, fromBase64(parts[1]));
		return areEqual;
	}

	private static boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}
		return diff == 0;
	}

	private static java.lang.String toBase64(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}

	private static byte[] fromBase64(java.lang.String hash) {
		return DatatypeConverter.parseBase64Binary(hash);
	}

}
