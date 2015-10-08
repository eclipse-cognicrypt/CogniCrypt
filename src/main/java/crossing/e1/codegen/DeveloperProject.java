/**
 * Copyright 2015 Technische Universität Darmstadt
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
package crossing.e1.codegen;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

import crossing.e1.configurator.Constants;

/**
 * This class represents the app developer's project, on which the plugin is working.
 *
 */
public class DeveloperProject {

	/**
	 * Application project
	 */
	private IProject project;
	
	
	public DeveloperProject(IProject _project) {
		this.project = _project;
	}
	
	/**
	 * @return Absolute Path of Project
	 */
	public String getProjectPath() {
		return project.getLocation().toOSString();
	}
	
	/**
	 * @return Absolute Path of Project
	 * @throws CoreException See {@link org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor) refreshLocal()}
	 */
	public void refresh() throws CoreException {
		//From JavaDoc: "This method is long-running." -> if it takes too long for big projects, reduce depth parameter in call 
		//or call refresh on Crypto package only
		project.refreshLocal(IProject.DEPTH_INFINITE, null);
	}
	
	/**
	 * @return Path to Source Folder of Project.
	 * @throws CoreException see {@link  org.eclipse.core.resources.IProject#hasNature(String) hasNature()}
	 */
	public String getSourcePath() throws CoreException {
		if (project.isOpen() && project.hasNature(Constants.JavaNatureID)){
	        IJavaProject javaProject = JavaCore.create(project);
	        IClasspathEntry[] classpathEntries = null;        
			classpathEntries = javaProject.getResolvedClasspath(true);
			
			for(int i = 0; i < classpathEntries.length; i++){
	            IClasspathEntry entry = classpathEntries[i];
	            if(entry.getContentKind() == IPackageFragmentRoot.K_SOURCE) {
	                return entry.getPath().removeFirstSegments(1).toOSString();
	            }
	        }
	    }
		return null;
	}
	
	public IPackageFragment getPackagesOfProject(String name) throws CoreException {
		return JavaCore.create(project).getPackageFragmentRoot(project.getFolder(getSourcePath())).getPackageFragment(name);
	}
	
	public IFile getIFile(String path) {
		return project.getFile(path.substring(path.indexOf(project.getName()) + project.getName().length()));
	}
}
