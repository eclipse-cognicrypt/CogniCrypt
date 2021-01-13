/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryption;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class SecureEncryptor encryption.
 */
public class SecureEncryptor {
	
	/**
	 * Gets the key.
	 *
	 * @param pwd The password.
	 * @returns encryptionKey the Secret key.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchAlgorithmException This exception is thrown when the algorithms that are being used are wrong.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 */
	public javax.crypto.SecretKey getKey(char[] pwd) {
		byte[] salt = new byte[32];
		javax.crypto.SecretKey encryptionKey = null;
		int keysize = 128;
		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "next").includeClass("javax.crypto.spec.PBEKeySpec")
			.addParameter(pwd, "password").addParameter(keysize, "keylength").includeClass("javax.crypto.SecretKeyFactory").includeClass("javax.crypto.SecretKey")
			.includeClass("javax.crypto.spec.SecretKeySpec").addParameter(encryptionKey, "this").generate();

		return encryptionKey;
	}
	

	/**
	 * Encrypt.
	 *
	 * @param plaintext text to be encrypted.
	 * @param key the SecretKey.
	 * @param plain_off the input offset.
	 * @param len the input length.
	 * @returns the byte[] encrypted text.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the input data size is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided (form cipher javadoc).
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi or SecureRandomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 */
	public byte[] encrypt(byte[] plaintext, javax.crypto.SecretKey key) {
		byte[] ivBytes = new byte[key.getEncoded().length];
		byte[] res = null;
		int mode = Cipher.ENCRYPT_MODE;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(ivBytes, "next").includeClass("javax.crypto.spec.IvParameterSpec")
			.addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(plaintext, "plainText").addParameter(key, "key")
			.addParameter(res, "cipherText").generate();

		byte[] ret = new byte[ivBytes.length + res.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(res, 0, ret, ivBytes.length, res.length);
		return ret;
	}

	/**
	 * Decrypt.
	 *
	 * @param ciphertext the encrypted text.
	 * @param key        the key.
	 * @param plain_off the input offset.
	 * @param len the input length.
	 * @returns the byte[].
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the input data size is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided (form cipher javadoc).
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws NoSuchAlgorithmException  This exception is thrown if no provider supports a CipherSpi implementation for the specified algorithm.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 */
	public byte[] decrypt(byte[] ciphertext, javax.crypto.SecretKey key) {

		byte[] ivBytes = new byte[key.getEncoded().length];
		byte[] data = new byte[ciphertext.length - ivBytes.length];
		System.arraycopy(ciphertext, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(ciphertext, ivBytes.length, data, 0, data.length);
 
		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher")
		.addParameter(mode, "encmode").addParameter(key, "key").addParameter(data, "plainText").addParameter(res, "cipherText").generate();

		return res;
	}

}
