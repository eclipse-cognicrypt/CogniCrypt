/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.cognicrypt.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.osgi.framework.Bundle;

import com.google.common.base.CharMatcher;

import crypto.analysis.CrySLRulesetSelector.RuleFormat;
import crypto.cryptslhandler.CrySLModelReader;
import crypto.rules.CryptSLRule;
import crypto.rules.CryptSLRuleReader;
import crypto.rules.StateNode;
import crypto.rules.TransitionEdge;
import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;

public class Utils {

	private static IWorkbenchWindow window = null;

	/**
	 * This method checks if a project passed as parameter is a Java project or not.
	 *
	 * @param Iproject
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if project is Java project
	 */
	public static boolean checkIfJavaProjectSelected(final IProject project) {
		try {
			return project.hasNature("org.eclipse.jdt.core.javanature");
		}
		catch (final CoreException e) {
			return false;
		}
	}

	public static List<IProject> complileListOfJavaProjectsInWorkspace() {
		final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		final List<IProject> javaProjects = new ArrayList<>();
		if (projects.length > 0) {
			for (int i = 0; i < projects.length; i++) {
				if (Utils.checkIfJavaProjectSelected(projects[i])) {
					javaProjects.add(projects[i]);
				}
			}
		}

		return javaProjects;
	}

	public static IResource findClassByName(final String className, final IProject currentProject) throws ClassNotFoundException {
		try {
			for (final IPackageFragment l : JavaCore.create(currentProject).getPackageFragments()) {
				for (final ICompilationUnit cu : l.getCompilationUnits()) {
					final IJavaElement cuResource = JavaCore.create(cu.getCorrespondingResource());
					String name = cuResource.getParent().getElementName() + "." + cuResource.getElementName();

					if (name.startsWith(".")) {
						name = name.substring(1);
					}
					if (name.startsWith(className)) {
						return cu.getCorrespondingResource();
					}
				}
			}
		}
		catch (final JavaModelException e) {
			throw new ClassNotFoundException("Class " + className + " not found.", e);
		}
		throw new ClassNotFoundException("Class " + className + " not found.");
	}

	/**
	 * This method returns the currently open editor as an {@link IEditorPart}.
	 *
	 * @return Current editor.
	 */
	public static IEditorPart getCurrentlyOpenEditor() {
		final Display defaultDisplay = Display.getDefault();
		final Runnable getWindow = () -> setWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		defaultDisplay.syncExec(getWindow);
		if (Utils.window == null) {
			try {
				Thread.sleep(500);
			}
			catch (final InterruptedException e) {
				Activator.getDefault().logError(e);
			}
			defaultDisplay.asyncExec(getWindow);
		}

		if (Utils.window != null) {
			return Utils.window.getActivePage().getActiveEditor();
		}
		return null;
	}

	/**
	 * Overload for {@link Utils#getCurrentlyOpenFile(IEditorPart) getCurrentlyOpenFile(IEditor part)}
	 *
	 * @return Currently open file.
	 */
	public static IFile getCurrentlyOpenFile() {
		return getCurrentlyOpenFile(getCurrentlyOpenEditor());
	}

	/**
	 * This method gets the file that is currently opened in the editor as an {@link IFile}.
	 *
	 * @param part Editor part that contains the file.
	 * @return Currently open file.
	 */
	public static IFile getCurrentlyOpenFile(final IEditorPart part) {
		if (part != null) {
			final IEditorInput editorInput = part.getEditorInput();
			if (editorInput instanceof FileEditorInput) {
				final FileEditorInput inputFile = (FileEditorInput) part.getEditorInput();
				return inputFile.getFile();
			}
		}
		return null;
	}

	public static IProject getCurrentProject() {
		final IFile currentlyOpenFile = Utils.getCurrentlyOpenFile();
		if (currentlyOpenFile != null) {
			final IProject curProject = currentlyOpenFile.getProject();
			if (checkIfJavaProjectSelected(curProject)) {
				return curProject;
			}
		}
		final IProject selectedProject = Utils.getCurrentlySelectedIProject();
		if (selectedProject != null && checkIfJavaProjectSelected(selectedProject)) {
			return selectedProject;
		}
		return null;
	}

