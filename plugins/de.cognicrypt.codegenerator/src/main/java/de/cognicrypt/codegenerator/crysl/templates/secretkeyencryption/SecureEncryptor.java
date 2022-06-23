/********************************************************************************
 * Copyright (c) 2015-2021 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.secretkeyencryption;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class SecureEncryptor provides encryption with secret key, no password required.
 */
public class SecureEncryptor {

	/**
	 * Generates session key for encryption and decryption of data.
	 *
	 * @return the secret key.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a KeyGeneratorSpi implementation for the specified algorithm.
	 */
	public javax.crypto.SecretKey generateSessionKey() throws NoSuchAlgorithmException {
		javax.crypto.SecretKey encryptionKey = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.KeyGenerator").addParameter(encryptionKey, "key").generate();
		return encryptionKey;
	}
	
	/**
	 * Encrypts a plaintext with cipher using the input secret key and algorithm specifications provided by
	 * initialized vector parameter (IvParameterSpec) from random bytes of same size as key. Returns a byte array that contains
	 * the ivBytes in the first part and the encrypted plaintext on the second part. AES algorithm with a 
	 * block size of 128 bits has been used to encrypt the data.
	 *
	 * @param plaintext the plaintext
	 * @param key the key
	 * @return the byte[]
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
	 * Reads all bytes in the ciphertext, then divides it into two parts, ivBytes and data. Then decrypts
	 * the data with the input secret key that was used to encrypt the data. Writes the result into a file where
	 * the ciphertext was located and returns the file. The same algorithm from encryption has been used to decrypt
	 * , the AES algorithm with 128 bits block size.
	 *
	 * @param ciphertext the ciphertext
	 * @param key the key
	 * @return the byte[]
	 */
	public byte[] decrypt(byte[] ciphertext, javax.crypto.SecretKey key) {

		byte[] ivBytes = new byte[key.getEncoded().length];
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
