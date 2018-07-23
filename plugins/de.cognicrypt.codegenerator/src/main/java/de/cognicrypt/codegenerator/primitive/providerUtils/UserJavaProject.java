/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.providerUtils;

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

import de.cognicrypt.codegenerator.Activator;
import de.cognicrypt.core.Constants;

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
		IJavaProject javaProject = toJavaProject(this.project);
		if (javaProject != null) {
			List<IMethod> methodsList = new ArrayList<IMethod>();
			IPackageFragment[] packages = javaProject.getPackageFragments();
			for (IPackageFragment pack : packages) {
				//look at the package from the source folder
				if (pack.getKind() == IPackageFragmentRoot.K_SOURCE) {

					for (ICompilationUnit unit : pack.getCompilationUnits()) {

						IType[] allTypes = unit.getAllTypes();
						for (IType type : allTypes) {
							IMethod[] methods = type.getMethods();

							for (IMethod method : methods) {
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
	public void ImportProject(String path) throws CoreException {
		IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path(path));
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
	private IJavaProject toJavaProject(IProject project) throws CoreException {
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
	public void addPackage(String packageName) throws CoreException {
		IJavaProject javaProject = toJavaProject(this.project);
		IPackageFragment pack = javaProject.getPackageFragmentRoot(this.project.getFolder("src")).createPackageFragment(packageName, false, null);
	}

	/**
	 * 
	 * @param packageName
	 * @return the package with the given name
	 */
	public IPackageFragment getPackageByName(String packageName) {
		IPackageFragment aPackage = null;
		try {
			IJavaProject javaProject = toJavaProject(this.project);
			IPackageFragment[] packages = javaProject.getPackageFragments();
			for (IPackageFragment pack : packages) {
				if (pack.getElementName().equals(packageName)) {
					aPackage = pack;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return aPackage;

	}

	/**
	 * This method creates a new class in a certain package
	 * 
	 * @param className
	 * @param content
	 *        contains the source code
	 * @param pack
	 *        is the package where the new class will be added
	 * @throws JavaModelException
	 */
	public void createNewClass(String className, String content, IPackageFragment pack) throws JavaModelException {
		//Add the package declaration into the source code
		StringBuffer buffer = new StringBuffer();
		buffer.append("package " + pack.getElementName() + ";\n");
		buffer.append("\n");
		buffer.append(content);

		//Generate the new class 
		ICompilationUnit cu = pack.createCompilationUnit(className, buffer.toString(), false, null);
	}

	/**
	 * 
	 * @param sourceProject
	 *        The name of the project to be cloned
	 * @param cloneName
	 *        The name of the cloned project
	 * @return a cloned project
	 * @throws CoreException
	 */

	public static IProject cloneProject(String sourceProject) throws CoreException {
		IProgressMonitor m = new NullProgressMonitor();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = workspaceRoot.getProject(sourceProject);
		IProjectDescription projectDescription = project.getDescription();
		String cloneName = sourceProject + "_copy";
		// create clone project in workspace
		IProjectDescription cloneDescription = workspaceRoot.getWorkspace().newProjectDescription(cloneName);
		// copy project files
		project.copy(cloneDescription, true, m);
		IProject clone = workspaceRoot.getProject(cloneName);
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

	public void setProject(IProject project) {
		this.project = project;
	}
}
