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

