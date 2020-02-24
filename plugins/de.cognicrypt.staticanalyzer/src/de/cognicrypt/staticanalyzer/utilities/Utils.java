package de.cognicrypt.staticanalyzer.utilities;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import de.cognicrypt.staticanalyzer.kotlin.utilities.KotlinUtils;

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

 			return KotlinUtils.findKotlinClassByName(className, currentProject);
 		}
 		catch (final JavaModelException e) {
 			throw new ClassNotFoundException("Class " + className + " not found.", e);
 		}
 	}	
}
