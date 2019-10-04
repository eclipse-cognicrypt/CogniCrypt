package de.cognicrypt.codegenerator.crysl.templates.encryptionstrings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class SecureEncryptor {

	public javax.crypto.SecretKey getKey(char[] pwd) {
		byte[] salt = new byte[32];
		javax.crypto.SecretKey encryptionKey = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "next").includeClass("java.security.PBEKeySpec")
			.addParameter(pwd, "password").includeClass("javax.crypto.SecretKeyFactory").includeClass("java.security.SecretKey").includeClass("javax.crypto.SecretKeySpec")
			.addReturnObject(encryptionKey).generate();

		return encryptionKey;
	}

	public java.lang.String encrypt(java.lang.String plaintext, javax.crypto.SecretKey key) throws IOException {
		byte[] ivBytes = new byte[key.getEncoded().length];
		byte[] res = null;
		byte[] plaintextString = plaintext.getBytes(StandardCharsets.UTF_8);
		int mode = Cipher.ENCRYPT_MODE;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(ivBytes, "next").includeClass("javax.crypto.spec.IvParameterSpec")
			.addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(plaintextString, "plainText").addParameter(key, "key")
			.addReturnObject(res).generate();

		byte[] ret = new byte[ivBytes.length + res.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(res, 0, ret, ivBytes.length, res.length);

		return Base64.getEncoder().encodeToString(ret);
	}

	public java.lang.String decrypt(java.lang.String ciphertext, javax.crypto.SecretKey key) throws IOException {
		byte[] ciphertextString = Base64.getDecoder().decode(ciphertext);
		byte[] ivBytes = new byte[key.getEncoded().length];
		byte[] data = new byte[ciphertextString.length - ivBytes.length];
		System.arraycopy(ciphertextString, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(ciphertextString, ivBytes.length, data, 0, data.length);

		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher")
			.addParameter(mode, "encmode").addParameter(data, "plainText").addParameter(key, "key").addReturnObject(res).generate();

		return new String(res, StandardCharsets.UTF_8);
	}

}
