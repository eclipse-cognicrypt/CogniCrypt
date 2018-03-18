package de.cognicrypt.codegenerator.primitive.providerUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import de.cognicrypt.codegenerator.Constants;

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
			for (IPackageFragment aPackage : packages) {
				//look at the package from the source folder
				if (aPackage.getKind() == IPackageFragmentRoot.K_SOURCE) {

					for (ICompilationUnit unit : aPackage.getCompilationUnits()) {

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

	public IProject getProject() {
		return this.project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}
}