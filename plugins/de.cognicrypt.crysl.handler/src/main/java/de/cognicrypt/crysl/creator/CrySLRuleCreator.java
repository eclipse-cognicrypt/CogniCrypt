package de.cognicrypt.crysl.creator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.ui.IEditorPart;

import com.google.common.base.Strings;

import de.cognicrypt.core.Constants;
import de.cognicrypt.crysl.handler.Activator;
import de.cognicrypt.crysl.reader.CrySLModelReader;
import de.cognicrypt.utils.Utils;

/**
 * This class creates programmatically a CrySL rule 
 * @author André Sonntag
 *
 */
public class CrySLRuleCreator {

	public CrySLRuleCreator() {}

	/**
	 * This method creates a CrySL rule and compiled them
	 * @param filePath	Path where the file should be stored
	 * @param spec		
	 * @param objects
	 * @param events
	 * @param order
	 * @param constraints
	 * @param requires
	 * @param ensures
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if no error occurs during generation and compiling process
	 */
	public boolean createRule(String filePath, String spec, List<String> objects, List<String> events, String order,
			List<String> constraints, List<String> requires, List<String> ensures) {

		if (Strings.isNullOrEmpty(spec) || Utils.isNullOrEmpty(objects) || Utils.isNullOrEmpty(events)
				|| Strings.isNullOrEmpty(order) || Utils.isNullOrEmpty(ensures)) {
			Activator.getDefault().logError(null, "One or more mandatory sections are null or empty");
			return false;
		}

		final String SPEC = "SPEC " + spec;
		final String OBJECTS = buildCrySLSectionString("OBJECTS", objects);
		final String EVENTS = buildCrySLSectionString("EVENTS", events);
		final String ORDER = "ORDER" + "\n" + "\t" + order;
		final String CONSTRAINTS = buildCrySLSectionString("CONSTRAINTS", constraints);
		final String REQUIRES = buildCrySLSectionString("REQUIRES", requires);
		final String ENSURES = buildCrySLSectionString("ENSURES", ensures);

		final String cryslRuleContent = buildCrySLContentString(SPEC, OBJECTS, EVENTS, ORDER, CONSTRAINTS, REQUIRES,
				ENSURES);

		try {
			createCrySLFile(filePath, cryslRuleContent);
			compileRule(filePath);
		} catch (IOException e) {
			Activator.getDefault().logError(null, Constants.ERROR_MESSAGE_NO_FILE);
		}

		return true;
	}

	/**
	 * This method adds a String to a certain section
	 * @param filePath Path where the file is stored
	 * @param section
	 * @param content
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if file and section could be find and no error occurs during compiling process
	 */
	public boolean extendRule(String filePath, String section, String content) {
		File f = new File(filePath);
		boolean successful = false;

		if (f.exists()) {
			try {
				List<String> rule = Files.readAllLines(Paths.get(filePath));
				for (int i = 0; i < rule.size(); i++) {
					if (rule.get(i).trim().toUpperCase().equals(section.trim())) {
						rule.add(i + 1, "\t" + content);
						successful = true;
						break;
					}
				}
				createCrySLFile(f.getAbsolutePath(), rule);
				compileRule(filePath);
			} catch (IOException e) {
				Activator.getDefault().logError(null, Constants.ERROR_MESSAGE_NO_FILE);
			}
		} else {
			Activator.getDefault().logError(null, Constants.ERROR_MESSAGE_NO_FILE);
		}
		return successful;
	}

	/**
	 * This method deletes a certain content string of a rule
	 * @param filePath Path where the file is stored
	 * @param content
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if file and content sting could be find and no error occurs during compiling process
	 */
	public boolean reduceRule(String filePath, String content) {

		File f = new File(filePath);
		boolean successful = false;

		if (f.exists()) {
			try {
				List<String> rule = Files.readAllLines(Paths.get(filePath));
				for (int i = 0; i < rule.size(); i++) {
					if (rule.get(i).trim().equals(content.trim())) {
						rule.remove(i);
						successful = true;
						break;
					}
				}
				createCrySLFile(f.getAbsolutePath(), rule);
				compileRule(filePath);
			} catch (IOException e) {
				Activator.getDefault().logError(null, Constants.ERROR_MESSAGE_NO_FILE);
			}
		} else {
			Activator.getDefault().logError(null, Constants.ERROR_MESSAGE_NO_FILE);
		}
		return successful;
	}

	/**
	 * This method composes the individual sections to a rule
	 * @param spec
	 * @param objects
	 * @param events
	 * @param order
	 * @param constraints
	 * @param requires
	 * @param ensures
	 * @return	composed rule as String
	 */
	private String buildCrySLContentString(String spec, String objects, String events, String order, String constraints,
			String requires, String ensures) {

		StringBuilder builder = new StringBuilder();

		builder.append(spec);
		builder.append("\n");
		builder.append(objects);
		builder.append("\n");
		builder.append(events);
		builder.append("\n");
		builder.append(order);
		builder.append("\n");
		builder.append(constraints);
		builder.append("\n");
		builder.append(requires);
		builder.append("\n");
		builder.append(ensures);

		return builder.toString();
	}

	/**
	 * This method composes a section together
	 * @param section
	 * @param values
	 * @return comoposed section as String
	 */
	private String buildCrySLSectionString(String section, List<String> values) {

		if (Utils.isNullOrEmpty(values)) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		builder.append(section);
		builder.append("\n");

		for (String value : values) {
			String temp = !value.endsWith(";") ? value + ";" : value;
			builder.append("\t");
			builder.append(temp);
			builder.append("\n");
		}
		return builder.toString();
	}

	/**
	 * This method writes a String to a file
	 * @param filePath
	 * @param content
	 * @throws IOException
	 */
	private void createCrySLFile(String filePath, String content) throws IOException {
		String path = filePath.endsWith(".crysl") ? filePath : filePath + ".crysl";
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content.getBytes());
		fos.flush();
		fos.close();
	}

	/**
	 * This method writes a {@link List<String>} to a file
	 * @param name
	 * @param content
	 * @throws IOException
	 */
	private void createCrySLFile(String name, List<String> content) throws IOException {
		StringBuilder builder = new StringBuilder();
		for (String s : content) {
			builder.append(s);
			builder.append("\n");
		}
		createCrySLFile(name, builder.toString());
	}

	/**
	 * This method compiles a CrySL file to a .cryptslbin file
	 * @param filePath
	 * @throws IOException
	 */
	private void compileRule(String filePath) throws IOException {

		File f = new File(filePath);

		if (f.exists()) {
			CrySLModelReader reader = new CrySLModelReader();
			reader.readRule(f);
		} else {
			throw new IOException();
		}

	}

}
