package de.cognicrypt.codegenerator.crysl.templates;

import javax.crypto.SecretKey;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class EncryptionTemplate {
	
	public byte[] encrypt(byte[] data, SecretKey key) {
		byte[] ivb = new byte[32];
		byte[] res = null;
		
		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.SecureRandom").addParameter(ivb).
			considerCrySLRule("java.security.IVParameterSpec").addParameter(ivb).
			considerCrySLRule("javax.crypto.Cipher").addParameter(key).addParameter(data).addReturnObject(res)
			.generate(); 
		
		byte[] ret = new byte[ivb.length + res.length];
		System.arraycopy(ivb, 0, ret, 0, ivb.length);
		System.arraycopy(ivb, 0, ret, ivb.length, res.length);
		return ret;
	}

}
