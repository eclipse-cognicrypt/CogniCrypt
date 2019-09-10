/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.sootbridge;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.preference.IPreferenceStore;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import boomerang.callgraph.ObservableDynamicICFG;
import boomerang.callgraph.ObservableICFG;
import boomerang.callgraph.ObservableStaticICFG;
import boomerang.preanalysis.BoomerangPretransformer;
import crypto.analysis.CryptoScanner;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.results.ResultsCCUIListener;
import de.cognicrypt.utils.Utils;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.options.Options;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

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
				BoomerangPretransformer.v().apply();
				final ObservableDynamicICFG icfg = new ObservableDynamicICFG(true);
				CryptoScanner scanner = new CryptoScanner() {

					@Override
					public ObservableICFG<Unit, SootMethod> icfg() {
						return icfg;
					}
				};
				scanner.getAnalysisListener().addReportListener(resultsReporter);
				scanner.scan(getRules());
			}
		};
	}

	private static List<CryptSLRule> getRules() {
		List<CryptSLRule> rules = Lists.newArrayList();
		// TODO Select rules according to selected rulesets in preference page. The
		// CrySL rules for each ruleset are in a separate subdirectory of
		// "/resources/CrySLRules/".
		try {
			rules.addAll(Files
					.find(Paths.get(Utils.getResourceFromWithin("/resources/CrySLRules/").getPath()), Integer.MAX_VALUE,
							(file, attr) -> file.toString().endsWith(".cryptslbin"))
					.map(path -> CryptSLRuleReader.readFromFile(path.toFile())).collect(Collectors.toList()));
		} catch (IOException e) {
			Activator.getDefault().logError(e, "Could not load CrySL Rules");
		}
		if (rules.isEmpty()) {
			Activator.getDefault().logInfo("No CrySL rules loaded");
		}
		return rules;
	}


	public static boolean runSoot(final IJavaProject project, final ResultsCCUIListener resultsReporter, final Boolean dependencyAnalyser) {
		
		G.reset();
		setSootOptions(project, dependencyAnalyser);
		registerTransformers(resultsReporter);
		try {
			runSoot();
		} catch (final Exception t) {
			Activator.getDefault().logError(t);
			return false;
		}
		return true;
	}

	private static void runSoot() {
		Stopwatch watch = Stopwatch.createStarted();
		PackManager.v().getPack("cg").apply();
		long elapsed = watch.elapsed(TimeUnit.SECONDS);
		watch.reset();
		watch.start();
		Activator.getDefault().logInfo("Call graph generated in  "+ elapsed + " seconds." );
		PackManager.v().getPack("wjtp").apply();
		long analysisTime = watch.elapsed(TimeUnit.SECONDS);
		Activator.getDefault().logInfo("CogniCrypt Analysis terminated in "+ analysisTime + " seconds." );
	}

	private static void setSootOptions(final IJavaProject project, final Boolean dependencyAnalyser) {

		if (dependencyAnalyser == true) {
			Options.v().set_soot_classpath(Joiner.on(File.pathSeparator).join(libraryClassPath(project, dependencyAnalyser))); 
			Options.v().set_process_dir(Lists.newArrayList(libraryClassPath(project, dependencyAnalyser)));
		}else {
			Options.v().set_soot_classpath(getSootClasspath(project, dependencyAnalyser)); 
			Options.v().set_process_dir(Lists.newArrayList(applicationClassPath(project))); 
		}
		Options.v().set_keep_line_number(true);
		Options.v().set_prepend_classpath(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_whole_program(true);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_include(getIncludeList());
		Options.v().set_exclude(getExcludeList());
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

	private static List<String> getExcludeList() {
		final List<String> excludeList = new LinkedList<String>();
		for (final CryptSLRule r : getRules()) {
			excludeList.add(crypto.Utils.getFullyQualifiedName(r));
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
		System.out.println(Joiner.on(File.pathSeparator).join(libraryClassPath));
		return Joiner.on(File.pathSeparator).join(libraryClassPath);
	}

	private static Collection<String> applicationClassPath(final IJavaProject javaProject) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			final List<String> urls = new ArrayList<>();
			final URI uriString = workspace.getRoot().getFile(javaProject.getOutputLocation()).getLocationURI();
			urls.add(new File(uriString).getAbsolutePath());
			return urls;
		} catch (final Exception e) {
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
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		return libraryClassPath;
	}
	
	private static void resolveClassPathEntry(IClasspathEntry entry, Collection<String> libraryClassPath, IJavaProject project) {
		IClasspathEntry[] rentries;
		switch (entry.getEntryKind()) {
		case IClasspathEntry.CPE_SOURCE:
			break;
		case IClasspathEntry.CPE_PROJECT:
            IJavaProject requiredProject = JavaCore.create((IProject) ResourcesPlugin.getWorkspace().getRoot().findMember(entry.getPath()));
			try {
				rentries = project.getRawClasspath();
				for (IClasspathEntry e : rentries) {
					resolveClassPathEntry(e, libraryClassPath, requiredProject);
				}
			} catch (JavaModelException e1) {
				e1.printStackTrace();
			}
			break;
		case IClasspathEntry.CPE_LIBRARY:
			IPath path = entry.getPath();
			libraryClassPath.add(path.toString());
			break;
		case IClasspathEntry.CPE_VARIABLE:
			// JRE entry
			break;
		case IClasspathEntry.CPE_CONTAINER:
			try {
				IClasspathContainer container = JavaCore.getClasspathContainer(
				          entry.getPath(), project);
				IClasspathEntry[] subEntries = container.getClasspathEntries();
				for(IClasspathEntry subEntry : subEntries) {
					resolveClassPathEntry(subEntry, libraryClassPath, project);
				}
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			break;
		}
	}

}
