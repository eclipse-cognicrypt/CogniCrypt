package de.cognicrypt.codegenerator.crysl.templates.stringhashing;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class StringHasher {

	public static java.lang.String createHash(String msg) throws GeneralSecurityException {
		byte[] plainBytes = msg.getBytes(StandardCharsets.UTF_8);
		byte[] out = null; 
		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.MessageDigest").addReturnObject(out).generate();
		return Base64.getEncoder().encodeToString(out);
	}
	
	public static boolean verifyHash(java.lang.String newMsg, java.lang.String compareeHash) {
		byte[] plainBytes = newMsg.getBytes(StandardCharsets.UTF_8);
		byte[] out = null; 
		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.MessageDigest").addReturnObject(out).generate();
		return Base64.getEncoder().encodeToString(out).equals(newMsg);
	}
	

}
