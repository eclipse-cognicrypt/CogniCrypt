package de.cognicrypt.staticanalyzer;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

import soot.SootClass;

public class Utils {

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
				final URL resolvedURL = FileLocator.toFileURL(bundle.getEntry(inputPath));
				return new File(new URI(resolvedURL.getProtocol(), resolvedURL.getPath(), null));
			}
		} catch (final Exception ex) {
			Activator.getDefault().logError(ex);
		}

		return null;
	}

	public static IProject getCurrentProject() {
		final IFile currentlyOpenFile = getCurrentlyOpenFile();
		if (currentlyOpenFile == null) {
			return null;
		} else {
			return currentlyOpenFile.getProject();
		}
	}

	/**
	 * This method returns the currently open editor as an {@link IEditorPart}.
	 *
	 * @return Current editor.
	 */
	private static IEditorPart getCurrentlyOpenEditor() {
		final Display defaultDisplay = Display.getDefault();
		final Runnable getWindow = () -> setWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		defaultDisplay.asyncExec(getWindow);
		if (Utils.window == null) {
			try {
				Thread.sleep(500);
			} catch (final InterruptedException e) {
				Activator.getDefault().logError(e);
			}
			defaultDisplay.asyncExec(getWindow);
		}

		if (Utils.window != null) {
			return Utils.window.getActivePage().getActiveEditor();
		}
		return null;
	}

	private static IWorkbenchWindow window = null;

	private static void setWindow(final IWorkbenchWindow activeWorkbenchWindow) {
		Utils.window = activeWorkbenchWindow;
	}

	/**
	 * Overload for {@link Utils#getCurrentlyOpenFile(IEditorPart) getCurrentlyOpenFile(IEditor part)}
	 *
	 * @return Currently open file.
	 *
	 */
	private static IFile getCurrentlyOpenFile() {
		return getCurrentlyOpenFile(getCurrentlyOpenEditor());
	}

	/**
	 * This method gets the file that is currently opened in the editor as an {@link IFile}.
	 *
	 * @param part
	 *        Editor part that contains the file.
	 * @return Currently open file.
	 */
	private static IFile getCurrentlyOpenFile(final IEditorPart part) {
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
	 * This method searches the passed project for the class that contains the main method.
	 *
	 * @param project
	 *        Project that is searched
	 * @param requestor
	 *        Object that handles the search results
	 */
	public static void findMainMethodInCurrentProject(final IJavaProject project, final SearchRequestor requestor) {
		final SearchPattern sp = SearchPattern.createPattern("main", IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);

		final SearchEngine se = new SearchEngine();
		final SearchParticipant[] searchParticipants = new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() };
		final IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { project });

		try {
			se.search(sp, searchParticipants, scope, requestor, null);
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
	}

	public static IResource findClassByName(final SootClass className, final IProject currentProject) throws ClassNotFoundException {
		try {
			for (final IPackageFragment l : JavaCore.create(currentProject).getPackageFragments()) {
				for (final ICompilationUnit cu : l.getCompilationUnits()) {
					final IJavaElement cuResource = JavaCore.create(cu.getCorrespondingResource());
					String name = cuResource.getParent().getElementName() + "." + cuResource.getElementName();

					if (name.startsWith(".")) {
						name = name.substring(1);
					}
					if (name.startsWith(className.getName())) {
						return cu.getCorrespondingResource();
					}
				}
			}
		} catch (final JavaModelException e) {
			throw new ClassNotFoundException("Class " + className + " not found.", e);
		}
		throw new ClassNotFoundException("Class " + className + " not found.");
	}

}
