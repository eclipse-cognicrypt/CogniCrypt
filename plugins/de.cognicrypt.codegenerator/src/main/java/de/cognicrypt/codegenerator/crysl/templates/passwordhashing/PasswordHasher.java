package de.cognicrypt.codegenerator.crysl.templates.passwordhashing;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class PasswordHasher {
	
	public static java.lang.String createPWHash(char[] pwd) throws GeneralSecurityException {
		byte[] salt = new byte[32];
		byte[] pwdHashBytes = null;
		
		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.SecureRandom").addParameter(salt, "nextBytes", 0).considerCrySLRule("java.security.PBEKeySpec")
		.addParameter(pwd, "PBEKeySpec", 0).considerCrySLRule("javax.crypto.SecretKeyFactory").considerCrySLRule("java.security.SecretKey").addReturnObject(pwdHashBytes)
		.generate();
		
		String pwdHash = toBase64(salt) + ":" + toBase64(pwdHashBytes);
		return pwdHash;
	}

	public static boolean verifyPWHash(char[] pwd, java.lang.String pwdhash) throws GeneralSecurityException {
		String[] parts = pwdhash.split(":");
		byte[] salt = fromBase64(parts[0]);
		byte[] res = null;

		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.PBEKeySpec").addParameter(salt, "PBEKeySpec", 1).addParameter(pwd, "PBEKeySpec", 0)
		.considerCrySLRule("javax.crypto.SecretKeyFactory").considerCrySLRule("java.security.SecretKey").addReturnObject(res)
		.generate();
		
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
