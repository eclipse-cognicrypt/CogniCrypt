package de.cognicrypt.codegenerator.crysl.templates.hybridencryptionstrings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class SecureEncryptor {

	public javax.crypto.SecretKey generateSessionKey() throws NoSuchAlgorithmException {
		javax.crypto.SecretKey sessionKey = null;
		CrySLCodeGenerator.getInstance().considerCrySLRule("javax.crypto.KeyGenerator").addReturnObject(sessionKey).generate();
		return sessionKey;
	}

	public java.security.KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		java.security.KeyPair keyPair = null;
		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.KeyPairGenerator").addReturnObject(keyPair);
		return keyPair;
	}

	public byte[] encryptSessionKey(javax.crypto.SecretKey sessionKey, java.security.KeyPair keyPair) throws GeneralSecurityException {
		byte[] wrappedKeyBytes = null;
		int mode = Cipher.WRAP_MODE;
		java.security.PublicKey publicKey = keyPair.getPublic();
		CrySLCodeGenerator.getInstance().considerCrySLRule("javax.crypto.Cipher").addParameter(mode, "init", 0).addParameter(publicKey, "init", 1)
			.addParameter(sessionKey, "wrap", 0).addReturnObject(wrappedKeyBytes).generate();
		return wrappedKeyBytes;
	}

	public java.io.File encryptData(java.io.File plaintext, javax.crypto.SecretKey key) throws IOException {
		byte[] ivBytes = new byte[32];
		byte[] cipherText = null;
		byte[] plaintextFile = Files.readAllBytes(Paths.get(plaintext.getAbsolutePath()));
		int mode = Cipher.ENCRYPT_MODE;

		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.SecureRandom").addParameter(ivBytes, "nextBytes", 0)
			.considerCrySLRule("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "IvParameterSpec", 0).considerCrySLRule("javax.crypto.Cipher")
			.addParameter(mode, "init", 0).addParameter(plaintextFile, "doFinal", 0).addParameter(key, "init", 1).addReturnObject(cipherText).generate();

		byte[] ret = new byte[ivBytes.length + cipherText.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(cipherText, 0, ret, ivBytes.length, cipherText.length);
		Files.write(Paths.get(plaintext.getAbsolutePath()), ret);
		return plaintext;
	}

	public java.io.File decryptData(java.io.File ciphertext, javax.crypto.SecretKey key) throws IOException {
		byte[] ciphertextFile = Files.readAllBytes(Paths.get(ciphertext.getAbsolutePath()));
		byte[] ivBytes = new byte[32];
		byte[] data = new byte[ciphertextFile.length - ivBytes.length];
		System.arraycopy(data, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(data, ivBytes.length, data, 0, data.length);

		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().considerCrySLRule("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "IvParameterSpec", 0).considerCrySLRule("javax.crypto.Cipher")
			.addParameter(mode, "init", 0).addParameter(data, "doFinal", 0).addParameter(key, "init", 1).addReturnObject(res).generate();

		Files.write(Paths.get(ciphertext.getAbsolutePath()), res);
		return ciphertext;
	}

}
