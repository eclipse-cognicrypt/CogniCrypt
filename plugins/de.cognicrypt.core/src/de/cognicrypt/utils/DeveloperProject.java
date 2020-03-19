/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.apache.maven.cli.MavenCli;
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import de.cognicrypt.core.Activator;
import de.cognicrypt.core.Constants;

/**
 * This class represents the app developer's project, on which the plugin is working.
 */
public class DeveloperProject {

	/**
	 * Application project
	 */
	private final IProject project;

	public DeveloperProject(final IProject developerProject) {
		this.project = developerProject;
	}

	/**
	 * The method adds one library to the developer's project physical and build path. In the context of the overall tool, this is necessary when the user chooses a task that comes
	 * with additional libraries.
	 *
	 * @param pathToJar path to library to be added
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if library was (not) added successfully.
	 * @throws CoreException {@link org.eclipse.core.resources.IProject#hasNature(String) hasNature()}, {@link org.eclipse.jdt.core.IJavaProject#getRawClasspath() getRawClassPath()}
	 *         and {@link org.eclipse.jdt.core.IJavaProject#setRawClassPath() setRawClassPath()}
	 */
	public boolean addJar(final String pathToJar) throws CoreException {
		if (!this.project.isOpen() || !this.project.hasNature(Constants.JavaNatureID)) {
			return false;
		}
		final IJavaProject projectAsJavaProject = JavaCore.create(this.project);
		final LinkedHashSet<IClasspathEntry> classPathEntryList = new LinkedHashSet<>();

		classPathEntryList.addAll(Arrays.asList(projectAsJavaProject.getRawClasspath()));
		classPathEntryList.add(JavaCore.newLibraryEntry(this.project.getFile(pathToJar).getFullPath(), null, null, false));

		projectAsJavaProject.setRawClasspath(classPathEntryList.toArray(new IClasspathEntry[1]), null);
		return true;
	}

	/**
	 * Retrieves folder from developer package
	 *
	 * @param name Project-relative path to folder
	 * @see org.eclipse.core.resources.IProject#getFolder(String) IProject.getFolder()
	 */
	public IFolder getFolder(final String name) {
		return this.project.getFolder(name);
	}

	/**
	 * Retrieves file from developer package
	 *
	 * @param path Project-relative path to file
	 * @see org.eclipse.core.resources.IProject#getFile(String) IProject.getFile()
	 */
	public IFile getIFile(final String path) {
		return this.project.getFile(path.substring(path.indexOf(this.project.getName()) + this.project.getName().length()));
	}

	public IFile getFile(final String path) {
		return this.project.getFile(path);
	}

	/**
	 * This method retrieves a package of the name {@linkplain name} that is in the developer's project.
	 *
	 * @param name name of the package
	 * @return package as {@link org.eclipse.jdt.core.IPackageFragment IPackageFragment}
	 * @throws CoreException see {@link de.cognicrypt.utils.DeveloperProject#getSourcePath() getSourcePath()}
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
	 * @throws CoreException See {@link org.eclipse.core.resources.IProject#hasNature(String) hasNature()}
	 */
	public String getSourcePath() throws CoreException {
		if (this.project.isOpen() && this.project.hasNature(Constants.JavaNatureID)) {
			for (final IClasspathEntry entry : JavaCore.create(this.project).getResolvedClasspath(true)) {
				if (entry.getContentKind() == IPackageFragmentRoot.K_SOURCE) {
					return entry.getPath().removeFirstSegments(1).toOSString();
				}
			}
		}
		return null;
	}

	/**
	 * Refreshes the project.
	 *
	 * @throws CoreException See {@link org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor) refreshLocal()}
	 */
	public void refresh() throws CoreException {
		// From JavaDoc: "This method is long-running." -> if it takes too long for big
		// projects, reduce depth parameter
		// in call or call refresh on Crypto package only
		this.project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	@Override
	public int hashCode() {
		return 31 + ((this.project == null) ? 0 : this.project.hashCode());
	}

	/**
	 * @return Get all methods of the project
	 */
	public Method[] getMethodsfromProject(final Class<?> testClass) {
		final Class<?> classes = this.project.getClass();
		return classes.getDeclaredMethods();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof DeveloperProject) {
			final DeveloperProject other = (DeveloperProject) obj;
			return !(this.project == null || other.project != null) && this.project.equals(other.project);
		} else if (obj instanceof IProject) {
			return this.project.equals((IProject) obj);
		}
		return false;
	}

	@Override
	public String toString() {
		return this.project.getName() + "(" + getProjectPath() + ")";
	}

	/**
	 * Removes package from developer project.
	 *
	 * @param packageName name of package that is removed
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if package removal was successful/failed.
	 */
	public Boolean removePackage(final String packageName) {
		try {
			final IPackageFragment delPackage = getPackagesOfProject(packageName);
			delPackage.delete(true, null);
			return true;
		}
		catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
		return false;
	}

