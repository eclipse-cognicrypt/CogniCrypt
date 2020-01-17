package de.cognicrypt.staticanalyzer.utilities;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.jetbrains.kotlin.core.model.KotlinNature;

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

			if(Platform.getBundle("org.jetbrains.kotlin.core") != null) {
				// This part is required because Eclipse JDT doesn’t provide any mapping of kotlin light classes to its source code
				// As a result the above IPackageFragment.getCompilationUnits() doesn't return any kotlin .class files
				if(KotlinNature.hasKotlinNature(currentProject)) {

					// computing corresponding source filename, since in kotlin .class filename is changed
					// Eg. Demo.kt is compiled to DemoKt.class
					String[] temp = className.split("\\.");
					String classFileName = temp[temp.length-1];
					String srcFilename = "";

					if(classFileName.substring(classFileName.length()-2).equals("Kt")) {
						srcFilename = classFileName.substring(0, classFileName.length()-2) + ".kt";
					}
					// because in some projects the class names aren't renamed
					else
						srcFilename = classFileName + ".kt";

					for (final IPackageFragment l : JavaCore.create(currentProject).getPackageFragments()) {
						// this check is needed because IJavaProject.getPackageFragments() returns dependencies as well
						if(l.getKind() == IPackageFragmentRoot.K_SOURCE) {
							// removing the <project_name> from path returned by IPackageFragment.getPath() because IProject.getFile() also appends it
							String[] originalPath = l.getPath().toString().split(File.separator);
							String[] modifiedPath = Arrays.copyOfRange(originalPath, 2, originalPath.length);
							String packageName = String.join(File.separator, modifiedPath);

							IFile sourceFile = currentProject.getFile(packageName + File.separator + srcFilename);
							if(sourceFile.exists())
								return (IResource) sourceFile;
						}
					}
				}
			}
		}
		catch (final JavaModelException e) {
			throw new ClassNotFoundException("Class " + className + " not found.", e);
		}
		throw new ClassNotFoundException("Class " + className + " not found.");
	}
}
