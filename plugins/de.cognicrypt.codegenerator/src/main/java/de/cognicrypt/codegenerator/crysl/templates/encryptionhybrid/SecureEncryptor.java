package de.cognicrypt.codegenerator.crysl.templates.encryptionhybrid;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class SecureEncryptor {

	public javax.crypto.SecretKey generateSessionKey() throws NoSuchAlgorithmException {
		javax.crypto.SecretKey sessionKey = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.KeyGenerator").addReturnObject(sessionKey).generate();
		return sessionKey;
	}

	public java.security.KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		java.security.KeyPair keyPair = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.KeyPairGenerator").addReturnObject(keyPair);
		return keyPair;
	}

	public byte[] encryptSessionKey(javax.crypto.SecretKey sessionKey, java.security.KeyPair keyPair) throws GeneralSecurityException {
		byte[] wrappedKeyBytes = null;
		int mode = Cipher.WRAP_MODE;
		java.security.PublicKey publicKey = keyPair.getPublic();
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(publicKey, "key").addParameter(sessionKey, "wrappedKey")
			.addReturnObject(wrappedKeyBytes).generate();
		return wrappedKeyBytes;
	}

	public byte[] encryptData(byte[] plaintext, javax.crypto.SecretKey key) {
		byte[] ivBytes = new byte[32];
		byte[] cipherText = null;
		int mode = Cipher.ENCRYPT_MODE;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(ivBytes, "next").includeClass("javax.crypto.spec.IvParameterSpec")
			.addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(plaintext, "plainText").addParameter(key, "key")
			.addReturnObject(cipherText).generate();

		byte[] ret = new byte[ivBytes.length + cipherText.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(cipherText, 0, ret, ivBytes.length, cipherText.length);
		return ret;
	}

	public byte[] decryptData(byte[] ciphertext, javax.crypto.SecretKey key) {

		byte[] ivBytes = new byte[32];
		byte[] data = new byte[ciphertext.length - ivBytes.length];
		System.arraycopy(data, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(data, ivBytes.length, data, 0, data.length);

		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher")
			.addParameter(mode, "encmode").addParameter(data, "plainText").addParameter(key, "key").addReturnObject(res).generate();

		return res;
	}

}
