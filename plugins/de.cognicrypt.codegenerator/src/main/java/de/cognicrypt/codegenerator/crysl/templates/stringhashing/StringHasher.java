/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.stringhashing;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class StringHasher {

	public static java.lang.String createHash(java.lang.String msg) throws GeneralSecurityException {
		byte[] plainBytes = msg.getBytes(StandardCharsets.UTF_8);
		byte[] out = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.MessageDigest").addParameter(out, "out").generate();
		return Base64.getEncoder().encodeToString(out);
	}

	public static boolean verifyHash(java.lang.String compareeHash, java.lang.String newMsg) {
		byte[] plainBytes = newMsg.getBytes(StandardCharsets.UTF_8);
		byte[] out = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.MessageDigest").addParameter(out , "out").generate();
		return Base64.getEncoder().encodeToString(out).equals(compareeHash);
	}

}
