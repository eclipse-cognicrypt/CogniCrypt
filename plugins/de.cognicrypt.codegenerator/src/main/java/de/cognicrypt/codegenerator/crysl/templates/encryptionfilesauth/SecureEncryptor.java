/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryptionfilesauth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class SecureEncryptor {

	public javax.crypto.SecretKey getKey(char[] pwd) {
		byte[] salt = new byte[32];
		
		javax.crypto.SecretKey encryptionKey = null;
		
		int keysize = 128;
		
		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "next").includeClass("javax.crypto.spec.PBEKeySpec")
			.addParameter(pwd, "password").addParameter(keysize, "keylength").includeClass("javax.crypto.SecretKeyFactory").includeClass("javax.crypto.SecretKey")
			.includeClass("javax.crypto.spec.SecretKeySpec").addParameter(encryptionKey, "this").generate();

		return encryptionKey;
	}
	
	public javax.crypto.spec.SecretKeySpec getSecretKey(javax.crypto.SecretKey secretKey){
		
		javax.crypto.spec.SecretKeySpec secretKeySpec = null;
		
		byte[] key = secretKey.getEncoded();
		
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.SecretKeySpec").addParameter(key, "keyMaterial")
		.addParameter(secretKeySpec, "this").generate();

		return secretKeySpec;
		
	}
	
	public java.io.File encrypt(java.io.File plaintext, javax.crypto.SecretKey secretKey, byte[] ivBytes,
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

	public java.io.File decrypt(java.io.File ciphertext, javax.crypto.SecretKey secretKey, byte[] ivBytes,
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