	/**
	 * This method returns the currently open page as an {@link IWorkbenchPage}.
	 *
	 * @return Current editor.
	 */
	public static IWorkbenchPage getCurrentlyOpenPage() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			return window.getActivePage();
		}
		return null;
	}

	/**
	 * This method closes the currently open editor.
	 *
	 * @param editor
	 */
	public static void closeEditor(final IEditorPart editor) {
		final IWorkbenchPage workbenchPage = Utils.getCurrentlyOpenPage();
		if (workbenchPage != null) {
			workbenchPage.closeEditor(editor, true);
		}
	}

	/**
	 * This method searches the passed project for the class that contains the main method.
	 *
	 * @param project Project that is searched
	 * @param requestor Object that handles the search results
	 */
	public static void findMainMethodInCurrentProject(final IJavaProject project, final SearchRequestor requestor) {
		final SearchPattern sp = SearchPattern.createPattern("main", IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);

		final SearchEngine se = new SearchEngine();
		final SearchParticipant[] searchParticipants = new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()};
		final IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] {project});

		try {
			se.search(sp, searchParticipants, scope, requestor, null);
		}
		catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
	}

	/**
	 * This method searches the passed project for the class that contains the main method.
	 *
	 * @param project Project that is searched
	 * @param requestor Object that handles the search results
	 * @throws CoreException
	 */
	public static IFile findFileInProject(IContainer container, String name) throws CoreException {
		for (IResource res : container.members()) {
			if (res instanceof IContainer) {
				IFile file = findFileInProject((IContainer) res, name);
				if (file != null) {
					return file;
				}
			} else if (res instanceof IFile && (res.getName().equals(name.substring(name.lastIndexOf(".") + 1) + ".java"))) {
				return (IFile) res;
			}
		}

		return null;
	}

	/**
	 * This method gets the project that is currently selected.
	 *
	 * @return Currently selected project.
	 */
	public static IProject getCurrentlySelectedIProject() {
		ISelection curSel = getCurrentSelection();
		Object resource = null;
		if ((resource = getIResourceFromSelection(curSel)) != null) {
			return ((IProject) resource).getProject();
		} else {
			return getJavaProjectFromSelection(curSel);
		}
	}

	private static ISelection getCurrentSelection() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
	}

	public static IResource getCurrentlySelectedIResource() {
		return getIResourceFromSelection(getCurrentSelection());
	}

	private static IResource getIResourceFromSelection(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IResource) {
				return (IResource) element;
			}
		}
		return null;
	}

	private static IProject getJavaProjectFromSelection(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IJavaElement) {
				return ((IJavaElement) element).getJavaProject().getProject();
			}
		}
		return null;
	}

	public static File getResourceFromWithin(final String inputPath) {
		return getResourceFromWithin(inputPath, Activator.PLUGIN_ID);
	}

	/***
	 * This method returns absolute path of a project-relative path.
	 *
	 * @param inputPath project-relative path
	 * @return absolute path
	 */
	public static File getResourceFromWithin(final String inputPath, final String pluginID) {
		try {
			final Bundle bundle = Platform.getBundle(pluginID);
			if (bundle == null) {
				return new File(inputPath);
			} else {
				final URL entry = bundle.getEntry(inputPath);
				final URL resolvedURL = FileLocator.toFileURL(entry);
				URI resolvedURI = null;
				if (!(resolvedURL == null)) {
					resolvedURI = new URI(resolvedURL.getProtocol(), resolvedURL.getPath(), null);
				} else {
					resolvedURI = FileLocator.resolve(entry).toURI();
				}
				return new File(resolvedURI);
			}
		}
		catch (final Exception ex) {
			Activator.getDefault().logError(ex);
		}

		return null;
	}
	
	/***
 	 * Returns parsed objects of resources/configuration.ini file.
 	 * @return Wini object
 	 */
 	public static Wini getConfig() {
 		Wini ini = null;
 		try {
 			ini = new Wini(getResourceFromWithin(Constants.CONFIG_FILE_PATH));
 		} catch (InvalidFileFormatException e) {
 			Activator.getDefault().logError("Could not read the configuration file due to: " + e.getMessage());
 		} catch (IOException e) {
 			Activator.getDefault().logError("Failed identifying configuration file due to: " + e.getMessage());
 		}
 		return ini;
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
 		for (File f: innerDirs) {
 			if (f.isDirectory()) {
 				String[] versionNumber = f.getPath().split(Matcher.quoteReplacement(System.getProperty("file.separator")));
 				versions.add(versionNumber[versionNumber.length - 1]);
 			}
 		}

 		versions.sort(new Comparator<String>() {
 			@Override
 			public int compare(String o1, String o2) {
 				Double one = Double.valueOf(o1);
 				Double two = Double.valueOf(o2);
 				return one.compareTo(two);
 			}
 		});

 		// https://shipilev.net/blog/2016/arrays-wisdom-ancients/
 		return versions.toArray(new String[0]);
 	}

	protected static void setWindow(final IWorkbenchWindow activeWorkbenchWindow) {
		Utils.window = activeWorkbenchWindow;
	}

	public static String filterQuotes(final String dirty) {
		return CharMatcher.anyOf("\"").removeFrom(dirty);
	}

	public static int getFirstIndexofUCL(final String searchString) {
		final OptionalInt index = searchString.chars().filter(n -> Character.isUpperCase(n)).findFirst();
		if (index.isPresent()) {
			return searchString.indexOf(index.getAsInt());
		} else {
			return -1;
		}
	}

	/**
	 * Returns the cryptsl rule with the name that is defined by the method parameter cryptslRule.
	 * 
	 * @param cryptslRule Name of cryptsl rule that should by returend.
	 * @return Returns the cryptsl rule with the name that is defined by the parameter cryptslRule.
	 * @throws MalformedURLException
	 */
	public static CryptSLRule getCryptSLRule(String cryptslRule) throws MalformedURLException {
		File ruleRes = Utils.getResourceFromWithin(Constants.RELATIVE_CUSTOM_RULES_DIR + "/" + cryptslRule + RuleFormat.SOURCE.toString(), de.cognicrypt.core.Activator.PLUGIN_ID);
		if (ruleRes == null || !ruleRes.exists() || !ruleRes.canRead()) {
			ruleRes = Utils.getResourceFromWithin(Constants.RELATIVE_CUSTOM_RULES_DIR + "/" + cryptslRule + RuleFormat.SOURCE.toString(), de.cognicrypt.core.Activator.PLUGIN_ID);
		}
		return (new CrySLModelReader()).readRule(ruleRes);
	}

	public static List<CryptSLRule> readCrySLRules() {
		return Stream.of(readCrySLRules(Utils.getResourceFromWithin(Constants.RELATIVE_CUSTOM_RULES_DIR).getAbsolutePath()),
				readCrySLRules(Utils.getResourceFromWithin(Constants.RELATIVE_CUSTOM_RULES_DIR).getAbsolutePath())).flatMap(Collection::stream).collect(Collectors.toList());
	}

	protected static List<CryptSLRule> readCrySLRules(String rulesFolder) {
		List<CryptSLRule> rules = new ArrayList<CryptSLRule>();

		for (File rule : (new File(rulesFolder)).listFiles()) {
			if (rule.isDirectory()) {
				rules.addAll(readCrySLRules(rule.getAbsolutePath()));
				continue;
			}

			CryptSLRule readFromSourceFile = CryptSLRuleReader.readFromSourceFile(rule);
			if (readFromSourceFile != null) {
				rules.add(readFromSourceFile);
			}
		}
		return rules;
	}

	public static List<TransitionEdge> getOutgoingEdges(Collection<TransitionEdge> collection, final StateNode curNode, final StateNode notTo) {
		final List<TransitionEdge> outgoingEdges = new ArrayList<>();
		for (final TransitionEdge comp : collection) {
			if (comp.getLeft().equals(curNode) && !(comp.getRight().equals(curNode) || comp.getRight().equals(notTo))) {
				outgoingEdges.add(comp);
			}
		}
		return outgoingEdges;
	}

	public static boolean isSubType(String typeOne, String typeTwo) {
		boolean subTypes = typeOne.equals(typeTwo);
		subTypes |= (typeOne + "[]").equals(typeTwo);
		if (!subTypes) {
			try {
				subTypes = Class.forName(typeOne).isAssignableFrom(Class.forName(typeTwo));
			}
			catch (ClassNotFoundException e) {}
		}
		return subTypes;
	}

	public static Group addHeaderGroup(Composite parent, String text) {
		final Group headerGroup = new Group(parent, SWT.SHADOW_IN);
		headerGroup.setText(text);
		headerGroup.setLayout(new GridLayout(1, true));
		return headerGroup;
	}

	public static boolean isIncompatibleJavaVersion() {
		return isIncompatibleJavaVersion(System.getProperty("java.version", null));
	}

	public static boolean isIncompatibleJavaVersion(String javaVersion) {
		return javaVersion == null || !javaVersion.startsWith("1.");
	}
	
	/**
	 * This method checks if a Collection is null or empty
	 * @param c
	 * @return 
	 */
	public static boolean isNullOrEmpty( final Collection< ? > c ) {
	    return c == null || c.isEmpty();
	}
}
