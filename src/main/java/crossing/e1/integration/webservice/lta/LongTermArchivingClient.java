package crossing.e1.integration.webservice.lta;

import java.io.IOException;
import java.util.List;

import de.tu_darmstadt.cs.cdc.moltas.exceptions.EntityNotFoundException;
import de.tu_darmstadt.cs.cdc.moltas.exceptions.InternalServiceErrorException;
import de.tu_darmstadt.cs.cdc.moltas.exceptions.ServiceClientCreationException;
import de.tu_darmstadt.cs.cdc.moltas.model.archiving_system.Archive;
import de.tu_darmstadt.cs.cdc.moltas.model.configuration.ArchiveConfiguration;
import de.tu_darmstadt.cs.cdc.moltas.model.configuration.Scheme;
import de.tu_darmstadt.cs.cdc.moltas.model.configuration.UpdateParameters;
import de.tu_darmstadt.cs.cdc.moltas.services.ServiceType;
import de.tu_darmstadt.cs.cdc.moltas.services.archiving_system.ArchivingSystem;
import de.tu_darmstadt.cs.cdc.moltas.services.utilities.ServiceClientCreator;


public class LongTermArchivingClient {

	private ArchivingSystem archivingSystem;
	
	public static ArchiveConfiguration mapDatastructureToScheme(String dataStructure){
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
		
		ArchiveConfiguration archConfig = 
				ArchiveConfiguration.createDefaultArchiveConfiguration(archivingScheme);
		
		return archConfig;
	}
	
	public LongTermArchivingClient() throws ServiceClientCreationException {
		initArchivingSystemClient();
	}
	
	public Archive createArchive(String archiveName, ArchiveConfiguration archiveConfig) throws InternalServiceErrorException, IOException{
		return archivingSystem.createArchive(archiveName, archiveConfig);
	}
	
	public void renameArchive(long archiveId, String newName) throws EntityNotFoundException, InternalServiceErrorException{
		archivingSystem.renameArchive(archiveId, newName);
	}
	
	public List<Archive> getArchives() throws InternalServiceErrorException{
		return archivingSystem.getArchives();
	}
	
	public void deleteArchive() throws EntityNotFoundException, InternalServiceErrorException, IOException{
		archivingSystem.deleteArchive(1l);
	}
	
	private void initArchivingSystemClient() throws ServiceClientCreationException {
		if(archivingSystem != null){
			archivingSystem = (ArchivingSystem) ServiceClientCreator.createServiceClient(ServiceType.ARCHIVING_SYSTEM);
		}
	}
}