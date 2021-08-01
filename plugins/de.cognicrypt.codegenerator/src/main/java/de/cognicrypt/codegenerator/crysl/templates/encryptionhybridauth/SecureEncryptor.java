/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryptionhybridauth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class SecureEncryptor {

	public javax.crypto.SecretKey generateSessionKey() throws NoSuchAlgorithmException {
		javax.crypto.SecretKey sessionKey = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.KeyGenerator").addParameter(sessionKey, "key").generate();
		return sessionKey;
	}

	public java.security.KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		java.security.KeyPair keyPair = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.KeyPairGenerator").addParameter(keyPair, "kp");
		return keyPair;
	}

	public javax.crypto.spec.SecretKeySpec generateSecretKey(javax.crypto.SecretKey secretKey){
		
		javax.crypto.spec.SecretKeySpec secretKeySpec = null;
		
		byte[] key = secretKey.getEncoded();
		
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.SecretKeySpec").addParameter(key, "keyMaterial")
		.addParameter(secretKeySpec, "this").generate();

		return secretKeySpec;
		
	}
	
	public byte[] encryptSessionKey(javax.crypto.SecretKey sessionKey, java.security.KeyPair keyPair) throws GeneralSecurityException {
		byte[] wrappedKeyBytes = null;
		int mode = Cipher.WRAP_MODE;
		java.security.PublicKey publicKey = keyPair.getPublic();
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(publicKey, "key").addParameter(sessionKey, "wrappedKey")
			.addParameter(wrappedKeyBytes, "wrappedKeyBytes").generate();
		return wrappedKeyBytes;
	}

	public byte[] encryptData(byte[] plaintext, javax.crypto.SecretKey secretKey, byte[] ivBytes,
		javax.crypto.spec.SecretKeySpec keySpec) {
		int mode = Cipher.ENCRYPT_MODE;
		
		int keysize = 128;
		
		java.lang.String transformation = "AES/GCM/NoPadding";

		byte[] ciphertext = null;

		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.GCMParameterSpec")
			.addParameter(keysize, "tLen").addParameter(ivBytes, "src")
			.includeClass("javax.crypto.Cipher").addParameter(transformation, "transformation")
			.addParameter(mode, "encmode").addParameter(keySpec, "key")
			.addParameter(plaintext, "plainText").addParameter(ciphertext, "ciphertext").generate();

        return ciphertext;	
	}

	public byte[] decryptData(byte[] ciphertext, javax.crypto.SecretKey secretKey, byte[] ivBytes,
		javax.crypto.spec.SecretKeySpec keySpec) throws IOException {

		int mode = Cipher.DECRYPT_MODE;
		
		int keysize = 128;
		
		java.lang.String transformation = "AES/GCM/NoPadding";

		byte[] plaintext = null;
		
		
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.GCMParameterSpec")
		.addParameter(keysize, "tLen").addParameter(ivBytes, "src")
		.includeClass("javax.crypto.Cipher").addParameter(transformation, "transformation")
		.addParameter(mode, "encmode").addParameter(keySpec, "key")
		.addParameter(ciphertext, "plainText").addParameter(plaintext, "ciphertext").generate();

		return plaintext;
	}

}
