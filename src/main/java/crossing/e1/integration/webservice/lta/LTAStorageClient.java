package crossing.e1.integration.webservice.lta;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

import de.tu_darmstadt.cs.cdc.moltas.exceptions.ServiceClientCreationException;
import de.tu_darmstadt.cs.cdc.moltas.services.ServiceType;
import de.tu_darmstadt.cs.cdc.moltas.services.storage.StorageManager;
import de.tu_darmstadt.cs.cdc.moltas.services.utilities.ServiceClientCreator;

/**
 * 
 * This client acts as connection to the storage service of the long-term archiving system MOLTAS. The client can be used to transfer, retrieve, delete, and hash documents to, from
 * or on the provided web server.
 * 
 * @note The correct web service address has to be configured within the configuration file found under: "src/main/resources/services.properties".
 * 
 * @author Michael Reif
 *
 */
public class LTAStorageClient {

	private StorageManager storageManager = null;

	private static StorageManager getStorageManager() throws ServiceClientCreationException {
		return (StorageManager) ServiceClientCreator.createServiceClient(ServiceType.STORAGE);
	}

	public LTAStorageClient() throws ServiceClientCreationException {
		this.storageManager = getStorageManager();
	}

	/**
	 * Uploads a document to the web service.
	 * 
	 * @return The id of the uploaded document.
	 * 
	 * @throws IOException
	 */
	public String addDocument(final InputStream document) throws IOException {
		return this.storageManager.upload(document);
	}

	/**
	 * Deletes the document with the given `documentId`.
	 */
	public void deleteDocument(final String dcoumentId) throws IOException {
		this.storageManager.delete(dcoumentId);
	}

	/**
	 * Deletes the documents with the given `documentIds`.
	 */
	public void deleteDocuments(final Set<String> documentIds) throws IOException {
		this.storageManager.delete(documentIds);
	}

	/**
	 * Retrieves the document from the web service.
	 * 
	 * @param documentId
	 *        The id of the document which shall be retrieved from the web service.
	 * @return A javax.ws.rs.core.Response which has to be parsed to get the document.
	 */
	public Response getDocument(final String documentId) throws IOException {
		return this.storageManager.download(documentId);
	}

	/**
	 * Generates the hash of the document with the passed `documentId` and the given `digestMethod`.
	 * 
	 * @param documentId
	 *        Id of the document where the hash is generated from.
	 * @param digestMethod
	 *        DigestMethod which is used to generate the hash.
	 * 
	 * @return A byte array which represents the hashed document.
	 */
	public byte[] getDoucmentHash(final String documentId, final ASN1ObjectIdentifier digestMethod) throws NoSuchAlgorithmException, IOException {
		return this.storageManager.hash(documentId, digestMethod);
	}
}
