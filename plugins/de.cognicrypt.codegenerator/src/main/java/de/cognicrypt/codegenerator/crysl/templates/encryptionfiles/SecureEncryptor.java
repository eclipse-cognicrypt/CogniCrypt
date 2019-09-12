package de.cognicrypt.codegenerator.crysl.templates.encryptionfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class SecureEncryptor {
	
	public javax.crypto.SecretKey getKey(char[] pwd) {
		byte[] salt = new byte[32];
		javax.crypto.SecretKey encryptionKey = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "nextBytes", 0).
			includeClass("java.security.PBEKeySpec").addParameter(pwd, "PBEKeySpec", 0).includeClass("javax.crypto.SecretKeyFactory").
			includeClass("java.security.SecretKey").includeClass("javax.crypto.SecretKeySpec").addReturnObject(encryptionKey)
			.generate();
		
		return encryptionKey;
	}
	
	public java.io.File encrypt(java.io.File plaintext, javax.crypto.SecretKey key) throws IOException {
		byte[] ivBytes = new byte[key.getEncoded().length];
		byte[] res = null;
		byte[] plaintextFile = Files.readAllBytes(Paths.get(plaintext.getAbsolutePath()));
		int mode = Cipher.ENCRYPT_MODE;
		
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "IvParameterSpec", 0)
		.includeClass("javax.crypto.Cipher").addParameter(mode, "init", 0).addParameter(plaintextFile,"doFinal", 0).addParameter(key, "init", 1).addReturnObject(res)
			.generate(); 
		
		byte[] ret = new byte[ivBytes.length + res.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(res, 0, ret, ivBytes.length, res.length);
		Files.write(Paths.get(plaintext.getAbsolutePath()), ret);
		return plaintext;
	}
	
	public java.io.File decrypt(java.io.File ciphertext, javax.crypto.SecretKey key) throws IOException {
		byte[] ciphertextFile = Files.readAllBytes(Paths.get(ciphertext.getAbsolutePath()));
		byte[] ivBytes = new byte[key.getEncoded().length];
		byte[] data = new byte[ciphertextFile.length - ivBytes.length]; 
		System.arraycopy(ciphertext, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(ciphertext, ivBytes.length, data, 0, data.length);
		
		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "IvParameterSpec", 0)
		.includeClass("javax.crypto.Cipher").addParameter(mode, "init", 0).addParameter(data,"doFinal", 0).addParameter(key, "init", 1).addReturnObject(res)
			.generate(); 
		
		Files.write(Paths.get(ciphertext.getAbsolutePath()), res);
		return ciphertext;
	}

}
