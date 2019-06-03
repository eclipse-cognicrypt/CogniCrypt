package de.cognicrypt.codegenerator.crysl.templates;

import javax.crypto.SecretKey;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class EncryptionTemplate {
	
	public byte[] encrypt(byte[] data, SecretKey key) {
		byte[] salt = new byte[32];
		byte[] res = null;
		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.SecureRandom").addParameter(salt).
			considerCrySLRule("java.security.PBEKeySpec").considerCrySLRule("javax.crypto.SecretKeyFactory").
			considerCrySLRule("java.security.SecretKey").considerCrySLRule("javax.crypto.SecretKeySpec").
			considerCrySLRule("javax.crypto.Cipher").addParameter(key).addParameter(data).addReturnObject(res)
			.generate(); 
		
		byte[] ret = new byte[salt.length + res.length];
		System.arraycopy(salt, 0, ret, 0, salt.length);
		System.arraycopy(salt, 0, ret, salt.length, res.length);
		return ret;
	}

}
