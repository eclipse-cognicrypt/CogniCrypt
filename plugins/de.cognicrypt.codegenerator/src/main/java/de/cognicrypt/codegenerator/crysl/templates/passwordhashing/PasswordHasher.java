package de.cognicrypt.codegenerator.crysl.templates.passwordhashing;

import java.security.GeneralSecurityException;

import javax.xml.bind.DatatypeConverter;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class PasswordHasher {

	public static java.lang.String createPWHash(char[] pwd) throws GeneralSecurityException {
		byte[] salt = new byte[32];
		byte[] pwdHashBytes = null;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "next").includeClass("java.security.PBEKeySpec")
			.addParameter(pwd, "password").includeClass("javax.crypto.SecretKeyFactory").includeClass("java.security.SecretKey").addReturnObject(pwdHashBytes).generate();

		String pwdHash = toBase64(salt) + ":" + toBase64(pwdHashBytes);
		return pwdHash;
	}

	public static boolean verifyPWHash(char[] pwd, java.lang.String pwdhash) throws GeneralSecurityException {
		String[] parts = pwdhash.split(":");
		byte[] salt = fromBase64(parts[0]);
		byte[] res = null;

		CrySLCodeGenerator.getInstance().includeClass("java.security.PBEKeySpec").addParameter(salt, "salt").addParameter(pwd, "passowrd")
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

	private static String toBase64(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}

	private static byte[] fromBase64(String hash) {
		return DatatypeConverter.parseBase64Binary(hash);
	}

}
