<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:variable name="Rounds"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations"/> </xsl:variable>
<xsl:variable name="outputSize"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/outputSize"/> </xsl:variable>

<xsl:if test="//task/algorithm[@type='SymmetricBlockCipher']">
<xsl:result-document href="SymmetricEnc.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */
public class SymmetricEnc {	
		<xsl:choose>
		<xsl:when test="//task/code/dataType='File'">
		public File encrypt(File file, SecretKey key) throws GeneralSecurityException, IOException { 
		</xsl:when>  
		<xsl:when test="//task/code/dataType='String'">
		public String encrypt(String message, SecretKey key) throws GeneralSecurityException, UnsupportedEncodingException { 
		</xsl:when>      
        <xsl:otherwise>
		public byte[] encrypt(byte[] data, SecretKey key) throws GeneralSecurityException { 
		</xsl:otherwise>
		</xsl:choose>	
		byte [] ivb = new byte [16];
	    SecureRandom.getInstanceStrong().nextBytes(ivb);
	    IvParameterSpec iv = new IvParameterSpec(ivb);
		
		Cipher c = Cipher.getInstance("<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/mode"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/padding"/>");
		c.init(Cipher.ENCRYPT_MODE, key, iv);
		<xsl:choose>
		<xsl:when test="//task/code/dataType='File'">
		byte[] data = Files.readAllBytes(file.toPath());	
		</xsl:when>
		<xsl:when test="//task/code/dataType='String'">
		byte[] data = message.getBytes("UTF-8");
		</xsl:when>
		</xsl:choose>
		<xsl:choose>
		<xsl:when test="//task/code/textsize='false'">
		byte[] res = c.doFinal(data);
		</xsl:when>        
         <xsl:otherwise>
         int conv_len = 0;
         byte[] res = new byte[c.getOutputSize(data.length)];
         for (int i = 0; i + 1024 &lt;= data.length; i += 1024) {
			byte[] input = new byte[1024];
			System.arraycopy(data, i, input, 0, 1024);
			conv_len += c.update(input, 0, input.length, res, i);
		}
		conv_len += c.doFinal(data, conv_len, data.length-conv_len, res, conv_len);
        </xsl:otherwise>
		</xsl:choose>
		byte [] ret = new byte[res.length + ivb.length];
		System.arraycopy(ivb, 0, ret, 0, ivb.length);
		System.arraycopy(res, 0, ret, ivb.length, res.length);
		<xsl:choose>	
		<xsl:when test="//task/code/dataType='File'">
		Files.write(file.toPath(), ret);
		return file;
		</xsl:when>
		<xsl:when test="//task/code/dataType='String'">
		return Base64.getEncoder().encodeToString(ret);
		</xsl:when>
		<xsl:otherwise>
		return ret;		
		</xsl:otherwise>
		</xsl:choose>
	}
	
	<xsl:choose>
		<xsl:when test="//task/code/dataType='File'">
		public File decrypt(File file, SecretKey key) throws GeneralSecurityException, IOException { 
		</xsl:when>  
		<xsl:when test="//task/code/dataType='String'">
		public String decrypt(String message, SecretKey key) throws GeneralSecurityException { 
		</xsl:when>      
        <xsl:otherwise>
		public byte[] decrypt(byte [] ciphertext, SecretKey key) throws GeneralSecurityException { 
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="//task/code/dataType='File'">
		byte[] ciphertext = Files.readAllBytes(file.toPath());
		</xsl:when>
		<xsl:when test="//task/code/dataType='String'">
		byte[] ciphertext = Base64.getDecoder().decode(message);
		</xsl:when>
	</xsl:choose>
		byte [] ivb = new byte [16];
		System.arraycopy(ciphertext, 0, ivb, 0, ivb.length);
	    IvParameterSpec iv = new IvParameterSpec(ivb);
		byte[] data = new byte[ciphertext.length - ivb.length];
		System.arraycopy(ciphertext, ivb.length, data, 0, data.length);
		
		Cipher c = Cipher.getInstance("<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/mode"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/padding"/>");
		c.init(Cipher.DECRYPT_MODE, key, iv);
	<xsl:choose>
		<xsl:when test="//task/code/textsize='false'">
		byte[] res = c.doFinal(data);
		</xsl:when>        
        <xsl:otherwise>
         int conv_len = 0;
         byte[] res = new byte[c.getOutputSize(data.length)];
         for (int i = 0; i + 1024 &lt;= ciphertext.length; i += 1024) {
			byte[] input = new byte[1024];
			System.arraycopy(data, i, input, 0, 1024);
			conv_len += c.update(input, 0, input.length, res, i);
		}
		conv_len += c.doFinal(data, conv_len, data.length-conv_len, res, conv_len);
        </xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="//task/code/dataType='File'">
		Files.write(file.toPath(), res);
		return file;
		</xsl:when>
		<xsl:when test="//task/code/dataType='String'">
		return new String(res);
		</xsl:when>
		<xsl:otherwise>
		return res;		
		</xsl:otherwise>
	</xsl:choose>
	}
}
</xsl:result-document>
</xsl:if>

<xsl:if test="//task/algorithm[@type='AsymmetricCipher']">
<xsl:result-document href="PublicKeyEnc.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */
public class PublicKeyEnc {	

	public byte[] encrypt(SecretKey sessionKey, PublicKey publicKey) throws GeneralSecurityException { 
	
	Cipher c = Cipher.getInstance("<xsl:value-of select="//task/algorithm[@type='AsymmetricCipher']/name"/>/<xsl:value-of select="//task/algorithm[@type='AsymmetricCipher']/mode"/>/<xsl:value-of select="//task/algorithm[@type='AsymmetricCipher']/padding"/>");
	c.init(Cipher.WRAP_MODE, publicKey);
	byte[] sessionKeyBytes = c.wrap(sessionKey);
	return sessionKeyBytes;
	}
}
</xsl:result-document>
</xsl:if>

<xsl:if test="//task[@description='HybridEncryption']">

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public void templateUsage(<xsl:value-of select="//task/code/dataType"/> data<xsl:if test="//task/code/keypair='false'">, PublicKey publicKey</xsl:if>) throws GeneralSecurityException<xsl:if test="//task/code/dataType='File'">, IOException</xsl:if><xsl:if test="//task/code/dataType='String'">, UnsupportedEncodingException</xsl:if>{
		
		KeyManagment km = new KeyManagment();
        SecretKey sessionKey = km.generateSessionKey(<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/keySize"/>);
		SymmetricEnc symEnc = new SymmetricEnc();
		<xsl:choose><xsl:when test="//task/code/dataType='File'">
        File encFile = symEnc.encrypt(data, sessionKey);</xsl:when>   
		<xsl:when test="//task/code/dataType='String'">
        String encMessage = symEnc.encrypt(data, sessionKey);</xsl:when>     
        <xsl:otherwise>
        byte[] ciphertext = symEnc.encrypt(data, sessionKey);</xsl:otherwise>
        </xsl:choose>
		<xsl:choose><xsl:when test="//task/code/keypair='true'">
		KeyPair keyPair = km.generateKeyPair(<xsl:value-of select="//task/algorithm[@type='AsymmetricCipher']/keySizePub"/>);
		</xsl:when></xsl:choose>
		PublicKeyEnc keyEnc = new PublicKeyEnc();		
		<xsl:choose><xsl:when test="//task/code/keypair='true'">
		byte[] encSessionKey = keyEnc.encrypt(sessionKey,keyPair.getPublic());</xsl:when>
		<xsl:otherwise>
        byte[] encSessionKey = keyEnc.encrypt(sessionKey,publicKey);</xsl:otherwise>
        </xsl:choose>
	}
}

<xsl:result-document href="KeyManagment.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */
public class KeyManagment{
	
	public SecretKey generateSessionKey(int keySize) throws NoSuchAlgorithmException {
		KeyGenerator kg = KeyGenerator.getInstance("<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>");
		kg.init(keySize);
		return kg.generateKey();
	}
	<xsl:if test="//task/code/keypair='true'">	
	public KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("<xsl:value-of select="//task/algorithm[@type='AsymmetricCipher']/name"/>");
		kpg.initialize(keySize);
        return kpg.generateKeyPair();
	}
	</xsl:if>
}
</xsl:result-document>
</xsl:if>

</xsl:template>

<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>

</xsl:stylesheet>
