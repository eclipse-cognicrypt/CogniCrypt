/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

public class Signatures {

	private static final String rndNumberGenerator = "NativePRNG";
	private static final String keyPairGenerator = "ECDSA";
	private static final String signatureAlgorithm = "SHA256withECDSA";

	public static KeyPair getKey() throws NoSuchAlgorithmException {
		SecureRandom prng = SecureRandom.getInstance(rndNumberGenerator);
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(keyPairGenerator);
		kpg.initialize(256, prng);
		KeyPair pair = kpg.generateKeyPair();
		return pair;
	}

	public static byte[] sign(String msg, PrivateKey privKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sig = Signature.getInstance(signatureAlgorithm);
		sig.initSign(privKey);
		sig.update(msg.getBytes());
		return sig.sign();
	}

	public static boolean vfy(String msg, byte[] signature, PublicKey pubKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sig = Signature.getInstance(signatureAlgorithm);
		sig.initVerify(pubKey);
		sig.update(msg.getBytes());
		return sig.verify(signature);
	}

}
