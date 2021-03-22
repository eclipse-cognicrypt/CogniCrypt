/********************************************************************************
 * Copyright (c) 2015-2021 TU Darmstadt, Paderborn University
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
 * The Class SecureEncryptor provides hybrid string encryption.
 */
public class SecureEncryptor {

	/**
	 * Generates session key to encrypt and decrypt the data.
	 *
	 * @return the session key the secret key for symmetric encryption.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a KeyGeneratorSpi implementation for the specified algorithm.
	 */
	public javax.crypto.SecretKey generateSessionKey() throws NoSuchAlgorithmException {
		javax.crypto.SecretKey sessionKey = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.KeyGenerator").addParameter(sessionKey, "key").generate();
		return sessionKey;
	}
	
	/**
	 * Generates a key pair for a safe communication. The public part should be shared with the communication partner,
	 * then they will encrypt their session key with this public key. Their session key will be decrypted with the private part of this key pair.
	 * This way both partners have the same secret session key to encrypt and decrypt the communication partner's data.
	 * 
	 * @return the key pair.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a KeyPairGeneratorSpi implementation for the specified algorithm.
	 */
	public java.security.KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		java.security.KeyPair keyPair = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.KeyPairGenerator").addParameter(keyPair, "kp");
		return keyPair;
	}

	/**
	 * Encrypts the session key with the public part of communication partner's key pair. It will be
	 * encrypted with RSA algorithm.
	 *
	 * @param sessionKey the session key to encrypt and decrypt data through the communication.
	 * @param keyPair the key pair from the communication partner.
	 * @return encrypted session key.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the size of input data is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi implementation for the specified algorithm.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
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
	 * First converts the plaintext to bytes and then encrypts it with cipher using the input secret key and algorithm 
	 * specifications provided by initialized vector parameter (IvParameterSpec) from random bytes of same size as key. Copies the ivBytes and 
	 * the result of the encryption in a new byte array and encode it with Base64 to string.
	 * AES algorithm with a block size of 128 bits has been used to encrypt the data.
	 * 
	 * @param plaintext the input string to be encrypted.
	 * @param key the secret key for encryption, it also will be used for decryption.
	 * @param plain_off the offset in input text where the input starts. 0, if all bytes in plaintext need to be encrypted.
	 * @param len the length of the plaintext.
	 * @return a string that contains the ivBytes and the outcome of encryption.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the size of input data is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws IOException This exception is thrown if the character encoding is not supported, while converting string to bytes and vice versa.
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi or SecureRandomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
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
	 * Converts the ciphertext string to array bytes, then divides it into two parts, ivBytes and data. Then decrypts
	 * the data with the input secret key that was used to encrypt the data. Stores the result in a byte array,
	 * converts it to string and returns the string. The same algorithm from encryption has been used to decrypt
	 * , the AES algorithm with 128 bits block size.
	 *
	 * @param ciphertext The encrypted string to be decrypted. Includes ivBytes as first part and the encrypted data as the second part.
	 * @param key the secret key that was used for encryption.
	 * @param plain_off the offset in input ciphertext where the input starts. 0, if all bytes in ciphertext need to be decrypted.
	 * @param len the length of the ciphertext.
	 * @return the decrypted string.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the size of input data is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws IOException This exception is thrown if the character encoding is not supported, while converting string to bytes and vice versa.
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi or SecureRandomSpi implementation for the specified algorithms.
	 * @throws BadPaddingExceptionxception This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
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
