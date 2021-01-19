/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryptionhybrid;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class SecureEncryptor hybrid.
 */
public class SecureEncryptor {
	
	/**
	 * Generate session key.
	 *
	 * @returns the session key.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a KeyGeneratorSpi implementation for the specified algorithm.
	 */
	public javax.crypto.SecretKey generateSessionKey() throws NoSuchAlgorithmException {
		javax.crypto.SecretKey sessionKey = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.KeyGenerator").addParameter(sessionKey, "key").generate();
		return sessionKey;
	}
	
	/**
	 * Generate key pair.
	 *
	 * @returns the key pair.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a KeyPairGeneratorSpi implementation for the specified algorithm.
	 */
	public java.security.KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		java.security.KeyPair keyPair = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.KeyPairGenerator").addParameter(keyPair, "kp");
		return keyPair;
	}
	
	/**
	 * Encrypt session key.
	 *
	 * @param sessionKey the session key.
	 * @param keyPair the key pair.
	 * @returns encrypted session key.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding for Cipher is not supported.
	 * @throws IllegalBlockSizeException This exception is thrown when the size of input data is not a multiple of the block-size.
	 * @throws NoSuchAlgorithmException This exception is thrown when Cipher uses wrong algorithm.
	 * @throws InvalidKeyException This exception is thrown when the key length is invalid for the chosen algorithm, or a parameter is missing when encrypting.
	 */
	public byte[] encryptSessionKey(javax.crypto.SecretKey sessionKey, java.security.KeyPair keyPair) throws GeneralSecurityException {
		byte[] wrappedKeyBytes = null;
		int mode = Cipher.WRAP_MODE;
		java.security.PublicKey publicKey = keyPair.getPublic();
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(publicKey, "key").addParameter(sessionKey, "wrappedKey")
			.addParameter(wrappedKeyBytes, "wrappedKeyBytes").generate();
		return wrappedKeyBytes;
	}
	
	/**
	 * Encrypt data.
	 *
	 * @param plaintext the bytes to be encrypted.
	 * @param key the key.
	 * @param plain_off the input offset.
	 * @param len the input length.
	 * @returns the encrypted bytes.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the size of input data is not a multiple of the block-size.
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws NoSuchAlgorithmException This exception is thrown when cipher object is created using padding or modes that are not supported by chosen algorithm.
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown when the key length is invalid for the chosen algorithm, or a parameter is missing when encrypting or iv missing.
	 */
	public byte[] encryptData(byte[] plaintext, javax.crypto.SecretKey key) {
		byte[] ivBytes = new byte[16];
		byte[] cipherText = null;
		int mode = Cipher.ENCRYPT_MODE;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(ivBytes, "next").includeClass("javax.crypto.spec.IvParameterSpec")
			.addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(plaintext, "plainText").addParameter(key, "key")
			.addParameter(cipherText, "cipherText").generate();

		byte[] ret = new byte[ivBytes.length + cipherText.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(cipherText, 0, ret, ivBytes.length, cipherText.length);
		return ret;
	}

	/**
	 * Decrypt data.
	 *
	 * @param ciphertext the encrypted bytes.
	 * @param key the key.
	 * @param plain_off the input offset.
	 * @param len the input length.
	 * @returns the decrypted bytes.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the size of input data is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi implementation for the specified algorithm.
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 */
	public byte[] decryptData(byte[] ciphertext, javax.crypto.SecretKey key) {

		byte[] ivBytes = new byte[16];
		byte[] data = new byte[ciphertext.length - ivBytes.length];
		System.arraycopy(ciphertext, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(ciphertext, ivBytes.length, data, 0, data.length);
		
		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher")
			.addParameter(mode, "encmode").addParameter(data, "plainText").addParameter(key, "key").addParameter(res, "cipherText").generate();

		return res;
	}

}
