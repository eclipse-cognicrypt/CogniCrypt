<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:variable name="Rounds"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations"/> </xsl:variable>

<xsl:if test="//task/algorithm[@type='SymmetricBlockCipher']">
<xsl:result-document href="Enc.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>

public class Enc {	
	
	public byte[] encrypt(byte [] stuff, SecretKeySpec key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException { 
		byte [] ivb = new byte [16];
	    SecureRandom.getInstance("SHA1PRNG").nextBytes(ivb);
	    IvParameterSpec iv = new IvParameterSpec(ivb);
		
		Cipher c = Cipher.getInstance("<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/mode"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/padding"/>");
		
		c.init(Cipher.ENCRYPT_MODE, key, iv);
		byte [] res = c.doFinal(stuff);
		byte [] ret = new byte[res.length + ivb.length];
		System.arraycopy(ivb, 0, ret, 0, ivb.length);
		System.arraycopy(res, 0, ret, ivb.length, ret.length);
		return ret;
	}
}
</xsl:result-document>
</xsl:if>

<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">
<xsl:result-document href="KeyDeriv.java">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>

public class KeyDeriv {
	
	public SecretKeySpec getKey(String pwd) throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecureRandom r = new SecureRandom();
		byte[] salt = new byte[16];
		r.nextBytes(salt);
		
		PBEKeySpec spec = new PBEKeySpec(pwd.toCharArray(), salt, <xsl:choose>
         <xsl:when test="$Rounds > 1000"> <xsl:value-of select="$Rounds"/> </xsl:when>
         <xsl:otherwise> 1000 </xsl:otherwise>
		 </xsl:choose>, <xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/keySize"/>);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/name"/>");
		
		return new SecretKeySpec(skf.generateSecret(spec).getEncoded(), "<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>" );
	}
}
</xsl:result-document>
</xsl:if>

<xsl:if test="//task[@description='Encrypt data based on password']">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public byte[] run(byte[] file<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">, String pwd</xsl:if>) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException  {
		 <xsl:choose>
         <xsl:when test="//task/algorithm[@type='KeyDerivationAlgorithm']">KeyDeriv kd = new KeyDeriv();
		 SecretKeySpec key = kd.getKey(pwd); </xsl:when>
         <xsl:otherwise>SecretKeySpec key = getKey(); </xsl:otherwise>
		 </xsl:choose>		
		
		Enc enc = new Enc();
		return enc.encrypt(file, key);
	}
}
</xsl:if>

</xsl:template>
	
<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>
</xsl:stylesheet>
