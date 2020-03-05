package de.cognicrypt.staticanalyzer.utilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.ini4j.Profile.Section;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.Utils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;;

public class ArtifactUtils {

	/**
	 * This method downloads the rule sets from the NEXUS server
	 */
	public static boolean downloadRulesets() {

		Map<String, Double> defaultRulesetUrls = new HashMap<String, Double>();
		Section ini = Utils.getConfig().get(Constants.INI_URL_HEADER);
		defaultRulesetUrls.put(ini.get(Constants.INI_JCA_NEXUS), Constants.MIN_JCA_RULE_VERSION);
		defaultRulesetUrls.put(ini.get(Constants.INI_BC_NEXUS), Constants.MIN_BC_RULE_VERSION);
		defaultRulesetUrls.put(ini.get(Constants.INI_TINK_NEXUS), Constants.MIN_TINK_RULE_VERSION);
		defaultRulesetUrls.put(ini.get(Constants.INI_BCJCA_NEXUS), Constants.MIN_BCJCA_RULE_VERSION);

		Iterator<Entry<String, Double>> it = defaultRulesetUrls.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Double> pair = (Entry<String, Double>) it.next();
			String metaFilePath = pair.getKey() + File.separator + "maven-metadata.xml";
			try {
				ArtifactUtils.parseMetaData(metaFilePath, pair.getValue());
			}
			catch (IOException | ParserConfigurationException | SAXException e) {
				Activator.getDefault().logError(e);
				return false;
			}
		}
		return true;
	}

	/***
	 * This method downloads the rule set from the given URL
	 * 
	 * @param url The URL of the rule set
	 * @return
	 */
	public static boolean downloadRulesets(String url) {

		String metaFilePath = url + File.separator + "maven-metadata.xml";
		try {
			ArtifactUtils.parseMetaData(metaFilePath);
		}
		catch (IOException | ParserConfigurationException | SAXException e) {
			Activator.getDefault().logError(e);
			return false;
		}
		return true;
	}

	/**
	 * This method retrieves different versions of various rule sets from NEXUS server using metadata file
	 * 
	 * @param pathToMetadataFile Path to the metadata file on the NEXUS server
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static void parseMetaData(String pathToMetadataFile, Double... minRuleVersion) throws IOException, ParserConfigurationException, SAXException {

		ArrayList<String> listOfVersions = new ArrayList<String>();
		// Used String[] instead of String to resolve "Local Variable Defined in an Enclosing Scope Must be Final or Effectively Final" error
		String[] groupId = new String[1];
		String[] artifactId = new String[1];
		Double minVersion = minRuleVersion.length > 0 ? minRuleVersion[0] : 0.0;

		URL rulesetUrl = new URL(pathToMetadataFile);

		File metaData = new File("maven-metadata.xml");
		FileUtils.copyURLToFile(rulesetUrl, metaData);

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		DefaultHandler handler = new DefaultHandler() {

			boolean bversion = false;
			boolean bgroupid = false;
			boolean bartifactid = false;

			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

				if (qName.equalsIgnoreCase("version")) {
					bversion = true;
				}

				if (qName.equalsIgnoreCase("groupId")) {
					bgroupid = true;
				}

				if (qName.equalsIgnoreCase("artifactId")) {
					bartifactid = true;
				}
			}

			public void characters(char ch[], int start, int length) throws SAXException {

				if (bversion) {
					String currentVersion = new String(ch, start, length);
					listOfVersions.add(currentVersion);
					bversion = false;
				}

				if (bgroupid) {
					groupId[0] = new String(ch, start, length);
					bgroupid = false;
				}

				if (bartifactid) {
					artifactId[0] = new String(ch, start, length);
					bartifactid = false;
				}
			}

		};

		saxParser.parse("maven-metadata.xml", handler);
		metaData.delete();

		for (String version : listOfVersions) {
			DefaultArtifactVersion min = new DefaultArtifactVersion(minVersion.toString());
			DefaultArtifactVersion cur = new DefaultArtifactVersion(version);
			if (cur.compareTo(min) >= 0) {
				downloadRulesetArtifact(groupId[0], artifactId[0], version, new File(System.getProperty("user.dir")));
			}
		}
	}

	// TODO use regular expression instead of actual filename
	/**
	 * This method downloads the artifact based on the parameters passed and saves them to the rulesDir path
	 * 
	 * @param groupId Group ID of the artifact
	 * @param artifactId Artifact ID of the artifact
	 * @param version Artifact version to be downloaded
	 * @param rulesDir Destination path
	 */
	public static void downloadRulesetArtifact(String groupId, String artifactId, String version, File rulesDir) {

		String artifactPath = rulesDir.getPath() + File.separator + artifactId;
		String downloadedZipFilename = artifactId + "-" + version + "-ruleset.zip";

		if (Files.notExists(Paths.get(artifactPath + File.separator + version))) {
			try {

				ArtifactDownload.getArtifactByAether(groupId, artifactId, version, "ruleset", "zip", rulesDir);

				Files.createDirectories(Paths.get(artifactPath));

				Files.move(
						Paths.get(
								rulesDir.getPath() + File.separator + groupId.replace(".", "/") + File.separator + artifactId + File.separator + version + File.separator + downloadedZipFilename),
						Paths.get(artifactPath + File.separator + downloadedZipFilename), StandardCopyOption.REPLACE_EXISTING);
				Files.walk(Paths.get(rulesDir.getPath() + File.separator + "de")).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

				try {
					ZipFile zipFile = new ZipFile(artifactPath + File.separator + downloadedZipFilename);
					zipFile.extractAll(artifactPath + File.separator + version);
				}
				catch (ZipException e) {
					Activator.getDefault().logError(e);
				}
				Files.delete(Paths.get(artifactPath + File.separator + downloadedZipFilename));
				new File(artifactPath + File.separator + artifactId).renameTo(new File(artifactPath + File.separator + version));
			}
			catch (IOException e) {
				Activator.getDefault().logError(e);
			}
		}
	}
}
