package de.cognicrypt.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.cryslhandler.CrySLModelReader;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Constants.Rules;

public class CrySLUtils {

	public static List<TransitionEdge> getOutgoingEdges(Collection<TransitionEdge> collection, final StateNode curNode, final StateNode notTo) {
		final List<TransitionEdge> outgoingEdges = new ArrayList<>();
		for (final TransitionEdge comp : collection) {
			if (comp.getLeft().equals(curNode) && !(comp.getRight().equals(curNode) || comp.getRight().equals(notTo))) {
				outgoingEdges.add(comp);
			}
		}
		return outgoingEdges;
	}

	/**
	 * This method checks if a Collection is null or empty
	 * @param c
	 * @return 
	 */
	public static boolean isNullOrEmpty( final Collection< ? > c ) {
	    return c == null || c.isEmpty();
	}

	protected static List<CrySLRule> readCrySLRules(String rulesFolder) {
		List<CrySLRule> rules = new ArrayList<CrySLRule>();
	
		for (File rule : (new File(rulesFolder)).listFiles()) {
			if (rule.isDirectory()) {
				rules.addAll(readCrySLRules(rule.getAbsolutePath()));
				continue;
			}
	
			CrySLRule readFromSourceFile = CrySLRuleReader.readFromSourceFile(rule);
			if (readFromSourceFile != null) {
				rules.add(readFromSourceFile);
			}
		}
		return rules;
	}

	public static List<CrySLRule> readCrySLRules() {
		return Stream.of(readCrySLRules(Utils.getResourceFromWithin(Constants.RELATIVE_RULES_DIR).getAbsolutePath()),
				readCrySLRules(Constants.ECLIPSE_RULES_DIR + Constants.outerFileSeparator + "JavaCryptographicArchitecture")).flatMap(Collection::stream).collect(Collectors.toList());
	}

	/**
	 * Returns the crysl rule with the name that is defined by the method parameter cryslRule.
	 * 
	 * @param cryslRule Name of crysl rule that should by returend.
	 * @return Returns the crysl rule with the name that is defined by the parameter cryslRule.
	 * @throws MalformedURLException
	 */
	
	public static CrySLRule getCrySLRule(String cryslRule) throws MalformedURLException {
		File ruleRes = new File(Constants.ECLIPSE_RULES_DIR + Constants.innerFileSeparator + Constants.Rules.JavaCryptographicArchitecture.toString() + Constants.innerFileSeparator + 
								CrySLUtils.getRuleVersions(Constants.Rules.JavaCryptographicArchitecture.toString())[CrySLUtils.getRuleVersions(Constants.Rules.JavaCryptographicArchitecture.toString()).length - 1] + 
								Constants.innerFileSeparator + Constants.Rules.JavaCryptographicArchitecture.toString() + Constants.innerFileSeparator + cryslRule + RuleFormat.SOURCE.toString());
		if (ruleRes == null || !ruleRes.exists() || !ruleRes.canRead()) {
			ruleRes = Utils.getResourceFromWithin(Constants.RELATIVE_CUSTOM_RULES_DIR + Constants.innerFileSeparator + cryslRule + RuleFormat.SOURCE.toString(), de.cognicrypt.core.Activator.PLUGIN_ID);
		}
		return (new CrySLModelReader()).readRule(ruleRes);
	}

	/***
	 * This method returns all sub-directories in a directory of the first level.
	 * @param ruleSet JavaCryptographicArchitecture, BouncyCastle, Tink
	 * @return array of version numbers
	 */
	public static String[] getRuleVersions(String ruleSet){
		List<String> versions = new ArrayList<String>();
		File path = new File(System.getProperty("user.dir") + File.separator + ruleSet);
		File[] innerDirs = path.listFiles();
		if (innerDirs == null) {
			return null;
		}
			for (File file: innerDirs) {
			if (file.isDirectory()) {
				String[] versionNumber = file.getPath().split(Matcher.quoteReplacement(System.getProperty("file.separator")));
				versions.add(versionNumber[versionNumber.length - 1]);
			}
		}
	
		versions.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				DefaultArtifactVersion v1 = new DefaultArtifactVersion(o1);
					DefaultArtifactVersion v2 = new DefaultArtifactVersion(o2);
					return v1.compareTo(v2);
			}
		});
	
		// https://shipilev.net/blog/2016/arrays-wisdom-ancients/
		return versions.toArray(new String[0]);
	}

}
