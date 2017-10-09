package de.cognicrypt.staticanalyzer;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
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
				final URL fileURL = bundle.getEntry(inputPath);
				final URL resolvedURL = FileLocator.toFileURL(fileURL);
				final URI uri = new URI(resolvedURL.getProtocol(), resolvedURL.getPath(), null);
				return new File(uri);
			}
		} catch (final Exception ex) {
//			Activator.getDefault().logError(ex);
		}

		return null;
	}
	
	public static IProject getCurrentProject() {
		return getCurrentlyOpenFile().getProject();
	}
	
	/**
	 * This method returns the currently open editor as an {@link IEditorPart}.
	 *
	 * @return Current editor.
	 */
	public static IEditorPart getCurrentlyOpenEditor() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				setWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			}
			
		});
		if (window != null) {
			return window.getActivePage().getActiveEditor();
		}
		return null;
	}
	
	private static IWorkbenchWindow window = null;
	protected static void setWindow(IWorkbenchWindow activeWorkbenchWindow) {
		window = activeWorkbenchWindow;
	}

	/**
	 * Overload for {@link Utils#getCurrentlyOpenFile(IEditorPart) getCurrentlyOpenFile(IEditor part)}
	 *
	 * @return Currently open file.
	 *
	 */
	public static IFile getCurrentlyOpenFile() {
		return getCurrentlyOpenFile(getCurrentlyOpenEditor());
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
	
	public static void findMainMethodInCurrentProject(SearchRequestor requestor) {
		SearchPattern sp = SearchPattern.createPattern("main", IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
		
		SearchEngine se = new SearchEngine();
		final SearchParticipant[] searchParticipants = new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()};
		final IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		
		try {
			se.search(sp, searchParticipants, scope, requestor, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
