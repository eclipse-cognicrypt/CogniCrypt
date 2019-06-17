/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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

import de.cognicrypt.core.Constants;
import de.cognicrypt.core.Activator;

/**
 * This class represents the app developer's project, on which the plugin is working.
 *
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
			final LinkedHashSet<IClasspathEntry> classPathEntryList = new LinkedHashSet<>();

			classPathEntryList.addAll(Arrays.asList(projectAsJavaProject.getRawClasspath()));
			classPathEntryList.add(JavaCore.newLibraryEntry(this.project.getFile(pathToJar).getFullPath(), null, null, false));

			projectAsJavaProject.setRawClasspath(classPathEntryList.toArray(new IClasspathEntry[1]), null);
			return true;
		}

		return false;
	}

	/**
	 * Retrieves folder from developer package
	 *
	 * @param name
	 *        Project-relative path to folder
	 * @see org.eclipse.core.resources.IProject#getFolder(String) IProject.getFolder()
	 */
	public IFolder getFolder(final String name) {
		return this.project.getFolder(name);
	}

	/**
	 * Retrieves file from developer package
	 *
	 * @param path
	 *        Project-relative path to file
	 * @see org.eclipse.core.resources.IProject#getFile(String) IProject.getFile()
	 */
	public IFile getIFile(final String path) {
		return this.project.getFile(path.substring(path.indexOf(this.project.getName()) + this.project.getName().length()));
	}

	/**
	 * This method retrieves a package of the name {@linkplain name} that is in the developer's project.
	 *
	 * @param name
	 *        name of the package
	 * @return package as {@link org.eclipse.jdt.core.IPackageFragment IPackageFragment}
	 * @throws CoreException
	 *         see {@link de.cognicrypt.utils.DeveloperProject#getSourcePath() getSourcePath()}
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
	 * @throws CoreException
	 *         See {@link org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor) refreshLocal()}
	 */
	public void refresh() throws CoreException {
		// From JavaDoc: "This method is long-running." -> if it takes too long for big projects, reduce depth parameter
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
			final IProject other = (IProject) obj;
			return this.project.equals(other);
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
	 * @param packageName
	 *        name of package that is removed
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if package removal was successful/failed.
	 */
	public Boolean removePackage(final String packageName) {
		try {
			final IPackageFragment delPackage = getPackagesOfProject(packageName);
			delPackage.delete(true, null);
			return true;
		} catch (final CoreException e) {
			Activator.getDefault().logError(e);
		}
		return false;
	}

	/**
	 * This method checks if a project is a MavenProject. 
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if MavenNature is existing.
	 * @throws CoreException
	 */
	public boolean isMavenProject() throws CoreException {
		if (this.project.hasNature(Constants.MavenNatureID)) {
			return true;
		}
		return false;
	}

	/**
	 * This method adds a Maven dependency entry to the pom.xml, if the entry doesn't exist.
	 * @param groupId		
	 * @param artifactId
	 * @param version
	 * @return <CODE>true</CODE>/<CODE>false</CODE> if the adding is successful.
	 */
	public boolean addMavenDependency(String groupId, String artifactId, String version) {
		XMLParser xmlParser;
		File pom = new File(project.getLocation().toOSString() + Constants.outerFileSeparator + "pom.xml");		
		if (pom.exists()) {
			xmlParser = new XMLParser(pom);
			xmlParser.useDocFromFile();


			Node dependenciesNode = xmlParser.getChildNodeByTagName(xmlParser.getRoot(), Constants.DEPENDENCIES_TAG);
			if(dependenciesNode != null) {
				NodeList dependencyList = dependenciesNode.getChildNodes();
				for (int i = 0; i < dependencyList.getLength(); i++) {
					if (isEqualMavenDependency(dependencyList.item(i), groupId, artifactId, version)) {
						return false;
					}
				}
			}
			else {
				dependenciesNode = xmlParser.getDoc().createElement(Constants.DEPENDENCIES_TAG);
				xmlParser.getRoot().appendChild(dependenciesNode);
			}

			Element dependency = xmlParser.getDoc().createElement(Constants.DEPENDENCY_TAG);
			xmlParser.createChildElement(dependency, Constants.GROUPID_TAG, groupId);
			xmlParser.createChildElement(dependency, Constants.ARTIFACTID_TAG, artifactId);
			xmlParser.createChildElement(dependency, Constants.VERSION_TAG, version);
			dependenciesNode.appendChild(dependency);

			xmlParser.writeXML();
			execute(pom, Arrays.asList(Constants.MVN_INSTALL_COMMAND));

		} else {
			Activator.getDefault().logInfo("pom.xml doesn't exist at this place: " + project.getLocation().toOSString()
					+ Constants.outerFileSeparator + "pom.xml");
		}
		return true;
	}

	/**
	 * This method 
	 * @param dependency
	 * @param groupId
	 * @param artifactId
	 * @param version
	 * @return
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
					String nodeName = currentDependencyNode.getNodeName();
					if (nodeName.equals(Constants.GROUPID_TAG)) {
						groupIdNode = currentDependencyNode;
					} else if (nodeName.equals(Constants.ARTIFACTID_TAG)) {
						artifactIdNode = currentDependencyNode;
					} else if (nodeName.equals(Constants.VERSION_TAG)) {
						versionNode = currentDependencyNode;
					}
					// Activator.getDefault().logInfo("NodeName: " +nodeName );
					// Activator.getDefault().logInfo("Content: " +nodeContent );
				}
			}
		}
		if (groupIdNode != null && artifactIdNode != null && versionNode != null) {
			if (groupIdNode.getTextContent().equals(groupId) && artifactIdNode.getTextContent().equals(artifactId)
					&& versionNode.getTextContent().equals(version)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method executes Maven commands
	 * @param pom
	 * @param commands
	 */
	private void execute(File pom, List<String> commands) {
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(pom);
		request.setGoals( commands );
		Invoker invoker = new DefaultInvoker();
		try {
		InvocationResult result = invoker.execute( request );
		} catch (MavenInvocationException e) {
			Activator.getDefault().logError(e);
		}
	}
}
