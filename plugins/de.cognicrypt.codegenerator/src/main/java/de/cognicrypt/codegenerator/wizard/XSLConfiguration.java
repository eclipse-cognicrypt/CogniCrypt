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
import de.cognicrypt.codegenerator.utilities.XMLClaferParser;
import de.cognicrypt.core.Constants;

public class XSLConfiguration extends Configuration {

	final private InstanceClafer instance;
	
	public XSLConfiguration(InstanceClafer instance, Map<Question, Answer> constraints, String pathOnDisk) {
		super( constraints, pathOnDisk);
		this.instance = instance;
	}

	@Override
	public File persistConf() throws IOException {
		final XMLClaferParser parser = new XMLClaferParser();
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

	@Override
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

}
