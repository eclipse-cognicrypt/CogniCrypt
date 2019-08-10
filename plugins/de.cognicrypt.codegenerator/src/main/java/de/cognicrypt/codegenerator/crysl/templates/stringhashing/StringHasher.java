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
		
		CrySLCodeGenerator.getInstance().considerCrySLRule("java.security.MessageDigest").addParameter(plainBytes, "digest", 0).addReturnObject(out).generate();

		String hashString = Base64.getEncoder().encodeToString(out);

		return hashString;
	}

}
