<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:if test="//task[@description='DigitalSignatures']">

<xsl:variable name="keysize"> <xsl:value-of select="//task/element[@type='DigitalSignatures']/Keysize"/> </xsl:variable>

<xsl:variable name="keyPairGenerator">
	<xsl:choose>
		<xsl:when test="//task/element[@type='DigitalSignatures']/Scheme='RSA'">RSA</xsl:when>
		<xsl:otherwise>EC</xsl:otherwise>
	</xsl:choose>
</xsl:variable>

<xsl:variable name="signatureAlgorithm">
	<xsl:choose>
		<xsl:when test="//task/element[@type='DigitalSignatures']/Scheme='RSA'">SHA256withRSA</xsl:when>
		<xsl:otherwise>SHA256withECDSA</xsl:otherwise>
	</xsl:choose>
</xsl:variable>

<xsl:result-document href="EcdsaSignature.java">
package <xsl:value-of select="//task/Package"/>;
<xsl:apply-templates select="//Import"/>

public class EcdsaSignature {

	private static final String rndNumberGenerator = "NativePRNG";
	private static final String keyPairGenerator = "<xsl:value-of select="$keyPairGenerator"/>";
	private static final String signatureAlgorithm = "<xsl:value-of select="$signatureAlgorithm"/>";

	public static KeyPair getKey() throws NoSuchAlgorithmException {
		SecureRandom prng = SecureRandom.getInstance(rndNumberGenerator);
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(keyPairGenerator);
		kpg.initialize(<xsl:value-of select="$keysize"/>, prng);
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

	<xsl:if test="//task/code/signingAndVerification='both'">
	public static boolean vfy(String msg, byte[] signature, PublicKey pubKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature ecdsa = Signature.getInstance(signatureAlgorithm);
		ecdsa.initVerify(pubKey);
		ecdsa.update(msg.getBytes());
		return ecdsa.verify(signature);
	}
	</xsl:if>

}

</xsl:result-document>

package <xsl:value-of select="//Package"/>;
<xsl:apply-templates select="//Import"/>
public class Output {

	public static void templateUsage() throws GeneralSecurityException {

		// key generation
		KeyPair pair = EcdsaSignature.getKey();

		// message
		String msg = "Zehn zahme Ziegen zogen zehn Zentner Zucker zum Zoo.";

		// signing
		byte[] signature = EcdsaSignature.sign(msg, pair.getPrivate());

		<xsl:if test="//task/code/signingAndVerification='both'">
		// verification
		if (EcdsaSignature.vfy(msg, signature, pair.getPublic())) {
			System.out.println("Signature verification successful");
		} else {
			System.out.println("Signature verification failed");
		}
		</xsl:if>

	}
}

</xsl:if>

</xsl:template>

<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>

</xsl:stylesheet>
