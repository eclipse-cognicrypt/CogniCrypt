/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.sootbridge;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import boomerang.callgraph.ObservableDynamicICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.preanalysis.BoomerangPretransformer;
import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.providerdetection.ProviderDetection;
import crypto.analysis.CryptoScanner;
import crypto.rules.CrySLRule;
import crypto.rules.CrySLRuleReader;
import de.cognicrypt.core.Constants;
import de.cognicrypt.crysl.reader.CrySLParser;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;
import de.cognicrypt.staticanalyzer.utilities.Ruleset;
import de.cognicrypt.utils.CrySLUtils;
import de.cognicrypt.utils.Utils;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.options.Options;

/**
 * This runner triggers Soot.
 *
 * @author Johannes Spaeth
 * @author Eric Bodden
 */
public class SootRunner {

	final Boolean depValue = false;

	private static SceneTransformer createAnalysisTransformer(final ResultsCCUIListener resultsReporter) {
		return new SceneTransformer() {

			@Override
			protected void internalTransform(final String phaseName, final Map<String, String> options) {
				IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				BoomerangPretransformer.v().apply();
				final ObservableDynamicICFG icfg = new ObservableDynamicICFG(true);
				CryptoScanner scanner = new CryptoScanner() {

					@Override
					public ObservableICFG<Unit, SootMethod> icfg() {
						return icfg;
					}

				};
				scanner.getAnalysisListener().addReportListener(resultsReporter);
				List<CrySLRule> rules = getRules(resultsReporter.getReporterProject());
				if (store.getBoolean(Constants.PROVIDER_DETECTION_ANALYSIS)) {
					ProviderDetection providerDetection = new ProviderDetection();
					String rootRulesDirectory = Constants.ECLIPSE_RULES_DIR;
					String detectedProvider = providerDetection.doAnalysis(icfg, rootRulesDirectory);
					if(detectedProvider != null) {
						rules.clear();
						String newRulesDirectory = Constants.ECLIPSE_RULES_DIR + Constants.innerFileSeparator + detectedProvider + Constants.innerFileSeparator + 
													CrySLUtils.getRuleVersions(detectedProvider)[CrySLUtils.getRuleVersions(detectedProvider).length - 1] + Constants.innerFileSeparator + detectedProvider;
						rules.addAll(providerDetection.chooseRules(newRulesDirectory));
					}
				}
				scanner.scan(rules);
			}
		};
	}

	private static List<CrySLRule> getRules(IProject project) {
		
		List<CrySLRule> rules = Lists.newArrayList();
		// TODO Select rules according to selected rulesets in preference page. The CrySL rules for each ruleset are in a separate subdirectory of "/resources/CrySLRules/".
		Set<String> readRules = Sets.newHashSet();
 		IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
 		
		try {
			CrySLParser r = new CrySLParser(project);
			for (String path : projectClassPath(JavaCore.create(project))) {
				List<CrySLRule> readRuleFromBinaryFiles = r.readRulesOutside(path);
//				readRuleFromBinaryFiles.stream().forEach(e -> System.out.println(e.getClassName()));
				rules.addAll(readRuleFromBinaryFiles);
			}

			for (String path : applicationClassPath(JavaCore.create(project))) {
				List<CrySLRule> readRuleFromBinaryFiles = r.readRulesOutside(path);
//				readRuleFromBinaryFiles.stream().forEach(e -> System.out.println(e.getClassName()));
				rules.addAll(readRuleFromBinaryFiles);
			}
			
			if (prefStore.getBoolean(Constants.SELECT_CUSTOM_RULES)) {
 				Activator.getDefault().logInfo("Loading custom rules.");
 				rules.addAll(Files
 						.find(Paths.get(Utils.getResourceFromWithin(Constants.RELATIVE_CUSTOM_RULES_DIR).getPath()), 
 								Integer.MAX_VALUE,
 								(file, attr) -> file.toString().endsWith(RuleFormat.SOURCE.toString()))
 						.map(path -> {
 								readRules.add(path.getFileName().toString());
 								return CrySLRuleReader.readFromSourceFile(path.toFile());
 						}).collect(Collectors.toList()));
 			}
			
			Preferences prefs = InstanceScope.INSTANCE.getNode(de.cognicrypt.core.Activator.PLUGIN_ID);
 			try {
 				String[] listOfNodes = prefs.childrenNames();
 				for (String currentNode : listOfNodes) {
 					Preferences subPref = prefs.node(currentNode);
 					Ruleset loadedRuleset = new Ruleset(subPref);
 					if (loadedRuleset.isChecked()) {
 						String folderPath = Constants.ECLIPSE_RULES_DIR + File.separator + loadedRuleset.getFolderName() + 
 								File.separator + loadedRuleset.getSelectedVersion();
 						rules.addAll(Files
 								.find(Paths.get(new File(folderPath).getPath()), Integer.MAX_VALUE,
 										(file, attr) -> { 
 											if (file.toString().endsWith(RuleFormat.SOURCE.toString()) && 
 													!readRules.contains(file.getFileName().toString())) {
 												return true;
 											}
 											return false;
 										})
 								.map(path -> {
 										return r.readRule(path.toFile());
 								}).collect(Collectors.toList()));
 					}
 				}
 			} catch (BackingStoreException e) {
 				Activator.getDefault().logError(e);
 			}
 			
		}
		catch (IOException | CoreException e) {
			Activator.getDefault().logError(e, "Could not load CrySL Rules");
		} 
		if (rules.isEmpty()) {
			Activator.getDefault().logInfo("No CrySL rules loaded");
		}
		
		return rules;
	}

