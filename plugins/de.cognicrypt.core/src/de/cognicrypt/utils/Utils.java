/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;
import com.google.common.base.CharMatcher;
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
	 * This method gets the project that is currently selected.
	 *
	 * @return Currently selected project.
	 */
	public static IProject getCurrentlySelectedIProject() {
		final ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
		final ISelection selection = selectionService.getSelection();

		return getIProjectFromISelection(selection);
	}

	public static IProject getIProjectFromISelection(final ISelection selection) {
		IProject iproject = null;
		if (selection instanceof IStructuredSelection) {
			final Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IResource) {
				iproject = ((IResource) element).getProject();
			} else if (element instanceof IJavaElement) {
				final IJavaProject jProject = ((IJavaElement) element).getJavaProject();
				iproject = jProject.getProject();
			}
		}

		return iproject;
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
	 * This method checks if a {@link ICompilationUnit} contains a certain import
	 * 
	 * @param unit
	 *            ICompilationUnit of the source file
	 * @param packagePath
	 *            path of the annotation
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the import (not) exists
	 * @throws CoreException
	 */
	public static boolean hasJarImport(final ICompilationUnit unit, final String packagePath) throws CoreException {
		final IImportDeclaration importDecalaration = unit.getImport(packagePath);
		if (importDecalaration.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method inserts a certain import to a {@link ICompilationUnit}
	 * 
	 * @param unit
	 *            ICompilationUnit of the source file
	 * @param packagePath
	 *            path of the annotation
	 * @throws CoreException
	 */
	public static void insertJarImport(final ICompilationUnit unit, final String packagePath) throws CoreException {
		unit.createImport(packagePath, null, null);
		unit.save(null, true);
		final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		editor.doSave(null);
	}
	
	/**
	 * This method gets a {@link ICompilationUnit} from the marker
	 * 
	 * @param marker
	 *            problem marker
	 * @return the ICompilationUnit of the source file of the problem marker
	 * @throws CoreException
	 */
	public static ICompilationUnit getCompilationUnitFromMarker(final IMarker marker) throws CoreException {
		final IJavaElement javaElement = JavaCore.create(marker.getResource());
		ICompilationUnit unit = null;
		if (javaElement instanceof ICompilationUnit) {
			unit = (ICompilationUnit) javaElement;
		}
		if (unit == null) {
			throw new CoreException(null);
		}
		return unit;
	}
	
	
	/**
	 * This method adds all files of a certain source to the client project
	 * 
	 * @param source
	 *            path to the source
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the adding was (not)
	 *         successfully
	 */
	public static boolean addAdditionalFiles(final String source, final String projectID, DeveloperProject clientProject) {
		if (source.isEmpty()) {
			return true;
		}
		try {
			final File[] members = Utils.getResourceFromWithin(source, projectID).listFiles();
			if (members == null) {
				Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_ADDITIONAL_RES_DIRECTORY);
			}
			for (int i = 0; i < members.length; i++) {
				final File addFile = members[i];
				if (!addAddtionalFile(clientProject, addFile)) {
					return false;
				}
			}
		} catch (IOException | CoreException e) {
			Activator.getDefault().logError(e, Constants.CodeGenerationErrorMessage);
			return false;
		}
		return true;
	}

	/**
	 * This method adds a {@link File} to the client project
	 * 
	 * @param fileToBeAdded
	 *            current file
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the adding was (not)
	 *         successfully
	 * @throws IOException
	 * @throws CoreException
	 */
	private static boolean addAddtionalFile(DeveloperProject clientProject,final File fileToBeAdded) throws IOException, CoreException {
		final IFolder libFolder = clientProject.getFolder(Constants.pathsForLibrariesInDevProject);
		if (!libFolder.exists()) {
			libFolder.create(true, true, null);
		}

		final Path memberPath = fileToBeAdded.toPath();
		Files.copy(memberPath,
				new File(clientProject.getProjectPath() + Constants.outerFileSeparator
						+ Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator
						+ memberPath.getFileName()).toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		final String filePath = fileToBeAdded.toString();
		final String cutPath = filePath.substring(filePath.lastIndexOf(Constants.outerFileSeparator));
		if (Constants.JAR.equals(cutPath.substring(cutPath.indexOf(".")))) {
			if (!clientProject.addJar(
					Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator + fileToBeAdded.getName())) {
				return false;
			}
		}
		return true;
	}

}
