package de.cognicrypt.codegenerator.crysl.templates.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class SecureEncryptor {
	
	public javax.crypto.SecretKey getKey(char[] pwd) {
		byte[] salt = new byte[32];
		javax.crypto.SecretKey encryptionKey = null;
		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.SecureRandom").addParameter(salt, "nextBytes", 0).
			considerCrySLRule("java.security.PBEKeySpec").addParameter(pwd, "PBEKeySpec", 0).considerCrySLRule("javax.crypto.SecretKeyFactory").
			considerCrySLRule("java.security.SecretKey").considerCrySLRule("javax.crypto.SecretKeySpec").addReturnObject(encryptionKey)
			.generate();
		
		return encryptionKey;
	}
	
	public byte[] encrypt(byte[] plaintext, javax.crypto.SecretKey key) {
		byte[] ivBytes = new byte[32];
		byte[] res = null;
		int mode = Cipher.ENCRYPT_MODE;
		
		CrySLCodeGenerator.getInstance().considerCrySLRule("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "IvParameterSpec", 0)
		.considerCrySLRule("javax.crypto.Cipher").addParameter(mode, "init", 0).addParameter(plaintext,"doFinal", 0).addParameter(key, "init", 1).addReturnObject(res)
			.generate(); 
		
		byte[] ret = new byte[ivBytes.length + res.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(res, 0, ret, ivBytes.length, res.length);
		return ret;
	}
	
	public byte[] decrypt(byte[] ciphertext, javax.crypto.SecretKey key) {
		
		byte[] ivBytes = new byte[32];
		byte[] data = new byte[ciphertext.length - ivBytes.length]; 
		System.arraycopy(data, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(data, ivBytes.length, data, 0, data.length);
		
		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().considerCrySLRule("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "IvParameterSpec", 0)
		.considerCrySLRule("javax.crypto.Cipher").addParameter(mode, "init", 0).addParameter(data,"doFinal", 0).addParameter(key, "init", 1).addReturnObject(res)
			.generate(); 
		
		
		return res;
	}

}
