/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Sarah Nadi
 *
 */
package crossing.e1.configurator.utilities;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

import crossing.e1.configurator.Activator;

@SuppressWarnings("restriction")
public class Utils {

<<<<<<< HEAD
=======
	public static List<IProject> javaProjects;

>>>>>>> feature/EONE-91
	/**
	 * This method returns if a Java project is selected for code generation.
	 *
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if library java project selected.
	 */
	public static boolean checkIfJavaProjectSelected() {
		final IProject project = Utils.getIProjectFromSelection();
<<<<<<< HEAD
		final IJavaProject javaProject = JavaCore.create(project);
		if (javaProject == null || !javaProject.exists() || project == null) {
			/*
			 * MessageDialog.openWarning(new Shell(), "Warning",
			 * "CogniCrypt requires a target Java project in order to perform successful code generation. Please select or create a Java project. " );
			 */
=======
		return (checkIfJavaProjectSelected(project));
	}

	/**
	 * This method checks if a project passed as parameter is a Java project or not.
	 * 
	 * @param Iproject
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if project is Java project
	 */
	public static boolean checkIfJavaProjectSelected(final IProject project) {
		final IJavaProject javaProject = JavaCore.create(project);
		if (javaProject == null || !javaProject.exists()) {
>>>>>>> feature/EONE-91
			return false;
		}
		return true;
	}

<<<<<<< HEAD
	/**
	 * This method checks if a project passed as parameter is a Java project or not.
	 * 
	 * @param Iproject
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if project is Java project
	 */
	public static boolean checkIfJavaProjectSelected(final IProject project) {
		final IJavaProject javaProject = JavaCore.create(project);
		if (javaProject == null || !javaProject.exists()) {
			return false;
		}
		return true;
=======
	public static IProject defaultProjectSelectionforDropdownList() {
		IProject defaultProject = null;

		if (Utils.getCurrentlyOpenFile() != null && Utils.getCurrentlyOpenFile().getFileExtension().equalsIgnoreCase("java")) {
			defaultProject = Utils.getCurrentlyOpenFile().getProject();
		} else if (Utils.checkIfJavaProjectSelected()) {
			defaultProject = Utils.getIProjectFromSelection();
		} else {
			defaultProject = null;
		}
		return defaultProject;
>>>>>>> feature/EONE-91
	}

	/**
	 * This method returns the currently open editor as an {@link IEditorPart}.
	 *
	 * @return Current editor.
	 */
	public static IEditorPart getCurrentlyOpenEditor() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			return window.getActivePage().getActiveEditor();
		}
		return null;
	}

	/**
	 * Overload for {@link Utils#getCurrentlyOpenFile(IEditorPart) getCurrentlyOpenFile(IEditor part)}
	 *
	 * @return Currently open file.
	 *
	 */
	public static IFile getCurrentlyOpenFile() {
		return Utils.getCurrentlyOpenFile(getCurrentlyOpenEditor());
	}

	/**
	 * This method gets the file that is currently opened in the editor as an {@link IFile}.
	 *
	 * @param part
	 *        Editor part that contains the file.
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

	/**
	 * This method gets the project that is currently selected.
	 *
	 * @return Currently selected project.
	 */
	public static IProject getIProjectFromSelection() {
		final ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
		final ISelection selection = selectionService.getSelection();

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

	/***
	 * This method returns absolute path of a project-relative path.
	 * 
	 * @param inputPath
	 *        project-relative path
	 * @return absolute path
	 */
	public static File getResourceFromWithin(final String inputPath) {
		try {
			final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);

			if (bundle == null) {
				System.out.println("Bundle is null");
				// running as application
				// final String fileName =
				// inputPath.substring(inputPath.lastIndexOf("/") + 1);
				return new File(inputPath);// Utilities.class.getClassLoader().getResource(fileName).getPath();
			} else {
				System.out.println(bundle.getSymbolicName());
				final URL fileURL = bundle.getEntry(inputPath);
				System.out.println("PATH: " + inputPath);
				final URL resolvedURL = FileLocator.toFileURL(fileURL);
				final URI uri = new URI(resolvedURL.getProtocol(), resolvedURL.getPath(), null);
				return new File(uri);
			}
		} catch (final Exception ex) {
			Activator.getDefault().logError(ex);
		}

		return null;
	}

<<<<<<< HEAD
	public static IProject getProjectSelection() {
		IProject defaultProject = null;
=======
	public static List<IProject> createListOfJavaProjectsInCurrentWorkspace() {
>>>>>>> feature/EONE-91
		final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		final List<IProject> javaProjects = new ArrayList<>();
		if (projects.length > 0) {
			for (int i = 0; i < projects.length; i++) {
				if (Utils.checkIfJavaProjectSelected(projects[i])) {
					javaProjects.add(projects[i]);
				}
			}

		}
<<<<<<< HEAD
		if (Utils.getCurrentlyOpenFile() != null && Utils.getCurrentlyOpenFile().getFileExtension().equalsIgnoreCase("java")) {
			defaultProject = Utils.getCurrentlyOpenFile().getProject();
		} else if (Boolean.TRUE.equals(Utils.checkIfJavaProjectSelected())) {
			defaultProject = Utils.getIProjectFromSelection();
		} else {
			defaultProject = null;
		}
		final IProject[] javaProject = javaProjects.toArray(new IProject[javaProjects.size()]);
		final IProject targetFile = (IProject) JOptionPane.showInputDialog(null,
			"CogniCrypt requires a java project which acts as target for code generation. Please choose a Java project.", "CogniCrypt", JOptionPane.QUESTION_MESSAGE, null,
			javaProject, defaultProject);
		return targetFile;
=======
		return javaProjects;
>>>>>>> feature/EONE-91
	}

}
