/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryptionhybridfilesauth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;


/**
 * The Class SecureEncryptor provides authenticated hybrid file encryption.
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
	 * @return the java.security. key pair
	 * @throws GeneralSecurityException the general security exception
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public java.security.KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		java.security.KeyPair keyPair = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.KeyPairGenerator").addParameter(keyPair, "kp");
		return keyPair;
	}
	
	/**
	 * Constructs a secret key from given key material.
	 * 
	 * @param secretKey the given key material.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a SecretKeyFactorySpi or SecureRadnomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 */
	public javax.crypto.spec.SecretKeySpec generateSecretKey(javax.crypto.SecretKey secretKey){
		
		javax.crypto.spec.SecretKeySpec secretKeySpec = null;
		
		byte[] key = secretKey.getEncoded();
		
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.SecretKeySpec").addParameter(key, "keyMaterial")
		.addParameter(secretKeySpec, "this").generate();

		return secretKeySpec;
		
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
	 * Encrypts a file with cipher using the input secret key and the provided initialized vector.
	 * Returns a byte array that contains the encrypted plaintext.
	 * AES algorithm in Galois/Counter mode has been used to encrypt the data.
	 * 
	 * @param plaintext text to be encrypted.
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
	public java.io.File encryptData(java.io.File plaintext, byte[] ivBytes,
		javax.crypto.spec.SecretKeySpec keySpec) throws IOException {
		
		int mode = Cipher.ENCRYPT_MODE;
		
		int keysize = 128;
		
		java.lang.String transformation = "AES/GCM/NoPadding";

		byte[] plaintextFile = Files.readAllBytes(Paths.get(plaintext.getAbsolutePath()));
		
		byte[] ciphertext = null;

		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.GCMParameterSpec")
			.addParameter(keysize, "tLen").addParameter(ivBytes, "src")
			.includeClass("javax.crypto.Cipher").addParameter(transformation, "transformation")
			.addParameter(mode, "encmode").addParameter(keySpec, "key")
			.addParameter(plaintextFile, "plainText").addParameter(ciphertext, "ciphertext").generate();

		Files.write(Paths.get(plaintext.getAbsolutePath()), ciphertext);
		return plaintext;
	}

	/**
	 * Decrypts the data with the input secret key that was used to encrypt the data
	 * and the ivBytes. The ivBytes are the random bytes that the data was encrypted with it. 
	 * The same algorithm from encryption has been used to decrypt.
	 * 
	 * @param ciphertext the encrypted byte array to be decrypted.
	 * @param ivBytes the bytes used for encryption.
	 * @param keySpec the constructed secret key.
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
	public java.io.File decryptData(java.io.File ciphertext, byte[] ivBytes,
		javax.crypto.spec.SecretKeySpec keySpec) throws IOException {

		int mode = Cipher.DECRYPT_MODE;
		
		int keysize = 128;
		
		java.lang.String transformation = "AES/GCM/NoPadding";
		
		byte[] ciphertextFile = Files.readAllBytes(Paths.get(ciphertext.getAbsolutePath()));

		byte[] plaintext = null;
		
		
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.GCMParameterSpec")
		.addParameter(keysize, "tLen").addParameter(ivBytes, "src")
		.includeClass("javax.crypto.Cipher").addParameter(transformation, "transformation")
		.addParameter(mode, "encmode").addParameter(keySpec, "key")
		.addParameter(ciphertextFile, "plainText").addParameter(plaintext, "ciphertext").generate();
		
		Files.write(Paths.get(ciphertext.getAbsolutePath()), plaintext);
		return ciphertext;
	}

}
