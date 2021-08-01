/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryptionhybridstringsauth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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

	public byte[] encryptSessionKey(javax.crypto.SecretKey sessionKey, java.security.KeyPair keyPair) throws GeneralSecurityException {
		byte[] wrappedKeyBytes = null;
		int mode = Cipher.WRAP_MODE;
		java.security.PublicKey publicKey = keyPair.getPublic();
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(publicKey, "key").addParameter(sessionKey, "wrappedKey")
			.addParameter(wrappedKeyBytes, "wrappedKeyBytes").generate();
		return wrappedKeyBytes;
	}
	
	public javax.crypto.spec.SecretKeySpec getSecretKey(javax.crypto.SecretKey secretKey){
		
		javax.crypto.spec.SecretKeySpec secretKeySpec = null;
		
		byte[] key = secretKey.getEncoded();
		
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.SecretKeySpec").addParameter(key, "keyMaterial")
		.addParameter(secretKeySpec, "this").generate();

		return secretKeySpec;	
	}

	public java.lang.String encryptData(java.lang.String text, javax.crypto.SecretKey secretKey, byte[] ivBytes,
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

	public java.lang.String decryptData(java.lang.String encodedString, javax.crypto.SecretKey secretKey, byte[] ivBytes,
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
