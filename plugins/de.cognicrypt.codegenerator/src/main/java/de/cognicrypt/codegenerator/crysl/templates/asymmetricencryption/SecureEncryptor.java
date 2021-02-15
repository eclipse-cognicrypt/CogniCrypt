/********************************************************************************
 * Copyright (c) 2015-2021 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.asymmetricencryption;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class SecureEncryptor provides an asymmetric encryption.
 */
public class SecureEncryptor {

	/**
	 * Generates a key pair for later encryption and decryption. The public part
	 * will be used to encrypt the data and the private part to decrypt it.
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
	 * Gets the public key of the communication partner's key pair and encrypts the plaintext.
	 *
	 * @param plaintext the text to be encrypted.
	 * @param keyPair the key pair of the communication partner, not this user's key pair.
	 * @return the cipherText the encrypted data.
	 */
	public byte[] encryptData(byte[] plaintext, java.security.KeyPair keyPair) {
		byte[] cipherText = null;
		int mode = Cipher.ENCRYPT_MODE;
		// Note: You need to use the public key of your communication partner here, not your public key. 
		java.security.PublicKey pubKey = keyPair.getPublic();
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(plaintext, "plainText").addParameter(pubKey, "key")
			.addParameter(cipherText, "cipherText").generate();
		return cipherText;
	}

	/**
	 * Gets the private key of this user's key pair and decrypts the cipherText.
	 *
	 * @param ciphertext the ciphertext to be decrypted.
	 * @param keyPair the key pair of this user.
	 * @return the decrypted data.
	 */
	public byte[] decrypt(byte[] ciphertext, java.security.KeyPair keyPair) {
		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		java.security.PrivateKey privateKey = keyPair.getPrivate();

		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(ciphertext, "plainText").addParameter(privateKey, "key")
			.addParameter(res, "cipherText").generate();
		return res;
	}

}
