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
	
	public byte[] encrypt(byte [] data, SecretKey key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException { 
		byte [] ivb = new byte [16];
	    SecureRandom.getInstanceStrong().nextBytes(ivb);
	    IvParameterSpec iv = new IvParameterSpec(ivb);
		
		Cipher c = Cipher.getInstance("<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/mode"/>/<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/padding"/>");
		
		c.init(Cipher.ENCRYPT_MODE, key, iv);
		byte [] res = c.doFinal(data);
		byte [] ret = new byte[res.length + ivb.length];
		System.arraycopy(ivb, 0, ret, 0, ivb.length);
		System.arraycopy(res, 0, ret, ivb.length, ret.length);
		return ret;
	}
}
</xsl:result-document>
</xsl:if>

<xsl:if test="//task[@description='PasswordBasedEncryption']">

<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">
<xsl:result-document href="KeyDeriv.java">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>

public class KeyDeriv {
	
	public SecretKey getKey(String pwd) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] salt = new byte[16];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		
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

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public byte[] run(byte[] data<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">, String pwd</xsl:if>) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException  {
		 <xsl:choose>
         <xsl:when test="//task/algorithm[@type='KeyDerivationAlgorithm']">KeyDeriv kd = new KeyDeriv();
		 SecretKey key = kd.getKey(pwd); </xsl:when>
         <xsl:otherwise>SecretKeySpec key = getKey(); </xsl:otherwise>
		 </xsl:choose>		
		
		Enc enc = new Enc();
		return enc.encrypt(data, key);
	}
}
</xsl:if>

<xsl:if test="//task[@description='SymmetricEncryption']">
package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public byte[] run(byte[] data) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException  {
		KeyGenerator kg = KeyGenerator.getInstance("<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/name"/>");
		kg.init(<xsl:value-of select="//task/algorithm[@type='SymmetricBlockCipher']/keySize"/>);
		SecretKey key = kg.generateKey();

		Enc enc = new Enc();
		return enc.encrypt(data, key);
	}
}
</xsl:if>
<xsl:if test="//task[@description='SecurePassword']">

<xsl:if test="//task/algorithm[@type='KeyDerivationAlgorithm']">
<xsl:result-document href="PWHasher.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>

public class PWHasher {	
	
	public byte[] hashPW(String pwd) throws NoSuchAlgorithmException, InvalidKeySpecException { 
		byte[] salt = new byte[32];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		KeySpec spec = new PBEKeySpec(pwd.toCharArray(), salt, 65536, <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/outputSize"/>);
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		return f.generateSecret(spec).getEncoded();}
}
</xsl:result-document>
</xsl:if>



package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public byte[] run(String pwd) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException  {
		PWHasher pwHasher = new PWHasher();
		return pwHasher.hashPW(pwd);
	}
}
</xsl:if>

<xsl:if test="//task[@description='LongTermArchiving']">
<xsl:result-document href="LongTermArchivingClient.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
import java.util.List;

public class LongTermArchivingClient {

	private ArchivingSystem archivingSystem;
	private ArchiveConfiguration archiveConfig;
	
	/**
	 * 
	 * Translates a `dataStructure` string to an ArchivingConfiguration.
	 * 
	 * Allowed dataStructures: 
	 * 	- Simple_List
	 *  - Skip_List
	 *  - Merkle_Tree_Sequence
	 *  - Notarial_Attestation_Wrapper
	 * 
	 * @param dataStructure DataStructe that fulfill the clafer constrains w.r.t. to the questionaire.
	 * @return null, if the given dataStructure is unknown else the correct ArchiveConfiguration that
	 * is required to create an Archive.
	 */
	private  static ArchiveConfiguration mapDatastructureToScheme(String dataStructure){
		Scheme archivingScheme = null;
		switch(dataStructure){
			case "Simple_List": 
				archivingScheme = Scheme.AdES;
				break;
			case "Skip_List": 
				archivingScheme = Scheme.CISS;
				break;
			case "Merkle_Tree_Sequence": 
				archivingScheme= Scheme.ERS;
				break;
			case "Notarial_Attestation_Wrapper": 
				archivingScheme= Scheme.ERS;
				break;
		}
		
		ArchiveConfiguration archConfig = (archivingScheme == null)? null : 
				ArchiveConfiguration.createDefaultArchiveConfiguration(archivingScheme);
		
		return archConfig;
	}
	
	public LongTermArchivingClient() throws ServiceClientCreationException {
		if(archivingSystem != null){
			this.archivingSystem = (ArchivingSystem) ServiceClientCreator.createServiceClient(ServiceType.ARCHIVING_SYSTEM);
			this.archiveConfig = mapDatastructureToScheme("<xsl:value-of select="//task/algorithm[@type='List']/list"/>");
		}
	}
	
	public Archive createArchive(String archiveName) throws InternalServiceErrorException, IOException{
		return archivingSystem.createArchive(archiveName, archiveConfig);
	}
	
	public void renameArchive(long archiveId, String newName) throws EntityNotFoundException, InternalServiceErrorException{
		archivingSystem.renameArchive(archiveId, newName);
	}
	
	public List&lt;Archive&gt; getArchives() throws InternalServiceErrorException{
		return archivingSystem.getArchives();
	}
	
	public void deleteArchive(long archiveID) throws EntityNotFoundException, InternalServiceErrorException, IOException{
		archivingSystem.deleteArchive(archiveID);
	}
} 
</xsl:result-document>

package <xsl:value-of select="//Package"/>; 
<xsl:apply-templates select="//Import"/>	
public class Output {
	public void run(String archiveName) throws ServiceClientCreationException, InternalServiceErrorException, IOException  {
		LongTermArchivingClient ltac = new LongTermArchivingClient();
		Archive a = ltac.createArchive(archiveName);
	}
}
</xsl:if>


</xsl:template>
	
<xsl:template match="Import">
import <xsl:value-of select="."/>;
</xsl:template>
</xsl:stylesheet>
