package crossing.e1.integration.webservice.lta;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.tu_darmstadt.cs.cdc.moltas.exceptions.EntityNotFoundException;
import de.tu_darmstadt.cs.cdc.moltas.exceptions.InternalServiceErrorException;
import de.tu_darmstadt.cs.cdc.moltas.exceptions.ServiceClientCreationException;
import de.tu_darmstadt.cs.cdc.moltas.model.archiving_system.Archive;
import de.tu_darmstadt.cs.cdc.moltas.model.configuration.ArchiveConfiguration;
import de.tu_darmstadt.cs.cdc.moltas.model.configuration.Scheme;
import de.tu_darmstadt.cs.cdc.moltas.services.ServiceType;
import de.tu_darmstadt.cs.cdc.moltas.services.archiving_system.ArchivingSystem;
import de.tu_darmstadt.cs.cdc.moltas.services.utilities.ServiceClientCreator;

/**
 * 
 * A client that communicates with the REST API of the long term archiving system. The client allows to create, rename and delete archives.
 * 
 * @note The funcutionally of this class does only work within the university network of TU Darmstadt.
 *
 * 
 * @author Michael Reif
 *
 */
public class LongTermArchivingClient {

	/**
	 * 
	 * Translates a `dataStructure` string to an ArchivingConfiguration.
	 * 
	 * Allowed dataStructures: - Simple_List - Skip_List - Merkle_Tree_Sequence - Notarial_Attestation_Wrapper
	 * 
	 * @param dataStructure
	 *        DataStructe that fulfill the clafer constrains w.r.t. to the questionaire.
	 * @return null, if the given dataStructure is unknown else the correct ArchiveConfiguration that is required to create an Archive.
	 */
	private static ArchiveConfiguration mapDatastructureToScheme(final String dataStructure) {
		Scheme archivingScheme = null;
		switch (dataStructure) {
			case "Simple_List":
				archivingScheme = Scheme.AdES;
				break;
			case "Skip_List":
				archivingScheme = Scheme.CISS;
				break;
			case "Merkle_Tree_Sequence":
				archivingScheme = Scheme.ERS;
				break;
			case "Notarial_Attestation_Wrapper":
				archivingScheme = Scheme.ERS;
				break;
		}

		final ArchiveConfiguration archConfig = (archivingScheme == null) ? null : ArchiveConfiguration.createDefaultArchiveConfiguration(archivingScheme);

		return archConfig;
	}

	private ArchivingSystem archivingSystem;

	public LongTermArchivingClient() throws ServiceClientCreationException {
		this.archivingSystem = (ArchivingSystem) ServiceClientCreator.createServiceClient(ServiceType.ARCHIVING_SYSTEM);
		mapDatastructureToScheme("");
	}

	public Archive createArchive(final String archiveName, final ArchiveConfiguration archiveConfig) throws InternalServiceErrorException, IOException {
		return this.archivingSystem.createArchive(archiveName, archiveConfig);
	}

	public void deleteArchive(final long archiveID) throws EntityNotFoundException, InternalServiceErrorException, IOException {
		this.archivingSystem.deleteArchive(archiveID);
	}

	public List<Archive> getArchives() throws InternalServiceErrorException {
		return this.archivingSystem.getArchives();
	}

	public void renameArchive(final long archiveId, final String newName) throws EntityNotFoundException, InternalServiceErrorException {
		this.archivingSystem.renameArchive(archiveId, newName);
	}
	
	public List<Map<String, String>> verifyArchive(final long archiveId) throws EntityNotFoundException, InternalServiceErrorException {
		return this.archivingSystem.verifyArchive(archiveId);
	}
	
	
	
}