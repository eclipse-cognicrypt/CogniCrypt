/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
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

public class SecureEncryptor {

	public java.security.KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		java.security.KeyPair keyPair = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.KeyPairGenerator").addParameter(keyPair, "kp");
		return keyPair;
	}

	public byte[] encryptData(byte[] plaintext, java.security.KeyPair keyPair) {
		byte[] cipherText = null;
		int mode = Cipher.ENCRYPT_MODE;
		// Note: You need to use the public key of your communication partner here, not your public key. 
		java.security.PublicKey pubKey = keyPair.getPublic();
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(plaintext, "plainText").addParameter(pubKey, "key")
			.addParameter(cipherText, "cipherText").generate();
		return cipherText;
	}

	public byte[] decrypt(byte[] ciphertext, java.security.KeyPair keyPair) {
		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		java.security.PrivateKey privateKey = keyPair.getPrivate();

		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(ciphertext, "plainText").addParameter(privateKey, "key")
			.addParameter(res, "cipherText").generate();
		return res;
	}

}
