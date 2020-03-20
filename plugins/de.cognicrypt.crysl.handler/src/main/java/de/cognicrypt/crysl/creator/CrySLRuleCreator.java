package de.cognicrypt.crysl.creator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.google.common.base.Strings;
import de.cognicrypt.core.Constants;
import de.cognicrypt.crysl.Activator;
import de.cognicrypt.utils.CrySLUtils;

/**
 * This class creates programmatically a CrySL rule 
 * @author André Sonntag
 *
 */
public class CrySLRuleCreator {

	public CrySLRuleCreator() {}

	/**
	 * This method creates a CrySL rule and compiled them
	 * @param filePath - path where the file should be stored
	 * @param spec - fully-qualified name of the class	
	 * @param objects - {@link List<String>} of objects in format: fully-qualified className varName; (i.e."java.lang.Object obj;")
	 * @param events - {@link List<String>} of events that contribute to successful usage of the class
	 * @param order - {@link String} regular expression of method event patterns that are defined in @param events
	 * @param constraints - {@link List<String>} of constraints for objects defined in @param objects
	 * @param requires - {@link List<String>} of required predicates 
	 * @param ensures - {@link List<String>} of ensured predicates 
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if no error occurs during generation and compiling process
	 */
	public boolean createRule(String filePath, String spec, List<String> objects, List<String> events, String order,
			List<String> constraints, List<String> requires, List<String> ensures) {

		if (Strings.isNullOrEmpty(spec) || CrySLUtils.isNullOrEmpty(objects) || CrySLUtils.isNullOrEmpty(events)
				|| Strings.isNullOrEmpty(order) || CrySLUtils.isNullOrEmpty(ensures)) {
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
		final String cryslRuleContent = buildCrySLContentString(SPEC, OBJECTS, EVENTS, ORDER, CONSTRAINTS, REQUIRES, ENSURES);

		try {
			createCrySLFile(filePath, cryslRuleContent);
		} catch (IOException e) {
			Activator.getDefault().logError(null, Constants.ERROR_MESSAGE_NO_FILE);
		} 

		return true;
	}

	/**
	 * This method extends a certain section by further a {@link String}
	 * @param filePath - Path where the file is stored
	 * @param section - Section to be extended
	 * @param content - {@link String} object to extend the section
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
						boolean doublicate = false;
						for(int j = i; j < rule.size(); j++) {
							if(rule.get(j).trim().equals(content.trim())) {
								doublicate = true;
								break;
							}
						}
						if(!doublicate) {
							rule.add(i + 1, "\t" + content);
							successful = true;
							break;
						}
					}
				}
				createCrySLFile(f.getAbsolutePath(), rule);
			} catch (IOException e) {
				Activator.getDefault().logError(null, Constants.ERROR_MESSAGE_NO_FILE);
			}		
		} else {
			Activator.getDefault().logError(null, Constants.ERROR_MESSAGE_NO_FILE);
		}
		return successful;
	}

	/**
	 * This method deletes the matched {@link String} from the rule content
	 * @param filePath Path where the file is stored
	 * @param {@link String} to be deleted from the rule 
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
	 * @param spec - spec {@link String}
	 * @param objects - objects {@link List<String>}
	 * @param events - events {@link List<String>}
	 * @param order - order {@link String}
	 * @param constraints - constraints {@link List<String>}
	 * @param requires - requires {@link List<String>}
	 * @param ensures - ensures {@link List<String>}
	 * @return composed rule as String
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
	 * This method checks the format of the values for a certain section, furthermore
	 * the rule composes the section together
	 * @param section - {@link String} section name
	 * @param values - {@link List<String>} with the values for the section 
	 * @return composed section as String
	 */
	private String buildCrySLSectionString(String section, List<String> values) {

		if (CrySLUtils.isNullOrEmpty(values)) {
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
	 * This method writes the rule content to a file
	 * @param filePath path where the rule should be stored
	 * @param content rule content as {@link String}
	 * @throws IOException
	 */
	private void createCrySLFile(String filePath, String content) throws IOException {
		String path = filePath.endsWith(Constants.cryslFileEnding) ? filePath : filePath + Constants.cryslFileEnding;
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(content.getBytes());
		fos.flush();
		fos.close();
	}

	/**
	 * This method writes the rule content from {@link List<String>} to a file
	 * @param filePath path where the rule should be store 
	 * @param content rule content as {@link List<String>}
	 * @throws IOException
	 */
	private void createCrySLFile(String filePath, List<String> content) throws IOException {
		StringBuilder builder = new StringBuilder();
		for (String s : content) {
			builder.append(s);
			builder.append("\n");
		}
		createCrySLFile(filePath, builder.toString());
	}


}
