/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryptionstringsauth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class SecureEncryptor for authenticated string symmetric encryption.
 */
public class SecureEncryptor {

	/**
	 * Gets a password to generate a key together with a random salt and
	 * hashes the key to create a secure secret key for later symmetric encryption or decryption.
	 *
	 * @param pwd the users chosen password for a password-based encryption (PBE).
	 * @return encryptionKey the secret key to be used for later encryption.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a SecretKeyFactorySpi or SecureRadnomSpi implementation for the specified algorithms.
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
	 * Constructs a secret key from given key material.
	 * 
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a SecretKeyFactorySpi or SecureRadnomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 */
	public javax.crypto.spec.SecretKeySpec getSecretKey(javax.crypto.SecretKey secretKey){
		
		javax.crypto.spec.SecretKeySpec secretKeySpec = null;
		
		byte[] key = secretKey.getEncoded();
		
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.SecretKeySpec").addParameter(key, "keyMaterial")
		.addParameter(secretKeySpec, "this").generate();

		return secretKeySpec;
		
	}
	
	/**
	 * Encrypts a plaintext with cipher using the input secret key and the provided initialized vector.
	 * Returns a byte array that contains the encrypted plaintext.
	 * AES algorithm in Galois/Counter mode has been used to encrypt the data.
	 * 
	 * @param text the text to be encrypted.
	 * @param ivBytes the bytes used for encryption.
	 * @param keySpec the constructed secret key.
	 * @return the result that contains the ivBytes and the outcome of encryption.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the input data size is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * @throws IOException This exception is thrown when an I/O error occurred.
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi or SecureRandomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 */
	public java.lang.String encrypt(java.lang.String text, byte[] ivBytes,
		javax.crypto.spec.SecretKeySpec keySpec) throws IOException {
		
		int mode = Cipher.ENCRYPT_MODE;
		
		int keysize = 128;
		
		java.lang.String transformation = "AES/GCM/NoPadding";

		byte[] plainText = text.getBytes(StandardCharsets.UTF_8);
		
		byte[] ciphertext = null;

		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.GCMParameterSpec")
			.addParameter(keysize, "tLen").addParameter(ivBytes, "src")
			.includeClass("javax.crypto.Cipher").addParameter(transformation, "transformation")
			.addParameter(mode, "encmode").addParameter(keySpec, "key")
			.addParameter(plainText, "plainText").addParameter(ciphertext, "ciphertext").generate();

        return Base64.getEncoder().encodeToString(ciphertext);	
	}
	
	
	/**
	 * Decrypts the data with the input secret key that was used to encrypt the data
	 * and the ivBytes. The ivBytes are the random bytes that the data was encrypted with it. 
	 * The same algorithm from encryption has been used to decrypt.
	 * 
	 * @param ciphertext the encrypted byte array to be decrypted.
	 * @param ivBytes the bytes used for encryption.
	 * @param keySpec constructed secret key.
	 * @return the decrypted data.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the input data size is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws NoSuchAlgorithmException  This exception is thrown if no provider supports a CipherSpi implementation for the specified algorithm.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 */
	public java.lang.String decrypt(java.lang.String encodedString, byte[] ivBytes,
		javax.crypto.spec.SecretKeySpec keySpec) throws IOException {

		int mode = Cipher.DECRYPT_MODE;
		
		int keysize = 128;
		
		java.lang.String transformation = "AES/GCM/NoPadding";
		
		byte[] ciphertext = Base64.getDecoder().decode(encodedString);

		byte[] plaintext = null;
		
		
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.GCMParameterSpec")
		.addParameter(keysize, "tLen").addParameter(ivBytes, "src")
		.includeClass("javax.crypto.Cipher").addParameter(transformation, "transformation")
		.addParameter(mode, "encmode").addParameter(keySpec, "key")
		.addParameter(ciphertext, "plainText").addParameter(plaintext, "ciphertext").generate();

		return new String(plaintext, StandardCharsets.UTF_8);
	}


}
