/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

public class Output {

	public static void templateUsage() throws GeneralSecurityException {

		// key generation
		KeyPair pair = Signatures.getKey();

		// message
		String msg = "Zehn zahme Ziegen zogen zehn Zentner Zucker zum Zoo.";

		// signing
		byte[] signature = Signatures.sign(msg, pair.getPrivate());

		// verification
		if (Signatures.vfy(msg, signature, pair.getPublic())) {
			System.out.println("Signature verification successful");
		} else {
			System.out.println("Signature verification failed");
		}

	}
}

