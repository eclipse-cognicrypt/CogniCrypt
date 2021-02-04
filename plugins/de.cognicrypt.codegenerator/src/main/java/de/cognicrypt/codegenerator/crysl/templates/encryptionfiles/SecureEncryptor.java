/********************************************************************************
 * Copyright (c) 2015-2021 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryptionfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class SecureEncryptor for encrypting a file.
 */
public class SecureEncryptor {

	/**
	 * Gets a password to generate a key together with a random salt and
	 * hashes the key to create a secure secret key for later symmetric encryption or decryption.
	 *
	 * @param pwd the user's chosen password for a password-based encryption (PBE).
	 * @returns encryptionKey the secret key to be used for later encryption.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a SecretKeyFactorySpi or SecureRadnomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 */
	public javax.crypto.SecretKey generateKey(char[] pwd) {
		byte[] salt = new byte[32];
		javax.crypto.SecretKey encryptionKey = null;
		int keysize = 128;
		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "next").includeClass("javax.crypto.spec.PBEKeySpec")
			.addParameter(pwd, "password").addParameter(keysize, "keylength").includeClass("javax.crypto.SecretKeyFactory").includeClass("javax.crypto.SecretKey")
			.includeClass("javax.crypto.spec.SecretKeySpec").addParameter(encryptionKey, "this").generate();

		return encryptionKey;
	}
	
	/**
	 * First reads all bytes of the plaintext file and then encrypts it with cipher using the input secret key and algorithm specifications provided by
	 * initialized vector parameter (IvParameterSpec) from random bytes. Copies the ivBytes and the result of the encryption
	 * in a new byte array and write it to a file where plaintext was located and returns the file. AES algorithm with a 
	 * block size of 128 bits has been used to encrypt the data.
	 *
	 * @param plaintext the input file to be encrypted.
	 * @param key the secret key for encryption, it also will be used for decryption.
	 * @param plain_off the offset in input text where the input starts. 0, if all bytes in plaintext need to be encrypted.
	 * @param len the length of the plaintext.
	 * @returns a file that contains the byte with ivBytes and the outcome of encryption.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the size of input data is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws IOException This exception is thrown when the file cannot be read or written in.
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi or SecureRandomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 */
	public java.io.File encrypt(java.io.File plaintext, javax.crypto.SecretKey key) throws IOException {
		byte[] ivBytes = new byte[key.getEncoded().length];
		byte[] res = null;
		byte[] plaintextFile = Files.readAllBytes(Paths.get(plaintext.getAbsolutePath()));
		int mode = Cipher.ENCRYPT_MODE;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(ivBytes, "next").includeClass("javax.crypto.spec.IvParameterSpec")
			.addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(plaintextFile, "plainText").addParameter(key, "key")
			.addParameter(res, "cipherText").generate();

		byte[] ret = new byte[ivBytes.length + res.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(res, 0, ret, ivBytes.length, res.length);
		Files.write(Paths.get(plaintext.getAbsolutePath()), ret);
		return plaintext;
	}
	
	/**
	 * Reads all bytes in the ciphertext, then divides it into two parts, ivBytes and data. Then decrypts
	 * the data with the input secret key that was used to encrypt the data. Writes the result into a file where
	 * the ciphertext was located and returns the file. The same algorithm from encryption has been used to decrypt
	 * , the AES algorithm with 128 bits block size.
	 *
	 * @param ciphertext the encrypted file to be decrypted. Includes ivBytes as first part and the encrypted data as the second part.
	 * @param key the secret key that was used for encryption.
	 * @param plain_off the offset in input ciphertext where the input starts. 0, if all bytes in ciphertext need to be decrypted.
	 * @param len the length of the ciphertext.
	 * @returns the decrypted file.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the size of input data is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws IOException This exception is thrown when the file cannot be read or written in.
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi or SecureRandomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 */
	public java.io.File decrypt(java.io.File ciphertext, javax.crypto.SecretKey key) throws IOException {
		byte[] ciphertextFile = Files.readAllBytes(Paths.get(ciphertext.getAbsolutePath()));
		byte[] ivBytes = new byte[key.getEncoded().length];
		byte[] data = new byte[ciphertextFile.length - ivBytes.length];
		System.arraycopy(ciphertextFile, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(ciphertextFile, ivBytes.length, data, 0, data.length);

		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher")
			.addParameter(mode, "encmode").addParameter(data, "plainText").addParameter(key, "key").addParameter(res, "cipherText").generate();

		Files.write(Paths.get(ciphertext.getAbsolutePath()), res);
		return ciphertext;
	}

}
