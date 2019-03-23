<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:variable name="Rounds"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations"/> </xsl:variable>
<xsl:variable name="outputSize"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/outputSize"/> </xsl:variable>

<xsl:if test="//task/algorithm[@type='SymmetricBlockCipher']">
<xsl:result-document href="Enc.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */
public class Enc {	
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

<xsl:if test="//task[@description='SymmetricEncryption']">

<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">
<xsl:result-document href="KeyDeriv.java">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>
/** @author CogniCrypt */	
public class KeyDeriv {
	
	public SecretKey getKey(char[] pwd) throws GeneralSecurityException {
		byte[] salt = new byte[16];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		
		PBEKeySpec spec = new PBEKeySpec(pwd, salt, <xsl:choose>
         <xsl:when test="$Rounds > 1000"> <xsl:value-of select="$Rounds"/> </xsl:when>
         <xsl:otherwise> 1000 </xsl:otherwise>
		 </xsl:choose>, <xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/keySize"/>);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("<xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/name"/>WithHmacSHA256");
		SecretKeySpec ret = new SecretKeySpec(skf.generateSecret(spec).getEncoded(), "<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>" );
		spec.clearPassword();
		return ret;
	}
}
</xsl:result-document>
</xsl:if>


package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public <xsl:value-of select="//task/code/dataType"/> templateUsage(<xsl:value-of select="//task/code/dataType"/> data<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">, char[] pwd</xsl:if>) throws GeneralSecurityException<xsl:if test="//task/code/dataType='File'">, IOException</xsl:if><xsl:if test="//task/code/dataType='String'">, UnsupportedEncodingException</xsl:if>{
		<xsl:choose>
        <xsl:when test="//task/algorithm[@type='KeyDerivationAlgorithm']">KeyDeriv kd = new KeyDeriv();
		SecretKey key = kd.getKey(pwd); </xsl:when>
        <xsl:otherwise>KeyGenerator kg = KeyGenerator.getInstance("<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>");
		<xsl:choose>
		<xsl:when test="//task/algorithm[@type='SymmetricBlockCipher']/keySize &gt; 128">
	 // KeySize > 128 needs unlimited strength policy files http://www.oracle.com/technetwork/java/javase/downloads</xsl:when></xsl:choose>
		kg.init(<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/keySize"/>);
		SecretKey key = kg.generateKey(); </xsl:otherwise>
		</xsl:choose>	
		Enc enc = new Enc();
		<xsl:choose>
		<xsl:when test="//task/code/dataType='File'">
        File encFile = enc.encrypt(data, key);
        //enc.decrypt(encFile, key);
        return encFile;
		</xsl:when>   
		<xsl:when test="//task/code/dataType='String'">
        String encMessage = enc.encrypt(data, key);
        enc.decrypt(encMessage, key);
      	return encMessage;
		</xsl:when>     
         <xsl:otherwise>
        byte[] ciphertext = enc.encrypt(data, key);
        return ciphertext;
        </xsl:otherwise>
        </xsl:choose>
	}
}
</xsl:if>

</xsl:template>

<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>


