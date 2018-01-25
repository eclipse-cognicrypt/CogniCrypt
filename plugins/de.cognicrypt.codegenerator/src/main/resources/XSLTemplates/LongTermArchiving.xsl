<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text"/>
<xsl:template match="/">

<xsl:variable name="Rounds"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations"/> </xsl:variable>
<xsl:variable name="outputSize"> <xsl:value-of select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/outputSize"/> </xsl:variable>


<xsl:if test="//task[@description='LongTermArchiving']">
<xsl:result-document href="LongTermArchivingClient.java">
package <xsl:value-of select="//task/Package"/>; 
<xsl:apply-templates select="//Import"/>
import java.util.List;

/** @author CogniCrypt */
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
				archivingScheme= Scheme.AC;
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
	public void templateUsage(String archiveName) throws ServiceClientCreationException, InternalServiceErrorException, IOException  {
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
