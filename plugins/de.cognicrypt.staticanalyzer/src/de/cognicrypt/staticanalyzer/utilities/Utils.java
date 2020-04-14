package de.cognicrypt.staticanalyzer.utilities;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.staticanalyzer.IListener;

public class Utils {

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
 			
 			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("de.cognicrypt.staticanalyzer.listeners");
 			try {
 	            for (IConfigurationElement e : config) {
 	                final Object o =
 	                        e.createExecutableExtension("class");
 	                if (o instanceof IListener) {
 	                	return ((IListener) o).listen2(className, currentProject);
 	                }
 	            }
 	        } catch (CoreException ex) {
 	        	Activator.getDefault().logError(ex);
 	        }
 		}
 		catch (final JavaModelException e) {
 			throw new ClassNotFoundException("Class " + className + " not found.", e);
 		}
 		throw new ClassNotFoundException("Class " + className + " not found.");
 	}	
}
