/**
 * Copyright 2015-2016 Technische Universitaet Darmstadt
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
package crossing.e1.configurator;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

/**
 * This class represents the app developer's project, on which the plugin is working.
 *
 */
public class DeveloperProject {

	/**
	 * Application project
	 */
	private final IProject project;

	public DeveloperProject(final IProject _project) {
		this.project = _project;
	}

	/***
	 * The method adds one library to the developer's project physical and build path. In the context of the overall tool, this is necessary when the user chooses a task that comes
	 * with additional libraries.
	 * 
	 * @param pathToJar
	 *        path to library to be added
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if library was (not) added successfully.
	 * @throws CoreException 
	 *         {@link org.eclipse.core.resources.IProject#hasNature(String) hasNature()}, {@link org.eclipse.jdt.core.IJavaProject#getRawClasspath() getRawClassPath()} and
	 *         {@link org.eclipse.jdt.core.IJavaProject#setRawClassPath() setRawClassPath()}
	 */
	public boolean addJar(final String pathToJar) throws CoreException {
		if (this.project.isOpen() && this.project.hasNature(Constants.JavaNatureID)) {
			final IJavaProject projectAsJavaProject = JavaCore.create(this.project);
			final IFile file = this.project.getFile(pathToJar);
			final LinkedHashSet<IClasspathEntry> classPathEntryList = new LinkedHashSet<IClasspathEntry>();
			final IClasspathEntry newEntryOfLibrary = JavaCore.newLibraryEntry(file.getFullPath(), null, null, false);

			classPathEntryList.addAll(Arrays.asList(projectAsJavaProject.getRawClasspath()));
			classPathEntryList.add(newEntryOfLibrary);

			projectAsJavaProject.setRawClasspath(classPathEntryList.toArray(new IClasspathEntry[1]), null);
			return true;
		}

		return false;
	}

	/***
	 *  @see  {@link org.eclipse.core.resources.IProject#getFolder() IProject.getFolder()}
	 */
	public IFolder getFolder(final String name) {
		return this.project.getFolder(name);
	}

	public IFile getIFile(final String path) {
		return this.project.getFile(path.substring(path.indexOf(this.project.getName()) + this.project.getName().length()));
	}

	/***
	 *  This method retrieves a package of the name {@linkplain name} that is in the developer's project.
	 * @param name name of the package
	 * @return package as {@link org.eclipse.jdt.core.IPackageFragment IPackageFragment}
	 * @throws CoreException see {@link crossing.e1.configurator.DeveloperProject#getSourcePath() getSourcePath()}
	 */
	public IPackageFragment getPackagesOfProject(final String name) throws CoreException {
		return JavaCore.create(this.project).getPackageFragmentRoot(this.project.getFolder(getSourcePath())).getPackageFragment(name);
	}

	/**
	 * @return Absolute Path of Project
	 */
	public String getProjectPath() {
		return this.project.getLocation().toOSString();
	}

	/**
	 * @return Path to Source Folder of Project.
	 * @throws CoreException
	 *         See {@link org.eclipse.core.resources.IProject#hasNature(String) hasNature()}
	 */
	public String getSourcePath() throws CoreException {
		if (this.project.isOpen() && this.project.hasNature(Constants.JavaNatureID)) {
			final IJavaProject javaProject = JavaCore.create(this.project);
			IClasspathEntry[] classpathEntries = null;
			classpathEntries = javaProject.getResolvedClasspath(true);

			for (int i = 0; i < classpathEntries.length; i++) {
				final IClasspathEntry entry = classpathEntries[i];
				if (entry.getContentKind() == IPackageFragmentRoot.K_SOURCE) {
					return entry.getPath().removeFirstSegments(1).toOSString();
				}
			}
		}
		return null;
	}

	/**
	 * @return Absolute Path of Project
	 * @throws CoreException
	 *         See {@link org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor) refreshLocal()}
	 */
	public void refresh() throws CoreException {
		// From JavaDoc: "This method is long-running." -> if it takes too long for big projects, reduce depth parameter
		// in call or call refresh on Crypto package only
		this.project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}
}
