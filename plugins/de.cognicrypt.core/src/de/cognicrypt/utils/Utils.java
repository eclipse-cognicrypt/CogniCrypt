/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.cognicrypt.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
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
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.osgi.framework.Bundle;
import com.google.common.base.CharMatcher;
import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;

public class Utils {

	static IWorkbenchWindow window = null;

	/**
	 * This method checks if a project passed as parameter is a Java project or not.
	 *
	 * @param Iproject
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if project is Java project
	 */
	public static boolean checkIfJavaProjectSelected(final IProject project) {
		try {
			return project.hasNature(Constants.JavaNatureID);
		}
		catch (final CoreException e) {
			Activator.getDefault().logError(e, Constants.NOT_HAVE_NATURE);
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
					for (IJavaElement a : (ArrayList<IJavaElement>)((CompilationUnit) cuResource).getChildrenOfType(7)) {
						String name = cuResource.getParent().getElementName() + "." + a.getElementName();

						if (name.startsWith(".")) {
							name = name.substring(1);
						}
						if (name.equals(className)) {
							return cu.getCorrespondingResource();
						}
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
	 * Overload for {@link UIUtils#getCurrentlyOpenFile(IEditorPart) getCurrentlyOpenFile(IEditor part)}
	 *
	 * @return Currently open file.
	 */
	public static IFile getCurrentlyOpenFile() {
		return UIUtils.getCurrentlyOpenFile(UIUtils.getCurrentlyOpenEditor());
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
			Activator.getDefault().logError(e, "Could not find main method in the project: "+project.getProject().getName());
		}
	}

	/**
	 * This method searches the passed project for the class that contains the main method.
	 *
	 * @param project Project that is searched
	 * @param requestor Object that handles the search results
	 * @throws CoreException
	 */
	public static IFile findFileInProject(IContainer container, String name) {
		try {
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
		} catch (CoreException e) {
			Activator.getDefault().logError(e);
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
				if (entry == null) {
					return null;
				}
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
		catch (final IOException ex) {
			Activator.getDefault().logError(ex, Constants.ERROR_MESSAGE_NO_FILE);
		} catch (URISyntaxException ex) {
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

	public static boolean isSubType(String typeOne, String typeTwo) {
		boolean subTypes = typeOne.equals(typeTwo);
		subTypes |= ("byte".equals(typeOne) && (typeOne + "[]").equals(typeTwo));
		if (!subTypes) {
			try {
				subTypes = Class.forName(typeOne).isAssignableFrom(Class.forName(typeTwo));
			}
			catch (ClassNotFoundException e) {
				// It's fine if above throws a ClassNotFoundException
			}
		}
		return subTypes;
	}
}