	private static List<String> projectClassPath(final IJavaProject javaProject) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			final List<String> urls = new ArrayList<>();
			final URI uriString = workspace.getRoot().getFile(javaProject.getOutputLocation()).getLocationURI();
			urls.add(new File(uriString).getAbsolutePath());
			return urls;
		}
		catch (final Exception e) {
			Activator.getDefault().logError(e, "Error building project classpath");
			return Lists.newArrayList();
		}
	}

	public static boolean runSoot(final IJavaProject project, final ResultsCCUIListener resultsReporter, final Boolean dependencyAnalyser) {

		G.reset();
		setSootOptions(project, dependencyAnalyser);
		registerTransformers(resultsReporter);
		try {
			runSoot(resultsReporter);
		}
		catch (final Exception t) {
			Activator.getDefault().logError(t);
			return false;
		}
		return true;
	}

	private static void runSoot(final ResultsCCUIListener resultsReporter) {
		Scene.v().loadNecessaryClasses();
		PackManager.v().getPack("cg").apply();
		resultsReporter.setCgGenComplete(true);
		PackManager.v().getPack("wjtp").apply();
	}

	private static void setSootOptions(final IJavaProject project, final Boolean dependencyAnalyser) {

		if (dependencyAnalyser == true) {
			Options.v().set_soot_classpath(Joiner.on(File.pathSeparator).join(libraryClassPath(project, dependencyAnalyser)));
			Options.v().set_process_dir(Lists.newArrayList(libraryClassPath(project, dependencyAnalyser)));
		} else {
			Options.v().set_soot_classpath(getSootClasspath(project, dependencyAnalyser));
			Options.v().set_process_dir(Lists.newArrayList(applicationClassPath(project)));
		}
		Options.v().set_keep_line_number(true);
		Options.v().set_prepend_classpath(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_whole_program(true);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_include(getIncludeList());
		Options.v().set_exclude(getExcludeList(project));
		Scene.v().loadNecessaryClasses();
		// choose call graph based on what user selected on preference page
		switch (Activator.getDefault().getPreferenceStore().getInt(Constants.CALL_GRAPH_SELECTION)) {
			case 1:
				Options.v().setPhaseOption("cg.spark", "on");
				Options.v().setPhaseOption("cg", "all-reachable:true,library:any-subtype");
				break;
			case 0:
			default:
				Options.v().setPhaseOption("cg.cha", "on");
				Options.v().setPhaseOption("cg", "all-reachable:true");
		}
		Options.v().setPhaseOption("jb", "use-original-names:true");
		Options.v().set_output_format(Options.output_format_none);
	}

	private static List<String> getIncludeList() {
		final List<String> includeList = new LinkedList<String>();
		includeList.add("java.lang.AbstractStringBuilder");
		includeList.add("java.lang.Boolean");
		includeList.add("java.lang.Byte");
		includeList.add("java.lang.Class");
		includeList.add("java.lang.Integer");
		includeList.add("java.lang.Long");
		includeList.add("java.lang.Object");
		includeList.add("java.lang.String");
		includeList.add("java.lang.StringCoding");
		includeList.add("java.lang.StringIndexOutOfBoundsException");
		return includeList;
	}

	private static List<String> getExcludeList(IJavaProject project) {
		final List<String> excludeList = new LinkedList<String>();
		for (final CrySLRule r : getRules(project.getProject())) {
			try {
				String fullyQualifiedName = crypto.Utils.getFullyQualifiedName(r);
				excludeList.add(fullyQualifiedName);
			}
			catch (RuntimeException e) {
				Activator.getDefault().logError(e);
			}
		}
		return excludeList;
	}

	private static void registerTransformers(final ResultsCCUIListener resultsReporter) {
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.ifds", createAnalysisTransformer(resultsReporter)));
	}

	private static String getSootClasspath(final IJavaProject javaProject, final Boolean dependencyAnalyser) {

		Collection<String> applicationClassPath = applicationClassPath(javaProject);
		Collection<String> libraryClassPath = libraryClassPath(javaProject, dependencyAnalyser);

		libraryClassPath.addAll(applicationClassPath);
		return Joiner.on(File.pathSeparator).join(libraryClassPath);
	}

	private static Collection<String> applicationClassPath(final IJavaProject javaProject) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			final List<String> urls = new ArrayList<>();
			final URI uriString = workspace.getRoot().getFile(javaProject.getOutputLocation()).getLocationURI();
			urls.add(new File(uriString).getAbsolutePath());
			return urls;
		}
		catch (final Exception e) {
			Activator.getDefault().logError(e, "Error building project classpath");
			return Lists.newArrayList();
		}
	}

	private static Collection<String> libraryClassPath(IJavaProject project, Boolean dependencyAnalyser) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		Collection<String> libraryClassPath = Sets.newHashSet();
		IClasspathEntry[] rentries;
		try {
			// check if "include dependencies" checkbox is checked in preference page or analysis is running for dependencies
			if (store.getBoolean(Constants.ANALYSE_DEPENDENCIES) || dependencyAnalyser) {

				rentries = project.getRawClasspath();
				for (IClasspathEntry entry : rentries) {
					resolveClassPathEntry(entry, libraryClassPath, project);
				}
			}
		}
		catch (CoreException e1) {
			e1.printStackTrace();
		}
		return libraryClassPath;
	}

	private static void resolveClassPathEntry(IClasspathEntry entry, Collection<String> libraryClassPath, IJavaProject project) {
		IClasspathEntry[] rentries;
		switch (entry.getEntryKind()) {
			case IClasspathEntry.CPE_SOURCE:
				libraryClassPath.addAll(applicationClassPath(project));
				break;
			case IClasspathEntry.CPE_PROJECT:
				IJavaProject requiredProject = JavaCore.create((IProject) ResourcesPlugin.getWorkspace().getRoot().findMember(entry.getPath()));
				try {
					rentries = project.getRawClasspath();
					for (IClasspathEntry e : rentries) {
						resolveClassPathEntry(e, libraryClassPath, requiredProject);
					}

				}
				catch (JavaModelException e1) {
					Activator.getDefault().logError(e1);
				}
				break;
			case IClasspathEntry.CPE_LIBRARY:

				if (entry.getPath().segment(0).equals(project.getProject().getName())) {
					libraryClassPath.add(project.getProject().getParent().getRawLocation() + Constants.innerFileSeparator + entry.getPath().toOSString());
				} else {
					libraryClassPath.add(entry.getPath().toOSString());
				}
				break;
			case IClasspathEntry.CPE_VARIABLE:
				// JRE entry
				break;
			case IClasspathEntry.CPE_CONTAINER:
				try {
					IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), project);
					IClasspathEntry[] subEntries = container.getClasspathEntries();
					for (IClasspathEntry subEntry : subEntries) {
						resolveClassPathEntry(subEntry, libraryClassPath, project);
					}
				}
				catch (JavaModelException e) {
					Activator.getDefault().logError(e);
				}
				break;
		}
	}

}