/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryptionhybridstrings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class SecureEncryptor provides a hybrid string encryption.
 */
public class SecureEncryptor {

	/**
	 * Generate session key.
	 *
	 * @return SessionKey the secret key.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchAlgorithmException This exception is thrown when the algorithm in KeyGenerator is wrong.
	 */
	public javax.crypto.SecretKey generateSessionKey() throws NoSuchAlgorithmException {
		javax.crypto.SecretKey sessionKey = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.KeyGenerator").addParameter(sessionKey, "key").generate();
		return sessionKey;
	}
	/**
	 * Generate key pair.
	 *
	 * @return key pair.
	 * @throws GeneralSecurityException This exception can happen if RSA cipher cannot be retrieved.
	 * @throws NoSuchAlgorithmException This exception is thrown when KeyPairGenerator uses wrong algorithm.
	 */
	public java.security.KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		java.security.KeyPair keyPair = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.KeyPairGenerator").addParameter(keyPair, "kp");
		return keyPair;
	}
	/**
	 * Encrypt session key.
	 *
	 * @param sessionkey the secret key.
	 * @param key pair.
	 * @returns wrappedKeyBytes the Encrypted session key.
	 * @throws GeneralSecurityException the general security exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding for Cipher is not supported.
	 * @throws IllegalBlockSizeException This exception is thrown when the input data (data that is being decoded or encoded) size is not a multiple of the block-size or if this encryption algorithm is unable to process the input data provided.
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
	 * @param plaintext text to be encrypted.
	 * @param key the secret key.
	 * @return encrypted string.
	 * @throws GeneralSecurityException the general security exception
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * 		  
	 * @throw IllegalBlockSizeException This exception is thrown when the input data (data that is being decoded or encoded) size is not a multiple of the block-size.
	 * @throws IOException This exception is thrown when the charset name is wrong.
	 * 		  
	 * @throws NoSuchAlgorithmException This exception is thrown when cipher object is created using padding or modes that are not supported by chosen algorithm.
	 * 		   see {@link javax.crypto.Cipher#getInstance(String) getInstance()}
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown when the key length is invalid for the chosen algorithm, or a parameter is missing when encrypting or iv missing.
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 */
	public java.lang.String encryptData(java.lang.String plaintext, javax.crypto.SecretKey key) throws IOException {
		byte[] ivBytes = new byte[16];
		byte[] cipherText = null;
		byte[] plaintextString = plaintext.getBytes("UTF-8");
		int mode = Cipher.ENCRYPT_MODE;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(ivBytes, "next").includeClass("javax.crypto.spec.IvParameterSpec")
			.addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(plaintextString, "plainText").addParameter(key, "key")
			.addParameter(cipherText, "cipherText").generate();

		byte[] ret = new byte[ivBytes.length + cipherText.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(cipherText, 0, ret, ivBytes.length, cipherText.length);
		return Base64.getEncoder().encodeToString(ret);

	}
	/**
	 * Decrypt data.
	 *
	 * @param Ciphertext encrypted string.
	 * @param key the secret key.
	 * @return decrypted string.
	 * 
	 * @throws GeneralSecurityException This exception is thrown when the algorithm requested in Cipher.getInstance() is not available in the underlying library.
	 * 		   see {@link javax.crypto.Cipher#getInstance(String) getInstance(CipherAlgorithm)}
	 * 		   this can also be thrown of invalid ParameterSpec 
	 * 		   see {@link javax.crypto.spec.IvParameterSpec#IvParameterSpec(byte[]) IvParameterSpec()}!!!!!!!!
	 * 
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * 		   see {@link javax.crypto.Cipher#getInstance(String) getInstance(Padding)}
	 * @throws IllegalBlockSizeException This exception is thrown when the input data (data that is being decoded or encoded) size is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * 
	 * @throws IOException This exception is thrown when the charset name is wrong.
	 * 		   see {@link java.lang.String.String#String(byte[], java.lang.String) charsetName}
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi implementation for the specified algorithm.
	 * 		   see {@link javax.crypto.Cipher#getInstance(String) getInstance()}
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 * 		   see {@link javax.crypto.Cipher#init(int, javax.crypto.SecretKey, javax.crypto.spec.IvParameterSpec); init()}
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 *		   see {@link javax.crypto.Cipher#init(int, javax.crypto.SecretKey, javax.crypto.spec.IvParameterSpec); init()}
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 */
	public java.lang.String decryptData(java.lang.String ciphertext, javax.crypto.SecretKey key) throws IOException {
		byte[] ciphertextString = Base64.getDecoder().decode(ciphertext);
		byte[] ivBytes = new byte[16];
		byte[] data = new byte[ciphertextString.length - ivBytes.length];
		System.arraycopy(ciphertextString, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(ciphertextString, ivBytes.length, data, 0, data.length);

		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher")
			.addParameter(mode, "encmode").addParameter(data, "plainText").addParameter(key, "key").addParameter(res, "cipherText").generate();
		return new String(res, "UTF-8");
	}

}
