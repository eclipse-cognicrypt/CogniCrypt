<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:variable name="Rounds"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations"/> </xsl:variable>
<xsl:variable name="outputSize"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/outputSize"/> </xsl:variable>



<xsl:if test="//task[@description='SecurePassword']">

<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">
<xsl:result-document href="PWHasher.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */
public class PWHasher {	
	//adopted code from https://github.com/defuse/password-hashing
	
	public static String createPWHash(char[] pwd) throws GeneralSecurityException { 
		byte[] salt = new byte[<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/outputSize"/>/8];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		
		PBEKeySpec spec = new PBEKeySpec(pwd, salt, 65536, <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/outputSize"/>);
		SecretKeyFactory f = SecretKeyFactory.getInstance("<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/name"/>WithHmac<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/name"/><xsl:choose><xsl:when test="$outputSize > 200"> <xsl:value-of select="$outputSize"/> </xsl:when>
         <xsl:otherwise>1</xsl:otherwise></xsl:choose>");
		String pwdHash = toBase64(salt) + ":" + toBase64(f.generateSecret(spec).getEncoded());
		spec.clearPassword();
		return pwdHash;
	}
	
	public static boolean verifyPWHash(char[] pwd, String pwdhash) throws GeneralSecurityException {
		String[] parts = pwdhash.split(":");
		byte[] salt = fromBase64(parts[0]);

		PBEKeySpec spec = new PBEKeySpec(pwd, salt, 65536, <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/outputSize"/>);
		SecretKeyFactory f = SecretKeyFactory.getInstance("<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/name"/>WithHmac<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/name"/><xsl:choose><xsl:when test="$outputSize > 200"> <xsl:value-of select="$outputSize"/> </xsl:when>
         <xsl:otherwise>1</xsl:otherwise></xsl:choose>");
		Boolean areEqual = slowEquals(f.generateSecret(spec).getEncoded(), fromBase64(parts[1]));
		spec.clearPassword();
		return areEqual;
	}
	
	private static boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i &lt; a.length <xsl:text disable-output-escaping="yes"><![CDATA[&&]]></xsl:text> i &lt; b.length; i++) {
			diff |= a[i] ^ b[i];
		}	
		return diff == 0;
	}

	private static String toBase64(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}

	private static byte[] fromBase64(String hash) {
		return DatatypeConverter.parseBase64Binary(hash);
	}
}
</xsl:result-document>
</xsl:if>

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public static void templateUsage(char[] pwd) throws GeneralSecurityException  {
		//In order to store a password, it is hashed. 
		String pwdHash = PWHasher.createPWHash(pwd); // This call hashes the password pwd.
		boolean t = PWHasher.verifyPWHash(pwd, pwdHash); // This call verifies that the password pwd belongs to the password hash pwdHash
	}
}
</xsl:if>


</xsl:template>

<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>

</xsl:stylesheet>
