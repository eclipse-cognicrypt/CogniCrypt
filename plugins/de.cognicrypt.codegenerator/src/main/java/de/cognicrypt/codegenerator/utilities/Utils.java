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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
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
		return checkIfJavaProjectSelected(project);
	}

	/**
	 * This method checks if a project passed as parameter is a Java project or not.
	 *
	 * @param project
	 *        project to be checked
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if project is Java project
	 */
	public static boolean checkIfJavaProjectSelected(final IProject project) {
		try {
			return project.hasNature(Constants.JavaNatureID);
		} catch (CoreException e) {
			return false;
		}
	}

	/**
	 * Compiles a list of all Java Projects in the workspace.
	 * 
	 * @return List of Java Projects as {@link IProject}
	 */
	public static List<IProject> retrieveAllJavaProjectsInWorkspace() {
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

	/** This method closes the currently open editor.
	 * 
	 * @param editor
	 */
	public static void closeEditor(IEditorPart editor) {
		IWorkbenchPage workbenchPage = Utils.getCurrentlyOpenPage();
		if (workbenchPage != null) {
			workbenchPage.closeEditor(editor, true);
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
	 * Overload for {@link Utils#getCurrentlyOpenFile(IEditorPart) getCurrentlyOpenFile(IEditor part)}.
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
	 * Retrieves the current project. There are several options for what counts as the 'current' project. First, if CogniCrypt was started through context menu, the project
	 * right-clicked is the current project. Second, if the currently opened file is a Java file, its project is returned. Third, if the currently selected project, is a Java
	 * project, it is returned. If none of these conditions is fulfilled, <code>null</code> is returned.
	 * 
	 * @return Current project/<code>null</code> if project could be retrieved succesfully.
	 */
	public static IProject getCurrentProject() {
		final IProject selectedProject = Utils.getIProjectFromSelection();
		if (selectedProject != null && Constants.WizardActionFromContextMenuFlag) {
			return selectedProject;
		}

		final IFile currentlyOpenFile = Utils.getCurrentlyOpenFile();
		if (currentlyOpenFile != null) {
			final IProject curProject = currentlyOpenFile.getProject();
			if (checkIfJavaProjectSelected(curProject)) {
				return curProject;
			}
		}
		
		if (selectedProject != null && checkIfJavaProjectSelected(selectedProject)) {
			return selectedProject;
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

		if (selection instanceof IStructuredSelection) {
			final Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof IResource) {
				return ((IResource) element).getProject();
			} else if (element instanceof IJavaElement) {
				final IJavaProject jProject = ((IJavaElement) element).getJavaProject();
				return jProject.getProject();
			}
		}
		return null;
	}

	/**
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