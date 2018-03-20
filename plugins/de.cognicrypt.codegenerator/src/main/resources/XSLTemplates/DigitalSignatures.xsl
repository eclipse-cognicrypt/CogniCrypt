<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:if test="//task[@description='DigitalSignatures']">
	
<xsl:result-document href="EcdsaSignature.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>

public class EcdsaSignature {

	private static final String rndNumberGenerator = "NativePRNG";
	private static final String keyPairGenerator = "EC";
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
		Signature ecdsa = Signature.getInstance(signatureAlgorithm);
		ecdsa.initSign(privKey);
		ecdsa.update(msg.getBytes());
		return ecdsa.sign();
	}

	public static boolean vfy(String msg, byte[] signature, PublicKey pubKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature ecdsa = Signature.getInstance(signatureAlgorithm);
		ecdsa.initVerify(pubKey);
		ecdsa.update(msg.getBytes());
		return ecdsa.verify(signature);
	}

}

</xsl:result-document>

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {

	public static void templateUsage() throws GeneralSecurityException {
	
		// key generation
		KeyPair pair = EcdsaSignature.getKey();

		// signing
		String msg = "Zehn zahme Ziegen zogen zehn Zentner Zucker zum Zoo.";
		byte[] signature = EcdsaSignature.sign(msg, pair.getPrivate());

		// verification
		if (EcdsaSignature.vfy(msg, signature, pair.getPublic())) {
			System.out.println("Signature verification successful");
		} else {
			System.out.println("Signature verification failed");
		}

	}
}

</xsl:if>

</xsl:template>

<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>

</xsl:stylesheet>
