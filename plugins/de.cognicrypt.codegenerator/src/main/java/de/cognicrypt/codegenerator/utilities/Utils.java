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
package de.cognicrypt.codegenerator.utilities;

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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.codegenerator.Constants;

@SuppressWarnings("restriction")
public class Utils {

	public static List<IProject> javaProjects;

	/**
	 * This method returns if a Java project is selected for code generation.
	 *
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if library java project selected.
	 */
	public static boolean checkIfJavaProjectSelected() {
		final IProject project = Utils.getIProjectFromSelection();
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
			return false;
		}
		return true;
	}

	public static List<IProject> createListOfJavaProjectsInCurrentWorkspace() {
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

	/** This method close the currently open editor
	 * 
	 * @param editorPart
	 */
	public static void closeEditor(IEditorPart editorPart) {
		IWorkbenchPage workbenchPage = Utils.getCurrentlyOpenPage();
		if (workbenchPage != null) {
			workbenchPage.closeEditor(editorPart, true);
		}
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

	public static IProject getCurrentProject() {
		IProject currentProject = null;
		if (Constants.WizardActionFromContextMenuFlag) {
			currentProject = Utils.getIProjectFromSelection();
		} else if (!Constants.WizardActionFromContextMenuFlag && (Utils.getCurrentlyOpenFile() != null && Utils.getCurrentlyOpenFile().getFileExtension()
			.equalsIgnoreCase("java"))) {
			currentProject = Utils.getCurrentlyOpenFile().getProject();
		} else if (Utils.checkIfJavaProjectSelected()) {
			currentProject = Utils.getIProjectFromSelection();
		} else {
			currentProject = null;
		}

		return currentProject;
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
				// running as application
				return new File(inputPath);
			} else {
				final URL fileURL = bundle.getEntry(inputPath);
				final URL resolvedURL = FileLocator.toFileURL(fileURL);
				final URI uri = new URI(resolvedURL.getProtocol(), resolvedURL.getPath(), null);
				return new File(uri);
			}
		} catch (final Exception ex) {
			Activator.getDefault().logError(ex);
		}

		return null;
	}

}
