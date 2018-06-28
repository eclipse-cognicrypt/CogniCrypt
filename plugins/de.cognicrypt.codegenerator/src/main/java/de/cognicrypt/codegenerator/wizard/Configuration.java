package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.clafer.instance.InstanceClafer;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.featuremodel.clafer.ClaferModelUtils;
import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.XMLParser;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.FileHelper;

/**
 * This class is a storage for the configuration chosen by the user.
 * 
 * @author Stefan Krueger
 *
 */
public class Configuration {

	final private InstanceClafer instance;
	final private Map<Question, Answer> options;
	final private String pathOnDisk;

	public Configuration(InstanceClafer instance, Map<Question, Answer> constraints, String pathOnDisk) {
		this.instance = instance;
		this.pathOnDisk = pathOnDisk;
		this.options = constraints;
	}

	/**
	 * Writes chosen configuration to hard disk.
	 * 
	 * @return Written file.
	 * @throws IOException
	 *         see {@link FileWriter#FileWriter(String)) FileWriter} and {@link XMLWriter#write(String) XMLWriter.write()}
	 */
	public File persistConf() throws IOException {
		final XMLParser parser = new XMLParser();
		Document configInXMLFormat = parser.displayInstanceValues(instance, this.options);
		if (configInXMLFormat != null) {
			final OutputFormat format = OutputFormat.createPrettyPrint();
			final XMLWriter writer = new XMLWriter(new FileWriter(pathOnDisk), format);
			writer.write(configInXMLFormat);
			writer.close();
			configInXMLFormat = null;

			return new File(pathOnDisk);
		} else {
			Activator.getDefault().logError(Constants.NO_XML_INSTANCE_FILE_TO_WRITE);
		}
		return null;
	}

	/**
	 * Retrieves list of custom providers from configuration.
	 * 
	 * @return List of custom providers
	 */
	public List<String> getProviders() {
		List<String> providers = new ArrayList<String>();
		for (InstanceClafer instanceChild : instance.getChildren()) {
			if (instanceChild.hasRef() && instanceChild.getRef() instanceof InstanceClafer) {
				for (InstanceClafer innerChild : ((InstanceClafer) instanceChild.getRef()).getChildren()) {
					if (ClaferModelUtils.removeScopePrefix(innerChild.getType().getName()).equals("Provider")) {
						try {
							String provider = ClaferModelUtils.removeScopePrefix(((InstanceClafer) innerChild.getRef()).getType().getName());
							if (!provider.equals(Constants.DEFAULT_PROVIDER)) {
								providers.add(provider);
							}
						} catch (ClassCastException ex) {
							Activator.getDefault().logError(ex, "Not all custom providers set successfully.");
						}
					}
				}
			}
		}
		return providers;
	}

	/**
	 * Deletes config file from hard disk.
	 */
	public void deleteConfFromDisk() {
		FileHelper.deleteFile(pathOnDisk);
	}
}
