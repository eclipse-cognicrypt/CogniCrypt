package de.cognicrypt.codegenerator.crysl.templates.asymmetricencryption;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class SecureEncryptor {
	
	public java.security.KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		java.security.KeyPair keyPair = null;
		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.KeyPairGenerator").addReturnObject(keyPair);
		return keyPair;
	}
	
	public byte[] encryptData(byte[] plaintext, java.security.KeyPair keyPair) {
		byte[] cipherText = null;
		int mode = Cipher.ENCRYPT_MODE;
		// Note: You need to use the public key of your communication partner here, not your public key. 
		java.security.PublicKey pubKey = keyPair.getPublic();
		
		CrySLCodeGenerator.getInstance().considerCrySLRule("javax.crypto.Cipher").addParameter(mode, "init", 0).addParameter(plaintext,"doFinal", 0).addParameter(pubKey, "init", 1)
		.addReturnObject(cipherText).generate(); 
		return cipherText;
	}
	
	public byte[] decrypt(byte[] ciphertext, java.security.KeyPair keyPair) {
		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		java.security.PrivateKey privateKey = keyPair.getPrivate();
		
		CrySLCodeGenerator.getInstance().considerCrySLRule("javax.crypto.Cipher").addParameter(mode, "init", 0).addParameter(ciphertext,"doFinal", 0)
		.addParameter(privateKey, "init", 1).addReturnObject(res).generate(); 
		return res;
	}

}