	/**
	 * This method checks if a project possesses the Maven nature, marking it as a Maven project.
	 * 
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if project is Maven project.
	 */
	public boolean isMavenProject() {
		try {
			return this.project.hasNature(Constants.MavenNatureID);
		}
		catch (CoreException e) {
			Activator.getDefault().logError(e);
		}
		return false;
	}

	/**
	 * This method checks if the pom.xml exists in client project
	 * 
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if pom.xml is existing.
	 */
	private boolean doesPomExists() {
		return getPomFile() != null;
	}

	/**
	 * This method returns the pom.xml {@link File}
	 * 
	 * @return pom.xml
	 */
	private File getPomFile() {
		File pom = null;
		if ((pom = new File(project.getLocation().toOSString() + Constants.outerFileSeparator + "pom.xml")).exists()) {
			return pom;
		} else if ((pom = new File(project.getLocation().toOSString() + Constants.outerFileSeparator + "parent" + Constants.outerFileSeparator + "pom.xml")).exists()) {
			return pom;
		} else {
			return null;
		}
	}

	/**
	 * This method adds a Maven dependency entry to the pom.xml, if the entry doesn't exist.
	 * 
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the adding is successful.
	 */
	public boolean addMavenDependency(String groupId, String artifactId, String version) {

		if (isMavenProject()) {
			if (doesPomExists()) {
				XMLParser xmlParser;
				File pom = getPomFile();
				xmlParser = new XMLParser(pom);
				xmlParser.useDocFromFile();

				Node dependenciesNode = xmlParser.getChildNodeByTagName(xmlParser.getRoot(), Constants.DEPENDENCIES_TAG);
				if (dependenciesNode != null) {
					NodeList dependencyList = dependenciesNode.getChildNodes();
					for (int i = 0; i < dependencyList.getLength(); i++) {
						if (isEqualMavenDependency(dependencyList.item(i), groupId, artifactId, version)) {
							Activator.getDefault().logInfo("Maven Dependency already exists in pom.xml\n groupId:" + groupId + " artifactId: " + artifactId + " ver:" + version);
							return false;
						}
					}
				} else {
					dependenciesNode = xmlParser.getDoc().createElement(Constants.DEPENDENCIES_TAG);
					xmlParser.getRoot().appendChild(dependenciesNode);
				}

				Element dependency = xmlParser.getDoc().createElement(Constants.DEPENDENCY_TAG);
				xmlParser.createChildElement(dependency, Constants.GROUPID_TAG, groupId);
				xmlParser.createChildElement(dependency, Constants.ARTIFACTID_TAG, artifactId);
				xmlParser.createChildElement(dependency, Constants.VERSION_TAG, version);
				dependenciesNode.appendChild(dependency);
				xmlParser.writeXML();
				return true;

			} else {
				Activator.getDefault().logError("Project " + project.getName() + " doesn't contain pom.xml");
				return false;
			}
		} else {
			Activator.getDefault().logError("Project " + project.getName() + " is not a Maven Project [no Maven Nature available]");
			return false;
		}
	}

	/**
	 * This method checks if a Maven Dependency {@link Node} contains a certain groupId,artifactId and version
	 * 
	 * @param dependency
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the node contains the right Maven Dependency information
	 */
	private boolean isEqualMavenDependency(Node dependency, String groupId, String artifactId, String version) {

		Node groupIdNode = null;
		Node artifactIdNode = null;
		Node versionNode = null;

		if (dependency.getNodeType() == Node.ELEMENT_NODE) {
			NodeList dependencyNodeList = dependency.getChildNodes();
			for (int j = 0; j < dependencyNodeList.getLength(); j++) {
				Node currentDependencyNode = dependencyNodeList.item(j);
				if (currentDependencyNode.getNodeType() == Node.ELEMENT_NODE) {
					switch (currentDependencyNode.getNodeName()) {
						case Constants.GROUPID_TAG:
							groupIdNode = currentDependencyNode;
							break;
						case Constants.ARTIFACTID_TAG:
							artifactIdNode = currentDependencyNode;
							break;
						case Constants.VERSION_TAG:
							versionNode = currentDependencyNode;
							break;
					}
				}
			}
		}
		return groupIdNode != null && artifactIdNode != null && versionNode != null && groupIdNode.getTextContent().equals(groupId)
				&& artifactIdNode.getTextContent().equals(artifactId) && versionNode.getTextContent().equals(version);
	}

	/**
	 * This method executes Maven commands via Embedded Maven
	 * 
	 * @param workingDirectoryPath
	 * @param commands
	 */
	public void execMaven(String[] commands) {
		if (isMavenProject()) {
			(new MavenCli()).doMain(commands, getProjectPath(), System.out, System.out);
		} else {
			Activator.getDefault().logError("Project " + project.getName() + " is not a Maven Project [no Maven Nature available]");
		}
	}
}
