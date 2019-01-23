<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="text" />
	<xsl:template match="/">

		<xsl:variable name="Rounds">
			<xsl:value-of
				select="//task/algorithm[@type='KeyDerivationAlgorithm']/iterations" />
		</xsl:variable>
		<xsl:variable name="outputSize">
			<xsl:value-of
				select="//task/algorithm[@type='KeyDerivationAlgorithm']/algorithm[@type='Digest']/outputSize" />
		</xsl:variable>


		<xsl:if test="//task[@description='LongTermArchiving']">
			<xsl:result-document href="LongTermArchivingClient.java">
package	<xsl:value-of select="//task/Package" />;
<xsl:apply-templates select="//Import" />
import java.util.List;
import javax.ws.rs.core.Response;
import de.tu_darmstadt.cs.cdc.mops.services.ServiceType;
import org.apache.commons.io.IOUtils;
import de.tu_darmstadt.cs.cdc.mops.model.archiving_system.Document;

				public class LongTermArchivingClient {

				private ArchivingSystem archivingSystem;
				private ArchiveConfiguration archiveConfig;
				private static String url =	"https://moltas.cdc.informatik.tu-darmstadt.de";

				public LongTermArchivingClient() throws ServiceClientCreationException {
				if (archivingSystem == null) {
				this.archivingSystem = (ArchivingSystem)
				ServiceClientCreator.createServiceClient(ServiceType.ARCHIVING_SYSTEM, url + "/archiving-system");

				archiveConfig = new ArchiveConfiguration();
				archiveConfig.setDataStructure(DataStructureType.
				<xsl:choose>
					<xsl:when test="//task/element/List='Merkle_Tree_Sequence'">
						MERKLE_TREE_SEQUENCE
					</xsl:when>
					<xsl:when test="//task/element/List='Skip_List'">
						SKIPLIST
					</xsl:when>
					<xsl:when test="//task/element/List='Notarial_Attestation_Wrapper'">
						NOTARIAL_ATTESTATION_WRAPPER
					</xsl:when>
					<xsl:otherwise>
						SIMPLE_LIST
					</xsl:otherwise>
				</xsl:choose>);
				archiveConfig.setScheme(Scheme.MODULAR);
				archiveConfig.setAddingNewDocuments(<xsl:choose><xsl:when test="//task/element/AddDoc='Once'">false</xsl:when><xsl:otherwise>true</xsl:otherwise></xsl:choose>);
				archiveConfig.setMultipleDocuments(<xsl:choose><xsl:when test="//task/element/NumDoc='Single'">false</xsl:when><xsl:otherwise>true</xsl:otherwise></xsl:choose>);
				Set&lt;AttestationTechnique&gt; attsTec = new HashSet&lt;AttestationTechnique&gt;();
				<xsl:if test="//task//element//Trust='Both'">
					attsTec.add(AttestationTechnique.SIGNATURE_BASED_TIMESTAMPING);
					attsTec.add(AttestationTechnique.NOTARISATION);
				</xsl:if>
				<xsl:if test="//task//element//Trust='SignatureBased'">
					attsTec.add(AttestationTechnique.SIGNATURE_BASED_TIMESTAMPING);
				</xsl:if>
				<xsl:if test="//task//element//Trust='Notaries'">
					attsTec.add(AttestationTechnique.NOTARISATION);
				</xsl:if>
				archiveConfig.setAttestationTechniques(attsTec);
				}
				}

				public Archive createArchive(String archiveName) throws
				InternalServiceErrorException, IOException {
				return archivingSystem.createArchive(archiveName, archiveConfig);
				}

				public void renameArchive(long archiveId, String newName) throws
				EntityNotFoundException, InternalServiceErrorException {
				archivingSystem.renameArchive(archiveId, newName);
				}

				public List&lt;Archive&gt; getArchives() throws
				InternalServiceErrorException {
				return archivingSystem.getArchives();
				}

				public void deleteArchive(long archiveID) throws EntityNotFoundException,
				InternalServiceErrorException, IOException {
				archivingSystem.deleteArchive(archiveID);
				}

				public void prepareFileForArchiving(String documentName, File
				documentFile, File mopsFile, final String keyStorePath, final String
				keyStorePassword) throws Exception {
				if (!mopsFile.exists()) {
				mopsFile.createNewFile();
				}

				XadesBesSigner signer = new XadesBesSigner(new File(keyStorePath),
				keyStorePassword);

				// prepare a zip output stream that does no compression
				OutputStream outputStream = Files.newOutputStream(mopsFile.toPath());
				ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
				zipOutputStream.setLevel(Deflater.NO_COMPRESSION);

				// copy the document into the zip file
				zipOutputStream.putNextEntry(new ZipEntry(documentName));
				IOUtils.copy(Files.newInputStream(documentFile.toPath()),
				zipOutputStream);

				// sign the document and copy the signature into the zip file
				zipOutputStream.putNextEntry(new ZipEntry(documentName +
				".sig.xml"));
				signer.sign(documentFile, zipOutputStream);

				zipOutputStream.close();
				}

				public Document addFileToArchive(Archive arch, File archFile) {
				List&lt;ArchiveElement&gt; elements;
				try {
				elements = archivingSystem.importArchiveElements(new
				FileInputStream(archFile));
				UpdateParameters updaters = new UpdateParameters();
				updaters.setNewDigestMethod(true);
				final Set&lt;AttestationTechnique&gt; attestationTechniques =
				archiveConfig.getAttestationTechniques();
				if
				(attestationTechniques.contains(AttestationTechnique.SIGNATURE_BASED_TIMESTAMPING))
				{
				updaters.setServiceAddress(url + "/tsa");
				updaters.setAttestationTechnique(AttestationTechnique.SIGNATURE_BASED_TIMESTAMPING);
				} else {
				updaters.setServiceAddress(url + "/notary");
				updaters.setAttestationTechnique(AttestationTechnique.NOTARISATION);
				}
				updaters.setDigestMethodName("SHA-256");
				return archivingSystem.addDocument(arch.getId(), elements.get(0).getId(),
				updaters);
				} catch (InternalServiceErrorException | IOException | XMLException |
				CoreServiceException | EntityNotFoundException e) {
				return null;
				}
				}

				public File retrieveFileFromArchive(Archive arch, Document file, String
				filepath) throws IOException, EntityNotFoundException,
				InternalServiceErrorException {
				Response resp = archivingSystem.exportDocument(arch.getId(), file.getId());
				InputStream ins = ((InputStream) resp.getEntity());

				File retrievedFile = new File(filepath);
				retrievedFile.createNewFile();
				OutputStream outputStream = new FileOutputStream(retrievedFile);
				IOUtils.copy(ins, outputStream);
				outputStream.close();
				return retrievedFile;
				}

				public boolean verifyFileInArchive(Archive arch, Document file) {
				try {
				return archivingSystem.verifyDocument(file.getId()) == null;
				} catch (EntityNotFoundException | InternalServiceErrorException e) {
				return false;
				}
				}

				}
			</xsl:result-document>

