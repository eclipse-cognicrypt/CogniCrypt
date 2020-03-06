/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.staticanalyzer.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.PlatformUI;
import de.cognicrypt.core.Constants;
import de.cognicrypt.staticanalyzer.Activator;
import de.cognicrypt.utils.DeveloperProject;
import de.cognicrypt.utils.Utils;

public class QuickFixUtils {
	/**
	 * This method checks if a {@link ICompilationUnit} contains a certain import
	 * 
	 * @param unit ICompilationUnit of the source file
	 * @param packagePath path of the annotation
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the import (not) exists
	 * @throws CoreException
	 */
	public static boolean hasJarImport(final ICompilationUnit unit, final String packagePath) throws CoreException {
		return unit.getImport(packagePath).exists();
	}

	/**
	 * This method inserts a certain import to a {@link ICompilationUnit}
	 * 
	 * @param unit ICompilationUnit of the source file
	 * @param packagePath path of the annotation
	 * @throws CoreException
	 */
	public static void insertJarImport(final ICompilationUnit unit, final String packagePath) throws CoreException {
		unit.createImport(packagePath, null, null);
		unit.save(null, true);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().doSave(null);
	}

	/**
	 * This method gets a {@link ICompilationUnit} from the marker
	 * 
	 * @param marker problem marker
	 * @return the ICompilationUnit of the source file of the problem marker
	 * @throws CoreException
	 */
	public static ICompilationUnit getCompilationUnitFromMarker(final IMarker marker) throws CoreException {
		final IJavaElement javaElement = JavaCore.create(marker.getResource());
		ICompilationUnit unit = null;
		if (javaElement instanceof ICompilationUnit) {
			unit = (ICompilationUnit) javaElement;
		}
		if (unit == null) {
			throw new CoreException(null);
		}
		return unit;
	}

	/**
	 * This method adds all files of a certain source to the client project
	 * 
	 * @param source path to the source
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the adding was (not) successfully
	 */
	public static boolean addAdditionalFiles(final String source, final String projectID, DeveloperProject clientProject) {
		if (source.isEmpty()) {
			return true;
		}
		try {
			final File[] members = Utils.getResourceFromWithin(source, projectID).listFiles();
			if (members == null) {
				Activator.getDefault().logError(Constants.ERROR_MESSAGE_NO_ADDITIONAL_RES_DIRECTORY);
			}
			for (int i = 0; i < members.length; i++) {
				final File addFile = members[i];
				if (!addAddtionalFile(clientProject, addFile)) {
					return false;
				}
			}
		}
		catch (IOException | CoreException e) {
			Activator.getDefault().logError(e, Constants.CodeGenerationErrorMessage);
			return false;
		}
		return true;
	}

	/**
	 * This method adds a {@link File} to the client project
	 * 
	 * @param fileToBeAdded current file
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the adding was (not) successfully
	 * @throws IOException
	 * @throws CoreException
	 */
	private static boolean addAddtionalFile(DeveloperProject clientProject, final File fileToBeAdded) throws IOException, CoreException {
		final IFolder libFolder = clientProject.getFolder(Constants.pathsForLibrariesInDevProject);
		if (!libFolder.exists()) {
			libFolder.create(true, true, null);
		}

		final Path memberPath = fileToBeAdded.toPath();
		Files.copy(memberPath,
				new File(clientProject.getProjectPath() + Constants.outerFileSeparator + Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator + memberPath.getFileName())
						.toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		final String filePath = fileToBeAdded.toString();
		final String cutPath = filePath.substring(filePath.lastIndexOf(Constants.outerFileSeparator));
		if (Constants.JAR.equals(cutPath.substring(cutPath.indexOf(".")))) {
			if (!clientProject.addJar(Constants.pathsForLibrariesInDevProject + Constants.outerFileSeparator + fileToBeAdded.getName())) {
				return false;
			}
		}
		return true;
	}
	
	public static String getErrorTypeFromMarkerType(String markerType) {
		
		String temp = markerType.substring(0, markerType.length() - 6);
		String[] parts = temp.split("\\.");
		String type = parts[parts.length-1];		

		return type;
	}

}
