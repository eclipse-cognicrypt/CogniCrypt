/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt This program and the accompanying materials are made available under the terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.primitive.providerUtils;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.primitive.Activator;

public class UserJavaProject {

	private IProject project;

	public UserJavaProject() {
		this.project = null;
	}

	/**
	 * List all methods located in the source folder of a javaProject
	 *
	 * @param javaProject
	 * @return list of methods
	 * @throws CoreException
	 */
	public List<IMethod> listOfAllMethods() throws CoreException {
		final IJavaProject javaProject = toJavaProject(this.project);
		if (javaProject != null) {
			final List<IMethod> methodsList = new ArrayList<IMethod>();
			final IPackageFragment[] packages = javaProject.getPackageFragments();
			for (final IPackageFragment pack : packages) {
				// look at the package from the source folder
				if (pack.getKind() == IPackageFragmentRoot.K_SOURCE) {

					for (final ICompilationUnit unit : pack.getCompilationUnits()) {

						final IType[] allTypes = unit.getAllTypes();
						for (final IType type : allTypes) {
							final IMethod[] methods = type.getMethods();

							for (final IMethod method : methods) {
								methodsList.add(method);
							}
						}
					}
				}
			}
			return methodsList;
		}
		return null;
	}

	/**
	 * Import a project from path
	 *
	 * @param path
	 * @throws CoreException
	 */
	public void ImportProject(final String path) throws CoreException {
		final IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path(path));
		this.project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
		this.project.create(description, null);
		this.project.open(null);
		this.project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

	/**
	 * Convert a project to javaProject if its nature is Java
	 *
	 * @param project
	 * @return javaProject
	 * @throws CoreException
	 */
	private IJavaProject toJavaProject(final IProject project) throws CoreException {
		IJavaProject javaProject = null;
		if (project.hasNature(JavaCore.NATURE_ID)) {
			javaProject = JavaCore.create(project);
		} else {
			Activator.getDefault().logError(Constants.NOT_JAVA_PROJECT);
		}
		return javaProject;
	}

	/**
	 * This method creates a new package in the java project of the user
	 *
	 * @param packageName
	 * @throws CoreException
	 */
	public void addPackage(final String packageName) throws CoreException {
		final IJavaProject javaProject = toJavaProject(this.project);
		javaProject.getPackageFragmentRoot(this.project.getFolder("src")).createPackageFragment(packageName, false, null);
	}

	/**
	 * @param packageName
	 * @return the package with the given name
	 */
	public IPackageFragment getPackageByName(final String packageName) {
		IPackageFragment aPackage = null;
		try {
			final IJavaProject javaProject = toJavaProject(this.project);
			final IPackageFragment[] packages = javaProject.getPackageFragments();
			for (final IPackageFragment pack : packages) {
				if (pack.getElementName().equals(packageName)) {
					aPackage = pack;
				}
			}
		}
		catch (final CoreException e) {
			e.printStackTrace();
		}
		return aPackage;

	}

	/**
	 * This method creates a new class in a certain package
	 *
	 * @param className
	 * @param content contains the source code
	 * @param pack is the package where the new class will be added
	 * @throws JavaModelException
	 */
	public void createNewClass(final String className, final String content, final IPackageFragment pack) throws JavaModelException {
		// Add the package declaration into the source code
		final StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\n");
		buffer.append("\n");
		buffer.append(content);

		pack.createCompilationUnit(className, buffer.toString(), false, null);
	}

	/**
	 * @param sourceProject The name of the project to be cloned
	 * @param cloneName The name of the cloned project
	 * @return a cloned project
	 * @throws CoreException
	 */

	public static IProject cloneProject(final String sourceProject) throws CoreException {
		final IProgressMonitor m = new NullProgressMonitor();
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = workspaceRoot.getProject(sourceProject);
		final IProjectDescription projectDescription = project.getDescription();
		final String cloneName = sourceProject + "_copy";
		// create clone project in workspace
		final IProjectDescription cloneDescription = workspaceRoot.getWorkspace().newProjectDescription(cloneName);
		// copy project files
		project.copy(cloneDescription, true, m);
		final IProject clone = workspaceRoot.getProject(cloneName);
		// copy the project properties
		cloneDescription.setNatureIds(projectDescription.getNatureIds());
		cloneDescription.setReferencedProjects(projectDescription.getReferencedProjects());
		cloneDescription.setDynamicReferences(projectDescription.getDynamicReferences());
		cloneDescription.setBuildSpec(projectDescription.getBuildSpec());
		cloneDescription.setReferencedProjects(projectDescription.getReferencedProjects());
		clone.setDescription(cloneDescription, null);
		return clone;
	}

	public void deleteProject() throws CoreException {
		this.project.delete(true, true, null);
		ResourcesPlugin.getWorkspace().save(true, null);
	}

	public IProject getProject() {
		return this.project;
	}

	public void setProject(final IProject project) {
		this.project = project;
	}
}
