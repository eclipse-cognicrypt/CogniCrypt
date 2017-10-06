package de.cognicrypt.staticanalyzer.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.core.search.JavaWorkspaceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

import crypto.SourceCryptoScanner;
import de.cognicrypt.staticanalyzer.Activator;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CogniCryptStaticAnalyzerHandler extends AbstractHandler {

	private String mainClass; 
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		SearchPattern sp = SearchPattern.createPattern("main", IJavaSearchConstants.METHOD, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
		SearchRequestor requestor = new SearchRequestor() {

			@Override
			public void acceptSearchMatch(SearchMatch match) throws CoreException {
				String name = match.getResource().getProjectRelativePath().toString();
				name = name.substring(name.indexOf('/') + 1);
				name = name.replace("." + match.getResource().getFileExtension(), "");
				name = name.replace("/", ".");
				if (!name.isEmpty()) {
					mainClass = name;
				}
			}
		};
		SearchEngine se = new SearchEngine();
		final SearchParticipant[] searchParticipants = new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()};
		final IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
		
		try {
			se.search(sp, searchParticipants, scope, requestor, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IProject curProj = getCurrentProject();
		final File cryslFolder = getResourceFromWithin("/resources/CrySLRules/");
		final String path = cryslFolder.toPath().toAbsolutePath().toString();
		PrintStream out;
		try {
			PrintStream tmp = System.out;

			String outputPath = curProj.getLocation().toOSString();
			outputPath += "\\src\\output.log";
			new File(outputPath).createNewFile();
			out = new PrintStream(outputPath);
			System.setOut(out);
			
			SourceCryptoScanner.main(curProj.getLocation().toOSString() + "\\bin", mainClass, path);
			
			System.setOut(tmp);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(window.getShell(), "CogniCrypt Static Analyzer", "Hello, CogniCrypt.");
		
		return null;
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
}
