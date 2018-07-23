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
