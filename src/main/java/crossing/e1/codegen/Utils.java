/**
 * Copyright 2015 Technische Universität Darmstadt
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
 * @author Stefan Krueger
 *
 */
package crossing.e1.codegen;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
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
	
	private final static Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
	
	/**
	 * This method resolves a project-relative path to a canonical path and creates a file out of it.
	 * @param path Path to file within plugin.
	 * @return Created file.
	 * @throws IOException See {@link  org.eclipse.core.runtime.FileLocator#toFileURL(URL) toFileURL()}.
	 * @throws URISyntaxException See {@link  org.osgi.framework.Bundle#getEntry(String) getEntry()}.
	 */
	public static File resolveResourcePathToFile(String path) throws URISyntaxException, IOException {
		if  (bundle != null) {
			URL resolvedFileURL = org.eclipse.core.runtime.FileLocator.toFileURL(bundle.getEntry(path));
			return new File(new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null));
		}
		return null;
	}
	
	/**
	 * Overload for {@link Utils#getCurrentlyOpenFile(IEditorPart) getCurrentlyOpenFile(IEditor part)}
	 * @return Currently open file.
	 * 
	 */
	public static IFile getCurrentlyOpenFile() {
		return getCurrentlyOpenFile(getCurrentlyOpenEditor());
	}
	
	/**
	 * This method gets the file that is currently opened in the editor as an {@link IFile}.
	 * @param part Editor part that contains the file.
	 * @return Currently open file.
	 */
	public static IFile getCurrentlyOpenFile(IEditorPart part) {
		if (part != null) {
			IEditorInput editorInput = part.getEditorInput();
			if (editorInput instanceof FileEditorInput) {
				FileEditorInput inputFile = (FileEditorInput) part.getEditorInput();
				return inputFile.getFile();
			}
		}
		return null;
	}

	/**
	 * This method gets the currently open editor as an {@link IEditorPart}.
	 * @return Current editor.
	 */
	public static IEditorPart getCurrentlyOpenEditor() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow(); 
		if (window != null) {
			return window.getActivePage().getActiveEditor();
	    }
		return null;
	}
		
	/**
	 * This method gets the project, which is currently selected.
	 * @return Currently selected project.
	 */
	public static IProject getIProjectFromSelection() {
		ISelectionService selectionService = Workbench.getInstance().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService.getSelection();    

		IProject iproject = null;    
		if (selection instanceof IStructuredSelection) {    
		    Object element = ((IStructuredSelection)selection).getFirstElement();
		    if (element instanceof IResource) {    
		        iproject= ((IResource)element).getProject();
		    } else if (element instanceof IJavaElement) {    
		        IJavaProject jProject= ((IJavaElement)element).getJavaProject();    
		        iproject = jProject.getProject();    
		    }    
		}
		return iproject;
	}

}