package	<xsl:value-of select="//Package" />;
<xsl:apply-templates select="//Import" />
			public class Output {
			public static void templateUsage(String archiveName, String pathName, String
			fileName, String keyStorePath, String keyStorePassword) throws
			Exception {
			LongTermArchivingClient archivingClient = new LongTermArchivingClient();
			Archive archive = archivingClient.createArchive(archiveName);

			final File toBeArchivedFile = new File(fileName + ".mops.zip");
			archivingClient.prepareFileForArchiving(fileName, new File(pathName +
			fileName), toBeArchivedFile, keyStorePath, keyStorePassword);

			Document archivedDoc = archivingClient.addFileToArchive(archive,
			toBeArchivedFile);
			System.out.println("Document storage " + ((archivedDoc != null) ? "successful" : "failed"));

			File retrievedArchiveFile =
			archivingClient.retrieveFileFromArchive(archive, archivedDoc,
			pathName + "ret.mops.zip");
			System.out.println("File retrieval " + ((retrievedArchiveFile != null) ? "successful" :
			"failed"));

			boolean verified = archivingClient.verifyFileInArchive(archive,
			archivedDoc);
			System.out.println("Verification " + ((verified) ? "successful" : "failed"));
			}
			}
		</xsl:if>

	</xsl:template>

	<xsl:template match="Import">
import	<xsl:value-of select="." />;
	</xsl:template>


</xsl:stylesheet>
